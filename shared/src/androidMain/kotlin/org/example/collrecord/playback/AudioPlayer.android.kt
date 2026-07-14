package org.example.collrecord.playback

import android.media.MediaPlayer
import org.example.collrecord.platform.PlatformContext

actual class AudioPlayer actual constructor(private val context: PlatformContext) {
    private var mediaPlayer: MediaPlayer? = null

    actual fun play(filePath: String, onCompletion: () -> Unit) {
        stop()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(filePath)
            setOnCompletionListener {
                onCompletion()
                stop()
            }
            setOnErrorListener { _, _, _ ->
                onCompletion()
                true
            }
            prepare()
            start()
        }
    }

    actual fun stop() {
        mediaPlayer?.apply {
            runCatching { reset() }
            release()
        }
        mediaPlayer = null
    }

    actual fun isPlaying(): Boolean = mediaPlayer?.isPlaying == true
}
