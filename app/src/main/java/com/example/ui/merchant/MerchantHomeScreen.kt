package com.example.ui.merchant

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import com.example.util.ImageUtils
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Share
import com.example.ui.common.openWhatsApp
import com.example.ui.common.dialPhone
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.runtime.mutableStateMapOf
import com.example.data.model.OrderItemEntity
import com.example.data.model.OrderEntity
import com.example.data.model.ProductEntity
import com.example.ui.AlbakalViewModel
import com.example.ui.common.AlbakalTopBar
import com.example.ui.common.MerchantHonorCodeDialog
import com.example.ui.common.OrderStatusBadge
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.GreenContainer
import com.example.ui.theme.GreenDark
import com.example.ui.theme.GreenPrimary
import java.util.UUID

import androidx.compose.material.icons.filled.Info
import com.example.ui.common.AnnouncementsAndUpdatesView
import com.example.ui.common.MandatoryAreaSelectionDialog

@Composable
fun MerchantHomeScreen(viewModel: AlbakalViewModel) {
  val currentUser by viewModel.currentUser.collectAsState()
  val hasActiveAlarm by viewModel.hasActiveAlarm.collectAsState()
  val appSettings by viewModel.appSettings.collectAsState()
  val latestAnnouncement by viewModel.latestAnnouncement.collectAsState()
  val areas by viewModel.governoratesWithDistricts.collectAsState()
  var lastHandledAnnouncementId by remember { mutableStateOf<String?>(null) }

  var selectedTab by remember { mutableIntStateOf(0) }
  var showHonorDialog by remember { mutableStateOf(true) }
  var showAddProductDialog by remember { mutableStateOf(false) }

  val userNeedsAreaSelection = remember(currentUser, areas) {
    val u = currentUser
    u != null && (u.governorate.isBlank() || u.district.isBlank() || (areas.isNotEmpty() && areas.none { it.district.trim().equals(u.district.trim(), ignoreCase = true) }))
  }

  if (userNeedsAreaSelection && currentUser != null) {
    MandatoryAreaSelectionDialog(
      user = currentUser!!,
      areas = areas,
      onSave = { gov, dist ->
        viewModel.updateUserLocation(currentUser!!.id, gov, dist)
      }
    )
  }

  LaunchedEffect(latestAnnouncement) {
    val ann = latestAnnouncement
    if (ann != null && (ann.targetAudience == "ALL" || ann.targetAudience == "MERCHANT")) {
      if (lastHandledAnnouncementId != null && lastHandledAnnouncementId != ann.id) {
        selectedTab = 4
      }
      lastHandledAnnouncementId = ann.id
    }
  }

  // Auto-switch directly to Orders Tab when incoming order alarm sounds
  LaunchedEffect(hasActiveAlarm) {
    if (hasActiveAlarm) {
      selectedTab = 1
    }
  }

  LaunchedEffect(appSettings?.isCaptainsFleetInterfaceActive) {
    if (appSettings?.isCaptainsFleetInterfaceActive != true && selectedTab == 5) {
      selectedTab = 0
    }
  }

  Scaffold(
    topBar = {
      AlbakalTopBar(
        title = "لوحة التاجر - البقال",
        subtitle = currentUser?.name ?: "التاجر",
        showLogoutButton = true,
        onLogoutClick = { viewModel.logout() },
        appLogoUrl = appSettings?.appLogoUrl
      )
    },
    bottomBar = {
      MerchantBottomNav(
        selectedTab = selectedTab,
        isFleetActive = appSettings?.isCaptainsFleetInterfaceActive == true,
        onTabSelected = { selectedTab = it }
      )
    },
    floatingActionButton = {
      if (selectedTab == 0) {
        FloatingActionButton(
          onClick = { showAddProductDialog = true },
          containerColor = GreenPrimary,
          contentColor = Color.White,
          modifier = Modifier.testTag("merchant_add_product_fab")
        ) {
          Icon(Icons.Default.Add, contentDescription = "إضافة منتج")
        }
      }
    }
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .background(MaterialTheme.colorScheme.background)
    ) {
      // Active Sound Alert Banner (Ceases when order is acknowledged)
      AnimatedVisibility(visible = hasActiveAlarm) {
        Surface(
          color = Color(0xFFD32F2F),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = "🚨 تنبيه: لديك طلبات جديدة واردة! الرنين مستمر حتى تضغط على 'تحضير المنتج'",
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp,
              modifier = Modifier.weight(1f)
            )
          }
        }
      }

      when (selectedTab) {
        0 -> MerchantProductsTab(viewModel)
        1 -> MerchantOrdersTab(viewModel)
        2 -> MerchantDebtAndFinancialsTab(viewModel)
        3 -> MerchantProfileTab(viewModel, onNavigateToCaptains = { selectedTab = 5 })
        4 -> AnnouncementsAndUpdatesView(viewModel, "MERCHANT")
        5 -> if (appSettings?.isCaptainsFleetInterfaceActive == true) MerchantCaptainsTab(viewModel) else MerchantProductsTab(viewModel)
      }
    }
  }

  if (showHonorDialog) {
    MerchantHonorCodeDialog(
      onAccept = {
        viewModel.acceptHonorCode()
        showHonorDialog = false
      },
      onDismiss = { showHonorDialog = false }
    )
  }

  if (showAddProductDialog) {
    MerchantAddProductDialog(
      viewModel = viewModel,
      onDismiss = { showAddProductDialog = false }
    )
  }
}

