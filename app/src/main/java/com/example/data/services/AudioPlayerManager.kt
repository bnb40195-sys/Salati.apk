package com.example.data.services

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class PlaybackState(
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val currentTitle: String = "",
    val currentSubtitle: String = "",
    val currentUrl: String = "",
    val currentPositionMs: Int = 0,
    val durationMs: Int = 0,
    val errorMessage: String? = null
)

class AudioPlayerManager(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private var progressJob: Job? = null

    private val _playbackState = MutableStateFlow(PlaybackState())
    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    fun playAudio(url: String, title: String, subtitle: String = "") {
        try {
            stopAudio()

            _playbackState.value = PlaybackState(
                isBuffering = true,
                currentTitle = title,
                currentSubtitle = subtitle,
                currentUrl = url
            )

            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(url)
                setOnPreparedListener { mp ->
                    mp.start()
                    _playbackState.value = _playbackState.value.copy(
                        isPlaying = true,
                        isBuffering = false,
                        durationMs = mp.duration
                    )
                    startProgressTracker()
                }
                setOnCompletionListener {
                    _playbackState.value = _playbackState.value.copy(
                        isPlaying = false,
                        currentPositionMs = 0
                    )
                    stopProgressTracker()
                }
                setOnErrorListener { _, what, extra ->
                    Log.e("AudioPlayerManager", "MediaPlayer error: what=$what, extra=$extra")
                    _playbackState.value = _playbackState.value.copy(
                        isPlaying = false,
                        isBuffering = false,
                        errorMessage = "تعذر تشغيل الصوت (تأكد من الاتصال بالإنترنت)"
                    )
                    true
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            Log.e("AudioPlayerManager", "Exception playing audio", e)
            _playbackState.value = _playbackState.value.copy(
                isPlaying = false,
                isBuffering = false,
                errorMessage = "خطأ في تشغيل الصوت"
            )
        }
    }

    fun togglePlayPause() {
        mediaPlayer?.let { mp ->
            if (mp.isPlaying) {
                mp.pause()
                _playbackState.value = _playbackState.value.copy(isPlaying = false)
            } else {
                mp.start()
                _playbackState.value = _playbackState.value.copy(isPlaying = true)
                startProgressTracker()
            }
        }
    }

    fun seekTo(positionMs: Int) {
        mediaPlayer?.seekTo(positionMs)
        _playbackState.value = _playbackState.value.copy(currentPositionMs = positionMs)
    }

    fun stopAudio() {
        stopProgressTracker()
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (e: Exception) {
            // ignore
        }
        mediaPlayer = null
        _playbackState.value = PlaybackState()
    }

    private fun startProgressTracker() {
        stopProgressTracker()
        progressJob = scope.launch {
            while (isActive) {
                mediaPlayer?.let { mp ->
                    if (mp.isPlaying) {
                        _playbackState.value = _playbackState.value.copy(
                            currentPositionMs = mp.currentPosition,
                            durationMs = mp.duration
                        )
                    }
                }
                delay(500)
            }
        }
    }

    private fun stopProgressTracker() {
        progressJob?.cancel()
        progressJob = null
    }

    fun release() {
        stopAudio()
    }
}
