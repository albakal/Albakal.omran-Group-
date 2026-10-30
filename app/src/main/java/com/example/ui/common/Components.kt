package com.example.ui.common

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.data.model.GovernorateAreaEntity
import com.example.data.model.UserEntity
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.GreenContainer
import com.example.ui.theme.GreenDark
import com.example.ui.theme.GreenPrimary
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.data.model.AnnouncementEntity
import com.example.ui.AlbakalViewModel
import com.example.util.ApkInstaller
import com.example.R
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbakalTopBar(
  title: String,
  subtitle: String? = null,
  appLogoUrl: String? = null,
  showBackButton: Boolean = false,
  onBackClick: (() -> Unit)? = null,
  showLogoutButton: Boolean = true,
  onLogoutClick: (() -> Unit)? = null,
  actions: @Composable (() -> Unit)? = null
) {
  var showLogoutConfirmDialog by remember { mutableStateOf(false) }

  TopAppBar(
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Color.White),
          contentAlignment = Alignment.Center
        ) {
          val logoModel = if (!appLogoUrl.isNullOrBlank()) {
            appLogoUrl
          } else {
            R.drawable.app_icon_albakal_1792756068308
          }
          AsyncImage(
            model = logoModel,
            contentDescription = "شعار تطبيق البقال",
            contentScale = ContentScale.Crop,
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
          )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White
          )
          if (subtitle != null) {
            Text(
              text = subtitle,
              style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
              color = Color.White.copy(alpha = 0.85f)
            )
          }
        }
      }
    },
    navigationIcon = {
      if (showBackButton && onBackClick != null) {
        IconButton(
          onClick = onBackClick,
          modifier = Modifier.testTag("topbar_back_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "رجوع",
            tint = Color.White
          )
        }
      }
    },
    actions = {
      val context = androidx.compose.ui.platform.LocalContext.current
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = AmberSecondary,
        modifier = Modifier
          .padding(end = 6.dp)
          .clickable { com.example.util.ApkInstaller.installOrShareApk(context) }
          .testTag("topbar_apk_button")
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "APK",
            tint = Color.White,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(3.dp))
          Text(
            text = "تثبيت APK",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
          )
        }
      }
      if (actions != null) {
        actions()
      }
      if (showLogoutButton && onLogoutClick != null) {
        IconButton(
          onClick = { showLogoutConfirmDialog = true },
          modifier = Modifier.testTag("topbar_logout_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.Logout,
            contentDescription = "تسجيل الخروج",
            tint = Color.White
          )
        }
      }
    },
    colors = TopAppBarDefaults.topAppBarColors(
      containerColor = GreenPrimary
    )
  )

  if (showLogoutConfirmDialog) {
    AlertDialog(
      onDismissRequest = { showLogoutConfirmDialog = false },
      title = { Text("تأكيد تسجيل الخروج", fontWeight = FontWeight.Bold) },
      text = { Text("هل أنت متأكد من رغبتك في تسجيل الخروج من الحساب؟") },
      confirmButton = {
        Button(
          onClick = {
            showLogoutConfirmDialog = false
            onLogoutClick?.invoke()
          },
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
        ) {
          Text("تأكيد الخروج")
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
fun OrderStatusBadge(status: String) {
  val (bgColor, textColor, label) = when (status) {
    "NEW" -> Triple(Color(0xFFE3F2FD), Color(0xFF1565C0), "طلب جديد 🔔")
    "PREPARING" -> Triple(Color(0xFFFFF3E0), Color(0xFFE65100), "جاري تحضير المنتج 📦")
    "SHIPPED" -> Triple(Color(0xFFE8F5E9), Color(0xFF2E7D32), "جاري إرسال المنتج 🛵")
    "DELIVERED" -> Triple(Color(0xFFE0F2F1), Color(0xFF00695C), "تم التسليم بنجاح ✅")
    "CANCELLED" -> Triple(Color(0xFFFFEBEE), Color(0xFFC62828), "ملغي ❌")
    else -> Triple(Color(0xFFF5F5F5), Color.DarkGray, status)
  }

  Surface(
    shape = RoundedCornerShape(16.dp),
    color = bgColor,
    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
  ) {
    Text(
      text = label,
      color = textColor,
      fontWeight = FontWeight.Bold,
      fontSize = 12.sp,
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
    )
  }
}

@Composable
fun MerchantHonorCodeDialog(
  onAccept: () -> Unit,
  onDismiss: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    icon = {
      Icon(
        imageVector = Icons.Default.CheckCircle,
        contentDescription = "الشروط والأحكام وميثاق الشرف",
        tint = GreenPrimary,
        modifier = Modifier.size(44.dp)
      )
    },
    title = {
      Text(
        text = "الشروط والأحكام وميثاق الشرف للتاجر",
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        fontSize = 16.sp,
        modifier = Modifier.fillMaxWidth()
      )
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
          .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Card(
          colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
          shape = RoundedCornerShape(12.dp)
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
          ) {
            Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFC62828), modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "أولاً: لا يمكن إرسال بضاعة تالفة أو معيوبة نهائياً، وفي حالة إرسال أي بضاعة تالفة أو معيوبة من حق العميل رفض الاستلام فوراً دون دفع أي مصاريف أو رسوم شحن.",
              fontWeight = FontWeight.Bold,
              color = Color(0xFFB71C1C),
              fontSize = 13.sp,
              lineHeight = 18.sp
            )
          }
        }

        HonorItem(
          icon = Icons.Default.CheckCircle,
          text = "ثانياً: البضائع والمنتجات المعروضة يجب أن تكون سليمة بنسبة 100% وطازجة ومطابقة للكميات والأسعار المحددة."
        )

        HonorItem(
          icon = Icons.Default.Info,
          text = "ثالثاً: عند استلام العميل لأي منتج به عيب أو تلف، يتم استرجاعه فوراً وتتحمل إدارة المتجر المخالف كامل المسؤولية."
        )

        HonorItem(
          icon = Icons.Default.Warning,
          text = "رابعاً: تقوم الإدارة بمتابعة بلاغات الجودة بدقة متناهية، وتكرار إرسال سلع غير مطابقة يعرض حساب التاجر للحظر النهائي من المنصة."
        )
      }
    },
    confirmButton = {
      Button(
        onClick = onAccept,
        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth().testTag("accept_honor_code_btn")
      ) {
        Text("أوافق على الشروط والأحكام وميثاق الشرف", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      OutlinedButton(
        onClick = onDismiss,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Text("إلغاء (عدم تأكيد)")
      }
    }
  )
}


@Composable
private fun HonorItem(icon: ImageVector, text: String) {
  Row(verticalAlignment = Alignment.Top) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = AmberSecondary,
      modifier = Modifier
        .size(18.dp)
        .padding(top = 2.dp)
    )
    Spacer(modifier = Modifier.width(8.dp))
    Text(
      text = text,
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurface
    )
  }
}