@Composable
private fun MerchantBottomNav(
  selectedTab: Int,
  isFleetActive: Boolean = false,
  onTabSelected: (Int) -> Unit
) {
  NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
    NavigationBarItem(
      selected = selectedTab == 0,
      onClick = { onTabSelected(0) },
      icon = { Icon(Icons.Default.Inventory, contentDescription = "منتجاتي") },
      label = { Text("منتجاتي", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
      colors = NavigationBarItemDefaults.colors(selectedIconColor = GreenPrimary),
      modifier = Modifier.testTag("m_tab_products")
    )
    NavigationBarItem(
      selected = selectedTab == 1,
      onClick = { onTabSelected(1) },
      icon = { Icon(Icons.Default.LocalShipping, contentDescription = "الطلبات") },
      label = { Text("الطلبات", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
      colors = NavigationBarItemDefaults.colors(selectedIconColor = GreenPrimary),
      modifier = Modifier.testTag("m_tab_orders")
    )
    if (isFleetActive) {
      NavigationBarItem(
        selected = selectedTab == 5,
        onClick = { onTabSelected(5) },
        icon = { Icon(Icons.Default.DeliveryDining, contentDescription = "الكباتن") },
        label = { Text("الكباتن", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
        colors = NavigationBarItemDefaults.colors(selectedIconColor = GreenPrimary),
        modifier = Modifier.testTag("m_tab_captains")
      )
    }
    NavigationBarItem(
      selected = selectedTab == 2,
      onClick = { onTabSelected(2) },
      icon = { Icon(Icons.Default.AttachMoney, contentDescription = "المديونية") },
      label = { Text("المديونية", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
      colors = NavigationBarItemDefaults.colors(selectedIconColor = GreenPrimary),
      modifier = Modifier.testTag("m_tab_debt")
    )
    NavigationBarItem(
      selected = selectedTab == 4,
      onClick = { onTabSelected(4) },
      icon = { Icon(Icons.Default.Info, contentDescription = "التحديثات") },
      label = { Text("التحديثات", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
      colors = NavigationBarItemDefaults.colors(selectedIconColor = GreenPrimary),
      modifier = Modifier.testTag("m_tab_announcements")
    )
    NavigationBarItem(
      selected = selectedTab == 3,
      onClick = { onTabSelected(3) },
      icon = { Icon(Icons.Default.Person, contentDescription = "حسابي") },
      label = { Text("حسابي", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
      colors = NavigationBarItemDefaults.colors(selectedIconColor = GreenPrimary),
      modifier = Modifier.testTag("m_tab_profile")
    )
  }
}

// 1. MERCHANT PRODUCTS TAB
@Composable
private fun MerchantProductsTab(viewModel: AlbakalViewModel) {
  val products by viewModel.allProducts.collectAsState()
  val currentUser by viewModel.currentUser.collectAsState()
  var productToDelete by remember { mutableStateOf<ProductEntity?>(null) }

  val myProducts = remember(products, currentUser) {
    products.filter { it.merchantId == currentUser?.id }
  }

  if (myProducts.isEmpty()) {
    Box(
      modifier = Modifier.fillMaxSize(),
      contentAlignment = Alignment.Center
    ) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Default.Inventory, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(60.dp))
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = "لم تقم بإضافة أي منتجات حتى الآن", color = Color.Gray)
        Text(text = "اضغط على الزر الدائري (+) لإضافة منتجك الأول", fontSize = 12.sp, color = Color.Gray)
      }
    }
    return
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 14.dp),
    contentPadding = PaddingValues(vertical = 14.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    item {
      Text(
        text = "منتجات متجرك المعروضة (${myProducts.size})",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = GreenDark
      )
    }

    items(myProducts) { product ->
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(text = product.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(text = "القسم: ${product.categoryName} • الوحدة: ${product.unit}", fontSize = 11.sp, color = Color.Gray)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Text(text = "${product.price} ج.م", fontWeight = FontWeight.Bold, color = GreenPrimary)
              if (product.discountPrice != null) {
                Text(text = "خصم: ${product.discountPrice} ج.م", color = AmberSecondary, fontSize = 12.sp)
              }
            }
          }
          IconButton(
            onClick = { productToDelete = product },
            modifier = Modifier.testTag("delete_prod_${product.id}")
          ) {
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
      text = { Text("هل أنت متأكد من رغبتك في حذف المنتج (${productToDelete?.name}) نهائياً من متجرك؟") },
      confirmButton = {
        Button(
          onClick = {
            productToDelete?.let { p ->
              viewModel.deleteProduct(p.id)
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
}

// 2. MERCHANT ORDERS TAB (Stages & Sound Cease)
@Composable
private fun MerchantOrdersTab(viewModel: AlbakalViewModel) {
  val orders by viewModel.allOrders.collectAsState()
  val currentUser by viewModel.currentUser.collectAsState()

  var selectedOrderForShipping by remember { mutableStateOf<OrderEntity?>(null) }
  var selectedOrderForPreparation by remember { mutableStateOf<OrderEntity?>(null) }
  var orderToConfirmDelivered by remember { mutableStateOf<OrderEntity?>(null) }

  val myOrders = remember(orders, currentUser) {
    orders
      .filter { it.merchantId == currentUser?.id }
      .sortedWith(
        compareBy<OrderEntity> {
          when (it.orderStatus) {
            "NEW" -> 0
            "PREPARING" -> 1
            "SHIPPED" -> 2
            "DELIVERED" -> 3
            "CANCELLED" -> 4
            else -> 5
          }
        }.thenBy { it.createdAt } // Oldest orders first (FIFO)
      )
  }

  // Automatically pop up new order dialog on screen when incoming order alert triggers
  val hasActiveAlarm by viewModel.hasActiveAlarm.collectAsState()
  var autoOpenedOrderId by remember { mutableStateOf<String?>(null) }
  LaunchedEffect(hasActiveAlarm, myOrders) {
    if (hasActiveAlarm) {
      val incomingNewOrder = myOrders.firstOrNull { it.orderStatus == "NEW" }
      if (incomingNewOrder != null && autoOpenedOrderId != incomingNewOrder.id) {
        selectedOrderForPreparation = incomingNewOrder
        autoOpenedOrderId = incomingNewOrder.id
      }
    }
  }

  // If customer cancels the order while merchant has it open or in view, auto-dismiss and notify
  LaunchedEffect(myOrders) {
    if (selectedOrderForPreparation != null) {
      val currentOpen = myOrders.find { it.id == selectedOrderForPreparation?.id }
      if (currentOpen == null || currentOpen.orderStatus == "CANCELLED") {
        selectedOrderForPreparation = null
        if (currentOpen?.cancelledBy == "CUSTOMER") {
          viewModel.showMessage("تم إلغاء هذا الطلب من العميل (${currentOpen.orderNumber})")
        }
      }
    }
  }

  val newOrders = remember(myOrders) { myOrders.filter { it.orderStatus == "NEW" } }
  val activeOrders = remember(myOrders) { myOrders.filter { it.orderStatus in listOf("PREPARING", "SHIPPED") } }
  val pastOrders = remember(myOrders) { myOrders.filter { it.orderStatus in listOf("DELIVERED", "CANCELLED") } }

  var orderSubTab by remember { mutableIntStateOf(if (newOrders.isNotEmpty()) 0 else if (activeOrders.isNotEmpty()) 1 else 0) }

  // Auto-switch to SubTab 0 (الطلبات الجديدة الواردة في صفحة مستقلة) whenever alert sounds or new orders exist
  LaunchedEffect(hasActiveAlarm, newOrders.size) {
    if (hasActiveAlarm || newOrders.isNotEmpty()) {
      orderSubTab = 0
    }
  }

  val context = LocalContext.current
  val powerManager = remember { context.getSystemService(Context.POWER_SERVICE) as? PowerManager }
  val isIgnoringBattery = remember {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      powerManager?.isIgnoringBatteryOptimizations(context.packageName) ?: true
    } else true
  }

  Column(modifier = Modifier.fillMaxSize()) {
    // Segmented Sub-Tabs for Orders
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      // Tab 0: New Incoming Orders (Dedicated Independent Page)
      Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (orderSubTab == 0) Color(0xFFD32F2F) else MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, if (orderSubTab == 0) Color(0xFFB71C1C) else Color.LightGray),
        modifier = Modifier
          .weight(1.1f)
          .clickable { orderSubTab = 0 }
          .testTag("merchant_subtab_new_orders")
      ) {
        Column(
          modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "🚨 طلبات جديدة",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = if (orderSubTab == 0) Color.White else Color.DarkGray
            )
            if (newOrders.isNotEmpty()) {
              Spacer(modifier = Modifier.width(4.dp))
              Surface(
                shape = CircleShape,
                color = if (orderSubTab == 0) Color.White else Color(0xFFD32F2F)
              ) {
                Text(
                  text = "${newOrders.size}",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (orderSubTab == 0) Color(0xFFD32F2F) else Color.White,
                  modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                )
              }
            }
          }
        }
      }

      // Tab 1: In Progress
      Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (orderSubTab == 1) GreenPrimary else MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, if (orderSubTab == 1) GreenDark else Color.LightGray),
        modifier = Modifier
          .weight(1f)
          .clickable { orderSubTab = 1 }
          .testTag("merchant_subtab_active_orders")
      ) {
        Column(
          modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "📦 قيد التنفيذ (${activeOrders.size})",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (orderSubTab == 1) Color.White else Color.DarkGray
          )
        }
      }

      // Tab 2: Past Orders (Delivered & Cancelled)
      Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (orderSubTab == 2) Color(0xFF455A64) else MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, if (orderSubTab == 2) Color(0xFF37474F) else Color.LightGray),
        modifier = Modifier
          .weight(1f)
          .clickable { orderSubTab = 2 }
          .testTag("merchant_subtab_past_orders")
      ) {
        Column(
          modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "📜 السجل (${pastOrders.size})",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (orderSubTab == 2) Color.White else Color.DarkGray
          )
        }
      }
    }

    val displayedOrders = when (orderSubTab) {
      0 -> newOrders
      1 -> activeOrders
      else -> pastOrders
    }

    if (displayedOrders.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(24.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          when (orderSubTab) {
            0 -> {
              Text("🔔", fontSize = 40.sp)
              Spacer(modifier = Modifier.height(8.dp))
              Text("لا توجد طلبات جديدة واردة حالياً", fontWeight = FontWeight.Bold, fontSize = 14.sp)
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                "عند وصول أي طلب جديد أثناء التنبيه، سيظهر هنا فوراً في هذه الصفحة المستقلة ليتسنى لك رؤيته جيداً.",
                fontSize = 11.sp,
                color = Color.Gray,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
              )
            }
            1 -> {
              Text("📦", fontSize = 40.sp)
              Spacer(modifier = Modifier.height(8.dp))
              Text("لا توجد طلبات قيد التجهيز أو الشحن حالياً", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            else -> {
              Text("📜", fontSize = 40.sp)
              Spacer(modifier = Modifier.height(8.dp))
              Text("لا توجد طلبات سابقة في السجل حتى الآن", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
          }
        }
      }
    } else {
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        if (!isIgnoringBattery && orderSubTab == 0) {
          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(containerColor = AmberSecondary.copy(alpha = 0.15f))
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = AmberSecondary)
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = "تفعيل إيقاظ الشاشة والتنبيه الصوتي المستمر",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFFE65100)
                  )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "لضمان إيقاظ شاشة هاتفك وسماع السارينة فوراً عند وصول أي طلب جديد حتى أثناء قفل الهاتف أو الخمول، يرجى تفعيل إذن عمل التطبيق في الخلفية.",
                  fontSize = 11.sp,
                  color = Color.DarkGray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                  onClick = {
                    try {
                      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                          data = Uri.parse("package:${context.packageName}")
                        }
                        context.startActivity(intent)
                      }
                    } catch (_: Exception) {
                      try {
                        context.startActivity(Intent(Settings.ACTION_SETTINGS))
                      } catch (_: Exception) {}
                    }
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = AmberSecondary),
                  shape = RoundedCornerShape(8.dp),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Text("منح إذن استمرار عمل التطبيق في الخلفية وإيقاظ الشاشة", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
              }
            }
          }
        }

        items(displayedOrders) { order ->
          MerchantOrderCard(
            order = order,
            viewModel = viewModel,
            onOpenPreparation = { selectedOrderForPreparation = order },
            onOpenShipping = { selectedOrderForShipping = order },
            onConfirmDelivered = { orderToConfirmDelivered = order }
          )
        }
      }
    }
  }

  // Dialog: Detailed Order Preparation with item types, units, and quantities
  if (selectedOrderForPreparation != null) {
    OrderPreparationDialog(
      order = selectedOrderForPreparation!!,
      viewModel = viewModel,
      onStartPreparing = { selectedItemIds ->
        viewModel.updatePreparedOrder(selectedOrderForPreparation!!.id, selectedItemIds)
        selectedOrderForPreparation = null
      },
      onShipOrder = { selectedItemIds ->
        val o = selectedOrderForPreparation
        if (o != null) {
          viewModel.updatePreparedOrder(o.id, selectedItemIds)
          selectedOrderForPreparation = null
          selectedOrderForShipping = o
        }
      },
      onDismiss = { selectedOrderForPreparation = null }
    )
  }

  if (selectedOrderForShipping != null) {
    CaptainDataDialog(
      order = selectedOrderForShipping!!,
      viewModel = viewModel,
      onDismiss = { selectedOrderForShipping = null }
    )
  }

  if (orderToConfirmDelivered != null) {
    AlertDialog(
      onDismissRequest = { orderToConfirmDelivered = null },
      title = { Text("تأكيد تسليم الطلب", fontWeight = FontWeight.Bold) },
      text = { Text("هل أنت متأكد من تسليم طلب رقم ${orderToConfirmDelivered?.orderNumber} للعميل بنجاح؟ سيتم تحويل الأرباح لمحفظتك تلقائياً.") },
      confirmButton = {
        Button(
          onClick = {
            orderToConfirmDelivered?.let { o ->
              viewModel.merchantConfirmDelivered(o.id)
            }
            orderToConfirmDelivered = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00695C))
        ) {
          Text("تأكيد")
        }
      },
      dismissButton = {
        OutlinedButton(onClick = { orderToConfirmDelivered = null }) {
          Text("إلغاء (عدم تأكيد)")
        }
      }
    )
  }
}

