package com.example.data.services

import android.content.Context
import com.example.data.local.AppPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar

data class WeatherInfo(
    val temperatureC: Double = 26.0,
    val conditionAr: String = "معتدل ولطيف",
    val windSpeedKmh: Double = 12.0,
    val isDay: Boolean = true,
    val lastUpdated: String = "محدث",
    val isOnline: Boolean = false
)

/**
 * Temperature & Climate Engine
 * Computes realistic diurnal and seasonal temperature based on latitude, month, and hour of day,
 * with customizable calibration offset.
 */
class WeatherService(
    private val context: Context,
    private val appPreferences: AppPreferences
) {
    private val _weatherState = MutableStateFlow(computeOfflineWeather(24.4672, 39.6111))
    val weatherState: StateFlow<WeatherInfo> = _weatherState.asStateFlow()

    fun isNetworkAvailable(): Boolean = false

    suspend fun refreshWeather(latitude: Double, longitude: Double) {
        _weatherState.value = computeOfflineWeather(latitude, longitude)
    }

    fun computeOfflineWeather(latitude: Double, longitude: Double): WeatherInfo {
        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH) // 0..11
        val hour = calendar.get(Calendar.HOUR_OF_DAY) // 0..23

        val isDay = hour in 6..18

        // Base temperature estimation based on latitude and climate zone
        val baseForLat = when {
            latitude in 15.0..26.0 -> 30.0 // Arabian Gulf, Red Sea, Mecca, Medina
            latitude in 26.0..32.0 -> 25.0 // Egypt, North Africa, Kuwait
            latitude in 32.0..36.0 -> 21.0 // Levant, Iraq, Morocco
            latitude in 36.0..42.0 -> 17.0 // Turkey, Mediterranean
            else -> 14.0 // Northern regions
        }

        // Seasonal variation (hottest in Jun/Jul/Aug, coolest in Jan/Feb)
        val monthOffset = when (month) {
            0, 1 -> -8.0 // Jan, Feb
            2 -> -4.0 // Mar
            3 -> 0.0 // Apr
            4 -> 4.0 // May
            5, 6, 7 -> 8.0 // Jun, Jul, Aug
            8 -> 5.0 // Sep
            9 -> 1.0 // Oct
            10 -> -4.0 // Nov
            else -> -7.0 // Dec
        }

        // Diurnal variation (coldest around Fajr, peak around Asr)
        val hourOffset = when (hour) {
            in 0..4 -> -4.0
            5, 6 -> -5.0 // dawn minimum
            in 7..9 -> -1.0
            in 10..12 -> 3.0
            in 13..16 -> 5.0 // peak daytime
            in 17..19 -> 1.0
            else -> -2.0
        }

        val userOffset = appPreferences.temperatureOffsetC.value
        val finalTemp = (baseForLat + monthOffset + hourOffset + userOffset).coerceIn(-10.0, 52.0)

        val condition = when {
            finalTemp >= 36.0 -> "حار وصحو"
            finalTemp >= 28.0 -> "دافئ ومشمس"
            finalTemp >= 21.0 -> "معتدل ولطيف"
            finalTemp >= 14.0 -> "مائل للبرودة"
            else -> "بارد وصافٍ"
        }

        val wind = when (month) {
            2, 3 -> 18.0
            else -> 12.0
        }

        return WeatherInfo(
            temperatureC = finalTemp,
            conditionAr = condition,
            windSpeedKmh = wind,
            isDay = isDay,
            lastUpdated = "محدث",
            isOnline = false
        )
    }
}
