package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
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

  companion object {
    const val CHANNEL_ID = "albakal_merchant_orders_channel"
    const val NOTIFICATION_ID = 9901
    const val ACTION_START_ALERT = "com.example.albakal.START_ALERT"
    const val ACTION_STOP_ALERT = "com.example.albakal.STOP_ALERT"
    const val EXTRA_ORDER_NUMBER = "order_number"
    const val EXTRA_CUSTOMER_NAME = "customer_name"

    fun startService(context: Context, orderNumber: String, customerName: String) {
      val intent = Intent(context, OrderAlertForegroundService::class.java).apply {
        action = ACTION_START_ALERT
        putExtra(EXTRA_ORDER_NUMBER, orderNumber)
        putExtra(EXTRA_CUSTOMER_NAME, customerName)
      }
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        context.startForegroundService(intent)
      } else {
        context.startService(intent)
      }
    }

    fun stopService(context: Context) {
      val intent = Intent(context, OrderAlertForegroundService::class.java).apply {
        action = ACTION_STOP_ALERT
      }
      context.startService(intent)
    }
  }

  override fun onBind(intent: Intent?): IBinder? = null

  override fun onCreate() {
    super.onCreate()
    createNotificationChannel()
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
      ACTION_START_ALERT -> {
        val orderNum = intent.getStringExtra(EXTRA_ORDER_NUMBER) ?: "طلب جديد"
        val customer = intent.getStringExtra(EXTRA_CUSTOMER_NAME) ?: "عميل"
        startForegroundAlert(orderNum, customer)
      }
      ACTION_STOP_ALERT -> {
        stopAlertAndSelf()
      }
      else -> {
        stopAlertAndSelf()
      }
    }
    return START_NOT_STICKY
  }

  private fun startForegroundAlert(orderNumber: String, customerName: String) {
    // 1. Acquire WakeLock to wake the phone screen from sleep/idle/doze mode
    try {
      val pm = getSystemService(Context.POWER_SERVICE) as? PowerManager
      wakeLock?.release()
      @Suppress("DEPRECATION")
      wakeLock = pm?.newWakeLock(
        PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.ON_AFTER_RELEASE,
        "albakal:merchant_wake_lock"
      )
      wakeLock?.acquire(10 * 60 * 1000L) // 10 minutes max wake
    } catch (_: Exception) {}

    // 2. Full-Screen Intent to immediately bring app / order popup to front
    val openIntent = Intent(this, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_SINGLE_TOP
    }
    val pendingIntent = PendingIntent.getActivity(
      this,
      1001,
      openIntent,
      PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
    )

    val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
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

    // 3. Play continuous looping siren alert & vibrations
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

  private fun stopAlertAndSelf() {
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

    stopForeground(true)
    stopSelf()
  }

  override fun onDestroy() {
    stopAlertAndSelf()
    super.onDestroy()
  }

  private fun createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel = NotificationChannel(
        CHANNEL_ID,
        "تنبيهات طلبات البقال للتاجر",
        NotificationManager.IMPORTANCE_HIGH
      ).apply {
        description = "إنذار صوتي فوري وتنبيه بقدوم طلبات جديدة للتاجر"
        enableLights(true)
        enableVibration(true)
        lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        setBypassDnd(true)
      }
      val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      nm.createNotificationChannel(channel)
    }
  }
}
