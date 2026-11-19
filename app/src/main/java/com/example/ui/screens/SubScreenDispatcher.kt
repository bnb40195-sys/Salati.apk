package com.example.ui.screens

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.SubScreen
import com.example.data.models.*
import com.example.data.services.WeatherInfo
import com.example.ui.screens.*
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun SubScreenDispatcher(
    currentSubScreen: SubScreen,
    managers: AppManagers,
    selectedCity: CityLocation,
    prayerTimes: DayPrayerTimes?,
    hijriDate: HijriDate,
    weatherInfo: WeatherInfo,
    isDarkTheme: Boolean,
    isGpsDetecting: Boolean,
    onDetectGpsLocation: () -> Unit,
    triggerCenterNotification: () -> Unit,
    initialAthkarCat: String,
    onUpdateInitialAthkarCat: (String) -> Unit,
    onNavigateSubScreen: (SubScreen?) -> Unit,
    onOpenDrawer: () -> Unit,
    onOpenCitySelector: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val appPreferences = managers.appPreferences
    val repository = managers.repository
    val customContentManager = managers.customContentManager
    val audioPlayerManager = managers.audioPlayerManager
    val smartVoiceManager = managers.smartVoiceManager

    when (currentSubScreen) {
        SubScreen.ADVANCED_SETTINGS -> {
            val autoGpsLocation by appPreferences.autoGpsLocation.collectAsStateWithLifecycle()
            val calcMethod by appPreferences.calculationMethod.collectAsStateWithLifecycle()
            val juristicMethod by appPreferences.juristicMethod.collectAsStateWithLifecycle()
            val prayerOffsets by appPreferences.prayerOffsets.collectAsStateWithLifecycle()
            val fridayFirstAdhan by appPreferences.fridayFirstAdhan.collectAsStateWithLifecycle()
            val fridaySecondAdhan by appPreferences.fridaySecondAdhan.collectAsStateWithLifecycle()
            val fridaySoundOnly by appPreferences.fridaySoundOnly.collectAsStateWithLifecycle()
            val salawatInterval by appPreferences.salawatIntervalMinutes.collectAsStateWithLifecycle()
            val salawatSoundType by appPreferences.salawatSoundType.collectAsStateWithLifecycle()
            val prePrayerBigMsg by appPreferences.prePrayerBigMessage.collectAsStateWithLifecycle()
            val prePrayerMinsBefore by appPreferences.prePrayerMinutesBefore.collectAsStateWithLifecycle()
            val hijriType by appPreferences.hijriCalendarType.collectAsStateWithLifecycle()
            val hijriAdjustment by appPreferences.hijriAdjustmentDays.collectAsStateWithLifecycle()
            val monthDisplayMode by appPreferences.monthDisplayMode.collectAsStateWithLifecycle()
            val utcMode by appPreferences.utcMode.collectAsStateWithLifecycle()
            val manualUtcHours by appPreferences.manualUtcHours.collectAsStateWithLifecycle()
            val bgTheme by appPreferences.appBackgroundTheme.collectAsStateWithLifecycle()
            val sectionsList by customContentManager.sections.collectAsStateWithLifecycle()
            val wallpapersList by customContentManager.wallpapers.collectAsStateWithLifecycle()
            val adhanAudiosList by customContentManager.adhanAudios.collectAsStateWithLifecycle()
            val tawbahStoriesList by customContentManager.tawbahStories.collectAsStateWithLifecycle()
            val prophetStoriesList by customContentManager.prophetStories.collectAsStateWithLifecycle()
            val islamicQAsList by customContentManager.islamicQAs.collectAsStateWithLifecycle()
            val timeFormat by appPreferences.timeFormat.collectAsStateWithLifecycle()
            val digitType by appPreferences.digitType.collectAsStateWithLifecycle()
            val ongoingPrayerNotification by appPreferences.ongoingPrayerNotification.collectAsStateWithLifecycle()
            val googleAccountEmail by appPreferences.googleAccountEmail.collectAsStateWithLifecycle()
            val isGoogleSignedIn by appPreferences.isGoogleSignedIn.collectAsStateWithLifecycle()
            val isDeveloperUnlocked by appPreferences.isDeveloperUnlocked.collectAsStateWithLifecycle()
            val athkarReminderMode by appPreferences.athkarReminderMode.collectAsStateWithLifecycle()
            val quickStartEnabled by appPreferences.quickStartEnabled.collectAsStateWithLifecycle()
            val postPrayerDelayMinutes by appPreferences.postPrayerDelayMinutes.collectAsStateWithLifecycle()

            SettingsScreen(
                selectedCity = selectedCity,
                autoGpsLocation = autoGpsLocation,
                onToggleAutoGpsLocation = { appPreferences.setAutoGpsLocation(it) },
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
                onOpenCitySelector = onOpenCitySelector,
                onSaveCityCoords = { lat, lng, name ->
                    appPreferences.setCity(selectedCity.copy(latitude = lat, longitude = lng, nameAr = name))
                },
                onSelectCalculationMethod = { appPreferences.setCalculationMethod(it) },
                onSelectJuristicMethod = { appPreferences.setJuristicMethod(it) },
                onSetPrayerOffset = { type, off -> appPreferences.setPrayerOffset(type, off) },
                onRefreshWeather = {
                    scope.launch {
                        managers.weatherService.refreshWeather(selectedCity.latitude, selectedCity.longitude)
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
                onOpenCreatorStudio = { if (isDeveloperUnlocked) onNavigateSubScreen(SubScreen.CREATOR_STUDIO) },
                onOpenWidgetsStudio = { onNavigateSubScreen(SubScreen.WIDGETS_STUDIO) },
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
                onOpenPrayerAdjustments = { onNavigateSubScreen(SubScreen.PRAYER_ADJUSTMENT) },
                onOpenAthkarDuas = { cat ->
                    onUpdateInitialAthkarCat(cat)
                    onNavigateSubScreen(SubScreen.ATHKAR_DUAS)
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
                onLogoutAccount = { appPreferences.logoutAccount() },
                onBack = { onNavigateSubScreen(null) },
                modifier = modifier
            )
        }

        SubScreen.CREATOR_STUDIO -> {
            val developerEmail by appPreferences.developerEmail.collectAsStateWithLifecycle()
            val developerSecQuestion by appPreferences.developerSecurityQuestion.collectAsStateWithLifecycle()
            val developerSecAnswer by appPreferences.developerSecurityAnswer.collectAsStateWithLifecycle()
            val isDeveloperUnlocked by appPreferences.isDeveloperUnlocked.collectAsStateWithLifecycle()

            if (!isDeveloperUnlocked) {
                LaunchedEffect(Unit) { onNavigateSubScreen(null) }
                return
            }

            val tawbahStoriesList by customContentManager.tawbahStories.collectAsStateWithLifecycle()
            val prophetStoriesList by customContentManager.prophetStories.collectAsStateWithLifecycle()
            val adhanAudiosList by customContentManager.adhanAudios.collectAsStateWithLifecycle()
            val sectionsList by customContentManager.sections.collectAsStateWithLifecycle()
            val wallpapersList by customContentManager.wallpapers.collectAsStateWithLifecycle()
            val playbackState by audioPlayerManager.playbackState.collectAsStateWithLifecycle()

            CreatorStudioScreen(
                developerEmail = developerEmail,
                securityQuestion = developerSecQuestion,
                securityAnswer = developerSecAnswer,
                isUnlocked = isDeveloperUnlocked,
                customStories = emptyList(),
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
                onBack = { onNavigateSubScreen(null) },
                modifier = modifier
            )
        }

        SubScreen.UNIFIED_ADHAN_REMINDER -> {
            val playbackState by audioPlayerManager.playbackState.collectAsStateWithLifecycle()
            val selectedMuadhinId by appPreferences.selectedMuadhin.collectAsStateWithLifecycle()
            val adhanAudiosList by customContentManager.adhanAudios.collectAsStateWithLifecycle()
            val fridayFirstAdhan by appPreferences.fridayFirstAdhan.collectAsStateWithLifecycle()
            val fridaySecondAdhan by appPreferences.fridaySecondAdhan.collectAsStateWithLifecycle()
            val fridaySoundOnly by appPreferences.fridaySoundOnly.collectAsStateWithLifecycle()
            val fridayFirstAdhanSound by appPreferences.fridayFirstAdhanSound.collectAsStateWithLifecycle()
            val fridaySecondAdhanSound by appPreferences.fridaySecondAdhanSound.collectAsStateWithLifecycle()
            val fridayFirstAdhanMins by appPreferences.fridayFirstAdhanMinutesBefore.collectAsStateWithLifecycle()
            val fridaySecondAdhanMins by appPreferences.fridaySecondAdhanMinutes.collectAsStateWithLifecycle()
            val fajrAdhanSound by appPreferences.fajrAdhanSound.collectAsStateWithLifecycle()
            val dhuhrAdhanSound by appPreferences.dhuhrAdhanSound.collectAsStateWithLifecycle()
            val asrAdhanSound by appPreferences.asrAdhanSound.collectAsStateWithLifecycle()
            val maghribAdhanSound by appPreferences.maghribAdhanSound.collectAsStateWithLifecycle()
            val ishaAdhanSound by appPreferences.ishaAdhanSound.collectAsStateWithLifecycle()
            val morningAthkarSoundEnabled by appPreferences.morningAthkarSoundEnabled.collectAsStateWithLifecycle()
            val eveningAthkarSoundEnabled by appPreferences.eveningAthkarSoundEnabled.collectAsStateWithLifecycle()
            val autoGpsLocation by appPreferences.autoGpsLocation.collectAsStateWithLifecycle()
            val salawatCustomText by appPreferences.salawatCustomText.collectAsStateWithLifecycle()
            val morningAthkarCustomText by appPreferences.morningAthkarCustomText.collectAsStateWithLifecycle()
            val eveningAthkarCustomText by appPreferences.eveningAthkarCustomText.collectAsStateWithLifecycle()
            val postPrayerCustomText by appPreferences.postPrayerCustomText.collectAsStateWithLifecycle()
            val prePrayerDuaCustomText by appPreferences.prePrayerDuaCustomText.collectAsStateWithLifecycle()
            val prePrayerBigMsg by appPreferences.prePrayerBigMessage.collectAsStateWithLifecycle()
            val prePrayerMinsBefore by appPreferences.prePrayerMinutesBefore.collectAsStateWithLifecycle()
            val prePrayerSoundType by appPreferences.prePrayerSoundType.collectAsStateWithLifecycle()
            val iqamaReminderEnabled by appPreferences.iqamaReminderEnabled.collectAsStateWithLifecycle()
            val iqamaMinutesAfter by appPreferences.iqamaMinutesAfter.collectAsStateWithLifecycle()
            val iqamaSoundType by appPreferences.iqamaSoundType.collectAsStateWithLifecycle()
            val morningAthkarReminder by appPreferences.morningAthkarReminder.collectAsStateWithLifecycle()
            val morningAthkarTime by appPreferences.morningAthkarTime.collectAsStateWithLifecycle()
            val eveningAthkarReminder by appPreferences.eveningAthkarReminder.collectAsStateWithLifecycle()
            val eveningAthkarTime by appPreferences.eveningAthkarTime.collectAsStateWithLifecycle()
            val morningAthkarSound by appPreferences.morningAthkarSound.collectAsStateWithLifecycle()
            val eveningAthkarSound by appPreferences.eveningAthkarSound.collectAsStateWithLifecycle()
            val postPrayerAthkarRem by appPreferences.postPrayerAthkarReminder.collectAsStateWithLifecycle()
            val postPrayerAthkarTime by appPreferences.postPrayerAthkarTime.collectAsStateWithLifecycle()
            val postPrayerAthkarSound by appPreferences.postPrayerAthkarSound.collectAsStateWithLifecycle()
            val salawatInterval by appPreferences.salawatIntervalMinutes.collectAsStateWithLifecycle()
            val salawatSoundType by appPreferences.salawatSoundType.collectAsStateWithLifecycle()
            val prayerOffsets by appPreferences.prayerOffsets.collectAsStateWithLifecycle()
            val isDeveloperUnlocked by appPreferences.isDeveloperUnlocked.collectAsStateWithLifecycle()
            val postPrayerDelayMinutes by appPreferences.postPrayerDelayMinutes.collectAsStateWithLifecycle()

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
                fridayFirstAdhanMinutesBefore = fridayFirstAdhanMins,
                fridaySecondAdhanMinutes = fridaySecondAdhanMins,
                onUpdateFridayMinutes = { first, second -> appPreferences.setFridayMinutes(first, second) },
                autoGpsLocation = autoGpsLocation,
                onToggleAutoGpsLocation = { appPreferences.setAutoGpsLocation(it) },
                salawatCustomText = salawatCustomText,
                onUpdateSalawatCustomText = { appPreferences.setSalawatCustomText(it) },
                morningAthkarCustomText = morningAthkarCustomText,
                onUpdateMorningAthkarCustomText = { appPreferences.setMorningAthkarCustomText(it) },
                eveningAthkarCustomText = eveningAthkarCustomText,
                onUpdateEveningAthkarCustomText = { appPreferences.setEveningAthkarCustomText(it) },
                postPrayerCustomText = postPrayerCustomText,
                onUpdatePostPrayerCustomText = { appPreferences.setPostPrayerCustomText(it) },
                prePrayerDuaCustomText = prePrayerDuaCustomText,
                onUpdatePrePrayerDuaCustomText = { appPreferences.setPrePrayerDuaCustomText(it) },
                fajrAdhanSound = fajrAdhanSound,
                dhuhrAdhanSound = dhuhrAdhanSound,
                asrAdhanSound = asrAdhanSound,
                maghribAdhanSound = maghribAdhanSound,
                ishaAdhanSound = ishaAdhanSound,
                onUpdatePrayerAdhanSound = { type, sId -> appPreferences.setPrayerAdhanSound(type, sId) },
                morningAthkarSoundEnabled = morningAthkarSoundEnabled,
                onToggleMorningAthkarSound = { appPreferences.setMorningAthkarSoundEnabled(it) },
                eveningAthkarSoundEnabled = eveningAthkarSoundEnabled,
                onToggleEveningAthkarSound = { appPreferences.setEveningAthkarSoundEnabled(it) },
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
                onOpenCreatorStudio = { if (isDeveloperUnlocked) onNavigateSubScreen(SubScreen.CREATOR_STUDIO) },
                isDeveloperUnlocked = isDeveloperUnlocked,
                onSendCenterNotification = triggerCenterNotification,
                onOpenDrawer = onOpenDrawer,
                postPrayerDelayMinutes = postPrayerDelayMinutes,
                onUpdatePostPrayerDelayMinutes = { appPreferences.setPostPrayerDelayMinutes(it) },
                onBack = { onNavigateSubScreen(null) },
                modifier = modifier
            )
        }

        SubScreen.UNIFIED_QURAN_SUNNAH -> {
            val selectedRiwayahId by appPreferences.selectedRiwayahId.collectAsStateWithLifecycle()
            val selectedQariId by appPreferences.selectedQariId.collectAsStateWithLifecycle()
            val playbackState by audioPlayerManager.playbackState.collectAsStateWithLifecycle()
            val lastReadSurah by appPreferences.lastReadSurah.collectAsStateWithLifecycle()
            val bookmarkSurah by appPreferences.bookmarkSurah.collectAsStateWithLifecycle()
            val bookmarkAyah by appPreferences.bookmarkAyah.collectAsStateWithLifecycle()
            val bookmarkSurahName by appPreferences.bookmarkSurahName.collectAsStateWithLifecycle()
            val bookmarkAyahText by appPreferences.bookmarkAyahText.collectAsStateWithLifecycle()
            val hasActiveBookmark by appPreferences.hasActiveBookmark.collectAsStateWithLifecycle()
            val selectedTranslationLang by appPreferences.selectedTranslationLang.collectAsStateWithLifecycle()
            val isTranslationVisible by appPreferences.isTranslationVisible.collectAsStateWithLifecycle()
            val quranFontSize by appPreferences.quranFontSize.collectAsStateWithLifecycle()
            val isSmartSpeaking by smartVoiceManager.isSpeaking.collectAsStateWithLifecycle()

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
                    val formattedNum = String.format(Locale.US, "%03d", surah.number)
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
                onOpenDrawer = onOpenDrawer,
                onBack = { onNavigateSubScreen(null) },
                modifier = modifier
            )
        }

        SubScreen.SUNNAH_HADITH -> {
            val isSmartSpeaking by smartVoiceManager.isSpeaking.collectAsStateWithLifecycle()
            SunnahScreen(
                hadiths = repository.getSunnahHadiths(),
                onSpeakHadith = { smartVoiceManager.speakDhikr(it) },
                onStopSpeaking = { smartVoiceManager.stop() },
                isSpeaking = isSmartSpeaking,
                onBack = { onNavigateSubScreen(null) },
                modifier = modifier
            )
        }

        SubScreen.MONTHLY_CALENDAR -> {
            val calcMethod by appPreferences.calculationMethod.collectAsStateWithLifecycle()
            val juristicMethod by appPreferences.juristicMethod.collectAsStateWithLifecycle()
            val prayerOffsets by appPreferences.prayerOffsets.collectAsStateWithLifecycle()
            val cal = java.util.Calendar.getInstance()
            val monthTimes = remember(selectedCity, calcMethod, juristicMethod, prayerOffsets) {
                com.example.data.services.MonthlyPrayerCalculator.generateMonthPrayerTimes(
                    city = selectedCity,
                    year = cal.get(java.util.Calendar.YEAR),
                    month = cal.get(java.util.Calendar.MONTH) + 1,
                    method = calcMethod,
                    juristic = juristicMethod,
                    offsets = prayerOffsets
                )
            }
            MonthlyCalendarScreen(
                selectedCity = selectedCity,
                monthPrayerTimes = monthTimes,
                hijriEvents = repository.getHijriEvents(),
                onBack = { onNavigateSubScreen(null) },
                onOpenDrawer = onOpenDrawer,
                modifier = modifier
            )
        }

        SubScreen.WIDGETS_STUDIO -> {
            val widgetAccent by appPreferences.widgetAccentColor.collectAsStateWithLifecycle()
            val widgetTheme by appPreferences.widgetTheme.collectAsStateWithLifecycle()
            val widgetDhikr by appPreferences.widgetDhikr.collectAsStateWithLifecycle()
            val widgetShowTemp by appPreferences.widgetShowTemperature.collectAsStateWithLifecycle()
            val widgetTemp by appPreferences.widgetTemperature.collectAsStateWithLifecycle()

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
                onBack = { onNavigateSubScreen(null) },
                modifier = modifier
            )
        }

        SubScreen.GHAZWAT -> {
            GhazwatScreen(
                ghazwatList = repository.getBattles(),
                majorConquests = repository.getMajorConquests(),
                majorSaraya = repository.getMajorSaraya(),
                onBack = { onNavigateSubScreen(null) },
                modifier = modifier
            )
        }

        SubScreen.PRAYER_ADJUSTMENT -> {
            val prayerOffsets by appPreferences.prayerOffsets.collectAsStateWithLifecycle()
            val salawatInterval by appPreferences.salawatIntervalMinutes.collectAsStateWithLifecycle()
            val salawatSoundType by appPreferences.salawatSoundType.collectAsStateWithLifecycle()
            val prePrayerMinsBefore by appPreferences.prePrayerMinutesBefore.collectAsStateWithLifecycle()
            val prePrayerSoundType by appPreferences.prePrayerSoundType.collectAsStateWithLifecycle()
            val prePrayerBigMsg by appPreferences.prePrayerBigMessage.collectAsStateWithLifecycle()

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
                    onUpdateInitialAthkarCat("post_prayer")
                    onNavigateSubScreen(SubScreen.ATHKAR_DUAS)
                },
                onBack = { onNavigateSubScreen(null) },
                modifier = modifier
            )
        }

        SubScreen.ATHKAR_DUAS -> {
            AthkarDuasScreen(
                adhkarList = repository.getAdhkar(),
                initialCategory = initialAthkarCat,
                onSpeakDhikr = { smartVoiceManager.speakDhikr(it) },
                onStopSpeaking = { smartVoiceManager.stop() },
                onBack = { onNavigateSubScreen(null) },
                modifier = modifier
            )
        }

        SubScreen.UNIFIED_INVENTIONS_BATTLES -> {
            UnifiedInventionsBattlesScreen(
                battles = repository.getBattles(),
                majorConquests = repository.getMajorConquests(),
                majorSaraya = repository.getMajorSaraya(),
                inventions = repository.getInventions(),
                onBack = { onNavigateSubScreen(null) },
                modifier = modifier
            )
        }

        SubScreen.UNIFIED_PROPHETS_TAWBAH -> {
            val prophetStoriesList by customContentManager.prophetStories.collectAsStateWithLifecycle()
            val tawbahStoriesList by customContentManager.tawbahStories.collectAsStateWithLifecycle()

            UnifiedProphetsTawbahScreen(
                prophets = prophetStoriesList,
                tawbahStories = tawbahStoriesList,
                onBack = { onNavigateSubScreen(null) },
                onOpenDrawer = onOpenDrawer,
                onOpenCreatorStudio = { if (appPreferences.isDeveloperUnlocked.value) onNavigateSubScreen(SubScreen.CREATOR_STUDIO) },
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
                modifier = modifier
            )
        }

        SubScreen.ALERTS_AND_REMINDERS -> {
            val postPrayerDelayMinutes by appPreferences.postPrayerDelayMinutes.collectAsStateWithLifecycle()
            AlertsAndRemindersScreen(
                onBack = { onNavigateSubScreen(null) },
                onOpenDrawer = onOpenDrawer,
                postPrayerDelayMinutes = postPrayerDelayMinutes,
                onUpdatePostPrayerDelay = { appPreferences.setPostPrayerDelayMinutes(it) },
                onSendCenterNotification = triggerCenterNotification,
                onPlayTestSound = { soundType ->
                    audioPlayerManager.playAudio("https://server8.mp3quran.net/afs/001.mp3", "تنبيه صوتي", "تجربة")
                },
                modifier = modifier
            )
        }

        SubScreen.UNIFIED_QUESTIONS -> {
            UnifiedQuestionsScreen(
                religiousQAs = repository.getQAs(),
                generalQAs = repository.getGeneralQAs(),
                onBack = { onNavigateSubScreen(null) },
                modifier = modifier
            )
        }
    }
}
