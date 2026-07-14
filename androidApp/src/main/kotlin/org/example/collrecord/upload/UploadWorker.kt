package org.example.collrecord.upload

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import org.example.collrecord.data.RemoteUploadRepository
import java.io.File

/**
 * WorkManager worker buat upload rekaman + koordinat di background.
 * Auto-retry (dengan backoff) kalau gagal, survive meskipun app di-kill
 * atau device restart (selama constraint network terpenuhi).
 */
class UploadWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val taskId = inputData.getString(KEY_TASK_ID) ?: return Result.failure()
        val filePath = inputData.getString(KEY_FILE_PATH) ?: return Result.failure()
        val latitude = inputData.getDouble(KEY_LATITUDE, 0.0)
        val longitude = inputData.getDouble(KEY_LONGITUDE, 0.0)

        val file = File(filePath)
        if (!file.exists()) return Result.failure()

        val repository = RemoteUploadRepository()
        val uploadResult = repository.uploadVisit(
            taskId = taskId,
            audioBytes = file.readBytes(),
            audioFileName = file.name,
            latitude = latitude,
            longitude = longitude
        )

        return if (uploadResult.isSuccess) {
            file.delete()
            Result.success()
        } else {
            Result.retry()
        }
    }

    companion object {
        const val KEY_TASK_ID = "task_id"
        const val KEY_FILE_PATH = "file_path"
        const val KEY_LATITUDE = "latitude"
        const val KEY_LONGITUDE = "longitude"

        fun buildInputData(taskId: String, filePath: String, lat: Double, lng: Double) =
            workDataOf(
                KEY_TASK_ID to taskId,
                KEY_FILE_PATH to filePath,
                KEY_LATITUDE to lat,
                KEY_LONGITUDE to lng
            )
    }
}
