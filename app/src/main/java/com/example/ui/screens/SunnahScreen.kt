package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.SunnahDataset
import com.example.data.models.Hadith
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.GoldAccent
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SunnahScreen(
    hadiths: List<Hadith>,
    onSpeakHadith: (String) -> Unit,
    onStopSpeaking: () -> Unit,
    isSpeaking: Boolean,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedBook by remember { mutableStateOf("الكل") }
    var selectedChapter by remember { mutableStateOf("الكل") }
    var hadithFontSize by remember { mutableFloatStateOf(17f) }
    var currentlyPlayingHadithId by remember { mutableStateOf<Int?>(null) }
    var randomHadith by remember { mutableStateOf<Hadith?>(null) }
    var showRandomDialog by remember { mutableStateOf(false) }

    // Hadith of the Day (day-based deterministic selection)
    val hadithOfTheDay = remember(hadiths) {
        if (hadiths.isEmpty()) null
        else {
            val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
            hadiths[dayOfYear % hadiths.size]
        }
    }

    // Filtered list
    val filteredHadiths = remember(hadiths, searchQuery, selectedBook, selectedChapter) {
        hadiths.filter { h ->
            val matchesBook = selectedBook == "الكل" || h.bookAr == selectedBook
            val matchesChapter = selectedChapter == "الكل" || h.chapterAr == selectedChapter
            val matchesQuery = searchQuery.isBlank() ||
                    h.textAr.contains(searchQuery, ignoreCase = true) ||
                    h.narratorAr.contains(searchQuery, ignoreCase = true) ||
                    h.explanationAr.contains(searchQuery, ignoreCase = true) ||
                    h.chapterAr.contains(searchQuery, ignoreCase = true) ||
                    h.bookAr.contains(searchQuery, ignoreCase = true)
            matchesBook && matchesChapter && matchesQuery
        }
    }

    Column(
        modifier = modifier
            .testTag("sunnah_screen")
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("btn_back_sunnah")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "الرجوع")
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "السنة النبوية الشريفة",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldAccent
                        )
                        Text(
                            text = "الأربعون النووية وصحيح البخاري ومسلم ورياض الصالحين",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Random Hadith Button
                    IconButton(
                        onClick = {
                            if (hadiths.isNotEmpty()) {
                                randomHadith = hadiths.random()
                                showRandomDialog = true
                            }
                        },
                        modifier = Modifier
                            .size(38.dp)
                            .background(GoldAccent.copy(alpha = 0.15f), CircleShape)
                            .testTag("btn_random_hadith")
                    ) {
                        Icon(
                            Icons.Filled.Casino,
                            contentDescription = "حديث عشوائي",
                            tint = GoldAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Font Zoom Controls
                    Row(
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { if (hadithFontSize > 13f) hadithFontSize -= 1f },
                            modifier = Modifier.size(30.dp)
                        ) {
                            Text("A-", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        IconButton(
                            onClick = { if (hadithFontSize < 25f) hadithFontSize += 1f },
                            modifier = Modifier.size(30.dp)
                        ) {
                            Text("A+", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("search_hadith_input"),
                    placeholder = { Text("ابحث في المتن، الراوي، الشرح، الموضوع...", fontSize = 12.sp) },
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

                Spacer(modifier = Modifier.height(8.dp))

                // Book Filter Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SunnahDataset.collections.forEach { book ->
                        val isSelected = selectedBook == book
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedBook = book },
                            label = {
                                Text(
                                    text = book,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GoldAccent.copy(alpha = 0.22f),
                                selectedLabelColor = MaterialTheme.colorScheme.onSurface
                            ),
                            shape = RoundedCornerShape(14.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Topic / Chapter Filter Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SunnahDataset.chapters.forEach { chapter ->
                        val isSelected = selectedChapter == chapter
                        Surface(
                            onClick = { selectedChapter = chapter },
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = chapter,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // Main List Content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 80.dp)
        ) {
            // Hadith of the Day Banner (if no search or specific filter is active)
            if (searchQuery.isBlank() && selectedBook == "الكل" && selectedChapter == "الكل" && hadithOfTheDay != null) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            GoldAccent.copy(alpha = 0.2f),
                                            MaterialTheme.colorScheme.surface
                                        )
                                    )
                                )
                                .padding(14.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Filled.Star,
                                            contentDescription = null,
                                            tint = GoldAccent,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "حديث اليوم المبارك",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GoldAccent
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = GoldAccent.copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = hadithOfTheDay.bookAr,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "عن ${hadithOfTheDay.narratorAr} قال: قال رسول الله ﷺ:",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = hadithOfTheDay.textAr,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 24.sp,
                                    textAlign = TextAlign.Start
                                )

                                if (hadithOfTheDay.explanationAr.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "الفوائد: ${hadithOfTheDay.explanationAr}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.primary,
                                        lineHeight = 17.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Results count
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "الأحاديث النبوية (${filteredHadiths.size})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (isSpeaking) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "جارٍ القراءة الصوتية...",
                                fontSize = 11.sp,
                                color = EmeraldLight,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            IconButton(
                                onClick = {
                                    onStopSpeaking()
                                    currentlyPlayingHadithId = null
                                },
                                modifier = Modifier.size(26.dp)
                            ) {
                                Icon(
                                    Icons.Filled.Stop,
                                    contentDescription = "إيقاف الصوت",
                                    tint = EmeraldLight,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Hadith List Items
            items(filteredHadiths, key = { it.id }) { hadith ->
                var isExpanded by remember { mutableStateOf(false) }
                val isThisPlaying = isSpeaking && currentlyPlayingHadithId == hadith.id

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .testTag("hadith_card_${hadith.id}"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isThisPlaying)
                            EmeraldLight.copy(alpha = 0.08f)
                        else
                            MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(14.dp)
                            .animateContentSize()
                    ) {
                        // Card Header: Badges & Grade
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Book Badge
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Text(
                                        text = hadith.bookAr,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                    )
                                }

                                // Chapter Badge
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Text(
                                        text = hadith.chapterAr,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            // Grade badge
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = GoldAccent.copy(alpha = 0.18f)
                            ) {
                                Text(
                                    text = hadith.gradeAr,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldAccent,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Narrator attribution
                        Text(
                            text = "عن ${hadith.narratorAr} قال: قال رسول الله ﷺ:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Hadith Matn (Text)
                        Text(
                            text = hadith.textAr,
                            fontSize = hadithFontSize.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = (hadithFontSize * 1.55f).sp,
                            textAlign = TextAlign.Start
                        )

                        // Explanation (Sharh) section
                        if (hadith.explanationAr.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Filled.Lightbulb,
                                            contentDescription = null,
                                            tint = GoldAccent,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "الشرح والفوائد التربوية:",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = hadith.explanationAr,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Actions Row: Listen, Copy, Share
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "حديث رقم #${hadith.id}",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                // TTS Listen Button
                                IconButton(
                                    onClick = {
                                        if (isThisPlaying) {
                                            onStopSpeaking()
                                            currentlyPlayingHadithId = null
                                        } else {
                                            currentlyPlayingHadithId = hadith.id
                                            val fullSpeech = "عن ${hadith.narratorAr} قال: قال رسول الله صلى الله عليه وسلم: ${hadith.textAr}"
                                            onSpeakHadith(fullSpeech)
                                        }
                                    },
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Icon(
                                        if (isThisPlaying) Icons.Filled.Stop else Icons.Filled.VolumeUp,
                                        contentDescription = if (isThisPlaying) "إيقاف الاستماع" else "استماع صوتي",
                                        tint = if (isThisPlaying) EmeraldLight else MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                // Copy Button
                                IconButton(
                                    onClick = {
                                        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clipText = "عن ${hadith.narratorAr} قال: قال رسول الله ﷺ:\n\n«${hadith.textAr}»\n\n[المصدر: ${hadith.bookAr} - حكم الحديث: ${hadith.gradeAr}]"
                                        val clip = ClipData.newPlainText("حديث نبوي", clipText)
                                        cm.setPrimaryClip(clip)
                                        Toast.makeText(context, "تم نسخ الحديث الشريف", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Icon(
                                        Icons.Filled.ContentCopy,
                                        contentDescription = "نسخ الحديث",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                // Share Button
                                IconButton(
                                    onClick = {
                                        val shareText = "عن ${hadith.narratorAr} قال: قال رسول الله ﷺ:\n\n«${hadith.textAr}»\n\n[المصدر: ${hadith.bookAr} - حكم الحديث: ${hadith.gradeAr}]"
                                        val sendIntent = Intent().apply {
                                            action = Intent.ACTION_SEND
                                            putExtra(Intent.EXTRA_TEXT, shareText)
                                            type = "text/plain"
                                        }
                                        context.startActivity(Intent.createChooser(sendIntent, "مشاركة الحديث الشريف"))
                                    },
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Icon(
                                        Icons.Filled.Share,
                                        contentDescription = "مشاركة",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Random Hadith Dialog
    if (showRandomDialog && randomHadith != null) {
        val h = randomHadith!!
        AlertDialog(
            onDismissRequest = { showRandomDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Casino, contentDescription = null, tint = GoldAccent)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("حديث نبوي كريم", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                ) {
                    Text(
                        text = "عن ${h.narratorAr} قال: قال رسول الله ﷺ:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = h.textAr,
                        fontSize = 15.sp,
                        lineHeight = 24.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (h.explanationAr.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = h.explanationAr,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "[المصدر: ${h.bookAr} - ${h.gradeAr}]",
                        fontSize = 10.sp,
                        color = GoldAccent,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val fullSpeech = "عن ${h.narratorAr} قال: قال رسول الله صلى الله عليه وسلم: ${h.textAr}"
                        onSpeakHadith(fullSpeech)
                    }
                ) {
                    Icon(Icons.Filled.VolumeUp, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("استماع")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRandomDialog = false }) {
                    Text("إغلاق")
                }
            }
        )
    }
}
