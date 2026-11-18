package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.data.models.CityLocation
import com.example.data.models.PrayerType
import com.example.data.services.CalculationMethod
import com.example.data.services.JuristicMethod
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppPreferences(private val context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("salati_prefs", Context.MODE_PRIVATE)

    // Time Format Preference: 12h or 24h
    private val _timeFormat = MutableStateFlow(prefs.getString("time_format", "12h") ?: "12h")
    val timeFormat: StateFlow<String> = _timeFormat.asStateFlow()

    private val _selectedCity = MutableStateFlow(loadCity())
    val selectedCity: StateFlow<CityLocation> = _selectedCity.asStateFlow()

    private val _calculationMethod = MutableStateFlow(loadCalculationMethod())
    val calculationMethod: StateFlow<CalculationMethod> = _calculationMethod.asStateFlow()

    private val _juristicMethod = MutableStateFlow(loadJuristicMethod())
    val juristicMethod: StateFlow<JuristicMethod> = _juristicMethod.asStateFlow()

    private val _prayerOffsets = MutableStateFlow(loadOffsets())
    val prayerOffsets: StateFlow<Map<PrayerType, Int>> = _prayerOffsets.asStateFlow()

    private val _selectedMuadhin = MutableStateFlow(prefs.getString("selected_muadhin", "makkah") ?: "makkah")
    val selectedMuadhin: StateFlow<String> = _selectedMuadhin.asStateFlow()

    private val _selectedQariId = MutableStateFlow(prefs.getString("selected_qari", "alafasy") ?: "alafasy")
    val selectedQariId: StateFlow<String> = _selectedQariId.asStateFlow()

    private val _lastReadSurah = MutableStateFlow(prefs.getInt("last_read_surah", 1))
    val lastReadSurah: StateFlow<Int> = _lastReadSurah.asStateFlow()

    private val _lastReadAyah = MutableStateFlow(prefs.getInt("last_read_ayah", 1))
    val lastReadAyah: StateFlow<Int> = _lastReadAyah.asStateFlow()

    // Quran Reading Bookmark (فاصل القراءة وعلامة التوقف)
    private val _bookmarkSurah = MutableStateFlow(prefs.getInt("quran_bookmark_surah", 1))
    val bookmarkSurah: StateFlow<Int> = _bookmarkSurah.asStateFlow()

    private val _bookmarkAyah = MutableStateFlow(prefs.getInt("quran_bookmark_ayah", 1))
    val bookmarkAyah: StateFlow<Int> = _bookmarkAyah.asStateFlow()

    private val _bookmarkSurahName = MutableStateFlow(prefs.getString("quran_bookmark_surah_name", "الفاتحة") ?: "الفاتحة")
    val bookmarkSurahName: StateFlow<String> = _bookmarkSurahName.asStateFlow()

    private val _bookmarkAyahText = MutableStateFlow(
        prefs.getString("quran_bookmark_ayah_text", "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ") ?: "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ"
    )
    val bookmarkAyahText: StateFlow<String> = _bookmarkAyahText.asStateFlow()

    private val _hasActiveBookmark = MutableStateFlow(prefs.getBoolean("quran_has_bookmark", true))
    val hasActiveBookmark: StateFlow<Boolean> = _hasActiveBookmark.asStateFlow()

    // Quran Riwayah / Mushaf (روايات ومصاحف القرآن الكريم)
    private val _selectedRiwayahId = MutableStateFlow(prefs.getString("selected_riwayah", "hafs") ?: "hafs")
    val selectedRiwayahId: StateFlow<String> = _selectedRiwayahId.asStateFlow()

    // Quran Translations (ترجمة معاني القرآن الكريم)
    private val _selectedTranslationLang = MutableStateFlow(prefs.getString("selected_translation_lang", "en") ?: "en")
    val selectedTranslationLang: StateFlow<String> = _selectedTranslationLang.asStateFlow()

    private val _isTranslationVisible = MutableStateFlow(prefs.getBoolean("is_translation_visible", true))
    val isTranslationVisible: StateFlow<Boolean> = _isTranslationVisible.asStateFlow()

    // Electronic Muadhin Offline Temperature Calibration (درجة الحرارة بدون إنترنت)
    private val _temperatureOffsetC = MutableStateFlow(prefs.getInt("temp_offset_c", 0))
    val temperatureOffsetC: StateFlow<Int> = _temperatureOffsetC.asStateFlow()

    private val _isDarkMode = MutableStateFlow(prefs.getBoolean("dark_mode", true))
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _tasbihCount = MutableStateFlow(prefs.getInt("tasbih_count", 0))
    val tasbihCount: StateFlow<Int> = _tasbihCount.asStateFlow()

    private val _tasbihTotal = MutableStateFlow(prefs.getInt("tasbih_total", 0))
    val tasbihTotal: StateFlow<Int> = _tasbihTotal.asStateFlow()

    // Adhan Repetition & Alerts (per screenshots)
    private val _adhanRepeatCount = MutableStateFlow(prefs.getInt("adhan_repeat_count", 1))
    val adhanRepeatCount: StateFlow<Int> = _adhanRepeatCount.asStateFlow()

    private val _preAdhanSoundEnabled = MutableStateFlow(prefs.getBoolean("pre_adhan_sound", true))
    val preAdhanSoundEnabled: StateFlow<Boolean> = _preAdhanSoundEnabled.asStateFlow()

    private val _preAdhanMinutes = MutableStateFlow(prefs.getInt("pre_adhan_minutes", 10))
    val preAdhanMinutes: StateFlow<Int> = _preAdhanMinutes.asStateFlow()
    val prePrayerMinutesBefore: StateFlow<Int> = _preAdhanMinutes.asStateFlow()

    private val _atAdhanSoundEnabled = MutableStateFlow(prefs.getBoolean("at_adhan_sound", true))
    val atAdhanSoundEnabled: StateFlow<Boolean> = _atAdhanSoundEnabled.asStateFlow()

    private val _prePrayerBigMessage = MutableStateFlow(prefs.getBoolean("pre_prayer_big_msg", true))
    val prePrayerBigMessage: StateFlow<Boolean> = _prePrayerBigMessage.asStateFlow()

    private val _prePrayerAyahText = MutableStateFlow(
        prefs.getString("pre_prayer_ayah", "إِنَّ الصَّلَاةَ كَانَتْ عَلَى الْمُؤْمِنِينَ كِتَابًا مَّوْقُوتًا ۝ صدق الله العظيم")
            ?: "إِنَّ الصَّلَاةَ كَانَتْ عَلَى الْمُؤْمِنِينَ كِتَابًا مَّوْقُوتًا ۝ صدق الله العظيم"
    )
    val prePrayerAyahText: StateFlow<String> = _prePrayerAyahText.asStateFlow()

    // Friday Adhans
    private val _fridayFirstAdhan = MutableStateFlow(prefs.getBoolean("friday_first_adhan", true))
    val fridayFirstAdhan: StateFlow<Boolean> = _fridayFirstAdhan.asStateFlow()

    private val _fridaySecondAdhan = MutableStateFlow(prefs.getBoolean("friday_second_adhan", true))
    val fridaySecondAdhan: StateFlow<Boolean> = _fridaySecondAdhan.asStateFlow()

    private val _fridayFirstAdhanMinutesBefore = MutableStateFlow(prefs.getInt("friday_first_min_before", 20))
    val fridayFirstAdhanMinutesBefore: StateFlow<Int> = _fridayFirstAdhanMinutesBefore.asStateFlow()

    private val _fridaySoundOnly = MutableStateFlow(prefs.getBoolean("friday_sound_only", true))
    val fridaySoundOnly: StateFlow<Boolean> = _fridaySoundOnly.asStateFlow()

    private val _fridayFirstAdhanSound = MutableStateFlow(prefs.getString("friday_first_sound", "friday_1") ?: "friday_1")
    val fridayFirstAdhanSound: StateFlow<String> = _fridayFirstAdhanSound.asStateFlow()

    private val _fridaySecondAdhanSound = MutableStateFlow(prefs.getString("friday_second_sound", "friday_2") ?: "friday_2")
    val fridaySecondAdhanSound: StateFlow<String> = _fridaySecondAdhanSound.asStateFlow()

    private val _fridaySecondAdhanMinutes = MutableStateFlow(prefs.getInt("friday_second_min", 0))
    val fridaySecondAdhanMinutes: StateFlow<Int> = _fridaySecondAdhanMinutes.asStateFlow()

    // Per-prayer adhan sounds (Fajr, Dhuhr, Asr, Maghrib, Isha)
    private val _fajrAdhanSound = MutableStateFlow(prefs.getString("adhan_sound_fajr", "makkah") ?: "makkah")
    val fajrAdhanSound: StateFlow<String> = _fajrAdhanSound.asStateFlow()

    private val _dhuhrAdhanSound = MutableStateFlow(prefs.getString("adhan_sound_dhuhr", "makkah") ?: "makkah")
    val dhuhrAdhanSound: StateFlow<String> = _dhuhrAdhanSound.asStateFlow()

    private val _asrAdhanSound = MutableStateFlow(prefs.getString("adhan_sound_asr", "makkah") ?: "makkah")
    val asrAdhanSound: StateFlow<String> = _asrAdhanSound.asStateFlow()

    private val _maghribAdhanSound = MutableStateFlow(prefs.getString("adhan_sound_maghrib", "makkah") ?: "makkah")
    val maghribAdhanSound: StateFlow<String> = _maghribAdhanSound.asStateFlow()

    private val _ishaAdhanSound = MutableStateFlow(prefs.getString("adhan_sound_isha", "makkah") ?: "makkah")
    val ishaAdhanSound: StateFlow<String> = _ishaAdhanSound.asStateFlow()

    // Custom reminder texts for Salawat, Dua, Morning/Evening & Post-prayer Athkar
    private val _salawatCustomText = MutableStateFlow(prefs.getString("salawat_custom_text", "اللهم صل وسلم على نبينا محمد ﷺ") ?: "اللهم صل وسلم على نبينا محمد ﷺ")
    val salawatCustomText: StateFlow<String> = _salawatCustomText.asStateFlow()

    private val _prePrayerDuaCustomText = MutableStateFlow(prefs.getString("pre_prayer_dua_text", "اللهم إني أسألك الهدى والتقى والعفاف والغنى") ?: "اللهم إني أسألك الهدى والتقى والعفاف والغنى")
    val prePrayerDuaCustomText: StateFlow<String> = _prePrayerDuaCustomText.asStateFlow()

    private val _morningAthkarCustomText = MutableStateFlow(prefs.getString("morning_athkar_text", "أصبحنا وأصبح الملك لله والحمد لله ولا إله إلا الله وحده لا شريك له") ?: "أصبحنا وأصبح الملك لله والحمد لله ولا إله إلا الله وحده لا شريك له")
    val morningAthkarCustomText: StateFlow<String> = _morningAthkarCustomText.asStateFlow()

    private val _eveningAthkarCustomText = MutableStateFlow(prefs.getString("evening_athkar_text", "أمسينا وأمسى الملك لله والحمد لله ولا إله إلا الله وحده لا شريك له") ?: "أمسينا وأمسى الملك لله والحمد لله ولا إله إلا الله وحده لا شريك له")
    val eveningAthkarCustomText: StateFlow<String> = _eveningAthkarCustomText.asStateFlow()

    private val _postPrayerCustomText = MutableStateFlow(prefs.getString("post_prayer_text", "أستغفر الله (٣ مرات)، اللهم أنت السلام ومنك السلام، تباركت يا ذا الجلال والإكرام") ?: "أستغفر الله (٣ مرات)، اللهم أنت السلام ومنك السلام، تباركت يا ذا الجلال والإكرام")
    val postPrayerCustomText: StateFlow<String> = _postPrayerCustomText.asStateFlow()

    // Reminders
    private val _postPrayerAthkarEnabled = MutableStateFlow(prefs.getBoolean("post_prayer_athkar", true))
    val postPrayerAthkarEnabled: StateFlow<Boolean> = _postPrayerAthkarEnabled.asStateFlow()

    private val _postPrayerAudioEnabled = MutableStateFlow(prefs.getBoolean("post_prayer_athkar_sound_enabled", true))
    val postPrayerAudioEnabled: StateFlow<Boolean> = _postPrayerAudioEnabled.asStateFlow()

    private val _postPrayerAthkarFullList = MutableStateFlow(prefs.getBoolean("post_prayer_athkar_full", true))
    val postPrayerAthkarFullList: StateFlow<Boolean> = _postPrayerAthkarFullList.asStateFlow()

    private val _iqamaReminderEnabled = MutableStateFlow(prefs.getBoolean("iqama_reminder", true))
    val iqamaReminderEnabled: StateFlow<Boolean> = _iqamaReminderEnabled.asStateFlow()

    private val _iqamaMinutesAfter = MutableStateFlow(prefs.getInt("iqama_minutes_after", 15))
    val iqamaMinutesAfter: StateFlow<Int> = _iqamaMinutesAfter.asStateFlow()

    private val _iqamaSoundType = MutableStateFlow(prefs.getString("iqama_sound_type", "iqama_takbeer") ?: "iqama_takbeer")
    val iqamaSoundType: StateFlow<String> = _iqamaSoundType.asStateFlow()

    private val _suhoorReminderEnabled = MutableStateFlow(prefs.getBoolean("suhoor_reminder", false))
    val suhoorReminderEnabled: StateFlow<Boolean> = _suhoorReminderEnabled.asStateFlow()

    private val _iftarReminderEnabled = MutableStateFlow(prefs.getBoolean("iftar_reminder", false))
    val iftarReminderEnabled: StateFlow<Boolean> = _iftarReminderEnabled.asStateFlow()

    private val _salawatReminderEnabled = MutableStateFlow(prefs.getBoolean("salawat_enabled", true))
    val salawatReminderEnabled: StateFlow<Boolean> = _salawatReminderEnabled.asStateFlow()

    private val _salawatMode = MutableStateFlow(prefs.getString("salawat_mode", "interval") ?: "interval")
    val salawatMode: StateFlow<String> = _salawatMode.asStateFlow()

    private val _salawatIntervalMinutes = MutableStateFlow(prefs.getInt("salawat_interval", 5))
    val salawatIntervalMinutes: StateFlow<Int> = _salawatIntervalMinutes.asStateFlow()

    private val _salawatSoundType = MutableStateFlow(prefs.getString("salawat_sound_type", "tts_calm") ?: "tts_calm")
    val salawatSoundType: StateFlow<String> = _salawatSoundType.asStateFlow()

    private val _prePrayerSoundType = MutableStateFlow(prefs.getString("pre_prayer_sound_type", "tts_verse") ?: "tts_verse")
    val prePrayerSoundType: StateFlow<String> = _prePrayerSoundType.asStateFlow()

    private val _showWeatherHome = MutableStateFlow(prefs.getBoolean("show_weather_home", true))
    val showWeatherHome: StateFlow<Boolean> = _showWeatherHome.asStateFlow()

    private val _quranFontSize = MutableStateFlow(prefs.getInt("quran_font_size", 22))
    val quranFontSize: StateFlow<Int> = _quranFontSize.asStateFlow()

    private val _morningAthkarReminder = MutableStateFlow(prefs.getBoolean("morning_athkar_rem", true))
    val morningAthkarReminder: StateFlow<Boolean> = _morningAthkarReminder.asStateFlow()

    private val _morningAthkarTime = MutableStateFlow(prefs.getString("morning_athkar_time", "06:00") ?: "06:00")
    val morningAthkarTime: StateFlow<String> = _morningAthkarTime.asStateFlow()

    private val _morningAthkarSound = MutableStateFlow(prefs.getString("morning_athkar_sound", "athkar_morning_voice") ?: "athkar_morning_voice")
    val morningAthkarSound: StateFlow<String> = _morningAthkarSound.asStateFlow()

    private val _morningAthkarSoundEnabled = MutableStateFlow(prefs.getBoolean("morning_athkar_sound_enabled", true))
    val morningAthkarSoundEnabled: StateFlow<Boolean> = _morningAthkarSoundEnabled.asStateFlow()

    private val _eveningAthkarReminder = MutableStateFlow(prefs.getBoolean("evening_athkar_rem", true))
    val eveningAthkarReminder: StateFlow<Boolean> = _eveningAthkarReminder.asStateFlow()

    private val _eveningAthkarTime = MutableStateFlow(prefs.getString("evening_athkar_time", "15:00") ?: "15:00")
    val eveningAthkarTime: StateFlow<String> = _eveningAthkarTime.asStateFlow()

    private val _eveningAthkarSound = MutableStateFlow(prefs.getString("evening_athkar_sound", "athkar_evening_voice") ?: "athkar_evening_voice")
    val eveningAthkarSound: StateFlow<String> = _eveningAthkarSound.asStateFlow()

    private val _eveningAthkarSoundEnabled = MutableStateFlow(prefs.getBoolean("evening_athkar_sound_enabled", true))
    val eveningAthkarSoundEnabled: StateFlow<Boolean> = _eveningAthkarSoundEnabled.asStateFlow()

    // Post-Prayer Athkar Reminder Options (e.g. at 5:00 AM or after prayer)
    private val _postPrayerAthkarReminder = MutableStateFlow(prefs.getBoolean("post_prayer_athkar_rem", true))
    val postPrayerAthkarReminder: StateFlow<Boolean> = _postPrayerAthkarReminder.asStateFlow()

    private val _postPrayerAthkarTime = MutableStateFlow(prefs.getString("post_prayer_athkar_time", "05:00") ?: "05:00")
    val postPrayerAthkarTime: StateFlow<String> = _postPrayerAthkarTime.asStateFlow()

    private val _postPrayerAthkarSound = MutableStateFlow(prefs.getString("post_prayer_athkar_sound", "athkar_morning_voice") ?: "athkar_morning_voice")
    val postPrayerAthkarSound: StateFlow<String> = _postPrayerAthkarSound.asStateFlow()

    // Calendar Options
    private val _hijriCalendarType = MutableStateFlow(prefs.getString("hijri_type", "auto") ?: "auto")
    val hijriCalendarType: StateFlow<String> = _hijriCalendarType.asStateFlow()

    private val _hijriAdjustmentDays = MutableStateFlow(prefs.getInt("hijri_adjustment", 0))
    val hijriAdjustmentDays: StateFlow<Int> = _hijriAdjustmentDays.asStateFlow()

    private val _monthDisplayMode = MutableStateFlow(prefs.getString("month_display_mode", "both") ?: "both")
    val monthDisplayMode: StateFlow<String> = _monthDisplayMode.asStateFlow()

    // UTC Mode
    private val _utcMode = MutableStateFlow(prefs.getString("utc_mode", "auto") ?: "auto")
    val utcMode: StateFlow<String> = _utcMode.asStateFlow()

    private val _manualUtcHours = MutableStateFlow(prefs.getFloat("manual_utc_hours", 3.0f).toDouble())
    val manualUtcHours: StateFlow<Double> = _manualUtcHours.asStateFlow()

    // Wallpaper Theme
    private val _appBackgroundTheme = MutableStateFlow(prefs.getString("bg_theme", "emerald") ?: "emerald")
    val appBackgroundTheme: StateFlow<String> = _appBackgroundTheme.asStateFlow()

    // Creator / Developer Account (Locked strictly unless email is bnb40195@sat and answer is صلاتي)
    private val _developerEmail = MutableStateFlow(prefs.getString("developer_email", "bnb40195@sat") ?: "bnb40195@sat")
    val developerEmail: StateFlow<String> = _developerEmail.asStateFlow()

    private val _developerSecurityQuestion = MutableStateFlow("ما هو اسم التطبيق الأساسي؟")
    val developerSecurityQuestion: StateFlow<String> = _developerSecurityQuestion.asStateFlow()

    private val _developerSecurityAnswer = MutableStateFlow(prefs.getString("dev_sec_ans", "صلاتي") ?: "صلاتي")
    val developerSecurityAnswer: StateFlow<String> = _developerSecurityAnswer.asStateFlow()

    private val _isAccountAuthenticated = MutableStateFlow(prefs.getBoolean("account_authenticated", false))
    val isAccountAuthenticated: StateFlow<Boolean> = _isAccountAuthenticated.asStateFlow()

    private val _accountUserName = MutableStateFlow(prefs.getString("account_user_name", "") ?: "")
    val accountUserName: StateFlow<String> = _accountUserName.asStateFlow()

    private val _isDeveloperUnlocked = MutableStateFlow(
        prefs.getBoolean("account_authenticated", false) && prefs.getBoolean("dev_unlocked", false)
    )
    val isDeveloperUnlocked: StateFlow<Boolean> = _isDeveloperUnlocked.asStateFlow()

    // Account Email Registration / Sign-In
    private val _googleAccountEmail = MutableStateFlow(prefs.getString("google_account_email", "") ?: "")
    val googleAccountEmail: StateFlow<String> = _googleAccountEmail.asStateFlow()

    private val _isGoogleSignedIn = MutableStateFlow(prefs.getBoolean("account_authenticated", false))
    val isGoogleSignedIn: StateFlow<Boolean> = _isGoogleSignedIn.asStateFlow()

    // Athkar Reminder Mode: "both" (voice & text), "voice", "text"
    private val _athkarReminderMode = MutableStateFlow(prefs.getString("athkar_reminder_mode", "both") ?: "both")
    val athkarReminderMode: StateFlow<String> = _athkarReminderMode.asStateFlow()

    // Quick Start / Fast App Entry
    private val _quickStartEnabled = MutableStateFlow(prefs.getBoolean("quick_start_enabled", true))
    val quickStartEnabled: StateFlow<Boolean> = _quickStartEnabled.asStateFlow()

    // Widget & UI Customization Preferences
    private val _widgetAccentColor = MutableStateFlow(prefs.getString("widget_accent", "orange") ?: "orange")
    val widgetAccentColor: StateFlow<String> = _widgetAccentColor.asStateFlow()

    private val _widgetTheme = MutableStateFlow(prefs.getString("widget_theme", "forest") ?: "forest")
    val widgetTheme: StateFlow<String> = _widgetTheme.asStateFlow()

    private val _widgetDhikr = MutableStateFlow(prefs.getString("widget_dhikr", "سبحان الله العظيم") ?: "سبحان الله العظيم")
    val widgetDhikr: StateFlow<String> = _widgetDhikr.asStateFlow()

    private val _widgetShowTemperature = MutableStateFlow(prefs.getBoolean("widget_show_temp", true))
    val widgetShowTemperature: StateFlow<Boolean> = _widgetShowTemperature.asStateFlow()

    private val _widgetTemperature = MutableStateFlow(prefs.getString("widget_temp", "24°C") ?: "24°C")
    val widgetTemperature: StateFlow<String> = _widgetTemperature.asStateFlow()

    // Post prayer reminder delay mode (e.g. 5 minutes after prayer)
    private val _postPrayerDelayMinutes = MutableStateFlow(prefs.getInt("post_prayer_delay_mins", 5))
    val postPrayerDelayMinutes: StateFlow<Int> = _postPrayerDelayMinutes.asStateFlow()

    private val _postPrayerReminderMode = MutableStateFlow(prefs.getString("post_prayer_mode", "delay_5min") ?: "delay_5min")
    val postPrayerReminderMode: StateFlow<String> = _postPrayerReminderMode.asStateFlow()

    private val _widgetTransparentBg = MutableStateFlow(prefs.getBoolean("widget_transparent", false))
    val widgetTransparentBg: StateFlow<Boolean> = _widgetTransparentBg.asStateFlow()

    private val _widgetCardCorner = MutableStateFlow(prefs.getInt("widget_corner", 16))
    val widgetCardCorner: StateFlow<Int> = _widgetCardCorner.asStateFlow()

    private val _widgetShowCountdown = MutableStateFlow(prefs.getBoolean("widget_countdown", true))
    val widgetShowCountdown: StateFlow<Boolean> = _widgetShowCountdown.asStateFlow()

    // App Language & Font Preferences
    private val _appLanguage = MutableStateFlow(prefs.getString("app_language", "ar") ?: "ar")
    val appLanguage: StateFlow<String> = _appLanguage.asStateFlow()

    // Digit Format: "western" (1234) or "eastern" (١٢٣٤ - أرقام جوجل)
    private val _digitType = MutableStateFlow(prefs.getString("digit_type", "western") ?: "western")
    val digitType: StateFlow<String> = _digitType.asStateFlow()

    fun setDigitType(type: String) {
        _digitType.value = type
        prefs.edit().putString("digit_type", type).apply()
        com.example.widget.PrayerAppWidgetProvider.updateAllWidgets(context)
    }

    // Ongoing Notification in Notification Center
    private val _ongoingPrayerNotification = MutableStateFlow(prefs.getBoolean("ongoing_prayer_notification", true))
    val ongoingPrayerNotification: StateFlow<Boolean> = _ongoingPrayerNotification.asStateFlow()

    fun setOngoingPrayerNotification(enabled: Boolean) {
        _ongoingPrayerNotification.value = enabled
        prefs.edit().putBoolean("ongoing_prayer_notification", enabled).apply()
    }

    // Auto GPS Location on entry
    private val _autoGpsLocation = MutableStateFlow(prefs.getBoolean("auto_gps_location", true))
    val autoGpsLocation: StateFlow<Boolean> = _autoGpsLocation.asStateFlow()

    fun setAutoGpsLocation(enabled: Boolean) {
        _autoGpsLocation.value = enabled
        prefs.edit().putBoolean("auto_gps_location", enabled).apply()
    }

    private val _appFontFamily = MutableStateFlow(prefs.getString("app_font_family", "cairo") ?: "cairo")
    val appFontFamily: StateFlow<String> = _appFontFamily.asStateFlow()

    private val _appFontScale = MutableStateFlow(prefs.getFloat("app_font_scale", 1.0f))
    val appFontScale: StateFlow<Float> = _appFontScale.asStateFlow()

    fun setCity(city: CityLocation) {
        prefs.edit()
            .putString("city_name_ar", city.nameAr)
            .putString("city_name_en", city.nameEn)
            .putString("country_ar", city.countryAr)
            .putString("country_en", city.countryEn)
            .putFloat("lat", city.latitude.toFloat())
            .putFloat("lng", city.longitude.toFloat())
            .putFloat("tz", city.timezoneOffsetHours.toFloat())
            .apply()
        _selectedCity.value = city
    }

    private fun loadCity(): CityLocation {
        val nameAr = prefs.getString("city_name_ar", "جاري تحديد الموقع...") ?: "جاري تحديد الموقع..."
        val nameEn = prefs.getString("city_name_en", "Detecting...") ?: "Detecting..."
        val countryAr = prefs.getString("country_ar", "الموقع الجغرافي الحقيقي") ?: "الموقع الجغرافي الحقيقي"
        val countryEn = prefs.getString("country_en", "") ?: ""
        val lat = prefs.getFloat("lat", 21.4225f).toDouble()
        val lng = prefs.getFloat("lng", 39.8262f).toDouble()
        val tz = prefs.getFloat("tz", 3.0f).toDouble()
        return CityLocation(nameAr, nameEn, countryAr, countryEn, lat, lng, tz)
    }

    fun setCalculationMethod(method: CalculationMethod) {
        prefs.edit().putString("calc_method", method.name).apply()
        _calculationMethod.value = method
    }

    private fun loadCalculationMethod(): CalculationMethod {
        val name = prefs.getString("calc_method", CalculationMethod.UMM_AL_QURA.name)
        return try {
            CalculationMethod.valueOf(name ?: CalculationMethod.UMM_AL_QURA.name)
        } catch (e: Exception) {
            CalculationMethod.UMM_AL_QURA
        }
    }

    fun setJuristicMethod(juristic: JuristicMethod) {
        prefs.edit().putString("juristic_method", juristic.name).apply()
        _juristicMethod.value = juristic
    }

    private fun loadJuristicMethod(): JuristicMethod {
        val name = prefs.getString("juristic_method", JuristicMethod.SHAFI_STANDARD.name)
        return try {
            JuristicMethod.valueOf(name ?: JuristicMethod.SHAFI_STANDARD.name)
        } catch (e: Exception) {
            JuristicMethod.SHAFI_STANDARD
        }
    }

    fun setPrayerOffset(type: PrayerType, minutes: Int) {
        prefs.edit().putInt("offset_${type.name}", minutes).apply()
        val newMap = _prayerOffsets.value.toMutableMap()
        newMap[type] = minutes
        _prayerOffsets.value = newMap
    }

    private fun loadOffsets(): Map<PrayerType, Int> {
        val map = mutableMapOf<PrayerType, Int>()
        for (type in PrayerType.values()) {
            val offset = prefs.getInt("offset_${type.name}", 0)
            if (offset != 0) map[type] = offset
        }
        return map
    }

    fun setSelectedMuadhin(muadhinId: String) {
        prefs.edit().putString("selected_muadhin", muadhinId).apply()
        _selectedMuadhin.value = muadhinId
    }

    fun setSelectedQariId(qariId: String) {
        prefs.edit().putString("selected_qari", qariId).apply()
        _selectedQariId.value = qariId
    }

    fun setLastRead(surahNumber: Int, ayahNumber: Int) {
        prefs.edit()
            .putInt("last_read_surah", surahNumber)
            .putInt("last_read_ayah", ayahNumber)
            .apply()
        _lastReadSurah.value = surahNumber
        _lastReadAyah.value = ayahNumber
    }

    fun setBookmark(surahNumber: Int, surahName: String, ayahNumber: Int, ayahText: String) {
        prefs.edit()
            .putInt("quran_bookmark_surah", surahNumber)
            .putString("quran_bookmark_surah_name", surahName)
            .putInt("quran_bookmark_ayah", ayahNumber)
            .putString("quran_bookmark_ayah_text", ayahText)
            .putBoolean("quran_has_bookmark", true)
            .apply()
        _bookmarkSurah.value = surahNumber
        _bookmarkSurahName.value = surahName
        _bookmarkAyah.value = ayahNumber
        _bookmarkAyahText.value = ayahText
        _hasActiveBookmark.value = true
        // Also update last read
        setLastRead(surahNumber, ayahNumber)
    }

    fun clearBookmark() {
        prefs.edit().putBoolean("quran_has_bookmark", false).apply()
        _hasActiveBookmark.value = false
    }

    fun setSelectedRiwayahId(riwayahId: String) {
        prefs.edit().putString("selected_riwayah", riwayahId).apply()
        _selectedRiwayahId.value = riwayahId
    }

    fun setSelectedTranslationLang(langCode: String) {
        prefs.edit().putString("selected_translation_lang", langCode).apply()
        _selectedTranslationLang.value = langCode
    }

    fun setTranslationVisible(visible: Boolean) {
        prefs.edit().putBoolean("is_translation_visible", visible).apply()
        _isTranslationVisible.value = visible
    }

    fun setTemperatureOffsetC(offset: Int) {
        prefs.edit().putInt("temp_offset_c", offset).apply()
        _temperatureOffsetC.value = offset
    }

    fun setDarkMode(isDark: Boolean) {
        prefs.edit().putBoolean("dark_mode", isDark).apply()
        _isDarkMode.value = isDark
    }

    fun setAdhanOptions(
        repeatCount: Int,
        preSound: Boolean,
        preMinutes: Int,
        atSound: Boolean,
        bigMessage: Boolean
    ) {
        prefs.edit()
            .putInt("adhan_repeat_count", repeatCount)
            .putBoolean("pre_adhan_sound", preSound)
            .putInt("pre_adhan_minutes", preMinutes)
            .putBoolean("at_adhan_sound", atSound)
            .putBoolean("pre_prayer_big_msg", bigMessage)
            .apply()
        _adhanRepeatCount.value = repeatCount
        _preAdhanSoundEnabled.value = preSound
        _preAdhanMinutes.value = preMinutes
        _atAdhanSoundEnabled.value = atSound
        _prePrayerBigMessage.value = bigMessage
    }

    fun setFridaySettings(first: Boolean, second: Boolean, soundOnly: Boolean, firstMinBefore: Int = 20, secondMin: Int = 0) {
        prefs.edit()
            .putBoolean("friday_first_adhan", first)
            .putBoolean("friday_second_adhan", second)
            .putBoolean("friday_sound_only", soundOnly)
            .putInt("friday_first_min_before", firstMinBefore)
            .putInt("friday_second_min", secondMin)
            .apply()
        _fridayFirstAdhan.value = first
        _fridaySecondAdhan.value = second
        _fridaySoundOnly.value = soundOnly
        _fridayFirstAdhanMinutesBefore.value = firstMinBefore
        _fridaySecondAdhanMinutes.value = secondMin
    }

    fun setFridayFirstAdhanSound(soundId: String) {
        prefs.edit().putString("friday_first_sound", soundId).apply()
        _fridayFirstAdhanSound.value = soundId
    }

    fun setFridaySecondAdhanSound(soundId: String) {
        prefs.edit().putString("friday_second_sound", soundId).apply()
        _fridaySecondAdhanSound.value = soundId
    }

    fun setFridaySecondAdhanMinutes(mins: Int) {
        prefs.edit().putInt("friday_second_min", mins).apply()
        _fridaySecondAdhanMinutes.value = mins
    }

    fun setFridayMinutes(firstMinBefore: Int, secondMin: Int) {
        prefs.edit()
            .putInt("friday_first_min_before", firstMinBefore)
            .putInt("friday_second_min", secondMin)
            .apply()
        _fridayFirstAdhanMinutesBefore.value = firstMinBefore
        _fridaySecondAdhanMinutes.value = secondMin
    }

    fun setSalawatCustomText(text: String) {
        prefs.edit().putString("salawat_custom_text", text).apply()
        _salawatCustomText.value = text
    }

    fun setPrePrayerDuaCustomText(text: String) {
        prefs.edit().putString("pre_prayer_dua_text", text).apply()
        _prePrayerDuaCustomText.value = text
    }

    fun setMorningAthkarCustomText(text: String) {
        prefs.edit().putString("morning_athkar_text", text).apply()
        _morningAthkarCustomText.value = text
    }

    fun setEveningAthkarCustomText(text: String) {
        prefs.edit().putString("evening_athkar_text", text).apply()
        _eveningAthkarCustomText.value = text
    }

    fun setPostPrayerCustomText(text: String) {
        prefs.edit().putString("post_prayer_text", text).apply()
        _postPrayerCustomText.value = text
    }

    fun setSalawatSettings(enabled: Boolean, mode: String, intervalMinutes: Int) {
        prefs.edit()
            .putBoolean("salawat_enabled", enabled)
            .putString("salawat_mode", mode)
            .putInt("salawat_interval", intervalMinutes)
            .apply()
        _salawatReminderEnabled.value = enabled
        _salawatMode.value = mode
        _salawatIntervalMinutes.value = intervalMinutes
    }

    fun setSalawatInterval(minutes: Int) {
        prefs.edit().putInt("salawat_interval", minutes).apply()
        _salawatIntervalMinutes.value = minutes
    }

    fun setSalawatSoundType(soundType: String) {
        prefs.edit().putString("salawat_sound_type", soundType).apply()
        _salawatSoundType.value = soundType
    }

    fun setPrePrayerSoundType(soundType: String) {
        prefs.edit().putString("pre_prayer_sound_type", soundType).apply()
        _prePrayerSoundType.value = soundType
    }

    fun setShowWeatherHome(show: Boolean) {
        prefs.edit().putBoolean("show_weather_home", show).apply()
        _showWeatherHome.value = show
    }

    fun setQuranFontSize(size: Int) {
        prefs.edit().putInt("quran_font_size", size).apply()
        _quranFontSize.value = size
    }

    fun setPrePrayerBigMessage(enabled: Boolean, minutesBefore: Int = 10) {
        prefs.edit()
            .putBoolean("pre_prayer_big_msg", enabled)
            .putInt("pre_adhan_minutes", minutesBefore)
            .apply()
        _prePrayerBigMessage.value = enabled
        _preAdhanMinutes.value = minutesBefore
    }

    fun incrementTasbih() {
        val newCount = _tasbihCount.value + 1
        val newTotal = _tasbihTotal.value + 1
        prefs.edit().putInt("tasbih_count", newCount).putInt("tasbih_total", newTotal).apply()
        _tasbihCount.value = newCount
        _tasbihTotal.value = newTotal
    }

    fun resetTasbih() {
        prefs.edit().putInt("tasbih_count", 0).apply()
        _tasbihCount.value = 0
    }

    fun setPrePrayerSettings(enabled: Boolean, minutesBefore: Int, ayahText: String) {
        prefs.edit()
            .putBoolean("pre_prayer_big_msg", enabled)
            .putInt("pre_adhan_minutes", minutesBefore)
            .putString("pre_prayer_ayah", ayahText)
            .apply()
        _prePrayerBigMessage.value = enabled
        _preAdhanMinutes.value = minutesBefore
        _prePrayerAyahText.value = ayahText
    }

    fun setReminderToggles(
        postPrayerAthkar: Boolean,
        postPrayerSound: Boolean,
        postPrayerFull: Boolean,
        iqama: Boolean,
        suhoor: Boolean,
        iftar: Boolean,
        morning: Boolean,
        evening: Boolean
    ) {
        prefs.edit()
            .putBoolean("post_prayer_athkar", postPrayerAthkar)
            .putBoolean("post_prayer_athkar_sound", postPrayerSound)
            .putBoolean("post_prayer_athkar_full", postPrayerFull)
            .putBoolean("iqama_reminder", iqama)
            .putBoolean("suhoor_reminder", suhoor)
            .putBoolean("iftar_reminder", iftar)
            .putBoolean("morning_athkar_rem", morning)
            .putBoolean("evening_athkar_rem", evening)
            .apply()
        _postPrayerAthkarEnabled.value = postPrayerAthkar
        _postPrayerAudioEnabled.value = postPrayerSound
        _postPrayerAthkarFullList.value = postPrayerFull
        _iqamaReminderEnabled.value = iqama
        _suhoorReminderEnabled.value = suhoor
        _iftarReminderEnabled.value = iftar
        _morningAthkarReminder.value = morning
        _eveningAthkarReminder.value = evening
    }

    fun setIqamaReminder(enabled: Boolean, minutesAfter: Int = 15, soundType: String = "iqama_takbeer") {
        prefs.edit()
            .putBoolean("iqama_reminder", enabled)
            .putInt("iqama_minutes_after", minutesAfter)
            .putString("iqama_sound_type", soundType)
            .apply()
        _iqamaReminderEnabled.value = enabled
        _iqamaMinutesAfter.value = minutesAfter
        _iqamaSoundType.value = soundType
    }

    fun setMorningAthkarSettings(enabled: Boolean, time: String) {
        prefs.edit()
            .putBoolean("morning_athkar_rem", enabled)
            .putString("morning_athkar_time", time)
            .apply()
        _morningAthkarReminder.value = enabled
        _morningAthkarTime.value = time
    }

    fun setMorningAthkarSound(soundId: String) {
        prefs.edit().putString("morning_athkar_sound", soundId).apply()
        _morningAthkarSound.value = soundId
    }

    fun setEveningAthkarSettings(enabled: Boolean, time: String) {
        prefs.edit()
            .putBoolean("evening_athkar_rem", enabled)
            .putString("evening_athkar_time", time)
            .apply()
        _eveningAthkarReminder.value = enabled
        _eveningAthkarTime.value = time
    }

    fun setEveningAthkarSound(soundId: String) {
        prefs.edit().putString("evening_athkar_sound", soundId).apply()
        _eveningAthkarSound.value = soundId
    }

    fun setMorningAthkarSoundEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("morning_athkar_sound_enabled", enabled).apply()
        _morningAthkarSoundEnabled.value = enabled
    }

    fun setEveningAthkarSoundEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("evening_athkar_sound_enabled", enabled).apply()
        _eveningAthkarSoundEnabled.value = enabled
    }

    fun setPrayerAdhanSound(prayerType: PrayerType, soundId: String) {
        val key = when (prayerType) {
            PrayerType.FAJR -> "adhan_sound_fajr"
            PrayerType.DHUHR -> "adhan_sound_dhuhr"
            PrayerType.ASR -> "adhan_sound_asr"
            PrayerType.MAGHRIB -> "adhan_sound_maghrib"
            PrayerType.ISHA -> "adhan_sound_isha"
            else -> "selected_muadhin"
        }
        prefs.edit().putString(key, soundId).apply()
        when (prayerType) {
            PrayerType.FAJR -> _fajrAdhanSound.value = soundId
            PrayerType.DHUHR -> _dhuhrAdhanSound.value = soundId
            PrayerType.ASR -> _asrAdhanSound.value = soundId
            PrayerType.MAGHRIB -> _maghribAdhanSound.value = soundId
            PrayerType.ISHA -> _ishaAdhanSound.value = soundId
            else -> _selectedMuadhin.value = soundId
        }
    }

    fun getPrayerAdhanSound(prayerType: PrayerType): String {
        return when (prayerType) {
            PrayerType.FAJR -> _fajrAdhanSound.value
            PrayerType.DHUHR -> _dhuhrAdhanSound.value
            PrayerType.ASR -> _asrAdhanSound.value
            PrayerType.MAGHRIB -> _maghribAdhanSound.value
            PrayerType.ISHA -> _ishaAdhanSound.value
            else -> _selectedMuadhin.value
        }
    }

    fun setPostPrayerAthkarSettings(enabled: Boolean, time: String) {
        prefs.edit()
            .putBoolean("post_prayer_athkar_rem", enabled)
            .putString("post_prayer_athkar_time", time)
            .apply()
        _postPrayerAthkarReminder.value = enabled
        _postPrayerAthkarTime.value = time
    }

    fun setPostPrayerAthkarSound(soundId: String) {
        prefs.edit().putString("post_prayer_athkar_sound", soundId).apply()
        _postPrayerAthkarSound.value = soundId
    }

    fun setHijriSettings(type: String, adjustment: Int, monthDisplay: String) {
        prefs.edit()
            .putString("hijri_type", type)
            .putInt("hijri_adjustment", adjustment)
            .putString("month_display_mode", monthDisplay)
            .apply()
        _hijriCalendarType.value = type
        _hijriAdjustmentDays.value = adjustment
        _monthDisplayMode.value = monthDisplay
    }

    fun setUtcSettings(mode: String, hours: Double) {
        prefs.edit()
            .putString("utc_mode", mode)
            .putFloat("manual_utc_hours", hours.toFloat())
            .apply()
        _utcMode.value = mode
        _manualUtcHours.value = hours
    }

    fun setAppBackgroundTheme(theme: String) {
        prefs.edit().putString("bg_theme", theme).apply()
        _appBackgroundTheme.value = theme
    }

    fun setDeveloperEmail(email: String) {
        prefs.edit().putString("developer_email", email).apply()
        _developerEmail.value = email
    }

    fun setDeveloperSecurityAnswer(answer: String) {
        prefs.edit().putString("dev_sec_ans", answer).apply()
        _developerSecurityAnswer.value = answer
    }

    fun setDeveloperUnlocked(unlocked: Boolean) {
        prefs.edit().putBoolean("dev_unlocked", unlocked).apply()
        _isDeveloperUnlocked.value = unlocked
    }

    fun checkAndUnlockDeveloper(email: String, answer: String): Boolean {
        val cleanEmail = email.trim()
        val cleanAnswer = answer.trim()
        val isMatch = cleanEmail.equals("bnb40195@sat", ignoreCase = true) && (cleanAnswer == "صلاتي")
        setDeveloperUnlocked(isMatch)
        return isMatch
    }

    fun registerUserAccount(name: String, email: String, password: String): Pair<Boolean, String?> {
        val cleanName = name.trim()
        val cleanEmail = email.trim()
        val cleanPass = password.trim()
        if (cleanEmail.isEmpty() || !cleanEmail.contains("@")) {
            return false to "يرجى إدخال بريد إلكتروني صحيح"
        }
        if (cleanPass.isEmpty()) {
            return false to "يرجى إدخال كلمة المرور"
        }
        val isMakerEmail = cleanEmail.equals("bnb40195@sat", ignoreCase = true)
        if (isMakerEmail && cleanPass != "صلاتي") {
            return false to "كلمة المرور غير صحيحة"
        }
        val isMakerMatch = isMakerEmail && cleanPass == "صلاتي"
        val keyEmail = cleanEmail.lowercase()
        prefs.edit()
            .putString("account_pass_$keyEmail", cleanPass)
            .putString("account_name_$keyEmail", cleanName)
            .putString("account_user_name", cleanName.ifEmpty { cleanEmail.substringBefore("@") })
            .putString("google_account_email", cleanEmail)
            .putBoolean("google_signed_in", true)
            .putBoolean("account_authenticated", true)
            .putBoolean("dev_unlocked", isMakerMatch)
            .apply()
        _accountUserName.value = cleanName.ifEmpty { cleanEmail.substringBefore("@") }
        _googleAccountEmail.value = cleanEmail
        _isGoogleSignedIn.value = true
        _isAccountAuthenticated.value = true
        _isDeveloperUnlocked.value = isMakerMatch
        return true to null
    }

    fun signInUserAccount(email: String, password: String): Pair<Boolean, String?> {
        val cleanEmail = email.trim()
        val cleanPass = password.trim()
        if (cleanEmail.isEmpty() || !cleanEmail.contains("@")) {
            return false to "يرجى إدخال بريد إلكتروني صحيح"
        }
        if (cleanPass.isEmpty()) {
            return false to "يرجى إدخال كلمة المرور"
        }
        val isMakerEmail = cleanEmail.equals("bnb40195@sat", ignoreCase = true)
        if (isMakerEmail) {
            if (cleanPass != "صلاتي") {
                return false to "كلمة المرور غير صحيحة"
            }
            prefs.edit()
                .putString("account_user_name", "صانع التطبيق")
                .putString("google_account_email", cleanEmail)
                .putBoolean("google_signed_in", true)
                .putBoolean("account_authenticated", true)
                .putBoolean("dev_unlocked", true)
                .apply()
            _accountUserName.value = "صانع التطبيق"
            _googleAccountEmail.value = cleanEmail
            _isGoogleSignedIn.value = true
            _isAccountAuthenticated.value = true
            _isDeveloperUnlocked.value = true
            return true to null
        }

        val keyEmail = cleanEmail.lowercase()
        val storedPass = prefs.getString("account_pass_$keyEmail", null)
        if (storedPass != null && storedPass != cleanPass) {
            return false to "البريد الإلكتروني أو كلمة المرور غير صحيحة"
        }
        val savedName = prefs.getString("account_name_$keyEmail", "") ?: ""
        val displayName = savedName.ifEmpty { cleanEmail.substringBefore("@") }
        prefs.edit()
            .putString("account_pass_$keyEmail", cleanPass)
            .putString("account_user_name", displayName)
            .putString("google_account_email", cleanEmail)
            .putBoolean("google_signed_in", true)
            .putBoolean("account_authenticated", true)
            .putBoolean("dev_unlocked", false)
            .apply()
        _accountUserName.value = displayName
        _googleAccountEmail.value = cleanEmail
        _isGoogleSignedIn.value = true
        _isAccountAuthenticated.value = true
        _isDeveloperUnlocked.value = false
        return true to null
    }

    fun logoutAccount() {
        prefs.edit()
            .putBoolean("account_authenticated", false)
            .putBoolean("google_signed_in", false)
            .putBoolean("dev_unlocked", false)
            .apply()
        _isAccountAuthenticated.value = false
        _isGoogleSignedIn.value = false
        _isDeveloperUnlocked.value = false
    }

    fun setPostPrayerDelayMinutes(mins: Int) {
        prefs.edit().putInt("post_prayer_delay_mins", mins).apply()
        _postPrayerDelayMinutes.value = mins
    }

    fun lockDeveloper() {
        setDeveloperUnlocked(false)
    }

    fun setGoogleAccount(email: String, signedIn: Boolean) {
        prefs.edit()
            .putString("google_account_email", email.trim())
            .putBoolean("google_signed_in", signedIn)
            .apply()
        _googleAccountEmail.value = email.trim()
        _isGoogleSignedIn.value = signedIn
    }

    fun setAthkarReminderMode(mode: String) {
        prefs.edit().putString("athkar_reminder_mode", mode).apply()
        _athkarReminderMode.value = mode
    }

    fun setQuickStartEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("quick_start_enabled", enabled).apply()
        _quickStartEnabled.value = enabled
    }

    fun setWidgetAccentColor(colorName: String) {
        prefs.edit().putString("widget_accent", colorName).apply()
        _widgetAccentColor.value = colorName
    }

    fun setWidgetCustomization(theme: String, dhikr: String, showTemp: Boolean, temp: String, accent: String) {
        prefs.edit()
            .putString("widget_theme", theme)
            .putString("widget_dhikr", dhikr)
            .putBoolean("widget_show_temp", showTemp)
            .putString("widget_temp", temp)
            .putString("widget_accent", accent)
            .apply()
        _widgetTheme.value = theme
        _widgetDhikr.value = dhikr
        _widgetShowTemperature.value = showTemp
        _widgetTemperature.value = temp
        _widgetAccentColor.value = accent
    }

    fun setPostPrayerDelaySettings(mode: String, delayMins: Int, time: String) {
        prefs.edit()
            .putString("post_prayer_mode", mode)
            .putInt("post_prayer_delay_mins", delayMins)
            .putString("post_prayer_athkar_time", time)
            .apply()
        _postPrayerReminderMode.value = mode
        _postPrayerDelayMinutes.value = delayMins
        _postPrayerAthkarTime.value = time
    }

    fun setWidgetTransparentBg(transparent: Boolean) {
        prefs.edit().putBoolean("widget_transparent", transparent).apply()
        _widgetTransparentBg.value = transparent
    }

    fun setWidgetCardCorner(corner: Int) {
        prefs.edit().putInt("widget_corner", corner).apply()
        _widgetCardCorner.value = corner
    }

    fun setWidgetShowCountdown(show: Boolean) {
        prefs.edit().putBoolean("widget_countdown", show).apply()
        _widgetShowCountdown.value = show
    }

    fun setAppLanguage(lang: String) {
        prefs.edit().putString("app_language", lang).apply()
        _appLanguage.value = lang
    }

    fun setAppFontFamily(fontFamily: String) {
        prefs.edit().putString("app_font_family", fontFamily).apply()
        _appFontFamily.value = fontFamily
    }

    fun setAppFontScale(scale: Float) {
        prefs.edit().putFloat("app_font_scale", scale).apply()
        _appFontScale.value = scale
    }

    fun setTimeFormat(format: String) {
        prefs.edit().putString("time_format", format).apply()
        _timeFormat.value = format
        try {
            com.example.widget.PrayerAppWidgetProvider.updateAllWidgets(context)
        } catch (_: Exception) {}
    }
}
