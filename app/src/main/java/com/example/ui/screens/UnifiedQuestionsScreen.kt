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
import com.example.data.models.GeneralQA
import com.example.data.models.IslamicQA
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.GoldAccent

@Composable
fun UnifiedQuestionsScreen(
    religiousQAs: List<IslamicQA>,
    generalQAs: List<GeneralQA>,
    initialTab: Int = 0,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(initialTab) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedReligiousCategory by remember { mutableStateOf("الكل") }
    var selectedGeneralCategory by remember { mutableStateOf("الكل") }
    var revealAllAnswers by remember { mutableStateOf(false) }

    // Dialog state for random quiz test
    var showRandomDialog by remember { mutableStateOf(false) }
    var currentRandomQuestion by remember { mutableStateOf<Any?>(null) }
    var isRandomAnswerRevealed by remember { mutableStateOf(false) }

    val religiousCategories = remember(religiousQAs) {
        listOf("الكل") + religiousQAs.map { it.categoryAr }.distinct()
    }

    val generalCategories = remember(generalQAs) {
        listOf("الكل") + generalQAs.map { it.categoryAr }.distinct()
    }

    val filteredReligious = remember(religiousQAs, searchQuery, selectedReligiousCategory) {
        religiousQAs.filter { qa ->
            val matchesCat = selectedReligiousCategory == "الكل" || qa.categoryAr == selectedReligiousCategory
            val matchesQuery = searchQuery.isBlank() ||
                    qa.questionAr.contains(searchQuery, ignoreCase = true) ||
                    qa.answerAr.contains(searchQuery, ignoreCase = true) ||
                    qa.evidenceAr.contains(searchQuery, ignoreCase = true)
            matchesCat && matchesQuery
        }
    }

    val filteredGeneral = remember(generalQAs, searchQuery, selectedGeneralCategory) {
        generalQAs.filter { qa ->
            val matchesCat = selectedGeneralCategory == "الكل" || qa.categoryAr == selectedGeneralCategory
            val matchesQuery = searchQuery.isBlank() ||
                    qa.questionAr.contains(searchQuery, ignoreCase = true) ||
                    qa.answerAr.contains(searchQuery, ignoreCase = true) ||
                    qa.hintAr.contains(searchQuery, ignoreCase = true)
            matchesCat && matchesQuery
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("unified_questions_screen")
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
                            text = "بنك الأسئلة الدينية والعامة",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.End
                        )
                        Text(
                            text = "مسابقات ومعارف إسلامية وعامة مع الأدلة والشروح",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.End
                        )
                    }

                    // Random question button
                    IconButton(
                        onClick = {
                            val pool: List<Any> = if (selectedTab == 0) religiousQAs else generalQAs
                            if (pool.isNotEmpty()) {
                                currentRandomQuestion = pool.random()
                                isRandomAnswerRevealed = false
                                showRandomDialog = true
                            }
                        },
                        modifier = Modifier.testTag("random_question_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Shuffle,
                            contentDescription = "سؤال عشوائي",
                            tint = GoldAccent
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
                                "أسئلة دينية وفقهية (${filteredReligious.size})",
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp
                            )
                        },
                        icon = {
                            Icon(Icons.Filled.MenuBook, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                "أسئلة عامة وثقافية (${filteredGeneral.size})",
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp
                            )
                        },
                        icon = {
                            Icon(Icons.Filled.Quiz, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Search field & Global Reveal Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text(
                                if (selectedTab == 0) "ابحث في الأسئلة الدينية أو الأدلة..." else "ابحث في الأسئلة العامة أو المعارف...",
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
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    FilledTonalButton(
                        onClick = { revealAllAnswers = !revealAllAnswers },
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = if (revealAllAnswers) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (revealAllAnswers) "إخفاء الكل" else "كشف الكل",
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Category Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val cats = if (selectedTab == 0) religiousCategories else generalCategories
                    items(cats) { cat ->
                        val isSelected = if (selectedTab == 0) selectedReligiousCategory == cat else selectedGeneralCategory == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                if (selectedTab == 0) selectedReligiousCategory = cat
                                else selectedGeneralCategory = cat
                            },
                            label = { Text(cat, fontSize = 12.sp) }
                        )
                    }
                }
            }
        }

        // List Content
        if (selectedTab == 0) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredReligious, key = { it.id }) { qa ->
                    var isRevealed by remember { mutableStateOf(false) }
                    val actuallyRevealed = isRevealed || revealAllAnswers

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { isRevealed = !isRevealed },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Text(
                                        text = qa.categoryAr,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                Icon(
                                    imageVector = if (actuallyRevealed) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = GoldAccent
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "س: ${qa.questionAr}",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 22.sp
                            )

                            AnimatedVisibility(visible = actuallyRevealed) {
                                Column(modifier = Modifier.padding(top = 10.dp)) {
                                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = "ج: ${qa.answerAr}",
                                        fontSize = 14.sp,
                                        color = EmeraldLight,
                                        fontWeight = FontWeight.Medium,
                                        lineHeight = 22.sp
                                    )

                                    if (qa.evidenceAr.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(modifier = Modifier.padding(10.dp)) {
                                                Text(
                                                    text = "الدليل والبيان الشرعي:",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = GoldAccent
                                                )
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = qa.evidenceAr,
                                                    fontSize = 12.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            if (!actuallyRevealed) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "اضغط لإظهار الإجابة والدليل الشرعي",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
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
                items(filteredGeneral, key = { it.id }) { gqa ->
                    var isRevealed by remember { mutableStateOf(false) }
                    val actuallyRevealed = isRevealed || revealAllAnswers

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { isRevealed = !isRevealed },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.secondaryContainer
                                ) {
                                    Text(
                                        text = "${gqa.categoryAr} • ${gqa.difficultyAr}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                Icon(
                                    imageVector = if (actuallyRevealed) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = GoldAccent
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "س: ${gqa.questionAr}",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 22.sp
                            )

                            if (gqa.hintAr.isNotBlank() && !actuallyRevealed) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "تلميح: ${gqa.hintAr}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            AnimatedVisibility(visible = actuallyRevealed) {
                                Column(modifier = Modifier.padding(top = 10.dp)) {
                                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = "الجواب: ${gqa.answerAr}",
                                        fontSize = 14.sp,
                                        color = EmeraldLight,
                                        fontWeight = FontWeight.Bold,
                                        lineHeight = 22.sp
                                    )
                                }
                            }

                            if (!actuallyRevealed) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "اضغط لكشف الجواب الصحيح",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Random Question Dialog
    if (showRandomDialog && currentRandomQuestion != null) {
        val q = currentRandomQuestion
        AlertDialog(
            onDismissRequest = { showRandomDialog = false },
            title = {
                Text(
                    text = if (q is IslamicQA) "سؤال ديني عشوائي 💡" else "سؤال ثقافي عام 💡",
                    fontWeight = FontWeight.Bold,
                    color = GoldAccent
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    if (q is IslamicQA) {
                        Text(
                            text = q.questionAr,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 22.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        if (isRandomAnswerRevealed) {
                            Text(
                                text = "الجواب: ${q.answerAr}",
                                fontSize = 14.sp,
                                color = EmeraldLight,
                                fontWeight = FontWeight.Bold
                            )
                            if (q.evidenceAr.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "الدليل: ${q.evidenceAr}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            Text(
                                text = "فكر في الإجابة أولاً ثم اضغط كشف الجواب!",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else if (q is GeneralQA) {
                        Text(
                            text = q.questionAr,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 22.sp
                        )
                        if (q.hintAr.isNotBlank() && !isRandomAnswerRevealed) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "تلميح: ${q.hintAr}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        if (isRandomAnswerRevealed) {
                            Text(
                                text = "الجواب: ${q.answerAr}",
                                fontSize = 14.sp,
                                color = EmeraldLight,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (!isRandomAnswerRevealed) {
                            isRandomAnswerRevealed = true
                        } else {
                            // Pick another random question
                            val pool: List<Any> = if (selectedTab == 0) religiousQAs else generalQAs
                            if (pool.isNotEmpty()) {
                                currentRandomQuestion = pool.random()
                                isRandomAnswerRevealed = false
                            }
                        }
                    }
                ) {
                    Text(if (!isRandomAnswerRevealed) "كشف الجواب" else "سؤال آخر ↻")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRandomDialog = false }) {
                    Text("إغلاق")
                }
            }
        )
    }
}
