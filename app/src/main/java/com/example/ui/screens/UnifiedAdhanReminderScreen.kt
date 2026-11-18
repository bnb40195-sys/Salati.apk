package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import kotlinx.coroutines.launch
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.AdhanAudioModel
import com.example.data.models.DayPrayerTimes
import com.example.data.models.PrayerType
import com.example.data.services.PlaybackState
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.GoldAccent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnifiedAdhanReminderScreen(
    prayerTimes: DayPrayerTimes?,
    playbackState: PlaybackState,
    selectedMuadhinId: String,
    adhanAudiosList: List<AdhanAudioModel>,
    onSelectMuadhin: (String) -> Unit,
    onTestAudio: (String, String) -> Unit,
    onTogglePlayPause: () -> Unit,
    onStopAudio: () -> Unit,
    fridayFirstAdhan: Boolean,
    fridaySecondAdhan: Boolean,
    fridaySoundOnly: Boolean,
    onUpdateFridaySettings: (Boolean, Boolean, Boolean) -> Unit,
    fridayFirstAdhanSoundId: String = "friday_1",
    fridaySecondAdhanSoundId: String = "friday_2",
    onUpdateFridayFirstSound: (String) -> Unit = {},
    onUpdateFridaySecondSound: (String) -> Unit = {},
    prePrayerBigMessage: Boolean,
    prePrayerMinutesBefore: Int,
    prePrayerSoundType: String,
    onUpdatePrePrayerSettings: (Boolean, Int, String) -> Unit,
    iqamaReminderEnabled: Boolean,
    iqamaMinutesAfter: Int,
    iqamaSoundType: String,
    onUpdateIqamaSettings: (Boolean, Int, String) -> Unit,
    morningAthkarReminder: Boolean,
    morningAthkarTime: String,
    onUpdateMorningAthkar: (Boolean, String) -> Unit,
    morningAthkarSoundId: String = "athkar_morning_voice",
    onUpdateMorningAthkarSound: (String) -> Unit = {},
    eveningAthkarReminder: Boolean,
    eveningAthkarTime: String,
    onUpdateEveningAthkar: (Boolean, String) -> Unit,
    eveningAthkarSoundId: String = "athkar_evening_voice",
    onUpdateEveningAthkarSound: (String) -> Unit = {},
    postPrayerAthkarReminder: Boolean = true,
    postPrayerAthkarTime: String = "05:00",
    onUpdatePostPrayerAthkar: (Boolean, String) -> Unit = { _, _ -> },
    postPrayerAthkarSoundId: String = "athkar_morning_voice",
    onUpdatePostPrayerAthkarSound: (String) -> Unit = {},
    salawatIntervalMinutes: Int,
    salawatSoundType: String,
    onUpdateSalawatSettings: (Int, String) -> Unit,
    onPlaySalawatTest: (String) -> Unit = {},
    fridayFirstAdhanMinutesBefore: Int = 20,
    fridaySecondAdhanMinutes: Int = 0,
    onUpdateFridayMinutes: (Int, Int) -> Unit = { _, _ -> },
    autoGpsLocation: Boolean = true,
    onToggleAutoGpsLocation: (Boolean) -> Unit = {},
    salawatCustomText: String = "اللهم صل وسلم على نبينا محمد ﷺ",
    onUpdateSalawatCustomText: (String) -> Unit = {},
    morningAthkarCustomText: String = "أصبحنا وأصبح الملك لله والحمد لله ولا إله إلا الله وحده لا شريك له",
    onUpdateMorningAthkarCustomText: (String) -> Unit = {},
    eveningAthkarCustomText: String = "أمسينا وأمسى الملك لله والحمد لله ولا إله إلا الله وحده لا شريك له",
    onUpdateEveningAthkarCustomText: (String) -> Unit = {},
    postPrayerCustomText: String = "أستغفر الله (٣ مرات)، اللهم أنت السلام ومنك السلام",
    onUpdatePostPrayerCustomText: (String) -> Unit = {},
    prePrayerDuaCustomText: String = "اللهم إني أسألك الهدى والتقى والعفاف والغنى",
    onUpdatePrePrayerDuaCustomText: (String) -> Unit = {},
    fajrAdhanSound: String = "makkah",
    dhuhrAdhanSound: String = "makkah",
    asrAdhanSound: String = "makkah",
    maghribAdhanSound: String = "makkah",
    ishaAdhanSound: String = "makkah",
    onUpdatePrayerAdhanSound: (PrayerType, String) -> Unit = { _, _ -> },
    morningAthkarSoundEnabled: Boolean = true,
    onToggleMorningAthkarSound: (Boolean) -> Unit = {},
    eveningAthkarSoundEnabled: Boolean = true,
    onToggleEveningAthkarSound: (Boolean) -> Unit = {},
    prayerOffsets: Map<PrayerType, Int>,
    onSetOffset: (PrayerType, Int) -> Unit,
    onResetOffsets: () -> Unit,
    onAddSound: (String, String, String, String) -> Unit,
    onDeleteSound: (String) -> Unit = {},
    onOpenCreatorStudio: () -> Unit = {},
    isDeveloperUnlocked: Boolean = false,
    onSendCenterNotification: () -> Unit = {},
    onOpenDrawer: () -> Unit = {},
    postPrayerDelayMinutes: Int = 5,
    onUpdatePostPrayerDelayMinutes: (Int) -> Unit = {},
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }

    // Dialog for picking prayer adhan sound
    var showPrayerSoundDialog by remember { mutableStateOf(false) }
    var selectedPrayerForSoundDialog by remember { mutableStateOf(PrayerType.FAJR) }

    // Dialog for adding custom sound
    var showAddSoundDialog by remember { mutableStateOf(false) }
    var newSoundName by remember { mutableStateOf("") }
    var newSoundDesc by remember { mutableStateOf("") }
    var newSoundUrl by remember { mutableStateOf("") }
    var newSoundCategory by remember { mutableStateOf("reminder") }

    // Dialogs for picking Friday Adhan sounds
    var showFridayFirstSoundPicker by remember { mutableStateOf(false) }
    var showFridaySecondSoundPicker by remember { mutableStateOf(false) }

    // Dialogs for picking Morning, Evening, and Pre-Prayer reminder sounds
    var showMorningSoundPicker by remember { mutableStateOf(false) }
    var showEveningSoundPicker by remember { mutableStateOf(false) }
    var showPostPrayerSoundPicker by remember { mutableStateOf(false) }
    var showPrePrayerSoundPicker by remember { mutableStateOf(false) }

    // Dialog for picking custom morning/evening/post-prayer athkar time
    var showMorningTimeDialog by remember { mutableStateOf(false) }
    var morningHourInput by remember { mutableStateOf(morningAthkarTime.split(":").getOrNull(0) ?: "06") }
    var morningMinInput by remember { mutableStateOf(morningAthkarTime.split(":").getOrNull(1) ?: "00") }

    var showEveningTimeDialog by remember { mutableStateOf(false) }
    var eveningHourInput by remember { mutableStateOf(eveningAthkarTime.split(":").getOrNull(0) ?: "15") }
    var eveningMinInput by remember { mutableStateOf(eveningAthkarTime.split(":").getOrNull(1) ?: "00") }

    var showPostPrayerTimeDialog by remember { mutableStateOf(false) }
    var postPrayerHourInput by remember { mutableStateOf(postPrayerAthkarTime.split(":").getOrNull(0) ?: "05") }
    var postPrayerMinInput by remember { mutableStateOf(postPrayerAthkarTime.split(":").getOrNull(1) ?: "00") }

    var showFullAthkarDialog by remember { mutableStateOf(false) }

    // Dialogs for picking Dua & Iqama sounds per prayer
    var showDuaSoundPicker by remember { mutableStateOf(false) }
    var showIqamaSoundPicker by remember { mutableStateOf(false) }
    var selectedPrayerForSoundModal by remember { mutableStateOf("الفجر") }
    var activeHorizontalPrayerIndex by remember { mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()
    val horizontalPrayerListState = rememberLazyListState()

    var localFridayFirstMins by remember(fridayFirstAdhanMinutesBefore) { mutableStateOf(fridayFirstAdhanMinutesBefore) }
    var localFridaySecondMins by remember(fridaySecondAdhanMinutes) { mutableStateOf(fridaySecondAdhanMinutes) }

    // Text customization dialog states
    var showDuaTextDialog by remember { mutableStateOf(false) }
    var currentPrayerForDuaText by remember { mutableStateOf("الفجر") }
    var customDuaInput by remember { mutableStateOf("") }

    var showSalawatTextDialog by remember { mutableStateOf(false) }
    var salawatTextInput by remember { mutableStateOf(salawatCustomText) }

    var showMorningTextDialog by remember { mutableStateOf(false) }
    var morningTextInput by remember { mutableStateOf(morningAthkarCustomText) }

    var showEveningTextDialog by remember { mutableStateOf(false) }
    var eveningTextInput by remember { mutableStateOf(eveningAthkarCustomText) }

    var showPostPrayerTextDialog by remember { mutableStateOf(false) }
    var postPrayerTextInput by remember { mutableStateOf(postPrayerCustomText) }

    // Per-prayer timing configurations (الفجر 5د، الظهر 10د، الإقامة، وتذكير قبل الصلاة)
    var prayerScheduleMap by remember {
        mutableStateOf(
            mapOf(
                "الفجر" to PrayerScheduleItem("الفجر", preMinutes = 10, preEnabled = true, iqamaMinutes = 20, iqamaEnabled = true, postAthkarMinutes = 5, postAthkarEnabled = true, duaSoundId = "dua_pre_athan", iqamaSoundId = "iqama_makkah"),
                "الظهر" to PrayerScheduleItem("الظهر", preMinutes = 5, preEnabled = true, iqamaMinutes = 15, iqamaEnabled = true, postAthkarMinutes = 10, postAthkarEnabled = true, duaSoundId = "pre_prayer_ayah", iqamaSoundId = "iqama_takbeer"),
                "العصر" to PrayerScheduleItem("العصر", preMinutes = 5, preEnabled = true, iqamaMinutes = 15, iqamaEnabled = true, postAthkarMinutes = 10, postAthkarEnabled = true, duaSoundId = "pre_prayer_ayah", iqamaSoundId = "iqama_takbeer"),
                "المغرب" to PrayerScheduleItem("المغرب", preMinutes = 5, preEnabled = true, iqamaMinutes = 10, iqamaEnabled = true, postAthkarMinutes = 5, postAthkarEnabled = true, duaSoundId = "dua_waseela", iqamaSoundId = "iqama_calm"),
                "العشاء" to PrayerScheduleItem("العشاء", preMinutes = 5, preEnabled = true, iqamaMinutes = 15, iqamaEnabled = true, postAthkarMinutes = 10, postAthkarEnabled = true, duaSoundId = "dua_tasbeeh", iqamaSoundId = "iqama_madinah")
            )
        )
    }

    androidx.activity.compose.BackHandler(onBack = onBack)

    Column(
        modifier = modifier
            .testTag("unified_adhan_reminder_screen")
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 3.dp
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Filled.Menu, contentDescription = "القائمة الجانبية", tint = GoldAccent)
                    }

                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "الرجوع", tint = MaterialTheme.colorScheme.onSurface)
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "الأذان وتذكير الصلاة",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.End
                        )
                        Text(
                            text = "أصوات المؤذنين وتذكيرات الإقامة والصلوات والأذكار",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.End
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Unified Scrollable Tab Row
                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    edgePadding = 0.dp,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    contentColor = GoldAccent,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("الأذان والجمعة", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal, fontSize = 12.sp) },
                        icon = { Icon(Icons.Filled.VolumeUp, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("تذكير بالأوقات الأخرى", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal, fontSize = 12.sp) },
                        icon = { Icon(Icons.Filled.Alarm, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("دقائق المواقيت", fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal, fontSize = 12.sp) },
                        icon = { Icon(Icons.Filled.Tune, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                }
            }
        }

        // TAB 0: الأذان والمؤذنون
        if (selectedTab == 0) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Playback Banner if playing
                if (playbackState.isPlaying) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = EmeraldLight.copy(alpha = 0.2f)),
                            border = BorderStroke(1.dp, EmeraldLight)
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
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(Icons.Filled.GraphicEq, contentDescription = null, tint = EmeraldLight)
                                    Column {
                                        Text(text = playbackState.currentTitle.ifEmpty { "صوت الأذان يعمل الآن" }, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Text(text = "صوت ندي خاشع", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                                IconButton(onClick = onStopAudio) {
                                    Icon(Icons.Filled.Stop, contentDescription = "إيقاف", tint = Color.Red)
                                }
                            }
                        }
                    }
                }

                // Add sound button
                item {
                    Button(
                        onClick = {
                            newSoundCategory = "adhan"
                            showAddSoundDialog = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("إضافة صوت أذان جديد", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Auto GPS Location Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = EmeraldLight.copy(alpha = 0.15f),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Filled.LocationOn, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(20.dp))
                                    }
                                }
                                Column {
                                    Text(
                                        text = "تشغيل الموقع الجغرافي تلقائياً (GPS)",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = if (autoGpsLocation) "مفعّل: تحديث المواقيت والقبلة تلقائياً عبر GPS" else "متوقف: استخدام المدينة المحددة يدوياً",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Switch(
                                checked = autoGpsLocation,
                                onCheckedChange = onToggleAutoGpsLocation
                            )
                        }
                    }
                }

                // Per-prayer Adhan Sound Customization (الفجر، الظهر، العصر، المغرب، العشاء)
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "تخصيص صوت الأذان لكل صلاة (الفجر، الظهر، العصر، المغرب، العشاء) 🕌",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldAccent
                                    )
                                    Text(
                                        text = "إمكانية تعيين مؤذن وصوت مختلف لكل صلاة مع الاستماع المباشر",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Icon(Icons.Filled.Tune, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(20.dp))
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

                            val prayersList = listOf(
                                Triple(PrayerType.FAJR, "أذان الفجر 🌅", fajrAdhanSound),
                                Triple(PrayerType.DHUHR, "أذان الظهر ☀️", dhuhrAdhanSound),
                                Triple(PrayerType.ASR, "أذان العصر 🌤️", asrAdhanSound),
                                Triple(PrayerType.MAGHRIB, "أذان المغرب 🌇", maghribAdhanSound),
                                Triple(PrayerType.ISHA, "أذان العشاء 🌙", ishaAdhanSound)
                            )

                            prayersList.forEach { (pType, pName, soundId) ->
                                val curAudio = adhanAudiosList.find { it.id == soundId }
                                    ?: adhanAudiosList.find { it.id == selectedMuadhinId }
                                    ?: adhanAudiosList.firstOrNull()

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(text = pName, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                            Text(
                                                text = "المؤذن: ${curAudio?.nameAr ?: "أذان الحرم المكي"}",
                                                fontSize = 11.sp,
                                                color = GoldAccent
                                            )
                                        }
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            if (curAudio != null) {
                                                IconButton(
                                                    onClick = { onTestAudio(curAudio.audioUrl, "${pName} - ${curAudio.nameAr}") },
                                                    modifier = Modifier.size(32.dp)
                                                ) {
                                                    val isThisPlaying = playbackState.isPlaying && playbackState.currentTitle.contains(pName)
                                                    Icon(
                                                        imageVector = if (isThisPlaying) Icons.Filled.Stop else Icons.Filled.PlayArrow,
                                                        contentDescription = "استماع",
                                                        tint = GoldAccent
                                                    )
                                                }
                                            }
                                            FilledTonalButton(
                                                onClick = {
                                                    selectedPrayerForSoundDialog = pType
                                                    showPrayerSoundDialog = true
                                                },
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                            ) {
                                                Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(12.dp))
                                                Spacer(modifier = Modifier.width(3.dp))
                                                Text("تعديل الصوت 🎵", fontSize = 11.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Adhan Audios List
                item {
                    Text(
                        text = "المؤذن الافتراضي لجميع الصلوات:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                items(adhanAudiosList.filter { it.category == "adhan" || it.category == "friday" }) { audio ->
                    val isSelected = audio.id == selectedMuadhinId
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { onSelectMuadhin(audio.id) },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                        ),
                        border = if (isSelected) BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { onSelectMuadhin(audio.id) },
                                    colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                                )
                                Column {
                                    Text(
                                        text = audio.nameAr,
                                        fontSize = 14.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = audio.descriptionAr,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { onTestAudio(audio.audioUrl, audio.nameAr) }
                                ) {
                                    Icon(
                                        imageVector = if (playbackState.isPlaying && playbackState.currentTitle == audio.nameAr) Icons.Filled.Stop else Icons.Filled.PlayArrow,
                                        contentDescription = "استماع",
                                        tint = GoldAccent
                                    )
                                }
                            }
                        }
                    }
                }

                // Friday Adhans with sound and timing selectors
                item {
                    val firstAdhanAudio = adhanAudiosList.find { it.id == fridayFirstAdhanSoundId }
                        ?: adhanAudiosList.find { it.category == "friday" }
                        ?: adhanAudiosList.firstOrNull()
                    val secondAdhanAudio = adhanAudiosList.find { it.id == fridaySecondAdhanSoundId }
                        ?: adhanAudiosList.find { it.category == "adhan" }
                        ?: adhanAudiosList.firstOrNull()

                    Spacer(modifier = Modifier.height(6.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = "إعدادات أذان يوم الجمعة المبارك", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                                    Text(text = "تخصيص الوقت وتغيير أصوات الأذان الأول والثاني", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Icon(Icons.Filled.Mosque, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(22.dp))
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                            Spacer(modifier = Modifier.height(10.dp))

                            // Friday First Adhan
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("الأذان الأول ليوم الجمعة (تنبيه السعي)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text("يُرفع قبل صلاة الجمعة بـ $localFridayFirstMins دقيقة لتنبيه المصلين", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Switch(
                                    checked = fridayFirstAdhan,
                                    onCheckedChange = { onUpdateFridaySettings(it, fridaySecondAdhan, fridaySoundOnly) }
                                )
                            }

                            if (fridayFirstAdhan) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "تعديل وقت الأذان الأول (قبل صلاة الجمعة):",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    listOf(15 to "15د", 20 to "20د", 30 to "30د", 45 to "45د", 60 to "ساعة").forEach { (mins, lbl) ->
                                        val isSelected = localFridayFirstMins == mins
                                        Surface(
                                            onClick = {
                                                localFridayFirstMins = mins
                                                onUpdateFridayMinutes(mins, localFridaySecondMins)
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (isSelected) GoldAccent else MaterialTheme.colorScheme.surfaceVariant,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                text = lbl,
                                                fontSize = 10.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.padding(vertical = 6.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(text = "صوت الأذان الأول المعتمد:", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                                            Text(
                                                text = firstAdhanAudio?.nameAr ?: "أذان الجمعة الأول",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            if (firstAdhanAudio != null) {
                                                IconButton(
                                                    onClick = { onTestAudio(firstAdhanAudio.audioUrl, firstAdhanAudio.nameAr) },
                                                    modifier = Modifier.size(32.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = if (playbackState.isPlaying && playbackState.currentTitle == firstAdhanAudio.nameAr) Icons.Filled.Stop else Icons.Filled.VolumeUp,
                                                        contentDescription = "استماع",
                                                        tint = GoldAccent,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }
                                            }
                                            FilledTonalButton(
                                                onClick = { showFridayFirstSoundPicker = true },
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("تعديل الصوت", fontSize = 11.sp)
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                            Spacer(modifier = Modifier.height(10.dp))

                            // Friday Second Adhan
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("الأذان الثاني ليوم الجمعة (صعود المنبر)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text(
                                        text = if (localFridaySecondMins == 0) "يُرفع مباشرة عند صعود الإمام إلى المنبر" else "يُرفع بعد الأذان الأول بـ $localFridaySecondMins دقيقة",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Switch(
                                    checked = fridaySecondAdhan,
                                    onCheckedChange = { onUpdateFridaySettings(fridayFirstAdhan, it, fridaySoundOnly) }
                                )
                            }

                            if (fridaySecondAdhan) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "تعديل وقت موعد الأذان الثاني:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    listOf(0 to "عند المنبر", 10 to "بعد 10د", 15 to "بعد 15د", 20 to "بعد 20د", 30 to "بعد 30د").forEach { (mins, lbl) ->
                                        val isSelected = localFridaySecondMins == mins
                                        Surface(
                                            onClick = {
                                                localFridaySecondMins = mins
                                                onUpdateFridayMinutes(localFridayFirstMins, mins)
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (isSelected) EmeraldLight else MaterialTheme.colorScheme.surfaceVariant,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                text = lbl,
                                                fontSize = 10.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.padding(vertical = 6.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = EmeraldLight.copy(alpha = 0.15f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(text = "صوت الأذان الثاني المعتمد:", fontSize = 11.sp, color = EmeraldLight)
                                            Text(
                                                text = secondAdhanAudio?.nameAr ?: "أذان الحرم المكي الشريف",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            if (secondAdhanAudio != null) {
                                                IconButton(
                                                    onClick = { onTestAudio(secondAdhanAudio.audioUrl, secondAdhanAudio.nameAr) },
                                                    modifier = Modifier.size(32.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = if (playbackState.isPlaying && playbackState.currentTitle == secondAdhanAudio.nameAr) Icons.Filled.Stop else Icons.Filled.VolumeUp,
                                                        contentDescription = "استماع",
                                                        tint = GoldAccent,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }
                                            }
                                            FilledTonalButton(
                                                onClick = { showFridaySecondSoundPicker = true },
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("تعديل الصوت", fontSize = 11.sp)
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

        // ==========================================
        // TAB 1: تذكير بالأوقات الأخرى
        // ==========================================
        if (selectedTab == 1) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header Banner
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                        border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = GoldAccent.copy(alpha = 0.2f),
                                modifier = Modifier.size(42.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Filled.Alarm, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(24.dp))
                                }
                            }
                            Column {
                                Text(
                                    text = "تذكير بالأوقات الأخرى",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "تخصيص كامل للأوقات، النصوص، والأصوات لكل تنبيه وتذكير",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // 1. تذكير الصلاة على النبي ﷺ دورياً (كل 5 إلى 30 دقيقة + تعديل النص + تعديل الصوت)
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Filled.Favorite, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(22.dp))
                                    Column {
                                        Text(
                                            text = "تذكير الصلاة على النبي ﷺ",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = EmeraldLight
                                        )
                                        Text(
                                            text = "تنبيه دوري مع إمكانية تعديل النص والصوت والوقت",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                FilledTonalButton(
                                    onClick = { onPlaySalawatTest(salawatSoundType) },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(Icons.Filled.VolumeUp, contentDescription = null, modifier = Modifier.size(16.dp), tint = EmeraldLight)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("تجربة الصوت", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            // Interval chips (كل 5 دقائق حتى 30 دقيقة)
                            Text(text = "تكرار التنبيه كل (من 5 إلى 30 دقيقة):", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                listOf(5, 10, 15, 20, 25, 30).forEach { mins ->
                                    val isSelected = salawatIntervalMinutes == mins
                                    Surface(
                                        onClick = { onUpdateSalawatSettings(mins, salawatSoundType) },
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSelected) EmeraldLight else MaterialTheme.colorScheme.surfaceVariant,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = "$mins د",
                                            fontSize = 10.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(vertical = 6.dp)
                                        )
                                    }
                                }
                            }

                            // Custom Text Card with Edit Button
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = EmeraldLight.copy(alpha = 0.1f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = "النص المقروء للتذكير:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EmeraldLight)
                                        OutlinedButton(
                                            onClick = {
                                                salawatTextInput = salawatCustomText
                                                showSalawatTextDialog = true
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(13.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("تعديل النص ✍️", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    Text(
                                        text = salawatCustomText,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            // Sound Selection
                            Text(text = "اختر صوت التنبيه المعتمد:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            val salawatVoiceOptions = listOf(
                                Triple("tts_calm", "صوت هادئ خاشع", "اللَّهُمَّ صَلِّ وَسَلِّمْ عَلَى نَبِيِّنَا مُحَمَّدٍ ﷺ"),
                                Triple("tts_reverent", "صوت وقور مبجل مع الآل والصحب", "اللَّهُمَّ صَلِّ وَسَلِّمْ عَلَى سَيِّدِنَا مُحَمَّدٍ وَآلِهِ"),
                                Triple("voice_haram_makkah", "صوت الحرم المكي الشريف", "الصَّلَاةُ وَالسَّلَامُ عَلَيْكَ يَا رَسُولَ اللهِ"),
                                Triple("chime_gentle", "رنين هادئ", "نغمة أثيرية إسلامية مريحة للقلب")
                            )

                            salawatVoiceOptions.forEach { (soundKey, soundTitle, soundDesc) ->
                                val isSelected = salawatSoundType == soundKey
                                Surface(
                                    onClick = { onUpdateSalawatSettings(salawatIntervalMinutes, soundKey) },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) EmeraldLight.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                    border = if (isSelected) BorderStroke(1.dp, EmeraldLight) else null,
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            RadioButton(
                                                selected = isSelected,
                                                onClick = { onUpdateSalawatSettings(salawatIntervalMinutes, soundKey) },
                                                colors = RadioButtonDefaults.colors(selectedColor = EmeraldLight)
                                            )
                                            Column {
                                                Text(text = soundTitle, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                                                Text(text = soundDesc, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                        }
                                        IconButton(onClick = { onPlaySalawatTest(soundKey) }, modifier = Modifier.size(28.dp)) {
                                            Icon(Icons.Filled.VolumeUp, contentDescription = "استماع", tint = EmeraldLight, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 2. تذكير بعد الأذان (الإقامة) - حسب كل صلاة وتعديل الوقت والصوت
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "تذكير بعد الأذان (الإقامة)",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "تنبيه بعد الأذان بـ $iqamaMinutesAfter دقيقة",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Switch(
                                    checked = iqamaReminderEnabled,
                                    onCheckedChange = { onUpdateIqamaSettings(it, iqamaMinutesAfter, iqamaSoundType) }
                                )
                            }

                            if (iqamaReminderEnabled) {
                                Text(text = "تعديل وقت الإقامة بعد الأذان (من 5 إلى 30 دقيقة):", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    listOf(5, 10, 15, 20, 25, 30).forEach { mins ->
                                        val isSelected = iqamaMinutesAfter == mins
                                        Surface(
                                            onClick = { onUpdateIqamaSettings(true, mins, iqamaSoundType) },
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                text = "$mins د",
                                                fontSize = 10.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.padding(vertical = 6.dp)
                                            )
                                        }
                                    }
                                }

                                // Sound Picker button
                                val currentIqamaAudio = adhanAudiosList.find { it.id == iqamaSoundType }
                                    ?: adhanAudiosList.find { it.category == "iqama" }
                                    ?: adhanAudiosList.firstOrNull()

                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(text = "صوت الإقامة المعتمد:", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                                            Text(text = currentIqamaAudio?.nameAr ?: "صوت الإقامة", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            if (currentIqamaAudio != null) {
                                                IconButton(
                                                    onClick = { onTestAudio(currentIqamaAudio.audioUrl, currentIqamaAudio.nameAr) },
                                                    modifier = Modifier.size(32.dp)
                                                ) {
                                                    Icon(Icons.Filled.VolumeUp, contentDescription = "استماع", tint = MaterialTheme.colorScheme.primary)
                                                }
                                            }
                                            FilledTonalButton(
                                                onClick = {
                                                    selectedPrayerForSoundModal = "عام"
                                                    showIqamaSoundPicker = true
                                                },
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(13.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("تعديل الصوت", fontSize = 11.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 3. تذكير قبل الصلاة (الدعاء) - تعديل الوقت (5-30د) + النص + الصوت
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "تذكير قبل الصلاة (الدعاء والتنبيه)",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldAccent
                                    )
                                    Text(
                                        text = "تنبيه قبل الأذان بـ $prePrayerMinutesBefore دقيقة بالدعاء المبارك",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Switch(
                                    checked = prePrayerBigMessage,
                                    onCheckedChange = { onUpdatePrePrayerSettings(it, prePrayerMinutesBefore, prePrayerSoundType) }
                                )
                            }

                            if (prePrayerBigMessage) {
                                Text(text = "تعديل وقت الدعاء قبل الأذان (من 5 إلى 30 دقيقة):", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    listOf(5, 10, 15, 20, 25, 30).forEach { mins ->
                                        val isSelected = prePrayerMinutesBefore == mins
                                        Surface(
                                            onClick = { onUpdatePrePrayerSettings(true, mins, prePrayerSoundType) },
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (isSelected) GoldAccent else MaterialTheme.colorScheme.surfaceVariant,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                text = "$mins د",
                                                fontSize = 10.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.padding(vertical = 6.dp)
                                            )
                                        }
                                    }
                                }

                                // Editable Dua Text Box
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = GoldAccent.copy(alpha = 0.1f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(text = "نص دعاء وتنبيه قبل الصلاة:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                                            OutlinedButton(
                                                onClick = {
                                                    currentPrayerForDuaText = "عام"
                                                    customDuaInput = prePrayerDuaCustomText
                                                    showDuaTextDialog = true
                                                },
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                            ) {
                                                Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(13.dp), tint = GoldAccent)
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("تعديل النص ✍️", fontSize = 10.sp, color = GoldAccent, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                        Text(
                                            text = prePrayerDuaCustomText,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }

                                // Sound Picker button
                                val currentDuaAudio = adhanAudiosList.find { it.id == prePrayerSoundType }
                                    ?: adhanAudiosList.find { it.category == "dua" }
                                    ?: adhanAudiosList.firstOrNull()

                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = GoldAccent.copy(alpha = 0.15f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(text = "صوت الدعاء المعتمد:", fontSize = 11.sp, color = GoldAccent)
                                            Text(text = currentDuaAudio?.nameAr ?: "دعاء وتنبيه", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            if (currentDuaAudio != null) {
                                                IconButton(
                                                    onClick = { onTestAudio(currentDuaAudio.audioUrl, currentDuaAudio.nameAr) },
                                                    modifier = Modifier.size(32.dp)
                                                ) {
                                                    Icon(Icons.Filled.VolumeUp, contentDescription = "استماع", tint = GoldAccent)
                                                }
                                            }
                                            FilledTonalButton(
                                                onClick = {
                                                    selectedPrayerForSoundModal = "عام"
                                                    showDuaSoundPicker = true
                                                },
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(13.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("تعديل الصوت", fontSize = 11.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 4. تذكير أذكار ما بعد الصلاة (على شكل نص صامت بدون صوت + تعديل الوقت 5-30د + تعديل النص)
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, EmeraldLight.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "تذكير أذكار بعد الصلاة (بالنص)",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldLight
                                    )
                                    Text(
                                        text = "تنبيه بالنص صامت بدون صوت بعد الصلاة بـ $postPrayerDelayMinutes دقيقة",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Switch(
                                    checked = postPrayerAthkarReminder,
                                    onCheckedChange = { onUpdatePostPrayerAthkar(it, postPrayerAthkarTime) }
                                )
                            }

                            if (postPrayerAthkarReminder) {
                                // Silent Notification Badge
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = EmeraldLight.copy(alpha = 0.15f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(Icons.Filled.VolumeOff, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(16.dp))
                                        Text(
                                            text = "تذكير بالنص صامت بدون صوت (يظهر في الإشعارات وعلى الشاشة)",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = EmeraldLight
                                        )
                                    }
                                }

                                Text(text = "تعديل وقت الظهور بعد الصلاة (من 5 إلى 30 دقيقة):", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    listOf(5, 10, 15, 20, 25, 30).forEach { mins ->
                                        val isSelected = postPrayerDelayMinutes == mins
                                        Surface(
                                            onClick = { onUpdatePostPrayerDelayMinutes(mins) },
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (isSelected) EmeraldLight else MaterialTheme.colorScheme.surfaceVariant,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                text = "$mins د",
                                                fontSize = 10.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.padding(vertical = 6.dp)
                                            )
                                        }
                                    }
                                }

                                // Editable Post-Prayer Athkar Text
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(text = "النص المقروء لأذكار بعد الصلاة:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EmeraldLight)
                                            OutlinedButton(
                                                onClick = {
                                                    postPrayerTextInput = postPrayerCustomText
                                                    showPostPrayerTextDialog = true
                                                },
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                            ) {
                                                Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(13.dp), tint = EmeraldLight)
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("تعديل النص ✍️", fontSize = 10.sp, color = EmeraldLight, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                        Text(
                                            text = postPrayerCustomText,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }

                                Button(
                                    onClick = { showFullAthkarDialog = true },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Color.Black),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Filled.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("عرض أذكار الصلاة كاملة 📜 (تذكير بالنص صامت)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // 5. تذكير أذكار الصباح (نص + صوت + تعديل الوقت 5-30د + تعديل النص + تعديل الصوت)
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "تذكير أذكار الصباح (نص + صوت)",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldAccent
                                    )
                                    Text(
                                        text = "تنبيه بالصوت والنص في الموعد المحدد: $morningAthkarTime",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Switch(
                                    checked = morningAthkarReminder,
                                    onCheckedChange = { onUpdateMorningAthkar(it, morningAthkarTime) }
                                )
                            }

                            if (morningAthkarReminder) {
                                Text(text = "أوقات مقترحة سريعة لأذكار الصباح (من 5 إلى 30 دقيقة بعد الشروق/الفجر):", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    listOf("05:30" to "5د", "06:00" to "10د", "06:30" to "15د", "07:00" to "20د", "07:30" to "30د").forEach { (timeVal, lbl) ->
                                        val isSelected = morningAthkarTime == timeVal
                                        Surface(
                                            onClick = { onUpdateMorningAthkar(true, timeVal) },
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (isSelected) GoldAccent else MaterialTheme.colorScheme.surfaceVariant,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                text = lbl,
                                                fontSize = 10.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.padding(vertical = 6.dp)
                                            )
                                        }
                                    }
                                }

                                // Custom Morning Text Box
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = GoldAccent.copy(alpha = 0.1f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(text = "نص أذكار الصباح:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                                            OutlinedButton(
                                                onClick = {
                                                    morningTextInput = morningAthkarCustomText
                                                    showMorningTextDialog = true
                                                },
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                            ) {
                                                Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(13.dp), tint = GoldAccent)
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("تعديل النص ✍️", fontSize = 10.sp, color = GoldAccent, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                        Text(
                                            text = morningAthkarCustomText,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }

                                // Sound Notification Toggle Switch for Morning Athkar
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "تشغيل الإشعار الصوتي لأذكار الصباح",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = if (morningAthkarSoundEnabled) "مفعّل: تشغيل الصوت المعتمد مع الإشعار" else "صامت: تذكير بالنص فقط دون صوت",
                                                fontSize = 10.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        Switch(
                                            checked = morningAthkarSoundEnabled,
                                            onCheckedChange = onToggleMorningAthkarSound
                                        )
                                    }
                                }

                                if (morningAthkarSoundEnabled) {
                                    // Sound Picker button
                                    val currentMorningAudio = adhanAudiosList.find { it.id == morningAthkarSoundId }
                                        ?: adhanAudiosList.find { it.category == "athkar" }
                                        ?: adhanAudiosList.firstOrNull()

                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = GoldAccent.copy(alpha = 0.15f),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(10.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(text = "صوت أذكار الصباح المعتمد:", fontSize = 11.sp, color = GoldAccent)
                                                Text(text = currentMorningAudio?.nameAr ?: "صوت أذكار الصباح", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                            }
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                if (currentMorningAudio != null) {
                                                    IconButton(
                                                        onClick = { onTestAudio(currentMorningAudio.audioUrl, currentMorningAudio.nameAr) },
                                                        modifier = Modifier.size(32.dp)
                                                    ) {
                                                        Icon(Icons.Filled.VolumeUp, contentDescription = "استماع", tint = GoldAccent)
                                                    }
                                                }
                                                FilledTonalButton(
                                                    onClick = { showMorningSoundPicker = true },
                                                    shape = RoundedCornerShape(8.dp),
                                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                                ) {
                                                    Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(13.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("تعديل الصوت", fontSize = 11.sp)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 6. تذكير أذكار المساء (نص + صوت + تعديل الوقت 5-30د + تعديل النص + تعديل الصوت)
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "تذكير أذكار المساء (نص + صوت)",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "تنبيه بالصوت والنص في الموعد المحدد: $eveningAthkarTime",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Switch(
                                    checked = eveningAthkarReminder,
                                    onCheckedChange = { onUpdateEveningAthkar(it, eveningAthkarTime) }
                                )
                            }

                            if (eveningAthkarReminder) {
                                Text(text = "أوقات مقترحة سريعة لأذكار المساء (من 5 إلى 30 دقيقة بعد العصر):", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    listOf("15:30" to "5د", "16:00" to "10د", "16:30" to "15د", "17:00" to "20د", "17:30" to "30د").forEach { (timeVal, lbl) ->
                                        val isSelected = eveningAthkarTime == timeVal
                                        Surface(
                                            onClick = { onUpdateEveningAthkar(true, timeVal) },
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                text = lbl,
                                                fontSize = 10.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.padding(vertical = 6.dp)
                                            )
                                        }
                                    }
                                }

                                // Custom Evening Text Box
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(text = "نص أذكار المساء:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                            OutlinedButton(
                                                onClick = {
                                                    eveningTextInput = eveningAthkarCustomText
                                                    showEveningTextDialog = true
                                                },
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                            ) {
                                                Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(13.dp), tint = MaterialTheme.colorScheme.primary)
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("تعديل النص ✍️", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                        Text(
                                            text = eveningAthkarCustomText,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }

                                // Sound Notification Toggle Switch for Evening Athkar
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "تشغيل الإشعار الصوتي لأذكار المساء",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = if (eveningAthkarSoundEnabled) "مفعّل: تشغيل الصوت المعتمد مع الإشعار" else "صامت: تذكير بالنص فقط دون صوت",
                                                fontSize = 10.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        Switch(
                                            checked = eveningAthkarSoundEnabled,
                                            onCheckedChange = onToggleEveningAthkarSound
                                        )
                                    }
                                }

                                if (eveningAthkarSoundEnabled) {
                                    // Sound Picker button
                                    val currentEveningAudio = adhanAudiosList.find { it.id == eveningAthkarSoundId }
                                        ?: adhanAudiosList.find { it.category == "athkar" }
                                        ?: adhanAudiosList.firstOrNull()

                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(10.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(text = "صوت أذكار المساء المعتمد:", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                                                Text(text = currentEveningAudio?.nameAr ?: "صوت أذكار المساء", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                            }
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                if (currentEveningAudio != null) {
                                                    IconButton(
                                                        onClick = { onTestAudio(currentEveningAudio.audioUrl, currentEveningAudio.nameAr) },
                                                        modifier = Modifier.size(32.dp)
                                                    ) {
                                                        Icon(Icons.Filled.VolumeUp, contentDescription = "استماع", tint = MaterialTheme.colorScheme.primary)
                                                    }
                                                }
                                                FilledTonalButton(
                                                    onClick = { showEveningSoundPicker = true },
                                                    shape = RoundedCornerShape(8.dp),
                                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                                ) {
                                                    Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(13.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("تعديل الصوت", fontSize = 11.sp)
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
        }

        // ==========================================
        // TAB 3: دقائق المواقيت (Prayer Time Adjustments)
        // ==========================================
        if (selectedTab == 2) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "تعديل دقائق كل صلاة يدوياً", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Text(text = "لضبط المواقيت بدقة حسب المسجد المحلي (+/- دقائق)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        TextButton(onClick = onResetOffsets) {
                            Text("إعادة ضبط", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }

                items(PrayerType.values()) { prayer ->
                    val offset = prayerOffsets[prayer] ?: 0
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = prayer.arName, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    text = if (offset > 0) "+$offset دقيقة" else if (offset < 0) "$offset دقيقة" else "الموعد الرسمي",
                                    fontSize = 11.sp,
                                    color = if (offset != 0) GoldAccent else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = { onSetOffset(prayer, offset - 1) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(Icons.Filled.Remove, contentDescription = "تقليل دقيقة")
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.widthIn(min = 40.dp)
                                ) {
                                    Text(
                                        text = "$offset",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                IconButton(
                                    onClick = { onSetOffset(prayer, offset + 1) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(Icons.Filled.Add, contentDescription = "زيادة دقيقة")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // ==========================================
    // DIALOGS
    // ==========================================

    // 1. Add Sound Dialog
    if (showAddSoundDialog) {
        AlertDialog(
            onDismissRequest = { showAddSoundDialog = false },
            title = { Text("إضافة صوت جديد للتطبيق", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newSoundName,
                        onValueChange = { newSoundName = it },
                        label = { Text("اسم الصوت أو المؤذن") },
                        placeholder = { Text("مثلاً: أذان الحرم المكي أو صوت الإقامة") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newSoundDesc,
                        onValueChange = { newSoundDesc = it },
                        label = { Text("وصف الصوت") },
                        placeholder = { Text("مثلاً: صوت ندي خاشع") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newSoundUrl,
                        onValueChange = { newSoundUrl = it },
                        label = { Text("رابط أو مسار الملف الصوتي (MP3)") },
                        placeholder = { Text("https://... أو اسم النغمة") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(text = "تصنيف الصوت:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(
                            "adhan" to "أذان",
                            "reminder" to "قبل الصلاة",
                            "iqama" to "إقامة",
                            "athkar" to "أذكار"
                        ).forEach { (cat, label) ->
                            val isSel = newSoundCategory == cat
                            Surface(
                                onClick = { newSoundCategory = cat },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
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
                        if (newSoundName.isNotBlank()) {
                            onAddSound(
                                newSoundName,
                                newSoundDesc.ifEmpty { "صوت مضاف بواسطة المستخدم" },
                                newSoundUrl.ifEmpty { "https://cdn.aladhan.com/audio/adhans/makkah.mp3" },
                                newSoundCategory
                            )
                            showAddSoundDialog = false
                            newSoundName = ""
                            newSoundDesc = ""
                            newSoundUrl = ""
                        }
                    }
                ) {
                    Text("حفظ وإضافة")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddSoundDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // 2. Pick Sound Dialog for Friday First Adhan
    if (showFridayFirstSoundPicker) {
        AlertDialog(
            onDismissRequest = { showFridayFirstSoundPicker = false },
            title = {
                Text("اختر صوت الأذان الأول ليوم الجمعة", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = GoldAccent)
            },
            text = {
                val availableAudios = adhanAudiosList.filter { it.category == "friday" || it.category == "adhan" }
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().heightIn(max = 350.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(availableAudios) { audio ->
                        val isSelected = audio.id == fridayFirstAdhanSoundId
                        Card(
                            onClick = {
                                onUpdateFridayFirstSound(audio.id)
                                showFridayFirstSoundPicker = false
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                            ),
                            border = if (isSelected) BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = {
                                            onUpdateFridayFirstSound(audio.id)
                                            showFridayFirstSoundPicker = false
                                        }
                                    )
                                    Column {
                                        Text(text = audio.nameAr, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Text(text = audio.descriptionAr, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                                IconButton(onClick = { onTestAudio(audio.audioUrl, audio.nameAr) }) {
                                    Icon(Icons.Filled.VolumeUp, contentDescription = "استماع", tint = GoldAccent)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showFridayFirstSoundPicker = false }) {
                    Text("إغلاق")
                }
            }
        )
    }

    // 3. Pick Sound Dialog for Friday Second Adhan
    if (showFridaySecondSoundPicker) {
        AlertDialog(
            onDismissRequest = { showFridaySecondSoundPicker = false },
            title = {
                Text("اختر صوت الأذان الثاني ليوم الجمعة", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = GoldAccent)
            },
            text = {
                val availableAudios = adhanAudiosList.filter { it.category == "friday" || it.category == "adhan" }
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().heightIn(max = 350.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(availableAudios) { audio ->
                        val isSelected = audio.id == fridaySecondAdhanSoundId
                        Card(
                            onClick = {
                                onUpdateFridaySecondSound(audio.id)
                                showFridaySecondSoundPicker = false
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                            ),
                            border = if (isSelected) BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = {
                                            onUpdateFridaySecondSound(audio.id)
                                            showFridaySecondSoundPicker = false
                                        }
                                    )
                                    Column {
                                        Text(text = audio.nameAr, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Text(text = audio.descriptionAr, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                                IconButton(onClick = { onTestAudio(audio.audioUrl, audio.nameAr) }) {
                                    Icon(Icons.Filled.VolumeUp, contentDescription = "استماع", tint = GoldAccent)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showFridaySecondSoundPicker = false }) {
                    Text("إغلاق")
                }
            }
        )
    }

    // Morning Athkar Sound Picker Dialog
    if (showMorningSoundPicker) {
        AlertDialog(
            onDismissRequest = { showMorningSoundPicker = false },
            title = { Text("اختيار صوت أذكار الصباح", fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 340.dp)) {
                    val candidateSounds = adhanAudiosList.filter { it.category in listOf("athkar", "reminder", "adhan") }
                    items(candidateSounds, key = { it.id }) { audio ->
                        val isSelected = morningAthkarSoundId == audio.id
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    onUpdateMorningAthkarSound(audio.id)
                                    showMorningSoundPicker = false
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = {
                                            onUpdateMorningAthkarSound(audio.id)
                                            showMorningSoundPicker = false
                                        }
                                    )
                                    Column {
                                        Text(text = audio.nameAr, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Text(text = audio.descriptionAr, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                                IconButton(onClick = { onTestAudio(audio.audioUrl, audio.nameAr) }) {
                                    Icon(Icons.Filled.VolumeUp, contentDescription = "استماع", tint = GoldAccent)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showMorningSoundPicker = false }) {
                    Text("إغلاق")
                }
            }
        )
    }

    // Evening Athkar Sound Picker Dialog
    if (showEveningSoundPicker) {
        AlertDialog(
            onDismissRequest = { showEveningSoundPicker = false },
            title = { Text("اختيار صوت أذكار المساء", fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 340.dp)) {
                    val candidateSounds = adhanAudiosList.filter { it.category in listOf("athkar", "reminder", "adhan") }
                    items(candidateSounds, key = { it.id }) { audio ->
                        val isSelected = eveningAthkarSoundId == audio.id
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    onUpdateEveningAthkarSound(audio.id)
                                    showEveningSoundPicker = false
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) GoldAccent.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = {
                                            onUpdateEveningAthkarSound(audio.id)
                                            showEveningSoundPicker = false
                                        }
                                    )
                                    Column {
                                        Text(text = audio.nameAr, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Text(text = audio.descriptionAr, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                                IconButton(onClick = { onTestAudio(audio.audioUrl, audio.nameAr) }) {
                                    Icon(Icons.Filled.VolumeUp, contentDescription = "استماع", tint = GoldAccent)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showEveningSoundPicker = false }) {
                    Text("إغلاق")
                }
            }
        )
    }

    // 3. Morning Athkar Time Dialog
    if (showMorningTimeDialog) {
        AlertDialog(
            onDismissRequest = { showMorningTimeDialog = false },
            title = { Text("تحديد وقت إشعار أذكار الصباح", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("حدد الساعة والدقيقة (تنسيق 24 ساعة):", fontSize = 12.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = morningHourInput,
                            onValueChange = { if (it.length <= 2) morningHourInput = it },
                            label = { Text("الساعة") },
                            modifier = Modifier.width(80.dp),
                            singleLine = true
                        )
                        Text(text = " : ", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = morningMinInput,
                            onValueChange = { if (it.length <= 2) morningMinInput = it },
                            label = { Text("الدقيقة") },
                            modifier = Modifier.width(80.dp),
                            singleLine = true
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val h = morningHourInput.padStart(2, '0')
                        val m = morningMinInput.padStart(2, '0')
                        onUpdateMorningAthkar(true, "$h:$m")
                        showMorningTimeDialog = false
                    }
                ) {
                    Text("حفظ التوقيت")
                }
            },
            dismissButton = {
                TextButton(onClick = { showMorningTimeDialog = false }) { Text("إلغاء") }
            }
        )
    }

    // 4. Evening Athkar Time Dialog
    if (showEveningTimeDialog) {
        AlertDialog(
            onDismissRequest = { showEveningTimeDialog = false },
            title = { Text("تحديد وقت إشعار أذكار المساء", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("حدد الساعة والدقيقة (مثلاً 15:00 عصراً):", fontSize = 12.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = eveningHourInput,
                            onValueChange = { if (it.length <= 2) eveningHourInput = it },
                            label = { Text("الساعة") },
                            modifier = Modifier.width(80.dp),
                            singleLine = true
                        )
                        Text(text = " : ", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = eveningMinInput,
                            onValueChange = { if (it.length <= 2) eveningMinInput = it },
                            label = { Text("الدقيقة") },
                            modifier = Modifier.width(80.dp),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = {
                            eveningHourInput = "15"
                            eveningMinInput = "00"
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("ضبط سريع على 15:00 (المقترح)", color = Color.Black, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val h = eveningHourInput.padStart(2, '0')
                        val m = eveningMinInput.padStart(2, '0')
                        onUpdateEveningAthkar(true, "$h:$m")
                        showEveningTimeDialog = false
                    }
                ) {
                    Text("حفظ التوقيت")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEveningTimeDialog = false }) { Text("إلغاء") }
            }
        )
    }

    // Post-Prayer Athkar Sound Picker Dialog
    if (showPostPrayerSoundPicker) {
        AlertDialog(
            onDismissRequest = { showPostPrayerSoundPicker = false },
            title = { Text("اختيار صوت أذكار ما بعد الصلاة", fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 340.dp)) {
                    val candidateSounds = adhanAudiosList.filter { it.category in listOf("athkar", "reminder", "adhan") }
                    items(candidateSounds, key = { it.id }) { audio ->
                        val isSelected = postPrayerAthkarSoundId == audio.id
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    onUpdatePostPrayerAthkarSound(audio.id)
                                    showPostPrayerSoundPicker = false
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) EmeraldLight.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = {
                                            onUpdatePostPrayerAthkarSound(audio.id)
                                            showPostPrayerSoundPicker = false
                                        }
                                    )
                                    Column {
                                        Text(text = audio.nameAr, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Text(text = audio.descriptionAr, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                                IconButton(onClick = { onTestAudio(audio.audioUrl, audio.nameAr) }) {
                                    Icon(Icons.Filled.VolumeUp, contentDescription = "استماع", tint = GoldAccent)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showPostPrayerSoundPicker = false }) {
                    Text("إغلاق")
                }
            }
        )
    }

    // 5. Post-Prayer Athkar Time Dialog
    if (showPostPrayerTimeDialog) {
        AlertDialog(
            onDismissRequest = { showPostPrayerTimeDialog = false },
            title = { Text("تحديد وقت إشعار أذكار بعد الصلاة", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("حدد الساعة والدقيقة (مثلاً 05:00 بعد الفجر):", fontSize = 12.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = postPrayerHourInput,
                            onValueChange = { if (it.length <= 2) postPrayerHourInput = it },
                            label = { Text("الساعة") },
                            modifier = Modifier.width(80.dp),
                            singleLine = true
                        )
                        Text(text = " : ", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = postPrayerMinInput,
                            onValueChange = { if (it.length <= 2) postPrayerMinInput = it },
                            label = { Text("الدقيقة") },
                            modifier = Modifier.width(80.dp),
                            singleLine = true
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = {
                            postPrayerHourInput = "05"
                            postPrayerMinInput = "00"
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldLight),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("ضبط سريع على 05:00 صباحاً (المقترح)", color = Color.White, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val h = postPrayerHourInput.padStart(2, '0')
                        val m = postPrayerMinInput.padStart(2, '0')
                        onUpdatePostPrayerAthkar(true, "$h:$m")
                        showPostPrayerTimeDialog = false
                    }
                ) {
                    Text("حفظ التوقيت")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPostPrayerTimeDialog = false }) { Text("إلغاء") }
            }
        )
    }

    if (showFullAthkarDialog) {
        FullPostPrayerAthkarBigMessageDialog(
            onDismiss = { showFullAthkarDialog = false }
        )
    }

    // Modal: Choose sound for Pre-Prayer Dua
    if (showDuaSoundPicker) {
        var applyDuaToAll by remember { mutableStateOf(false) }
        val currentSoundId = prayerScheduleMap[selectedPrayerForSoundModal]?.duaSoundId ?: "dua_pre_athan"
        val availableDuaAudios = adhanAudiosList.filter {
            it.category == "dua" || it.category == "reminder" || it.category == "athkar" || it.category == "adhan"
        }.ifEmpty { adhanAudiosList }

        AlertDialog(
            onDismissRequest = { showDuaSoundPicker = false },
            title = {
                Column {
                    Text(
                        text = "اختر صوت الدعاء والتنبيه لصلاة $selectedPrayerForSoundModal",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = GoldAccent
                    )
                    Text(
                        text = "يمكنك الاستماع للتسجيل وتعيينه للتنبيه قبل الأذان",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 340.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(availableDuaAudios) { audio ->
                            val isSelected = audio.id == currentSoundId
                            Card(
                                onClick = {
                                    if (applyDuaToAll) {
                                        prayerScheduleMap = prayerScheduleMap.mapValues { (_, item) ->
                                            item.copy(duaSoundId = audio.id)
                                        }
                                    } else {
                                        val cur = prayerScheduleMap[selectedPrayerForSoundModal] ?: PrayerScheduleItem(selectedPrayerForSoundModal)
                                        prayerScheduleMap = prayerScheduleMap + (selectedPrayerForSoundModal to cur.copy(duaSoundId = audio.id))
                                    }
                                    val curMins = prayerScheduleMap[selectedPrayerForSoundModal]?.preMinutes ?: 5
                                    onUpdatePrePrayerSettings(true, curMins, audio.id)
                                    showDuaSoundPicker = false
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) GoldAccent.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface
                                ),
                                border = if (isSelected) BorderStroke(1.5.dp, GoldAccent) else BorderStroke(0.6.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        RadioButton(
                                            selected = isSelected,
                                            onClick = {
                                                if (applyDuaToAll) {
                                                    prayerScheduleMap = prayerScheduleMap.mapValues { (_, item) ->
                                                        item.copy(duaSoundId = audio.id)
                                                    }
                                                } else {
                                                    val cur = prayerScheduleMap[selectedPrayerForSoundModal] ?: PrayerScheduleItem(selectedPrayerForSoundModal)
                                                    prayerScheduleMap = prayerScheduleMap + (selectedPrayerForSoundModal to cur.copy(duaSoundId = audio.id))
                                                }
                                                val curMins = prayerScheduleMap[selectedPrayerForSoundModal]?.preMinutes ?: 5
                                                onUpdatePrePrayerSettings(true, curMins, audio.id)
                                                showDuaSoundPicker = false
                                            },
                                            colors = RadioButtonDefaults.colors(selectedColor = GoldAccent)
                                        )
                                        Column {
                                            Text(text = audio.nameAr, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            Text(text = audio.descriptionAr, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                    IconButton(onClick = { onTestAudio(audio.audioUrl, audio.nameAr) }) {
                                        Icon(Icons.Filled.PlayArrow, contentDescription = "استماع", tint = GoldAccent)
                                    }
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { applyDuaToAll = !applyDuaToAll }
                            .padding(top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Checkbox(
                            checked = applyDuaToAll,
                            onCheckedChange = { applyDuaToAll = it },
                            colors = CheckboxDefaults.colors(checkedColor = GoldAccent)
                        )
                        Text(
                            text = "تطبيق هذا الصوت على جميع الصلوات الخمس",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDuaSoundPicker = false }) {
                    Text("إغلاق")
                }
            }
        )
    }

    // Modal: Choose sound for Iqama
    if (showIqamaSoundPicker) {
        var applyIqamaToAll by remember { mutableStateOf(false) }
        val currentSoundId = prayerScheduleMap[selectedPrayerForSoundModal]?.iqamaSoundId ?: "iqama_takbeer"
        val availableIqamaAudios = adhanAudiosList.filter {
            it.category == "iqama" || it.category == "reminder" || it.category == "adhan"
        }.ifEmpty { adhanAudiosList }

        AlertDialog(
            onDismissRequest = { showIqamaSoundPicker = false },
            title = {
                Column {
                    Text(
                        text = "اختر صوت الإقامة لصلاة $selectedPrayerForSoundModal",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "يمكنك الاستماع للتسجيل وتعيينه لصوت الإقامة بعد الأذان",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 340.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(availableIqamaAudios) { audio ->
                            val isSelected = audio.id == currentSoundId
                            Card(
                                onClick = {
                                    if (applyIqamaToAll) {
                                        prayerScheduleMap = prayerScheduleMap.mapValues { (_, item) ->
                                            item.copy(iqamaSoundId = audio.id)
                                        }
                                    } else {
                                        val cur = prayerScheduleMap[selectedPrayerForSoundModal] ?: PrayerScheduleItem(selectedPrayerForSoundModal)
                                        prayerScheduleMap = prayerScheduleMap + (selectedPrayerForSoundModal to cur.copy(iqamaSoundId = audio.id))
                                    }
                                    val curMins = prayerScheduleMap[selectedPrayerForSoundModal]?.iqamaMinutes ?: 15
                                    onUpdateIqamaSettings(true, curMins, audio.id)
                                    showIqamaSoundPicker = false
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                                ),
                                border = if (isSelected) BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else BorderStroke(0.6.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        RadioButton(
                                            selected = isSelected,
                                            onClick = {
                                                if (applyIqamaToAll) {
                                                    prayerScheduleMap = prayerScheduleMap.mapValues { (_, item) ->
                                                        item.copy(iqamaSoundId = audio.id)
                                                    }
                                                } else {
                                                    val cur = prayerScheduleMap[selectedPrayerForSoundModal] ?: PrayerScheduleItem(selectedPrayerForSoundModal)
                                                    prayerScheduleMap = prayerScheduleMap + (selectedPrayerForSoundModal to cur.copy(iqamaSoundId = audio.id))
                                                }
                                                val curMins = prayerScheduleMap[selectedPrayerForSoundModal]?.iqamaMinutes ?: 15
                                                onUpdateIqamaSettings(true, curMins, audio.id)
                                                showIqamaSoundPicker = false
                                            },
                                            colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                                        )
                                        Column {
                                            Text(text = audio.nameAr, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            Text(text = audio.descriptionAr, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                    IconButton(onClick = { onTestAudio(audio.audioUrl, audio.nameAr) }) {
                                        Icon(Icons.Filled.VolumeUp, contentDescription = "استماع", tint = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { applyIqamaToAll = !applyIqamaToAll }
                            .padding(top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Checkbox(
                            checked = applyIqamaToAll,
                            onCheckedChange = { applyIqamaToAll = it },
                            colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                        )
                        Text(
                            text = "تطبيق هذا الصوت على جميع الصلوات الخمس",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showIqamaSoundPicker = false }) {
                    Text("إغلاق")
                }
            }
        )
    }

    // ==========================================
    // TEXT EDITING DIALOGS (Dua, Salawat, Athkar)
    // ==========================================

    // 1. Dua Text Dialog
    if (showDuaTextDialog) {
        AlertDialog(
            onDismissRequest = { showDuaTextDialog = false },
            title = {
                Text(
                    text = "تعديل نص دعاء وتنبيه قبل الصلاة ($currentPrayerForDuaText)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = GoldAccent
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "اكتب النص أو الدعاء الذي تود ظهوره في إشعار التذكير قبل الصلاة:",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = customDuaInput,
                        onValueChange = { customDuaInput = it },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp),
                        placeholder = { Text("مثال: اللهم إني أسألك الهدى والتقى والعفاف والغنى") },
                        maxLines = 4
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (currentPrayerForDuaText == "عام") {
                            onUpdatePrePrayerDuaCustomText(customDuaInput)
                        } else {
                            val cur = prayerScheduleMap[currentPrayerForDuaText] ?: PrayerScheduleItem(currentPrayerForDuaText)
                            prayerScheduleMap = prayerScheduleMap + (currentPrayerForDuaText to cur.copy(customDuaText = customDuaInput))
                        }
                        showDuaTextDialog = false
                    }
                ) {
                    Text("حفظ النص")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDuaTextDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // 2. Salawat Text Dialog
    if (showSalawatTextDialog) {
        AlertDialog(
            onDismissRequest = { showSalawatTextDialog = false },
            title = {
                Text(
                    text = "تعديل نص التذكير بالصلاة على النبي ﷺ",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = EmeraldLight
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "اكتب نص الصلاة على النبي ﷺ المفضل لديك للتنبيه الدوري:",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = salawatTextInput,
                        onValueChange = { salawatTextInput = it },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp),
                        placeholder = { Text("اللهم صل وسلم على نبينا محمد ﷺ") },
                        maxLines = 4
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateSalawatCustomText(salawatTextInput)
                        showSalawatTextDialog = false
                    }
                ) {
                    Text("حفظ النص")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSalawatTextDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // 3. Morning Athkar Text Dialog
    if (showMorningTextDialog) {
        AlertDialog(
            onDismissRequest = { showMorningTextDialog = false },
            title = {
                Text(
                    text = "تعديل نص أذكار الصباح",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = GoldAccent
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "اكتب نص الذكر أو التنبيه لأذكار الصباح:",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = morningTextInput,
                        onValueChange = { morningTextInput = it },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp),
                        placeholder = { Text("أصبحنا وأصبح الملك لله والحمد لله...") },
                        maxLines = 4
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateMorningAthkarCustomText(morningTextInput)
                        showMorningTextDialog = false
                    }
                ) {
                    Text("حفظ النص")
                }
            },
            dismissButton = {
                TextButton(onClick = { showMorningTextDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // 4. Evening Athkar Text Dialog
    if (showEveningTextDialog) {
        AlertDialog(
            onDismissRequest = { showEveningTextDialog = false },
            title = {
                Text(
                    text = "تعديل نص أذكار المساء",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "اكتب نص الذكر أو التنبيه لأذكار المساء:",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = eveningTextInput,
                        onValueChange = { eveningTextInput = it },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp),
                        placeholder = { Text("أمسينا وأمسى الملك لله والحمد لله...") },
                        maxLines = 4
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateEveningAthkarCustomText(eveningTextInput)
                        showEveningTextDialog = false
                    }
                ) {
                    Text("حفظ النص")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEveningTextDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // 5. Post Prayer Athkar Text Dialog
    if (showPostPrayerTextDialog) {
        AlertDialog(
            onDismissRequest = { showPostPrayerTextDialog = false },
            title = {
                Text(
                    text = "تعديل نص أذكار ما بعد الصلاة ($currentPrayerForDuaText)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = EmeraldLight
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "اكتب نص الأذكار التي تود ظهورها بعد الصلاة (بالنص صامت بدون صوت):",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = postPrayerTextInput,
                        onValueChange = { postPrayerTextInput = it },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp),
                        placeholder = { Text("أستغفر الله (٣ مرات)، اللهم أنت السلام ومنك السلام...") },
                        maxLines = 4
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (currentPrayerForDuaText != "عام") {
                            val cur = prayerScheduleMap[currentPrayerForDuaText] ?: PrayerScheduleItem(currentPrayerForDuaText)
                            prayerScheduleMap = prayerScheduleMap + (currentPrayerForDuaText to cur.copy(customPostAthkarText = postPrayerTextInput))
                        }
                        onUpdatePostPrayerCustomText(postPrayerTextInput)
                        showPostPrayerTextDialog = false
                    }
                ) {
                    Text("حفظ النص")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPostPrayerTextDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    // Dialog for picking prayer-specific adhan sound
    if (showPrayerSoundDialog) {
        val prayerArName = when (selectedPrayerForSoundDialog) {
            PrayerType.FAJR -> "صلاة الفجر"
            PrayerType.DHUHR -> "صلاة الظهر"
            PrayerType.ASR -> "صلاة العصر"
            PrayerType.MAGHRIB -> "صلاة المغرب"
            PrayerType.ISHA -> "صلاة العشاء"
            else -> "الصلاة"
        }
        val currentSelectedSoundId = when (selectedPrayerForSoundDialog) {
            PrayerType.FAJR -> fajrAdhanSound
            PrayerType.DHUHR -> dhuhrAdhanSound
            PrayerType.ASR -> asrAdhanSound
            PrayerType.MAGHRIB -> maghribAdhanSound
            PrayerType.ISHA -> ishaAdhanSound
            else -> selectedMuadhinId
        }

        AlertDialog(
            onDismissRequest = { showPrayerSoundDialog = false },
            title = {
                Text(
                    text = "اختيار صوت أذان $prayerArName",
                    fontWeight = FontWeight.Bold,
                    color = GoldAccent
                )
            },
            text = {
                LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 350.dp)) {
                    val availableAudios = adhanAudiosList.filter { it.category == "adhan" || it.category == "friday" }
                    items(availableAudios, key = { it.id }) { audio ->
                        val isSelected = currentSelectedSoundId == audio.id
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    onUpdatePrayerAdhanSound(selectedPrayerForSoundDialog, audio.id)
                                    showPrayerSoundDialog = false
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) GoldAccent.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface
                            ),
                            border = if (isSelected) BorderStroke(1.dp, GoldAccent) else null
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = {
                                            onUpdatePrayerAdhanSound(selectedPrayerForSoundDialog, audio.id)
                                            showPrayerSoundDialog = false
                                        }
                                    )
                                    Column {
                                        Text(text = audio.nameAr, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Text(text = audio.descriptionAr, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                                IconButton(onClick = { onTestAudio(audio.audioUrl, audio.nameAr) }) {
                                    Icon(Icons.Filled.VolumeUp, contentDescription = "استماع", tint = GoldAccent)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showPrayerSoundDialog = false }) {
                    Text("إغلاق")
                }
            }
        )
    }
}
}

data class PrayerScheduleItem(
    val prayerName: String,
    val preMinutes: Int = 5,
    val preEnabled: Boolean = true,
    val iqamaMinutes: Int = 15,
    val iqamaEnabled: Boolean = true,
    val postAthkarMinutes: Int = 5,
    val postAthkarEnabled: Boolean = true,
    val duaSoundId: String = "pre_prayer_ayah",
    val iqamaSoundId: String = "iqama_takbeer",
    val volume: Float = 0.85f,
    val selectedDays: Set<Int> = setOf(1, 2, 3, 4, 5, 6, 7),
    val customDuaText: String = "اللهم إني أسألك الهدى والتقى والعفاف والغنى",
    val customPostAthkarText: String = "أستغفر الله (٣ مرات)، اللهم أنت السلام ومنك السلام، تباركت يا ذا الجلال والإكرام"
)
