package com.example.data.services

import com.example.data.local.MoonPhaseInfo
import java.util.Calendar
import java.util.Date
import kotlin.math.*

object MoonPhaseCalculator {

    /**
     * Calculates moon phase, age in days, and illumination percentage for a given date.
     */
    fun calculateMoonPhase(date: Date = Date()): MoonPhaseInfo {
        val cal = Calendar.getInstance().apply { time = date }
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) + 1
        val day = cal.get(Calendar.DAY_OF_MONTH)

        // Julian date
        var y = year
        var m = month
        if (m <= 2) {
            y -= 1
            m += 12
        }
        val a = floor(y / 100.0)
        val b = 2 - a + floor(a / 4.0)
        val jd = floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + day + b - 1524.5

        // Days since known new moon (Jan 6, 2000 18:14 UTC: JD 2451549.5)
        val daysSinceNew = jd - 2451549.5
        val synodicMonth = 29.53058867
        val newMoons = daysSinceNew / synodicMonth
        val currentPhaseFrac = (newMoons - floor(newMoons))
        val moonAgeDays = currentPhaseFrac * synodicMonth

        // Illumination: 0 to 100%
        val illuminationPercent = ((1 - cos(currentPhaseFrac * 2 * PI)) / 2.0 * 100).roundToInt()

        val (phaseNameAr, phaseNameEn, emoji) = when {
            moonAgeDays < 1.84 -> Triple("محاق (ولادة الهلال الجديد)", "New Moon", "🌑")
            moonAgeDays < 5.53 -> Triple("هلال متزايد (أيام بيض قادمة)", "Waxing Crescent", "🌒")
            moonAgeDays < 9.22 -> Triple("تربيع أول", "First Quarter", "🌓")
            moonAgeDays < 12.91 -> Triple("أحدب متزايد", "Waxing Gibbous", "🌔")
            moonAgeDays < 16.61 -> Triple("بدر كامل منير (ليلة اكتمال القمر)", "Full Moon", "🌕")
            moonAgeDays < 20.30 -> Triple("أحدب متناقص", "Waning Gibbous", "🌖")
            moonAgeDays < 23.99 -> Triple("تربيع أخير", "Last Quarter", "🌗")
            moonAgeDays < 27.68 -> Triple("هلال متناقص (نهاية الشهر)", "Waning Crescent", "🌘")
            else -> Triple("محاق", "New Moon", "🌑")
        }

        return MoonPhaseInfo(
            phaseNameAr = phaseNameAr,
            phaseNameEn = phaseNameEn,
            illuminationPercent = illuminationPercent,
            moonAgeDays = ((moonAgeDays * 10).roundToInt() / 10.0),
            iconEmoji = emoji
        )
    }
}
