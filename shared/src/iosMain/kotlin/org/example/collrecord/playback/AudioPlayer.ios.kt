package org.example.collrecord.playback

import org.example.collrecord.platform.PlatformContext

// Stub — belum diimplementasikan untuk iOS di phase 1 ini.
actual class AudioPlayer actual constructor(context: PlatformContext) {
    actual fun play(filePath: String, onCompletion: () -> Unit) {}
    actual fun stop() {}
    actual fun isPlaying(): Boolean = false
    actual fun currentPositionMs(): Int = 0
    actual fun durationMs(): Int = 0
    actual fun seekTo(positionMs: Int) {}
}
