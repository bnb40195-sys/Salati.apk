package com.example.ui.screens

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.os.Build
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.CityLocation
import com.example.data.models.DayPrayerTimes
import com.example.data.models.HijriDate
import com.example.data.models.PrayerType
import com.example.widget.PrayerAppWidgetProvider
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WidgetsPreviewScreen(
    selectedCity: CityLocation,
    prayerTimes: DayPrayerTimes?,
    hijriDate: HijriDate,
    initialTheme: String = "forest",
    initialDhikr: String = "سبحان الله العظيم",
    initialShowTemp: Boolean = true,
    initialTemp: String = "24°C",
    initialAccent: String = "orange",
    onSaveConfig: (theme: String, dhikr: String, showTemp: Boolean, temp: String, accent: String) -> Unit = { _, _, _, _, _ -> },
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Live Customization State
    var selectedTheme by remember { mutableStateOf(initialTheme) }
    var currentDhikr by remember { mutableStateOf(initialDhikr) }
    var customDhikrInput by remember { mutableStateOf(initialDhikr) }
    var showTemperature by remember { mutableStateOf(initialShowTemp) }
    var temperatureText by remember { mutableStateOf(initialTemp) }
    var selectedAccent by remember { mutableStateOf(initialAccent) }
    var showPinInstructionsDialog by remember { mutableStateOf(false) }

    val accentColor = when (selectedAccent) {
        "gold" -> Color(0xFFE5A93C)
        "emerald" -> Color(0xFF10B981)
        "teal" -> Color(0xFF14B8A6)
        else -> Color(0xFFFFA040) // Default Orange from user screenshot
    }

    val greenSunriseColor = Color(0xFF4ADE80) // Green from screenshot

    val timeFormatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val gregorianDateFormatter = remember { SimpleDateFormat("d MMMM yyyy", Locale("ar")) }
    val clockFormatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    val currentTimeMillis = System.currentTimeMillis()
    val gregorianDateStr = remember(currentTimeMillis) { gregorianDateFormatter.format(Date(currentTimeMillis)) }
    val currentClockStr = remember(currentTimeMillis) { clockFormatter.format(Date(currentTimeMillis)) }

    // Next Prayer & Countdown
    val prayerList = remember(prayerTimes) {
        if (prayerTimes == null) emptyList()
        else listOf(
            PrayerType.FAJR to prayerTimes.fajr,
            PrayerType.SUNRISE to prayerTimes.sunrise,
            PrayerType.DHUHR to prayerTimes.dhuhr,
            PrayerType.ASR to prayerTimes.asr,
            PrayerType.MAGHRIB to prayerTimes.maghrib,
            PrayerType.ISHA to prayerTimes.isha
        )
    }

    val (nextPrayer, nextPrayerMillis) = remember(prayerList, currentTimeMillis) {
        val next = prayerList.firstOrNull { it.second > currentTimeMillis }
        if (next != null) next
        else PrayerType.FAJR to ((prayerList.firstOrNull()?.second ?: currentTimeMillis) + 86400000L)
    }

    val diffMinutes = remember(nextPrayerMillis, currentTimeMillis) {
        val diff = (nextPrayerMillis - currentTimeMillis).coerceAtLeast(0)
        (diff / (1000 * 60)).toInt()
    }

    val countdownDisplay = if (diffMinutes < 60) "$diffMinutes دقيقة" else "${diffMinutes / 60} ساعة و ${diffMinutes % 60} د"
    val nextPrayerFormattedTime = timeFormatter.format(Date(nextPrayerMillis))

    // Background gradient for preview
    val cardBackgroundBrush = when (selectedTheme) {
        "amoled" -> Brush.verticalGradient(listOf(Color(0xFF0F1115), Color(0xFF060709)))
        "emerald" -> Brush.verticalGradient(listOf(Color(0xFF062B21), Color(0xFF021711)))
        "navy" -> Brush.verticalGradient(listOf(Color(0xFF0B192C), Color(0xFF030712)))
        else -> Brush.verticalGradient(
            // Forest Nature Dark (as in user screenshot)
            listOf(Color(0xFF242F23), Color(0xFF161E16), Color(0xFF0D130D))
        )
    }

    fun saveAndBroadcast() {
        onSaveConfig(selectedTheme, currentDhikr, showTemperature, temperatureText, selectedAccent)
        PrayerAppWidgetProvider.updateAllWidgets(context)
        Toast.makeText(context, "تم حفظ التعديلات وتحديث الويدجت بنجاح!", Toast.LENGTH_SHORT).show()
    }

    LazyColumn(
        modifier = modifier
            .testTag("widgets_preview_screen")
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Screen Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "ويدجت مواقيت الصلاة (Widgets Studio)",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "معاينة وتخصيص وإضافة الويدجت المطور مع درجة الحرارة",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Live Exact Widget Preview Card (Matching Screenshot)
        item {
            Text(
                text = "معاينة حية للويدجت (كما تظهر على سطح مكتب الهاتف):",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(6.dp))

            // The Widget Replica Frame
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("prayer_widget_live_preview"),
                shape = RoundedCornerShape(22.dp),
                border = BorderStroke(1.5.dp, Color.White.copy(alpha = 0.15f)),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(cardBackgroundBrush)
                        .padding(16.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Top Row: Left Countdown + Location/Temp, Right Dates + Clock
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            // Left Section (Countdown, target prayer, city & temperature)
                            Column(horizontalAlignment = Alignment.Start) {
                                Text(
                                    text = countdownDisplay,
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = accentColor
                                )
                                Text(
                                    text = "حتى ${nextPrayer.arName} @ $nextPrayerFormattedTime",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.padding(top = 2.dp)
                                ) {
                                    Text(
                                        text = "${selectedCity.nameAr}, ${selectedCity.countryAr}",
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                    if (showTemperature) {
                                        Text(
                                            text = "• $temperatureText ☀️",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = accentColor
                                        )
                                    }
                                }
                            }

                            // Right Section (Hijri Date, Gregorian Date, Digital Clock)
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = hijriDate.formattedAr,
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                                Text(
                                    text = gregorianDateStr,
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                                Text(
                                    text = currentClockStr,
                                    fontSize = 36.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    letterSpacing = 1.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // 6 Prayer Columns Horizontal Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val prayerDisplayList = listOf(
                                Triple(PrayerType.ISHA, "العشاء", prayerTimes?.let { timeFormatter.format(Date(it.isha)) } ?: "19:31"),
                                Triple(PrayerType.MAGHRIB, "المغرب", prayerTimes?.let { timeFormatter.format(Date(it.maghrib)) } ?: "18:14"),
                                Triple(PrayerType.ASR, "العصر", prayerTimes?.let { timeFormatter.format(Date(it.asr)) } ?: "15:47"),
                                Triple(PrayerType.DHUHR, "الظهر", prayerTimes?.let { timeFormatter.format(Date(it.dhuhr)) } ?: "12:51"),
                                Triple(PrayerType.SUNRISE, "الشروق", prayerTimes?.let { timeFormatter.format(Date(it.sunrise)) } ?: "07:22"),
                                Triple(PrayerType.FAJR, "الفجر", prayerTimes?.let { timeFormatter.format(Date(it.fajr)) } ?: "05:56")
                            )

                            prayerDisplayList.forEach { (type, name, timeStr) ->
                                val isNext = type == nextPrayer
                                val isSunrise = type == PrayerType.SUNRISE
                                val timeColor = when {
                                    isSunrise -> greenSunriseColor
                                    isNext -> accentColor
                                    else -> Color.White
                                }

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = name,
                                        fontSize = 12.sp,
                                        color = if (isNext) accentColor else Color.White.copy(alpha = 0.85f),
                                        fontWeight = if (isNext) FontWeight.Bold else FontWeight.Normal
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = timeStr,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = timeColor
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Bottom Center Dhikr
                        Text(
                            text = currentDhikr,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.95f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        // Action: Pin Widget to Home Screen ("إضافة الويدجت")
        item {
            Button(
                onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val appWidgetManager = context.getSystemService(AppWidgetManager::class.java)
                        val provider = ComponentName(context, PrayerAppWidgetProvider::class.java)
                        if (appWidgetManager.isRequestPinAppWidgetSupported) {
                            saveAndBroadcast()
                            appWidgetManager.requestPinAppWidget(provider, null, null)
                            Toast.makeText(context, "جاري فتح نافذة إضافة الويدجت إلى الشاشة الرئيسية...", Toast.LENGTH_LONG).show()
                        } else {
                            showPinInstructionsDialog = true
                        }
                    } else {
                        showPinInstructionsDialog = true
                    }
                },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("pin_widget_to_home_button")
            ) {
                Icon(Icons.Filled.AddCircleOutline, contentDescription = null, tint = Color.Black)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "إضافة الويدجت إلى الشاشة الرئيسية للهاتف (خارج التطبيق)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }

        // 1. Customization Section: Temperature ("أيضا إضافة الحرارة")
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "درجة الحرارة على الويدجت",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "إظهار درجة الحرارة بجانب اسم المدينة كما طلبت",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = showTemperature,
                            onCheckedChange = {
                                showTemperature = it
                                saveAndBroadcast()
                            }
                        )
                    }

                    if (showTemperature) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "درجة الحرارة العادية المعروضة (مثل التطبيق):",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(1.dp, accentColor.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Filled.Thermostat, contentDescription = null, tint = accentColor)
                                    Column {
                                        Text(
                                            text = "درجة الحرارة الحالية العادية: $temperatureText",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "مطابقة لدرجة حرارة تطبيق صلاتي وحالة الطقس المباشرة لمدينتك",
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = accentColor.copy(alpha = 0.2f),
                                    border = BorderStroke(1.dp, accentColor)
                                ) {
                                    Text(
                                        text = "تلقائي مع التطبيق",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = accentColor,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. Customization Section: Dhikr at Footer
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "الذكر المبارك أسفل الويدجت:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "اختر الذكر الظاهر أو اكتب ذكراً ودعاءً خاصاً بك",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    val presets = listOf(
                        "سبحان الله العظيم",
                        "أستغفر الله وأتوب إليه",
                        "لا إله إلا الله وحده لا شريك له",
                        "اللهم صلِّ وسلم على نبينا محمد",
                        "سبحان الله وبحمده سبحان الله العظيم",
                        "لا حول ولا قوة إلا بالله"
                    )

                    presets.chunked(2).forEach { rowPresets ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(vertical = 3.dp)
                        ) {
                            rowPresets.forEach { dhikrItem ->
                                val isSelected = currentDhikr == dhikrItem
                                Surface(
                                    onClick = {
                                        currentDhikr = dhikrItem
                                        customDhikrInput = dhikrItem
                                        saveAndBroadcast()
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) accentColor.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                                    border = if (isSelected) BorderStroke(1.dp, accentColor) else null,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = dhikrItem,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 8.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = customDhikrInput,
                        onValueChange = {
                            customDhikrInput = it
                            currentDhikr = it
                        },
                        label = { Text("أو اكتب ذكراً مخصصاً", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            IconButton(onClick = { saveAndBroadcast() }) {
                                Icon(Icons.Filled.Check, contentDescription = "حفظ الذكر", tint = accentColor)
                            }
                        },
                        singleLine = true
                    )
                }
            }
        }

        // 3. Customization Section: Background Theme
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "خلفية الويدجت:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "اختر النمط المناسب لذوقك وشاشة جهازك",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    val themes = listOf(
                        "forest" to "طبيعة وغابات (كما في الصورة)",
                        "amoled" to "أسود فاحم AMOLED",
                        "emerald" to "أخضر زمردي إسلامي",
                        "navy" to "أزرق ملكي ليلي"
                    )

                    themes.forEach { (key, label) ->
                        val isSelected = selectedTheme == key
                        Surface(
                            onClick = {
                                selectedTheme = key
                                saveAndBroadcast()
                            },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                                if (isSelected) {
                                    Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. Customization Section: Accent Color
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "لون تمييز الصلاة القادمة والعد التنازلي:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val colorOptions = listOf(
                        "orange" to ("برتقالي (كما في الصورة)" to Color(0xFFFFA040)),
                        "gold" to ("ذهبي ملكي" to Color(0xFFE5A93C)),
                        "emerald" to ("زمردي هادئ" to Color(0xFF10B981)),
                        "teal" to ("سماوي فيروزي" to Color(0xFF14B8A6))
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        colorOptions.forEach { (key, pair) ->
                            val (label, col) = pair
                            val isSelected = selectedAccent == key
                            Surface(
                                onClick = {
                                    selectedAccent = key
                                    saveAndBroadcast()
                                },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) col else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = label.split(" ")[0],
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Save Button at bottom
        item {
            FilledTonalButton(
                onClick = { saveAndBroadcast() },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("حفظ وتطبيق التعديلات على الويدجت فوراً", fontWeight = FontWeight.Bold)
            }
        }
    }

    // Manual Instructions Dialog if PIN API is not direct
    if (showPinInstructionsDialog) {
        AlertDialog(
            onDismissRequest = { showPinInstructionsDialog = false },
            title = {
                Text(
                    text = "طريقة إضافة الويدجت يدوياً إلى شاشة الهاتف",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("1. اخرج إلى الشاشة الرئيسية لهاتفك (Home Screen).", fontSize = 13.sp)
                    Text("2. اضغط مطولاً على أي مساحة فارغة في الشاشة الرئيسية.", fontSize = 13.sp)
                    Text("3. اختر \"التطبيقات المصغرة\" أو \"Widgets\".", fontSize = 13.sp)
                    Text("4. ابحث عن تطبيق \"صلاتي\" واختر ويدجت مواقيت الصلاة.", fontSize = 13.sp)
                    Text("5. اسحب الويدجت وضعه في المكان المناسب على شاشتك.", fontSize = 13.sp)
                }
            },
            confirmButton = {
                TextButton(onClick = { showPinInstructionsDialog = false }) {
                    Text("حسناً فهمت", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}
