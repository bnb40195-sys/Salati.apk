package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.models.PrayerType
import com.example.data.services.CalculationMethod
import com.example.data.services.PrayerTimeCalculator
import com.example.data.services.QiblaCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Date

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("صلاتي", appName)
    }

    @Test
    fun `test offline prayer calculation for Mecca`() {
        val times = PrayerTimeCalculator.calculatePrayerTimes(
            latitude = 21.4225,
            longitude = 39.8262,
            date = Date(),
            method = CalculationMethod.UMM_AL_QURA
        )
        assertTrue(times.fajr < times.sunrise)
        assertTrue(times.sunrise < times.dhuhr)
        assertTrue(times.dhuhr < times.asr)
        assertTrue(times.asr < times.maghrib)
        assertTrue(times.maghrib < times.isha)
    }

    @Test
    fun `test qibla bearing to Mecca`() {
        // From Cairo (lat 30.04, lng 31.23) to Mecca is approximately ~136 degrees (SE)
        val bearing = QiblaCalculator.calculateQiblaBearing(30.0444, 31.2357)
        assertTrue(bearing in 130.0..145.0)

        // Distance from Cairo to Mecca is around ~1200-1350 km
        val distance = QiblaCalculator.calculateDistanceToKaabaKm(30.0444, 31.2357)
        assertTrue(distance in 1200.0..1350.0)
    }
}
