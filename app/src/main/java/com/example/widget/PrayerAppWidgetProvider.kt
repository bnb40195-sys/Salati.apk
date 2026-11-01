package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R
import com.example.data.models.CityLocation
import com.example.data.models.PrayerType
import com.example.data.services.PrayerTimeCalculator
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PrayerAppWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_UPDATE_WIDGET || intent.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val thisWidget = ComponentName(context, PrayerAppWidgetProvider::class.java)
            val allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
            for (widgetId in allWidgetIds) {
                updateAppWidget(context, appWidgetManager, widgetId)
            }
        }
    }

    companion object {
        const val ACTION_UPDATE_WIDGET = "com.example.widget.ACTION_UPDATE_WIDGET"

        fun updateAllWidgets(context: Context) {
            val intent = Intent(context, PrayerAppWidgetProvider::class.java).apply {
                action = ACTION_UPDATE_WIDGET
            }
            context.sendBroadcast(intent)
        }

        fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val prefs = context.getSharedPreferences("salati_prefs", Context.MODE_PRIVATE)
            val fallbackPrefs = context.getSharedPreferences("salati_app_prefs", Context.MODE_PRIVATE)

            // Read preferences
            val cityName = prefs.getString("city_name_ar", null)
                ?: fallbackPrefs.getString("city_name_ar", "وهران (Oran)") ?: "وهران (Oran)"
            val countryName = prefs.getString("country_ar", null)
                ?: fallbackPrefs.getString("country_ar", "الجزائر") ?: "الجزائر"
            val lat = prefs.getFloat("lat", fallbackPrefs.getFloat("city_lat", 35.6971f)).toDouble()
            val lng = prefs.getFloat("lng", fallbackPrefs.getFloat("city_lng", -0.6308f)).toDouble()
            val calcMethod = prefs.getString("calc_method", null)
                ?: fallbackPrefs.getString("calc_method", "MWL") ?: "MWL"
            val juristic = prefs.getString("juristic_method", null)
                ?: fallbackPrefs.getString("juristic_method", "SHAFI") ?: "SHAFI"

            val timeFormat = prefs.getString("time_format", "12h") ?: "12h"
            val widgetTheme = prefs.getString("widget_theme", null)
                ?: fallbackPrefs.getString("widget_theme", "forest") ?: "forest"
            val widgetDhikr = prefs.getString("widget_dhikr", null)
                ?: fallbackPrefs.getString("widget_dhikr", "سبحان الله العظيم") ?: "سبحان الله العظيم"
            val showTemp = if (prefs.contains("widget_show_temp")) prefs.getBoolean("widget_show_temp", true)
                else fallbackPrefs.getBoolean("widget_show_temp", true)
            val customTemp = prefs.getString("widget_temp", null)
                ?: fallbackPrefs.getString("widget_temp", "24°C") ?: "24°C"
            val widgetAccent = prefs.getString("widget_accent", null)
                ?: fallbackPrefs.getString("widget_accent", "orange") ?: "orange"

            val accentColorInt = when (widgetAccent) {
                "gold" -> Color.parseColor("#E5A93C")
                "emerald" -> Color.parseColor("#10B981")
                "teal" -> Color.parseColor("#14B8A6")
                else -> Color.parseColor("#FFA040") // Orange as in user screenshot
            }

            val greenHighlightInt = Color.parseColor("#4ADE80")
            val normalTimeColor = Color.parseColor("#FFFFFF")

            val methodEnum = try {
                com.example.data.services.CalculationMethod.valueOf(calcMethod)
            } catch (e: Exception) {
                com.example.data.services.CalculationMethod.MUSLIM_WORLD_LEAGUE
            }
            val juristicEnum = try {
                com.example.data.services.JuristicMethod.valueOf(juristic)
            } catch (e: Exception) {
                com.example.data.services.JuristicMethod.SHAFI_STANDARD
            }

            // Load offsets
            val offsets = mutableMapOf<PrayerType, Int>()
            for (type in PrayerType.values()) {
                val off = prefs.getInt("offset_${type.name}", 0)
                if (off != 0) offsets[type] = off
            }

            // Calculate prayer times
            val prayerTimes = PrayerTimeCalculator.calculatePrayerTimes(
                latitude = lat,
                longitude = lng,
                method = methodEnum,
                juristic = juristicEnum,
                offsets = offsets,
                cityName = cityName,
                countryName = countryName
            )

            val now = System.currentTimeMillis()
            val timeFormatter = if (timeFormat == "24h") {
                SimpleDateFormat("HH:mm", Locale.getDefault())
            } else {
                SimpleDateFormat("hh:mm a", Locale("ar"))
            }
            val gregorianFormatter = SimpleDateFormat("d MMMM yyyy", Locale("ar"))

            val hijriDate = PrayerTimeCalculator.getHijriDate(Date())
            val gregorianDateStr = gregorianFormatter.format(Date(now))

            // RemoteViews layout
            val views = RemoteViews(context.packageName, R.layout.widget_prayer_times)

            // Configure TextClock formats for live ticking on phone home screen
            if (timeFormat == "24h") {
                views.setString(R.id.widget_tv_clock, "setFormat24Hour", "HH:mm")
                views.setString(R.id.widget_tv_clock, "setFormat12Hour", "HH:mm")
            } else {
                views.setString(R.id.widget_tv_clock, "setFormat12Hour", "hh:mm")
                views.setString(R.id.widget_tv_clock, "setFormat24Hour", "hh:mm")
            }

            // Background theme
            val bgDrawableRes = when (widgetTheme) {
                "amoled" -> R.drawable.widget_bg_amoled
                "emerald" -> R.drawable.widget_bg_emerald
                else -> R.drawable.widget_bg_forest
            }
            views.setInt(R.id.widget_root, "setBackgroundResource", bgDrawableRes)

            // Determine next prayer and remaining time
            val prayers = listOf(
                PrayerType.FAJR to prayerTimes.fajr,
                PrayerType.SUNRISE to prayerTimes.sunrise,
                PrayerType.DHUHR to prayerTimes.dhuhr,
                PrayerType.ASR to prayerTimes.asr,
                PrayerType.MAGHRIB to prayerTimes.maghrib,
                PrayerType.ISHA to prayerTimes.isha
            )

            val nextPrayerPair = prayers.firstOrNull { it.second > now } ?: (PrayerType.FAJR to (prayerTimes.fajr + 86400000L))
            val nextPrayer = nextPrayerPair.first
            val nextPrayerMillis = nextPrayerPair.second

            val diffMillis = (nextPrayerMillis - now).coerceAtLeast(0)
            val diffMinutes = (diffMillis / (1000 * 60)).toInt()

            val countdownText = if (diffMinutes < 60) {
                "$diffMinutes دقيقة"
            } else {
                val h = diffMinutes / 60
                val m = diffMinutes % 60
                if (m == 0) "$h ساعة" else "$h س و $m د"
            }

            val nextTimeFormatted = timeFormatter.format(Date(nextPrayerMillis))
            val nextPrayerText = "حتى ${nextPrayer.arName} @ $nextTimeFormatted"

            val locationTempText = if (showTemp) {
                "$cityName • $customTemp"
            } else {
                cityName
            }

            // Set top text
            views.setTextViewText(R.id.widget_tv_countdown, countdownText)
            views.setTextColor(R.id.widget_tv_countdown, accentColorInt)
            views.setTextViewText(R.id.widget_tv_next_prayer, nextPrayerText)
            views.setTextViewText(R.id.widget_tv_location_temp, locationTempText)

            views.setTextViewText(R.id.widget_tv_hijri_date, hijriDate.formattedAr)
            views.setTextViewText(R.id.widget_tv_gregorian_date, gregorianDateStr)

            // Format 6 prayer times
            val fajrStr = timeFormatter.format(Date(prayerTimes.fajr))
            val sunriseStr = timeFormatter.format(Date(prayerTimes.sunrise))
            val dhuhrStr = timeFormatter.format(Date(prayerTimes.dhuhr))
            val asrStr = timeFormatter.format(Date(prayerTimes.asr))
            val maghribStr = timeFormatter.format(Date(prayerTimes.maghrib))
            val ishaStr = timeFormatter.format(Date(prayerTimes.isha))

            views.setTextViewText(R.id.widget_tv_fajr_time, fajrStr)
            views.setTextViewText(R.id.widget_tv_sunrise_time, sunriseStr)
            views.setTextViewText(R.id.widget_tv_dhuhr_time, dhuhrStr)
            views.setTextViewText(R.id.widget_tv_asr_time, asrStr)
            views.setTextViewText(R.id.widget_tv_maghrib_time, maghribStr)
            views.setTextViewText(R.id.widget_tv_isha_time, ishaStr)

            // Highlight sunrise in green (as in screenshot)
            views.setTextColor(R.id.widget_tv_sunrise_time, greenHighlightInt)

            // Highlight current/next prayer
            views.setTextColor(R.id.widget_tv_fajr_time, if (nextPrayer == PrayerType.FAJR) accentColorInt else normalTimeColor)
            views.setTextColor(R.id.widget_tv_dhuhr_time, if (nextPrayer == PrayerType.DHUHR) accentColorInt else normalTimeColor)
            views.setTextColor(R.id.widget_tv_asr_time, if (nextPrayer == PrayerType.ASR) accentColorInt else normalTimeColor)
            views.setTextColor(R.id.widget_tv_maghrib_time, if (nextPrayer == PrayerType.MAGHRIB) accentColorInt else normalTimeColor)
            views.setTextColor(R.id.widget_tv_isha_time, if (nextPrayer == PrayerType.ISHA) accentColorInt else normalTimeColor)

            // Bottom Dhikr
            views.setTextViewText(R.id.widget_tv_dhikr, widgetDhikr)

            // PendingIntent to launch MainActivity when widget is tapped
            val appIntent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                appIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
