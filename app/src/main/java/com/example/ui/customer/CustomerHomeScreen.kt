package com.example.ui.customer

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Chat
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.CartItemEntity
import com.example.data.model.OrderEntity
import com.example.data.model.ProductEntity
import com.example.data.model.SponsoredAdEntity
import com.example.data.model.UserEntity
import com.example.ui.AlbakalViewModel
import com.example.ui.common.AlbakalTopBar
import com.example.ui.common.OrderStatusBadge
import com.example.ui.common.dialPhone
import com.example.ui.common.openWhatsApp
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.GreenContainer
import com.example.ui.theme.GreenDark
import com.example.ui.theme.GreenPrimary

import androidx.compose.material.icons.filled.Info
import com.example.ui.common.AnnouncementsAndUpdatesView

@Composable
fun CustomerHomeScreen(viewModel: AlbakalViewModel) {
  val currentUser by viewModel.currentUser.collectAsState()
  val cartItems by viewModel.cart.collectAsState()
  val appSettings by viewModel.appSettings.collectAsState()
  val latestAnnouncement by viewModel.latestAnnouncement.collectAsState()
  var lastHandledAnnouncementId by remember { mutableStateOf<String?>(null) }
  var selectedTab by remember { mutableIntStateOf(0) }

  LaunchedEffect(latestAnnouncement) {
    val ann = latestAnnouncement
    if (ann != null && (ann.targetAudience == "ALL" || ann.targetAudience == "CUSTOMER")) {
      if (lastHandledAnnouncementId != null && lastHandledAnnouncementId != ann.id) {
        selectedTab = 6
      }
      lastHandledAnnouncementId = ann.id
    }
  }

  Scaffold(
    topBar = {
      AlbakalTopBar(
        title = "البقال - سوبر ماركت",
        subtitle = "مرحباً ${currentUser?.name ?: ""}",
        showLogoutButton = true,
        onLogoutClick = { viewModel.logout() },
        appLogoUrl = appSettings?.appLogoUrl
      )
    },
    bottomBar = {
      CustomerBottomNav(
        selectedTab = selectedTab,
        cartCount = cartItems.sumOf { it.quantity },
        onTabSelected = { selectedTab = it }
      )
    }
  ) { padding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .background(MaterialTheme.colorScheme.background)
    ) {
      when (selectedTab) {
        0 -> StoreTab(viewModel)
        1 -> CartTab(viewModel, onOrderPlaced = { selectedTab = 2 })
        2 -> MyOrdersTab(viewModel)
        3 -> ProfileTab(viewModel)
        4 -> FinancialsAndSubscriptionTab(viewModel)
        5 -> SupportTab(viewModel)
        6 -> AnnouncementsAndUpdatesView(viewModel, "CUSTOMER")
      }
    }
  }
}

