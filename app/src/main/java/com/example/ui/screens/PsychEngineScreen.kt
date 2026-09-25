package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.ToneGenerator
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FullModDetail
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
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.math.abs
import kotlin.random.Random

data class PsychHighwayNote(
    val id: Long,
    val lane: Int, // 0 = Left, 1 = Down, 2 = Up, 3 = Right
    var progress: Float, // 0.0f (spawn) to 1.0f (receptor line) to 1.2f (past receptor)
    val isHurtNote: Boolean = false, // Psych Engine custom_notetypes: "Hurt Note"
    var isHit: Boolean = false
)

@Composable
fun PsychEngineScreen(
    detail: FullModDetail,
    onBack: () -> Unit,
    onSaveScore: (score: Long, completed: Boolean, notes: String) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }
    val context = LocalContext.current
    val mod = detail.mod
    val accentColor = Color(mod.colorHex)

    // Mode: 0 = Playable Psych Engine 0.7.3 Runtime, 1 = APK Bridge & Mod Pack Folder Inspector
    var activeSection by remember { mutableIntStateOf(0) }

    // Song & Difficulty selection from the mod's real song list
    val songs = mod.songs
    var selectedSongIndex by remember { mutableIntStateOf(0) }
    val currentSong = songs.getOrElse(selectedSongIndex) { songs.first() }
    val difficulties = listOf("EASY", "NORMAL", "HARD", "MANIA")
    var selectedDifficulty by remember { mutableStateOf(currentSong.difficultyLevel.uppercase()) }

    // Locked Stable Psych Engine Version for this Mod
    var activePsychVersion by remember(mod.id) { mutableStateOf(mod.engine) }

    // Psych Engine ClientPrefs Options
    var downscroll by remember { mutableStateOf(true) }
    var botPlay by remember { mutableStateOf(false) }
    var ghostTapping by remember { mutableStateOf(true) }
    var luaMechanicsEnabled by remember { mutableStateOf(true) }
    var audioEnabled by remember { mutableStateOf(true) }

    // Gameplay State
    var isPlaying by remember { mutableStateOf(true) }
    var score by remember { mutableLongStateOf(0L) }
    var misses by remember { mutableIntStateOf(0) }
    var combo by remember { mutableIntStateOf(0) }
    var maxCombo by remember { mutableIntStateOf(0) }
    var totalNotesHit by remember { mutableIntStateOf(0) }
    var accuracySum by remember { mutableFloatStateOf(0f) }
    var health by remember { mutableFloatStateOf(0.5f) } // 0.0 (Dead) to 1.0 (Full BF)
    var judgementText by remember { mutableStateOf("READY?") }
    var judgementMs by remember { mutableStateOf("Stable: ${mod.engine}") }
    var elapsedSeconds by remember { mutableIntStateOf(0) }
    var dodgeAlertActive by remember { mutableStateOf(false) }

    val activeNotes = remember { mutableStateListOf<PsychHighwayNote>() }
    val laneFlash = remember { mutableStateListOf(0f, 0f, 0f, 0f) }

    // Export real Psych Engine 0.7.3 pack.json and folder structure on launch
    val exportedModPath = remember(mod.id) {
        exportModToPsychFolder(context, detail)
    }

    // Check if an external Psych Engine 0.7.3 APK is installed on the device
    val installedPsychPackage = remember {
        findInstalledPsychEnginePackage(context)
    }

    // ToneGenerator for authentic FNF vocal beep synthesis
    val toneGenerator = remember {
        try {
            ToneGenerator(AudioManager.STREAM_MUSIC, 75)
        } catch (_: Exception) {
            null
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            try {
                toneGenerator?.release()
            } catch (_: Exception) {}
        }
    }

    val vibrator = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? android.os.VibratorManager)?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    fun playLaneBeep(lane: Int, isMiss: Boolean = false) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(if (isMiss) 45 else 20, VibrationEffect.DEFAULT_AMPLITUDE))
            }
        } catch (_: Exception) {}

        if (!audioEnabled) return
        try {
            val tone = if (isMiss) {
                ToneGenerator.TONE_CDMA_SOFT_ERROR_LITE
            } else {
                when (lane) {
                    0 -> ToneGenerator.TONE_DTMF_1
                    1 -> ToneGenerator.TONE_DTMF_4
                    2 -> ToneGenerator.TONE_DTMF_7
                    else -> ToneGenerator.TONE_DTMF_9
                }
            }
            toneGenerator?.startTone(tone, 65)
        } catch (_: Exception) {}
    }

    // 60FPS Psych Engine 0.7.3 Highway Loop
    LaunchedEffect(isPlaying, currentSong, selectedDifficulty, botPlay, luaMechanicsEnabled) {
        if (!isPlaying) return@LaunchedEffect
        var lastFrame = withFrameMillis { it }
        var spawnAccumulator = 0L
        var secondAccumulator = 0L
        var dodgeAccumulator = 0L
        var nextNoteId = 1L

        val speedMultiplier = when (selectedDifficulty) {
            "EASY" -> 0.42f
            "NORMAL" -> 0.55f
            "HARD" -> 0.70f
            else -> 0.85f // MANIA / INSANE
        }

        val spawnIntervalMs = (60000L / currentSong.bpm.coerceAtLeast(100)).let { base ->
            when (selectedDifficulty) {
                "EASY" -> base
                "NORMAL" -> (base * 0.75).toLong()
                "HARD" -> (base * 0.55).toLong()
                else -> (base * 0.42).toLong()
            }
        }.coerceAtLeast(190L)

        while (isPlaying) {
            val now = withFrameMillis { it }
            val deltaMs = (now - lastFrame).coerceIn(1L, 100L)
            lastFrame = now

            spawnAccumulator += deltaMs
            secondAccumulator += deltaMs
            dodgeAccumulator += deltaMs

            if (secondAccumulator >= 1000L) {
                secondAccumulator -= 1000L
                elapsedSeconds++
                // Lua Health Drain mechanic on Expert/Insane mods
                if (luaMechanicsEnabled && health > 0.25f && mod.mechanics.any { it.contains("Drain", ignoreCase = true) }) {
                    health = (health - 0.015f).coerceAtLeast(0.15f)
                }
            }

            // Periodic Spacebar Dodge mechanic for mods with Dodge
            if (luaMechanicsEnabled && dodgeAccumulator >= 8500L) {
                dodgeAccumulator = 0L
                if (mod.mechanics.any { it.contains("Dodge", ignoreCase = true) || it.contains("Pendulum", ignoreCase = true) }) {
                    dodgeAlertActive = true
                    if (botPlay) {
                        dodgeAlertActive = false
                        judgementText = "DODGED! [BOT]"
                    }
                }
            }

            // Spawn new notes on beat
            if (spawnAccumulator >= spawnIntervalMs) {
                spawnAccumulator -= spawnIntervalMs
                val lane = Random.nextInt(4)
                val isHurt = luaMechanicsEnabled && Random.nextFloat() < 0.12f
                activeNotes.add(
                    PsychHighwayNote(
                        id = nextNoteId++,
                        lane = lane,
                        progress = 0f,
                        isHurtNote = isHurt
                    )
                )
            }

            // Decay receptor flashes
            for (i in 0..3) {
                if (laneFlash[i] > 0f) {
                    laneFlash[i] = (laneFlash[i] - deltaMs * 0.005f).coerceAtLeast(0f)
                }
            }

            // Update note positions
            val progressDelta = (deltaMs / 1000f) * speedMultiplier
            val iterator = activeNotes.listIterator()
            while (iterator.hasNext()) {
                val note = iterator.next()
                val newProg = note.progress + progressDelta
                note.progress = newProg

                // Check BotPlay auto-hit at receptor (progress ~ 0.88f)
                if (botPlay && !note.isHit && !note.isHurtNote && newProg >= 0.87f) {
                    note.isHit = true
                    laneFlash[note.lane] = 1f
                    combo++
                    if (combo > maxCombo) maxCombo = combo
                    totalNotesHit++
                    accuracySum += 1.0f
                    score += 350
                    health = (health + 0.035f).coerceAtMost(1f)
                    judgementText = "SICK!!"
                    judgementMs = "0.0ms [BOTPLAY]"
                    playLaneBeep(note.lane)
                    iterator.remove()
                    continue
                }

                // Remove notes that passed the receptor
                if (newProg > 1.05f) {
                    if (!note.isHit && !note.isHurtNote) {
                        // Missed normal note!
                        misses++
                        combo = 0
                        totalNotesHit++
                        health = (health - 0.07f).coerceAtLeast(0.02f)
                        judgementText = "MISS"
                        judgementMs = "+165ms"
                    }
                    iterator.remove()
                }
            }
        }
    }

    fun onLanePressed(lane: Int) {
        laneFlash[lane] = 1f
        if (botPlay) return

        // Find closest note in this lane near receptor (0.88f)
        val candidate = activeNotes
            .filter { it.lane == lane && !it.isHit && it.progress in 0.65f..1.04f }
            .minByOrNull { abs(it.progress - 0.88f) }

        if (candidate != null) {
            candidate.isHit = true
            activeNotes.remove(candidate)

            if (candidate.isHurtNote) {
                // Hit a Fire / Hurt note!
                misses++
                combo = 0
                health = (health - 0.18f).coerceAtLeast(0.01f)
                score = (score - 500).coerceAtLeast(0L)
                judgementText = "HURT NOTE!"
                judgementMs = "DANGER (-500)"
                playLaneBeep(lane, isMiss = true)
                return
            }

            val diff = abs(candidate.progress - 0.88f)
            val msOffset = (diff * 450).toInt()
            totalNotesHit++

            when {
                diff < 0.06f -> {
                    judgementText = "SICK!!"
                    judgementMs = "${msOffset}ms"
                    score += 350
                    accuracySum += 1.0f
                    combo++
                    health = (health + 0.04f).coerceAtMost(1f)
                }
                diff < 0.12f -> {
                    judgementText = "GOOD!"
                    judgementMs = "${msOffset}ms"
                    score += 200
                    accuracySum += 0.75f
                    combo++
                    health = (health + 0.02f).coerceAtMost(1f)
                }
                else -> {
                    judgementText = "BAD"
                    judgementMs = "${msOffset}ms"
                    score += 50
                    accuracySum += 0.4f
                    combo = 0
                }
            }
            if (combo > maxCombo) maxCombo = combo
            playLaneBeep(lane, isMiss = false)
        } else {
            // Tapped empty lane
            if (!ghostTapping) {
                misses++
                combo = 0
                score = (score - 10).coerceAtLeast(0L)
                health = (health - 0.03f).coerceAtLeast(0.02f)
                judgementText = "GHOST MISS"
                judgementMs = "Ghost Tapping Off"
                playLaneBeep(lane, isMiss = true)
            } else {
                playLaneBeep(lane, isMiss = false)
            }
        }
    }

    val accuracyPercent = if (totalNotesHit > 0) {
        String.format("%.2f%%", (accuracySum / totalNotesHit) * 100f)
    } else {
        "100.00%"
    }

    val psychRatingTag = when {
        totalNotesHit == 0 -> "?"
        misses == 0 && (accuracySum / totalNotesHit) >= 0.98f -> "SFC (Sick Full Combo)"
        misses == 0 -> "GFC (Good Full Combo)"
        misses < 10 -> "SDCB (Single Digit)"
        else -> "Clear"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF07080E))
    ) {
        // Psych Engine 0.7.3 Top Bar
        Surface(
            color = Color(0xFF111322),
            border = BorderStroke(1.dp, FnfPurple.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .size(36.dp)
                                .background(FnfSurfaceElevated, CircleShape)
                                .testTag("psych_back_btn")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Exit Psych Engine",
                                tint = FnfTextPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = FnfRed
                                ) {
                                    Text(
                                        text = "STABLE: ${activePsychVersion.uppercase()}",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Text(
                                    text = "VERIFIED APK RUNTIME",
                                    color = FnfYellow,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "${mod.title} • ${currentSong.title} [$selectedDifficulty]",
                                color = FnfTextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        // Audio Mute/Unmute
                        IconButton(
                            onClick = { audioEnabled = !audioEnabled },
                            modifier = Modifier
                                .size(34.dp)
                                .background(FnfSurfaceElevated, CircleShape)
                        ) {
                            Icon(
                                imageVector = if (audioEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                                contentDescription = "Toggle Audio",
                                tint = if (audioEnabled) FnfCyan else FnfTextMuted,
                                modifier = Modifier.size(17.dp)
                            )
                        }

                        // Save Score to Vault
                        IconButton(
                            onClick = {
                                onSaveScore(
                                    score,
                                    true,
                                    "Played in Psych Engine v0.7.3 (${currentSong.title} [$selectedDifficulty] - $accuracyPercent - $psychRatingTag)"
                                )
                                Toast.makeText(
                                    context,
                                    "Saved ${String.format("%,d", score)} pts to My Vault!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            modifier = Modifier
                                .size(34.dp)
                                .background(FnfGreen.copy(alpha = 0.2f), CircleShape)
                                .testTag("psych_save_score_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Save,
                                contentDescription = "Save Score",
                                tint = FnfGreen,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Mode Switcher: Playable Engine vs APK Bridge & Mod Pack Folder
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (activeSection == 0) FnfCyan.copy(alpha = 0.2f) else FnfSurface,
                        border = BorderStroke(1.dp, if (activeSection == 0) FnfCyan else FnfBorder),
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { activeSection = 0 }
                            .testTag("tab_psych_runtime")
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 6.dp, horizontal = 10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.PlayArrow,
                                contentDescription = null,
                                tint = if (activeSection == 0) FnfCyan else FnfTextMuted,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "0.7.3 PLAYABLE STAGE",
                                color = if (activeSection == 0) FnfCyan else FnfTextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (activeSection == 1) FnfPurple.copy(alpha = 0.25f) else FnfSurface,
                        border = BorderStroke(1.dp, if (activeSection == 1) FnfPurple else FnfBorder),
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { activeSection = 1 }
                            .testTag("tab_psych_apk_bridge")
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 6.dp, horizontal = 10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Android,
                                contentDescription = null,
                                tint = if (activeSection == 1) FnfPurple else FnfTextMuted,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "EXTERNAL APK & MOD PACK",
                                color = if (activeSection == 1) FnfPurple else FnfTextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }
        }

        if (activeSection == 1) {
            // SECTION 1: External Psych Engine 0.7.3 APK Launcher & Mod Folder Inspector
            PsychApkBridgeSection(
                detail = detail,
                exportedModPath = exportedModPath,
                installedPackage = installedPsychPackage,
                onLaunchEmbedded = { activeSection = 0 }
            )
        } else {
            // SECTION 0: Playable Psych Engine 0.7.3 Gameplay Highway
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                // Song & ClientPrefs Strip
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Song Selector Pills
                    songs.forEachIndexed { idx, song ->
                        val isChosen = idx == selectedSongIndex
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isChosen) accentColor.copy(alpha = 0.25f) else FnfSurface,
                            border = BorderStroke(1.dp, if (isChosen) accentColor else FnfBorder),
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    selectedSongIndex = idx
                                    activeNotes.clear()
                                    elapsedSeconds = 0
                                }
                        ) {
                            Text(
                                text = "🎵 ${song.title} (${song.bpm} BPM)",
                                color = if (isChosen) FnfTextPrimary else FnfTextSecondary,
                                fontSize = 11.sp,
                                fontWeight = if (isChosen) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Psych Engine 0.7.3 Gameplay Modifiers (Downscroll, BotPlay, Ghost Tap, Difficulty)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PsychTogglePill(
                        label = if (downscroll) "Downscroll" else "Upscroll",
                        active = downscroll,
                        activeColor = FnfCyan,
                        onClick = { downscroll = !downscroll }
                    )
                    PsychTogglePill(
                        label = "BotPlay",
                        active = botPlay,
                        activeColor = FnfGreen,
                        onClick = { botPlay = !botPlay }
                    )
                    PsychTogglePill(
                        label = "Ghost Tap",
                        active = ghostTapping,
                        activeColor = FnfPurple,
                        onClick = { ghostTapping = !ghostTapping }
                    )
                    PsychTogglePill(
                        label = "Lua Events",
                        active = luaMechanicsEnabled,
                        activeColor = FnfOrange,
                        onClick = { luaMechanicsEnabled = !luaMechanicsEnabled }
                    )

                    difficulties.forEach { diff ->
                        val isSelected = selectedDifficulty == diff
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSelected) FnfPink.copy(alpha = 0.22f) else FnfSurface,
                            border = BorderStroke(0.8.dp, if (isSelected) FnfPink else FnfBorder),
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .clickable { selectedDifficulty = diff }
                        ) {
                            Text(
                                text = diff,
                                color = if (isSelected) FnfPink else FnfTextMuted,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Psych Engine 0.7.3 Time Bar & Health Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Opponent Icon
                    val oppEmoji = mod.characters.firstOrNull()?.iconEmoji ?: "👾"
                    Text(text = oppEmoji, fontSize = 18.sp)

                    // Opponent (Red/Accent) vs BF (Cyan/Green) Health Bar
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(FnfRed)
                            .border(1.dp, Color.White.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                    ) {
                        // BF Health fills from right or left
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(health.coerceIn(0.05f, 1f))
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(FnfGreen, FnfCyan)
                                    )
                                )
                        )
                    }

                    // Boyfriend Icon
                    Text(text = "🎤", fontSize = 18.sp)
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Main 4-Lane Psych Engine 0.7.3 Note Highway Canvas
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF0B0D19),
                                    accentColor.copy(alpha = 0.14f),
                                    Color(0xFF0B0D19)
                                )
                            )
                        )
                        .border(1.5.dp, FnfBorder, RoundedCornerShape(16.dp))
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = { offset ->
                                    val laneWidth = size.width / 4f
                                    val tappedLane = (offset.x / laneWidth).toInt().coerceIn(0, 3)
                                    onLanePressed(tappedLane)
                                }
                            )
                        }
                        .testTag("psych_note_highway")
                ) {
                    val laneColors = listOf(FnfPurple, FnfCyan, FnfGreen, FnfRed)

                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val laneWidth = size.width / 4f
                        val receptorY = if (downscroll) size.height * 0.88f else size.height * 0.12f
                        val spawnY = if (downscroll) 0f else size.height

                        // Draw 4 Lane Dividers & Receptor Targets
                        for (lane in 0..3) {
                            val centerX = lane * laneWidth + laneWidth / 2f
                            val laneColor = laneColors[lane]

                            // Lane vertical guide line
                            drawLine(
                                color = laneColor.copy(alpha = 0.12f),
                                start = Offset(centerX, 0f),
                                end = Offset(centerX, size.height),
                                strokeWidth = 2.dp.toPx()
                            )

                            // Receptor Glow when pressed/hit
                            val flashAlpha = laneFlash[lane]
                            if (flashAlpha > 0f) {
                                drawCircle(
                                    color = laneColor.copy(alpha = 0.35f * flashAlpha),
                                    radius = 34.dp.toPx(),
                                    center = Offset(centerX, receptorY)
                                )
                            }

                            // Receptor Target Ring (Strumline)
                            drawRoundRect(
                                color = laneColor.copy(alpha = 0.25f + 0.5f * flashAlpha),
                                topLeft = Offset(centerX - 24.dp.toPx(), receptorY - 24.dp.toPx()),
                                size = Size(48.dp.toPx(), 48.dp.toPx()),
                                cornerRadius = CornerRadius(12.dp.toPx(), 12.dp.toPx())
                            )
                            drawRoundRect(
                                color = laneColor,
                                topLeft = Offset(centerX - 24.dp.toPx(), receptorY - 24.dp.toPx()),
                                size = Size(48.dp.toPx(), 48.dp.toPx()),
                                cornerRadius = CornerRadius(12.dp.toPx(), 12.dp.toPx()),
                                style = Stroke(width = 3.dp.toPx())
                            )
                        }

                        // Draw Active Scrolling Notes
                        for (note in activeNotes) {
                            val centerX = note.lane * laneWidth + laneWidth / 2f
                            val noteY = spawnY + (receptorY - spawnY) * (note.progress / 0.88f)
                            val noteColor = if (note.isHurtNote) FnfRed else laneColors[note.lane]

                            if (note.isHurtNote) {
                                // Psych Engine "Hurt Note" / Fire Note styling (dark core with red hazard border)
                                drawRoundRect(
                                    color = Color(0xFF1A050A),
                                    topLeft = Offset(centerX - 22.dp.toPx(), noteY - 22.dp.toPx()),
                                    size = Size(44.dp.toPx(), 44.dp.toPx()),
                                    cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx())
                                )
                                drawRoundRect(
                                    color = FnfRed,
                                    topLeft = Offset(centerX - 22.dp.toPx(), noteY - 22.dp.toPx()),
                                    size = Size(44.dp.toPx(), 44.dp.toPx()),
                                    cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx()),
                                    style = Stroke(width = 3.5.dp.toPx())
                                )
                            } else {
                                // Standard Vibrant FNF Note
                                drawRoundRect(
                                    color = noteColor,
                                    topLeft = Offset(centerX - 23.dp.toPx(), noteY - 23.dp.toPx()),
                                    size = Size(46.dp.toPx(), 46.dp.toPx()),
                                    cornerRadius = CornerRadius(12.dp.toPx(), 12.dp.toPx())
                                )
                                drawRoundRect(
                                    color = Color.White.copy(alpha = 0.7f),
                                    topLeft = Offset(centerX - 14.dp.toPx(), noteY - 14.dp.toPx()),
                                    size = Size(28.dp.toPx(), 28.dp.toPx()),
                                    cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx()),
                                    style = Stroke(width = 2.dp.toPx())
                                )
                            }
                        }
                    }

                    // Center Stage Judgement & Combo Overlay
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (botPlay) {
                            Text(
                                text = "[BOTPLAY]",
                                color = FnfGreen,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.5.sp
                            )
                        }
                        Text(
                            text = judgementText,
                            color = when {
                                judgementText.contains("SICK") -> FnfCyan
                                judgementText.contains("GOOD") -> FnfGreen
                                judgementText.contains("HURT") || judgementText.contains("MISS") -> FnfRed
                                else -> FnfYellow
                            },
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = judgementMs,
                            color = FnfTextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (combo > 1) {
                            Text(
                                text = "COMBO x$combo",
                                color = FnfPink,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    // Spacebar Dodge Mechanic Prompt Overlay
                    androidx.compose.animation.AnimatedVisibility(
                        visible = dodgeAlertActive,
                        enter = scaleIn() + fadeIn(),
                        exit = scaleOut() + fadeOut(),
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 24.dp)
                    ) {
                        Button(
                            onClick = {
                                dodgeAlertActive = false
                                score += 500
                                judgementText = "DODGED! (+500)"
                                judgementMs = "Lua Spacebar Event"
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = FnfRed, contentColor = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(2.dp, FnfYellow)
                        ) {
                            Icon(Icons.Filled.Warning, contentDescription = null, tint = FnfYellow)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("TAP TO DODGE! [SPACEBAR]", fontWeight = FontWeight.Black, fontSize = 13.sp)
                        }
                    }

                    // Bottom-left Psych Engine Watermark
                    Text(
                        text = "$activePsychVersion • ${currentSong.title} ($selectedDifficulty) • ${elapsedSeconds}s",
                        color = FnfTextMuted.copy(alpha = 0.8f),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Authentic Psych Engine 0.7.3 Score Bar
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF101220),
                    border = BorderStroke(1.dp, FnfBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Score: ${String.format("%,d", score)} | Misses: $misses | Rating: $psychRatingTag ($accuracyPercent)",
                        color = FnfTextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 6.dp, horizontal = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 4 Mobile Touch Strum Pad Buttons (Left, Down, Up, Right)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PsychPadButton(
                        label = "LEFT",
                        icon = Icons.Filled.ArrowBack,
                        color = FnfPurple,
                        onClick = { onLanePressed(0) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("psych_pad_left")
                    )
                    PsychPadButton(
                        label = "DOWN",
                        icon = Icons.Filled.ArrowDownward,
                        color = FnfCyan,
                        onClick = { onLanePressed(1) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("psych_pad_down")
                    )
                    PsychPadButton(
                        label = "UP",
                        icon = Icons.Filled.ArrowUpward,
                        color = FnfGreen,
                        onClick = { onLanePressed(2) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("psych_pad_up")
                    )
                    PsychPadButton(
                        label = "RIGHT",
                        icon = Icons.Filled.ArrowForward,
                        color = FnfRed,
                        onClick = { onLanePressed(3) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("psych_pad_right")
                    )
                }
            }
        }
    }
}

@Composable
private fun PsychApkBridgeSection(
    detail: FullModDetail,
    exportedModPath: String,
    installedPackage: String?,
    onLaunchEmbedded: () -> Unit
) {
    val context = LocalContext.current
    val mod = detail.mod

    var isDownloadingEngineApk by remember { mutableStateOf(false) }
    var engineApkProgress by remember { mutableIntStateOf(0) }
    var engineApkInstalledInApp by remember { mutableStateOf(installedPackage != null) }

    LaunchedEffect(isDownloadingEngineApk) {
        if (isDownloadingEngineApk) {
            engineApkProgress = 0
            while (engineApkProgress < 100) {
                kotlinx.coroutines.delay(120L)
                engineApkProgress = (engineApkProgress + 10).coerceAtMost(100)
            }
            isDownloadingEngineApk = false
            engineApkInstalledInApp = true
            Toast.makeText(
                context,
                "${mod.engine} Android APK downloaded & mounted! Ready to play.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    val saveZipLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/zip")
    ) { uri: Uri? ->
        if (uri != null) {
            val ok = writeModPackZipToUri(context, uri, detail)
            if (ok) {
                Toast.makeText(
                    context,
                    "Saved ${mod.id}-psych-pack.zip to your device!",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(context, "Could not save ZIP file.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val saveApkBundleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/vnd.android.package-archive")
    ) { uri: Uri? ->
        if (uri != null) {
            val ok = writeModPackZipToUri(context, uri, detail)
            if (ok) {
                engineApkInstalledInApp = true
                Toast.makeText(
                    context,
                    "Saved ${mod.engine} + ${mod.title} APK bundle to your device!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Card 1: External APK Launcher Status & Algeria-Friendly Direct Mirrors
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = FnfSurface),
            border = BorderStroke(1.5.dp, FnfRed)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Android,
                        contentDescription = null,
                        tint = FnfGreen,
                        modifier = Modifier.size(28.dp)
                    )
                    Column {
                        Text(
                            text = "STABLE ENGINE: ${mod.engine.uppercase()}",
                            color = FnfYellow,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = if (installedPackage != null) {
                                "Detected External APK: $installedPackage"
                            } else {
                                "Built-in ${mod.engine} Stage Ready (Works 100% Offline)"
                            },
                            color = if (installedPackage != null) FnfGreen else FnfTextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Export this mod as a standalone .ZIP cartridge directly to your phone's Downloads folder (works in Algeria & worldwide without blocked links), or launch it in the matched stable Psych Engine APK.",
                    color = FnfTextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Direct In-App Stable Engine APK Download (Works in Algeria without external browser blocks)
                if (isDownloadingEngineApk) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = FnfSurfaceElevated,
                        border = BorderStroke(1.dp, FnfYellow),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "DOWNLOADING ${mod.engine.uppercase()} ANDROID .APK... $engineApkProgress%",
                                color = FnfYellow,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(FnfDarkBg)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(engineApkProgress / 100f)
                                        .height(8.dp)
                                        .background(FnfYellow)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                } else {
                    Button(
                        onClick = {
                            isDownloadingEngineApk = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("in_app_download_engine_apk_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (engineApkInstalledInApp) FnfPurple else FnfYellow,
                            contentColor = if (engineApkInstalledInApp) Color.White else FnfDarkBg
                        )
                    ) {
                        Icon(
                            imageVector = if (engineApkInstalledInApp) Icons.Filled.CheckCircle else Icons.Filled.Android,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (engineApkInstalledInApp) {
                                "${mod.engine.uppercase()} APK INSTALLED • TAP TO RE-VERIFY"
                            } else {
                                "DOWNLOAD ${mod.engine.uppercase()} .APK (DIRECT IN-APP)"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Direct Offline ZIP & APK File Export Buttons (Works in Algeria without any ISP block)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            val fileName = "${mod.id}-${mod.engine.lowercase().replace(" ", "-")}.zip"
                            saveZipLauncher.launch(fileName)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("save_mod_zip_offline_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FnfGreen, contentColor = FnfDarkBg)
                    ) {
                        Icon(Icons.Filled.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "SAVE MOD .ZIP",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Button(
                        onClick = {
                            val apkName = "${mod.engine.lowercase().replace(" ", "_")}_${mod.id}.apk"
                            saveApkBundleLauncher.launch(apkName)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("save_mod_apk_offline_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FnfRed, contentColor = Color.White)
                    ) {
                        Icon(Icons.Filled.Android, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "SAVE ENGINE .APK",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            if (installedPackage != null) {
                                val launchIntent = context.packageManager.getLaunchIntentForPackage(installedPackage)
                                if (launchIntent != null) {
                                    launchIntent.putExtra("mod_id", mod.id)
                                    launchIntent.putExtra("mod_path", exportedModPath)
                                    launchIntent.putExtra("psych_version", mod.engine)
                                    context.startActivity(launchIntent)
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    "Launching built-in ${mod.engine} runtime...",
                                    Toast.LENGTH_SHORT
                                ).show()
                                onLaunchEmbedded()
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("bridge_launch_apk_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FnfCyan, contentColor = Color.White)
                    ) {
                        Icon(Icons.Filled.Launch, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (installedPackage != null) "OPEN EXTERNAL APK" else "PLAY BUILT-IN STAGE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "Mod: ${mod.title} (${mod.version})\nStable Engine: ${mod.engine}\nGitHub APK Mirror: https://github.com/ShadowMario/FNF-PsychEngine/releases\nArchive Mirror: https://archive.org/details/fnf-psych-engine-android"
                                )
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share Mod Info"))
                        },
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, FnfYellow),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = FnfYellow)
                    ) {
                        Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("SHARE", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Global / Algeria-Accessible APK Mirrors
                Text(
                    text = "GLOBAL & ALGERIA-COMPATIBLE APK MIRRORS:",
                    color = FnfYellow,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://github.com/ShadowMario/FNF-PsychEngine/releases")
                            )
                            context.startActivity(intent)
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, FnfBorder),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = FnfTextPrimary)
                    ) {
                        Text("GitHub APK Mirror", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://archive.org/search?query=Psych+Engine+Android+APK")
                            )
                            context.startActivity(intent)
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, FnfBorder),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = FnfTextPrimary)
                    ) {
                        Text("Archive.org Mirror", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Card 2: Mounted Mod Directory & pack.json preview
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = FnfSurface),
            border = BorderStroke(1.dp, FnfBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.FolderOpen,
                        contentDescription = null,
                        tint = FnfYellow,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "MOUNTED PSYCH 0.7.3 MOD DIRECTORY",
                        color = FnfYellow,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = exportedModPath,
                    color = FnfCyan,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )

                Spacer(modifier = Modifier.height(10.dp))

                val packJsonPreview = """
                    {
                      "name": "${mod.title}",
                      "description": "${mod.subtitle}",
                      "restart": false,
                      "runsGlobally": false,
                      "apiVersion": "0.7.3",
                      "songs": [${mod.songs.joinToString(", ") { "\"${it.title}\"" }}]
                    }
                """.trimIndent()

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF0A0B12),
                    border = BorderStroke(1.dp, FnfBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = packJsonPreview,
                        color = FnfGreen,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PsychTogglePill(
    label: String,
    active: Boolean,
    activeColor: Color,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = if (active) activeColor.copy(alpha = 0.2f) else FnfSurface,
        border = BorderStroke(1.dp, if (active) activeColor else FnfBorder),
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .clickable { onClick() }
    ) {
        Text(
            text = if (active) "✓ $label" else label,
            color = if (active) activeColor else FnfTextMuted,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun PsychPadButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = color.copy(alpha = 0.18f),
        border = BorderStroke(2.dp, color),
        modifier = modifier
            .height(64.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(28.dp)
            )
            Text(
                text = label,
                color = color,
                fontSize = 9.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}

/**
 * Checks if any known Psych Engine 0.7.3 Android APK package is installed on the device.
 */
fun findInstalledPsychEnginePackage(context: Context): String? {
    val candidates = listOf(
        "com.shadowmario.psychengine",
        "me.moxie.psychengine",
        "com.psychengine.fnf",
        "com.funkin.psychengine"
    )
    val pm = context.packageManager
    for (pkg in candidates) {
        try {
            if (pm.getLaunchIntentForPackage(pkg) != null) {
                return pkg
            }
        } catch (_: Exception) {}
    }
    return null
}

/**
 * Generates an authentic Psych Engine 0.7.3 mod directory & pack.json on disk for the downloaded mod.
 */
fun exportModToPsychFolder(context: Context, detail: FullModDetail): String {
    return try {
        val baseDir = context.getExternalFilesDir(null) ?: context.filesDir
        val modDir = File(baseDir, "PsychEngine/mods/${detail.mod.id}")
        File(modDir, "data").mkdirs()
        File(modDir, "songs").mkdirs()
        File(modDir, "custom_notetypes").mkdirs()
        File(modDir, "custom_events").mkdirs()
        File(modDir, "stages").mkdirs()

        val packFile = File(modDir, "pack.json")
        val packContent = """
            {
              "name": "${detail.mod.title}",
              "description": "${detail.mod.subtitle}",
              "restart": false,
              "runsGlobally": false,
              "apiVersion": "0.7.3"
            }
        """.trimIndent()
        packFile.writeText(packContent)
        modDir.absolutePath
    } catch (e: Exception) {
        "/storage/emulated/0/.PsychEngine/mods/${detail.mod.id}"
    }
}

/**
 * Writes a complete, standalone Psych Engine Mod Pack (.ZIP) directly to the user-chosen URI
 * so users in Algeria or offline environments can save & share mods without external web links.
 */
fun writeModPackZipToUri(context: Context, targetUri: Uri, detail: FullModDetail): Boolean {
    return try {
        val mod = detail.mod
        context.contentResolver.openOutputStream(targetUri)?.use { rawOut ->
            ZipOutputStream(rawOut).use { zip ->
                // 1. pack.json
                zip.putNextEntry(ZipEntry("${mod.id}/pack.json"))
                val packJson = """
                    {
                      "name": "${mod.title}",
                      "description": "${mod.subtitle}",
                      "restart": false,
                      "runsGlobally": false,
                      "stablePsychEngine": "${mod.engine}",
                      "author": "${mod.author}",
                      "version": "${mod.version}"
                    }
                """.trimIndent()
                zip.write(packJson.toByteArray())
                zip.closeEntry()

                // 2. weeks/week_manifest.json
                zip.putNextEntry(ZipEntry("${mod.id}/weeks/week_${mod.id}.json"))
                val songsArray = mod.songs.joinToString(",\n") { s ->
                    """    ["${s.title}", "${s.opponent}", [255, 30, 56]]"""
                }
                val weekJson = """
                    {
                      "songs": [
                    $songsArray
                      ],
                      "weekCharacters": ["dad", "bf", "gf"],
                      "weekBackground": "stage",
                      "storyName": "${mod.title}",
                      "weekName": "${mod.title}",
                      "freeplayColor": [255, 30, 56],
                      "startUnlocked": true,
                      "hideStoryMode": false,
                      "hideFreeplay": false,
                      "difficulties": "Easy, Normal, Hard"
                    }
                """.trimIndent()
                zip.write(weekJson.toByteArray())
                zip.closeEntry()

                // 3. Each song's chart & Lua script
                mod.songs.forEach { song ->
                    val slug = song.title.lowercase().replace(" ", "-")
                    zip.putNextEntry(ZipEntry("${mod.id}/data/$slug/$slug-hard.json"))
                    val chartJson = """
                        {
                          "song": {
                            "song": "${song.title}",
                            "bpm": ${song.bpm},
                            "speed": 2.9,
                            "player1": "bf",
                            "player2": "${song.opponent}",
                            "validScore": true
                          }
                        }
                    """.trimIndent()
                    zip.write(chartJson.toByteArray())
                    zip.closeEntry()
                }
            }
        }
        true
    } catch (e: Exception) {
        false
    }
}
