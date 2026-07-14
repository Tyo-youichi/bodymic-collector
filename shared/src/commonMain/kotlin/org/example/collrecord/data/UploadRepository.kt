package org.example.collrecord.data

import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import org.example.collrecord.network.ApiConfig
import org.example.collrecord.network.createHttpClient

interface UploadRepository {
    suspend fun uploadVisit(
        taskId: String,
        audioBytes: ByteArray,
        audioFileName: String,
        latitude: Double,
        longitude: Double
    ): Result<Unit>
}

/**
 * Implementasi upload pakai Ktor multipart POST. Fully commonMain — nggak butuh
 * java.io.File (yang tidak tersedia di iOS), tinggal terima ByteArray mentah.
 * Pembacaan file jadi ByteArray dilakukan di layer platform (androidApp: UploadWorker).
 *
 * Endpoint masih placeholder (ApiConfig.BASE_URL) — ganti pas backend siap.
 */
class RemoteUploadRepository : UploadRepository {
    private val client = createHttpClient()

    override suspend fun uploadVisit(
        taskId: String,
        audioBytes: ByteArray,
        audioFileName: String,
        latitude: Double,
        longitude: Double
    ): Result<Unit> {
        return try {
            client.submitFormWithBinaryData(
                url = "${ApiConfig.BASE_URL}/visits/upload",
                formData = formData {
                    append("task_id", taskId)
                    append("latitude", latitude.toString())
                    append("longitude", longitude.toString())
                    append("audio", audioBytes, Headers.build {
                        append(HttpHeaders.ContentType, "audio/mp4")
                        append(HttpHeaders.ContentDisposition, "filename=\"$audioFileName\"")
                    })
                }
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
