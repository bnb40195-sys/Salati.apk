package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.QuranRiwayahsDataset
import com.example.data.models.Qari
import com.example.data.models.Surah
import com.example.data.services.PlaybackState
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.GoldAccent

@Composable
fun QuranRecitationView(
    surahs: List<Surah>,
    qaris: List<Qari>,
    selectedQari: Qari,
    selectedRiwayahId: String,
    playbackState: PlaybackState,
    onSelectQari: (Qari) -> Unit,
    onSelectRiwayah: (String) -> Unit,
    onPlaySurah: (Surah, Qari) -> Unit,
    onTogglePlayPause: () -> Unit,
    onStopAudio: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var showRiwayahMenu by remember { mutableStateOf(false) }

    val currentRiwayah = remember(selectedRiwayahId) {
        QuranRiwayahsDataset.getRiwayah(selectedRiwayahId)
    }
    val allRiwayahs = remember { QuranRiwayahsDataset.riwayahs }
    val availableQaris = remember(selectedRiwayahId, qaris) {
        QuranRiwayahsDataset.getRecitersForRiwayah(selectedRiwayahId).ifEmpty { qaris }
    }

    val filteredSurahs = remember(surahs, searchQuery) {
        if (searchQuery.isBlank()) surahs
        else surahs.filter {
            it.nameAr.contains(searchQuery) ||
                it.nameEn.contains(searchQuery, ignoreCase = true) ||
                it.number.toString() == searchQuery.trim()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("quran_recitation_view")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (playbackState.isPlaying || playbackState.isBuffering) 84.dp else 0.dp)
        ) {
            // Top Header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 3.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.testTag("recitation_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "الرجوع للفهرس",
                                tint = GoldAccent
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.VolumeUp,
                                    contentDescription = null,
                                    tint = GoldAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "تلاوة القرآن الكريم",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Text(
                                text = "استمع لكبار القراء بمختلف الروايات المتواترة",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Riwayah Picker + Active Qari Badge
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box {
                            FilledTonalButton(
                                onClick = { showRiwayahMenu = true },
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Filled.AutoStories, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(currentRiwayah.nameAr, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Icon(Icons.Filled.ArrowDropDown, contentDescription = null, modifier = Modifier.size(18.dp))
                            }

                            DropdownMenu(
                                expanded = showRiwayahMenu,
                                onDismissRequest = { showRiwayahMenu = false }
                            ) {
                                allRiwayahs.forEach { riwayah ->
                                    DropdownMenuItem(
                                        text = { Text(riwayah.nameAr, fontSize = 12.sp) },
                                        onClick = {
                                            onSelectRiwayah(riwayah.id)
                                            showRiwayahMenu = false
                                        },
                                        leadingIcon = {
                                            if (riwayah.id == selectedRiwayahId) {
                                                Icon(Icons.Filled.Check, contentDescription = null, tint = GoldAccent)
                                            }
                                        }
                                    )
                                }
                            }
                        }

                        Text(
                            text = "القارئ: ${selectedQari.nameAr}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = EmeraldLight,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Qari Horizontal Carousel
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(availableQaris, key = { it.id }) { qari ->
                            val isSelected = qari.id == selectedQari.id
                            FilterChip(
                                selected = isSelected,
                                onClick = { onSelectQari(qari) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = if (isSelected) Icons.Filled.RecordVoiceOver else Icons.Filled.Person,
                                        contentDescription = null,
                                        modifier = Modifier.size(15.dp)
                                    )
                                },
                                label = { Text(qari.nameAr, fontSize = 12.sp) },
                                shape = RoundedCornerShape(12.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = GoldAccent.copy(alpha = 0.22f),
                                    selectedLabelColor = GoldAccent,
                                    selectedLeadingIconColor = GoldAccent
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Surah Search
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("ابحث عن السورة للاستماع إليها...", fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Filled.Clear, contentDescription = "مسح البحث", modifier = Modifier.size(16.dp))
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Surahs List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                contentPadding = PaddingValues(top = 10.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredSurahs, key = { it.number }) { surah ->
                    val isCurrentPlaying = playbackState.currentTitle.contains(surah.nameAr) &&
                        (playbackState.isPlaying || playbackState.isBuffering)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (isCurrentPlaying) onTogglePlayPause()
                                else onPlaySurah(surah, selectedQari)
                            },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isCurrentPlaying)
                                EmeraldLight.copy(alpha = 0.14f)
                            else
                                MaterialTheme.colorScheme.surface
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isCurrentPlaying) EmeraldLight else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = if (isCurrentPlaying) EmeraldLight else GoldAccent.copy(alpha = 0.18f),
                                    modifier = Modifier.size(38.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "${surah.number}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isCurrentPlaying) Color.Black else GoldAccent
                                        )
                                    }
                                }

                                Column {
                                    Text(
                                        text = "سورة ${surah.nameAr}",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${surah.revelationType} • ${surah.versesCount} آية • ${selectedQari.nameAr}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            IconButton(
                                onClick = {
                                    if (isCurrentPlaying) onTogglePlayPause()
                                    else onPlaySurah(surah, selectedQari)
                                },
                                modifier = Modifier.minimumInteractiveComponentSize()
                            ) {
                                Icon(
                                    imageVector = if (isCurrentPlaying && playbackState.isPlaying)
                                        Icons.Filled.PauseCircle
                                    else
                                        Icons.Filled.PlayCircle,
                                    contentDescription = if (isCurrentPlaying && playbackState.isPlaying) "إيقاف مؤقت" else "تشغيل السورة",
                                    tint = if (isCurrentPlaying) EmeraldLight else GoldAccent,
                                    modifier = Modifier.size(34.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Bottom Audio Player Bar
        if (playbackState.isPlaying || playbackState.isBuffering) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                tonalElevation = 8.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.6f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = GoldAccent,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Filled.Headphones, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                            }
                        }
                        Column {
                            Text(
                                text = playbackState.currentTitle.ifBlank { "تلاوة القرآن الكريم" },
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = playbackState.currentSubtitle.ifBlank { selectedQari.nameAr },
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onTogglePlayPause) {
                            if (playbackState.isBuffering) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(22.dp),
                                    strokeWidth = 2.dp,
                                    color = GoldAccent
                                )
                            } else {
                                Icon(
                                    imageVector = if (playbackState.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                    contentDescription = "تشغيل أو إيقاف مؤقت",
                                    tint = GoldAccent,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        IconButton(onClick = onStopAudio) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "إيقاف التلاوة",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
