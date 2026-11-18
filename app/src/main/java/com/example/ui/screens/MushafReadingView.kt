package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BookmarkAdd
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.Ayah
import com.example.data.models.Qari
import com.example.data.models.QuranRiwayah
import com.example.data.models.Surah
import com.example.data.services.PlaybackState
import com.example.ui.theme.EmeraldBorderDark
import com.example.ui.theme.EmeraldCardDark
import com.example.ui.theme.EmeraldDeepDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldSurfaceDark
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.IslamicGold
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun MushafReadingView(
    surah: Surah,
    allSurahs: List<Surah>,
    loadedAyahs: List<Ayah>,
    isLoadingAyahs: Boolean,
    currentRiwayah: QuranRiwayah,
    availableQaris: List<Qari>,
    selectedQari: Qari,
    playbackState: PlaybackState,
    currentFontSize: Int,
    bookmarkSurahNumber: Int,
    bookmarkAyahNumber: Int,
    listState: LazyListState,
    onBack: () -> Unit,
    onSelectSurah: (Surah) -> Unit,
    onOpenRiwayahPicker: () -> Unit,
    onSelectQari: (Qari) -> Unit,
    onPlayPauseSurah: () -> Unit,
    onIncreaseFontSize: () -> Unit,
    onDecreaseFontSize: () -> Unit,
    onSaveBookmarkAtAyah: (Ayah) -> Unit,
    modifier: Modifier = Modifier
) {
    // Lightbulb toggle: Rich Islamic Emerald & Gold Night Mushaf (default) vs Warm Parchment Paper Mushaf
    var isLightReadingMode by remember { mutableStateOf(false) }
    var feedbackBanner by remember { mutableStateOf<String?>(null) }

    val nextSurah = remember(surah.number, allSurahs) {
        allSurahs.find { it.number == surah.number + 1 }
    }

    // Redesigned Islamic Emerald & Gold Palette (replaces the flat grey screenshot skin)
    val screenBackdropBrush = if (isLightReadingMode) {
        Brush.verticalGradient(listOf(Color(0xFFF6EFE0), Color(0xFFECE1C8)))
    } else {
        Brush.verticalGradient(listOf(EmeraldDeepDark, EmeraldSurfaceDark, EmeraldDeepDark))
    }
    val cardSurfaceColor = if (isLightReadingMode) Color(0xFFFFFDF7) else Color(0xFF0C261E)
    val cardOuterBorderColor = if (isLightReadingMode) Color(0xFFB38B3A) else GoldAccent.copy(alpha = 0.65f)
    val cardInnerBorderColor = if (isLightReadingMode) Color(0xFFD8C59E) else EmeraldBorderDark
    val dividerColor = if (isLightReadingMode) Color(0xFFE6D9BC) else Color(0xFF194235)
    val primaryTextColor = if (isLightReadingMode) Color(0xFF1E1A14) else Color(0xFFF4F8F5)
    val secondaryTextColor = if (isLightReadingMode) Color(0xFF6B5B42) else Color(0xFFA7C7BA)
    val rosetteColor = if (isLightReadingMode) Color(0xFF9E7422) else GoldAccentLightOrDefault()
    val plaqueBgColor = if (isLightReadingMode) Color(0xFFF3E8CF) else EmeraldCardDark
    val controlBgColor = if (isLightReadingMode) Color(0xFFEADEC4) else EmeraldCardDark
    val controlTextColor = if (isLightReadingMode) Color(0xFF2D2315) else Color.White

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(screenBackdropBrush)
                .statusBarsPadding()
                .navigationBarsPadding()
                .testTag("mushaf_reading_view")
        ) {
            // 1. Top Header Bar: Back button, Surah Pill, Riwayah Selector Pill
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    shape = CircleShape,
                    color = controlBgColor,
                    border = BorderStroke(1.dp, cardOuterBorderColor),
                    modifier = Modifier
                        .size(40.dp)
                        .clickable { onBack() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "الرجوع",
                            tint = controlTextColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Centered Surah Ornament Pill
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = plaqueBgColor,
                    border = BorderStroke(1.5.dp, cardOuterBorderColor),
                    shadowElevation = 3.dp,
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .padding(horizontal = 8.dp)
                ) {
                    Text(
                        text = "سُورَةُ ${surah.nameAr}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isLightReadingMode) Color(0xFF4B3713) else GoldAccent,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 22.dp, vertical = 6.dp)
                    )
                }

                // Riwayah Picker Pill ("حفص عن عاصم ⌄")
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isLightReadingMode) Color(0xFFE7D6B0) else Color(0xFF1F2B18),
                    border = BorderStroke(1.dp, cardOuterBorderColor),
                    modifier = Modifier
                        .testTag("mushaf_riwayah_button")
                        .clickable { onOpenRiwayahPicker() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Text(
                            text = currentRiwayah.nameAr.replace("رواية ", ""),
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isLightReadingMode) Color(0xFF5D4210) else GoldAccent,
                            maxLines = 1
                        )
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowDown,
                            contentDescription = "تغيير الرواية",
                            tint = if (isLightReadingMode) Color(0xFF5D4210) else GoldAccent,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // 2. Clean Action Toolbar: Font Zoom (- / Size / +) | Audio Play + Qari | Lightbulb | Bookmark
            //    (Tafsir completely removed per user request)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Font Size Control Capsule: [ + | 22 | - ]
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = controlBgColor,
                    border = BorderStroke(1.dp, cardInnerBorderColor)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (isLightReadingMode) Color(0xFF8C6621) else GoldAccent,
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("mushaf_zoom_in_button")
                                .clickable { onIncreaseFontSize() }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.Add,
                                    contentDescription = "تكبير النص",
                                    tint = if (isLightReadingMode) Color.White else Color.Black,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Text(
                            text = "$currentFontSize",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = controlTextColor,
                            modifier = Modifier.padding(horizontal = 6.dp)
                        )

                        Surface(
                            shape = CircleShape,
                            color = if (isLightReadingMode) Color(0xFFD5C4A1) else EmeraldBorderDark,
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("mushaf_zoom_out_button")
                                .clickable { onDecreaseFontSize() }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.Remove,
                                    contentDescription = "تصغير النص",
                                    tint = controlTextColor,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                // Light/Dark Mode Toggle + Bookmark Icon
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = controlBgColor,
                        border = BorderStroke(1.dp, cardInnerBorderColor),
                        modifier = Modifier
                            .size(36.dp)
                            .clickable { isLightReadingMode = !isLightReadingMode }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isLightReadingMode) Icons.Filled.Lightbulb else Icons.Outlined.Lightbulb,
                                contentDescription = "تبديل وضع القراءة",
                                tint = if (isLightReadingMode) Color(0xFF9E7422) else GoldAccent,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    val isSurahBookmarked = bookmarkSurahNumber == surah.number
                    Surface(
                        shape = CircleShape,
                        color = controlBgColor,
                        border = BorderStroke(1.dp, if (isSurahBookmarked) cardOuterBorderColor else cardInnerBorderColor),
                        modifier = Modifier
                            .size(36.dp)
                            .clickable {
                                val firstVisibleIndex = listState.firstVisibleItemIndex.coerceIn(0, (loadedAyahs.size - 1).coerceAtLeast(0))
                                val targetAyah = loadedAyahs.getOrNull(firstVisibleIndex) ?: loadedAyahs.firstOrNull()
                                if (targetAyah != null) {
                                    onSaveBookmarkAtAyah(targetAyah)
                                    feedbackBanner = "تم حفظ فاصل القراءة عند سورة ${surah.nameAr} - آية ${targetAyah.numberInSurah}"
                                }
                            }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isSurahBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkAdd,
                                contentDescription = "حفظ علامة التوقف",
                                tint = if (isSurahBookmarked) GoldAccent else controlTextColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // Optional Bookmark Saved Feedback Banner
            if (feedbackBanner != null) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = GoldAccent
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = feedbackBanner!!,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { feedbackBanner = null },
                            modifier = Modifier.size(22.dp)
                        ) {
                            Icon(Icons.Filled.Close, contentDescription = "إغلاق", tint = Color.Black, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }

            // 3. Main Framed Islamic Emerald/Parchment Mushaf Container
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                shape = RoundedCornerShape(20.dp),
                color = cardSurfaceColor,
                border = BorderStroke(1.8.dp, cardOuterBorderColor),
                shadowElevation = 4.dp
            ) {
                if (isLoadingAyahs) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = GoldAccent)
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        // Illuminated Ornamental Surah Header Plaque
                        item {
                            OrnamentalSurahHeader(
                                surah = surah,
                                plaqueBgColor = plaqueBgColor,
                                borderColor = cardOuterBorderColor,
                                titleColor = if (isLightReadingMode) Color(0xFF4B3713) else GoldAccent,
                                subtitleColor = secondaryTextColor
                            )
                            HorizontalDivider(thickness = 1.dp, color = dividerColor)
                        }

                        // Basmalah Row for Surahs other than Al-Fatihah (1) and At-Tawbah (9)
                        if (surah.number != 1 && surah.number != 9) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(cardSurfaceColor)
                                        .padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                                        fontSize = (currentFontSize + 2).sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isLightReadingMode) Color(0xFF6B4C12) else GoldAccent,
                                        textAlign = TextAlign.Center
                                    )
                                }
                                HorizontalDivider(thickness = 0.8.dp, color = dividerColor)
                            }
                        }

                        // Clean Ayah Rows matching the screenshot structure (Arabic text right + Rosette left + Divider)
                        items(loadedAyahs.size, key = { idx -> loadedAyahs[idx].numberInSurah }) { idx ->
                            val ayah = loadedAyahs[idx]
                            val isBookmarked = (surah.number == bookmarkSurahNumber && ayah.numberInSurah == bookmarkAyahNumber)

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        if (isBookmarked) GoldAccent.copy(alpha = 0.14f) else cardSurfaceColor
                                    )
                                    .clickable {
                                        onSaveBookmarkAtAyah(ayah)
                                        feedbackBanner = "تم حفظ علامة التوقف عند الآية (${ayah.numberInSurah})"
                                    }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = ayah.textAr,
                                        fontSize = currentFontSize.sp,
                                        fontWeight = FontWeight.Medium,
                                        lineHeight = (currentFontSize * 1.8).sp,
                                        color = primaryTextColor,
                                        textAlign = TextAlign.Right,
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(end = 10.dp)
                                    )

                                    OrnateAyahRosette(
                                        ayahNumber = ayah.numberInSurah,
                                        ringColor = if (isBookmarked) GoldAccent else rosetteColor,
                                        fillTint = if (isLightReadingMode) Color(0xFFF6EBD2) else Color(0xFF12362B),
                                        textColor = if (isBookmarked) GoldAccent else primaryTextColor
                                    )
                                }

                                HorizontalDivider(thickness = 0.8.dp, color = dividerColor)
                            }
                        }

                        // Next Surah Pill Button right below the last verse
                        if (nextSurah != null) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(cardSurfaceColor)
                                        .padding(vertical = 16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(18.dp),
                                        color = plaqueBgColor,
                                        border = BorderStroke(1.3.dp, cardOuterBorderColor),
                                        modifier = Modifier
                                            .testTag("mushaf_next_surah_button")
                                            .clickable { onSelectSurah(nextSurah) }
                                    ) {
                                        Text(
                                            text = "سورة ${nextSurah.nameAr}",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isLightReadingMode) Color(0xFF4B3713) else GoldAccent,
                                            modifier = Modifier.padding(horizontal = 42.dp, vertical = 8.dp)
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

@Composable
private fun OrnamentalSurahHeader(
    surah: Surah,
    plaqueBgColor: Color,
    borderColor: Color,
    titleColor: Color,
    subtitleColor: Color
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 10.dp),
        shape = RoundedCornerShape(14.dp),
        color = plaqueBgColor,
        border = BorderStroke(1.2.dp, borderColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            // Decorative diamond side motifs
            Canvas(modifier = Modifier.matchParentSize()) {
                val r = 5.dp.toPx()
                val cy = size.height / 2f
                listOf(10.dp.toPx(), size.width - 10.dp.toPx()).forEach { cx ->
                    val diamond = Path().apply {
                        moveTo(cx, cy - r)
                        lineTo(cx + r, cy)
                        lineTo(cx, cy + r)
                        lineTo(cx - r, cy)
                        close()
                    }
                    drawPath(diamond, color = borderColor, style = Stroke(width = 1.2.dp.toPx()))
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "سُورَةُ ${surah.nameAr}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = titleColor
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${surah.revelationType} • ${surah.versesCount} آية",
                    fontSize = 11.sp,
                    color = subtitleColor
                )
            }
        }
    }
}

