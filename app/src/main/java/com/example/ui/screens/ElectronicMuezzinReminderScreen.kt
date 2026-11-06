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
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.GoldAccent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class AlertDetailType {
    DUA_BEFORE,
    ADHAN,
    IQAMA_AFTER,
    ATHKAR_AFTER,
    SUHUR_ALARM
}

data class PrayerAlertItemState(
    val prayerName: String,
    val duaEnabled: Boolean = false,
    val duaMinutes: Int = 5,
    val adhanEnabled: Boolean = true,
    val iqamaEnabled: Boolean = false,
    val iqamaMinutes: Int = 5,
    val athkarEnabled: Boolean = true,
    val athkarMinutes: Int = 5,
    val selectedDays: Set<Int> = setOf(1, 2, 3, 4, 5, 6, 7),
    val volume: Float = 0.8f,
    val soundName: String = "افتراضي"
)

@Composable
fun ElectronicMuezzinReminderSection(
    postPrayerDelayMinutes: Int,
    onUpdatePostPrayerDelay: (Int) -> Unit,
    onPlayTestSound: (String) -> Unit = {},
    onSendCenterNotification: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var activeDetail by remember { mutableStateOf<Pair<String, AlertDetailType>?>(null) }

    // Map of prayer alerts state
    var prayerStates by remember {
        mutableStateOf(
            mapOf(
                "الفجر" to PrayerAlertItemState("الفجر", duaEnabled = false, duaMinutes = 5, adhanEnabled = true, iqamaEnabled = false, iqamaMinutes = 5, athkarEnabled = true, athkarMinutes = postPrayerDelayMinutes, soundName = "أذان الفجر"),
                "الظهر" to PrayerAlertItemState("الظهر", duaEnabled = false, duaMinutes = 5, adhanEnabled = true, iqamaEnabled = false, iqamaMinutes = 5, athkarEnabled = true, athkarMinutes = postPrayerDelayMinutes, soundName = "أذان الظهر"),
                "العصر" to PrayerAlertItemState("العصر", duaEnabled = false, duaMinutes = 5, adhanEnabled = true, iqamaEnabled = false, iqamaMinutes = 5, athkarEnabled = true, athkarMinutes = postPrayerDelayMinutes, soundName = "أذان العصر"),
                "المغرب" to PrayerAlertItemState("المغرب", duaEnabled = false, duaMinutes = 5, adhanEnabled = true, iqamaEnabled = false, iqamaMinutes = 5, athkarEnabled = true, athkarMinutes = postPrayerDelayMinutes, soundName = "أذان المغرب"),
                "العشاء" to PrayerAlertItemState("العشاء", duaEnabled = false, duaMinutes = 5, adhanEnabled = true, iqamaEnabled = false, iqamaMinutes = 5, athkarEnabled = true, athkarMinutes = postPrayerDelayMinutes, soundName = "أذان العشاء")
            )
        )
    }

    // Suhur state
    var suhurEnabled by remember { mutableStateOf(false) }
    var suhurMinsBefore by remember { mutableStateOf(60) }
    var suhurPeriod by remember { mutableStateOf("رمضان 1-30") }
    var suhurSound by remember { mutableStateOf("تكبير") }
    var suhurVolume by remember { mutableStateOf(0.85f) }

    val prayersList = listOf("الفجر", "الظهر", "العصر", "المغرب", "العشاء")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("electronic_muezzin_reminder_list"),
        contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Banner info
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Filled.NotificationsActive, contentDescription = null, tint = GoldAccent)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "شاشة التذكير والتنبيه • نمط المؤذن الإلكتروني",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "اضغط على أي تنبيه لتعديل الوقت (5 أو 10 دقائق إلخ)، الأيام، والصوت",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = onSendCenterNotification,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldLight),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Filled.Notifications, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("إرسال إشعار فوري في مركز الإشعارات 🔔", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 5 Prayers matching Image 6
        items(prayersList) { prayerName ->
            val pState = prayerStates[prayerName] ?: PrayerAlertItemState(prayerName)

            PrayerReminderRowCard(
                prayerName = prayerName,
                duaText = if (pState.duaEnabled) "الدعاء ${pState.duaMinutes} دقيقة قبل" else "الدعاء قبل الأذان\n[موقوف]",
                isDuaActive = pState.duaEnabled,
                onDuaClick = { activeDetail = prayerName to AlertDetailType.DUA_BEFORE },
                adhanText = if (pState.adhanEnabled) "الأذان\n[مفعل]" else "الأذان\n[موقوف]",
                isAdhanActive = pState.adhanEnabled,
                onAdhanClick = { activeDetail = prayerName to AlertDetailType.ADHAN },
                iqamaText = if (pState.iqamaEnabled) "الإقامة ${pState.iqamaMinutes} دقيقة بعد" else "الإقامة بعد الأذان\n[موقوف]",
                isIqamaActive = pState.iqamaEnabled,
                onIqamaClick = { activeDetail = prayerName to AlertDetailType.IQAMA_AFTER },
                athkarText = if (pState.athkarEnabled) "تذكير بأذكار بعد الصلاة\n[بعد ${pState.athkarMinutes} دقائق]" else "تذكير بأذكار بعد الصلاة\n[موقوف]",
                isAthkarActive = pState.athkarEnabled,
                onAthkarClick = { activeDetail = prayerName to AlertDetailType.ATHKAR_AFTER }
            )
        }

        // Section for Fasting & Suhur (Image 2)
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "الصيام / السحور",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Icon(Icons.Filled.NightsStay, contentDescription = null, tint = GoldAccent)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Grid of alarms matching Image 2
                    val suhurOptions = listOf(
                        "إضافة منبه - السحور" to true,
                        "منبه صيام رمضان" to false,
                        "صيام الإثنين والخميس" to false,
                        "صيام الأيام البيض" to false,
                        "إضافة منبه - الإفطار" to false,
                        "منبه صيام عاشوراء" to false,
                        "منبه صيام عرفة" to false,
                        "منبه الست من شوال" to false
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            SuhurQuickButton(
                                title = "إضافة منبه - السحور",
                                subtitle = if (suhurEnabled) "الفجر - $suhurMinsBefore دقيقة" else "موقوف",
                                isActive = suhurEnabled,
                                modifier = Modifier.weight(1f),
                                onClick = { activeDetail = "السحور" to AlertDetailType.SUHUR_ALARM }
                            )
                            SuhurQuickButton(
                                title = "منبه صيام رمضان",
                                subtitle = "ليالي رمضان المبارك",
                                isActive = false,
                                modifier = Modifier.weight(1f),
                                onClick = { activeDetail = "السحور" to AlertDetailType.SUHUR_ALARM }
                            )
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            SuhurQuickButton(
                                title = "صيام الإثنين والخميس",
                                subtitle = "سنة المصطفى ﷺ",
                                isActive = false,
                                modifier = Modifier.weight(1f),
                                onClick = { activeDetail = "السحور" to AlertDetailType.SUHUR_ALARM }
                            )
                            SuhurQuickButton(
                                title = "صيام الأيام البيض",
                                subtitle = "13 و 14 و 15 هجري",
                                isActive = false,
                                modifier = Modifier.weight(1f),
                                onClick = { activeDetail = "السحور" to AlertDetailType.SUHUR_ALARM }
                            )
                        }
                    }
                }
            }
        }
    }

    // Detail Dialogs matching Images 1, 3, 4, 5
    if (activeDetail != null) {
        val (targetName, alertType) = activeDetail!!
        val currentState = prayerStates[targetName] ?: PrayerAlertItemState(targetName)

        when (alertType) {
            AlertDetailType.DUA_BEFORE -> {
                // Image 1 (الفجر - الدعاء)
                DuaBeforeDetailDialog(
                    prayerName = targetName,
                    minutesBefore = currentState.duaMinutes,
                    isEnabled = currentState.duaEnabled,
                    selectedDays = currentState.selectedDays,
                    volume = currentState.volume,
                    soundName = currentState.soundName,
                    onDismiss = { activeDetail = null },
                    onSave = { mins, enabled, days, vol, snd ->
                        prayerStates = prayerStates + (targetName to currentState.copy(
                            duaMinutes = mins,
                            duaEnabled = enabled,
                            selectedDays = days,
                            volume = vol,
                            soundName = snd
                        ))
                        activeDetail = null
                    },
                    onPlaySound = { onPlayTestSound("dua") }
                )
            }

            AlertDetailType.ADHAN -> {
                // Image 3 (الفجر - الأذان)
                AdhanDetailDialog(
                    prayerName = targetName,
                    isEnabled = currentState.adhanEnabled,
                    selectedDays = currentState.selectedDays,
                    volume = currentState.volume,
                    soundName = currentState.soundName,
                    onDismiss = { activeDetail = null },
                    onSave = { enabled, days, vol, snd ->
                        prayerStates = prayerStates + (targetName to currentState.copy(
                            adhanEnabled = enabled,
                            selectedDays = days,
                            volume = vol,
                            soundName = snd
                        ))
                        activeDetail = null
                    },
                    onPlaySound = { onPlayTestSound("adhan") }
                )
            }

            AlertDetailType.IQAMA_AFTER -> {
                // Image 4 (الفجر - الإقامة)
                IqamaDetailDialog(
                    prayerName = targetName,
                    minutesAfter = currentState.iqamaMinutes,
                    isEnabled = currentState.iqamaEnabled,
                    selectedDays = currentState.selectedDays,
                    volume = currentState.volume,
                    soundName = currentState.soundName,
                    onDismiss = { activeDetail = null },
                    onSave = { mins, enabled, days, vol, snd ->
                        prayerStates = prayerStates + (targetName to currentState.copy(
                            iqamaMinutes = mins,
                            iqamaEnabled = enabled,
                            selectedDays = days,
                            volume = vol,
                            soundName = snd
                        ))
                        activeDetail = null
                    },
                    onPlaySound = { onPlayTestSound("iqama") }
                )
            }

            AlertDetailType.ATHKAR_AFTER -> {
                // تذكير بأذكار بعد الصلاة (اختياري 5 أو 10 دقائق إلخ)
                AthkarAfterDetailDialog(
                    prayerName = targetName,
                    minutesAfter = currentState.athkarMinutes,
                    isEnabled = currentState.athkarEnabled,
                    selectedDays = currentState.selectedDays,
                    volume = currentState.volume,
                    soundName = currentState.soundName,
                    onDismiss = { activeDetail = null },
                    onSave = { mins, enabled, days, vol, snd ->
                        onUpdatePostPrayerDelay(mins)
                        prayerStates = prayerStates + (targetName to currentState.copy(
                            athkarMinutes = mins,
                            athkarEnabled = enabled,
                            selectedDays = days,
                            volume = vol,
                            soundName = snd
                        ))
                        activeDetail = null
                    },
                    onPlaySound = { onPlayTestSound("athkar") }
                )
            }

            AlertDetailType.SUHUR_ALARM -> {
                // Image 5 (إضافة منبه - السحور)
                SuhurAlarmDetailDialog(
                    minutesBefore = suhurMinsBefore,
                    period = suhurPeriod,
                    isEnabled = suhurEnabled,
                    volume = suhurVolume,
                    soundName = suhurSound,
                    onDismiss = { activeDetail = null },
                    onSave = { mins, period, enabled, vol, snd ->
                        suhurMinsBefore = mins
                        suhurPeriod = period
                        suhurEnabled = enabled
                        suhurVolume = vol
                        suhurSound = snd
                        activeDetail = null
                    },
                    onPlaySound = { onPlayTestSound("takbeer") }
                )
            }
        }
    }
}

