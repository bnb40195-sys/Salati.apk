package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.DayEventsCollection
import com.example.data.local.OnThisDayDataset
import com.example.data.local.OnThisDayItem
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.GoldAccent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnThisDayScreen(
    onBack: () -> Unit,
    onOpenDrawer: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    eventsList: List<OnThisDayItem> = emptyList(),
    dayCollections: List<DayEventsCollection> = OnThisDayDataset.collections
) {
    val context = LocalContext.current
    var selectedDayOffset by remember { mutableIntStateOf(0) }
    var selectedFilter by remember { mutableStateOf("all") }
    var searchQuery by remember { mutableStateOf("") }

    val daysList = remember(dayCollections) {
        dayCollections.sortedBy { it.dayOffset }
    }

    val currentDayCollection = remember(selectedDayOffset, daysList) {
        daysList.find { it.dayOffset == selectedDayOffset }
            ?: daysList.firstOrNull { it.dayOffset == 0 }
            ?: DayEventsCollection(
                dayOffset = 0,
                dayTitleAr = "اليوم",
                dateDisplayAr = "25 أكتوبر 2026",
                hijriDateAr = "14 جمادى الأولى 1448 هـ",
                events = eventsList
            )
    }

    val activeRawEvents = remember(currentDayCollection, eventsList) {
        if (currentDayCollection.events.isNotEmpty()) {
            currentDayCollection.events
        } else {
            eventsList
        }
    }

    val filters = listOf(
        "all" to "الكل",
        "event" to "أحداث ومعارك",
        "birth" to "مواليد",
        "death" to "وفيات",
        "holiday" to "أعياد ومناسبات"
    )

    val filteredList = remember(selectedFilter, searchQuery, activeRawEvents) {
        activeRawEvents.filter { item ->
            val matchesCategory = selectedFilter == "all" || item.category == selectedFilter
            val matchesQuery = searchQuery.isBlank() ||
                    item.titleAr.contains(searchQuery, ignoreCase = true) ||
                    item.detailsAr.contains(searchQuery, ignoreCase = true) ||
                    item.year.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }

    Column(
        modifier = modifier
            .testTag("on_this_day_screen")
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("btn_back_on_this_day")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "الرجوع")
                    }

                    if (onOpenDrawer != null) {
                        IconButton(
                            onClick = onOpenDrawer,
                            modifier = Modifier.testTag("btn_drawer_on_this_day")
                        ) {
                            Icon(Icons.Filled.Menu, contentDescription = "فتح القائمة", tint = GoldAccent)
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "حدث في مثل هذا اليوم",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldAccent
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = EmeraldLight.copy(alpha = 0.2f),
                                border = androidx.compose.foundation.BorderStroke(0.5.dp, EmeraldLight)
                            ) {
                                Text(
                                    text = "بدون نت ✓",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldLight,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "سجل التاريخ الإسلامي والعالمي من 25 أكتوبر 2026 م",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Count Badge
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = "${filteredList.size} حدث",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Day Selection Tabs (قبل أمس, أمس, اليوم, غداً, بعد غد)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    daysList.forEach { col ->
                        val isSelected = col.dayOffset == selectedDayOffset
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedDayOffset = col.dayOffset },
                            label = {
                                Text(
                                    text = col.dayTitleAr,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 12.sp
                                )
                            },
                            leadingIcon = {
                                if (col.dayOffset == 0) {
                                    Icon(
                                        Icons.Filled.Today,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                } else if (col.dayOffset < 0) {
                                    Icon(
                                        Icons.Filled.History,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                } else {
                                    Icon(
                                        Icons.Filled.Event,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GoldAccent.copy(alpha = 0.25f),
                                selectedLabelColor = MaterialTheme.colorScheme.onSurface,
                                selectedLeadingIconColor = GoldAccent
                            ),
                            shape = RoundedCornerShape(14.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Active Day Indicator & Navigation Banner
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = {
                                val minOffset = daysList.minOfOrNull { it.dayOffset } ?: -2
                                if (selectedDayOffset > minOffset) {
                                    selectedDayOffset--
                                }
                            },
                            enabled = selectedDayOffset > (daysList.minOfOrNull { it.dayOffset } ?: -2)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "اليوم السابق")
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${currentDayCollection.dayTitleAr}: ${currentDayCollection.dateDisplayAr}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            val subLabel = if (currentDayCollection.hijriDateAr.isNotBlank()) {
                                "الموافق: ${currentDayCollection.hijriDateAr} • تحديث أوفلاين بدون نت"
                            } else {
                                "تصفح أحداث الأيام السابقة والمقبلة بدون إنترنت"
                            }
                            Text(
                                text = subLabel,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = EmeraldLight
                            )
                        }

                        IconButton(
                            onClick = {
                                val maxOffset = daysList.maxOfOrNull { it.dayOffset } ?: 2
                                if (selectedDayOffset < maxOffset) {
                                    selectedDayOffset++
                                }
                            },
                            enabled = selectedDayOffset < (daysList.maxOfOrNull { it.dayOffset } ?: 2)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "اليوم التالي")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("search_events_input"),
                    placeholder = { Text("ابحث في أحداث اليوم، المعارك، الشخصيات...", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = GoldAccent) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Filled.Clear, contentDescription = "مسح")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Category Filter chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    filters.forEach { (cat, label) ->
                        val isSelected = selectedFilter == cat
                        Surface(
                            onClick = { selectedFilter = cat },
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
            }
        }

        // List of Events
        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Filled.EventBusy,
                        contentDescription = null,
                        modifier = Modifier.size(52.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "لا توجد نتائج مطابقة لبحثك",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                contentPadding = PaddingValues(top = 10.dp, bottom = 80.dp)
            ) {
                items(filteredList) { item ->
                    val badgeColor = when (item.category) {
                        "event" -> Color(0xFFE5A93C) // Gold
                        "birth" -> Color(0xFF10B981) // Emerald Green
                        "death" -> Color(0xFFEF4444) // Red
                        else -> Color(0xFF3B82F6) // Blue
                    }

                    val categoryLabel = when (item.category) {
                        "event" -> "حدث تاريخي"
                        "birth" -> "مولد"
                        "death" -> "وفاة"
                        else -> "مناسبة"
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = badgeColor.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = categoryLabel,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = badgeColor,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = item.year,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )

                                    Spacer(modifier = Modifier.width(6.dp))

                                    // Quick Copy Button
                                    IconButton(
                                        onClick = {
                                            val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            val clip = ClipData.newPlainText(
                                                "حدث تاريخي",
                                                "${item.titleAr} (${item.year})\n${item.detailsAr}"
                                            )
                                            cm.setPrimaryClip(clip)
                                            Toast.makeText(context, "تم نسخ الحدث", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            Icons.Filled.ContentCopy,
                                            contentDescription = "نسخ الحدث",
                                            modifier = Modifier.size(16.dp),
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = item.titleAr,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 22.sp
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = item.detailsAr,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 19.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
