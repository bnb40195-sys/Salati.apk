package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.data.local.QuranRiwayahsDataset
import com.example.data.models.Ayah
import com.example.data.models.Hadith
import com.example.data.models.Qari
import com.example.data.models.Surah
import com.example.data.services.PlaybackState

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
    initialSection: QuranHubSection = if (initialMainTab == 1) QuranHubSection.SUNNAH else QuranHubSection.HUB,
    modifier: Modifier = Modifier
) {
    var activeSection by remember { mutableStateOf(initialSection) }
    var pendingSurahToOpen by remember { mutableStateOf<Surah?>(null) }

    val currentRiwayah = remember(selectedRiwayahId) {
        QuranRiwayahsDataset.getRiwayah(selectedRiwayahId)
    }

    // Intercept system back button when inside any sub-section to return to Hub
    BackHandler(enabled = activeSection != QuranHubSection.HUB) {
        pendingSurahToOpen = null
        activeSection = QuranHubSection.HUB
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("unified_quran_sunnah_screen")
    ) {
        Crossfade(
            targetState = activeSection,
            label = "quran_hub_section_transition"
        ) { section ->
            when (section) {
                QuranHubSection.HUB -> {
                    QuranHubScreen(
                        surahs = surahs,
                        bookmarkSurahName = bookmarkSurahName,
                        bookmarkAyahNumber = bookmarkAyahNumber,
                        hasActiveBookmark = hasActiveBookmark,
                        currentRiwayahName = currentRiwayah.nameAr,
                        playbackState = playbackState,
                        onSelectSection = { target ->
                            pendingSurahToOpen = null
                            activeSection = target
                        },
                        onResumeBookmark = {
                            pendingSurahToOpen = surahs.find { it.number == bookmarkSurahNumber }
                            activeSection = QuranHubSection.MUSHAF
                        },
                        onOpenDrawer = onOpenDrawer,
                        onBack = onBack,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                QuranHubSection.MUSHAF -> {
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
                        onBackToHub = {
                            pendingSurahToOpen = null
                            activeSection = QuranHubSection.HUB
                        },
                        initialReadingSurah = pendingSurahToOpen,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                QuranHubSection.RECITATION -> {
                    QuranRecitationView(
                        surahs = surahs,
                        qaris = qaris,
                        selectedQari = selectedQari,
                        selectedRiwayahId = selectedRiwayahId,
                        playbackState = playbackState,
                        onSelectQari = onSelectQari,
                        onSelectRiwayah = onSelectRiwayah,
                        onPlaySurah = onPlaySurah,
                        onTogglePlayPause = onTogglePlayPause,
                        onStopAudio = onStopAudio,
                        onBack = { activeSection = QuranHubSection.HUB },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                QuranHubSection.SEARCH -> {
                    QuranSearchView(
                        surahs = surahs,
                        onOpenSurah = { surah, _ ->
                            pendingSurahToOpen = surah
                            activeSection = QuranHubSection.MUSHAF
                        },
                        onBack = { activeSection = QuranHubSection.HUB },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                QuranHubSection.IRAB -> {
                    QuranIrabView(
                        surahs = surahs,
                        onLoadAyahs = onLoadAyahs,
                        onBack = { activeSection = QuranHubSection.HUB },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                QuranHubSection.TAFSIR -> {
                    QuranTafsirView(
                        surahs = surahs,
                        onLoadAyahs = onLoadAyahs,
                        onBack = { activeSection = QuranHubSection.HUB },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                QuranHubSection.TRANSLATION -> {
                    QuranTranslationView(
                        surahs = surahs,
                        selectedLang = selectedTranslationLang,
                        onSelectLang = onSelectTranslationLang,
                        onLoadAyahs = onLoadAyahs,
                        onBack = { activeSection = QuranHubSection.HUB },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                QuranHubSection.SMART_RECITATION -> {
                    RecitationCorrectorView(
                        surahs = surahs,
                        selectedQari = selectedQari,
                        playbackState = playbackState,
                        onPlayAudio = onPlayAudio,
                        onStopAudio = onStopAudio,
                        onLoadAyahs = onLoadAyahs,
                        onBack = { activeSection = QuranHubSection.HUB },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                QuranHubSection.SUNNAH -> {
                    SunnahScreen(
                        hadiths = hadiths,
                        onSpeakHadith = onSpeakHadith,
                        onStopSpeaking = onStopSpeaking,
                        isSpeaking = isSpeaking,
                        onBack = { activeSection = QuranHubSection.HUB },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}
