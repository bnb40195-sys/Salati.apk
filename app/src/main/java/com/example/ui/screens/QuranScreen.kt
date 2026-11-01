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
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var readingSurah by remember { mutableStateOf<Surah?>(null) }
    var loadedAyahs by remember { mutableStateOf<List<Ayah>>(emptyList()) }
    var isLoadingAyahs by remember { mutableStateOf(false) }

    // Reading mode: true = Mushaf Page view (نص المصحف المتصل), false = Ayah by Ayah list
    var isMushafPageView by remember { mutableStateOf(false) }
    var currentFontSize by remember { mutableStateOf(savedFontSize) }

    // Dialog & Sheet states
    var selectedAyahForDetails by remember { mutableStateOf<Ayah?>(null) }
    var detailsTab by remember { mutableIntStateOf(0) } // 0: التفسير, 1: الإعراب, 2: الترجمة
    var showTafsirInline by remember { mutableStateOf(true) }
    var showIrabInline by remember { mutableStateOf(true) }
    var showTranslationInline by remember { mutableStateOf(isTranslationVisible) }

    var showRiwayahSheet by remember { mutableStateOf(false) }
    var showRiwayahDifferencesDialog by remember { mutableStateOf(false) }
    var showTranslationSheet by remember { mutableStateOf(false) }
    var showBookmarkFeedbackSnackbar by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val currentRiwayah = remember(selectedRiwayahId) {
        QuranRiwayahsDataset.getRiwayah(selectedRiwayahId)
    }

    val currentTranslationEdition = remember(selectedTranslationLang) {
        QuranTranslationsDataset.getEdition(selectedTranslationLang)
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
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "القرآن الكريم",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                // Active Riwayah Badge
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = GoldAccent.copy(alpha = 0.2f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.6f)),
                                    modifier = Modifier.clickable { showRiwayahSheet = true }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = currentRiwayah.nameAr,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GoldAccent
                                        )
                                        Icon(
                                            imageVector = Icons.Filled.KeyboardArrowDown,
                                            contentDescription = null,
                                            tint = GoldAccent,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                            Text(
                                text = "114 سورة • مصاحف متعددة وترجمة معاني القرآن",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Qari / Reciter Selector
                        var showQariMenu by remember { mutableStateOf(false) }
                        Box {
                            OutlinedButton(
                                onClick = { showQariMenu = true },
                                shape = RoundedCornerShape(20.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.RecordVoiceOver,
                                    contentDescription = null,
                                    tint = GoldAccent,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = selectedQari.nameAr.split(" ").take(2).joinToString(" "),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Icon(
                                    imageVector = Icons.Filled.ArrowDropDown,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            DropdownMenu(
                                expanded = showQariMenu,
                                onDismissRequest = { showQariMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "قراء ${currentRiwayah.nameAr}:",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    },
                                    onClick = {},
                                    enabled = false
                                )
                                availableQarisForRiwayah.forEach { qari ->
                                    DropdownMenuItem(
                                        text = { Text(qari.nameAr, fontSize = 13.sp) },
                                        onClick = {
                                            onSelectQari(qari)
                                            showQariMenu = false
                                        },
                                        leadingIcon = {
                                            if (qari.id == selectedQari.id) {
                                                Icon(Icons.Filled.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                            }
                                        }
                                    )
                                }
                            }
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
                // 🔖 Quran Reading Bookmark Card (فاصل القراءة ومكان التوقف)
                if (hasActiveBookmark) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp)
                                .testTag("quran_bookmark_card"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.2.dp, GoldAccent.copy(alpha = 0.7f))
                        ) {
                            Row(
                                modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = GoldAccent,
                                    modifier = Modifier.size(42.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Filled.Bookmark,
                                            contentDescription = "Bookmark",
                                            tint = Color.Black,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(
                                            text = "فاصل القراءة ومكان التوقف 🔖",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = GoldAccent.copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                text = "آخر قراءة",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = GoldAccent,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "سورة $bookmarkSurahName • الآية ($bookmarkAyahNumber)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    if (bookmarkAyahText.isNotBlank()) {
                                        Text(
                                            text = "﴿${bookmarkAyahText.take(45)}...﴾",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1
                                        )
                                    }
                                }

                                Button(
                                    onClick = {
                                        val targetSurah = surahs.find { it.number == bookmarkSurahNumber } ?: surahs.firstOrNull()
                                        if (targetSurah != null) {
                                            readingSurah = targetSurah
                                        }
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Filled.MenuBook, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("تابع القراءة", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                }
                            }
                        }
                    }
                }

                // Quick Tools Row: Riwayahs & Translations info buttons
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { showRiwayahSheet = true },
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Filled.AutoStories, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                                Column {
                                    Text(text = "المصحف والرواية", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(text = currentRiwayah.nameAr, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                }
                            }
                        }

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { showTranslationSheet = true },
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Filled.Translate, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                Column {
                                    Text(text = "ترجمة المعاني", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(
                                        text = "${currentTranslationEdition.flagEmoji} ${currentTranslationEdition.languageNameNative}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }

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

                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                // Read button
                                TextButton(
                                    onClick = { readingSurah = surah },
                                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                                ) {
                                    Icon(Icons.Filled.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("قراءة", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                }

                                // Play audio recitation button
                                IconButton(onClick = { onPlaySurah(surah, selectedQari) }) {
                                    Icon(
                                        imageVector = Icons.Filled.PlayCircleOutline,
                                        contentDescription = "Listen",
                                        tint = GoldAccent,
                                        modifier = Modifier.size(26.dp)
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

    // 📖 Full Mushaf Reading Dialog Modal
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
                Column(modifier = Modifier.fillMaxSize()) {
                    // Modal Top Bar
                    TopAppBar(
                        title = {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        text = "سورة ${surahToRead.nameAr}",
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = GoldAccent.copy(alpha = 0.2f),
                                        modifier = Modifier.clickable { showRiwayahSheet = true }
                                    ) {
                                        Text(
                                            text = currentRiwayah.nameAr.replace("رواية ", ""),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GoldAccent,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "${surahToRead.revelationType} • ${surahToRead.versesCount} آيات • ص ${surahToRead.pageNumber}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = { readingSurah = null }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                            }
                        },
                        actions = {
                            // Jump to Bookmark button
                            if (bookmarkSurahNumber == surahToRead.number) {
                                IconButton(
                                    onClick = {
                                        coroutineScope.launch {
                                            val targetIndex = (bookmarkAyahNumber - 1).coerceAtLeast(0)
                                            listState.animateScrollToItem(targetIndex)
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Bookmark,
                                        contentDescription = "انتقال إلى علامة التوقف",
                                        tint = GoldAccent
                                    )
                                }
                            }

                            // Riwayah Differences Button
                            IconButton(onClick = { showRiwayahDifferencesDialog = true }) {
                                Icon(
                                    imageVector = Icons.Filled.AutoStories,
                                    contentDescription = "فروق الرواية",
                                    tint = GoldAccent
                                )
                            }

                            // Translations Switcher / Settings
                            IconButton(onClick = { showTranslationSheet = true }) {
                                Icon(
                                    imageVector = Icons.Filled.Translate,
                                    contentDescription = "ترجمة المعاني",
                                    tint = if (showTranslationInline) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Toggle View Mode (نص المصحف المتصل أو آية بآية)
                            IconButton(onClick = { isMushafPageView = !isMushafPageView }) {
                                Icon(
                                    imageVector = if (isMushafPageView) Icons.Filled.ViewList else Icons.Filled.MenuBook,
                                    contentDescription = "Toggle View",
                                    tint = GoldAccent
                                )
                            }

                            // Font size controls
                            IconButton(onClick = {
                                if (currentFontSize < 34) {
                                    currentFontSize += 2
                                    onUpdateFontSize(currentFontSize)
                                }
                            }) {
                                Text("A+", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            IconButton(onClick = {
                                if (currentFontSize > 16) {
                                    currentFontSize -= 2
                                    onUpdateFontSize(currentFontSize)
                                }
                            }) {
                                Text("A-", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
                    )

                    // Secondary Subheader for Display Toggles
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                        tonalElevation = 1.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Filled.Tune, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(text = "عرض:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                FilterChip(
                                    selected = showTranslationInline,
                                    onClick = {
                                        showTranslationInline = !showTranslationInline
                                        onToggleTranslationVisibility(showTranslationInline)
                                    },
                                    label = { Text("الترجمة (${currentTranslationEdition.flagEmoji})", fontSize = 10.sp) },
                                    leadingIcon = {
                                        Icon(Icons.Filled.Translate, contentDescription = null, modifier = Modifier.size(12.dp))
                                    },
                                    shape = RoundedCornerShape(10.dp)
                                )

                                FilterChip(
                                    selected = showTafsirInline,
                                    onClick = { showTafsirInline = !showTafsirInline },
                                    label = { Text("التفسير", fontSize = 10.sp) },
                                    shape = RoundedCornerShape(10.dp)
                                )

                                FilterChip(
                                    selected = showIrabInline,
                                    onClick = { showIrabInline = !showIrabInline },
                                    label = { Text("الإعراب", fontSize = 10.sp) },
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }
                        }
                    }

                    // Floating feedback toast for bookmark
                    if (showBookmarkFeedbackSnackbar != null) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            color = GoldAccent,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(Icons.Filled.Bookmark, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                    Text(text = showBookmarkFeedbackSnackbar!!, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                }
                                IconButton(onClick = { showBookmarkFeedbackSnackbar = null }, modifier = Modifier.size(20.dp)) {
                                    Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.Black, modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                    }

                    if (isLoadingAyahs) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        }
                    } else if (isMushafPageView) {
                        // 📖 FULL MUSHAF CONTINUOUS TEXT MODE (نص المصحف المتصل)
                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            contentPadding = PaddingValues(top = 16.dp, bottom = 60.dp)
                        ) {
                            // Surah Ornate Frame Header
                            item {
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 10.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldAccent)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "۞ سُورَةُ ${surahToRead.nameAr} ۞",
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = GoldAccent
                                        )
                                        Text(
                                            text = "برواية ${currentRiwayah.nameAr} • ${surahToRead.revelationType} • ${surahToRead.versesCount} آيات",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            // Basmalah (except for At-Tawbah 9)
                            if (surahToRead.number != 9) {
                                item {
                                    Text(
                                        text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                                        fontSize = (currentFontSize + 2).sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldAccent,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 12.dp)
                                    )
                                }
                            }

                            // Continuous Mushaf Text Card
                            item {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surface
                                    )
                                ) {
                                    val fullMushafText = buildAnnotatedString {
                                        loadedAyahs.forEach { ayah ->
                                            val isBookmarkedAyah = (surahToRead.number == bookmarkSurahNumber && ayah.numberInSurah == bookmarkAyahNumber)
                                            if (isBookmarkedAyah) {
                                                withStyle(
                                                    style = SpanStyle(
                                                        background = GoldAccent.copy(alpha = 0.35f),
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.primary
                                                    )
                                                ) {
                                                    append(" [🔖 فاصل القراءة] ")
                                                    append(ayah.textAr)
                                                }
                                            } else {
                                                append(ayah.textAr)
                                            }

                                            // Decorative Ayah end symbol with verse number ۝
                                            withStyle(
                                                style = SpanStyle(
                                                    color = GoldAccent,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = (currentFontSize - 4).sp
                                                )
                                            ) {
                                                append(" ۝${ayah.numberInSurah} ")
                                            }
                                        }
                                    }

                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(
                                            text = fullMushafText,
                                            fontSize = currentFontSize.sp,
                                            lineHeight = (currentFontSize * 1.85).sp,
                                            textAlign = TextAlign.Justify,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }

                            // Quick Ayah Navigator Grid
                            item {
                                Text(
                                    text = "تصفح سريع ووضع علامة التوقف عند آية معينة:",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                                )
                            }

                            item {
                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    loadedAyahs.forEach { a ->
                                        val isCurrentBookmark = (surahToRead.number == bookmarkSurahNumber && a.numberInSurah == bookmarkAyahNumber)
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (isCurrentBookmark) GoldAccent else MaterialTheme.colorScheme.surfaceVariant,
                                            border = if (isCurrentBookmark) androidx.compose.foundation.BorderStroke(1.dp, Color.Black) else null,
                                            onClick = { selectedAyahForDetails = a }
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                                            ) {
                                                if (isCurrentBookmark) {
                                                    Icon(Icons.Filled.Bookmark, contentDescription = null, tint = Color.Black, modifier = Modifier.size(11.dp))
                                                }
                                                Text(
                                                    text = "آية ${a.numberInSurah}",
                                                    fontSize = 11.sp,
                                                    fontWeight = if (isCurrentBookmark) FontWeight.Bold else FontWeight.SemiBold,
                                                    color = if (isCurrentBookmark) Color.Black else MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        // 📜 VERSE BY VERSE WITH TRANSLATION, TAFSIR & IRAB MODE
                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(14.dp),
                            contentPadding = PaddingValues(bottom = 60.dp)
                        ) {
                            if (surahToRead.number != 9) {
                                item {
                                    Text(
                                        text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldAccent,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 12.dp)
                                    )
                                }
                            }

                            items(loadedAyahs.size) { idx ->
                                val ayah = loadedAyahs[idx]
                                val isBookmarkedAyah = (surahToRead.number == bookmarkSurahNumber && ayah.numberInSurah == bookmarkAyahNumber)
                                val translationText = ayah.getTranslation(selectedTranslationLang)

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isBookmarkedAyah)
                                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                                        else
                                            MaterialTheme.colorScheme.surface
                                    ),
                                    border = if (isBookmarkedAyah) androidx.compose.foundation.BorderStroke(1.5.dp, GoldAccent) else null
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        // Header Row: Ayah Number, Bookmark Button, Details, Copy
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                Surface(
                                                    shape = CircleShape,
                                                    color = if (isBookmarkedAyah) GoldAccent else MaterialTheme.colorScheme.primaryContainer,
                                                    modifier = Modifier.size(28.dp)
                                                ) {
                                                    Box(contentAlignment = Alignment.Center) {
                                                        Text(
                                                            text = "${ayah.numberInSurah}",
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = if (isBookmarkedAyah) Color.Black else MaterialTheme.colorScheme.primary
                                                        )
                                                    }
                                                }

                                                if (isBookmarkedAyah) {
                                                    Surface(
                                                        shape = RoundedCornerShape(6.dp),
                                                        color = GoldAccent
                                                    ) {
                                                        Row(
                                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                                                        ) {
                                                            Icon(Icons.Filled.Bookmark, contentDescription = null, tint = Color.Black, modifier = Modifier.size(12.dp))
                                                            Text(
                                                                text = "مكان توقفك الأخير (علامة القراءة)",
                                                                fontSize = 10.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = Color.Black
                                                            )
                                                        }
                                                    }
                                                } else {
                                                    Text(
                                                        text = "الآية الكريمة",
                                                        fontSize = 11.sp,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }

                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                // 🔖 Bookmark toggle button for this exact ayah
                                                IconButton(
                                                    onClick = {
                                                        onSaveBookmark(
                                                            surahToRead.number,
                                                            surahToRead.nameAr,
                                                            ayah.numberInSurah,
                                                            ayah.textAr
                                                        )
                                                        showBookmarkFeedbackSnackbar = "تم حفظ علامة التوقف عند الآية ${ayah.numberInSurah} من سورة ${surahToRead.nameAr}"
                                                    },
                                                    modifier = Modifier.size(32.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = if (isBookmarkedAyah) Icons.Filled.Bookmark else Icons.Outlined.BookmarkAdd,
                                                        contentDescription = "علامة التوقف",
                                                        tint = if (isBookmarkedAyah) GoldAccent else MaterialTheme.colorScheme.primary,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                }

                                                // Open Full Details (Tafsir, I'rab & Translation modal)
                                                IconButton(
                                                    onClick = {
                                                        detailsTab = 0
                                                        selectedAyahForDetails = ayah
                                                    },
                                                    modifier = Modifier.size(32.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Filled.AutoStories,
                                                        contentDescription = "شرح الآية",
                                                        tint = MaterialTheme.colorScheme.primary,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }

                                                // Copy Ayah text
                                                val clipboard = LocalClipboardManager.current
                                                IconButton(
                                                    onClick = {
                                                        clipboard.setText(AnnotatedString("﴿${ayah.textAr}﴾ [${surahToRead.nameAr}: ${ayah.numberInSurah}]"))
                                                        showBookmarkFeedbackSnackbar = "تم نسخ نص الآية إلى الحافظة"
                                                    },
                                                    modifier = Modifier.size(32.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Filled.ContentCopy,
                                                        contentDescription = "نسخ",
                                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        // Arabic Ayah Text
                                        Text(
                                            text = "﴿${ayah.textAr}﴾",
                                            fontSize = currentFontSize.sp,
                                            fontWeight = FontWeight.Bold,
                                            lineHeight = (currentFontSize * 1.8).sp,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            textAlign = TextAlign.Right,
                                            modifier = Modifier.fillMaxWidth()
                                        )

                                        // 🌐 Quran Translation inline
                                        if (showTranslationInline && translationText.isNotBlank()) {
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Surface(
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(10.dp),
                                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                                                border = androidx.compose.foundation.BorderStroke(0.8.dp, MaterialTheme.colorScheme.outlineVariant)
                                            ) {
                                                Column(modifier = Modifier.padding(10.dp)) {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                            Text(text = currentTranslationEdition.flagEmoji, fontSize = 12.sp)
                                                            Text(
                                                                text = "${currentTranslationEdition.languageNameNative} (${currentTranslationEdition.translatorName})",
                                                                fontSize = 10.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = MaterialTheme.colorScheme.primary
                                                            )
                                                        }
                                                        Text(
                                                            text = "الترجمة",
                                                            fontSize = 9.sp,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                    }
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Text(
                                                        text = translationText,
                                                        fontSize = 13.sp,
                                                        lineHeight = 19.sp,
                                                        color = MaterialTheme.colorScheme.onSurface,
                                                        textAlign = if (selectedTranslationLang == "ur") TextAlign.Right else TextAlign.Left
                                                    )
                                                }
                                            }
                                        }

                                        // Inline Tafsir
                                        if (showTafsirInline && ayah.tafsirShort.isNotBlank()) {
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Surface(
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(10.dp),
                                                color = EmeraldLight.copy(alpha = 0.08f),
                                                border = androidx.compose.foundation.BorderStroke(0.8.dp, EmeraldLight.copy(alpha = 0.25f))
                                            ) {
                                                Column(modifier = Modifier.padding(10.dp)) {
                                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                        Icon(Icons.Filled.AutoStories, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(13.dp))
                                                        Text(
                                                            text = "التفسير الميسر:",
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = EmeraldLight
                                                        )
                                                    }
                                                    Spacer(modifier = Modifier.height(2.dp))
                                                    Text(
                                                        text = ayah.tafsirShort,
                                                        fontSize = 12.sp,
                                                        lineHeight = 18.sp,
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                }
                                            }
                                        }

                                        // Inline I'rab
                                        if (showIrabInline && ayah.irabShort.isNotBlank()) {
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Surface(
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(10.dp),
                                                color = GoldAccent.copy(alpha = 0.08f),
                                                border = androidx.compose.foundation.BorderStroke(0.8.dp, GoldAccent.copy(alpha = 0.25f))
                                            ) {
                                                Column(modifier = Modifier.padding(10.dp)) {
                                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                        Icon(Icons.Filled.MenuBook, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(13.dp))
                                                        Text(
                                                            text = "إعراب الآية:",
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = GoldAccent
                                                        )
                                                    }
                                                    Spacer(modifier = Modifier.height(2.dp))
                                                    Text(
                                                        text = ayah.irabShort,
                                                        fontSize = 11.sp,
                                                        lineHeight = 17.sp,
                                                        color = MaterialTheme.colorScheme.onSurface
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

    // 🌐 Modal Sheet: اختيار لغة الترجمة
    if (showTranslationSheet) {
        AlertDialog(
            onDismissRequest = { showTranslationSheet = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Filled.Translate, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text(text = "ترجمة معاني القرآن الكريم", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "تفعيل عرض الترجمة تحت الآيات:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Switch(
                            checked = showTranslationInline,
                            onCheckedChange = {
                                showTranslationInline = it
                                onToggleTranslationVisibility(it)
                            }
                        )
                    }

                    Text(
                        text = "اختر لغة الترجمة المعتمدة:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 280.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(QuranTranslationsDataset.editions) { edition ->
                            val isSelected = edition.code == selectedTranslationLang
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onSelectTranslationLang(edition.code)
                                        showTranslationInline = true
                                        onToggleTranslationVisibility(true)
                                        showTranslationSheet = false
                                    },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected)
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                else
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.2.dp, MaterialTheme.colorScheme.primary) else null
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text(text = edition.flagEmoji, fontSize = 18.sp)
                                        Column {
                                            Text(
                                                text = "${edition.languageNameNative} (${edition.languageNameAr})",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = edition.translatorName,
                                                fontSize = 10.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    if (isSelected) {
                                        Icon(Icons.Filled.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showTranslationSheet = false }) {
                    Text("حسناً")
                }
            }
        )
    }

    // 📜 Modal: فروق الروايات وأصول القراءة
    if (showRiwayahDifferencesDialog && readingSurah != null) {
        val s = readingSurah!!
        val diffList = remember(s.number, selectedRiwayahId) {
            QuranRiwayahsDataset.getDifferencesForSurah(s.number, selectedRiwayahId)
        }
        AlertDialog(
            onDismissRequest = { showRiwayahDifferencesDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Filled.AutoStories, contentDescription = null, tint = GoldAccent)
                    Text(text = "فروق ${currentRiwayah.nameAr} في سورة ${s.nameAr}", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "📖 قاعدة البسملة في هذه الرواية:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = currentRiwayah.basmalahRule,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    if (diffList.isEmpty()) {
                        Text(
                            text = "تتفق هذه السورة الكريمة في معظم كلماتها وأصولها مع الرواية، مع مراعاة قواعد الأصول العامة (كالترقيق والتغليظ والنقل والإمالة).",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 260.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(diffList) { diff ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(text = "الآية (${diff.ayahNumber})", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                                            Text(text = diff.ruleCategoryAr, fontSize = 10.sp, color = MaterialTheme.colorScheme.primary)
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(text = "حفص: ﴿${diff.hafsText}﴾", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(text = "${currentRiwayah.nameAr.replace("رواية ", "")}: ﴿${diff.riwayahText}﴾", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(text = diff.explanationAr, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showRiwayahDifferencesDialog = false }) {
                    Text("إغلاق")
                }
            }
        )
    }

    // Modal: بطاقة الآية الكريمة (التفسير الميسر، الإعراب، والترجمة)
    if (selectedAyahForDetails != null) {
        val ayah = selectedAyahForDetails!!
        val clipboard = LocalClipboardManager.current
        AlertDialog(
            onDismissRequest = { selectedAyahForDetails = null },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "الآية (${ayah.numberInSurah})",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // Tab selector between Tafsir, I'rab, and Translation
                    Row {
                        FilterChip(
                            selected = detailsTab == 0,
                            onClick = { detailsTab = 0 },
                            label = { Text("التفسير", fontSize = 10.sp) },
                            shape = RoundedCornerShape(8.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        FilterChip(
                            selected = detailsTab == 1,
                            onClick = { detailsTab = 1 },
                            label = { Text("الإعراب", fontSize = 10.sp) },
                            shape = RoundedCornerShape(8.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        FilterChip(
                            selected = detailsTab == 2,
                            onClick = { detailsTab = 2 },
                            label = { Text("الترجمة 🌐", fontSize = 10.sp) },
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Ayah Text Box
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "﴿${ayah.textAr}﴾",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(12.dp),
                            lineHeight = 24.sp
                        )
                    }

                    when (detailsTab) {
                        0 -> {
                            // Tafsir Tab
                            Text(
                                text = "📖 التفسير الميسر:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldLight
                            )
                            Text(
                                text = if (ayah.tafsirShort.isNotBlank()) ayah.tafsirShort else "بيان معاني الآية ومقاصدها الإيمانية السامية لتدبر كتاب الله والعمل به.",
                                fontSize = 13.sp,
                                lineHeight = 20.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        1 -> {
                            // I'rab Tab
                            Text(
                                text = "📐 إعراب القرآن الكريم:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldAccent
                            )
                            Text(
                                text = if (ayah.irabShort.isNotBlank()) ayah.irabShort else "إعراب مفردات وجمل الآية المباركة وقواعدها النحوية وموقعها الإعرابي.",
                                fontSize = 12.sp,
                                lineHeight = 19.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        else -> {
                            // Translation Tab
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "🌐 ترجمة معاني الآية (${currentTranslationEdition.flagEmoji} ${currentTranslationEdition.languageNameNative}):",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            val trans = ayah.getTranslation(selectedTranslationLang)
                            Text(
                                text = if (trans.isNotBlank()) trans else "Translation available for this verse.",
                                fontSize = 13.sp,
                                lineHeight = 19.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = if (selectedTranslationLang == "ur") TextAlign.Right else TextAlign.Left
                            )
                            Text(
                                text = "المترجم: ${currentTranslationEdition.translatorName}",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Set Bookmark button
                    Button(
                        onClick = {
                            if (readingSurah != null) {
                                onSaveBookmark(
                                    readingSurah!!.number,
                                    readingSurah!!.nameAr,
                                    ayah.numberInSurah,
                                    ayah.textAr
                                )
                                showBookmarkFeedbackSnackbar = "تم حفظ علامة التوقف عند الآية ${ayah.numberInSurah}"
                            }
                            selectedAyahForDetails = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
                    ) {
                        Icon(Icons.Filled.Bookmark, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("ضع فاصل هنا", color = Color.Black)
                    }

                    OutlinedButton(
                        onClick = {
                            val fullText = "﴿${ayah.textAr}﴾\nالتفسير: ${ayah.tafsirShort}\nالإعراب: ${ayah.irabShort}\nالترجمة (${currentTranslationEdition.languageNameNative}): ${ayah.getTranslation(selectedTranslationLang)}"
                            clipboard.setText(AnnotatedString(fullText))
                        }
                    ) {
                        Icon(Icons.Filled.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("نسخ")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedAyahForDetails = null }) {
                    Text("إغلاق")
                }
            }
        )
    }
}