@Composable
private fun CustomerBottomNav(
  selectedTab: Int,
  cartCount: Int,
  onTabSelected: (Int) -> Unit
) {
  NavigationBar(
    containerColor = MaterialTheme.colorScheme.surface,
    tonalElevation = 6.dp
  ) {
    NavigationBarItem(
      selected = selectedTab == 0,
      onClick = { onTabSelected(0) },
      icon = { Icon(Icons.Default.Storefront, contentDescription = "المتجر") },
      label = { Text("المتجر", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
      colors = NavigationBarItemDefaults.colors(selectedIconColor = GreenPrimary),
      modifier = Modifier.testTag("tab_store")
    )
    NavigationBarItem(
      selected = selectedTab == 1,
      onClick = { onTabSelected(1) },
      icon = {
        BadgedBox(
          badge = {
            if (cartCount > 0) {
              Badge(containerColor = AmberSecondary) {
                Text(cartCount.toString(), color = Color.White)
              }
            }
          }
        ) {
          Icon(Icons.Default.ShoppingCart, contentDescription = "السلة")
        }
      },
      label = { Text("السلة", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
      colors = NavigationBarItemDefaults.colors(selectedIconColor = GreenPrimary),
      modifier = Modifier.testTag("tab_cart")
    )
    NavigationBarItem(
      selected = selectedTab == 2,
      onClick = { onTabSelected(2) },
      icon = { Icon(Icons.Default.LocalShipping, contentDescription = "طلباتي") },
      label = { Text("طلباتي", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
      colors = NavigationBarItemDefaults.colors(selectedIconColor = GreenPrimary),
      modifier = Modifier.testTag("tab_orders")
    )
    NavigationBarItem(
      selected = selectedTab == 6,
      onClick = { onTabSelected(6) },
      icon = { Icon(Icons.Default.Info, contentDescription = "التحديثات") },
      label = { Text("التحديثات", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
      colors = NavigationBarItemDefaults.colors(selectedIconColor = GreenPrimary),
      modifier = Modifier.testTag("tab_updates")
    )
    NavigationBarItem(
      selected = selectedTab == 3,
      onClick = { onTabSelected(3) },
      icon = { Icon(Icons.Default.Person, contentDescription = "حسابي") },
      label = { Text("حسابي", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
      colors = NavigationBarItemDefaults.colors(selectedIconColor = GreenPrimary),
      modifier = Modifier.testTag("tab_profile")
    )
    NavigationBarItem(
      selected = selectedTab == 4,
      onClick = { onTabSelected(4) },
      icon = { Icon(Icons.Default.CreditCard, contentDescription = "الاشتراك") },
      label = { Text("الاشتراك", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
      colors = NavigationBarItemDefaults.colors(selectedIconColor = GreenPrimary),
      modifier = Modifier.testTag("tab_subscription")
    )
    NavigationBarItem(
      selected = selectedTab == 5,
      onClick = { onTabSelected(5) },
      icon = { Icon(Icons.Default.HeadsetMic, contentDescription = "الدعم") },
      label = { Text("الدعم", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
      colors = NavigationBarItemDefaults.colors(selectedIconColor = GreenPrimary),
      modifier = Modifier.testTag("tab_support")
    )
  }
}

// 1. STORE TAB
@Composable
private fun StoreTab(viewModel: AlbakalViewModel) {
  val products by viewModel.publishedProducts.collectAsState()
  val categories by viewModel.categories.collectAsState()
  val ads by viewModel.sponsoredAds.collectAsState()
  val allUsers by viewModel.allUsers.collectAsState()

  val merchantsMap = remember(allUsers) {
    allUsers.filter { it.role == "MERCHANT" }.associateBy { it.id }
  }

  var selectedCategory by remember { mutableStateOf("الكل") }
  var searchQuery by remember { mutableStateOf("") }
  var selectedProductForQuantity by remember { mutableStateOf<ProductEntity?>(null) }

  val filteredProducts = remember(products, selectedCategory, searchQuery) {
    products.filter { p ->
      val matchesCat = when (selectedCategory) {
        "الكل" -> true
        "🔥 عروض البقال" -> p.isAlbakalOffer || p.isOffer || (p.discountPrice != null && p.discountPrice < p.price)
        else -> p.categoryName == selectedCategory
      }
      val matchesQuery = searchQuery.isBlank() || p.name.contains(searchQuery, ignoreCase = true) ||
          p.description.contains(searchQuery, ignoreCase = true) ||
          p.merchantName.contains(searchQuery, ignoreCase = true)
      matchesCat && matchesQuery
    }
  }

  val promoCodes by viewModel.promoCodes.collectAsState()
  val currentUser by viewModel.currentUser.collectAsState()
  val context = androidx.compose.ui.platform.LocalContext.current

  val activePromo = remember(promoCodes, currentUser) {
    val custId = currentUser?.id ?: ""
    promoCodes.firstOrNull { promo ->
      promo.isActive && !promo.usedCustomerIds.split(",").map { it.trim() }.contains(custId)
    }
  }

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    contentPadding = PaddingValues(bottom = 20.dp)
  ) {
    // 1. Shipping Promo Code Announcement Bubble
    if (activePromo != null) {
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
          border = BorderStroke(1.5.dp, AmberSecondary),
          elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .testTag("store_promo_bubble")
        ) {
          Row(
            modifier = Modifier
              .padding(14.dp)
              .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(AmberSecondary),
              contentAlignment = Alignment.Center
            ) {
              Text("🎁", fontSize = 24.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = GreenPrimary
                ) {
                  Text(
                    text = "كود خصم على الشحن 🔥",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = if (activePromo.isFreeShipping) "شحن مجاني!" else "خصم ${activePromo.discountAmount} ج.م",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = GreenDark
                )
              }
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "استخدم الكود: ${activePromo.code} في السلة للحصول على الخصم فوراً",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.DarkGray
              )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
              onClick = {
                val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                val clip = android.content.ClipData.newPlainText("Promo Code", activePromo.code)
                clipboard.setPrimaryClip(clip)
                viewModel.showMessage("تم نسخ كود الخصم (${activePromo.code}) بنجاح! سيتم استخدامه في السلة 🎉")
              },
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.buttonColors(containerColor = AmberSecondary)
            ) {
              Text("نسخ الكود", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }
          }
        }
      }
    }

    // Top Hero Banner
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(140.dp)
          .padding(horizontal = 14.dp, vertical = 8.dp)
          .clip(RoundedCornerShape(16.dp))
      ) {
        AsyncImage(
          model = R.drawable.store_hero_banner_1792756082201,
          contentDescription = "Albakal Banner",
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )
      }
    }

    // Sponsored Ads Carousel
    if (ads.isNotEmpty()) {
      item {
        LazyRow(
          contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          items(ads) { ad ->
            SponsoredAdCard(ad)
          }
        }
      }
    }

    // Search Box
    item {
      OutlinedTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        placeholder = { Text("ابحث عن خضار، فاكهة، أرز، شوكولاتة...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = GreenPrimary) },
        singleLine = true,
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 14.dp, vertical = 6.dp)
          .testTag("store_search_input"),
        shape = RoundedCornerShape(12.dp)
      )
    }

    // Albakal Offers Special Highlight Banner Card
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 14.dp, vertical = 4.dp)
          .clickable { selectedCategory = "🔥 عروض البقال" }
          .testTag("albakal_offers_banner_btn"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
          containerColor = if (selectedCategory == "🔥 عروض البقال") Color(0xFFFFEBEE) else AmberSecondary.copy(alpha = 0.15f)
        ),
        border = BorderStroke(
          if (selectedCategory == "🔥 عروض البقال") 2.dp else 1.dp,
          if (selectedCategory == "🔥 عروض البقال") Color(0xFFD32F2F) else AmberSecondary
        )
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
              shape = CircleShape,
              color = Color(0xFFD32F2F),
              modifier = Modifier.size(36.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Text("🔥", fontSize = 18.sp)
              }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "عروض البقال الحصرية 🏷️",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color(0xFFB71C1C)
              )
              Text(
                text = "تخفيضات كبرى وأسعار خاصة منتقاة لك لباب بيتك",
                fontSize = 11.sp,
                color = Color.DarkGray
              )
            }
          }
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (selectedCategory == "🔥 عروض البقال") Color(0xFFD32F2F) else AmberSecondary
          ) {
            Text(
              text = if (selectedCategory == "🔥 عروض البقال") "المعروضة الآن" else "تصفح العروض",
              fontSize = 11.sp,
              color = Color.White,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
        }
      }
    }

    // Categories Horizontal Filter
    item {
      val allCats = listOf("الكل", "🔥 عروض البقال") + categories.map { it.name }
      LazyRow(
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(allCats) { cat ->
          val isSelected = cat == selectedCategory
          Surface(
            shape = RoundedCornerShape(20.dp),
            color = if (isSelected) {
              if (cat == "🔥 عروض البقال") Color(0xFFD32F2F) else GreenPrimary
            } else MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
              .clickable { selectedCategory = cat }
              .testTag("filter_cat_$cat")
          ) {
            Text(
              text = cat,
              color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
              fontSize = 13.sp,
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
            )
          }
        }
      }
    }

    // Products Section Header
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 14.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "المنتجات المتوفرة (${filteredProducts.size})",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = GreenDark
        )
      }
    }

    if (filteredProducts.isEmpty()) {
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "لا توجد منتجات مطابقة في هذا القسم حالياً",
            color = Color.Gray,
            style = MaterialTheme.typography.bodyMedium
          )
        }
      }
    } else {
      // Chunk into pairs of 2 for clean responsive grid inside LazyColumn
      val pairs = filteredProducts.chunked(2)
      items(pairs) { pair ->
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          ProductCard(
            product = pair[0],
            merchantUser = merchantsMap[pair[0].merchantId],
            onAddToCart = { selectedProductForQuantity = pair[0] },
            modifier = Modifier.weight(1f)
          )
          if (pair.size > 1) {
            ProductCard(
              product = pair[1],
              merchantUser = merchantsMap[pair[1].merchantId],
              onAddToCart = { selectedProductForQuantity = pair[1] },
              modifier = Modifier.weight(1f)
            )
          } else {
            Spacer(modifier = Modifier.weight(1f))
          }
        }
      }
    }
  }

  if (selectedProductForQuantity != null) {
    ProductQuantityDialog(
      product = selectedProductForQuantity!!,
      onConfirm = { qty ->
        viewModel.addToCart(selectedProductForQuantity!!, qty)
        selectedProductForQuantity = null
      },
      onDismiss = { selectedProductForQuantity = null }
    )
  }
}

