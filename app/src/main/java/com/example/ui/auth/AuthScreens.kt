package com.example.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.example.R
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import android.content.Intent
import android.net.Uri
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserEntity
import com.example.ui.AlbakalViewModel
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.GreenContainer
import com.example.ui.theme.GreenDark
import com.example.ui.theme.GreenPrimary
import java.util.UUID

@Composable
fun LoginScreen(viewModel: AlbakalViewModel) {
  var emailOrPhone by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  var isPasswordVisible by remember { mutableStateOf(false) }
  var showForgotPasswordDialog by remember { mutableStateOf(false) }
  val settings by viewModel.appSettings.collectAsState()

  val scrollState = rememberScrollState()

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(scrollState)
        .padding(horizontal = 24.dp, vertical = 32.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      // Albakal Logo & Header
      Box(
        modifier = Modifier
          .size(90.dp)
          .clip(CircleShape)
          .background(Color.White)
          .border(2.dp, GreenPrimary, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        val loginLogoModel = if (!settings.appLogoUrl.isNullOrBlank()) {
          settings.appLogoUrl
        } else {
          R.drawable.app_icon_albakal_1792756068308
        }
        AsyncImage(
          model = loginLogoModel,
          contentDescription = "شعار تطبيق البقال",
          contentScale = ContentScale.Crop,
          modifier = Modifier
            .size(90.dp)
            .clip(CircleShape)
        )
      }
      Spacer(modifier = Modifier.height(16.dp))
      Text(
        text = "البقال",
        style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
        color = GreenDark
      )
      Text(
        text = "سوقك وخضارك وبقالتك لباب بيتك",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      Spacer(modifier = Modifier.height(28.dp))

      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(modifier = Modifier.padding(20.dp)) {
          Text(
            text = "تسجيل الدخول",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = GreenPrimary
          )
          Text(
            text = "أدخل بيانات حسابك للمتابعة",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(20.dp))

          // Email or Phone
          OutlinedTextField(
            value = emailOrPhone,
            onValueChange = { emailOrPhone = it },
            label = { Text("البريد الإلكتروني أو رقم الهاتف") },
            leadingIcon = {
              Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = GreenPrimary)
            },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("login_input_email_or_phone"),
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(14.dp))

          // Password
          OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("كلمة المرور") },
            leadingIcon = {
              Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = GreenPrimary)
            },
            trailingIcon = {
              IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                Icon(
                  imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                  contentDescription = "تبديل الرؤية"
                )
              }
            },
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("login_input_password"),
            shape = RoundedCornerShape(12.dp)
          )

          // Forgot password link
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
          ) {
            TextButton(
              onClick = { viewModel.navigateTo("FORGOT_PASSWORD") },
              modifier = Modifier.testTag("forgot_password_btn")
            ) {
              Text(
                text = "نسيت كلمة السر؟",
                color = AmberSecondary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Login Button
          Button(
            onClick = {
              if (emailOrPhone.isBlank() || password.isBlank()) {
                viewModel.showMessage("يرجى إدخال البريد الإلكتروني أو الهاتف وكلمة المرور")
              } else {
                viewModel.login(emailOrPhone, password) {}
              }
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(50.dp)
              .testTag("login_submit_btn"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
          ) {
            Text(
              text = "تسجيل الدخول",
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Register Link
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(text = "ليس لديك حساب؟ ", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
              text = "إنشاء حساب جديد",
              color = GreenPrimary,
              fontWeight = FontWeight.Bold,
              modifier = Modifier
                .clickable { viewModel.navigateTo("REGISTER") }
                .testTag("goto_register_btn")
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))
    }
  }

  if (showForgotPasswordDialog) {
    ForgotPasswordDialog(
      viewModel = viewModel,
      onDismiss = { showForgotPasswordDialog = false }
    )
  }
}

@Composable
fun ForgotPasswordDialog(
  viewModel: AlbakalViewModel,
  onDismiss: () -> Unit
) {
  var phone by remember { mutableStateOf("") }
  var step by remember { mutableStateOf(1) } // 1: enter phone, 2: enter code & new pass
  var receivedCode by remember { mutableStateOf("") }
  var enteredCode by remember { mutableStateOf("") }
  var newPassword by remember { mutableStateOf("") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = if (step == 1) "استعادة الحساب" else "تعيين كلمة المرور الجديدة",
        fontWeight = FontWeight.Bold
      )
    },
    text = {
      Column {
        if (step == 1) {
          Text(
            text = "أدخل رقم الهاتف المسجل بحسابك لطلب رمز التحقق المؤقت:",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(bottom = 12.dp)
          )
          OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("رقم الهاتف") },
            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("forgot_phone_input")
          )
        } else {
          Text(
            text = "تم إنشاء رمز التحقق من الإدارة: $receivedCode\nيرجى كتابة الرمز وكلمة المرور الجديدة:",
            style = MaterialTheme.typography.bodySmall,
            color = GreenPrimary,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 12.dp)
          )
          OutlinedTextField(
            value = enteredCode,
            onValueChange = { enteredCode = it },
            label = { Text("رمز التحقق") },
            leadingIcon = { Icon(Icons.Default.Key, contentDescription = null) },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("forgot_code_input")
          )
          Spacer(modifier = Modifier.height(10.dp))
          OutlinedTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = { Text("كلمة المرور الجديدة") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("forgot_new_pass_input")
          )
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (step == 1) {
            if (phone.isBlank()) {
              viewModel.showMessage("يرجى إدخال رقم الهاتف")
            } else {
              viewModel.requestPasswordReset(phone) { code ->
                receivedCode = code
                step = 2
              }
            }
          } else {
            if (enteredCode != receivedCode) {
              viewModel.showMessage("رمز التحقق غير متطابق")
            } else if (newPassword.isBlank()) {
              viewModel.showMessage("يرجى إدخال كلمة المرور الجديدة")
            } else {
              viewModel.verifyAndResetPassword(phone, enteredCode, newPassword) {
                onDismiss()
              }
            }
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
        modifier = Modifier.testTag("forgot_action_btn")
      ) {
        Text(if (step == 1) "إرسال الطلب" else "حفظ كلمة المرور")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("إلغاء")
      }
    }
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(viewModel: AlbakalViewModel) {
  var selectedRole by remember { mutableStateOf("CUSTOMER") } // CUSTOMER or MERCHANT
  var name by remember { mutableStateOf("") }
  var email by remember { mutableStateOf("") }
  var phone by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  var confirmPassword by remember { mutableStateOf("") }

  // Address - Strictly populated from admin-defined areas in database/Firestore
  val areas by viewModel.governoratesWithDistricts.collectAsState()
  val distinctGovs = remember(areas) {
    areas.map { it.governorate.trim() }.filter { it.isNotBlank() }.distinct()
  }

  var selectedGov by remember(distinctGovs) {
    mutableStateOf(distinctGovs.firstOrNull() ?: "")
  }
  var govExpanded by remember { mutableStateOf(false) }

  val districtsForGov = remember(areas, selectedGov) {
    areas.filter { it.governorate.trim() == selectedGov.trim() }
      .map { it.district.trim() }
      .filter { it.isNotBlank() }
      .distinct()
  }

  var district by remember(districtsForGov) {
    mutableStateOf(districtsForGov.firstOrNull() ?: "")
  }
  var districtExpanded by remember { mutableStateOf(false) }
  var street by remember { mutableStateOf("") }
  var apartment by remember { mutableStateOf("") }
  var floor by remember { mutableStateOf("") }

  // Merchant Working Hours (Mandatory)
  var merchantHoursType by remember { mutableStateOf("ALL_DAY") }
  var hoursDropdownExpanded by remember { mutableStateOf(false) }
  var customStartHour by remember { mutableStateOf("09:00") }
  var customEndHour by remember { mutableStateOf("21:00") }
  var customStartExpanded by remember { mutableStateOf(false) }
  var customEndExpanded by remember { mutableStateOf(false) }

  val hoursPresets = remember { com.example.util.EgyptTimeHelper.getWorkingHoursPresets() }
  val scrollState = rememberScrollState()

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    // Header
    Surface(
      color = GreenPrimary,
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(
          onClick = { viewModel.navigateTo("LOGIN") },
          modifier = Modifier.testTag("register_back_btn")
        ) {
          Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "رجوع",
            tint = Color.White
          )
        }
        Text(
          text = "إنشاء حساب جديد في البقال",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = Color.White
        )
      }
    }

    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(scrollState)
        .padding(20.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Role Switcher Card
      Text(
        text = "اختر نوع الحساب:",
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.fillMaxWidth()
      )
      Spacer(modifier = Modifier.height(8.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        AccountTypeChip(
          title = "عميل (مشتري)",
          subtitle = "شراء وتوصيل للمنزل",
          icon = Icons.Default.Person,
          isSelected = selectedRole == "CUSTOMER",
          onClick = { selectedRole = "CUSTOMER" },
          modifier = Modifier
            .weight(1f)
            .testTag("role_customer_btn")
        )
        AccountTypeChip(
          title = "تاجر (محل/مورد)",
          subtitle = "عرض المنتجات والبيع",
          icon = Icons.Default.Storefront,
          isSelected = selectedRole == "MERCHANT",
          onClick = { selectedRole = "MERCHANT" },
          modifier = Modifier
            .weight(1f)
            .testTag("role_merchant_btn")
        )
      }

      Spacer(modifier = Modifier.height(20.dp))

      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Text(
            text = "البيانات الشخصية",
            fontWeight = FontWeight.Bold,
            color = GreenPrimary,
            fontSize = 15.sp
          )
          Spacer(modifier = Modifier.height(12.dp))

          OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(if (selectedRole == "MERCHANT") "اسم التاجر أو المحل" else "الاسم الكامل") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = GreenPrimary) },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("reg_name_input")
          )
          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("البريد الإلكتروني") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = GreenPrimary) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("reg_email_input")
          )
          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("رقم الهاتف (للتواصل وتأكيد الطلبات)") },
            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = GreenPrimary) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("reg_phone_input")
          )
          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("كلمة المرور") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = GreenPrimary) },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("reg_pass_input")
          )
          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("تأكيد كلمة المرور") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = GreenPrimary) },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("reg_confirm_pass_input")
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Address Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Text(
            text = "بيانات الموقع والعنوان",
            fontWeight = FontWeight.Bold,
            color = GreenPrimary,
            fontSize = 15.sp
          )
          Spacer(modifier = Modifier.height(12.dp))

          if (distinctGovs.isEmpty()) {
            Card(
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
              Text(
                text = "⚠️ لم يتم إدراج محافظات ومناطق من لوحة التحكم حتى الآن. يرجى مراجعة إدارة التطبيق.",
                color = MaterialTheme.colorScheme.onErrorContainer,
                fontSize = 12.sp,
                modifier = Modifier.padding(10.dp)
              )
            }
          } else {
            // Governorate Dropdown (ReadOnly, strictly from admin list)
            ExposedDropdownMenuBox(
              expanded = govExpanded,
              onExpandedChange = { govExpanded = it },
              modifier = Modifier.fillMaxWidth()
            ) {
              OutlinedTextField(
                value = selectedGov,
                onValueChange = {},
                readOnly = true,
                label = { Text("المحافظة * (محددة من الإدارة)") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = govExpanded) },
                modifier = Modifier
                  .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                  .fillMaxWidth()
                  .testTag("reg_gov_dropdown")
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
                      district = newDistricts.firstOrNull() ?: ""
                    }
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // District / Section Dropdown (ReadOnly, strictly from admin list)
            ExposedDropdownMenuBox(
              expanded = districtExpanded,
              onExpandedChange = { districtExpanded = it },
              modifier = Modifier.fillMaxWidth()
            ) {
              OutlinedTextField(
                value = district,
                onValueChange = {},
                readOnly = true,
                label = { Text("القسم / الحي / المنطقة * (المحددة من الإدارة)") },
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = GreenPrimary) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = districtExpanded) },
                modifier = Modifier
                  .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                  .fillMaxWidth()
                  .testTag("reg_district_dropdown")
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
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Street
          OutlinedTextField(
            value = street,
            onValueChange = { street = it },
            label = { Text("اسم الشارع") },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("reg_street_input")
          )

          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            OutlinedTextField(
              value = apartment,
              onValueChange = { apartment = it },
              label = { Text(if (selectedRole == "MERCHANT") "رقم المحل / الفرع" else "رقم الشقة/المحل") },
              modifier = Modifier
                .weight(1f)
                .testTag("reg_apartment_input")
            )
            OutlinedTextField(
              value = floor,
              onValueChange = { floor = it },
              label = { Text("رقم الدور") },
              modifier = Modifier
                .weight(1f)
                .testTag("reg_floor_input")
            )
          }
        }
      }

      // Mandatory Working Hours for Merchant
      if (selectedRole == "MERCHANT") {
        Spacer(modifier = Modifier.height(16.dp))
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Schedule, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(20.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "مواعيد عمل المتجر (إجباري)",
                fontWeight = FontWeight.Bold,
                color = GreenPrimary,
                fontSize = 15.sp
              )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "تظهر منتجاتك للعملاء فقط أثناء وقت العمل المحدد بتوقيت مصر وتختفي تلقائياً خارجه",
              fontSize = 11.sp,
              color = Color.Gray
            )
            Spacer(modifier = Modifier.height(12.dp))

            val selectedDisplayLabel = hoursPresets.firstOrNull { it.type == merchantHoursType }?.label ?: "متاح خدمة 24 ساعة (طوال اليوم)"

            ExposedDropdownMenuBox(
              expanded = hoursDropdownExpanded,
              onExpandedChange = { hoursDropdownExpanded = it },
              modifier = Modifier.fillMaxWidth()
            ) {
              OutlinedTextField(
                value = selectedDisplayLabel,
                onValueChange = {},
                readOnly = true,
                label = { Text("اختر مواعيد العمل من القائمة *") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = hoursDropdownExpanded) },
                modifier = Modifier
                  .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                  .fillMaxWidth()
                  .testTag("reg_working_hours_dropdown")
              )
              ExposedDropdownMenu(
                expanded = hoursDropdownExpanded,
                onDismissRequest = { hoursDropdownExpanded = false }
              ) {
                hoursPresets.forEach { preset ->
                  DropdownMenuItem(
                    text = {
                      Text(
                        preset.label,
                        fontSize = 13.sp,
                        fontWeight = if (merchantHoursType == preset.type) FontWeight.Bold else FontWeight.Normal
                      )
                    },
                    onClick = {
                      merchantHoursType = preset.type
                      hoursDropdownExpanded = false
                    }
                  )
                }
              }
            }

            if (merchantHoursType == "CUSTOM") {
              Spacer(modifier = Modifier.height(12.dp))
              Text(
                text = "اختر وقت البدء والانتهاء (توقيت 24 ساعة من 01:00 ص إلى 12:00 م):",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = GreenDark
              )
              Spacer(modifier = Modifier.height(8.dp))
              val time24List = remember { com.example.util.EgyptTimeHelper.get24HourOptions() }

              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                  val startLabel = time24List.firstOrNull { it.first == customStartHour }?.second ?: customStartHour
                  OutlinedTextField(
                    value = startLabel,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("من الساعة") },
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth().clickable { customStartExpanded = true }
                  )
                  DropdownMenu(
                    expanded = customStartExpanded,
                    onDismissRequest = { customStartExpanded = false }
                  ) {
                    time24List.forEach { (val24, label) ->
                      DropdownMenuItem(
                        text = { Text(label, fontSize = 12.sp) },
                        onClick = {
                          customStartHour = val24
                          customStartExpanded = false
                        }
                      )
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
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth().clickable { customEndExpanded = true }
                  )
                  DropdownMenu(
                    expanded = customEndExpanded,
                    onDismissRequest = { customEndExpanded = false }
                  ) {
                    time24List.forEach { (val24, label) ->
                      DropdownMenuItem(
                        text = { Text(label, fontSize = 12.sp) },
                        onClick = {
                          customEndHour = val24
                          customEndExpanded = false
                        }
                      )
                    }
                  }
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      Button(
        onClick = {
          if (name.isBlank() || email.isBlank() || phone.isBlank() || password.isBlank()) {
            viewModel.showMessage("يرجى إكمال جميع الحقول الأساسية")
          } else if (password != confirmPassword) {
            viewModel.showMessage("كلمة المرور وتأكيدها غير متطابقين")
          } else if (selectedGov.isBlank() || district.isBlank() || !distinctGovs.contains(selectedGov) || !districtsForGov.contains(district)) {
            viewModel.showMessage("يرجى اختيار المحافظة والمنطقة حصرياً من القوائم المعتمدة من الإدارة")
          } else {
            val (startH, endH, displayLabel) = if (selectedRole == "MERCHANT") {
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

            val cleanPhone = com.example.util.PhoneHelper.cleanPhone(phone).ifBlank { phone.trim() }
            val newUser = UserEntity(
              id = "usr_" + UUID.randomUUID().toString().take(8),
              name = name.trim(),
              email = email.trim(),
              phone = cleanPhone,
              password = password.trim(),
              role = selectedRole,
              status = if (selectedRole == "CUSTOMER") "APPROVED" else "PENDING_APPROVAL", // Customers active immediately, merchants reviewed
              governorate = selectedGov,
              district = district.trim(),
              street = street.trim(),
              apartment = apartment.trim(),
              floor = floor.trim(),
              honorCodeAccepted = false,
              workingHoursType = merchantHoursType,
              workStartHour = startH,
              workEndHour = endH,
              workingHoursDisplay = displayLabel
            )
            viewModel.registerUser(newUser) {
              viewModel.navigateTo("LOGIN")
            }
          }
        },
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("register_submit_btn"),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
      ) {
        Text(
          text = "إرسال طلب إنشاء الحساب",
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold,
          color = Color.White
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      TextButton(
        onClick = { viewModel.navigateTo("LOGIN") },
        modifier = Modifier.testTag("cancel_register_btn")
      ) {
        Text("العودة إلى تسجيل الدخول", color = MaterialTheme.colorScheme.onSurfaceVariant)
      }
    }
  }
}

@Composable
private fun AccountTypeChip(
  title: String,
  subtitle: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier.clickable { onClick() },
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isSelected) GreenContainer else MaterialTheme.colorScheme.surface
    ),
    border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, GreenPrimary) else null
  ) {
    Column(
      modifier = Modifier.padding(12.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = if (isSelected) GreenPrimary else Color.Gray,
        modifier = Modifier.size(28.dp)
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = title,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        color = if (isSelected) GreenDark else MaterialTheme.colorScheme.onSurface
      )
      Text(
        text = subtitle,
        fontSize = 11.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center
      )
    }
  }
}

