package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.Surah
import com.example.data.services.PlaybackState
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.GoldAccent

enum class QuranHubSection {
    HUB,
    MUSHAF,               // القرآن الكريم
    RECITATION,           // تلاوة القرآن الكريم
    SEARCH,               // البحث في القرآن الكريم
    IRAB,                 // إعراب القرآن الكريم
    TAFSIR,               // تفسير القرآن الكريم
    TRANSLATION,          // ترجمة القرآن الكريم
    SMART_RECITATION,     // تلاوة ذكية
    SUNNAH                // السنة النبوية
}

data class QuranHubCardItem(
    val section: QuranHubSection,
    val titleAr: String,
    val subtitleAr: String,
    val badgeType: HubBadgeType,
    val testTag: String
)

enum class HubBadgeType {
    NONE,
    SPEAKER,
    SEARCH,
    PEN,
    CHAT,
    TRANSLATE,
    MIC_AI,
    MOSQUE
}

@Composable
fun QuranHubScreen(
    surahs: List<Surah>,
    bookmarkSurahName: String = "الفاتحة",
    bookmarkAyahNumber: Int = 1,
    hasActiveBookmark: Boolean = true,
    currentRiwayahName: String = "رواية حفص عن عاصم",
    playbackState: PlaybackState,
    onSelectSection: (QuranHubSection) -> Unit,
    onResumeBookmark: () -> Unit,
    onOpenDrawer: () -> Unit,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val primarySixCards = listOf(
        QuranHubCardItem(
            section = QuranHubSection.MUSHAF,
            titleAr = "المصحف",
            subtitleAr = "المصحف الشريف بالروايات",
            badgeType = HubBadgeType.NONE,
            testTag = "hub_card_mushaf"
        ),
        QuranHubCardItem(
            section = QuranHubSection.RECITATION,
            titleAr = "تلاوة القرآن الكريم",
            subtitleAr = "استماع بأصوات كبار القراء",
            badgeType = HubBadgeType.SPEAKER,
            testTag = "hub_card_recitation"
        ),
        QuranHubCardItem(
            section = QuranHubSection.SEARCH,
            titleAr = "البحث في القرآن الكريم",
            subtitleAr = "بحث فوري في الآيات والسور",
            badgeType = HubBadgeType.SEARCH,
            testTag = "hub_card_search"
        ),
        QuranHubCardItem(
            section = QuranHubSection.IRAB,
            titleAr = "إعراب القرآن الكريم",
            subtitleAr = "قواعد النحو وإعراب الآيات",
            badgeType = HubBadgeType.PEN,
            testTag = "hub_card_irab"
        ),
        QuranHubCardItem(
            section = QuranHubSection.TAFSIR,
            titleAr = "تفسير القرآن الكريم",
            subtitleAr = "التفسير الميسر ومعاني الآيات",
            badgeType = HubBadgeType.CHAT,
            testTag = "hub_card_tafsir"
        ),
        QuranHubCardItem(
            section = QuranHubSection.TRANSLATION,
            titleAr = "ترجمة القرآن الكريم",
            subtitleAr = "معاني القرآن بلغات العالم",
            badgeType = HubBadgeType.TRANSLATE,
            testTag = "hub_card_translation"
        )
    )

    val extraTwoCards = listOf(
        QuranHubCardItem(
            section = QuranHubSection.SMART_RECITATION,
            titleAr = "تلاوة ذكية",
            subtitleAr = "المصحح الصوتي وتقييم التلاوة",
            badgeType = HubBadgeType.MIC_AI,
            testTag = "hub_card_smart_recitation"
        ),
        QuranHubCardItem(
            section = QuranHubSection.SUNNAH,
            titleAr = "السنة النبوية",
            subtitleAr = "الأحاديث النبوية الشريفة",
            badgeType = HubBadgeType.MOSQUE,
            testTag = "hub_card_sunnah"
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("quran_hub_screen")
    ) {
        // Top Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 3.dp
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (onBack != null) {
                            IconButton(
                                onClick = onBack,
                                modifier = Modifier.minimumInteractiveComponentSize()
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "رجوع",
                                    tint = GoldAccent
                                )
                            }
                        }

                        IconButton(
                            onClick = onOpenDrawer,
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = "القائمة الجانبية",
                                tint = GoldAccent,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "القرآن الكريم",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "المصحف الشريف • التلاوة الذكية • السنة النبوية",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Active Audio Bar if playing
        if (playbackState.isPlaying || playbackState.isBuffering) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = EmeraldLight.copy(alpha = 0.15f),
                tonalElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.VolumeUp, contentDescription = null, tint = EmeraldLight)
                        Text(
                            text = playbackState.currentTitle.ifBlank { "جارٍ الاستماع للتلاوة العطرة..." },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    TextButton(onClick = { onSelectSection(QuranHubSection.RECITATION) }) {
                        Text("التحكم بالصوت", fontSize = 11.sp, color = EmeraldLight)
                    }
                }
            }
        }

        // Grid Section (Always RTL order so top-right is القرآن الكريم matching screenshot)
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Row 1 (3 cards): القرآن الكريم | تلاوة القرآن الكريم | البحث في القرآن الكريم
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    primarySixCards.take(3).forEach { item ->
                        QuranHubCard(
                            item = item,
                            onClick = { onSelectSection(item.section) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Row 2 (3 cards): إعراب القرآن الكريم | تفسير القرآن الكريم | ترجمة القرآن الكريم
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    primarySixCards.drop(3).forEach { item ->
                        QuranHubCard(
                            item = item,
                            onClick = { onSelectSection(item.section) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Row 3 (2 balanced cards): تلاوة ذكية | السنة النبوية
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    extraTwoCards.forEach { item ->
                        QuranHubWideCard(
                            item = item,
                            onClick = { onSelectSection(item.section) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun QuranHubCard(
    item: QuranHubCardItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val topBg = Brush.verticalGradient(
        colors = listOf(Color(0xFF4F5259), Color(0xFF43454C))
    )
    val footerBg = Color(0xFF4B4D54)
    val borderColor = Color(0xFF1F2126)

    Card(
        modifier = modifier
            .height(146.dp)
            .testTag(item.testTag)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF45474E)),
        border = BorderStroke(1.2.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Icon Container
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(topBg),
                contentAlignment = Alignment.Center
            ) {
                QuranHubCardIcon(badgeType = item.badgeType)
            }

            // Subtle Divider
            HorizontalDivider(thickness = 1.dp, color = Color(0xFF33353B))

            // Footer Label Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(footerBg)
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.titleAr,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF3F3F5),
                    textAlign = TextAlign.Center,
                    lineHeight = 17.sp,
                    maxLines = 2
                )
            }
        }
    }
}

@Composable
fun QuranHubWideCard(
    item: QuranHubCardItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val topBg = Brush.verticalGradient(
        colors = listOf(Color(0xFF4F5259), Color(0xFF3D4047))
    )
    val accentBorder = if (item.section == QuranHubSection.SMART_RECITATION) {
        EmeraldLight.copy(alpha = 0.65f)
    } else {
        GoldAccent.copy(alpha = 0.65f)
    }

    Card(
        modifier = modifier
            .height(146.dp)
            .testTag(item.testTag)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF45474E)),
        border = BorderStroke(1.2.dp, accentBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(topBg),
                contentAlignment = Alignment.Center
            ) {
                QuranHubCardIcon(badgeType = item.badgeType)
            }

            HorizontalDivider(thickness = 1.dp, color = Color(0xFF33353B))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .background(Color(0xFF4B4D54))
                    .padding(horizontal = 6.dp, vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = item.titleAr,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
                Text(
                    text = item.subtitleAr,
                    fontSize = 9.5.sp,
                    color = if (item.section == QuranHubSection.SMART_RECITATION) Color(0xFFA5D6A7) else Color(0xFFFFE082),
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun QuranHubCardIcon(
    badgeType: HubBadgeType,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(72.dp),
        contentAlignment = Alignment.Center
    ) {
        // Custom Canvas: Circular Emblem + Wooden X-Stand Rehal + Open Mushaf Pages + Decorative Badge
        Canvas(modifier = Modifier.size(64.dp)) {
            val w = size.width
            val h = size.height
            val cx = w / 2f
            val cy = h / 2f + 3.dp.toPx()

            // 1. Slate-blue circular backdrop matching screenshot
            drawCircle(
                color = Color(0xFF6B7591),
                radius = w * 0.42f,
                center = Offset(cx, cy)
            )

            // 2. Wooden Rehal (X-shaped cross stand underneath the Mushaf)
            val woodDark = Color(0xFF7B4E23)
            val woodLight = Color(0xFFAA7137)

            // Left-down to right-up leg
            drawLine(
                color = woodDark,
                start = Offset(cx - w * 0.24f, cy + h * 0.30f),
                end = Offset(cx + w * 0.16f, cy + h * 0.04f),
                strokeWidth = 6.dp.toPx(),
                cap = StrokeCap.Round
            )
            // Right-down to left-up leg
            drawLine(
                color = woodLight,
                start = Offset(cx + w * 0.24f, cy + h * 0.30f),
                end = Offset(cx - w * 0.16f, cy + h * 0.04f),
                strokeWidth = 6.dp.toPx(),
                cap = StrokeCap.Round
            )

            // 3. Book Cover (Dark Brown / Gold Rim underneath open pages)
            val coverPath = Path().apply {
                moveTo(cx - w * 0.34f, cy - h * 0.03f)
                lineTo(cx, cy + h * 0.14f)
                lineTo(cx + w * 0.34f, cy - h * 0.03f)
                lineTo(cx + w * 0.30f, cy + h * 0.03f)
                lineTo(cx, cy + h * 0.19f)
                lineTo(cx - w * 0.30f, cy + h * 0.03f)
                close()
            }
            drawPath(path = coverPath, color = Color(0xFF3E2712))

            // 4. Left Open Page (Cream/Gold Parchment)
            val leftPage = Path().apply {
                moveTo(cx, cy + h * 0.12f)
                lineTo(cx - w * 0.31f, cy - h * 0.02f)
                lineTo(cx - w * 0.22f, cy - h * 0.24f)
                lineTo(cx, cy - h * 0.10f)
                close()
            }
            drawPath(path = leftPage, color = Color(0xFFF6E9B9))
            drawPath(
                path = leftPage,
                color = Color(0xFF5A4424),
                style = Stroke(width = 1.2.dp.toPx(), join = StrokeJoin.Round)
            )

            // 5. Right Open Page (Slightly shaded warm parchment)
            val rightPage = Path().apply {
                moveTo(cx, cy + h * 0.12f)
                lineTo(cx + w * 0.31f, cy - h * 0.02f)
                lineTo(cx + w * 0.22f, cy - h * 0.24f)
                lineTo(cx, cy - h * 0.10f)
                close()
            }
            drawPath(path = rightPage, color = Color(0xFFEDE0AA))
            drawPath(
                path = rightPage,
                color = Color(0xFF5A4424),
                style = Stroke(width = 1.2.dp.toPx(), join = StrokeJoin.Round)
            )

            // 6. Center Spine Line & Page Text Lines
            drawLine(
                color = Color(0xFF7B5D32),
                start = Offset(cx, cy - h * 0.10f),
                end = Offset(cx, cy + h * 0.12f),
                strokeWidth = 1.5.dp.toPx()
            )
            // Tiny script lines on left page
            for (i in 0..2) {
                val yOffset = (i - 1) * 4.5.dp.toPx()
                drawLine(
                    color = Color(0xFF8C7752),
                    start = Offset(cx - w * 0.20f, cy - h * 0.10f + yOffset),
                    end = Offset(cx - w * 0.05f, cy - h * 0.03f + yOffset),
                    strokeWidth = 1.1.dp.toPx(),
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = Color(0xFF8C7752),
                    start = Offset(cx + w * 0.05f, cy - h * 0.03f + yOffset),
                    end = Offset(cx + w * 0.20f, cy - h * 0.10f + yOffset),
                    strokeWidth = 1.1.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }

        // Top Decorative Badge matching the exact badge style in the user's screenshot
        when (badgeType) {
            HubBadgeType.NONE -> Unit
            HubBadgeType.SPEAKER -> {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF3E4A61),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.85f)),
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.TopCenter)
                        .offset(y = (-1).dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.VolumeUp,
                            contentDescription = "تلاوة",
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }
            HubBadgeType.SEARCH -> {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF81D4FA),
                    border = BorderStroke(2.dp, Color(0xFF283546)),
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.TopCenter)
                        .offset(x = 2.dp, y = (-1).dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "بحث",
                            tint = Color(0xFF1A237E),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
            HubBadgeType.PEN -> {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF7B6020),
                    border = BorderStroke(1.2.dp, Color(0xFFFFD54F)),
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.TopCenter)
                        .offset(y = (-1).dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "إعراب",
                            tint = Color(0xFFFFE082),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
            HubBadgeType.CHAT -> {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFFFF8E1),
                    border = BorderStroke(1.4.dp, Color(0xFF4A3C26)),
                    modifier = Modifier
                        .size(width = 28.dp, height = 20.dp)
                        .align(Alignment.TopCenter)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.FormatAlignRight,
                            contentDescription = "تفسير",
                            tint = Color(0xFF3E2723),
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }
            HubBadgeType.TRANSLATE -> {
                Row(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .offset(y = (-1).dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFB74E5A),
                        border = BorderStroke(1.dp, Color.White),
                        modifier = Modifier.size(19.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("ع", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF6B5344),
                        border = BorderStroke(1.dp, Color.White),
                        modifier = Modifier.size(19.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("A", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
            HubBadgeType.MIC_AI -> {
                Surface(
                    shape = CircleShape,
                    color = EmeraldLight,
                    border = BorderStroke(1.4.dp, Color.White),
                    modifier = Modifier
                        .size(25.dp)
                        .align(Alignment.TopCenter)
                        .offset(y = (-1).dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.Mic,
                            contentDescription = "تلاوة ذكية",
                            tint = Color.Black,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }
            HubBadgeType.MOSQUE -> {
                Surface(
                    shape = CircleShape,
                    color = GoldAccent,
                    border = BorderStroke(1.4.dp, Color.White),
                    modifier = Modifier
                        .size(25.dp)
                        .align(Alignment.TopCenter)
                        .offset(y = (-1).dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.Mosque,
                            contentDescription = "السنة النبوية",
                            tint = Color.Black,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }
        }
    }
}
