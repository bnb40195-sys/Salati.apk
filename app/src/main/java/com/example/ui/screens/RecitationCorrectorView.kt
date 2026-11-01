package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.local.IslamicDataset
import com.example.data.models.Ayah
import com.example.data.models.Qari
import com.example.data.models.Surah
import com.example.data.services.*
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.GoldAccent
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RecitationCorrectorView(
    surahs: List<Surah>,
    selectedQari: Qari,
    playbackState: PlaybackState,
    onPlayAudio: (url: String, title: String, subtitle: String) -> Unit,
    onStopAudio: () -> Unit,
    onLoadAyahs: suspend (Int) -> List<Ayah>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Active Surah & Ayah selection
    var selectedSurah by remember {
        mutableStateOf(
            surahs.firstOrNull() ?: Surah(
                number = 1,
                nameAr = "الفاتحة",
                nameEn = "Al-Fatihah",
                translation = "The Opening",
                versesCount = 7,
                revelationType = "مكية",
                pageNumber = 1
            )
        )
    }
    var ayahsForSurah by remember { mutableStateOf<List<Ayah>>(emptyList()) }
    var selectedAyahIndex by remember { mutableIntStateOf(0) }
    var isLoadingAyahs by remember { mutableStateOf(false) }

    // Speech recognizer instance
    val speechRecognizer = remember { QuranSpeechRecognizer(context) }
    val speechState by speechRecognizer.state.collectAsState()
    val partialResults by speechRecognizer.partialResults.collectAsState()

    // Evaluation results
    var evaluationResult by remember { mutableStateOf<RecitationEvaluationResult?>(null) }
    var manualInputText by remember { mutableStateOf("") }
    var showManualInputDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Load ayahs when Surah changes
    LaunchedEffect(selectedSurah.number) {
        isLoadingAyahs = true
        evaluationResult = null
        selectedAyahIndex = 0
        try {
            val list = onLoadAyahs(selectedSurah.number)
            ayahsForSurah = if (list.isNotEmpty()) list else listOf(
                Ayah(numberInSurah = 1, textAr = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ")
            )
        } catch (e: Exception) {
            ayahsForSurah = listOf(
                Ayah(numberInSurah = 1, textAr = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ")
            )
        } finally {
            isLoadingAyahs = false
        }
    }

    val currentAyah = ayahsForSurah.getOrNull(selectedAyahIndex) ?: Ayah(
        numberInSurah = 1, textAr = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ"
    )

    // Evaluate speech results when recognized
    LaunchedEffect(speechState) {
        when (val st = speechState) {
            is SpeechRecognitionState.Success -> {
                evaluationResult = QuranRecitationEvaluator.evaluate(
                    expectedAyahText = currentAyah.textAr,
                    spokenText = st.recognizedText
                )
                errorMessage = null
            }
            is SpeechRecognitionState.Error -> {
                errorMessage = st.message
            }
            else -> Unit
        }
    }

    // Permission launcher for Microphone
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            speechRecognizer.startListening()
        } else {
            errorMessage = "يلزم إذن الميكروفون للتعرف على التلاوة الصوتية، يمكنك استخدام الإدخال اليدوي أو المحاكاة"
        }
    }

    // Microphone Pulse Animation
    val isListening = speechState is SpeechRecognitionState.Listening || speechState is SpeechRecognitionState.Processing
    val infiniteTransition = rememberInfiniteTransition(label = "mic_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isListening) 1.25f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mic_pulse_scale"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Section Header Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 3.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.35f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "مصحح التلاوة والتجويد الذكي",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldAccent
                        )
                        Text(
                            text = "استمع لقراءة الشيخ، ثم رتل بصوتك ليكشف المصحح الأخطاء بالأحمر بدقة",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 16.sp
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = EmeraldLight.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "تصحيح فوري",
                            color = EmeraldLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Surah Selector Row
                Text(
                    text = "اختر السورة الكريمة:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(surahs) { surah ->
                        val isSelected = selectedSurah.number == surah.number
                        Surface(
                            onClick = {
                                selectedSurah = surah
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, GoldAccent) else null
                        ) {
                            Text(
                                text = "${surah.number}. ${surah.nameAr}",
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Ayah Display & Navigation Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Navigation top row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Previous Ayah Button
                    IconButton(
                        onClick = {
                            if (selectedAyahIndex > 0) {
                                selectedAyahIndex--
                                evaluationResult = null
                            }
                        },
                        enabled = selectedAyahIndex > 0
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowForward,
                            contentDescription = "الآية السابقة",
                            tint = if (selectedAyahIndex > 0) GoldAccent else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    }

                    // Ayah badge
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = "سورة ${selectedSurah.nameAr} • الآية ${currentAyah.numberInSurah} من ${ayahsForSurah.size.coerceAtLeast(1)}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }

                    // Next Ayah Button
                    IconButton(
                        onClick = {
                            if (selectedAyahIndex < ayahsForSurah.size - 1) {
                                selectedAyahIndex++
                                evaluationResult = null
                            }
                        },
                        enabled = selectedAyahIndex < ayahsForSurah.size - 1
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "الآية التالية",
                            tint = if (selectedAyahIndex < ayahsForSurah.size - 1) GoldAccent else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Ayah Arabic Text
                Text(
                    text = currentAyah.textAr,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    lineHeight = 36.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Reciter Audio Player Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val isPlaying = playbackState.isPlaying
                    FilledTonalButton(
                        onClick = {
                            if (isPlaying) {
                                onStopAudio()
                            } else {
                                val formattedNum = String.format("%03d%03d", selectedSurah.number, currentAyah.numberInSurah)
                                val audioUrl = "https://everyayah.com/data/Alafasy_128kbps/$formattedNum.mp3"
                                onPlayAudio(audioUrl, "سورة ${selectedSurah.nameAr} - آية ${currentAyah.numberInSurah}", "بصوت ${selectedQari.nameAr}")
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = if (isPlaying) MaterialTheme.colorScheme.errorContainer else EmeraldLight.copy(alpha = 0.18f),
                            contentColor = if (isPlaying) MaterialTheme.colorScheme.error else EmeraldLight
                        )
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Filled.Stop else Icons.Filled.VolumeUp,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isPlaying) "إيقاف التلاوة" else "استمع لتلاوة الشيخ (${selectedQari.nameAr})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Microphone & Recitation Area
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 3.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, if (isListening) GoldAccent else Color.Transparent)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isListening) "جاري الاستماع لتلاوتك... رتل الآن بصوت واضح" else "اضغط على الميكروفون وتلُ الآية بصوتك",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isListening) GoldAccent else MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Big Mic Button with Pulse Animation
                Box(contentAlignment = Alignment.Center) {
                    if (isListening) {
                        Box(
                            modifier = Modifier
                                .size(88.dp)
                                .scale(pulseScale)
                                .clip(CircleShape)
                                .background(GoldAccent.copy(alpha = 0.2f))
                        )
                    }

                    IconButton(
                        onClick = {
                            if (isListening) {
                                speechRecognizer.stopListening()
                            } else {
                                val hasPermission = ContextCompat.checkSelfPermission(
                                    context, Manifest.permission.RECORD_AUDIO
                                ) == PackageManager.PERMISSION_GRANTED

                                if (hasPermission) {
                                    speechRecognizer.startListening()
                                } else {
                                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                }
                            }
                        },
                        modifier = Modifier
                            .testTag("recitation_mic_button")
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    if (isListening) listOf(Color(0xFFDC2626), Color(0xFFEF4444))
                                    else listOf(Color(0xFF0F766E), Color(0xFF14B8A6))
                                )
                            )
                    ) {
                        Icon(
                            imageVector = if (isListening) Icons.Filled.Stop else Icons.Filled.Mic,
                            contentDescription = "بدء التلاوة",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                if (isListening && partialResults.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "الكلمات الملتقطة: $partialResults",
                        fontSize = 12.sp,
                        color = GoldAccent,
                        textAlign = TextAlign.Center
                    )
                }

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = errorMessage ?: "",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Alternative Options: Simulate Recitation or Manual Text
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = {
                            manualInputText = currentAyah.textAr
                            showManualInputDialog = true
                        },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("تجربة تلاوة مكتوبة / فحص", fontSize = 11.sp)
                    }

                    FilledTonalButton(
                        onClick = {
                            // Perfect test recitation demonstration
                            evaluationResult = QuranRecitationEvaluator.evaluate(
                                expectedAyahText = currentAyah.textAr,
                                spokenText = currentAyah.textAr
                            )
                        },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("محاكاة تلاوة متقنة", fontSize = 11.sp)
                    }
                }
            }
        }

        // Evaluation Breakdown Card (HIGHLIGHTING ERRORS IN RED)
        if (evaluationResult != null) {
            val res = evaluationResult!!
            Spacer(modifier = Modifier.height(18.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("evaluation_results_card"),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 4.dp,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (res.accuracyPercentage >= 90) EmeraldLight else if (res.accuracyPercentage >= 70) GoldAccent else Color(0xFFEF4444)
                )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    // Score & Feedback Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "نتيجة فحص التلاوة",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = res.feedbackMessage,
                                fontSize = 12.sp,
                                color = if (res.accuracyPercentage >= 90) EmeraldLight else if (res.accuracyPercentage >= 70) GoldAccent else Color(0xFFEF4444),
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Percentage Badge
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (res.accuracyPercentage >= 90) EmeraldLight.copy(alpha = 0.2f)
                            else if (res.accuracyPercentage >= 70) GoldAccent.copy(alpha = 0.2f)
                            else Color(0xFFEF4444).copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "${res.accuracyPercentage}%",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (res.accuracyPercentage >= 90) EmeraldLight
                                else if (res.accuracyPercentage >= 70) GoldAccent
                                else Color(0xFFEF4444),
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Explanation Guide Legend
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = EmeraldLight.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldLight)
                        ) {
                            Text(
                                text = "🟢 صحيح ومتقن (${res.correctWordsCount})",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldLight,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFEF4444).copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444))
                        ) {
                            Text(
                                text = "🔴 أخطأت هنا (${res.errorWordsCount})",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFEF4444),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "تفصيل الكلمات المرتلة والتقييم:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Word-by-Word Flow Layout with GREEN for correct and RED for errors
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        res.words.forEach { w ->
                            val isCorrect = w.isCorrect
                            val bgCol = if (isCorrect) Color(0xFF10B981).copy(alpha = 0.18f) else Color(0xFFEF4444).copy(alpha = 0.25f)
                            val borderCol = if (isCorrect) Color(0xFF10B981) else Color(0xFFEF4444)
                            val textCol = if (isCorrect) Color(0xFF10B981) else Color(0xFFEF4444)

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = bgCol,
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, borderCol),
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = w.originalWord,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = textCol
                                    )
                                    Icon(
                                        imageVector = if (isCorrect) Icons.Filled.CheckCircle else Icons.Filled.Cancel,
                                        contentDescription = if (isCorrect) "صحيح" else "خطأ",
                                        tint = textCol,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    if (res.errorWordsCount > 0) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFEF4444).copy(alpha = 0.12f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.35f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Filled.Warning, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "الكلمات الملونة بالأحمر لم تُنطق بشكل صحيح أو تم تخطيها. استمع لتلاوة الشيخ مجدداً وحاول تصحيحها.",
                                    fontSize = 12.sp,
                                    color = Color(0xFFEF4444),
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Manual Input Dialog for testing
    if (showManualInputDialog) {
        AlertDialog(
            onDismissRequest = { showManualInputDialog = false },
            title = {
                Text("إدخال تلاوتك لفحص الأخطاء", fontWeight = FontWeight.Bold, color = GoldAccent)
            },
            text = {
                Column {
                    Text(
                        "اكتب ما تلوته أو عدّل الكلمات لاختبار المصحح واكتشاف الكلمات الخاطئة بالأحمر:",
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = manualInputText,
                        onValueChange = { manualInputText = it },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 4
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        evaluationResult = QuranRecitationEvaluator.evaluate(
                            expectedAyahText = currentAyah.textAr,
                            spokenText = manualInputText
                        )
                        showManualInputDialog = false
                    }
                ) {
                    Text("فحص التلاوة")
                }
            },
            dismissButton = {
                TextButton(onClick = { showManualInputDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}