// Prayer row matching Image 6
@Composable
fun PrayerReminderRowCard(
    prayerName: String,
    duaText: String,
    isDuaActive: Boolean,
    onDuaClick: () -> Unit,
    adhanText: String,
    isAdhanActive: Boolean,
    onAdhanClick: () -> Unit,
    iqamaText: String,
    isIqamaActive: Boolean,
    onIqamaClick: () -> Unit,
    athkarText: String,
    isAthkarActive: Boolean,
    onAthkarClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.15f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header with prayer name
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left callout badge: تذكير بأذكار بعد الصلاة
                Surface(
                    onClick = onAthkarClick,
                    shape = RoundedCornerShape(18.dp),
                    color = if (isAthkarActive) GoldAccent.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, if (isAthkarActive) GoldAccent else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Filled.ChatBubbleOutline, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                        Text(
                            text = athkarText.replace("\n", " "),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Text(
                    text = prayerName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.15f))
            Spacer(modifier = Modifier.height(10.dp))

            // 3 items row: الدعاء قبل الأذان | الأذان | الإقامة بعد الأذان
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // الدعاء قبل الأذان
                ReminderColumnButton(
                    title = duaText,
                    isActive = isDuaActive,
                    onClick = onDuaClick,
                    modifier = Modifier.weight(1f)
                )

                // الأذان
                ReminderColumnButton(
                    title = adhanText,
                    isActive = isAdhanActive,
                    onClick = onAdhanClick,
                    modifier = Modifier.weight(1f)
                )

                // الإقامة بعد الأذان
                ReminderColumnButton(
                    title = iqamaText,
                    isActive = isIqamaActive,
                    onClick = onIqamaClick,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ReminderColumnButton(
    title: String,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = if (isActive) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = BorderStroke(1.dp, if (isActive) MaterialTheme.colorScheme.primary else Color.Transparent),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                textAlign = TextAlign.Center,
                color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 15.sp
            )
        }
    }
}

