package com.example.ui.screens

import android.widget.Toast
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import com.example.widget.PrayerAppWidgetProvider
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.*
import com.example.data.services.CalculationMethod
import com.example.data.services.JuristicMethod
import com.example.data.services.WeatherInfo
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.GoldAccent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    selectedCity: CityLocation,
    calculationMethod: CalculationMethod,
    juristicMethod: JuristicMethod,
    prayerOffsets: Map<PrayerType, Int>,
    weatherInfo: WeatherInfo,
    isDarkMode: Boolean,
    fridayFirstAdhan: Boolean = false,
    fridaySecondAdhan: Boolean = true,
    fridaySoundOnly: Boolean = false,
    salawatIntervalMinutes: Int = 5,
    salawatSoundType: String = "tts_calm",
    prePrayerBigMessage: Boolean = true,
    prePrayerMinutesBefore: Int = 10,
    hijriType: String = "auto",
    hijriAdjustment: Int = 0,
    monthDisplayMode: String = "both",
    utcMode: String = "auto",
    manualUtcHours: Double = 3.0,
    currentBgTheme: String = "emerald_palace",
    sections: List<AppSectionModel> = emptyList(),
    wallpapers: List<WallpaperModel> = emptyList(),
    adhanAudios: List<AdhanAudioModel> = emptyList(),
    tawbahStories: List<TawbahStory> = emptyList(),
    prophetStories: List<ProphetStory> = emptyList(),
    islamicQAs: List<IslamicQA> = emptyList(),
    timeFormat: String = "12h",
    onUpdateTimeFormat: (String) -> Unit = {},
    digitType: String = "western",
    onUpdateDigitType: (String) -> Unit = {},
    ongoingNotification: Boolean = true,
    onToggleOngoingNotification: (Boolean) -> Unit = {},
    onSendCenterNotification: () -> Unit = {},
    onDetectLocationGps: () -> Unit = {},
    isGpsDetecting: Boolean = false,
    initialTab: Int = 0,
    onOpenCitySelector: () -> Unit = {},
    onSaveCityCoords: (Double, Double, String) -> Unit = { _, _, _ -> },
    onSelectCalculationMethod: (CalculationMethod) -> Unit = {},
    onSelectJuristicMethod: (JuristicMethod) -> Unit = {},
    onSetPrayerOffset: (PrayerType, Int) -> Unit = { _, _ -> },
    onRefreshWeather: () -> Unit = {},
    onToggleDarkMode: (Boolean) -> Unit = {},
    onUpdateFridaySettings: (Boolean, Boolean, Boolean) -> Unit = { _, _, _ -> },
    onUpdateSalawatInterval: (Int) -> Unit = {},
    onUpdateSalawatSoundType: (String) -> Unit = {},
    onUpdatePrePrayerSettings: (Boolean, Int) -> Unit = { _, _ -> },
    onUpdateHijriSettings: (String, Int, String) -> Unit = { _, _, _ -> },
    onUpdateUtcSettings: (String, Double) -> Unit = { _, _ -> },
    onSelectBgTheme: (String) -> Unit = {},
    onOpenCreatorStudio: () -> Unit = {},
    onOpenWidgetsStudio: () -> Unit = {},
    onToggleSection: (String, Boolean) -> Unit = { _, _ -> },
    onDeleteSection: (String) -> Unit = {},
    onAddSection: (String, String) -> Unit = { _, _ -> },
    onResetSections: () -> Unit = {},
    onAddWallpaper: (String, String, Long, String?) -> Unit = { _, _, _, _ -> },
    onDeleteWallpaper: (String) -> Unit = {},
    onResetWallpapers: () -> Unit = {},
    onAddSound: (String, String, String, String) -> Unit = { _, _, _, _ -> },
    onDeleteSound: (String) -> Unit = {},
    onResetSounds: () -> Unit = {},
    onAddTawbahStory: (String, String, String, String, String) -> Unit = { _, _, _, _, _ -> },
    onDeleteTawbahStory: (String) -> Unit = {},
    onResetTawbahStories: () -> Unit = {},
    onAddProphetStory: (String, String, String, String, String, String) -> Unit = { _, _, _, _, _, _ -> },
    onDeleteProphetStory: (String) -> Unit = {},
    onResetProphetStories: () -> Unit = {},
    onAddQA: (String, String, String, String) -> Unit = { _, _, _, _ -> },
    onDeleteQA: (String) -> Unit = {},
    onResetQAs: () -> Unit = {},
    onPlayTestAudio: (String, String) -> Unit = { _, _ -> },
    onOpenPrayerAdjustments: () -> Unit = {},
    onOpenAthkarDuas: (String) -> Unit = {},
    googleAccountEmail: String = "bnb40195@gmail.com",
    isGoogleSignedIn: Boolean = true,
    onUpdateGoogleAccount: (String, Boolean) -> Unit = { _, _ -> },
    isDeveloperUnlocked: Boolean = false,
    onUnlockDeveloper: ((String, String) -> Boolean)? = null,
    onLockDeveloper: (() -> Unit)? = null,
    athkarReminderMode: String = "both",
    onUpdateAthkarReminderMode: (String) -> Unit = {},
    postPrayerDelayMinutes: Int = 5,
    onUpdatePostPrayerDelayMinutes: (Int) -> Unit = {},
    quickStartEnabled: Boolean = true,
    onUpdateQuickStartEnabled: (Boolean) -> Unit = {},
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    if (onBack != null) {
        androidx.activity.compose.BackHandler(onBack = onBack)
    }

    val context = LocalContext.current
    var selectedTopTab by remember { mutableStateOf(initialTab) }

    // Dialog states for Google Account & Developer unlock
    var showGoogleAccountDialog by remember { mutableStateOf(false) }
    var showDeveloperUnlockDialog by remember { mutableStateOf(false) }
    var devEmailInput by remember { mutableStateOf("") }
    var devAnswerInput by remember { mutableStateOf("") }
    var devUnlockError by remember { mutableStateOf<String?>(null) }

    val topTabs = listOf(
        "العامة والطقس والموقع",
        "الحساب والجمعة والتقويم",
        "قسم الأذان وتذكير الصلاة",
        "قسم تعديل الويدجات",
        "تخصيص الأقسام والخلفيات"
    )

    // Local form states
    var customMawaqitUrl by remember { mutableStateOf("") }
    var isLiveSyncEnabled by remember { mutableStateOf(true) }

    // Dialog States
    var showManualLocationDialog by remember { mutableStateOf(false) }
    var showAddSectionDialog by remember { mutableStateOf(false) }
    var showAddWallpaperDialog by remember { mutableStateOf(false) }
    var showAddSoundDialog by remember { mutableStateOf(false) }
    var showAddStoryDialog by remember { mutableStateOf(false) }
    var showAddProphetDialog by remember { mutableStateOf(false) }
    var showAddQADialog by remember { mutableStateOf(false) }

    // Search & story subtabs
    var storySearchQuery by remember { mutableStateOf("") }
    var storySubTab by remember { mutableStateOf(0) } // 0: التوبة, 1: الأنبياء, 2: الأسئلة

    LazyColumn(
        modifier = modifier
            .testTag("settings_screen")
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onBack?.invoke() },
                    modifier = Modifier
                        .testTag("settings_back_button")
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "الرجوع",
                        tint = GoldAccent,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "الإعدادات الشاملة • صلاتي",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "تم دمج كافة الإعدادات العامة والمتقدمة في واجهة موحدة واحدة وسهلة الوصول",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Top Categorized Tabs (ScrollableRow)
        item {
            ScrollableTabRow(
                selectedTabIndex = selectedTopTab,
                edgePadding = 0.dp,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
            ) {
                topTabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTopTab == index,
                        onClick = { selectedTopTab = index },
                        text = {
                            Text(
                                text = title,
                                fontSize = 12.sp,
                                fontWeight = if (selectedTopTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }
        }

        // ==========================================
        // TAB 0: العامة والطقس والموقع
        // ==========================================
        if (selectedTopTab == 0) {
            // Live Weather & Temperature Card (as requested like Electronic Muezzin)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("weather_settings_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
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
                                        contentDescription = null,
                                        tint = if (weatherInfo.isDay) GoldAccent else MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "درجة الحرارة",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${selectedCity.nameAr}: ${weatherInfo.temperatureC.toInt()}°م (${weatherInfo.conditionAr})",
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            // Temperature Badge
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = GoldAccent.copy(alpha = 0.2f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.6f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Filled.Thermostat, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(14.dp))
                                    Text("درجة الحرارة", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "حساب محلي حسب مناخ وفصول ${selectedCity.nameAr}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "● مباشر",
                                fontSize = 11.sp,
                                color = EmeraldLight,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "آخر تحديث: ${weatherInfo.lastUpdated}",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // City & Location Card + Manual GPS button
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "المدينة والموقع الجغرافي", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    text = "${selectedCity.nameAr} - ${selectedCity.countryAr}",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "خط العرض: ${String.format("%.4f", selectedCity.latitude)} • خط الطول: ${String.format("%.4f", selectedCity.longitude)}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            OutlinedButton(
                                onClick = onOpenCitySelector,
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("تغيير", fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = onDetectLocationGps,
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier.weight(1f)
                            ) {
                                if (isGpsDetecting) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(Icons.Filled.MyLocation, contentDescription = null, modifier = Modifier.size(16.dp))
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("تحديد تلقائي (GPS)", fontSize = 12.sp)
                            }

                            Button(
                                onClick = { showManualLocationDialog = true },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Filled.EditLocation, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSecondaryContainer)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("تعديل يدوي", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSecondaryContainer)
                            }
                        }
                    }
                }
            }

            // Mawaqit Link & Live Sync option
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "مزامنة موقع مواقيت (Mawaqit)", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                Text(text = "حساب ومزامنة المواقيت عبر الإنترنت عند توفر الشبكة", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = isLiveSyncEnabled,
                                onCheckedChange = { isLiveSyncEnabled = it }
                            )
                        }

                        if (isLiveSyncEnabled) {
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = customMawaqitUrl,
                                onValueChange = { customMawaqitUrl = it },
                                label = { Text("رابط مسجد في موقع مواقيت (اختياري)", fontSize = 12.sp) },
                                placeholder = { Text("https://mawaqit.net/...", fontSize = 11.sp) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                }
            }

            // Dark Theme Switch
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "المظهر الليلي (الزمردي الداكن)", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Text(text = "مظهر مريح للعين بألوان إسلامية فخمة", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = isDarkMode,
                            onCheckedChange = onToggleDarkMode
                        )
                    }
                }
            }

            // Time Format Card (12h vs 24h as requested by user)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("settings_time_format_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Filled.Schedule, contentDescription = null, tint = GoldAccent)
                            Column {
                                Text(
                                    text = "نظام وتنسيق عرض الوقت",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "اختر التوقيت بنظام 12 ساعة أو 24 ساعة لجميع المواقيت والشاشة الرئيسية والويدجت",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val options = listOf(
                                "12h" to "12 ساعة (ص / م)",
                                "24h" to "24 ساعة (عسكري / مستمر)"
                            )
                            options.forEach { (mode, label) ->
                                val isSelected = timeFormat == mode
                                Surface(
                                    onClick = { onUpdateTimeFormat(mode) },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                    border = if (isSelected) BorderStroke(1.dp, GoldAccent) else null,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        RadioButton(
                                            selected = isSelected,
                                            onClick = { onUpdateTimeFormat(mode) },
                                            colors = RadioButtonDefaults.colors(selectedColor = GoldAccent)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = label,
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Digit Format Card (الأرقام العربية 1234 مقابل أرقام جوجل ١٢٣٤)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("settings_digit_format_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Filled.Pin, contentDescription = null, tint = GoldAccent)
                            Column {
                                Text(
                                    text = "نوع وشكل الأرقام",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "اختر بين الأرقام العربية (1234) أو أرقام جوجل المشرقية (١٢٣٤) لجميع الساعات والمواقيت والويدجت",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val digitOptions = listOf(
                                "western" to "العربية (1234)",
                                "eastern" to "أرقام جوجل (١٢٣٤)"
                            )
                            digitOptions.forEach { (type, label) ->
                                val isSelected = digitType == type
                                Surface(
                                    onClick = { onUpdateDigitType(type) },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                    border = if (isSelected) BorderStroke(1.dp, GoldAccent) else null,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        RadioButton(
                                            selected = isSelected,
                                            onClick = { onUpdateDigitType(type) },
                                            colors = RadioButtonDefaults.colors(selectedColor = GoldAccent)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = label,
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Notification Center Ongoing Notification Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("settings_ongoing_notification_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Filled.NotificationsActive, contentDescription = null, tint = GoldAccent)
                            Column {
                                Text(
                                    text = "إشعار الصلاة في مركز الإشعارات",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "إشعار مستمر في شريط الإشعارات والخلفية يعرض الصلاة القادمة والوقت المتبقي ودرجة الحرارة",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Switch(
                            checked = ongoingNotification,
                            onCheckedChange = onToggleOngoingNotification
                        )
                    }

                    if (ongoingNotification) {
                        OutlinedButton(
                            onClick = onSendCenterNotification,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .padding(bottom = 12.dp)
                        ) {
                            Icon(Icons.Filled.NotificationsActive, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("إرسال وتحديث الإشعار في مركز الإشعارات 🔔", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Google Account Registration / Sign-In Card (Requested!)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("settings_google_account_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(GoldAccent.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.AccountCircle, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(24.dp))
                            }
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(text = "تسجيل بريد إلكتروني Google", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                    Surface(shape = RoundedCornerShape(4.dp), color = EmeraldLight.copy(alpha = 0.2f)) {
                                        Text("Google Account", fontSize = 9.sp, color = EmeraldLight, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                    }
                                }
                                Text(
                                    text = if (isDeveloperUnlocked) googleAccountEmail else if (isGoogleSignedIn) "حساب معتمد ومحمي (••••••••@gmail.com)" else "غير مرتبط",
                                    fontSize = 12.sp,
                                    color = GoldAccent,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(text = if (isGoogleSignedIn) "مسجل ومزامن لحفظ الإعدادات السحابية" else "غير متصل", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        OutlinedButton(
                            onClick = { showGoogleAccountDialog = true },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("إدارة الحساب", fontSize = 11.sp)
                        }
                    }
                }
            }

            // Athkar Reminder Mode (Voice, Text, or Both as requested!)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("settings_athkar_mode_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Filled.Campaign, contentDescription = null, tint = GoldAccent)
                            Column {
                                Text(
                                    text = "طريقة تذكير أذكار الصباح والمساء",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "تشغيل التذكير إما عن طريق الصوت أو النص أو كلاهما معاً",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val athkarModes = listOf(
                                "both" to "كلاهما (صوت ونص)",
                                "voice" to "صوت فقط",
                                "text" to "نص فقط"
                            )
                            athkarModes.forEach { (mode, label) ->
                                val isSelected = athkarReminderMode == mode
                                Surface(
                                    onClick = { onUpdateAthkarReminderMode(mode) },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                    border = if (isSelected) BorderStroke(1.dp, GoldAccent) else null,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        RadioButton(
                                            selected = isSelected,
                                            onClick = { onUpdateAthkarReminderMode(mode) },
                                            colors = RadioButtonDefaults.colors(selectedColor = GoldAccent)
                                        )
                                        Text(
                                            text = label,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Post Prayer Athkar Timing Customization (إختياري بالدقائق: 5، 10، 15، إلخ)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("settings_post_prayer_delay_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Filled.Schedule, contentDescription = null, tint = GoldAccent)
                            Column {
                                Text(
                                    text = "تعديل التذكير بأذكار بعد الصلاة (إختياري)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "اختر الوقت المناسب لتنبيه أذكار ما بعد الصلاة (5 دقائق، 10 دقائق، أو وقت مخصص)",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Quick Chips: 5, 10, 15, 20, 30
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val standardOptions = listOf(5, 10, 15, 20, 30)
                            standardOptions.forEach { mins ->
                                val isSelected = postPrayerDelayMinutes == mins
                                Surface(
                                    onClick = { onUpdatePostPrayerDelayMinutes(mins) },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                    border = if (isSelected) BorderStroke(1.dp, GoldAccent) else null,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    ) {
                                        Text(
                                            text = "$mins د",
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "التنبيه ينطلق بعد الصلاة بـ: $postPrayerDelayMinutes دقيقة",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = GoldAccent
                            )
                        }

                        Slider(
                            value = postPrayerDelayMinutes.toFloat(),
                            onValueChange = { onUpdatePostPrayerDelayMinutes(it.toInt().coerceIn(1, 60)) },
                            valueRange = 1f..60f,
                            steps = 59,
                            colors = SliderDefaults.colors(
                                thumbColor = GoldAccent,
                                activeTrackColor = GoldAccent
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Athkar presses / clicks selector (5 ضغط بعد الصلاة أو 10 إلخ)
                        var athkarClicks by remember { mutableStateOf(5) }
                        Text(
                            text = "عدد الضغطات والتسبيحات بعد الصلاة (اختياري): $athkarClicks ضغطات",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(5, 10, 33, 100).forEach { count ->
                                val isSelectedClick = athkarClicks == count
                                Surface(
                                    onClick = { athkarClicks = count },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelectedClick) EmeraldLight.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant,
                                    border = BorderStroke(1.dp, if (isSelectedClick) EmeraldLight else Color.Transparent),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.padding(vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = "$count ضغط",
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelectedClick) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelectedClick) EmeraldLight else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Quick App Start Card (دخول إلى تطبيق سريع)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("settings_quick_start_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Filled.FlashOn, contentDescription = null, tint = GoldAccent)
                            Column {
                                Text(
                                    text = "دخول سريع للتطبيق (Quick Start)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "تشغيل التطبيق فوراً بدون شاشات انتظار والوصول المباشر للمواقيت",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Switch(
                            checked = quickStartEnabled,
                            onCheckedChange = onUpdateQuickStartEnabled
                        )
                    }
                }
            }

            // Creator Studio (Strictly hidden unless unlocked with bnb40195@sat and صلاتي)
            item {
                if (isDeveloperUnlocked) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("settings_creator_studio_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, GoldAccent)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Filled.AdminPanelSettings, contentDescription = null, tint = GoldAccent)
                                Column {
                                    Text(
                                        text = "قسم صانع التطبيق (مفعّل للمطور)",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldAccent
                                    )
                                    Text(
                                        text = "لوحة التحكم بالمحتوى والخلفيات والويدجات والأقسام",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Button(
                                    onClick = onOpenCreatorStudio,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("دخول الاستوديو", fontSize = 11.sp)
                                }
                                OutlinedButton(
                                    onClick = { onLockDeveloper?.invoke() },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Filled.Lock, contentDescription = "قفل", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                } else {
                    // Secret discreet unlock card: Clicking it opens the strict unlock dialog
                    Surface(
                        onClick = {
                            devEmailInput = ""
                            devAnswerInput = ""
                            devUnlockError = null
                            showDeveloperUnlockDialog = true
                        },
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.15f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Filled.Key, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                                Text(
                                    text = "تفعيل وضع صانع التطبيق (أمان المطور)",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Icon(Icons.Filled.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            // About Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "صلاتي • Salati", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "يعمل 100% بدون إنترنت مع تجديد تلقائي للمواقيت والطقس عند توفر شبكة الهاتف أو الواي فاي أو الـ VPN",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }

        // ==========================================
        // TAB 1: الحساب والجمعة والتقويم
        // ==========================================
        if (selectedTopTab == 1) {
            // Calculation Method Selector
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "طريقة حساب مواقيت الصلاة (حسب المناطق والدول)", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Text(text = "حدد الهيئة الإسلامية المعتمدة لحساب زوايا الفجر والعشاء", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(8.dp))

                        CalculationMethod.values().forEach { method ->
                            val isSelected = method == calculationMethod
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSelectCalculationMethod(method) }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { onSelectCalculationMethod(method) },
                                    colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = method.titleAr,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = method.regionAr,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Asr Juristic Method
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "المذهب الفقهي لحساب صلاة العصر", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Text(text = "الجمهور (ظل الشيء مثله) أو الحنفي (ظل الشيء مثليه)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(8.dp))

                        JuristicMethod.values().forEach { juristic ->
                            val isSelected = juristic == juristicMethod
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSelectJuristicMethod(juristic) }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { onSelectJuristicMethod(juristic) },
                                    colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = juristic.titleAr,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            // Friday Adhans
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "إعدادات صلاة الجمعة وأذان الجمعة", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Text(text = "تخصيص الأذان الأول والثاني للجمعة وطريقة التنبيه", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("الأذان الأول للجمعة (قبل دخول الوقت للتذكير)", fontSize = 12.sp)
                            Switch(
                                checked = fridayFirstAdhan,
                                onCheckedChange = { onUpdateFridaySettings(it, fridaySecondAdhan, fridaySoundOnly) }
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("الأذان الثاني للجمعة (عند اعتلاء المنبر)", fontSize = 12.sp)
                            Switch(
                                checked = fridaySecondAdhan,
                                onCheckedChange = { onUpdateFridaySettings(fridayFirstAdhan, it, fridaySoundOnly) }
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("التنبيه بصوت فقط بدون إشعارات منبثقة", fontSize = 12.sp)
                            Switch(
                                checked = fridaySoundOnly,
                                onCheckedChange = { onUpdateFridaySettings(fridayFirstAdhan, fridaySecondAdhan, it) }
                            )
                        }
                    }
                }
            }

            // Hijri Calendar & UTC
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "التقويم الهجري وتوقيت غرينتش (UTC)", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(text = "نوع التقويم الهجري:", fontSize = 12.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 4.dp)) {
                            listOf("auto" to "حسب الدولة", "manual" to "تعديل يدوي").forEach { (type, label) ->
                                val isSelected = hijriType == type
                                Surface(
                                    onClick = { onUpdateHijriSettings(type, hijriAdjustment, monthDisplayMode) },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "تعديل التاريخ الهجري (+/- 2 يوم لرؤية الهلال):", fontSize = 12.sp)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            listOf(-2, -1, 0, 1, 2).forEach { adj ->
                                val isSelected = hijriAdjustment == adj
                                Surface(
                                    onClick = { onUpdateHijriSettings(hijriType, adj, monthDisplayMode) },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Text(
                                        text = if (adj > 0) "+$adj يوم" else if (adj == 0) "الرسمي" else "$adj يوم",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "عرض أسماء الأشهر:", fontSize = 12.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 4.dp)) {
                            listOf("hijri" to "هجري فقط", "gregorian" to "ميلادي فقط", "both" to "كلاهما معاً").forEach { (mode, label) ->
                                val isSelected = monthDisplayMode == mode
                                Surface(
                                    onClick = { onUpdateHijriSettings(hijriType, hijriAdjustment, mode) },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "حساب فارق توقيت غرينتش (UTC) والتوقيت الصيفي:", fontSize = 12.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 4.dp)) {
                            listOf("auto" to "تلقائي من نظام الهاتف", "manual" to "تحديد يدوي").forEach { (mode, label) ->
                                val isSelected = utcMode == mode
                                Surface(
                                    onClick = { onUpdateUtcSettings(mode, manualUtcHours) },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // ==========================================
        // TAB 2: قسم الأذان وتذكير الصلاة
        // ==========================================
        if (selectedTopTab == 2) {
            // Header Card: Direct Shortcut to Unified Adhan and Prayer Reminder
            item {
                Surface(
                    onClick = onOpenPrayerAdjustments,
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.fillMaxWidth().testTag("open_prayer_reminders_from_settings")
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Filled.NotificationsActive, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Column {
                            Text(text = "قسم الأذان وتذكير الصلاة والمواقيت", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Text(text = "تنبيهات ما قبل الصلاة، تذكير الإقامة، وأذكار ما بعد الصلاة والأذان", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            // Pre-Prayer Reminder (إن الصلاة كانت على المؤمنين كتاباً موقوتاً)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "تذكير قبل الصلاة (إن الصلاة كانت على المؤمنين)", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    text = "تنبيه صوتي بالآية الكريمة: \"إن الصلاة كانت على المؤمنين كتاباً موقوتاً - صدق الله العظيم\"",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = prePrayerBigMessage,
                                onCheckedChange = { onUpdatePrePrayerSettings(it, prePrayerMinutesBefore) }
                            )
                        }

                        if (prePrayerBigMessage) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(text = "التنبيه قبل الأذان بـ:", fontSize = 12.sp)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 4.dp)) {
                                listOf(5, 10, 15, 20).forEach { mins ->
                                    val isSelected = prePrayerMinutesBefore == mins
                                    Surface(
                                        onClick = { onUpdatePrePrayerSettings(true, mins) },
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                                    ) {
                                        Text(
                                            text = "$mins دقائق",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Salawat on the Prophet ﷺ (Every X Minutes)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "تذكير الصلاة على النبي ﷺ دورياً", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(text = "تنبيه صوتي عذب بالصلاة والسلام على رسول الله ﷺ في فترات منتظمة", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(text = "تكرار التذكير كل:", fontSize = 12.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(vertical = 4.dp)) {
                            listOf(
                                1 to "دقيقة",
                                5 to "5 دقائق",
                                10 to "10 د",
                                15 to "15 د",
                                30 to "30 د",
                                60 to "ساعة"
                            ).forEach { (mins, label) ->
                                val isSelected = salawatIntervalMinutes == mins
                                Surface(
                                    onClick = { onUpdateSalawatInterval(mins) },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        Divider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.surfaceVariant)

                        Text(text = "صوت التنبيه بالصلاة على النبي ﷺ:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                        Spacer(modifier = Modifier.height(4.dp))

                        val salawatVoices = listOf(
                            Triple("tts_calm", "صوت هادئ خاشع", "اللَّهُمَّ صَلِّ وَسَلِّمْ وَبَارِكْ عَلَى نَبِيِّنَا مُحَمَّدٍ ﷺ"),
                            Triple("tts_reverent", "صوت وقور مبجل مع الآل والصحب", "اللَّهُمَّ صَلِّ وَسَلِّمْ عَلَى سَيِّدِنَا مُحَمَّدٍ وَعَلَى آلِهِ وَصَحْبِهِ أَجْمَعِينَ"),
                            Triple("tts_ibrahimi", "الصيغة الإبراهيمية المباركة", "اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ وَعَلَى آلِ مُحَمَّدٍ كَمَا صَلَّيْتَ عَلَى إِبْرَاهِيمَ"),
                            Triple("voice_haram_makkah", "صوت الحرم المكي الشريف", "الصَّلَاةُ وَالسَّلَامُ عَلَيْكَ يَا رَسُولَ اللهِ بنبرة الحرم المكي"),
                            Triple("voice_madinah_nabawi", "صوت المسجد النبوي الشريف", "يَا طَيِّبَةَ الْخَيْرِ، صَلَوَاتُ رَبِّي وَسَلَامُهُ عَلَى نَبِيِّ الرَّحْمَةِ"),
                            Triple("chime_gentle", "نغمة أثيرية إسلامية هادئة", "رنين نقي متناغم ومريح للقلب"),
                            Triple("chime_madinah", "رنين مقامات روحانية", "نغمات وترية مستوحاة من سكينة المدينة المنورة")
                        )

                        salawatVoices.forEach { (key, title, desc) ->
                            val isSelected = salawatSoundType == key
                            Surface(
                                onClick = { onUpdateSalawatSoundType(key) },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                border = if (isSelected) BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        RadioButton(
                                            selected = isSelected,
                                            onClick = { onUpdateSalawatSoundType(key) },
                                            colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                                        )
                                        Column {
                                            Text(text = title, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                                            Text(text = desc, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // ==========================================
        // TAB 3: قسم تعديل الويدجات (Widget Customization & Unlimited Widgets)
        // ==========================================
        if (selectedTopTab == 3) {
            // Header card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("widget_editor_header_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                    border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(Icons.Filled.Widgets, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(24.dp))
                            Column {
                                Text(
                                    text = "قسم تعديل وتخصيص الويدجات (غير محدود)",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "اختر وثبّت أي عدد تريده من الويدجات مع تخصيص المظهر ودرجة الحرارة العادية للتطبيق",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = onOpenWidgetsStudio,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Filled.Visibility, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("فتح استوديو المعاينة الحية للويدجات", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }

            // Normal temperature notice (حذف المثبتة واعتماد العادية للتطبيق)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Filled.Thermostat, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Text(text = "درجة الحرارة العادية في الويدجات (مثل التطبيق)", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "تم إلغاء درجة الحرارة الثابتة، والويدجات تستخدم الآن درجة الحرارة العادية المباشرة لمدينة ${selectedCity.nameAr}: ${weatherInfo.temperatureC.toInt()}°م (${weatherInfo.conditionAr}).",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Unlimited Widgets Catalog with Direct Pinning
            item {
                Text(
                    text = "كتالوج الويدجات المتاحة للتثبيت المباشر (غير محدود):",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                )
            }

            val availableWidgets = listOf(
                Triple("ويدجت مواقيت الصلاة الكامل", "يعرض المواقيت الستة مع الشروق، ساعة حية، عد تنازلي، والحرارة العادية", Icons.Filled.AccessTime),
                Triple("ويدجت الصلاة القادمة السريع", "تصميم مدمج وأنيق يبرز الصلاة التالية والدقائق المتبقية", Icons.Filled.NotificationsActive),
                Triple("ويدجت حدث في مثل هذا اليوم", "يعرض أهم محطات التاريخ الإسلامي اليومية والتقويم الهجري", Icons.Filled.Event),
                Triple("ويدجت الأذكار والتسبيح اليومي", "يعرض تسابيح مباركة متجددة مع عداد الذكر السريع", Icons.Filled.AutoAwesome),
                Triple("ويدجت منبه السحور والصيام", "موعد الإمساك والإفطار المتبقي وعداد صيام الأيام البيض والإثنين والخميس", Icons.Filled.Bedtime),
                Triple("ويدجت الطقس المباشر ومواقيت اليوم", "درجة الحرارة العادية وحالة الجو لمدينتك مع الصلاة القادمة", Icons.Filled.WbSunny)
            )

            items(availableWidgets) { (wTitle, wDesc, wIcon) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = EmeraldLight.copy(alpha = 0.15f),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(wIcon, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(22.dp))
                                }
                            }
                            Column {
                                Text(text = wTitle, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text(text = wDesc, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        Button(
                            onClick = {
                                val appWidgetManager = AppWidgetManager.getInstance(context)
                                val myProvider = ComponentName(context, PrayerAppWidgetProvider::class.java)
                                if (appWidgetManager.isRequestPinAppWidgetSupported) {
                                    appWidgetManager.requestPinAppWidget(myProvider, null, null)
                                    Toast.makeText(context, "جاري طلب تثبيت $wTitle على شاشة هاتفك", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "يمكنك تثبيت الويدجت بالضغط المطول على شاشة الهاتف الرئيسية", Toast.LENGTH_LONG).show()
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Filled.PushPin, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("تثبيت 📌", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Global Widget Appearance Customization
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "تعديل مظهر وثيمات الويدجات:", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))

                        val widgetThemes = listOf(
                            "forest" to "غابة خضراء",
                            "amoled" to "أسود AMOLED",
                            "emerald" to "زمردي إسلامي"
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            widgetThemes.forEach { (tKey, tLabel) ->
                                OutlinedButton(
                                    onClick = {
                                        val p = context.getSharedPreferences("salati_prefs", android.content.Context.MODE_PRIVATE)
                                        p.edit().putString("widget_theme", tKey).apply()
                                        PrayerAppWidgetProvider.updateAllWidgets(context)
                                        Toast.makeText(context, "تم حفظ ثيم الويدجت: $tLabel", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(tLabel, fontSize = 10.sp, maxLines = 1)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                PrayerAppWidgetProvider.updateAllWidgets(context)
                                Toast.makeText(context, "تم تحديث كافة الويدجات على الهاتف بالحرارة والمواقيت العادية", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Filled.Sync, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("حفظ وتحديث كافة الويدجات على الهاتف الآن", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // ==========================================
        // TAB 4: تخصيص الأقسام والخلفيات
        // ==========================================
        if (selectedTopTab == 4) {
            // Shortcut: Widgets Studio
            item {
                Surface(
                    onClick = onOpenWidgetsStudio,
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("widgets_studio_shortcut")
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Filled.Widgets, contentDescription = null, tint = GoldAccent)
                        Column {
                            Text(
                                text = "استوديو الويدجت (Widgets Studio)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "معاينة وتخصيص وتثبيت ويدجت مواقيت الصلاة على شاشة الهاتف الرئيسية خارج التطبيق",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Background Theme Selection
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "ثيم وخلفية شاشة التطبيق", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(text = "اختر التنسيق الجمالي الإسلامي المفضل لشاشات صلاتي", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(10.dp))

                        listOf(
                            "emerald_palace" to "الزمرد الملكي",
                            "night_sky" to "سماء الليل الهادئة",
                            "kaaba_gold" to "الذهب الكعبوي",
                            "medina_green" to "أخضر المدينة المنورة"
                        ).forEach { (themeId, themeName) ->
                            val isSelected = currentBgTheme == themeId
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSelectBgTheme(themeId) }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { onSelectBgTheme(themeId) },
                                    colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = themeName, fontSize = 13.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                            }
                        }
                    }
                }
            }

            // Sections Header
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "أقسام التطبيق وتفعيلها", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    text = if (isDeveloperUnlocked) "إدارة وتعديل الأقسام متوفرة في صانع التطبيق" else "التحكم في ظهور الأقسام وتفعيلها",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (isDeveloperUnlocked) {
                                Button(
                                    onClick = onOpenCreatorStudio,
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Filled.Construction, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("صانع التطبيق", fontSize = 12.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Sections List (Display & toggle only; edit/delete moved to Creator Studio)
            items(sections, key = { it.id }) { sec ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = sec.titleAr, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(text = sec.subtitleAr, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Switch(
                            checked = sec.isEnabled,
                            onCheckedChange = { onToggleSection(sec.id, it) }
                        )
                    }
                }
            }

            // Wallpapers Management Header
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "إدارة الخلفيات والصور الإسلامية", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(text = "إضافة أو حذف خلفيات مخصصة للواجهات", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            IconButton(onClick = onResetWallpapers) {
                                Icon(Icons.Filled.Refresh, contentDescription = "Reset", tint = MaterialTheme.colorScheme.primary)
                            }
                            Button(
                                onClick = { showAddWallpaperDialog = true },
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("إضافة خلفية", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Wallpapers List
            items(wallpapers, key = { it.id }) { wall ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(wall.colorHex))
                            )
                            Column {
                                Text(text = wall.nameAr, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text(text = wall.descriptionAr, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        if (!wall.isPreset || wall.id.startsWith("custom")) {
                            IconButton(onClick = { onDeleteWallpaper(wall.id) }) {
                                Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    // ==========================================
    // ALL DIALOGS (Preserved & Integrated)
    // ==========================================

    // 1. Add Section Dialog
    if (showAddSectionDialog) {
        var sectionTitle by remember { mutableStateOf("") }
        var sectionSubtitle by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddSectionDialog = false },
            title = { Text("إضافة قسم جديد لتطبيق صلاتي") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = sectionTitle,
                        onValueChange = { sectionTitle = it },
                        label = { Text("عنوان القسم") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = sectionSubtitle,
                        onValueChange = { sectionSubtitle = it },
                        label = { Text("الوصف المختصر للقسم") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (sectionTitle.isNotBlank()) {
                            onAddSection(sectionTitle, sectionSubtitle)
                            showAddSectionDialog = false
                            Toast.makeText(context, "تمت إضافة القسم بنجاح", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("إضافة")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddSectionDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // 2. Add Custom Wallpaper Dialog
    if (showAddWallpaperDialog) {
        var wallName by remember { mutableStateOf("") }
        var wallDesc by remember { mutableStateOf("") }
        var wallUrl by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddWallpaperDialog = false },
            title = { Text("إضافة خلفية إسلامية جديدة") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = wallName,
                        onValueChange = { wallName = it },
                        label = { Text("اسم الخلفية") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = wallDesc,
                        onValueChange = { wallDesc = it },
                        label = { Text("الوصف (مثلاً: نقش أندلسي)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = wallUrl,
                        onValueChange = { wallUrl = it },
                        label = { Text("رابط الصورة أو مسارها (اختياري)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (wallName.isNotBlank()) {
                            onAddWallpaper(wallName, wallDesc, 0xFF1B4D3E, wallUrl.ifBlank { null })
                            showAddWallpaperDialog = false
                            Toast.makeText(context, "تمت إضافة الخلفية بنجاح", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("إضافة")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddWallpaperDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // 3. Add Custom Sound Dialog
    if (showAddSoundDialog) {
        var soundName by remember { mutableStateOf("") }
        var soundDesc by remember { mutableStateOf("") }
        var soundUrl by remember { mutableStateOf("") }
        var soundCategory by remember { mutableStateOf("adhan") }

        AlertDialog(
            onDismissRequest = { showAddSoundDialog = false },
            title = { Text("إضافة صوت أو أذان جديد") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = soundName,
                        onValueChange = { soundName = it },
                        label = { Text("اسم الصوت أو الأذان") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = soundDesc,
                        onValueChange = { soundDesc = it },
                        label = { Text("الوصف أو اسم المؤذن") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = soundUrl,
                        onValueChange = { soundUrl = it },
                        label = { Text("رابط الصوت MP3 أو مسار الملف") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("adhan" to "أذان", "friday" to "جمعة", "reminder" to "تنبيه", "salawat" to "صلاة على النبي").forEach { (cat, label) ->
                            val isSel = soundCategory == cat
                            Surface(
                                onClick = { soundCategory = cat },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (soundName.isNotBlank() && soundUrl.isNotBlank()) {
                            onAddSound(soundName, soundDesc, soundUrl, soundCategory)
                            showAddSoundDialog = false
                            Toast.makeText(context, "تمت إضافة الصوت بنجاح", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("إضافة")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddSoundDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // 4. Add Tawbah Story Dialog
    if (showAddStoryDialog) {
        var storyTitle by remember { mutableStateOf("") }
        var storyPerson by remember { mutableStateOf("") }
        var storyDetails by remember { mutableStateOf("") }
        var storyLesson by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddStoryDialog = false },
            title = { Text("إضافة قصة توبة جديدة") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = storyTitle,
                        onValueChange = { storyTitle = it },
                        label = { Text("عنوان القصة") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = storyPerson,
                        onValueChange = { storyPerson = it },
                        label = { Text("اسم صاحب القصة أو التائب") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = storyDetails,
                        onValueChange = { storyDetails = it },
                        label = { Text("تفاصيل القصة والحدث") },
                        maxLines = 4,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = storyLesson,
                        onValueChange = { storyLesson = it },
                        label = { Text("العبرة والدرس المستفاد") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (storyTitle.isNotBlank() && storyDetails.isNotBlank()) {
                            onAddTawbahStory(storyTitle, storyPerson, storyDetails, "سير أعلام النبلاء والتوابين", storyLesson)
                            showAddStoryDialog = false
                            Toast.makeText(context, "تمت إضافة قصة التوبة بنجاح", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("إضافة")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddStoryDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // 5. Add Prophet Story Dialog
    if (showAddProphetDialog) {
        var prophetName by remember { mutableStateOf("") }
        var prophetTitle by remember { mutableStateOf("") }
        var prophetVerses by remember { mutableStateOf("") }
        var prophetMiracles by remember { mutableStateOf("") }
        var prophetLessons by remember { mutableStateOf("") }
        var prophetStoryText by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddProphetDialog = false },
            title = { Text("إضافة قصة نبي أو رسول") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = prophetName,
                        onValueChange = { prophetName = it },
                        label = { Text("اسم النبي (عليه السلام)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = prophetTitle,
                        onValueChange = { prophetTitle = it },
                        label = { Text("اللقب أو الصفة") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = prophetStoryText,
                        onValueChange = { prophetStoryText = it },
                        label = { Text("السرد الكامل والتفصيلي للقصة") },
                        maxLines = 4,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = prophetMiracles,
                        onValueChange = { prophetMiracles = it },
                        label = { Text("المعجزات والآيات") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (prophetName.isNotBlank() && prophetStoryText.isNotBlank()) {
                            onAddProphetStory(prophetName, prophetTitle, prophetVerses, prophetMiracles, prophetLessons, prophetStoryText)
                            showAddProphetDialog = false
                            Toast.makeText(context, "تمت إضافة قصة النبي بنجاح", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("إضافة")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddProphetDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // 6. Add QA Dialog
    if (showAddQADialog) {
        var qaQuestion by remember { mutableStateOf("") }
        var qaAnswer by remember { mutableStateOf("") }
        var qaCategory by remember { mutableStateOf("فقه وعقيدة") }
        var qaEvidence by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddQADialog = false },
            title = { Text("إضافة سؤال وجواب إسلامي") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = qaQuestion,
                        onValueChange = { qaQuestion = it },
                        label = { Text("نص السؤال") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = qaAnswer,
                        onValueChange = { qaAnswer = it },
                        label = { Text("الإجابة الشرعية الوافية") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = qaEvidence,
                        onValueChange = { qaEvidence = it },
                        label = { Text("الدليل من القرآن أو السنة") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (qaQuestion.isNotBlank() && qaAnswer.isNotBlank()) {
                            onAddQA(qaQuestion, qaAnswer, qaCategory, qaEvidence)
                            showAddQADialog = false
                            Toast.makeText(context, "تمت إضافة السؤال والجواب بنجاح", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("إضافة")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddQADialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // 7. Manual Location Dialog
    if (showManualLocationDialog) {
        var latText by remember { mutableStateOf(selectedCity.latitude.toString()) }
        var lngText by remember { mutableStateOf(selectedCity.longitude.toString()) }
        var cityNameText by remember { mutableStateOf(selectedCity.nameAr) }

        AlertDialog(
            onDismissRequest = { showManualLocationDialog = false },
            title = { Text("تعديل الإحداثيات يدوياً (Prayers Gadget)") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = cityNameText,
                        onValueChange = { cityNameText = it },
                        label = { Text("اسم المدينة") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = latText,
                        onValueChange = { latText = it },
                        label = { Text("خط العرض (Latitude)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = lngText,
                        onValueChange = { lngText = it },
                        label = { Text("خط الطول (Longitude)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val lat = latText.toDoubleOrNull() ?: selectedCity.latitude
                        val lng = lngText.toDoubleOrNull() ?: selectedCity.longitude
                        onSaveCityCoords(lat, lng, cityNameText)
                        showManualLocationDialog = false
                    }
                ) {
                    Text("حفظ")
                }
            },
            dismissButton = {
                TextButton(onClick = { showManualLocationDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // 8. Strict Developer Unlock Dialog (bnb40195@sat + صلاتي)
    if (showDeveloperUnlockDialog) {
        AlertDialog(
            onDismissRequest = { showDeveloperUnlockDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Filled.AdminPanelSettings,
                    contentDescription = null,
                    tint = GoldAccent,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = "التحقق من هوية صانع التطبيق",
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
                        text = "قسم صانع التطبيق محمي بالكامل ومخفي. للوصول، يرجى إدخال البريد الإلكتروني المعتمد والإجابة الصحيحة عن السؤال الأمني:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 17.sp
                    )

                    OutlinedTextField(
                        value = devEmailInput,
                        onValueChange = {
                            devEmailInput = it
                            devUnlockError = null
                        },
                        label = { Text("البريد الإلكتروني المعتمد") },
                        placeholder = { Text("أدخل البريد الإلكتروني") },
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null, tint = GoldAccent) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dev_unlock_email_field")
                    )

                    OutlinedTextField(
                        value = devAnswerInput,
                        onValueChange = {
                            devAnswerInput = it
                            devUnlockError = null
                        },
                        label = { Text("السؤال الأمني: ما هو اسم التطبيق؟") },
                        placeholder = { Text("أدخل الجواب الأمني") },
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Filled.Key, contentDescription = null, tint = GoldAccent) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dev_unlock_answer_field")
                    )

                    if (devUnlockError != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.errorContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = devUnlockError ?: "",
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val trimmedEmail = devEmailInput.trim()
                        val trimmedAnswer = devAnswerInput.trim()
                        val success = onUnlockDeveloper?.invoke(trimmedEmail, trimmedAnswer) ?: false
                        if (success) {
                            showDeveloperUnlockDialog = false
                            Toast.makeText(context, "تم التحقق بنجاح! تم تفعيل قسم صانع التطبيق.", Toast.LENGTH_LONG).show()
                        } else {
                            devUnlockError = "البيانات المدخلة غير صحيحة! لا يمكن تفعيل قسم صانع التطبيق."
                        }
                    },
                    modifier = Modifier.testTag("dev_unlock_confirm_button")
                ) {
                    Text("تحقق وتفعيل")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeveloperUnlockDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // 9. Google Account Management Dialog
    if (showGoogleAccountDialog) {
        var emailInput by remember { mutableStateOf(if (isDeveloperUnlocked) googleAccountEmail else "") }

        AlertDialog(
            onDismissRequest = { showGoogleAccountDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = null,
                    tint = GoldAccent,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = "حساب Google المرتبط",
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
                        text = "يمكنك تعديل البريد الإلكتروني لحساب Google المستخدم في المزامنة السحابية الاحتياطية وإعدادات صلاتي.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = { emailInput = it },
                        label = { Text("بريد Google الإلكتروني") },
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null, tint = GoldAccent) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("google_account_email_field")
                    )
                }
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            if (emailInput.isNotBlank()) {
                                onUpdateGoogleAccount(emailInput.trim(), true)
                                showGoogleAccountDialog = false
                                Toast.makeText(context, "تم حفظ حساب Google بنجاح", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Text("حفظ")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showGoogleAccountDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}
