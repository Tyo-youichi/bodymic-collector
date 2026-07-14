package org.example.collrecord.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.collrecord.ui.theme.CollectorBlue

/**
 * Grafik waveform gaya recorder biasa (bar simetris dari garis tengah + playhead), no external
 * library — cuma Canvas biasa.
 * - Rekam live: panggil tanpa `progress` (semua bar warna sama, growing dari tengah).
 * - Playback: kasih `progress` (0f..1f) + `showPlayhead = true` buat garis vertikal yang gerak.
 */
@Composable
fun WaveformBars(
    amplitudes: List<Int>,
    modifier: Modifier = Modifier,
    progress: Float = -1f,
    showPlayhead: Boolean = false,
    playedColor: Color = CollectorBlue,
    unplayedColor: Color = Color(0xFF555555),
    playheadColor: Color = Color.White,
    onSeek: ((Float) -> Unit)? = null
) {
    val seekModifier = if (onSeek != null) {
        Modifier
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    onSeek((offset.x / size.width).coerceIn(0f, 1f))
                }
            }
            .pointerInput(Unit) {
                detectHorizontalDragGestures { change, _ ->
                    onSeek((change.position.x / size.width).coerceIn(0f, 1f))
                }
            }
    } else {
        Modifier
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .then(seekModifier)
    ) {
        if (amplitudes.isEmpty()) return@Canvas
        val maxAmp = 32767f
        val barWidth = size.width / amplitudes.size
        val centerY = size.height / 2f

        amplitudes.forEachIndexed { index, amp ->
            val normalized = (amp / maxAmp).coerceIn(0.06f, 1f)
            val halfHeight = (size.height / 2f) * normalized
            val fractionPos = index.toFloat() / amplitudes.size
            val color = if (progress < 0f || fractionPos <= progress) playedColor else unplayedColor
            val x = index * barWidth + barWidth / 2
            drawLine(
                color = color,
                start = Offset(x, centerY - halfHeight),
                end = Offset(x, centerY + halfHeight),
                strokeWidth = (barWidth * 0.6f).coerceAtLeast(2f)
            )
        }

        if (showPlayhead && progress in 0f..1f) {
            val x = size.width * progress
            drawLine(
                color = playheadColor,
                start = Offset(x, 0f),
                end = Offset(x, size.height),
                strokeWidth = 2f
            )
        }
    }
}

/** Ruler waktu di bawah waveform, kayak "00:00  00:02  00:04  00:06". */
@Composable
fun WaveformRuler(
    durationMs: Int,
    modifier: Modifier = Modifier,
    tickCount: Int = 4,
    color: Color = Color.Gray
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        for (i in 0..tickCount) {
            val ms = (durationMs.toLong() * i / tickCount).toInt()
            Text(formatDurationShort(ms), color = color, fontSize = 10.sp)
        }
    }
}
