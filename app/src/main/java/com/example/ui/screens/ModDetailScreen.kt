package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DownloadStatus
import com.example.data.model.FullModDetail
import com.example.data.model.SongItem
import com.example.ui.theme.FnfBorder
import com.example.ui.theme.FnfCyan
import com.example.ui.theme.FnfDarkBg
import com.example.ui.theme.FnfGreen
import com.example.ui.theme.FnfOrange
import com.example.ui.theme.FnfPink
import com.example.ui.theme.FnfPurple
import com.example.ui.theme.FnfRed
import com.example.ui.theme.FnfSurface
import com.example.ui.theme.FnfSurfaceElevated
import com.example.ui.theme.FnfTextMuted
import com.example.ui.theme.FnfTextPrimary
import com.example.ui.theme.FnfTextSecondary
import com.example.ui.theme.FnfYellow

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ModDetailScreen(
    detail: FullModDetail,
    onBack: () -> Unit,
    onFavoriteToggle: () -> Unit,
    onStartDownload: () -> Unit,
    onCancelDownload: () -> Unit,
    onOpenLogScore: () -> Unit,
    onLaunchPsychEngine: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }
    val context = LocalContext.current
    val mod = detail.mod
    val accentColor = Color(mod.colorHex)
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var activePreviewSong by remember { mutableStateOf<String?>(null) }

    val tabs = listOf("Overview", "Tracklist (${mod.songs.size})", "Cast", "My Tracker")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FnfDarkBg)
    ) {
        // Sticky Header with glowing backdrop
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            accentColor.copy(alpha = 0.35f),
                            FnfDarkBg
                        )
                    )
                )
                .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 12.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(40.dp)
                            .background(FnfSurfaceElevated, CircleShape)
                            .testTag("detail_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = FnfTextPrimary
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Open External Link
                        IconButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(mod.downloadUrl))
                                context.startActivity(intent)
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .background(FnfSurfaceElevated, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.OpenInBrowser,
                                contentDescription = "Open Web Download Link",
                                tint = FnfCyan
                            )
                        }

                        // Bookmark / Favorite
                        IconButton(
                            onClick = onFavoriteToggle,
                            modifier = Modifier
                                .size(40.dp)
                                .background(FnfSurfaceElevated, CircleShape)
                                .testTag("detail_favorite_button")
                        ) {
                            Icon(
                                imageVector = if (detail.isFavorite) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                                contentDescription = "Bookmark Mod",
                                tint = if (detail.isFavorite) FnfPink else FnfTextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Title & Author
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = accentColor.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, accentColor)
                    ) {
                        Text(
                            text = mod.category.uppercase(),
                            color = accentColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Text(
                        text = mod.version,
                        color = FnfTextMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = mod.title,
                    color = FnfTextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black
                )

                Text(
                    text = "By ${mod.author}",
                    color = FnfCyan,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Metadata chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetaPill(label = "Engine", value = mod.engine.take(14))
                    MetaPill(label = "Size", value = mod.downloadSize)
                    MetaPill(label = "Difficulty", value = mod.difficulty)
                    MetaPill(label = "Downloads", value = mod.downloadCount)
                }
            }
        }

        // Action CTA Bar: Download button & Log score
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = FnfSurface,
            border = BorderStroke(1.dp, FnfBorder)
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                when (detail.downloadStatus) {
                    DownloadStatus.DOWNLOADING -> {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "DOWNLOADING... ${detail.downloadProgress}%",
                                    color = FnfCyan,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black
                                )
                                OutlinedButton(
                                    onClick = onCancelDownload,
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = FnfRed),
                                    border = BorderStroke(1.dp, FnfRed)
                                ) {
                                    Text("CANCEL", fontSize = 11.sp)
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { detail.downloadProgress / 100f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = FnfCyan,
                                trackColor = FnfSurfaceElevated
                            )
                        }
                    }
                    DownloadStatus.DOWNLOADED -> {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = onLaunchPsychEngine,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("launch_psych_detail_btn"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = FnfRed, contentColor = Color.White)
                            ) {
                                Icon(Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("PLAY IN ${mod.engine.uppercase()} (STABLE APK)", fontWeight = FontWeight.Black)
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = onOpenLogScore,
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("log_score_cta_btn"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = FnfGreen, contentColor = FnfDarkBg)
                                ) {
                                    Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("LOG SCORE & NOTES", fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = onCancelDownload,
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, FnfBorder),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = FnfTextMuted)
                                ) {
                                    Text("REMOVE")
                                }
                            }
                        }
                    }
                    DownloadStatus.NOT_DOWNLOADED -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = onStartDownload,
                                modifier = Modifier
                                    .weight(1.3f)
                                    .testTag("start_download_cta_btn"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = FnfCyan, contentColor = FnfDarkBg)
                            ) {
                                Icon(Icons.Filled.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("DOWNLOAD & INSTALL", fontWeight = FontWeight.Black)
                            }

                            OutlinedButton(
                                onClick = onOpenLogScore,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, FnfBorder),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = FnfPink)
                            ) {
                                Text("TRACK PROGRESS", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Tabs Row
        SecondaryTabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = FnfSurfaceElevated,
            contentColor = FnfCyan,
            divider = {}
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 12.sp,
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTabIndex == index) FnfCyan else FnfTextSecondary
                        )
                    }
                )
            }
        }

        // Tab Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            when (selectedTabIndex) {
                0 -> OverviewTab(mod = mod)
                1 -> TracklistTab(
                    songs = mod.songs,
                    activePreviewSong = activePreviewSong,
                    onTogglePreview = { songTitle ->
                        activePreviewSong = if (activePreviewSong == songTitle) null else songTitle
                    }
                )
                2 -> CharactersTab(characters = mod.characters)
                3 -> PersonalTrackerTab(detail = detail, onEditClick = onOpenLogScore)
            }
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun OverviewTab(mod: com.example.data.model.FnfMod) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "SYNOPSIS & STORY LORE",
            color = FnfCyan,
            fontSize = 12.sp,
            fontWeight = FontWeight.Black
        )
        Spacer(modifier = Modifier.height(6.dp))
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = FnfSurface),
            border = BorderStroke(1.dp, FnfBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = mod.description,
                    color = FnfTextPrimary,
                    fontSize = 14.sp,
                    lineHeight = 21.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "LORE BACKGROUND:",
                    color = FnfPink,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = mod.lore,
                    color = FnfTextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Unique Mechanics
        Text(
            text = "CUSTOM GAMEPLAY MECHANICS",
            color = FnfPink,
            fontSize = 12.sp,
            fontWeight = FontWeight.Black
        )
        Spacer(modifier = Modifier.height(6.dp))
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            mod.mechanics.forEach { mechanic ->
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = FnfSurface,
                    border = BorderStroke(1.dp, FnfBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(FnfYellow, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = mechanic,
                            color = FnfTextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Supported Platforms
        Text(
            text = "SUPPORTED PLATFORMS",
            color = FnfGreen,
            fontSize = 12.sp,
            fontWeight = FontWeight.Black
        )
        Spacer(modifier = Modifier.height(6.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            mod.platforms.forEach { platform ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = FnfGreen.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, FnfGreen.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = "✓ $platform",
                        color = FnfGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun TracklistTab(
    songs: List<SongItem>,
    activePreviewSong: String?,
    onTogglePreview: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "FULL TRACKLIST & CHARTS (${songs.size} SONGS)",
            color = FnfCyan,
            fontSize = 12.sp,
            fontWeight = FontWeight.Black
        )
        Spacer(modifier = Modifier.height(10.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            songs.forEachIndexed { index, song ->
                val isPlaying = activePreviewSong == song.title

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = FnfSurface),
                    border = BorderStroke(1.dp, if (isPlaying) FnfCyan else FnfBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (isPlaying) FnfCyan else FnfSurfaceElevated,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "#${index + 1}",
                                    color = if (isPlaying) FnfDarkBg else FnfTextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = song.title,
                                color = if (isPlaying) FnfCyan else FnfTextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "vs ${song.opponent}",
                                    color = FnfTextSecondary,
                                    fontSize = 12.sp
                                )
                                Text(text = "•", color = FnfTextMuted, fontSize = 10.sp)
                                Text(
                                    text = "${song.bpm} BPM",
                                    color = FnfYellow,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(text = "•", color = FnfTextMuted, fontSize = 10.sp)
                                Text(
                                    text = song.duration,
                                    color = FnfTextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        // Difficulty Tag
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = when (song.difficultyLevel.lowercase()) {
                                "mania", "insane" -> FnfRed.copy(alpha = 0.2f)
                                "hard" -> FnfOrange.copy(alpha = 0.2f)
                                else -> FnfGreen.copy(alpha = 0.2f)
                            }
                        ) {
                            Text(
                                text = song.difficultyLevel,
                                color = when (song.difficultyLevel.lowercase()) {
                                    "mania", "insane" -> FnfRed
                                    "hard" -> FnfOrange
                                    else -> FnfGreen
                                },
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Beat preview toggle button
                        IconButton(
                            onClick = { onTogglePreview(song.title) },
                            modifier = Modifier
                                .size(36.dp)
                                .background(if (isPlaying) FnfCyan.copy(alpha = 0.2f) else FnfSurfaceElevated, CircleShape)
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Filled.GraphicEq else Icons.Filled.PlayArrow,
                                contentDescription = "Preview Beat",
                                tint = if (isPlaying) FnfCyan else FnfTextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CharactersTab(characters: List<com.example.data.model.ModCharacter>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "FEATURED CHARACTERS & SINGERS",
            color = FnfPink,
            fontSize = 12.sp,
            fontWeight = FontWeight.Black
        )
        Spacer(modifier = Modifier.height(10.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            characters.forEach { char ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = FnfSurface),
                    border = BorderStroke(1.dp, FnfBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = FnfSurfaceElevated,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = char.iconEmoji,
                                    fontSize = 22.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = char.name,
                                    color = FnfTextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = FnfPurple.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = char.role,
                                        color = FnfPurple,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = char.description,
                                color = FnfTextSecondary,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PersonalTrackerTab(detail: FullModDetail, onEditClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "YOUR PERSONAL LOG & PROGRESS",
                color = FnfYellow,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black
            )

            Button(
                onClick = onEditClick,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = FnfCyan, contentColor = FnfDarkBg)
            ) {
                Text("EDIT LOG", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Progress Card
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = FnfSurface),
            border = BorderStroke(1.dp, FnfBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Stars & Rating
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Your Rating", color = FnfTextSecondary, fontSize = 13.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        for (i in 1..5) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = null,
                                tint = if (detail.userRating >= i) FnfYellow else FnfTextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (detail.userRating > 0) "${detail.userRating.toInt()}/5" else "Not rated",
                            color = FnfTextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // High score
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Personal Best Score", color = FnfTextSecondary, fontSize = 13.sp)
                    Text(
                        text = if (detail.highestScore > 0) String.format("%,d PTS", detail.highestScore) else "No score recorded",
                        color = FnfCyan,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Completed status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Status", color = FnfTextSecondary, fontSize = 13.sp)
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (detail.isCompleted) FnfGreen.copy(alpha = 0.2f) else FnfSurfaceElevated
                    ) {
                        Text(
                            text = if (detail.isCompleted) "✓ COMPLETED ALL WEEKS" else "IN PROGRESS",
                            color = if (detail.isCompleted) FnfGreen else FnfTextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                if (detail.userNotes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text("Your Notes:", color = FnfTextMuted, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = detail.userNotes,
                        color = FnfTextPrimary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun MetaPill(label: String, value: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = FnfSurfaceElevated,
        border = BorderStroke(0.8.dp, FnfBorder)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = label.uppercase(), color = FnfTextMuted, fontSize = 9.sp)
            Text(text = value, color = FnfTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}
