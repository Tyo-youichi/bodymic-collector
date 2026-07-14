package org.example.collrecord.recording

import android.media.MediaRecorder
import android.os.Build
import org.example.collrecord.platform.PlatformContext
import java.io.File

actual class AudioRecorder actual constructor(private val context: PlatformContext) {
    private var recorder: MediaRecorder? = null
    private var outputPath: String? = null

    actual fun startRecording(taskId: String): String {
        val file = File(context.context.cacheDir, "$taskId.m4a")
        outputPath = file.absolutePath

        recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context.context)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(file.absolutePath)
            prepare()
            start()
        }
        return file.absolutePath
    }

    actual fun stopRecording(): String? {
        return try {
            recorder?.apply {
                stop()
                release()
            }
            recorder = null
            outputPath
        } catch (e: Exception) {
            recorder?.release()
            recorder = null
            null
        }
    }

    actual fun isRecording(): Boolean = recorder != null
}
