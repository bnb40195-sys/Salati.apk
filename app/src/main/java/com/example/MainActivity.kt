package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.AppPreferences
import com.example.data.local.IslamicRepository
import com.example.data.local.CustomContentManager
import com.example.data.models.*
import com.example.data.services.*
import com.example.ui.components.AppBottomNav
import com.example.ui.components.AppNavigationDrawer
import com.example.ui.components.AppTab
import com.example.ui.components.CitySelectionDialog
import com.example.ui.components.DrawerDestination
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.GoldAccent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

enum class SubScreen {
    ADVANCED_SETTINGS,
    CREATOR_STUDIO,
    ON_THIS_DAY,
    MONTHLY_CALENDAR,
    WIDGETS_STUDIO,
    GHAZWAT,
    PRAYER_ADJUSTMENT,
    ATHKAR_DUAS,
    SUNNAH_HADITH,
    UNIFIED_INVENTIONS_BATTLES,
    UNIFIED_PROPHETS_TAWBAH,
    UNIFIED_QUESTIONS,
    CAPITALS,
    UNIFIED_QURAN_SUNNAH,
    UNIFIED_ADHAN_REMINDER,
    ALERTS_AND_REMINDERS
}

class MainActivity : ComponentActivity() {

    private lateinit var appPreferences: AppPreferences
    private lateinit var repository: IslamicRepository
    private lateinit var customContentManager: CustomContentManager
    private lateinit var audioPlayerManager: AudioPlayerManager
    private lateinit var compassSensorManager: CompassSensorManager
    private lateinit var weatherService: WeatherService
    private lateinit var smartVoiceManager: SmartVoiceManager
    private lateinit var prayerNotificationManager: PrayerNotificationManager
    private lateinit var salawatReminderManager: SalawatReminderManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        appPreferences = AppPreferences(applicationContext)
        repository = IslamicRepository()
        customContentManager = CustomContentManager(applicationContext)
        audioPlayerManager = AudioPlayerManager(applicationContext)
        compassSensorManager = CompassSensorManager(applicationContext)
        weatherService = WeatherService(applicationContext, appPreferences)
        smartVoiceManager = SmartVoiceManager(applicationContext)
        prayerNotificationManager = PrayerNotificationManager(applicationContext)
        salawatReminderManager = SalawatReminderManager(applicationContext, audioPlayerManager, smartVoiceManager, prayerNotificationManager)

