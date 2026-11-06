package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.BattleInfo
import com.example.data.models.InventionInfo
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.GoldAccent

@Composable
fun UnifiedInventionsBattlesScreen(
    battles: List<BattleInfo>,
    majorConquests: List<BattleInfo> = emptyList(),
    majorSaraya: List<BattleInfo> = emptyList(),
    inventions: List<InventionInfo>,
    initialTab: Int = 0,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    androidx.activity.compose.BackHandler(onBack = onBack)

    var selectedTab by remember { mutableStateOf(initialTab) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedBattleFilter by remember { mutableStateOf("الكل") }
    var selectedInventionField by remember { mutableStateOf("الكل") }

    val allBattlesCombined = remember(battles, majorConquests, majorSaraya) {
        val list = mutableListOf<BattleInfo>()
        list.addAll(battles)
        majorConquests.forEach { c -> if (list.none { it.id == c.id }) list.add(c) }
        majorSaraya.forEach { s -> if (list.none { it.id == s.id }) list.add(s) }
        list
    }

    val inventionFields = remember(inventions) {
        listOf("الكل") + inventions.map { it.fieldAr }.distinct()
    }

    val filteredBattles = remember(allBattlesCombined, searchQuery, selectedBattleFilter) {
        allBattlesCombined.filter { b ->
            val matchesFilter = when (selectedBattleFilter) {
                "الغزوات الكبرى" -> b.isMajor
                "الفتوحات" -> b.nameAr.contains("فتح")
                "السرايا" -> b.nameAr.contains("سرية")
                else -> true
            }
            val matchesQuery = searchQuery.isBlank() ||
                    b.nameAr.contains(searchQuery, ignoreCase = true) ||
                    b.leaderMuslims.contains(searchQuery, ignoreCase = true) ||
                    b.detailsAr.contains(searchQuery, ignoreCase = true) ||
                    b.eventsAr.contains(searchQuery, ignoreCase = true)
            matchesFilter && matchesQuery
        }
    }

    val filteredInventions = remember(inventions, searchQuery, selectedInventionField) {
        inventions.filter { inv ->
            val matchesField = selectedInventionField == "الكل" || inv.fieldAr == selectedInventionField
            val matchesQuery = searchQuery.isBlank() ||
                    inv.nameAr.contains(searchQuery, ignoreCase = true) ||
                    inv.inventorAr.contains(searchQuery, ignoreCase = true) ||
                    inv.howItWorksAr.contains(searchQuery, ignoreCase = true) ||
                    inv.impactAr.contains(searchQuery, ignoreCase = true)
            matchesField && matchesQuery
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("unified_inventions_battles_screen")
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
                    verticalAlignment = Alignment.CenterVertically
                ) {
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

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "إختراعات المسلمين والمعارك الإسلامية",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.End
                        )
                        Text(
                            text = "تاريخ البطولات والفتوحات وإنجازات الحضارة الإسلامية",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.End
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Unified Two Tabs
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    contentColor = GoldAccent,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                "الغزوات والمعارك (${filteredBattles.size})",
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp
                            )
                        },
                        icon = {
                            Icon(Icons.Filled.Shield, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                "إختراعات المسلمين (${filteredInventions.size})",
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp
                            )
                        },
                        icon = {
                            Icon(Icons.Filled.Science, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Search field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            if (selectedTab == 0) "ابحث عن غزوة، قائد، أو معركة..." else "ابحث عن اختراع، عالم، أو مجال...",
                            fontSize = 12.sp
                        )
                    },
                    leadingIcon = {
                        Icon(Icons.Filled.Search, contentDescription = null, tint = GoldAccent)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Filled.Clear, contentDescription = "مسح")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Sub-category Filter Chips
                if (selectedTab == 0) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val filters = listOf("الكل", "الغزوات الكبرى", "الفتوحات", "السرايا")
                        items(filters) { f ->
                            FilterChip(
                                selected = selectedBattleFilter == f,
                                onClick = { selectedBattleFilter = f },
                                label = { Text(f, fontSize = 12.sp) }
                            )
                        }
                    }
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(inventionFields) { field ->
                            FilterChip(
                                selected = selectedInventionField == field,
                                onClick = { selectedInventionField = field },
                                label = { Text(field, fontSize = 12.sp) }
                            )
                        }
                    }
                }
            }
        }

        // Tab Content
        if (selectedTab == 0) {
            // Battles list
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredBattles, key = { it.id }) { b ->
                    var isExpanded by remember { mutableStateOf(false) }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { isExpanded = !isExpanded },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = b.nameAr,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldAccent
                                )
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Text(
                                        text = "${b.yearH} هـ / ${b.yearG} م",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "القائد: ${b.leaderMuslims} • الأطراف: ${b.sidesAr}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = b.detailsAr,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 20.sp
                            )

                            AnimatedVisibility(visible = isExpanded) {
                                Column(modifier = Modifier.padding(top = 10.dp)) {
                                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                    Spacer(modifier = Modifier.height(8.dp))

                                    if (b.eventsAr.isNotBlank()) {
                                        Text(
                                            "سرد مجريات وأحداث المعركة:",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = EmeraldLight
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = b.eventsAr,
                                            fontSize = 13.sp,
                                            lineHeight = 20.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }

                                    if (b.lessonsAr.isNotBlank()) {
                                        Text(
                                            "الدروس والعبر المستفادة:",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFF59E0B)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = b.lessonsAr,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            lineHeight = 19.sp
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isExpanded) "عرض تفاصيل أقل ▲" else "عرض سرد المعركة والدروس ▼",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "النتيجة: ${b.resultAr}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldLight
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // Inventions list
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredInventions, key = { it.id }) { inv ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = inv.nameAr,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldAccent
                                )
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Text(
                                        text = inv.fieldAr,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "العالم والمبتكر: ${inv.inventorAr} • ${inv.placeAndEraAr}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "كيف يعمل والأساس العلمي:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = inv.howItWorksAr,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 20.sp
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = EmeraldLight.copy(alpha = 0.1f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "الأثر العالمي والحضاري:",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldLight
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = inv.impactAr,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
