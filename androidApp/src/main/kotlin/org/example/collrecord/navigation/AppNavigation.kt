package org.example.collrecord.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import org.example.collrecord.model.WorkingPaper
import org.example.collrecord.ui.RecordingHistoryScreen
import org.example.collrecord.ui.RecordingScreen
import org.example.collrecord.ui.WorkingPaperListScreen
import org.example.collrecord.viewmodel.WorkingPaperViewModel

// Navigasi sederhana pakai state, tanpa dependency navigation-compose tambahan.
// Cukup buat 3 layar phase 1; upgrade ke Navigation library kalau alurnya makin banyak.
private sealed class Screen {
    object List : Screen()
    data class Recording(val task: WorkingPaper) : Screen()
    object History : Screen()
}

@Composable
fun AppNavigation() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.List) }
    val workingPaperViewModel: WorkingPaperViewModel = viewModel()
    val taskList by workingPaperViewModel.taskList.collectAsState()

    when (val screen = currentScreen) {
        is Screen.List -> {
            WorkingPaperListScreen(
                taskList = taskList,
                onTaskSelected = { task -> currentScreen = Screen.Recording(task) },
                onHistoryClick = { currentScreen = Screen.History }
            )
        }
        is Screen.Recording -> {
            RecordingScreen(
                task = screen.task,
                onFinished = { currentScreen = Screen.List }
            )
        }
        is Screen.History -> {
            RecordingHistoryScreen(onBack = { currentScreen = Screen.List })
        }
    }
}
