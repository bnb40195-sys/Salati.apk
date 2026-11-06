package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import com.example.data.models.AdhanAudioModel
import com.example.data.models.AppSectionModel
import com.example.data.models.ProphetStory
import com.example.data.models.TawbahStory
import com.example.data.models.WallpaperModel
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldAccentLight
import kotlin.random.Random

@Composable
fun CreatorStudioScreen(
    developerEmail: String,
    securityQuestion: String,
    securityAnswer: String,
    isUnlocked: Boolean,
    customStories: List<TawbahStory> = emptyList(),
    prophetsList: List<ProphetStory> = emptyList(),
    tawbahStoriesList: List<TawbahStory> = emptyList(),
    adhanAudiosList: List<AdhanAudioModel> = emptyList(),
    sectionsList: List<AppSectionModel> = emptyList(),
    wallpapersList: List<WallpaperModel> = emptyList(),
    onUnlockCreator: (String, String) -> Boolean,
    onChangeDeveloperEmail: (String) -> Unit,
    onAddCustomStory: (String, String, String) -> Unit = { _, _, _ -> },
    onUpdateTawbahStory: ((String, String, String, String, String, String) -> Unit)? = null,
    onDeleteCustomStory: (String) -> Unit = {},
    onAddProphetStory: ((String, String, String, String, String, String) -> Unit)? = null,
    onUpdateProphetStory: ((String, String, String, String, String, String, String) -> Unit)? = null,
    onDeleteProphetStory: ((String) -> Unit)? = null,
    onAddSound: ((String, String, String, String) -> Unit)? = null,
    onUpdateSound: ((String, String, String, String, String) -> Unit)? = null,
    onDeleteSound: ((String) -> Unit)? = null,
    onToggleSection: ((String, Boolean) -> Unit)? = null,
    onUpdateSection: ((String, String, String) -> Unit)? = null,
    onDeleteSection: ((String) -> Unit)? = null,
    onAddSection: ((String, String) -> Unit)? = null,
    onResetSections: (() -> Unit)? = null,
    onUpdateWallpaper: ((String, String, String, Long, String?) -> Unit)? = null,
    onDeleteWallpaper: ((String) -> Unit)? = null,
    onAddWallpaper: ((String, String, Long, String?) -> Unit)? = null,
    onResetWallpapers: (() -> Unit)? = null,
    onPlayAudio: ((String, String) -> Unit)? = null,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboard = remember { context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager }

    var emailInput by remember { mutableStateOf(if (isUnlocked) developerEmail else "") }
    var answerInput by remember { mutableStateOf("") }
    var loginError by remember { mutableStateOf<String?>(null) }
    var unlocked by remember { mutableStateOf(isUnlocked) }

    var selectedAdminTab by remember { mutableStateOf(0) }
    val adminTabs = listOf(
        "المحتوى والقصص",
        "أصوات الأذان والتنبيهات",
        "الخلفيات والتصاميم",
        "الأقسام وإدارتها",
        "الويدجات الـ 5",
        "بيانات المطور"
    )

    // Sub-tab for stories: 0 -> Tawbah, 1 -> Prophets
    var storiesSubTab by remember { mutableStateOf(0) }

    // Add Tawbah Story Dialog
    var showAddStoryDialog by remember { mutableStateOf(false) }
    var storyTitle by remember { mutableStateOf("") }
    var storyPerson by remember { mutableStateOf("") }
    var storyDetails by remember { mutableStateOf("") }
    var storySource by remember { mutableStateOf("سير أعلام النبلاء") }
    var storyMoral by remember { mutableStateOf("عظمة رحمة الله ومغفرته للتائبين") }

    // Edit Tawbah Story Dialog
    var storyToEdit by remember { mutableStateOf<TawbahStory?>(null) }
    var editStoryTitle by remember { mutableStateOf("") }
    var editStoryPerson by remember { mutableStateOf("") }
    var editStoryDetails by remember { mutableStateOf("") }
    var editStorySource by remember { mutableStateOf("") }
    var editStoryMoral by remember { mutableStateOf("") }

    // Delete Tawbah Story Dialog
    var storyToDelete by remember { mutableStateOf<TawbahStory?>(null) }

    // Add Prophet Story Dialog
    var showAddProphetDialog by remember { mutableStateOf(false) }
    var newProphetName by remember { mutableStateOf("") }
    var newProphetTitle by remember { mutableStateOf("") }
    var newProphetStory by remember { mutableStateOf("") }
    var newProphetMiracles by remember { mutableStateOf("") }
    var newProphetLessons by remember { mutableStateOf("") }

    // Edit Prophet Story Dialog
    var prophetToEdit by remember { mutableStateOf<ProphetStory?>(null) }
    var editProphetName by remember { mutableStateOf("") }
    var editProphetTitle by remember { mutableStateOf("") }
    var editProphetSummary by remember { mutableStateOf("") }
    var editProphetFullStory by remember { mutableStateOf("") }
    var editProphetMiracles by remember { mutableStateOf("") }
    var editProphetLessons by remember { mutableStateOf("") }

    // Delete Prophet Story Dialog
    var prophetToDelete by remember { mutableStateOf<ProphetStory?>(null) }

    // Delete Sound Dialog
    var soundToDelete by remember { mutableStateOf<AdhanAudioModel?>(null) }

    // Add Sound Dialog
    var showAddSoundDialog by remember { mutableStateOf(false) }
    var newSoundName by remember { mutableStateOf("") }
    var newSoundDesc by remember { mutableStateOf("") }
    var newSoundUrl by remember { mutableStateOf("") }
    var newSoundCategory by remember { mutableStateOf("adhan") }

    // Change Email Dialog
    var showChangeEmailDialog by remember { mutableStateOf(false) }
    var newEmailText by remember { mutableStateOf("") }

    // Change Security Question Dialog
    var showChangeSecDialog by remember { mutableStateOf(false) }
    var newSecQuestion by remember { mutableStateOf(securityQuestion) }
    var newSecAnswer by remember { mutableStateOf("") }

    // User In-App Email Generator
    var generatedEmail by remember { mutableStateOf<String?>(null) }
    var copyStatusMsg by remember { mutableStateOf<String?>(null) }

    // Wallpapers and Section management dialog states
    var wallpaperToDelete by remember { mutableStateOf<WallpaperModel?>(null) }
    var showAddWallpaperDialog by remember { mutableStateOf(false) }
    var newWallpaperName by remember { mutableStateOf("") }
    var newWallpaperDesc by remember { mutableStateOf("") }

    var showAddSectionDialog by remember { mutableStateOf(false) }
    var sectionToDelete by remember { mutableStateOf<AppSectionModel?>(null) }
    var newSectionTitle by remember { mutableStateOf("") }
    var newSectionSubtitle by remember { mutableStateOf("") }

    // Widgets Customization State
    var widget1Active by remember { mutableStateOf(true) }
    var widget2Active by remember { mutableStateOf(true) }
    var widget3Active by remember { mutableStateOf(true) }
    var widget4Active by remember { mutableStateOf(true) }
    var widget5Active by remember { mutableStateOf(true) }

    androidx.activity.compose.BackHandler(onBack = onBack)

    Column(
        modifier = modifier
            .testTag("creator_studio_screen")
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // App Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Filled.AdminPanelSettings, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(20.dp))
                        Text(
                            text = "قسم صانع التطبيق (Creator Studio)",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldAccent
                        )
                    }
                    Text(
                        text = if (unlocked) "لوحة المطور مفعّلة" else "تسجيل دخول المطور والتحقق الأمني",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        if (!unlocked) {
            // Lock & Verification Screen (Screenshot 6)
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(GoldAccent.copy(alpha = 0.15f))
                            .border(2.dp, GoldAccent, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = "Lock",
                            tint = GoldAccent,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "بوابة صانع التطبيق والمطور",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "قسم خاص بمطور وصانع التطبيق لإدارة المحتوى والقصص والأصوات والويدجات. الرجاء التحقق من هويتك للدخول.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            OutlinedTextField(
                                value = emailInput,
                                onValueChange = { emailInput = it },
                                label = { Text("البريد الإلكتروني المعتمد") },
                                placeholder = { Text("أدخل البريد الإلكتروني") },
                                leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null, tint = GoldAccent) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "السؤال الأمني: ما هو اسم التطبيق الأساسي؟",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )

                            OutlinedTextField(
                                value = answerInput,
                                onValueChange = { answerInput = it },
                                label = { Text("إجابة سؤال التحقق") },
                                placeholder = { Text("أدخل إجابة الأمان...") },
                                leadingIcon = { Icon(Icons.Filled.Key, contentDescription = null, tint = GoldAccent) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )

                            if (loginError != null) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = loginError ?: "",
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            Button(
                                onClick = {
                                    val success = onUnlockCreator(emailInput.trim(), answerInput.trim())
                                    if (success) {
                                        unlocked = true
                                        loginError = null
                                    } else {
                                        unlocked = false
                                        loginError = "البيانات المدخلة غير صحيحة! لن يظهر قسم صانع التطبيق إلا بالبيانات الصحيحة."
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                            ) {
                                Icon(Icons.Filled.CheckCircle, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("تسجيل الدخول للمطور", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        } else {
            // Unlocked Creator Dashboard (Screenshots 7 & 8)
            Column(modifier = Modifier.fillMaxSize()) {
                ScrollableTabRow(
                    selectedTabIndex = selectedAdminTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary,
                    edgePadding = 12.dp
                ) {
                    adminTabs.forEachIndexed { idx, title ->
                        val isSelected = selectedAdminTab == idx
                        Tab(
                            selected = isSelected,
                            onClick = { selectedAdminTab = idx },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }

                when (selectedAdminTab) {
                    0 -> {
                        // Content & Stories Management (Tawbah & Prophets with Editing & Deletion)
                        val displayedTawbah = remember(tawbahStoriesList, customStories) {
                            if (tawbahStoriesList.isNotEmpty()) tawbahStoriesList else customStories
                        }
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentPadding = PaddingValues(bottom = 80.dp)
                        ) {
                            item {
                                // Content summary stats
                                Text(
                                    text = "إدارة وتعديل المحتوى والقصص",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "يمكنك تعديل أي قصة، إضافة قصص جديدة، أو حذف أي قصة برغبتك",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Card(
                                        modifier = Modifier.weight(1f),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Text("قصص التوبة", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text("${displayedTawbah.size} قصة", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = EmeraldLight)
                                        }
                                    }
                                    Card(
                                        modifier = Modifier.weight(1f),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Text("قصص الأنبياء", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text("${prophetsList.size} نبياً", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Sub Tabs for Stories
                                TabRow(
                                    selectedTabIndex = storiesSubTab,
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    contentColor = GoldAccent,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                ) {
                                    Tab(
                                        selected = storiesSubTab == 0,
                                        onClick = { storiesSubTab = 0 },
                                        text = { Text("قصص التوبة (${displayedTawbah.size})", fontSize = 12.sp, fontWeight = if (storiesSubTab == 0) FontWeight.Bold else FontWeight.Normal) },
                                        icon = { Icon(Icons.Filled.Favorite, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                    )
                                    Tab(
                                        selected = storiesSubTab == 1,
                                        onClick = { storiesSubTab = 1 },
                                        text = { Text("قصص الأنبياء (${prophetsList.size})", fontSize = 12.sp, fontWeight = if (storiesSubTab == 1) FontWeight.Bold else FontWeight.Normal) },
                                        icon = { Icon(Icons.Filled.AutoStories, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (storiesSubTab == 0) "قائمة قصص التوبة وسير التائبين" else "قائمة قصص وسير الأنبياء",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = GoldAccent
                                    )
                                    Button(
                                        onClick = {
                                            if (storiesSubTab == 0) showAddStoryDialog = true
                                            else showAddProphetDialog = true
                                        },
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(if (storiesSubTab == 0) "إضافة قصة توبة" else "إضافة قصة نبي", fontSize = 11.sp)
                                    }
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                            }

                            if (storiesSubTab == 0) {
                                // Tawbah Stories List
                                items(displayedTawbah, key = { it.id }) { story ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                    ) {
                                        Column(modifier = Modifier.padding(14.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(text = story.titleAr, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                                                    Text(text = "صاحب القصة: ${story.personAr}", fontSize = 11.sp, color = GoldAccent)
                                                }

                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    // Edit button
                                                    IconButton(
                                                        onClick = {
                                                            storyToEdit = story
                                                            editStoryTitle = story.titleAr
                                                            editStoryPerson = story.personAr
                                                            editStoryDetails = story.detailsAr
                                                            editStorySource = story.sourceAr
                                                            editStoryMoral = story.moralLesson
                                                        }
                                                    ) {
                                                        Icon(Icons.Filled.Edit, contentDescription = "تعديل القصة", tint = EmeraldLight, modifier = Modifier.size(20.dp))
                                                    }

                                                    // Delete button
                                                    IconButton(onClick = { storyToDelete = story }) {
                                                        Icon(Icons.Filled.Delete, contentDescription = "حذف القصة", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
                                                    }
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(text = story.detailsAr, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)

                                            if (story.moralLesson.isNotBlank()) {
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(text = "العبرة: ${story.moralLesson}", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary, maxLines = 1)
                                            }
                                        }
                                    }
                                }
                            } else {
                                // Prophet Stories List
                                items(prophetsList, key = { it.id }) { prophet ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                    ) {
                                        Column(modifier = Modifier.padding(14.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(text = prophet.nameAr, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = GoldAccent)
                                                    Text(text = prophet.titleAr, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                                                }

                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    // Edit button
                                                    IconButton(
                                                        onClick = {
                                                            prophetToEdit = prophet
                                                            editProphetName = prophet.nameAr
                                                            editProphetTitle = prophet.titleAr
                                                            editProphetSummary = prophet.summaryAr
                                                            editProphetFullStory = prophet.fullStoryAr
                                                            editProphetMiracles = prophet.miraclesAr
                                                            editProphetLessons = prophet.keyLessons
                                                        }
                                                    ) {
                                                        Icon(Icons.Filled.Edit, contentDescription = "تعديل القصة", tint = EmeraldLight, modifier = Modifier.size(20.dp))
                                                    }

                                                    // Delete button
                                                    IconButton(onClick = { prophetToDelete = prophet }) {
                                                        Icon(Icons.Filled.Delete, contentDescription = "حذف القصة", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
                                                    }
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(text = prophet.summaryAr.ifEmpty { prophet.fullStoryAr.take(120) + "..." }, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    1 -> {
                        // Adhan & Reminder Sounds Management with Full Deletion and Custom Addition
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentPadding = PaddingValues(bottom = 80.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "إدارة وحذف أصوات الأذان والتنبيهات (${adhanAudiosList.size})",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "حذف أي صوت أو إضافة تسجيلات مخصصة للتطبيق",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Button(
                                        onClick = { showAddSoundDialog = true },
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("إضافة صوت", fontSize = 11.sp)
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                            }

                            items(adhanAudiosList, key = { it.id }) { audio ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
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
                                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(38.dp)
                                                    .clip(CircleShape)
                                                    .background(
                                                        when (audio.category) {
                                                            "friday" -> GoldAccent.copy(alpha = 0.2f)
                                                            "iqama" -> EmeraldLight.copy(alpha = 0.2f)
                                                            "reminder" -> MaterialTheme.colorScheme.primaryContainer
                                                            else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                                        }
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = when (audio.category) {
                                                        "friday" -> Icons.Filled.Mosque
                                                        "iqama" -> Icons.Filled.NotificationsActive
                                                        else -> Icons.Filled.VolumeUp
                                                    },
                                                    contentDescription = null,
                                                    tint = when (audio.category) {
                                                        "friday" -> GoldAccent
                                                        "iqama" -> EmeraldLight
                                                        else -> MaterialTheme.colorScheme.primary
                                                    },
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }

                                            Column {
                                                Text(text = audio.nameAr, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                                Text(text = audio.descriptionAr, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                Surface(
                                                    shape = RoundedCornerShape(4.dp),
                                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                                    modifier = Modifier.padding(top = 2.dp)
                                                ) {
                                                    Text(
                                                        text = when (audio.category) {
                                                            "friday" -> "أذان الجمعة"
                                                            "iqama" -> "إقامة الصلاة"
                                                            "reminder" -> "تذكير قبل الصلاة"
                                                            "salawat" -> "الصلاة على النبي"
                                                            "athkar" -> "أذكار"
                                                            else -> "أذان الصلوات"
                                                        },
                                                        fontSize = 9.sp,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            IconButton(onClick = { onPlayAudio?.invoke(audio.audioUrl, audio.nameAr) }) {
                                                Icon(Icons.Filled.PlayArrow, contentDescription = "استماع", tint = GoldAccent)
                                            }

                                            // Delete sound button
                                            IconButton(onClick = { soundToDelete = audio }) {
                                                Icon(Icons.Filled.Delete, contentDescription = "حذف الصوت", tint = MaterialTheme.colorScheme.error)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    2 -> {
                        // Wallpapers Management with Deletion support
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentPadding = PaddingValues(bottom = 80.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "الخلفيات والتصاميم الإسلامية (${wallpapersList.size})",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "يمكنك إضافة أو حذف أي خلفية من خلفيات التطبيق",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Button(
                                        onClick = { showAddWallpaperDialog = true },
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(Icons.Filled.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("إضافة خلفية", fontSize = 12.sp)
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                            }

                            items(wallpapersList, key = { it.id }) { wp ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .padding(14.dp)
                                            .fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(wp.colorHex))
                                                    .border(1.dp, GoldAccent.copy(alpha = 0.5f), CircleShape)
                                            )
                                            Column {
                                                Text(text = wp.nameAr, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                                Text(text = wp.descriptionAr, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = MaterialTheme.colorScheme.primaryContainer
                                            ) {
                                                Text(
                                                    text = if (wp.isPreset) "أساسية" else "مخصصة",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }

                                            // Delete Wallpaper Button (Requested!)
                                            IconButton(
                                                onClick = { wallpaperToDelete = wp }
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Filled.Delete,
                                                    contentDescription = "حذف الخلفية",
                                                    tint = MaterialTheme.colorScheme.error,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    3 -> {
                        // Sections Management & Addition (Requested feature: إضافة وتعديل الأقسام الأخرى)
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentPadding = PaddingValues(bottom = 80.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "إدارة وتخصيص أقسام التطبيق (${sectionsList.size})",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "إضافة أقسام جديدة، تفعيل أو إخفاء، وحذف الأقسام المخصصة",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Button(
                                        onClick = { showAddSectionDialog = true },
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(Icons.Filled.AddCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("إضافة قسم", fontSize = 11.sp)
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                            }

                            items(sectionsList, key = { it.id }) { section ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
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
                                                Text(text = section.titleAr, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                                if (section.isCustom) {
                                                    Surface(
                                                        shape = RoundedCornerShape(4.dp),
                                                        color = GoldAccent.copy(alpha = 0.2f)
                                                    ) {
                                                        Text("مخصص", fontSize = 9.sp, color = GoldAccent, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                                    }
                                                }
                                            }
                                            Text(text = section.subtitleAr, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Switch(
                                                checked = section.isEnabled,
                                                onCheckedChange = { isChecked ->
                                                    onToggleSection?.invoke(section.id, isChecked)
                                                }
                                            )
                                            if (section.isCustom) {
                                                IconButton(onClick = { sectionToDelete = section }) {
                                                    Icon(Icons.Filled.Delete, contentDescription = "حذف القسم", tint = MaterialTheme.colorScheme.error)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    4 -> {
                        // 5 Widgets Management & Previews (Requested feature: 5 widgets with preview and customization)
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentPadding = PaddingValues(bottom = 80.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            item {
                                Text(
                                    text = "إدارة وتخصيص قوالب الويدجات الـ 5 لشاشة الهاتف",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "ويدجات تعمل خارج التطبيق على شاشة الهاتف الرئيسية مع إمكانية التعديل والتفعيل",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                            }

                            val widgetConfigs = listOf(
                                Triple("ويدجت 1: شريط المواقيت الخمسة الكامل", "يعرض الفجر والشروق والظهر والعصر والمغرب والعشاء مع الوقت المتبقي", widget1Active) to { widget1Active = !widget1Active },
                                Triple("ويدجت 2: الصلاة القادمة فقط بالعد التنازلي", "عرض صلاة قادمة بشكل بطاقة بارزة وساعة رقمية ودرجة الحرارة", widget2Active) to { widget2Active = !widget2Active },
                                Triple("ويدجت 3: آية اليوم والحديث الشريف المتجدد", "آية قرآنية مختارة وحديث نبوي شريف مع التفسير والتدبر اليومي", widget3Active) to { widget3Active = !widget3Active },
                                Triple("ويدجت 4: التقويم الهجري وطور القمر والتاريخ", "التاريخ الهجري الكامل، التقويم الميلادي، وشكل طور القمر الحالي", widget4Active) to { widget4Active = !widget4Active },
                                Triple("ويدجت 5: المسبحة الإلكترونية والأذكار السريعة", "عداد تسبيح فوري مع زر الاستغفار وأذكار الصباح والمساء السريعة", widget5Active) to { widget5Active = !widget5Active }
                            )

                            items(widgetConfigs) { (info, toggle) ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(text = info.first, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                                                Text(text = info.second, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                            Switch(
                                                checked = info.third,
                                                onCheckedChange = { toggle() }
                                            )
                                        }

                                        // Mini preview mockup of the widget on Android launcher
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.25f)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                    Icon(Icons.Filled.Widgets, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(18.dp))
                                                    Text("معاينة الويدجت للشاشة الخارجية", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                }
                                                Surface(shape = RoundedCornerShape(6.dp), color = if (info.third) EmeraldLight.copy(alpha = 0.2f) else MaterialTheme.colorScheme.error.copy(alpha = 0.2f)) {
                                                    Text(
                                                        text = if (info.third) "جاهز للعمل" else "معطّل",
                                                        fontSize = 10.sp,
                                                        color = if (info.third) EmeraldLight else MaterialTheme.colorScheme.error,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    5 -> {
                        // Developer Account & Preferences
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentPadding = PaddingValues(bottom = 80.dp)
                        ) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(text = "بيانات المطور الأساسية", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(10.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text("البريد الإلكتروني المعتمد:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                Text(developerEmail, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                                            }
                                            OutlinedButton(
                                                onClick = {
                                                    newEmailText = developerEmail
                                                    showChangeEmailDialog = true
                                                },
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text("تغيير البريد", fontSize = 11.sp)
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(14.dp))
                                        Divider()
                                        Spacer(modifier = Modifier.height(14.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text("سؤال الأمان المخصص:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                Text(securityQuestion, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                            }
                                            OutlinedButton(
                                                onClick = {
                                                    newSecQuestion = securityQuestion
                                                    showChangeSecDialog = true
                                                },
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text("تعديل السؤال", fontSize = 11.sp)
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // In-App User Account Generation
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(text = "توليد حسابات للمستخدمين", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                        Text(
                                            text = "توليد بريد إلكتروني تعريفي فوري لأي مستخدم مع صلاحيات الوصول المعتمدة.",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Button(
                                            onClick = {
                                                val id = Random.nextInt(10000, 99999)
                                                generatedEmail = "user_$id@salati.app"
                                            },
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Icon(Icons.Filled.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("إنشاء بريد مستخدم جديد", fontSize = 12.sp)
                                        }

                                        if (generatedEmail != null) {
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Text(
                                                text = "البريد المُنشأ: $generatedEmail",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
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

    // 1. Add Tawbah Story Dialog
    if (showAddStoryDialog) {
        AlertDialog(
            onDismissRequest = { showAddStoryDialog = false },
            title = { Text("إضافة قصة توبة جديدة", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = GoldAccent) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = storyTitle,
                        onValueChange = { storyTitle = it },
                        label = { Text("عنوان القصة") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = storyPerson,
                        onValueChange = { storyPerson = it },
                        label = { Text("صاحب القصة / الشخصية") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = storyDetails,
                        onValueChange = { storyDetails = it },
                        label = { Text("تفاصيل القصة والسرد") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                    OutlinedTextField(
                        value = storyMoral,
                        onValueChange = { storyMoral = it },
                        label = { Text("العبرة المستفادة") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (storyTitle.isNotBlank() && storyDetails.isNotBlank()) {
                            onAddCustomStory(storyTitle.trim(), storyPerson.trim(), storyDetails.trim())
                            storyTitle = ""
                            storyPerson = ""
                            storyDetails = ""
                            showAddStoryDialog = false
                        }
                    }
                ) {
                    Text("إضافة")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddStoryDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // 2. Edit Tawbah Story Dialog
    if (storyToEdit != null) {
        val s = storyToEdit!!
        AlertDialog(
            onDismissRequest = { storyToEdit = null },
            title = { Text("تعديل قصة التوبة", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = EmeraldLight) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = editStoryTitle,
                        onValueChange = { editStoryTitle = it },
                        label = { Text("عنوان القصة") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editStoryPerson,
                        onValueChange = { editStoryPerson = it },
                        label = { Text("صاحب القصة") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editStoryDetails,
                        onValueChange = { editStoryDetails = it },
                        label = { Text("تفاصيل القصة") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                    OutlinedTextField(
                        value = editStoryMoral,
                        onValueChange = { editStoryMoral = it },
                        label = { Text("العبرة المستفادة") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editStoryTitle.isNotBlank() && editStoryDetails.isNotBlank()) {
                            onUpdateTawbahStory?.invoke(
                                s.id,
                                editStoryTitle.trim(),
                                editStoryPerson.trim(),
                                editStoryDetails.trim(),
                                editStorySource.trim().ifEmpty { s.sourceAr },
                                editStoryMoral.trim()
                            )
                            storyToEdit = null
                        }
                    }
                ) {
                    Text("حفظ التعديلات")
                }
            },
            dismissButton = {
                TextButton(onClick = { storyToEdit = null }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // 3. Confirm Delete Tawbah Story Dialog
    if (storyToDelete != null) {
        val s = storyToDelete!!
        AlertDialog(
            onDismissRequest = { storyToDelete = null },
            title = { Text("تأكيد حذف القصة", fontWeight = FontWeight.Bold) },
            text = { Text("هل أنت متأكد من رغبتك في حذف قصة \"${s.titleAr}\" نهائياً؟") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteCustomStory(s.id)
                        storyToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("حذف")
                }
            },
            dismissButton = {
                TextButton(onClick = { storyToDelete = null }) { Text("إلغاء") }
            }
        )
    }

    // 4. Add Prophet Story Dialog
    if (showAddProphetDialog) {
        AlertDialog(
            onDismissRequest = { showAddProphetDialog = false },
            title = { Text("إضافة قصة نبي جديدة", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = GoldAccent) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newProphetName,
                        onValueChange = { newProphetName = it },
                        label = { Text("اسم النبي (عليه السلام)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newProphetTitle,
                        onValueChange = { newProphetTitle = it },
                        label = { Text("اللقب أو الصفة (مثلاً: خليل الله)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newProphetStory,
                        onValueChange = { newProphetStory = it },
                        label = { Text("السيرة والقصة كاملة") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                    OutlinedTextField(
                        value = newProphetMiracles,
                        onValueChange = { newProphetMiracles = it },
                        label = { Text("المعجزات المؤيدة") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newProphetLessons,
                        onValueChange = { newProphetLessons = it },
                        label = { Text("الدروس والعبر") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newProphetName.isNotBlank() && newProphetStory.isNotBlank()) {
                            onAddProphetStory?.invoke(
                                newProphetName.trim(),
                                newProphetTitle.trim().ifEmpty { "نبي الله ورسوله" },
                                "القرآن الكريم",
                                newProphetMiracles.trim(),
                                newProphetLessons.trim(),
                                newProphetStory.trim()
                            )
                            newProphetName = ""
                            newProphetTitle = ""
                            newProphetStory = ""
                            newProphetMiracles = ""
                            newProphetLessons = ""
                            showAddProphetDialog = false
                        }
                    }
                ) {
                    Text("إضافة")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddProphetDialog = false }) { Text("إلغاء") }
            }
        )
    }

    // 5. Edit Prophet Story Dialog
    if (prophetToEdit != null) {
        val p = prophetToEdit!!
        AlertDialog(
            onDismissRequest = { prophetToEdit = null },
            title = { Text("تعديل قصة ${p.nameAr}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = EmeraldLight) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = editProphetName,
                        onValueChange = { editProphetName = it },
                        label = { Text("اسم النبي") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editProphetTitle,
                        onValueChange = { editProphetTitle = it },
                        label = { Text("اللقب أو الصفة") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editProphetFullStory,
                        onValueChange = { editProphetFullStory = it },
                        label = { Text("السيرة والقصة كاملة") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                    OutlinedTextField(
                        value = editProphetMiracles,
                        onValueChange = { editProphetMiracles = it },
                        label = { Text("المعجزات المؤيدة") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editProphetLessons,
                        onValueChange = { editProphetLessons = it },
                        label = { Text("الدروس والعبر") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editProphetName.isNotBlank() && editProphetFullStory.isNotBlank()) {
                            onUpdateProphetStory?.invoke(
                                p.id,
                                editProphetName.trim(),
                                editProphetTitle.trim(),
                                editProphetSummary.trim().ifEmpty { editProphetFullStory.take(120) + "..." },
                                editProphetFullStory.trim(),
                                editProphetMiracles.trim(),
                                editProphetLessons.trim()
                            )
                            prophetToEdit = null
                        }
                    }
                ) {
                    Text("حفظ التعديلات")
                }
            },
            dismissButton = {
                TextButton(onClick = { prophetToEdit = null }) { Text("إلغاء") }
            }
        )
    }

    // 6. Confirm Delete Prophet Story Dialog
    if (prophetToDelete != null) {
        val p = prophetToDelete!!
        AlertDialog(
            onDismissRequest = { prophetToDelete = null },
            title = { Text("تأكيد حذف القصة", fontWeight = FontWeight.Bold) },
            text = { Text("هل أنت متأكد من رغبتك في حذف قصة \"${p.nameAr}\" نهائياً؟") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteProphetStory?.invoke(p.id)
                        prophetToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("حذف")
                }
            },
            dismissButton = {
                TextButton(onClick = { prophetToDelete = null }) { Text("إلغاء") }
            }
        )
    }

    // 7. Confirm Delete Sound Dialog
    if (soundToDelete != null) {
        val sound = soundToDelete!!
        AlertDialog(
            onDismissRequest = { soundToDelete = null },
            title = { Text("تأكيد حذف الصوت من صانع التطبيق", fontWeight = FontWeight.Bold) },
            text = { Text("هل أنت متأكد من رغبتك في حذف تسجيل \"${sound.nameAr}\" نهائياً من قائمة أصوات التطبيق؟") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteSound?.invoke(sound.id)
                        soundToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("حذف الصوت الآن")
                }
            },
            dismissButton = {
                TextButton(onClick = { soundToDelete = null }) { Text("إلغاء") }
            }
        )
    }

    // 8. Add Sound Dialog
    if (showAddSoundDialog) {
        AlertDialog(
            onDismissRequest = { showAddSoundDialog = false },
            title = { Text("إضافة صوت جديد إلى التطبيق", fontWeight = FontWeight.Bold, color = GoldAccent) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newSoundName,
                        onValueChange = { newSoundName = it },
                        label = { Text("اسم الصوت / المؤذن") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newSoundDesc,
                        onValueChange = { newSoundDesc = it },
                        label = { Text("وصف التسجيل أو المسجد") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newSoundUrl,
                        onValueChange = { newSoundUrl = it },
                        label = { Text("رابط ملف الصوت (MP3)") },
                        placeholder = { Text("https://cdn.aladhan.com/audio/...") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("التصنيف:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("adhan" to "أذان", "friday" to "جمعة", "iqama" to "إقامة", "reminder" to "تنبيه").forEach { (cat, label) ->
                            val isSel = newSoundCategory == cat
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { newSoundCategory = cat }
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    color = if (isSel) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newSoundName.isNotBlank()) {
                            onAddSound?.invoke(
                                newSoundName.trim(),
                                newSoundDesc.trim().ifEmpty { "صوت مضاف عبر صانع التطبيق" },
                                newSoundUrl.trim().ifEmpty { "https://cdn.aladhan.com/audio/adhans/makkah.mp3" },
                                newSoundCategory
                            )
                            newSoundName = ""
                            newSoundDesc = ""
                            newSoundUrl = ""
                            showAddSoundDialog = false
                        }
                    }
                ) {
                    Text("حفظ وإضافة الصوت")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddSoundDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // Add Wallpaper Dialog
    if (showAddWallpaperDialog) {
        AlertDialog(
            onDismissRequest = { showAddWallpaperDialog = false },
            title = { Text("إضافة خلفية إسلامية جديدة", fontWeight = FontWeight.Bold, color = GoldAccent) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newWallpaperName,
                        onValueChange = { newWallpaperName = it },
                        label = { Text("اسم الخلفية") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newWallpaperDesc,
                        onValueChange = { newWallpaperDesc = it },
                        label = { Text("وصف الخلفية والتصميم") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newWallpaperName.isNotBlank()) {
                            onAddWallpaper?.invoke(
                                newWallpaperName.trim(),
                                newWallpaperDesc.trim().ifEmpty { "خلفية إسلامية مخصصة" },
                                0xFF064E3B,
                                null
                            )
                            newWallpaperName = ""
                            newWallpaperDesc = ""
                            showAddWallpaperDialog = false
                        }
                    }
                ) {
                    Text("إضافة الخلفية")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddWallpaperDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // Confirm Delete Wallpaper Dialog (Requested!)
    if (wallpaperToDelete != null) {
        val wp = wallpaperToDelete!!
        AlertDialog(
            onDismissRequest = { wallpaperToDelete = null },
            title = { Text("تأكيد حذف الخلفية", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error) },
            text = { Text("هل أنت متأكد من رغبتك في حذف خلفية \"${wp.nameAr}\" نهائياً؟") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteWallpaper?.invoke(wp.id)
                        wallpaperToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("حذف الخلفية")
                }
            },
            dismissButton = {
                TextButton(onClick = { wallpaperToDelete = null }) { Text("إلغاء") }
            }
        )
    }

    // Add Section Dialog (Requested!)
    if (showAddSectionDialog) {
        AlertDialog(
            onDismissRequest = { showAddSectionDialog = false },
            title = { Text("إضافة قسم جديد للتطبيق", fontWeight = FontWeight.Bold, color = GoldAccent) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newSectionTitle,
                        onValueChange = { newSectionTitle = it },
                        label = { Text("اسم القسم الجديد") },
                        placeholder = { Text("مثال: أدعية مختارة، قصص الصالحين...") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newSectionSubtitle,
                        onValueChange = { newSectionSubtitle = it },
                        label = { Text("وصف مختصر للقسم") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newSectionTitle.isNotBlank()) {
                            onAddSection?.invoke(newSectionTitle.trim(), newSectionSubtitle.trim())
                            newSectionTitle = ""
                            newSectionSubtitle = ""
                            showAddSectionDialog = false
                        }
                    }
                ) {
                    Text("إضافة القسم الآن")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddSectionDialog = false }) { Text("إلغاء") }
            }
        )
    }

    // Confirm Delete Section Dialog
    if (sectionToDelete != null) {
        val s = sectionToDelete!!
        AlertDialog(
            onDismissRequest = { sectionToDelete = null },
            title = { Text("تأكيد حذف القسم المخصص", fontWeight = FontWeight.Bold) },
            text = { Text("هل تريد حذف قسم \"${s.titleAr}\" نهائياً؟") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteSection?.invoke(s.id)
                        sectionToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("حذف القسم")
                }
            },
            dismissButton = {
                TextButton(onClick = { sectionToDelete = null }) { Text("إلغاء") }
            }
        )
    }
}
