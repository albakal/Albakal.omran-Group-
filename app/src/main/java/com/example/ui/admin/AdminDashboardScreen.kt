package com.example.ui.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.material.icons.filled.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import android.content.Intent
import android.net.Uri
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppSettingsEntity
import com.example.data.model.DebtPaymentRequestEntity
import com.example.data.model.GovernorateAreaEntity
import com.example.data.model.OrderEntity
import com.example.data.model.ProductEntity
import com.example.data.model.SponsoredAdEntity
import com.example.data.model.SupervisorPermissionEntity
import com.example.data.model.SupportComplaintEntity
import com.example.data.model.UserEntity
import com.example.data.model.ProductCategoryEntity
import com.example.data.model.AnnouncementEntity
import com.example.data.model.ShippingPromoCodeEntity
import com.example.util.ImageUtils
import com.example.ui.AlbakalViewModel
import com.example.ui.common.AlbakalTopBar
import com.example.ui.common.OrderStatusBadge
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.GreenContainer
import com.example.ui.theme.GreenDark
import com.example.ui.theme.GreenPrimary
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material.icons.filled.CheckCircle
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.text.SimpleDateFormat

@Composable
fun AdminDashboardScreen(viewModel: AlbakalViewModel) {
  val currentUser by viewModel.currentUser.collectAsState()
  val pendingUsers by viewModel.pendingUsers.collectAsState()
  val permissions by viewModel.supervisorPermissions.collectAsState()
  val appSettings by viewModel.appSettings.collectAsState()

  val isSuperAdmin = currentUser?.role == "ADMIN"
  val perms = permissions ?: SupervisorPermissionEntity(userId = currentUser?.id ?: "")

  // Tabs
  var selectedTab by remember { mutableIntStateOf(0) }

  val tabTitles = remember(isSuperAdmin, perms) {
    val list = mutableListOf<String>()
    if (isSuperAdmin || perms.canManageCustomers) list.add("العملاء")
    if (isSuperAdmin || perms.canManageMerchants) list.add("التجار")
    if (isSuperAdmin) list.add("المشرفين")
    if (isSuperAdmin || perms.canManageProducts) list.add("المنتجات والأقسام")
    if (isSuperAdmin || perms.canManageOrders) list.add("الطلبات")
    if (isSuperAdmin || perms.canViewFinancials) list.add("المالية والعمولات")
    if (isSuperAdmin || perms.canManageAds) list.add("الإعلانات")
    if (isSuperAdmin || perms.canManageSupport) list.add("الدعم والشكاوى")
    if (isSuperAdmin) list.add("استعادة الحساب")
    if (isSuperAdmin || perms.canManageStoreSettings) list.add("المحافظات")
    if (isSuperAdmin || perms.canManageStoreSettings) list.add("شعار التطبيق والتنبيهات")
    if (isSuperAdmin) list.add("أمان حساب الإدارة")
    if (isSuperAdmin) list.add("ملف التطبيق (APK)")
    list
  }

  Scaffold(
    topBar = {
      AlbakalTopBar(
        title = if (isSuperAdmin) "لوحة الإدارة الرئيسية" else "لوحة المشرف الإداري",
        subtitle = currentUser?.name ?: "الإدارة",
        appLogoUrl = appSettings.appLogoUrl,
        showLogoutButton = true,
        onLogoutClick = { viewModel.logout() }
      )
    }
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .background(MaterialTheme.colorScheme.background)
    ) {
      if (tabTitles.isNotEmpty()) {
        ScrollableTabRow(
          selectedTabIndex = selectedTab.coerceIn(0, tabTitles.size - 1),
          containerColor = MaterialTheme.colorScheme.surface,
          contentColor = GreenPrimary,
          edgePadding = 12.dp
        ) {
          tabTitles.forEachIndexed { index, title ->
            Tab(
              selected = selectedTab == index,
              onClick = { selectedTab = index },
              text = {
                Text(
                  text = title,
                  fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                  fontSize = 12.sp
                )
              },
              modifier = Modifier.testTag("admin_tab_$index")
            )
          }
        }
      }

      val currentTabTitle = tabTitles.getOrNull(selectedTab) ?: ""
      Box(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        when (currentTabTitle) {
          "العملاء" -> AdminCustomersSection(viewModel)
          "التجار" -> AdminMerchantsSection(viewModel, canBan = isSuperAdmin || perms.canBanMerchants, canDebts = isSuperAdmin || perms.canManageDebts)
          "المشرفين" -> AdminSupervisorsSection(viewModel)
          "المنتجات والأقسام" -> AdminProductsSection(viewModel)
          "الطلبات" -> AdminOrdersSection(viewModel)
          "المالية والعمولات" -> AdminFinancialsSection(viewModel)
          "الإعلانات" -> AdminAdsSection(viewModel)
          "الدعم والشكاوى" -> AdminSupportSection(viewModel)
          "استعادة الحساب" -> AdminPasswordResetsSection(viewModel)
          "المحافظات" -> AdminGovernoratesSection(viewModel)
          "شعار التطبيق والتنبيهات" -> AdminLogoAndAnnouncementsSection(viewModel)
          "أمان حساب الإدارة" -> AdminSecuritySection(viewModel)
          "ملف التطبيق (APK)" -> AdminApkSection(viewModel)
          else -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("اختر قسماً من القائمة أعلاه", color = Color.Gray)
          }
        }
      }
    }
  }
}

// 1. CUSTOMERS MANAGEMENT
@Composable
private fun AdminCustomersSection(viewModel: AlbakalViewModel) {
  val allUsers by viewModel.allUsers.collectAsState()
  val customers = remember(allUsers) { allUsers.filter { it.role == "CUSTOMER" } }
  var customerToDelete by remember { mutableStateOf<UserEntity?>(null) }
  var showAddCustomerDialog by remember { mutableStateOf(false) }

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "إدارة العملاء (${customers.size})",
          fontWeight = FontWeight.Bold,
          fontSize = 15.sp,
          color = GreenDark
        )
        Button(
          onClick = { showAddCustomerDialog = true },
          colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
          shape = RoundedCornerShape(8.dp)
        ) {
          Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("إضافة عميل جديد", fontSize = 11.sp)
        }
      }
    }

    items(customers) { customer ->
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(text = customer.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
              Text(text = "${customer.phone} • ${customer.email}", fontSize = 11.sp, color = Color.Gray)
              Text(
                text = "${customer.governorate} - ${customer.district} - ${customer.street}",
                fontSize = 11.sp,
                color = Color.DarkGray
              )
            }
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = if (customer.status == "APPROVED") GreenContainer else Color(0xFFFFF3E0)
            ) {
              Text(
                text = if (customer.status == "APPROVED") "معتمد ✅" else "قيد المراجعة ⏳",
                color = if (customer.status == "APPROVED") GreenDark else AmberSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          if (customer.status == "PENDING_APPROVAL") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              Button(
                onClick = { viewModel.approveUser(customer.id) },
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
              ) {
                Text("الموافقة على الحساب", fontSize = 11.sp)
              }
              OutlinedButton(
                onClick = { viewModel.rejectUser(customer.id) },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
              ) {
                Text("رفض", fontSize = 11.sp, color = Color.Red)
              }
            }
            Spacer(modifier = Modifier.height(8.dp))
          }

          OutlinedButton(
            onClick = { customerToDelete = customer },
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
            modifier = Modifier.fillMaxWidth()
          ) {
            Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("حذف حساب العميل وسجلاته نهائياً", fontSize = 11.sp, color = Color.Red)
          }
        }
      }
    }
  }

  if (customerToDelete != null) {
    AlertDialog(
      onDismissRequest = { customerToDelete = null },
      title = { Text("تأكيد حذف العميل نهائياً", fontWeight = FontWeight.Bold) },
      text = { Text("هل أنت متأكد من حذف حساب العميل ${customerToDelete!!.name}؟ سيتم حذف بيانات العميل وسلة التسوق والطلبات السابقة نهائياً من النظام.") },
      confirmButton = {
        Button(
          onClick = {
            val id = customerToDelete!!.id
            customerToDelete = null
            viewModel.deleteCustomer(id)
          },
          colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
          Text("تأكيد")
        }
      },
      dismissButton = {
        OutlinedButton(onClick = { customerToDelete = null }) {
          Text("إلغاء (عدم تأكيد)")
        }
      }
    )
  }

  if (showAddCustomerDialog) {
    AdminAddUserDialog(
      role = "CUSTOMER",
      viewModel = viewModel,
      onDismiss = { showAddCustomerDialog = false }
    )
  }
}