@Composable
fun ForgotPasswordScreen(viewModel: AlbakalViewModel) {
  val context = LocalContext.current
  val allUsers by viewModel.allUsers.collectAsState()
  val settings by viewModel.appSettings.collectAsState()
  var phoneInput by remember { mutableStateOf("") }
  var errorMessage by remember { mutableStateOf<String?>(null) }
  val scrollState = rememberScrollState()

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(scrollState)
        .padding(horizontal = 24.dp, vertical = 28.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Top Bar with Back Arrow
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(
          onClick = { viewModel.navigateTo("LOGIN") },
          modifier = Modifier.testTag("forgot_pass_back_btn")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "العودة لتسجيل الدخول",
            tint = GreenDark
          )
        }
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "استعادة كلمة المرور",
          fontWeight = FontWeight.Bold,
          fontSize = 18.sp,
          color = GreenDark
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Logo
      Box(
        modifier = Modifier
          .size(85.dp)
          .clip(CircleShape)
          .background(Color.White)
          .border(2.dp, GreenPrimary, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        val logoModel = if (!settings.appLogoUrl.isNullOrBlank()) {
          settings.appLogoUrl
        } else {
          R.drawable.app_icon_albakal_1792756068308
        }
        AsyncImage(
          model = logoModel,
          contentDescription = "شعار تطبيق البقال",
          contentScale = ContentScale.Crop,
          modifier = Modifier
            .size(85.dp)
            .clip(CircleShape)
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      Text(
        text = "البقال",
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
        color = GreenDark
      )

      Spacer(modifier = Modifier.height(20.dp))

      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(modifier = Modifier.padding(20.dp)) {
          Text(
            text = "إرسال كلمة المرور عبر واتساب",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = GreenPrimary
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "أدخل رقم الهاتف المسجل بحسابك. سيتم إرسال الرقم السري وكلمة المرور مباشرة إلى تطبيق واتساب دون إظهارها على الشاشة.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 18.sp
          )

          Spacer(modifier = Modifier.height(18.dp))

          // Phone Number Input with Fixed +2 Prefix
          OutlinedTextField(
            value = phoneInput,
            onValueChange = {
              phoneInput = it.filter { ch -> ch.isDigit() }
              errorMessage = null
            },
            label = { Text("رقم الهاتف المسجل") },
            prefix = {
              Text(
                text = "+2 ",
                fontWeight = FontWeight.Bold,
                color = GreenPrimary,
                fontSize = 15.sp
              )
            },
            leadingIcon = {
              Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = GreenPrimary)
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("forgot_pass_phone_input"),
            shape = RoundedCornerShape(12.dp)
          )

          if (errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = errorMessage!!,
              color = Color.Red,
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold
            )
          }

          Spacer(modifier = Modifier.height(22.dp))

          Button(
            onClick = {
              val raw = phoneInput.trim().removePrefix("+20").removePrefix("+2").removePrefix("0")
              if (raw.length < 8) {
                errorMessage = "يرجى كتابة رقم هاتف مصري صحيح"
                return@Button
              }
              val foundUser = allUsers.firstOrNull {
                val cleanUserPhone = it.phone.replace("+20", "").replace("+2", "").trimStart('0')
                cleanUserPhone == raw
              }
              if (foundUser == null) {
                errorMessage = "رقم الهاتف غير مسجل في أي حساب بتطبيق البقال"
              } else {
                errorMessage = null
                val fullIntlPhone = "20$raw"
                val whatsappMsg = "مرحباً بك في تطبيق البقال 🛒\nبيانات دخولك المسجلة هي:\nرقم الهاتف: 0$raw\nكلمة المرور: ${foundUser.password}\nنتمنى لك تجربة تسوق ممتعة!"
                val encodedUrl = "https://api.whatsapp.com/send?phone=$fullIntlPhone&text=${Uri.encode(whatsappMsg)}"
                val intent = Intent(Intent.ACTION_VIEW).apply {
                  data = Uri.parse(encodedUrl)
                  flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                try {
                  context.startActivity(intent)
                } catch (e: Exception) {
                  // Fallback
                }
                viewModel.showMessage("تم إرسال الرقم السري إلى واتساب")
                viewModel.navigateTo("LOGIN")
              }
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(50.dp)
              .testTag("send_whatsapp_pass_btn"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
          ) {
            Icon(Icons.Default.Phone, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "إرسال الرقم السري إلى واتساب",
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          OutlinedButton(
            onClick = { viewModel.navigateTo("LOGIN") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
          ) {
            Text("العودة إلى شاشة تسجيل الدخول")
          }
        }
      }
    }
  }
}

