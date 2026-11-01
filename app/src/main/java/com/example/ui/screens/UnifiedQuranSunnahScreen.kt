package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.Ayah
import com.example.data.models.Hadith
import com.example.data.models.Qari
import com.example.data.models.Surah
import com.example.data.services.PlaybackState
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.GoldAccent

@Composable
fun UnifiedQuranSunnahScreen(
    // Quran datasets & callbacks
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
    // Quran Riwayahs
    selectedRiwayahId: String = "hafs",
    onSelectRiwayah: (String) -> Unit = {},
    // Quran Translations
    selectedTranslationLang: String = "en",
    isTranslationVisible: Boolean = true,
    onSelectTranslationLang: (String) -> Unit = {},
    onToggleTranslationVisibility: (Boolean) -> Unit = {},
    savedFontSize: Int = 22,
    onUpdateFontSize: (Int) -> Unit = {},
    // Sunnah datasets & callbacks
    hadiths: List<Hadith>,
    onSpeakHadith: (String) -> Unit,
    onStopSpeaking: () -> Unit,
    isSpeaking: Boolean,
    // Audio Player for Recitation Corrector
    onPlayAudio: (url: String, title: String, subtitle: String) -> Unit,
    // Navigation & drawer
    onOpenDrawer: () -> Unit,
    onBack: (() -> Unit)? = null,
    initialMainTab: Int = 0,
    initialQuranSubTab: Int = 0,
    modifier: Modifier = Modifier
) {
    // Top Tab: 0 = Quran, 1 = Sunnah
    var mainTab by remember { mutableIntStateOf(initialMainTab) }

    // Inside Quran Tab: 0 = مصحح التلاوة والتصحيح الصوتي بالأحمر, 1 = المصحف الشريف
    var quranSubTab by remember { mutableIntStateOf(initialQuranSubTab) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("unified_quran_sunnah_screen")
    ) {
        // Top Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 3.dp
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Drawer Button & Back Button
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (onBack != null) {
                            IconButton(
                                onClick = onBack,
                                modifier = Modifier.testTag("back_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ArrowBack,
                                    contentDescription = "الرجوع",
                                    tint = GoldAccent
                                )
                            }
                        }

                        IconButton(
                            onClick = onOpenDrawer,
                            modifier = Modifier
                                .testTag("unified_open_drawer_button")
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = "فتح قائمة الأقسام",
                                tint = GoldAccent,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Title
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "القرآن الكريم والسنة النبوية",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldAccent,
                            textAlign = TextAlign.End
                        )
                        Text(
                            text = "قسم موحد يجمع التلاوة المصححة، المصحف، وتراث السنة النبوية",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.End
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Primary Tabs: القرآن الكريم vs السنة النبوية
                TabRow(
                    selectedTabIndex = mainTab,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    contentColor = GoldAccent,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    Tab(
                        selected = mainTab == 0,
                        onClick = { mainTab = 0 },
                        text = {
                            Text(
                                "القرآن الكريم",
                                fontWeight = if (mainTab == 0) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp
                            )
                        },
                        icon = {
                            Icon(Icons.Filled.MenuBook, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                    )
                    Tab(
                        selected = mainTab == 1,
                        onClick = { mainTab = 1 },
                        text = {
                            Text(
                                "السنة النبوية (${hadiths.size})",
                                fontWeight = if (mainTab == 1) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp
                            )
                        },
                        icon = {
                            Icon(Icons.Filled.Bookmark, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                    )
                }

                // If inside Quran tab, show the two requested sub-tabs:
                // 1) مصحح التلاوة وتصحيح الأخطاء بالأحمر
                // 2) المصحف الشريف (الترجمة، الإعراب، التفسير، علامة الحفظ، الرواية)
                if (mainTab == 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Sub-Tab 0: مصحح التلاوة
                        Surface(
                            onClick = { quranSubTab = 0 },
                            shape = RoundedCornerShape(10.dp),
                            color = if (quranSubTab == 0) EmeraldLight.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                            border = if (quranSubTab == 0) androidx.compose.foundation.BorderStroke(1.dp, EmeraldLight) else null,
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Mic,
                                    contentDescription = null,
                                    tint = if (quranSubTab == 0) EmeraldLight else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "مصحح التلاوة والأخطاء",
                                    fontSize = 11.sp,
                                    fontWeight = if (quranSubTab == 0) FontWeight.Bold else FontWeight.Normal,
                                    color = if (quranSubTab == 0) EmeraldLight else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Sub-Tab 1: المصحف الشريف
                        Surface(
                            onClick = { quranSubTab = 1 },
                            shape = RoundedCornerShape(10.dp),
                            color = if (quranSubTab == 1) GoldAccent.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                            border = if (quranSubTab == 1) androidx.compose.foundation.BorderStroke(1.dp, GoldAccent) else null,
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.AutoStories,
                                    contentDescription = null,
                                    tint = if (quranSubTab == 1) GoldAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "المصحف الشريف",
                                    fontSize = 11.sp,
                                    fontWeight = if (quranSubTab == 1) FontWeight.Bold else FontWeight.Normal,
                                    color = if (quranSubTab == 1) GoldAccent else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // Main Tab Content
        Box(modifier = Modifier.weight(1f)) {
            if (mainTab == 0) {
                if (quranSubTab == 0) {
                    // 1. مصحح التلاوة والأخطاء بالأحمر
                    RecitationCorrectorView(
                        surahs = surahs,
                        selectedQari = selectedQari,
                        playbackState = playbackState,
                        onPlayAudio = onPlayAudio,
                        onStopAudio = onStopAudio,
                        onLoadAyahs = onLoadAyahs,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // 2. المصحف الشريف (التراجم، الإعراب، التفسير، الروايات، علامة الحفظ)
                    QuranScreen(
                        surahs = surahs,
                        qaris = qaris,
                        selectedQari = selectedQari,
                        playbackState = playbackState,
                        lastReadSurahNumber = lastReadSurahNumber,
                        onSelectQari = onSelectQari,
                        onPlaySurah = onPlaySurah,
                        onTogglePlayPause = onTogglePlayPause,
                        onStopAudio = onStopAudio,
                        onLoadAyahs = onLoadAyahs,
                        onSaveLastRead = onSaveLastRead,
                        bookmarkSurahNumber = bookmarkSurahNumber,
                        bookmarkAyahNumber = bookmarkAyahNumber,
                        bookmarkSurahName = bookmarkSurahName,
                        bookmarkAyahText = bookmarkAyahText,
                        hasActiveBookmark = hasActiveBookmark,
                        onSaveBookmark = onSaveBookmark,
                        onClearBookmark = onClearBookmark,
                        selectedRiwayahId = selectedRiwayahId,
                        onSelectRiwayah = onSelectRiwayah,
                        selectedTranslationLang = selectedTranslationLang,
                        isTranslationVisible = isTranslationVisible,
                        onSelectTranslationLang = onSelectTranslationLang,
                        onToggleTranslationVisibility = onToggleTranslationVisibility,
                        savedFontSize = savedFontSize,
                        onUpdateFontSize = onUpdateFontSize,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            } else {
                // 3. السنة النبوية الشريفة
                SunnahScreen(
                    hadiths = hadiths,
                    onSpeakHadith = onSpeakHadith,
                    onStopSpeaking = onStopSpeaking,
                    isSpeaking = isSpeaking,
                    onBack = { mainTab = 0 },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
