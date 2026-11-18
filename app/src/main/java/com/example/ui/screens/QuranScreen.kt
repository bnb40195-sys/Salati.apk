package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.QuranRiwayahsDataset
import com.example.data.local.QuranTranslationsDataset
import com.example.data.models.Ayah
import com.example.data.models.Qari
import com.example.data.models.QuranRiwayah
import com.example.data.models.Surah
import com.example.data.services.PlaybackState
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldAccentLight

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun QuranScreen(
    surahs: List<Surah>,
    qaris: List<Qari>,
    selectedQari: Qari,
    playbackState: PlaybackState,
    lastReadSurahNumber: Int,
    onSelectQari: (Qari) -> Unit,
    onPlaySurah: (Surah, Qari) -> Unit,
    onTogglePlayPause: () -> Unit,
    onStopAudio: () -> Unit,
    onLoadAyahs: suspend (Int) -> List<Ayah>,
    onSaveLastRead: (Int, Int) -> Unit,
    // Quran Bookmark (فاصل القراءة وعلامة التوقف)
    bookmarkSurahNumber: Int = 1,
    bookmarkAyahNumber: Int = 1,
    bookmarkSurahName: String = "الفاتحة",
    bookmarkAyahText: String = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
    hasActiveBookmark: Boolean = true,
    onSaveBookmark: (surahNumber: Int, surahName: String, ayahNumber: Int, ayahText: String) -> Unit = { _, _, _, _ -> },
    onClearBookmark: () -> Unit = {},
    // Quran Riwayahs / Mushafs (روايات ومصاحف القرآن الكريم)
    selectedRiwayahId: String = "hafs",
    onSelectRiwayah: (String) -> Unit = {},
    // Quran Translations (ترجمة معاني القرآن الكريم)
    selectedTranslationLang: String = "en",
    isTranslationVisible: Boolean = true,
    onSelectTranslationLang: (String) -> Unit = {},
    onToggleTranslationVisibility: (Boolean) -> Unit = {},
    savedFontSize: Int = 22,
    onUpdateFontSize: (Int) -> Unit = {},
    // Optional legacy fallback parameters to preserve caller compatibility
    onSmartReciteAyah: (ayahNum: Int, text: String, speed: Float, onDone: () -> Unit) -> Unit = { _, _, _, _ -> },
    onStopSmartRecitation: () -> Unit = {},
    isSmartSpeaking: Boolean = false,
    smartRecitingAyahNumber: Int? = null,
    onBackToHub: (() -> Unit)? = null,
    initialReadingSurah: Surah? = null,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var readingSurah by remember { mutableStateOf<Surah?>(initialReadingSurah) }
    var loadedAyahs by remember { mutableStateOf<List<Ayah>>(emptyList()) }
    var isLoadingAyahs by remember { mutableStateOf(false) }

    if (onBackToHub != null) {
        androidx.activity.compose.BackHandler(enabled = readingSurah == null) {
            onBackToHub()
        }
    }

    var currentFontSize by remember { mutableStateOf(savedFontSize) }
    var showRiwayahSheet by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()

    val currentRiwayah = remember(selectedRiwayahId) {
        QuranRiwayahsDataset.getRiwayah(selectedRiwayahId)
    }

    val availableQarisForRiwayah = remember(selectedRiwayahId, qaris) {
        val specific = QuranRiwayahsDataset.getRecitersForRiwayah(selectedRiwayahId)
        if (specific.isNotEmpty()) specific else qaris
    }

    val filteredSurahs = remember(surahs, searchQuery) {
        if (searchQuery.isBlank()) surahs
        else surahs.filter {
            it.nameAr.contains(searchQuery) ||
            it.nameEn.contains(searchQuery, ignoreCase = true) ||
            it.number.toString() == searchQuery.trim()
        }
    }

    // Load ayahs when readingSurah is clicked
    LaunchedEffect(readingSurah, selectedRiwayahId) {
        val s = readingSurah
        if (s != null) {
            isLoadingAyahs = true
            val rawAyahs = onLoadAyahs(s.number)
            // Adapt ayahs for active Riwayah
            loadedAyahs = rawAyahs.map { a ->
                val adaptedText = QuranRiwayahsDataset.getAyahTextForRiwayah(
                    surahNumber = s.number,
                    ayahNumber = a.numberInSurah,
                    defaultHafsText = a.textAr,
                    riwayahId = selectedRiwayahId
                )
                a.copy(textAr = adaptedText)
            }
            isLoadingAyahs = false
            onSaveLastRead(s.number, 1)
        }
    }

    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (playbackState.isPlaying || playbackState.isBuffering) 130.dp else 70.dp)
        ) {
            // Header Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 4.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                if (onBackToHub != null) {
                                    IconButton(onClick = onBackToHub, modifier = Modifier.size(36.dp)) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = "الرجوع للفهرس",
                                            tint = GoldAccent
                                        )
                                    }
                                }
                                Text(
                                    text = "المصحف",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Text(
                                text = "114 سورة",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Search input
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("ابحث عن سورة بالاسم أو الرقم...", fontSize = 13.sp) },
                        leadingIcon = {
                            Icon(Icons.Filled.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Filled.Clear, contentDescription = "Clear")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("quran_search_field"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.background,
                            unfocusedContainerColor = MaterialTheme.colorScheme.background
                        )
                    )
                }
            }

            // Surahs List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 10.dp)
            ) {
                items(filteredSurahs, key = { it.number }) { surah ->
                    val isLastRead = surah.number == lastReadSurahNumber
                    val isBookmarkedSurah = surah.number == bookmarkSurahNumber
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .testTag("surah_item_${surah.number}"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isBookmarkedSurah)
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                            else if (isLastRead)
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            else
                                MaterialTheme.colorScheme.surface
                        ),
                        border = if (isBookmarkedSurah)
                            androidx.compose.foundation.BorderStroke(1.2.dp, GoldAccent)
                        else if (isLastRead)
                            androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.6f))
                        else null,
                        onClick = { readingSurah = surah }
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
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Surah Number Badge
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isBookmarkedSurah) GoldAccent else MaterialTheme.colorScheme.primaryContainer,
                                    modifier = Modifier.size(38.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "${surah.number}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = if (isBookmarkedSurah) Color.Black else MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(
                                            text = "سورة ${surah.nameAr}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        if (isBookmarkedSurah) {
                                            Icon(
                                                imageVector = Icons.Filled.Bookmark,
                                                contentDescription = "علامة التوقف هنا",
                                                tint = GoldAccent,
                                                modifier = Modifier.size(15.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "${surah.nameEn} • ${surah.revelationType} • ${surah.versesCount} آية",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                        }
                    }
                }
            }
        }

        // Mini Audio Player bar
        if (playbackState.isPlaying || playbackState.isBuffering) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                tonalElevation = 8.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = GoldAccent,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Filled.Headphones, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                            }
                        }
                        Column {
                            Text(
                                text = playbackState.currentTitle.ifBlank { "تلاوة القرآن الكريم" },
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1
                            )
                            Text(
                                text = "${playbackState.currentSubtitle} • ${currentRiwayah.nameAr}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onTogglePlayPause) {
                            if (playbackState.isBuffering) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = GoldAccent)
                            } else {
                                Icon(
                                    imageVector = if (playbackState.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                    contentDescription = "Play/Pause",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        IconButton(onClick = onStopAudio) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Close",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // 📖 Full Mushaf Reading Dialog Modal (مثل الصورة: نص صافي مع نوع الرواية وتكبير/تصغير وبدون تفسير)
    val surahToRead = readingSurah
    if (surahToRead != null) {
        Dialog(
            onDismissRequest = { readingSurah = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("surah_reading_dialog"),
                color = MaterialTheme.colorScheme.background
            ) {
                MushafReadingView(
                    surah = surahToRead,
                    allSurahs = surahs,
                    loadedAyahs = loadedAyahs,
                    isLoadingAyahs = isLoadingAyahs,
                    currentRiwayah = currentRiwayah,
                    availableQaris = availableQarisForRiwayah,
                    selectedQari = selectedQari,
                    playbackState = playbackState,
                    currentFontSize = currentFontSize,
                    bookmarkSurahNumber = bookmarkSurahNumber,
                    bookmarkAyahNumber = bookmarkAyahNumber,
                    listState = listState,
                    onBack = { readingSurah = null },
                    onSelectSurah = { next -> readingSurah = next },
                    onOpenRiwayahPicker = { showRiwayahSheet = true },
                    onSelectQari = onSelectQari,
                    onPlayPauseSurah = {
                        val isPlayingThis = playbackState.currentTitle.contains(surahToRead.nameAr) &&
                            (playbackState.isPlaying || playbackState.isBuffering)
                        if (isPlayingThis) onTogglePlayPause()
                        else onPlaySurah(surahToRead, selectedQari)
                    },
                    onIncreaseFontSize = {
                        if (currentFontSize < 40) {
                            currentFontSize += 2
                            onUpdateFontSize(currentFontSize)
                        }
                    },
                    onDecreaseFontSize = {
                        if (currentFontSize > 14) {
                            currentFontSize -= 2
                            onUpdateFontSize(currentFontSize)
                        }
                    },
                    onSaveBookmarkAtAyah = { ayah ->
                        onSaveBookmark(
                            surahToRead.number,
                            surahToRead.nameAr,
                            ayah.numberInSurah,
                            ayah.textAr
                        )
                    }
                )
            }
        }
    }

    // 🕌 Modal Sheet: اختيار المصحف والرواية (ورش، حفص، قالون، الدوري، السوسي، شعبة)
    if (showRiwayahSheet) {
        AlertDialog(
            onDismissRequest = { showRiwayahSheet = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Filled.AutoStories, contentDescription = null, tint = GoldAccent)
                    Text(text = "اختر المصحف والرواية المعتمدة", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Text(
                            text = "تتيح لك هذه الميزة القراءة والاستماع وفق الروايات المتواترة المعتمدة بفرش حروفها وأصولها وقرائها المتقنين:",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 16.sp
                        )
                    }

                    items(QuranRiwayahsDataset.riwayahs) { riwayah ->
                        val isSelected = riwayah.id == selectedRiwayahId
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelectRiwayah(riwayah.id)
                                    showRiwayahSheet = false
                                },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected)
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                else
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            ),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, GoldAccent) else null
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = riwayah.nameAr,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                    if (isSelected) {
                                        Surface(shape = CircleShape, color = GoldAccent, modifier = Modifier.size(20.dp)) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(Icons.Filled.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                                            }
                                        }
                                    }
                                }

                                Text(
                                    text = "الراوي: ${riwayah.rawiName}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "أماكن الانتشار: ${riwayah.regionAr}",
                                    fontSize = 10.sp,
                                    color = GoldAccent
                                )

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "أبرز القواعد: ${riwayah.keyRules.take(2).joinToString(" • ")}",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showRiwayahSheet = false }) {
                    Text("إغلاق")
                }
            }
        )
    }
}
