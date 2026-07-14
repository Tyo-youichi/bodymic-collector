package org.example.collrecord.network

import io.ktor.client.HttpClient

object ApiConfig {
    // TODO: ganti dengan base URL server yang sebenarnya kalau sudah tersedia.
    const val BASE_URL = "https://mock-api.collrecord.local"
}

/** Setiap platform menyediakan HttpClient dengan engine masing-masing (Android/Darwin). */
expect fun createHttpClient(): HttpClient