@Composable
private fun MerchantOrderCard(
  order: OrderEntity,
  viewModel: AlbakalViewModel,
  onOpenPreparation: () -> Unit,
  onOpenShipping: () -> Unit,
  onConfirmDelivered: () -> Unit
) {
  val items by viewModel.getOrderItems(order.id).collectAsState(initial = emptyList())

  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(text = order.orderNumber, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        OrderStatusBadge(status = order.orderStatus)
      }

      Spacer(modifier = Modifier.height(6.dp))
      Text(text = "العميل: ${order.customerName} - ${order.customerPhone}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)

      // Detailed Customer Address Card
      Surface(
        color = AmberSecondary.copy(alpha = 0.12f),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp)
      ) {
        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.Top) {
          Icon(Icons.Default.LocationOn, contentDescription = null, tint = AmberSecondary, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Column {
            Text(text = "📍 عنوان التوصيل بالتفصيل:", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF795548))
            Text(text = order.customerAddress, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
          }
        }
      }

      Text(
        text = "إجمالي المنتجات: ${order.totalProductsPrice} ج.م | عمولة التطبيق: ${order.appCommission} ج.م",
        fontSize = 11.sp,
        color = GreenPrimary
      )

      // Products & Quantities Breakdown Card on the Order Card
      if (items.isNotEmpty()) {
        Spacer(modifier = Modifier.height(8.dp))
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(10.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "📦 الأصناف المطلوبة بالتفصيل (${items.size}):",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = GreenDark
              )
              Text(
                text = "إجمالي الكمية: ${items.sumOf { it.quantity }}",
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                color = Color.DarkGray
              )
            }
            Spacer(modifier = Modifier.height(6.dp))
            items.forEachIndexed { idx, item ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "${idx + 1}. ${item.productName}",
                  fontWeight = FontWeight.SemiBold,
                  fontSize = 12.sp,
                  modifier = Modifier.weight(1f)
                )
                Surface(
                  color = GreenPrimary.copy(alpha = 0.12f),
                  shape = RoundedCornerShape(4.dp)
                ) {
                  Text(
                    text = "الكمية: ${item.quantity} ${item.unit}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = GreenDark,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Action buttons depending on order stage
      when (order.orderStatus) {
        "NEW" -> {
          Button(
            onClick = onOpenPreparation,
            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("ack_order_${order.id}")
          ) {
            Text("📦 جاري التجهيز (فحص نوع وكميات المنتجات)")
          }
        }
        "PREPARING" -> {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            OutlinedButton(
              onClick = onOpenPreparation,
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.weight(1f)
            ) {
              Text("📋 فحص وتجهيز الأصناف", fontSize = 11.sp)
            }
            Button(
              onClick = onOpenShipping,
              colors = ButtonDefaults.buttonColors(containerColor = AmberSecondary),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier
                .weight(1f)
                .testTag("ship_order_${order.id}")
            ) {
              Text("🛵 إرسال مع الكابتن", fontSize = 11.sp)
            }
          }
        }
        "SHIPPED" -> {
          Text(
            text = "🛵 الطلب مع الكابتن: ${order.captainName} (${order.captainPhone})",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = GreenDark
          )
          Spacer(modifier = Modifier.height(6.dp))
          Button(
            onClick = onConfirmDelivered,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00695C)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("delivered_order_${order.id}")
          ) {
            Text("تم التسليم للعميل بنجاح")
          }
        }
        "DELIVERED" -> {
          Surface(
            color = GreenContainer,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = "✅ تم تسليم الطلب وإضافة الأرباح لمحفظتك تلقائياً",
              color = GreenDark,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(8.dp)
            )
          }
        }
        "CANCELLED" -> {
          Surface(
            color = Color(0xFFFFEBEE),
            border = BorderStroke(1.dp, Color(0xFFEF5350)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Close, contentDescription = null, tint = Color(0xFFC62828), modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = if (order.cancelledBy == "CUSTOMER") "تم الإلغاء من العميل" else "تم إلغاء الطلب من الإدارة",
                  color = Color(0xFFC62828),
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold
                )
              }
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "✅ لا يتم احتساب أي عمولة للتطبيق على هذا الأوردر (عمولة التطبيق: 0.00 ج.م)",
                color = GreenDark,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
              )
              if (!order.cancelReason.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = "ملاحظة: ${order.cancelReason}",
                  color = Color(0xFFB71C1C),
                  fontSize = 10.sp
                )
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun OrderPreparationDialog(
  order: OrderEntity,
  viewModel: AlbakalViewModel,
  onStartPreparing: (Set<String>) -> Unit,
  onShipOrder: (Set<String>) -> Unit,
  onDismiss: () -> Unit
) {
  val items by viewModel.getOrderItems(order.id).collectAsState(initial = emptyList())
  val preparedCheckMap = remember(items) {
    mutableStateMapOf<String, Boolean>().apply {
      items.forEach { this[it.id] = true }
    }
  }

  // Only products shaded/checked in preparation are counted
  val selectedItems = items.filter { preparedCheckMap[it.id] != false }
  val calculatedProductsTotal = selectedItems.sumOf { it.totalPrice }
  val commissionRate = if (order.totalProductsPrice > 0.0) order.appCommission / order.totalProductsPrice else 0.05
  val calculatedCommission = (calculatedProductsTotal * commissionRate * 100.0).toLong() / 100.0

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text("تجهيز الطلب: ${order.orderNumber}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
          Text("فحص وتظليل المنتجات المتوفرة والمجهزة بدقة", fontSize = 11.sp, color = Color.Gray)
        }
        OrderStatusBadge(status = order.orderStatus)
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Customer Details Box
        Card(
          shape = RoundedCornerShape(10.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
        ) {
          Column(modifier = Modifier.padding(10.dp)) {
            Text("👤 العميل: ${order.customerName} - ${order.customerPhone}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text("📍 العنوان: ${order.customerAddress}", fontSize = 11.sp, color = Color.DarkGray)
            Text("💳 طريقة الدفع: ${order.paymentMethod}", fontSize = 11.sp, color = Color.DarkGray)
          }
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "📋 الأصناف المطلوب تجهيزها (${items.size}):",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = GreenDark
          )
          Text(
            text = "المظلل: ${selectedItems.size} من ${items.size}",
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            color = if (selectedItems.size == items.size) GreenDark else AmberSecondary
          )
        }

        if (items.isEmpty()) {
          Text("جاري استدعاء تفاصيل البضاعة...", fontSize = 12.sp, color = Color.Gray)
        } else {
          items.forEachIndexed { index, item ->
            val isChecked = preparedCheckMap[item.id] ?: true
            Card(
              shape = RoundedCornerShape(10.dp),
              colors = CardDefaults.cardColors(
                containerColor = if (isChecked) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
              ),
              border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isChecked) GreenPrimary else Color.LightGray.copy(alpha = 0.6f)
              ),
              modifier = Modifier
                .fillMaxWidth()
                .clickable { preparedCheckMap[item.id] = !isChecked }
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Checkbox(
                  checked = isChecked,
                  onCheckedChange = { preparedCheckMap[item.id] = it },
                  colors = CheckboxDefaults.colors(checkedColor = GreenPrimary)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = "${index + 1}. ${item.productName}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = if (isChecked) MaterialTheme.colorScheme.onSurface else Color.Gray
                  )
                  Spacer(modifier = Modifier.height(3.dp))
                  Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                      color = if (isChecked) AmberSecondary.copy(alpha = 0.15f) else Color.LightGray.copy(alpha = 0.3f),
                      shape = RoundedCornerShape(4.dp)
                    ) {
                      Text(
                        text = "الكمية: ${item.quantity} ${item.unit}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = if (isChecked) Color(0xFFE65100) else Color.Gray,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                      )
                    }
                    Text(
                      text = "سعر الوحدة: ${item.unitPrice} ج.م",
                      fontSize = 11.sp,
                      color = Color.Gray
                    )
                  }
                  Text(
                    text = "الإجمالي: ${item.totalPrice} ج.م",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp,
                    color = if (isChecked) GreenPrimary else Color.Gray
                  )
                }
                if (isChecked) {
                  Surface(
                    color = GreenPrimary,
                    shape = RoundedCornerShape(6.dp)
                  ) {
                    Text("مظلل للتجهيز ✓", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                  }
                } else {
                  Text("مستبعد ✕", color = Color.Gray, fontSize = 10.sp)
                }
              }
            }
          }
        }

        // Dynamic Calculation Summary Box (Calculated STRICTLY from shaded items)
        Card(
          shape = RoundedCornerShape(10.dp),
          colors = CardDefaults.cardColors(containerColor = GreenContainer),
          border = androidx.compose.foundation.BorderStroke(1.dp, GreenPrimary.copy(alpha = 0.5f))
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "الأصناف المظللة للتجهيز:",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = GreenDark
              )
              Text(
                text = "${selectedItems.size} من أصل ${items.size} صنف",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = GreenDark
              )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text("إجمالي البضاعة المجهزة فقط:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = GreenDark)
              Text("${String.format("%.2f", calculatedProductsTotal)} ج.م", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = GreenPrimary)
            }
            Spacer(modifier = Modifier.height(2.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text("عمولة التطبيق المحتسبة:", fontSize = 11.sp, color = Color.DarkGray)
              Text("${String.format("%.2f", calculatedCommission)} ج.م (تُحسب على المجهز فقط)", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFFE65100))
            }
            Spacer(modifier = Modifier.height(3.dp))
            Text(
              text = "⚡ يتم احتساب الإجمالي والعمولة أوتوماتيكياً فقط على المنتج الذي يظلل أمامك في التجهيز.",
              fontSize = 10.sp,
              color = Color.DarkGray
            )
          }
        }
      }
    },
    confirmButton = {
      val selectedIds = selectedItems.map { it.id }.toSet()
      if (order.orderStatus == "NEW") {
        Button(
          onClick = {
            if (selectedIds.isEmpty()) {
              viewModel.showMessage("يرجى تظليل صنف واحد على الأقل للتجهيز")
            } else {
              onStartPreparing(selectedIds)
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
          shape = RoundedCornerShape(8.dp)
        ) {
          Text("تأكيد التجهيز بالأصناف المظللة فقط ✅")
        }
      } else if (order.orderStatus == "PREPARING") {
        Button(
          onClick = {
            if (selectedIds.isEmpty()) {
              viewModel.showMessage("يرجى تظليل صنف واحد على الأقل للشحن")
            } else {
              onShipOrder(selectedIds)
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = AmberSecondary),
          shape = RoundedCornerShape(8.dp)
        ) {
          Text("جاهز للشحن مع الكابتن 🛵")
        }
      } else {
        Button(onClick = onDismiss) {
          Text("حسناً")
        }
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("إغلاق")
      }
    }
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CaptainDataDialog(
  order: OrderEntity,
  viewModel: AlbakalViewModel,
  onDismiss: () -> Unit
) {
  val allUsers by viewModel.allUsers.collectAsState()
  val currentUser by viewModel.currentUser.collectAsState()
  val appSettings by viewModel.appSettings.collectAsState()
  val orderItems by viewModel.getOrderItems(order.id).collectAsState(initial = emptyList())
  val context = LocalContext.current

  val localCaptains = remember(allUsers, currentUser) {
    allUsers.filter { it.role == "CAPTAIN" && it.district.trim().equals(currentUser?.district?.trim() ?: "", ignoreCase = true) }
  }

  var captainName by remember { mutableStateOf("") }
  var captainPhone by remember { mutableStateOf("") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.DeliveryDining, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text("بيانات مندوب التوصيل (الكابتن)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Text(
          text = "اختر كابتن من منطقتك أو أدخل اسم وهاتف المندوب يدوياً؛ وسيتم إرسال تفاصيل الطرد كاملة لواتساب الكابتن فور التأكيد:",
          fontSize = 12.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Show local captains chips if fleet feature is active
        if (appSettings.isCaptainsFleetInterfaceActive && localCaptains.isNotEmpty()) {
          Text(
            text = "الكباتن المتاحين في منطقتك (${currentUser?.district}):",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = GreenDark
          )
          LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            items(localCaptains) { cap ->
              val isSelected = captainPhone == cap.phone
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isSelected) GreenPrimary else Color(0xFFF1F8E9),
                border = BorderStroke(1.dp, if (isSelected) GreenDark else Color(0xFFC8E6C9)),
                modifier = Modifier.clickable {
                  captainName = cap.name
                  captainPhone = cap.phone
                }
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text("🛵", fontSize = 14.sp)
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = cap.name,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.White else GreenDark
                  )
                }
              }
            }
          }
        }

        OutlinedTextField(
          value = captainName,
          onValueChange = { captainName = it },
          label = { Text("اسم الكابتن *") },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("captain_name_input")
        )

        OutlinedTextField(
          value = captainPhone,
          onValueChange = { captainPhone = it },
          label = { Text("رقم هاتف الكابتن (لإرسال بيانات الطرد للواتساب) *") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("captain_phone_input")
        )

        Card(
          shape = RoundedCornerShape(8.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.Share, contentDescription = null, tint = GreenDark, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "فور الضغط على تأكيد، سيتم تحويلك لواتساب الكابتن برسالة تشمل اسم العميل ورقمه وعنوانه والسلع والسعر شاملاً الشحن.",
              fontSize = 11.sp,
              color = GreenDark
            )
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (captainName.isNotBlank() && captainPhone.isNotBlank()) {
            viewModel.shipOrder(order.id, captainName.trim(), captainPhone.trim())

            val itemsSummary = if (orderItems.isNotEmpty()) {
              orderItems.joinToString("\n") { "• ${it.productName} (${it.quantity} ${it.unit}) - ${it.totalPrice} ج.م" }
            } else {
              "• تفاصيل السلع متوفرة في الأوردر"
            }

            val parcelMsg = """
📦 *تفاصيل طرد توصيل طلبية البقال*
📋 *رقم الطلب:* #${order.orderNumber}
────────────────
👤 *اسم العميل:* ${order.customerName}
📞 *هاتف العميل:* ${order.customerPhone}
📍 *عنوان العميل بالتفصيل:* ${order.customerAddress}
────────────────
🛍️ *محتويات ومواصفات السلع:*
$itemsSummary
────────────────
💵 *إجمالي المبلغ المطلوب تحصيله (شامل الشحن):* ${order.grandTotal} ج.م
🏪 *التاجر:* ${currentUser?.name ?: "البقال"} (${currentUser?.district ?: ""})
────────────────
🛵 يرجى التوجه لموقع المتجر لاستلام الطرد وتوصيله للعميل.
            """.trimIndent()

            openWhatsApp(context, captainPhone.trim(), parcelMsg)
            onDismiss()
          } else {
            viewModel.showMessage("يرجى إدخال اسم ورقم هاتف الكابتن")
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
        modifier = Modifier.testTag("confirm_captain_btn")
      ) {
        Text("تأكيد وإرسال الطرد للواتساب")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("إلغاء") }
    }
  )
}

