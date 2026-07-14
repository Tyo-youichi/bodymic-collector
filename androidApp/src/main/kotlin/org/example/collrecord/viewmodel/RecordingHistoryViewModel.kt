package org.example.collrecord.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.example.collrecord.data.MockWorkingPaperRepository
import org.example.collrecord.data.RecordingFile
import org.example.collrecord.data.RecordingStorage
import org.example.collrecord.model.WorkingPaper
import org.example.collrecord.platform.PlatformContext
import org.example.collrecord.playback.AudioPlayer

data class RecordingListItem(
    val recording: RecordingFile,
    val workingPaper: WorkingPaper?
)

class RecordingHistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val player = AudioPlayer(PlatformContext(application))
    private val workingPaperRepository = MockWorkingPaperRepository()

    private val _items = MutableStateFlow<List<RecordingListItem>>(emptyList())
    val items: StateFlow<List<RecordingListItem>> = _items

    private val _playingTaskId = MutableStateFlow<String?>(null)
    val playingTaskId: StateFlow<String?> = _playingTaskId

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val recordings = RecordingStorage.listRecordings(getApplication())
            val papers = workingPaperRepository.getTaskList()
            _items.value = recordings.map { rec ->
                RecordingListItem(rec, papers.find { it.taskId == rec.taskId })
            }
        }
    }

    fun togglePlay(recording: RecordingFile) {
        if (_playingTaskId.value == recording.taskId) {
            player.stop()
            _playingTaskId.value = null
        } else {
            player.play(recording.filePath) {
                _playingTaskId.value = null
            }
            _playingTaskId.value = recording.taskId
        }
    }

    override fun onCleared() {
        super.onCleared()
        player.stop()
    }
}
