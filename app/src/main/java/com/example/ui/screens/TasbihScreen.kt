package com.example.ui.screens

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.DhikrItem
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldAccentLight

@Composable
fun TasbihScreen(
    currentCount: Int,
    totalCount: Int,
    adhkarList: List<DhikrItem>,
    onIncrement: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val vibrator = remember { context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator }

    var selectedMode by remember { mutableStateOf(0) } // 0: Tasbih, 1: Athkar
    var targetGoal by remember { mutableStateOf(33) }
    var selectedPresetDhikr by remember { mutableStateOf("سبحان الله وبحمده") }

    val presets = remember {
        listOf(
            "سبحان الله وبحمده",
            "الحمد لله حمداً كثيراً",
            "لا إله إلا الله وحده لا شريك له",
            "الله أكبر كبيراً",
            "أستغفر الله وأتوب إليه",
            "لا حول ولا قوة إلا بالله العلي العظيم",
            "اللهم صل وسلم على نبينا محمد"
        )
    }

    val progress = remember(currentCount, targetGoal) {
        if (targetGoal <= 0) 1f else (currentCount % targetGoal).toFloat() / targetGoal
    }
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "tasbih_progress")

    fun triggerHaptic(isGoal: Boolean = false) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (isGoal) {
                    vibrator?.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 100, 80, 150), -1))
                } else {
                    vibrator?.vibrate(VibrationEffect.createOneShot(35, VibrationEffect.DEFAULT_AMPLITUDE))
                }
            } else {
                vibrator?.vibrate(if (isGoal) 150 else 35)
            }
        } catch (e: Exception) {
            // ignore
        }
    }

    Column(
        modifier = modifier
            .testTag("tasbih_screen")
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Tab Selector Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp
        ) {
            TabRow(
                selectedTabIndex = selectedMode,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Tab(
                    selected = selectedMode == 0,
                    onClick = { selectedMode = 0 },
                    text = { Text("المسبحة الإلكترونية", fontWeight = FontWeight.Bold, fontSize = 14.sp) },
                    icon = { Icon(Icons.Filled.TouchApp, contentDescription = null, modifier = Modifier.size(20.dp)) }
                )
                Tab(
                    selected = selectedMode == 1,
                    onClick = { selectedMode = 1 },
                    text = { Text("الأذكار والأدعية", fontWeight = FontWeight.Bold, fontSize = 14.sp) },
                    icon = { Icon(Icons.Filled.MenuBook, contentDescription = null, modifier = Modifier.size(20.dp)) }
                )
            }
        }

        if (selectedMode == 0) {
            // Electronic Tasbih Mode
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
            ) {
                // Preset Dhikr Selector Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "الذكر الحالي",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = selectedPresetDhikr,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldAccent,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Target Pills: 33, 100, Free
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                listOf(33, 100, 0).forEach { goal ->
                                    val isSelected = targetGoal == goal
                                    Surface(
                                        onClick = { targetGoal = goal },
                                        shape = RoundedCornerShape(20.dp),
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                                    ) {
                                        Text(
                                            text = if (goal == 0) "حر" else "$goal",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Big Circular Tap Counter Button
                item {
                    Spacer(modifier = Modifier.height(24.dp))

                    Box(
                        modifier = Modifier
                            .size(230.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primaryContainer,
                                        MaterialTheme.colorScheme.surfaceVariant
                                    )
                                )
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(bounded = true, radius = 115.dp)
                            ) {
                                val willReachGoal = targetGoal > 0 && ((currentCount + 1) % targetGoal == 0)
                                onIncrement()
                                triggerHaptic(willReachGoal)
                            }
                            .testTag("tasbih_counter_tap_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        // Circular Progress Indicator Ring
                        CircularProgressIndicator(
                            progress = { animatedProgress },
                            modifier = Modifier.size(210.dp),
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 6.dp,
                            trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$currentCount",
                                fontSize = 48.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (targetGoal > 0) {
                                Text(
                                    text = "الهدف: $targetGoal",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "اضغط للتسبيح",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Control Buttons (Reset & Total stats)
                item {
                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Reset current count
                        OutlinedButton(
                            onClick = onReset,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("reset_tasbih_button")
                        ) {
                            Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("إعادة تصفير", fontSize = 12.sp)
                        }

                        // Total Count Display Card
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Filled.History, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                                Text(text = "إجمالي التسبيح: $totalCount", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Preset List selection chips
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "اختر صيغة الذكر:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )

                    presets.forEach { preset ->
                        val isSelected = preset == selectedPresetDhikr
                        Card(
                            onClick = { selectedPresetDhikr = preset },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                else MaterialTheme.colorScheme.surface
                            ),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = preset,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                if (isSelected) {
                                    Icon(Icons.Filled.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Categorized Athkar Mode
            var selectedCategory by remember { mutableStateOf("morning") }
            val categories = listOf(
                "morning" to "أذكار الصباح",
                "evening" to "أذكار المساء",
                "prayer" to "أذكار الصلاة",
                "sleep" to "أذكار النوم"
            )

            val filteredAthkar = remember(selectedCategory, adhkarList) {
                adhkarList.filter { it.category == selectedCategory }
            }

            Column(modifier = Modifier.fillMaxSize()) {
                // Category Pills
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { (catId, catTitle) ->
                        val isSelected = selectedCategory == catId
                        Surface(
                            onClick = { selectedCategory = catId },
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = catTitle,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }

                // Athkar Items List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(filteredAthkar, key = { it.id }) { item ->
                        var itemCounter by remember { mutableStateOf(0) }
                        val isDone = itemCounter >= item.targetCount

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isDone) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                else MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = item.textAr,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 26.sp,
                                    textAlign = TextAlign.Right,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = item.benefitAr,
                                            fontSize = 11.sp,
                                            color = EmeraldLight
                                        )
                                        if (item.sourceAr.isNotBlank()) {
                                            Text(
                                                text = "المصدر: ${item.sourceAr}",
                                                fontSize = 10.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    // Tap counter button
                                    Button(
                                        onClick = {
                                            if (itemCounter < item.targetCount) {
                                                itemCounter++
                                                triggerHaptic(itemCounter >= item.targetCount)
                                            }
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isDone) EmeraldLight else MaterialTheme.colorScheme.primary
                                        ),
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = if (isDone) "تم ✓" else "$itemCounter / ${item.targetCount}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
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
}