// 3. MERCHANT DEBT & FINANCIALS TAB
@Composable
private fun MerchantDebtAndFinancialsTab(viewModel: AlbakalViewModel) {
  val currentUser by viewModel.currentUser.collectAsState()
  val payments by viewModel.allDebtPayments.collectAsState()
  val settings by viewModel.appSettings.collectAsState()

  var showRepayDialog by remember { mutableStateOf(false) }

  val myPayments = remember(payments, currentUser) {
    payments.filter { it.merchantId == currentUser?.id }
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Text(
        text = "المالية والمديونية والعمولات",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = GreenDark
      )
    }

    // Financial Cards
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = GreenContainer.copy(alpha = 0.5f))
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(text = "الأرباح الشهرية الحالية:", fontSize = 13.sp)
          Text(
            text = "${currentUser?.totalEarnings ?: 0.0} ج.م",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = GreenPrimary
          )
          Spacer(modifier = Modifier.height(8.dp))
          Text(text = "تُحسب من الطلبات المسلمة فعلياً وتبدأ دورة جديدة مع بداية كل شهر دون حذف السجلات.", fontSize = 10.sp, color = Color.DarkGray)
        }
      }
    }

    // Current Debt / Credit Card
    item {
      val debt = currentUser?.currentDebt ?: 0.0
      val isCredit = debt < 0.0
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
          containerColor = if (isCredit) Color(0xFFE8F5E9) else if (debt > 0.0) Color(0xFFFFEBEE) else MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
          1.dp,
          if (isCredit) GreenPrimary else if (debt > 0.0) Color(0xFFEF5350) else Color.LightGray
        )
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          if (isCredit) {
            Text(text = "رصيد المتجر الحالي (رصيد دائن بالموجب) 🌟:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = GreenDark)
            Text(
              text = "+${String.format("%.2f", -debt)} ج.م",
              fontWeight = FontWeight.Bold,
              fontSize = 24.sp,
              color = GreenPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "لديك رصيد إيجابي دائن لدى المنصة، سيتم خصم عمولات الطلبات القادمة منه تلقائياً دون أي مديونية مستحقة.",
              fontSize = 11.sp,
              color = Color.DarkGray
            )
          } else {
            Text(text = "المديونية الحالية (عمولات التطبيق المستحقة):", fontSize = 13.sp)
            Text(
              text = "${debt} ج.م",
              fontWeight = FontWeight.Bold,
              fontSize = 22.sp,
              color = if (debt > 0.0) Color(0xFFC62828) else GreenPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "نسبة عمولة المنصة: ${settings.commissionRatePercent}% من قيمة المبيعات (تُحسب تلقائياً عند التسليم).", fontSize = 11.sp, color = Color.Gray)
          }

          Spacer(modifier = Modifier.height(12.dp))
          Button(
            onClick = { showRepayDialog = true },
            colors = ButtonDefaults.buttonColors(containerColor = AmberSecondary),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.testTag("merchant_repay_debt_btn")
          ) {
            Text("سداد المديونية / رفع إثبات التحويل")
          }
        }
      }
    }

    // Debt Payment Requests History
    item {
      Text(
        text = "سجل طلبات سداد المديونية (${myPayments.size})",
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
      )
    }

    items(myPayments) { pay ->
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(text = "مبلغ السداد: ${pay.amount} ج.م", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text(text = "الطريقة: ${pay.paymentMethod}", fontSize = 11.sp, color = Color.Gray)
            if (!pay.notes.isNullOrBlank()) {
              Text(text = "ملاحظة: ${pay.notes}", fontSize = 10.sp, color = Color.DarkGray)
            }
          }
          val statusText = when (pay.status) {
            "APPROVED" -> "معتمد ومخصوم ✅"
            "REJECTED" -> "مرفوض ❌"
            else -> "قيد المراجعة ⏳"
          }
          val statusColor = when (pay.status) {
            "APPROVED" -> GreenPrimary
            "REJECTED" -> Color.Red
            else -> AmberSecondary
          }
          Text(text = statusText, color = statusColor, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
      }
    }
  }

  if (showRepayDialog) {
    RepayDebtDialog(
      viewModel = viewModel,
      onDismiss = { showRepayDialog = false }
    )
  }
}