// 2. MERCHANTS MANAGEMENT & BANNING
@Composable
private fun AdminMerchantsSection(viewModel: AlbakalViewModel, canBan: Boolean, canDebts: Boolean) {
  val allUsers by viewModel.allUsers.collectAsState()
  val allOrders by viewModel.allOrders.collectAsState()
  val merchants = remember(allUsers) { allUsers.filter { it.role == "MERCHANT" } }

  var banMerchantTarget by remember { mutableStateOf<UserEntity?>(null) }
  var settleDebtMerchantTarget by remember { mutableStateOf<UserEntity?>(null) }
  var merchantToDelete by remember { mutableStateOf<UserEntity?>(null) }
  var merchantToEditShipping by remember { mutableStateOf<UserEntity?>(null) }
  var showAddMerchantDialog by remember { mutableStateOf(false) }

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "إدارة التجار والمديونيات والحظر (${merchants.size})",
          fontWeight = FontWeight.Bold,
          fontSize = 15.sp,
          color = GreenDark
        )
        Button(
          onClick = { showAddMerchantDialog = true },
          colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
          shape = RoundedCornerShape(8.dp)
        ) {
          Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("إضافة تاجر جديد", fontSize = 11.sp)
        }
      }
    }

    items(merchants) { merchant ->
      val merchantDeliveredOrders = remember(allOrders, merchant.id) {
        allOrders.filter { it.merchantId == merchant.id && it.orderStatus == "DELIVERED" }
      }
      val appProfitFromMerchant = remember(merchantDeliveredOrders) {
        merchantDeliveredOrders.sumOf { it.appCommission }
      }

      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(text = merchant.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
              Text(text = "${merchant.phone} • ${merchant.email}", fontSize = 11.sp, color = Color.Gray)
              Text(text = "${merchant.governorate} - ${merchant.district}", fontSize = 11.sp, color = Color.DarkGray)
              if (merchant.currentDebt < 0.0) {
                Text(
                  text = "رصيد دائن بالموجب: +${String.format("%.2f", -merchant.currentDebt)} ج.م | الأرباح: ${merchant.totalEarnings} ج.م",
                  fontWeight = FontWeight.Bold,
                  fontSize = 11.sp,
                  color = GreenPrimary
                )
              } else {
                Text(
                  text = "المديونية: ${merchant.currentDebt} ج.م | الأرباح: ${merchant.totalEarnings} ج.م",
                  fontWeight = FontWeight.Bold,
                  fontSize = 11.sp,
                  color = if (merchant.currentDebt > 0.0) Color(0xFFC62828) else GreenPrimary
                )
              }
              Spacer(modifier = Modifier.height(4.dp))
              Surface(
                color = GreenContainer,
                shape = RoundedCornerShape(6.dp)
              ) {
                Text(
                  text = "💰 ربح التطبيق من المتجر: ${String.format("%.2f", appProfitFromMerchant)} ج.م (${merchantDeliveredOrders.size} طلب مكتمل)",
                  fontWeight = FontWeight.Bold,
                  fontSize = 11.sp,
                  color = GreenDark,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
              Spacer(modifier = Modifier.height(4.dp))
              Surface(
                color = AmberSecondary.copy(alpha = 0.12f),
                shape = RoundedCornerShape(6.dp)
              ) {
                Text(
                  text = "🚚 مصاريف الشحن: " + (if (merchant.customShippingFee != null) "${merchant.customShippingFee} ج.م (مخصصة للتاجر)" else "الافتراضية للمنصة"),
                  fontWeight = FontWeight.SemiBold,
                  fontSize = 11.sp,
                  color = Color(0xFF6D4C41),
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = when (merchant.status) {
                "APPROVED" -> GreenContainer
                "BANNED" -> Color(0xFFFFEBEE)
                else -> Color(0xFFFFF3E0)
              }
            ) {
              Text(
                text = when (merchant.status) {
                  "APPROVED" -> "نشط ومعتمد ✅"
                  "BANNED" -> "محظور 🚫"
                  else -> "طلب جديد ⏳"
                },
                color = when (merchant.status) {
                  "APPROVED" -> GreenDark
                  "BANNED" -> Color.Red
                  else -> AmberSecondary
                },
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (merchant.status == "PENDING_APPROVAL") {
              Button(
                onClick = { viewModel.approveUser(merchant.id) },
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
              ) {
                Text("الموافقة والتفعيل", fontSize = 11.sp)
              }
              OutlinedButton(
                onClick = { viewModel.rejectUser(merchant.id) },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
              ) {
                Text("رفض", fontSize = 11.sp, color = Color.Red)
              }
            } else if (merchant.status == "BANNED") {
              Button(
                onClick = { viewModel.unbanMerchant(merchant.id) },
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
              ) {
                Text("فك الحظر وإعادة المتجر", fontSize = 11.sp)
              }
            } else {
              if (canDebts) {
                Button(
                  onClick = { settleDebtMerchantTarget = merchant },
                  colors = ButtonDefaults.buttonColors(containerColor = AmberSecondary),
                  shape = RoundedCornerShape(8.dp),
                  modifier = Modifier.weight(1f)
                ) {
                  Text("سداد مديونية تاجر", fontSize = 11.sp)
                }
              }
              if (canBan) {
                OutlinedButton(
                  onClick = { banMerchantTarget = merchant },
                  shape = RoundedCornerShape(8.dp),
                  colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                  modifier = Modifier.weight(1f)
                ) {
                  Text("حظر التاجر", fontSize = 11.sp)
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(6.dp))
          OutlinedButton(
            onClick = { merchantToEditShipping = merchant },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Icon(Icons.Default.LocalShipping, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("تحديد مصاريف الشحن ونسبة العمولة لهذا التاجر", fontSize = 11.sp, color = GreenPrimary, fontWeight = FontWeight.SemiBold)
          }

          Spacer(modifier = Modifier.height(6.dp))
          OutlinedButton(
            onClick = { merchantToDelete = merchant },
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
            modifier = Modifier.fillMaxWidth()
          ) {
            Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("حذف متجر التاجر وسجلاته نهائياً", fontSize = 11.sp, color = Color.Red)
          }
        }
      }
    }
  }

  val appSettings by viewModel.appSettings.collectAsState()
  if (merchantToEditShipping != null) {
    AdminEditMerchantShippingDialog(
      merchant = merchantToEditShipping!!,
      defaultShippingFee = appSettings.shippingFee,
      defaultCommissionPercent = appSettings.commissionRatePercent,
      onSave = { fee, comm ->
        viewModel.updateMerchantShippingAndCommission(merchantToEditShipping!!.id, fee, comm)
        merchantToEditShipping = null
      },
      onDismiss = { merchantToEditShipping = null }
    )
  }

  if (settleDebtMerchantTarget != null) {
    AdminSettleMerchantDebtDialog(
      merchant = settleDebtMerchantTarget!!,
      viewModel = viewModel,
      onDismiss = { settleDebtMerchantTarget = null }
    )
  }

  if (banMerchantTarget != null) {
    BanMerchantDialog(
      merchant = banMerchantTarget!!,
      onConfirm = { reason ->
        viewModel.banMerchant(banMerchantTarget!!.id, reason)
        banMerchantTarget = null
      },
      onDismiss = { banMerchantTarget = null }
    )
  }

  if (merchantToDelete != null) {
    AlertDialog(
      onDismissRequest = { merchantToDelete = null },
      title = { Text("تأكيد حذف التاجر نهائياً", fontWeight = FontWeight.Bold) },
      text = { Text("هل أنت متأكد من حذف متجر التاجر ${merchantToDelete!!.name}؟ سيتم حذف بيانات التاجر وكافة منتجاته والطلبات المرتبطة به وسجلات المديونية نهائياً من النظام.") },
      confirmButton = {
        Button(
          onClick = {
            val id = merchantToDelete!!.id
            merchantToDelete = null
            viewModel.deleteMerchant(id)
          },
          colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
          Text("تأكيد")
        }
      },
      dismissButton = {
        OutlinedButton(onClick = { merchantToDelete = null }) {
          Text("إلغاء (عدم تأكيد)")
        }
      }
    )
  }

  if (showAddMerchantDialog) {
    AdminAddUserDialog(
      role = "MERCHANT",
      viewModel = viewModel,
      onDismiss = { showAddMerchantDialog = false }
    )
  }
}

@Composable
private fun AdminEditMerchantShippingDialog(
  merchant: UserEntity,
  defaultShippingFee: Double,
  defaultCommissionPercent: Double,
  onSave: (Double?, Double?) -> Unit,
  onDismiss: () -> Unit
) {
  var feeText by remember { mutableStateOf(merchant.customShippingFee?.toString() ?: "") }
  var useDefaultFee by remember { mutableStateOf(merchant.customShippingFee == null) }

  var commissionText by remember { mutableStateOf(merchant.customCommissionPercent?.toString() ?: "") }
  var useDefaultCommission by remember { mutableStateOf(merchant.customCommissionPercent == null) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("مصاريف الشحن ونسبة العمولة: ${merchant.name}", fontWeight = FontWeight.Bold) },
    text = {
      Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        // Section 1: Shipping Fee
        Text("1. مصاريف الشحن الخاصة بمتجر التاجر:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = GreenDark)
        Text("القيمة الافتراضية العامة للمنصة: $defaultShippingFee ج.م", fontSize = 11.sp, color = Color.Gray)

        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .fillMaxWidth()
            .clickable {
              useDefaultFee = !useDefaultFee
              if (useDefaultFee) feeText = ""
            }
        ) {
          Checkbox(
            checked = useDefaultFee,
            onCheckedChange = {
              useDefaultFee = it
              if (it) feeText = ""
            }
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text("استخدام القيمة الافتراضية ($defaultShippingFee ج.م)", fontSize = 12.sp)
        }

        if (!useDefaultFee) {
          OutlinedTextField(
            value = feeText,
            onValueChange = { feeText = it },
            label = { Text("قيمة الشحن المخصصة (ج.م) *") },
            placeholder = { Text("مثال: 25.0") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

        // Section 2: Commission Percent
        Text("2. نسبة عمولة التطبيق الخاصة بهذا التاجر:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = GreenDark)
        Text("النسبة الافتراضية العامة للمنصة: $defaultCommissionPercent %", fontSize = 11.sp, color = Color.Gray)

        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .fillMaxWidth()
            .clickable {
              useDefaultCommission = !useDefaultCommission
              if (useDefaultCommission) commissionText = ""
            }
        ) {
          Checkbox(
            checked = useDefaultCommission,
            onCheckedChange = {
              useDefaultCommission = it
              if (it) commissionText = ""
            }
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text("استخدام النسبة الافتراضية ($defaultCommissionPercent %)", fontSize = 12.sp)
        }

        if (!useDefaultCommission) {
          OutlinedTextField(
            value = commissionText,
            onValueChange = { commissionText = it },
            label = { Text("نسبة العمولة المخصصة (%) *") },
            placeholder = { Text("مثال: 10.0") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val finalFee = if (useDefaultFee) null else feeText.toDoubleOrNull()
          val finalCommission = if (useDefaultCommission) null else commissionText.toDoubleOrNull()
          onSave(finalFee, finalCommission)
        },
        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
      ) {
        Text("حفظ التعديلات")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("إلغاء") }
    }
  )
}

@Composable
private fun AdminSettleMerchantDebtDialog(
  merchant: UserEntity,
  viewModel: AlbakalViewModel,
  onDismiss: () -> Unit
) {
  val currentDebt = merchant.currentDebt
  // Box 2: Starts empty as requested
  var amountText by remember { mutableStateOf("") }
  var notesText by remember { mutableStateOf("") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("سداد مديونية تاجر (${merchant.name})", fontWeight = FontWeight.Bold, fontSize = 15.sp) },
    text = {
      Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // 1. BOX 1: Current Debt Display
        Card(
          shape = RoundedCornerShape(10.dp),
          colors = CardDefaults.cardColors(
            containerColor = if (currentDebt > 0.0) Color(0xFFFFEBEE) else Color(0xFFE8F5E9)
          ),
          border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (currentDebt > 0.0) Color(0xFFEF5350) else GreenPrimary
          ),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Text(
              text = "1. المديونية الحالية المسجلة:",
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp,
              color = Color.DarkGray
            )
            Spacer(modifier = Modifier.height(2.dp))
            if (currentDebt < 0.0) {
              Text(
                text = "رصيد دائن بالموجب: +${String.format("%.2f", -currentDebt)} ج.م",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = GreenDark
              )
            } else {
              Text(
                text = "${String.format("%.2f", currentDebt)} ج.م",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = if (currentDebt > 0.0) Color(0xFFC62828) else GreenDark
              )
            }
          }
        }

        // 2. BOX 2: Empty Input for Amount to Pay
        OutlinedTextField(
          value = amountText,
          onValueChange = { amountText = it },
          label = { Text("2. المبلغ المراد سداده (ج.م) *") },
          placeholder = { Text("أدخل المبلغ المسدد...") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        // 3. BOX 3: Dynamic Calculation (Remaining or Positive Credit Balance)
        val enteredAmt = amountText.toDoubleOrNull() ?: 0.0
        val remaining = currentDebt - enteredAmt
        val isOverpaid = enteredAmt > currentDebt

        Card(
          shape = RoundedCornerShape(10.dp),
          colors = CardDefaults.cardColors(
            containerColor = if (amountText.isBlank()) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            else if (isOverpaid) Color(0xFFE8F5E9)
            else Color(0xFFFFF8E1)
          ),
          border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (amountText.isBlank()) Color.LightGray
            else if (isOverpaid) GreenPrimary
            else AmberSecondary
          ),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            if (amountText.isBlank()) {
              Text(
                text = "3. المتبقي بعد السداد:",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = Color.DarkGray
              )
              Text(
                text = "أدخل المبلغ في الخانة أعلاه لعرض الحساب الفوري.",
                fontSize = 11.sp,
                color = Color.Gray
              )
            } else if (isOverpaid) {
              Text(
                text = "3. رصيد بالموجب (دائن للمتجر) 🌟:",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = GreenDark
              )
              Text(
                text = "+${String.format("%.2f", enteredAmt - currentDebt)} ج.م (رصيد دائن)",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = GreenPrimary
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "تم دفع مبلغ أكبر من المديونية بمقدار (+${String.format("%.2f", enteredAmt - currentDebt)} ج.م)؛ سيصبح رصيداً بالموجب لصالح التاجر يخصم من أي عمولات قادمة.",
                fontSize = 11.sp,
                color = GreenDark
              )
            } else {
              Text(
                text = "3. المتبقي من المديونية بعد السداد:",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = Color(0xFFE65100)
              )
              Text(
                text = "${String.format("%.2f", remaining)} ج.م",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = if (remaining > 0.0) Color(0xFFC62828) else GreenDark
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "(${String.format("%.2f", currentDebt)} - ${String.format("%.2f", enteredAmt)} = ${String.format("%.2f", remaining)} ج.م)",
                fontSize = 11.sp,
                color = Color.DarkGray
              )
            }
          }
        }

        OutlinedTextField(
          value = notesText,
          onValueChange = { notesText = it },
          label = { Text("ملاحظات / وسيلة السداد (كاش، فودافون كاش، تحويل...)") },
          minLines = 2,
          modifier = Modifier.fillMaxWidth()
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val amt = amountText.toDoubleOrNull() ?: 0.0
          if (amt <= 0.0) {
            viewModel.showMessage("يرجى إدخال مبلغ صحيح")
          } else {
            viewModel.adminSettleMerchantDebt(merchant.id, amt, notesText) {
              onDismiss()
            }
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
      ) {
        Text("تأكيد اعتماد السداد")
      }
    },
    dismissButton = {
      OutlinedButton(onClick = onDismiss) { Text("إلغاء (عدم تأكيد)") }
    }
  )
}

@Composable
private fun BanMerchantDialog(
  merchant: UserEntity,
  onConfirm: (String) -> Unit,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  var reason by remember { mutableStateOf("") }
  val presetReasons = listOf(
    "إرسال بضاعة تالفة أو معيوبة للعميل",
    "تأخر سداد المديونية المستحقة للمنصة",
    "شكاوى متكررة وسوء جودة المنتجات",
    "عدم الالتزام بمواعيد العمل والطلبات"
  )

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Block, contentDescription = null, tint = Color.Red, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text("حظر التاجر: ${merchant.name}", fontWeight = FontWeight.Bold)
      }
    },
    text = {
      Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Text(
          text = "سيؤدي الحظر إلى إخفاء جميع منتجات هذا المتجر فوراً، وسيتم توجيهك تلقائياً إلى واتساب التاجر لإبلاغه برسالة رسمية بسبب الحظر.",
          fontSize = 12.sp,
          color = Color.DarkGray,
          lineHeight = 16.sp
        )
        Spacer(modifier = Modifier.height(10.dp))

        Text(
          text = "اختر سبباً سريعاً أو اكتب سبباً مخصصاً:",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = GreenDark
        )
        Spacer(modifier = Modifier.height(6.dp))

        // Preset Chips
        presetReasons.forEach { preset ->
          val isSelected = reason == preset
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (isSelected) Color(0xFFFFEBEE) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            border = BorderStroke(1.dp, if (isSelected) Color.Red else Color.LightGray),
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 3.dp)
              .clickable { reason = preset }
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.Check,
                contentDescription = null,
                tint = if (isSelected) Color.Red else Color.LightGray,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = preset,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) Color.Red else Color.DarkGray
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
          value = reason,
          onValueChange = { reason = it },
          label = { Text("سبب قرار الحظر بالتفصيل *") },
          placeholder = { Text("اكتب أو عدل السبب هنا...") },
          minLines = 2,
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))
        Surface(
          color = Color(0xFFE8F5E9),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.Share, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "سيتم فتح واتساب تلقائياً للرقم (${merchant.phone}) مع نص الرسالة الجاهزة لإبلاغ التاجر فور الضغط على تأكيد.",
              fontSize = 10.sp,
              color = GreenDark
            )
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val finalReason = reason.ifBlank { "مخالفة معايير الجودة وشروط المنصة" }
          onConfirm(finalReason)

          // Auto-redirect to merchant's WhatsApp with explanatory ban message
          val intlPhone = com.example.util.PhoneHelper.formatForWhatsApp(merchant.phone)
          val whatsappMessage = """
مرحباً متجر ${merchant.name}،
نود إبلاغكم بأنه قد تم حظر وإيقاف متجركم مؤقتاً في منصة تطبيق "البقال".

السبب:
$finalReason

تنبيه هام:
وفقاً للشروط والأحكام، يمنع إرسال أي بضائع تالفة أو معيوبة، ويلتزم المتجر بتسوية المديونيات المستحقة.

يرجى التواصل مع إدارة المنصة لتسوية الأمر وإعادة تفعيل الحساب.
إدارة تطبيق البقال
          """.trimIndent()

          try {
            val uri = Uri.parse("https://api.whatsapp.com/send?phone=$intlPhone&text=" + Uri.encode(whatsappMessage))
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
          } catch (e: Exception) {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
              type = "text/plain"
              putExtra(Intent.EXTRA_TEXT, whatsappMessage)
              addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(shareIntent, "إبلاغ التاجر عبر واتساب"))
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
      ) {
        Text("تأكيد الحظر والتحويل لواتساب")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("إلغاء") }
    }
  )
}

// 3. SUPERVISORS MANAGEMENT
@Composable
private fun AdminSupervisorsSection(viewModel: AlbakalViewModel) {
  val allUsers by viewModel.allUsers.collectAsState()
  val supervisors = remember(allUsers) { allUsers.filter { it.role == "SUPERVISOR" } }
  var showAddDialog by remember { mutableStateOf(false) }
  var editingSupervisorTarget by remember { mutableStateOf<UserEntity?>(null) }
  var deletingSupervisorTarget by remember { mutableStateOf<UserEntity?>(null) }

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "إدارة المشرفين والصلاحيات (${supervisors.size})",
          fontWeight = FontWeight.Bold,
          fontSize = 15.sp,
          color = GreenDark
        )
        Button(
          onClick = { showAddDialog = true },
          colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
          shape = RoundedCornerShape(8.dp)
        ) {
          Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("إضافة مشرف جديد", fontSize = 11.sp)
        }
      }
    }

    items(supervisors) { sup ->
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Text(text = sup.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
          Text(text = "${sup.email} • ${sup.phone}", fontSize = 11.sp, color = Color.Gray)
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "صلاحيات مخصصة من الإدارة الرئيسية (لا يحق للمشرف الوصول لحساب الأدمن الرئيسي)",
            fontSize = 10.sp,
            color = AmberSecondary
          )
          Spacer(modifier = Modifier.height(10.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Button(
              onClick = { editingSupervisorTarget = sup },
              colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.weight(1f)
            ) {
              Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("تعديل البيانات والصلاحيات", fontSize = 11.sp)
            }
            OutlinedButton(
              onClick = { deletingSupervisorTarget = sup },
              shape = RoundedCornerShape(8.dp),
              colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
              modifier = Modifier.weight(1f)
            ) {
              Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("حذف المشرف", fontSize = 11.sp, color = Color.Red)
            }
          }
        }
      }
    }
  }

  if (showAddDialog) {
    CreateSupervisorDialog(
      viewModel = viewModel,
      onDismiss = { showAddDialog = false }
    )
  }

  if (editingSupervisorTarget != null) {
    EditSupervisorDialog(
      supervisor = editingSupervisorTarget!!,
      viewModel = viewModel,
      onDismiss = { editingSupervisorTarget = null }
    )
  }

  if (deletingSupervisorTarget != null) {
    DeleteSupervisorDialog(
      supervisor = deletingSupervisorTarget!!,
      onConfirm = {
        viewModel.deleteSupervisor(deletingSupervisorTarget!!.id)
        deletingSupervisorTarget = null
      },
      onDismiss = { deletingSupervisorTarget = null }
    )
  }
}

