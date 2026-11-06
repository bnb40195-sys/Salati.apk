package com.example.data.services

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.data.local.AppPreferences
import com.example.widget.PrayerAppWidgetProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.Calendar
import java.util.Locale

data class WeatherInfo(
    val temperatureC: Double = 26.0,
    val conditionAr: String = "معتدل ولطيف",
    val windSpeedKmh: Double = 12.0,
    val isDay: Boolean = true,
    val lastUpdated: String = "محدث",
    val isOnline: Boolean = false,
    val humidityPercent: Int = 50
)

/**
 * Temperature & Climate Engine
 * Supports real-time online weather fetching via Open-Meteo REST API when internet is available,
 * and seamless fallback to astronomical diurnal/seasonal calculations when offline.
 */
class WeatherService(
    private val context: Context,
    private val appPreferences: AppPreferences
) {
    private val _weatherState = MutableStateFlow(computeOfflineWeather(24.4672, 39.6111))
    val weatherState: StateFlow<WeatherInfo> = _weatherState.asStateFlow()

    fun isNetworkAvailable(): Boolean {
        return try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return false
            val network = cm.activeNetwork ?: return false
            val caps = cm.getNetworkCapabilities(network) ?: return false
            caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } catch (e: Exception) {
            false
        }
    }

    suspend fun refreshWeather(latitude: Double, longitude: Double) {
        if (isNetworkAvailable()) {
            val onlineWeather = fetchOnlineWeather(latitude, longitude)
            if (onlineWeather != null) {
                _weatherState.value = onlineWeather
                // Update widget temperature preference
                val tempStr = "${onlineWeather.temperatureC.toInt()}°C"
                context.getSharedPreferences("salati_prefs", Context.MODE_PRIVATE)
                    .edit().putString("widget_temp", tempStr).apply()
                PrayerAppWidgetProvider.updateAllWidgets(context)
                return
            }
        }

        // Fallback to offline astronomical calculation
        val offlineWeather = computeOfflineWeather(latitude, longitude)
        _weatherState.value = offlineWeather
        val tempStr = "${offlineWeather.temperatureC.toInt()}°C"
        context.getSharedPreferences("salati_prefs", Context.MODE_PRIVATE)
            .edit().putString("widget_temp", tempStr).apply()
        PrayerAppWidgetProvider.updateAllWidgets(context)
    }

    private suspend fun fetchOnlineWeather(latitude: Double, longitude: Double): WeatherInfo? = withContext(Dispatchers.IO) {
        try {
            val urlStr = String.format(
                Locale.US,
                "https://api.open-meteo.com/v1/forecast?latitude=%.4f&longitude=%.4f&current=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m&timezone=auto",
                latitude, longitude
            )
            val url = URL(urlStr)
            val conn = url.openConnection() as HttpURLConnection
            conn.connectTimeout = 5000
            conn.readTimeout = 5000
            conn.requestMethod = "GET"

            if (conn.responseCode == 200) {
                val reader = BufferedReader(InputStreamReader(conn.inputStream))
                val response = reader.readText()
                reader.close()

                val json = JSONObject(response)
                if (json.has("current")) {
                    val current = json.getJSONObject("current")
                    val temp = current.getDouble("temperature_2m")
                    val humidity = current.optInt("relative_humidity_2m", 50)
                    val wind = current.optDouble("wind_speed_10m", 12.0)
                    val weatherCode = current.optInt("weather_code", 0)

                    val calendar = Calendar.getInstance()
                    val hour = calendar.get(Calendar.HOUR_OF_DAY)
                    val isDay = hour in 6..18

                    val condition = mapWeatherCodeToAr(weatherCode, isDay)

                    return@withContext WeatherInfo(
                        temperatureC = temp,
                        conditionAr = condition,
                        windSpeedKmh = wind,
                        isDay = isDay,
                        lastUpdated = "مباشر عبر الإنترنت",
                        isOnline = true,
                        humidityPercent = humidity
                    )
                }
            }
            null
        } catch (e: Exception) {
            null
        }
    }

    private fun mapWeatherCodeToAr(code: Int, isDay: Boolean): String {
        return when (code) {
            0 -> if (isDay) "صحو ومشمس" else "صافٍ ولطيف"
            1, 2, 3 -> "صحو إلى غائم جزئياً"
            45, 48 -> "ضباب خفيف"
            51, 53, 55 -> "رذاذ مطري خفيف"
            61, 63, 65 -> "أمطار خير وبركة"
            71, 73, 75, 77 -> "تساقط ثلوج"
            80, 81, 82 -> "زخات مطرية"
            95, 96, 99 -> "عواصف رعدية وأمطار"
            else -> "معتدل ولطيف"
        }
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
            lastUpdated = "محلي فلكي",
            isOnline = false
        )
    }
}
