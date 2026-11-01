package com.example.data.services

import kotlin.math.*

object QiblaCalculator {
    const val KAABA_LATITUDE = 21.422487
    const val KAABA_LONGITUDE = 39.826206

    /**
     * Calculates the bearing in degrees (0 to 360) from a given location to the Kaaba in Mecca.
     */
    fun calculateQiblaBearing(latitude: Double, longitude: Double): Double {
        val latRad = Math.toRadians(latitude)
        val lngRad = Math.toRadians(longitude)
        val kaabaLatRad = Math.toRadians(KAABA_LATITUDE)
        val kaabaLngRad = Math.toRadians(KAABA_LONGITUDE)

        val deltaLng = kaabaLngRad - lngRad

        val y = sin(deltaLng) * cos(kaabaLatRad)
        val x = cos(latRad) * sin(kaabaLatRad) - sin(latRad) * cos(kaabaLatRad) * cos(deltaLng)

        var bearing = Math.toDegrees(atan2(y, x))
        bearing = (bearing + 360.0) % 360.0
        return bearing
    }

    /**
     * Calculates the distance in kilometers from a given location to the Kaaba.
     */
    fun calculateDistanceToKaabaKm(latitude: Double, longitude: Double): Double {
        val r = 6371.0 // Earth radius in km
        val lat1Rad = Math.toRadians(latitude)
        val lat2Rad = Math.toRadians(KAABA_LATITUDE)
        val dLat = Math.toRadians(KAABA_LATITUDE - latitude)
        val dLng = Math.toRadians(KAABA_LONGITUDE - longitude)

        val a = sin(dLat / 2).pow(2) + cos(lat1Rad) * cos(lat2Rad) * sin(dLng / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }
}