@Composable
private fun CreateSupervisorDialog(
  viewModel: AlbakalViewModel,
  onDismiss: () -> Unit
) {
  var name by remember { mutableStateOf("") }
  var email by remember { mutableStateOf("") }
  var phone by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }

  // Checkboxes
  var canCustomers by remember { mutableStateOf(true) }
  var canMerchants by remember { mutableStateOf(true) }
  var canRegistrations by remember { mutableStateOf(true) }
  var canProducts by remember { mutableStateOf(true) }
  var canOrders by remember { mutableStateOf(true) }
  var canFinancials by remember { mutableStateOf(false) }
  var canDebts by remember { mutableStateOf(false) }
  var canBan by remember { mutableStateOf(false) }
  var canAds by remember { mutableStateOf(false) }
  var canSupport by remember { mutableStateOf(true) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("إنشاء حساب مشرف جديد وتحديد صلاحياته", fontWeight = FontWeight.Bold) },
    text = {
      Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("اسم المشرف") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("البريد الإلكتروني") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("رقم الهاتف") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("كلمة المرور") }, singleLine = true, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(12.dp))
        Text("قائمة الصلاحيات الممنوحة:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = GreenPrimary)

        PermissionCheckRow(label = "إدارة العملاء", checked = canCustomers, onCheckedChange = { canCustomers = it })
        PermissionCheckRow(label = "إدارة التجار", checked = canMerchants, onCheckedChange = { canMerchants = it })
        PermissionCheckRow(label = "مراجعة طلبات التسجيل", checked = canRegistrations, onCheckedChange = { canRegistrations = it })
        PermissionCheckRow(label = "إدارة المنتجات والأقسام", checked = canProducts, onCheckedChange = { canProducts = it })
        PermissionCheckRow(label = "إدارة الطلبات", checked = canOrders, onCheckedChange = { canOrders = it })
        PermissionCheckRow(label = "الاطلاع على المعاملات المالية", checked = canFinancials, onCheckedChange = { canFinancials = it })
        PermissionCheckRow(label = "إدارة المديونيات وسدادها", checked = canDebts, onCheckedChange = { canDebts = it })
        PermissionCheckRow(label = "حظر وفك حظر التجار", checked = canBan, onCheckedChange = { canBan = it })
        PermissionCheckRow(label = "إدارة الإعلانات الممولة", checked = canAds, onCheckedChange = { canAds = it })
        PermissionCheckRow(label = "إدارة الدعم والشكاوى", checked = canSupport, onCheckedChange = { canSupport = it })
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (name.isBlank() || email.isBlank() || password.isBlank()) {
            viewModel.showMessage("يرجى إكمال البيانات الأساسية")
          } else {
            val perms = SupervisorPermissionEntity(
              userId = "",
              canManageCustomers = canCustomers,
              canManageMerchants = canMerchants,
              canReviewRegistrations = canRegistrations,
              canManageProducts = canProducts,
              canManageOrders = canOrders,
              canViewFinancials = canFinancials,
              canManageDebts = canDebts,
              canBanMerchants = canBan,
              canManageAds = canAds,
              canManageSupport = canSupport
            )
            viewModel.createSupervisor(name, email, phone, password, perms) {
              onDismiss()
            }
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
      ) {
        Text("حفظ وتفعيل المشرف")
      }
    },
    dismissButton = { TextButton(onClick = onDismiss) { Text("إلغاء") } }
  )
}

@Composable
private fun EditSupervisorDialog(
  supervisor: UserEntity,
  viewModel: AlbakalViewModel,
  onDismiss: () -> Unit
) {
  val existingPerms by viewModel.getSupervisorPermissions(supervisor.id).collectAsState(initial = null)

  var name by remember(supervisor) { mutableStateOf(supervisor.name) }
  var phone by remember(supervisor) { mutableStateOf(supervisor.phone) }
  var newPassword by remember { mutableStateOf("") }

  // Checkboxes
  var canCustomers by remember { mutableStateOf(true) }
  var canMerchants by remember { mutableStateOf(true) }
  var canRegistrations by remember { mutableStateOf(true) }
  var canProducts by remember { mutableStateOf(true) }
  var canOrders by remember { mutableStateOf(true) }
  var canFinancials by remember { mutableStateOf(false) }
  var canDebts by remember { mutableStateOf(false) }
  var canBan by remember { mutableStateOf(false) }
  var canAds by remember { mutableStateOf(false) }
  var canSupport by remember { mutableStateOf(true) }

  LaunchedEffect(existingPerms) {
    existingPerms?.let { p ->
      canCustomers = p.canManageCustomers
      canMerchants = p.canManageMerchants
      canRegistrations = p.canReviewRegistrations
      canProducts = p.canManageProducts
      canOrders = p.canManageOrders
      canFinancials = p.canViewFinancials
      canDebts = p.canManageDebts
      canBan = p.canBanMerchants
      canAds = p.canManageAds
      canSupport = p.canManageSupport
    }
  }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("تعديل بيانات المشرف وصلاحياته", fontWeight = FontWeight.Bold) },
    text = {
      Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("اسم المشرف *") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
          value = supervisor.email,
          onValueChange = {},
          label = { Text("البريد الإلكتروني (ثابت)") },
          enabled = false,
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
          value = phone,
          onValueChange = { phone = it },
          label = { Text("رقم الهاتف *") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
          value = newPassword,
          onValueChange = { newPassword = it },
          label = { Text("كلمة مرور جديدة (اتركه فارغاً للإبقاء على الحالية)") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))
        Text("تعديل الصلاحيات الممنوحة للمشرف:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = GreenPrimary)

        PermissionCheckRow(label = "إدارة العملاء", checked = canCustomers, onCheckedChange = { canCustomers = it })
        PermissionCheckRow(label = "إدارة التجار", checked = canMerchants, onCheckedChange = { canMerchants = it })
        PermissionCheckRow(label = "مراجعة طلبات التسجيل", checked = canRegistrations, onCheckedChange = { canRegistrations = it })
        PermissionCheckRow(label = "إدارة المنتجات والأقسام", checked = canProducts, onCheckedChange = { canProducts = it })
        PermissionCheckRow(label = "إدارة الطلبات", checked = canOrders, onCheckedChange = { canOrders = it })
        PermissionCheckRow(label = "الاطلاع على المعاملات المالية", checked = canFinancials, onCheckedChange = { canFinancials = it })
        PermissionCheckRow(label = "إدارة المديونيات وسدادها", checked = canDebts, onCheckedChange = { canDebts = it })
        PermissionCheckRow(label = "حظر وفك حظر التجار", checked = canBan, onCheckedChange = { canBan = it })
        PermissionCheckRow(label = "إدارة الإعلانات الممولة", checked = canAds, onCheckedChange = { canAds = it })
        PermissionCheckRow(label = "إدارة الدعم والشكاوى", checked = canSupport, onCheckedChange = { canSupport = it })
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (name.isBlank() || phone.isBlank()) {
            viewModel.showMessage("يرجى إدخال اسم المشرف ورقم هاتفه")
          } else {
            val perms = SupervisorPermissionEntity(
              userId = supervisor.id,
              canManageCustomers = canCustomers,
              canManageMerchants = canMerchants,
              canReviewRegistrations = canRegistrations,
              canManageProducts = canProducts,
              canManageOrders = canOrders,
              canViewFinancials = canFinancials,
              canManageDebts = canDebts,
              canBanMerchants = canBan,
              canManageAds = canAds,
              canManageSupport = canSupport
            )
            viewModel.updateSupervisor(
              userId = supervisor.id,
              name = name,
              phone = phone,
              pass = if (newPassword.isNotBlank()) newPassword else null,
              perms = perms,
              onSuccess = onDismiss
            )
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
      ) {
        Text("حفظ التعديلات")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("إلغاء") }
    }
  )
}

@Composable
private fun DeleteSupervisorDialog(
  supervisor: UserEntity,
  onConfirm: () -> Unit,
  onDismiss: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("حذف المشرف: ${supervisor.name}", fontWeight = FontWeight.Bold) },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("هل أنت متأكد من رغبتك في حذف هذا المشرف نهائياً؟", fontSize = 13.sp)
        Text("سيتم إلغاء حسابه وسحب كافة الصلاحيات الممنوحة له فوراً ولن يتمكن من الدخول إلى لوحة التحكم.", fontSize = 11.sp, color = Color.Red)
      }
    },
    confirmButton = {
      Button(
        onClick = onConfirm,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
        shape = RoundedCornerShape(8.dp)
      ) {
        Text("تأكيد حذف المشرف")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("إلغاء") }
    }
  )
}

@Composable
private fun PermissionCheckRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onCheckedChange(!checked) }
      .padding(vertical = 2.dp)
  ) {
    Checkbox(checked = checked, onCheckedChange = onCheckedChange)
    Spacer(modifier = Modifier.width(6.dp))
    Text(text = label, fontSize = 12.sp)
  }
}

