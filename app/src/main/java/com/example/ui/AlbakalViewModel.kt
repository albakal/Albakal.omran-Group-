package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AlbakalDatabase
import com.example.data.model.AnnouncementEntity
import com.example.data.model.AppSettingsEntity
import java.util.UUID
import com.example.data.model.CartItemEntity
import com.example.data.model.DebtPaymentRequestEntity
import com.example.data.model.GovernorateAreaEntity
import com.example.data.model.OrderEntity
import com.example.data.model.OrderItemEntity
import com.example.data.model.PasswordResetRequestEntity
import com.example.data.model.ProductCategoryEntity
import com.example.data.model.ProductEntity
import com.example.data.model.SponsoredAdEntity
import com.example.data.model.SupervisorPermissionEntity
import com.example.data.model.SupportComplaintEntity
import com.example.data.model.UserEntity
import com.example.data.model.ShippingPromoCodeEntity
import com.example.data.repository.AlbakalRepository
import com.example.util.EgyptTimeHelper
import com.example.util.OrderAlarmManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import com.example.data.remote.FirestoreSyncManager
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AlbakalViewModel(application: Application) : AndroidViewModel(application) {
  private val repository: AlbakalRepository
  private val alarmManager: OrderAlarmManager = OrderAlarmManager(application)
  private val syncManager: FirestoreSyncManager
  private val sessionPrefs = application.getSharedPreferences("albakal_session_prefs", android.content.Context.MODE_PRIVATE)

  init {
    val db = AlbakalDatabase.getDatabase(application)
    syncManager = FirestoreSyncManager(application, db.appDao())
    repository = AlbakalRepository(db.appDao(), syncManager)
    viewModelScope.launch {
      syncManager.seedInitialAdminIfMissing()
      // Auto-restore login session so user remains logged in permanently until explicit logout
      val savedUserId = sessionPrefs.getString("saved_user_id", null)
      if (!savedUserId.isNullOrBlank()) {
        var user = repository.getUserById(savedUserId)
        if (user == null) {
          try {
            val firestore = syncManager.getFirestore()
            if (firestore != null) {
              val doc = firestore.collection("users").document(savedUserId).get().await()
              if (doc.exists()) {
                val fetched = syncManager.docToUser(doc)
                if (fetched != null) {
                  repository.insertLocalUser(fetched)
                  user = fetched
                }
              }
            }
          } catch (e: Exception) {
            android.util.Log.e("AlbakalViewModel", "Session user restore error: ${e.message}")
          }
        }
        if (user != null && user.status != "REJECTED" && user.status != "BANNED") {
          _currentUser.value = user
          when (user.role) {
            "ADMIN" -> _currentScreen.value = "ADMIN_DASHBOARD"
            "SUPERVISOR" -> {
              loadSupervisorPermissions(user.id)
              _currentScreen.value = "ADMIN_DASHBOARD"
            }
            "MERCHANT" -> {
              _currentScreen.value = "MERCHANT_HOME"
              startListeningForMerchantOrders(user.id)
            }
            "CAPTAIN" -> _currentScreen.value = "CAPTAIN_HOME"
            else -> { // CUSTOMER
              _currentScreen.value = "CUSTOMER_HOME"
              observeCustomerCart(user.id)
            }
          }
        } else {
          _currentScreen.value = "LOGIN"
        }
      } else {
        _currentScreen.value = "LOGIN"
      }
    }
  }

  // Session & Navigation State
  private val _currentUser = MutableStateFlow<UserEntity?>(null)
  val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

  private val _currentScreen = MutableStateFlow("SPLASH")
  val currentScreen: StateFlow<String> = _currentScreen.asStateFlow()

  private val _toastMessage = MutableSharedFlow<String>()
  val toastMessage: SharedFlow<String> = _toastMessage.asSharedFlow()

  // Dynamic Flows from Database
  val allProducts: StateFlow<List<ProductEntity>> = repository.getAllProducts()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val publishedProducts: StateFlow<List<ProductEntity>> = combine(
    repository.getPublishedProducts(),
    repository.getAllUsers()
  ) { products, users ->
    val merchantsMap = users.filter { it.role == "MERCHANT" }.associateBy { it.id }
    products.filter { prod ->
      val merchant = merchantsMap[prod.merchantId]
      if (merchant == null) {
        true
      } else {
        merchant.status == "APPROVED" && EgyptTimeHelper.isMerchantOpenNow(
          merchant.workingHoursType,
          merchant.workStartHour,
          merchant.workEndHour
        )
      }
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val categories: StateFlow<List<ProductCategoryEntity>> = repository.getAllCategories()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val albakalOffers: StateFlow<List<ProductEntity>> = publishedProducts
    .map { list ->
      list.filter { it.isAlbakalOffer || it.isOffer || (it.discountPrice != null && it.discountPrice < it.price) }
    }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val sponsoredAds: StateFlow<List<SponsoredAdEntity>> = repository.getActiveAds()
    .map { list ->
      val now = System.currentTimeMillis()
      list.filter { it.expiryDate <= 0L || it.expiryDate > now }
    }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val allAds: StateFlow<List<SponsoredAdEntity>> = repository.getAllAds()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val governoratesWithDistricts: StateFlow<List<GovernorateAreaEntity>> = repository.getAllAreas()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val appSettings: StateFlow<AppSettingsEntity> = repository.getSettings()
    .map { it ?: AppSettingsEntity() }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppSettingsEntity())

  val allUsers: StateFlow<List<UserEntity>> = repository.getAllUsers()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val pendingUsers: StateFlow<List<UserEntity>> = repository.getPendingUsers()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val allOrders: StateFlow<List<OrderEntity>> = repository.getAllOrders()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val allDebtPayments: StateFlow<List<DebtPaymentRequestEntity>> = repository.getAllDebtPayments()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val allComplaints: StateFlow<List<SupportComplaintEntity>> = repository.getAllComplaints()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val passwordResets: StateFlow<List<PasswordResetRequestEntity>> = repository.getAllPasswordResets()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val allAnnouncements: StateFlow<List<AnnouncementEntity>> = repository.getAllAnnouncements()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val latestAnnouncement: StateFlow<AnnouncementEntity?> = repository.getLatestAnnouncement()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  val promoCodes: StateFlow<List<ShippingPromoCodeEntity>> = repository.getAllPromoCodes()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Customer Cart
  private val _cart = MutableStateFlow<List<CartItemEntity>>(emptyList())
  val cart: StateFlow<List<CartItemEntity>> = _cart.asStateFlow()

  // Supervisor Permissions
  private val _supervisorPermissions = MutableStateFlow<SupervisorPermissionEntity?>(null)
  val supervisorPermissions: StateFlow<SupervisorPermissionEntity?> = _supervisorPermissions.asStateFlow()

  // Continuous Sound Alert State for Merchant
  private val _hasActiveAlarm = MutableStateFlow(false)
  val hasActiveAlarm: StateFlow<Boolean> = _hasActiveAlarm.asStateFlow()

  fun navigateTo(screen: String) {
    _currentScreen.value = screen
  }

  fun showMessage(msg: String) {
    viewModelScope.launch {
      _toastMessage.emit(msg)
    }
  }

  // Auth Operations
  fun login(emailOrPhone: String, pass: String, onSuccess: () -> Unit) {
    viewModelScope.launch {
      val res = repository.login(emailOrPhone, pass)
      res.onSuccess { user ->
        _currentUser.value = user
        // Route according to user role
        when (user.role) {
          "ADMIN" -> _currentScreen.value = "ADMIN_DASHBOARD"
          "SUPERVISOR" -> {
            loadSupervisorPermissions(user.id)
            _currentScreen.value = "ADMIN_DASHBOARD"
          }
          "MERCHANT" -> {
            _currentScreen.value = "MERCHANT_HOME"
            startListeningForMerchantOrders(user.id)
          }
          "CAPTAIN" -> _currentScreen.value = "CAPTAIN_HOME"
          else -> { // CUSTOMER
            _currentScreen.value = "CUSTOMER_HOME"
            observeCustomerCart(user.id)
          }
        }
        sessionPrefs.edit().putString("saved_user_id", user.id).apply()
        // Bind notification token strictly to this user ID
        val token = "fcm_token_${user.id}_" + System.currentTimeMillis()
        repository.updateUserFcmToken(user.id, token)

        showMessage("مرحباً بك يا ${user.name}")
        onSuccess()
      }.onFailure { err ->
        showMessage(err.message ?: "فشل تسجيل الدخول")
      }
    }
  }

  fun registerUser(user: UserEntity, onSuccess: () -> Unit) {
    viewModelScope.launch {
      val res = repository.registerUser(user)
      res.onSuccess {
        showMessage("تم إرسال طلب إنشاء الحساب بنجاح! سيتم مراجعته وتفعيله من قبل الإدارة.")
        _currentScreen.value = "LOGIN"
        onSuccess()
      }.onFailure { err ->
        showMessage(err.message ?: "فشل إنشاء الحساب")
      }
    }
  }

  private var merchantReminderJob: Job? = null

  fun logout() {
    val uid = _currentUser.value?.id
    if (uid != null) {
      viewModelScope.launch {
        // Clear FCM token in Firestore and local database immediately
        repository.clearUserFcmToken(uid)
      }
    }
    merchantReminderJob?.cancel()
    merchantReminderJob = null
    sessionPrefs.edit().remove("saved_user_id").apply()
    alarmManager.stopAlert()
    _hasActiveAlarm.value = false
    _currentUser.value = null
    _supervisorPermissions.value = null
    _cart.value = emptyList()
    _currentScreen.value = "LOGIN"
    showMessage("تم تسجيل الخروج بنجاح")
  }

  private fun loadSupervisorPermissions(userId: String) {
    viewModelScope.launch {
      val perms = repository.getSupervisorPermissionsDirect(userId)
      _supervisorPermissions.value = perms ?: SupervisorPermissionEntity(userId = userId)
    }
  }

  private fun observeCustomerCart(customerId: String) {
    viewModelScope.launch {
      repository.getCart(customerId).collect { items ->
        _cart.value = items
      }
    }
  }

  private fun startListeningForMerchantOrders(merchantId: String) {
    // 1. Instant alert when new unacknowledged order arrives
    viewModelScope.launch {
      repository.getNewUnacknowledgedOrders(merchantId).collect { unackOrders ->
        if (unackOrders.isNotEmpty()) {
          _hasActiveAlarm.value = true
          val first = unackOrders.first()
          alarmManager.startAlert(first.orderNumber, first.customerName)
        } else {
          _hasActiveAlarm.value = false
          alarmManager.stopAlert()
        }
      }
    }

    // 2. Schedule recurring 5-minute reminder loop so merchant doesn't forget preparing orders
    merchantReminderJob?.cancel()
    merchantReminderJob = viewModelScope.launch {
      while (isActive) {
        delay(5 * 60 * 1000L) // 5 minutes
        val pendingOrders = allOrders.value.filter {
          it.merchantId == merchantId && (it.orderStatus == "NEW" || it.orderStatus == "PREPARING")
        }
        if (pendingOrders.isNotEmpty()) {
          _hasActiveAlarm.value = true
          val oldest = pendingOrders.minByOrNull { it.createdAt } ?: pendingOrders.first()
          alarmManager.startAlert(oldest.orderNumber, "تذكير كل 5 دقائق: طلب بانتظار التجهيز - ${oldest.customerName}")
        }
      }
    }
  }

  // Cart actions
  fun addToCart(product: ProductEntity, qty: Int = 1) {
    val user = _currentUser.value ?: return
    viewModelScope.launch {
      val price = product.discountPrice ?: product.price
      repository.addToCart(
        customerId = user.id,
        productId = product.id,
        productName = product.name,
        merchantId = product.merchantId,
        merchantName = product.merchantName,
        price = price,
        quantity = qty,
        imageUrl = product.imageUrl,
        unit = product.unit
      )
      showMessage("تمت إضافة ${product.name} إلى السلة")
    }
  }

  fun removeFromCart(cartItemId: String) {
    viewModelScope.launch {
      repository.removeCartItem(cartItemId)
    }
  }

  fun clearCart() {
    val user = _currentUser.value ?: return
    viewModelScope.launch {
      repository.clearCart(user.id)
    }
  }

  // Placing Order
  fun placeOrder(
    paymentMethod: String,
    paymentProofUrl: String?,
    customShippingFee: Double? = null,
    promoCode: String? = null,
    onSuccess: () -> Unit
  ) {
    val user = _currentUser.value ?: return
    val items = _cart.value
    if (items.isEmpty()) {
      showMessage("السلة فارغة")
      return
    }

    viewModelScope.launch {
      val settings = appSettings.value
      val merchantId = items.firstOrNull()?.merchantId
      val merchant = if (merchantId != null) repository.getUserById(merchantId) else null
      val effectiveShippingFee = customShippingFee ?: merchant?.customShippingFee ?: settings.shippingFee
      val res = repository.placeOrder(
        customer = user,
        cartItems = items,
        shippingFee = effectiveShippingFee,
        commissionPercent = settings.commissionRatePercent,
        paymentMethod = paymentMethod,
        paymentProofUrl = paymentProofUrl,
        promoCode = promoCode
      )
      res.onSuccess { orderNums ->
        showMessage("تم تأكيد طلبك بنجاح! رقم الطلب: $orderNums")
        onSuccess()
      }.onFailure { err ->
        showMessage(err.message ?: "فشل إرسال الطلب")
      }
    }
  }

  suspend fun validateShippingPromo(code: String, customerId: String, baseShippingFee: Double): Pair<Boolean, Double> {
    if (code.isBlank()) return Pair(false, 0.0)
    val promo = repository.getPromoCode(code.trim().uppercase()) ?: return Pair(false, 0.0)
    if (!promo.isActive) return Pair(false, 0.0)
    val usedCustomers = promo.usedCustomerIds.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    if (usedCustomers.contains(customerId)) {
      return Pair(false, -1.0) // Flag for: already used by this customer
    }
    val discount = if (promo.isFreeShipping) baseShippingFee else promo.discountAmount.coerceAtMost(baseShippingFee)
    return Pair(true, discount)
  }

  fun createPromoCode(promo: ShippingPromoCodeEntity, onSuccess: () -> Unit = {}) {
    viewModelScope.launch {
      repository.createPromoCode(promo)
      showMessage("تم إنشاء كود خصم الشحن بنجاح")
      onSuccess()
    }
  }

  fun deletePromoCode(code: String, onSuccess: () -> Unit = {}) {
    viewModelScope.launch {
      repository.deletePromoCode(code)
      showMessage("تم حذف كود الخصم")
      onSuccess()
    }
  }

  fun updateMerchantShippingFee(userId: String, fee: Double?, onSuccess: () -> Unit = {}) {
    viewModelScope.launch {
      val res = repository.updateMerchantShippingFee(userId, fee)
      res.onSuccess {
        showMessage("تم تحديث مصاريف الشحن للتاجر بنجاح")
        onSuccess()
      }.onFailure { err ->
        showMessage(err.message ?: "فشل تحديث مصاريف الشحن")
      }
    }
  }

  fun updateMerchantShippingAndCommission(
    userId: String,
    shippingFee: Double?,
    commissionPercent: Double?,
    onSuccess: () -> Unit = {}
  ) {
    viewModelScope.launch {
      val res = repository.updateMerchantShippingAndCommission(userId, shippingFee, commissionPercent)
      res.onSuccess {
        showMessage("تم تحديث مصاريف الشحن ونسبة العمولة للتاجر بنجاح")
        onSuccess()
      }.onFailure { err ->
        showMessage(err.message ?: "فشل التحديث")
      }
    }
  }

  fun acknowledgeOrder(orderId: String) {
    viewModelScope.launch {
      repository.acknowledgeOrder(orderId)
      showMessage("تم قبول الطلب وتحويله إلى: جاري التحضير")
    }
  }

  fun updatePreparedOrder(orderId: String, preparedItemIds: Set<String>, onSuccess: () -> Unit = {}) {
    viewModelScope.launch {
      repository.updatePreparedOrderItems(orderId, preparedItemIds)
      showMessage("تم تحديث الطلب بالمنتجات المحددة وإعادة احتساب الإجمالي والعمولة أوتوماتيكياً")
      onSuccess()
    }
  }

  fun shipOrder(orderId: String, captainName: String, captainPhone: String) {
    viewModelScope.launch {
      repository.shipOrder(orderId, captainName, captainPhone)
      showMessage("تم تحديث حالة الطلب إلى: جاري الإرسال مع الكابتن $captainName")
    }
  }

  fun merchantConfirmDelivered(orderId: String) {
    viewModelScope.launch {
      repository.merchantConfirmDelivered(orderId)
      showMessage("تم تأكيد تسليم الطلب من جانب التاجر")
    }
  }

  fun customerConfirmDelivered(orderId: String) {
    viewModelScope.launch {
      repository.customerConfirmDelivered(orderId)
      showMessage("شكراً لك! تم تأكيد استلام الطلب")
    }
  }

  fun cancelOrderByCustomer(orderId: String, onSuccess: () -> Unit = {}) {
    viewModelScope.launch {
      val order = repository.getOrderById(orderId)
      if (order == null) {
        showMessage("الطلب غير موجود")
        return@launch
      }
      val elapsed = System.currentTimeMillis() - order.createdAt
      if (elapsed > 2 * 60 * 1000L) {
        showMessage("عذراً، انتهت المهلة المسموحة لإلغاء الطلب (دقيقتان من وقت إرساله)")
        return@launch
      }
      if (order.orderStatus == "SHIPPED" || order.orderStatus == "DELIVERED") {
        showMessage("لا يمكن إلغاء الطلب لأنه قيد الشحن أو تم تسليمه")
        return@launch
      }
      repository.cancelOrder(orderId, "تم إلغاء الطلب بواسطة العميل خلال أول دقيقتين", "CUSTOMER")
      showMessage("تم إلغاء طلبك بنجاح")
      onSuccess()
    }
  }

  fun cancelOrderByAdmin(orderId: String, reason: String = "تم الإلغاء بواسطة الإدارة بعد مرور 10 دقائق", onSuccess: () -> Unit = {}) {
    viewModelScope.launch {
      val order = repository.getOrderById(orderId)
      if (order == null) {
        showMessage("الطلب غير موجود")
        return@launch
      }
      val elapsed = System.currentTimeMillis() - order.createdAt
      if (elapsed < 10 * 60 * 1000L) {
        showMessage("لا يمكن إلغاء الطلب من قبل الإدارة إلا بعد مرور 10 دقائق من إنشائه")
        return@launch
      }
      if (order.orderStatus == "DELIVERED") {
        showMessage("لا يمكن إلغاء طلب مكتمل ومسلّم")
        return@launch
      }
      repository.cancelOrder(orderId, reason, "ADMIN")
      showMessage("تم إلغاء الطلب بواسطة الإدارة بنجاح")
      onSuccess()
    }
  }

  // Merchant Debt Repayment
  fun submitDebtPayment(amount: Double, method: String, proofUrl: String?, notes: String?, onComplete: () -> Unit) {
    val merchant = _currentUser.value ?: return
    viewModelScope.launch {
      repository.submitDebtPayment(merchant.id, merchant.name, amount, method, proofUrl, notes)
      showMessage("تم رفع طلب سداد المديونية بنجاح! سيتم مراجعته واعتماده من الإدارة.")
      onComplete()
    }
  }

  fun updateMerchantWorkingHours(
    userId: String,
    hoursType: String,
    startHour: String,
    endHour: String,
    displayLabel: String,
    onSuccess: () -> Unit = {}
  ) {
    viewModelScope.launch {
      val user = repository.getUserById(userId) ?: return@launch
      val updated = user.copy(
        workingHoursType = hoursType,
        workStartHour = startHour,
        workEndHour = endHour,
        workingHoursDisplay = displayLabel
      )
      repository.updateUser(updated)
      if (_currentUser.value?.id == userId) {
        _currentUser.value = updated
      }
      showMessage("تم تحديث مواعيد عمل المتجر بنجاح")
      onSuccess()
    }
  }

  fun updateUserLocation(
    userId: String,
    governorate: String,
    district: String,
    onSuccess: () -> Unit = {}
  ) {
    viewModelScope.launch {
      val user = repository.getUserById(userId) ?: _currentUser.value ?: return@launch
      val updated = user.copy(
        governorate = governorate.trim(),
        district = district.trim()
      )
      repository.updateUser(updated)
      if (_currentUser.value?.id == userId) {
        _currentUser.value = updated
      }
      showMessage("تم تحديث المحافظة والمنطقة بنجاح")
      onSuccess()
    }
  }

  // Admin Actions
  fun approveUser(userId: String) {
    viewModelScope.launch {
      repository.approveUser(userId)
      showMessage("تمت الموافقة على تفعيل الحساب")
    }
  }

  fun rejectUser(userId: String) {
    viewModelScope.launch {
      repository.rejectUser(userId)
      showMessage("تم رفض طلب الحساب")
    }
  }

  fun banMerchant(merchantId: String, reason: String) {
    viewModelScope.launch {
      repository.banMerchant(merchantId, reason)
      showMessage("تم حظر التاجر وإخفاء منتجاته")
    }
  }

  fun unbanMerchant(merchantId: String) {
    viewModelScope.launch {
      repository.unbanMerchant(merchantId)
      showMessage("تم فك الحظر عن التاجر وإعادة منتجاته")
    }
  }

  fun addCaptain(
    name: String,
    phone: String,
    password: String,
    governorate: String,
    district: String,
    onSuccess: (UserEntity) -> Unit = {}
  ) {
    viewModelScope.launch {
      val cleanPhone = com.example.util.PhoneHelper.cleanPhone(phone).ifBlank { phone.trim() }
      val captain = UserEntity(
        id = "cpt_" + java.util.UUID.randomUUID().toString().take(8),
        name = name.trim(),
        email = "$cleanPhone@captain.albakal.com",
        phone = cleanPhone,
        password = password.trim(),
        role = "CAPTAIN",
        status = "APPROVED",
        governorate = governorate.trim(),
        district = district.trim(),
        street = "",
        apartment = "",
        floor = "",
        workingHoursType = "ALL_DAY",
        workingHoursDisplay = "متاح للتوصيل 24 ساعة"
      )
      repository.registerUser(captain)
      showMessage("تم تسجيل الكابتن بنجاح في النظام")
      onSuccess(captain)
    }
  }

  fun deleteCaptain(captainId: String) {
    viewModelScope.launch {
      repository.deleteUser(captainId)
      showMessage("تم حذف الكابتن بنجاح")
    }
  }

  fun getOrderItems(orderId: String): Flow<List<OrderItemEntity>> = repository.getOrderItems(orderId)

  fun approveDebtPayment(payment: DebtPaymentRequestEntity) {
    viewModelScope.launch {
      repository.approveDebtPayment(payment.id, payment)
      showMessage("تم اعتماد سداد المديونية وتحديث رصيد التاجر")
    }
  }

  fun adminSettleMerchantDebt(merchantId: String, amount: Double, notes: String? = null, onComplete: () -> Unit = {}) {
    viewModelScope.launch {
      repository.adminSettleMerchantDebt(merchantId, amount, notes)
      showMessage("تمت تسوية مديونية التاجر بنجاح وتحديث الرصيد فوراً")
      onComplete()
    }
  }

  fun rejectDebtPayment(payment: DebtPaymentRequestEntity) {
    viewModelScope.launch {
      repository.rejectDebtPayment(payment)
      showMessage("تم رفض طلب سداد المديونية")
    }
  }

  fun updateSettings(settings: AppSettingsEntity) {
    viewModelScope.launch {
      repository.updateSettings(settings)
      showMessage("تم حفظ إعدادات التطبيق بنجاح")
    }
  }

  fun addCategory(name: String) {
    viewModelScope.launch {
      repository.addCategory(name)
      showMessage("تمت إضافة القسم")
    }
  }

  fun addArea(gov: String, dist: String) {
    viewModelScope.launch {
      repository.addArea(gov, dist)
      showMessage("تمت إضافة المنطقة")
    }
  }

  fun updateArea(id: String, gov: String, dist: String, onSuccess: () -> Unit = {}) {
    viewModelScope.launch {
      repository.updateArea(id, gov, dist)
      showMessage("تم تعديل المحافظة والمنطقة بنجاح")
      onSuccess()
    }
  }

  fun deleteArea(id: String, onSuccess: () -> Unit = {}) {
    viewModelScope.launch {
      repository.deleteArea(id)
      showMessage("تم حذف المنطقة بنجاح")
      onSuccess()
    }
  }

  fun deleteCustomer(customerId: String, onSuccess: () -> Unit = {}) {
    viewModelScope.launch {
      val res = repository.deleteCustomerPermanently(customerId)
      res.onSuccess {
        showMessage("تم حذف حساب العميل وسجلاته نهائياً")
        onSuccess()
      }.onFailure { err ->
        showMessage(err.message ?: "فشل حذف العميل")
      }
    }
  }

  fun deleteMerchant(merchantId: String, onSuccess: () -> Unit = {}) {
    viewModelScope.launch {
      val res = repository.deleteMerchantPermanently(merchantId)
      res.onSuccess {
        showMessage("تم حذف حساب التاجر وسجلاته ومنتجاته نهائياً")
        onSuccess()
      }.onFailure { err ->
        showMessage(err.message ?: "فشل حذف التاجر")
      }
    }
  }

  fun addProduct(product: ProductEntity) {
    viewModelScope.launch {
      repository.addProduct(product)
      showMessage("تمت إضافة المنتج بنجاح")
    }
  }

  fun updateProduct(product: ProductEntity) {
    viewModelScope.launch {
      repository.updateProduct(product)
      showMessage("تم تحديث المنتج")
    }
  }

  fun deleteProduct(productId: String) {
    viewModelScope.launch {
      repository.deleteProduct(productId)
      showMessage("تم حذف المنتج")
    }
  }

  fun addAd(ad: SponsoredAdEntity) {
    viewModelScope.launch {
      repository.addAd(ad)
      showMessage("تمت إضافة الإعلان بنجاح")
    }
  }

  fun toggleAdActive(ad: SponsoredAdEntity) {
    viewModelScope.launch {
      repository.updateAd(ad.copy(isActive = !ad.isActive))
    }
  }

  fun deleteAd(adId: String) {
    viewModelScope.launch {
      repository.deleteAd(adId)
      showMessage("تم حذف الإعلان")
    }
  }

  fun createSupervisor(name: String, email: String, phone: String, pass: String, perms: SupervisorPermissionEntity, onSuccess: () -> Unit) {
    viewModelScope.launch {
      val res = repository.createSupervisor(name, email, phone, pass, perms)
      res.onSuccess {
        showMessage("تم إنشاء حساب المشرف وتحديد صلاحياته بنجاح")
        onSuccess()
      }.onFailure { err ->
        showMessage(err.message ?: "فشل إنشاء المشرف")
      }
    }
  }

  fun updateSupervisorPermissions(perms: SupervisorPermissionEntity) {
    viewModelScope.launch {
      repository.saveSupervisorPermissions(perms)
      showMessage("تم تحديث صلاحيات المشرف")
    }
  }

  fun getSupervisorPermissions(userId: String): kotlinx.coroutines.flow.Flow<SupervisorPermissionEntity?> {
    return repository.getSupervisorPermissions(userId)
  }

  fun updateSupervisor(
    userId: String,
    name: String,
    phone: String,
    pass: String?,
    perms: SupervisorPermissionEntity,
    onSuccess: () -> Unit
  ) {
    viewModelScope.launch {
      val res = repository.updateSupervisor(userId, name, phone, pass, perms)
      res.onSuccess {
        showMessage("تم حفظ وتحديث بيانات وصلاحيات المشرف بنجاح")
        onSuccess()
      }.onFailure { err ->
        showMessage(err.message ?: "فشل تحديث بيانات المشرف")
      }
    }
  }

  fun deleteSupervisor(userId: String, onSuccess: () -> Unit = {}) {
    viewModelScope.launch {
      val res = repository.deleteSupervisor(userId)
      res.onSuccess {
        showMessage("تم حذف المشرف وسحب كافة صلاحياته بنجاح")
        onSuccess()
      }.onFailure { err ->
        showMessage(err.message ?: "فشل حذف المشرف")
      }
    }
  }

  fun updateAdminCredentials(newEmail: String, newPass: String, onSuccess: () -> Unit) {
    val admin = _currentUser.value ?: return
    viewModelScope.launch {
      val res = repository.updateAdminCredentials(admin.id, newEmail, newPass)
      res.onSuccess {
        _currentUser.value = admin.copy(email = newEmail, password = newPass)
        showMessage("تم تحديث بيانات حساب الإدارة بنجاح")
        onSuccess()
      }.onFailure { err ->
        showMessage(err.message ?: "فشل تحديث البيانات")
      }
    }
  }

  fun submitComplaint(subject: String, details: String, isDamaged: Boolean, merchantId: String?) {
    val user = _currentUser.value ?: return
    viewModelScope.launch {
      val complaint = SupportComplaintEntity(
        id = "comp_" + java.util.UUID.randomUUID().toString().take(8),
        userId = user.id,
        userName = user.name,
        userPhone = user.phone,
        userRole = user.role,
        subject = subject,
        details = details,
        isDamagedGoodsReport = isDamaged,
        merchantIdReported = merchantId
      )
      repository.submitComplaint(complaint)
      showMessage("تم إرسال بلاغك/شكواك بنجاح وسيتواصل معك الدعم الفني")
    }
  }

  fun resolveComplaint(complaint: SupportComplaintEntity, note: String) {
    viewModelScope.launch {
      repository.resolveComplaint(complaint, note)
      showMessage("تم حل الشكوى")
    }
  }

  fun requestPasswordReset(phone: String, onCodeGenerated: (String) -> Unit) {
    viewModelScope.launch {
      val res = repository.requestPasswordReset(phone)
      res.onSuccess { code ->
        showMessage("تم تقديم طلب استعادة الحساب كود التحقق المؤقت: $code")
        onCodeGenerated(code)
      }.onFailure { err ->
        showMessage(err.message ?: "فشل تقديم طلب الاستعادة")
      }
    }
  }

  fun verifyAndResetPassword(phone: String, code: String, newPass: String, onSuccess: () -> Unit) {
    viewModelScope.launch {
      val res = repository.verifyAndResetPassword(phone, code, newPass)
      res.onSuccess {
        showMessage("تم تغيير كلمة المرور بنجاح! يمكنك الآن تسجيل الدخول.")
        onSuccess()
      }.onFailure { err ->
        showMessage(err.message ?: "فشل تعيين كلمة المرور")
      }
    }
  }

  fun acceptHonorCode() {
    val merchant = _currentUser.value ?: return
    viewModelScope.launch {
      repository.acceptHonorCode(merchant.id)
      _currentUser.value = merchant.copy(honorCodeAccepted = true)
      showMessage("تمت الموافقة على ميثاق الشرف للتجار")
    }
  }

  // Categories Management (Admin / Supervisor)
  fun addCategory(name: String, order: Int = 0) {
    viewModelScope.launch {
      if (name.isBlank()) {
        showMessage("يرجى إدخال اسم القسم")
        return@launch
      }
      repository.addCategory(name.trim(), order)
      showMessage("تمت إضافة قسم $name بنجاح")
    }
  }

  fun updateCategory(id: String, name: String, order: Int = 0) {
    viewModelScope.launch {
      if (name.isBlank()) {
        showMessage("يرجى إدخال اسم القسم")
        return@launch
      }
      repository.updateCategory(id, name.trim(), order)
      showMessage("تم تعديل القسم بنجاح")
    }
  }

  fun deleteCategory(id: String) {
    viewModelScope.launch {
      repository.deleteCategory(id)
      showMessage("تم حذف القسم بنجاح")
    }
  }

  // App Logo & Download URL Customization
  fun updateAppLogo(logoUrl: String?) {
    viewModelScope.launch {
      val current = repository.getSettingsDirect()
      val updated = current.copy(appLogoUrl = logoUrl?.trim())
      repository.updateSettings(updated)
      showMessage("تم تحديث وتعميم شعار التطبيق بنجاح")
    }
  }

  fun updateApkDownloadUrl(url: String) {
    viewModelScope.launch {
      val current = repository.getSettingsDirect()
      val updated = current.copy(apkDownloadUrl = url.trim())
      repository.updateSettings(updated)
      showMessage("تم تحديث رابط تحميل الـ APK في التطبيق")
    }
  }

  // Announcements & Updates Management
  fun postAnnouncement(
    title: String,
    message: String,
    apkDownloadUrl: String? = null,
    targetAudience: String = "ALL",
    onSuccess: () -> Unit = {}
  ) {
    viewModelScope.launch {
      if (title.isBlank() || message.isBlank()) {
        showMessage("يرجى إدخال عنوان ونص التنبيه")
        return@launch
      }
      val ann = AnnouncementEntity(
        id = "ann_" + UUID.randomUUID().toString().take(8),
        title = title.trim(),
        message = message.trim(),
        apkDownloadUrl = apkDownloadUrl?.trim()?.ifBlank { null },
        targetAudience = targetAudience,
        createdAt = System.currentTimeMillis()
      )
      repository.postAnnouncement(ann)
      showMessage("تم نشر التنبيه وتوجيه الصفحة تلقائياً لجميع العملاء والتجار")
      onSuccess()
    }
  }

  fun deleteAnnouncement(id: String) {
    viewModelScope.launch {
      repository.deleteAnnouncement(id)
      showMessage("تم حذف التنبيه")
    }
  }

  // Admin Direct User Registration with Pre-Approval
  fun adminRegisterUser(user: UserEntity, onSuccess: (UserEntity) -> Unit) {
    viewModelScope.launch {
      val approvedUser = user.copy(status = "APPROVED")
      val res = repository.registerUser(approvedUser)
      res.onSuccess {
        showMessage("تم تسجيل ${if (user.role == "MERCHANT") "التاجر" else "العميل"} ${user.name} بنجاح!")
        onSuccess(approvedUser)
      }.onFailure { err ->
        showMessage(err.message ?: "فشل تسجيل المستخدم")
      }
    }
  }

  override fun onCleared() {
    super.onCleared()
    alarmManager.stopAlert()
  }
}
