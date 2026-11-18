package com.example.ui.screens

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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.QuranOfflineData
import com.example.data.models.Surah
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.GoldAccent

data class QuranSearchResult(
    val surahNumber: Int,
    val surahNameAr: String,
    val ayahNumber: Int,
    val textAr: String
)

@Composable
fun QuranSearchView(
    surahs: List<Surah>,
    onOpenSurah: (Surah, Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("الكل") }

    val filterChips = listOf("الكل", "مكية", "مدنية", "قصار السور", "الرحمة", "الصبر", "الجنة")

    // Filter surahs matching search
    val matchingSurahs = remember(searchQuery, selectedFilter, surahs) {
        if (searchQuery.isBlank()) emptyList()
        else {
            surahs.filter { s ->
                val matchesText = s.nameAr.contains(searchQuery) ||
                        s.nameEn.contains(searchQuery, ignoreCase = true) ||
                        s.number.toString() == searchQuery.trim()
                val matchesFilter = when (selectedFilter) {
                    "مكية" -> s.revelationType == "مكية"
                    "مدنية" -> s.revelationType == "مدنية"
                    "قصار السور" -> s.versesCount <= 15
                    else -> true
                }
                matchesText && matchesFilter
            }
        }
    }

    // Search inside ayahs from offline database
    val matchingAyahs = remember(searchQuery, selectedFilter) {
        if (searchQuery.length < 2) emptyList()
        else {
            val results = mutableListOf<QuranSearchResult>()
            // Search through representative offline surahs
            listOf(1, 2, 18, 36, 55, 56, 67, 108, 112, 113, 114).forEach { sNum ->
                val offlineList = QuranOfflineData.getOfflineSurahAyahs(sNum) ?: emptyList()
                val surah = surahs.find { it.number == sNum }
                val sName = surah?.nameAr ?: "السورة $sNum"
                offlineList.forEach { a ->
                    if (a.textAr.contains(searchQuery)) {
                        results.add(
                            QuranSearchResult(
                                surahNumber = sNum,
                                surahNameAr = sName,
                                ayahNumber = a.numberInSurah,
                                textAr = a.textAr
                            )
                        )
                    }
                }
            }
            results
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("quran_search_view")
    ) {
        // Top Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 3.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "الرجوع للفهرس",
                            tint = GoldAccent
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Filled.Search, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(20.dp))
                            Text(
                                text = "البحث في القرآن الكريم",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = "بحث فوري في أسماء السور والآيات والكلمات القرآنية",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("اكتب كلمة قرآنية أو اسم سورة (مثل: الفاتحة، الحمد، الرحمن)...", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = GoldAccent) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Filled.Clear, contentDescription = null)
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldAccent,
                        focusedLabelColor = GoldAccent
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Filters
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(filterChips) { chip ->
                        FilterChip(
                            selected = selectedFilter == chip,
                            onClick = {
                                selectedFilter = chip
                                if (chip !in listOf("الكل", "مكية", "مدنية", "قصار السور")) {
                                    searchQuery = chip
                                }
                            },
                            label = { Text(chip, fontSize = 11.sp) },
                            shape = RoundedCornerShape(10.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GoldAccent.copy(alpha = 0.25f),
                                selectedLabelColor = GoldAccent
                            )
                        )
                    }
                }
            }
        }

        // Search Results
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (searchQuery.isBlank()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Filled.Search, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(40.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "ابحث عن أي سورة أو آية أو كلمة في كتاب الله",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "يمكنك كتابة اسم السورة (مثل: الكهف) أو كلمات الآيات الكريمة",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                // Surahs Matches
                if (matchingSurahs.isNotEmpty()) {
                    item {
                        Text(
                            text = "السور المطابقة (${matchingSurahs.size}):",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldAccent
                        )
                    }

                    items(matchingSurahs, key = { "surah_${it.number}" }) { surah ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onOpenSurah(surah, 1) },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
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
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = GoldAccent.copy(alpha = 0.2f),
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text("${surah.number}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                                        }
                                    }
                                    Column {
                                        Text("سورة ${surah.nameAr}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                        Text("${surah.revelationType} • ${surah.versesCount} آيات", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }

                                TextButton(onClick = { onOpenSurah(surah, 1) }) {
                                    Text("فتح السورة", fontSize = 11.sp, color = GoldAccent)
                                }
                            }
                        }
                    }
                }

                // Ayahs Matches
                if (matchingAyahs.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "الآيات المطابقة (${matchingAyahs.size}):",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldLight
                        )
                    }

                    items(matchingAyahs, key = { "ayah_${it.surahNumber}_${it.ayahNumber}" }) { result ->
                        val surahObj = surahs.find { it.number == result.surahNumber }
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (surahObj != null) onOpenSurah(surahObj, result.ayahNumber)
                                },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldLight.copy(alpha = 0.3f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "الآية (${result.ayahNumber})",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldLight
                                    )
                                    Text(
                                        text = "سورة ${result.surahNameAr}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldAccent
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                // Highlight matched text
                                val annotatedText = buildAnnotatedString {
                                    val full = result.textAr
                                    val startIdx = full.indexOf(searchQuery)
                                    if (startIdx >= 0) {
                                        append(full.substring(0, startIdx))
                                        withStyle(SpanStyle(color = GoldAccent, fontWeight = FontWeight.ExtraBold)) {
                                            append(full.substring(startIdx, startIdx + searchQuery.length))
                                        }
                                        append(full.substring(startIdx + searchQuery.length))
                                    } else {
                                        append(full)
                                    }
                                }

                                Text(
                                    text = annotatedText,
                                    fontSize = 15.sp,
                                    lineHeight = 24.sp,
                                    textAlign = TextAlign.Right,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