@Composable
private fun SponsoredAdCard(ad: SponsoredAdEntity) {
  Card(
    modifier = Modifier
      .width(260.dp)
      .height(100.dp),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = AmberSecondary.copy(alpha = 0.12f)),
    border = androidx.compose.foundation.BorderStroke(1.dp, AmberSecondary.copy(alpha = 0.4f))
  ) {
    Row(
      modifier = Modifier
        .fillMaxSize()
        .padding(10.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(46.dp)
          .clip(CircleShape)
          .background(AmberSecondary),
        contentAlignment = Alignment.Center
      ) {
        Icon(Icons.Default.Storefront, contentDescription = null, tint = Color.White)
      }
      Spacer(modifier = Modifier.width(10.dp))
      Column {
        Surface(
          color = AmberSecondary,
          shape = RoundedCornerShape(6.dp)
        ) {
          Text(
            text = ad.category,
            fontSize = 9.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = ad.title,
          fontWeight = FontWeight.Bold,
          fontSize = 12.sp,
          maxLines = 1
        )
        Text(
          text = ad.details,
          fontSize = 10.sp,
          color = Color.DarkGray,
          maxLines = 2
        )
      }
    }
  }
}

@Composable
private fun ProductCard(
  product: ProductEntity,
  merchantUser: UserEntity?,
  onAddToCart: () -> Unit,
  modifier: Modifier = Modifier
) {
  val isOpen = if (merchantUser != null) {
    com.example.util.EgyptTimeHelper.isMerchantOpenNow(
      merchantUser.workingHoursType,
      merchantUser.workStartHour,
      merchantUser.workEndHour
    )
  } else {
    true
  }

  Card(
    modifier = modifier.then(if (!isOpen) Modifier.alpha(0.55f) else Modifier),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(modifier = Modifier.padding(10.dp)) {
      // Product Image or Banner
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(100.dp)
          .clip(RoundedCornerShape(10.dp))
          .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
      ) {
        val imgModel = if (product.imageUrl.isNotBlank() && (product.imageUrl.startsWith("/") || product.imageUrl.startsWith("content:") || product.imageUrl.startsWith("file:"))) {
          product.imageUrl
        } else {
          R.drawable.store_hero_banner_1792756082201
        }
        AsyncImage(
          model = imgModel,
          contentDescription = product.name,
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )
        if (!isOpen) {
          Surface(
            color = Color(0xFF424242),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
              .align(Alignment.TopEnd)
              .padding(4.dp)
          ) {
            Text(
              text = "غير متاح حالياً ⏱️",
              fontSize = 9.sp,
              color = Color.White,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        } else if (product.isAlbakalOffer || product.discountPrice != null) {
          Surface(
            color = if (product.isAlbakalOffer) Color(0xFFD32F2F) else AmberSecondary,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
              .align(Alignment.TopEnd)
              .padding(4.dp)
          ) {
            Text(
              text = if (product.isAlbakalOffer) "عروض البقال 🔥" else "عرض خاص",
              fontSize = 10.sp,
              color = Color.White,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = product.name,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        maxLines = 2,
        color = MaterialTheme.colorScheme.onSurface
      )
      Text(
        text = "التاجر: ${product.merchantName}",
        fontSize = 10.sp,
        color = GreenDark,
        maxLines = 1
      )

      Spacer(modifier = Modifier.height(6.dp))

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        val currentPrice = product.discountPrice ?: product.price
        Text(
          text = "${currentPrice} ج.م",
          fontWeight = FontWeight.Bold,
          color = GreenPrimary,
          fontSize = 14.sp
        )
        if (product.discountPrice != null) {
          Text(
            text = "${product.price} ج.م",
            color = Color.Gray,
            fontSize = 11.sp,
            textDecoration = TextDecoration.LineThrough
          )
        }
      }

      Text(
        text = "/ ${product.unit}",
        fontSize = 10.sp,
        color = Color.Gray
      )

      Spacer(modifier = Modifier.height(8.dp))

      if (isOpen) {
        Button(
          onClick = onAddToCart,
          modifier = Modifier
            .fillMaxWidth()
            .height(38.dp)
            .testTag("add_to_cart_${product.id}"),
          colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
          shape = RoundedCornerShape(8.dp),
          contentPadding = PaddingValues(0.dp)
        ) {
          Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("أضف للسلة", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
      } else {
        Button(
          onClick = {},
          enabled = false,
          modifier = Modifier
            .fillMaxWidth()
            .height(38.dp)
            .testTag("add_to_cart_disabled_${product.id}"),
          colors = ButtonDefaults.buttonColors(
            disabledContainerColor = Color(0xFFEEEEEE),
            disabledContentColor = Color(0xFF757575)
          ),
          shape = RoundedCornerShape(8.dp),
          contentPadding = PaddingValues(0.dp)
        ) {
          Text("غير متاح حالياً (المتجر مغلق)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

@Composable
private fun ProductQuantityDialog(
  product: ProductEntity,
  onConfirm: (Int) -> Unit,
  onDismiss: () -> Unit
) {
  var quantity by remember { mutableIntStateOf(1) }
  var customQuantityText by remember { mutableStateOf("1") }
  val unit = if (product.unit.isNotBlank()) product.unit else "كيلو"
  val effectivePrice = product.discountPrice ?: product.price
  val totalPrice = effectivePrice * quantity

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(text = product.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1)
          Text(text = "التاجر: ${product.merchantName}", fontSize = 11.sp, color = GreenDark)
        }
        Surface(
          color = GreenContainer,
          shape = RoundedCornerShape(8.dp)
        ) {
          Text(
            text = "$effectivePrice ج.م / $unit",
            color = GreenDark,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Text(
          text = "اختر عدد ($unit) المطلوبة بكل حرية:",
          fontWeight = FontWeight.Bold,
          fontSize = 13.sp,
          color = MaterialTheme.colorScheme.onSurface
        )

        // Presets: 1, 2, 3, 5, 10
        val presets = listOf(1, 2, 3, 5, 10)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          presets.forEach { presetVal ->
            val isSelected = quantity == presetVal
            OutlinedButton(
              onClick = {
                quantity = presetVal
                customQuantityText = presetVal.toString()
              },
              shape = RoundedCornerShape(8.dp),
              colors = ButtonDefaults.outlinedButtonColors(
                containerColor = if (isSelected) GreenPrimary else Color.Transparent,
                contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
              ),
              border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isSelected) GreenPrimary else Color.LightGray
              ),
              contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp),
              modifier = Modifier.weight(1f)
            ) {
              Text(
                text = "$presetVal",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
              )
            }
          }
        }

        // Stepper: Minus, Input, Plus
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(8.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          FilledIconButton(
            onClick = {
              if (quantity > 1) {
                quantity -= 1
                customQuantityText = quantity.toString()
              }
            },
            colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.size(44.dp)
          ) {
            Text("-", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = GreenDark)
          }

          OutlinedTextField(
            value = customQuantityText,
            onValueChange = { input ->
              val filtered = input.filter { it.isDigit() }.take(4)
              customQuantityText = filtered
              val parsed = filtered.toIntOrNull()
              if (parsed != null && parsed > 0) {
                quantity = parsed
              }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            textStyle = androidx.compose.ui.text.TextStyle(
              textAlign = TextAlign.Center,
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold
            ),
            suffix = { Text(unit, fontSize = 12.sp, color = Color.Gray) },
            modifier = Modifier.width(130.dp),
            shape = RoundedCornerShape(8.dp)
          )

          FilledIconButton(
            onClick = {
              quantity += 1
              customQuantityText = quantity.toString()
            },
            colors = IconButtonDefaults.filledIconButtonColors(containerColor = GreenPrimary),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.size(44.dp)
          ) {
            Icon(Icons.Default.Add, contentDescription = "زيادة", tint = Color.White)
          }
        }

        // Real-time Automatic Calculation Box
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = GreenContainer),
          border = androidx.compose.foundation.BorderStroke(1.dp, GreenPrimary.copy(alpha = 0.5f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "الكمية المحددة:",
                fontSize = 12.sp,
                color = GreenDark
              )
              Text(
                text = "$quantity $unit",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = GreenDark
              )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "السعر الإجمالي أوتوماتيكياً:",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = GreenDark
              )
              Text(
                text = "${String.format("%.2f", totalPrice)} ج.م",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = GreenPrimary
              )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "⚡ يتم الحساب تلقائياً: $quantity $unit × $effectivePrice ج.م",
              fontSize = 10.sp,
              color = Color.DarkGray
            )
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = { onConfirm(quantity) },
        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "إضافة $quantity $unit للسلة (${String.format("%.2f", totalPrice)} ج.م)",
          fontWeight = FontWeight.Bold
        )
      }
    },
    dismissButton = {
      TextButton(
        onClick = onDismiss,
        modifier = Modifier.fillMaxWidth()
      ) {
        Text("إلغاء")
      }
    }
  )
}

// 2. CART TAB
@Composable
private fun CartTab(viewModel: AlbakalViewModel, onOrderPlaced: () -> Unit = {}) {
  val cartItems by viewModel.cart.collectAsState()
  val settings by viewModel.appSettings.collectAsState()
  val currentUser by viewModel.currentUser.collectAsState()
  val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()

  var selectedPaymentMethod by remember { mutableStateOf("CASH_ON_DELIVERY") }
  var paymentNote by remember { mutableStateOf("") }
  var showConfirmOrderDialog by remember { mutableStateOf(false) }

  // Promo Code State
  var promoInput by remember { mutableStateOf("") }
  var appliedPromoCode by remember { mutableStateOf<String?>(null) }
  var promoDiscountAmount by remember { mutableStateOf(0.0) }
  var promoStatusMessage by remember { mutableStateOf<String?>(null) }
  var promoIsError by remember { mutableStateOf(false) }

  if (cartItems.isEmpty()) {
    Box(
      modifier = Modifier.fillMaxSize(),
      contentAlignment = Alignment.Center
    ) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
          imageVector = Icons.Default.ShoppingCart,
          contentDescription = null,
          tint = Color.LightGray,
          modifier = Modifier.size(70.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
          text = "سلة المشتريات فارغة",
          style = MaterialTheme.typography.titleMedium,
          color = Color.Gray
        )
        Text(
          text = "تصفح المتجر وأضف ما تحتاجه من خضار وفاكهة وبقالة",
          style = MaterialTheme.typography.bodySmall,
          color = Color.Gray
        )
      }
    }
    return
  }

  val allUsers by viewModel.allUsers.collectAsState()
  val subtotal = cartItems.sumOf { it.price * it.quantity }
  val firstMerchantId = cartItems.firstOrNull()?.merchantId
  val merchantUser = allUsers.firstOrNull { it.id == firstMerchantId }
  val customMerchantShipping = merchantUser?.customShippingFee
  val baseShippingFee = customMerchantShipping ?: settings.shippingFee
  val effectiveShippingFee = (baseShippingFee - promoDiscountAmount).coerceAtLeast(0.0)
  val grandTotal = subtotal + effectiveShippingFee

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    contentPadding = PaddingValues(vertical = 16.dp)
  ) {
    item {
      Text(
        text = "سلة المشتريات (${cartItems.size} أصناف)",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = GreenDark
      )
      Spacer(modifier = Modifier.height(10.dp))
    }

    items(cartItems) { item ->
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(text = item.productName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(text = "التاجر: ${item.merchantName}", fontSize = 11.sp, color = Color.Gray)
            Text(
              text = "${item.price} ج.م × ${item.quantity} = ${item.price * item.quantity} ج.م",
              fontWeight = FontWeight.Bold,
              color = GreenPrimary,
              fontSize = 12.sp
            )
          }
          IconButton(
            onClick = { viewModel.removeFromCart(item.id) },
            modifier = Modifier.testTag("remove_cart_${item.id}")
          ) {
            Icon(Icons.Default.Delete, contentDescription = "حذف", tint = Color.Red.copy(alpha = 0.7f))
          }
        }
      }
    }

    // Delivery Address Details Card
    item {
      Spacer(modifier = Modifier.height(14.dp))
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.LocationOn, contentDescription = null, tint = GreenPrimary)
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "عنوان التوصيل المسجل بحسابك:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
          }
          Spacer(modifier = Modifier.height(6.dp))
          currentUser?.let { u ->
            Text(
              text = "${u.name} - ${u.phone}\n${u.governorate} - ${u.district} - ${u.street} - عمارة/شقة ${u.apartment} - الدور ${u.floor}",
              fontSize = 12.sp,
              color = Color.DarkGray
            )
          }
        }
      }
    }

    // Payment Methods Card
    item {
      Spacer(modifier = Modifier.height(14.dp))
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(text = "اختر طريقة الدفع:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
          Spacer(modifier = Modifier.height(8.dp))

          if (settings.cashOnDeliveryEnabled) {
            PaymentOptionRow(
              title = "الدفع نقدًا عند الاستلام (كاش)",
              subtitle = "تدفع المبلغ الإجمالي للمندوب عند استلام طلبك",
              isSelected = selectedPaymentMethod == "CASH_ON_DELIVERY",
              onSelect = { selectedPaymentMethod = "CASH_ON_DELIVERY" }
            )
          }

          if (settings.vodafoneCashEnabled) {
            PaymentOptionRow(
              title = "فودافون كاش (تحويل إلكتروني)",
              subtitle = "التحويل إلى رقم المحفظة: ${settings.vodafoneCashNumber}",
              isSelected = selectedPaymentMethod == "VODAFONE_CASH",
              onSelect = { selectedPaymentMethod = "VODAFONE_CASH" }
            )
          }

          if (settings.instaPayEnabled) {
            PaymentOptionRow(
              title = "إنستا باي (InstaPay)",
              subtitle = "التحويل إلى العنوان: ${settings.instaPayHandle}",
              isSelected = selectedPaymentMethod == "INSTAPAY",
              onSelect = { selectedPaymentMethod = "INSTAPAY" }
            )
          }

          if (selectedPaymentMethod != "CASH_ON_DELIVERY") {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
              value = paymentNote,
              onValueChange = { paymentNote = it },
              label = { Text("رقم العملية أو هاتف المحول منه لإثبات الدفع") },
              singleLine = true,
              modifier = Modifier
                .fillMaxWidth()
                .testTag("payment_ref_input")
            )
          }
        }
      }
    }

    // Shipping Promo Code Card
    item {
      Spacer(modifier = Modifier.height(14.dp))
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, AmberSecondary.copy(alpha = 0.5f))
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.CreditCard, contentDescription = null, tint = AmberSecondary)
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "كود خصم مصاريف الشحن 🎁", fontWeight = FontWeight.Bold, fontSize = 13.sp)
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "أدخل كود الخصم (للاستخدام مرة واحدة فقط لكل عميل على مصاريف الشحن)",
            fontSize = 11.sp,
            color = Color.Gray
          )
          val allPromos by viewModel.promoCodes.collectAsState()
          val custId = currentUser?.id ?: ""
          val availablePromo = remember(allPromos, custId) {
            allPromos.firstOrNull { promo ->
              promo.isActive && !promo.usedCustomerIds.split(",").map { it.trim() }.contains(custId)
            }
          }

          if (availablePromo != null && appliedPromoCode == null) {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = Color(0xFFFFF3E0),
              border = BorderStroke(1.dp, AmberSecondary),
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clickable {
                  promoInput = availablePromo.code
                  coroutineScope.launch {
                    val (isValid, discount) = viewModel.validateShippingPromo(availablePromo.code, custId, baseShippingFee)
                    if (isValid) {
                      appliedPromoCode = availablePromo.code
                      promoDiscountAmount = discount
                      promoStatusMessage = "تم تطبيق كود الخصم بنجاح! خصم $discount ج.م من مصاريف الشحن 🎉"
                      promoIsError = false
                    }
                  }
                }
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text("🎁", fontSize = 16.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "كود متاح: ${availablePromo.code} (${if (availablePromo.isFreeShipping) "شحن مجاني" else "خصم ${availablePromo.discountAmount} ج.م"}) - اضغط للتطبيق الفوري",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFFE65100)
                )
              }
            }
            Spacer(modifier = Modifier.height(6.dp))
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            OutlinedTextField(
              value = promoInput,
              onValueChange = { promoInput = it.uppercase() },
              placeholder = { Text("مثال: ALBAKAL", fontSize = 12.sp) },
              singleLine = true,
              modifier = Modifier
                .weight(1f)
                .testTag("shipping_promo_input"),
              shape = RoundedCornerShape(10.dp)
            )
            Button(
              onClick = {
                coroutineScope.launch {
                  if (promoInput.isBlank()) {
                    promoStatusMessage = "يرجى كتابة كود الخصم أولاً"
                    promoIsError = true
                    return@launch
                  }
                  val custId = currentUser?.id ?: ""
                  val (isValid, discount) = viewModel.validateShippingPromo(promoInput, custId, baseShippingFee)
                  if (isValid) {
                    appliedPromoCode = promoInput.trim().uppercase()
                    promoDiscountAmount = discount
                    promoStatusMessage = "تم تطبيق كود الخصم بنجاح! خصم $discount ج.م من مصاريف الشحن 🎉"
                    promoIsError = false
                  } else if (discount == -1.0) {
                    appliedPromoCode = null
                    promoDiscountAmount = 0.0
                    promoStatusMessage = "عذراً! هذا الكود مخصص للاستخدام مرة واحدة فقط لكل عميل، وقد تم استخدامه مسبقاً بحسابك ⚠️"
                    promoIsError = true
                  } else {
                    appliedPromoCode = null
                    promoDiscountAmount = 0.0
                    promoStatusMessage = "كود الخصم غير صحيح أو غير متاح حالياً ❌"
                    promoIsError = true
                  }
                }
              },
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.buttonColors(containerColor = AmberSecondary),
              modifier = Modifier
                .height(52.dp)
                .testTag("apply_promo_btn")
            ) {
              Text("تطبيق", fontWeight = FontWeight.Bold, color = Color.White)
            }
          }

          if (promoStatusMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
              color = if (promoIsError) Color(0xFFFFEBEE) else Color(0xFFE8F5E9),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(
                text = promoStatusMessage!!,
                color = if (promoIsError) Color(0xFFC62828) else Color(0xFF2E7D32),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
              )
            }
          }
        }
      }
    }

    // Price Breakdown Summary Card
    item {
      Spacer(modifier = Modifier.height(14.dp))
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = GreenContainer.copy(alpha = 0.5f))
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(text = "إجمالي أسعار المنتجات:", fontSize = 13.sp)
            Text(text = "$subtotal ج.م", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
          }
          Spacer(modifier = Modifier.height(4.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(text = "مصاريف الشحن والتوصيل:", fontSize = 13.sp)
              if (customMerchantShipping != null) {
                Text(
                  text = "🚚 (سعر شحن مخصص لمتجر ${merchantUser?.name ?: "التاجر"})",
                  fontSize = 11.sp,
                  color = GreenPrimary,
                  fontWeight = FontWeight.SemiBold
                )
              }
            }
            Text(text = "$baseShippingFee ج.م", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
          }

          if (promoDiscountAmount > 0.0) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "خصم كود الشحن ($appliedPromoCode):",
                fontSize = 12.sp,
                color = Color(0xFF2E7D32),
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "- $promoDiscountAmount ج.م",
                fontSize = 13.sp,
                color = Color(0xFF2E7D32),
                fontWeight = FontWeight.Bold
              )
            }
          }

          HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "إجمالي المبلغ المستحق:",
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp,
              color = GreenDark
            )
            Text(
              text = "$grandTotal ج.م",
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp,
              color = GreenPrimary
            )
          }
        }
      }
    }

    // Submit Order Button
    item {
      Spacer(modifier = Modifier.height(20.dp))
      Button(
        onClick = { showConfirmOrderDialog = true },
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("confirm_order_btn"),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
      ) {
        Text("تأكيد وإرسال الطلب الآن", fontSize = 16.sp, fontWeight = FontWeight.Bold)
      }
    }
  }

  if (showConfirmOrderDialog) {
    AlertDialog(
      onDismissRequest = { showConfirmOrderDialog = false },
      title = { Text("تأكيد إتمام الطلب", fontWeight = FontWeight.Bold) },
      text = {
        Text("هل أنت متأكد من رغبتك في إرسال وتأكيد الطلب؟ سيتم تحويلك مباشرة إلى شاشة طلباتي لمتابعة حالة الشحن والتوصيل.")
      },
      confirmButton = {
        Button(
          onClick = {
            showConfirmOrderDialog = false
            viewModel.placeOrder(
              paymentMethod = selectedPaymentMethod,
              paymentProofUrl = if (paymentNote.isNotBlank()) paymentNote else null,
              customShippingFee = baseShippingFee,
              promoCode = appliedPromoCode,
              onSuccess = { onOrderPlaced() }
            )
          },
          colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
        ) {
          Text("تأكيد")
        }
      },
      dismissButton = {
        OutlinedButton(onClick = { showConfirmOrderDialog = false }) {
          Text("إلغاء (عدم تأكيد)")
        }
      }
    )
  }
}

