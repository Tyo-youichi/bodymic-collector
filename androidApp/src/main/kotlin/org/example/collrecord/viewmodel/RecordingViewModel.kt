package org.example.collrecord.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.example.collrecord.location.LocationProvider
import org.example.collrecord.platform.PlatformContext
import org.example.collrecord.recording.AudioRecorder
import org.example.collrecord.upload.UploadWorker
import java.io.File

sealed class RecordingUiState {
    object Idle : RecordingUiState()
    object Recording : RecordingUiState()
    object Finishing : RecordingUiState()
    object Uploaded : RecordingUiState()
    data class Error(val message: String) : RecordingUiState()
}

class RecordingViewModel(application: Application) : AndroidViewModel(application) {
    private val platformContext = PlatformContext(application)
    private val recorder = AudioRecorder(platformContext)
    private val locationProvider = LocationProvider(platformContext)

    private val _uiState = MutableStateFlow<RecordingUiState>(RecordingUiState.Idle)
    val uiState: StateFlow<RecordingUiState> = _uiState

    fun startRecording(taskId: String) {
        try {
            recorder.startRecording(taskId)
            _uiState.value = RecordingUiState.Recording
        } catch (e: Exception) {
            _uiState.value = RecordingUiState.Error("Gagal mulai rekam: ${e.message}")
        }
    }

    /** Stop recording, ambil koordinat, lalu enqueue background upload via WorkManager. */
    fun finishVisit(taskId: String) {
        _uiState.value = RecordingUiState.Finishing
        viewModelScope.launch {
            val filePath = recorder.stopRecording()
            if (filePath == null) {
                _uiState.value = RecordingUiState.Error("File rekaman tidak ditemukan")
                return@launch
            }

            val coordinate = locationProvider.getCurrentLocation()
            if (coordinate == null) {
                _uiState.value = RecordingUiState.Error("Gagal ambil lokasi")
                return@launch
            }

            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val uploadRequest = OneTimeWorkRequestBuilder<UploadWorker>()
                .setInputData(
                    UploadWorker.buildInputData(
                        taskId = taskId,
                        filePath = filePath,
                        lat = coordinate.latitude,
                        lng = coordinate.longitude
                    )
                )
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(getApplication()).enqueue(uploadRequest)
            _uiState.value = RecordingUiState.Uploaded
        }
    }

    /** Dipanggil kalau user tekan Back sebelum/saat rekam — batalin & buang file, jangan di-upload. */
    fun cancelRecording() {
        val filePath = recorder.stopRecording()
        filePath?.let { File(it).delete() }
    }
}
