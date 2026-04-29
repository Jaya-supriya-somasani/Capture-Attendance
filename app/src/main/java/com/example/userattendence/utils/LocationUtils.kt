package com.example.userattendence.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
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

    val location =
        suspendCancellableCoroutine<android.location.Location?> { cont ->
            fusedClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationToken.token
            ).addOnSuccessListener { location ->
                cont.resume(location)
            }.addOnFailureListener {
                cont.resume(null)
            }

            cont.invokeOnCancellation { cancellationToken.cancel() }
        } ?: return null

    val address = withContext(Dispatchers.IO) {
        getAddress(context, location.latitude, location.longitude)
    }

    return LocationData(location.latitude, location.longitude, address)
}

@Suppress("DEPRECATION")
private suspend fun getAddress(context: Context, lat: Double, lng: Double): String {
    return try {
        val geocoder = Geocoder(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            withTimeoutOrNull(5000L) {
                suspendCancellableCoroutine { cont ->
                    geocoder.getFromLocation(lat, lng, 1) { addresses ->
                        cont.resume(formatAddress(addresses))
                    }
                }
            } ?: "Address unavailable"
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