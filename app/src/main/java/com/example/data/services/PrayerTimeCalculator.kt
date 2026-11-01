package com.example.data.services

import com.example.data.models.DayPrayerTimes
import com.example.data.models.HijriDate
import com.example.data.models.PrayerType
import java.util.Calendar
import java.util.Date
import java.util.TimeZone
import kotlin.math.*

enum class CalculationMethod(
    val titleAr: String,
    val fajrAngle: Double,
    val ishaAngle: Double,
    val ishaMinutesAfterMaghrib: Int? = null,
    val regionAr: String = "عام"
) {
    MAWAQIT_NET("طريقة موقع مواقيت (Mawaqit - دقة المساجد أوفلاين بدون نت)", 18.0, 17.0, null, "مواقيت المساجد (أوفلاين)"),
    UMM_AL_QURA("أم القرى (السعودية وشبه الجزيرة العربية)", 18.5, 0.0, 90, "الجزيرة العربية"),
    MUSLIM_WORLD_LEAGUE("رابطة العالم الإسلامي", 18.0, 17.0, null, "عالمي"),
    MAGHREB_ARAB("دول المغرب العربي (المغرب، الجزائر، تونس)", 19.0, 17.0, null, "المغرب العربي"),
    MASHREQ_EGYPT("دول المشرق العربي (مصر، السودان - الهيئة العامة)", 19.5, 17.5, null, "المشرق العربي"),
    LEVANT_SHAM("دول الشام (الأردن، سوريا، فلسطين، لبنان)", 18.0, 17.0, null, "بلاد الشام"),
    GULF_DUBAI("دول الخليج العربي (الإمارات، دبي)", 18.2, 18.2, null, "الخليج"),
    BALKANS_TURKEY("دول البلقان وتركيا (ديانت / البوسنة)", 18.0, 17.0, null, "البلقان وتركيا"),
    CAUCASUS_CENTRAL_ASIA("دول القوقاز ووسط آسيا (أوزبكستان، أذربيجان)", 18.0, 17.0, null, "القوقاز"),
    KARACHI("جنوب آسيا (باكستان، الهند، بنغلاديش - كراتشي)", 18.0, 18.0, null, "جنوب آسيا"),
    NORTH_AMERICA("أمريكا الشمالية (ISNA)", 15.0, 15.0, null, "أمريكا الشمالية"),
    EUROPE_UOIF("دول أوروبا (اتحاد المنظمات الإسلامية)", 12.0, 12.0, null, "أوروبا")
}

enum class JuristicMethod(val titleAr: String, val shadowFactor: Double) {
    SHAFI_STANDARD("الشافعي / الحنبلي / المالكي (الأصل)", 1.0),
    HANAFI("الحنفي (المثلين)", 2.0)
}

object PrayerTimeCalculator {

    fun calculatePrayerTimes(
        latitude: Double,
        longitude: Double,
        date: Date = Date(),
        method: CalculationMethod = CalculationMethod.UMM_AL_QURA,
        juristic: JuristicMethod = JuristicMethod.SHAFI_STANDARD,
        offsets: Map<PrayerType, Int> = emptyMap(),
        cityName: String = "مكة المكرمة",
        countryName: String = "المملكة العربية السعودية"
    ): DayPrayerTimes {
        val cal = Calendar.getInstance().apply { time = date }
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) + 1
        val day = cal.get(Calendar.DAY_OF_MONTH)

        val tzOffsetHours = TimeZone.getDefault().getOffset(date.time) / 3600000.0

        // Julian Date
        val julianDate = getJulianDate(year, month, day)

        // Solar calculations
        val d = julianDate - 2451545.0
        val g = fixAngle(357.529 + 0.98560028 * d)
        val q = fixAngle(280.459 + 0.98564736 * d)
        val l = fixAngle(q + 1.915 * sin(toRadians(g)) + 0.020 * sin(toRadians(2 * g)))

        val e = 23.439 - 0.00000036 * d
        val ra = fixAngle(toDegrees(atan2(cos(toRadians(e)) * sin(toRadians(l)), cos(toRadians(l))))) / 15.0

        val declination = toDegrees(asin(sin(toRadians(e)) * sin(toRadians(l))))
        val equationOfTime = q / 15.0 - ra

        // Midday (Dhuhr)
        val noonUtc = 12.0 - equationOfTime - (longitude / 15.0)

        // Sunrise & Sunset angle is approx 0.8333 degrees below horizon
        val sunriseSunHour = getHourAngle(-0.8333, latitude, declination)
        val fajrSunHour = getHourAngle(-method.fajrAngle, latitude, declination)

        // Asr angle (sun is above horizon)
        val asrAltitude = toDegrees(atan(1.0 / (juristic.shadowFactor + tan(toRadians(abs(latitude - declination))))))
        val asrSunHour = getHourAngle(asrAltitude, latitude, declination)

        // Maghrib angle
        val maghribSunHour = sunriseSunHour // same solar elevation for sunset

        // Isha angle
        val ishaSunHour = if (method.ishaMinutesAfterMaghrib != null) {
            null
        } else {
            getHourAngle(-method.ishaAngle, latitude, declination)
        }

