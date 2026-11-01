package com.example.data.services

import com.example.data.models.CityLocation
import com.example.data.models.DayPrayerTimes
import com.example.data.models.PrayerType
import java.util.Calendar
import java.util.Date

object MonthlyPrayerCalculator {

    fun generateMonthPrayerTimes(
        city: CityLocation,
        year: Int,
        month: Int, // 1 to 12
        method: CalculationMethod,
        juristic: JuristicMethod,
        offsets: Map<PrayerType, Int>
    ): List<DayPrayerTimes> {
        val list = mutableListOf<DayPrayerTimes>()
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1)
            set(Calendar.DAY_OF_MONTH, 1)
        }
        val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

        for (day in 1..daysInMonth) {
            cal.set(Calendar.DAY_OF_MONTH, day)
            val dayTimes = PrayerTimeCalculator.calculatePrayerTimes(
                latitude = city.latitude,
                longitude = city.longitude,
                date = cal.time,
                method = method,
                juristic = juristic,
                offsets = offsets,
                cityName = city.nameAr,
                countryName = city.countryAr
            )
            list.add(dayTimes)
        }
        return list
    }
}
