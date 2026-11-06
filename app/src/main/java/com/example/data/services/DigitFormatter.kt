package com.example.data.services

/**
 * Utility to format numbers into:
 * - "western": Arabic/Latin numerals: 1234
 * - "eastern": Eastern Arabic / Google Arabic numerals: ١٢٣٤
 */
object DigitFormatter {

    fun format(text: String, digitType: String = "western"): String {
        if (text.isEmpty()) return text
        return if (digitType == "eastern" || digitType == "google_arabic" || digitType == "١٢٣٤") {
            val easternDigits = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
            val sb = StringBuilder(text.length)
            for (ch in text) {
                if (ch in '0'..'9') {
                    sb.append(easternDigits[ch - '0'])
                } else {
                    sb.append(ch)
                }
            }
            sb.toString()
        } else {
            val westernDigits = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
            val sb = StringBuilder(text.length)
            for (ch in text) {
                if (ch in '٠'..'٩') {
                    sb.append(westernDigits[ch - '٠'])
                } else {
                    sb.append(ch)
                }
            }
            sb.toString()
        }
    }

    fun format(number: Number, digitType: String = "western"): String {
        return format(number.toString(), digitType)
    }
}
