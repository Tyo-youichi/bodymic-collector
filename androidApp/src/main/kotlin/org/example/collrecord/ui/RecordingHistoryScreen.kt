package org.example.collrecord.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.example.collrecord.viewmodel.RecordingHistoryViewModel
import org.example.collrecord.viewmodel.RecordingListItem

@Composable
fun RecordingHistoryScreen(
    onBack: () -> Unit,
    viewModel: RecordingHistoryViewModel = viewModel()
) {
    val items by viewModel.items.collectAsState()
    val playingTaskId by viewModel.playingTaskId.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Riwayat Rekaman") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Kembali") } }
            )
        }
    ) { padding ->
        if (items.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Belum ada rekaman tersimpan")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(items) { item ->
                    RecordingRow(
                        item = item,
                        isPlaying = playingTaskId == item.recording.taskId,
                        onToggle = { viewModel.togglePlay(item.recording) }
                    )
                }
            }
        }
    }
}

@Composable
private fun RecordingRow(
    item: RecordingListItem,
    isPlaying: Boolean,
    onToggle: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                item.workingPaper?.debiturName ?: item.recording.taskId,
                style = MaterialTheme.typography.titleMedium
            )
            Text("Task: ${item.recording.taskId}")
            Text("Ukuran: ${item.recording.fileSizeBytes / 1024} KB")
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onToggle) {
                Text(if (isPlaying) "Stop" else "Play")
            }
        }
    }
}
