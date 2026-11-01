package com.example.ui.screens

import android.widget.Toast
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
    onOpenAdvancedSettings: () -> Unit = {},
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTopTab by remember { mutableStateOf(initialTab) }

    val topTabs = listOf(
        "العامة والطقس والموقع",
        "الحساب والجمعة والتقويم",
        "قسم تذكير الصلاة",
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

                        Button(
                            onClick = { showManualLocationDialog = true },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Filled.EditLocation, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSecondaryContainer)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("تعديل الإحداثيات والاسم يدوياً (Prayers Gadget)", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSecondaryContainer)
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
        // TAB 2: قسم تذكير الصلاة
        // ==========================================
        if (selectedTopTab == 2) {
            // Header Card: Direct Shortcut to Unified Prayer Reminder
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
                            Text(text = "قسم تذكير الصلاة والمواقيت الموحد", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
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
        // TAB 3: تخصيص الأقسام والخلفيات
        // ==========================================
        if (selectedTopTab == 3) {
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
                                Text(text = "تم نقل حذف وتعديل وإضافة الأقسام إلى صانع التطبيق", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
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
}
