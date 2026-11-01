package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class AppTab(
    val titleAr: String,
    val iconFilled: ImageVector,
    val iconOutlined: ImageVector,
    val showInBottomBar: Boolean = true
) {
    HOME("الرئيسية", Icons.Filled.Home, Icons.Outlined.Home, true),
    ADHAN("الأذان", Icons.Filled.VolumeUp, Icons.Outlined.VolumeUp, true),
    ATHKAR_DUAS("الأذكار والأدعية", Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome, true),
    QURAN("القرآن", Icons.Filled.MenuBook, Icons.Outlined.MenuBook, true),
    SETTINGS("الإعدادات", Icons.Filled.Settings, Icons.Outlined.Settings, true),
    QIBLA("القبلة", Icons.Filled.Explore, Icons.Outlined.Explore, false)
}

@Composable
fun AppBottomNav(
    selectedTab: AppTab,
    onTabSelected: (AppTab) -> Unit,
    sections: List<com.example.data.models.AppSectionModel> = emptyList(),
    modifier: Modifier = Modifier
) {
    val visibleTabs = AppTab.values().filter { tab ->
        if (!tab.showInBottomBar) return@filter false
        if (sections.isEmpty()) return@filter true
        when (tab) {
            AppTab.HOME, AppTab.SETTINGS -> true
            AppTab.ADHAN -> sections.find { it.id == "adhan_audio" }?.isEnabled ?: true
            AppTab.ATHKAR_DUAS -> sections.find { it.id == "athkar_duas" }?.isEnabled ?: true
            AppTab.QURAN -> sections.find { it.id == "quran" }?.isEnabled ?: true
            else -> true
        }
    }

    NavigationBar(
        modifier = modifier
            .testTag("bottom_nav_bar")
            .fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        visibleTabs.forEach { tab ->
            val isSelected = selectedTab == tab
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) tab.iconFilled else tab.iconOutlined,
                        contentDescription = tab.titleAr,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = tab.titleAr,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        maxLines = 1
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.testTag("nav_item_${tab.name.lowercase()}")
            )
        }
    }
}
