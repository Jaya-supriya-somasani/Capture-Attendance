package com.example.userattendence.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val address: String
)

@SuppressLint("MissingPermission")
suspend fun getCurrentLocation(context: Context): LocationData? {
    val fusedClient = LocationServices.getFusedLocationProviderClient(context)
    val cancellationToken = CancellationTokenSource()

    return suspendCancellableCoroutine { cont ->
        fusedClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            cancellationToken.token
        ).addOnSuccessListener { location ->
            if (location != null) {
                // ✅ getAddress is now called inside the location callback
                // so we have real coordinates before resolving
                val address = getAddressFromCoordinatesSync(
                    context,
                    location.latitude,
                    location.longitude
                )
                cont.resume(
                    LocationData(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        address = address
                    )
                )
            } else {
                cont.resume(null)
            }
        }.addOnFailureListener {
            cont.resume(null)
        }

        cont.invokeOnCancellation {
            cancellationToken.cancel()
        }
    }
}

// ✅ Sync version for older Android + properly awaited callback for Android 13+
@Suppress("DEPRECATION")
fun getAddressFromCoordinatesSync(context: Context, lat: Double, lng: Double): String {
    return try {
        val geocoder = Geocoder(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // ✅ Use blocking approach for TIRAMISU+ to avoid async race condition
            var resultAddress = "Address unavailable"
            val latch = java.util.concurrent.CountDownLatch(1)

            geocoder.getFromLocation(lat, lng, 1) { addresses ->
                resultAddress = formatAddress(addresses)
                latch.countDown()   // ✅ Unblocks after callback fires
            }

            latch.await(5, java.util.concurrent.TimeUnit.SECONDS) // wait max 5 sec
            resultAddress
        } else {
            val addresses = geocoder.getFromLocation(lat, lng, 1)
            formatAddress(addresses ?: emptyList())
        }
    } catch (e: Exception) {
        "Address unavailable"
    }
}

private fun formatAddress(addresses: List<Address>): String {
    if (addresses.isEmpty()) return "Unknown location"
    val address = addresses[0]
    return buildString {
        address.subLocality?.let { append("$it, ") }
        address.locality?.let { append("$it, ") }
        address.adminArea?.let { append("$it, ") }
        address.countryName?.let { append(it) }
    }.trimEnd(',', ' ')
}