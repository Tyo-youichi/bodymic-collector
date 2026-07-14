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

    init {
        viewModelScope.launch {
            _taskList.value = repository.getTaskList()
        }
    }
}
