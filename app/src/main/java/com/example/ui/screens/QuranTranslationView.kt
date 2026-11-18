package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.QuranTranslationsDataset
import com.example.data.models.Ayah
import com.example.data.models.Surah
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.GoldAccent

@Composable
fun QuranTranslationView(
    surahs: List<Surah>,
    selectedLang: String,
    onSelectLang: (String) -> Unit,
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
    var showLangMenu by remember { mutableStateOf(false) }

    val availableEditions = remember { QuranTranslationsDataset.editions }
    val currentEdition = remember(selectedLang) { QuranTranslationsDataset.getEdition(selectedLang) }

    LaunchedEffect(selectedSurah) {
        isLoading = true
        ayahs = onLoadAyahs(selectedSurah.number)
        isLoading = false
    }

    val filteredAyahs = remember(ayahs, filterQuery) {
        if (filterQuery.isBlank()) ayahs
        else ayahs.filter {
            it.textAr.contains(filterQuery) ||
                it.getTranslation(selectedLang).contains(filterQuery, ignoreCase = true) ||
                it.numberInSurah.toString() == filterQuery.trim()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("quran_translation_view")
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
                            Icon(Icons.Filled.Translate, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(20.dp))
                            Text(
                                text = "ترجمة القرآن الكريم",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = "ترجمة معاني القرآن الكريم بلغات العالم المعتمدة",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Language Selector Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box {
                        FilledTonalButton(
                            onClick = { showLangMenu = true },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Filled.Language, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("${currentEdition.flagEmoji} ${currentEdition.languageNameAr} (${currentEdition.languageNameNative})", fontSize = 12.sp)
                            Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
                        }

                        DropdownMenu(
                            expanded = showLangMenu,
                            onDismissRequest = { showLangMenu = false }
                        ) {
                            availableEditions.forEach { edition ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "${edition.flagEmoji} ${edition.languageNameAr} - ${edition.languageNameNative} (${edition.translatorName})",
                                            fontSize = 12.sp
                                        )
                                    },
                                    onClick = {
                                        onSelectLang(edition.code)
                                        showLangMenu = false
                                    },
                                    leadingIcon = {
                                        if (edition.code == selectedLang) {
                                            Icon(Icons.Filled.Check, contentDescription = null, tint = GoldAccent)
                                        }
                                    }
                                )
                            }
                        }
                    }

                    Text(
                        text = "المترجم: ${currentEdition.translatorName}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Surah Selector
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

                // Filter inside surah
                OutlinedTextField(
                    value = filterQuery,
                    onValueChange = { filterQuery = it },
                    placeholder = { Text("بحث في آيات أو ترجمة سورة ${selectedSurah.nameAr}...", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    trailingIcon = {
                        if (filterQuery.isNotEmpty()) {
                            IconButton(onClick = { filterQuery = "" }) {
                                Icon(Icons.Filled.Clear, contentDescription = "مسح البحث", modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Ayahs with Translations
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
                items(filteredAyahs, key = { it.numberInSurah }) { ayah ->
                    val translationText = remember(selectedLang, selectedSurah.number, ayah.numberInSurah) {
                        val direct = ayah.getTranslation(selectedLang)
                        if (direct.isNotBlank() && !direct.startsWith("Ayah ")) {
                            direct
                        } else {
                            val map = QuranTranslationsDataset.getTranslations(selectedSurah.number, ayah.numberInSurah)
                            map[selectedLang.lowercase()] ?: direct
                        }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            // Ayah Top Bar
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = {
                                        clipboardManager.setText(
                                            AnnotatedString("${ayah.textAr}\n\nTranslation [${currentEdition.languageNameAr}]:\n$translationText")
                                        )
                                        copiedAyahNumber = ayah.numberInSurah
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = if (copiedAyahNumber == ayah.numberInSurah) Icons.Filled.Check else Icons.Filled.ContentCopy,
                                        contentDescription = "نسخ الترجمة",
                                        tint = if (copiedAyahNumber == ayah.numberInSurah) EmeraldLight else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF834460).copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "الآية (${ayah.numberInSurah})",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFFB4AB),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Arabic Ayah
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

                            // Translated text
                            Text(
                                text = translationText,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.primary,
                                lineHeight = 20.sp,
                                textAlign = if (currentEdition.code == "ur") TextAlign.Right else TextAlign.Left,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}
