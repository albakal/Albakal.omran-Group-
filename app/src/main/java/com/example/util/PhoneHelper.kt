package com.example.util

object PhoneHelper {

  /**
   * Converts Arabic/Eastern Arabic digits to standard ASCII digits.
   * e.g., '٠١٢٣٤٥٦٧٨٩' -> '0123456789'
   */
  fun normalizeDigits(input: String?): String {
    if (input.isNullOrBlank()) return ""
    val arabic = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩', '۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
    val english = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
    var result = input.trim()
    for (i in arabic.indices) {
      result = result.replace(arabic[i], english[i])
    }
    return result
  }

  /**
   * Cleans phone number by converting digits and stripping whitespace, dashes, and extra chars.
   */
  fun cleanPhone(input: String?): String {
    val norm = normalizeDigits(input)
    return norm.replace(" ", "")
      .replace("-", "")
      .replace("(", "")
      .replace(")", "")
  }

  /**
   * Generates all common Egyptian / international phone variants for query matching.
   * e.g. for "01012345678", produces ["01012345678", "+201012345678", "201012345678", "1012345678"]
   */
  fun getPhoneVariants(rawPhone: String?): List<String> {
    val clean = cleanPhone(rawPhone)
    if (clean.isBlank()) return emptyList()

    val variants = mutableSetOf<String>()
    variants.add(clean)
    rawPhone?.trim()?.let { variants.add(it) }

    val bare = clean.removePrefix("+20").removePrefix("20").removePrefix("0")
    if (bare.isNotBlank()) {
      variants.add(bare)
      variants.add("0$bare")
      variants.add("20$bare")
      variants.add("+20$bare")
      variants.add("0020$bare")
    }

    return variants.toList()
  }

  /**
   * Checks if two phone numbers refer to the same Egyptian subscriber number.
   */
  fun matchesPhone(p1: String?, p2: String?): Boolean {
    val b1 = cleanPhone(p1).removePrefix("+20").removePrefix("20").removePrefix("0")
    val b2 = cleanPhone(p2).removePrefix("+20").removePrefix("20").removePrefix("0")
    return b1.isNotBlank() && b1 == b2
  }

  /**
   * Formats a phone number into an international number suitable for WhatsApp URL (e.g. 201000000000).
   */
  fun formatForWhatsApp(rawPhone: String?): String {
    val clean = cleanPhone(rawPhone).removePrefix("+")
    return if (clean.startsWith("0")) {
      "2$clean"
    } else if (clean.startsWith("2")) {
      clean
    } else {
      "20$clean"
    }
  }
}
