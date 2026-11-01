package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.models.Ayah
import com.example.data.models.Qari
import com.example.data.models.Surah
import com.example.data.services.QuranRecitationEvaluator
import com.example.data.services.QuranSpeechRecognizer
import com.example.data.services.RecitationEvaluationResult
import com.example.data.services.SpeechRecognitionState
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.GoldAccent
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SmartRecitationCorrectionScreen(
    surahs: List<Surah>,
    qaris: List<Qari>,
    selectedQari: Qari,
    onPlayAyahAudio: (audioUrl: String, title: String) -> Unit,
    onStopAudio: () -> Unit,
    onLoadAyahs: suspend (Int) -> List<Ayah>,
    onSelectQari: (Qari) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val speechRecognizer = remember { QuranSpeechRecognizer(context) }
    val speechState by speechRecognizer.state.collectAsState()
    val partialResults by speechRecognizer.partialResults.collectAsState()

    var selectedSurah by remember { mutableStateOf(surahs.firstOrNull { it.number == 1 } ?: surahs.first()) }
    var loadedAyahs by remember { mutableStateOf<List<Ayah>>(emptyList()) }
    var selectedAyahIndex by remember { mutableIntStateOf(0) }
    var isLoadingAyahs by remember { mutableStateOf(false) }

    var evaluationResult by remember { mutableStateOf<RecitationEvaluationResult?>(null) }
    var selectedWordErrorExplanation by remember { mutableStateOf<String?>(null) }
    var hasRecordPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasRecordPermission = isGranted
        if (isGranted) {
            speechRecognizer.startListening()
        }
    }

    // Load ayahs when surah changes
    LaunchedEffect(selectedSurah.number) {
        isLoadingAyahs = true
        evaluationResult = null
        val ayahs = onLoadAyahs(selectedSurah.number)
        loadedAyahs = ayahs
        selectedAyahIndex = 0
        isLoadingAyahs = false
    }

    // Handle speech recognition success and error
    LaunchedEffect(speechState) {
        when (val state = speechState) {
            is SpeechRecognitionState.Success -> {
                val currentAyah = loadedAyahs.getOrNull(selectedAyahIndex)
                if (currentAyah != null) {
                    val result = QuranRecitationEvaluator.evaluate(currentAyah.textAr, state.recognizedText)
                    evaluationResult = result
                }
            }
            else -> Unit
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            speechRecognizer.destroy()
        }
    }

    val currentAyah = loadedAyahs.getOrNull(selectedAyahIndex)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("smart_recitation_screen")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = EmeraldLight.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "مصحح التلاوة الصوتي 🎙️",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldLight,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            Text(
                                text = "التلاوة وتصحيح الأخطاء",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldAccent
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "استمع لتلاوة القارئ أولاً، ثم اتلُ الآية بصوتك ليقوم المصحح الآلي باكتشاف الأخطاء وتلوينها باللون الأحمر مع بيان الصواب.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // Quick Surah Selector
            item {
                Text(
                    text = "اختر السورة الكريمة:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val recommendedSurahs = surahs.filter { it.number in listOf(1, 112, 113, 114, 108, 110, 97, 2) }
                    items(recommendedSurahs) { s ->
                        val isSelected = selectedSurah.number == s.number
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedSurah = s },
                            label = { Text("سورة ${s.nameAr}", fontSize = 12.sp) }
                        )
                    }
                }
            }

            // Ayah Card & Navigation
            item {
                if (isLoadingAyahs) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = GoldAccent)
                    }
                } else if (currentAyah != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.4f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Ayah indicator row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(
                                    onClick = {
                                        if (selectedAyahIndex > 0) {
                                            selectedAyahIndex--
                                            evaluationResult = null
                                            selectedWordErrorExplanation = null
                                        }
                                    },
                                    enabled = selectedAyahIndex > 0
                                ) {
                                    Text("◀ الآية السابقة")
                                }

                                Text(
                                    text = "سورة ${selectedSurah.nameAr} • آية ${currentAyah.numberInSurah}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldAccent
                                )

                                TextButton(
                                    onClick = {
                                        if (selectedAyahIndex < loadedAyahs.size - 1) {
                                            selectedAyahIndex++
                                            evaluationResult = null
                                            selectedWordErrorExplanation = null
                                        }
                                    },
                                    enabled = selectedAyahIndex < loadedAyahs.size - 1
                                ) {
                                    Text("الآية التالية ▶")
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Interactive Word Display (shows errors in red if evaluated)
                            if (evaluationResult != null) {
                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    evaluationResult!!.words.forEach { evaluatedWord ->
                                        val isError = !evaluatedWord.isCorrect
                                        val wordBg = if (isError) Color(0xFFEF4444).copy(alpha = 0.2f) else EmeraldLight.copy(alpha = 0.15f)
                                        val wordBorder = if (isError) Color(0xFFEF4444) else EmeraldLight
                                        val wordColor = if (isError) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurface

                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = wordBg,
                                            border = androidx.compose.foundation.BorderStroke(1.5.dp, wordBorder),
                                            modifier = Modifier
                                                .padding(horizontal = 4.dp)
                                                .clickable {
                                                    if (isError) {
                                                        selectedWordErrorExplanation = "خطأ في كلمة [${evaluatedWord.originalWord}]: ${evaluatedWord.errorMessage ?: "نطق غير مطابق"}"
                                                    } else {
                                                        selectedWordErrorExplanation = "كلمة [${evaluatedWord.originalWord}]: نطق صحيح ومتقن ما شاء الله ✅"
                                                    }
                                                }
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                if (isError) {
                                                    Icon(
                                                        imageVector = Icons.Filled.Close,
                                                        contentDescription = "أخطأت هنا",
                                                        tint = Color(0xFFEF4444),
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                } else {
                                                    Icon(
                                                        imageVector = Icons.Filled.Check,
                                                        contentDescription = "صحيحة",
                                                        tint = EmeraldLight,
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                }
                                                Text(
                                                    text = evaluatedWord.originalWord,
                                                    fontSize = 20.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = wordColor
                                                )
                                            }
                                        }
                                    }
                                }
                            } else {
                                // Default Ayah text display
                                Text(
                                    text = currentAyah.textAr,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 36.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Listen to Reciter Button
                            FilledTonalButton(
                                onClick = {
                                    val formattedNum = String.format("%03d%03d", selectedSurah.number, currentAyah.numberInSurah)
                                    val audioUrl = "https://everyayah.com/data/Alafasy_128kbps/$formattedNum.mp3"
                                    onPlayAyahAudio(audioUrl, "تلاوة بصوت ${selectedQari.nameAr}")
                                },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Filled.VolumeUp, contentDescription = null, tint = GoldAccent)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("استمع لتلاوة القارئ (${selectedQari.nameAr})")
                            }
                        }
                    }
                }
            }

            // Word Error Tip / Explanation Card
            if (selectedWordErrorExplanation != null) {
                item {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF231818),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Info,
                                contentDescription = null,
                                tint = Color(0xFFEF4444)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = selectedWordErrorExplanation!!,
                                fontSize = 13.sp,
                                color = Color(0xFFFCA5A5),
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = { selectedWordErrorExplanation = null }) {
                                Icon(Icons.Filled.Close, contentDescription = "إغلاق", tint = Color.LightGray)
                            }
                        }
                    }
                }
            }

            // Evaluation Result Card (Score & Summary)
            if (evaluationResult != null) {
                item {
                    val res = evaluationResult!!
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (res.accuracyPercentage >= 80) Color(0xFF0F2B1D) else Color(0xFF2E1515)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (res.accuracyPercentage >= 80) EmeraldLight else Color(0xFFEF4444)
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "نتيجة التلاوة: ${res.accuracyPercentage}%",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (res.accuracyPercentage >= 80) EmeraldLight else Color(0xFFEF4444)
                                )

                                Text(
                                    text = "الكلمات الصحيحة: ${res.correctWordsCount} / ${res.totalWords}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = res.feedbackMessage,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            if (res.errorWordsCount > 0) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "⚠️ الكلمات التي أخطأت فيها تم تلوينها باللون الأحمر أعلاه، اضغط على أي كلمة لمعرفة الصواب.",
                                    fontSize = 12.sp,
                                    color = Color(0xFFEF4444)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "ما تم سماعه من صوتك: \"${res.spokenText}\"",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Speech Recording Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = when (speechState) {
                                is SpeechRecognitionState.Listening -> "جاري الاستماع... اقرأ الآية بوضوح 🎙️"
                                is SpeechRecognitionState.Processing -> "جاري تدقيق التلاوة واكتشاف الأخطاء..."
                                is SpeechRecognitionState.Error -> (speechState as SpeechRecognitionState.Error).message
                                else -> "اضغط زر الميكروفون وتلُ الآية ليصحح لك التطبيق"
                            },
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = when (speechState) {
                                is SpeechRecognitionState.Listening -> EmeraldLight
                                is SpeechRecognitionState.Error -> Color(0xFFEF4444)
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            textAlign = TextAlign.Center
                        )

                        if (partialResults.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "المسموع: $partialResults",
                                fontSize = 12.sp,
                                color = GoldAccent
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        val isListening = speechState is SpeechRecognitionState.Listening

                        FilledIconButton(
                            onClick = {
                                if (isListening) {
                                    speechRecognizer.stopListening()
                                } else {
                                    if (hasRecordPermission) {
                                        evaluationResult = null
                                        selectedWordErrorExplanation = null
                                        speechRecognizer.startListening()
                                    } else {
                                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                    }
                                }
                            },
                            modifier = Modifier
                                .testTag("record_recitation_button")
                                .size(68.dp),
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = if (isListening) Color(0xFFEF4444) else EmeraldLight
                            )
                        ) {
                            Icon(
                                imageVector = if (isListening) Icons.Filled.Stop else Icons.Filled.Mic,
                                contentDescription = if (isListening) "إيقاف التسجيل" else "بدء التلاوة",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isListening) "اضغط لإيقاف التسجيل وعرض النتيجة" else "بدء تلاوة الآية",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}