@Composable
private fun SuhurQuickButton(
    title: String,
    subtitle: String,
    isActive: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (isActive) EmeraldLight.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, if (isActive) EmeraldLight else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtitle, fontSize = 10.sp, color = if (isActive) EmeraldLight else MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

// -------------------------------------------------------------
// DETAIL DIALOGS (Matching Images 1, 3, 4, 5)
// -------------------------------------------------------------

// Image 1: الفجر - الدعاء
@Composable
fun DuaBeforeDetailDialog(
    prayerName: String,
    minutesBefore: Int,
    isEnabled: Boolean,
    selectedDays: Set<Int>,
    volume: Float,
    soundName: String,
    onDismiss: () -> Unit,
    onSave: (Int, Boolean, Set<Int>, Float, String) -> Unit,
    onPlaySound: () -> Unit
) {
    var mins by remember { mutableStateOf(minutesBefore) }
    var active by remember { mutableStateOf(isEnabled) }
    var days by remember { mutableStateOf(selectedDays) }
    var vol by remember { mutableStateOf(volume) }
    var sound by remember { mutableStateOf(soundName) }

    val currentTime = remember { SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth(0.95f),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = currentTime, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = "$prayerName - الدعاء", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Divider(color = MaterialTheme.colorScheme.primary, thickness = 2.dp)

                // Dropdown indicator
                Text(text = "▼ عند الأذان / قبل", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)

                // Big number display (5 دقيقة قبل)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "قبل  ", fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = "$mins دقيقة", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                }

                // Quick minute chips: 5, 10, 15, 20
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(3, 5, 10, 15, 20).forEach { m ->
                        val isSel = mins == m
                        Surface(
                            onClick = { mins = m },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "$m د",
                                fontSize = 11.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSel) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                        }
                    }
                }

                // Weekdays row
                WeekdaysSelectorRow(
                    selectedDays = days,
                    onToggleDay = { dayIndex ->
                        days = if (days.contains(dayIndex)) days - dayIndex else days + dayIndex
                    }
                )

                // Switch row: الدعاء 5 دقيقة قبل الأذان [كل يوم]
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Switch(checked = active, onCheckedChange = { active = it })
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "الدعاء $mins دقيقة قبل الأذان", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text(text = if (days.size == 7) "[كل يوم]" else "[أيام محددة]", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                // Volume slider with speaker icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Filled.VolumeUp, contentDescription = null, tint = GoldAccent)
                    Slider(
                        value = vol,
                        onValueChange = { vol = it },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Sound selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onPlaySound) {
                        Icon(Icons.Filled.PlayCircle, contentDescription = "تشغيل", tint = GoldAccent)
                    }
                    Text(text = "صوت: دعاء", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                }
            }
        },
        confirmButton = {
            Button(onClick = { onSave(mins, active, days, vol, sound) }) {
                Text("حفظ")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}

// Image 3: الفجر - الأذان
@Composable
fun AdhanDetailDialog(
    prayerName: String,
    isEnabled: Boolean,
    selectedDays: Set<Int>,
    volume: Float,
    soundName: String,
    onDismiss: () -> Unit,
    onSave: (Boolean, Set<Int>, Float, String) -> Unit,
    onPlaySound: () -> Unit
) {
    var active by remember { mutableStateOf(isEnabled) }
    var days by remember { mutableStateOf(selectedDays) }
    var vol by remember { mutableStateOf(volume) }
    var sound by remember { mutableStateOf(soundName) }

    val currentTime = remember { SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth(0.95f),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = currentTime, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = "$prayerName - الأذان", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Divider(color = MaterialTheme.colorScheme.primary, thickness = 2.dp)

                Text(text = "▼ عند الأذان", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "عند  ", fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = "0 دقيقة", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                }

                WeekdaysSelectorRow(
                    selectedDays = days,
                    onToggleDay = { dayIndex ->
                        days = if (days.contains(dayIndex)) days - dayIndex else days + dayIndex
                    }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Switch(checked = active, onCheckedChange = { active = it })
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "الأذان", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text(text = if (days.size == 7) "[كل يوم]" else "[أيام محددة]", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Filled.VolumeUp, contentDescription = null, tint = GoldAccent)
                    Slider(
                        value = vol,
                        onValueChange = { vol = it },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onPlaySound) {
                        Icon(Icons.Filled.PlayCircle, contentDescription = "تشغيل", tint = GoldAccent)
                    }
                    Text(text = "صوت: $prayerName", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                }
            }
        },
        confirmButton = {
            Button(onClick = { onSave(active, days, vol, sound) }) {
                Text("حفظ")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}

// Image 4: الفجر - الإقامة
@Composable
fun IqamaDetailDialog(
    prayerName: String,
    minutesAfter: Int,
    isEnabled: Boolean,
    selectedDays: Set<Int>,
    volume: Float,
    soundName: String,
    onDismiss: () -> Unit,
    onSave: (Int, Boolean, Set<Int>, Float, String) -> Unit,
    onPlaySound: () -> Unit
) {
    var mins by remember { mutableStateOf(minutesAfter) }
    var active by remember { mutableStateOf(isEnabled) }
    var days by remember { mutableStateOf(selectedDays) }
    var vol by remember { mutableStateOf(volume) }
    var sound by remember { mutableStateOf(soundName) }

    val currentTime = remember { SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth(0.95f),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = currentTime, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = "$prayerName - الإقامة", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Divider(color = MaterialTheme.colorScheme.primary, thickness = 2.dp)

                Text(text = "▼ عند الأذان / بعد", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "بعد  ", fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = "$mins دقيقة", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(5, 10, 15, 20, 25).forEach { m ->
                        val isSel = mins == m
                        Surface(
                            onClick = { mins = m },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "$m د",
                                fontSize = 11.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSel) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                        }
                    }
                }

                WeekdaysSelectorRow(
                    selectedDays = days,
                    onToggleDay = { dayIndex ->
                        days = if (days.contains(dayIndex)) days - dayIndex else days + dayIndex
                    }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Switch(checked = active, onCheckedChange = { active = it })
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "الإقامة $mins دقيقة بعد الأذان", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text(text = if (days.size == 7) "[كل يوم]" else "[أيام محددة]", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Filled.VolumeUp, contentDescription = null, tint = GoldAccent)
                    Slider(
                        value = vol,
                        onValueChange = { vol = it },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onPlaySound) {
                        Icon(Icons.Filled.PlayCircle, contentDescription = "تشغيل", tint = GoldAccent)
                    }
                    Text(text = "صوت: الإقامة", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                }
            }
        },
        confirmButton = {
            Button(onClick = { onSave(mins, active, days, vol, sound) }) {
                Text("حفظ")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}

// Post-Prayer Athkar Detail Dialog (تذكير بأذكار بعد الصلاة - اختياري 5 أو 10 دقائق إلخ)
@Composable
fun AthkarAfterDetailDialog(
    prayerName: String,
    minutesAfter: Int,
    isEnabled: Boolean,
    selectedDays: Set<Int>,
    volume: Float,
    soundName: String,
    onDismiss: () -> Unit,
    onSave: (Int, Boolean, Set<Int>, Float, String) -> Unit,
    onPlaySound: () -> Unit
) {
    var mins by remember { mutableStateOf(minutesAfter) }
    var active by remember { mutableStateOf(isEnabled) }
    var days by remember { mutableStateOf(selectedDays) }
    var vol by remember { mutableStateOf(volume) }
    var sound by remember { mutableStateOf(soundName) }

    val currentTime = remember { SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth(0.95f),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = currentTime, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = "$prayerName - أذكار بعد الصلاة", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Divider(color = GoldAccent, thickness = 2.dp)

                Text(text = "▼ التذكير بأذكار بعد الصلاة (إختياري - دقائق وضغطات)", fontSize = 12.sp, color = GoldAccent, fontWeight = FontWeight.Bold)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "التنبيه بعد الصلاة بـ  ", fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = "$mins دقيقة", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = GoldAccent)
                }

                // Quick chips for delay: 5, 10, 15, 20, 30
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(5, 10, 15, 20, 30).forEach { m ->
                        val isSel = mins == m
                        Surface(
                            onClick = { mins = m },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSel) GoldAccent else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "$m دقيقة",
                                fontSize = 11.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSel) Color.Black else MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                        }
                    }
                }

                // Tasbih / Athkar Counter presses option (5 ضغطات أو 10 ضغطات إلخ)
                var tasbihClicks by remember { mutableStateOf(5) }
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "عدد الضغطات بعد الصلاة:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = "$tasbihClicks ضغطات", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EmeraldLight)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(5, 10, 33, 100).forEach { clicks ->
                            val isClickSel = tasbihClicks == clicks
                            Surface(
                                onClick = { tasbihClicks = clicks },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isClickSel) EmeraldLight.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                border = BorderStroke(1.dp, if (isClickSel) EmeraldLight else Color.Transparent),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "$clicks ضغط",
                                    fontSize = 11.sp,
                                    fontWeight = if (isClickSel) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isClickSel) EmeraldLight else MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 5.dp)
                                )
                            }
                        }
                    }
                }

                WeekdaysSelectorRow(
                    selectedDays = days,
                    onToggleDay = { dayIndex ->
                        days = if (days.contains(dayIndex)) days - dayIndex else days + dayIndex
                    }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Switch(checked = active, onCheckedChange = { active = it })
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "تذكير أذكار بعد الصلاة", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text(text = "بعد الصلاة بـ $mins دقائق [كل يوم]", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Filled.VolumeUp, contentDescription = null, tint = GoldAccent)
                    Slider(
                        value = vol,
                        onValueChange = { vol = it },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onPlaySound) {
                        Icon(Icons.Filled.PlayCircle, contentDescription = "تشغيل", tint = GoldAccent)
                    }
                    Text(text = "صوت: أذكار ما بعد الصلاة", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                }
            }
        },
        confirmButton = {
            Button(onClick = { onSave(mins, active, days, vol, sound) }) {
                Text("حفظ")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}

// Image 5: إضافة منبه - السحور
@Composable
fun SuhurAlarmDetailDialog(
    minutesBefore: Int,
    period: String,
    isEnabled: Boolean,
    volume: Float,
    soundName: String,
    onDismiss: () -> Unit,
    onSave: (Int, String, Boolean, Float, String) -> Unit,
    onPlaySound: () -> Unit
) {
    var mins by remember { mutableStateOf(minutesBefore) }
    var active by remember { mutableStateOf(isEnabled) }
    var pPeriod by remember { mutableStateOf(period) }
    var vol by remember { mutableStateOf(volume) }
    var sound by remember { mutableStateOf(soundName) }

    val currentTime = remember { SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth(0.95f),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = currentTime, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = "إضافة منبه - السحور", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Divider(color = MaterialTheme.colorScheme.primary, thickness = 2.dp)

                Text(text = "▼ الفجر", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "قبل  ", fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = "$mins دقيقة", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                }

                // Quick chips: 30, 45, 60, 90
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(30, 45, 60, 90).forEach { m ->
                        val isSel = mins == m
                        Surface(
                            onClick = { mins = m },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "$m د",
                                fontSize = 11.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSel) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                        }
                    }
                }

                // Period selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "الفترة: $pPeriod", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                    Text(text = "▼ رمضان 1-30", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Switch(checked = active, onCheckedChange = { active = it })
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "رمضان 1-30 السحور", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text(text = "الفجر - $mins دقيقة", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                // Note matching Image 5
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = "ملاحظة: ينطلق منبه السحور في ليلة (أي قبل) يوم الصيام الذي يتم اختياره هنا. يتوقف المنبه خلال العيد حتى لو كان فعالاً.",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 14.sp,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Filled.VolumeUp, contentDescription = null, tint = GoldAccent)
                    Slider(
                        value = vol,
                        onValueChange = { vol = it },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onPlaySound) {
                        Icon(Icons.Filled.PlayCircle, contentDescription = "تشغيل", tint = GoldAccent)
                    }
                    Text(text = "صوت: تكبير", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                }
            }
        },
        confirmButton = {
            Button(onClick = { onSave(mins, pPeriod, active, vol, sound) }) {
                Text("حفظ")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}

// Weekdays row matching Images 1, 3, 4
@Composable
private fun WeekdaysSelectorRow(
    selectedDays: Set<Int>,
    onToggleDay: (Int) -> Unit
) {
    val daysLabels = listOf(
        1 to "الأحد",
        2 to "الإثنين",
        3 to "الثلاثاء",
        4 to "الأربعاء",
        5 to "الخميس",
        6 to "الجمعة",
        7 to "السبت"
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        daysLabels.forEach { (index, name) ->
            val isSelected = selectedDays.contains(index)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onToggleDay(index) }
                    .padding(vertical = 4.dp, horizontal = 2.dp)
            ) {
                Text(
                    text = name,
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(2.dp))
                // Underline indicator matching images
                Box(
                    modifier = Modifier
                        .width(24.dp)
                        .height(2.dp)
                        .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                )
            }
        }
    }
}
