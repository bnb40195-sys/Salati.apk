package com.example.data.services

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.sin

class SmartVoiceManager(private val context: Context) {

    private var tts: TextToSpeech? = null
    private var isTtsReady = false
    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _currentSpokenAyahNumber = MutableStateFlow<Int?>(null)
    val currentSpokenAyahNumber: StateFlow<Int?> = _currentSpokenAyahNumber.asStateFlow()

    private var onCompletionCallback: (() -> Unit)? = null

    init {
        initializeTts()
    }

    private fun initializeTts() {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // Try Arabic locale first
                val arabicLocale = Locale("ar")
                val result = tts?.setLanguage(arabicLocale)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    // Fallback to default
                    tts?.language = Locale.getDefault()
                }
                tts?.setPitch(0.95f)
                tts?.setSpeechRate(0.85f) // Calm, measured Quranic recitation pace

                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        _isSpeaking.value = true
                    }

                    override fun onDone(utteranceId: String?) {
                        _isSpeaking.value = false
                        _currentSpokenAyahNumber.value = null
                        scope.launch {
                            onCompletionCallback?.invoke()
                        }
                    }

                    override fun onError(utteranceId: String?) {
                        _isSpeaking.value = false
                        _currentSpokenAyahNumber.value = null
                    }
                })
                isTtsReady = true
            }
        }
    }

    /**
     * Smart offline Quranic recitation of a specific Ayah text
     */
    fun reciteAyahOffline(
        ayahNumber: Int,
        ayahText: String,
        speechRate: Float = 0.85f,
        onDone: () -> Unit = {}
    ) {
        stop()
        onCompletionCallback = onDone
        _currentSpokenAyahNumber.value = ayahNumber

        if (isTtsReady && tts != null) {
            tts?.setSpeechRate(speechRate)
            val params = Bundle()
            params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "ayah_$ayahNumber")
            tts?.speak(cleanArabicForTts(ayahText), TextToSpeech.QUEUE_FLUSH, params, "ayah_$ayahNumber")
        } else {
            // Play gentle synthesized chime if TTS not yet ready
            playHarmonicTone(440.0, 600)
            scope.launch {
                kotlinx.coroutines.delay(800)
                _isSpeaking.value = false
                _currentSpokenAyahNumber.value = null
                onDone()
            }
        }
    }

    /**
     * Recite the Pre-Prayer reminder verse:
     * "إن الصلاة كانت على المؤمنين كتاباً موقوتا - صدق الله العظيم"
     */
    fun playPrePrayerReminder(soundType: String = "tts_verse") {
        stop()
        vibrateGentle()

        when (soundType) {
            "chime_soft" -> {
                playHarmonicTone(528.0, 1200)
            }
            "chime_makkah" -> {
                playArpeggioTone(listOf(440.0, 554.37, 659.25, 880.0))
            }
            else -> {
                // Arabic voice recitation
                val reminderText = "إِنَّ الصَّلَاةَ كَانَتْ عَلَى الْمُؤْمِنِينَ كِتَابًا مَوْقُوتًا. صَدَقَ اللهُ الْعَظِيمُ. اقْتَرَبَ وَقْتُ الصَّلَاةِ، تَهَيَّأْ لِلْوُقُوفِ بَيْنَ يَدَيْ اللهِ."
                if (isTtsReady && tts != null) {
                    tts?.setPitch(0.92f)
                    tts?.setSpeechRate(0.82f)
                    val params = Bundle()
                    params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "pre_prayer")
                    tts?.speak(reminderText, TextToSpeech.QUEUE_FLUSH, params, "pre_prayer")
                } else {
                    playArpeggioTone(listOf(528.0, 660.0, 792.0))
                }
            }
        }
    }

    /**
     * Recite Salawat on the Prophet ﷺ with chosen sound type
     */
    fun playSalawatReminder(soundType: String = "tts_calm") {
        stop()
        vibrateGentle()

        when (soundType) {
            "vibrate_only" -> {
                // Vibration already triggered
            }
            "chime_gentle" -> {
                playHarmonicTone(660.0, 900)
            }
            "chime_madinah" -> {
                playArpeggioTone(listOf(523.25, 659.25, 783.99, 1046.50))
            }
            "tts_ibrahimi" -> {
                val salawatText = "اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ وَعَلَى آلِ مُحَمَّدٍ، كَمَا صَلَّيْتَ عَلَى إِبْرَاهِيمَ وَعَلَى آلِ إِبْرَاهِيمَ، إِنَّكَ حَمِيدٌ مَجِيدٌ."
                if (isTtsReady && tts != null) {
                    tts?.setPitch(0.90f)
                    tts?.setSpeechRate(0.82f)
                    tts?.speak(salawatText, TextToSpeech.QUEUE_FLUSH, null, "salawat_ibrahimi")
                } else {
                    playArpeggioTone(listOf(440.0, 528.0, 660.0, 880.0))
                }
            }
            "voice_haram_makkah" -> {
                val salawatText = "الصَّلَاةُ وَالسَّلَامُ عَلَيْكَ يَا رَسُولَ اللهِ. اللَّهُمَّ صَلِّ وَسَلِّمْ عَلَى نَبِيِّنَا مُحَمَّدٍ."
                if (isTtsReady && tts != null) {
                    tts?.setPitch(0.92f)
                    tts?.setSpeechRate(0.78f)
                    tts?.speak(salawatText, TextToSpeech.QUEUE_FLUSH, null, "salawat_haram")
                } else {
                    playArpeggioTone(listOf(523.25, 659.25, 783.99))
                }
            }
            "voice_madinah_nabawi" -> {
                val salawatText = "يَا طَيِّبَةَ الْخَيْرِ، اللَّهُمَّ صَلِّ وَسَلِّمْ وَبَارِكْ عَلَى نَبِيِّ الرَّحْمَةِ سَيِّدِنَا مُحَمَّدٍ."
                if (isTtsReady && tts != null) {
                    tts?.setPitch(0.98f)
                    tts?.setSpeechRate(0.82f)
                    tts?.speak(salawatText, TextToSpeech.QUEUE_FLUSH, null, "salawat_madinah")
                } else {
                    playHarmonicTone(587.33, 1000)
                }
            }
            "tts_reverent" -> {
                val salawatText = "اللَّهُمَّ صَلِّ وَسَلِّمْ وَبَارِكْ عَلَى سَيِّدِنَا وَنَبِيِّنَا مُحَمَّدٍ، وَعَلَى آلِهِ وَصَحْبِهِ أَجْمَعِينَ."
                if (isTtsReady && tts != null) {
                    tts?.setPitch(0.88f)
                    tts?.setSpeechRate(0.80f)
                    tts?.speak(salawatText, TextToSpeech.QUEUE_FLUSH, null, "salawat_reverent")
                } else {
                    playArpeggioTone(listOf(440.0, 528.0, 660.0))
                }
            }
            else -> { // "tts_calm"
                val salawatText = "اللَّهُمَّ صَلِّ وَسَلِّمْ وَبَارِكْ عَلَى نَبِيِّنَا مُحَمَّدٍ ﷺ."
                if (isTtsReady && tts != null) {
                    tts?.setPitch(0.95f)
                    tts?.setSpeechRate(0.85f)
                    tts?.speak(salawatText, TextToSpeech.QUEUE_FLUSH, null, "salawat_calm")
                } else {
                    playHarmonicTone(528.0, 800)
                }
            }
        }
    }

    /**
     * Read any Dhikr out loud
     */
    fun speakDhikr(dhikrText: String) {
        stop()
        if (isTtsReady && tts != null) {
            tts?.setSpeechRate(0.85f)
            tts?.speak(cleanArabicForTts(dhikrText), TextToSpeech.QUEUE_FLUSH, null, "dhikr")
        } else {
            playHarmonicTone(440.0, 400)
        }
    }

    fun stop() {
        try {
            tts?.stop()
        } catch (e: Exception) {
            // ignore
        }
        _isSpeaking.value = false
        _currentSpokenAyahNumber.value = null
    }

    fun release() {
        stop()
        try {
            tts?.shutdown()
        } catch (e: Exception) {
            // ignore
        }
        tts = null
        isTtsReady = false
    }

    private fun vibrateGentle() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 120, 80, 120), -1))
            } else {
                vibrator?.vibrate(200)
            }
        } catch (e: Exception) {
            // ignore
        }
    }

    /**
     * Cleans verse text for smooth TTS pronunciation (removes ornate brackets and verse markers)
     */
    private fun cleanArabicForTts(text: String): String {
        return text
            .replace(Regex("[﴿﴾۝۞]"), "")
            .replace(Regex("[0-9٠-٩]+"), "")
            .trim()
    }

    /**
     * Generates a pure harmonic sine wave chime (100% offline via AudioTrack)
     */
    private fun playHarmonicTone(freqHz: Double, durationMs: Int) {
        scope.launch(Dispatchers.Default) {
            try {
                val sampleRate = 44100
                val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
                val buffer = ShortArray(numSamples)

                for (i in 0 until numSamples) {
                    val time = i.toDouble() / sampleRate
                    // Apply smooth envelope (fade out at end)
                    val envelope = (1.0 - (i.toDouble() / numSamples)).coerceIn(0.0, 1.0)
                    // Primary tone + gentle overtone
                    val wave = (sin(2.0 * Math.PI * freqHz * time) * 0.75 +
                            sin(2.0 * Math.PI * (freqHz * 2) * time) * 0.25) * envelope
                    buffer[i] = (wave * Short.MAX_VALUE).toInt().toShort()
                }

                val audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setSampleRate(sampleRate)
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(buffer.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                audioTrack.write(buffer, 0, buffer.size)
                audioTrack.play()
                kotlinx.coroutines.delay(durationMs.toLong() + 100)
                audioTrack.release()
            } catch (e: Exception) {
                Log.e("SmartVoiceManager", "Error playing tone", e)
            }
        }
    }

    /**
     * Generates an arpeggio chord of tones
     */
    private fun playArpeggioTone(frequencies: List<Double>) {
        scope.launch(Dispatchers.Default) {
            frequencies.forEach { freq ->
                playHarmonicTone(freq, 350)
                kotlinx.coroutines.delay(180)
            }
        }
    }
}
