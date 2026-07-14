package org.example.collrecord.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import org.example.collrecord.model.WorkingPaper
import org.example.collrecord.ui.RecordingScreen
import org.example.collrecord.ui.WorkingPaperDetailScreen
import org.example.collrecord.ui.WorkingPaperListScreen
import org.example.collrecord.viewmodel.WorkingPaperViewModel

// Navigasi sederhana pakai state, tanpa dependency navigation-compose tambahan.
// Cukup buat 3 layar phase 1; upgrade ke Navigation library kalau alurnya makin banyak.
private sealed class Screen {
    object List : Screen()
    data class Detail(val task: WorkingPaper) : Screen()
    // instanceId dibuat baru tiap kali user masuk ke Recording screen (termasuk task yang sama
    // dibuka ulang), supaya RecordingViewModel selalu fresh dan nggak kebawa state lama
    // (bug: state "Uploaded" dari task sebelumnya bikin task berikutnya langsung ke-bounce balik).
    data class Recording(val task: WorkingPaper, val instanceId: Long = System.nanoTime()) : Screen()
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
                onTaskSelected = { task -> currentScreen = Screen.Detail(task) }
            )
        }
        is Screen.Detail -> {
            WorkingPaperDetailScreen(
                task = screen.task,
                onBack = { currentScreen = Screen.List },
                onStartVisit = { currentScreen = Screen.Recording(screen.task) }
            )
        }
        is Screen.Recording -> {
            RecordingScreen(
                task = screen.task,
                instanceKey = screen.instanceId.toString(),
                onFinished = { currentScreen = Screen.List },
                onBack = { currentScreen = Screen.Detail(screen.task) }
            )
        }
    }
}
