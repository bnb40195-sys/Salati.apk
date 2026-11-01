package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.PrayerType
import com.example.data.services.PlaybackState
import com.example.ui.components.IslamicPatternBackground
import com.example.ui.theme.EmeraldCardDark
import com.example.ui.theme.GoldAccent

@Composable
fun AdhanScreen(
    playbackState: PlaybackState = PlaybackState(),
    selectedMuadhinId: String = "makkah",
    repeatCount: Int = 1,
    preAdhanSound: Boolean = true,
    preAdhanMinutes: Int = 10,
    atAdhanSound: Boolean = true,
    bigMessageAtAdhan: Boolean = true,
    fridayFirstAdhan: Boolean = true,
    fridaySecondAdhan: Boolean = true,
    fridayMinutesBefore: Int = 20,
    customPrayerAdhans: Map<PrayerType, String> = emptyMap(),
    muadhinList: List<Triple<String, String, String>> = emptyList(),
    onSelectMuadhin: (String) -> Unit = {},
    onTestAudio: () -> Unit = {},
    onSetRepeatCount: (Int) -> Unit = {},
    onSetAdhanOptions: (Int, Boolean, Int, Boolean, Boolean) -> Unit = { _, _, _, _, _ -> },
    onSetFridayOptions: (Boolean, Boolean, Int) -> Unit = { _, _, _ -> },
    onSetCustomPrayerAdhan: (PrayerType, String) -> Unit = { _, _ -> },
    onOpenCreatorStudio: () -> Unit = {},
    prayerTimes: com.example.data.models.DayPrayerTimes? = null,
    onTogglePlayPause: () -> Unit = {},
    onStopAudio: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    IslamicPatternBackground(modifier = modifier) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .testTag("adhan_screen"),
            contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "الأذان",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldAccent
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "اختر المؤذن وعدد التكرار والتنبيهات الصوتية",
                        fontSize = 12.sp,
                        color = Color(0xFF9CA3AF)
                    )
                }
            }

            // Card 1: اختيار المؤذن
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = EmeraldCardDark),
                    border = BorderStroke(1.dp, Color(0xFF1B4336))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Filled.VolumeUp, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(20.dp))
                            Text(
                                text = "اختيار المؤذن",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldAccent
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Muadhin List
                        muadhinList.forEach { (id, name, _) ->
                            val isSelected = id == selectedMuadhinId
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { onSelectMuadhin(id) },
                                color = if (isSelected) Color(0xFF263D2E) else Color(0xFF0F261E),
                                shape = RoundedCornerShape(12.dp),
                                border = if (isSelected) BorderStroke(1.dp, GoldAccent.copy(alpha = 0.6f)) else null
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = name,
                                        fontSize = 14.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) GoldAccent else Color(0xFFE5E7EB)
                                    )
                                    if (isSelected) {
                                        Text(text = "✓", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Play Audio Test Button
                        Button(
                            onClick = onTestAudio,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("test_adhan_audio_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GoldAccent,
                                contentColor = Color(0xFF111827)
                            )
                        ) {
                            Icon(
                                imageVector = if (playbackState.isPlaying) Icons.Filled.Stop else Icons.Filled.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (playbackState.isPlaying) "إيقاف الصوت" else "تجربة الصوت",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Card 2: صوت مخصص لكل صلاة (اختياري)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = EmeraldCardDark),
                    border = BorderStroke(1.dp, Color(0xFF1B4336))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Filled.VolumeUp, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(20.dp))
                            Text(
                                text = "صوت مخصص لكل صلاة (اختياري)",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldAccent
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "اختر مؤذناً مختلفاً لأي صلاة - إن تُرِك «افتراضي» يُستخدم صوت المؤذن المختار أعلاه.",
                            fontSize = 11.sp,
                            color = Color(0xFF9CA3AF)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        val prayersWithFriday = listOf(
                            Pair(PrayerType.FAJR, "الفجر"),
                            Pair(PrayerType.DHUHR, "الظهر"),
                            Pair(PrayerType.ASR, "العصر"),
                            Pair(PrayerType.MAGHRIB, "المغرب"),
                            Pair(PrayerType.ISHA, "العشاء")
                        )

                        prayersWithFriday.forEach { (prayerType, prayerName) ->
                            var expanded by remember { mutableStateOf(false) }
                            val currentChoice = customPrayerAdhans[prayerType] ?: "default"
                            val label = if (currentChoice == "default") "افتراضي (صوت المؤذن)" else {
                                muadhinList.find { it.first == currentChoice }?.second ?: "افتراضي (صوت المؤذن)"
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = prayerName, fontSize = 13.sp, color = Color(0xFFE5E7EB), fontWeight = FontWeight.Medium)

                                Box {
                                    Surface(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable { expanded = true },
                                        color = Color(0xFF0F261E),
                                        shape = RoundedCornerShape(8.dp),
                                        border = BorderStroke(1.dp, Color(0xFF1F4336))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(text = label, fontSize = 11.sp, color = Color(0xFFE5E7EB))
                                            Icon(Icons.Filled.ArrowDropDown, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                                        }
                                    }

                                    DropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false }
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text("افتراضي (صوت المؤذن)") },
                                            onClick = {
                                                onSetCustomPrayerAdhan(prayerType, "default")
                                                expanded = false
                                            }
                                        )
                                        muadhinList.forEach { (mId, mName, _) ->
                                            DropdownMenuItem(
                                                text = { Text(mName) },
                                                onClick = {
                                                    onSetCustomPrayerAdhan(prayerType, mId)
                                                    expanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Card 3: عدد مرات التكرار
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = EmeraldCardDark),
                    border = BorderStroke(1.dp, Color(0xFF1B4336))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Filled.Sync, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(20.dp))
                            Text(text = "عدد مرات التكرار", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE5E7EB))
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            val options = listOf(Pair(1, "مرة واحدة"), Pair(2, "مرتان"), Pair(3, "ثلاث مرات"))
                            options.forEach { (cnt, title) ->
                                val isSelected = repeatCount == cnt
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable {
                                            onSetRepeatCount(cnt)
                                            onSetAdhanOptions(cnt, preAdhanSound, preAdhanMinutes, atAdhanSound, bigMessageAtAdhan)
                                        },
                                    color = if (isSelected) Color(0xFF263D2E) else Color(0xFF0F261E),
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, if (isSelected) GoldAccent else Color(0xFF1F4336))
                                ) {
                                    Text(
                                        text = title,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) GoldAccent else Color(0xFF9CA3AF),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Card 4: إشعار صوتي قبل الأذان
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = EmeraldCardDark),
                    border = BorderStroke(1.dp, Color(0xFF1B4336))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Filled.NotificationsNone, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(20.dp))
                                Text(text = "إشعار صوتي قبل الأذان", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE5E7EB))
                            }
                            Switch(
                                checked = preAdhanSound,
                                onCheckedChange = {
                                    onSetAdhanOptions(repeatCount, it, preAdhanMinutes, atAdhanSound, bigMessageAtAdhan)
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = GoldAccent
                                )
                            )
                        }

                        if (preAdhanSound) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(Icons.Filled.Schedule, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                                    Text(text = "قبل الأذان بـ (دقائق)", fontSize = 12.sp, color = Color(0xFF9CA3AF))
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF0F261E),
                                    border = BorderStroke(1.dp, Color(0xFF1F4336))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        TextButton(onClick = {
                                            if (preAdhanMinutes > 1) onSetAdhanOptions(repeatCount, preAdhanSound, preAdhanMinutes - 1, atAdhanSound, bigMessageAtAdhan)
                                        }) { Text("-", fontSize = 16.sp, color = GoldAccent) }
                                        Text(text = "$preAdhanMinutes", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE5E7EB))
                                        TextButton(onClick = {
                                            if (preAdhanMinutes < 60) onSetAdhanOptions(repeatCount, preAdhanSound, preAdhanMinutes + 1, atAdhanSound, bigMessageAtAdhan)
                                        }) { Text("+", fontSize = 16.sp, color = GoldAccent) }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Card 5: إشعار صوتي عند الأذان
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = EmeraldCardDark),
                    border = BorderStroke(1.dp, Color(0xFF1B4336))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Filled.VolumeUp, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(20.dp))
                            Text(text = "إشعار صوتي عند الأذان", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE5E7EB))
                        }
                        Switch(
                            checked = atAdhanSound,
                            onCheckedChange = {
                                onSetAdhanOptions(repeatCount, preAdhanSound, preAdhanMinutes, it, bigMessageAtAdhan)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = GoldAccent
                            )
                        )
                    }
                }
            }

            // Card 6: رسالة كبيرة عند الأذان
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = EmeraldCardDark),
                    border = BorderStroke(1.dp, Color(0xFF1B4336))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "رسالة كبيرة عند الأذان",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE5E7EB)
                            )
                            Switch(
                                checked = bigMessageAtAdhan,
                                onCheckedChange = {
                                    onSetAdhanOptions(repeatCount, preAdhanSound, preAdhanMinutes, atAdhanSound, it)
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = GoldAccent
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "عرض شاشة الأذان كاملة (نص الأذان ودعاؤه) عند كل صلاة – أوقفه ليظهر تنبيه صغير فقط مع الصوت.",
                            fontSize = 11.sp,
                            color = Color(0xFF9CA3AF)
                        )
                    }
                }
            }

            // Card 7: أذان الجمعة
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = EmeraldCardDark),
                    border = BorderStroke(1.dp, Color(0xFF1B4336))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "أذان الجمعة",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldAccent
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "الأذان الأول للجمعة", fontSize = 13.sp, color = Color(0xFFE5E7EB))
                            Switch(
                                checked = fridayFirstAdhan,
                                onCheckedChange = {
                                    onSetFridayOptions(it, fridaySecondAdhan, fridayMinutesBefore)
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = GoldAccent
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "الأذان الثاني للجمعة", fontSize = 13.sp, color = Color(0xFFE5E7EB))
                            Switch(
                                checked = fridaySecondAdhan,
                                onCheckedChange = {
                                    onSetFridayOptions(fridayFirstAdhan, it, fridayMinutesBefore)
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = GoldAccent
                                )
                            )
                        }

                        if (fridayFirstAdhan) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "الأذان الأول قبل الظهر بـ (دقائق)", fontSize = 12.sp, color = Color(0xFF9CA3AF))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF0F261E),
                                    border = BorderStroke(1.dp, Color(0xFF1F4336))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        TextButton(onClick = {
                                            if (fridayMinutesBefore > 5) onSetFridayOptions(fridayFirstAdhan, fridaySecondAdhan, fridayMinutesBefore - 5)
                                        }) { Text("-", fontSize = 16.sp, color = GoldAccent) }
                                        Text(text = "$fridayMinutesBefore", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE5E7EB))
                                        TextButton(onClick = {
                                            if (fridayMinutesBefore < 60) onSetFridayOptions(fridayFirstAdhan, fridaySecondAdhan, fridayMinutesBefore + 5)
                                        }) { Text("+", fontSize = 16.sp, color = GoldAccent) }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "صوت فقط في وقته بدون تنبيه مسبق",
                            fontSize = 11.sp,
                            color = Color(0xFF6B7280)
                        )
                    }
                }
            }

            // Note at bottom
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF0F261E),
                    border = BorderStroke(1.dp, Color(0xFF1B4336))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "ملاحظة: يعمل الصوت والتنبيهات أثناء فتح التطبيق وعند مواعيد الأذان بدقة عالية.",
                            fontSize = 11.sp,
                            color = Color(0xFF9CA3AF),
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}
