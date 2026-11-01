package com.example.data.services

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

sealed class SpeechRecognitionState {
    object Idle : SpeechRecognitionState()
    object Ready : SpeechRecognitionState()
    data class Listening(val rmsDb: Float) : SpeechRecognitionState()
    data class Processing(val partialText: String) : SpeechRecognitionState()
    data class Success(val recognizedText: String) : SpeechRecognitionState()
    data class Error(val message: String, val canRetry: Boolean = true) : SpeechRecognitionState()
}

class QuranSpeechRecognizer(private val context: Context) {

    private var speechRecognizer: SpeechRecognizer? = null

    private val _state = MutableStateFlow<SpeechRecognitionState>(SpeechRecognitionState.Idle)
    val state: StateFlow<SpeechRecognitionState> = _state.asStateFlow()

    private val _partialResults = MutableStateFlow("")
    val partialResults: StateFlow<String> = _partialResults.asStateFlow()

    fun isRecognitionAvailable(): Boolean {
        return SpeechRecognizer.isRecognitionAvailable(context)
    }

    private fun createRecognizer(): SpeechRecognizer {
        speechRecognizer?.destroy()
        val recognizer = SpeechRecognizer.createSpeechRecognizer(context)
        recognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                _state.value = SpeechRecognitionState.Ready
            }

            override fun onBeginningOfSpeech() {
                _state.value = SpeechRecognitionState.Listening(0f)
            }

            override fun onRmsChanged(rmsdB: Float) {
                if (_state.value is SpeechRecognitionState.Listening) {
                    _state.value = SpeechRecognitionState.Listening(rmsdB)
                }
            }

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                _state.value = SpeechRecognitionState.Processing(_partialResults.value)
            }

            override fun onError(error: Int) {
                val errorMsg = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "خطأ في تسجيل الصوت، تأكد من سلامة الميكروفون"
                    SpeechRecognizer.ERROR_CLIENT -> "خطأ داخلي في نظام التعرف على الصوت"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "لم يتم منح إذن الميكروفون"
                    SpeechRecognizer.ERROR_NETWORK, SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "خطأ في الاتصال بالشبكة لمعالجة الصوت"
                    SpeechRecognizer.ERROR_NO_MATCH -> "لم يتم التعرف على كلمات واضحة، حاول التلاوة بصوت أعلى وأقرب للميكروفون"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "محرك الصوت مشغول حالياً، أعد المحاولة"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "انتهى الوقت دون سماع صوت، اضغط الزر واقرأ الآية مباشرة"
                    else -> "تعذر التعرف على الصوت، يمكنك إعادة المحاولة"
                }
                _state.value = SpeechRecognitionState.Error(errorMsg)
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val text = matches?.firstOrNull() ?: _partialResults.value
                if (text.isNotBlank()) {
                    _state.value = SpeechRecognitionState.Success(text)
                } else {
                    _state.value = SpeechRecognitionState.Error("لم يتم التقاط أي صوت، يرجى المحاولة ثانية")
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val text = matches?.firstOrNull() ?: ""
                if (text.isNotBlank()) {
                    _partialResults.value = text
                    _state.value = SpeechRecognitionState.Processing(text)
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
        return recognizer
    }

    fun startListening() {
        try {
            _partialResults.value = ""
            speechRecognizer?.destroy()
            speechRecognizer = createRecognizer()

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ar-SA")
                putExtra("android.speech.extra.EXTRA_ADDITIONAL_LANGUAGES", arrayOf("ar", "ar-EG", "ar-AE", "ar-MA"))
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
                putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
            }

            speechRecognizer?.startListening(intent)
        } catch (e: Exception) {
            Log.e("QuranSpeechRecognizer", "Error starting listening", e)
            _state.value = SpeechRecognitionState.Error("تعذر تشغيل الميكروفون: ${e.localizedMessage}")
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
        } catch (e: Exception) {
            Log.e("QuranSpeechRecognizer", "Error stopping listening", e)
        }
    }

    fun cancel() {
        try {
            speechRecognizer?.cancel()
            _state.value = SpeechRecognitionState.Idle
        } catch (e: Exception) {
            Log.e("QuranSpeechRecognizer", "Error canceling", e)
        }
    }

    fun reset() {
        _partialResults.value = ""
        _state.value = SpeechRecognitionState.Idle
    }

    fun destroy() {
        try {
            speechRecognizer?.destroy()
            speechRecognizer = null
        } catch (e: Exception) {
            Log.e("QuranSpeechRecognizer", "Error destroying", e)
        }
    }
}
