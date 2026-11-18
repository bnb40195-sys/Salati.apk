package com.example.data.services

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.os.Looper
import androidx.core.content.ContextCompat
import com.example.data.local.IslamicRepository
import com.example.data.models.CityLocation
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale
import java.util.TimeZone
import kotlin.coroutines.resume
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object LocationHelper {

    fun hasLocationPermission(context: Context): Boolean {
        val fine = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    fun isLocationServiceEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            ?: return false
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                locationManager.isLocationEnabled
            } else {
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            }
        } catch (_: Exception) {
            false
        }
    }

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(context: Context): CityLocation? = withContext(Dispatchers.IO) {
        if (!hasLocationPermission(context) || !isLocationServiceEnabled(context)) {
            return@withContext null
        }

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            ?: return@withContext null

        // 1. Concurrently request a real fresh fix from GPS / Network / Fused providers
        val freshLocation = withTimeoutOrNull(5500L) {
            requestFreshFixConcurrent(context, locationManager)
        }

        // 2. Fall back to freshest real cached fix if fresh update timed out indoors
        val bestLocation = freshLocation ?: getBestLastKnownLocation(locationManager)
            ?: return@withContext null

        val lat = bestLocation.latitude
        val lng = bestLocation.longitude

        val (resolvedCity, resolvedCountry) = resolvePlaceName(context, lat, lng)
        val currentOffsetHours = TimeZone.getDefault().getOffset(System.currentTimeMillis()) / 3600000.0

        CityLocation(
            nameAr = resolvedCity,
            nameEn = "Current Location",
            countryAr = resolvedCountry,
            countryEn = "",
            latitude = lat,
            longitude = lng,
            timezoneOffsetHours = currentOffsetHours
        )
    }

    private fun resolvePlaceName(context: Context, lat: Double, lng: Double): Pair<String, String> {
        var cityName = ""
        var countryName = ""

        // 1. Android Geocoder (Arabic)
        try {
            val geocoder = Geocoder(context, Locale("ar"))
            @Suppress("DEPRECATION")
            val addresses = geocoder.getFromLocation(lat, lng, 1)
            if (!addresses.isNullOrEmpty()) {
                val addr = addresses[0]
                cityName = listOfNotNull(
                    addr.locality,
                    addr.subAdminArea,
                    addr.adminArea,
                    addr.thoroughfare,
                    addr.featureName
                ).firstOrNull { it.isNotBlank() } ?: ""
                countryName = addr.countryName ?: ""
            }
        } catch (_: Exception) {
            // Try HTTP fallback
        }

        // 2. OpenStreetMap Nominatim reverse geocode (for devices/emulators without GMS Geocoder)
        if (cityName.isBlank()) {
            val nominatim = reverseGeocodeNominatim(lat, lng)
            if (nominatim != null) {
                cityName = nominatim.first
                if (countryName.isBlank()) countryName = nominatim.second
            }
        }

        // 3. Offline nearest known city within 45 km if both Geocoders are offline
        if (cityName.isBlank()) {
            val nearest = findNearestPresetCity(lat, lng, maxDistanceKm = 45.0)
            if (nearest != null) {
                cityName = nearest.nameAr
                if (countryName.isBlank()) countryName = nearest.countryAr
            }
        }

        if (cityName.isBlank()) {
            cityName = "موقعي (${String.format(Locale.US, "%.2f, %.2f", lat, lng)})"
        }
        if (countryName.isBlank()) {
            countryName = "الموقع الجغرافي الحقيقي"
        }

        return cityName to countryName
    }

    private fun reverseGeocodeNominatim(lat: Double, lng: Double): Pair<String, String>? {
        return try {
            val urlStr = "https://nominatim.openstreetmap.org/reverse?format=jsonv2&lat=$lat&lon=$lng&accept-language=ar"
            val conn = (URL(urlStr).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 3500
                readTimeout = 3500
                setRequestProperty("User-Agent", "SalatiPrayerApp/1.0 (Android)")
            }
            if (conn.responseCode == 200) {
                val body = conn.inputStream.bufferedReader().use { it.readText() }
                val json = JSONObject(body)
                val addr = json.optJSONObject("address")
                val city = addr?.let {
                    listOf(
                        it.optString("city"),
                        it.optString("town"),
                        it.optString("village"),
                        it.optString("suburb"),
                        it.optString("city_district"),
                        it.optString("state"),
                        it.optString("county")
                    ).firstOrNull { s -> s.isNotBlank() }
                } ?: json.optString("name")
                val country = addr?.optString("country") ?: ""
                if (city.isNotBlank()) city to country else null
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }

    private fun findNearestPresetCity(lat: Double, lng: Double, maxDistanceKm: Double): CityLocation? {
        val cities = runCatching { IslamicRepository().getPresetCities() }.getOrDefault(emptyList())
        var bestCity: CityLocation? = null
        var bestDist = maxDistanceKm
        for (city in cities) {
            val d = haversineKm(lat, lng, city.latitude, city.longitude)
            if (d < bestDist) {
                bestDist = d
                bestCity = city
            }
        }
        return bestCity
    }

    private fun haversineKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLon / 2) * sin(dLon / 2)
        return r * 2 * atan2(sqrt(a), sqrt(1 - a))
    }

    @SuppressLint("MissingPermission")
    private suspend fun requestFreshFixConcurrent(
        context: Context,
        locationManager: LocationManager
    ): Location? = coroutineScope {
        val candidateProviders = buildList {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (runCatching { locationManager.isProviderEnabled(LocationManager.FUSED_PROVIDER) }.getOrDefault(false)) {
                    add(LocationManager.FUSED_PROVIDER)
                }
            }
            if (runCatching { locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) }.getOrDefault(false)) {
                add(LocationManager.GPS_PROVIDER)
            }
            if (runCatching { locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) }.getOrDefault(false)) {
                add(LocationManager.NETWORK_PROVIDER)
            }
        }
        if (candidateProviders.isEmpty()) return@coroutineScope null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val firstFix = CompletableDeferred<Location?>()
            val signals = mutableListOf<CancellationSignal>()
            val jobs = candidateProviders.map { provider ->
                launch {
                    val loc = suspendCancellableCoroutine<Location?> { cont ->
                        val signal = CancellationSignal()
                        synchronized(signals) { signals.add(signal) }
                        cont.invokeOnCancellation { signal.cancel() }
                        try {
                            locationManager.getCurrentLocation(
                                provider,
                                signal,
                                ContextCompat.getMainExecutor(context)
                            ) { location ->
                                if (cont.isActive) cont.resume(location)
                            }
                        } catch (_: Exception) {
                            if (cont.isActive) cont.resume(null)
                        }
                    }
                    if (loc != null) {
                        firstFix.complete(loc)
                    }
                }
            }
            launch {
                jobs.forEach { it.join() }
                if (!firstFix.isCompleted) firstFix.complete(null)
            }
            val result = firstFix.await()
            synchronized(signals) { signals.forEach { it.cancel() } }
            jobs.forEach { it.cancel() }
            return@coroutineScope result
        }

        suspendCancellableCoroutine { cont ->
            val listener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    runCatching { locationManager.removeUpdates(this) }
                    if (cont.isActive) cont.resume(location)
                }

                @Deprecated("Deprecated in Java")
                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                override fun onProviderEnabled(provider: String) {}
                override fun onProviderDisabled(provider: String) {}
            }
            cont.invokeOnCancellation {
                runCatching { locationManager.removeUpdates(listener) }
            }
            try {
                for (provider in candidateProviders) {
                    @Suppress("DEPRECATION")
                    locationManager.requestSingleUpdate(provider, listener, Looper.getMainLooper())
                }
            } catch (_: Exception) {
                runCatching { locationManager.removeUpdates(listener) }
                if (cont.isActive) cont.resume(null)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getBestLastKnownLocation(locationManager: LocationManager): Location? {
        val providers = buildList {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                add(LocationManager.FUSED_PROVIDER)
            }
            add(LocationManager.GPS_PROVIDER)
            add(LocationManager.NETWORK_PROVIDER)
            add(LocationManager.PASSIVE_PROVIDER)
        }
        var best: Location? = null
        for (provider in providers) {
            val loc = runCatching {
                if (locationManager.isProviderEnabled(provider)) {
                    locationManager.getLastKnownLocation(provider)
                } else {
                    null
                }
            }.getOrNull()
            if (loc != null && (best == null || loc.time > best.time)) {
                best = loc
            }
        }
        return best
    }
}
