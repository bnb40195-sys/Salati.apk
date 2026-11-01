package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.CityLocation
import com.example.data.models.DayPrayerTimes
import com.example.data.models.PrayerType
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.GoldAccent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrayerAdjustmentScreen(
    selectedCity: CityLocation,
    prayerTimes: DayPrayerTimes?,
    prayerOffsets: Map<PrayerType, Int>,
    salawatIntervalMinutes: Int,
    salawatSoundType: String,
    prePrayerMinutesBefore: Int,
    prePrayerSoundType: String,
    prePrayerBigMessage: Boolean,
    onSetOffset: (PrayerType, Int) -> Unit,
    onResetOffsets: () -> Unit,
    onUpdateSalawatInterval: (Int) -> Unit,
    onUpdateSalawatSoundType: (String) -> Unit,
    onUpdatePrePrayerSettings: (Boolean, Int, String) -> Unit,
    onPlayPrePrayerTest: (String) -> Unit,
    onPlaySalawatTest: (String) -> Unit,
    onOpenPostPrayerAthkar: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val timeFormatter = remember { SimpleDateFormat("hh:mm a", Locale("ar")) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "تعديل مواقيت الصلاة والتذكيرات",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("prayer_adjust_back")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = onResetOffsets) {
                        Text("إعادة الضبط", color = GoldAccent, fontSize = 12.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 14.dp, horizontal = 0.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Description header
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Filled.Tune, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Column {
                            Text(
                                text = "تعديل الدقائق يدوياً لكل صلاة",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "المدينة: ${selectedCity.nameAr} • يمكنك تقديم أو تأخير كل صلاة حتى تطابق توقيت مسجد حيك بدقة.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Prayer Offsets Cards
            item {
                Text(
                    text = "دقة المواقيت بالدقائق (+ / -)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            val prayers = listOf(
                PrayerType.FAJR to (prayerTimes?.fajr ?: 0L),
                PrayerType.SUNRISE to (prayerTimes?.sunrise ?: 0L),
                PrayerType.DHUHR to (prayerTimes?.dhuhr ?: 0L),
                PrayerType.ASR to (prayerTimes?.asr ?: 0L),
                PrayerType.MAGHRIB to (prayerTimes?.maghrib ?: 0L),
                PrayerType.ISHA to (prayerTimes?.isha ?: 0L)
            )

            items(prayers.size) { idx ->
                val (type, baseTime) = prayers[idx]
                val offset = prayerOffsets[type] ?: 0
                val adjustedTime = if (baseTime > 0) baseTime + (offset * 60 * 1000L) else 0L

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("prayer_adjust_card_${type.name}"),
                    shape = RoundedCornerShape(14.dp),
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
                            Text(
                                text = type.arName,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (adjustedTime > 0) timeFormatter.format(Date(adjustedTime)) else "--:--",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Adjustment Controls (- / offset / +)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilledIconButton(
                                onClick = { onSetOffset(type, offset - 1) },
                                modifier = Modifier.size(34.dp),
                                colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Icon(Icons.Filled.Remove, contentDescription = "Decrease", modifier = Modifier.size(16.dp))
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (offset != 0) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.widthIn(min = 48.dp)
                            ) {
                                Text(
                                    text = if (offset > 0) "+$offset د" else "$offset د",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (offset != 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                                )
                            }

                            FilledIconButton(
                                onClick = { onSetOffset(type, offset + 1) },
                                modifier = Modifier.size(34.dp),
                                colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(Icons.Filled.Add, contentDescription = "Increase", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onPrimary)
                            }
                        }
                    }
                }
            }

            // Section 2: Pre-Prayer Voice Reminder
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pre_prayer_voice_settings_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "تذكير قبل الصلاة بالصوت القرآني",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "﴿ إِنَّ الصَّلَاةَ كَانَتْ عَلَى الْمُؤْمِنِينَ كِتَابًا مَّوْقُوتًا ﴾ صدق الله العظيم",
                                    fontSize = 12.sp,
                                    color = GoldAccent,
                                    lineHeight = 18.sp
                                )
                            }
                            Switch(
                                checked = prePrayerBigMessage,
                                onCheckedChange = { onUpdatePrePrayerSettings(it, prePrayerMinutesBefore, prePrayerSoundType) },
                                modifier = Modifier.testTag("pre_prayer_toggle")
                            )
                        }

                        if (prePrayerBigMessage) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "وقت التنبيه قبل الصلاة:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(5, 10, 15, 20).forEach { mins ->
                                    val isSelected = prePrayerMinutesBefore == mins
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { onUpdatePrePrayerSettings(true, mins, prePrayerSoundType) },
                                        label = { Text("قبل $mins د", fontSize = 11.sp) },
                                        modifier = Modifier.testTag("pre_prayer_min_$mins")
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Test Audio Button
                            OutlinedButton(
                                onClick = { onPlayPrePrayerTest(prePrayerSoundType) },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("test_pre_prayer_audio_button")
                            ) {
                                Icon(Icons.Filled.VolumeUp, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "استمع لصوت التذكير قبل الصلاة الآن",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Section 3: Salawat on Prophet (every 5 min choice & voice selection)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("salawat_voice_settings_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "تذكير الصلاة على النبي ﷺ",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "اللَّهُمَّ صَلِّ وَسَلِّمْ عَلَى نَبِيِّنَا مُحَمَّدٍ",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            // Quick play button
                            IconButton(onClick = { onPlaySalawatTest(salawatSoundType) }) {
                                Icon(Icons.Filled.PlayCircle, contentDescription = "Play", tint = MaterialTheme.colorScheme.primary)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "تكرار التذكير:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Intervals including "كل 5 دقائق"
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                1 to "دقيقة",
                                5 to "5 دقائق",
                                10 to "10 د",
                                15 to "15 د",
                                30 to "30 د",
                                60 to "ساعة"
                            ).forEach { (m, label) ->
                                val isSelected = salawatIntervalMinutes == m
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { onUpdateSalawatInterval(m) },
                                    label = { Text(label, fontSize = 11.sp) },
                                    modifier = Modifier.testTag("salawat_interval_$m")
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "اختيار الصوت وتغييره:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        val sounds = listOf(
                            "tts_calm" to "التلاوة الذكية الهادئة",
                            "tts_reverent" to "صوت خاشع وندي",
                            "chime_madinah" to "نغمة الروضة الشريفة",
                            "chime_gentle" to "رنين إسلامي عذب",
                            "vibrate_only" to "اهتزاز لطيف"
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            sounds.forEach { (type, name) ->
                                val isSelected = salawatSoundType == type
                                Surface(
                                    onClick = {
                                        onUpdateSalawatSoundType(type)
                                        onPlaySalawatTest(type)
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = name,
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                        )
                                        if (isSelected) {
                                            Icon(Icons.Filled.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Section 4: Post-Prayer Adhkar
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("post_prayer_athkar_card"),
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
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "أذكار ما بعد الأذان والصلاة",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "الاستغفار، آية الكرسي، التسبيحات، ودعاء الوسيلة",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Button(
                            onClick = onOpenPostPrayerAthkar,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("open_post_prayer_athkar_button")
                        ) {
                            Text("فتح الأذكار", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
