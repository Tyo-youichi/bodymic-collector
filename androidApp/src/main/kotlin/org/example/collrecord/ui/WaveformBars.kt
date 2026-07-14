package org.example.collrecord.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.example.collrecord.ui.theme.CollectorBlue
import org.example.collrecord.ui.theme.CollectorBlueLight

/**
 * Grafik waveform sederhana (garis vertikal per sample amplitudo), no external library.
 * - Buat rekam live: panggil tanpa `progress` (semua bar warna sama).
 * - Buat playback: kasih `progress` (0f..1f) biar bar yang udah kelewat playhead beda warna.
 */
@Composable
fun WaveformBars(
    amplitudes: List<Int>,
    modifier: Modifier = Modifier,
    progress: Float = -1f,
    playedColor: Color = CollectorBlue,
    unplayedColor: Color = CollectorBlueLight
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        if (amplitudes.isEmpty()) return@Canvas
        val maxAmp = 32767f
        val barWidth = size.width / amplitudes.size
        amplitudes.forEachIndexed { index, amp ->
            val normalized = (amp / maxAmp).coerceIn(0.05f, 1f)
            val barHeight = size.height * normalized
            val fractionPos = index.toFloat() / amplitudes.size
            val color = if (progress < 0f || fractionPos <= progress) playedColor else unplayedColor
            drawLine(
                color = color,
                start = Offset(x = index * barWidth + barWidth / 2, y = size.height),
                end = Offset(x = index * barWidth + barWidth / 2, y = size.height - barHeight),
                strokeWidth = (barWidth * 0.6f).coerceAtLeast(2f)
            )
        }
    }
}
