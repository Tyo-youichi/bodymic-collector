package org.example.collrecord.data

import android.content.Context
import java.io.File

data class RecordingFile(
    val taskId: String,
    val filePath: String,
    val fileSizeBytes: Long,
    val lastModified: Long
)

/** Baca daftar file rekaman yang tersimpan persistent di filesDir/recordings. */
object RecordingStorage {
    fun listRecordings(context: Context): List<RecordingFile> {
        val dir = File(context.filesDir, "recordings")
        if (!dir.exists()) return emptyList()
        return dir.listFiles { file -> file.extension == "m4a" }
            ?.sortedByDescending { it.lastModified() }
            ?.map { file ->
                RecordingFile(
                    taskId = file.nameWithoutExtension,
                    filePath = file.absolutePath,
                    fileSizeBytes = file.length(),
                    lastModified = file.lastModified()
                )
            }
            ?: emptyList()
    }
}
