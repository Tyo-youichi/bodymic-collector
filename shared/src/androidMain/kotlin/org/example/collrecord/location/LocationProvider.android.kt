package org.example.collrecord.location

import android.annotation.SuppressLint
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.suspendCancellableCoroutine
import org.example.collrecord.platform.PlatformContext
import kotlin.coroutines.resume

actual class LocationProvider actual constructor(private val context: PlatformContext) {

    // Permission (ACCESS_FINE_LOCATION) dicek di layer UI sebelum fungsi ini dipanggil.
    @SuppressLint("MissingPermission")
    actual suspend fun getCurrentLocation(): Coordinate? = suspendCancellableCoroutine { cont ->
        val client = LocationServices.getFusedLocationProviderClient(context.context)
        client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location ->
                if (location != null) {
                    cont.resume(Coordinate(location.latitude, location.longitude))
                } else {
                    cont.resume(null)
                }
            }
            .addOnFailureListener {
                cont.resume(null)
            }
    }
}