@Composable
private fun RepayDebtDialog(
  viewModel: AlbakalViewModel,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  val settings by viewModel.appSettings.collectAsState()
  val currentUser by viewModel.currentUser.collectAsState()

  val currentDebt = currentUser?.currentDebt ?: 0.0

  // Box 2: Starts EMPTY as requested
  var amountText by remember { mutableStateOf("") }
  var selectedMethod by remember { mutableStateOf("VODAFONE_CASH") }
  var proofNote by remember { mutableStateOf("") }
  var pickedProofPath by remember { mutableStateOf<String?>(null) }

  val photoPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia(),
    onResult = { uri ->
      if (uri != null) {
        val path = ImageUtils.copyUriToInternalStorage(context, uri, prefix = "debt_proof")
        pickedProofPath = path
      }
    }
  )

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("سداد مديونية التاجر", fontWeight = FontWeight.Bold) },
    text = {
      Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Text("طرق السداد المعتمدة لدى الإدارة:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        Text("• فودافون كاش: ${settings.vodafoneCashNumber}", fontSize = 12.sp, color = GreenPrimary)
        Text("• إنستا باي: ${settings.instaPayHandle}", fontSize = 12.sp, color = GreenPrimary)

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
              text = "1. المديونية الحالية المستحقة:",
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
          placeholder = { Text("اكتب هنا المبلغ المراد دفعه...") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("repay_amount_input")
        )

        // 3. BOX 3: Dynamic Calculation (Remaining Debt or Positive Credit Balance)
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
                text = "يرجى كتابة المبلغ في الخانة أعلاه لعرض الحساب الفوري.",
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
                text = "دفعت مبلغاً أزيد من المديونية بمقدار (+${String.format("%.2f", enteredAmt - currentDebt)} ج.م)؛ سيظهر كرصيد بالموجب في حسابك ويُخصم من أي عمولات قادمة تلقائياً.",
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
                text = "تم حساب الخصم: (${String.format("%.2f", currentDebt)} - ${String.format("%.2f", enteredAmt)} = ${String.format("%.2f", remaining)} ج.م)",
                fontSize = 11.sp,
                color = Color.DarkGray
              )
            }
          }
        }

        // Method Selection
        Text("طريقة التحويل:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        Row(verticalAlignment = Alignment.CenterVertically) {
          RadioButton(selected = selectedMethod == "VODAFONE_CASH", onClick = { selectedMethod = "VODAFONE_CASH" })
          Text("فودافون كاش", fontSize = 12.sp)
          Spacer(modifier = Modifier.width(12.dp))
          RadioButton(selected = selectedMethod == "INSTAPAY", onClick = { selectedMethod = "INSTAPAY" })
          Text("إنستا باي", fontSize = 12.sp)
        }

        // Photo Proof Picker
        OutlinedButton(
          onClick = {
            photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
          },
          modifier = Modifier.fillMaxWidth().testTag("pick_debt_proof_btn")
        ) {
          Icon(Icons.Default.Image, contentDescription = null, tint = GreenPrimary)
          Spacer(modifier = Modifier.width(6.dp))
          Text(if (pickedProofPath != null) "تم اختيار صورة الإيصال (تغيير)" else "رفع صورة إيصال التحويل (إثبات الدفع)")
        }

        if (pickedProofPath != null) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(120.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(Color.LightGray.copy(alpha = 0.3f))
          ) {
            AsyncImage(
              model = pickedProofPath,
              contentDescription = "صورة الإيصال",
              contentScale = ContentScale.Crop,
              modifier = Modifier.fillMaxSize()
            )
          }
        }

        OutlinedTextField(
          value = proofNote,
          onValueChange = { proofNote = it },
          label = { Text("بيانات التحويل أو رقم العملية كإثبات إضافي") },
          minLines = 2,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("repay_proof_input")
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val amt = amountText.toDoubleOrNull() ?: 0.0
          if (amt <= 0.0) {
            viewModel.showMessage("يرجى إدخال مبلغ صحيح في خانة المبلغ المراد سداده")
          } else {
            viewModel.submitDebtPayment(amt, selectedMethod, pickedProofPath, proofNote) {
              onDismiss()
            }
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
        modifier = Modifier.testTag("submit_repay_btn")
      ) {
        Text("إرسال طلب السداد للإدارة")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("إلغاء") }
    }
  )
}

