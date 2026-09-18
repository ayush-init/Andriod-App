package com.roxstar.audio.ui.studio

import android.app.Application
import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.roxstar.audio.bridge.NativeAudioBridge
import com.roxstar.audio.data.model.VoiceDraft
import com.roxstar.audio.data.repository.DraftRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File

sealed class StudioRecordingState {
    object Idle : StudioRecordingState()
    data class Recording(val durationMs: Long, val audioLevel: Float) : StudioRecordingState()
    data class Stopped(val outputFile: File, val durationMs: Long, val effectApplied: String) : StudioRecordingState()
}

enum class AudioEffectMode(val label: String, val nativeMode: Int) {
    CLEAN("Clean", 0),
    ECHO("Echo", 1),
    REVERB("Reverb", 2)
}

class StudioViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "StudioViewModel"
    private val repository = DraftRepository(application.applicationContext)

    val draftsFlow = repository.draftsFlow

    private val _recordingState = MutableStateFlow<StudioRecordingState>(StudioRecordingState.Idle)
    val recordingState: StateFlow<StudioRecordingState> = _recordingState.asStateFlow()

    private val _effectMode = MutableStateFlow(AudioEffectMode.ECHO)
    val effectMode: StateFlow<AudioEffectMode> = _effectMode.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    // Playback state
    private var mediaPlayer: MediaPlayer? = null
    private val _playingDraftId = MutableStateFlow<String?>(null)
    val playingDraftId: StateFlow<String?> = _playingDraftId.asStateFlow()

    private var currentOutputFile: File? = null
    private var recordingTickerJob: Job? = null

    init {
        NativeAudioBridge.setEffectMode(AudioEffectMode.ECHO.nativeMode)
    }

    fun setEffectMode(mode: AudioEffectMode) {
        _effectMode.value = mode
        NativeAudioBridge.setEffectMode(mode.nativeMode)
        _statusMessage.value = "${mode.label} DSP enabled for the next recording"
        Log.i(TAG, "Set audio effect: ${mode.label}")
    }

    fun startRecording(): Boolean {
        if (!NativeAudioBridge.isNativeLoaded()) {
            _errorMessage.value = "Native audio library not loaded"
            return false
        }

        stopPlayback()

        val outputFile = repository.createOutputFile()
        currentOutputFile = outputFile

        val success = NativeAudioBridge.startRecording(outputFile.absolutePath)
        if (!success) {
            _errorMessage.value = "Failed to start native recording stream"
            return false
        }

        _statusMessage.value = "Recording started with ${_effectMode.value.label} DSP"
        _recordingState.value = StudioRecordingState.Recording(0L, 0.0f)

        // Launch duration & level ticker
        recordingTickerJob?.cancel()
        recordingTickerJob = viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            while (isActive && NativeAudioBridge.isRecording()) {
                val duration = System.currentTimeMillis() - startTime
                val level = NativeAudioBridge.getCurrentLevel()
                _recordingState.value = StudioRecordingState.Recording(duration, level)
                delay(50)
            }
        }
        return true
    }

    fun stopRecording() {
        recordingTickerJob?.cancel()
        recordingTickerJob = null

        val durationMs = NativeAudioBridge.getRecordingDurationMs()
        val success = NativeAudioBridge.stopRecording()

        val file = currentOutputFile
        if (success && file != null && file.exists() && file.length() > 44) {
            val effect = _effectMode.value.label.uppercase()
            _recordingState.value = StudioRecordingState.Stopped(file, durationMs, effect)
            _statusMessage.value = "Recording complete — ready to save your ${effect.lowercase()} take"
        } else {
            _recordingState.value = StudioRecordingState.Idle
            if (file != null && file.exists()) {
                file.delete()
            }
        }
    }

    fun cancelRecording() {
        recordingTickerJob?.cancel()
        recordingTickerJob = null
        NativeAudioBridge.cancelRecording()
        currentOutputFile = null
        _recordingState.value = StudioRecordingState.Idle
    }

    fun saveDraft(title: String) {
        val state = _recordingState.value
        if (state is StudioRecordingState.Stopped) {
            viewModelScope.launch {
                val draftTitle = if (title.isBlank()) "Take ${System.currentTimeMillis()}" else title.trim()
                val draft = VoiceDraft(
                    title = draftTitle,
                    filePath = state.outputFile.absolutePath,
                    durationMs = state.durationMs,
                    effectApplied = state.effectApplied
                )
                repository.saveDraft(draft)
                _recordingState.value = StudioRecordingState.Idle
                _statusMessage.value = "Draft \"$draftTitle\" saved locally"
            }
        }
    }

    fun discardStoppedTake() {
        val state = _recordingState.value
        if (state is StudioRecordingState.Stopped) {
            if (state.outputFile.exists()) {
                state.outputFile.delete()
            }
            _recordingState.value = StudioRecordingState.Idle
            _statusMessage.value = "Recording discarded"
        }
    }

    fun togglePlayback(draft: VoiceDraft) {
        if (_playingDraftId.value == draft.id) {
            stopPlayback()
        } else {
            startPlayback(draft)
        }
    }

    private fun startPlayback(draft: VoiceDraft) {
        stopPlayback()
        try {
            val player = MediaPlayer().apply {
                setDataSource(draft.filePath)
                prepare()
                setOnCompletionListener {
                    stopPlayback()
                }
                start()
            }
            mediaPlayer = player
            _playingDraftId.value = draft.id
        } catch (e: Exception) {
            Log.e(TAG, "Playback failed for ${draft.filePath}", e)
            _errorMessage.value = "Failed to play audio draft"
            stopPlayback()
        }
    }

    fun stopPlayback() {
        mediaPlayer?.let {
            try {
                if (it.isPlaying) it.stop()
                it.release()
            } catch (e: Exception) {
                Log.w(TAG, "Error stopping media player", e)
            }
        }
        mediaPlayer = null
        _playingDraftId.value = null
    }

    fun deleteDraft(draftId: String) {
        if (_playingDraftId.value == draftId) {
            stopPlayback()
        }
        viewModelScope.launch {
            val deleted = repository.deleteDraft(draftId)
            _statusMessage.value = if (deleted) "Draft deleted" else "Draft could not be deleted"
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }

    override fun onCleared() {
        super.onCleared()
        stopPlayback()
        if (NativeAudioBridge.isRecording()) {
            NativeAudioBridge.cancelRecording()
        }
    }
}
