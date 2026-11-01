package com.example.ui.screens

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.DhikrItem
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.GoldAccent

enum class AthkarMainSide(val titleAr: String, val icon: String) {
    ATHKAR("جهة الأذكار", "📿"),
    DUAS("جهة الأدعية", "🤲")
}

data class AthkarSubCategory(val id: String, val titleAr: String, val icon: String, val side: AthkarMainSide)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AthkarDuasScreen(
    adhkarList: List<DhikrItem>,
    initialCategory: String = "post_prayer",
    onSpeakDhikr: (String) -> Unit = {},
    onStopSpeaking: () -> Unit = {},
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val allSubCategories = remember {
        listOf(
            // جهة الأذكار
            AthkarSubCategory("post_prayer", "بعد الصلاة والأذان", "🕌", AthkarMainSide.ATHKAR),
            AthkarSubCategory("morning", "أذكار الصباح", "☀️", AthkarMainSide.ATHKAR),
            AthkarSubCategory("evening", "أذكار المساء", "🌙", AthkarMainSide.ATHKAR),
            AthkarSubCategory("sleep", "النوم والاستيقاظ", "🛏️", AthkarMainSide.ATHKAR),
            AthkarSubCategory("mosque_wudu", "المسجد والوضوء", "🚿", AthkarMainSide.ATHKAR),
            AthkarSubCategory("travel_home", "السفر والمنزل", "🚗", AthkarMainSide.ATHKAR),
            AthkarSubCategory("salawat", "الصلاة على النبي ﷺ", "💚", AthkarMainSide.ATHKAR),
            AthkarSubCategory("tasbih_istighfar", "تسبيح واستغفار جامع", "📿", AthkarMainSide.ATHKAR),

            // جهة الأدعية
            AthkarSubCategory("duas", "أدعية قرآنية جامعة", "📖", AthkarMainSide.DUAS),
            AthkarSubCategory("prophetic", "أدعية نبوية شريفة", "🕊️", AthkarMainSide.DUAS),
            AthkarSubCategory("distress_relief", "تفريج الهم والكرب والدين", "🤲", AthkarMainSide.DUAS),
            AthkarSubCategory("healing_ruqyah", "الشفاء والرقية الشرعية", "🌿", AthkarMainSide.DUAS),
            AthkarSubCategory("parents_family", "الوالدين والذرية والبركة", "👨‍👩‍👧‍👦", AthkarMainSide.DUAS),
            AthkarSubCategory("forgiveness_repentance", "التوبة والاستغفار", "💫", AthkarMainSide.DUAS),
            AthkarSubCategory("friday_night", "يوم الجمعة وقيام الليل", "🌙", AthkarMainSide.DUAS)
        )
    }

    // Determine initial side from initialCategory
    val initialFound = allSubCategories.find { it.id == initialCategory }
    var selectedSide by remember { mutableStateOf(initialFound?.side ?: AthkarMainSide.ATHKAR) }
    var selectedCategoryId by remember { mutableStateOf(initialFound?.id ?: "post_prayer") }
    var searchQuery by remember { mutableStateOf("") }

    // Map to track progress count for each dhikr
    val countsMap = remember { mutableStateMapOf<String, Int>() }
    val context = LocalContext.current
    val vibrator = remember { context.getSystemService(android.content.Context.VIBRATOR_SERVICE) as? Vibrator }
    val clipboardManager = LocalClipboardManager.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Sub-categories for current side
    val currentSideCategories = remember(selectedSide) {
        allSubCategories.filter { it.side == selectedSide }
    }

    // When side changes, select its first sub-category if current is not in side
    LaunchedEffect(selectedSide) {
        if (currentSideCategories.none { it.id == selectedCategoryId }) {
            selectedCategoryId = currentSideCategories.firstOrNull()?.id ?: "post_prayer"
        }
    }

    // Filter items based on category and search query
    val filteredList = remember(adhkarList, selectedCategoryId, searchQuery, selectedSide) {
        val base = if (searchQuery.isBlank()) {
            adhkarList.filter { it.category == selectedCategoryId }
        } else {
            val q = searchQuery.trim()
            adhkarList.filter { it.textAr.contains(q) || it.benefitAr.contains(q) || it.sourceAr.contains(q) }
        }
        base
    }

    // Calculate completion progress
    val totalInCat = filteredList.size
    val completedInCat = filteredList.count { (countsMap[it.id] ?: 0) >= it.targetCount }
    val progressFraction = if (totalInCat > 0) completedInCat.toFloat() / totalInCat else 0f

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "الأذكار والأدعية الشاملة",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${selectedSide.titleAr} • مكتمل: $completedInCat / $totalInCat",
                            fontSize = 11.sp,
                            color = GoldAccent
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onStopSpeaking()
                            onBack()
                        },
                        modifier = Modifier.testTag("athkar_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            filteredList.forEach { countsMap[it.id] = 0 }
                        }
                    ) {
                        Icon(Icons.Filled.RestartAlt, contentDescription = "Reset Progress", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // ==========================================
            // PRIMARY TWO-SIDED SELECTOR (جهة الأذكار / جهة الأدعية)
            // ==========================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AthkarMainSide.values().forEach { side ->
                    val isSelected = selectedSide == side
                    Surface(
                        onClick = {
                            onStopSpeaking()
                            selectedSide = side
                        },
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        tonalElevation = if (isSelected) 6.dp else 1.dp,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("athkar_main_side_${side.name.lowercase()}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp, horizontal = 12.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = side.icon, fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = side.titleAr,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                placeholder = { Text("ابحث في ${selectedSide.titleAr} بالكلمة...", fontSize = 12.sp) },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = GoldAccent) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Filled.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )

            // Sub-Categories Horizontal Tabs
            if (searchQuery.isBlank()) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(currentSideCategories) { cat ->
                        val isSelected = selectedCategoryId == cat.id
                        Surface(
                            onClick = {
                                onStopSpeaking()
                                selectedCategoryId = cat.id
                            },
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null,
                            tonalElevation = if (isSelected) 4.dp else 1.dp,
                            modifier = Modifier.testTag("athkar_cat_${cat.id}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(text = cat.icon, fontSize = 13.sp)
                                Text(
                                    text = cat.titleAr,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            // Progress Indicator Bar
            if (searchQuery.isBlank()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "نسبة الإنجاز: ${(progressFraction * 100).toInt()}%",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (completedInCat == totalInCat && totalInCat > 0) {
                            Text(
                                text = "مبارك! أتممت ذكر هذه الفئة ✨",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldLight
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { progressFraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Adhkar / Duas List
            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "لا توجد نتائج مطابقة لبحثك في ${selectedSide.titleAr}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredList, key = { it.id }) { dhikr ->
                        val currentCount = countsMap[dhikr.id] ?: 0
                        val isDone = currentCount >= dhikr.targetCount

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("dhikr_card_${dhikr.id}"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isDone)
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                                else
                                    MaterialTheme.colorScheme.surface
                            ),
                            border = if (isDone)
                                androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.6f))
                            else null
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                // Header: Count Badge & Actions
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isDone) EmeraldLight.copy(alpha = 0.2f) else GoldAccent.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = if (isDone) "تم الإكمال ✓" else "التكرار المطلوب: ${dhikr.targetCount} مرات",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isDone) EmeraldLight else GoldAccent,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        // Reset single count button
                                        if (currentCount > 0) {
                                            IconButton(
                                                onClick = { countsMap[dhikr.id] = 0 },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Filled.RotateLeft,
                                                    contentDescription = "Reset item",
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }

                                        // Audio listen button
                                        IconButton(
                                            onClick = { onSpeakDhikr(dhikr.textAr) },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.VolumeUp,
                                                contentDescription = "Speak Dhikr",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }

                                        // Copy button
                                        IconButton(
                                            onClick = {
                                                clipboardManager.setText(AnnotatedString(dhikr.textAr))
                                            },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.ContentCopy,
                                                contentDescription = "Copy",
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Arabic Text (Tashkeel)
                                Text(
                                    text = dhikr.textAr,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 30.sp,
                                    textAlign = TextAlign.Right,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                if (dhikr.benefitAr.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                            .padding(8.dp),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Filled.Info,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = if (dhikr.sourceAr.isNotBlank()) "${dhikr.benefitAr} • [${dhikr.sourceAr}]" else dhikr.benefitAr,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            lineHeight = 16.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Interactive Integrated Digital Tasbeeh Counter Button
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "المنجز: $currentCount من ${dhikr.targetCount}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isDone) EmeraldLight else MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Button(
                                        onClick = {
                                            if (currentCount < dhikr.targetCount) {
                                                val next = currentCount + 1
                                                countsMap[dhikr.id] = next
                                                try {
                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                        val duration = if (next >= dhikr.targetCount) 150L else 40L
                                                        vibrator?.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
                                                    } else {
                                                        vibrator?.vibrate(40)
                                                    }
                                                } catch (e: Exception) {}
                                            }
                                        },
                                        enabled = !isDone,
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isDone) EmeraldLight else MaterialTheme.colorScheme.primary
                                        ),
                                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                        modifier = Modifier.testTag("count_dhikr_${dhikr.id}")
                                    ) {
                                        Icon(
                                            imageVector = if (isDone) Icons.Filled.Check else Icons.Filled.TouchApp,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (isDone) "تم الإكمال" else "تسبيح (${dhikr.targetCount - currentCount})",
                                            fontSize = 13.sp,
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
