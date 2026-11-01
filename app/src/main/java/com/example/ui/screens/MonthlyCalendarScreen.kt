package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.MoonPhaseInfo
import com.example.data.models.CityLocation
import com.example.data.models.DayPrayerTimes
import com.example.data.models.HijriEvent
import com.example.data.services.MoonPhaseCalculator
import com.example.data.services.PrayerTimeCalculator
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.GoldAccent
import java.text.SimpleDateFormat
import java.util.*

private data class CalendarDayCell(
    val dayOfMonth: Int,
    val date: Date,
    val hijriDay: Int,
    val hijriMonthName: String,
    val isToday: Boolean,
    val isCurrentMonth: Boolean,
    val isFriday: Boolean,
    val isWhiteDay: Boolean,
    val dayPrayerTimes: DayPrayerTimes?
)

@Composable
fun MonthlyCalendarScreen(
    selectedCity: CityLocation,
    monthPrayerTimes: List<DayPrayerTimes>,
    hijriEvents: List<HijriEvent>,
    onBack: () -> Unit,
    onOpenDrawer: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val moonPhase = remember { MoonPhaseCalculator.calculateMoonPhase(Date()) }
    var selectedTab by remember { mutableStateOf(0) } // 0: التقويم الهجري والميلادي, 1: مواقيت الشهر, 2: أطوار القمر والمناسبات

    val timeFormatter = remember { SimpleDateFormat("hh:mm", Locale.getDefault()) }
    val dayFormatter = remember { SimpleDateFormat("d MMM", Locale("ar")) }
    val fullDateFormatter = remember { SimpleDateFormat("EEEE, d MMMM yyyy", Locale("ar")) }

    // Month Navigation State (Default: Current month October 2026)
    var currentCalendar by remember {
        mutableStateOf(Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
        })
    }

    val currentYear = currentCalendar.get(Calendar.YEAR)
    val currentMonth = currentCalendar.get(Calendar.MONTH) // 0-based

    val gregorianMonthName = remember(currentMonth) {
        val months = listOf(
            "يناير", "فبراير", "مارس", "أبريل", "مايو", "يونيو",
            "يوليو", "أغسطس", "سبتمبر", "أكتوبر", "نوفمبر", "ديسمبر"
        )
        months.getOrElse(currentMonth) { "أكتوبر" }
    }

    // Mid-month Hijri info for header
    val midMonthHijri = remember(currentCalendar) {
        val testCal = currentCalendar.clone() as Calendar
        testCal.set(Calendar.DAY_OF_MONTH, 15)
        PrayerTimeCalculator.getHijriDate(testCal.time)
    }

    // Selected Day in Calendar
    var selectedDayCell by remember {
        mutableStateOf<CalendarDayCell?>(null)
    }

    // Build the grid days for the month
    val daysInMonthList = remember(currentCalendar, monthPrayerTimes) {
        val cal = currentCalendar.clone() as Calendar
        cal.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) // 1 = Sunday, 7 = Saturday
        val maxDaysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

        val todayCal = Calendar.getInstance()
        val isSameYearAndMonth = (todayCal.get(Calendar.YEAR) == cal.get(Calendar.YEAR)) &&
                (todayCal.get(Calendar.MONTH) == cal.get(Calendar.MONTH))
        val todayDay = if (isSameYearAndMonth) todayCal.get(Calendar.DAY_OF_MONTH) else -1

        val list = mutableListOf<CalendarDayCell>()

        // Leading empty/offset days (Sunday as start of week in Arabic calendar)
        val leadingBlanks = (firstDayOfWeek - Calendar.SUNDAY + 7) % 7
        for (i in 0 until leadingBlanks) {
            val prevCal = cal.clone() as Calendar
            prevCal.add(Calendar.DAY_OF_MONTH, -(leadingBlanks - i))
            val hDate = PrayerTimeCalculator.getHijriDate(prevCal.time)
            list.add(
                CalendarDayCell(
                    dayOfMonth = prevCal.get(Calendar.DAY_OF_MONTH),
                    date = prevCal.time,
                    hijriDay = hDate.day,
                    hijriMonthName = hDate.monthNameAr,
                    isToday = false,
                    isCurrentMonth = false,
                    isFriday = prevCal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY,
                    isWhiteDay = hDate.day in 13..15,
                    dayPrayerTimes = null
                )
            )
        }

        // Days of current month
        for (day in 1..maxDaysInMonth) {
            val dayCal = cal.clone() as Calendar
            dayCal.set(Calendar.DAY_OF_MONTH, day)
            val dDate = dayCal.time
            val hDate = PrayerTimeCalculator.getHijriDate(dDate)
            val isToday = day == todayDay
            val isFriday = dayCal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY
            val isWhiteDay = hDate.day in 13..15

            val matchedPrayerTimes = monthPrayerTimes.find { pt ->
                val ptCal = Calendar.getInstance().apply { time = pt.date }
                ptCal.get(Calendar.DAY_OF_MONTH) == day && ptCal.get(Calendar.MONTH) == currentMonth
            }

            list.add(
                CalendarDayCell(
                    dayOfMonth = day,
                    date = dDate,
                    hijriDay = hDate.day,
                    hijriMonthName = hDate.monthNameAr,
                    isToday = isToday,
                    isCurrentMonth = true,
                    isFriday = isFriday,
                    isWhiteDay = isWhiteDay,
                    dayPrayerTimes = matchedPrayerTimes
                )
            )
        }

        list
    }

    // Automatically select today or day 25
    LaunchedEffect(daysInMonthList) {
        if (selectedDayCell == null) {
            selectedDayCell = daysInMonthList.find { it.isToday && it.isCurrentMonth }
                ?: daysInMonthList.find { it.dayOfMonth == 25 && it.isCurrentMonth }
                ?: daysInMonthList.firstOrNull { it.isCurrentMonth }
        }
    }

    Column(
        modifier = modifier
            .testTag("monthly_calendar_screen")
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 3.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Filled.Menu, contentDescription = "القائمة", tint = GoldAccent)
                    }
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "الرجوع")
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "التقويم الهجري والميلادي الشامل",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${selectedCity.nameAr} • عرض هجري وميلادي ومواقيت الشهر",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    contentColor = GoldAccent,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("التقويم الهجري والميلادي", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal, fontSize = 11.sp) },
                        icon = { Icon(Icons.Filled.DateRange, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("مواقيت الشهر", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal, fontSize = 11.sp) },
                        icon = { Icon(Icons.Filled.CalendarMonth, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("أطوار القمر والمناسبات", fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal, fontSize = 11.sp) },
                        icon = { Icon(Icons.Filled.NightsStay, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                }
            }
        }

        // TAB 0: التقويم الهجري والميلادي التفاعلي
        if (selectedTab == 0) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                contentPadding = PaddingValues(top = 10.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Month Navigation Bar
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = {
                                    val newCal = currentCalendar.clone() as Calendar
                                    newCal.add(Calendar.MONTH, -1)
                                    currentCalendar = newCal
                                }
                            ) {
                                Icon(Icons.Filled.ChevronRight, contentDescription = "الشهر السابق", tint = GoldAccent)
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$gregorianMonthName $currentYear م",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${midMonthHijri.monthNameAr} ${midMonthHijri.year} هـ",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = GoldAccent
                                )
                            }

                            IconButton(
                                onClick = {
                                    val newCal = currentCalendar.clone() as Calendar
                                    newCal.add(Calendar.MONTH, 1)
                                    currentCalendar = newCal
                                }
                            ) {
                                Icon(Icons.Filled.ChevronLeft, contentDescription = "الشهر التالي", tint = GoldAccent)
                            }
                        }
                    }
                }

                // Days of week header
                item {
                    val weekDays = listOf("أحد", "إثنين", "ثلاثاء", "أربعاء", "خميس", "جمعة", "سبت")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        weekDays.forEachIndexed { idx, day ->
                            Text(
                                text = day,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (idx == 5) GoldAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // Grid of Days
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            // Render rows of 7
                            val chunkedDays = daysInMonthList.chunked(7)
                            chunkedDays.forEach { week ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp),
                                    horizontalArrangement = Arrangement.SpaceAround
                                ) {
                                    week.forEach { cell ->
                                        val isSelected = selectedDayCell?.date == cell.date
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .aspectRatio(0.95f)
                                                .padding(2.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(
                                                    when {
                                                        isSelected -> MaterialTheme.colorScheme.primary
                                                        cell.isToday -> EmeraldLight.copy(alpha = 0.25f)
                                                        cell.isFriday -> GoldAccent.copy(alpha = 0.12f)
                                                        cell.isWhiteDay -> Color(0xFF6366F1).copy(alpha = 0.1f)
                                                        else -> Color.Transparent
                                                    }
                                                )
                                                .then(
                                                    if (cell.isToday) Modifier.border(
                                                        1.5.dp,
                                                        EmeraldLight,
                                                        RoundedCornerShape(10.dp)
                                                    )
                                                    else Modifier
                                                )
                                                .clickable { selectedDayCell = cell },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                // Gregorian day
                                                Text(
                                                    text = "${cell.dayOfMonth}",
                                                    fontSize = 13.sp,
                                                    fontWeight = if (cell.isToday || isSelected) FontWeight.Bold else FontWeight.Medium,
                                                    color = when {
                                                        isSelected -> MaterialTheme.colorScheme.onPrimary
                                                        !cell.isCurrentMonth -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                                                        cell.isToday -> EmeraldLight
                                                        cell.isFriday -> GoldAccent
                                                        else -> MaterialTheme.colorScheme.onSurface
                                                    }
                                                )
                                                // Hijri day
                                                Text(
                                                    text = "${cell.hijriDay}",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = when {
                                                        isSelected -> MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                                                        !cell.isCurrentMonth -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                                                        cell.isFriday -> GoldAccent
                                                        else -> GoldAccent.copy(alpha = 0.85f)
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Selected Day Details Card
                selectedDayCell?.let { cell ->
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                            border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.4f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = fullDateFormatter.format(cell.date),
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "${cell.hijriDay} ${cell.hijriMonthName} ${midMonthHijri.year} هـ",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GoldAccent
                                        )
                                    }

                                    if (cell.isToday) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = EmeraldLight
                                        ) {
                                            Text(
                                                text = "اليوم",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }
                                    } else if (cell.isWhiteDay) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = Color(0xFF6366F1)
                                        ) {
                                            Text(
                                                text = "الأيام البيض",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Prayer Times for this day
                                cell.dayPrayerTimes?.let { pt ->
                                    Text(text = "مواقيت الصلاة لهذا اليوم:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        listOf(
                                            "فجر" to pt.fajr,
                                            "شروق" to pt.sunrise,
                                            "ظهر" to pt.dhuhr,
                                            "عصر" to pt.asr,
                                            "مغرب" to pt.maghrib,
                                            "عشاء" to pt.isha
                                        ).forEach { (name, time) ->
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text(text = name, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                Text(
                                                    text = timeFormatter.format(Date(time)),
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (name == "مغرب") GoldAccent else MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // TAB 1: جدول مواقيت الصلاة للشهر
        if (selectedTab == 1) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                contentPadding = PaddingValues(top = 10.dp, bottom = 80.dp)
            ) {
                // Table Header Row
                item {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp, horizontal = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "اليوم", fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(1.2f), textAlign = TextAlign.Center)
                            Text(text = "فجر", fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                            Text(text = "شروق", fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                            Text(text = "ظهر", fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                            Text(text = "عصر", fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                            Text(text = "مغرب", fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                            Text(text = "عشاء", fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                        }
                    }
                }

                items(monthPrayerTimes) { pt ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp, horizontal = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = dayFormatter.format(pt.date),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.weight(1.2f),
                                textAlign = TextAlign.Center
                            )
                            Text(text = timeFormatter.format(Date(pt.fajr)), fontSize = 10.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                            Text(text = timeFormatter.format(Date(pt.sunrise)), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                            Text(text = timeFormatter.format(Date(pt.dhuhr)), fontSize = 10.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                            Text(text = timeFormatter.format(Date(pt.asr)), fontSize = 10.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                            Text(text = timeFormatter.format(Date(pt.maghrib)), fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = GoldAccent, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                            Text(text = timeFormatter.format(Date(pt.isha)), fontSize = 10.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                        }
                    }
                }
            }
        }

        // TAB 2: أطوار القمر والمناسبات الإسلامية
        if (selectedTab == 2) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp)
            ) {
                // Moon Phase Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "طور القمر ومنازل الهلال اليوم", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                            Spacer(modifier = Modifier.height(10.dp))

                            Text(text = moonPhase.iconEmoji, fontSize = 56.sp)
                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = moonPhase.phaseNameAr,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = "نسبة الإضاءة", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(text = "${moonPhase.illuminationPercent}%", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = EmeraldLight)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = "عمر القمر", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(text = "${moonPhase.moonAgeDays} يوم", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                                }
                            }
                        }
                    }
                }

                // Islamic Occasions Header
                item {
                    Spacer(modifier = Modifier.height(18.dp))
                    Text(text = "المناسبات الإسلامية الكبرى", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(hijriEvents) { event ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = event.titleAr, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                Text(text = event.descriptionAr, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = "${event.hijriDay}/${event.hijriMonth}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
