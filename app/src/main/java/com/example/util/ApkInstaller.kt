package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

object ApkInstaller {
  /**
   * Attempts to launch the package installer for the compiled APK,
   * or share the APK file.
   */
  fun installOrShareApk(context: Context, customUrl: String? = null) {
    val downloadUrl = customUrl?.takeIf { it.isNotBlank() }
      ?: "https://github.com/albakal/Albakal.omran-Group-/raw/main/apk/albakal.apk"
    try {
      val internalApk = File(context.filesDir, "albakal.apk")
      val possibleApkPaths = listOf(
        internalApk,
        File("/app/build/outputs/apk/debug/app-debug.apk"),
        File("/apk/albakal.apk"),
        File("/albakal.apk"),
        File(context.filesDir, "app-debug.apk"),
        File(context.cacheDir, "albakal.apk")
      )

      val sourceApk = possibleApkPaths.firstOrNull { it.exists() && it.length() > 0 }
      if (sourceApk != null) {
        val targetApk = if (sourceApk != internalApk && !sourceApk.absolutePath.startsWith(context.filesDir.absolutePath)) {
          try {
            sourceApk.copyTo(internalApk, overwrite = true)
            internalApk
          } catch (_: Exception) {
            sourceApk
          }
        } else {
          sourceApk
        }

        val apkUri: Uri = FileProvider.getUriForFile(
          context,
          "${context.packageName}.fileprovider",
          targetApk
        )

        val installIntent = Intent(Intent.ACTION_VIEW).apply {
          setDataAndType(apkUri, "application/vnd.android.package-archive")
          flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        context.startActivity(installIntent)
      } else {
        // Direct download APK file link
        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl)).apply {
          flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(webIntent)
      }
    } catch (_: Exception) {
      try {
        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl)).apply {
          flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(webIntent)
      } catch (_: Exception) {}
    }
  }
}


