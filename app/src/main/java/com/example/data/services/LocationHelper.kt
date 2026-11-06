package com.example.data.services

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.example.data.models.CityLocation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale
import java.util.TimeZone

object LocationHelper {

    fun hasLocationPermission(context: Context): Boolean {
        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(context: Context): CityLocation? = withContext(Dispatchers.IO) {
        if (!hasLocationPermission(context)) return@withContext null

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: return@withContext null

        var bestLocation: Location? = null

        try {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                bestLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            }
        } catch (e: Exception) {
            // ignore
        }

        if (bestLocation == null) {
            try {
                if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    bestLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                }
            } catch (e: Exception) {
                // ignore
            }
        }

        if (bestLocation == null) {
            try {
                if (locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)) {
                    bestLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
                }
            } catch (e: Exception) {
                // ignore
            }
        }

        val loc = bestLocation ?: return@withContext null

        var cityName = "موقعي الحالي"
        var countryName = "الجزائر"

        try {
            val geocoder = Geocoder(context, Locale("ar"))
            val addresses = geocoder.getFromLocation(loc.latitude, loc.longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                val addr = addresses[0]
                cityName = addr.locality
                    ?: addr.subAdminArea
                    ?: addr.adminArea
                    ?: "موقعي الحالي"
                countryName = addr.countryName ?: ""
            }
        } catch (e: Exception) {
            cityName = "موقعي الحالي (${String.format(Locale.US, "%.2f, %.2f", loc.latitude, loc.longitude)})"
        }

        val rawOffsetHours = (TimeZone.getDefault().rawOffset.toDouble() / 3600000.0)

        CityLocation(
            nameAr = cityName,
            nameEn = "Current Location",
            countryAr = countryName,
            countryEn = "",
            latitude = loc.latitude,
            longitude = loc.longitude,
            timezoneOffsetHours = rawOffsetHours
        )
    }
}