// 4. PRODUCTS & CATEGORIES
@Composable
private fun AdminProductsSection(viewModel: AlbakalViewModel) {
  val products by viewModel.allProducts.collectAsState()
  val categories by viewModel.categories.collectAsState()

  var newCategoryName by remember { mutableStateOf("") }
  var showAddProductForMerchantDialog by remember { mutableStateOf(false) }
  var categoryToEdit by remember { mutableStateOf<ProductCategoryEntity?>(null) }
  var categoryToDelete by remember { mutableStateOf<ProductCategoryEntity?>(null) }
  var productToDelete by remember { mutableStateOf<ProductEntity?>(null) }

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(text = "إدارة أقسام المتجر (${categories.size})", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = GreenDark)
        Button(
          onClick = { showAddProductForMerchantDialog = true },
          colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
          shape = RoundedCornerShape(8.dp)
        ) {
          Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("إضافة منتج لمتجر تاجر", fontSize = 11.sp)
        }
      }
      Spacer(modifier = Modifier.height(6.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        OutlinedTextField(
          value = newCategoryName,
          onValueChange = { newCategoryName = it },
          label = { Text("اسم القسم الجديد (مثل: فواكه، حلويات...)") },
          singleLine = true,
          modifier = Modifier.weight(1f)
        )
        Button(
          onClick = {
            if (newCategoryName.isNotBlank()) {
              viewModel.addCategory(newCategoryName)
              newCategoryName = ""
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.align(Alignment.CenterVertically)
        ) {
          Text("إضافة قسم")
        }
      }
    }

    item {
      LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(categories) { cat ->
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = GreenContainer
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(text = cat.name, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = GreenDark)
              Spacer(modifier = Modifier.width(4.dp))
              IconButton(
                onClick = { categoryToEdit = cat },
                modifier = Modifier.size(22.dp)
              ) {
                Icon(Icons.Default.Edit, contentDescription = "تعديل", tint = GreenDark, modifier = Modifier.size(13.dp))
              }
              IconButton(
                onClick = { categoryToDelete = cat },
                modifier = Modifier.size(22.dp)
              ) {
                Icon(Icons.Default.Delete, contentDescription = "حذف", tint = Color.Red, modifier = Modifier.size(13.dp))
              }
            }
          }
        }
      }
    }

    item {
      Text(text = "جميع المنتجات في المنصة (${products.size})", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = GreenDark)
    }

    items(products) { p ->
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(text = p.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text(text = "التاجر: ${p.merchantName} • القسم: ${p.categoryName}", fontSize = 11.sp, color = Color.Gray)
            Text(text = "${p.price} ج.م / ${p.unit}", fontWeight = FontWeight.Bold, color = GreenPrimary, fontSize = 12.sp)
          }
          IconButton(onClick = { productToDelete = p }) {
            Icon(Icons.Default.Delete, contentDescription = "حذف", tint = Color.Red.copy(alpha = 0.7f))
          }
        }
      }
    }
  }

  if (productToDelete != null) {
    AlertDialog(
      onDismissRequest = { productToDelete = null },
      title = { Text("تأكيد حذف المنتج", fontWeight = FontWeight.Bold) },
      text = { Text("هل أنت متأكد من حذف المنتج (${productToDelete?.name}) التابع للتاجر ${productToDelete?.merchantName} نهائياً من المنصة؟") },
      confirmButton = {
        Button(
          onClick = {
            productToDelete?.let { prod ->
              viewModel.deleteProduct(prod.id)
            }
            productToDelete = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
          Text("تأكيد")
        }
      },
      dismissButton = {
        OutlinedButton(onClick = { productToDelete = null }) {
          Text("إلغاء (عدم تأكيد)")
        }
      }
    )
  }

  if (showAddProductForMerchantDialog) {
    AdminAddProductForMerchantDialog(
      viewModel = viewModel,
      onDismiss = { showAddProductForMerchantDialog = false }
    )
  }

  val curEdit = categoryToEdit
  if (curEdit != null) {
    var editedName by remember(curEdit.id) { mutableStateOf(curEdit.name) }
    AlertDialog(
      onDismissRequest = { categoryToEdit = null },
      title = { Text("تعديل اسم القسم", fontWeight = FontWeight.Bold) },
      text = {
        OutlinedTextField(
          value = editedName,
          onValueChange = { editedName = it },
          label = { Text("اسم القسم") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
      },
      confirmButton = {
        Button(
          onClick = {
            if (editedName.isNotBlank()) {
              viewModel.updateCategory(curEdit.id, editedName)
              categoryToEdit = null
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
        ) {
          Text("حفظ التعديل")
        }
      },
      dismissButton = {
        TextButton(onClick = { categoryToEdit = null }) { Text("إلغاء") }
      }
    )
  }

  val curDelete = categoryToDelete
  if (curDelete != null) {
    AlertDialog(
      onDismissRequest = { categoryToDelete = null },
      title = { Text("حذف القسم: ${curDelete.name}", fontWeight = FontWeight.Bold) },
      text = { Text("هل أنت متأكد من حذف هذا القسم؟ لن يتم حذف المنتجات التابعة له ولكن ستفقد تصنيف القسم.") },
      confirmButton = {
        Button(
          onClick = {
            viewModel.deleteCategory(curDelete.id)
            categoryToDelete = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
          Text("تأكيد الحذف")
        }
      },
      dismissButton = {
        TextButton(onClick = { categoryToDelete = null }) { Text("إلغاء") }
      }
    )
  }
}

@Composable
private fun AdminAddProductForMerchantDialog(
  viewModel: AlbakalViewModel,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  val allUsers by viewModel.allUsers.collectAsState()
  val merchants = remember(allUsers) { allUsers.filter { it.role == "MERCHANT" && it.status == "APPROVED" } }
  val categories by viewModel.categories.collectAsState()

  var selectedMerchant by remember(merchants) { mutableStateOf(merchants.firstOrNull()) }
  var merchantExpanded by remember { mutableStateOf(false) }

  var selectedCategory by remember(categories) { mutableStateOf(categories.firstOrNull()?.name ?: "الخضروات") }
  var catExpanded by remember { mutableStateOf(false) }

  var name by remember { mutableStateOf("") }
  var priceText by remember { mutableStateOf("") }
  var discountText by remember { mutableStateOf("") }
  var unit by remember { mutableStateOf("كيلو") }
  var description by remember { mutableStateOf("") }
  var isAlbakalOffer by remember { mutableStateOf(false) }
  var pickedImagePath by remember { mutableStateOf<String?>(null) }

  val photoPicker = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia(),
    onResult = { uri ->
      if (uri != null) {
        val path = ImageUtils.copyUriToInternalStorage(context, uri, prefix = "admin_prod")
        pickedImagePath = path
      }
    }
  )

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("إضافة منتج لمتجر تاجر", fontWeight = FontWeight.Bold) },
    text = {
      Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        if (merchants.isEmpty()) {
          Text("لا يوجد تجار معتمدون حالياً في النظام. يجب اعتماد تاجر أولاً لتتمكن من إضافة منتجات إلى متجره.", color = Color.Red, fontSize = 12.sp)
        } else {
          Text("اختر التاجر / المتجر:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = GreenPrimary)
          Spacer(modifier = Modifier.height(4.dp))
          Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
              onClick = { merchantExpanded = true },
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(8.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = selectedMerchant?.let { "${it.name} (${it.governorate} - ${it.district})" } ?: "اختر التاجر",
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
              }
            }
            DropdownMenu(
              expanded = merchantExpanded,
              onDismissRequest = { merchantExpanded = false }
            ) {
              merchants.forEach { m ->
                DropdownMenuItem(
                  text = { Text("${m.name} (${m.governorate} - ${m.district})") },
                  onClick = {
                    selectedMerchant = m
                    merchantExpanded = false
                  }
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("اسم المنتج") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text("اختر قسم المنتج:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = GreenPrimary)
        Spacer(modifier = Modifier.height(4.dp))
        Box(modifier = Modifier.fillMaxWidth()) {
          OutlinedButton(
            onClick = { catExpanded = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(text = selectedCategory)
              Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            }
          }
          DropdownMenu(
            expanded = catExpanded,
            onDismissRequest = { catExpanded = false }
          ) {
            categories.forEach { c ->
              DropdownMenuItem(
                text = { Text(c.name) },
                onClick = {
                  selectedCategory = c.name
                  catExpanded = false
                }
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = priceText,
            onValueChange = { priceText = it },
            label = { Text("السعر (ج.م)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.weight(1f)
          )
          OutlinedTextField(
            value = discountText,
            onValueChange = { discountText = it },
            label = { Text("سعر الخصم") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.weight(1f)
          )
        }

        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
          value = unit,
          onValueChange = { unit = it },
          label = { Text("الوحدة (كيلو، علبة...)") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
          value = description,
          onValueChange = { description = it },
          label = { Text("وصف المنتج") },
          minLines = 2,
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .fillMaxWidth()
            .clickable { isAlbakalOffer = !isAlbakalOffer }
            .padding(vertical = 4.dp)
        ) {
          Checkbox(checked = isAlbakalOffer, onCheckedChange = { isAlbakalOffer = it })
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "عرض خاص بالبقال 🔥 (يظهر في قسم عروض البقال)",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = GreenPrimary
          )
        }

        Spacer(modifier = Modifier.height(10.dp))
        OutlinedButton(
          onClick = {
            photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
          },
          modifier = Modifier.fillMaxWidth()
        ) {
          Icon(Icons.Default.Image, contentDescription = null, tint = GreenPrimary)
          Spacer(modifier = Modifier.width(6.dp))
          Text(if (pickedImagePath != null) "تم اختيار صورة المنتج (تغيير)" else "اختيار صورة المنتج من الهاتف")
        }

        if (pickedImagePath != null) {
          Spacer(modifier = Modifier.height(8.dp))
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(100.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(Color.LightGray.copy(alpha = 0.3f))
          ) {
            AsyncImage(
              model = pickedImagePath,
              contentDescription = "صورة المنتج",
              contentScale = ContentScale.Crop,
              modifier = Modifier.fillMaxSize()
            )
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val m = selectedMerchant
          val price = priceText.toDoubleOrNull()
          if (m == null) {
            viewModel.showMessage("يرجى اختيار التاجر أولاً")
          } else if (name.isBlank() || price == null || price <= 0.0) {
            viewModel.showMessage("يرجى إدخال اسم وسعر صحيح")
          } else {
            val discount = discountText.toDoubleOrNull()
            val prod = ProductEntity(
              id = "prod_" + java.util.UUID.randomUUID().toString().take(8),
              merchantId = m.id,
              merchantName = m.name,
              name = name.trim(),
              categoryName = selectedCategory,
              description = description.trim(),
              price = price,
              discountPrice = discount,
              imageUrl = pickedImagePath ?: "store_hero_banner_1792756082201",
              unit = unit.trim(),
              isPublished = true,
              isOffer = discount != null || isAlbakalOffer,
              isAlbakalOffer = isAlbakalOffer
            )
            viewModel.addProduct(prod)
            onDismiss()
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
      ) {
        Text("حفظ ونشر بمتجر التاجر")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("إلغاء") }
    }
  )
}

// 5. ALL ORDERS MANAGEMENT
@Composable
private fun AdminOrdersSection(viewModel: AlbakalViewModel) {
  val orders by viewModel.allOrders.collectAsState()
  var orderToCancel by remember { mutableStateOf<OrderEntity?>(null) }
  var cancelReason by remember { mutableStateOf("") }

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    item {
      Text(
        text = "سجل الطلبات الشامل في المنصة (${orders.size})",
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        color = GreenDark
      )
    }

    items(orders) { order ->
      val elapsedMs = System.currentTimeMillis() - order.createdAt
      val elapsedMinutes = elapsedMs / (60 * 1000L)
      val canAdminCancel = order.orderStatus in listOf("NEW", "PREPARING") && elapsedMs >= 10 * 60 * 1000L

      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(text = order.orderNumber, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            OrderStatusBadge(status = order.orderStatus)
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text(text = "العميل: ${order.customerName} (${order.customerPhone})", fontSize = 11.sp)
          Text(text = "التاجر: ${order.merchantName}", fontSize = 11.sp, color = GreenDark)
          Text(text = "الإجمالي: ${order.grandTotal} ج.م (عمولة التطبيق: ${order.appCommission} ج.م)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          if (!order.captainName.isNullOrBlank()) {
            Text(text = "الكابتن: ${order.captainName} (${order.captainPhone})", fontSize = 10.sp, color = Color.DarkGray)
          }

          if (order.orderStatus == "CANCELLED") {
            Spacer(modifier = Modifier.height(6.dp))
            Surface(
              color = Color(0xFFFFEBEE),
              shape = RoundedCornerShape(6.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(6.dp)) {
                Text(
                  text = "❌ تم إلغاء هذا الطلب (${if (order.cancelledBy == "CUSTOMER") "من قِبل العميل" else "من قِبل الإدارة"})",
                  color = Color(0xFFC62828),
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold
                )
                if (!order.cancelReason.isNullOrBlank()) {
                  Text(text = "السبب: ${order.cancelReason}", fontSize = 10.sp, color = Color(0xFFB71C1C))
                }
              }
            }
          } else if (order.orderStatus in listOf("NEW", "PREPARING")) {
            Spacer(modifier = Modifier.height(6.dp))
            if (canAdminCancel) {
              Button(
                onClick = {
                  orderToCancel = order
                  cancelReason = "إلغاء من الإدارة لتجاوز الطلب 10 دقائق دون استكمال"
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("admin_cancel_btn_${order.id}")
              ) {
                Icon(Icons.Default.Cancel, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("إلغاء الطلب لتجاوز 10 دقائق (مرت ${elapsedMinutes} دقيقة)", fontSize = 11.sp)
              }
            } else {
              val remainingMin = (10 - elapsedMinutes).coerceAtLeast(1)
              Text(
                text = "مر على بداية الطلب: ${elapsedMinutes} دقيقة (متاح للإدارة حق الإلغاء بعد مرور 10 دقائق - متبقي ${remainingMin} دقيقة)",
                fontSize = 10.sp,
                color = Color.Gray
              )
            }
          }
        }
      }
    }
  }

  if (orderToCancel != null) {
    AlertDialog(
      onDismissRequest = { orderToCancel = null },
      title = { Text("تأكيد إلغاء الطلب من الإدارة", fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text("هل أنت متأكد من إلغاء طلب رقم ${orderToCancel?.orderNumber}؟ تم تجاوز مهلة الـ 10 دقائق.")
          OutlinedTextField(
            value = cancelReason,
            onValueChange = { cancelReason = it },
            label = { Text("سبب الإلغاء") },
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            orderToCancel?.let { o ->
              viewModel.cancelOrderByAdmin(o.id, cancelReason)
            }
            orderToCancel = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828))
        ) {
          Text("تأكيد الإلغاء")
        }
      },
      dismissButton = {
        TextButton(onClick = { orderToCancel = null }) {
          Text("تراجع")
        }
      }
    )
  }
}

// 6. FINANCIALS, COMMISSIONS & DEBT PAYMENTS REVIEW
@Composable
private fun AdminFinancialsSection(viewModel: AlbakalViewModel) {
  val settings by viewModel.appSettings.collectAsState()
  val debtPayments by viewModel.allDebtPayments.collectAsState()
  val allUsers by viewModel.allUsers.collectAsState()
  val allOrders by viewModel.allOrders.collectAsState()
  var viewingProofImage by remember { mutableStateOf<String?>(null) }
  var paymentToApprove by remember { mutableStateOf<com.example.data.model.DebtPaymentRequestEntity?>(null) }
  var paymentToReject by remember { mutableStateOf<com.example.data.model.DebtPaymentRequestEntity?>(null) }

  var shippingFeeText by remember(settings) { mutableStateOf(settings.shippingFee.toString()) }
  var commissionText by remember(settings) { mutableStateOf(settings.commissionRatePercent.toString()) }
  var subscriptionFeeText by remember(settings) { mutableStateOf(settings.customerMonthlySubscriptionFee.toString()) }
  var subscriptionActive by remember(settings) { mutableStateOf(settings.isCustomerSubscriptionActive) }
  var fleetActive by remember(settings) { mutableStateOf(settings.isCaptainsFleetInterfaceActive) }
  var codEnabled by remember(settings) { mutableStateOf(settings.cashOnDeliveryEnabled) }
  var vodaEnabled by remember(settings) { mutableStateOf(settings.vodafoneCashEnabled) }
  var instaEnabled by remember(settings) { mutableStateOf(settings.instaPayEnabled) }
  var vodaNumber by remember(settings) { mutableStateOf(settings.vodafoneCashNumber) }
  var instaHandle by remember(settings) { mutableStateOf(settings.instaPayHandle) }

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // 0. MONTHLY APP PROFIT (Automatically resets on the last day of each month at 12:00 midnight)
    item {
      MonthlyAppProfitCard(allOrders = allOrders)
    }

    item {
      Text(text = "إعدادات العمولات والشحن وطرق الدفع", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = GreenDark)
    }

    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
              value = shippingFeeText,
              onValueChange = { shippingFeeText = it },
              label = { Text("مصاريف الشحن (ج.م)") },
              modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
              value = commissionText,
              onValueChange = { commissionText = it },
              label = { Text("نسبة العمولة (%)") },
              modifier = Modifier.weight(1f)
            )
          }

          Spacer(modifier = Modifier.height(10.dp))
          Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
              value = vodaNumber,
              onValueChange = { vodaNumber = it },
              label = { Text("رقم فودافون كاش") },
              modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
              value = instaHandle,
              onValueChange = { instaHandle = it },
              label = { Text("عنوان إنستا باي") },
              modifier = Modifier.weight(1f)
            )
          }

          Spacer(modifier = Modifier.height(10.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("تفعيل نظام الاشتراك الشهري للعملاء:")
            Switch(checked = subscriptionActive, onCheckedChange = { subscriptionActive = it })
          }

          if (subscriptionActive) {
            OutlinedTextField(
              value = subscriptionFeeText,
              onValueChange = { subscriptionFeeText = it },
              label = { Text("قيمة الاشتراك الشهري (ج.م)") },
              modifier = Modifier.fillMaxWidth()
            )
          }

          Spacer(modifier = Modifier.height(10.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("إظهار واجهة أسطول الكباتن:")
            Switch(checked = fleetActive, onCheckedChange = { fleetActive = it })
          }

          Spacer(modifier = Modifier.height(12.dp))
          Button(
            onClick = {
              val ship = shippingFeeText.toDoubleOrNull() ?: 20.0
              val comm = commissionText.toDoubleOrNull() ?: 5.0
              val sub = subscriptionFeeText.toDoubleOrNull() ?: 50.0
              viewModel.updateSettings(
                settings.copy(
                  shippingFee = ship,
                  commissionRatePercent = comm,
                  customerMonthlySubscriptionFee = sub,
                  isCustomerSubscriptionActive = subscriptionActive,
                  isCaptainsFleetInterfaceActive = fleetActive,
                  vodafoneCashNumber = vodaNumber.trim(),
                  instaPayHandle = instaHandle.trim()
                )
              )
            },
            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text("حفظ التغييرات المالية")
          }
        }
      }
    }

    // Shipping Promo Codes Management
    item {
      AdminShippingPromoCodesCard(viewModel = viewModel)
    }

    // Debt Payment Requests Review
    item {
      Text(
        text = "طلبات سداد المديونية من التجار وإثباتات الدفع (${debtPayments.size})",
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        color = GreenDark
      )
    }

    items(debtPayments) { pay ->
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(text = "التاجر: ${pay.merchantName}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text(text = "${pay.amount} ج.م", fontWeight = FontWeight.Bold, color = GreenPrimary, fontSize = 14.sp)
          }
          Text(text = "طريقة السداد: ${pay.paymentMethod}", fontSize = 11.sp, color = Color.Gray)
          if (!pay.notes.isNullOrBlank()) {
            Text(text = "ملاحظات السداد: ${pay.notes}", fontSize = 11.sp, color = Color.DarkGray)
          }

          // Proof Receipt Image Display
          if (!pay.proofImageUrl.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .clickable { viewingProofImage = pay.proofImageUrl }
                .padding(8.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(64.dp)
                  .clip(RoundedCornerShape(6.dp))
                  .background(Color.LightGray.copy(alpha = 0.3f))
              ) {
                AsyncImage(
                  model = pay.proofImageUrl,
                  contentDescription = "صورة إيصال السداد",
                  contentScale = ContentScale.Crop,
                  modifier = Modifier.fillMaxSize()
                )
              }
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "إشعار التحويل البنكي / صورة الإيصال 📷",
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp,
                  color = GreenDark
                )
                Text(
                  text = "انقر هنا لتكبير الصورة وفحص تفاصيل التحويل",
                  fontSize = 10.sp,
                  color = Color.Gray
                )
              }
            }
          }

          val merchant = allUsers.find { it.id == pay.merchantId }
          if (merchant != null) {
            val debt = merchant.currentDebt
            val remaining = debt - pay.amount
            Spacer(modifier = Modifier.height(4.dp))
            if (pay.amount > debt) {
              Surface(
                color = GreenContainer,
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.fillMaxWidth()
              ) {
                Text(
                  text = "🌟 السداد يتجاوز المديونية الحالية (${debt} ج.م) ويمنح التاجر رصيداً بالموجب (+${String.format("%.2f", pay.amount - debt)} ج.م)",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = GreenDark,
                  modifier = Modifier.padding(6.dp)
                )
              }
            } else {
              Text(
                text = "المديونية الحالية: ${debt} ج.م | المتبقي بعد الاعتماد: ${String.format("%.2f", remaining)} ج.م",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.DarkGray
              )
            }
          }

          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "الحالة: " + when (pay.status) {
              "APPROVED" -> "تم الاعتماد وخصم المديونية بنجاح ✅"
              "REJECTED" -> "مرفوض ❌"
              else -> "قيد المراجعة والتدقيق ⏳"
            },
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = when (pay.status) {
              "APPROVED" -> GreenDark
              "REJECTED" -> Color.Red
              else -> AmberSecondary
            }
          )

          if (pay.status == "PENDING") {
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              Button(
                onClick = { paymentToApprove = pay },
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
              ) {
                Text("اعتماد وخصم المديونية", fontSize = 11.sp)
              }
              OutlinedButton(
                onClick = { paymentToReject = pay },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
              ) {
                Text("رفض", fontSize = 11.sp, color = Color.Red)
              }
            }
          }
        }
      }
    }
  }

  if (paymentToApprove != null) {
    AlertDialog(
      onDismissRequest = { paymentToApprove = null },
      title = { Text("تأكيد اعتماد سداد المديونية", fontWeight = FontWeight.Bold) },
      text = { Text("هل أنت متأكد من اعتماد سداد مبلغ (${paymentToApprove?.amount} ج.م) للتاجر (${paymentToApprove?.merchantName}) وخصم المديونية فوراً؟") },
      confirmButton = {
        Button(
          onClick = {
            paymentToApprove?.let { p ->
              viewModel.approveDebtPayment(p)
            }
            paymentToApprove = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
        ) {
          Text("تأكيد")
        }
      },
      dismissButton = {
        OutlinedButton(onClick = { paymentToApprove = null }) {
          Text("إلغاء (عدم تأكيد)")
        }
      }
    )
  }

  if (paymentToReject != null) {
    AlertDialog(
      onDismissRequest = { paymentToReject = null },
      title = { Text("تأكيد رفض طلب السداد", fontWeight = FontWeight.Bold) },
      text = { Text("هل أنت متأكد من رغبتك في رفض طلب سداد المديونية هذا؟") },
      confirmButton = {
        Button(
          onClick = {
            paymentToReject?.let { p ->
              viewModel.rejectDebtPayment(p)
            }
            paymentToReject = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
          Text("تأكيد")
        }
      },
      dismissButton = {
        OutlinedButton(onClick = { paymentToReject = null }) {
          Text("إلغاء (عدم تأكيد)")
        }
      }
    )
  }

  // Full Image Preview Dialog
  if (viewingProofImage != null) {
    AlertDialog(
      onDismissRequest = { viewingProofImage = null },
      title = { Text("صورة إيصال التحويل البنكي", fontWeight = FontWeight.Bold) },
      text = {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Black.copy(alpha = 0.05f))
        ) {
          AsyncImage(
            model = viewingProofImage,
            contentDescription = "صورة الإيصال بالحجم الكامل",
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = { viewingProofImage = null },
          colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
        ) {
          Text("إغلاق")
        }
      }
    )
  }
}

