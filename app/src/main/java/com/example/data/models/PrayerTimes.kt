package com.example.data.models

import java.util.Date

enum class PrayerType(val arName: String, val enName: String) {
    FAJR("الفجر", "Fajr"),
    SUNRISE("الشروق", "Sunrise"),
    DHUHR("الظهر", "Dhuhr"),
    ASR("العصر", "Asr"),
    MAGHRIB("المغرب", "Maghrib"),
    ISHA("العشاء", "Isha")
}

data class PrayerTimeItem(
    val type: PrayerType,
    val timeMillis: Long,
    val formattedTime: String,
    val isNext: Boolean = false,
    val isPassed: Boolean = false
)

data class DayPrayerTimes(
    val fajr: Long,
    val sunrise: Long,
    val dhuhr: Long,
    val asr: Long,
    val maghrib: Long,
    val isha: Long,
    val date: Date,
    val cityName: String,
    val countryName: String
)

data class HijriDate(
    val day: Int,
    val month: Int,
    val monthNameAr: String,
    val monthNameEn: String,
    val year: Int,
    val formattedAr: String
)

data class CityLocation(
    val nameAr: String,
    val nameEn: String,
    val countryAr: String,
    val countryEn: String,
    val latitude: Double,
    val longitude: Double,
    val timezoneOffsetHours: Double
)
