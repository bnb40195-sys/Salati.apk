package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Delete
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
import com.example.data.models.ProphetStory
import com.example.data.models.TawbahStory
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.GoldAccent

@Composable
fun UnifiedProphetsTawbahScreen(
    prophets: List<ProphetStory>,
    tawbahStories: List<TawbahStory>,
    initialTab: Int = 0,
    onBack: () -> Unit,
    onOpenDrawer: () -> Unit = {},
    onOpenCreatorStudio: () -> Unit = {},
    onAddProphetStory: ((String, String, String, String, String, String) -> Unit)? = null,
    onDeleteProphetStory: ((String) -> Unit)? = null,
    onAddTawbahStory: ((String, String, String, String, String) -> Unit)? = null,
    onDeleteTawbahStory: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    androidx.activity.compose.BackHandler(onBack = onBack)

    var selectedTab by remember { mutableStateOf(initialTab) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredProphets = remember(prophets, searchQuery) {
        if (searchQuery.isBlank()) prophets
        else prophets.filter {
            it.nameAr.contains(searchQuery, ignoreCase = true) ||
                    it.titleAr.contains(searchQuery, ignoreCase = true) ||
                    it.summaryAr.contains(searchQuery, ignoreCase = true) ||
                    it.fullStoryAr.contains(searchQuery, ignoreCase = true) ||
                    it.keyLessons.contains(searchQuery, ignoreCase = true)
        }
    }

    val filteredTawbah = remember(tawbahStories, searchQuery) {
        if (searchQuery.isBlank()) tawbahStories
        else tawbahStories.filter {
            it.titleAr.contains(searchQuery, ignoreCase = true) ||
                    it.personAr.contains(searchQuery, ignoreCase = true) ||
                    it.detailsAr.contains(searchQuery, ignoreCase = true) ||
                    it.moralLesson.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("unified_prophets_tawbah_screen")
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
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Filled.Menu, contentDescription = "القائمة الجانبية", tint = GoldAccent)
                    }

                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("back_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "الرجوع",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "قصص الأنبياء والتوبة",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.End
                        )
                        Text(
                            text = "قدوات الهدى وسير التائبين العائدين إلى الله",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.End
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Unified Tab Row
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
                                "قصص الأنبياء (${filteredProphets.size})",
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp
                            )
                        },
                        icon = {
                            Icon(Icons.Filled.AutoStories, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                "قصص التوبة (${filteredTawbah.size})",
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp
                            )
                        },
                        icon = {
                            Icon(Icons.Filled.Favorite, contentDescription = null, modifier = Modifier.size(18.dp))
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
                            if (selectedTab == 0) "ابحث عن نبي، معجزة، أو درس..." else "ابحث عن قصة توبة، صحابي، أو عبرة...",
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
            }
        }

        // Tab Content
        if (selectedTab == 0) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredProphets, key = { it.id }) { p ->
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
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = p.nameAr,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldAccent
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer
                                    ) {
                                        Text(
                                            text = p.titleAr,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = p.summaryAr,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 20.sp
                            )

                            AnimatedVisibility(visible = isExpanded) {
                                Column(modifier = Modifier.padding(top = 10.dp)) {
                                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = "القصة كاملة والسيرة المباركة:",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldLight
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = p.fullStoryAr,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        lineHeight = 22.sp
                                    )

                                    if (p.miraclesAr.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = MaterialTheme.colorScheme.surfaceVariant,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(modifier = Modifier.padding(8.dp)) {
                                                Text(
                                                    text = "المعجزات المؤيدة من الله:",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = GoldAccent
                                                )
                                                Text(text = p.miraclesAr, fontSize = 12.sp)
                                            }
                                        }
                                    }

                                    if (p.keyLessons.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = EmeraldLight.copy(alpha = 0.1f),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(modifier = Modifier.padding(8.dp)) {
                                                Text(
                                                    text = "الدروس والعبر المستفادة:",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = EmeraldLight
                                                )
                                                Text(text = p.keyLessons, fontSize = 12.sp)
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (isExpanded) "عرض تفاصيل أقل ▲" else "قراءة القصة كاملة والعبر ▼",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredTawbah, key = { it.id }) { t ->
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
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = t.titleAr,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldAccent
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.secondaryContainer
                                    ) {
                                        Text(
                                            text = t.personAr,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.secondary,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = t.detailsAr,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 21.sp
                            )

                            if (t.moralLesson.isNotBlank()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = EmeraldLight.copy(alpha = 0.1f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(
                                            text = "العبرة والأثر الإيماني:",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = EmeraldLight
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = t.moralLesson,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "المصدر: ${t.sourceAr}",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }

}
