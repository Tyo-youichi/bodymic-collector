package org.example.collrecord.playback

import org.example.collrecord.platform.PlatformContext

/** Player buat rekaman yang udah tersimpan. iOS: belum diimplementasikan (phase 1 fokus Android). */
expect class AudioPlayer(context: PlatformContext) {
    fun play(filePath: String, onCompletion: () -> Unit)
    fun stop()
    fun isPlaying(): Boolean

    /** Buat sinkronisasi progress waveform saat play. */
    fun currentPositionMs(): Int
    fun durationMs(): Int
}
