package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.models.CityLocation
import com.example.data.models.DayPrayerTimes
import com.example.data.models.HijriDate
import com.example.data.models.PrayerType
import com.example.data.services.PrayerTimeCalculator
import com.example.ui.components.AppTab
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.lazy.items
import com.example.data.models.AppSectionModel

import com.example.ui.components.IslamicPatternBackground
import com.example.data.models.WallpaperModel
import com.example.data.services.WeatherInfo

@Composable
fun HomeScreen(
    prayerTimes: DayPrayerTimes?,
    hijriDate: HijriDate,
    selectedCity: CityLocation,
    weatherInfo: WeatherInfo = WeatherInfo(),
    showWeatherOnHome: Boolean = true,
    salawatAlertVisible: Boolean = false,
    prePrayerMessageVisible: Boolean = false,
    postPrayerReminderVisible: Boolean = false,
    postPrayerAthkarTime: String = "05:00",
    onDismissPostPrayerReminder: () -> Unit = {},
    onNavigateTab: (AppTab) -> Unit,
    onOpenCitySelector: () -> Unit,
    onPlayAdhan: () -> Unit,
    onOpenMonthlyCalendar: () -> Unit,
    onOpenOnThisDay: () -> Unit,
    onOpenSunnah: () -> Unit = {},
    onOpenGhazwat: () -> Unit,
    onOpenAdvancedSettings: () -> Unit,
    onOpenWidgetsStudio: () -> Unit = {},
    onRefreshWeather: () -> Unit = {},
    onOpenPrayerAdjustments: () -> Unit = {},
    onOpenAthkarDuas: (String) -> Unit = {},
    onPlayPrePrayerVoice: () -> Unit = {},
    onPlaySalawatVoice: () -> Unit = {},
    onDismissSalawatAlert: () -> Unit = {},
    onOpenDrawer: () -> Unit = {},
    onOpenUnifiedProphetsTawbah: (Int) -> Unit = {},
    onOpenUnifiedInventionsBattles: (Int) -> Unit = {},
    onOpenUnifiedQuestions: (Int) -> Unit = {},
    onOpenCapitals: () -> Unit = {},
    onOpenUnifiedAdhanReminder: () -> Unit = {},
    onOpenUnifiedQuranSunnah: () -> Unit = {},
    onOpenCreatorStudio: () -> Unit = {},
    onOpenAlertsAndReminders: () -> Unit = {},
    timeFormat: String = "12h",
    digitType: String = "western",
    onDetectLocationGps: () -> Unit = {},
    isGpsDetecting: Boolean = false,
    sections: List<AppSectionModel> = emptyList(),
    currentWallpaper: WallpaperModel? = null,
    modifier: Modifier = Modifier
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var showPinWidgetDialog by remember { mutableStateOf(false) }

    // Current time ticking state
    var currentTimeMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            currentTimeMillis = System.currentTimeMillis()
            delay(1000)
        }
    }

    // Determine next prayer and time remaining
    val prayerList = remember(prayerTimes) {
        if (prayerTimes == null) emptyList()
        else listOf(
            PrayerType.FAJR to prayerTimes.fajr,
            PrayerType.SUNRISE to prayerTimes.sunrise,
            PrayerType.DHUHR to prayerTimes.dhuhr,
            PrayerType.ASR to prayerTimes.asr,
            PrayerType.MAGHRIB to prayerTimes.maghrib,
            PrayerType.ISHA to prayerTimes.isha
        )
    }

    val (nextPrayer, nextPrayerMillis) = remember(prayerList, currentTimeMillis) {
        val next = prayerList.firstOrNull { it.second > currentTimeMillis }
        if (next != null) {
            next
        } else {
            // Next is tomorrow's Fajr (add 24h approximation)
            val tomorrowFajr = (prayerList.firstOrNull()?.second ?: currentTimeMillis) + 86400000L
            PrayerType.FAJR to tomorrowFajr
        }
    }

    val remainingSeconds = ((nextPrayerMillis - currentTimeMillis) / 1000).coerceAtLeast(0)
    val remHours = remainingSeconds / 3600
    val remMinutes = (remainingSeconds % 3600) / 60
    val remSecs = remainingSeconds % 60
    val rawCountdown = String.format("%02d:%02d:%02d", remHours, remMinutes, remSecs)
    val formattedCountdown = com.example.data.services.DigitFormatter.format(rawCountdown, digitType)

    val timeFormatter = remember(timeFormat) {
        if (timeFormat == "24h") SimpleDateFormat("HH:mm", Locale.getDefault())
        else SimpleDateFormat("hh:mm a", Locale("ar"))
    }
    val dateFormatter = remember { SimpleDateFormat("EEEE, d MMMM yyyy", Locale("ar")) }
    val gregorianDateStr = remember(currentTimeMillis, digitType) {
        com.example.data.services.DigitFormatter.format(dateFormatter.format(Date(currentTimeMillis)), digitType)
    }
    val formattedHijriDate = remember(hijriDate, digitType) {
        com.example.data.services.DigitFormatter.format(hijriDate.formattedAr, digitType)
    }

    IslamicPatternBackground(
        modifier = modifier.fillMaxSize(),
        wallpaper = currentWallpaper
    ) {
        LazyColumn(
            modifier = Modifier
                .testTag("home_screen")
                .fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
        // Hero Mosque Banner Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            ) {
                // Background Mosque Artwork
                Image(
                    painter = painterResource(id = R.drawable.mosque_banner),
                    contentDescription = "Mosque Banner",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Dark gradient overlay for perfect readability
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color(0x99061813),
                                    MaterialTheme.colorScheme.background
                                )
                            )
                        )
                )

                // Header Top Details
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Top row: City picker button & GPS button & Drawer
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                onClick = onOpenCitySelector,
                                shape = RoundedCornerShape(20.dp),
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                                tonalElevation = 4.dp,
                                modifier = Modifier.testTag("city_selector_button")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.LocationOn,
                                        contentDescription = "Location",
                                        tint = GoldAccent,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Text(
                                        text = selectedCity.nameAr,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Icon(
                                        imageVector = Icons.Filled.KeyboardArrowDown,
                                        contentDescription = "Select City",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            }

                            // GPS Auto Detect Button
                            Surface(
                                onClick = onDetectLocationGps,
                                shape = RoundedCornerShape(20.dp),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                                tonalElevation = 4.dp,
                                modifier = Modifier.testTag("gps_header_detect_button")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    if (isGpsDetecting) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(14.dp),
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Filled.MyLocation,
                                            contentDescription = "GPS",
                                            tint = MaterialTheme.colorScheme.onPrimary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                    Text(
                                        text = "GPS",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            }
                        }

                        // App Title Badge & Drawer Menu Button
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                onClick = onOpenDrawer,
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f),
                                modifier = Modifier.testTag("app_title_drawer_badge")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Star,
                                        contentDescription = null,
                                        tint = GoldAccent,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = "صلاتي",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            IconButton(
                                onClick = onOpenDrawer,
                                modifier = Modifier
                                    .testTag("open_navigation_drawer_button")
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f))
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Menu,
                                    contentDescription = "فتح قائمة الأقسام",
                                    tint = GoldAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    // Bottom of hero: Dates & greeting
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = formattedHijriDate,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldAccentLight
                        )
                        Text(
                            text = gregorianDateStr,
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            }
        }

        // Next Prayer Countdown Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("next_prayer_countdown_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.AccessTime,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "الصلاة القادمة",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = nextPrayer.arName,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        // Play Azan audio button
                        Button(
                            onClick = onPlayAdhan,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                            modifier = Modifier.testTag("quick_play_adhan_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.VolumeUp,
                                contentDescription = "Play Adhan",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "الأذان",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Countdown numbers display
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.7f))
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "متبقي:",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = formattedCountdown,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = GoldAccent,
                            letterSpacing = 2.sp
                        )
                    }
                }
            }
        }

        // Electronic Muezzin Style Temperature & Weather Widget
        val showWeatherSection = sections.find { it.id == "weather" }?.isEnabled ?: true
        if (showWeatherSection && showWeatherOnHome) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .testTag("muezzin_weather_card"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(if (weatherInfo.isDay) GoldAccent.copy(alpha = 0.2f) else MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (weatherInfo.isDay) Icons.Filled.WbSunny else Icons.Filled.NightsStay,
                                        contentDescription = "Weather",
                                        tint = if (weatherInfo.isDay) GoldAccent else MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(
                                            text = com.example.data.services.DigitFormatter.format("${weatherInfo.temperatureC.toInt()}°م", digitType),
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = MaterialTheme.colorScheme.surfaceVariant
                                        ) {
                                            Text(
                                                text = weatherInfo.conditionAr,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = "${selectedCity.nameAr} • ",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = if (weatherInfo.isOnline) "🌐 مباشر (إنترنت)" else "📡 محلي فلكي",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (weatherInfo.isOnline) Color(0xFF10B981) else GoldAccent
                                        )
                                    }
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                IconButton(
                                    onClick = onRefreshWeather,
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Refresh,
                                        contentDescription = "تحديث الطقس",
                                        tint = GoldAccent,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                // Temperature Badge
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = GoldAccent.copy(alpha = 0.15f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.5f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(Icons.Filled.Thermostat, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(14.dp))
                                        Text(
                                            text = "درجة الحرارة",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GoldAccent
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // On This Day Card (حدث في مثل هذا اليوم حسب الهاتف)
        item {
            val phoneTodayEvents = remember(currentTimeMillis) {
                com.example.data.local.OnThisDayDataset.getEventsForPhoneToday()
            }
            val primaryEvent = phoneTodayEvents.events.firstOrNull()

            if (primaryEvent != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 5.dp)
                        .clickable { onOpenOnThisDay() }
                        .testTag("home_on_this_day_phone_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)),
                    border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.35f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.HistoryEdu,
                                    contentDescription = null,
                                    tint = GoldAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "في مثل هذا اليوم حسب الهاتف",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            TextButton(
                                onClick = onOpenOnThisDay,
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                            ) {
                                Text(
                                    text = "عرض الكل",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldAccent
                                )
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = GoldAccent,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = GoldAccent.copy(alpha = 0.2f),
                                modifier = Modifier.padding(top = 2.dp)
                            ) {
                                Text(
                                    text = com.example.data.services.DigitFormatter.format(primaryEvent.year, digitType),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldAccent,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = primaryEvent.titleAr,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = primaryEvent.detailsAr,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }

        // Prayer Times List Cards
        val showMainPrayer = sections.find { it.id == "main_prayer" }?.isEnabled ?: true
        if (showMainPrayer) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "مواقيت اليوم",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = selectedCity.nameAr,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                prayerList.forEach { (type, timeMillis) ->
                    val isNext = type == nextPrayer
                    val rawFormatted = timeFormatter.format(Date(timeMillis))
                    val formatted = com.example.data.services.DigitFormatter.format(rawFormatted, digitType)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .testTag("prayer_card_${type.name.lowercase()}"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isNext)
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            else MaterialTheme.colorScheme.surface
                        ),
                        border = if (isNext)
                            androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
                        else null,
                        elevation = CardDefaults.cardElevation(defaultElevation = if (isNext) 4.dp else 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isNext) MaterialTheme.colorScheme.primary
                                            else MaterialTheme.colorScheme.surfaceVariant
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    val icon = when (type) {
                                        PrayerType.FAJR -> Icons.Filled.WbTwilight
                                        PrayerType.SUNRISE -> Icons.Filled.WbSunny
                                        PrayerType.DHUHR -> Icons.Filled.LightMode
                                        PrayerType.ASR -> Icons.Filled.WbCloudy
                                        PrayerType.MAGHRIB -> Icons.Filled.NightsStay
                                        PrayerType.ISHA -> Icons.Filled.Bedtime
                                    }
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = if (isNext) MaterialTheme.colorScheme.onPrimary
                                        else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                Text(
                                    text = type.arName,
                                    fontSize = 15.sp,
                                    fontWeight = if (isNext) FontWeight.Bold else FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (isNext) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.primary
                                    ) {
                                        Text(
                                            text = "القادمة",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Text(
                                    text = formatted,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isNext) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }


        // Fasting (Siyam) Info Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "الإمساك (الفجر)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = prayerTimes?.let { timeFormatter.format(Date(it.fajr)) } ?: "--:--",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Divider(
                        modifier = Modifier
                            .height(28.dp)
                            .width(1.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "الإفطار (المغرب)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = prayerTimes?.let { timeFormatter.format(Date(it.maghrib)) } ?: "--:--",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldAccent
                        )
                    }
                }
            }
        }

        // Post-Prayer Athkar Reminder on Screen Banner (مثل 5 دقائق بعد الصلاة)
        if (postPrayerReminderVisible) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .testTag("home_post_prayer_reminder_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = EmeraldLight.copy(alpha = 0.15f)),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, EmeraldLight)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = EmeraldLight.copy(alpha = 0.2f),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = EmeraldLight)
                                }
                            }
                            Column {
                                Text(
                                    text = "تذكير: أذكار ما بعد الصلاة",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldLight
                                )
                                Text(
                                    text = "الموعد: بعد 5 دقائق من الصلاة ($postPrayerAthkarTime) • ألا بذكر الله تطمئن القلوب",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Button(
                                onClick = onOpenAlertsAndReminders,
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldLight),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("رسالة الأذكار كاملة", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                            IconButton(onClick = onDismissPostPrayerReminder) {
                                Icon(Icons.Filled.Close, contentDescription = "Close", modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }

        // Periodic Salawat on Prophet Alert Banner
        if (salawatAlertVisible) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            IconButton(onClick = onPlaySalawatVoice) {
                                Icon(Icons.Filled.VolumeUp, contentDescription = "Play Salawat Voice", tint = GoldAccent)
                            }
                            Column {
                                Text(
                                    text = "تذكير: الصلاة على النبي ﷺ",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "اللَّهُمَّ صَلِّ وَسَلِّمْ وَبَارِكْ عَلَى نَبِيِّنَا مُحَمَّدٍ",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                        IconButton(onClick = onDismissSalawatAlert) {
                            Icon(Icons.Filled.Close, contentDescription = "Close", modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }

        // Dedicated Home Widget Preview Card (Matching Screenshot & user request)
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("home_screen_widget_card")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFFA040).copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Widgets,
                                    contentDescription = null,
                                    tint = Color(0xFFFFA040)
                                )
                            }
                            Column {
                                Text(
                                    text = "ويدجت مواقيت الصلاة (Widgets)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "معاينة الويدجت المطور مع درجة الحرارة والعد التنازلي",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        FilledTonalButton(
                            onClick = onOpenWidgetsStudio,
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Filled.Tune, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("تخصيص وإضافة", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Embedded Mini Replica of the Widget (as in screenshot)
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenWidgetsStudio() }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(Color(0xFF242F23), Color(0xFF161E16), Color(0xFF0D130D))
                                    )
                                )
                                .padding(12.dp)
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                // Top row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column {
                                        Text(
                                            text = "$remMinutes دقيقة",
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color(0xFFFFA040)
                                        )
                                        Text(
                                            text = "حتى ${nextPrayer.arName} @ ${timeFormatter.format(Date(nextPrayerMillis))}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "${selectedCity.nameAr} • ${weatherInfo.temperatureC.toInt()}°C ☀️",
                                            fontSize = 11.sp,
                                            color = Color.White.copy(alpha = 0.8f)
                                        )
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = hijriDate.formattedAr,
                                            fontSize = 10.sp,
                                            color = Color.White.copy(alpha = 0.9f)
                                        )
                                        Text(
                                            text = gregorianDateStr,
                                            fontSize = 10.sp,
                                            color = Color.White.copy(alpha = 0.7f)
                                        )
                                        val timeOnly = remember(currentTimeMillis) {
                                            SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(currentTimeMillis))
                                        }
                                        Text(
                                            text = timeOnly,
                                            fontSize = 28.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color.White
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // 6 prayers row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    val miniPrayers = listOf(
                                        Triple(PrayerType.ISHA, "العشاء", prayerTimes?.let { timeFormatter.format(Date(it.isha)) } ?: "19:31"),
                                        Triple(PrayerType.MAGHRIB, "المغرب", prayerTimes?.let { timeFormatter.format(Date(it.maghrib)) } ?: "18:14"),
                                        Triple(PrayerType.ASR, "العصر", prayerTimes?.let { timeFormatter.format(Date(it.asr)) } ?: "15:47"),
                                        Triple(PrayerType.DHUHR, "الظهر", prayerTimes?.let { timeFormatter.format(Date(it.dhuhr)) } ?: "12:51"),
                                        Triple(PrayerType.SUNRISE, "الشروق", prayerTimes?.let { timeFormatter.format(Date(it.sunrise)) } ?: "07:22"),
                                        Triple(PrayerType.FAJR, "الفجر", prayerTimes?.let { timeFormatter.format(Date(it.fajr)) } ?: "05:56")
                                    )
                                    miniPrayers.forEach { (type, name, tStr) ->
                                        val isNext = type == nextPrayer
                                        val isSunrise = type == PrayerType.SUNRISE
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = name,
                                                fontSize = 10.sp,
                                                color = if (isNext) Color(0xFFFFA040) else Color.White.copy(alpha = 0.8f)
                                            )
                                            Text(
                                                text = tStr,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = when {
                                                    isSunrise -> Color(0xFF4ADE80)
                                                    isNext -> Color(0xFFFFA040)
                                                    else -> Color.White
                                                }
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "سبحان الله العظيم",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.9f),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Direct Pin to Phone Home Screen (Outside Application)
                    Button(
                        onClick = {
                            val appWidgetManager = context.getSystemService(android.appwidget.AppWidgetManager::class.java)
                            val provider = android.content.ComponentName(context, com.example.widget.PrayerAppWidgetProvider::class.java)
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O && appWidgetManager?.isRequestPinAppWidgetSupported == true) {
                                com.example.widget.PrayerAppWidgetProvider.updateAllWidgets(context)
                                appWidgetManager.requestPinAppWidget(provider, null, null)
                                android.widget.Toast.makeText(context, "جاري فتح نافذة تثبيت الويدجت على شاشة هاتفك الرئيسية...", android.widget.Toast.LENGTH_LONG).show()
                            } else {
                                showPinWidgetDialog = true
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA040)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("pin_widget_outside_app_button")
                    ) {
                        Icon(Icons.Filled.AddBox, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "تثبيت الويدجت على شاشة الهاتف الرئيسية (خارج التطبيق)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        TextButton(onClick = { showPinWidgetDialog = true }) {
                            Icon(Icons.Filled.HelpOutline, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("طريقة إضافة الويدجت يدوياً من لانشر الهاتف", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }

    if (showPinWidgetDialog) {
        AlertDialog(
            onDismissRequest = { showPinWidgetDialog = false },
            icon = {
                Icon(Icons.Filled.Widgets, contentDescription = null, tint = Color(0xFFFFA040), modifier = Modifier.size(32.dp))
            },
            title = {
                Text(
                    text = "تشغيل الويدجت خارج التطبيق على شاشة الهاتف",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "يعمل ويدجت مواقيت الصلاة كتطبيق مصغر حقيقي (AppWidget) خارج التطبيق على واجهة وشاشة هاتفك الرئيسية:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Divider(color = MaterialTheme.colorScheme.surfaceVariant)
                    Text("1. اخرج من التطبيق واذهب إلى شاشة هاتفك الرئيسية (Home Screen).", fontSize = 12.sp)
                    Text("2. اضغط مطولاً على أي مساحة فارغة في الشاشة الرئيسية.", fontSize = 12.sp)
                    Text("3. اختر 'التطبيقات المصغرة' أو 'Widgets' من القائمة.", fontSize = 12.sp)
                    Text("4. ابحث عن تطبيق 'صلاتي' أو 'ويدجت مواقيت الصلاة'.", fontSize = 12.sp)
                    Text("5. اسحب الويدجت إلى الشاشة الرئيسية وضعه في المكان المفضل لديك.", fontSize = 12.sp)
                    Text("★ سيعمل الويدجت بتوقيت حي محدث باستمرار ودرجة الحرارة دون الحاجة لفتح التطبيق!", fontSize = 11.sp, color = EmeraldLight, fontWeight = FontWeight.Bold)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val appWidgetManager = context.getSystemService(android.appwidget.AppWidgetManager::class.java)
                        val provider = android.content.ComponentName(context, com.example.widget.PrayerAppWidgetProvider::class.java)
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O && appWidgetManager?.isRequestPinAppWidgetSupported == true) {
                            com.example.widget.PrayerAppWidgetProvider.updateAllWidgets(context)
                            appWidgetManager.requestPinAppWidget(provider, null, null)
                        }
                        showPinWidgetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA040))
                ) {
                    Text("إضافة تلقائية الآن", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPinWidgetDialog = false }) {
                    Text("حسناً فهمت")
                }
            }
        )
    }
    }
}
