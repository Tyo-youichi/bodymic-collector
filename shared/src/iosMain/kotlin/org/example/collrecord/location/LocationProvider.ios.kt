package org.example.collrecord.location

import org.example.collrecord.platform.PlatformContext

// Stub — belum diimplementasikan untuk iOS di phase 1 ini.
actual class LocationProvider actual constructor(context: PlatformContext) {
    actual suspend fun getCurrentLocation(): Coordinate? = null
}
