package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.BattleInfo
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.GoldAccent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GhazwatScreen(
    ghazwatList: List<BattleInfo>,
    majorConquests: List<BattleInfo> = emptyList(),
    majorSaraya: List<BattleInfo> = emptyList(),
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(0) }
    var expandedBattleId by remember { mutableStateOf<String?>(null) }
    var textScaleDelta by remember { mutableStateOf(0) }
    val clipboardManager = LocalClipboardManager.current
    var copiedMessage by remember { mutableStateOf<String?>(null) }

    val categories = listOf(
        "جميع الغزوات (27)",
        "الغزوات الكبرى",
        "معارك الفتوحات الخالدة",
        "السرايا النبوية الكبرى"
    )

    val currentList = when (selectedCategory) {
        0 -> ghazwatList
        1 -> ghazwatList.filter { it.isMajor }
        2 -> if (majorConquests.isNotEmpty()) majorConquests else ghazwatList.filter { it.id.contains("conquest") }
        3 -> majorSaraya
        else -> ghazwatList
    }

    val filteredList = remember(searchQuery, currentList) {
        if (searchQuery.isBlank()) currentList
        else {
            currentList.filter {
                it.nameAr.contains(searchQuery, ignoreCase = true) ||
                        it.leaderMuslims.contains(searchQuery, ignoreCase = true) ||
                        it.detailsAr.contains(searchQuery, ignoreCase = true) ||
                        it.eventsAr.contains(searchQuery, ignoreCase = true) ||
                        it.causesAr.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = modifier
            .testTag("ghazwat_screen")
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // App Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 3.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع")
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "السرد الكامل لغزوات النبي ﷺ",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldAccent
                        )
                        Text(
                            text = "سرد الأحداث، الأسباب، الخطط الحربية، والدروس المستفادة",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Font size adjustment buttons
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Surface(
                            onClick = { if (textScaleDelta > -2) textScaleDelta-- },
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = "A-",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Surface(
                            onClick = { if (textScaleDelta < 4) textScaleDelta++ },
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = "A+",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Search field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("ابحث في الغزوات، القادة، أو الأحداث...", fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = GoldAccent) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Filled.Clear, contentDescription = "مسح")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Category chips
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories.indices.toList()) { index ->
                        val isSelected = selectedCategory == index
                        Surface(
                            onClick = { selectedCategory = index },
                            shape = RoundedCornerShape(18.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = categories[index],
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        // Copied toast banner
        if (copiedMessage != null) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                color = EmeraldLight.copy(alpha = 0.15f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Check, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = copiedMessage ?: "", fontSize = 12.sp, color = EmeraldLight)
                }
            }
        }

        // Battles List with Full Chronicle Reading Mode
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 80.dp)
        ) {
            items(filteredList, key = { it.id }) { b ->
                val isExpanded = expandedBattleId == b.id
                val baseTextSize = (13 + textScaleDelta).sp
                val titleSize = (16 + textScaleDelta).sp
                val headerSize = (13 + textScaleDelta).sp

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            expandedBattleId = if (isExpanded) null else b.id
                        },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = if (isExpanded) 4.dp else 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Title row
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
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (b.isMajor) GoldAccent.copy(alpha = 0.2f) else MaterialTheme.colorScheme.primaryContainer,
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = if (b.isMajor) Icons.Filled.MilitaryTech else Icons.Filled.Shield,
                                            contentDescription = null,
                                            tint = if (b.isMajor) GoldAccent else MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                                Column {
                                    Text(
                                        text = b.nameAr,
                                        fontSize = titleSize,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "القائد: ${b.leaderMuslims}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = "${b.yearH} • ${b.yearG}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Brief summary
                        Text(
                            text = b.detailsAr,
                            fontSize = baseTextSize,
                            lineHeight = (baseTextSize.value * 1.5).sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Sides info chip
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Filled.Group, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = b.sidesAr, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        // Expanded Full Chronicle Narrative
                        AnimatedVisibility(visible = isExpanded) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp)
                            ) {
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                Spacer(modifier = Modifier.height(12.dp))

                                // Causes section
                                if (b.causesAr.isNotBlank()) {
                                    ChronicleSection(
                                        title = "أسباب الغزوة ومقدماتها",
                                        icon = Icons.Filled.Info,
                                        content = b.causesAr,
                                        textSize = baseTextSize,
                                        headerSize = headerSize,
                                        iconColor = GoldAccent
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                }

                                // Full Events section (السرد الكامل لمجريات القتال)
                                if (b.eventsAr.isNotBlank()) {
                                    ChronicleSection(
                                        title = "السرد الكامل لمجريات المعركة والأحداث",
                                        icon = Icons.Filled.HistoryEdu,
                                        content = b.eventsAr,
                                        textSize = baseTextSize,
                                        headerSize = headerSize,
                                        iconColor = EmeraldLight
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                }

                                // Results section
                                if (b.resultAr.isNotBlank()) {
                                    ChronicleSection(
                                        title = "نتائج الغزوة والآثار الإستراتيجية",
                                        icon = Icons.Filled.EmojiEvents,
                                        content = b.resultAr,
                                        textSize = baseTextSize,
                                        headerSize = headerSize,
                                        iconColor = Color(0xFF10B981)
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                }

                                // Lessons learned
                                if (b.lessonsAr.isNotBlank()) {
                                    ChronicleSection(
                                        title = "الدروس والعبر المستفادة",
                                        icon = Icons.Filled.Lightbulb,
                                        content = b.lessonsAr,
                                        textSize = baseTextSize,
                                        headerSize = headerSize,
                                        iconColor = Color(0xFFF59E0B)
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                }

                                // Quran Verses
                                if (b.quranVerses.isNotBlank()) {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = EmeraldLight.copy(alpha = 0.1f),
                                        border = CardDefaults.outlinedCardBorder(),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Filled.AutoStories, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(text = "الآيات النازلة في الغزوة", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EmeraldLight)
                                            }
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = b.quranVerses,
                                                fontSize = baseTextSize,
                                                fontWeight = FontWeight.SemiBold,
                                                lineHeight = (baseTextSize.value * 1.5).sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                }

                                // Actions (Copy full chronicle)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    TextButton(
                                        onClick = {
                                            val fullText = buildString {
                                                appendLine("📖 ${b.nameAr} (${b.yearH} / ${b.yearG})")
                                                appendLine("👤 القائد: ${b.leaderMuslims}")
                                                appendLine("⚔️ الأطراف: ${b.sidesAr}")
                                                if (b.causesAr.isNotBlank()) appendLine("\n📌 الأسباب:\n${b.causesAr}")
                                                if (b.eventsAr.isNotBlank()) appendLine("\n📜 مجريات المعركة:\n${b.eventsAr}")
                                                if (b.resultAr.isNotBlank()) appendLine("\n🏆 النتيجة:\n${b.resultAr}")
                                                if (b.lessonsAr.isNotBlank()) appendLine("\n💡 الدروس المستفادة:\n${b.lessonsAr}")
                                                if (b.quranVerses.isNotBlank()) appendLine("\n📖 الآيات:\n${b.quranVerses}")
                                                appendLine("\nمن تطبيق صلاتي")
                                            }
                                            clipboardManager.setText(AnnotatedString(fullText))
                                            copiedMessage = "تم نسخ السرد الكامل لـ ${b.nameAr}"
                                        }
                                    ) {
                                        Icon(Icons.Filled.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("نسخ السرد الكامل", fontSize = 12.sp)
                                    }
                                }
                            }
                        }

                        // Expand toggle indicator
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 6.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isExpanded) "طي السرد والتفاصيل" else "قراءة السرد الكامل والمجريات",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldAccent
                            )
                            Icon(
                                imageVector = if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                                contentDescription = null,
                                tint = GoldAccent,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChronicleSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: String,
    textSize: androidx.compose.ui.unit.TextUnit,
    headerSize: androidx.compose.ui.unit.TextUnit,
    iconColor: Color
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = title, fontSize = headerSize, fontWeight = FontWeight.Bold, color = iconColor)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = content,
            fontSize = textSize,
            lineHeight = (textSize.value * 1.5).sp,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Start
        )
    }
}
