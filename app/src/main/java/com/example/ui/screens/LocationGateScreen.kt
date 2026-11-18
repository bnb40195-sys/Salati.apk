package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.EmeraldBorderDark
import com.example.ui.theme.EmeraldCardDark
import com.example.ui.theme.EmeraldDeepDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldSurfaceDark
import com.example.ui.theme.GoldAccent

@Composable
fun LocationGateScreen(
    hasLocationPermission: Boolean,
    isLocationEnabled: Boolean,
    hasAttemptedPermission: Boolean,
    onRequestPermission: () -> Unit,
    onOpenDeviceLocationSettings: () -> Unit,
    onOpenAppPermissionSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(EmeraldDeepDark, EmeraldSurfaceDark, EmeraldDeepDark)
                    )
                )
                .statusBarsPadding()
                .navigationBarsPadding()
                .testTag("location_gate_screen"),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 520.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 22.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // Holy Kaaba Visual Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(155.dp)
                        .clip(RoundedCornerShape(22.dp))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_kaaba_banner),
                        contentDescription = "الكعبة الشريفة",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Transparent, EmeraldDeepDark.copy(alpha = 0.85f))
                                )
                            )
                    )
                    Surface(
                        shape = CircleShape,
                        color = EmeraldCardDark.copy(alpha = 0.92f),
                        border = BorderStroke(1.5.dp, GoldAccent),
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 12.dp)
                            .size(56.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.MyLocation,
                                contentDescription = "الموقع الجغرافي الحقيقي",
                                tint = GoldAccent,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "تفعيل الموقع الحقيقي إجباري",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = GoldAccent,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "يعتمد تطبيق صلاتي على موقعك الجغرافي الحقيقي عبر GPS لحساب مواقيت الصلاة بدقة وتحديد اتجاه القبلة وحالة الطقس.",
                        fontSize = 13.sp,
                        color = Color(0xFFC4DDD2),
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = EmeraldCardDark),
                    border = BorderStroke(1.dp, EmeraldBorderDark)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        LocationStepRow(
                            title = "1. إذن الوصول للموقع الجغرافي",
                            subtitle = if (hasLocationPermission) "تم منح الإذن بنجاح" else "مطلوب السماح بالوصول إلى الموقع الدقيق",
                            isCompleted = hasLocationPermission
                        )

                        LocationStepRow(
                            title = "2. تشغيل خدمة الموقع (GPS) بالهاتف",
                            subtitle = if (isLocationEnabled) "خدمة الموقع الحقيقي مفعلة" else "يرجى تشغيل الـ GPS من إعدادات الهاتف",
                            isCompleted = isLocationEnabled
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        if (!hasLocationPermission) {
                            Button(
                                onClick = onRequestPermission,
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = EmeraldLight,
                                    contentColor = Color.Black
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("location_grant_permission_btn")
                            ) {
                                Icon(Icons.Filled.LocationOn, contentDescription = null, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "منح إذن الموقع الآن",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            if (hasAttemptedPermission) {
                                OutlinedButton(
                                    onClick = onOpenAppPermissionSettings,
                                    shape = RoundedCornerShape(14.dp),
                                    border = BorderStroke(1.dp, GoldAccent),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(46.dp)
                                        .testTag("location_app_settings_btn")
                                ) {
                                    Icon(Icons.Filled.Settings, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "فتح أذونات التطبيق في الإعدادات",
                                        color = GoldAccent,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        } else if (!isLocationEnabled) {
                            Button(
                                onClick = onOpenDeviceLocationSettings,
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GoldAccent,
                                    contentColor = Color.Black
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("location_enable_gps_btn")
                            ) {
                                Icon(Icons.Filled.GpsFixed, contentDescription = null, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "تشغيل خدمة الموقع (GPS)",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LocationStepRow(
    title: String,
    subtitle: String,
    isCompleted: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(EmeraldDeepDark.copy(alpha = 0.7f))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = if (isCompleted) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (isCompleted) EmeraldLight else GoldAccent,
            modifier = Modifier.size(22.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = if (isCompleted) EmeraldLight else Color(0xFFA7C7BA)
            )
        }
    }
}