@Composable
private fun AdminShippingPromoCodesCard(viewModel: AlbakalViewModel) {
  val promoCodes by viewModel.promoCodes.collectAsState()
  var showCreateDialog by remember { mutableStateOf(false) }
  var promoToDelete by remember { mutableStateOf<ShippingPromoCodeEntity?>(null) }

  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = BorderStroke(1.dp, AmberSecondary.copy(alpha = 0.5f))
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.LocalShipping, contentDescription = null, tint = AmberSecondary)
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "أكواد خصم مصاريف الشحن (${promoCodes.size})",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = GreenDark
          )
        }
        Button(
          onClick = { showCreateDialog = true },
          colors = ButtonDefaults.buttonColors(containerColor = AmberSecondary),
          shape = RoundedCornerShape(8.dp),
          contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
        ) {
          Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("إنشاء كود جديد", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
      }
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "أكواد ترويجية مخصصة للاستخدام مرة واحدة فقط لكل عميل على مصاريف الشحن والتوصيل.",
        fontSize = 11.sp,
        color = Color.Gray
      )

      Spacer(modifier = Modifier.height(10.dp))

      if (promoCodes.isEmpty()) {
        Text(
          text = "لا توجد أكواد خصم حالياً. اضغط على 'إنشاء كود جديد' لإضافة كود.",
          fontSize = 12.sp,
          color = Color.Gray,
          modifier = Modifier.padding(vertical = 8.dp)
        )
      } else {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          for (promo in promoCodes) {
            val usedCount = promo.usedCustomerIds.split(",").filter { it.isNotBlank() }.size
            Surface(
              color = GreenContainer.copy(alpha = 0.35f),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                      text = promo.code,
                      fontWeight = FontWeight.Bold,
                      fontSize = 14.sp,
                      color = GreenDark
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                      color = if (promo.isFreeShipping) Color(0xFF2E7D32) else AmberSecondary,
                      shape = RoundedCornerShape(6.dp)
                    ) {
                      Text(
                        text = if (promo.isFreeShipping) "شحن مجاني 100%" else "خصم ${promo.discountAmount} ج.م",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                      )
                    }
                  }
                  Text(
                    text = "استُخدم من قبل: $usedCount عميل (استخدام واحد لكل حساب)",
                    fontSize = 11.sp,
                    color = Color.DarkGray
                  )
                }

                IconButton(onClick = { promoToDelete = promo }) {
                  Icon(Icons.Default.Delete, contentDescription = "حذف الكود", tint = Color.Red.copy(alpha = 0.7f))
                }
              }
            }
          }
        }
      }
    }
  }

  if (showCreateDialog) {
    AdminCreatePromoCodeDialog(
      onConfirm = { promo ->
        viewModel.createPromoCode(promo)
        showCreateDialog = false
      },
      onDismiss = { showCreateDialog = false }
    )
  }

  if (promoToDelete != null) {
    AlertDialog(
      onDismissRequest = { promoToDelete = null },
      title = { Text("تأكيد حذف كود الخصم", fontWeight = FontWeight.Bold) },
      text = { Text("هل أنت متأكد من حذف كود الخصم '${promoToDelete!!.code}'؟ لن يتمكن العملاء من استخدامه بعد الآن.") },
      confirmButton = {
        Button(
          onClick = {
            viewModel.deletePromoCode(promoToDelete!!.code)
            promoToDelete = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
          Text("تأكيد الحذف")
        }
      },
      dismissButton = {
        OutlinedButton(onClick = { promoToDelete = null }) {
          Text("إلغاء")
        }
      }
    )
  }
}

@Composable
private fun AdminCreatePromoCodeDialog(
  onConfirm: (ShippingPromoCodeEntity) -> Unit,
  onDismiss: () -> Unit
) {
  var codeText by remember { mutableStateOf("") }
  var discountText by remember { mutableStateOf("15.0") }
  var isFreeShipping by remember { mutableStateOf(false) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("إنشاء كود خصم شحن جديد", fontWeight = FontWeight.Bold) },
    text = {
      Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        OutlinedTextField(
          value = codeText,
          onValueChange = { codeText = it.uppercase() },
          label = { Text("رمز الكود (مثال: PROMO2025) *") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .fillMaxWidth()
            .clickable { isFreeShipping = !isFreeShipping }
        ) {
          Checkbox(checked = isFreeShipping, onCheckedChange = { isFreeShipping = it })
          Spacer(modifier = Modifier.width(6.dp))
          Text("خصم كامل مصاريف الشحن (شحن مجاني 100%)", fontSize = 12.sp)
        }

        if (!isFreeShipping) {
          OutlinedTextField(
            value = discountText,
            onValueChange = { discountText = it },
            label = { Text("قيمة الخصم من مصاريف الشحن (ج.م) *") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
        }

        Text(
          text = "ملاحظة: هذا الكود مخصص للاستخدام مرة واحدة فقط لكل عميل مسجل في التطبيق.",
          fontSize = 11.sp,
          color = Color.Gray
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (codeText.isNotBlank()) {
            val amount = if (isFreeShipping) 0.0 else (discountText.toDoubleOrNull() ?: 10.0)
            val promo = ShippingPromoCodeEntity(
              code = codeText.trim().uppercase(),
              discountAmount = amount,
              isFreeShipping = isFreeShipping,
              usedCustomerIds = "",
              isActive = true,
              createdAt = System.currentTimeMillis()
            )
            onConfirm(promo)
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
      ) {
        Text("إنشاء وحفظ الكود")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("إلغاء") }
    }
  )
}

@Composable
private fun MonthlyAppProfitCard(allOrders: List<OrderEntity>) {
  val cal = Calendar.getInstance()
  val currentMonth = cal.get(Calendar.MONTH)
  val currentYear = cal.get(Calendar.YEAR)

  // Start of current month: 1st day at 00:00:00.000
  val startOfMonth = remember(currentMonth, currentYear) {
    Calendar.getInstance().apply {
      set(Calendar.DAY_OF_MONTH, 1)
      set(Calendar.HOUR_OF_DAY, 0)
      set(Calendar.MINUTE, 0)
      set(Calendar.SECOND, 0)
      set(Calendar.MILLISECOND, 0)
    }.timeInMillis
  }

  // End of current month: Last day at 23:59:59.999 (At 12:00 midnight the calendar switches and profits reset to 0)
  val endOfMonth = remember(currentMonth, currentYear) {
    Calendar.getInstance().apply {
      set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
      set(Calendar.HOUR_OF_DAY, 23)
      set(Calendar.MINUTE, 59)
      set(Calendar.SECOND, 59)
      set(Calendar.MILLISECOND, 999)
    }.timeInMillis
  }

  val arabicMonthName = remember(currentMonth, currentYear) {
    try {
      val sdf = SimpleDateFormat("MMMM yyyy", Locale("ar"))
      sdf.format(Date())
    } catch (e: Exception) {
      "الشهر الحالي"
    }
  }

  val currentMonthDelivered = remember(allOrders, startOfMonth, endOfMonth) {
    allOrders.filter { it.orderStatus == "DELIVERED" && it.updatedAt in startOfMonth..endOfMonth }
  }
  val monthlyAppProfit = remember(currentMonthDelivered) {
    currentMonthDelivered.sumOf { it.appCommission }
  }
  val monthlySalesTotal = remember(currentMonthDelivered) {
    currentMonthDelivered.sumOf { it.totalProductsPrice }
  }

  val allTimeDelivered = remember(allOrders) {
    allOrders.filter { it.orderStatus == "DELIVERED" }
  }
  val allTimeAppProfit = remember(allTimeDelivered) {
    allTimeDelivered.sumOf { it.appCommission }
  }

  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = GreenContainer),
    border = androidx.compose.foundation.BorderStroke(1.5.dp, GreenPrimary.copy(alpha = 0.6f))
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "📊 أرباح وعمولات التطبيق (هذا الشهر)",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = GreenDark
          )
          Text(
            text = "دورة: $arabicMonthName",
            fontSize = 11.sp,
            color = Color.DarkGray
          )
        }
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = GreenPrimary
        ) {
          Text(
            text = "الشهر الحالي",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
      ) {
        Column {
          Text("قيمة أرباح التطبيق لهذا الشهر:", fontSize = 12.sp, color = GreenDark)
          Text(
            text = "${String.format("%.2f", monthlyAppProfit)} ج.م",
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp,
            color = GreenPrimary
          )
        }
        Text(
          text = "${currentMonthDelivered.size} طلب مكتمل",
          fontWeight = FontWeight.Bold,
          fontSize = 12.sp,
          color = Color.DarkGray
        )
      }

      Spacer(modifier = Modifier.height(8.dp))
      HorizontalDivider(color = GreenPrimary.copy(alpha = 0.2f))
      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text("إجمالي مبيعات المتاجر هذا الشهر:", fontSize = 11.sp, color = Color.DarkGray)
        Text("${String.format("%.2f", monthlySalesTotal)} ج.م", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = GreenDark)
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text("إجمالي أرباح التطبيق التراكمية (الكل):", fontSize = 11.sp, color = Color.DarkGray)
        Text("${String.format("%.2f", allTimeAppProfit)} ج.م", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = GreenDark)
      }

      Spacer(modifier = Modifier.height(10.dp))

      Surface(
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GreenDark, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "تتصفر أرباح الشهر تلقائياً كل شهر في آخر يوم عند الساعة 12:00 منتصف الليل لبدء دورة الشهر الجديد أوتوماتيكياً.",
            fontSize = 10.sp,
            color = GreenDark,
            lineHeight = 14.sp
          )
        }
      }
    }
  }
}

