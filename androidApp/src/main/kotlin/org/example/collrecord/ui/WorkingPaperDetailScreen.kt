package org.example.collrecord.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.example.collrecord.data.RecordingStorage
import org.example.collrecord.model.WorkingPaper
import org.example.collrecord.platform.PlatformContext
import org.example.collrecord.playback.AudioPlayer
import org.example.collrecord.ui.theme.CollectorBlue
import org.example.collrecord.ui.theme.accentColorFor

@Composable
fun WorkingPaperDetailScreen(
    task: WorkingPaper,
    onBack: () -> Unit,
    onStartVisit: () -> Unit
) {
    val context = LocalContext.current
    val accent = accentColorFor(task.businessUnit)

    val player = remember { AudioPlayer(PlatformContext(context.applicationContext)) }
    var isPlaying by remember { mutableStateOf(false) }
    var recordingPath by remember { mutableStateOf<String?>(null) }

    // Cek ulang status rekaman tiap kali task-nya beda (buka detail debitur lain).
    LaunchedEffect(task.taskId) {
        player.stop()
        isPlaying = false
        recordingPath = RecordingStorage.listRecordings(context)
            .find { it.taskId == task.taskId }
            ?.filePath
    }

    // Stop playback otomatis begitu keluar dari halaman Detail ini (balik ke Daftar, lanjut Rekam, dll).
    DisposableEffect(Unit) {
        onDispose { player.stop() }
    }

    fun togglePlay() {
        val path = recordingPath ?: return
        if (isPlaying) {
            player.stop()
            isPlaying = false
        } else {
            player.play(path) { isPlaying = false }
            isPlaying = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    TextButton(onClick = {
                        player.stop()
                        onBack()
                    }) { Text("Tutup") }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    player.stop()
                    onStartVisit()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CollectorBlue)
            ) {
                Text(if (recordingPath == null) "Mulai Kunjungan & Rekam" else "Rekam Ulang Kunjungan")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(
                    modifier = Modifier
                        .width(4.dp)
                        .height(28.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(accent.solid)
                ) {}
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    task.debiturName,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(accent.solid)
                ) {}
            }

            Spacer(modifier = Modifier.height(24.dp))

            InfoRow(
                icon = "🕒",
                primary = "Jatuh Tempo: ${task.dueDate}",
                secondary = "Overdue ${task.overdue} hari • Tenor ${task.tenor} bulan"
            )
            HorizontalDivider()
            InfoRow(
                icon = "📍",
                primary = task.unit,
                secondary = task.businessUnit
            )
            HorizontalDivider()
            InfoRow(
                icon = "📄",
                primary = "No. Kontrak: ${task.noContract}",
                secondary = "Customer ID: ${task.customerId}"
            )
            HorizontalDivider()
            InfoRow(
                icon = "💰",
                primary = "Total Tagihan: ${formatRupiah(task.total)}",
                secondary = "Billing ${formatRupiah(task.billing)} • Denda ${formatRupiah(task.denda)}"
            )
            HorizontalDivider()

            if (recordingPath != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🎙️", modifier = Modifier.width(32.dp))
                    Column {
                        Text("Rekaman Kunjungan", fontWeight = FontWeight.Medium)
                        Text("Tersimpan di device", style = MaterialTheme.typography.bodySmall)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { togglePlay() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isPlaying) "Stop" else "Play Rekaman")
                }
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun InfoRow(icon: String, primary: String, secondary: String? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(icon, modifier = Modifier.width(32.dp))
        Column {
            Text(primary, fontWeight = FontWeight.Medium)
            if (secondary != null) {
                Text(secondary, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

private fun formatRupiah(amount: Long): String =
    "Rp" + amount.toString().reversed().chunked(3).joinToString(".").reversed()
