package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.*
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.GoldAccent

@Composable
fun KnowledgeScreen(
    prophets: List<ProphetStory>,
    tawbahStories: List<TawbahStory>,
    capitals: List<CapitalInfo>,
    battles: List<BattleInfo>,
    inventions: List<InventionInfo>,
    qas: List<IslamicQA>,
    generalQAs: List<GeneralQA> = emptyList(),
    onOpenFullGhazwat: (() -> Unit)? = null,
    onOpenSunnah: (() -> Unit)? = null,
    onOpenUnifiedInventionsBattles: (() -> Unit)? = null,
    onOpenUnifiedProphetsTawbah: (() -> Unit)? = null,
    onOpenUnifiedQuestions: (() -> Unit)? = null,
    onOpenCapitals: (() -> Unit)? = null,
    onOpenDrawer: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var selectedSection by remember { mutableStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var textScaleDelta by remember { mutableStateOf(0) }
    val clipboardManager = LocalClipboardManager.current
    var copiedMessage by remember { mutableStateOf<String?>(null) }
    var selectedReligiousCategory by remember { mutableStateOf("الكل") }
    var selectedGeneralCategory by remember { mutableStateOf("الكل") }
    var selectedInventionField by remember { mutableStateOf("الكل") }
    var revealAllAnswers by remember { mutableStateOf(false) }
    var randomDialogQA by remember { mutableStateOf<Any?>(null) }
    var randomDialogInvention by remember { mutableStateOf<InventionInfo?>(null) }
    val inventionFields = remember(inventions) {
        listOf("الكل") + inventions.map { it.fieldAr }.distinct()
    }

    val sections = listOf(
        "قصص الأنبياء (${prophets.size})",
        "قصص التوبة (${tawbahStories.size})",
        "الغزوات والمعارك (${battles.size})",
        "العواصم الإسلامية (${capitals.size})",
        "اختراعات المسلمين (${inventions.size})",
        "أسئلة دينية (${qas.size})",
        "أسئلة عامة (${generalQAs.size})"
    )

    val baseTextSize = (13 + textScaleDelta).sp
    val titleSize = (16 + textScaleDelta).sp

    Column(
        modifier = modifier
            .testTag("knowledge_screen")
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Section Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "المعرفة الإسلامية الشاملة",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "سرد كامل لقصص الأنبياء، قصص التوبة، الغزوات النبوية والمعارك",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Font adjustment & Drawer Button
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            onClick = { if (textScaleDelta > -2) textScaleDelta-- },
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text("A-", fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                        }
                        Surface(
                            onClick = { if (textScaleDelta < 4) textScaleDelta++ },
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text("A+", fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                        }

                        if (onOpenDrawer != null) {
                            IconButton(
                                onClick = onOpenDrawer,
                                modifier = Modifier
                                    .testTag("knowledge_open_drawer_button")
                                    .size(36.dp)
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
                    }
                }

                // Quick access shortcuts to the unified sections
                if (onOpenUnifiedInventionsBattles != null || onOpenUnifiedProphetsTawbah != null || onOpenUnifiedQuestions != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (onOpenUnifiedInventionsBattles != null) {
                            item {
                                Surface(
                                    onClick = onOpenUnifiedInventionsBattles,
                                    shape = RoundedCornerShape(10.dp),
                                    color = EmeraldLight.copy(alpha = 0.15f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldLight.copy(alpha = 0.4f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(Icons.Filled.Shield, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(16.dp))
                                        Text("الغزوات والاختراعات", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EmeraldLight)
                                    }
                                }
                            }
                        }
                        if (onOpenUnifiedProphetsTawbah != null) {
                            item {
                                Surface(
                                    onClick = onOpenUnifiedProphetsTawbah,
                                    shape = RoundedCornerShape(10.dp),
                                    color = GoldAccent.copy(alpha = 0.15f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.4f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(Icons.Filled.AutoStories, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                                        Text("الأنبياء والتوبة", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                                    }
                                }
                            }
                        }
                        if (onOpenUnifiedQuestions != null) {
                            item {
                                Surface(
                                    onClick = onOpenUnifiedQuestions,
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(Icons.Filled.HelpOutline, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                        Text("الأسئلة الدينية والعامة", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }
                        }
                        if (onOpenCapitals != null) {
                            item {
                                Surface(
                                    onClick = onOpenCapitals,
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(Icons.Filled.Public, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(16.dp))
                                        Text("عواصم العالم الإسلامي", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Search field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("بحث في هذا القسم...", fontSize = 13.sp) },
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

                Spacer(modifier = Modifier.height(10.dp))

                // Scrollable category chips
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(sections.indices.toList()) { index ->
                        val isSelected = selectedSection == index
                        Surface(
                            onClick = {
                                selectedSection = index
                                searchQuery = ""
                            },
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = sections[index],
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                            )
                        }
                    }
                }
            }
        }

        // Sunnah section link card
        if (onOpenSunnah != null) {
            Card(
                onClick = onOpenSunnah,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("btn_knowledge_open_sunnah"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
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
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(GoldAccent.copy(alpha = 0.18f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.AutoStories, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(20.dp))
                        }
                        Column {
                            Text(
                                text = "قسم السنة النبوية الشريفة",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldAccent
                            )
                            Text(
                                text = "الأربعون النووية كاملة وصحيح البخاري ومسلم ورياض الصالحين",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Icon(
                        Icons.Filled.ArrowForwardIos,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Feedback toast
        if (copiedMessage != null) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                color = EmeraldLight.copy(alpha = 0.15f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Check, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = copiedMessage ?: "", fontSize = 12.sp, color = EmeraldLight)
                }
            }
        }

        // Section Content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 80.dp)
        ) {
            when (selectedSection) {
                0 -> {
                    // Prophets Stories (سرد كامل لقصص الأنبياء)
                    val filteredProphets = if (searchQuery.isBlank()) prophets else prophets.filter {
                        it.nameAr.contains(searchQuery, ignoreCase = true) ||
                                it.summaryAr.contains(searchQuery, ignoreCase = true) ||
                                it.fullStoryAr.contains(searchQuery, ignoreCase = true) ||
                                it.titleAr.contains(searchQuery, ignoreCase = true)
                    }

                    items(filteredProphets, key = { it.id }) { p ->
                        var isExpanded by remember { mutableStateOf(false) }
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { isExpanded = !isExpanded },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = if (isExpanded) 4.dp else 1.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = GoldAccent.copy(alpha = 0.15f),
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(Icons.Filled.AutoStories, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(18.dp))
                                            }
                                        }
                                        Column {
                                            Text(
                                                text = p.nameAr,
                                                fontSize = titleSize,
                                                fontWeight = FontWeight.Bold,
                                                color = GoldAccent
                                            )
                                            Text(
                                                text = p.titleAr,
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = MaterialTheme.colorScheme.surfaceVariant
                                        ) {
                                            Text(
                                                text = "${p.chronologicalOrder} من 25",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                        Icon(
                                            imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = p.summaryAr,
                                    fontSize = baseTextSize,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = (baseTextSize.value * 1.5).sp
                                )

                                AnimatedVisibility(visible = isExpanded) {
                                    Column(modifier = Modifier.padding(top = 12.dp)) {
                                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                        Spacer(modifier = Modifier.height(10.dp))

                                        // Full Chronicle Header
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Filled.MenuBook, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("السرد القرآني والتاريخي الكامل:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = p.fullStoryAr,
                                            fontSize = baseTextSize,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            lineHeight = (baseTextSize.value * 1.6).sp,
                                            textAlign = TextAlign.Start
                                        )

                                        if (p.miraclesAr.isNotBlank()) {
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Filled.Stars, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("المعجزات والآيات:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EmeraldLight)
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(text = p.miraclesAr, fontSize = baseTextSize, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }

                                        if (p.keyLessons.isNotBlank()) {
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Filled.Lightbulb, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("الدروس والعبر المستفادة:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF59E0B))
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(text = p.keyLessons, fontSize = baseTextSize, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = "مواضع الذكر في القرآن: ${p.versesMentioned}",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                            )
                                        }

                                        // Copy Action
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.End
                                        ) {
                                            TextButton(
                                                onClick = {
                                                    val copyText = "📖 قصة ${p.nameAr} - ${p.titleAr}\n\n${p.fullStoryAr}\n\n💡 الدروس: ${p.keyLessons}\nمن تطبيق صلاتي"
                                                    clipboardManager.setText(AnnotatedString(copyText))
                                                    copiedMessage = "تم نسخ قصة ${p.nameAr}"
                                                }
                                            ) {
                                                Icon(Icons.Filled.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("نسخ القصة كاملة", fontSize = 12.sp)
                                            }
                                        }
                                    }
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 6.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (isExpanded) "طي السرد" else "قراءة السرد الكامل والدروس",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldAccent
                                    )
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // Tawbah Stories (سرد كامل لقصص التوبة المؤثرة)
                    val filteredTawbah = if (searchQuery.isBlank()) tawbahStories else tawbahStories.filter {
                        it.titleAr.contains(searchQuery, ignoreCase = true) ||
                                it.personAr.contains(searchQuery, ignoreCase = true) ||
                                it.detailsAr.contains(searchQuery, ignoreCase = true)
                    }

                    items(filteredTawbah, key = { it.id }) { t ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = t.titleAr,
                                            fontSize = titleSize,
                                            fontWeight = FontWeight.Bold,
                                            color = GoldAccent
                                        )
                                        Text(
                                            text = "صاحب القصة: ${t.personAr}",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    IconButton(
                                        onClick = {
                                            val copyText = "📖 ${t.titleAr} (${t.personAr})\n\n${t.detailsAr}\nالمصدر: ${t.sourceAr}\nمن تطبيق صلاتي"
                                            clipboardManager.setText(AnnotatedString(copyText))
                                            copiedMessage = "تم نسخ ${t.titleAr}"
                                        }
                                    ) {
                                        Icon(Icons.Filled.ContentCopy, contentDescription = "نسخ", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = t.detailsAr,
                                    fontSize = baseTextSize,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = (baseTextSize.value * 1.5).sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                ) {
                                    Text(
                                        text = "المصدر: ${t.sourceAr}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // Battles & Ghazwat
                    val filteredBattles = if (searchQuery.isBlank()) battles else battles.filter {
                        it.nameAr.contains(searchQuery, ignoreCase = true) ||
                                it.leaderMuslims.contains(searchQuery, ignoreCase = true) ||
                                it.detailsAr.contains(searchQuery, ignoreCase = true) ||
                                it.eventsAr.contains(searchQuery, ignoreCase = true)
                    }

                    item {
                        if (onOpenFullGhazwat != null) {
                            Card(
                                onClick = onOpenFullGhazwat,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Filled.Shield, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text("فتح شاشة الغزوات والمعارك الكاملة", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            Text("تصفح الـ 27 غزوة مع السرد ومجريات القتال التفصيلية", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                    Icon(Icons.Filled.ChevronRight, contentDescription = null)
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    }

                    items(filteredBattles, key = { it.id }) { b ->
                        var isExpanded by remember { mutableStateOf(false) }
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
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
                                    Text(text = b.nameAr, fontSize = titleSize, fontWeight = FontWeight.Bold, color = GoldAccent)
                                    Text(text = "${b.yearH} / ${b.yearG}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                }
                                Text(text = "القائد: ${b.leaderMuslims} • الأطراف: ${b.sidesAr}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = b.detailsAr, fontSize = baseTextSize, color = MaterialTheme.colorScheme.onSurface, lineHeight = (baseTextSize.value * 1.5).sp)

                                if (isExpanded && b.eventsAr.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("سرد الأحداث ومجريات المعركة:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EmeraldLight)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = b.eventsAr, fontSize = baseTextSize, lineHeight = (baseTextSize.value * 1.5).sp, color = MaterialTheme.colorScheme.onSurface)

                                    if (b.lessonsAr.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("الدروس المستفادة:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF59E0B))
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = b.lessonsAr, fontSize = baseTextSize, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = "النتيجة: ${b.resultAr}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EmeraldLight)
                            }
                        }
                    }
                }

                3 -> {
                    // Islamic Capitals
                    val filteredCapitals = if (searchQuery.isBlank()) capitals else capitals.filter {
                        it.nameAr.contains(searchQuery, ignoreCase = true) ||
                                it.countryAr.contains(searchQuery, ignoreCase = true) ||
                                it.howIslamArrivedAr.contains(searchQuery, ignoreCase = true)
                    }

                    items(filteredCapitals, key = { it.id }) { c ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(text = c.nameAr, fontSize = titleSize, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                        Text(text = "${c.countryAr} (${c.nameEn})", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant
                                    ) {
                                        Text(
                                            text = "UTC ${c.utcOffset}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GoldAccent,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "العملة: ${c.currencyAr}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(text = "السكان: ${c.population}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                Spacer(modifier = Modifier.height(8.dp))

                                Text(text = "كيف دخل الإسلام:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = c.howIslamArrivedAr, fontSize = baseTextSize, color = MaterialTheme.colorScheme.onSurface, lineHeight = (baseTextSize.value * 1.5).sp)
                            }
                        }
                    }
                }

                4 -> {
                    // Muslim Inventions
                    val filteredInventions = inventions.filter { inv ->
                        val matchesField = (selectedInventionField == "الكل" || inv.fieldAr == selectedInventionField)
                        val matchesSearch = searchQuery.isBlank() ||
                                inv.nameAr.contains(searchQuery, ignoreCase = true) ||
                                inv.inventorAr.contains(searchQuery, ignoreCase = true) ||
                                inv.howItWorksAr.contains(searchQuery, ignoreCase = true) ||
                                inv.impactAr.contains(searchQuery, ignoreCase = true) ||
                                inv.fieldAr.contains(searchQuery, ignoreCase = true)
                        matchesField && matchesSearch
                    }

                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(
                                            Icons.Filled.Lightbulb,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                        Column {
                                            Text(
                                                text = "موسوعة اختراعات المسلمين (${inventions.size} اختراعاً)",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            Text(
                                                text = "إسهامات واكتشافات علماء الحضارة الإسلامية التي غيرت العالم",
                                                fontSize = 10.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    Button(
                                        onClick = {
                                            if (inventions.isNotEmpty()) {
                                                randomDialogInvention = inventions.random()
                                            }
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Icon(Icons.Filled.Casino, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("اختراع عشوائي", fontSize = 11.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Filter Chips
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    items(inventionFields) { field ->
                                        FilterChip(
                                            selected = selectedInventionField == field,
                                            onClick = { selectedInventionField = field },
                                            label = { Text(field, fontSize = 11.sp) },
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (filteredInventions.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "لم يتم العثور على اختراعات مطابقة للبحث أو التصنيف",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    items(filteredInventions, key = { it.id }) { inv ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = inv.nameAr,
                                            fontSize = titleSize,
                                            fontWeight = FontWeight.Bold,
                                            color = GoldAccent
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Surface(
                                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                                shape = RoundedCornerShape(6.dp)
                                            ) {
                                                Text(
                                                    text = inv.fieldAr,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                            Text(
                                                text = inv.placeAndEraAr,
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    IconButton(
                                        onClick = {
                                            clipboardManager.setText(
                                                AnnotatedString(
                                                    "${inv.nameAr}\nالعالم: ${inv.inventorAr}\nالمجال: ${inv.fieldAr}\nالعصر: ${inv.placeAndEraAr}\n\nكيف تم الاختراع:\n${inv.howItWorksAr}\n\nالأثر العالمي:\n${inv.impactAr}"
                                                )
                                            )
                                            copiedMessage = "تم نسخ تفاصيل اختراع ${inv.nameAr}"
                                        },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            Icons.Filled.ContentCopy,
                                            contentDescription = "نسخ تفاصيل الاختراع",
                                            modifier = Modifier.size(18.dp),
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            Icons.Filled.Person,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp),
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "العالم المخترع: ${inv.inventorAr}",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "كيف تم الاختراع وآلية عمله:",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = inv.howItWorksAr,
                                    fontSize = baseTextSize,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = (baseTextSize.value * 1.5).sp
                                )

                                Spacer(modifier = Modifier.height(10.dp))
                                Surface(
                                    color = EmeraldLight.copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(
                                            text = "الأثر العالمي والإنساني:",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = EmeraldLight
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = inv.impactAr,
                                            fontSize = (baseTextSize.value - 1).sp,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            lineHeight = 18.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                5 -> {
                    // 5: أسئلة دينية
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Icon(Icons.Filled.Mosque, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                        Column {
                                            Text(text = "بنك الأسئلة الدينية والإسلامية", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                            Text(text = "موسوعة شاملة في القرآن والسيرة والفقه والعقيدة", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }

                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Button(
                                            onClick = {
                                                if (qas.isNotEmpty()) {
                                                    randomDialogQA = qas.random()
                                                }
                                            },
                                            shape = RoundedCornerShape(10.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Icon(Icons.Filled.Casino, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("سؤال عشوائي", fontSize = 11.sp)
                                        }

                                        OutlinedButton(
                                            onClick = { revealAllAnswers = !revealAllAnswers },
                                            shape = RoundedCornerShape(10.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                                        ) {
                                            Text(if (revealAllAnswers) "إخفاء الكل" else "كشف الكل", fontSize = 11.sp)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Category Filter chips
                                val religiousCategories = listOf("الكل") + qas.map { it.categoryAr }.distinct()
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    items(religiousCategories) { cat ->
                                        val isSelected = selectedReligiousCategory == cat
                                        Surface(
                                            onClick = { selectedReligiousCategory = cat },
                                            shape = RoundedCornerShape(16.dp),
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                                        ) {
                                            Text(
                                                text = cat,
                                                fontSize = 11.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    val filteredReligiousQAs = qas.filter { qa ->
                        (selectedReligiousCategory == "الكل" || qa.categoryAr == selectedReligiousCategory) &&
                                (searchQuery.isBlank() || qa.questionAr.contains(searchQuery, ignoreCase = true) || qa.answerAr.contains(searchQuery, ignoreCase = true) || qa.categoryAr.contains(searchQuery, ignoreCase = true))
                    }

                    if (filteredReligiousQAs.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                Text("لا توجد أسئلة تطابق البحث الحالي", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    itemsIndexed(filteredReligiousQAs, key = { _, it -> it.id }) { index, qa ->
                        var isAnswerRevealed by remember(qa.id, revealAllAnswers) { mutableStateOf(revealAllAnswers) }
                        Card(
                            onClick = { isAnswerRevealed = !isAnswerRevealed },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = MaterialTheme.colorScheme.primaryContainer
                                        ) {
                                            Text(
                                                text = "#${index + 1}",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                        Text(text = qa.categoryAr, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                    }

                                    IconButton(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString("س: ${qa.questionAr}\nج: ${qa.answerAr}"))
                                            copiedMessage = "تم نسخ السؤال والجواب"
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Filled.ContentCopy, contentDescription = "Copy", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "س: ${qa.questionAr}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 22.sp
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                if (isAnswerRevealed) {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Text(
                                                text = "الجواب: ${qa.answerAr}",
                                                fontSize = baseTextSize,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                lineHeight = (baseTextSize.value * 1.5).sp
                                            )
                                            if (qa.evidenceAr.isNotBlank()) {
                                                Spacer(modifier = Modifier.height(6.dp))
                                                Text(
                                                    text = "المصدر / الدليل: ${qa.evidenceAr}",
                                                    fontSize = 10.sp,
                                                    color = EmeraldLight,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "اضغط لكشف الجواب الشافي...",
                                                fontSize = 11.sp,
                                                color = GoldAccent,
                                                fontWeight = FontWeight.Medium
                                            )
                                            Icon(Icons.Filled.Visibility, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                6 -> {
                    // 6: أسئلة عامة وثقافية (New General Questions Section!)
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Icon(Icons.Filled.Psychology, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                        Column {
                                            Text(text = "بنك الأسئلة العامة والثقافية", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
                                            Text(text = "معلومات في العلوم والجغرافيا والفلك والتاريخ والذكاء", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }

                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Button(
                                            onClick = {
                                                if (generalQAs.isNotEmpty()) {
                                                    randomDialogQA = generalQAs.random()
                                                }
                                            },
                                            shape = RoundedCornerShape(10.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Icon(Icons.Filled.Casino, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("سؤال عشوائي", fontSize = 11.sp)
                                        }

                                        OutlinedButton(
                                            onClick = { revealAllAnswers = !revealAllAnswers },
                                            shape = RoundedCornerShape(10.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                                        ) {
                                            Text(if (revealAllAnswers) "إخفاء الكل" else "كشف الكل", fontSize = 11.sp)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Category Filter chips
                                val generalCategories = listOf("الكل") + generalQAs.map { it.categoryAr }.distinct()
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    items(generalCategories) { cat ->
                                        val isSelected = selectedGeneralCategory == cat
                                        Surface(
                                            onClick = { selectedGeneralCategory = cat },
                                            shape = RoundedCornerShape(16.dp),
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                                        ) {
                                            Text(
                                                text = cat,
                                                fontSize = 11.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    val filteredGeneralQAs = generalQAs.filter { qa ->
                        (selectedGeneralCategory == "الكل" || qa.categoryAr == selectedGeneralCategory) &&
                                (searchQuery.isBlank() || qa.questionAr.contains(searchQuery, ignoreCase = true) || qa.answerAr.contains(searchQuery, ignoreCase = true) || qa.categoryAr.contains(searchQuery, ignoreCase = true))
                    }

                    if (filteredGeneralQAs.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                Text("لا توجد أسئلة تطابق البحث الحالي", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    itemsIndexed(filteredGeneralQAs, key = { _, it -> it.id }) { index, qa ->
                        var isAnswerRevealed by remember(qa.id, revealAllAnswers) { mutableStateOf(revealAllAnswers) }
                        var showHint by remember { mutableStateOf(false) }

                        Card(
                            onClick = { isAnswerRevealed = !isAnswerRevealed },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = MaterialTheme.colorScheme.secondaryContainer
                                        ) {
                                            Text(
                                                text = "#${index + 1}",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                        Text(text = qa.categoryAr, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)

                                        // Difficulty badge
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = when (qa.difficultyAr) {
                                                "سهل" -> EmeraldLight.copy(alpha = 0.2f)
                                                "صعب" -> Color(0xFFE57373).copy(alpha = 0.2f)
                                                else -> GoldAccent.copy(alpha = 0.2f)
                                            }
                                        ) {
                                            Text(
                                                text = qa.difficultyAr,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = when (qa.difficultyAr) {
                                                    "سهل" -> EmeraldLight
                                                    "صعب" -> Color(0xFFD32F2F)
                                                    else -> GoldAccent
                                                },
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                            )
                                        }
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (qa.hintAr.isNotBlank()) {
                                            IconButton(
                                                onClick = { showHint = !showHint },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(Icons.Filled.Lightbulb, contentDescription = "Hint", tint = GoldAccent, modifier = Modifier.size(16.dp))
                                            }
                                        }
                                        IconButton(
                                            onClick = {
                                                clipboardManager.setText(AnnotatedString("س: ${qa.questionAr}\nج: ${qa.answerAr}"))
                                                copiedMessage = "تم نسخ السؤال والجواب"
                                            },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(Icons.Filled.ContentCopy, contentDescription = "Copy", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }

                                if (showHint && qa.hintAr.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = GoldAccent.copy(alpha = 0.1f),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "💡 تلميح: ${qa.hintAr}",
                                            fontSize = 11.sp,
                                            color = GoldAccent,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "س: ${qa.questionAr}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 22.sp
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                if (isAnswerRevealed) {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Text(
                                                text = "الجواب: ${qa.answerAr}",
                                                fontSize = baseTextSize,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                lineHeight = (baseTextSize.value * 1.5).sp
                                            )
                                        }
                                    }
                                } else {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "اضغط لكشف الإجابة الصحيحة...",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.primary,
                                                fontWeight = FontWeight.Medium
                                            )
                                            Icon(Icons.Filled.Visibility, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
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

    // Random Question Quiz Dialog
    if (randomDialogQA != null) {
        var revealedInDialog by remember { mutableStateOf(false) }
        val isReligious = randomDialogQA is IslamicQA
        val questionText = if (isReligious) (randomDialogQA as IslamicQA).questionAr else (randomDialogQA as GeneralQA).questionAr
        val answerText = if (isReligious) (randomDialogQA as IslamicQA).answerAr else (randomDialogQA as GeneralQA).answerAr
        val categoryText = if (isReligious) (randomDialogQA as IslamicQA).categoryAr else (randomDialogQA as GeneralQA).categoryAr

        AlertDialog(
            onDismissRequest = { randomDialogQA = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(if (isReligious) Icons.Filled.Mosque else Icons.Filled.Psychology, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text(if (isReligious) "تحدي الأسئلة الدينية" else "تحدي الأسئلة العامة", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(text = "التصنيف: $categoryText", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                    Text(text = "سؤال:\n$questionText", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)

                    if (revealedInDialog) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "الإجابة الصحيحة:\n$answerText",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    } else {
                        Button(
                            onClick = { revealedInDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Filled.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("كشف الإجابة", fontSize = 12.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        revealedInDialog = false
                        randomDialogQA = if (isReligious) qas.randomOrNull() else generalQAs.randomOrNull()
                    }
                ) {
                    Text("سؤال آخر")
                }
            },
            dismissButton = {
                TextButton(onClick = { randomDialogQA = null }) {
                    Text("إغلاق")
                }
            }
        )
    }

    if (randomDialogInvention != null) {
        val inv = randomDialogInvention!!
        AlertDialog(
            onDismissRequest = { randomDialogInvention = null },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Filled.Lightbulb,
                        contentDescription = null,
                        tint = GoldAccent
                    )
                    Text("اختراع إسلامي ملهم", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = inv.nameAr,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldAccent
                    )
                    Text(
                        text = "المخترع: ${inv.inventorAr} (${inv.placeAndEraAr})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "آلية الاختراع:\n${inv.howItWorksAr}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        color = EmeraldLight.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "الأثر العالمي: ${inv.impactAr}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(8.dp),
                            lineHeight = 16.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        randomDialogInvention = inventions.randomOrNull()
                    }
                ) {
                    Text("اختراع آخر")
                }
            },
            dismissButton = {
                TextButton(onClick = { randomDialogInvention = null }) {
                    Text("إغلاق")
                }
            }
        )
    }
}