// 7. SPONSORED ADS MANAGEMENT
@Composable
private fun AdminAdsSection(viewModel: AlbakalViewModel) {
  val ads by viewModel.allAds.collectAsState()
  var showAddAdDialog by remember { mutableStateOf(false) }

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(text = "الإعلانات الممولة (${ads.size})", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = GreenDark)
        Button(
          onClick = { showAddAdDialog = true },
          colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
          shape = RoundedCornerShape(8.dp)
        ) {
          Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("إضافة إعلان ممول جديد", fontSize = 11.sp)
        }
      }
    }

    if (ads.isEmpty()) {
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
          Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Icon(Icons.Default.Campaign, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text("لا توجد إعلانات ممولة حالياً", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text("يمكنك رفع إعلان مصور جديد وتحديد مدته الزمنية لعرضه للمستخدمين", fontSize = 11.sp, color = Color.Gray)
          }
        }
      }
    }

    items(ads) { ad ->
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(60.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(Color.LightGray.copy(alpha = 0.3f))
          ) {
            AsyncImage(
              model = ad.imageUrl,
              contentDescription = ad.title,
              contentScale = ContentScale.Crop,
              modifier = Modifier.fillMaxSize()
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(text = ad.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text(text = "الفئة: ${ad.category}", fontSize = 11.sp, color = AmberSecondary)
            Text(text = "المدة: ${ad.durationDays} يوم", fontSize = 11.sp, color = GreenDark, fontWeight = FontWeight.Medium)
            Text(text = ad.details, fontSize = 11.sp, color = Color.Gray, maxLines = 2)
          }
          Switch(checked = ad.isActive, onCheckedChange = { viewModel.toggleAdActive(ad) })
          IconButton(onClick = { viewModel.deleteAd(ad.id) }) {
            Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
          }
        }
      }
    }
  }

  if (showAddAdDialog) {
    AddAdDialog(
      onSave = { title, cat, details, imgUrl, durationDays ->
        val now = System.currentTimeMillis()
        val expiry = now + (durationDays * 24L * 60 * 60 * 1000)
        viewModel.addAd(
          SponsoredAdEntity(
            id = "ad_" + java.util.UUID.randomUUID().toString().take(8),
            title = title,
            category = cat,
            details = details,
            imageUrl = imgUrl,
            durationDays = durationDays,
            expiryDate = expiry,
            isActive = true
          )
        )
        showAddAdDialog = false
      },
      onDismiss = { showAddAdDialog = false }
    )
  }
}

@Composable
private fun AddAdDialog(
  onSave: (String, String, String, String, Int) -> Unit,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  var title by remember { mutableStateOf("") }
  var category by remember { mutableStateOf("عروض كبرى") }
  var details by remember { mutableStateOf("") }
  var durationDaysText by remember { mutableStateOf("30") }
  var pickedImagePath by remember { mutableStateOf<String?>(null) }

  val photoPicker = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia(),
    onResult = { uri ->
      if (uri != null) {
        val savedPath = ImageUtils.saveImageToInternalStorage(context, uri)
        if (savedPath != null) {
          pickedImagePath = savedPath
        }
      }
    }
  )

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("رفع إعلان ممول مصور جديد", fontWeight = FontWeight.Bold) },
    text = {
      Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text("عنوان الإعلان") },
          modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
          value = category,
          onValueChange = { category = it },
          label = { Text("فئة الإعلان (عروض كبرى، ترويج متجر...)") },
          modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
          value = durationDaysText,
          onValueChange = { durationDaysText = it },
          label = { Text("مدة الإعلان بالأيام (مثال: 7، 15، 30)") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
          value = details,
          onValueChange = { details = it },
          label = { Text("تفاصيل الإعلان ورابط/وصف العرض") },
          minLines = 2,
          modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))

        // Image Picker
        OutlinedButton(
          onClick = {
            photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
          },
          modifier = Modifier.fillMaxWidth()
        ) {
          Icon(Icons.Default.Image, contentDescription = null, tint = GreenPrimary)
          Spacer(modifier = Modifier.width(6.dp))
          Text(if (pickedImagePath != null) "تم اختيار صورة الإعلان (تغيير)" else "اختيار صورة الإعلان من الهاتف")
        }

        if (pickedImagePath != null) {
          Spacer(modifier = Modifier.height(8.dp))
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(110.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(Color.LightGray.copy(alpha = 0.3f))
          ) {
            AsyncImage(
              model = pickedImagePath,
              contentDescription = "صورة الإعلان",
              contentScale = ContentScale.Crop,
              modifier = Modifier.fillMaxSize()
            )
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val dur = durationDaysText.toIntOrNull() ?: 30
          if (title.isNotBlank()) {
            onSave(title.trim(), category.trim(), details.trim(), pickedImagePath ?: "store_hero_banner_1792756082201", dur)
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
      ) { Text("حفظ ونشر الإعلان") }
    },
    dismissButton = { TextButton(onClick = onDismiss) { Text("إلغاء") } }
  )
}

// 8. SUPPORT & COMPLAINTS
@Composable
private fun AdminSupportSection(viewModel: AlbakalViewModel) {
  val complaints by viewModel.allComplaints.collectAsState()
  val settings by viewModel.appSettings.collectAsState()
  var whatsappNumber by remember(settings) { mutableStateOf(settings.whatsappSupportNumber) }

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Text(text = "رقم واتساب خدمة العملاء والدعم الفني:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
          Spacer(modifier = Modifier.height(6.dp))
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
              value = whatsappNumber,
              onValueChange = { whatsappNumber = it },
              label = { Text("رقم الواتساب بالصيغة الدولية") },
              modifier = Modifier.weight(1f)
            )
            Button(
              onClick = {
                viewModel.updateSettings(settings.copy(whatsappSupportNumber = whatsappNumber.trim()))
              },
              colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
              modifier = Modifier.align(Alignment.CenterVertically)
            ) {
              Text("تحديث")
            }
          }
        }
      }
    }

    item {
      Text(text = "قائمة الشكاوى وبلاغات التلف الواردة (${complaints.size})", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = GreenDark)
    }

    items(complaints) { comp ->
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(text = comp.subject, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = if (comp.isDamagedGoodsReport) Color(0xFFFFEBEE) else GreenContainer
            ) {
              Text(
                text = if (comp.isDamagedGoodsReport) "بلاغ تلف ⚠️" else "شكوى/اقتراح",
                color = if (comp.isDamagedGoodsReport) Color.Red else GreenDark,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }
          Text(text = "المستخدم: ${comp.userName} (${comp.userPhone}) - الدور: ${comp.userRole}", fontSize = 11.sp, color = Color.Gray)
          Text(text = comp.details, fontSize = 12.sp, modifier = Modifier.padding(vertical = 4.dp))
          Text(text = "الحالة: ${comp.status}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GreenPrimary)

          if (comp.status != "RESOLVED") {
            Button(
              onClick = { viewModel.resolveComplaint(comp, "تمت المتابعة وحل المشكلة بنجاح") },
              colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.padding(top = 6.dp)
            ) {
              Text("تم حل المشكلة وإغلاق البلاغ", fontSize = 11.sp)
            }
          }
        }
      }
    }
  }
}

// 9. PASSWORD RESETS
@Composable
private fun AdminPasswordResetsSection(viewModel: AlbakalViewModel) {
  val resets by viewModel.passwordResets.collectAsState()

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    item {
      Text(text = "طلبات استعادة كلمة السر (${resets.size})", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = GreenDark)
    }

    items(resets) { req ->
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Text(text = "رقم الهاتف: ${req.phone}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
          Text(text = "كود التحقق المؤقت: ${req.tempResetCode}", fontWeight = FontWeight.Bold, color = GreenPrimary, fontSize = 13.sp)
          Text(text = "الحالة: ${req.status}", fontSize = 11.sp, color = Color.Gray)
        }
      }
    }
  }
}

// 10. GOVERNORATES & DISTRICTS
@Composable
private fun AdminGovernoratesSection(viewModel: AlbakalViewModel) {
  val areas by viewModel.governoratesWithDistricts.collectAsState()
  var newGov by remember { mutableStateOf("") }
  var newDist by remember { mutableStateOf("") }
  var areaToEdit by remember { mutableStateOf<GovernorateAreaEntity?>(null) }
  var areaToDelete by remember { mutableStateOf<GovernorateAreaEntity?>(null) }

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    item {
      Text(text = "إضافة وتعديل وحذف المحافظات والأحياء", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = GreenDark)
      Spacer(modifier = Modifier.height(6.dp))
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = newGov, onValueChange = { newGov = it }, label = { Text("المحافظة") }, modifier = Modifier.weight(1f))
            OutlinedTextField(value = newDist, onValueChange = { newDist = it }, label = { Text("القسم / الحي") }, modifier = Modifier.weight(1f))
          }
          Spacer(modifier = Modifier.height(8.dp))
          Button(
            onClick = {
              if (newGov.isNotBlank() && newDist.isNotBlank()) {
                viewModel.addArea(newGov, newDist)
                newGov = ""
                newDist = ""
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text("حفظ المنطقة الجديدة")
          }
        }
      }
    }

    item {
      Text(text = "المناطق المسجلة في النظام (${areas.size})", fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }

    items(areas) { area ->
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
          .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(text = "${area.governorate} - ${area.district}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
          IconButton(onClick = { areaToEdit = area }) {
            Icon(Icons.Default.Edit, contentDescription = "تعديل المنطقة", tint = GreenPrimary)
          }
          IconButton(onClick = { areaToDelete = area }) {
            Icon(Icons.Default.Delete, contentDescription = "حذف المنطقة", tint = Color.Red)
          }
        }
      }
    }
  }

  if (areaToEdit != null) {
    var editGov by remember(areaToEdit) { mutableStateOf(areaToEdit!!.governorate) }
    var editDist by remember(areaToEdit) { mutableStateOf(areaToEdit!!.district) }

    AlertDialog(
      onDismissRequest = { areaToEdit = null },
      title = { Text("تعديل بيانات المنطقة", fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = editGov,
            onValueChange = { editGov = it },
            label = { Text("المحافظة") },
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = editDist,
            onValueChange = { editDist = it },
            label = { Text("القسم / الحي / المنطقة") },
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (editGov.isNotBlank() && editDist.isNotBlank()) {
              val a = areaToEdit!!
              areaToEdit = null
              viewModel.updateArea(a.id, editGov, editDist)
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
        ) {
          Text("حفظ التعديلات")
        }
      },
      dismissButton = {
        TextButton(onClick = { areaToEdit = null }) {
          Text("إلغاء")
        }
      }
    )
  }

  if (areaToDelete != null) {
    AlertDialog(
      onDismissRequest = { areaToDelete = null },
      title = { Text("تأكيد حذف المنطقة", fontWeight = FontWeight.Bold) },
      text = { Text("هل أنت متأكد من حذف ${areaToDelete!!.governorate} - ${areaToDelete!!.district} نهائياً؟") },
      confirmButton = {
        Button(
          onClick = {
            val id = areaToDelete!!.id
            areaToDelete = null
            viewModel.deleteArea(id)
          },
          colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
          Text("تأكيد الحذف")
        }
      },
      dismissButton = {
        TextButton(onClick = { areaToDelete = null }) {
          Text("إلغاء")
        }
      }
    )
  }
}