        setContent {
            val isDarkTheme by appPreferences.isDarkMode.collectAsStateWithLifecycle()
            val selectedCity by appPreferences.selectedCity.collectAsStateWithLifecycle()
            val calcMethod by appPreferences.calculationMethod.collectAsStateWithLifecycle()
            val juristicMethod by appPreferences.juristicMethod.collectAsStateWithLifecycle()
            val prayerOffsets by appPreferences.prayerOffsets.collectAsStateWithLifecycle()
            val playbackState by audioPlayerManager.playbackState.collectAsStateWithLifecycle()
            val selectedMuadhinId by appPreferences.selectedMuadhin.collectAsStateWithLifecycle()
            val selectedQariId by appPreferences.selectedQariId.collectAsStateWithLifecycle()
            val tasbihCount by appPreferences.tasbihCount.collectAsStateWithLifecycle()
            val tasbihTotal by appPreferences.tasbihTotal.collectAsStateWithLifecycle()
            val lastReadSurah by appPreferences.lastReadSurah.collectAsStateWithLifecycle()
            val azimuth by compassSensorManager.azimuth.collectAsStateWithLifecycle()
            val isSensorAvailable by compassSensorManager.isSensorAvailable.collectAsStateWithLifecycle()
            val weatherInfo by weatherService.weatherState.collectAsStateWithLifecycle()

            // Advanced Settings States
            val fridayFirstAdhan by appPreferences.fridayFirstAdhan.collectAsStateWithLifecycle()
            val fridaySecondAdhan by appPreferences.fridaySecondAdhan.collectAsStateWithLifecycle()
            val fridaySoundOnly by appPreferences.fridaySoundOnly.collectAsStateWithLifecycle()
            val fridayFirstAdhanSound by appPreferences.fridayFirstAdhanSound.collectAsStateWithLifecycle()
            val fridaySecondAdhanSound by appPreferences.fridaySecondAdhanSound.collectAsStateWithLifecycle()
            val salawatInterval by appPreferences.salawatIntervalMinutes.collectAsStateWithLifecycle()
            val salawatSoundType by appPreferences.salawatSoundType.collectAsStateWithLifecycle()
            val prePrayerBigMsg by appPreferences.prePrayerBigMessage.collectAsStateWithLifecycle()
            val prePrayerMinsBefore by appPreferences.prePrayerMinutesBefore.collectAsStateWithLifecycle()
            val prePrayerSoundType by appPreferences.prePrayerSoundType.collectAsStateWithLifecycle()
            val showWeatherHome by appPreferences.showWeatherHome.collectAsStateWithLifecycle()
            val timeFormat by appPreferences.timeFormat.collectAsStateWithLifecycle()
            val digitType by appPreferences.digitType.collectAsStateWithLifecycle()
            val ongoingPrayerNotification by appPreferences.ongoingPrayerNotification.collectAsStateWithLifecycle()
            val autoGpsLocation by appPreferences.autoGpsLocation.collectAsStateWithLifecycle()
            val quranFontSize by appPreferences.quranFontSize.collectAsStateWithLifecycle()
            val isSmartSpeaking by smartVoiceManager.isSpeaking.collectAsStateWithLifecycle()
            val smartRecitingAyahNumber by smartVoiceManager.currentSpokenAyahNumber.collectAsStateWithLifecycle()

            // Quran Bookmark, Riwayah & Translation States
            val bookmarkSurah by appPreferences.bookmarkSurah.collectAsStateWithLifecycle()
            val bookmarkAyah by appPreferences.bookmarkAyah.collectAsStateWithLifecycle()
            val bookmarkSurahName by appPreferences.bookmarkSurahName.collectAsStateWithLifecycle()
            val bookmarkAyahText by appPreferences.bookmarkAyahText.collectAsStateWithLifecycle()
            val hasActiveBookmark by appPreferences.hasActiveBookmark.collectAsStateWithLifecycle()
            val selectedRiwayahId by appPreferences.selectedRiwayahId.collectAsStateWithLifecycle()
            val selectedTranslationLang by appPreferences.selectedTranslationLang.collectAsStateWithLifecycle()
            val isTranslationVisible by appPreferences.isTranslationVisible.collectAsStateWithLifecycle()

            val hijriType by appPreferences.hijriCalendarType.collectAsStateWithLifecycle()
            val hijriAdjustment by appPreferences.hijriAdjustmentDays.collectAsStateWithLifecycle()
            val monthDisplayMode by appPreferences.monthDisplayMode.collectAsStateWithLifecycle()
            val utcMode by appPreferences.utcMode.collectAsStateWithLifecycle()
            val manualUtcHours by appPreferences.manualUtcHours.collectAsStateWithLifecycle()
            val bgTheme by appPreferences.appBackgroundTheme.collectAsStateWithLifecycle()
            val developerEmail by appPreferences.developerEmail.collectAsStateWithLifecycle()
            val developerSecQuestion by appPreferences.developerSecurityQuestion.collectAsStateWithLifecycle()
            val developerSecAnswer by appPreferences.developerSecurityAnswer.collectAsStateWithLifecycle()
            val isDeveloperUnlocked by appPreferences.isDeveloperUnlocked.collectAsStateWithLifecycle()
            val googleAccountEmail by appPreferences.googleAccountEmail.collectAsStateWithLifecycle()
            val isGoogleSignedIn by appPreferences.isGoogleSignedIn.collectAsStateWithLifecycle()
            val athkarReminderMode by appPreferences.athkarReminderMode.collectAsStateWithLifecycle()
            val quickStartEnabled by appPreferences.quickStartEnabled.collectAsStateWithLifecycle()
            val postPrayerDelayMinutes by appPreferences.postPrayerDelayMinutes.collectAsStateWithLifecycle()
            val widgetAccent by appPreferences.widgetAccentColor.collectAsStateWithLifecycle()
            val widgetTheme by appPreferences.widgetTheme.collectAsStateWithLifecycle()
            val widgetDhikr by appPreferences.widgetDhikr.collectAsStateWithLifecycle()
            val widgetShowTemp by appPreferences.widgetShowTemperature.collectAsStateWithLifecycle()
            val widgetTemp by appPreferences.widgetTemperature.collectAsStateWithLifecycle()
            val iqamaReminderEnabled by appPreferences.iqamaReminderEnabled.collectAsStateWithLifecycle()
            val iqamaMinutesAfter by appPreferences.iqamaMinutesAfter.collectAsStateWithLifecycle()
            val iqamaSoundType by appPreferences.iqamaSoundType.collectAsStateWithLifecycle()
            val morningAthkarReminder by appPreferences.morningAthkarReminder.collectAsStateWithLifecycle()
            val morningAthkarTime by appPreferences.morningAthkarTime.collectAsStateWithLifecycle()
            val morningAthkarSound by appPreferences.morningAthkarSound.collectAsStateWithLifecycle()
            val eveningAthkarReminder by appPreferences.eveningAthkarReminder.collectAsStateWithLifecycle()
            val eveningAthkarTime by appPreferences.eveningAthkarTime.collectAsStateWithLifecycle()
            val eveningAthkarSound by appPreferences.eveningAthkarSound.collectAsStateWithLifecycle()
            val postPrayerAthkarRem by appPreferences.postPrayerAthkarReminder.collectAsStateWithLifecycle()
            val postPrayerAthkarTime by appPreferences.postPrayerAthkarTime.collectAsStateWithLifecycle()
            val postPrayerAthkarSound by appPreferences.postPrayerAthkarSound.collectAsStateWithLifecycle()
            var postPrayerDismissed by remember { mutableStateOf(false) }

            // Custom Content Manager States (Sections, Wallpapers, Sounds, Stories, QA)
            val sectionsList by customContentManager.sections.collectAsStateWithLifecycle()
            val wallpapersList by customContentManager.wallpapers.collectAsStateWithLifecycle()
            val adhanAudiosList by customContentManager.adhanAudios.collectAsStateWithLifecycle()
            val tawbahStoriesList by customContentManager.tawbahStories.collectAsStateWithLifecycle()
            val prophetStoriesList by customContentManager.prophetStories.collectAsStateWithLifecycle()
            val islamicQAsList by customContentManager.islamicQAs.collectAsStateWithLifecycle()

            val currentWallpaper = remember(bgTheme, wallpapersList) {
                wallpapersList.find { it.id == bgTheme } ?: wallpapersList.firstOrNull()
            }

            val salawatAlert by salawatReminderManager.currentAlert.collectAsStateWithLifecycle()

            val scope = rememberCoroutineScope()
            var currentTab by remember { mutableStateOf(AppTab.HOME) }
            var currentSubScreen by remember { mutableStateOf<SubScreen?>(null) }
            var isDrawerOpen by remember { mutableStateOf(false) }
            var showCityDialog by remember { mutableStateOf(false) }
            var initialAthkarCat by remember { mutableStateOf("post_prayer") }

            // Custom added stories
            var customStories by remember { mutableStateOf<List<TawbahStory>>(emptyList()) }

            val currentDrawerDestination = remember(currentTab, currentSubScreen) {
                when (currentSubScreen) {
                    SubScreen.UNIFIED_QURAN_SUNNAH, SubScreen.SUNNAH_HADITH -> DrawerDestination.QURAN_AND_SUNNAH
                    SubScreen.UNIFIED_ADHAN_REMINDER, SubScreen.PRAYER_ADJUSTMENT -> DrawerDestination.ADHAN_AND_REMINDER
                    SubScreen.ALERTS_AND_REMINDERS -> DrawerDestination.PRAYER_REMINDER
                    SubScreen.CREATOR_STUDIO -> DrawerDestination.CREATOR_STUDIO
                    SubScreen.MONTHLY_CALENDAR -> DrawerDestination.CALENDAR
                    SubScreen.ON_THIS_DAY -> DrawerDestination.ON_THIS_DAY
                    SubScreen.ATHKAR_DUAS -> DrawerDestination.ATHKAR_DUAS
                    SubScreen.UNIFIED_INVENTIONS_BATTLES, SubScreen.GHAZWAT -> DrawerDestination.INVENTIONS_AND_BATTLES
                    SubScreen.UNIFIED_PROPHETS_TAWBAH -> DrawerDestination.PROPHETS_AND_TAWBAH
                    SubScreen.UNIFIED_QUESTIONS -> DrawerDestination.QUESTIONS_RELIGIOUS_AND_GENERAL
                    SubScreen.CAPITALS -> DrawerDestination.CAPITALS
                    SubScreen.ADVANCED_SETTINGS, SubScreen.WIDGETS_STUDIO -> DrawerDestination.SETTINGS
                    null -> when (currentTab) {
                        AppTab.HOME -> DrawerDestination.HOME
                        AppTab.ADHAN -> DrawerDestination.ADHAN_AND_REMINDER
                        AppTab.ATHKAR_DUAS -> DrawerDestination.ATHKAR_DUAS
                        AppTab.QIBLA -> DrawerDestination.QIBLA
                        AppTab.QURAN -> DrawerDestination.QURAN_AND_SUNNAH
                        AppTab.SETTINGS -> DrawerDestination.SETTINGS
                    }
                }
            }

            // Start salawat timer based on preference
            LaunchedEffect(salawatInterval, salawatSoundType) {
                salawatReminderManager.startSalawatTimer(salawatInterval, salawatSoundType)
            }

            // Prayer times state (100% offline mathematical calculation + live sync)
            var prayerTimes by remember {
                mutableStateOf<DayPrayerTimes?>(
                    PrayerTimeCalculator.calculatePrayerTimes(
                        latitude = selectedCity.latitude,
                        longitude = selectedCity.longitude,
                        method = calcMethod,
                        juristic = juristicMethod,
                        offsets = prayerOffsets,
                        cityName = selectedCity.nameAr,
                        countryName = selectedCity.countryAr
                    )
                )
            }

            val hijriDate = remember(selectedCity, hijriAdjustment) {
                PrayerTimeCalculator.getHijriDate(Date(), adjustmentDays = hijriAdjustment)
            }

            // Month prayer times
            val monthPrayerTimes = remember(selectedCity, calcMethod, juristicMethod, prayerOffsets) {
                val cal = Calendar.getInstance()
                MonthlyPrayerCalculator.generateMonthPrayerTimes(
                    city = selectedCity,
                    year = cal.get(Calendar.YEAR),
                    month = cal.get(Calendar.MONTH) + 1,
                    method = calcMethod,
                    juristic = juristicMethod,
                    offsets = prayerOffsets
                )
            }

            // Recalculate prayer times and refresh weather when city or settings change
            LaunchedEffect(selectedCity, calcMethod, juristicMethod, prayerOffsets) {
                prayerTimes = PrayerTimeCalculator.calculatePrayerTimes(
                    latitude = selectedCity.latitude,
                    longitude = selectedCity.longitude,
                    method = calcMethod,
                    juristic = juristicMethod,
                    offsets = prayerOffsets,
                    cityName = selectedCity.nameAr,
                    countryName = selectedCity.countryAr
                )

                weatherService.refreshWeather(selectedCity.latitude, selectedCity.longitude)

                val liveTimes = repository.getLivePrayerTimes(
                    city = selectedCity,
                    method = calcMethod,
                    juristic = juristicMethod,
                    offsets = prayerOffsets
                )
                prayerTimes = liveTimes
            }

            var isGpsDetecting by remember { mutableStateOf(false) }

            val locationPermissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
                androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->
                val isGranted = permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                                permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] == true
                if (isGranted) {
                    scope.launch {
                        isGpsDetecting = true
                        val loc = com.example.data.services.LocationHelper.getCurrentLocation(applicationContext)
                        if (loc != null) {
                            appPreferences.setCity(loc)
                            weatherService.refreshWeather(loc.latitude, loc.longitude)
                        }
                        isGpsDetecting = false
                    }
                }
            }

            val notificationPermissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
                androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
            ) { /* granted */ }

            val onDetectGpsLocation: () -> Unit = {
                if (com.example.data.services.LocationHelper.hasLocationPermission(applicationContext)) {
                    scope.launch {
                        isGpsDetecting = true
                        val loc = com.example.data.services.LocationHelper.getCurrentLocation(applicationContext)
                        if (loc != null) {
                            appPreferences.setCity(loc)
                            weatherService.refreshWeather(loc.latitude, loc.longitude)
                        }
                        isGpsDetecting = false
                    }
                } else {
                    locationPermissionLauncher.launch(
                        arrayOf(
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            }

            var showStrictPermissionsDialog by remember { mutableStateOf(false) }

            // On Startup: Strict permissions verification and auto location update
            LaunchedEffect(Unit) {
                val hasLoc = com.example.data.services.LocationHelper.hasLocationPermission(applicationContext)
                val hasNotif = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    androidx.core.content.ContextCompat.checkSelfPermission(
                        applicationContext,
                        android.Manifest.permission.POST_NOTIFICATIONS
                    ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                } else true

                if (!hasLoc || !hasNotif) {
                    showStrictPermissionsDialog = true
                }

                if (hasLoc) {
                    scope.launch {
                        val loc = com.example.data.services.LocationHelper.getCurrentLocation(applicationContext)
                        if (loc != null) {
                            appPreferences.setCity(loc)
                            weatherService.refreshWeather(loc.latitude, loc.longitude)
                        }
                    }
                }
                scope.launch {
                    weatherService.refreshWeather(selectedCity.latitude, selectedCity.longitude)
                }
            }

            // Background & Notification Center Ongoing Status Bar Notification
            LaunchedEffect(prayerTimes, weatherInfo, ongoingPrayerNotification, digitType, timeFormat, prePrayerMinsBefore) {
                if (ongoingPrayerNotification && prayerTimes != null) {
                    val now = System.currentTimeMillis()
                    val prayers = listOf(
                        PrayerType.FAJR to prayerTimes!!.fajr,
                        PrayerType.SUNRISE to prayerTimes!!.sunrise,
                        PrayerType.DHUHR to prayerTimes!!.dhuhr,
                        PrayerType.ASR to prayerTimes!!.asr,
                        PrayerType.MAGHRIB to prayerTimes!!.maghrib,
                        PrayerType.ISHA to prayerTimes!!.isha
                    )
                    val nextPair = prayers.firstOrNull { it.second > now }
                        ?: (PrayerType.FAJR to (prayerTimes!!.fajr + 86400000L))
                    val nextP = nextPair.first
                    val nextMillis = nextPair.second
                    val diffMinutes = ((nextMillis - now) / 60000).coerceAtLeast(0)

                    val timeFormatter = if (timeFormat == "24h") java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
                    else java.text.SimpleDateFormat("hh:mm a", java.util.Locale("ar"))

                    val nextTimeFormatted = com.example.data.services.DigitFormatter.format(timeFormatter.format(java.util.Date(nextMillis)), digitType)
                    val countdownText = com.example.data.services.DigitFormatter.format("$diffMinutes دقيقة", digitType)
                    val prePrayerRemainingMins = (diffMinutes - prePrayerMinsBefore).coerceAtLeast(0)
                    val prePrayerCountdownText = if (prePrayerRemainingMins > 0) com.example.data.services.DigitFormatter.format("$prePrayerRemainingMins دقيقة", digitType) else "حان وقت الدعاء قبل الأذان"
                    val tempText = com.example.data.services.DigitFormatter.format("${weatherInfo.temperatureC.toInt()}°م (${weatherInfo.conditionAr})", digitType)
                    val todayEvt = com.example.data.local.OnThisDayDataset.getEventsForPhoneToday().events.firstOrNull()?.titleAr
                        ?: "يوم مبارك عامر بالذكر والطاعات"

                    prayerNotificationManager.updateOngoingPrayerNotification(
                        cityName = selectedCity.nameAr,
                        nextPrayerName = nextP.arName,
                        nextPrayerTimeFormatted = nextTimeFormatted,
                        countdownText = countdownText,
                        prePrayerCountdownText = prePrayerCountdownText,
                        temperatureText = tempText,
                        todayEventText = todayEvt,
                        enabled = ongoingPrayerNotification
                    )
                } else if (!ongoingPrayerNotification) {
                    prayerNotificationManager.cancelOngoingNotification()
                }
            }

            // Handle back navigation per guidelines:
            // 0. If drawer is open, close it
            // 1. If inside a sub-screen, return to main tab
            // 2. If on a sub-tab, return to Home
            if (isDrawerOpen) {
                BackHandler {
                    isDrawerOpen = false
                }
            } else if (currentSubScreen != null) {
                BackHandler {
                    currentSubScreen = null
                }
            } else if (currentTab != AppTab.HOME) {
                BackHandler {
                    currentTab = AppTab.HOME
                }
            }

            val triggerCenterNotification: () -> Unit = {
                val now = System.currentTimeMillis()
                val prayers = if (prayerTimes != null) listOf(
                    PrayerType.FAJR to prayerTimes!!.fajr,
                    PrayerType.SUNRISE to prayerTimes!!.sunrise,
                    PrayerType.DHUHR to prayerTimes!!.dhuhr,
                    PrayerType.ASR to prayerTimes!!.asr,
                    PrayerType.MAGHRIB to prayerTimes!!.maghrib,
                    PrayerType.ISHA to prayerTimes!!.isha
                ) else emptyList()
                val nextPair = prayers.firstOrNull { it.second > now }
                    ?: if (prayerTimes != null) (PrayerType.FAJR to (prayerTimes!!.fajr + 86400000L)) else null
                val nextP = nextPair?.first?.arName ?: "الصلاة"
                val diffMinutes = if (nextPair != null) ((nextPair.second - now) / 60000).coerceAtLeast(0) else 0
                val countdownText = com.example.data.services.DigitFormatter.format("$diffMinutes دقيقة", digitType)
                val prePrayerRemainingMins = (diffMinutes - prePrayerMinsBefore).coerceAtLeast(0)
                val prePrayerCountdownText = if (prePrayerRemainingMins > 0) com.example.data.services.DigitFormatter.format("$prePrayerRemainingMins دقيقة", digitType) else "حان وقت الدعاء قبل الأذان"
                val tempText = com.example.data.services.DigitFormatter.format("${weatherInfo.temperatureC.toInt()}°م (${weatherInfo.conditionAr})", digitType)
                val todayEvt = com.example.data.local.OnThisDayDataset.getEventsForPhoneToday().events.firstOrNull()?.titleAr
                    ?: "اليوم المبارك في التاريخ الإسلامي"

                prayerNotificationManager.postCenterNotification(
                    title = "مركز إشعارات صلاتي 🕌 • ${selectedCity.nameAr}",
                    message = "مواقيت الصلاة والتنبيهات المباشرة لمدينة ${selectedCity.nameAr}",
                    subText = "مركز الإشعارات • صلاتي",
                    prayerCountdown = "متبقي $countdownText على صلاة $nextP",
                    prePrayerCountdown = prePrayerCountdownText,
                    temperatureText = tempText,
                    todayEventText = todayEvt
                )
            }

            MyApplicationTheme(darkTheme = isDarkTheme) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize()
                    ) { innerPadding ->
                    val screenModifier = Modifier.padding(innerPadding)

                    if (currentSubScreen != null) {
                        when (currentSubScreen) {
                            SubScreen.ADVANCED_SETTINGS -> {
                                SettingsScreen(
                                    selectedCity = selectedCity,
                                    calculationMethod = calcMethod,
                                    juristicMethod = juristicMethod,
                                    prayerOffsets = prayerOffsets,
                                    weatherInfo = weatherInfo,
                                    isDarkMode = isDarkTheme,
                                    fridayFirstAdhan = fridayFirstAdhan,
                                    fridaySecondAdhan = fridaySecondAdhan,
                                    fridaySoundOnly = fridaySoundOnly,
                                    salawatIntervalMinutes = salawatInterval,
                                    prePrayerBigMessage = prePrayerBigMsg,
                                    prePrayerMinutesBefore = prePrayerMinsBefore,
                                    hijriType = hijriType,
                                    hijriAdjustment = hijriAdjustment,
                                    monthDisplayMode = monthDisplayMode,
                                    utcMode = utcMode,
                                    manualUtcHours = manualUtcHours,
                                    currentBgTheme = bgTheme,
                                    sections = sectionsList,
                                    wallpapers = wallpapersList,
                                    adhanAudios = adhanAudiosList,
                                    tawbahStories = tawbahStoriesList,
                                    prophetStories = prophetStoriesList,
                                    islamicQAs = islamicQAsList,
                                    timeFormat = timeFormat,
                                    onUpdateTimeFormat = { appPreferences.setTimeFormat(it) },
                                    digitType = digitType,
                                    onUpdateDigitType = { appPreferences.setDigitType(it) },
                                    ongoingNotification = ongoingPrayerNotification,
                                    onToggleOngoingNotification = { appPreferences.setOngoingPrayerNotification(it) },
                                    onSendCenterNotification = triggerCenterNotification,
                                    onDetectLocationGps = onDetectGpsLocation,
                                    isGpsDetecting = isGpsDetecting,
                                    initialTab = 1,
                                    onOpenCitySelector = { showCityDialog = true },
                                    onSaveCityCoords = { lat, lng, name ->
                                        appPreferences.setCity(selectedCity.copy(latitude = lat, longitude = lng, nameAr = name))
                                    },
                                    onSelectCalculationMethod = { appPreferences.setCalculationMethod(it) },
                                    onSelectJuristicMethod = { appPreferences.setJuristicMethod(it) },
                                    onSetPrayerOffset = { type, off -> appPreferences.setPrayerOffset(type, off) },
                                    onRefreshWeather = {
                                        scope.launch {
                                            weatherService.refreshWeather(selectedCity.latitude, selectedCity.longitude)
                                        }
                                    },
                                    onToggleDarkMode = { appPreferences.setDarkMode(it) },
                                    onUpdateFridaySettings = { first, second, soundOnly ->
                                        appPreferences.setFridaySettings(first, second, soundOnly)
                                    },
                                    onUpdateSalawatInterval = { appPreferences.setSalawatInterval(it) },
                                    salawatSoundType = salawatSoundType,
                                    onUpdateSalawatSoundType = { appPreferences.setSalawatSoundType(it) },
                                    onUpdatePrePrayerSettings = { enabled, mins ->
                                        appPreferences.setPrePrayerBigMessage(enabled, mins)
                                    },
                                    onUpdateHijriSettings = { type, adj, mode ->
                                        appPreferences.setHijriSettings(type, adj, mode)
                                    },
                                    onUpdateUtcSettings = { mode, hours ->
                                        appPreferences.setUtcSettings(mode, hours)
                                    },
                                    onSelectBgTheme = { appPreferences.setAppBackgroundTheme(it) },
                                    onOpenCreatorStudio = { currentSubScreen = SubScreen.CREATOR_STUDIO },
                                    onOpenWidgetsStudio = { currentSubScreen = SubScreen.WIDGETS_STUDIO },
                                    onToggleSection = { id, enabled -> customContentManager.toggleSection(id, enabled) },
                                    onDeleteSection = { id -> customContentManager.deleteSection(id) },
                                    onAddSection = { title, sub -> customContentManager.addCustomSection(title, sub) },
                                    onResetSections = { customContentManager.resetSections() },
                                    onAddWallpaper = { name, desc, col, url -> customContentManager.addCustomWallpaper(name, desc, col, url) },
                                    onDeleteWallpaper = { id -> customContentManager.deleteWallpaper(id) },
                                    onResetWallpapers = { customContentManager.resetWallpapers() },
                                    onAddSound = { name, desc, url, cat -> customContentManager.addCustomSound(name, desc, url, cat) },
                                    onDeleteSound = { id -> customContentManager.deleteSound(id) },
                                    onResetSounds = { customContentManager.resetSounds() },
                                    onAddTawbahStory = { t, p, d, s, m -> customContentManager.addCustomTawbahStory(t, p, d, s, m) },
                                    onDeleteTawbahStory = { id -> customContentManager.deleteTawbahStory(id) },
                                    onResetTawbahStories = { customContentManager.resetTawbahStories() },
                                    onAddProphetStory = { n, t, v, m, l, s -> customContentManager.addCustomProphetStory(n, t, v, m, l, s) },
                                    onDeleteProphetStory = { id -> customContentManager.deleteProphetStory(id) },
                                    onResetProphetStories = { customContentManager.resetProphetStories() },
                                    onAddQA = { q, a, c, e -> customContentManager.addCustomQA(q, a, c, e) },
                                    onDeleteQA = { id -> customContentManager.deleteQA(id) },
                                    onResetQAs = { customContentManager.resetQAs() },
                                    onPlayTestAudio = { url, title -> audioPlayerManager.playAudio(url, title, "تجربة صوتية") },
                                    onOpenPrayerAdjustments = { currentSubScreen = SubScreen.PRAYER_ADJUSTMENT },
                                    onOpenAthkarDuas = { cat ->
                                        initialAthkarCat = cat
                                        currentSubScreen = SubScreen.ATHKAR_DUAS
                                    },
                                    googleAccountEmail = googleAccountEmail,
                                    isGoogleSignedIn = isGoogleSignedIn,
                                    onUpdateGoogleAccount = { email, signedIn -> appPreferences.setGoogleAccount(email, signedIn) },
                                    isDeveloperUnlocked = isDeveloperUnlocked,
                                    onUnlockDeveloper = { email, ans -> appPreferences.checkAndUnlockDeveloper(email, ans) },
                                    onLockDeveloper = { appPreferences.lockDeveloper() },
                                    athkarReminderMode = athkarReminderMode,
                                    onUpdateAthkarReminderMode = { appPreferences.setAthkarReminderMode(it) },
                                    quickStartEnabled = quickStartEnabled,
                                    onUpdateQuickStartEnabled = { appPreferences.setQuickStartEnabled(it) },
                                    postPrayerDelayMinutes = postPrayerDelayMinutes,
                                    onUpdatePostPrayerDelayMinutes = { appPreferences.setPostPrayerDelayMinutes(it) },
                                    onBack = { currentSubScreen = null },
                                    modifier = screenModifier
                                )
                            }
                            SubScreen.CREATOR_STUDIO -> {
                                CreatorStudioScreen(
                                    developerEmail = developerEmail,
                                    securityQuestion = developerSecQuestion,
                                    securityAnswer = developerSecAnswer,
                                    isUnlocked = isDeveloperUnlocked,
                                    customStories = customStories,
                                    prophetsList = prophetStoriesList,
                                    tawbahStoriesList = tawbahStoriesList,
                                    adhanAudiosList = adhanAudiosList,
                                    sectionsList = sectionsList,
                                    wallpapersList = wallpapersList,
                                    onUnlockCreator = { inputEmail, inputAnswer ->
                                        appPreferences.checkAndUnlockDeveloper(inputEmail, inputAnswer)
                                    },
                                    onChangeDeveloperEmail = { appPreferences.setDeveloperEmail(it) },
                                    onAddCustomStory = { title, person, details ->
                                        customContentManager.addCustomTawbahStory(title, person, details, "مضافة من صانع التطبيق", "العبرة والرجوع إلى الله")
                                    },
                                    onUpdateTawbahStory = { id, title, person, details, source, moral ->
                                        customContentManager.updateTawbahStory(id, title, person, details, source, moral)
                                    },
                                    onDeleteCustomStory = { id ->
                                        customContentManager.deleteTawbahStory(id)
                                    },
                                    onAddProphetStory = { name, title, verses, miracles, lessons, story ->
                                        customContentManager.addCustomProphetStory(name, title, verses, miracles, lessons, story)
                                    },
                                    onUpdateProphetStory = { id, name, title, summary, fullStory, miracles, lessons ->
                                        customContentManager.updateProphetStory(id, name, title, summary, fullStory, miracles, lessons)
                                    },
                                    onDeleteProphetStory = { id ->
                                        customContentManager.deleteProphetStory(id)
                                    },
                                    onAddSound = { name, desc, url, cat ->
                                        customContentManager.addCustomSound(name, desc, url, cat)
                                    },
                                    onDeleteSound = { id ->
                                        customContentManager.deleteSound(id)
                                    },
                                    onToggleSection = { id, enabled ->
                                        customContentManager.toggleSection(id, enabled)
                                    },
                                    onDeleteSection = { id ->
                                        customContentManager.deleteSection(id)
                                    },
                                    onAddSection = { title, sub ->
                                        customContentManager.addCustomSection(title, sub)
                                    },
                                    onResetSections = {
                                        customContentManager.resetSections()
                                    },
                                    onDeleteWallpaper = { id ->
                                        customContentManager.deleteWallpaper(id)
                                    },
                                    onAddWallpaper = { name, desc, col, url ->
                                        customContentManager.addCustomWallpaper(name, desc, col, url)
                                    },
                                    onResetWallpapers = {
                                        customContentManager.resetWallpapers()
                                    },
                                    onPlayAudio = { url, name ->
                                        if (playbackState.isPlaying) {
                                            audioPlayerManager.stopAudio()
                                        } else {
                                            audioPlayerManager.playAudio(url, name, "استماع للتسجيل")
                                        }
                                    },
                                    onBack = { currentSubScreen = null },
                                    modifier = screenModifier
                                )
                            }
                            SubScreen.ON_THIS_DAY -> {
                                OnThisDayScreen(
                                    eventsList = repository.getOnThisDayEvents(),
                                    dayCollections = repository.getOnThisDayCollections(),
                                    onOpenDrawer = { isDrawerOpen = true },
                                    onBack = { currentSubScreen = null },
                                    modifier = screenModifier
                                )
                            }
                            SubScreen.SUNNAH_HADITH -> {
                                SunnahScreen(
                                    hadiths = repository.getSunnahHadiths(),
                                    onSpeakHadith = { smartVoiceManager.speakDhikr(it) },
                                    onStopSpeaking = { smartVoiceManager.stop() },
                                    isSpeaking = isSmartSpeaking,
                                    onBack = { currentSubScreen = null },
                                    modifier = screenModifier
                                )
                            }
                            SubScreen.MONTHLY_CALENDAR -> {
                                MonthlyCalendarScreen(
                                    selectedCity = selectedCity,
                                    monthPrayerTimes = monthPrayerTimes,
                                    hijriEvents = repository.getHijriEvents(),
                                    onBack = { currentSubScreen = null },
                                    onOpenDrawer = { isDrawerOpen = true },
                                    modifier = screenModifier
                                )
                            }
                            SubScreen.WIDGETS_STUDIO -> {
                                WidgetsPreviewScreen(
                                    selectedCity = selectedCity,
                                    prayerTimes = prayerTimes,
                                    hijriDate = hijriDate,
                                    initialTheme = widgetTheme,
                                    initialDhikr = widgetDhikr,
                                    initialShowTemp = widgetShowTemp,
                                    initialTemp = widgetTemp,
                                    initialAccent = widgetAccent,
                                    onSaveConfig = { theme, dhikr, showTemp, temp, accent ->
                                        appPreferences.setWidgetCustomization(theme, dhikr, showTemp, temp, accent)
                                    },
                                    onBack = { currentSubScreen = null },
                                    modifier = screenModifier
                                )
                            }
                            SubScreen.GHAZWAT -> {
                                GhazwatScreen(
                                    ghazwatList = repository.getBattles(),
                                    majorConquests = repository.getMajorConquests(),
                                    majorSaraya = repository.getMajorSaraya(),
                                    onBack = { currentSubScreen = null },
                                    modifier = screenModifier
                                )
                            }
                            SubScreen.PRAYER_ADJUSTMENT -> {
                                PrayerAdjustmentScreen(
                                    selectedCity = selectedCity,
                                    prayerTimes = prayerTimes,
                                    prayerOffsets = prayerOffsets,
                                    salawatIntervalMinutes = salawatInterval,
                                    salawatSoundType = salawatSoundType,
                                    prePrayerMinutesBefore = prePrayerMinsBefore,
                                    prePrayerSoundType = prePrayerSoundType,
                                    prePrayerBigMessage = prePrayerBigMsg,
                                    onSetOffset = { type, off -> appPreferences.setPrayerOffset(type, off) },
                                    onResetOffsets = {
                                        PrayerType.values().forEach { appPreferences.setPrayerOffset(it, 0) }
                                    },
                                    onUpdateSalawatInterval = { appPreferences.setSalawatInterval(it) },
                                    onUpdateSalawatSoundType = { appPreferences.setSalawatSoundType(it) },
                                    onUpdatePrePrayerSettings = { en, mins, sound ->
                                        appPreferences.setPrePrayerSettings(en, mins, "إِنَّ الصَّلَاةَ كَانَتْ عَلَى الْمُؤْمِنِينَ كِتَابًا مَّوْقُوتًا ۝ صدق الله العظيم")
                                        appPreferences.setPrePrayerSoundType(sound)
                                    },
                                    onPlayPrePrayerTest = { smartVoiceManager.playPrePrayerReminder(it) },
                                    onPlaySalawatTest = { smartVoiceManager.playSalawatReminder(it) },
                                    onOpenPostPrayerAthkar = {
                                        initialAthkarCat = "post_prayer"
                                        currentSubScreen = SubScreen.ATHKAR_DUAS
                                    },
                                    onBack = { currentSubScreen = null },
                                    modifier = screenModifier
                                )
                            }
                            SubScreen.ATHKAR_DUAS -> {
                                AthkarDuasScreen(
                                    adhkarList = repository.getAdhkar(),
                                    initialCategory = initialAthkarCat,
                                    onSpeakDhikr = { smartVoiceManager.speakDhikr(it) },
                                    onStopSpeaking = { smartVoiceManager.stop() },
                                    onBack = { currentSubScreen = null },
                                    modifier = screenModifier
                                )
                            }
                            SubScreen.UNIFIED_INVENTIONS_BATTLES -> {
                                UnifiedInventionsBattlesScreen(
                                    battles = repository.getBattles(),
                                    majorConquests = repository.getMajorConquests(),
                                    majorSaraya = repository.getMajorSaraya(),
                                    inventions = repository.getInventions(),
                                    onBack = { currentSubScreen = null },
                                    modifier = screenModifier
                                )
                            }
                            SubScreen.UNIFIED_PROPHETS_TAWBAH -> {
                                UnifiedProphetsTawbahScreen(
                                    prophets = prophetStoriesList,
                                    tawbahStories = tawbahStoriesList + customStories,
                                    onBack = { currentSubScreen = null },
                                    onOpenDrawer = { isDrawerOpen = true },
                                    onOpenCreatorStudio = { currentSubScreen = SubScreen.CREATOR_STUDIO },
                                    onAddProphetStory = { name, title, verses, miracles, lessons, story ->
                                        customContentManager.addCustomProphetStory(name, title, verses, miracles, lessons, story)
                                    },
                                    onDeleteProphetStory = { id ->
                                        customContentManager.deleteProphetStory(id)
                                    },
                                    onAddTawbahStory = { title, person, details, source, moral ->
                                        customContentManager.addCustomTawbahStory(title, person, details, source, moral)
                                    },
                                    onDeleteTawbahStory = { id ->
                                        customContentManager.deleteTawbahStory(id)
                                    },
                                    modifier = screenModifier
                                )
                            }
                            SubScreen.UNIFIED_ADHAN_REMINDER -> {
                                UnifiedAdhanReminderScreen(
                                    prayerTimes = prayerTimes,
                                    playbackState = playbackState,
                                    selectedMuadhinId = selectedMuadhinId,
                                    adhanAudiosList = adhanAudiosList,
                                    onSelectMuadhin = { id ->
                                        appPreferences.setSelectedMuadhin(id)
                                        val adhan = adhanAudiosList.find { it.id == id }
                                        if (adhan != null) {
                                            audioPlayerManager.playAudio(adhan.audioUrl, adhan.nameAr, "النداء للصلاة")
                                        }
                                    },
                                    onTestAudio = { url, name ->
                                        if (playbackState.isPlaying) {
                                            audioPlayerManager.stopAudio()
                                        } else {
                                            audioPlayerManager.playAudio(url, name, "استماع للتنبيه الصوتي")
                                        }
                                    },
                                    onTogglePlayPause = { audioPlayerManager.togglePlayPause() },
                                    onStopAudio = { audioPlayerManager.stopAudio() },
                                    fridayFirstAdhan = fridayFirstAdhan,
                                    fridaySecondAdhan = fridaySecondAdhan,
                                    fridaySoundOnly = fridaySoundOnly,
                                    fridayFirstAdhanSoundId = fridayFirstAdhanSound,
                                    fridaySecondAdhanSoundId = fridaySecondAdhanSound,
                                    onUpdateFridaySettings = { first, second, soundOnly ->
                                        appPreferences.setFridaySettings(first, second, soundOnly)
                                    },
                                    onUpdateFridayFirstSound = { appPreferences.setFridayFirstAdhanSound(it) },
                                    onUpdateFridaySecondSound = { appPreferences.setFridaySecondAdhanSound(it) },
                                    prePrayerBigMessage = prePrayerBigMsg,
                                    prePrayerMinutesBefore = prePrayerMinsBefore,
                                    prePrayerSoundType = prePrayerSoundType,
                                    onUpdatePrePrayerSettings = { enabled, mins, sound ->
                                        appPreferences.setPrePrayerSettings(enabled, mins, "إِنَّ الصَّلَاةَ كَانَتْ عَلَى الْمُؤْمِنِينَ كِتَابًا مَّوْقُوتًا ۝ صدق الله العظيم")
                                        appPreferences.setPrePrayerSoundType(sound)
                                    },
                                    iqamaReminderEnabled = iqamaReminderEnabled,
                                    iqamaMinutesAfter = iqamaMinutesAfter,
                                    iqamaSoundType = iqamaSoundType,
                                    onUpdateIqamaSettings = { enabled, mins, sound ->
                                        appPreferences.setIqamaReminder(enabled, mins, sound)
                                    },
                                    morningAthkarReminder = morningAthkarReminder,
                                    morningAthkarTime = morningAthkarTime,
                                    onUpdateMorningAthkar = { enabled, time ->
                                        appPreferences.setMorningAthkarSettings(enabled, time)
                                    },
                                    eveningAthkarReminder = eveningAthkarReminder,
                                    eveningAthkarTime = eveningAthkarTime,
                                    onUpdateEveningAthkar = { enabled, time ->
                                        appPreferences.setEveningAthkarSettings(enabled, time)
                                    },
                                    morningAthkarSoundId = morningAthkarSound,
                                    onUpdateMorningAthkarSound = { appPreferences.setMorningAthkarSound(it) },
                                    eveningAthkarSoundId = eveningAthkarSound,
                                    onUpdateEveningAthkarSound = { appPreferences.setEveningAthkarSound(it) },
                                    postPrayerAthkarReminder = postPrayerAthkarRem,
                                    postPrayerAthkarTime = postPrayerAthkarTime,
                                    onUpdatePostPrayerAthkar = { enabled, time ->
                                        appPreferences.setPostPrayerAthkarSettings(enabled, time)
                                    },
                                    postPrayerAthkarSoundId = postPrayerAthkarSound,
                                    onUpdatePostPrayerAthkarSound = { appPreferences.setPostPrayerAthkarSound(it) },
                                    salawatIntervalMinutes = salawatInterval,
                                    salawatSoundType = salawatSoundType,
                                    onUpdateSalawatSettings = { mins, sound ->
                                        appPreferences.setSalawatInterval(mins)
                                        appPreferences.setSalawatSoundType(sound)
                                    },
                                    onPlaySalawatTest = { smartVoiceManager.playSalawatReminder(it) },
                                    prayerOffsets = prayerOffsets,
                                    onSetOffset = { type, off -> appPreferences.setPrayerOffset(type, off) },
                                    onResetOffsets = {
                                        PrayerType.values().forEach { appPreferences.setPrayerOffset(it, 0) }
                                    },
                                    onAddSound = { name, desc, url, cat ->
                                        customContentManager.addCustomSound(name, desc, url, cat)
                                    },
                                    onDeleteSound = { id ->
                                        customContentManager.deleteSound(id)
                                    },
                                    onOpenCreatorStudio = { if (isDeveloperUnlocked) currentSubScreen = SubScreen.CREATOR_STUDIO },
                                    isDeveloperUnlocked = isDeveloperUnlocked,
                                    onSendCenterNotification = triggerCenterNotification,
                                    onOpenDrawer = { isDrawerOpen = true },
                                    postPrayerDelayMinutes = postPrayerDelayMinutes,
                                    onUpdatePostPrayerDelayMinutes = { appPreferences.setPostPrayerDelayMinutes(it) },
                                    onBack = { currentSubScreen = null },
                                    modifier = screenModifier
                                )
                            }
                            SubScreen.ALERTS_AND_REMINDERS -> {
                                AlertsAndRemindersScreen(
                                    onBack = { currentSubScreen = null },
                                    onOpenDrawer = { isDrawerOpen = true },
                                    postPrayerDelayMinutes = postPrayerDelayMinutes,
                                    onUpdatePostPrayerDelay = { appPreferences.setPostPrayerDelayMinutes(it) },
                                    onSendCenterNotification = triggerCenterNotification,
                                    onPlayTestSound = { soundType ->
                                        audioPlayerManager.playAudio("https://server8.mp3quran.net/afs/001.mp3", "تنبيه صوتي", "تجربة")
                                    },
                                    modifier = screenModifier
                                )
                            }
                            SubScreen.UNIFIED_QUESTIONS -> {
                                UnifiedQuestionsScreen(
                                    religiousQAs = repository.getQAs(),
                                    generalQAs = repository.getGeneralQAs(),
                                    onBack = { currentSubScreen = null },
                                    modifier = screenModifier
                                )
                            }
                            SubScreen.CAPITALS -> {
                                CapitalsScreen(
                                    capitals = repository.getCapitals(),
                                    onBack = { currentSubScreen = null },
                                    modifier = screenModifier
                                )
                            }
                            SubScreen.UNIFIED_QURAN_SUNNAH -> {
                                val allQaris = remember(selectedRiwayahId) {
                                    repository.getQarisForRiwayah(selectedRiwayahId).ifEmpty {
                                        repository.getQaris()
                                    }
                                }
                                val currentQari = remember(allQaris, selectedQariId) {
                                    allQaris.find { it.id == selectedQariId } ?: allQaris.first()
                                }
                                UnifiedQuranSunnahScreen(
                                    surahs = repository.getSurahs(),
                                    qaris = allQaris,
                                    selectedQari = currentQari,
                                    playbackState = playbackState,
                                    lastReadSurahNumber = lastReadSurah,
                                    onSelectQari = { qari -> appPreferences.setSelectedQariId(qari.id) },
                                    onPlaySurah = { surah, qari ->
                                        val formattedNum = String.format("%03d", surah.number)
                                        val audioUrl = "${qari.serverUrl}$formattedNum.mp3"
                                        audioPlayerManager.playAudio(
                                            url = audioUrl,
                                            title = "سورة ${surah.nameAr}",
                                            subtitle = qari.nameAr
                                        )
                                    },
                                    onTogglePlayPause = { audioPlayerManager.togglePlayPause() },
                                    onStopAudio = { audioPlayerManager.stopAudio() },
                                    onLoadAyahs = { num -> repository.getSurahAyahs(num) },
                                    onSaveLastRead = { surah, ayah -> appPreferences.setLastRead(surah, ayah) },
                                    bookmarkSurahNumber = bookmarkSurah,
                                    bookmarkAyahNumber = bookmarkAyah,
                                    bookmarkSurahName = bookmarkSurahName,
                                    bookmarkAyahText = bookmarkAyahText,
                                    hasActiveBookmark = hasActiveBookmark,
                                    onSaveBookmark = { sNum, sName, aNum, aText ->
                                        appPreferences.setBookmark(sNum, sName, aNum, aText)
                                    },
                                    onClearBookmark = { appPreferences.clearBookmark() },
                                    selectedRiwayahId = selectedRiwayahId,
                                    onSelectRiwayah = { riwayahId ->
                                        appPreferences.setSelectedRiwayahId(riwayahId)
                                        val qarisForRiwayah = repository.getQarisForRiwayah(riwayahId)
                                        if (qarisForRiwayah.isNotEmpty()) {
                                            appPreferences.setSelectedQariId(qarisForRiwayah.first().id)
                                        }
                                    },
                                    selectedTranslationLang = selectedTranslationLang,
                                    isTranslationVisible = isTranslationVisible,
                                    onSelectTranslationLang = { appPreferences.setSelectedTranslationLang(it) },
                                    onToggleTranslationVisibility = { appPreferences.setTranslationVisible(it) },
                                    savedFontSize = quranFontSize,
                                    onUpdateFontSize = { appPreferences.setQuranFontSize(it) },
                                    hadiths = repository.getSunnahHadiths(),
                                    onSpeakHadith = { smartVoiceManager.speakDhikr(it) },
                                    onStopSpeaking = { smartVoiceManager.stop() },
                                    isSpeaking = isSmartSpeaking,
                                    onPlayAudio = { url, title, sub -> audioPlayerManager.playAudio(url, title, sub) },
                                    onOpenDrawer = { isDrawerOpen = true },
                                    onBack = { currentSubScreen = null },
                                    modifier = screenModifier
                                )
                            }
                            null -> Unit
                        }
                    } else {
                        when (currentTab) {
                            AppTab.HOME -> {
                                HomeScreen(
                                    prayerTimes = prayerTimes,
                                    hijriDate = hijriDate,
                                    selectedCity = selectedCity,
                                    weatherInfo = weatherInfo,
                                    showWeatherOnHome = showWeatherHome,
                                    timeFormat = timeFormat,
                                    digitType = digitType,
                                    onDetectLocationGps = onDetectGpsLocation,
                                    isGpsDetecting = isGpsDetecting,
                                    salawatAlertVisible = salawatAlert.isVisible,
                                    prePrayerMessageVisible = prePrayerBigMsg,
                                    postPrayerReminderVisible = postPrayerAthkarRem && !postPrayerDismissed,
                                    postPrayerAthkarTime = postPrayerAthkarTime,
                                    onDismissPostPrayerReminder = { postPrayerDismissed = true },
                                    onNavigateTab = { currentTab = it },
                                    onOpenCitySelector = { showCityDialog = true },
                                    onOpenDrawer = { isDrawerOpen = true },
                                    onPlayAdhan = {
                                        val adhan = adhanAudiosList.find { it.id == selectedMuadhinId }
                                            ?: adhanAudiosList.firstOrNull()
                                        if (adhan != null) {
                                            audioPlayerManager.playAudio(
                                                url = adhan.audioUrl,
                                                title = adhan.nameAr,
                                                subtitle = "أذان بصوت ندي"
                                            )
                                        }
                                        currentSubScreen = SubScreen.UNIFIED_ADHAN_REMINDER
                                    },
                                    onOpenMonthlyCalendar = { currentSubScreen = SubScreen.MONTHLY_CALENDAR },
                                    onOpenOnThisDay = { currentSubScreen = SubScreen.ON_THIS_DAY },
                                    onOpenSunnah = { currentSubScreen = SubScreen.SUNNAH_HADITH },
                                    onOpenGhazwat = { currentSubScreen = SubScreen.GHAZWAT },
                                    onOpenAdvancedSettings = { currentSubScreen = SubScreen.ADVANCED_SETTINGS },
                                    onOpenWidgetsStudio = { currentSubScreen = SubScreen.WIDGETS_STUDIO },
                                    onRefreshWeather = {
                                        scope.launch {
                                            weatherService.refreshWeather(selectedCity.latitude, selectedCity.longitude)
                                        }
                                    },
                                    onOpenPrayerAdjustments = { currentSubScreen = SubScreen.UNIFIED_ADHAN_REMINDER },
                                    onOpenAthkarDuas = { cat ->
                                        initialAthkarCat = cat
                                        currentSubScreen = SubScreen.ATHKAR_DUAS
                                    },
                                    onPlayPrePrayerVoice = { smartVoiceManager.playPrePrayerReminder(prePrayerSoundType) },
                                    onPlaySalawatVoice = { smartVoiceManager.playSalawatReminder(salawatSoundType) },
                                    onDismissSalawatAlert = { salawatReminderManager.dismissAlert() },
                                    onOpenUnifiedProphetsTawbah = { tab ->
                                        currentSubScreen = SubScreen.UNIFIED_PROPHETS_TAWBAH
                                    },
                                    onOpenUnifiedInventionsBattles = { tab ->
                                        currentSubScreen = SubScreen.UNIFIED_INVENTIONS_BATTLES
                                    },
                                    onOpenUnifiedQuestions = { tab ->
                                        currentSubScreen = SubScreen.UNIFIED_QUESTIONS
                                    },
                                    onOpenCapitals = {
                                        currentSubScreen = SubScreen.CAPITALS
                                    },
                                    onOpenUnifiedAdhanReminder = {
                                        currentSubScreen = SubScreen.UNIFIED_ADHAN_REMINDER
                                    },
                                    onOpenUnifiedQuranSunnah = {
                                        currentSubScreen = SubScreen.UNIFIED_QURAN_SUNNAH
                                    },
                                    onOpenCreatorStudio = {
                                        currentSubScreen = SubScreen.CREATOR_STUDIO
                                    },
                                    onOpenAlertsAndReminders = {
                                        currentSubScreen = SubScreen.ALERTS_AND_REMINDERS
                                    },
                                    sections = sectionsList,
                                    currentWallpaper = currentWallpaper,
                                    modifier = screenModifier
                                )
                            }

                            AppTab.ADHAN -> {
                                UnifiedAdhanReminderScreen(
                                    prayerTimes = prayerTimes,
                                    playbackState = playbackState,
                                    selectedMuadhinId = selectedMuadhinId,
                                    adhanAudiosList = adhanAudiosList,
                                    onSelectMuadhin = { id ->
                                        appPreferences.setSelectedMuadhin(id)
                                        val adhan = adhanAudiosList.find { it.id == id }
                                        if (adhan != null) {
                                            audioPlayerManager.playAudio(adhan.audioUrl, adhan.nameAr, "النداء للصلاة")
                                        }
                                    },
                                    onTestAudio = { url, name ->
                                        if (playbackState.isPlaying) {
                                            audioPlayerManager.stopAudio()
                                        } else {
                                            audioPlayerManager.playAudio(url, name, "استماع للتنبيه الصوتي")
                                        }
                                    },
                                    onTogglePlayPause = { audioPlayerManager.togglePlayPause() },
                                    onStopAudio = { audioPlayerManager.stopAudio() },
                                    fridayFirstAdhan = fridayFirstAdhan,
                                    fridaySecondAdhan = fridaySecondAdhan,
                                    fridaySoundOnly = fridaySoundOnly,
                                    onUpdateFridaySettings = { first, second, soundOnly ->
                                        appPreferences.setFridaySettings(first, second, soundOnly)
                                    },
                                    prePrayerBigMessage = prePrayerBigMsg,
                                    prePrayerMinutesBefore = prePrayerMinsBefore,
                                    prePrayerSoundType = prePrayerSoundType,
                                    onUpdatePrePrayerSettings = { enabled, mins, sound ->
                                        appPreferences.setPrePrayerSettings(enabled, mins, "إِنَّ الصَّلَاةَ كَانَتْ عَلَى الْمُؤْمِنِينَ كِتَابًا مَّوْقُوتًا ۝ صدق الله العظيم")
                                        appPreferences.setPrePrayerSoundType(sound)
                                    },
                                    iqamaReminderEnabled = iqamaReminderEnabled,
                                    iqamaMinutesAfter = iqamaMinutesAfter,
                                    iqamaSoundType = iqamaSoundType,
                                    onUpdateIqamaSettings = { enabled, mins, sound ->
                                        appPreferences.setIqamaReminder(enabled, mins, sound)
                                    },
                                    morningAthkarReminder = morningAthkarReminder,
                                    morningAthkarTime = morningAthkarTime,
                                    onUpdateMorningAthkar = { enabled, time ->
                                        appPreferences.setMorningAthkarSettings(enabled, time)
                                    },
                                    eveningAthkarReminder = eveningAthkarReminder,
                                    eveningAthkarTime = eveningAthkarTime,
                                    onUpdateEveningAthkar = { enabled, time ->
                                        appPreferences.setEveningAthkarSettings(enabled, time)
                                    },
                                    morningAthkarSoundId = morningAthkarSound,
                                    onUpdateMorningAthkarSound = { appPreferences.setMorningAthkarSound(it) },
                                    eveningAthkarSoundId = eveningAthkarSound,
                                    onUpdateEveningAthkarSound = { appPreferences.setEveningAthkarSound(it) },
                                    postPrayerAthkarReminder = postPrayerAthkarRem,
                                    postPrayerAthkarTime = postPrayerAthkarTime,
                                    onUpdatePostPrayerAthkar = { enabled, time ->
                                        appPreferences.setPostPrayerAthkarSettings(enabled, time)
                                    },
                                    postPrayerAthkarSoundId = postPrayerAthkarSound,
                                    onUpdatePostPrayerAthkarSound = { appPreferences.setPostPrayerAthkarSound(it) },
                                    salawatIntervalMinutes = salawatInterval,
                                    salawatSoundType = salawatSoundType,
                                    onUpdateSalawatSettings = { mins, sound ->
                                        appPreferences.setSalawatInterval(mins)
                                        appPreferences.setSalawatSoundType(sound)
                                    },
                                    onPlaySalawatTest = { smartVoiceManager.playSalawatReminder(it) },
                                    prayerOffsets = prayerOffsets,
                                    onSetOffset = { type, off -> appPreferences.setPrayerOffset(type, off) },
                                    onResetOffsets = {
                                        PrayerType.values().forEach { appPreferences.setPrayerOffset(it, 0) }
                                    },
                                    onAddSound = { name, desc, url, cat ->
                                        customContentManager.addCustomSound(name, desc, url, cat)
                                    },
                                    onDeleteSound = { id ->
                                        customContentManager.deleteSound(id)
                                    },
                                    onOpenCreatorStudio = { if (isDeveloperUnlocked) currentSubScreen = SubScreen.CREATOR_STUDIO },
                                    isDeveloperUnlocked = isDeveloperUnlocked,
                                    onSendCenterNotification = triggerCenterNotification,
                                    onOpenDrawer = { isDrawerOpen = true },
                                    postPrayerDelayMinutes = postPrayerDelayMinutes,
                                    onUpdatePostPrayerDelayMinutes = { appPreferences.setPostPrayerDelayMinutes(it) },
                                    onBack = { currentTab = AppTab.HOME },
                                    modifier = screenModifier
                                )
                            }

                            AppTab.QIBLA -> {
                                QiblaScreen(
                                    currentAzimuth = azimuth,
                                    isSensorAvailable = isSensorAvailable,
                                    selectedCity = selectedCity,
                                    onOpenCitySelector = { showCityDialog = true },
                                    modifier = screenModifier
                                )
                            }

                            AppTab.QURAN -> {
                                val allQaris = remember(selectedRiwayahId) {
                                    repository.getQarisForRiwayah(selectedRiwayahId)
                                }
                                val currentQari = remember(selectedQariId, allQaris) {
                                    allQaris.find { it.id == selectedQariId } ?: allQaris.first()
                                }
                                QuranScreen(
                                    surahs = repository.getSurahs(),
                                    qaris = allQaris,
                                    selectedQari = currentQari,
                                    playbackState = playbackState,
                                    lastReadSurahNumber = lastReadSurah,
                                    onSelectQari = { qari -> appPreferences.setSelectedQariId(qari.id) },
                                    onPlaySurah = { surah, qari ->
                                        val formattedNum = String.format("%03d", surah.number)
                                        val audioUrl = "${qari.serverUrl}$formattedNum.mp3"
                                        audioPlayerManager.playAudio(
                                            url = audioUrl,
                                            title = "سورة ${surah.nameAr}",
                                            subtitle = qari.nameAr
                                        )
                                    },
                                    onTogglePlayPause = { audioPlayerManager.togglePlayPause() },
                                    onStopAudio = { audioPlayerManager.stopAudio() },
                                    onLoadAyahs = { num -> repository.getSurahAyahs(num) },
                                    onSaveLastRead = { surah, ayah -> appPreferences.setLastRead(surah, ayah) },
                                    bookmarkSurahNumber = bookmarkSurah,
                                    bookmarkAyahNumber = bookmarkAyah,
                                    bookmarkSurahName = bookmarkSurahName,
                                    bookmarkAyahText = bookmarkAyahText,
                                    hasActiveBookmark = hasActiveBookmark,
                                    onSaveBookmark = { sNum, sName, aNum, aText ->
                                        appPreferences.setBookmark(sNum, sName, aNum, aText)
                                    },
                                    onClearBookmark = { appPreferences.clearBookmark() },
                                    selectedRiwayahId = selectedRiwayahId,
                                    onSelectRiwayah = { riwayahId ->
                                        appPreferences.setSelectedRiwayahId(riwayahId)
                                        val qarisForRiwayah = repository.getQarisForRiwayah(riwayahId)
                                        if (qarisForRiwayah.isNotEmpty()) {
                                            appPreferences.setSelectedQariId(qarisForRiwayah.first().id)
                                        }
                                    },
                                    selectedTranslationLang = selectedTranslationLang,
                                    isTranslationVisible = isTranslationVisible,
                                    onSelectTranslationLang = { appPreferences.setSelectedTranslationLang(it) },
                                    onToggleTranslationVisibility = { appPreferences.setTranslationVisible(it) },
                                    savedFontSize = quranFontSize,
                                    onUpdateFontSize = { appPreferences.setQuranFontSize(it) },
                                    modifier = screenModifier
                                )
                            }

                            AppTab.ATHKAR_DUAS -> {
                                AthkarDuasScreen(
                                    adhkarList = repository.getAdhkar(),
                                    initialCategory = initialAthkarCat,
                                    onSpeakDhikr = { smartVoiceManager.speakDhikr(it) },
                                    onStopSpeaking = { smartVoiceManager.stop() },
                                    onBack = { currentTab = AppTab.HOME },
                                    modifier = screenModifier
                                )
                            }

                            AppTab.SETTINGS -> {
                                SettingsScreen(
                                    selectedCity = selectedCity,
                                    calculationMethod = calcMethod,
                                    juristicMethod = juristicMethod,
                                    prayerOffsets = prayerOffsets,
                                    weatherInfo = weatherInfo,
                                    isDarkMode = isDarkTheme,
                                    fridayFirstAdhan = fridayFirstAdhan,
                                    fridaySecondAdhan = fridaySecondAdhan,
                                    fridaySoundOnly = fridaySoundOnly,
                                    salawatIntervalMinutes = salawatInterval,
                                    prePrayerBigMessage = prePrayerBigMsg,
                                    prePrayerMinutesBefore = prePrayerMinsBefore,
                                    hijriType = hijriType,
                                    hijriAdjustment = hijriAdjustment,
                                    monthDisplayMode = monthDisplayMode,
                                    utcMode = utcMode,
                                    manualUtcHours = manualUtcHours,
                                    currentBgTheme = bgTheme,
                                    sections = sectionsList,
                                    wallpapers = wallpapersList,
                                    adhanAudios = adhanAudiosList,
                                    tawbahStories = tawbahStoriesList,
                                    prophetStories = prophetStoriesList,
                                    islamicQAs = islamicQAsList,
                                    timeFormat = timeFormat,
                                    onUpdateTimeFormat = { appPreferences.setTimeFormat(it) },
                                    digitType = digitType,
                                    onUpdateDigitType = { appPreferences.setDigitType(it) },
                                    ongoingNotification = ongoingPrayerNotification,
                                    onToggleOngoingNotification = { appPreferences.setOngoingPrayerNotification(it) },
                                    onSendCenterNotification = triggerCenterNotification,
                                    onDetectLocationGps = onDetectGpsLocation,
                                    isGpsDetecting = isGpsDetecting,
                                    onOpenCitySelector = { showCityDialog = true },
                                    onSaveCityCoords = { lat, lng, name ->
                                        appPreferences.setCity(selectedCity.copy(latitude = lat, longitude = lng, nameAr = name))
                                    },
                                    onSelectCalculationMethod = { appPreferences.setCalculationMethod(it) },
                                    onSelectJuristicMethod = { appPreferences.setJuristicMethod(it) },
                                    onSetPrayerOffset = { type, off -> appPreferences.setPrayerOffset(type, off) },
                                    onRefreshWeather = {
                                        scope.launch {
                                            weatherService.refreshWeather(selectedCity.latitude, selectedCity.longitude)
                                        }
                                    },
                                    onToggleDarkMode = { appPreferences.setDarkMode(it) },
                                    onUpdateFridaySettings = { first, second, soundOnly ->
                                        appPreferences.setFridaySettings(first, second, soundOnly)
                                    },
                                    onUpdateSalawatInterval = { appPreferences.setSalawatInterval(it) },
                                    onUpdatePrePrayerSettings = { enabled, mins ->
                                        appPreferences.setPrePrayerBigMessage(enabled, mins)
                                    },
                                    onUpdateHijriSettings = { type, adj, mode ->
                                        appPreferences.setHijriSettings(type, adj, mode)
                                    },
                                    onUpdateUtcSettings = { mode, hours ->
                                        appPreferences.setUtcSettings(mode, hours)
                                    },
                                    onSelectBgTheme = { appPreferences.setAppBackgroundTheme(it) },
                                    onOpenCreatorStudio = { currentSubScreen = SubScreen.CREATOR_STUDIO },
                                    onOpenWidgetsStudio = { currentSubScreen = SubScreen.WIDGETS_STUDIO },
                                    onToggleSection = { id, enabled -> customContentManager.toggleSection(id, enabled) },
                                    onDeleteSection = { id -> customContentManager.deleteSection(id) },
                                    onAddSection = { title, sub -> customContentManager.addCustomSection(title, sub) },
                                    onResetSections = { customContentManager.resetSections() },
                                    onAddWallpaper = { name, desc, col, url -> customContentManager.addCustomWallpaper(name, desc, col, url) },
                                    onDeleteWallpaper = { id -> customContentManager.deleteWallpaper(id) },
                                    onResetWallpapers = { customContentManager.resetWallpapers() },
                                    onAddSound = { name, desc, url, cat -> customContentManager.addCustomSound(name, desc, url, cat) },
                                    onDeleteSound = { id -> customContentManager.deleteSound(id) },
                                    onResetSounds = { customContentManager.resetSounds() },
                                    onAddTawbahStory = { t, p, d, s, m -> customContentManager.addCustomTawbahStory(t, p, d, s, m) },
                                    onDeleteTawbahStory = { id -> customContentManager.deleteTawbahStory(id) },
                                    onResetTawbahStories = { customContentManager.resetTawbahStories() },
                                    onAddProphetStory = { n, t, v, m, l, s -> customContentManager.addCustomProphetStory(n, t, v, m, l, s) },
                                    onDeleteProphetStory = { id -> customContentManager.deleteProphetStory(id) },
                                    onResetProphetStories = { customContentManager.resetProphetStories() },
                                    onAddQA = { q, a, c, e -> customContentManager.addCustomQA(q, a, c, e) },
                                    onDeleteQA = { id -> customContentManager.deleteQA(id) },
                                    onResetQAs = { customContentManager.resetQAs() },
                                    onPlayTestAudio = { url, title -> audioPlayerManager.playAudio(url, title, "تجربة صوتية") },
                                    onOpenPrayerAdjustments = { currentSubScreen = SubScreen.PRAYER_ADJUSTMENT },
                                    onOpenAthkarDuas = { cat ->
                                        initialAthkarCat = cat
                                        currentSubScreen = SubScreen.ATHKAR_DUAS
                                    },
                                    googleAccountEmail = googleAccountEmail,
                                    isGoogleSignedIn = isGoogleSignedIn,
                                    onUpdateGoogleAccount = { email, signedIn -> appPreferences.setGoogleAccount(email, signedIn) },
                                    isDeveloperUnlocked = isDeveloperUnlocked,
                                    onUnlockDeveloper = { email, ans -> appPreferences.checkAndUnlockDeveloper(email, ans) },
                                    onLockDeveloper = { appPreferences.lockDeveloper() },
                                    athkarReminderMode = athkarReminderMode,
                                    onUpdateAthkarReminderMode = { appPreferences.setAthkarReminderMode(it) },
                                    quickStartEnabled = quickStartEnabled,
                                    onUpdateQuickStartEnabled = { appPreferences.setQuickStartEnabled(it) },
                                    postPrayerDelayMinutes = postPrayerDelayMinutes,
                                    onUpdatePostPrayerDelayMinutes = { appPreferences.setPostPrayerDelayMinutes(it) },
                                    salawatSoundType = salawatSoundType,
                                    onUpdateSalawatSoundType = { appPreferences.setSalawatSoundType(it) },
                                    onBack = { currentTab = AppTab.HOME },
                                    modifier = screenModifier
                                )
                            }
                        }
                    }

                    if (showCityDialog) {
                        CitySelectionDialog(
                            cities = repository.getPresetCities(),
                            currentCity = selectedCity,
                            onSelectCity = { newCity ->
                                appPreferences.setCity(newCity)
                                showCityDialog = false
                            },
                            onDismiss = { showCityDialog = false }
                        )
                    }

                    if (showStrictPermissionsDialog) {
                        AlertDialog(
                            onDismissRequest = { showStrictPermissionsDialog = false },
                            icon = {
                                Icon(
                                    imageVector = Icons.Filled.Security,
                                    contentDescription = null,
                                    tint = GoldAccent,
                                    modifier = Modifier.size(36.dp)
                                )
                            },
                            title = {
                                Text(
                                    text = "الدخول السريع وضبط الأذونات الصارمة • صلاتي",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    textAlign = TextAlign.Center
                                )
                            },
                            text = {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "للبدء الفوري وضمان دقة مواقيت الصلاة والأذان والتنبيهات، يتطلب صلاتي ضبط الأذونات التالية بشكل صارم:",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(Icons.Filled.LocationOn, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(24.dp))
                                        Column {
                                            Text("إذن الموقع الجغرافي (GPS)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            Text("لتحديد مواقيت الصلاة الدقيقة، القبلة، ودرجة الحرارة لمدينتك.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(Icons.Filled.NotificationsActive, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(24.dp))
                                        Column {
                                            Text("إذن الإشعارات وتنبيهات الأذان", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            Text("لتنبيهات الأذان، أذكار بعد الصلاة، وأذكار الصباح والمساء.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(Icons.Filled.Alarm, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(24.dp))
                                        Column {
                                            Text("إذن التنبيهات الدقيقة ومنبه السحور", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            Text("لإطلاق الأذان ومنبه السحور والإقامة في الوقت المضبوط بدقة تامة.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(Icons.Filled.FlashOn, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(24.dp))
                                        Column {
                                            Text("تفعيل الدخول السريع الصارم", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            Text("الدخول المباشر إلى الصفحة الرئيسية فور فتح التطبيق بدون تأخير.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                }
                            },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        showStrictPermissionsDialog = false
                                        appPreferences.setQuickStartEnabled(true)
                                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                                            notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                                        }
                                        locationPermissionLauncher.launch(
                                            arrayOf(
                                                android.Manifest.permission.ACCESS_FINE_LOCATION,
                                                android.Manifest.permission.ACCESS_COARSE_LOCATION
                                            )
                                        )
                                        triggerCenterNotification()
                                    }
                                ) {
                                    Text("منح الأذونات الصارمة والدخول السريع")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showStrictPermissionsDialog = false }) {
                                    Text("المتابعة السريعة")
                                }
                            }
                        )
                    }
                } // Closes Scaffold

                // Slide Navigation Drawer Overlay matching screenshot
                AppNavigationDrawer(
                    isOpen = isDrawerOpen,
                    currentDestination = currentDrawerDestination,
                    isDeveloperUnlocked = isDeveloperUnlocked,
                    onClose = { isDrawerOpen = false },
                    onNavigate = { dest ->
                        when (dest) {
                            DrawerDestination.HOME -> {
                                currentTab = AppTab.HOME
                                currentSubScreen = null
                            }
                            DrawerDestination.ADHAN_AND_REMINDER,
                            DrawerDestination.ADHAN -> {
                                currentSubScreen = SubScreen.UNIFIED_ADHAN_REMINDER
                            }
                            DrawerDestination.PRAYER_REMINDER -> {
                                currentSubScreen = SubScreen.ALERTS_AND_REMINDERS
                            }
                            DrawerDestination.QIBLA -> {
                                currentTab = AppTab.QIBLA
                                currentSubScreen = null
                            }
                            DrawerDestination.QURAN_AND_SUNNAH -> {
                                currentSubScreen = SubScreen.UNIFIED_QURAN_SUNNAH
                            }
                            DrawerDestination.CALENDAR -> {
                                currentSubScreen = SubScreen.MONTHLY_CALENDAR
                            }
                            DrawerDestination.SETTINGS -> {
                                currentTab = AppTab.SETTINGS
                                currentSubScreen = null
                            }
                            DrawerDestination.CREATOR_STUDIO -> {
                                if (isDeveloperUnlocked) {
                                    currentSubScreen = SubScreen.CREATOR_STUDIO
                                }
                            }
                            DrawerDestination.PROPHETS_AND_TAWBAH -> {
                                currentSubScreen = SubScreen.UNIFIED_PROPHETS_TAWBAH
                            }
                            DrawerDestination.INVENTIONS_AND_BATTLES -> {
                                currentSubScreen = SubScreen.UNIFIED_INVENTIONS_BATTLES
                            }
                            DrawerDestination.QUESTIONS_RELIGIOUS_AND_GENERAL -> {
                                currentSubScreen = SubScreen.UNIFIED_QUESTIONS
                            }
                            DrawerDestination.ON_THIS_DAY -> {
                                currentSubScreen = SubScreen.ON_THIS_DAY
                            }
                            DrawerDestination.CAPITALS -> {
                                currentSubScreen = SubScreen.CAPITALS
                            }
                            DrawerDestination.ATHKAR_DUAS -> {
                                initialAthkarCat = "post_prayer"
                                currentTab = AppTab.ATHKAR_DUAS
                                currentSubScreen = null
                            }
                            DrawerDestination.TASBIH -> {
                                initialAthkarCat = "tasbih_istighfar"
                                currentTab = AppTab.ATHKAR_DUAS
                                currentSubScreen = null
                            }
                        }
                    }
                )
            } // Closes Box
        } // Closes MyApplicationTheme
    } // Closes setContent
} // Closes onCreate

    override fun onResume() {
        super.onResume()
        compassSensorManager.startListening()
    }

    override fun onPause() {
        super.onPause()
        compassSensorManager.stopListening()
    }

    override fun onDestroy() {
        super.onDestroy()
        audioPlayerManager.release()
        smartVoiceManager.release()
        salawatReminderManager.stopSalawatTimer()
    }
}
