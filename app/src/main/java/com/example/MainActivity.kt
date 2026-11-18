package com.example

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.AppPreferences
import com.example.data.local.IslamicRepository
import com.example.data.local.CustomContentManager
import com.example.data.models.*
import com.example.data.services.*
import com.example.ui.components.AppNavigationDrawer
import com.example.ui.components.AppTab
import com.example.ui.components.CitySelectionDialog
import com.example.ui.components.DrawerDestination
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

enum class SubScreen {
    ADVANCED_SETTINGS,
    CREATOR_STUDIO,
    MONTHLY_CALENDAR,
    WIDGETS_STUDIO,
    GHAZWAT,
    PRAYER_ADJUSTMENT,
    ATHKAR_DUAS,
    SUNNAH_HADITH,
    UNIFIED_INVENTIONS_BATTLES,
    UNIFIED_PROPHETS_TAWBAH,
    UNIFIED_QUESTIONS,
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

        val managers = AppManagers(
            appPreferences = appPreferences,
            repository = repository,
            customContentManager = customContentManager,
            audioPlayerManager = audioPlayerManager,
            compassSensorManager = compassSensorManager,
            weatherService = weatherService,
            smartVoiceManager = smartVoiceManager,
            prayerNotificationManager = prayerNotificationManager,
            salawatReminderManager = salawatReminderManager
        )

        setContent {
            val isDarkTheme by appPreferences.isDarkMode.collectAsStateWithLifecycle()
            val selectedCity by appPreferences.selectedCity.collectAsStateWithLifecycle()
            val calcMethod by appPreferences.calculationMethod.collectAsStateWithLifecycle()
            val juristicMethod by appPreferences.juristicMethod.collectAsStateWithLifecycle()
            val prayerOffsets by appPreferences.prayerOffsets.collectAsStateWithLifecycle()
            val weatherInfo by weatherService.weatherState.collectAsStateWithLifecycle()
            val hijriAdjustment by appPreferences.hijriAdjustmentDays.collectAsStateWithLifecycle()
            val ongoingPrayerNotification by appPreferences.ongoingPrayerNotification.collectAsStateWithLifecycle()
            val timeFormat by appPreferences.timeFormat.collectAsStateWithLifecycle()
            val digitType by appPreferences.digitType.collectAsStateWithLifecycle()
            val prePrayerMinsBefore by appPreferences.prePrayerMinutesBefore.collectAsStateWithLifecycle()
            val isAccountAuthenticated by appPreferences.isAccountAuthenticated.collectAsStateWithLifecycle()
            val isDeveloperUnlocked by appPreferences.isDeveloperUnlocked.collectAsStateWithLifecycle()
            val salawatInterval by appPreferences.salawatIntervalMinutes.collectAsStateWithLifecycle()
            val salawatSoundType by appPreferences.salawatSoundType.collectAsStateWithLifecycle()

            val scope = rememberCoroutineScope()
            var currentTab by remember { mutableStateOf(AppTab.HOME) }
            var currentSubScreen by remember { mutableStateOf<SubScreen?>(null) }
            var isDrawerOpen by remember { mutableStateOf(false) }
            var showCityDialog by remember { mutableStateOf(false) }
            var initialAthkarCat by remember { mutableStateOf("post_prayer") }

            LaunchedEffect(isAccountAuthenticated) {
                if (!isAccountAuthenticated) {
                    currentTab = AppTab.HOME
                    currentSubScreen = null
                    isDrawerOpen = false
                }
            }

            val currentDrawerDestination = remember(currentTab, currentSubScreen) {
                when (currentSubScreen) {
                    SubScreen.UNIFIED_QURAN_SUNNAH, SubScreen.SUNNAH_HADITH -> DrawerDestination.QURAN_AND_SUNNAH
                    SubScreen.UNIFIED_ADHAN_REMINDER, SubScreen.PRAYER_ADJUSTMENT -> DrawerDestination.ADHAN_AND_REMINDER
                    SubScreen.ALERTS_AND_REMINDERS -> DrawerDestination.PRAYER_REMINDER
                    SubScreen.CREATOR_STUDIO -> DrawerDestination.CREATOR_STUDIO
                    SubScreen.MONTHLY_CALENDAR -> DrawerDestination.CALENDAR
                    SubScreen.ATHKAR_DUAS -> DrawerDestination.ATHKAR_DUAS
                    SubScreen.UNIFIED_INVENTIONS_BATTLES, SubScreen.GHAZWAT -> DrawerDestination.INVENTIONS_AND_BATTLES
                    SubScreen.UNIFIED_PROPHETS_TAWBAH -> DrawerDestination.PROPHETS_AND_TAWBAH
                    SubScreen.UNIFIED_QUESTIONS -> DrawerDestination.QUESTIONS_RELIGIOUS_AND_GENERAL
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

            // Offline mathematical calculation + live sync
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
            var hasLocationPermission by remember {
                mutableStateOf(LocationHelper.hasLocationPermission(applicationContext))
            }
            var isLocationServiceEnabled by remember {
                mutableStateOf(LocationHelper.isLocationServiceEnabled(applicationContext))
            }
            var hasAttemptedLocationPermission by remember { mutableStateOf(false) }

            val refreshLocationState: () -> Unit = {
                hasLocationPermission = LocationHelper.hasLocationPermission(applicationContext)
                isLocationServiceEnabled = LocationHelper.isLocationServiceEnabled(applicationContext)
            }

            val lifecycleOwner = LocalLifecycleOwner.current
            DisposableEffect(lifecycleOwner) {
                val observer = LifecycleEventObserver { _, event ->
                    if (event == Lifecycle.Event.ON_RESUME) {
                        refreshLocationState()
                    }
                }
                lifecycleOwner.lifecycle.addObserver(observer)
                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                }
            }

            val locationPermissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
                androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->
                hasAttemptedLocationPermission = true
                val isGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
                refreshLocationState()
                if (isGranted) {
                    scope.launch {
                        isGpsDetecting = true
                        val loc = LocationHelper.getCurrentLocation(applicationContext)
                        if (loc != null) {
                            appPreferences.setCity(loc)
                            weatherService.refreshWeather(loc.latitude, loc.longitude)
                        }
                        isGpsDetecting = false
                    }
                }
            }

            val onRequestMandatoryLocationPermission: () -> Unit = {
                locationPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                )
            }

