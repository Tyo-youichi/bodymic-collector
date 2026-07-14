package org.example.collrecord.recording

import org.example.collrecord.platform.PlatformContext

// Stub — belum diimplementasikan untuk iOS di phase 1 ini.
actual class AudioRecorder actual constructor(context: PlatformContext) {
    actual fun startRecording(taskId: String): String {
        throw NotImplementedError("Audio recording belum diimplementasikan untuk iOS")
    }

    actual fun stopRecording(): String? = null

    actual fun isRecording(): Boolean = false
}
