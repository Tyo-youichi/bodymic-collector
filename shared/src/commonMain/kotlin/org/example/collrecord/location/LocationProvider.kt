package org.example.collrecord.location

import org.example.collrecord.platform.PlatformContext

data class Coordinate(val latitude: Double, val longitude: Double)

/** Android: FusedLocationProviderClient. iOS: belum diimplementasikan (phase 1 fokus Android). */
expect class LocationProvider(context: PlatformContext) {
    suspend fun getCurrentLocation(): Coordinate?
}
