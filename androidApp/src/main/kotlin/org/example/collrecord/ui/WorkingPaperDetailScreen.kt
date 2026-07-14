package org.example.collrecord.ui

import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import org.example.collrecord.data.RecordingStorage
import org.example.collrecord.model.WorkingPaper
import org.example.collrecord.platform.PlatformContext
import org.example.collrecord.playback.AudioPlayer
import org.example.collrecord.ui.theme.CollectorBlue
import org.example.collrecord.ui.theme.accentColorFor
import java.io.File

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
    var waveform by remember { mutableStateOf<List<Int>>(emptyList()) }
    var playbackProgress by remember { mutableStateOf(0f) }
    var durationMs by remember { mutableStateOf(0) }
    var recordingLocation by remember { mutableStateOf<Pair<Double, Double>?>(null) }

    // Cek ulang status rekaman tiap kali task-nya beda (buka detail debitur lain).
    LaunchedEffect(task.taskId) {
        player.stop()
        isPlaying = false
        playbackProgress = 0f
        val path = RecordingStorage.listRecordings(context)
            .find { it.taskId == task.taskId }
            ?.filePath
        recordingPath = path
        waveform = path?.let { p ->
            val waveFile = File(File(p).parentFile, "${File(p).nameWithoutExtension}.wave")
            if (waveFile.exists()) {
                waveFile.readText().split(",").mapNotNull { it.toIntOrNull() }
            } else {
                emptyList()
            }
        } ?: emptyList()
        durationMs = path?.let { getAudioDurationMs(it) } ?: 0
        recordingLocation = path?.let { p ->
            val locationFile = File(File(p).parentFile, "${File(p).nameWithoutExtension}.location")
            if (locationFile.exists()) {
                val parts = locationFile.readText().split(",")
                val lat = parts.getOrNull(0)?.toDoubleOrNull()
                val lng = parts.getOrNull(1)?.toDoubleOrNull()
                if (lat != null && lng != null) lat to lng else null
            } else {
                null
            }
        }
    }

    // Stop playback otomatis begitu keluar dari halaman Detail ini (balik ke Daftar, lanjut Rekam, dll).
    DisposableEffect(Unit) {
        onDispose { player.stop() }
    }

    // Sinkronisasi posisi playhead ke waveform selama sedang play.
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            val duration = player.durationMs()
            playbackProgress = if (duration > 0) player.currentPositionMs().toFloat() / duration else 0f
            delay(100)
        }
    }

    fun togglePlay() {
        val path = recordingPath ?: return
        if (isPlaying) {
            player.stop()
            isPlaying = false
            playbackProgress = 0f
        } else {
            player.play(path) {
                isPlaying = false
                playbackProgress = 0f
            }
            isPlaying = true
        }
    }

    // Geser/tap di waveform buat lompat ke posisi tertentu. Kalau lagi nggak play, mulai
    // playback langsung dari posisi yang di-drag.
    fun seekTo(fraction: Float) {
        val path = recordingPath ?: return
        if (durationMs <= 0) return
        val targetMs = (fraction * durationMs).toInt()
        if (!isPlaying) {
            player.play(path) {
                isPlaying = false
                playbackProgress = 0f
            }
            isPlaying = true
        }
        player.seekTo(targetMs)
        playbackProgress = fraction
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
                .verticalScroll(rememberScrollState())
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
                icon = "🏠",
                primary = task.alamat,
                secondary = "Alamat debitur"
            )
            HorizontalDivider()
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
                Text("Rekaman Kunjungan", fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            formatDurationMs((playbackProgress * durationMs).toInt()),
                            color = Color.White,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            formatDurationMs(durationMs),
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodySmall
                        )

                        if (waveform.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            WaveformBars(
                                amplitudes = waveform,
                                progress = if (isPlaying) playbackProgress else -1f,
                                showPlayhead = isPlaying,
                                onSeek = { fraction -> seekTo(fraction) }
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            WaveformRuler(durationMs = durationMs)
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            OutlinedButton(
                                onClick = { togglePlay() },
                                modifier = Modifier.size(64.dp),
                                shape = CircleShape,
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                            ) {
                                Text(if (isPlaying) "⏸" else "▶", fontSize = 22.sp)
                            }
                        }

                        recordingLocation?.let { (lat, lng) ->
                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = Color(0xFF3A3A3A))
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("📍", modifier = Modifier.width(28.dp))
                                Column {
                                    Text("Posisi saat rekaman", color = Color.White, fontWeight = FontWeight.Medium)
                                    Text(
                                        "%.6f, %.6f".format(lat, lng),
                                        color = Color.Gray,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedButton(
                                onClick = {
                                    val uri = Uri.parse("geo:$lat,$lng?q=$lat,$lng(${Uri.encode(task.debiturName)})")
                                    val intent = Intent(Intent.ACTION_VIEW, uri)
                                    try {
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        // Nggak ada app peta terinstall — no-op, biarin user tau lewat UI lain kalau perlu.
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                            ) {
                                Text("Buka di Peta")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
            }
        }
    }
}

private fun getAudioDurationMs(path: String): Int {
    val retriever = MediaMetadataRetriever()
    return try {
        retriever.setDataSource(path)
        retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toIntOrNull() ?: 0
    } catch (e: Exception) {
        0
    } finally {
        retriever.release()
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