@Composable
private fun PaymentOptionRow(
  title: String,
  subtitle: String,
  isSelected: Boolean,
  onSelect: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onSelect() }
      .padding(vertical = 6.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    RadioButton(selected = isSelected, onClick = onSelect)
    Spacer(modifier = Modifier.width(6.dp))
    Column {
      Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
      Text(text = subtitle, fontSize = 11.sp, color = Color.Gray)
    }
  }
}

// 3. MY ORDERS TAB
@Composable
private fun MyOrdersTab(viewModel: AlbakalViewModel) {
  val orders by viewModel.allOrders.collectAsState()
  val currentUser by viewModel.currentUser.collectAsState()
  val context = LocalContext.current
  var currentTimeMs by remember { mutableStateOf(System.currentTimeMillis()) }
  var orderToCancel by remember { mutableStateOf<OrderEntity?>(null) }
  var orderToConfirmDelivery by remember { mutableStateOf<String?>(null) }

  LaunchedEffect(Unit) {
    while (true) {
      delay(1000L)
      currentTimeMs = System.currentTimeMillis()
    }
  }

  val myOrders = remember(orders, currentUser) {
    orders.filter { it.customerId == currentUser?.id }
  }

  if (myOrders.isEmpty()) {
    Box(
      modifier = Modifier.fillMaxSize(),
      contentAlignment = Alignment.Center
    ) {
      Text(text = "لا توجد طلبات سابقة حتى الآن", color = Color.Gray)
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
    items(myOrders) { order ->
      val orderItemsFlow = remember(order.id) { viewModel.getOrderItems(order.id) }
      val orderItems by orderItemsFlow.collectAsState(initial = emptyList())
      val elapsedMs = currentTimeMs - order.createdAt
      val remainingSeconds = ((120_000L - elapsedMs) / 1000).coerceAtLeast(0)
      val canCancel = order.orderStatus in listOf("NEW", "PREPARING") && elapsedMs <= 120_000L

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
            Column {
              Text(text = order.orderNumber, fontWeight = FontWeight.Bold, fontSize = 14.sp)
              Text(text = "التاجر: ${order.merchantName}", fontSize = 11.sp, color = Color.Gray)
            }
            OrderStatusBadge(status = order.orderStatus)
          }

          Spacer(modifier = Modifier.height(10.dp))
          Text(
            text = "إجمالي الفاتورة المحسوبة حسب التجهيز: ${order.grandTotal} ج.م",
            fontWeight = FontWeight.Bold,
            color = GreenPrimary
          )
          Text(text = "قيمة المنتجات: ${order.totalProductsPrice} ج.م | التوصيل: ${order.shippingFee} ج.م", fontSize = 11.sp, color = Color.Gray)
          Text(text = "طريقة الدفع: ${order.paymentMethod}", fontSize = 11.sp, color = Color.DarkGray)

          if (orderItems.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
              color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(8.dp)) {
                Text(
                  text = "الأصناف المجهزة في الفاتورة (${orderItems.size}):",
                  fontWeight = FontWeight.Bold,
                  fontSize = 11.sp,
                  color = GreenDark
                )
                Spacer(modifier = Modifier.height(4.dp))
                orderItems.forEach { item ->
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Text(text = "• ${item.productName} (${item.quantity} ${item.unit})", fontSize = 11.sp)
                    Text(text = "${item.totalPrice} ج.م", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                  }
                }
              }
            }
          }

          if (order.orderStatus == "SHIPPED") {
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
              color = GreenContainer,
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(Icons.Default.LocalShipping, contentDescription = null, tint = GreenDark, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "إشعار: تم تجهيز طلبك بالأصناف أعلاه وجاري إرسال المندوب إليك. الفاتورة المعتمدة حسب التجهيز: ${order.grandTotal} ج.م",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = GreenDark
                )
              }
            }
          }

          // Delivery Captain info if status is SHIPPED
          if (order.orderStatus == "SHIPPED" && !order.captainName.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(10.dp))
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(10.dp),
              colors = CardDefaults.cardColors(containerColor = GreenContainer.copy(alpha = 0.5f))
            ) {
              Column(modifier = Modifier.padding(10.dp)) {
                Text(
                  text = "🛵 طلبك في الطريق إليك مع الكابتن:",
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp,
                  color = GreenDark
                )
                Text(text = "الاسم: ${order.captainName}", fontSize = 12.sp)
                Text(text = "الهاتف: ${order.captainPhone}", fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  Button(
                    onClick = { dialPhone(context, order.captainPhone ?: "") },
                    modifier = Modifier
                      .weight(1f)
                      .testTag("call_captain_btn_${order.id}"),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                    shape = RoundedCornerShape(8.dp)
                  ) {
                    Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("اتصال هاتفي", fontSize = 11.sp)
                  }
                  Button(
                    onClick = {
                      openWhatsApp(
                        context,
                        order.captainPhone ?: "",
                        "مرحباً كابتن ${order.captainName}، أنا بخصوص طلبي من البقال رقم ${order.orderNumber}"
                      )
                    },
                    modifier = Modifier
                      .weight(1f)
                      .testTag("whatsapp_captain_btn_${order.id}"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                    shape = RoundedCornerShape(8.dp)
                  ) {
                    Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("واتساب", fontSize = 11.sp)
                  }
                }
              }
            }
          }

          // Cancellation status or action
          if (order.orderStatus == "CANCELLED") {
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
              color = Color(0xFFFFEBEE),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(10.dp)) {
                Text(
                  text = "❌ تم إلغاء الطلب (${if (order.cancelledBy == "CUSTOMER") "من قِبلك" else "من قِبل الإدارة"})",
                  color = Color(0xFFC62828),
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold
                )
                if (!order.cancelReason.isNullOrBlank()) {
                  Text(text = "السبب: ${order.cancelReason}", fontSize = 11.sp, color = Color(0xFFB71C1C))
                }
              }
            }
          } else if (order.orderStatus in listOf("NEW", "PREPARING")) {
            if (canCancel) {
              Spacer(modifier = Modifier.height(10.dp))
              OutlinedButton(
                onClick = { orderToCancel = order },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFC62828)),
                border = BorderStroke(1.dp, Color(0xFFC62828)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("customer_cancel_btn_${order.id}")
              ) {
                Icon(Icons.Default.Cancel, contentDescription = null, tint = Color(0xFFC62828), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("إلغاء الطلب (متبقي $remainingSeconds ثانية)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
              }
            } else {
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "⏳ مضت مهلة الإلغاء (دقيقتان) - الطلب قيد التنفيذ والتجهيز من قِبل المتجر",
                fontSize = 10.sp,
                color = Color.Gray
              )
            }
          }

          // Customer delivery confirmation button
          if (order.orderStatus != "DELIVERED" && order.orderStatus != "CANCELLED" && !order.customerConfirmedDelivered) {
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedButton(
              onClick = { orderToConfirmDelivery = order.id },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("customer_confirm_delivery_${order.id}"),
              shape = RoundedCornerShape(8.dp)
            ) {
              Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GreenPrimary)
              Spacer(modifier = Modifier.width(6.dp))
              Text("تم الاستلام (تأكيد وصول الطلب)")
            }
          }
        }
      }
    }
  }

  if (orderToConfirmDelivery != null) {
    AlertDialog(
      onDismissRequest = { orderToConfirmDelivery = null },
      title = { Text("تأكيد استلام الطلب", fontWeight = FontWeight.Bold) },
      text = { Text("هل أنت متأكد من استلامك للطلب كاملاً وبحالة سليمة وتأكيد التسليم؟") },
      confirmButton = {
        Button(
          onClick = {
            orderToConfirmDelivery?.let { id ->
              viewModel.customerConfirmDelivered(id)
            }
            orderToConfirmDelivery = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
        ) {
          Text("تأكيد")
        }
      },
      dismissButton = {
        OutlinedButton(onClick = { orderToConfirmDelivery = null }) {
          Text("إلغاء (عدم تأكيد)")
        }
      }
    )
  }

  if (orderToCancel != null) {
    val remSec = ((120_000L - (currentTimeMs - (orderToCancel?.createdAt ?: 0L))) / 1000).coerceAtLeast(0)
    AlertDialog(
      onDismissRequest = { orderToCancel = null },
      title = { Text("تأكيد إلغاء الطلب", fontWeight = FontWeight.Bold) },
      text = {
        Text("هل أنت متأكد من رغبتك في إلغاء طلب رقم ${orderToCancel?.orderNumber}؟\nمتبقي من مهلة الإلغاء: $remSec ثانية.")
      },
      confirmButton = {
        Button(
          onClick = {
            orderToCancel?.let { o ->
              viewModel.cancelOrderByCustomer(o.id)
            }
            orderToCancel = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828))
        ) {
          Text("تأكيد")
        }
      },
      dismissButton = {
        OutlinedButton(onClick = { orderToCancel = null }) {
          Text("إلغاء (عدم تأكيد)")
        }
      }
    )
  }
}