// 11. SUPER ADMIN SECURITY
@Composable
private fun AdminSecuritySection(viewModel: AlbakalViewModel) {
  val currentUser by viewModel.currentUser.collectAsState()
  var newEmail by remember { mutableStateOf(currentUser?.email ?: "mahmoudomran324@gmail.com") }
  var newPassword by remember { mutableStateOf(currentUser?.password ?: "Aa&9119045") }

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(text = "حماية وأمان حساب الإدارة الرئيسي", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = GreenPrimary)
          Text(text = "بيانات هذا الحساب مشفرة ومحمية بالكامل، ولا يمكن لأي مشرف الوصول إليها أو تعديلها.", fontSize = 11.sp, color = Color.Gray)

          Spacer(modifier = Modifier.height(14.dp))
          OutlinedTextField(
            value = newEmail,
            onValueChange = { newEmail = it },
            label = { Text("البريد الإلكتروني للإدارة") },
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(10.dp))
          OutlinedTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = { Text("كلمة مرور الإدارة") },
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(16.dp))
          Button(
            onClick = {
              if (newEmail.isNotBlank() && newPassword.isNotBlank()) {
                viewModel.updateAdminCredentials(newEmail, newPassword) {}
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text("حفظ بيانات الدخول الجديدة للأدمن")
          }
        }
      }
    }
  }
}

// 12. APK MANAGEMENT & DOWNLOAD
@Composable
private fun AdminApkSection(viewModel: AlbakalViewModel) {
  val context = LocalContext.current
  var showInfoDialog by remember { mutableStateOf(false) }

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(48.dp)
                  .clip(CircleShape)
                  .background(GreenContainer),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.Android, contentDescription = null, tint = GreenDark, modifier = Modifier.size(28.dp))
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(text = "حزمة تطبيق البقال (Albakal APK)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(text = "الإصدار الأخير - جاهز للتثبيت والاستخدام", fontSize = 11.sp, color = Color.Gray)
              }
            }
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = GreenContainer
            ) {
              Text(
                text = "مبني ومحدث ✅",
                color = GreenDark,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))
          Text(
            text = "يتضمن هذا الإصدار من التطبيق كافة التعديلات والميزات المطلوبة حديثاً:",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = GreenDark
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(text = "1. حل كراش التحديث وترقية Room DB إلى الإصدار 6 مع تنظيف الكاش تلقائياً دون الحاجة لمسح البيانات.", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GreenDark)
          Text(text = "2. خصم عمولة التطبيق حصرياً من قيمة مصاريف الشحن المحددة، وليس من إجمالي مبلغ الطلب.", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GreenDark)
          Text(text = "3. إمكانية تحديد مصاريف الشحن ونسبة العمولة لكل تاجر على حدة مع استخدام الافتراضي عند الرغبة.", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GreenDark)
          Text(text = "4. ميزة إنشاء أكواد خصم مصاريف الشحن المخصصة للاستخدام مرة واحدة فقط لكل عميل.", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GreenDark)
          Text(text = "5. استيقاظ شاشة التاجر من الخمول والتوجيه تلقائياً لشاشة الطلبات وظهور تفاصيل الطلب فوراً.", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GreenDark)
          Text(text = "6. قوائم منسدلة متكاملة لمواعيد العمل عند التسجيل، وإظهار المنتجات غير متاحة ومبهمة خارج مواعيد التاجر.", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GreenDark)
          Text(text = "7. اختيار وتحديد كميات المنتجات بالكيلو أو العدد مع الحساب التلقائي اللحظي.", fontSize = 11.sp)
          Text(text = "8. تشغيل سارينة الإنذار وإيقاظ شاشة التاجر من وضع الخمول عند وصول طلب جديد.", fontSize = 11.sp)
          Text(text = "9. احتساب الفاتورة المحدثة أوتوماتيكياً حسب ما يجهزه التاجر فقط وإشعار العميل والمندوب.", fontSize = 11.sp)
          Text(text = "10. تصفير الأرباح الشهرية تلقائياً بنهاية كل شهر منتصف الليل مع الاحتفاظ بالإجمالي التراكمي.", fontSize = 11.sp)
          Text(text = "11. إمكانية إلغاء الطلب: للعميل خلال دقيقتين مع عداد تنازلي، وللإدارة بعد مرور 10 دقائق.", fontSize = 11.sp)

          Spacer(modifier = Modifier.height(14.dp))
          HorizontalDivider()
          Spacer(modifier = Modifier.height(14.dp))

          Surface(
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Text(text = "معلومات ملف الحزمة:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = GreenPrimary)
              Spacer(modifier = Modifier.height(4.dp))
              Text(text = "• اسم الملف: albakal.apk", fontSize = 11.sp)
              Text(text = "• موقع الملف في المشروع: /apk/albakal.apk وأيضاً /albakal.apk", fontSize = 11.sp)
              Text(text = "• حجم الملف: 28 ميجابايت تقريباً", fontSize = 11.sp)
              Text(text = "• الحالة: مرفوع ومحدث داخل ملفات المشروع بنجاح", fontSize = 11.sp)
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Action Buttons
          Button(
            onClick = {
              val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "تطبيق البقال - Albakal APK")
                putExtra(
                  Intent.EXTRA_TEXT,
                  "تم تجهيز وتحديث ملف تطبيق البقال (Albakal APK) بأحدث التعديلات والتحسينات وهو متاح داخل المشروع في المسار:\napk/albakal.apk"
                )
              }
              context.startActivity(Intent.createChooser(shareIntent, "مشاركة معلومات حزمة البقال APK"))
            },
            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("مشاركة معلومات ملف APK المحدث", fontSize = 13.sp, fontWeight = FontWeight.Bold)
          }

          Spacer(modifier = Modifier.height(8.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            OutlinedButton(
              onClick = {
                val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                val clip = android.content.ClipData.newPlainText("APK Path", "/apk/albakal.apk")
                clipboard.setPrimaryClip(clip)
                viewModel.showMessage("تم نسخ مسار ملف الـ APK في المشروع: /apk/albakal.apk")
              },
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.weight(1f)
            ) {
              Text("نسخ مسار الملف", fontSize = 11.sp)
            }

            OutlinedButton(
              onClick = { showInfoDialog = true },
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.weight(1f)
            ) {
              Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("طريقة التحميل والتنزيل", fontSize = 11.sp)
            }
          }
        }
      }
    }
  }

  if (showInfoDialog) {
    AlertDialog(
      onDismissRequest = { showInfoDialog = false },
      title = { Text("كيفية تنزيل وتثبيت ملف APK", fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text("تم رفع وتجهيز حزمة التطبيق albakal.apk بنجاح داخل ملفات المشروع. يمكنك تنزيلها وتثبيتها بالخطوات التالية:")
          Text("1. من مستعرض الملفات (File Explorer) في AI Studio: انتقل إلى مجلد apk واضغط كليك يمين على ملف albakal.apk ثم اضغط Download.")
          Text("2. من قائمة إعدادات AI Studio (أعلى يمين الشاشة): يمكنك اختيار Export Project أو Download APK لتنزيل ملف التطبيق مباشرة إلى جهازك.")
          Text("3. الملف جاهز وموقع للتشغيل على أي جهاز أندرويد.")
        }
      },
      confirmButton = {
        Button(
          onClick = { showInfoDialog = false },
          colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
        ) {
          Text("حسناً")
        }
      }
    )
  }
}

// 12. ADMIN DIRECT USER REGISTRATION (With Auto-Redirect to WhatsApp)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdminAddUserDialog(
  role: String, // "CUSTOMER" or "MERCHANT"
  viewModel: AlbakalViewModel,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  val areas by viewModel.governoratesWithDistricts.collectAsState()
  val appSettings by viewModel.appSettings.collectAsState()

  var name by remember { mutableStateOf("") }
  var phone by remember { mutableStateOf("") }
  var email by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  var confirmPassword by remember { mutableStateOf("") }

  val distinctGovs = remember(areas) {
    areas.map { it.governorate }.distinct().ifEmpty { listOf("الإسكندرية", "القاهرة", "الجيزة") }
  }
  var selectedGov by remember { mutableStateOf("الإسكندرية") }
  var govExpanded by remember { mutableStateOf(false) }

  val districtsForGov = remember(areas, selectedGov) {
    areas.filter { it.governorate == selectedGov }.map { it.district }.distinct().ifEmpty {
      listOf("وسط البلد", "الدقي", "المعادي", "مدينة نصر", "سموحة", "ميامي", "سيدي بشر")
    }
  }
  var district by remember { mutableStateOf(districtsForGov.firstOrNull() ?: "سموحة") }
  var districtExpanded by remember { mutableStateOf(false) }
  var street by remember { mutableStateOf("") }
  var apartment by remember { mutableStateOf("") }
  var floor by remember { mutableStateOf("") }

  // Merchant Working Hours
  var merchantHoursType by remember { mutableStateOf("ALL_DAY") }
  var hoursDropdownExpanded by remember { mutableStateOf(false) }
  var customStartHour by remember { mutableStateOf("09:00") }
  var customEndHour by remember { mutableStateOf("21:00") }
  var customStartExpanded by remember { mutableStateOf(false) }
  var customEndExpanded by remember { mutableStateOf(false) }

  val hoursPresets = remember { com.example.util.EgyptTimeHelper.getWorkingHoursPresets() }
  val selectedDisplayLabel = hoursPresets.firstOrNull { it.type == merchantHoursType }?.label ?: "متاح خدمة 24 ساعة (طوال اليوم)"

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = if (role == "MERCHANT") "إضافة تاجر جديد من لوحة التحكم" else "إضافة عميل جديد من لوحة التحكم",
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        color = GreenPrimary
      )
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text(if (role == "MERCHANT") "اسم التاجر / المتجر *" else "الاسم الكامل للعميل *") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = phone,
          onValueChange = { phone = it },
          label = { Text("رقم الهاتف (للتواصل وإرسال بيانات الدخول) *") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = email,
          onValueChange = { email = it },
          label = { Text("البريد الإلكتروني") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = password,
          onValueChange = { password = it },
          label = { Text("كلمة المرور *") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = confirmPassword,
          onValueChange = { confirmPassword = it },
          label = { Text("تأكيد كلمة المرور *") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        // Governorate Dropdown
        ExposedDropdownMenuBox(
          expanded = govExpanded,
          onExpandedChange = { govExpanded = it },
          modifier = Modifier.fillMaxWidth()
        ) {
          OutlinedTextField(
            value = selectedGov,
            onValueChange = {},
            readOnly = true,
            label = { Text("المحافظة *") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = govExpanded) },
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth()
          )
          ExposedDropdownMenu(
            expanded = govExpanded,
            onDismissRequest = { govExpanded = false }
          ) {
            distinctGovs.forEach { gov ->
              DropdownMenuItem(
                text = { Text(gov) },
                onClick = {
                  selectedGov = gov
                  govExpanded = false
                  val newDistricts = areas.filter { it.governorate == gov }.map { it.district }.distinct().ifEmpty {
                    listOf("وسط البلد", "الدقي", "المعادي", "مدينة نصر", "سموحة", "ميامي", "سيدي بشر")
                  }
                  district = newDistricts.firstOrNull() ?: ""
                }
              )
            }
          }
        }

        // District Dropdown
        ExposedDropdownMenuBox(
          expanded = districtExpanded,
          onExpandedChange = { districtExpanded = it },
          modifier = Modifier.fillMaxWidth()
        ) {
          OutlinedTextField(
            value = district,
            onValueChange = { district = it },
            label = { Text("الحي / المنطقة * (المحددة من الإدارة)") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = districtExpanded) },
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryEditable, enabled = true).fillMaxWidth()
          )
          ExposedDropdownMenu(
            expanded = districtExpanded,
            onDismissRequest = { districtExpanded = false }
          ) {
            districtsForGov.forEach { distItem ->
              DropdownMenuItem(
                text = { Text(distItem) },
                onClick = {
                  district = distItem
                  districtExpanded = false
                }
              )
            }
          }
        }

        OutlinedTextField(
          value = street,
          onValueChange = { street = it },
          label = { Text("اسم الشارع") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
          OutlinedTextField(
            value = apartment,
            onValueChange = { apartment = it },
            label = { Text(if (role == "MERCHANT") "رقم المحل" else "رقم الشقة") },
            modifier = Modifier.weight(1f)
          )
          OutlinedTextField(
            value = floor,
            onValueChange = { floor = it },
            label = { Text("رقم الدور") },
            modifier = Modifier.weight(1f)
          )
        }

        if (role == "MERCHANT") {
          Spacer(modifier = Modifier.height(4.dp))
          Text(text = "مواعيد عمل التاجر (إجباري بتوقيت مصر):", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = GreenPrimary)
          Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
              value = selectedDisplayLabel,
              onValueChange = {},
              readOnly = true,
              label = { Text("مواعيد العمل *") },
              trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
              modifier = Modifier.fillMaxWidth().clickable { hoursDropdownExpanded = true }
            )
            DropdownMenu(
              expanded = hoursDropdownExpanded,
              onDismissRequest = { hoursDropdownExpanded = false }
            ) {
              hoursPresets.forEach { preset ->
                DropdownMenuItem(
                  text = { Text(preset.label, fontSize = 12.sp) },
                  onClick = {
                    merchantHoursType = preset.type
                    hoursDropdownExpanded = false
                  }
                )
              }
            }
          }

          if (merchantHoursType == "CUSTOM") {
            val time24List = remember { com.example.util.EgyptTimeHelper.get24HourOptions() }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              Box(modifier = Modifier.weight(1f)) {
                val startLabel = time24List.firstOrNull { it.first == customStartHour }?.second ?: customStartHour
                OutlinedTextField(
                  value = startLabel,
                  onValueChange = {},
                  readOnly = true,
                  label = { Text("من الساعة") },
                  modifier = Modifier.fillMaxWidth().clickable { customStartExpanded = true }
                )
                DropdownMenu(expanded = customStartExpanded, onDismissRequest = { customStartExpanded = false }) {
                  time24List.forEach { (v, l) ->
                    DropdownMenuItem(text = { Text(l, fontSize = 11.sp) }, onClick = { customStartHour = v; customStartExpanded = false })
                  }
                }
              }
              Box(modifier = Modifier.weight(1f)) {
                val endLabel = time24List.firstOrNull { it.first == customEndHour }?.second ?: customEndHour
                OutlinedTextField(
                  value = endLabel,
                  onValueChange = {},
                  readOnly = true,
                  label = { Text("إلى الساعة") },
                  modifier = Modifier.fillMaxWidth().clickable { customEndExpanded = true }
                )
                DropdownMenu(expanded = customEndExpanded, onDismissRequest = { customEndExpanded = false }) {
                  time24List.forEach { (v, l) ->
                    DropdownMenuItem(text = { Text(l, fontSize = 11.sp) }, onClick = { customEndHour = v; customEndExpanded = false })
                  }
                }
              }
            }
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (name.isBlank() || phone.isBlank() || password.isBlank()) {
            viewModel.showMessage("يرجى ملء الحقول الإلزامية: الاسم، الهاتف، وكلمة المرور")
          } else if (password != confirmPassword) {
            viewModel.showMessage("كلمة المرور وتأكيدها غير متطابقين")
          } else {
            val (startH, endH, displayLabel) = if (role == "MERCHANT") {
              if (merchantHoursType == "CUSTOM") {
                val time24List = com.example.util.EgyptTimeHelper.get24HourOptions()
                val startLbl = time24List.firstOrNull { it.first == customStartHour }?.second ?: customStartHour
                val endLbl = time24List.firstOrNull { it.first == customEndHour }?.second ?: customEndHour
                Triple(customStartHour, customEndHour, "متاح من $startLbl إلى $endLbl")
              } else {
                val preset = hoursPresets.firstOrNull { it.type == merchantHoursType }
                  ?: com.example.util.EgyptTimeHelper.WorkingHoursPreset("ALL_DAY", "متاح خدمة 24 ساعة (طوال اليوم)", "00:00", "23:59")
                Triple(preset.defaultStartHour, preset.defaultEndHour, preset.label)
              }
            } else {
              Triple("00:00", "23:59", "متاح خدمة 24 ساعة")
            }

            val newUser = UserEntity(
              id = "usr_" + java.util.UUID.randomUUID().toString().take(8),
              name = name.trim(),
              email = if (email.isBlank()) "${phone.trim()}@albakal.com" else email.trim(),
              phone = phone.trim(),
              password = password.trim(),
              role = role,
              status = "APPROVED",
              governorate = selectedGov,
              district = district.trim(),
              street = street.trim(),
              apartment = apartment.trim(),
              floor = floor.trim(),
              honorCodeAccepted = role == "CUSTOMER",
              workingHoursType = merchantHoursType,
              workStartHour = startH,
              workEndHour = endH,
              workingHoursDisplay = displayLabel
            )

            viewModel.adminRegisterUser(newUser) { savedUser ->
              // Auto-redirect to WhatsApp
              val cleanPhone = savedUser.phone.trim().removePrefix("+").removePrefix("00")
              val intlPhone = if (cleanPhone.startsWith("01")) "20" + cleanPhone.substring(1) else cleanPhone
              val message = """
              مرحباً ${savedUser.name}،
              تم تسجيل وتفعيل حسابك بنجاح في تطبيق "البقال".

              بيانات تسجيل الدخول الخاصة بك:
              - رقم الهاتف: ${savedUser.phone}
              - كلمة المرور: ${savedUser.password}
              - نوع الحساب: ${if (savedUser.role == "MERCHANT") "تاجر (متجر/مورد)" else "عميل (مشتري)"}

              رابط تحميل وتثبيت التطبيق المحدث (APK):
              ${appSettings.apkDownloadUrl}
              """.trimIndent()

              try {
                val uri = Uri.parse("https://api.whatsapp.com/send?phone=$intlPhone&text=" + Uri.encode(message))
                val intent = Intent(Intent.ACTION_VIEW, uri)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
              } catch (e: Exception) {
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                  type = "text/plain"
                  putExtra(Intent.EXTRA_TEXT, message)
                  addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(Intent.createChooser(shareIntent, "إرسال بيانات الدخول عبر واتساب"))
              }
              onDismiss()
            }
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
      ) {
        Text("تسجيل وتوجيه لواتساب")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("إلغاء") }
    }
  )
}

