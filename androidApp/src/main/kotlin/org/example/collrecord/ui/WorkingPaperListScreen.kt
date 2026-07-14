package org.example.collrecord.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.example.collrecord.model.WorkingPaper
import org.example.collrecord.ui.theme.CollectorBlue
import org.example.collrecord.ui.theme.accentColorFor

private val MONTH_NAMES = listOf(
    "JAN", "FEB", "MAR", "APR", "MEI", "JUN", "JUL", "AGU", "SEP", "OKT", "NOV", "DES"
)

@Composable
fun WorkingPaperListScreen(
    taskList: List<WorkingPaper>,
    hasMore: Boolean,
    onLoadMore: () -> Unit,
    onTaskSelected: (WorkingPaper) -> Unit,
    listState: LazyListState
) {
    val grouped = taskList.groupBy { it.dueDate }.toSortedMap()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daftar Kunjungan", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CollectorBlue,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        containerColor = Color.White
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            grouped.forEach { (dueDate, tasksForDate) ->
                item { Spacer(modifier = Modifier.height(12.dp)) }
                item { DateSectionHeader(dueDate) }
                items(tasksForDate) { task ->
                    WorkingPaperCard(task = task, onClick = { onTaskSelected(task) })
                }
            }
            if (hasMore) {
                // Sentinel: begitu item ini kekompos (user scroll sampai row paling bawah),
                // trigger loadMore() buat "buka" 10 data berikutnya.
                item(key = "load_more_sentinel") {
                    LaunchedEffect(Unit) { onLoadMore() }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
private fun DateSectionHeader(dueDate: String) {
    val parts = dueDate.split("-")
    val day = parts.getOrNull(2) ?: dueDate
    val monthIndex = parts.getOrNull(1)?.toIntOrNull()?.minus(1)
    val monthName = monthIndex?.let { MONTH_NAMES.getOrNull(it) } ?: ""

    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.width(48.dp)) {
            Text(monthName, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Text(day, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun WorkingPaperCard(task: WorkingPaper, onClick: () -> Unit) {
    val accent = accentColorFor(task.businessUnit)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(accent.background)
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .width(4.dp)
                .height(40.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(accent.solid)
        ) {}
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(task.debiturName, fontWeight = FontWeight.SemiBold)
            Text(
                "${task.unit} • Overdue ${task.overdue} hari",
                style = MaterialTheme.typography.bodySmall,
                color = Color.DarkGray
            )
        }
    }
}