// 4. PROFILE TAB
@Composable
private fun ProfileTab(viewModel: AlbakalViewModel) {
  val currentUser by viewModel.currentUser.collectAsState()
  var showLogoutConfirmDialog by remember { mutableStateOf(false) }

  currentUser?.let { user ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
    ) {
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
                .background(GreenContainer),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = GreenPrimary,
                modifier = Modifier.size(40.dp)
              )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = user.name, fontWeight = FontWeight.Bold, fontSize = 17.sp)
            Text(text = "عميل في البقال", color = Color.Gray, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(14.dp))

            ProfileField(label = "البريد الإلكتروني", value = user.email)
            ProfileField(label = "رقم الهاتف", value = user.phone)
            ProfileField(label = "المحافظة", value = user.governorate)
            ProfileField(label = "القسم / الحي", value = user.district)
            ProfileField(label = "الشارع والعمارة", value = "${user.street} - شقة ${user.apartment} - دور ${user.floor}")
          }
        }
      }

      item {
        Spacer(modifier = Modifier.height(20.dp))
        Button(
          onClick = { showLogoutConfirmDialog = true },
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("profile_logout_btn"),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
          shape = RoundedCornerShape(10.dp)
        ) {
          Text("تسجيل الخروج من الحساب", fontWeight = FontWeight.Bold)
        }
      }
    }
  }

  if (showLogoutConfirmDialog) {
    AlertDialog(
      onDismissRequest = { showLogoutConfirmDialog = false },
      title = { Text("تأكيد تسجيل الخروج", fontWeight = FontWeight.Bold) },
      text = { Text("هل أنت متأكد من رغبتك في تسجيل الخروج من حسابك؟") },
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
}

@Composable
private fun ProfileField(label: String, value: String) {
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

// 5. FINANCIALS & SUBSCRIPTION TAB
@Composable
private fun FinancialsAndSubscriptionTab(viewModel: AlbakalViewModel) {
  val settings by viewModel.appSettings.collectAsState()
  val currentUser by viewModel.currentUser.collectAsState()
  val orders by viewModel.allOrders.collectAsState()

  val myOrders = remember(orders, currentUser) {
    orders.filter { it.customerId == currentUser?.id }
  }
  val totalSpent = remember(myOrders) {
    myOrders.sumOf { it.grandTotal }
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Text(
        text = "المعاملات المالية والاشتراك الشهري",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = GreenDark
      )
    }

    // Monthly Subscription Card
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(text = "نظام الاشتراك الشهري للعملاء", fontWeight = FontWeight.Bold, fontSize = 14.sp)
          Spacer(modifier = Modifier.height(6.dp))

          if (settings.isCustomerSubscriptionActive) {
            Text(
              text = "نظام الاشتراك الشهري مفعّل من الإدارة! بقيمة ${settings.customerMonthlySubscriptionFee} ج.م شهرياً مع توصيل مجاني وغير محدود.",
              fontSize = 12.sp,
              color = GreenPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))
            Button(
              onClick = { viewModel.showMessage("تم تفعيل اشتراكك الشهري بنجاح!") },
              colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
              shape = RoundedCornerShape(8.dp)
            ) {
              Text("تفعيل / تجديد الاشتراك الشهري")
            }
          } else {
            Text(
              text = "نظام الاشتراك الشهري غير مفروض حالياً، الشراء متاح بدون أي اشتراك مع احتساب مصاريف الشحن العادية فقط.",
              fontSize = 12.sp,
              color = Color.DarkGray
            )
          }
        }
      }
    }

    // Purchasing History Summary Card
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = GreenContainer.copy(alpha = 0.5f))
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(text = "ملخص المشتريات والحساب:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
          Spacer(modifier = Modifier.height(8.dp))
          Text(text = "إجمالي عدد الطلبات المنفذة: ${myOrders.size}", fontSize = 13.sp)
          Text(text = "إجمالي المبالغ المنفقة: $totalSpent ج.م", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = GreenPrimary)
        }
      }
    }
  }
}