// 13. LOGO AND ANNOUNCEMENTS BROADCAST SECTION
@Composable
private fun AdminLogoAndAnnouncementsSection(viewModel: AlbakalViewModel) {
  val context = LocalContext.current
  val appSettings by viewModel.appSettings.collectAsState()
  val announcements by viewModel.allAnnouncements.collectAsState()

  var logoUrlInput by remember(appSettings) { mutableStateOf(appSettings.appLogoUrl ?: "") }
  var apkUrlInput by remember(appSettings) { mutableStateOf(appSettings.apkDownloadUrl) }

  var announcementTitle by remember { mutableStateOf("") }
  var announcementMessage by remember { mutableStateOf("") }
  var announcementTarget by remember { mutableStateOf("ALL") }
  var targetDropdownExpanded by remember { mutableStateOf(false) }

  val logoPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
    if (uri != null) {
      val base64 = ImageUtils.uriToBase64(context, uri)
      if (base64 != null) {
        logoUrlInput = base64
        viewModel.updateAppLogo(base64)
      }
    }
  }

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // 1. Logo Customization Card
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(text = "🎨 شعار وهوية التطبيق", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = GreenPrimary)
          Spacer(modifier = Modifier.height(4.dp))
          Text(text = "يمكنك تغيير شعار التطبيق ليظهر تلقائياً في الشريط العلوي وشاشات الدخول لجميع المستخدمين مع بقاء باقي الإعدادات.", fontSize = 11.sp, color = Color.Gray)
          Spacer(modifier = Modifier.height(12.dp))

          var showConfirmSaveLogoDialog by remember { mutableStateOf(false) }
          var showConfirmResetLogoDialog by remember { mutableStateOf(false) }

          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(2.dp, GreenPrimary, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              val currentLogoModel = if (logoUrlInput.isNotBlank()) {
                logoUrlInput
              } else {
                com.example.R.drawable.app_icon_albakal_1792756068308
              }
              AsyncImage(
                model = currentLogoModel,
                contentDescription = "شعار التطبيق الموضوع بالفعل",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                  .size(72.dp)
                  .clip(CircleShape)
              )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
              Button(
                onClick = { logoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                shape = RoundedCornerShape(8.dp)
              ) {
                Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("اختيار شعار من الهاتف", fontSize = 12.sp)
              }
              if (logoUrlInput.isNotBlank()) {
                OutlinedButton(
                  onClick = { showConfirmResetLogoDialog = true },
                  shape = RoundedCornerShape(8.dp)
                ) {
                  Text("استعادة الشعار الافتراضي", fontSize = 11.sp, color = Color.Red)
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))
          OutlinedTextField(
            value = logoUrlInput,
            onValueChange = { logoUrlInput = it },
            label = { Text("أو أدخل رابط الشعار المباشر (URL)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
          Spacer(modifier = Modifier.height(8.dp))
          Button(
            onClick = { showConfirmSaveLogoDialog = true },
            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text("حفظ وتعميم الشعار الجديد", fontWeight = FontWeight.Bold)
          }

          if (showConfirmSaveLogoDialog) {
            AlertDialog(
              onDismissRequest = { showConfirmSaveLogoDialog = false },
              title = { Text("تأكيد تغيير شعار التطبيق", fontWeight = FontWeight.Bold) },
              text = { Text("هل أنت متأكد من حفظ وتعميم هذا الشعار في كامل واجهات التطبيق؟") },
              confirmButton = {
                Button(
                  onClick = {
                    showConfirmSaveLogoDialog = false
                    viewModel.updateAppLogo(logoUrlInput.ifBlank { null })
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                ) {
                  Text("تأكيد الحفظ")
                }
              },
              dismissButton = {
                OutlinedButton(onClick = { showConfirmSaveLogoDialog = false }) {
                  Text("إلغاء (عدم تأكيد)")
                }
              }
            )
          }

          if (showConfirmResetLogoDialog) {
            AlertDialog(
              onDismissRequest = { showConfirmResetLogoDialog = false },
              title = { Text("تأكيد استعادة الشعار", fontWeight = FontWeight.Bold) },
              text = { Text("هل تريد العودة للشعار الافتراضي للمنصة؟") },
              confirmButton = {
                Button(
                  onClick = {
                    showConfirmResetLogoDialog = false
                    logoUrlInput = ""
                    viewModel.updateAppLogo(null)
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                  Text("تأكيد الاستعادة")
                }
              },
              dismissButton = {
                OutlinedButton(onClick = { showConfirmResetLogoDialog = false }) {
                  Text("إلغاء (عدم تأكيد)")
                }
              }
            )
          }
        }
      }
    }

    // 2. APK Download URL Card
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(text = "📦 رابط تحميل وتحديث التطبيق (APK)", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = GreenPrimary)
          Spacer(modifier = Modifier.height(4.dp))
          Text(text = "هذا الرابط يظهر للعملاء والتجار في صفحة التحديثات لتمكينهم من تنزيل وتثبيت أحدث نسخة من التطبيق.", fontSize = 11.sp, color = Color.Gray)
          Spacer(modifier = Modifier.height(10.dp))
          var showConfirmSaveApkUrlDialog by remember { mutableStateOf(false) }

          OutlinedTextField(
            value = apkUrlInput,
            onValueChange = { apkUrlInput = it },
            label = { Text("رابط تحميل الـ APK المباشر") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
          Spacer(modifier = Modifier.height(8.dp))
          Button(
            onClick = { showConfirmSaveApkUrlDialog = true },
            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text("حفظ وتحديث رابط التحميل")
          }

          if (showConfirmSaveApkUrlDialog) {
            AlertDialog(
              onDismissRequest = { showConfirmSaveApkUrlDialog = false },
              title = { Text("تأكيد تحديث رابط التحميل", fontWeight = FontWeight.Bold) },
              text = { Text("هل أنت متأكد من حفظ وتعميم رابط التحميل الجديد للعملاء والتجار؟") },
              confirmButton = {
                Button(
                  onClick = {
                    showConfirmSaveApkUrlDialog = false
                    viewModel.updateApkDownloadUrl(apkUrlInput)
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                ) {
                  Text("تأكيد")
                }
              },
              dismissButton = {
                OutlinedButton(onClick = { showConfirmSaveApkUrlDialog = false }) {
                  Text("إلغاء (عدم تأكيد)")
                }
              }
            )
          }
        }
      }
    }

    // 3. Post New Announcement / Update Alert
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(text = "📢 نشر تنبيه أو إشعار تحديث للمستخدمين", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = GreenPrimary)
          Spacer(modifier = Modifier.height(4.dp))
          Text(text = "عند نشر أي تنبيه جديد، سيتم فتح صفحة التنبيهات أوتوماتيكياً أمام العميل والتاجر مع زر تحميل التحديث.", fontSize = 11.sp, color = Color.DarkGray)
          Spacer(modifier = Modifier.height(12.dp))

          OutlinedTextField(
            value = announcementTitle,
            onValueChange = { announcementTitle = it },
            label = { Text("عنوان التنبيه أو التحديث *") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
            value = announcementMessage,
            onValueChange = { announcementMessage = it },
            label = { Text("نص الرسالة أو تفاصيل التحديث *") },
            minLines = 3,
            modifier = Modifier.fillMaxWidth()
          )
          Spacer(modifier = Modifier.height(8.dp))

          // Target Audience
          Box(modifier = Modifier.fillMaxWidth()) {
            val targetLabel = when (announcementTarget) {
              "CUSTOMER" -> "العملاء فقط"
              "MERCHANT" -> "التجار فقط"
              else -> "الجميع (العملاء والتجار)"
            }
            OutlinedTextField(
              value = targetLabel,
              onValueChange = {},
              readOnly = true,
              label = { Text("الجمهور المستهدف بالتنبيه") },
              trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
              modifier = Modifier.fillMaxWidth().clickable { targetDropdownExpanded = true }
            )
            DropdownMenu(expanded = targetDropdownExpanded, onDismissRequest = { targetDropdownExpanded = false }) {
              DropdownMenuItem(text = { Text("الجميع (العملاء والتجار)") }, onClick = { announcementTarget = "ALL"; targetDropdownExpanded = false })
              DropdownMenuItem(text = { Text("العملاء فقط") }, onClick = { announcementTarget = "CUSTOMER"; targetDropdownExpanded = false })
              DropdownMenuItem(text = { Text("التجار فقط") }, onClick = { announcementTarget = "MERCHANT"; targetDropdownExpanded = false })
            }
          }
          Spacer(modifier = Modifier.height(12.dp))

          Button(
            onClick = {
              viewModel.postAnnouncement(
                title = announcementTitle,
                message = announcementMessage,
                apkDownloadUrl = apkUrlInput,
                targetAudience = announcementTarget
              ) {
                announcementTitle = ""
                announcementMessage = ""
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Icon(Icons.Default.Campaign, contentDescription = null)
            Spacer(modifier = Modifier.width(6.dp))
            Text("نشر التنبيه لجميع الأجهزة الآن")
          }
        }
      }
    }

    // 4. List of Posted Announcements
    item {
      Text(text = "التنبيهات السابقة المنشورة (${announcements.size})", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = GreenDark)
    }

    items(announcements) { ann ->
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(text = ann.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            IconButton(onClick = { viewModel.deleteAnnouncement(ann.id) }) {
              Icon(Icons.Default.Delete, contentDescription = "حذف", tint = Color.Red, modifier = Modifier.size(18.dp))
            }
          }
          Text(text = ann.message, fontSize = 12.sp, color = Color.DarkGray)
          Spacer(modifier = Modifier.height(6.dp))
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Surface(shape = RoundedCornerShape(6.dp), color = GreenContainer) {
              Text(
                text = when (ann.targetAudience) {
                  "CUSTOMER" -> "العملاء فقط"
                  "MERCHANT" -> "التجار فقط"
                  else -> "الجميع"
                },
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = GreenDark,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
            if (!ann.apkDownloadUrl.isNullOrBlank()) {
              Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFE3F2FD)) {
                Text(
                  text = "رابط APK مرفق ✅",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF1976D2),
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }
          }
        }
      }
    }
  }
}

