package org.example.collrecord.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.example.collrecord.data.MockWorkingPaperRepository
import org.example.collrecord.data.WorkingPaperRepository
import org.example.collrecord.model.WorkingPaper

class WorkingPaperViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: WorkingPaperRepository = MockWorkingPaperRepository()

    private val _taskList = MutableStateFlow<List<WorkingPaper>>(emptyList())
    val taskList: StateFlow<List<WorkingPaper>> = _taskList

    // Lazy-load sederhana: semua data mock udah ada di memory, tapi cuma "dibuka" 10 per halaman
    // biar UX-nya kayak infinite scroll — nanti kalau backend asli sudah ada tinggal ganti
    // loadMore() supaya fetch page berikutnya dari API, tanpa ubah UI.
    private val _visibleCount = MutableStateFlow(PAGE_SIZE)
    val visibleCount: StateFlow<Int> = _visibleCount

    init {
        viewModelScope.launch {
            _taskList.value = repository.getTaskList()
        }
    }

    fun loadMore() {
        val total = _taskList.value.size
        _visibleCount.value = (_visibleCount.value + PAGE_SIZE).coerceAtMost(total)
    }

    companion object {
        private const val PAGE_SIZE = 10
    }
}
