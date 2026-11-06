package com.example.data.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R

class PrayerNotificationManager(private val context: Context) {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        const val CHANNEL_SALAWAT = "salati_salawat_channel"
        const val CHANNEL_ONGOING = "salati_ongoing_prayer_channel"
        const val CHANNEL_ADHAN = "salati_adhan_channel"
        const val CHANNEL_CENTER = "salati_center_alerts"

        const val NOTIFICATION_ID_SALAWAT = 1001
        const val NOTIFICATION_ID_ONGOING = 1002
        const val NOTIFICATION_ID_CENTER = 1003
    }

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Salawat Channel (High Importance with heads-up display)
            val salawatChannel = NotificationChannel(
                CHANNEL_SALAWAT,
                "تذكير الصلاة على النبي ﷺ",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "إشعارات التذكير بالصلاة على رسول الله ﷺ بكلام وكتابة"
                enableVibration(true)
            }

            // Ongoing Prayer Channel (Low Importance so it doesn't chime repeatedly, displays in Notification Center)
            val ongoingChannel = NotificationChannel(
                CHANNEL_ONGOING,
                "مركز إشعارات الصلاة المستمر",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "إشعار مستمر في شريط الإشعارات يعرض الصلاة القادمة والوقت المتبقي ودرجة الحرارة"
                setShowBadge(false)
            }

            // Adhan Channel
            val adhanChannel = NotificationChannel(
                CHANNEL_ADHAN,
                "تنبيهات وأذان الصلاة",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "تنبيهات دخول وقت الصلاة والإقامة"
                enableVibration(true)
            }

            // Center Alerts Channel
            val centerChannel = NotificationChannel(
                CHANNEL_CENTER,
                "مركز إشعارات صلاتي",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "إشعارات مركز الإشعارات الفورية والتنبيهات المباشرة"
                enableVibration(true)
                setShowBadge(true)
            }

            notificationManager.createNotificationChannel(salawatChannel)
            notificationManager.createNotificationChannel(ongoingChannel)
            notificationManager.createNotificationChannel(adhanChannel)
            notificationManager.createNotificationChannel(centerChannel)
        }
    }

    fun showSalawatNotification(
        title: String = "🕌 تذكير بالصلاة على النبي ﷺ",
        message: String = "اللَّهُمَّ صَلِّ وَسَلِّمْ وَبَارِكْ عَلَى سَيِّدِنَا وَنَبِيِّنَا مُحَمَّدٍ ﷺ"
    ) {
        try {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val builder = NotificationCompat.Builder(context, CHANNEL_SALAWAT)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)

            notificationManager.notify(NOTIFICATION_ID_SALAWAT, builder.build())
        } catch (e: Exception) {
            // Notifications may be disabled by system
        }
    }

    fun updateOngoingPrayerNotification(
        cityName: String,
        nextPrayerName: String,
        nextPrayerTimeFormatted: String,
        countdownText: String,
        prePrayerCountdownText: String = "",
        temperatureText: String,
        todayEventText: String = "",
        enabled: Boolean = true
    ) {
        if (!enabled) {
            cancelOngoingNotification()
            return
        }
        try {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                1,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val title = "صلاتي • $cityName ($temperatureText)"
            val shortContent = "الصلاة القادمة: $nextPrayerName @ $nextPrayerTimeFormatted (متبقي $countdownText)"

            val bigTextBuilder = StringBuilder()
            bigTextBuilder.append("🕌 متبقي على صلاة $nextPrayerName: $countdownText (عند $nextPrayerTimeFormatted)\n")
            if (prePrayerCountdownText.isNotEmpty()) {
                bigTextBuilder.append("🔔 تنبيه الدعاء قبل الصلاة: $prePrayerCountdownText\n")
            }
            bigTextBuilder.append("☀️ درجة الحرارة الحالية: $temperatureText\n")
            if (todayEventText.isNotEmpty()) {
                bigTextBuilder.append("📜 حدث اليوم: $todayEventText\n")
            }
            bigTextBuilder.append("«اللهم أعنا على ذكرك وشكرك وحسن عبادتك»")

            val builder = NotificationCompat.Builder(context, CHANNEL_ONGOING)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(shortContent)
                .setStyle(NotificationCompat.BigTextStyle().bigText(bigTextBuilder.toString()))
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .setContentIntent(pendingIntent)

            notificationManager.notify(NOTIFICATION_ID_ONGOING, builder.build())
        } catch (e: Exception) {
            // Notifications may be restricted
        }
    }

    fun cancelOngoingNotification() {
        try {
            notificationManager.cancel(NOTIFICATION_ID_ONGOING)
        } catch (e: Exception) {
            // ignore
        }
    }

    fun postCenterNotification(
        title: String = "مركز إشعارات صلاتي 🕌",
        message: String = "اللهم اجعلنا مقيمي الصلاة ومن ذريتنا ربنا وتقبل دعاء",
        subText: String = "مركز الإشعارات والتنبيهات",
        prayerCountdown: String = "",
        prePrayerCountdown: String = "",
        temperatureText: String = "",
        todayEventText: String = ""
    ) {
        try {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                2,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val bigTextBuilder = StringBuilder()
            bigTextBuilder.append(message).append("\n\n")
            if (prayerCountdown.isNotEmpty()) {
                bigTextBuilder.append("🕌 الوقت المتبقي للصلاة: ").append(prayerCountdown).append("\n")
            }
            if (prePrayerCountdown.isNotEmpty()) {
                bigTextBuilder.append("🔔 التذكير قبل الصلاة: ").append(prePrayerCountdown).append("\n")
            }
            if (temperatureText.isNotEmpty()) {
                bigTextBuilder.append("☀️ درجة الحرارة: ").append(temperatureText).append("\n")
            }
            if (todayEventText.isNotEmpty()) {
                bigTextBuilder.append("📜 حدث في مثل هذا اليوم: ").append(todayEventText).append("\n")
            }
            bigTextBuilder.append("«الصلاة عماد الدين، فحافظ عليها في وقتها»")

            val builder = NotificationCompat.Builder(context, CHANNEL_CENTER)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(if (prayerCountdown.isNotEmpty()) "متبقي $prayerCountdown | $temperatureText" else message)
                .setSubText(subText)
                .setStyle(NotificationCompat.BigTextStyle().bigText(bigTextBuilder.toString()))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)

            notificationManager.notify(NOTIFICATION_ID_CENTER, builder.build())
        } catch (e: Exception) {
            // Notifications may be restricted
        }
    }
}
