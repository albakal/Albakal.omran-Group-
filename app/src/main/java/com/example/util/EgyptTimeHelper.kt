package com.example.util

import java.util.Calendar
import java.util.TimeZone

object EgyptTimeHelper {
  val CAIRO_ZONE: TimeZone = TimeZone.getTimeZone("Africa/Cairo")

  /**
   * Checks whether a merchant is currently open based on current Cairo / Egypt local time.
   */
  fun isMerchantOpenNow(
    hoursType: String,
    startHour: String,
    endHour: String
  ): Boolean {
    // If 24 hours or unconfigured, always open
    if (hoursType.isBlank() || hoursType == "ALL_DAY") {
      return true
    }

    val cal = Calendar.getInstance(CAIRO_ZONE)
    val currentHour = cal.get(Calendar.HOUR_OF_DAY)
    val currentMinute = cal.get(Calendar.MINUTE)
    val currentMinutesOfDay = currentHour * 60 + currentMinute

    val startMinutes = parseMinutesOfDay(startHour, defaultMinutes = 0)
    val endMinutes = parseMinutesOfDay(endHour, defaultMinutes = 24 * 60)

    return if (startMinutes <= endMinutes) {
      currentMinutesOfDay in startMinutes until endMinutes
    } else {
      // Overnight span, e.g. 20:00 to 04:00
      currentMinutesOfDay >= startMinutes || currentMinutesOfDay < endMinutes
    }
  }

  private fun parseMinutesOfDay(timeStr: String, defaultMinutes: Int): Int {
    val clean = timeStr.trim()
    val parts = clean.split(":").mapNotNull { it.trim().toIntOrNull() }
    if (parts.isEmpty()) return defaultMinutes
    val hour = parts[0]
    val min = if (parts.size > 1) parts[1] else 0
    return (hour * 60 + min).coerceIn(0, 24 * 60)
  }

  /**
   * Generates 24-hour time slots starting from 01:00 AM (01:00 ص) up to 12:00 midnight (12:00 ص)
   */
  fun get24HourOptions(): List<Pair<String, String>> {
    val list = mutableListOf<Pair<String, String>>()
    // 01:00 AM to 11:00 AM
    for (h in 1..11) {
      val val24 = String.format("%02d:00", h)
      val label = String.format("%02d:00 ص", h)
      list.add(val24 to label)
    }
    // 12:00 PM (Noon)
    list.add("12:00" to "12:00 م (ظهراً)")
    // 01:00 PM to 11:00 PM
    for (h in 13..23) {
      val val24 = String.format("%02d:00", h)
      val label = String.format("%02d:00 م", h - 12)
      list.add(val24 to label)
    }
    // 00:00 (Midnight)
    list.add("00:00" to "12:00 ص (منتصف الليل)")
    return list
  }

  data class WorkingHoursPreset(
    val type: String,
    val label: String,
    val defaultStartHour: String = "00:00",
    val defaultEndHour: String = "23:59"
  )

  /**
   * Comprehensive list of expanded working hours presets for merchants
   */
  fun getWorkingHoursPresets(): List<WorkingHoursPreset> {
    return listOf(
      WorkingHoursPreset("ALL_DAY", "متاح خدمة 24 ساعة (طوال اليوم)", "00:00", "23:59"),
      WorkingHoursPreset("PRESET_08_TO_16", "متاح من 08:00 ص إلى 04:00 م (وردية صباحية)", "08:00", "16:00"),
      WorkingHoursPreset("PRESET_08_TO_20", "متاح من 08:00 ص إلى 08:00 م (12 ساعة)", "08:00", "20:00"),
      WorkingHoursPreset("PRESET_08_TO_23", "متاح من 08:00 ص إلى 11:00 م (طوال اليوم)", "08:00", "23:00"),
      WorkingHoursPreset("PRESET_09_TO_17", "متاح من 09:00 ص إلى 05:00 م (فترة عمل رسمية)", "09:00", "17:00"),
      WorkingHoursPreset("PRESET_09_TO_21", "متاح من 09:00 ص إلى 09:00 م (12 ساعة)", "09:00", "21:00"),
      WorkingHoursPreset("PRESET_09_TO_00", "متاح من 09:00 ص إلى 12:00 ص (منتصف الليل)", "09:00", "00:00"),
      WorkingHoursPreset("PRESET_10_TO_18", "متاح من 10:00 ص إلى 06:00 م (8 ساعات)", "10:00", "18:00"),
      WorkingHoursPreset("PRESET_10_TO_19", "متاح من 10:00 ص إلى 07:00 م (9 ساعات)", "10:00", "19:00"),
      WorkingHoursPreset("PRESET_10_TO_22", "متاح من 10:00 ص إلى 10:00 م (12 ساعة)", "10:00", "22:00"),
      WorkingHoursPreset("PRESET_10_TO_00", "متاح من 10:00 ص إلى 12:00 ص (منتصف الليل)", "10:00", "00:00"),
      WorkingHoursPreset("PRESET_11_TO_23", "متاح من 11:00 ص إلى 11:00 م (12 ساعة)", "11:00", "23:00"),
      WorkingHoursPreset("PRESET_12_TO_20", "متاح من 12:00 م إلى 08:00 م (وردية بعد الظهر)", "12:00", "20:00"),
      WorkingHoursPreset("PRESET_12_TO_00", "متاح من 12:00 م إلى 12:00 ص (منتصف الليل)", "12:00", "00:00"),
      WorkingHoursPreset("PRESET_13_TO_21", "متاح من 01:00 م إلى 09:00 م (وردية مسائية)", "13:00", "21:00"),
      WorkingHoursPreset("PRESET_14_TO_22", "متاح من 02:00 م إلى 10:00 م (وردية مسائية)", "14:00", "22:00"),
      WorkingHoursPreset("PRESET_14_TO_02", "متاح من 02:00 م إلى 02:00 ص (وردية ليلية)", "14:00", "02:00"),
      WorkingHoursPreset("PRESET_16_TO_00", "متاح من 04:00 م إلى 12:00 ص (وردية مسائية)", "16:00", "00:00"),
      WorkingHoursPreset("PRESET_18_TO_02", "متاح من 06:00 م إلى 02:00 ص (سهرة)", "18:00", "02:00"),
      WorkingHoursPreset("PRESET_20_TO_04", "متاح من 08:00 م إلى 04:00 ص (وردية ليلية)", "20:00", "04:00"),
      WorkingHoursPreset("CUSTOM", "تحديد مواعيد مخصصة (من ـ ـ : ـ ـ إلى ـ ـ : ـ ـ)", "09:00", "21:00")
    )
  }
}