        // Times in hours UTC
        val fajrUtc = noonUtc - fajrSunHour
        val sunriseUtc = noonUtc - sunriseSunHour
        val dhuhrUtc = noonUtc
        val asrUtc = noonUtc + asrSunHour
        val maghribUtc = noonUtc + maghribSunHour
        val ishaUtc = if (method.ishaMinutesAfterMaghrib != null) {
            maghribUtc + (method.ishaMinutesAfterMaghrib / 60.0)
        } else {
            noonUtc + (ishaSunHour ?: (maghribSunHour + 1.5))
        }

        // Convert UTC hours to local epoch millis
        fun toEpoch(hoursUtc: Double, offsetMinutes: Int = 0): Long {
            val baseCal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                time = date
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val totalSeconds = (hoursUtc * 3600.0).roundToLong()
            return baseCal.timeInMillis + (totalSeconds * 1000) + (offsetMinutes * 60 * 1000)
        }

        return DayPrayerTimes(
            fajr = toEpoch(fajrUtc, offsets[PrayerType.FAJR] ?: 0),
            sunrise = toEpoch(sunriseUtc, offsets[PrayerType.SUNRISE] ?: 0),
            dhuhr = toEpoch(dhuhrUtc, offsets[PrayerType.DHUHR] ?: 0),
            asr = toEpoch(asrUtc, offsets[PrayerType.ASR] ?: 0),
            maghrib = toEpoch(maghribUtc, offsets[PrayerType.MAGHRIB] ?: 0),
            isha = toEpoch(ishaUtc, offsets[PrayerType.ISHA] ?: 0),
            date = date,
            cityName = cityName,
            countryName = countryName
        )
    }

    private fun getHourAngle(altitude: Double, lat: Double, dec: Double): Double {
        val latRad = toRadians(lat)
        val decRad = toRadians(dec)
        val altRad = toRadians(altitude)
        val cosH = (sin(altRad) - sin(latRad) * sin(decRad)) / (cos(latRad) * cos(decRad))
        val clamped = cosH.coerceIn(-1.0, 1.0)
        return toDegrees(acos(clamped)) / 15.0
    }

    private fun getJulianDate(year: Int, month: Int, day: Int): Double {
        var y = year
        var m = month
        if (m <= 2) {
            y -= 1
            m += 12
        }
        val a = floor(y / 100.0)
        val b = 2 - a + floor(a / 4.0)
        return floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + day + b - 1524.5
    }

    private fun fixAngle(a: Double): Double {
        var res = a - 360.0 * floor(a / 360.0)
        if (res < 0) res += 360.0
        return res
    }

    private fun toRadians(deg: Double) = deg * (PI / 180.0)
    private fun toDegrees(rad: Double) = rad * (180.0 / PI)

    // Accurate Hijri conversion algorithm
    fun getHijriDate(date: Date = Date(), adjustmentDays: Int = 0): HijriDate {
        val cal = Calendar.getInstance().apply {
            time = date
            add(Calendar.DAY_OF_YEAR, adjustmentDays)
        }
        val y = cal.get(Calendar.YEAR)
        val m = cal.get(Calendar.MONTH) + 1
        val d = cal.get(Calendar.DAY_OF_MONTH)

        val jd = getJulianDate(y, m, d).toInt()
        val l = jd - 1948440 + 10632
        val n = floor((l - 1) / 10631.0).toInt()
        val l2 = l - 10631 * n + 354
        val j = (floor((10985 - l2) / 5316.0) * floor((50 * l2) / 17719.0) +
                floor(l2 / 5670.0) * floor((43 * l2) / 15238.0)).toInt()
        val l3 = l2 - floor((30 - j) / 15.0).toInt() * floor((17719 * j) / 50.0).toInt() -
                floor(j / 16.0).toInt() * floor((15238 * j) / 43.0).toInt() + 29
        val month = floor((24 * l3) / 709.0).toInt()
        val day = l3 - floor((709 * month) / 24.0).toInt()
        val year = 30 * n + j - 30

        val hijriMonthsAr = listOf(
            "محرم", "صفر", "ربيع الأول", "ربيع الآخر",
            "جمادى الأولى", "جمادى الآخرة", "رجب", "شعبان",
            "رمضان", "شوال", "ذو القعدة", "ذو الحجة"
        )
        val hijriMonthsEn = listOf(
            "Muharram", "Safar", "Rabi' al-Awwal", "Rabi' al-Thani",
            "Jumada al-Awwal", "Jumada al-Thani", "Rajab", "Sha'ban",
            "Ramadan", "Shawwal", "Dhu al-Qi'dah", "Dhu al-Hijjah"
        )

        val validMonth = (month.coerceIn(1, 12)) - 1
        val monthAr = hijriMonthsAr[validMonth]
        val monthEn = hijriMonthsEn[validMonth]
        val formattedAr = "$day $monthAr $year هـ"

        return HijriDate(
            day = day,
            month = validMonth + 1,
            monthNameAr = monthAr,
            monthNameEn = monthEn,
            year = year,
            formattedAr = formattedAr
        )
    }
}
