package com.example.data.services

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class SalawatAlert(
    val id: Long = 0,
    val textAr: String = "اللَّهُمَّ صَلِّ وَسَلِّمْ عَلَى نَبِيِّنَا مُحَمَّدٍ ﷺ",
    val isVisible: Boolean = false,
    val soundName: String = "صوت التلاوة الذكية"
)

class SalawatReminderManager(
    private val context: Context,
    private val audioPlayerManager: AudioPlayerManager,
    private val smartVoiceManager: SmartVoiceManager
) {
    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private var reminderJob: Job? = null

    private val _currentAlert = MutableStateFlow(SalawatAlert())
    val currentAlert: StateFlow<SalawatAlert> = _currentAlert.asStateFlow()

    private val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator

    fun startSalawatTimer(intervalMinutes: Int, soundType: String = "tts_calm") {
        stopSalawatTimer()
        if (intervalMinutes <= 0) return

        reminderJob = scope.launch {
            while (isActive) {
                delay(intervalMinutes * 60 * 1000L)
                triggerSalawatNotification(soundType)
            }
        }
    }

    fun triggerSalawatNotification(soundType: String = "tts_calm") {
        val soundLabel = when (soundType) {
            "tts_calm" -> "التلاوة الذكية الهادئة"
            "tts_reverent" -> "صوت خاشع وندي"
            "chime_madinah" -> "نغمة الروضة الشريفة"
            "chime_gentle" -> "رنين إسلامي عذب"
            "vibrate_only" -> "اهتزاز لطيف"
            else -> "صوت التلاوة الذكية"
        }

        _currentAlert.value = SalawatAlert(
            id = System.currentTimeMillis(),
            textAr = "اللَّهُمَّ صَلِّ وَسَلِّمْ وَبَارِكْ عَلَى نَبِيِّنَا مُحَمَّدٍ ﷺ",
            isVisible = true,
            soundName = soundLabel
        )

        // Play the chosen sound or voice
        smartVoiceManager.playSalawatReminder(soundType)

        // Auto dismiss banner after 12 seconds
        scope.launch {
            delay(12000)
            if (_currentAlert.value.isVisible) {
                _currentAlert.value = _currentAlert.value.copy(isVisible = false)
            }
        }
    }

    fun dismissAlert() {
        _currentAlert.value = _currentAlert.value.copy(isVisible = false)
    }

    fun stopSalawatTimer() {
        reminderJob?.cancel()
        reminderJob = null
    }
}
