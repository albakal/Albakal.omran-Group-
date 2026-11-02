package com.example.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.ui.AlbakalViewModel
import com.example.ui.theme.GreenContainer
import com.example.ui.theme.GreenDark
import com.example.ui.theme.GreenPrimary

@Composable
fun SplashScreen(viewModel: AlbakalViewModel) {
  val settings by viewModel.appSettings.collectAsState()

  val logoModel = if (!settings.appLogoUrl.isNullOrBlank()) {
    settings.appLogoUrl
  } else {
    R.drawable.app_icon_albakal_1792756068308
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(
            Color.White,
            GreenContainer.copy(alpha = 0.4f),
            Color.White
          )
        )
      ),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      // Circular Framed App Logo (Uploaded logo or high-res brand fallback)
      Box(
        modifier = Modifier
          .size(120.dp)
          .clip(CircleShape)
          .background(Color.White)
          .border(3.dp, GreenPrimary, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        AsyncImage(
          model = logoModel,
          contentDescription = "شعار تطبيق البقال",
          contentScale = ContentScale.Crop,
          modifier = Modifier
            .size(120.dp)
            .clip(CircleShape)
        )
      }

      Spacer(modifier = Modifier.height(24.dp))

      Text(
        text = "البقال",
        style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
        color = GreenDark,
        fontSize = 32.sp
      )

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = "سوقك وخضارك وبقالتك لباب بيتك",
        style = MaterialTheme.typography.bodyLarge,
        color = GreenPrimary,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp
      )

      Spacer(modifier = Modifier.height(36.dp))

      CircularProgressIndicator(
        color = GreenPrimary,
        strokeWidth = 3.dp,
        modifier = Modifier.size(36.dp)
      )

      Spacer(modifier = Modifier.height(14.dp))

      Text(
        text = "جاري التحقق من الجلسة وتحميل المتجر...",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 12.sp
      )
    }
  }
}