// Intent utilities for WhatsApp and Direct Calling
fun openWhatsApp(context: Context, rawPhone: String, message: String = "") {
  try {
    val cleanPhone = rawPhone.replace("+", "").replace(" ", "").replace("-", "")
    val url = "https://api.whatsapp.com/send?phone=$cleanPhone&text=${Uri.encode(message)}"
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    context.startActivity(intent)
  } catch (_: Exception) {
    // Fallback dial
    dialPhone(context, rawPhone)
  }
}

fun dialPhone(context: Context, phone: String) {
  try {
    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone")).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    context.startActivity(intent)
  } catch (_: Exception) {}
}

@Composable
fun AnnouncementsAndUpdatesView(
  viewModel: AlbakalViewModel,
  userRole: String, // "CUSTOMER" or "MERCHANT"
  modifier: Modifier = Modifier
) {
  val context = androidx.compose.ui.platform.LocalContext.current
  val announcements by viewModel.allAnnouncements.collectAsState()
  val appSettings by viewModel.appSettings.collectAsState()

  val relevantAnnouncements = remember(announcements, userRole) {
    announcements.filter { it.targetAudience == "ALL" || it.targetAudience == userRole }
  }

  val activeDownloadUrl = appSettings?.apkDownloadUrl?.takeIf { it.isNotBlank() }
    ?: "https://github.com/albakal/Albakal.omran-Group-/raw/main/apk/albakal.apk"

  val dateFormat = remember {
    java.text.SimpleDateFormat("yyyy/MM/dd hh:mm a", java.util.Locale.getDefault())
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp, vertical = 12.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Top App Update Banner
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = GreenContainer),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(44.dp)
                .background(GreenPrimary, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = Color.White
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "مركز التحديثات وتنزيل APK",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = GreenDark
              )
              Text(
                text = "الإصدار المحدث متاح للتنزيل المباشر",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = "قم بتحميل وتثبيت أحدث نسخة من تطبيق البقال لتستمتع بكافة الميزات والتحديثات الجديدة.",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurface
          )

          Spacer(modifier = Modifier.height(14.dp))
          Button(
            onClick = {
              ApkInstaller.installOrShareApk(context, activeDownloadUrl)
            },
            colors = ButtonDefaults.buttonColors(containerColor = AmberSecondary),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("تنزيل وتثبيت آخر تحديث (APK)", fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    // Header for Admin Announcements
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = Icons.Default.CheckCircle,
          contentDescription = null,
          tint = GreenPrimary,
          modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "التنبيهات والإشعارات الإدارية",
          fontWeight = FontWeight.Bold,
          fontSize = 16.sp,
          color = MaterialTheme.colorScheme.onBackground
        )
      }
    }

    if (relevantAnnouncements.isEmpty()) {
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = null,
              tint = GreenPrimary,
              modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = "لا توجد تنبيهات جديدة في الوقت الحالي",
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp,
              color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "أنت مطلع على جميع التحديثات والإشعارات الخاصة بالبقال.",
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              textAlign = TextAlign.Center
            )
          }
        }
      }
    } else {
      items(relevantAnnouncements, key = { it.id }) { ann ->
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = GreenContainer
              ) {
                Text(
                  text = "إشعار إداري",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = GreenDark,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
              Text(
                text = dateFormat.format(java.util.Date(ann.createdAt)),
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = ann.title,
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp,
              color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = ann.message,
              fontSize = 13.sp,
              color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
              lineHeight = 20.sp
            )

            if (!ann.apkDownloadUrl.isNullOrBlank()) {
              Spacer(modifier = Modifier.height(12.dp))
              OutlinedButton(
                onClick = {
                  ApkInstaller.installOrShareApk(context, ann.apkDownloadUrl)
                },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
              ) {
                Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("تنزيل التحديث المرفق مع الإشعار", fontSize = 12.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MandatoryAreaSelectionDialog(
  user: UserEntity,
  areas: List<GovernorateAreaEntity>,
  onSave: (governorate: String, district: String) -> Unit
) {
  val distinctGovs = remember(areas) {
    areas.map { it.governorate.trim() }.filter { it.isNotBlank() }.distinct()
  }

  var selectedGov by remember(distinctGovs, user) {
    val initial = if (distinctGovs.contains(user.governorate.trim())) user.governorate.trim() else distinctGovs.firstOrNull() ?: ""
    mutableStateOf(initial)
  }
  var govExpanded by remember { mutableStateOf(false) }

  val districtsForGov = remember(areas, selectedGov) {
    areas.filter { it.governorate.trim() == selectedGov.trim() }
      .map { it.district.trim() }
      .filter { it.isNotBlank() }
      .distinct()
  }

  var selectedDist by remember(districtsForGov, user) {
    val initial = if (districtsForGov.contains(user.district.trim())) user.district.trim() else districtsForGov.firstOrNull() ?: ""
    mutableStateOf(initial)
  }
  var distExpanded by remember { mutableStateOf(false) }

  androidx.compose.ui.window.Dialog(
    onDismissRequest = { /* unskippable / mandatory */ },
    properties = androidx.compose.ui.window.DialogProperties(
      dismissOnBackPress = false,
      dismissOnClickOutside = false
    )
  ) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp)
        .testTag("mandatory_area_dialog"),
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
      Column(
        modifier = Modifier.padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Surface(
          shape = CircleShape,
          color = GreenPrimary.copy(alpha = 0.15f),
          modifier = Modifier.size(56.dp)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(Icons.Default.LocationOn, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(32.dp))
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = "تحديد المحافظة والمنطقة (إلزامي)",
          fontWeight = FontWeight.Bold,
          fontSize = 16.sp,
          color = GreenDark,
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = "وفقاً لتطبيق شرط المركزية في تطبيق البقال، يلزم اختيار محافظتك ومنطقتك من القوائم المعتمدة من الإدارة لتتمكن من متابعة الاستخدام والتسوق.",
          fontSize = 12.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          textAlign = TextAlign.Center,
          lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(18.dp))

        if (distinctGovs.isEmpty()) {
          Text(
            text = "⚠️ لم يتم تفعيل أو إدراج محافظات ومناطق من لوحة التحكم حتى الآن. يرجى مراجعة إدارة التطبيق.",
            color = MaterialTheme.colorScheme.error,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
          )
        } else {
          // Governorate Dropdown (ReadOnly, only from admin list)
          ExposedDropdownMenuBox(
            expanded = govExpanded,
            onExpandedChange = { govExpanded = it },
            modifier = Modifier.fillMaxWidth()
          ) {
            OutlinedTextField(
              value = selectedGov,
              onValueChange = {},
              readOnly = true,
              label = { Text("المحافظة") },
              trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = govExpanded) },
              modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                .fillMaxWidth()
                .testTag("mandatory_gov_dropdown")
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
                    val newDistricts = areas.filter { it.governorate.trim() == gov.trim() }
                      .map { it.district.trim() }
                      .filter { it.isNotBlank() }
                      .distinct()
                    selectedDist = newDistricts.firstOrNull() ?: ""
                  }
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // District Dropdown (ReadOnly, only from admin list for selected gov)
          ExposedDropdownMenuBox(
            expanded = distExpanded,
            onExpandedChange = { distExpanded = it },
            modifier = Modifier.fillMaxWidth()
          ) {
            OutlinedTextField(
              value = selectedDist,
              onValueChange = {},
              readOnly = true,
              label = { Text("القسم / الحي / المنطقة") },
              trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = distExpanded) },
              modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                .fillMaxWidth()
                .testTag("mandatory_district_dropdown")
            )
            ExposedDropdownMenu(
              expanded = distExpanded,
              onDismissRequest = { distExpanded = false }
            ) {
              districtsForGov.forEach { distItem ->
                DropdownMenuItem(
                  text = { Text(distItem) },
                  onClick = {
                    selectedDist = distItem
                    distExpanded = false
                  }
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(20.dp))

          Button(
            onClick = {
              if (selectedGov.isNotBlank() && selectedDist.isNotBlank()) {
                onSave(selectedGov, selectedDist)
              }
            },
            enabled = selectedGov.isNotBlank() && selectedDist.isNotBlank(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("save_mandatory_area_btn")
          ) {
            Text("حفظ وتأكيد المنطقة", fontWeight = FontWeight.Bold, fontSize = 14.sp)
          }
        }
      }
    }
  }
}


