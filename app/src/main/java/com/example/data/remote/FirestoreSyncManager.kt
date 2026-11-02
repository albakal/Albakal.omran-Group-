package com.example.data.remote

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.local.AppDao
import com.example.data.model.*
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FirestoreSyncManager(
  private val context: Context,
  private val dao: AppDao
) {
  private val tag = "FirestoreSyncManager"
  private val scope = CoroutineScope(Dispatchers.IO)
  private var db: FirebaseFirestore? = null
  private val listeners = mutableListOf<ListenerRegistration>()

  init {
    try {
      val databaseId = try {
        context.getString(R.string.firestore_database_id)
      } catch (e: Exception) {
        ""
      }

      db = if (databaseId.isNotBlank() && databaseId != "(default)") {
        FirebaseFirestore.getInstance(FirebaseApp.getInstance(), databaseId)
      } else {
        FirebaseFirestore.getInstance()
      }
      Log.i(tag, "Firestore initialized successfully with databaseId: $databaseId")
      startRealtimeSync()
    } catch (e: Exception) {
      Log.e(tag, "Failed to initialize Firestore: ${e.message}", e)
    }
  }

  fun getFirestore(): FirebaseFirestore? {
    if (db == null) {
      try {
        val databaseId = try {
          context.getString(R.string.firestore_database_id)
        } catch (e: Exception) {
          ""
        }
        db = if (databaseId.isNotBlank() && databaseId != "(default)") {
          FirebaseFirestore.getInstance(FirebaseApp.getInstance(), databaseId)
        } else {
          FirebaseFirestore.getInstance()
        }
        Log.i(tag, "Lazy init Firestore succeeded")
      } catch (e: Exception) {
        Log.e(tag, "Lazy init Firestore failed: ${e.message}")
      }
    }
    return db
  }

  fun startRealtimeSync() {
    val firestore = db ?: return

    // 1. Sync Users (Registrations, Statuses, Roles, Approvals)
    try {
      val usersListener = firestore.collection("users")
        .addSnapshotListener { snapshot, error ->
          if (error != null) {
            Log.e(tag, "Users listen error: ${error.message}")
            return@addSnapshotListener
          }
          if (snapshot != null) {
            scope.launch {
              for (doc in snapshot.documents) {
                try {
                  val user = docToUser(doc)
                  if (user != null) {
                    dao.insertUser(user)
                  }
                } catch (e: Exception) {
                  Log.e(tag, "Error parsing user doc ${doc.id}: ${e.message}")
                }
              }
            }
          }
        }
      listeners.add(usersListener)
    } catch (e: Exception) {
      Log.e(tag, "Failed setting up users listener: ${e.message}")
    }

    // 2. Sync Products with Immediate Cache Eviction
    try {
      val productsListener = firestore.collection("products")
        .addSnapshotListener { snapshot, error ->
          if (error != null) {
            Log.e(tag, "Products listen error: ${error.message}")
            return@addSnapshotListener
          }
          if (snapshot != null) {
            scope.launch {
              // 1. Instantly process removed changes
              for (change in snapshot.documentChanges) {
                if (change.type == DocumentChange.Type.REMOVED) {
                  dao.deleteProductById(change.document.id)
                }
              }

              // 2. Full cache reconciliation: purge any local products deleted remotely
              val remoteIds = snapshot.documents.map { it.id }
              if (remoteIds.isNotEmpty()) {
                dao.purgeProductsNotInList(remoteIds)
              } else {
                dao.clearAllProducts()
              }

              // 3. Insert or update existing active products
              for (doc in snapshot.documents) {
                try {
                  val product = docToProduct(doc)
                  if (product != null) {
                    dao.insertProduct(product)
                  }
                } catch (e: Exception) {
                  Log.e(tag, "Error parsing product doc ${doc.id}: ${e.message}")
                }
              }
            }
          }
        }
      listeners.add(productsListener)
    } catch (e: Exception) {
      Log.e(tag, "Failed setting up products listener: ${e.message}")
    }

    // 3. Sync Orders
    try {
      val ordersListener = firestore.collection("orders")
        .addSnapshotListener { snapshot, error ->
          if (error != null) {
            Log.e(tag, "Orders listen error: ${error.message}")
            return@addSnapshotListener
          }
          if (snapshot != null) {
            scope.launch {
              for (doc in snapshot.documents) {
                try {
                  val order = docToOrder(doc)
                  if (order != null) {
                    dao.insertOrder(order)
                  }
                } catch (e: Exception) {
                  Log.e(tag, "Error parsing order doc ${doc.id}: ${e.message}")
                }
              }
            }
          }
        }
      listeners.add(ordersListener)
    } catch (e: Exception) {
      Log.e(tag, "Failed setting up orders listener: ${e.message}")
    }

    // 4. Sync Order Items
    try {
      val orderItemsListener = firestore.collection("order_items")
        .addSnapshotListener { snapshot, error ->
          if (error != null) return@addSnapshotListener
          if (snapshot != null) {
            scope.launch {
              for (doc in snapshot.documents) {
                try {
                  val item = docToOrderItem(doc)
                  if (item != null) {
                    dao.insertOrderItem(item)
                  }
                } catch (e: Exception) {
                  Log.e(tag, "Error parsing order item ${doc.id}: ${e.message}")
                }
              }
            }
          }
        }
      listeners.add(orderItemsListener)
    } catch (e: Exception) {
      Log.e(tag, "Failed setting up order items listener: ${e.message}")
    }

    // 5. Sync Debt Payments
    try {
      val debtPaymentsListener = firestore.collection("debt_payments")
        .addSnapshotListener { snapshot, error ->
          if (error != null) return@addSnapshotListener
          if (snapshot != null) {
            scope.launch {
              for (doc in snapshot.documents) {
                try {
                  val debt = docToDebtPayment(doc)
                  if (debt != null) {
                    dao.insertDebtPayment(debt)
                  }
                } catch (e: Exception) {
                  Log.e(tag, "Error parsing debt payment ${doc.id}: ${e.message}")
                }
              }
            }
          }
        }
      listeners.add(debtPaymentsListener)
    } catch (e: Exception) {
      Log.e(tag, "Failed setting up debt payments listener: ${e.message}")
    }

    // 6. Sync Sponsored Ads
    try {
      val adsListener = firestore.collection("sponsored_ads")
        .addSnapshotListener { snapshot, error ->
          if (error != null) return@addSnapshotListener
          if (snapshot != null) {
            scope.launch {
              for (doc in snapshot.documents) {
                try {
                  val ad = docToAd(doc)
                  if (ad != null) {
                    dao.insertAd(ad)
                  }
                } catch (e: Exception) {
                  Log.e(tag, "Error parsing ad ${doc.id}: ${e.message}")
                }
              }
            }
          }
        }
      listeners.add(adsListener)
    } catch (e: Exception) {
      Log.e(tag, "Failed setting up ads listener: ${e.message}")
    }

    // 7. Sync Governorate Areas
    try {
      val areasListener = firestore.collection("governorate_areas")
        .addSnapshotListener { snapshot, error ->
          if (error != null) return@addSnapshotListener
          if (snapshot != null) {
            scope.launch {
              for (doc in snapshot.documents) {
                try {
                  val area = docToArea(doc)
                  if (area != null) {
                    dao.insertArea(area)
                  }
                } catch (e: Exception) {
                  Log.e(tag, "Error parsing area ${doc.id}: ${e.message}")
                }
              }
            }
          }
        }
      listeners.add(areasListener)
    } catch (e: Exception) {
      Log.e(tag, "Failed setting up areas listener: ${e.message}")
    }

    // 8. Sync Product Categories
    try {
      val categoriesListener = firestore.collection("product_categories")
        .addSnapshotListener { snapshot, error ->
          if (error != null) return@addSnapshotListener
          if (snapshot != null) {
            scope.launch {
              for (doc in snapshot.documents) {
                try {
                  val cat = docToCategory(doc)
                  if (cat != null) {
                    dao.insertCategory(cat)
                  }
                } catch (e: Exception) {
                  Log.e(tag, "Error parsing category ${doc.id}: ${e.message}")
                }
              }
            }
          }
        }
      listeners.add(categoriesListener)
    } catch (e: Exception) {
      Log.e(tag, "Failed setting up categories listener: ${e.message}")
    }

    // 9. Sync App Settings
    try {
      val settingsListener = firestore.collection("app_settings").document("main_settings")
        .addSnapshotListener { doc, error ->
          if (error != null || doc == null || !doc.exists()) return@addSnapshotListener
          scope.launch {
            try {
              val settings = docToSettings(doc)
              if (settings != null) {
                dao.insertSettings(settings)
              }
            } catch (e: Exception) {
              Log.e(tag, "Error parsing settings doc: ${e.message}")
            }
          }
        }
      listeners.add(settingsListener)
    } catch (e: Exception) {
      Log.e(tag, "Failed setting up settings listener: ${e.message}")
    }

    // 10. Sync Supervisor Permissions
    try {
      val permsListener = firestore.collection("supervisor_permissions")
        .addSnapshotListener { snapshot, error ->
          if (error != null || snapshot == null) return@addSnapshotListener
          scope.launch {
            for (doc in snapshot.documents) {
              try {
                val perms = docToSupervisorPermissions(doc)
                if (perms != null) {
                  dao.insertPermission(perms)
                }
              } catch (e: Exception) {
                Log.e(tag, "Error parsing permissions ${doc.id}: ${e.message}")
              }
            }
          }
        }
      listeners.add(permsListener)
    } catch (e: Exception) {
      Log.e(tag, "Failed setting up permissions listener: ${e.message}")
    }

    // 11. Sync Announcements & Broadcast Updates
    try {
      val announcementsListener = firestore.collection("announcements")
        .addSnapshotListener { snapshot, error ->
          if (error != null || snapshot == null) return@addSnapshotListener
          scope.launch {
            for (change in snapshot.documentChanges) {
              if (change.type == DocumentChange.Type.REMOVED) {
                dao.deleteAnnouncementById(change.document.id)
              }
            }
            for (doc in snapshot.documents) {
              try {
                val announcement = docToAnnouncement(doc)
                if (announcement != null) {
                  dao.insertAnnouncement(announcement)
                }
              } catch (e: Exception) {
                Log.e(tag, "Error parsing announcement ${doc.id}: ${e.message}")
              }
            }
          }
        }
      listeners.add(announcementsListener)
    } catch (e: Exception) {
      Log.e(tag, "Failed setting up announcements listener: ${e.message}")
    }

    // 12. Sync Shipping Promo Codes
    try {
      val promoListener = firestore.collection("shipping_promo_codes")
        .addSnapshotListener { snapshot, error ->
          if (error != null || snapshot == null) return@addSnapshotListener
          scope.launch {
            for (change in snapshot.documentChanges) {
              if (change.type == DocumentChange.Type.REMOVED) {
                dao.deletePromoCode(change.document.id)
              }
            }
            for (doc in snapshot.documents) {
              try {
                val promo = docToPromoCode(doc)
                if (promo != null) {
                  dao.insertPromoCode(promo)
                }
              } catch (e: Exception) {
                Log.e(tag, "Error parsing promo ${doc.id}: ${e.message}")
              }
            }
          }
        }
      listeners.add(promoListener)
    } catch (e: Exception) {
      Log.e(tag, "Failed setting up promo codes listener: ${e.message}")
    }
  }

  // Cloud Write Methods
  suspend fun updateUserFcmToken(userId: String, token: String?) {
    val firestore = db ?: return
    try {
      firestore.collection("users").document(userId).set(
        mapOf("fcmToken" to token),
        SetOptions.merge()
      ).await()
      dao.updateUserFcmToken(userId, token)
    } catch (e: Exception) {
      Log.e(tag, "updateUserFcmToken failed: ${e.message}")
    }
  }

  suspend fun clearUserFcmToken(userId: String) {
    updateUserFcmToken(userId, null)
  }

  suspend fun pushAnnouncement(announcement: AnnouncementEntity) {
    val firestore = db ?: return
    try {
      firestore.collection("announcements").document(announcement.id)
        .set(announcementToMap(announcement), SetOptions.merge()).await()
      dao.insertAnnouncement(announcement)
    } catch (e: Exception) {
      Log.e(tag, "pushAnnouncement failed: ${e.message}")
    }
  }

  suspend fun deleteAnnouncement(announcementId: String) {
    val firestore = db ?: return
    try {
      firestore.collection("announcements").document(announcementId).delete().await()
      dao.deleteAnnouncementById(announcementId)
    } catch (e: Exception) {
      Log.e(tag, "deleteAnnouncement failed: ${e.message}")
    }
  }
  suspend fun pushUser(user: UserEntity) {
    val firestore = db ?: return
    try {
      firestore.collection("users").document(user.id).set(userToMap(user), SetOptions.merge()).await()
    } catch (e: Exception) {
      Log.e(tag, "pushUser failed: ${e.message}")
    }
  }

  suspend fun deleteUser(userId: String) {
    val firestore = db ?: return
    try {
      firestore.collection("users").document(userId).delete().await()
    } catch (e: Exception) {
      Log.e(tag, "deleteUser failed: ${e.message}")
    }
  }

  suspend fun pushProduct(product: ProductEntity) {
    val firestore = db ?: return
    try {
      firestore.collection("products").document(product.id).set(productToMap(product), SetOptions.merge()).await()
    } catch (e: Exception) {
      Log.e(tag, "pushProduct failed: ${e.message}")
    }
  }

  suspend fun deleteProduct(productId: String) {
    val firestore = db ?: return
    try {
      firestore.collection("products").document(productId).delete().await()
    } catch (e: Exception) {
      Log.e(tag, "deleteProduct failed: ${e.message}")
    }
  }

  suspend fun pushOrder(order: OrderEntity, items: List<OrderItemEntity>) {
    val firestore = db ?: return
    try {
      firestore.collection("orders").document(order.id).set(orderToMap(order), SetOptions.merge()).await()
      for (item in items) {
        firestore.collection("order_items").document(item.id).set(orderItemToMap(item), SetOptions.merge()).await()
      }
    } catch (e: Exception) {
      Log.e(tag, "pushOrder failed: ${e.message}")
    }
  }

  suspend fun updateOrderStatus(order: OrderEntity) {
    val firestore = db ?: return
    try {
      firestore.collection("orders").document(order.id).set(orderToMap(order), SetOptions.merge()).await()
    } catch (e: Exception) {
      Log.e(tag, "updateOrderStatus failed: ${e.message}")
    }
  }

  suspend fun pushDebtPayment(debt: DebtPaymentRequestEntity) {
    val firestore = db ?: return
    try {
      firestore.collection("debt_payments").document(debt.id).set(debtToMap(debt), SetOptions.merge()).await()
    } catch (e: Exception) {
      Log.e(tag, "pushDebtPayment failed: ${e.message}")
    }
  }

  suspend fun pushAd(ad: SponsoredAdEntity) {
    val firestore = db ?: return
    try {
      firestore.collection("sponsored_ads").document(ad.id).set(adToMap(ad), SetOptions.merge()).await()
    } catch (e: Exception) {
      Log.e(tag, "pushAd failed: ${e.message}")
    }
  }

  suspend fun deleteAd(adId: String) {
    val firestore = db ?: return
    try {
      firestore.collection("sponsored_ads").document(adId).delete().await()
    } catch (e: Exception) {
      Log.e(tag, "deleteAd failed: ${e.message}")
    }
  }

  suspend fun pushArea(area: GovernorateAreaEntity) {
    val firestore = db ?: return
    try {
      firestore.collection("governorate_areas").document(area.id).set(areaToMap(area), SetOptions.merge()).await()
    } catch (e: Exception) {
      Log.e(tag, "pushArea failed: ${e.message}")
    }
  }

  suspend fun deleteArea(areaId: String) {
    val firestore = db ?: return
    try {
      firestore.collection("governorate_areas").document(areaId).delete().await()
    } catch (e: Exception) {
      Log.e(tag, "deleteArea failed: ${e.message}")
    }
  }

  suspend fun pushCategory(cat: ProductCategoryEntity) {
    val firestore = db ?: return
    try {
      firestore.collection("product_categories").document(cat.id).set(categoryToMap(cat), SetOptions.merge()).await()
    } catch (e: Exception) {
      Log.e(tag, "pushCategory failed: ${e.message}")
    }
  }

  suspend fun deleteCategory(catId: String) {
    val firestore = db ?: return
    try {
      firestore.collection("product_categories").document(catId).delete().await()
    } catch (e: Exception) {
      Log.e(tag, "deleteCategory failed: ${e.message}")
    }
  }

  suspend fun pushSettings(settings: AppSettingsEntity) {
    val firestore = db ?: return
    try {
      firestore.collection("app_settings").document("main_settings").set(settingsToMap(settings), SetOptions.merge()).await()
    } catch (e: Exception) {
      Log.e(tag, "pushSettings failed: ${e.message}")
    }
  }

  suspend fun deleteOrderItem(itemId: String) {
    val firestore = db ?: return
    try {
      firestore.collection("order_items").document(itemId).delete().await()
    } catch (e: Exception) {
      Log.e(tag, "deleteOrderItem failed: ${e.message}")
    }
  }

  suspend fun pushSupervisorPermissions(perms: SupervisorPermissionEntity) {
    val firestore = db ?: return
    try {
      firestore.collection("supervisor_permissions").document(perms.userId).set(supervisorPermsToMap(perms), SetOptions.merge()).await()
    } catch (e: Exception) {
      Log.e(tag, "pushSupervisorPermissions failed: ${e.message}")
    }
  }

  suspend fun deleteSupervisorPermissions(userId: String) {
    val firestore = db ?: return
    try {
      firestore.collection("supervisor_permissions").document(userId).delete().await()
    } catch (e: Exception) {
      Log.e(tag, "deleteSupervisorPermissions failed: ${e.message}")
    }
  }

  suspend fun pushPromoCode(promo: ShippingPromoCodeEntity) {
    val firestore = db ?: return
    try {
      firestore.collection("shipping_promo_codes").document(promo.code).set(
        promoCodeToMap(promo),
        SetOptions.merge()
      ).await()
    } catch (e: Exception) {
      Log.e(tag, "pushPromoCode failed: ${e.message}")
    }
  }

  suspend fun deletePromoCode(code: String) {
    val firestore = db ?: return
    try {
      firestore.collection("shipping_promo_codes").document(code).delete().await()
    } catch (e: Exception) {
      Log.e(tag, "deletePromoCode failed: ${e.message}")
    }
  }

  // Seed remote database if empty
  suspend fun seedInitialAdminIfMissing() {
    val firestore = db ?: return
    try {
      val adminDoc = firestore.collection("users").document("admin_super_01").get().await()
      if (!adminDoc.exists()) {
        val admin = UserEntity(
          id = "admin_super_01",
          name = "محمود عمران (الإدارة الرئيسية)",
          email = "mahmoudomran324@gmail.com",
          phone = "01000000001",
          password = "Aa&9119045",
          role = "ADMIN",
          status = "APPROVED",
          governorate = "الإسكندرية",
          district = "سموحة",
          street = "شارع فوزي معاذ",
          apartment = "12",
          floor = "4",
          createdAt = System.currentTimeMillis()
        )
        pushUser(admin)
      }

      val promoDoc = firestore.collection("shipping_promo_codes").document("ALBAKAL").get().await()
      if (!promoDoc.exists()) {
        pushPromoCode(
          ShippingPromoCodeEntity(
            code = "ALBAKAL",
            discountAmount = 15.0,
            isFreeShipping = false,
            isActive = true
          )
        )
      }
    } catch (e: Exception) {
      Log.e(tag, "seedInitialAdminIfMissing failed: ${e.message}")
    }
  }

  // Mappers
  private fun userToMap(user: UserEntity): Map<String, Any?> = mapOf(
    "id" to user.id,
    "name" to user.name,
    "email" to user.email,
    "phone" to user.phone,
    "password" to user.password,
    "role" to user.role,
    "status" to user.status,
    "banReason" to user.banReason,
    "governorate" to user.governorate,
    "district" to user.district,
    "street" to user.street,
    "apartment" to user.apartment,
    "floor" to user.floor,
    "monthlySubscriptionActive" to user.monthlySubscriptionActive,
    "subscriptionExpiresAt" to user.subscriptionExpiresAt,
    "currentDebt" to user.currentDebt,
    "totalEarnings" to user.totalEarnings,
    "honorCodeAccepted" to user.honorCodeAccepted,
    "workingHoursType" to user.workingHoursType,
    "workStartHour" to user.workStartHour,
    "workEndHour" to user.workEndHour,
    "workingHoursDisplay" to user.workingHoursDisplay,
    "fcmToken" to user.fcmToken,
    "customShippingFee" to user.customShippingFee,
    "customCommissionPercent" to user.customCommissionPercent,
    "createdAt" to user.createdAt
  )

  fun docToUser(doc: DocumentSnapshot): UserEntity? {
    val id = doc.getString("id") ?: doc.id
    val name = doc.getString("name") ?: return null
    val email = doc.getString("email") ?: ""
    val phone = doc.getString("phone") ?: ""
    val password = doc.getString("password") ?: ""
    val role = doc.getString("role") ?: "CUSTOMER"
    val status = doc.getString("status") ?: "PENDING_APPROVAL"
    val banReason = doc.getString("banReason")
    val governorate = doc.getString("governorate") ?: ""
    val district = doc.getString("district") ?: ""
    val street = doc.getString("street") ?: ""
    val apartment = doc.getString("apartment") ?: ""
    val floor = doc.getString("floor") ?: ""
    val monthlySubscriptionActive = doc.getBoolean("monthlySubscriptionActive") ?: false
    val subscriptionExpiresAt = doc.getLong("subscriptionExpiresAt") ?: 0L
    val currentDebt = doc.getDouble("currentDebt") ?: 0.0
    val totalEarnings = doc.getDouble("totalEarnings") ?: 0.0
    val honorCodeAccepted = doc.getBoolean("honorCodeAccepted") ?: false
    val workingHoursType = doc.getString("workingHoursType") ?: "ALL_DAY"
    val workStartHour = doc.getString("workStartHour") ?: "00:00"
    val workEndHour = doc.getString("workEndHour") ?: "23:59"
    val workingHoursDisplay = doc.getString("workingHoursDisplay") ?: "متاح خدمة 24 ساعة"
    val fcmToken = doc.getString("fcmToken")
    val customShippingFee = doc.getDouble("customShippingFee")
    val customCommissionPercent = doc.getDouble("customCommissionPercent")
    val createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()

    return UserEntity(
      id = id,
      name = name,
      email = email,
      phone = phone,
      password = password,
      role = role,
      status = status,
      banReason = banReason,
      governorate = governorate,
      district = district,
      street = street,
      apartment = apartment,
      floor = floor,
      monthlySubscriptionActive = monthlySubscriptionActive,
      subscriptionExpiresAt = subscriptionExpiresAt,
      currentDebt = currentDebt,
      totalEarnings = totalEarnings,
      honorCodeAccepted = honorCodeAccepted,
      workingHoursType = workingHoursType,
      workStartHour = workStartHour,
      workEndHour = workEndHour,
      workingHoursDisplay = workingHoursDisplay,
      fcmToken = fcmToken,
      customShippingFee = customShippingFee,
      customCommissionPercent = customCommissionPercent,
      createdAt = createdAt
    )
  }

  private fun productToMap(p: ProductEntity): Map<String, Any?> = mapOf(
    "id" to p.id,
    "merchantId" to p.merchantId,
    "merchantName" to p.merchantName,
    "name" to p.name,
    "categoryName" to p.categoryName,
    "description" to p.description,
    "price" to p.price,
    "discountPrice" to p.discountPrice,
    "imageUrl" to p.imageUrl,
    "unit" to p.unit,
    "isPublished" to p.isPublished,
    "isOffer" to p.isOffer,
    "isAlbakalOffer" to p.isAlbakalOffer,
    "createdAt" to p.createdAt
  )

  private fun docToProduct(doc: DocumentSnapshot): ProductEntity? {
    val id = doc.getString("id") ?: doc.id
    val merchantId = doc.getString("merchantId") ?: ""
    val merchantName = doc.getString("merchantName") ?: ""
    val name = doc.getString("name") ?: return null
    val categoryName = doc.getString("categoryName") ?: ""
    val description = doc.getString("description") ?: ""
    val price = doc.getDouble("price") ?: 0.0
    val discountPrice = doc.getDouble("discountPrice")
    val imageUrl = doc.getString("imageUrl") ?: ""
    val unit = doc.getString("unit") ?: "قطعة"
    val isPublished = doc.getBoolean("isPublished") ?: true
    val isOffer = doc.getBoolean("isOffer") ?: false
    val isAlbakalOffer = doc.getBoolean("isAlbakalOffer") ?: false
    val createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()

    return ProductEntity(
      id = id,
      merchantId = merchantId,
      merchantName = merchantName,
      name = name,
      categoryName = categoryName,
      description = description,
      price = price,
      discountPrice = discountPrice,
      imageUrl = imageUrl,
      unit = unit,
      isPublished = isPublished,
      isOffer = isOffer,
      isAlbakalOffer = isAlbakalOffer,
      createdAt = createdAt
    )
  }

  private fun orderToMap(o: OrderEntity): Map<String, Any?> = mapOf(
    "id" to o.id,
    "orderNumber" to o.orderNumber,
    "customerId" to o.customerId,
    "customerName" to o.customerName,
    "customerPhone" to o.customerPhone,
    "customerAddress" to o.customerAddress,
    "merchantId" to o.merchantId,
    "merchantName" to o.merchantName,
    "totalProductsPrice" to o.totalProductsPrice,
    "shippingFee" to o.shippingFee,
    "grandTotal" to o.grandTotal,
    "appCommission" to o.appCommission,
    "paymentMethod" to o.paymentMethod,
    "paymentProofUrl" to o.paymentProofUrl,
    "paymentStatus" to o.paymentStatus,
    "orderStatus" to o.orderStatus,
    "captainName" to o.captainName,
    "captainPhone" to o.captainPhone,
    "isAcknowledgedByMerchant" to o.isAcknowledgedByMerchant,
    "customerConfirmedDelivered" to o.customerConfirmedDelivered,
    "merchantConfirmedDelivered" to o.merchantConfirmedDelivered,
    "cancelReason" to o.cancelReason,
    "cancelledBy" to o.cancelledBy,
    "createdAt" to o.createdAt,
    "updatedAt" to o.updatedAt
  )

  private fun docToOrder(doc: DocumentSnapshot): OrderEntity? {
    val id = doc.getString("id") ?: doc.id
    val orderNumber = doc.getString("orderNumber") ?: ""
    val customerId = doc.getString("customerId") ?: ""
    val customerName = doc.getString("customerName") ?: ""
    val customerPhone = doc.getString("customerPhone") ?: ""
    val customerAddress = doc.getString("customerAddress") ?: ""
    val merchantId = doc.getString("merchantId") ?: ""
    val merchantName = doc.getString("merchantName") ?: ""
    val totalProductsPrice = doc.getDouble("totalProductsPrice") ?: 0.0
    val shippingFee = doc.getDouble("shippingFee") ?: 0.0
    val grandTotal = doc.getDouble("grandTotal") ?: 0.0
    val appCommission = doc.getDouble("appCommission") ?: 0.0
    val paymentMethod = doc.getString("paymentMethod") ?: "CASH_ON_DELIVERY"
    val paymentProofUrl = doc.getString("paymentProofUrl")
    val paymentStatus = doc.getString("paymentStatus") ?: "CASH_ON_DELIVERY"
    val orderStatus = doc.getString("orderStatus") ?: "NEW"
    val captainName = doc.getString("captainName")
    val captainPhone = doc.getString("captainPhone")
    val isAcknowledgedByMerchant = doc.getBoolean("isAcknowledgedByMerchant") ?: false
    val customerConfirmedDelivered = doc.getBoolean("customerConfirmedDelivered") ?: false
    val merchantConfirmedDelivered = doc.getBoolean("merchantConfirmedDelivered") ?: false
    val cancelReason = doc.getString("cancelReason")
    val cancelledBy = doc.getString("cancelledBy")
    val createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()
    val updatedAt = doc.getLong("updatedAt") ?: System.currentTimeMillis()

    return OrderEntity(
      id = id,
      orderNumber = orderNumber,
      customerId = customerId,
      customerName = customerName,
      customerPhone = customerPhone,
      customerAddress = customerAddress,
      merchantId = merchantId,
      merchantName = merchantName,
      totalProductsPrice = totalProductsPrice,
      shippingFee = shippingFee,
      grandTotal = grandTotal,
      appCommission = appCommission,
      paymentMethod = paymentMethod,
      paymentProofUrl = paymentProofUrl,
      paymentStatus = paymentStatus,
      orderStatus = orderStatus,
      captainName = captainName,
      captainPhone = captainPhone,
      isAcknowledgedByMerchant = isAcknowledgedByMerchant,
      customerConfirmedDelivered = customerConfirmedDelivered,
      merchantConfirmedDelivered = merchantConfirmedDelivered,
      cancelReason = cancelReason,
      cancelledBy = cancelledBy,
      createdAt = createdAt,
      updatedAt = updatedAt
    )
  }

  private fun orderItemToMap(i: OrderItemEntity): Map<String, Any?> = mapOf(
    "id" to i.id,
    "orderId" to i.orderId,
    "productId" to i.productId,
    "productName" to i.productName,
    "quantity" to i.quantity,
    "unitPrice" to i.unitPrice,
    "totalPrice" to i.totalPrice,
    "unit" to i.unit
  )

  private fun docToOrderItem(doc: DocumentSnapshot): OrderItemEntity? {
    val id = doc.getString("id") ?: doc.id
    val orderId = doc.getString("orderId") ?: ""
    val productId = doc.getString("productId") ?: ""
    val productName = doc.getString("productName") ?: ""
    val quantity = doc.getLong("quantity")?.toInt() ?: 1
    val unitPrice = doc.getDouble("unitPrice") ?: 0.0
    val totalPrice = doc.getDouble("totalPrice") ?: (unitPrice * quantity)
    val unit = doc.getString("unit") ?: "قطعة"

    return OrderItemEntity(
      id = id,
      orderId = orderId,
      productId = productId,
      productName = productName,
      quantity = quantity,
      unitPrice = unitPrice,
      totalPrice = totalPrice,
      unit = unit
    )
  }

  private fun debtToMap(d: DebtPaymentRequestEntity): Map<String, Any?> = mapOf(
    "id" to d.id,
    "merchantId" to d.merchantId,
    "merchantName" to d.merchantName,
    "amount" to d.amount,
    "paymentMethod" to d.paymentMethod,
    "proofImageUrl" to d.proofImageUrl,
    "notes" to d.notes,
    "status" to d.status,
    "createdAt" to d.createdAt,
    "reviewedAt" to d.reviewedAt
  )

  private fun docToDebtPayment(doc: DocumentSnapshot): DebtPaymentRequestEntity? {
    val id = doc.getString("id") ?: doc.id
    val merchantId = doc.getString("merchantId") ?: ""
    val merchantName = doc.getString("merchantName") ?: ""
    val amount = doc.getDouble("amount") ?: 0.0
    val paymentMethod = doc.getString("paymentMethod") ?: "VODAFONE_CASH"
    val proofImageUrl = doc.getString("proofImageUrl")
    val notes = doc.getString("notes")
    val status = doc.getString("status") ?: "PENDING"
    val createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()
    val reviewedAt = doc.getLong("reviewedAt")

    return DebtPaymentRequestEntity(
      id = id,
      merchantId = merchantId,
      merchantName = merchantName,
      amount = amount,
      paymentMethod = paymentMethod,
      proofImageUrl = proofImageUrl,
      notes = notes,
      status = status,
      createdAt = createdAt,
      reviewedAt = reviewedAt
    )
  }

  private fun adToMap(a: SponsoredAdEntity): Map<String, Any?> = mapOf(
    "id" to a.id,
    "title" to a.title,
    "category" to a.category,
    "details" to a.details,
    "imageUrl" to a.imageUrl,
    "durationDays" to a.durationDays,
    "expiryDate" to a.expiryDate,
    "isActive" to a.isActive,
    "createdAt" to a.createdAt
  )

  private fun docToAd(doc: DocumentSnapshot): SponsoredAdEntity? {
    val id = doc.getString("id") ?: doc.id
    val title = doc.getString("title") ?: ""
    val category = doc.getString("category") ?: ""
    val details = doc.getString("details") ?: ""
    val imageUrl = doc.getString("imageUrl") ?: ""
    val durationDays = doc.getLong("durationDays")?.toInt() ?: 30
    val expiryDate = doc.getLong("expiryDate") ?: 0L
    val isActive = doc.getBoolean("isActive") ?: true
    val createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()

    return SponsoredAdEntity(
      id = id,
      title = title,
      category = category,
      details = details,
      imageUrl = imageUrl,
      durationDays = durationDays,
      expiryDate = expiryDate,
      isActive = isActive,
      createdAt = createdAt
    )
  }

  private fun areaToMap(a: GovernorateAreaEntity): Map<String, Any?> = mapOf(
    "id" to a.id,
    "governorate" to a.governorate,
    "district" to a.district
  )

  private fun docToArea(doc: DocumentSnapshot): GovernorateAreaEntity? {
    val id = doc.getString("id") ?: doc.id
    val gov = doc.getString("governorate") ?: ""
    val dist = doc.getString("district") ?: ""
    return GovernorateAreaEntity(id = id, governorate = gov, district = dist)
  }

  private fun categoryToMap(c: ProductCategoryEntity): Map<String, Any?> = mapOf(
    "id" to c.id,
    "name" to c.name,
    "displayOrder" to c.displayOrder
  )

  private fun docToCategory(doc: DocumentSnapshot): ProductCategoryEntity? {
    val id = doc.getString("id") ?: doc.id
    val name = doc.getString("name") ?: ""
    val order = doc.getLong("displayOrder")?.toInt() ?: 0
    return ProductCategoryEntity(id = id, name = name, displayOrder = order)
  }

  private fun settingsToMap(s: AppSettingsEntity): Map<String, Any?> = mapOf(
    "id" to s.id,
    "shippingFee" to s.shippingFee,
    "commissionRatePercent" to s.commissionRatePercent,
    "customerMonthlySubscriptionFee" to s.customerMonthlySubscriptionFee,
    "isCustomerSubscriptionActive" to s.isCustomerSubscriptionActive,
    "isCaptainsFleetInterfaceActive" to s.isCaptainsFleetInterfaceActive,
    "whatsappSupportNumber" to s.whatsappSupportNumber,
    "vodafoneCashNumber" to s.vodafoneCashNumber,
    "instaPayHandle" to s.instaPayHandle,
    "cashOnDeliveryEnabled" to s.cashOnDeliveryEnabled,
    "vodafoneCashEnabled" to s.vodafoneCashEnabled,
    "instaPayEnabled" to s.instaPayEnabled,
    "appLogoUrl" to s.appLogoUrl,
    "apkDownloadUrl" to s.apkDownloadUrl
  )

  private fun docToSettings(doc: DocumentSnapshot): AppSettingsEntity? {
    return AppSettingsEntity(
      id = 1,
      shippingFee = doc.getDouble("shippingFee") ?: 20.0,
      commissionRatePercent = doc.getDouble("commissionRatePercent") ?: 5.0,
      customerMonthlySubscriptionFee = doc.getDouble("customerMonthlySubscriptionFee") ?: 50.0,
      isCustomerSubscriptionActive = doc.getBoolean("isCustomerSubscriptionActive") ?: false,
      isCaptainsFleetInterfaceActive = doc.getBoolean("isCaptainsFleetInterfaceActive") ?: false,
      whatsappSupportNumber = doc.getString("whatsappSupportNumber") ?: "+201012345678",
      vodafoneCashNumber = doc.getString("vodafoneCashNumber") ?: "01099887766",
      instaPayHandle = doc.getString("instaPayHandle") ?: "albakal@instapay",
      cashOnDeliveryEnabled = doc.getBoolean("cashOnDeliveryEnabled") ?: true,
      vodafoneCashEnabled = doc.getBoolean("vodafoneCashEnabled") ?: true,
      instaPayEnabled = doc.getBoolean("instaPayEnabled") ?: true,
      appLogoUrl = doc.getString("appLogoUrl"),
      apkDownloadUrl = doc.getString("apkDownloadUrl") ?: "https://raw.githubusercontent.com/albakal/Albakal.omran-Group-/main/apk/albakal.apk"
    )
  }

  private fun announcementToMap(a: AnnouncementEntity): Map<String, Any?> = mapOf(
    "id" to a.id,
    "title" to a.title,
    "message" to a.message,
    "apkDownloadUrl" to a.apkDownloadUrl,
    "versionTag" to a.versionTag,
    "targetAudience" to a.targetAudience,
    "createdAt" to a.createdAt
  )

  private fun docToAnnouncement(doc: DocumentSnapshot): AnnouncementEntity? {
    val id = doc.getString("id") ?: doc.id
    val title = doc.getString("title") ?: return null
    val message = doc.getString("message") ?: ""
    val apkDownloadUrl = doc.getString("apkDownloadUrl")
    val versionTag = doc.getString("versionTag")
    val targetAudience = doc.getString("targetAudience") ?: "ALL"
    val createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()
    return AnnouncementEntity(
      id = id,
      title = title,
      message = message,
      apkDownloadUrl = apkDownloadUrl,
      versionTag = versionTag,
      targetAudience = targetAudience,
      createdAt = createdAt
    )
  }

  private fun supervisorPermsToMap(p: SupervisorPermissionEntity): Map<String, Any?> = mapOf(
    "userId" to p.userId,
    "canManageCustomers" to p.canManageCustomers,
    "canManageMerchants" to p.canManageMerchants,
    "canReviewRegistrations" to p.canReviewRegistrations,
    "canManageProducts" to p.canManageProducts,
    "canManageOrders" to p.canManageOrders,
    "canViewFinancials" to p.canViewFinancials,
    "canManageDebts" to p.canManageDebts,
    "canBanMerchants" to p.canBanMerchants,
    "canManageAds" to p.canManageAds,
    "canManageSupport" to p.canManageSupport
  )

  private fun docToSupervisorPermissions(doc: DocumentSnapshot): SupervisorPermissionEntity? {
    val userId = doc.getString("userId") ?: doc.id
    if (userId.isBlank()) return null
    return SupervisorPermissionEntity(
      userId = userId,
      canManageCustomers = doc.getBoolean("canManageCustomers") ?: false,
      canManageMerchants = doc.getBoolean("canManageMerchants") ?: false,
      canReviewRegistrations = doc.getBoolean("canReviewRegistrations") ?: false,
      canManageProducts = doc.getBoolean("canManageProducts") ?: false,
      canManageOrders = doc.getBoolean("canManageOrders") ?: false,
      canViewFinancials = doc.getBoolean("canViewFinancials") ?: false,
      canManageDebts = doc.getBoolean("canManageDebts") ?: false,
      canBanMerchants = doc.getBoolean("canBanMerchants") ?: false,
      canManageAds = doc.getBoolean("canManageAds") ?: false,
      canManageSupport = doc.getBoolean("canManageSupport") ?: false
    )
  }

  fun promoCodeToMap(promo: ShippingPromoCodeEntity): Map<String, Any?> = mapOf(
    "code" to promo.code,
    "discountAmount" to promo.discountAmount,
    "isFreeShipping" to promo.isFreeShipping,
    "isActive" to promo.isActive,
    "usedCustomerIds" to promo.usedCustomerIds,
    "createdAt" to promo.createdAt
  )

  fun docToPromoCode(doc: DocumentSnapshot): ShippingPromoCodeEntity? {
    val code = doc.getString("code") ?: doc.id
    if (code.isBlank()) return null
    val discountAmount = doc.getDouble("discountAmount") ?: 0.0
    val isFreeShipping = doc.getBoolean("isFreeShipping") ?: false
    val isActive = doc.getBoolean("isActive") ?: true
    val usedCustomerIds = doc.getString("usedCustomerIds") ?: ""
    val createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()

    return ShippingPromoCodeEntity(
      code = code,
      discountAmount = discountAmount,
      isFreeShipping = isFreeShipping,
      isActive = isActive,
      usedCustomerIds = usedCustomerIds,
      createdAt = createdAt
    )
  }
}
