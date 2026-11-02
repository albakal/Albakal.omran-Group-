package com.example.data.repository

import com.example.data.local.AppDao
import com.example.data.model.*
import com.example.data.remote.FirestoreSyncManager
import com.example.util.PhoneHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class AlbakalRepository(
  private val dao: AppDao,
  private val syncManager: FirestoreSyncManager? = null
) {

  suspend fun insertLocalUser(user: UserEntity) {
    dao.insertUser(user)
  }

  // Users & Auth
  suspend fun login(emailOrPhone: String, pass: String): Result<UserEntity> {
    val clean = emailOrPhone.trim()
    val isEmail = clean.contains("@")
    val variants = if (!isEmail) PhoneHelper.getPhoneVariants(clean) else emptyList()
    var user: UserEntity? = null

    if (isEmail) {
      user = dao.getUserByEmail(clean.lowercase()) ?: dao.getUserByEmail(clean)
    } else {
      for (variant in variants) {
        val found = dao.getUserByPhone(variant)
        if (found != null) {
          user = found
          break
        }
      }
    }

    // If not found in local Room yet (fresh device / different phone), fetch directly from Firestore
    if (user == null && syncManager != null) {
      val firestore = syncManager.getFirestore()
      if (firestore != null) {
        try {
          if (isEmail) {
            val q1 = firestore.collection("users")
              .whereEqualTo("email", clean.lowercase())
              .limit(1)
              .get()
              .await()
            if (!q1.isEmpty) {
              user = syncManager.docToUser(q1.documents.first())
            } else {
              val q2 = firestore.collection("users")
                .whereEqualTo("email", clean)
                .limit(1)
                .get()
                .await()
              if (!q2.isEmpty) {
                user = syncManager.docToUser(q2.documents.first())
              }
            }
          } else {
            // 1. Try querying whereIn for phone variants
            try {
              val qVariants = firestore.collection("users")
                .whereIn("phone", variants.take(10))
                .limit(1)
                .get()
                .await()
              if (!qVariants.isEmpty) {
                user = syncManager.docToUser(qVariants.documents.first())
              }
            } catch (_: Exception) {}

            // 2. If not found yet, query exact clean phone
            if (user == null) {
              for (v in variants) {
                val q = firestore.collection("users")
                  .whereEqualTo("phone", v)
                  .limit(1)
                  .get()
                  .await()
                if (!q.isEmpty) {
                  user = syncManager.docToUser(q.documents.first())
                  break
                }
              }
            }
          }

          // 3. Fallback scan: if indexing differences or prefixes prevented direct match, scan collection
          if (user == null) {
            val allUsersDocs = firestore.collection("users").get().await()
            for (doc in allUsersDocs.documents) {
              val docPhone = doc.getString("phone")
              val docEmail = doc.getString("email")
              if (isEmail) {
                if (docEmail.equals(clean, ignoreCase = true)) {
                  user = syncManager.docToUser(doc)
                  break
                }
              } else {
                if (PhoneHelper.matchesPhone(docPhone, clean) || variants.contains(docPhone?.trim())) {
                  user = syncManager.docToUser(doc)
                  break
                }
              }
            }
          }

          // If found from Firestore, cache it locally in Room
          if (user != null) {
            dao.insertUser(user)
          }
        } catch (e: Exception) {
          android.util.Log.e("AlbakalRepository", "Firestore login search failed: ${e.message}")
        }
      }
    }

    if (user == null) {
      return Result.failure(Exception("الحساب غير موجود، يرجى التأكد من البيانات المدخلة أو إنشاء حساب جديد"))
    }

    val cleanPass = pass.trim()
    val userPass = user.password.trim()
    val passMatches = cleanPass == userPass ||
      PhoneHelper.normalizeDigits(cleanPass) == PhoneHelper.normalizeDigits(userPass)

    if (!passMatches) {
      return Result.failure(Exception("كلمة المرور غير صحيحة"))
    }

    if (user.status == "PENDING_APPROVAL") {
      return Result.failure(Exception("الحساب قيد المراجعة والموافقة من الإدارة. يرجى الانتظار حتى يتم تفعيل حسابك."))
    }

    if (user.status == "BANNED") {
      val reason = user.banReason?.let { ": $it" } ?: ""
      return Result.failure(Exception("تم حظر هذا الحساب من قبل الإدارة$reason. يرجى التواصل مع الدعم الفني."))
    }

    if (user.status == "REJECTED") {
      return Result.failure(Exception("عذراً، تم رفض طلب انضمام هذا الحساب من قبل الإدارة."))
    }

    return Result.success(user)
  }

  suspend fun registerUser(user: UserEntity): Result<Unit> {
    val normPhone = PhoneHelper.cleanPhone(user.phone).ifBlank { user.phone.trim() }
    val normEmail = user.email.trim()
    val normalizedUser = user.copy(
      phone = normPhone,
      email = normEmail
    )

    // Check duplicate email or phone locally
    val existingEmail = dao.getUserByEmail(normEmail.lowercase()) ?: dao.getUserByEmail(normEmail)
    if (existingEmail != null) {
      return Result.failure(Exception("البريد الإلكتروني مسجل مسبقاً في النظام"))
    }

    val variants = PhoneHelper.getPhoneVariants(normPhone)
    for (v in variants) {
      if (dao.getUserByPhone(v) != null) {
        return Result.failure(Exception("رقم الهاتف مسجل مسبقاً في النظام"))
      }
    }

    // Insert locally in Room
    dao.insertUser(normalizedUser)

    // Sync to Firestore in real-time so other devices receive registration immediately
    syncManager?.pushUser(normalizedUser)

    return Result.success(Unit)
  }

  fun getAllUsers(): Flow<List<UserEntity>> = dao.getAllUsers()
  fun getUsersByRole(role: String): Flow<List<UserEntity>> = dao.getUsersByRole(role)
  fun getPendingUsers(): Flow<List<UserEntity>> = dao.getPendingUsers()
  suspend fun getUserById(id: String): UserEntity? = dao.getUserById(id)

  suspend fun updateUser(user: UserEntity) {
    dao.updateUser(user)
    syncManager?.pushUser(user)
  }

  suspend fun deleteUser(id: String) {
    dao.deleteUserById(id)
    syncManager?.deleteUser(id)
  }

  suspend fun approveUser(userId: String) {
    val user = dao.getUserById(userId) ?: return
    val approved = user.copy(status = "APPROVED")
    dao.updateUser(approved)
    syncManager?.pushUser(approved)
  }

  suspend fun rejectUser(userId: String) {
    val user = dao.getUserById(userId) ?: return
    val rejected = user.copy(status = "REJECTED")
    dao.updateUser(rejected)
    syncManager?.pushUser(rejected)
  }

  suspend fun banMerchant(merchantId: String, reason: String) {
    val merchant = dao.getUserById(merchantId) ?: return
    val banned = merchant.copy(status = "BANNED", banReason = reason)
    dao.updateUser(banned)
    syncManager?.pushUser(banned)
  }

  suspend fun unbanMerchant(merchantId: String) {
    val merchant = dao.getUserById(merchantId) ?: return
    val unbanned = merchant.copy(status = "APPROVED", banReason = null)
    dao.updateUser(unbanned)
    syncManager?.pushUser(unbanned)
  }

  suspend fun acceptHonorCode(merchantId: String) {
    val merchant = dao.getUserById(merchantId) ?: return
    val updated = merchant.copy(honorCodeAccepted = true)
    dao.updateUser(updated)
    syncManager?.pushUser(updated)
  }

  suspend fun updateAdminCredentials(adminId: String, newEmail: String, newPass: String): Result<Unit> {
    val admin = dao.getUserById(adminId) ?: return Result.failure(Exception("الحساب غير موجود"))
    if (admin.role != "ADMIN") {
      return Result.failure(Exception("لا توجد صلاحية لتعديل حساب الإدارة الرئيسي"))
    }
    val updated = admin.copy(email = newEmail.trim(), password = newPass.trim())
    dao.updateUser(updated)
    syncManager?.pushUser(updated)
    return Result.success(Unit)
  }

  // Supervisor & Permissions
  fun getSupervisorPermissions(userId: String): Flow<SupervisorPermissionEntity?> = dao.getPermissionsForUser(userId)
  suspend fun getSupervisorPermissionsDirect(userId: String): SupervisorPermissionEntity? = dao.getPermissionsDirect(userId)
  suspend fun saveSupervisorPermissions(permissions: SupervisorPermissionEntity) = dao.insertPermission(permissions)

  suspend fun createSupervisor(
    name: String,
    email: String,
    phone: String,
    pass: String,
    permissions: SupervisorPermissionEntity
  ): Result<Unit> {
    val id = "sup_" + UUID.randomUUID().toString().take(8)
    val user = UserEntity(
      id = id,
      name = name,
      email = email,
      phone = phone,
      password = pass,
      role = "SUPERVISOR",
      status = "APPROVED",
      governorate = "الإسكندرية",
      district = "الإدارة",
      street = "المقر الإداري",
      apartment = "1",
      floor = "1"
    )
    val reg = registerUser(user)
    if (reg.isFailure) return reg
    val perms = permissions.copy(userId = id)
    dao.insertPermission(perms)
    syncManager?.pushSupervisorPermissions(perms)
    return Result.success(Unit)
  }

  suspend fun updateSupervisor(
    userId: String,
    name: String,
    phone: String,
    pass: String?,
    permissions: SupervisorPermissionEntity
  ): Result<Unit> {
    val user = dao.getUserById(userId) ?: return Result.failure(Exception("حساب المشرف غير موجود"))
    val updated = user.copy(
      name = name.trim(),
      phone = phone.trim(),
      password = if (!pass.isNullOrBlank()) pass.trim() else user.password
    )
    dao.updateUser(updated)
    syncManager?.pushUser(updated)
    val updatedPerms = permissions.copy(userId = userId)
    dao.insertPermission(updatedPerms)
    syncManager?.pushSupervisorPermissions(updatedPerms)
    return Result.success(Unit)
  }

  suspend fun deleteSupervisor(userId: String): Result<Unit> {
    dao.deleteUserById(userId)
    dao.deleteSupervisorPermissions(userId)
    syncManager?.deleteUser(userId)
    syncManager?.deleteSupervisorPermissions(userId)
    return Result.success(Unit)
  }

  suspend fun deleteCustomerPermanently(customerId: String): Result<Unit> {
    dao.deleteUserById(customerId)
    dao.deleteCartByCustomerId(customerId)
    dao.deleteOrdersByCustomerId(customerId)
    syncManager?.deleteUser(customerId)
    return Result.success(Unit)
  }

  suspend fun deleteMerchantPermanently(merchantId: String): Result<Unit> {
    dao.deleteUserById(merchantId)
    dao.deleteProductsByMerchantId(merchantId)
    dao.deleteOrdersByMerchantId(merchantId)
    dao.deleteDebtPaymentsByMerchantId(merchantId)
    syncManager?.deleteUser(merchantId)
    return Result.success(Unit)
  }

  // Governorates & Districts
  fun getAllAreas(): Flow<List<GovernorateAreaEntity>> = dao.getAllAreas()
  suspend fun addArea(gov: String, dist: String) {
    val area = GovernorateAreaEntity("area_" + UUID.randomUUID().toString().take(8), gov.trim(), dist.trim())
    dao.insertArea(area)
    syncManager?.pushArea(area)
  }
  suspend fun updateArea(id: String, gov: String, dist: String) {
    val area = GovernorateAreaEntity(id, gov.trim(), dist.trim())
    dao.updateArea(area)
    syncManager?.pushArea(area)
  }
  suspend fun deleteArea(id: String) {
    dao.deleteAreaById(id)
    syncManager?.deleteArea(id)
  }

  // Categories
  fun getAllCategories(): Flow<List<ProductCategoryEntity>> = dao.getAllCategories()
  suspend fun addCategory(name: String, order: Int = 0) {
    val cat = ProductCategoryEntity("cat_" + UUID.randomUUID().toString().take(8), name.trim(), order)
    dao.insertCategory(cat)
    syncManager?.pushCategory(cat)
  }
  suspend fun updateCategory(id: String, name: String, order: Int = 0) {
    val cat = ProductCategoryEntity(id, name.trim(), order)
    dao.updateCategory(cat)
    syncManager?.pushCategory(cat)
  }
  suspend fun deleteCategory(id: String) {
    dao.deleteCategoryById(id)
    syncManager?.deleteCategory(id)
  }

  // Products
  fun getAllProducts(): Flow<List<ProductEntity>> = dao.getAllProducts()
  fun getPublishedProducts(): Flow<List<ProductEntity>> = dao.getPublishedProducts()
  fun getProductsByMerchant(merchantId: String): Flow<List<ProductEntity>> = dao.getProductsByMerchant(merchantId)
  suspend fun addProduct(product: ProductEntity) {
    dao.insertProduct(product)
    syncManager?.pushProduct(product)
  }
  suspend fun updateProduct(product: ProductEntity) {
    dao.updateProduct(product)
    syncManager?.pushProduct(product)
  }
  suspend fun deleteProduct(id: String) {
    dao.deleteProductById(id)
    syncManager?.deleteProduct(id)
  }

  // Sponsored Ads
  fun getAllAds(): Flow<List<SponsoredAdEntity>> = dao.getAllAds()
  fun getActiveAds(): Flow<List<SponsoredAdEntity>> = dao.getActiveAds()
  suspend fun addAd(ad: SponsoredAdEntity) {
    dao.insertAd(ad)
    syncManager?.pushAd(ad)
  }
  suspend fun updateAd(ad: SponsoredAdEntity) {
    dao.updateAd(ad)
    syncManager?.pushAd(ad)
  }
  suspend fun deleteAd(id: String) {
    dao.deleteAdById(id)
    syncManager?.deleteAd(id)
  }

  // Cart (Local to customer device)
  fun getCart(customerId: String): Flow<List<CartItemEntity>> = dao.getCartForCustomer(customerId)
  suspend fun addToCart(
    customerId: String,
    productId: String,
    productName: String,
    merchantId: String,
    merchantName: String,
    price: Double,
    quantity: Int,
    imageUrl: String,
    unit: String = "قطعة"
  ) {
    val id = "${customerId}_${productId}"
    dao.insertCartItem(
      CartItemEntity(
        id = id,
        customerId = customerId,
        productId = productId,
        productName = productName,
        merchantId = merchantId,
        merchantName = merchantName,
        price = price,
        quantity = quantity,
        imageUrl = imageUrl,
        unit = unit
      )
    )
  }
  suspend fun updateCartQuantity(cartItemId: String, quantity: Int) {
    if (quantity <= 0) {
      dao.deleteCartItem(cartItemId)
    }
  }
  suspend fun removeCartItem(cartItemId: String) = dao.deleteCartItem(cartItemId)
  suspend fun clearCart(customerId: String) = dao.clearCartForCustomer(customerId)

  // Orders
  fun getAllOrders(): Flow<List<OrderEntity>> = dao.getAllOrders()
  fun getOrdersForCustomer(customerId: String): Flow<List<OrderEntity>> = dao.getOrdersForCustomer(customerId)
  fun getOrdersForMerchant(merchantId: String): Flow<List<OrderEntity>> = dao.getOrdersForMerchant(merchantId)
  fun getNewUnacknowledgedOrders(merchantId: String): Flow<List<OrderEntity>> = dao.getNewUnacknowledgedOrdersForMerchant(merchantId)
  fun getOrderItems(orderId: String): Flow<List<OrderItemEntity>> = dao.getOrderItems(orderId)

  suspend fun placeOrder(
    customer: UserEntity,
    cartItems: List<CartItemEntity>,
    shippingFee: Double,
    commissionPercent: Double,
    paymentMethod: String,
    paymentProofUrl: String? = null,
    promoCode: String? = null
  ): Result<String> {
    if (cartItems.isEmpty()) return Result.failure(Exception("سلة المشتريات فارغة"))

    // Check promo code if supplied
    var promoDiscount = 0.0
    var promoEntity: ShippingPromoCodeEntity? = null
    if (!promoCode.isNullOrBlank()) {
      val foundPromo = dao.getPromoCode(promoCode.trim().uppercase())
      if (foundPromo != null && foundPromo.isActive) {
        val usedCustomers = foundPromo.usedCustomerIds.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        if (!usedCustomers.contains(customer.id)) {
          promoDiscount = foundPromo.discountAmount
          promoEntity = foundPromo
        }
      }
    }

    // Group items by merchant
    val merchantGroups = cartItems.groupBy { it.merchantId }
    val createdOrderIds = mutableListOf<String>()

    for ((mId, items) in merchantGroups) {
      val orderId = "ord_" + UUID.randomUUID().toString().take(10)
      val orderNumber = "ORD-" + (100000 + (Math.random() * 900000).toInt())
      val mName = items.first().merchantName
      val merchantUser = dao.getUserById(mId)

      val effectiveShippingFee = merchantUser?.customShippingFee ?: shippingFee
      val merchantCommissionRate = merchantUser?.customCommissionPercent ?: commissionPercent

      // Promo discount applies directly to the shipping fee
      val finalShippingFee = if (promoEntity != null && promoEntity.isFreeShipping) {
        0.0
      } else {
        (effectiveShippingFee - promoDiscount).coerceAtLeast(0.0)
      }

      val totalProductsPrice = items.sumOf { it.price * it.quantity }
      val grandTotal = totalProductsPrice + finalShippingFee

      // Commission is deducted EXCLUSIVELY from the shipping fee (not from products total)
      val appCommission = (finalShippingFee * (merchantCommissionRate / 100.0))

      val order = OrderEntity(
        id = orderId,
        orderNumber = orderNumber,
        customerId = customer.id,
        customerName = customer.name,
        customerPhone = customer.phone,
        customerAddress = "${customer.governorate} - ${customer.district} - ${customer.street} - عمارة/شقة ${customer.apartment} - الدور ${customer.floor}",
        merchantId = mId,
        merchantName = mName,
        totalProductsPrice = totalProductsPrice,
        shippingFee = finalShippingFee,
        grandTotal = grandTotal,
        appCommission = appCommission,
        paymentMethod = paymentMethod,
        paymentProofUrl = paymentProofUrl,
        paymentStatus = if (paymentMethod == "CASH_ON_DELIVERY") "CASH_ON_DELIVERY" else "PENDING_VERIFICATION",
        orderStatus = "NEW",
        isAcknowledgedByMerchant = false
      )
      dao.insertOrder(order)

      val orderItems = items.map { item ->
        OrderItemEntity(
          id = UUID.randomUUID().toString(),
          orderId = orderId,
          productId = item.productId,
          productName = item.productName,
          quantity = item.quantity,
          unitPrice = item.price,
          totalPrice = item.price * item.quantity,
          unit = item.unit
        )
      }
      dao.insertOrderItems(orderItems)

      // Sync to cloud Firestore
      syncManager?.pushOrder(order, orderItems)
      createdOrderIds.add(orderNumber)
    }

    // Mark promo code as used by customer (strictly one-time use per customer)
    if (promoEntity != null) {
      val existingList = promoEntity.usedCustomerIds.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toMutableList()
      if (!existingList.contains(customer.id)) {
        existingList.add(customer.id)
        val updatedPromo = promoEntity.copy(usedCustomerIds = existingList.joinToString(","))
        dao.insertPromoCode(updatedPromo)
        syncManager?.pushPromoCode(updatedPromo)
      }
    }

    dao.clearCartForCustomer(customer.id)
    return Result.success(createdOrderIds.joinToString(", "))
  }

  suspend fun acknowledgeOrder(orderId: String) {
    val order = dao.getOrderById(orderId) ?: return
    val updated = order.copy(
      isAcknowledgedByMerchant = true,
      orderStatus = "PREPARING",
      updatedAt = System.currentTimeMillis()
    )
    dao.updateOrder(updated)
    syncManager?.updateOrderStatus(updated)
  }

  suspend fun updatePreparedOrderItems(
    orderId: String,
    preparedItemIds: Set<String>
  ) {
    val order = dao.getOrderById(orderId) ?: return
    val allItems = dao.getOrderItemsDirect(orderId)
    val preparedItems = allItems.filter { it.id in preparedItemIds }
    val removedItems = allItems.filter { it.id !in preparedItemIds }

    // Delete items that were unselected / not prepared by the merchant
    for (removed in removedItems) {
      dao.deleteOrderItem(removed.id)
      syncManager?.deleteOrderItem(removed.id)
    }

    // Automatically recalculate product subtotal from shaded/checked items only
    val newProductsTotal = preparedItems.sumOf { it.totalPrice }
    // Commission is calculated exclusively from the shipping fee, NOT products total
    val newGrandTotal = newProductsTotal + order.shippingFee

    val updated = order.copy(
      totalProductsPrice = newProductsTotal,
      appCommission = order.appCommission,
      grandTotal = newGrandTotal,
      isAcknowledgedByMerchant = true,
      orderStatus = "PREPARING",
      updatedAt = System.currentTimeMillis()
    )
    dao.updateOrder(updated)
    syncManager?.updateOrderStatus(updated)
  }

  suspend fun shipOrder(orderId: String, captainName: String, captainPhone: String) {
    val order = dao.getOrderById(orderId) ?: return
    val updated = order.copy(
      orderStatus = "SHIPPED",
      captainName = captainName.trim(),
      captainPhone = captainPhone.trim(),
      updatedAt = System.currentTimeMillis()
    )
    dao.updateOrder(updated)
    syncManager?.updateOrderStatus(updated)
  }

  suspend fun merchantConfirmDelivered(orderId: String) {
    val order = dao.getOrderById(orderId) ?: return
    val wasDelivered = order.orderStatus == "DELIVERED"
    val updated = order.copy(
      merchantConfirmedDelivered = true,
      orderStatus = "DELIVERED",
      updatedAt = System.currentTimeMillis()
    )
    dao.updateOrder(updated)
    syncManager?.updateOrderStatus(updated)

    if (!wasDelivered) {
      applyDeliveryFinancials(order)
    }
  }

  suspend fun customerConfirmDelivered(orderId: String) {
    val order = dao.getOrderById(orderId) ?: return
    val wasDelivered = order.orderStatus == "DELIVERED"
    val updated = order.copy(
      customerConfirmedDelivered = true,
      orderStatus = "DELIVERED",
      updatedAt = System.currentTimeMillis()
    )
    dao.updateOrder(updated)
    syncManager?.updateOrderStatus(updated)

    if (!wasDelivered) {
      applyDeliveryFinancials(order)
    }
  }

  suspend fun getOrderById(orderId: String): OrderEntity? = dao.getOrderById(orderId)

  suspend fun cancelOrder(orderId: String, reason: String, cancelledBy: String) {
    val order = dao.getOrderById(orderId) ?: return
    val updated = order.copy(
      orderStatus = "CANCELLED",
      cancelReason = reason,
      cancelledBy = cancelledBy,
      appCommission = 0.0, // لا يتم احتساب أي عمولة للتطبيق على هذا الأوردر الملغي
      updatedAt = System.currentTimeMillis()
    )
    dao.updateOrder(updated)
    syncManager?.updateOrderStatus(updated)
  }

  private suspend fun applyDeliveryFinancials(order: OrderEntity) {
    val merchant = dao.getUserById(order.merchantId) ?: return
    // Automatically calculate sales value into monthly/total earnings,
    // and automatically calculate app commission into current debt
    val newEarnings = merchant.totalEarnings + order.totalProductsPrice
    val newDebt = merchant.currentDebt + order.appCommission
    val updatedMerchant = merchant.copy(
      totalEarnings = newEarnings,
      currentDebt = newDebt
    )
    dao.updateUser(updatedMerchant)
    syncManager?.pushUser(updatedMerchant)
  }

  // Debt Payments
  fun getAllDebtPayments(): Flow<List<DebtPaymentRequestEntity>> = dao.getAllDebtPayments()
  fun getDebtPaymentsForMerchant(merchantId: String): Flow<List<DebtPaymentRequestEntity>> = dao.getDebtPaymentsForMerchant(merchantId)

  suspend fun submitDebtPayment(
    merchantId: String,
    merchantName: String,
    amount: Double,
    method: String,
    proofUrl: String?,
    notes: String?
  ) {
    val req = DebtPaymentRequestEntity(
      id = "debt_pay_" + UUID.randomUUID().toString().take(8),
      merchantId = merchantId,
      merchantName = merchantName,
      amount = amount,
      paymentMethod = method,
      proofImageUrl = proofUrl,
      notes = notes,
      status = "PENDING"
    )
    dao.insertDebtPayment(req)
    syncManager?.pushDebtPayment(req)
  }

  suspend fun approveDebtPayment(paymentId: String, payment: DebtPaymentRequestEntity) {
    val approved = payment.copy(status = "APPROVED", reviewedAt = System.currentTimeMillis())
    dao.updateDebtPayment(approved)
    syncManager?.pushDebtPayment(approved)

    val merchant = dao.getUserById(payment.merchantId)
    if (merchant != null) {
      // Calculate remaining debt. If paid amount > debt, currentDebt becomes negative (positive credit balance)
      val newDebt = merchant.currentDebt - payment.amount
      val updatedMerchant = merchant.copy(currentDebt = newDebt)
      dao.updateUser(updatedMerchant)
      syncManager?.pushUser(updatedMerchant)
    }
  }

  suspend fun adminSettleMerchantDebt(
    merchantId: String,
    amount: Double,
    notes: String? = null
  ) {
    val merchant = dao.getUserById(merchantId) ?: return
    val newDebt = merchant.currentDebt - amount
    val updatedMerchant = merchant.copy(currentDebt = newDebt)
    dao.updateUser(updatedMerchant)
    syncManager?.pushUser(updatedMerchant)

    val req = DebtPaymentRequestEntity(
      id = "admin_settle_" + UUID.randomUUID().toString().take(8),
      merchantId = merchantId,
      merchantName = merchant.name,
      amount = amount,
      paymentMethod = "تسوية إدارة / كاش",
      proofImageUrl = null,
      notes = notes ?: "تسوية مديونية مباشرة عبر لوحة التحكم",
      status = "APPROVED",
      reviewedAt = System.currentTimeMillis()
    )
    dao.insertDebtPayment(req)
    syncManager?.pushDebtPayment(req)
  }

  suspend fun rejectDebtPayment(payment: DebtPaymentRequestEntity) {
    val rejected = payment.copy(status = "REJECTED", reviewedAt = System.currentTimeMillis())
    dao.updateDebtPayment(rejected)
    syncManager?.pushDebtPayment(rejected)
  }

  // Support & Complaints
  fun getAllComplaints(): Flow<List<SupportComplaintEntity>> = dao.getAllComplaints()
  fun getComplaintsForUser(userId: String): Flow<List<SupportComplaintEntity>> = dao.getComplaintsForUser(userId)
  suspend fun submitComplaint(complaint: SupportComplaintEntity) = dao.insertComplaint(complaint)
  suspend fun resolveComplaint(complaint: SupportComplaintEntity, adminResponse: String) {
    dao.updateComplaint(complaint.copy(status = "RESOLVED", adminNotes = adminResponse))
  }

  // Password Reset
  fun getAllPasswordResets(): Flow<List<PasswordResetRequestEntity>> = dao.getAllPasswordResets()

  suspend fun requestPasswordReset(phone: String): Result<String> {
    val user = dao.getUserByPhone(phone.trim())
      ?: return Result.failure(Exception("لم يتم العثور على حساب مرتبط برقم الهاتف المدخل"))
    val code = (100000 + (Math.random() * 900000).toInt()).toString()
    val req = PasswordResetRequestEntity(
      id = "reset_" + UUID.randomUUID().toString().take(8),
      phone = phone.trim(),
      userEmail = user.email,
      userName = user.name,
      tempResetCode = code,
      status = "PENDING"
    )
    dao.insertPasswordReset(req)
    return Result.success(code)
  }

  suspend fun verifyAndResetPassword(phone: String, code: String, newPass: String): Result<Unit> {
    val user = dao.getUserByPhone(phone.trim())
      ?: return Result.failure(Exception("رقم الهاتف غير موجود"))
    val updated = user.copy(password = newPass.trim())
    dao.updateUser(updated)
    syncManager?.pushUser(updated)
    return Result.success(Unit)
  }

  suspend fun updateMerchantShippingFee(userId: String, shippingFee: Double?): Result<Unit> {
    dao.updateMerchantShippingFee(userId, shippingFee)
    val user = dao.getUserById(userId)
    if (user != null) {
      syncManager?.pushUser(user)
    }
    return Result.success(Unit)
  }

  suspend fun updateMerchantShippingAndCommission(userId: String, shippingFee: Double?, commissionPercent: Double?): Result<Unit> {
    dao.updateMerchantShippingAndCommission(userId, shippingFee, commissionPercent)
    val user = dao.getUserById(userId)
    if (user != null) {
      syncManager?.pushUser(user)
    }
    return Result.success(Unit)
  }

  // Shipping Promo Codes
  fun getAllPromoCodes(): Flow<List<ShippingPromoCodeEntity>> = dao.getAllPromoCodes()
  suspend fun getPromoCode(code: String): ShippingPromoCodeEntity? = dao.getPromoCode(code.trim().uppercase())
  suspend fun createPromoCode(promo: ShippingPromoCodeEntity): Result<Unit> {
    val norm = promo.copy(code = promo.code.trim().uppercase())
    dao.insertPromoCode(norm)
    syncManager?.pushPromoCode(norm)
    return Result.success(Unit)
  }
  suspend fun deletePromoCode(code: String): Result<Unit> {
    val clean = code.trim().uppercase()
    dao.deletePromoCode(clean)
    syncManager?.deletePromoCode(clean)
    return Result.success(Unit)
  }

  // App Settings
  fun getSettings(): Flow<AppSettingsEntity?> = dao.getSettings()
  suspend fun getSettingsDirect(): AppSettingsEntity = dao.getSettingsDirect() ?: AppSettingsEntity()
  suspend fun updateSettings(settings: AppSettingsEntity) {
    dao.updateSettings(settings)
    syncManager?.pushSettings(settings)
  }

  // FCM Token Binding
  suspend fun updateUserFcmToken(userId: String, token: String?) {
    dao.updateUserFcmToken(userId, token)
    syncManager?.updateUserFcmToken(userId, token)
  }

  suspend fun clearUserFcmToken(userId: String) {
    dao.updateUserFcmToken(userId, null)
    syncManager?.clearUserFcmToken(userId)
  }

  // Announcements & Updates
  fun getAllAnnouncements(): Flow<List<AnnouncementEntity>> = dao.getAllAnnouncements()
  fun getAnnouncementsForAudience(audience: String): Flow<List<AnnouncementEntity>> = dao.getAnnouncementsForAudience(audience)
  fun getLatestAnnouncement(): Flow<AnnouncementEntity?> = dao.getLatestAnnouncement()

  suspend fun postAnnouncement(announcement: AnnouncementEntity) {
    dao.insertAnnouncement(announcement)
    syncManager?.pushAnnouncement(announcement)
  }

  suspend fun deleteAnnouncement(id: String) {
    dao.deleteAnnouncementById(id)
    syncManager?.deleteAnnouncement(id)
  }
}