            val onOpenDeviceLocationSettings: () -> Unit = {
                try {
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                } catch (_: Exception) {
                    startActivity(Intent(Settings.ACTION_SETTINGS))
                }
            }

            val onOpenAppPermissionSettings: () -> Unit = {
                try {
                    val intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", packageName, null)
                    )
                    startActivity(intent)
                } catch (_: Exception) {
                    onOpenDeviceLocationSettings()
                }
            }

            val onDetectGpsLocation: () -> Unit = {
                refreshLocationState()
                if (LocationHelper.hasLocationPermission(applicationContext)) {
                    scope.launch {
                        isGpsDetecting = true
                        val loc = LocationHelper.getCurrentLocation(applicationContext)
                        if (loc != null) {
                            appPreferences.setCity(loc)
                            appPreferences.setAutoGpsLocation(true)
                            com.example.widget.PrayerAppWidgetProvider.updateAllWidgets(applicationContext)
                            weatherService.refreshWeather(loc.latitude, loc.longitude)
                        }
                        isGpsDetecting = false
                    }
                } else {
                    onRequestMandatoryLocationPermission()
                }
            }

            // On Startup or when Location becomes ready: prompt permission if missing, and detect real GPS city & weather
            LaunchedEffect(hasLocationPermission, isLocationServiceEnabled, isAccountAuthenticated) {
                if (isAccountAuthenticated && !hasLocationPermission && !hasAttemptedLocationPermission) {
                    onRequestMandatoryLocationPermission()
                } else if (isAccountAuthenticated && hasLocationPermission && isLocationServiceEnabled) {
                    isGpsDetecting = true
                    var loc = LocationHelper.getCurrentLocation(applicationContext)
                    if (loc == null) {
                        kotlinx.coroutines.delay(1500)
                        loc = LocationHelper.getCurrentLocation(applicationContext)
                    }
                    isGpsDetecting = false
                    if (loc != null) {
                        appPreferences.setCity(loc)
                        appPreferences.setAutoGpsLocation(true)
                        com.example.widget.PrayerAppWidgetProvider.updateAllWidgets(applicationContext)
                        weatherService.refreshWeather(loc.latitude, loc.longitude)
                    } else {
                        weatherService.refreshWeather(selectedCity.latitude, selectedCity.longitude)
                    }
                } else {
                    weatherService.refreshWeather(selectedCity.latitude, selectedCity.longitude)
                }
            }

            // Status Bar Ongoing Notification
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

                    val nextTimeFormatted = DigitFormatter.format(timeFormatter.format(Date(nextMillis)), digitType)
                    val countdownText = DigitFormatter.format("$diffMinutes دقيقة", digitType)
                    val prePrayerRemainingMins = (diffMinutes - prePrayerMinsBefore).coerceAtLeast(0)
                    val prePrayerCountdownText = if (prePrayerRemainingMins > 0) DigitFormatter.format("$prePrayerRemainingMins دقيقة", digitType) else "حان وقت الدعاء قبل الأذان"
                    val tempText = DigitFormatter.format("${weatherInfo.temperatureC.toInt()}°م (${weatherInfo.conditionAr})", digitType)
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

            // Back navigation handling
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
                val countdownText = DigitFormatter.format("$diffMinutes دقيقة", digitType)
                val prePrayerRemainingMins = (diffMinutes - prePrayerMinsBefore).coerceAtLeast(0)
                val prePrayerCountdownText = if (prePrayerRemainingMins > 0) DigitFormatter.format("$prePrayerRemainingMins دقيقة", digitType) else "حان وقت الدعاء قبل الأذان"
                val tempText = DigitFormatter.format("${weatherInfo.temperatureC.toInt()}°م (${weatherInfo.conditionAr})", digitType)
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
                if (!isAccountAuthenticated) {
                    AccountGateScreen(
                        onSignIn = { email, password -> appPreferences.signInUserAccount(email, password) },
                        onSignUp = { name, email, password -> appPreferences.registerUserAccount(name, email, password) }
                    )
                } else if (!hasLocationPermission || !isLocationServiceEnabled) {
                    LocationGateScreen(
                        hasLocationPermission = hasLocationPermission,
                        isLocationEnabled = isLocationServiceEnabled,
                        hasAttemptedPermission = hasAttemptedLocationPermission,
                        onRequestPermission = onRequestMandatoryLocationPermission,
                        onOpenDeviceLocationSettings = onOpenDeviceLocationSettings,
                        onOpenAppPermissionSettings = onOpenAppPermissionSettings
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Scaffold(
                            modifier = Modifier.fillMaxSize()
                        ) { innerPadding ->
                        val screenModifier = Modifier.padding(innerPadding)

                        if (currentSubScreen != null) {
                            SubScreenDispatcher(
                                currentSubScreen = currentSubScreen!!,
                                managers = managers,
                                selectedCity = selectedCity,
                                prayerTimes = prayerTimes,
                                hijriDate = hijriDate,
                                weatherInfo = weatherInfo,
                                isDarkTheme = isDarkTheme,
                                isGpsDetecting = isGpsDetecting,
                                onDetectGpsLocation = onDetectGpsLocation,
                                triggerCenterNotification = triggerCenterNotification,
                                initialAthkarCat = initialAthkarCat,
                                onUpdateInitialAthkarCat = { initialAthkarCat = it },
                                onNavigateSubScreen = { currentSubScreen = it },
                                onOpenDrawer = { isDrawerOpen = true },
                                onOpenCitySelector = { showCityDialog = true },
                                modifier = screenModifier
                            )
                        } else {
                            MainTabDispatcher(
                                currentTab = currentTab,
                                managers = managers,
                                selectedCity = selectedCity,
                                prayerTimes = prayerTimes,
                                hijriDate = hijriDate,
                                weatherInfo = weatherInfo,
                                isDarkTheme = isDarkTheme,
                                isGpsDetecting = isGpsDetecting,
                                onDetectGpsLocation = onDetectGpsLocation,
                                triggerCenterNotification = triggerCenterNotification,
                                initialAthkarCat = initialAthkarCat,
                                onUpdateInitialAthkarCat = { initialAthkarCat = it },
                                onNavigateTab = { currentTab = it },
                                onNavigateSubScreen = { currentSubScreen = it },
                                onOpenDrawer = { isDrawerOpen = true },
                                onOpenCitySelector = { showCityDialog = true },
                                modifier = screenModifier
                            )
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
                    }

                    // Slide Navigation Drawer Overlay
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
                    }
                }
            }
        }
    }

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
