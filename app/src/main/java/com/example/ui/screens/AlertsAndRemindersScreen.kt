package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.GoldAccent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Data model for custom alarms in the 3 categories
data class CustomAlarmItem(
    val id: String,
    val title: String,
    val prayerRef: String = "الفجر",
    val minutesBefore: Int = 60,
    val selectedMonths: Set<String> = setOf("جميع الأشهر"),
    val selectedDays: Set<Int> = setOf(13, 14, 15),
    val isEnabled: Boolean = true,
    val volume: Float = 0.85f,
    val soundName: String = "تكبير",
    val themeColor: Color = Color(0xFF10B981), // Green or Cyan
    val category: String = "suhur_fasting" // "suhur_fasting", "fasting", "suhur"
)

val HIJRI_MONTHS_NAMES = listOf(
    "جميع الأشهر",
    "محرم",
    "صفر",
    "ربيع الأول",
    "ربيع الثاني",
    "جمادى الأولى",
    "جمادى الآخرة",
    "رجب",
    "شعبان",
    "رمضان",
    "شوال",
    "ذو القعدة",
    "ذو الحجة"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsAndRemindersScreen(
    onBack: () -> Unit,
    onOpenDrawer: () -> Unit = {},
    postPrayerDelayMinutes: Int = 5,
    onUpdatePostPrayerDelay: (Int) -> Unit = {},
    onSendCenterNotification: () -> Unit = {},
    onPlayTestSound: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val darkSlateBg = Brush.verticalGradient(
        colors = listOf(Color(0xFF1F2428), Color(0xFF14171A), Color(0xFF0F1113))
    )

    // Current time
    val currentTime = remember { SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()) }

    // Active sub-tab or view: 0: منبهات الصيام والسحور (صور 1، 2، 3، 4، 5), 1: تذكير الصلوات الخمس, 2: المناسبات والأعياد
    var selectedTab by remember { mutableStateOf(0) }

    // Dialog states
    var showBigAthkarDialog by remember { mutableStateOf(false) }
    var editingAlarm by remember { mutableStateOf<CustomAlarmItem?>(null) }
    var showDayPicker by remember { mutableStateOf(false) }
    var showMonthPicker by remember { mutableStateOf(false) }

    // Custom alarms in the 3 sections from Screenshot 1001532318-picsay_dd93da21.jpg
    var alarmsList by remember {
        mutableStateOf(
            listOf(
                // الصيام / السحور
                CustomAlarmItem("sf_1", "الأيام البيض", "العشاء", 60, setOf("جميع الأشهر"), setOf(13, 14, 15), true, 0.85f, "تكبير", Color(0xFF38BDF8), "suhur_fasting"),
                CustomAlarmItem("sf_2", "عرفة والمواسم", "الفجر", 60, setOf("ذو الحجة"), setOf(9), true, 0.9f, "تكبير", Color(0xFF38BDF8), "suhur_fasting"),
                CustomAlarmItem("sf_3", "عاشوراء وتاسوعاء", "الفجر", 60, setOf("محرم"), setOf(9, 10), false, 0.8f, "تكبير", Color(0xFF38BDF8), "suhur_fasting"),
                CustomAlarmItem("sf_4", "الست من شوال", "الفجر", 45, setOf("شوال"), setOf(2, 3, 4, 5, 6, 7), false, 0.8f, "تكبير", Color(0xFF38BDF8), "suhur_fasting"),
                CustomAlarmItem("sf_5", "سحور الأيام البيض", "العشاء", 60, setOf("جميع الأشهر"), setOf(13, 14, 15), true, 0.85f, "تكبير", Color(0xFF10B981), "suhur_fasting"),
                CustomAlarmItem("sf_6", "سحور ليلة عرفة", "الفجر", 60, setOf("ذو الحجة"), setOf(9), true, 0.9f, "تكبير", Color(0xFF10B981), "suhur_fasting"),
                CustomAlarmItem("sf_7", "سحور عاشوراء", "الفجر", 60, setOf("محرم"), setOf(9, 10), false, 0.8f, "تكبير", Color(0xFF10B981), "suhur_fasting"),
                CustomAlarmItem("sf_8", "سحور الإثنين والخميس", "الفجر", 60, setOf("جميع الأشهر"), setOf(1, 2, 3, 4, 5, 6, 7), false, 0.8f, "تكبير", Color(0xFF10B981), "suhur_fasting"),

                // الصيام
                CustomAlarmItem("f_1", "صيام الإثنين", "الفجر", 0, setOf("جميع الأشهر"), setOf(2), true, 0.85f, "تكبير", Color(0xFF38BDF8), "fasting"),
                CustomAlarmItem("f_2", "صيام الخميس", "الفجر", 0, setOf("جميع الأشهر"), setOf(5), true, 0.85f, "تكبير", Color(0xFF38BDF8), "fasting"),
                CustomAlarmItem("f_3", "صيام 13 هجري", "الفجر", 0, setOf("جميع الأشهر"), setOf(13), true, 0.85f, "تكبير", Color(0xFF38BDF8), "fasting"),
                CustomAlarmItem("f_4", "صيام 14 هجري", "الفجر", 0, setOf("جميع الأشهر"), setOf(14), true, 0.85f, "تكبير", Color(0xFF38BDF8), "fasting"),
                CustomAlarmItem("f_5", "صيام 15 هجري", "الفجر", 0, setOf("جميع الأشهر"), setOf(15), true, 0.85f, "تكبير", Color(0xFF10B981), "fasting"),
                CustomAlarmItem("f_6", "صيام يوم عرفة", "الفجر", 0, setOf("ذو الحجة"), setOf(9), true, 0.9f, "تكبير", Color(0xFF10B981), "fasting"),
                CustomAlarmItem("f_7", "صيام يوم عاشوراء", "الفجر", 0, setOf("محرم"), setOf(10), true, 0.85f, "تكبير", Color(0xFF10B981), "fasting"),
                CustomAlarmItem("f_8", "صيام يوم تاسوعاء", "الفجر", 0, setOf("محرم"), setOf(9), true, 0.85f, "تكبير", Color(0xFF10B981), "fasting"),

                // السحور
                CustomAlarmItem("s_1", "سحور الفجر - 60د", "الفجر", 60, setOf("جميع الأشهر"), setOf(13, 14, 15), true, 0.85f, "تكبير", Color(0xFF38BDF8), "suhur"),
                CustomAlarmItem("s_2", "سحور الفجر - 45د", "الفجر", 45, setOf("رمضان"), setOf(1, 2, 3), true, 0.85f, "تكبير", Color(0xFF38BDF8), "suhur"),
                CustomAlarmItem("s_3", "سحور الفجر - 30د", "الفجر", 30, setOf("رمضان"), setOf(1, 2, 3), false, 0.8f, "تكبير", Color(0xFF38BDF8), "suhur"),
                CustomAlarmItem("s_4", "سحور العشاء - 60د", "العشاء", 60, setOf("جميع الأشهر"), setOf(13, 14, 15), true, 0.85f, "تكبير", Color(0xFF38BDF8), "suhur"),
                CustomAlarmItem("s_5", "سحور عرفة", "الفجر", 60, setOf("ذو الحجة"), setOf(9), true, 0.9f, "تكبير", Color(0xFF10B981), "suhur"),
                CustomAlarmItem("s_6", "سحور عاشوراء", "الفجر", 60, setOf("محرم"), setOf(9, 10), false, 0.85f, "تكبير", Color(0xFF10B981), "suhur"),
                CustomAlarmItem("s_7", "سحور الست من شوال", "الفجر", 60, setOf("شوال"), setOf(2, 3, 4, 5, 6, 7), false, 0.8f, "تكبير", Color(0xFF10B981), "suhur"),
                CustomAlarmItem("s_8", "سحور عام مخصص", "الفجر", 60, setOf("جميع الأشهر"), setOf(13, 14, 15), false, 0.8f, "تكبير", Color(0xFF10B981), "suhur")
            )
        )
    }

    // 5 Prayers alert states (الفجر، الظهر، العصر، المغرب، العشاء)
    var prayerAlerts by remember {
        mutableStateOf(
            mapOf(
                "الفجر" to PrayerAlertItemState("الفجر", duaEnabled = true, duaMinutes = 5, adhanEnabled = true, iqamaEnabled = false, iqamaMinutes = 5, athkarEnabled = true, athkarMinutes = postPrayerDelayMinutes),
                "الظهر" to PrayerAlertItemState("الظهر", duaEnabled = false, duaMinutes = 5, adhanEnabled = true, iqamaEnabled = false, iqamaMinutes = 5, athkarEnabled = true, athkarMinutes = postPrayerDelayMinutes),
                "العصر" to PrayerAlertItemState("العصر", duaEnabled = false, duaMinutes = 5, adhanEnabled = true, iqamaEnabled = false, iqamaMinutes = 5, athkarEnabled = true, athkarMinutes = postPrayerDelayMinutes),
                "المغرب" to PrayerAlertItemState("المغرب", duaEnabled = false, duaMinutes = 5, adhanEnabled = true, iqamaEnabled = false, iqamaMinutes = 5, athkarEnabled = true, athkarMinutes = postPrayerDelayMinutes),
                "العشاء" to PrayerAlertItemState("العشاء", duaEnabled = false, duaMinutes = 5, adhanEnabled = true, iqamaEnabled = false, iqamaMinutes = 5, athkarEnabled = true, athkarMinutes = postPrayerDelayMinutes)
            )
        )
    }

    // Active prayer detail dialog
    var activePrayerDetail by remember { mutableStateOf<Pair<String, AlertDetailType>?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(darkSlateBg)
            .testTag("alerts_and_reminders_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Top Bar with metallic styling
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "رجوع", tint = Color.White)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "قسم التنبيه والتذكير",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "منبهات الصيام، السحور، الصلوات والمناسبات المباركة",
                        fontSize = 11.sp,
                        color = GoldAccent
                    )
                }

                IconButton(onClick = onOpenDrawer) {
                    Icon(Icons.Filled.Menu, contentDescription = "القائمة", tint = Color.White)
                }
            }

            // Quick Callout Card for Post-Prayer Athkar ("رسالة كبيرة كاملة بدون صوت")
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 4.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                border = BorderStroke(1.2.dp, GoldAccent.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Filled.VolumeOff, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(16.dp))
                            Text(
                                text = "أذكار ما بعد الصلاة (رسالة كبيرة كاملة - بدون صوت)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldAccent
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "تنبيه مرئي صامت مع النص الكامل للأذكار، المعوذات، آية الكرسي والعداد",
                            fontSize = 10.sp,
                            color = Color(0xFFCBD5E1)
                        )
                    }

                    Button(
                        onClick = { showBigAthkarDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Color.Black),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Filled.Visibility, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("عرض الرسالة", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Tabs Selector matching the app flow
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                edgePadding = 12.dp,
                containerColor = Color(0xFF111827),
                contentColor = GoldAccent,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("منبهات الصيام والسحور", fontSize = 12.sp, fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) },
                    icon = { Icon(Icons.Filled.NightsStay, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("تذكير الصلوات الخمس", fontSize = 12.sp, fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) },
                    icon = { Icon(Icons.Filled.AccessTime, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("المناسبات والأعياد", fontSize = 12.sp, fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal) },
                    icon = { Icon(Icons.Filled.Festival, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
            }

            // CONTENT BODY
            when (selectedTab) {
                0 -> {
                    // TAB 0: Exact match to Screenshot 1001532318-picsay_dd93da21.jpg
                    // 3 Sections: الصيام / السحور, الصيام, السحور with blue and green alarm buttons
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 14.dp),
                        contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Section 1: الصيام / السحور
                        item {
                            AlarmSectionCard(
                                title = "الصيام / السحور",
                                alarms = alarmsList.filter { it.category == "suhur_fasting" },
                                onAlarmClick = { alarm -> editingAlarm = alarm },
                                onAddAlarm = {
                                    val newA = CustomAlarmItem(
                                        id = "sf_${System.currentTimeMillis()}",
                                        title = "إضافة منبه",
                                        prayerRef = "الفجر",
                                        minutesBefore = 60,
                                        selectedMonths = setOf("جميع الأشهر"),
                                        selectedDays = setOf(13, 14, 15),
                                        isEnabled = true,
                                        themeColor = Color(0xFF38BDF8),
                                        category = "suhur_fasting"
                                    )
                                    alarmsList = alarmsList + newA
                                    editingAlarm = newA
                                }
                            )
                        }

                        // Section 2: الصيام
                        item {
                            AlarmSectionCard(
                                title = "الصيام",
                                alarms = alarmsList.filter { it.category == "fasting" },
                                onAlarmClick = { alarm -> editingAlarm = alarm },
                                onAddAlarm = {
                                    val newA = CustomAlarmItem(
                                        id = "f_${System.currentTimeMillis()}",
                                        title = "إضافة منبه",
                                        prayerRef = "الفجر",
                                        minutesBefore = 0,
                                        selectedMonths = setOf("جميع الأشهر"),
                                        selectedDays = setOf(13, 14, 15),
                                        isEnabled = true,
                                        themeColor = Color(0xFF10B981),
                                        category = "fasting"
                                    )
                                    alarmsList = alarmsList + newA
                                    editingAlarm = newA
                                }
                            )
                        }

                        // Section 3: السحور
                        item {
                            AlarmSectionCard(
                                title = "السحور",
                                alarms = alarmsList.filter { it.category == "suhur" },
                                onAlarmClick = { alarm -> editingAlarm = alarm },
                                onAddAlarm = {
                                    val newA = CustomAlarmItem(
                                        id = "s_${System.currentTimeMillis()}",
                                        title = "إضافة منبه",
                                        prayerRef = "الفجر",
                                        minutesBefore = 60,
                                        selectedMonths = setOf("ذو الحجة"),
                                        selectedDays = setOf(9),
                                        isEnabled = true,
                                        themeColor = Color(0xFF38BDF8),
                                        category = "suhur"
                                    )
                                    alarmsList = alarmsList + newA
                                    editingAlarm = newA
                                }
                            )
                        }

                        // Bottom Actions matching screenshot: القائمة الرئيسية, إغلاق, مسح
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(onClick = {
                                    alarmsList = alarmsList.map { it.copy(isEnabled = false) }
                                }) {
                                    Text("مسح الكل", color = Color(0xFF94A3B8), fontSize = 13.sp)
                                }

                                TextButton(onClick = onBack) {
                                    Text("إغلاق", color = Color(0xFF94A3B8), fontSize = 13.sp)
                                }

                                TextButton(onClick = onBack) {
                                    Text("القائمة الرئيسية", color = Color(0xFF38BDF8), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // TAB 1: 5 Prayers Alarm rows matching Images with PrayerReminderRowCard
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 14.dp),
                        contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            Text(
                                text = "تنبيهات وتذكيرات الصلوات الخمس",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldAccent,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }

                        items(listOf("الفجر", "الظهر", "العصر", "المغرب", "العشاء")) { prayerName ->
                            val pState = prayerAlerts[prayerName] ?: PrayerAlertItemState(prayerName)

                            PrayerReminderRowCard(
                                prayerName = prayerName,
                                duaText = if (pState.duaEnabled) "الدعاء ${pState.duaMinutes} دقيقة قبل" else "الدعاء قبل الأذان\n[موقوف]",
                                isDuaActive = pState.duaEnabled,
                                onDuaClick = { activePrayerDetail = prayerName to AlertDetailType.DUA_BEFORE },
                                adhanText = if (pState.adhanEnabled) "الأذان\n[مفعل]" else "الأذان\n[موقوف]",
                                isAdhanActive = pState.adhanEnabled,
                                onAdhanClick = { activePrayerDetail = prayerName to AlertDetailType.ADHAN },
                                iqamaText = if (pState.iqamaEnabled) "الإقامة ${pState.iqamaMinutes} دقيقة بعد" else "الإقامة بعد الأذان\n[موقوف]",
                                isIqamaActive = pState.iqamaEnabled,
                                onIqamaClick = { activePrayerDetail = prayerName to AlertDetailType.IQAMA_AFTER },
                                athkarText = if (pState.athkarEnabled) "تذكير بأذكار بعد الصلاة\n[بعد ${pState.athkarMinutes} دقيقة]" else "تذكير بأذكار بعد الصلاة\n[موقوف]",
                                isAthkarActive = pState.athkarEnabled,
                                onAthkarClick = { activePrayerDetail = prayerName to AlertDetailType.ATHKAR_AFTER }
                            )
                        }
                    }
                }

                2 -> {
                    // TAB 2: Special Islamic Fasting & Eid Reminders (الأيام البيض، عيد الفطر، عيد الأضحى، عرفة، عاشوراء)
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 14.dp),
                        contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Text(
                                text = "تذكيرات المناسبات الإسلامية والأعياد وصيام التطوع",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldAccent,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }

                        val specialReminders = listOf(
                            SpecialReminderDef(
                                title = "صيام الأيام البيض (13 و 14 و 15)",
                                subtitle = "من كل شهر هجري • وصية النبي ﷺ لأبي هريرة",
                                dateDesc = "13، 14، 15 من كل شهر هجري",
                                isEnabled = true,
                                icon = Icons.Filled.BrightnessMedium,
                                badge = "شهرياً"
                            ),
                            SpecialReminderDef(
                                title = "عيد الفطر المبارك",
                                subtitle = "1 شوال • تذكير صلاة العيد مع تكبيرات العيد",
                                dateDesc = "1 شوال من كل عام",
                                isEnabled = true,
                                icon = Icons.Filled.Celebration,
                                badge = "سنوياً"
                            ),
                            SpecialReminderDef(
                                title = "عيد الأضحى المبارك",
                                subtitle = "10 ذو الحجة • تكبيرات العيد وصلاة العيد والأضحية",
                                dateDesc = "10 ذو الحجة من كل عام",
                                isEnabled = true,
                                icon = Icons.Filled.Festival,
                                badge = "سنوياً"
                            ),
                            SpecialReminderDef(
                                title = "صيام يوم عرفة العظيم",
                                subtitle = "9 ذو الحجة • يكفر السنة الماضية والباقية",
                                dateDesc = "9 ذو الحجة",
                                isEnabled = true,
                                icon = Icons.Filled.WorkspacePremium,
                                badge = "فضل عظيم"
                            ),
                            SpecialReminderDef(
                                title = "صيام عاشوراء وتاسوعاء",
                                subtitle = "9 و 10 محرم • يكفر السنة الماضية",
                                dateDesc = "9 و 10 محرم",
                                isEnabled = true,
                                icon = Icons.Filled.Stars,
                                badge = "سنة نبوية"
                            ),
                            SpecialReminderDef(
                                title = "صيام الإثنين والخميس",
                                subtitle = "تعرض الأعمال على الله تعالى يومي الإثنين والخميس",
                                dateDesc = "أسبوعياً كل إثنين وخميس",
                                isEnabled = false,
                                icon = Icons.Filled.CalendarToday,
                                badge = "أسبوعياً"
                            ),
                            SpecialReminderDef(
                                title = "صيام الست من شوال",
                                subtitle = "من صام رمضان ثم أتبعه ستاً من شوال كان كصيام الدهر",
                                dateDesc = "شهر شوال",
                                isEnabled = false,
                                icon = Icons.Filled.EventRepeat,
                                badge = "أجر الدهر"
                            )
                        )

                        items(specialReminders) { item ->
                            var active by remember { mutableStateOf(item.isEnabled) }
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                                border = BorderStroke(1.dp, if (active) GoldAccent.copy(alpha = 0.5f) else Color(0xFF334155))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Switch(
                                        checked = active,
                                        onCheckedChange = { active = it }
                                    )

                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(horizontal = 12.dp),
                                        horizontalAlignment = Alignment.End
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = GoldAccent.copy(alpha = 0.15f)
                                            ) {
                                                Text(
                                                    text = item.badge,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = GoldAccent,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                            Text(
                                                text = item.title,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = item.subtitle,
                                            fontSize = 11.sp,
                                            color = Color(0xFF94A3B8),
                                            textAlign = TextAlign.End
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "الموعد: ${item.dateDesc}",
                                            fontSize = 10.sp,
                                            color = Color(0xFF38BDF8),
                                            textAlign = TextAlign.End
                                        )
                                    }

                                    Icon(item.icon, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(24.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        // 1. DIALOG: Detail Editor matching Screenshots 1 & 2 (إضافة منبه - السحور / الصيام)
        if (editingAlarm != null) {
            val alarm = editingAlarm!!
            AlarmDetailEditorDialog(
                alarm = alarm,
                currentTime = currentTime,
                onDismiss = { editingAlarm = null },
                onSave = { updatedAlarm ->
                    alarmsList = alarmsList.map { if (it.id == updatedAlarm.id) updatedAlarm else it }
                    editingAlarm = null
                },
                onOpenDayPicker = { showDayPicker = true },
                onOpenMonthPicker = { showMonthPicker = true },
                onPlaySound = { onPlayTestSound("takbeer") }
            )
        }

        // 2. DIALOG: Day Picker Modal matching Screenshot 3 (1..30 Checkboxes)
        if (showDayPicker && editingAlarm != null) {
            DayPickerModalDialog(
                selectedDays = editingAlarm!!.selectedDays,
                onDismiss = { showDayPicker = false },
                onConfirm = { newDays ->
                    editingAlarm = editingAlarm!!.copy(selectedDays = newDays)
                    showDayPicker = false
                }
            )
        }

        // 3. DIALOG: Month Picker Modal matching Screenshot 4 (12 Hijri months checkboxes)
        if (showMonthPicker && editingAlarm != null) {
            MonthPickerModalDialog(
                selectedMonths = editingAlarm!!.selectedMonths,
                onDismiss = { showMonthPicker = false },
                onConfirm = { newMonths ->
                    editingAlarm = editingAlarm!!.copy(selectedMonths = newMonths)
                    showMonthPicker = false
                }
            )
        }

        // 4. DIALOG: Prayer alert detail dialog (Dua, Adhan, Iqama, Athkar)
        if (activePrayerDetail != null) {
            val (targetName, alertType) = activePrayerDetail!!
            val currentState = prayerAlerts[targetName] ?: PrayerAlertItemState(targetName)

            when (alertType) {
                AlertDetailType.DUA_BEFORE -> {
                    DuaBeforeDetailDialog(
                        prayerName = targetName,
                        minutesBefore = currentState.duaMinutes,
                        isEnabled = currentState.duaEnabled,
                        selectedDays = currentState.selectedDays,
                        volume = currentState.volume,
                        soundName = currentState.soundName,
                        onDismiss = { activePrayerDetail = null },
                        onSave = { mins, enabled, days, vol, snd ->
                            prayerAlerts = prayerAlerts + (targetName to currentState.copy(
                                duaMinutes = mins,
                                duaEnabled = enabled,
                                selectedDays = days,
                                volume = vol,
                                soundName = snd
                            ))
                            activePrayerDetail = null
                        },
                        onPlaySound = { onPlayTestSound("dua") }
                    )
                }

                AlertDetailType.ADHAN -> {
                    AdhanDetailDialog(
                        prayerName = targetName,
                        isEnabled = currentState.adhanEnabled,
                        selectedDays = currentState.selectedDays,
                        volume = currentState.volume,
                        soundName = currentState.soundName,
                        onDismiss = { activePrayerDetail = null },
                        onSave = { enabled, days, vol, snd ->
                            prayerAlerts = prayerAlerts + (targetName to currentState.copy(
                                adhanEnabled = enabled,
                                selectedDays = days,
                                volume = vol,
                                soundName = snd
                            ))
                            activePrayerDetail = null
                        },
                        onPlaySound = { onPlayTestSound("adhan") }
                    )
                }

                AlertDetailType.IQAMA_AFTER -> {
                    IqamaDetailDialog(
                        prayerName = targetName,
                        minutesAfter = currentState.iqamaMinutes,
                        isEnabled = currentState.iqamaEnabled,
                        selectedDays = currentState.selectedDays,
                        volume = currentState.volume,
                        soundName = currentState.soundName,
                        onDismiss = { activePrayerDetail = null },
                        onSave = { mins, enabled, days, vol, snd ->
                            prayerAlerts = prayerAlerts + (targetName to currentState.copy(
                                iqamaMinutes = mins,
                                iqamaEnabled = enabled,
                                selectedDays = days,
                                volume = vol,
                                soundName = snd
                            ))
                            activePrayerDetail = null
                        },
                        onPlaySound = { onPlayTestSound("iqama") }
                    )
                }

                AlertDetailType.ATHKAR_AFTER -> {
                    AthkarAfterDetailDialog(
                        prayerName = targetName,
                        minutesAfter = currentState.athkarMinutes,
                        isEnabled = currentState.athkarEnabled,
                        selectedDays = currentState.selectedDays,
                        volume = currentState.volume,
                        soundName = currentState.soundName,
                        onDismiss = { activePrayerDetail = null },
                        onSave = { mins, enabled, days, vol, snd ->
                            onUpdatePostPrayerDelay(mins)
                            prayerAlerts = prayerAlerts + (targetName to currentState.copy(
                                athkarMinutes = mins,
                                athkarEnabled = enabled,
                                selectedDays = days,
                                volume = vol,
                                soundName = snd
                            ))
                            activePrayerDetail = null
                        },
                        onPlaySound = { onPlayTestSound("athkar") }
                    )
                }

                else -> {}
            }
        }

        // 5. DIALOG: Big Complete Post-Prayer Athkar Message Dialog (رسالة كبيرة في أذكار الصلاة كاملة بدون صوت)
        if (showBigAthkarDialog) {
            FullPostPrayerAthkarBigMessageDialog(
                onDismiss = { showBigAthkarDialog = false }
            )
        }
    }
}

private data class SpecialReminderDef(
    val title: String,
    val subtitle: String,
    val dateDesc: String,
    val isEnabled: Boolean,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val badge: String
)

// Card for Section 1, 2, 3 in Tab 0 matching Screenshot 1001532318-picsay_dd93da21.jpg
@Composable
private fun AlarmSectionCard(
    title: String,
    alarms: List<CustomAlarmItem>,
    onAlarmClick: (CustomAlarmItem) -> Unit,
    onAddAlarm: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Section title (right aligned)
        Text(
            text = title,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.End,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
        )

        // Split into two rows (Row 1: Light Blue, Row 2: Light Green)
        val blueAlarms = alarms.filter { it.themeColor == Color(0xFF38BDF8) }
        val greenAlarms = alarms.filter { it.themeColor == Color(0xFF10B981) }

        // Row 1 (Blue buttons)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val displayBlue = blueAlarms.take(4)
            for (i in 0 until 4) {
                val item = displayBlue.getOrNull(i)
                Surface(
                    onClick = {
                        if (item != null) onAlarmClick(item) else onAddAlarm()
                    },
                    shape = RoundedCornerShape(4.dp),
                    color = Color.Transparent,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = item?.title ?: "إضافة منبه",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF38BDF8),
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 2.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Row 2 (Green buttons)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val displayGreen = greenAlarms.take(4)
            for (i in 0 until 4) {
                val item = displayGreen.getOrNull(i)
                Surface(
                    onClick = {
                        if (item != null) onAlarmClick(item) else onAddAlarm()
                    },
                    shape = RoundedCornerShape(4.dp),
                    color = Color.Transparent,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = item?.title ?: "إضافة منبه",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF10B981),
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 2.dp)
                    )
                }
            }
        }

        Divider(
            color = Color(0xFF334155).copy(alpha = 0.5f),
            thickness = 0.8.dp,
            modifier = Modifier.padding(top = 10.dp)
        )
    }
}

// Dialog matching Screenshot 1 & 2: Alarm Detail Editor
@Composable
private fun AlarmDetailEditorDialog(
    alarm: CustomAlarmItem,
    currentTime: String,
    onDismiss: () -> Unit,
    onSave: (CustomAlarmItem) -> Unit,
    onOpenDayPicker: () -> Unit,
    onOpenMonthPicker: () -> Unit,
    onPlaySound: () -> Unit
) {
    var prayerRef by remember { mutableStateOf(alarm.prayerRef) }
    var minsBefore by remember { mutableStateOf(alarm.minutesBefore) }
    var isEnabled by remember { mutableStateOf(alarm.isEnabled) }
    var volume by remember { mutableStateOf(alarm.volume) }
    var soundName by remember { mutableStateOf(alarm.soundName) }
    var themeColor by remember { mutableStateOf(alarm.themeColor) }

    val formattedDays = remember(alarm.selectedDays) {
        if (alarm.selectedDays.size == 3 && alarm.selectedDays.containsAll(listOf(13, 14, 15))) {
            "15-13"
        } else if (alarm.selectedDays.size == 1) {
            "${alarm.selectedDays.first()}"
        } else if (alarm.selectedDays.size == 7) {
            "كل يوم"
        } else {
            alarm.selectedDays.sorted().joinToString("-")
        }
    }

    val formattedMonths = remember(alarm.selectedMonths) {
        if (alarm.selectedMonths.contains("جميع الأشهر") || alarm.selectedMonths.size >= 12) {
            "جميع الأشهر"
        } else {
            alarm.selectedMonths.joinToString("، ")
        }
    }

    val switchLabel = remember(formattedMonths, formattedDays, alarm.category, prayerRef, minsBefore) {
        val catName = if (alarm.category == "suhur") "السحور" else if (alarm.category == "fasting") "الصيام" else "السحور"
        "$formattedMonths $formattedDays\n$catName $prayerRef-$minsBefore"
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E242B))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Top Header matching Screenshot 1 & 2
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = currentTime,
                        fontSize = 17.sp,
                        color = Color(0xFF94A3B8)
                    )
                    Text(
                        text = "إضافة منبه - ${if (alarm.category == "fasting") "الصيام" else "السحور"}",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Colored Bar under Title (Green or Blue matching screenshot)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .background(themeColor)
                )

                // Prayer selector (الفجر / العشاء)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf("الفجر", "العشاء", "المغرب").forEach { p ->
                        val isSel = prayerRef == p
                        Surface(
                            onClick = { prayerRef = p },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSel) themeColor.copy(alpha = 0.2f) else Color.Transparent,
                            border = BorderStroke(1.dp, if (isSel) themeColor else Color(0xFF475569)),
                            modifier = Modifier.padding(horizontal = 4.dp)
                        ) {
                            Text(
                                text = "▼ $p",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) themeColor else Color(0xFF94A3B8),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Big Minutes Display (قبل 60 دقيقة)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "قبل  ", fontSize = 18.sp, color = Color(0xFFCBD5E1))
                    Text(
                        text = "$minsBefore دقيقة",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = themeColor
                    )
                }

                // Quick Minutes Chips
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(15, 30, 45, 60, 90).forEach { m ->
                        val isSel = minsBefore == m
                        Surface(
                            onClick = { minsBefore = m },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSel) themeColor else Color(0xFF334155),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "$m د",
                                fontSize = 11.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSel) Color.Black else Color.White,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 5.dp)
                            )
                        }
                    }
                }

                // Dropdown selectors row: "جميع الأشهر" and "15-13" (Clickable to open Day & Month Modals)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Day Selector Button
                    Surface(
                        onClick = onOpenDayPicker,
                        shape = RoundedCornerShape(6.dp),
                        color = Color.Transparent,
                        border = BorderStroke(1.dp, themeColor.copy(alpha = 0.6f)),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Text(
                            text = "▼ $formattedDays",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = themeColor,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Month Selector Button
                    Surface(
                        onClick = onOpenMonthPicker,
                        shape = RoundedCornerShape(6.dp),
                        color = Color.Transparent,
                        border = BorderStroke(1.dp, themeColor.copy(alpha = 0.6f)),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Text(
                            text = "▼ $formattedMonths",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = themeColor,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }

                // Switch row matching screenshot: Label on left/right + large Switch
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Switch(
                        checked = isEnabled,
                        onCheckedChange = { isEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.Black,
                            checkedTrackColor = themeColor
                        )
                    )

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = switchLabel,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.End,
                            lineHeight = 18.sp
                        )
                    }
                }

                // Note matching screenshot
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF141920).copy(alpha = 0.8f))
                ) {
                    Text(
                        text = "ملاحظة: ينطلق منبه السحور في ليلة (أي قبل) يوم الصيام الذي يتم اختياره هنا. يتوقف المنبه خلال العيد حتى لو كان فعالاً.",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8),
                        lineHeight = 15.sp,
                        textAlign = TextAlign.End,
                        modifier = Modifier.padding(10.dp)
                    )
                }

                // Volume slider with speaker icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Filled.VolumeUp, contentDescription = null, tint = themeColor)
                    Slider(
                        value = volume,
                        onValueChange = { volume = it },
                        colors = SliderDefaults.colors(
                            thumbColor = themeColor,
                            activeTrackColor = themeColor
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                // Sound label & theme toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onPlaySound) {
                        Icon(Icons.Filled.PlayCircle, contentDescription = "تشغيل", tint = themeColor, modifier = Modifier.size(28.dp))
                    }

                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "صوت: $soundName",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }

                // Action buttons: Save & Cancel
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("إلغاء", color = Color(0xFF94A3B8))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onSave(
                                alarm.copy(
                                    prayerRef = prayerRef,
                                    minutesBefore = minsBefore,
                                    isEnabled = isEnabled,
                                    volume = volume,
                                    soundName = soundName,
                                    themeColor = themeColor
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = themeColor, contentColor = Color.Black)
                    ) {
                        Text("حفظ المنبه", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// Modal Day Picker matching Screenshot 3: Checkboxes from 1 to 30
@Composable
private fun DayPickerModalDialog(
    selectedDays: Set<Int>,
    onDismiss: () -> Unit,
    onConfirm: (Set<Int>) -> Unit
) {
    var tempDays by remember { mutableStateOf(selectedDays) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.8f),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF22262B))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp)
            ) {
                // Quick Select preset buttons for convenience: الأيام البيض (13-15)، عرفة (9)، كل الأيام
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilledTonalButton(
                        onClick = { tempDays = setOf(13, 14, 15) },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("البيض (13-15)", fontSize = 10.sp)
                    }
                    FilledTonalButton(
                        onClick = { tempDays = setOf(9) },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("عرفة (9)", fontSize = 10.sp)
                    }
                    FilledTonalButton(
                        onClick = { tempDays = (1..30).toSet() },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("جميع الأيام", fontSize = 10.sp)
                    }
                }

                Divider(color = Color(0xFF334155), modifier = Modifier.padding(vertical = 8.dp))

                // List of days 1 to 30 with checkboxes matching Screenshot 3
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    items((1..30).toList()) { dayNumber ->
                        val isChecked = tempDays.contains(dayNumber)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    tempDays = if (isChecked) tempDays - dayNumber else tempDays + dayNumber
                                }
                                .padding(vertical = 6.dp, horizontal = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = {
                                    tempDays = if (it) tempDays + dayNumber else tempDays - dayNumber
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Color(0xFF38BDF8),
                                    checkmarkColor = Color.Black
                                )
                            )

                            Text(
                                text = "$dayNumber",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Divider(color = Color(0xFF334155).copy(alpha = 0.5f), thickness = 0.5.dp)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Bottom Buttons matching screenshot: موافق on right/left, إلغاء
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("إلغاء", color = Color(0xFF94A3B8), fontSize = 14.sp)
                    }

                    Button(
                        onClick = { onConfirm(tempDays) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38BDF8), contentColor = Color.Black)
                    ) {
                        Text("موافق", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// Modal Month Picker matching Screenshot 4: 12 Hijri Months Checkboxes
@Composable
private fun MonthPickerModalDialog(
    selectedMonths: Set<String>,
    onDismiss: () -> Unit,
    onConfirm: (Set<String>) -> Unit
) {
    var tempMonths by remember { mutableStateOf(selectedMonths) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.8f),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF22262B))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp)
            ) {
                // Header with quick "جميع الأشهر" button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledTonalButton(
                        onClick = { tempMonths = setOf("جميع الأشهر") },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("تحديد جميع الأشهر", fontSize = 11.sp)
                    }
                    Text("اختر الشهر الهجري", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                }

                Divider(color = Color(0xFF334155), modifier = Modifier.padding(vertical = 8.dp))

                // List of 12 months with checkboxes matching Screenshot 4
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    items(HIJRI_MONTHS_NAMES) { monthName ->
                        val isChecked = tempMonths.contains(monthName)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    tempMonths = if (monthName == "جميع الأشهر") {
                                        setOf("جميع الأشهر")
                                    } else {
                                        val withoutAll = tempMonths - "جميع الأشهر"
                                        if (isChecked) withoutAll - monthName else withoutAll + monthName
                                    }
                                }
                                .padding(vertical = 8.dp, horizontal = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = { checked ->
                                    tempMonths = if (monthName == "جميع الأشهر") {
                                        setOf("جميع الأشهر")
                                    } else {
                                        val withoutAll = tempMonths - "جميع الأشهر"
                                        if (checked) withoutAll + monthName else withoutAll - monthName
                                    }
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Color(0xFF38BDF8),
                                    checkmarkColor = Color.Black
                                )
                            )

                            Text(
                                text = monthName,
                                fontSize = 15.sp,
                                fontWeight = if (isChecked) FontWeight.Bold else FontWeight.Normal,
                                color = if (isChecked) Color(0xFF38BDF8) else Color.White
                            )
                        }
                        Divider(color = Color(0xFF334155).copy(alpha = 0.5f), thickness = 0.5.dp)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Bottom Buttons matching screenshot: موافق, إلغاء
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("إلغاء", color = Color(0xFF94A3B8), fontSize = 14.sp)
                    }

                    Button(
                        onClick = { onConfirm(tempMonths.ifEmpty { setOf("جميع الأشهر") }) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38BDF8), contentColor = Color.Black)
                    ) {
                        Text("موافق", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// 5. Full Post-Prayer Athkar Big Message Dialog ("رسالة كبيرة في أذكار الصلاة كاملة بدون صوت")
@Composable
fun FullPostPrayerAthkarBigMessageDialog(
    onDismiss: () -> Unit
) {
    var tasbihSubhan by remember { mutableStateOf(0) }
    var tasbihHamd by remember { mutableStateOf(0) }
    var tasbihAkbar by remember { mutableStateOf(0) }
    var isTammamDone by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.98f)
                .fillMaxHeight(0.92f),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = BorderStroke(1.5.dp, GoldAccent)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "إغلاق", tint = Color.White)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Filled.VolumeOff, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(18.dp))
                            Text(
                                text = "رسالة أذكار ما بعد الصلاة كاملة",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldAccent
                            )
                        }
                        Text(
                            text = "تذكير صامت بدون صوت • الأذكار المأثورة الصحيحة عن النبي ﷺ",
                            fontSize = 10.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }

                Divider(color = GoldAccent.copy(alpha = 0.4f), thickness = 1.dp, modifier = Modifier.padding(vertical = 10.dp))

                // Scrollable Athkar List
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 1. الاستغفار
                    item {
                        AthkarItemCard(
                            title = "١. الاستغفار بعد السلام مباشرة (ثلاثاً):",
                            text = "«أَسْتَغْفِرُ اللهَ، أَسْتَغْفِرُ اللهَ، أَسْتَغْفِرُ اللهَ»\n«اللَّهُمَّ أَنْتَ السَّلَامُ وَمِنْكَ السَّلَامُ، تَبَارَكْتَ يَا ذَا الجَلَالِ وَالإِكْرَامِ»",
                            countDesc = "مرة واحدة"
                        )
                    }

                    // 2. التهليل والتوحيد
                    item {
                        AthkarItemCard(
                            title = "٢. التهليل والتوحيد:",
                            text = "«لَا إِلَهَ إِلَّا اللهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ المُلْكُ وَلَهُ الحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ، اللَّهُمَّ لَا مَانِعَ لِمَا أَعْطَيْتَ، وَلَا مُعْطِيَ لِمَا مَنَعْتَ، وَلَا يَنْفَعُ ذَا الجَدِّ مِنْكَ الجَدُّ»\n\n«لَا إِلَهَ إِلَّا اللهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ المُلْكُ وَلَهُ الحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ، لَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللهِ، لَا إِلَهَ إِلَّا اللهُ، وَلَا نَعْبُدُ إِلَّا إِيَّاهُ، لَهُ النِّعْمَةُ وَلَهُ الفَضْلُ وَلَهُ الثَّنَاءُ الحَسَنُ، لَا إِلَهَ إِلَّا اللهُ مُخْلِصِينَ لَهُ الدِّينَ وَلَوْ كَرِهَ الكَافِرُونَ»",
                            countDesc = "مرة واحدة بعد كل صلاة"
                        )
                    }

                    // 3. آية الكرسي
                    item {
                        AthkarItemCard(
                            title = "٣. آية الكرسي (من قرأها دبر كل صلاة لم يمنعه من دخول الجنة إلا أن يموت):",
                            text = "«اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ ۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ ۚ لَّهُ مَا فِي السَّمَاوَاتِ وَمَا فِي الْأَرْضِ ۗ مَن ذَا الَّذِي يَشْفَعُ عِندَهُ إِلَّا بِإِذْنِهِ ۚ يَعْلَمُ مَا بَيْنَ أَيْدِيهِمْ وَمَا خَلْفَهُمْ ۖ وَلَا يُحِيطُونَ بِشَيْءٍ مِّنْ عِلْمِهِ إِلَّا بِمَا شَاءَ ۚ وَسِعَ كُرْسِيُّهُ السَّمَاوَاتِ وَالْأَرْضَ ۖ وَلَا يَئُودُهُ حِفْظُهُمَا ۚ وَهُوَ الْعَلِيُّ الْعَظِيمُ»",
                            countDesc = "سورة البقرة • آية 255"
                        )
                    }

                    // 4. المعوذات
                    item {
                        AthkarItemCard(
                            title = "٤. المعوذات (الإخلاص، الفلق، الناس):",
                            text = "قراءة {قُلْ هُوَ اللَّهُ أَحَدٌ}، و{قُلْ أَعُوذُ بِرَبِّ الْفَلَقِ}، و{قُلْ أَعُوذُ بِرَبِّ النَّاسِ}\nمرة بعد الظهر والعصر والعشاء، وثلاث مرات بعد الفجر والمغرب.",
                            countDesc = "سنة مؤكدة"
                        )
                    }

                    // 5. التسبيح والتحميد والتكبير التفاعلي (33 لكل منها)
                    item {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                            border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.5f))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = "٥. التسبيح والتحميد والتكبير (٣٣ مرة لكل منها):",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF38BDF8)
                                )

                                // Row of 3 interactive counters
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // SubhanAllah
                                    TasbihClickerColumn(
                                        title = "سُبْحَانَ اللهِ",
                                        count = tasbihSubhan,
                                        target = 33,
                                        onClick = { tasbihSubhan = (tasbihSubhan + 1).coerceAtMost(33) },
                                        color = EmeraldLight,
                                        modifier = Modifier.weight(1f)
                                    )

                                    // Alhamdulillah
                                    TasbihClickerColumn(
                                        title = "الحَمْدُ للهِ",
                                        count = tasbihHamd,
                                        target = 33,
                                        onClick = { tasbihHamd = (tasbihHamd + 1).coerceAtMost(33) },
                                        color = GoldAccent,
                                        modifier = Modifier.weight(1f)
                                    )

                                    // Allahu Akbar
                                    TasbihClickerColumn(
                                        title = "اللهُ أَكْبَرُ",
                                        count = tasbihAkbar,
                                        target = 33,
                                        onClick = { tasbihAkbar = (tasbihAkbar + 1).coerceAtMost(33) },
                                        color = Color(0xFF38BDF8),
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                // تمام المائة
                                Surface(
                                    onClick = { isTammamDone = !isTammamDone },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isTammamDone) EmeraldLight.copy(alpha = 0.2f) else Color(0xFF0F172A),
                                    border = BorderStroke(1.dp, if (isTammamDone) EmeraldLight else Color(0xFF475569)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Checkbox(
                                            checked = isTammamDone,
                                            onCheckedChange = { isTammamDone = it },
                                            colors = CheckboxDefaults.colors(checkedColor = EmeraldLight)
                                        )
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(
                                                text = "تمام المائة (غُفرت خطاياه وإن كانت مثل زبد البحر):",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isTammamDone) EmeraldLight else Color.White
                                            )
                                            Text(
                                                text = "«لَا إِلَهَ إِلَّا اللهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ المُلْكُ وَلَهُ الحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ»",
                                                fontSize = 11.sp,
                                                color = Color(0xFFCBD5E1),
                                                textAlign = TextAlign.End
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 6. الأدعية المأثورة
                    item {
                        AthkarItemCard(
                            title = "٦. الأدعية المأثورة بعد السلام:",
                            text = "«اللَّهُمَّ أَعِنِّي عَلَى ذِكْرِكَ وَشُكْرِكَ وَحُسْنِ عِبَادَتِكَ»\n\n«رَبِّ قِنِي عَذَابَكَ يَوْمَ تَبْعَثُ عِبَادَكَ»\n\n«اللَّهُمَّ إِنِّي أَسْأَلُكَ عِلْمًا نَافِعًا، وَرِزْقًا طَيِّبًا، وَعَمَلًا مُتَقَبَّلًا» (بعد صلاة الفجر)",
                            countDesc = "مستحب عظيم النفع"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Done Button
                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Color.Black),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("تمت قراءة الأذكار • جزاك الله خيراً", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun TasbihClickerColumn(
    title: String,
    count: Int,
    target: Int,
    onClick: () -> Unit,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFF0F172A),
        border = BorderStroke(1.dp, if (count >= target) color else Color(0xFF334155)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$count / $target",
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (count >= target) color else Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                shape = CircleShape,
                color = color.copy(alpha = 0.2f),
                modifier = Modifier.size(24.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (count >= target) Icons.Filled.Check else Icons.Filled.Add,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AthkarItemCard(
    title: String,
    text: String,
    countDesc: String
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        border = BorderStroke(1.dp, Color(0xFF334155))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = GoldAccent.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = countDesc,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldAccent,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldAccent,
                    textAlign = TextAlign.End
                )
            }

            Text(
                text = text,
                fontSize = 13.sp,
                color = Color.White,
                textAlign = TextAlign.End,
                lineHeight = 22.sp
            )
        }
    }
}
