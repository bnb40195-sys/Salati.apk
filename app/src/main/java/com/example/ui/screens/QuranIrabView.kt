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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.QuranIrabAndTafsirDataset
import com.example.data.models.Ayah
import com.example.data.models.Surah
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.GoldAccent

@Composable
fun QuranIrabView(
    surahs: List<Surah>,
    onLoadAyahs: suspend (Int) -> List<Ayah>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedSurah by remember {
        mutableStateOf(surahs.firstOrNull() ?: Surah(1, "الفاتحة", "Al-Fatihah", "The Opening", 7, "مكية", 1))
    }
    var ayahs by remember { mutableStateOf<List<Ayah>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var filterQuery by remember { mutableStateOf("") }
    val clipboardManager = LocalClipboardManager.current
    var copiedAyahNumber by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(selectedSurah) {
        isLoading = true
        ayahs = onLoadAyahs(selectedSurah.number)
        isLoading = false
    }

    val filteredAyahs = remember(ayahs, filterQuery) {
        if (filterQuery.isBlank()) ayahs
        else ayahs.filter {
            it.textAr.contains(filterQuery) || it.numberInSurah.toString() == filterQuery.trim()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("quran_irab_view")
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
                            Icon(Icons.Filled.Edit, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(20.dp))
                            Text(
                                text = "إعراب القرآن الكريم",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = "إعراب الآيات الكريمة وقواعد النحو وبيان الكلمات",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Surah Selector Row
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(surahs) { surah ->
                        val isSelected = surah.number == selectedSurah.number
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedSurah = surah },
                            label = { Text("${surah.number}. ${surah.nameAr}", fontSize = 12.sp) },
                            shape = RoundedCornerShape(10.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GoldAccent.copy(alpha = 0.25f),
                                selectedLabelColor = GoldAccent
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Search field within Surah
                OutlinedTextField(
                    value = filterQuery,
                    onValueChange = { filterQuery = it },
                    placeholder = { Text("بحث في آيات سورة ${selectedSurah.nameAr}...", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    trailingIcon = {
                        if (filterQuery.isNotEmpty()) {
                            IconButton(onClick = { filterQuery = "" }) {
                                Icon(Icons.Filled.Clear, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Ayahs List with I'rab Cards
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = GoldAccent)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    // Header card for current Surah
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "عدد الآيات: ${selectedSurah.versesCount} • ${selectedSurah.revelationType}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "سورة ${selectedSurah.nameAr}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldAccent
                            )
                        }
                    }
                }

                items(filteredAyahs, key = { it.numberInSurah }) { ayah ->
                    val irabText = remember(selectedSurah.number, ayah.numberInSurah) {
                        QuranIrabAndTafsirDataset.getAyahIrab(
                            surahNumber = selectedSurah.number,
                            ayahNumber = ayah.numberInSurah,
                            fallbackText = ayah.textAr
                        )
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            // Ayah Top bar
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    IconButton(
                                        onClick = {
                                            clipboardManager.setText(
                                                AnnotatedString("الآية: ${ayah.textAr}\n\nالإعراب:\n$irabText")
                                            )
                                            copiedAyahNumber = ayah.numberInSurah
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (copiedAyahNumber == ayah.numberInSurah) Icons.Filled.Check else Icons.Filled.ContentCopy,
                                            contentDescription = "نسخ الإعراب",
                                            tint = if (copiedAyahNumber == ayah.numberInSurah) EmeraldLight else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = GoldAccent.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "الآية (${ayah.numberInSurah})",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldAccent,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Ayah Quran Text
                            Text(
                                text = ayah.textAr,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Right,
                                lineHeight = 30.sp,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                            Spacer(modifier = Modifier.height(8.dp))

                            // I'rab Details Header
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Filled.MenuBook, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(16.dp))
                                Text(
                                    text = "الإعراب والبيان النحوي:",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldLight
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // I'rab Content
                            Text(
                                text = irabText,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                                lineHeight = 22.sp,
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