// 4. MERCHANT PROFILE TAB
@Composable
private fun MerchantProfileTab(
  viewModel: AlbakalViewModel,
  onNavigateToCaptains: () -> Unit = {}
) {
  val context = LocalContext.current
  val currentUser by viewModel.currentUser.collectAsState()
  val settings by viewModel.appSettings.collectAsState()
  var showHonorCodeDialog by remember { mutableStateOf(false) }
  var showEditHoursDialog by remember { mutableStateOf(false) }
  var showEditShippingDialog by remember { mutableStateOf(false) }
  var showLogoutConfirmDialog by remember { mutableStateOf(false) }

  currentUser?.let { user ->
    val isOpenNow = remember(user.workingHoursType, user.workStartHour, user.workEndHour) {
      com.example.util.EgyptTimeHelper.isMerchantOpenNow(user.workingHoursType, user.workStartHour, user.workEndHour)
    }

    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // 1. Profile Info Card
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Box(
              modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(AmberSecondary.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Storefront, contentDescription = null, tint = AmberSecondary, modifier = Modifier.size(40.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = user.name, fontWeight = FontWeight.Bold, fontSize = 17.sp)
            Text(text = "متجر / تاجر معتمد في البقال", color = GreenPrimary, fontSize = 12.sp)

            Spacer(modifier = Modifier.height(14.dp))
            ProfileRow(label = "البريد الإلكتروني", value = user.email)
            ProfileRow(label = "رقم الهاتف", value = user.phone)
            ProfileRow(label = "المحافظة والحي", value = "${user.governorate} - ${user.district}")
            ProfileRow(label = "العنوان التجاري", value = "${user.street} - ${user.apartment}")
          }
        }
      }

      // Captains Quick Navigation Banner (if enabled by admin)
      if (settings.isCaptainsFleetInterfaceActive) {
        item {
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { onNavigateToCaptains() }
              .testTag("merchant_profile_captains_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = GreenPrimary.copy(alpha = 0.08f)),
            border = BorderStroke(1.dp, GreenPrimary.copy(alpha = 0.3f))
          ) {
            Row(
              modifier = Modifier.padding(14.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(GreenPrimary),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(Icons.Default.DeliveryDining, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text("كباتن التوصيل في منطقتك 🛵", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = GreenDark)
                  Text("استعراض المناديب المتاحين والتواصل معهم فوراً", fontSize = 11.sp, color = Color.Gray)
                }
              }
              Text("عرض ◄", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GreenPrimary)
            }
          }
        }
      }

      // 2. Working Hours Card (Live Status & Editor)
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
                Icon(Icons.Default.Schedule, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "مواعيد عمل المتجر", fontWeight = FontWeight.Bold, fontSize = 14.sp)
              }
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isOpenNow) GreenContainer else Color(0xFFFFEBEE)
              ) {
                Text(
                  text = if (isOpenNow) "مفتوح الآن ويستقبل الطلبات" else "مغلق حالياً (المنتجات مخفية)",
                  color = if (isOpenNow) GreenDark else Color(0xFFC62828),
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
              text = user.workingHoursDisplay,
              fontSize = 13.sp,
              fontWeight = FontWeight.SemiBold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "تختفي منتجاتك أوتوماتيكياً من متجر العملاء خارج مواعيد عملك حسب توقيت مصر لتجنب الطلبات المتأخرة.",
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              lineHeight = 16.sp,
              modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))
            Button(
              onClick = { showEditHoursDialog = true },
              colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("تعديل مواعيد العمل والاختيار من القائمة", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
          }
        }
      }

      // Custom Merchant Shipping Fee Card
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
                Icon(Icons.Default.LocalShipping, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "مصاريف شحن متجرك للعملاء", fontWeight = FontWeight.Bold, fontSize = 14.sp)
              }
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = GreenContainer
              ) {
                Text(
                  text = if (user.customShippingFee != null) "${user.customShippingFee} ج.م" else "افتراضية (${settings.shippingFee} ج.م)",
                  color = GreenDark,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = if (user.customShippingFee != null)
                "يتم احتساب مصاريف الشحن لطلبات متجرك بقيمة (${user.customShippingFee} ج.م) بدلاً من مصاريف المنصة الافتراضية."
              else
                "يتم احتساب مصاريف الشحن لمتجرك بالقيمة الافتراضية المحددة من المنصة (${settings.shippingFee} ج.م).",
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(12.dp))
            Button(
              onClick = { showEditShippingDialog = true },
              colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.fillMaxWidth().testTag("merchant_edit_shipping_btn")
            ) {
              Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("تعديل وتحديد قيمة الشحن لمتجرك", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
          }
        }
      }

      // 3. Direct APK Installer Card
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = GreenContainer),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(40.dp)
                  .background(GreenPrimary, CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.Info, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(text = "حزمة تطبيق البقال المحدثة (APK)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = GreenDark)
                Text(text = "تثبيت آخر تحديث يتضمن كافة التعديلات الجديدة مباشرة", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
              }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(
              onClick = { com.example.util.ApkInstaller.installOrShareApk(context) },
              colors = ButtonDefaults.buttonColors(containerColor = AmberSecondary),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.fillMaxWidth().testTag("merchant_install_apk_btn")
            ) {
              Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text("تثبيت وتحديث حزمة التطبيق (APK) الآن", fontWeight = FontWeight.Bold)
            }
          }
        }
      }

      item {
        OutlinedButton(
          onClick = { showHonorCodeDialog = true },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("view_honor_code_btn"),
          shape = RoundedCornerShape(10.dp)
        ) {
          Icon(Icons.Default.AssignmentTurnedIn, contentDescription = null, tint = GreenPrimary)
          Spacer(modifier = Modifier.width(6.dp))
          Text("مراجعة بنود ميثاق الشرف للتجار")
        }
      }

      item {
        Button(
          onClick = { showLogoutConfirmDialog = true },
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("merchant_logout_btn"),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
          shape = RoundedCornerShape(10.dp)
        ) {
          Text("تسجيل الخروج من الحساب", fontWeight = FontWeight.Bold)
        }
      }
    }

    if (showLogoutConfirmDialog) {
      AlertDialog(
        onDismissRequest = { showLogoutConfirmDialog = false },
        title = { Text("تأكيد تسجيل الخروج", fontWeight = FontWeight.Bold) },
        text = { Text("هل أنت متأكد من رغبتك في تسجيل الخروج من حساب التاجر؟") },
        confirmButton = {
          Button(
            onClick = {
              showLogoutConfirmDialog = false
              viewModel.logout()
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
          ) {
            Text("تأكيد")
          }
        },
        dismissButton = {
          OutlinedButton(onClick = { showLogoutConfirmDialog = false }) {
            Text("إلغاء (عدم تأكيد)")
          }
        }
      )
    }

    if (showEditHoursDialog) {
      EditMerchantHoursDialog(
        currentUser = user,
        onDismiss = { showEditHoursDialog = false },
        onSave = { type, start, end, label ->
          viewModel.updateMerchantWorkingHours(user.id, type, start, end, label)
          showEditHoursDialog = false
        }
      )
    }

    if (showEditShippingDialog) {
      var feeText by remember { mutableStateOf(user.customShippingFee?.toString() ?: "") }
      var useDefault by remember { mutableStateOf(user.customShippingFee == null) }

      AlertDialog(
        onDismissRequest = { showEditShippingDialog = false },
        title = { Text("تحديد مصاريف الشحن لمتجرك", fontWeight = FontWeight.Bold) },
        text = {
          Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Text("قيمة الشحن الافتراضية في تطبيق البقال: ${settings.shippingFee} ج.م", fontSize = 12.sp, color = Color.Gray)

            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier
                .fillMaxWidth()
                .clickable {
                  useDefault = !useDefault
                  if (useDefault) feeText = ""
                }
            ) {
              Checkbox(
                checked = useDefault,
                onCheckedChange = {
                  useDefault = it
                  if (it) feeText = ""
                }
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text("استخدام القيمة الافتراضية للمنصة (${settings.shippingFee} ج.م)", fontSize = 12.sp)
            }

            if (!useDefault) {
              OutlinedTextField(
                value = feeText,
                onValueChange = { feeText = it },
                label = { Text("قيمة الشحن لمتجرك (ج.م) *") },
                placeholder = { Text("مثال: 20.0") },
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
              if (useDefault) {
                viewModel.updateMerchantShippingFee(user.id, null) {
                  showEditShippingDialog = false
                }
              } else {
                val parsed = feeText.toDoubleOrNull()
                if (parsed != null && parsed >= 0.0) {
                  viewModel.updateMerchantShippingFee(user.id, parsed) {
                    showEditShippingDialog = false
                  }
                } else {
                  viewModel.showMessage("يرجى إدخال قيمة صحيحة للشحن")
                }
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
          ) {
            Text("حفظ التعديل")
          }
        },
        dismissButton = {
          TextButton(onClick = { showEditShippingDialog = false }) { Text("إلغاء") }
        }
      )
    }
  }

  if (showHonorCodeDialog) {
    MerchantHonorCodeDialog(
      onAccept = { showHonorCodeDialog = false },
      onDismiss = { showHonorCodeDialog = false }
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditMerchantHoursDialog(
  currentUser: com.example.data.model.UserEntity,
  onDismiss: () -> Unit,
  onSave: (String, String, String, String) -> Unit
) {
  var selectedType by remember { mutableStateOf(currentUser.workingHoursType) }
  var customStart by remember { mutableStateOf(currentUser.workStartHour) }
  var customEnd by remember { mutableStateOf(currentUser.workEndHour) }
  var dropdownExpanded by remember { mutableStateOf(false) }
  var startExpanded by remember { mutableStateOf(false) }
  var endExpanded by remember { mutableStateOf(false) }

  val hoursPresets = remember { com.example.util.EgyptTimeHelper.getWorkingHoursPresets() }
  val selectedDisplayLabel = hoursPresets.firstOrNull { it.type == selectedType }?.label
    ?: currentUser.workingHoursDisplay

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(text = "تعديل مواعيد عمل المتجر", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = GreenPrimary)
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Text(
          text = "اختر مواعيد عمل المتجر الرسمية (تُحسب بتوقيت مصر وتختفي المنتجات تلقائياً وقت الإغلاق):",
          fontSize = 12.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // ExposedDropdownMenuBox ensures options open and show properly on click
        ExposedDropdownMenuBox(
          expanded = dropdownExpanded,
          onExpandedChange = { dropdownExpanded = it },
          modifier = Modifier.fillMaxWidth()
        ) {
          OutlinedTextField(
            value = selectedDisplayLabel,
            onValueChange = {},
            readOnly = true,
            label = { Text("مواعيد العمل *") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
            modifier = Modifier
              .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
              .fillMaxWidth()
              .testTag("edit_hours_preset_dropdown")
          )
          ExposedDropdownMenu(
            expanded = dropdownExpanded,
            onDismissRequest = { dropdownExpanded = false }
          ) {
            hoursPresets.forEach { preset ->
              DropdownMenuItem(
                text = {
                  Text(
                    preset.label,
                    fontSize = 12.sp,
                    fontWeight = if (selectedType == preset.type) FontWeight.Bold else FontWeight.Normal
                  )
                },
                onClick = {
                  selectedType = preset.type
                  dropdownExpanded = false
                }
              )
            }
          }
        }

        if (selectedType == "CUSTOM") {
          Text(text = "اختر وقت البدء والانتهاء (بنظام 24 ساعة):", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = GreenDark)
          val time24List = remember { com.example.util.EgyptTimeHelper.get24HourOptions() }
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(modifier = Modifier.weight(1f)) {
              val startLbl = time24List.firstOrNull { it.first == customStart }?.second ?: customStart
              ExposedDropdownMenuBox(
                expanded = startExpanded,
                onExpandedChange = { startExpanded = it },
                modifier = Modifier.fillMaxWidth()
              ) {
                OutlinedTextField(
                  value = startLbl,
                  onValueChange = {},
                  readOnly = true,
                  label = { Text("من الساعة") },
                  trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = startExpanded) },
                  modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                    .fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = startExpanded, onDismissRequest = { startExpanded = false }) {
                  time24List.forEach { (v, l) ->
                    DropdownMenuItem(text = { Text(l, fontSize = 11.sp) }, onClick = { customStart = v; startExpanded = false })
                  }
                }
              }
            }
            Box(modifier = Modifier.weight(1f)) {
              val endLbl = time24List.firstOrNull { it.first == customEnd }?.second ?: customEnd
              ExposedDropdownMenuBox(
                expanded = endExpanded,
                onExpandedChange = { endExpanded = it },
                modifier = Modifier.fillMaxWidth()
              ) {
                OutlinedTextField(
                  value = endLbl,
                  onValueChange = {},
                  readOnly = true,
                  label = { Text("إلى الساعة") },
                  trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = endExpanded) },
                  modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                    .fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = endExpanded, onDismissRequest = { endExpanded = false }) {
                  time24List.forEach { (v, l) ->
                    DropdownMenuItem(text = { Text(l, fontSize = 11.sp) }, onClick = { customEnd = v; endExpanded = false })
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
          val (startH, endH, displayLbl) = if (selectedType == "CUSTOM") {
            val time24List = com.example.util.EgyptTimeHelper.get24HourOptions()
            val startLbl = time24List.firstOrNull { it.first == customStart }?.second ?: customStart
            val endLbl = time24List.firstOrNull { it.first == customEnd }?.second ?: customEnd
            Triple(customStart, customEnd, "متاح من $startLbl إلى $endLbl")
          } else {
            val preset = hoursPresets.firstOrNull { it.type == selectedType }
              ?: com.example.util.EgyptTimeHelper.WorkingHoursPreset("ALL_DAY", "متاح خدمة 24 ساعة (طوال اليوم)", "00:00", "23:59")
            Triple(preset.defaultStartHour, preset.defaultEndHour, preset.label)
          }
          onSave(selectedType, startH, endH, displayLbl)
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
private fun ProfileRow(label: String, value: String) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Text(text = "$label:", color = Color.Gray, fontSize = 12.sp)
    Text(text = value, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
  }
}

// Add Product Dialog
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MerchantAddProductDialog(
  viewModel: AlbakalViewModel,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  val currentUser = viewModel.currentUser.value ?: return
  val categories by viewModel.categories.collectAsState()

  var name by remember { mutableStateOf("") }
  var selectedCategory by remember { mutableStateOf(categories.firstOrNull()?.name ?: "الخضروات") }
  var catExpanded by remember { mutableStateOf(false) }
  var priceText by remember { mutableStateOf("") }
  var discountText by remember { mutableStateOf("") }
  var unit by remember { mutableStateOf("كيلو") }
  var description by remember { mutableStateOf("") }
  var isAlbakalOffer by remember { mutableStateOf(false) }
  var pickedImagePath by remember { mutableStateOf<String?>(null) }

  val productPhotoPicker = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia(),
    onResult = { uri ->
      if (uri != null) {
        val path = ImageUtils.copyUriToInternalStorage(context, uri, prefix = "product")
        pickedImagePath = path
      }
    }
  )

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("إضافة منتج جديد للمتجر", fontWeight = FontWeight.Bold) },
    text = {
      Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("اسم المنتج") },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("add_prod_name_input")
        )
        Spacer(modifier = Modifier.height(8.dp))

        ExposedDropdownMenuBox(
          expanded = catExpanded,
          onExpandedChange = { catExpanded = it },
          modifier = Modifier.fillMaxWidth()
        ) {
          OutlinedTextField(
            value = selectedCategory,
            onValueChange = {},
            readOnly = true,
            label = { Text("القسم") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = catExpanded) },
            modifier = Modifier
              .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
              .fillMaxWidth()
          )
          ExposedDropdownMenu(
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
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedTextField(
            value = priceText,
            onValueChange = { priceText = it },
            label = { Text("السعر (ج.م)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
              .weight(1f)
              .testTag("add_prod_price_input")
          )
          OutlinedTextField(
            value = discountText,
            onValueChange = { discountText = it },
            label = { Text("سعر العرض (اختياري)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
              .weight(1f)
              .testTag("add_prod_discount_input")
          )
        }

        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
          value = unit,
          onValueChange = { unit = it },
          label = { Text("الوحدة (كيلو، علبة، كيس...)") },
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
            productPhotoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
          },
          modifier = Modifier.fillMaxWidth().testTag("pick_product_photo_btn")
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
          val price = priceText.toDoubleOrNull()
          if (name.isBlank() || price == null || price <= 0.0) {
            viewModel.showMessage("يرجى إدخال اسم وسعر صحيح للمنتج")
          } else {
            val discountPrice = discountText.toDoubleOrNull()
            val prod = ProductEntity(
              id = "prod_" + UUID.randomUUID().toString().take(8),
              merchantId = currentUser.id,
              merchantName = currentUser.name,
              name = name.trim(),
              categoryName = selectedCategory,
              description = description.trim(),
              price = price,
              discountPrice = discountPrice,
              imageUrl = pickedImagePath ?: "store_hero_banner_1792756082201",
              unit = unit.trim(),
              isPublished = true,
              isOffer = discountPrice != null || isAlbakalOffer,
              isAlbakalOffer = isAlbakalOffer
            )
            viewModel.addProduct(prod)
            onDismiss()
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
        modifier = Modifier.testTag("save_new_product_btn")
      ) {
        Text("حفظ ونشر المنتج")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("إلغاء") }
    }
  )
}

// 5. MERCHANT CAPTAINS TAB (Visible when Captains fleet is active)
@Composable
private fun MerchantCaptainsTab(viewModel: AlbakalViewModel) {
  val allUsers by viewModel.allUsers.collectAsState()
  val currentUser by viewModel.currentUser.collectAsState()
  val context = LocalContext.current

  val localCaptains = remember(allUsers, currentUser) {
    allUsers.filter { it.role == "CAPTAIN" && it.district.trim().equals(currentUser?.district?.trim() ?: "", ignoreCase = true) }
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(14.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = GreenPrimary.copy(alpha = 0.08f)),
        border = BorderStroke(1.dp, GreenPrimary.copy(alpha = 0.25f))
      ) {
        Row(
          modifier = Modifier.padding(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Default.DeliveryDining, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(26.dp))
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "كباتن التوصيل في منطقتك (${localCaptains.size})",
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp,
              color = GreenDark
            )
            Text(
              text = "يتم إظهار الكباتن المعتمدين والمتاحين في نطاق (${currentUser?.governorate ?: ""} - ${currentUser?.district ?: ""}) فقط",
              fontSize = 11.sp,
              color = Color.DarkGray
            )
          }
        }
      }
    }

    if (localCaptains.isEmpty()) {
      item {
        Card(
          modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
          Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text("🛵", fontSize = 42.sp)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
              text = "لا يوجد كباتن مسجلين في منطقتك حالياً",
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "تقوم الإدارة بإضافة وتعيين الكباتن حسب المناطق. يمكنك دائماً إدخال بيانات الكابتن الخاص بك يدوياً عند إرسال أي أوردر.",
              fontSize = 12.sp,
              color = Color.Gray,
              textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
          }
        }
      }
    }

    items(localCaptains) { captain ->
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(42.dp)
                  .clip(CircleShape)
                  .background(GreenPrimary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
              ) {
                Text("🛵", fontSize = 20.sp)
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(text = captain.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(text = "📞 ${captain.phone}", fontSize = 12.sp, color = Color.DarkGray)
              }
            }

            Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFE8F5E9)) {
              Text(
                text = "متاح للتوصيل",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = GreenDark,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.LocationOn, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "نطاق التغطية: ${captain.governorate} - ${captain.district}",
              fontSize = 12.sp,
              color = Color.Gray
            )
          }

          Spacer(modifier = Modifier.height(12.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Button(
              onClick = {
                val greeting = "مرحباً كابتن ${captain.name}، معك متجر ${currentUser?.name ?: "البقال"} بخصوص طلبيات التوصيل المتاحة في منطقة ${captain.district}."
                openWhatsApp(context, captain.phone, greeting)
              },
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.weight(1f)
            ) {
              Text("محادثة واتساب 💬", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            OutlinedButton(
              onClick = { dialPhone(context, captain.phone) },
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.weight(1f)
            ) {
              Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp), tint = GreenPrimary)
              Spacer(modifier = Modifier.width(4.dp))
              Text("اتصال هاتفي", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }
  }
}

