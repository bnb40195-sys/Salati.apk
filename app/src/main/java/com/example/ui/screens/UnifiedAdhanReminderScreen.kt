package com.example.ui.screens

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
                        text = { Text("الأذان والمؤذنون", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal, fontSize = 12.sp) },
                        icon = { Icon(Icons.Filled.VolumeUp, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("تذكير الصلاة والإقامة", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal, fontSize = 12.sp) },
                        icon = { Icon(Icons.Filled.AccessTime, contentDescription = null, modifier = Modifier.size(16.dp)) }
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

                // Add sound button & shortcut to creator studio
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                newSoundCategory = "adhan"
                                showAddSoundDialog = true
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("إضافة صوت أذان", fontSize = 12.sp)
                        }

                        if (isDeveloperUnlocked) {
                            OutlinedButton(
                                onClick = onOpenCreatorStudio,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Filled.Build, contentDescription = null, modifier = Modifier.size(16.dp), tint = GoldAccent)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("صانع التطبيق", fontSize = 12.sp)
                            }
                        }
                    }
                }

                // Adhan Audios List
                item {
                    Text(
                        text = "اختر المؤذن المفضل للصلوات الخمس:",
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

                // Move sound deletion to Creator Studio notification card
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.4f))
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
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Filled.Build, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(20.dp))
                                Column {
                                    Text(
                                        text = "إدارة وحذف الأصوات عبر صانع التطبيق",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    if (isDeveloperUnlocked) {
                                        Text(
                                            text = "لحذف أي صوت أو إضافة تسجيلات جديدة، انتقل إلى صانع التطبيق",
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    } else {
                                        Text(
                                            text = "أصوات أذان الجمعة والتنبيهات المعتمدة",
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                            if (isDeveloperUnlocked) {
                                OutlinedButton(
                                    onClick = onOpenCreatorStudio,
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("فتح الصانع", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Friday Adhans with sound selector
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
                                    Text(text = "تخصيص تفعيل وتغيير أصوات الأذان الأول والثاني", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                                    Text("يُرفع قبل وقت الجمعة بـ 20 دقيقة لتنبيه المسلمين", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Switch(
                                    checked = fridayFirstAdhan,
                                    onCheckedChange = { onUpdateFridaySettings(it, fridaySecondAdhan, fridaySoundOnly) }
                                )
                            }

                            if (fridayFirstAdhan) {
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
                                    Text("يُرفع عند صعود الإمام إلى المنبر لإلقاء الخطبة", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Switch(
                                    checked = fridaySecondAdhan,
                                    onCheckedChange = { onUpdateFridaySettings(fridayFirstAdhan, it, fridaySoundOnly) }
                                )
                            }

                            if (fridaySecondAdhan) {
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

        // TAB 1: تذكير الصلاة والإقامة
        if (selectedTab == 1) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Top Action: Add Custom Sound
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "إدارة مكتبة الأصوات والتنبيهات",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "يمكنك إضافة أي صوت للتذكير أو حذفه واختياره للصلوات والإقامة",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Button(
                                onClick = {
                                    newSoundCategory = "reminder"
                                    showAddSoundDialog = true
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("إضافة صوت", fontSize = 11.sp)
                            }
                        }
                    }
                }

                // 1. تذكير بعد الصلاة (الإقامة)
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
                                    Text(
                                        text = "تذكير بعد الصلاة (الإقامة)",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "تنبيه صوتي باقتراب إقامة الصلاة بعد الأذان بمقدار محدد",
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
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(text = "وقت الإقامة بعد الأذان بـ:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.padding(vertical = 6.dp)
                                ) {
                                    listOf(5, 10, 15, 20, 30).forEach { mins ->
                                        val isSelected = iqamaMinutesAfter == mins
                                        Surface(
                                            onClick = { onUpdateIqamaSettings(true, mins, iqamaSoundType) },
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                                        ) {
                                            Text(
                                                text = "$mins دقائق",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                Text(text = "اختر صوت الإقامة من القائمة:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Spacer(modifier = Modifier.height(6.dp))

                                // Sound Picker for Iqama
                                adhanAudiosList.forEach { sound ->
                                    val isSoundSelected = iqamaSoundType == sound.id
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable { onUpdateIqamaSettings(true, iqamaMinutesAfter, sound.id) }
                                            .padding(vertical = 4.dp, horizontal = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            RadioButton(
                                                selected = isSoundSelected,
                                                onClick = { onUpdateIqamaSettings(true, iqamaMinutesAfter, sound.id) },
                                                colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                                            )
                                            Column {
                                                Text(
                                                    text = sound.nameAr,
                                                    fontSize = 12.sp,
                                                    fontWeight = if (isSoundSelected) FontWeight.Bold else FontWeight.Normal
                                                )
                                                Text(text = sound.descriptionAr, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            IconButton(
                                                onClick = { onTestAudio(sound.audioUrl, sound.nameAr) },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(Icons.Filled.PlayArrow, contentDescription = "استماع", tint = GoldAccent, modifier = Modifier.size(18.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 2. تذكير قبل الصلاة
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
                                    Text(
                                        text = "تذكير قبل الصلاة",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "تنبيه للاستعداد والوضوء قبل دخول وقت الصلاة",
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
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(text = "التنبيه قبل الأذان بـ:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.padding(vertical = 6.dp)
                                ) {
                                    listOf(5, 10, 15, 20).forEach { mins ->
                                        val isSelected = prePrayerMinutesBefore == mins
                                        Surface(
                                            onClick = { onUpdatePrePrayerSettings(true, mins, prePrayerSoundType) },
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                                        ) {
                                            Text(
                                                text = "$mins دقائق",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                Text(text = "اختيار أي صوت للتذكير قبل الصلاة:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Spacer(modifier = Modifier.height(6.dp))

                                // Sound Picker for Pre-Prayer
                                adhanAudiosList.forEach { sound ->
                                    val isSoundSelected = prePrayerSoundType == sound.id
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable { onUpdatePrePrayerSettings(true, prePrayerMinutesBefore, sound.id) }
                                            .padding(vertical = 4.dp, horizontal = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            RadioButton(
                                                selected = isSoundSelected,
                                                onClick = { onUpdatePrePrayerSettings(true, prePrayerMinutesBefore, sound.id) },
                                                colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                                            )
                                            Column {
                                                Text(
                                                    text = sound.nameAr,
                                                    fontSize = 12.sp,
                                                    fontWeight = if (isSoundSelected) FontWeight.Bold else FontWeight.Normal
                                                )
                                                Text(text = sound.descriptionAr, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            IconButton(
                                                onClick = { onTestAudio(sound.audioUrl, sound.nameAr) },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(Icons.Filled.PlayArrow, contentDescription = "استماع", tint = GoldAccent, modifier = Modifier.size(18.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 3. أذكار الصباح والمساء وتحديد التوقيت بدقة (مثل 15:00)
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "توقيت إشعارات أذكار الصباح والمساء",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldAccent
                            )
                            Text(
                                text = "حدد الوقت الدقيق لإظهار إشعار الأذكار مع خيار التوقيت المخصص مثل 15:00",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // أذكار الصباح
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "إشعار أذكار الصباح", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                    Text(text = "التوقيت الحالي: $morningAthkarTime صباحاً", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                                    val morningSoundObj = adhanAudiosList.find { it.id == morningAthkarSoundId }
                                    Text(
                                        text = "صوت التنبيه: ${morningSoundObj?.nameAr ?: "تنبيه أذكار الصباح"}",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    val morningSoundObj = adhanAudiosList.find { it.id == morningAthkarSoundId }
                                    IconButton(
                                        onClick = {
                                            val url = morningSoundObj?.audioUrl ?: "https://cdn.aladhan.com/audio/adhans/athkar_morning.mp3"
                                            onTestAudio(url, morningSoundObj?.nameAr ?: "أذكار الصباح")
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Filled.VolumeUp, contentDescription = "معاينة الصوت", tint = GoldAccent, modifier = Modifier.size(18.dp))
                                    }
                                    OutlinedButton(
                                        onClick = { showMorningSoundPicker = true },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Icon(Icons.Filled.MusicNote, contentDescription = null, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text("تعديل الصوت", fontSize = 10.sp)
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))
                                    OutlinedButton(
                                        onClick = { showMorningTimeDialog = true },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Icon(Icons.Filled.Schedule, contentDescription = null, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text("تعديل الوقت", fontSize = 10.sp)
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Switch(
                                        checked = morningAthkarReminder,
                                        onCheckedChange = { onUpdateMorningAthkar(it, morningAthkarTime) }
                                    )
                                }
                            }

                            Divider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.surfaceVariant)

                            // أذكار المساء (مع خيار 15:00 المقترح)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "إشعار أذكار المساء", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                    Text(text = "التوقيت الحالي: $eveningAthkarTime (مثل 15:00 عصراً)", fontSize = 11.sp, color = GoldAccent)
                                    val eveningSoundObj = adhanAudiosList.find { it.id == eveningAthkarSoundId }
                                    Text(
                                        text = "صوت التنبيه: ${eveningSoundObj?.nameAr ?: "تنبيه أذكار المساء"}",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    val eveningSoundObj = adhanAudiosList.find { it.id == eveningAthkarSoundId }
                                    IconButton(
                                        onClick = {
                                            val url = eveningSoundObj?.audioUrl ?: "https://cdn.aladhan.com/audio/adhans/athkar_evening.mp3"
                                            onTestAudio(url, eveningSoundObj?.nameAr ?: "أذكار المساء")
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Filled.VolumeUp, contentDescription = "معاينة الصوت", tint = GoldAccent, modifier = Modifier.size(18.dp))
                                    }
                                    OutlinedButton(
                                        onClick = { showEveningSoundPicker = true },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Icon(Icons.Filled.MusicNote, contentDescription = null, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text("تعديل الصوت", fontSize = 10.sp)
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))
                                    OutlinedButton(
                                        onClick = { showEveningTimeDialog = true },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Icon(Icons.Filled.Schedule, contentDescription = null, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text("تعديل الوقت", fontSize = 10.sp)
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Switch(
                                        checked = eveningAthkarReminder,
                                        onCheckedChange = { onUpdateEveningAthkar(it, eveningAthkarTime) }
                                    )
                                }
                            }

                            // Quick preset chips for Evening Athkar
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "أوقات مقترحة سريعة لأذكار المساء:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                listOf("15:00", "15:30", "16:00", "17:00").forEach { timeOption ->
                                    val isSelected = eveningAthkarTime == timeOption
                                    Surface(
                                        onClick = { onUpdateEveningAthkar(true, timeOption) },
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSelected) GoldAccent else MaterialTheme.colorScheme.surfaceVariant
                                    ) {
                                        Text(
                                            text = if (timeOption == "15:00") "15:00 (المقترح)" else timeOption,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }

                            Divider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.surfaceVariant)

                            // أذكار ما بعد الصلاة على الشاشة (مثل بعد 5 صباحاً)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "تذكير أذكار ما بعد الصلاة على الشاشة", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                    Text(text = "وقت الظهور: $postPrayerAthkarTime (مثل بعد 5:00 صباحاً بعد الفجر)", fontSize = 11.sp, color = EmeraldLight)
                                    val postPrayerSoundObj = adhanAudiosList.find { it.id == postPrayerAthkarSoundId }
                                    Text(
                                        text = "صوت التنبيه: ${postPrayerSoundObj?.nameAr ?: "تنبيه أذكار الصلاة"}",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    val postPrayerSoundObj = adhanAudiosList.find { it.id == postPrayerAthkarSoundId }
                                    IconButton(
                                        onClick = {
                                            val url = postPrayerSoundObj?.audioUrl ?: "https://cdn.aladhan.com/audio/adhans/athkar_morning.mp3"
                                            onTestAudio(url, postPrayerSoundObj?.nameAr ?: "أذكار ما بعد الصلاة")
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Filled.VolumeUp, contentDescription = "معاينة الصوت", tint = GoldAccent, modifier = Modifier.size(18.dp))
                                    }
                                    OutlinedButton(
                                        onClick = { showPostPrayerSoundPicker = true },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Icon(Icons.Filled.MusicNote, contentDescription = null, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text("تعديل الصوت", fontSize = 10.sp)
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))
                                    OutlinedButton(
                                        onClick = { showPostPrayerTimeDialog = true },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Icon(Icons.Filled.Schedule, contentDescription = null, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text("تعديل الوقت", fontSize = 10.sp)
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Switch(
                                        checked = postPrayerAthkarReminder,
                                        onCheckedChange = { onUpdatePostPrayerAthkar(it, postPrayerAthkarTime) }
                                    )
                                }
                            }

                            // Quick preset chips for Post Prayer Athkar
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "أوقات مقترحة سريعة لأذكار بعد الصلاة:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                listOf("05:00", "05:30", "06:00", "12:45", "15:45").forEach { timeOption ->
                                    val isSelected = postPrayerAthkarTime == timeOption
                                    Surface(
                                        onClick = { onUpdatePostPrayerAthkar(true, timeOption) },
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSelected) EmeraldLight else MaterialTheme.colorScheme.surfaceVariant
                                    ) {
                                        Text(
                                            text = if (timeOption == "05:00") "05:00 (بعد 5 ص المقترح)" else timeOption,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 4. الصلاة على النبي ﷺ دورياً مع تخصيص الصوت
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
                                    Text(
                                        text = "تذكير الصلاة على النبي ﷺ دورياً",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "تنبيه صوتي عذب بالصلاة والسلام على رسول الله ﷺ في فترات منتظمة",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                FilledTonalButton(
                                    onClick = { onPlaySalawatTest(salawatSoundType) },
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(Icons.Filled.VolumeUp, contentDescription = "تجربة الصوت الحالي", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("تجربة الصوت", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(text = "التكرار كل:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.padding(vertical = 6.dp)
                            ) {
                                listOf(1 to "1 د", 5 to "5 د", 10 to "10 د", 15 to "15 د", 30 to "30 د", 60 to "ساعة").forEach { (mins, label) ->
                                    val isSelected = salawatIntervalMinutes == mins
                                    Surface(
                                        onClick = { onUpdateSalawatSettings(mins, salawatSoundType) },
                                        shape = RoundedCornerShape(8.dp),
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

                            Divider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.surfaceVariant)

                            // Voice / Sound Customization for Salawat ("تخصيص صوت لتذكير الصلاة على النبي")
                            Text(
                                text = "اختر الصوت المفضل للتنبيه بالصلاة على النبي ﷺ:",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldAccent
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            val salawatVoiceOptions = listOf(
                                Triple("tts_calm", "صوت هادئ خاشع", "اللَّهُمَّ صَلِّ وَسَلِّمْ وَبَارِكْ عَلَى نَبِيِّنَا مُحَمَّدٍ ﷺ"),
                                Triple("tts_reverent", "صوت وقور مبجل مع الآل والصحب", "اللَّهُمَّ صَلِّ وَسَلِّمْ عَلَى سَيِّدِنَا مُحَمَّدٍ وَعَلَى آلِهِ وَصَحْبِهِ أَجْمَعِينَ"),
                                Triple("tts_ibrahimi", "الصيغة الإبراهيمية المباركة", "اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ وَعَلَى آلِ مُحَمَّدٍ كَمَا صَلَّيْتَ عَلَى إِبْرَاهِيمَ"),
                                Triple("voice_haram_makkah", "صوت الحرم المكي الشريف", "الصَّلَاةُ وَالسَّلَامُ عَلَيْكَ يَا رَسُولَ اللهِ بنبرة الحرم المكي"),
                                Triple("voice_madinah_nabawi", "صوت المسجد النبوي الشريف", "يَا طَيِّبَةَ الْخَيْرِ، صَلَوَاتُ رَبِّي وَسَلَامُهُ عَلَى نَبِيِّ الرَّحْمَةِ"),
                                Triple("chime_gentle", "نغمة أثيرية إسلامية هادئة", "رنين نقي متناغم ومريح للقلب"),
                                Triple("chime_madinah", "رنين مقامات روحانية", "نغمات وترية مستوحاة من سكينة المدينة المنورة")
                            )

                            salawatVoiceOptions.forEach { (soundKey, soundTitle, soundDesc) ->
                                val isSelected = salawatSoundType == soundKey
                                Surface(
                                    onClick = { onUpdateSalawatSettings(salawatIntervalMinutes, soundKey) },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    border = if (isSelected) BorderStroke(1.2.dp, MaterialTheme.colorScheme.primary) else null,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 10.dp, vertical = 8.dp),
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
                                                onClick = { onUpdateSalawatSettings(salawatIntervalMinutes, soundKey) },
                                                colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                                            )
                                            Column {
                                                Text(
                                                    text = soundTitle,
                                                    fontSize = 12.sp,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                                )
                                                Text(
                                                    text = soundDesc,
                                                    fontSize = 10.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }

                                        IconButton(
                                            onClick = { onPlaySalawatTest(soundKey) },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.PlayArrow,
                                                contentDescription = "استماع للصوت",
                                                tint = GoldAccent,
                                                modifier = Modifier.size(20.dp)
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

        // TAB 2: دقائق المواقيت (Prayer Time Adjustments)
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
}
