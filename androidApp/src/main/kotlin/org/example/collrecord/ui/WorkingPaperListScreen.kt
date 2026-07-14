package org.example.collrecord.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.example.collrecord.model.WorkingPaper

@Composable
fun WorkingPaperListScreen(
    taskList: List<WorkingPaper>,
    onTaskSelected: (WorkingPaper) -> Unit,
    onHistoryClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daftar Kunjungan") },
                actions = { TextButton(onClick = onHistoryClick) { Text("Riwayat") } }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(taskList) { task ->
                WorkingPaperCard(task = task, onClick = { onTaskSelected(task) })
            }
        }
    }
}

@Composable
private fun WorkingPaperCard(task: WorkingPaper, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(task.debiturName, style = MaterialTheme.typography.titleMedium)
            Text("No. Kontrak: ${task.noContract}")
            Text("Unit: ${task.unit}")
            Text("Overdue: ${task.overdue} hari")
            Text("Total Tagihan: ${task.total}")
        }
    }
}