// 6. SUPPORT & COMPLAINTS TAB
@Composable
private fun SupportTab(viewModel: AlbakalViewModel) {
  val settings by viewModel.appSettings.collectAsState()
  val context = LocalContext.current

  var subject by remember { mutableStateOf("") }
  var details by remember { mutableStateOf("") }
  var isDamagedReport by remember { mutableStateOf(false) }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Text(
        text = "الدعم والشكاوى وميثاق الشرف",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = GreenDark
      )
    }

    // WhatsApp Direct Button
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(text = "تواصل فوري عبر واتساب:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
          Text(text = "فريق خدمة عملاء البقال جاهز للرد على استفساراتكم", fontSize = 11.sp, color = Color.Gray)
          Spacer(modifier = Modifier.height(10.dp))
          Button(
            onClick = {
              openWhatsApp(
                context,
                settings.whatsappSupportNumber,
                "مرحباً خدمة عملاء البقال، أريد الاستفسار بخصوص طلبي"
              )
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.testTag("whatsapp_support_btn")
          ) {
            Icon(Icons.Default.Chat, contentDescription = null)
            Spacer(modifier = Modifier.width(6.dp))
            Text("محادثة واتساب مباشرة (${settings.whatsappSupportNumber})")
          }
        }
      }
    }

    // Complaint / Damaged Goods Form
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(text = "إرسال شكوى أو بلاغ تلف بضائع", fontWeight = FontWeight.Bold, fontSize = 14.sp)
          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = subject,
            onValueChange = { subject = it },
            label = { Text("عنوان الشكوى أو الاقتراح") },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("complaint_subject_input")
          )
          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
            value = details,
            onValueChange = { details = it },
            label = { Text("تفاصيل الشكوى أو المنتجات التالفة...") },
            minLines = 3,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("complaint_details_input")
          )
          Spacer(modifier = Modifier.height(8.dp))

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { isDamagedReport = !isDamagedReport }
              .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            androidx.compose.material3.Checkbox(
              checked = isDamagedReport,
              onCheckedChange = { isDamagedReport = it }
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "بلاغ بضائع تالفة (ميثاق الشرف - استرجاع مجاني 100%)",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = if (isDamagedReport) AmberSecondary else Color.DarkGray
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          Button(
            onClick = {
              if (subject.isBlank() || details.isBlank()) {
                viewModel.showMessage("يرجى ملء موضوع الشكوى والتفاصيل")
              } else {
                viewModel.submitComplaint(subject, details, isDamagedReport, null)
                subject = ""
                details = ""
                isDamagedReport = false
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("submit_complaint_btn")
          ) {
            Text("إرسال الشكوى للإدارة")
          }
        }
      }
    }
  }
}
