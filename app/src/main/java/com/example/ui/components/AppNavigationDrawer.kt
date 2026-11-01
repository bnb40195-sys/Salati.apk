package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GoldAccent

enum class DrawerDestination {
    HOME,
    QURAN_AND_SUNNAH,
    ADHAN_AND_REMINDER,
    ADHAN,
    QIBLA,
    CALENDAR,
    PRAYER_REMINDER,
    TASBIH,
    SETTINGS,
    CREATOR_STUDIO,
    // Unified Informational Sections
    INVENTIONS_AND_BATTLES,
    PROPHETS_AND_TAWBAH,
    QUESTIONS_RELIGIOUS_AND_GENERAL,
    ON_THIS_DAY,
    CAPITALS,
    ATHKAR_DUAS
}

private data class DrawerItemDef(
    val destination: DrawerDestination,
    val titleAr: String,
    val icon: ImageVector,
    val badgeAr: String? = null
)

@Composable
fun AppNavigationDrawer(
    isOpen: Boolean,
    currentDestination: DrawerDestination,
    onClose: () -> Unit,
    onNavigate: (DrawerDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    val darkBg = Color(0xFF071913)
    val drawerSurface = Color(0xFF0A221A)
    val goldBorder = Color(0x55D4AF37)
    val goldText = Color(0xFFD4AF37)
    val activeItemBg = Color(0xFF1B382B)

    val mainItems = remember {
        listOf(
            DrawerItemDef(DrawerDestination.HOME, "الرئيسية", Icons.Filled.Home),
            DrawerItemDef(DrawerDestination.ADHAN_AND_REMINDER, "الأذان وتذكير الصلاة (قسم موحد)", Icons.Filled.NotificationsActive, "موحد"),
            DrawerItemDef(DrawerDestination.ATHKAR_DUAS, "الأذكار والأدعية الشاملة (جهتان وعداد)", Icons.Filled.AutoAwesome, "شامل"),
            DrawerItemDef(DrawerDestination.QURAN_AND_SUNNAH, "القرآن الكريم والسنة النبوية (قسم موحد)", Icons.Filled.MenuBook, "موحد"),
            DrawerItemDef(DrawerDestination.CALENDAR, "التقويم الهجري والميلادي", Icons.Filled.CalendarMonth),
            DrawerItemDef(DrawerDestination.QIBLA, "القبلة", Icons.Filled.Explore),
            DrawerItemDef(DrawerDestination.SETTINGS, "الإعدادات الشاملة", Icons.Filled.Settings),
            DrawerItemDef(DrawerDestination.CREATOR_STUDIO, "قسم صانع التطبيق", Icons.Filled.Build, "استوديو")
        )
    }

    val infoItems = remember {
        listOf(
            DrawerItemDef(
                DrawerDestination.PROPHETS_AND_TAWBAH,
                "قصص الأنبياء والتوبة (قسم موحد)",
                Icons.Filled.AutoStories,
                "موحد"
            ),
            DrawerItemDef(
                DrawerDestination.QUESTIONS_RELIGIOUS_AND_GENERAL,
                "أسئلة دينية وعامة (قسم موحد)",
                Icons.Filled.HelpOutline,
                "موحد"
            ),
            DrawerItemDef(DrawerDestination.ON_THIS_DAY, "حدث في مثل هذا اليوم", Icons.Filled.Event),
            DrawerItemDef(DrawerDestination.CAPITALS, "عواصم الدول العربية والإسلامية", Icons.Filled.Public)
        )
    }

    AnimatedVisibility(
        visible = isOpen,
        enter = fadeIn(animationSpec = tween(220)),
        exit = fadeOut(animationSpec = tween(180))
    ) {
        // Full screen overlay box
        Box(
            modifier = modifier
                .fillMaxSize()
                .testTag("app_navigation_drawer_container")
        ) {
            // Scrim / dark backdrop with tap to close
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.55f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onClose
                    )
            )

            // Slide drawer panel positioned on the right
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.End
            ) {
                AnimatedVisibility(
                    visible = isOpen,
                    enter = slideInHorizontally(
                        initialOffsetX = { fullWidth -> fullWidth },
                        animationSpec = tween(260)
                    ) + fadeIn(animationSpec = tween(200)),
                    exit = slideOutHorizontally(
                        targetOffsetX = { fullWidth -> fullWidth },
                        animationSpec = tween(200)
                    ) + fadeOut(animationSpec = tween(160))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(0.86f)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        drawerSurface,
                                        darkBg
                                    )
                                )
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { /* prevent click through */ }
                    ) {
                        // Gold decorative border along the left edge of drawer
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .width(1.5.dp)
                                .fillMaxHeight()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            Color.Transparent,
                                            goldBorder,
                                            goldText.copy(alpha = 0.7f),
                                            goldBorder,
                                            Color.Transparent
                                        )
                                    )
                                )
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .statusBarsPadding()
                                .navigationBarsPadding()
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            // Top Drawer Header: Close button on left, "صلاتي" on right
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp, top = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Close button (circular with X)
                                IconButton(
                                    onClick = onClose,
                                    modifier = Modifier
                                        .testTag("drawer_close_button")
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF142B21))
                                        .border(1.dp, Color(0x44D4AF37), CircleShape)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = "إغلاق القائمة",
                                        tint = Color(0xFFE0E0E0),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                // Title "صلاتي"
                                Text(
                                    text = "صلاتي",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = goldText,
                                    modifier = Modifier.padding(end = 6.dp)
                                )
                            }

                            // Scrollable Drawer Sections
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .verticalScroll(rememberScrollState())
                            ) {
                                // Category 1: الأقسام الرئيسية
                                Text(
                                    text = "الأقسام الرئيسية",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = goldText,
                                    textAlign = TextAlign.End,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp, horizontal = 4.dp)
                                )

                                mainItems.forEach { item ->
                                    val isSelected = currentDestination == item.destination
                                    DrawerItemRow(
                                        item = item,
                                        isSelected = isSelected,
                                        activeBg = activeItemBg,
                                        goldColor = goldText,
                                        onClick = {
                                            onNavigate(item.destination)
                                            onClose()
                                        }
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))
                                HorizontalDivider(
                                    color = Color(0x22D4AF37),
                                    thickness = 1.dp,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                // Category 2: الأقسام المعلوماتية
                                Text(
                                    text = "الأقسام المعلوماتية",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = goldText,
                                    textAlign = TextAlign.End,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp, horizontal = 4.dp)
                                )

                                infoItems.forEach { item ->
                                    val isSelected = currentDestination == item.destination
                                    DrawerItemRow(
                                        item = item,
                                        isSelected = isSelected,
                                        activeBg = activeItemBg,
                                        goldColor = goldText,
                                        onClick = {
                                            onNavigate(item.destination)
                                            onClose()
                                        }
                                    )
                                }

                                Spacer(modifier = Modifier.height(24.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DrawerItemRow(
    item: DrawerItemDef,
    isSelected: Boolean,
    activeBg: Color,
    goldColor: Color,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(12.dp)
    val backgroundModifier = if (isSelected) {
        Modifier
            .background(activeBg, shape)
            .border(1.dp, goldColor.copy(alpha = 0.5f), shape)
    } else {
        Modifier.background(Color.Transparent, shape)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .clip(shape)
            .then(backgroundModifier)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 11.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (item.badgeAr != null) {
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = goldColor.copy(alpha = 0.2f),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, goldColor.copy(alpha = 0.4f))
            ) {
                Text(
                    text = item.badgeAr,
                    fontSize = 10.sp,
                    color = goldColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Text(
            text = item.titleAr,
            color = if (isSelected) goldColor else Color(0xFFEDE8D0),
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f, fill = false)
        )

        Spacer(modifier = Modifier.width(14.dp))

        Icon(
            imageVector = item.icon,
            contentDescription = item.titleAr,
            tint = goldColor,
            modifier = Modifier.size(22.dp)
        )
    }
}
