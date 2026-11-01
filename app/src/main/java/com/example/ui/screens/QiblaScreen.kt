package com.example.ui.screens

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.CityLocation
import com.example.data.services.QiblaCalculator
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldAccentLight
import kotlin.math.*

@Composable
fun QiblaScreen(
    currentAzimuth: Float,
    isSensorAvailable: Boolean,
    selectedCity: CityLocation,
    onOpenCitySelector: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val vibrator = remember { context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator }
    var showOfflineGuideDialog by remember { mutableStateOf(false) }

    // Calculate Qibla bearing and distance for the selected city (100% offline geodesic formula)
    val qiblaBearing = remember(selectedCity) {
        QiblaCalculator.calculateQiblaBearing(selectedCity.latitude, selectedCity.longitude).toFloat()
    }

    val distanceKm = remember(selectedCity) {
        QiblaCalculator.calculateDistanceToKaabaKm(selectedCity.latitude, selectedCity.longitude).toInt()
    }

    // Relative angle between current phone heading and Qibla direction
    val relativeAngle = (qiblaBearing - currentAzimuth + 360f) % 360f
    val isAligned = abs(relativeAngle) <= 3f || abs(relativeAngle - 360f) <= 3f

    // Vibrate when reaching alignment
    var hadAligned by remember { mutableStateOf(false) }
    LaunchedEffect(isAligned) {
        if (isAligned && !hadAligned) {
            hadAligned = true
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    vibrator?.vibrate(100)
                }
            } catch (e: Exception) {
                // ignore
            }
        } else if (!isAligned) {
            hadAligned = false
        }
    }

    val glowColor by animateColorAsState(
        targetValue = if (isAligned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
        label = "qibla_glow"
    )

    Column(
        modifier = modifier
            .testTag("qibla_screen")
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Header with Offline Badge
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 6.dp)
        ) {
            // Prominent 100% Offline Badge
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = EmeraldLight.copy(alpha = 0.15f),
                border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldLight.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(Icons.Filled.Explore, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(14.dp))
                    Text(
                        text = "بوصلة القبلة تعمل أوفلاين 100% بدون إنترنت",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldLight
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "اتجاه القبلة المشرفة",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            // City Button with dropdown icon
            Surface(
                onClick = onOpenCitySelector,
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                border = androidx.compose.foundation.BorderStroke(0.8.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Filled.LocationOn, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                    Text(
                        text = "${selectedCity.nameAr} (${selectedCity.countryAr}) • زاوية القبلة: ${qiblaBearing.toInt()}°",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Icon(Icons.Filled.ArrowDropDown, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Text(
                text = "المسافة المباشرة إلى الكعبة المشرفة: $distanceKm كم",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Live Compass Dial Canvas
        Box(
            modifier = Modifier
                .size(280.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            // Compass Outer Dial rotating opposite to device azimuth
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .rotate(-currentAzimuth)
            ) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val radius = size.minDimension / 2f - 10.dp.toPx()

                // Draw outer ring
                drawCircle(
                    color = glowColor,
                    radius = radius,
                    style = Stroke(width = 3.dp.toPx())
                )

                // Draw degree tick marks
                for (angle in 0 until 360 step 30) {
                    val angleRad = Math.toRadians(angle.toDouble())
                    val isCardinal = angle % 90 == 0
                    val tickLen = if (isCardinal) 14.dp.toPx() else 8.dp.toPx()
                    val startX = center.x + (radius - tickLen) * sin(angleRad).toFloat()
                    val startY = center.y - (radius - tickLen) * cos(angleRad).toFloat()
                    val endX = center.x + radius * sin(angleRad).toFloat()
                    val endY = center.y - radius * cos(angleRad).toFloat()

                    drawLine(
                        color = if (isCardinal) Color(0xFFE5A93C) else Color(0x66FFFFFF),
                        start = Offset(startX, startY),
                        end = Offset(endX, endY),
                        strokeWidth = if (isCardinal) 3.dp.toPx() else 1.5.dp.toPx()
                    )
                }
            }

            // Fixed Center Needle Pointer
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .rotate(relativeAngle)
            ) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val needleLen = size.minDimension / 2f - 24.dp.toPx()

                // Top Qibla needle (Gold/Emerald)
                val topPath = Path().apply {
                    moveTo(center.x, center.y - needleLen)
                    lineTo(center.x - 12.dp.toPx(), center.y)
                    lineTo(center.x + 12.dp.toPx(), center.y)
                    close()
                }
                drawPath(
                    path = topPath,
                    color = if (isAligned) Color(0xFF10B981) else Color(0xFFE5A93C)
                )

                // Bottom opposite needle (Gray)
                val bottomPath = Path().apply {
                    moveTo(center.x, center.y + needleLen * 0.7f)
                    lineTo(center.x - 10.dp.toPx(), center.y)
                    lineTo(center.x + 10.dp.toPx(), center.y)
                    close()
                }
                drawPath(
                    path = bottomPath,
                    color = Color(0x44FFFFFF)
                )

                // Center pivot circle
                drawCircle(
                    color = if (isAligned) Color(0xFF10B981) else Color(0xFFE5A93C),
                    radius = 8.dp.toPx(),
                    center = center
                )
            }

            // Center Kaaba Icon Badge
            Surface(
                shape = CircleShape,
                modifier = Modifier.size(46.dp),
                color = if (isAligned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                shadowElevation = 6.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.Place,
                        contentDescription = "Kaaba",
                        tint = if (isAligned) MaterialTheme.colorScheme.onPrimary else GoldAccent,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // Direction & Alignment Status Banner
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isAligned) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                else MaterialTheme.colorScheme.surface
            ),
            border = if (isAligned) androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = if (isAligned) Icons.Filled.CheckCircle else Icons.Filled.Navigation,
                        contentDescription = null,
                        tint = if (isAligned) MaterialTheme.colorScheme.primary else GoldAccent,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = if (isAligned) "أنت تواجه الكعبة المشرفة بدقة!" else "وجّه هاتفك حتى يتطابق السهم الذهبي مع الكعبة",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isAligned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "الاتجاه الحالي للهاتف: ${currentAzimuth.toInt()}° • زاوية القبلة: ${qiblaBearing.toInt()}° • الفارق: ${relativeAngle.toInt()}°",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Offline Guide & Sensor Calibration Helper Card
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 60.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.OfflinePin,
                        contentDescription = "Offline Info",
                        tint = EmeraldLight,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "حساب زاوية القبلة يتم فلكياً بدقة متناهية أوفلاين، وبدون استهلاك للإنترنت أو البيانات.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 15.sp
                    )
                }

                TextButton(onClick = { showOfflineGuideDialog = true }) {
                    Text("إرشادات", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // Modal: إرشادات تحديد القبلة بدون نت ومعايرة البوصلة
    if (showOfflineGuideDialog) {
        AlertDialog(
            onDismissRequest = { showOfflineGuideDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Filled.Explore, contentDescription = null, tint = EmeraldLight)
                    Text(text = "إرشادات القبلة أوفلاين بدون نت", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "💡 يعمل هذا التطبيق أوفلاين بالكامل بحساب المثلثات الكروية الرياضية بدقة (Geodesic Bearings) من إحداثيات مدينتك إلى خط طول وعرض الكعبة المشرفة بمكة المكرمة.",
                            fontSize = 11.sp,
                            lineHeight = 16.sp,
                            modifier = Modifier.padding(10.dp)
                        )
                    }

                    Text(
                        text = "1️⃣ معايرة حساس البوصلة في هاتفك:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "حرّك الهاتف في الهواء على شكل الرقم (8) الإنجليزي عدة مرات لضبط مستشعر المغناطيسية بعيداً عن الأجهزة الإلكترونية أو القطع المعدنية الكبيرة.",
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = "2️⃣ تحديد القبلة بظل الشمس (بديل فيزيائي بدون نت):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldAccent
                    )
                    Text(
                        text = "في البلاد الواقعة شمال مكة يكون اتجاه القبلة جنوبياً، وفي البلاد الواقعة غربها يكون شرقياً. وتتعامد الشمس تماماً فوق الكعبة مرتين سنوياً (28 مايو و 16 يوليو وقت أذان الظهر بمكة)، فيكون ظل أي شاخص في العالم عكس اتجاه القبلة تماماً.",
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(onClick = { showOfflineGuideDialog = false }) {
                    Text("حسناً، فهمت")
                }
            }
        )
    }
}