@Composable
private fun GoldAccentLightOrDefault(): Color = IslamicGold.copy(alpha = 0.9f)

@Composable
fun OrnateAyahRosette(
    ayahNumber: Int,
    ringColor: Color,
    textColor: Color,
    fillTint: Color = Color.Transparent,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(40.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val outerR = size.minDimension * 0.45f
            val innerR = size.minDimension * 0.31f

            // Subtle medallion fill
            if (fillTint != Color.Transparent) {
                drawCircle(
                    color = fillTint,
                    radius = outerR * 0.88f,
                    center = Offset(cx, cy)
                )
            }

            // Outer 8-petal Islamic star rosette
            val petals = 8
            val path = Path()
            for (i in 0 until petals * 2) {
                val angle = (i * Math.PI / petals).toFloat()
                val r = if (i % 2 == 0) outerR else innerR * 1.15f
                val x = cx + r * cos(angle)
                val y = cy + r * sin(angle)
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            path.close()

            drawPath(
                path = path,
                color = ringColor,
                style = Stroke(width = 1.3.dp.toPx())
            )
            drawCircle(
                color = ringColor,
                radius = innerR,
                center = Offset(cx, cy),
                style = Stroke(width = 1.1.dp.toPx())
            )
            drawCircle(
                color = ringColor.copy(alpha = 0.5f),
                radius = outerR * 0.94f,
                center = Offset(cx, cy),
                style = Stroke(width = 0.7.dp.toPx())
            )
        }

        Text(
            text = "$ayahNumber",
            fontSize = if (ayahNumber >= 100) 10.sp else 12.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            textAlign = TextAlign.Center
        )
    }
}
