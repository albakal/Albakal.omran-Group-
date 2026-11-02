package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class OrderAlertForegroundService : Service() {
  private var wakeLock: PowerManager.WakeLock? = null
  private var ringtone: Ringtone? = null
  private var sirenJob: Job? = null
  private var vibrator: Vibrator? = null
  private var isKeepAliveActive: Boolean = false

  companion object {
    const val CHANNEL_ALERT_ID = "albakal_merchant_orders_channel"
    const val CHANNEL_PERSISTENT_ID = "albakal_persistent_bg_channel"
    const val NOTIFICATION_ID = 9901
    const val ACTION_START_ALERT = "com.example.albakal.START_ALERT"
    const val ACTION_STOP_ALERT = "com.example.albakal.STOP_ALERT"
    const val ACTION_KEEP_ALIVE = "com.example.albakal.KEEP_ALIVE"
    const val ACTION_STOP_KEEP_ALIVE = "com.example.albakal.STOP_KEEP_ALIVE"
    const val EXTRA_ORDER_NUMBER = "order_number"
    const val EXTRA_CUSTOMER_NAME = "customer_name"
    const val EXTRA_USER_TITLE = "user_title"

    fun startKeepAlive(context: Context, userTitle: String = "متصل") {
      val intent = Intent(context, OrderAlertForegroundService::class.java).apply {
        action = ACTION_KEEP_ALIVE
        putExtra(EXTRA_USER_TITLE, userTitle)
      }
      try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          context.startForegroundService(intent)
        } else {
          context.startService(intent)
        }
      } catch (_: Exception) {}
    }

    fun stopKeepAlive(context: Context) {
      val intent = Intent(context, OrderAlertForegroundService::class.java).apply {
        action = ACTION_STOP_KEEP_ALIVE
      }
      try {
        context.startService(intent)
      } catch (_: Exception) {}
    }

    fun startService(context: Context, orderNumber: String, customerName: String) {
      val intent = Intent(context, OrderAlertForegroundService::class.java).apply {
        action = ACTION_START_ALERT
        putExtra(EXTRA_ORDER_NUMBER, orderNumber)
        putExtra(EXTRA_CUSTOMER_NAME, customerName)
      }
      try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          context.startForegroundService(intent)
        } else {
          context.startService(intent)
        }
      } catch (_: Exception) {}
    }

    fun stopService(context: Context) {
      val intent = Intent(context, OrderAlertForegroundService::class.java).apply {
        action = ACTION_STOP_ALERT
      }
      try {
        context.startService(intent)
      } catch (_: Exception) {}
    }
  }

  override fun onBind(intent: Intent?): IBinder? = null

  override fun onCreate() {
    super.onCreate()
    createNotificationChannels()
    vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      val vm = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
      vm?.defaultVibrator
    } else {
      @Suppress("DEPRECATION")
      getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    when (intent?.action) {
      ACTION_KEEP_ALIVE -> {
        isKeepAliveActive = true
        val userTitle = intent.getStringExtra(EXTRA_USER_TITLE) ?: "متصل"
        showKeepAliveNotification(userTitle)
        return START_STICKY
      }
      ACTION_START_ALERT -> {
        val orderNum = intent.getStringExtra(EXTRA_ORDER_NUMBER) ?: "طلب جديد"
        val customer = intent.getStringExtra(EXTRA_CUSTOMER_NAME) ?: "عميل"
        startForegroundAlert(orderNum, customer)
        return START_STICKY
      }
      ACTION_STOP_ALERT -> {
        stopSirenAndWakeLock()
        if (isKeepAliveActive) {
          showKeepAliveNotification("متصل")
        } else {
          stopForegroundNotificationAndSelf()
        }
      }
      ACTION_STOP_KEEP_ALIVE -> {
        isKeepAliveActive = false
        stopSirenAndWakeLock()
        stopForegroundNotificationAndSelf()
      }
      else -> {
        if (!isKeepAliveActive) {
          stopForegroundNotificationAndSelf()
        }
      }
    }
    return START_STICKY
  }

  private fun showKeepAliveNotification(userTitle: String) {
    val openAppIntent = Intent(this, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
    }
    val pendingIntent = PendingIntent.getActivity(
      this,
      0,
      openAppIntent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val notification: Notification = NotificationCompat.Builder(this, CHANNEL_PERSISTENT_ID)
      .setSmallIcon(R.mipmap.ic_launcher)
      .setContentTitle("تطبيق البقال قيد التشغيل")
      .setContentText("متصل في الخلفية ($userTitle) لمتابعة الطلبات وتحديثات الأسعار فوراً")
      .setPriority(NotificationCompat.PRIORITY_LOW)
      .setCategory(NotificationCompat.CATEGORY_SERVICE)
      .setContentIntent(pendingIntent)
      .setAutoCancel(false)
      .setOngoing(true)
      .build()

    startForeground(NOTIFICATION_ID, notification)
  }

  private fun startForegroundAlert(orderNumber: String, customerName: String) {
    try {
      val pm = getSystemService(Context.POWER_SERVICE) as? PowerManager
      wakeLock?.release()
      wakeLock = pm?.newWakeLock(
        PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.ON_AFTER_RELEASE,
        "Albakal:OrderAlertWakeLock"
      )?.apply {
        acquire(3 * 60 * 1000L) // Keep awake for max 3 minutes
      }
    } catch (_: Exception) {}

    val openAppIntent = Intent(this, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
    }
    val pendingIntent = PendingIntent.getActivity(
      this,
      1,
      openAppIntent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ALERT_ID)
      .setSmallIcon(R.mipmap.ic_launcher)
      .setContentTitle("🚨 وصلك طلب جديد: $orderNumber")
      .setContentText("العميل $customerName بانتظار تجهيز الطلب. اضغط هنا فوراً.")
      .setPriority(NotificationCompat.PRIORITY_MAX)
      .setCategory(NotificationCompat.CATEGORY_ALARM)
      .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
      .setFullScreenIntent(pendingIntent, true)
      .setContentIntent(pendingIntent)
      .setAutoCancel(false)
      .setOngoing(true)
      .build()

    startForeground(NOTIFICATION_ID, notification)
    startSirenAudio()
  }

  private fun startSirenAudio() {
    sirenJob?.cancel()
    sirenJob = CoroutineScope(Dispatchers.Default).launch {
      try {
        val alertUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
          ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        ringtone = RingtoneManager.getRingtone(applicationContext, alertUri)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
          ringtone?.audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ALARM)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        }
        ringtone?.play()
      } catch (_: Exception) {}

      while (isActive) {
        try {
          if (ringtone?.isPlaying != true) {
            ringtone?.play()
          }
          vibrateDevice()
        } catch (_: Exception) {}
        delay(1200)
      }
    }
  }

  private fun vibrateDevice() {
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator?.vibrate(VibrationEffect.createOneShot(400, VibrationEffect.DEFAULT_AMPLITUDE))
      } else {
        @Suppress("DEPRECATION")
        vibrator?.vibrate(400)
      }
    } catch (_: Exception) {}
  }

  private fun stopSirenAndWakeLock() {
    sirenJob?.cancel()
    sirenJob = null
    try {
      ringtone?.stop()
      ringtone = null
    } catch (_: Exception) {}

    try {
      if (wakeLock?.isHeld == true) {
        wakeLock?.release()
      }
      wakeLock = null
    } catch (_: Exception) {}
  }

  private fun stopForegroundNotificationAndSelf() {
    stopSirenAndWakeLock()
    @Suppress("DEPRECATION")
    stopForeground(true)
    stopSelf()
  }

  override fun onDestroy() {
    stopForegroundNotificationAndSelf()
    super.onDestroy()
  }

  private fun createNotificationChannels() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val alertChannel = NotificationChannel(
        CHANNEL_ALERT_ID,
        "تنبيهات طلبات البقال العاجلة",
        NotificationManager.IMPORTANCE_HIGH
      ).apply {
        description = "إنذار صوتي فوري وتنبيه بقدوم طلبات جديدة للتاجر"
        enableLights(true)
        enableVibration(true)
        lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        setBypassDnd(true)
      }

      val persistentChannel = NotificationChannel(
        CHANNEL_PERSISTENT_ID,
        "تشغيل البقال في الخلفية",
        NotificationManager.IMPORTANCE_LOW
      ).apply {
        description = "إشعار استمرار اتصال التطبيق في الخلفية لمتابعة حالة الطلبات والأسعار"
        enableLights(false)
        enableVibration(false)
        setShowBadge(false)
      }

      val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      nm.createNotificationChannel(alertChannel)
      nm.createNotificationChannel(persistentChannel)
    }
  }
}
