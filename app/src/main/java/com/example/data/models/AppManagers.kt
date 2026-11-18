package com.example.data.models

import com.example.data.local.AppPreferences
import com.example.data.local.CustomContentManager
import com.example.data.local.IslamicRepository
import com.example.data.services.*

data class AppManagers(
    val appPreferences: AppPreferences,
    val repository: IslamicRepository,
    val customContentManager: CustomContentManager,
    val audioPlayerManager: AudioPlayerManager,
    val compassSensorManager: CompassSensorManager,
    val weatherService: WeatherService,
    val smartVoiceManager: SmartVoiceManager,
    val prayerNotificationManager: PrayerNotificationManager,
    val salawatReminderManager: SalawatReminderManager
)
