package org.example.collrecord.recording

import org.example.collrecord.platform.PlatformContext

/**
 * Rekam suara. Android: MediaRecorder, simpan sementara di cacheDir dengan nama {taskId}.m4a.
 * Default pakai mic bawaan; kalau ada Bluetooth headset yang paired & aktif sebagai input,
 * OS otomatis routing ke situ — tidak perlu kode tambahan untuk kasus dasar.
 * iOS: belum diimplementasikan (phase 1 fokus Android).
 */
expect class AudioRecorder(context: PlatformContext) {
    /** Mulai rekam, return path file hasil rekaman. */
    fun startRecording(taskId: String): String

    /** Stop rekam, return path file (atau null kalau gagal/tidak ada recording aktif). */
    fun stopRecording(): String?

    fun isRecording(): Boolean

    /** Level amplitudo mic saat ini (buat visualisasi waveform live). 0 kalau tidak sedang rekam. */
    fun currentAmplitude(): Int
}
