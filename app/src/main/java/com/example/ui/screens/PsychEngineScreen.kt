package com.example.ui.screens

import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.FullModDetail
import com.example.data.psych.MarioMadness3DModelEngine
import com.example.data.psych.MarioMadnessAudioEngine
import com.example.data.psych.Psych073ModBuilder
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
import kotlin.math.abs
import kotlin.random.Random
import kotlinx.coroutines.launch

data class PsychHighwayNote(
    val id: Long,
    val lane: Int, // 0 = Left, 1 = Down, 2 = Up, 3 = Right
    var progress: Float, // 0.0f (spawn) to 1.0f (receptor line) to 1.2f (past receptor)
    val isHurtNote: Boolean = false, // Fire Mario / Poison Mushroom Note
    val isStarmanNote: Boolean = false, // Golden Starman Note (Collect 3 to unlock Ending 3: Secret Exit!)
    var isHit: Boolean = false
)

data class EndingCutsceneData(
    val id: Int, // 1 = Bad Ending, 2 = Warp Pipe Escape, 3 = Secret Exit True Ending
    val badge: String,
    val title: String,
    val subtitle: String,
    val conditionText: String,
    val bannerResId: Int,
    val accentColor: Color,
    val musicMotifKey: String,
    val dialogueLines: List<Pair<String, String>>
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
    val coroutineScope = rememberCoroutineScope()
    val mod = detail.mod
    val accentColor = Color(mod.colorHex)

    // Mode: 0 = Playable Mario's Madness V2 5-Act Stage & 3 Endings, 1 = APK Bridge & ZIP Export, 2 = Lua & Chart Studio
    var activeSection by remember { mutableIntStateOf(0) }

    // Song & Difficulty selection from the mod's real song list
    val songs = mod.songs
    var selectedSongIndex by remember { mutableIntStateOf(0) }
    val currentSong = songs.getOrElse(selectedSongIndex) { songs.first() }
    val difficulties = listOf("EASY", "NORMAL", "HARD", "MANIA")
    var selectedDifficulty by remember { mutableStateOf(currentSong.difficultyLevel.uppercase()) }

    val activePsychVersion by remember(mod.id) { mutableStateOf(mod.engine) }

    // Psych Engine ClientPrefs Options
    var downscroll by remember { mutableStateOf(true) }
    var botPlay by remember { mutableStateOf(false) }
    var ghostTapping by remember { mutableStateOf(true) }
    var luaMechanicsEnabled by remember { mutableStateOf(true) }
    var audioEnabled by remember { mutableStateOf(true) }

    // 5-Act Progression & 3 Branching Endings State
    var currentAct by remember { mutableIntStateOf(1) } // Act 1..5
    var starmanStars by remember { mutableIntStateOf(0) } // Collect 3 Golden Starman Notes for Ending 3
    var activeEndingCutscene by remember { mutableIntStateOf(0) } // 0 = None, 1 = Bad, 2 = Escape, 3 = True Secret Exit
    var endingDialogueIndex by remember { mutableIntStateOf(0) }
    var bfPoseText by remember { mutableStateOf("IDLE") }
    var bossPoseText by remember { mutableStateOf("ULTRA M") }
    var animTick by remember { mutableIntStateOf(0) }
    var opponentPoseIndex by remember { mutableIntStateOf(0) }
    var bfPoseIndex by remember { mutableIntStateOf(0) }

    // Gameplay State
    var isPlaying by remember { mutableStateOf(true) }
    var score by remember { mutableLongStateOf(0L) }
    var misses by remember { mutableIntStateOf(0) }
    var combo by remember { mutableIntStateOf(0) }
    var maxCombo by remember { mutableIntStateOf(0) }
    var totalNotesHit by remember { mutableIntStateOf(0) }
    var accuracySum by remember { mutableFloatStateOf(0f) }
    var health by remember { mutableFloatStateOf(0.6f) }
    var judgementText by remember { mutableStateOf("ACT I: ULTRA M") }
    var judgementMs by remember { mutableStateOf("Collect 3 ★ Starman Notes for Secret Exit!") }
    var elapsedSeconds by remember { mutableIntStateOf(0) }
    var dodgeAlertActive by remember { mutableStateOf(false) }

    val activeNotes = remember { mutableStateListOf<PsychHighwayNote>() }
    val laneFlash = remember { mutableStateListOf(0f, 0f, 0f, 0f) }

    // 3 Branching Endings Catalog (Mario's Madness V2 #359554 + Secret Exit)
    val endingsCatalog = remember {
        listOf(
            EndingCutsceneData(
                id = 1,
                badge = "ENDING 1 OF 3 • CANON BAD ENDING",
                title = "ALL-STARS: \"SEE YOU NEXT TIME\"",
                subtitle = "Ultra M traps Boyfriend & Girlfriend inside the cursed NES cartridge forever",
                conditionText = "Triggered when Starman Notes < 3 & Low Health / High Misses",
                bannerResId = R.drawable.img_mmv2_ending_bad_1790591028072,
                accentColor = FnfRed,
                musicMotifKey = "ending-bad",
                dialogueLines = listOf(
                    "ULTRA M" to "\"You fought fiercely through my five worlds, little blue boy...\"",
                    "ULTRA M" to "\"From Horror Mario to Mr. Virtual and MX, you sang every last note. Yet without the 3 Golden Starman keys, my castle remains sealed.\"",
                    "GIRLFRIEND" to "\"Boyfriend, watch out! Crimson chains are rising from the cartridge floor!\"",
                    "ULTRA M" to "\"Come now, take the step. Don't look back—there's nothing left for you beyond the veil... SEE YOU NEXT TIME.\""
                )
            ),
            EndingCutsceneData(
                id = 2,
                badge = "ENDING 2 OF 3 • BITTERSWEET ESCAPE ENDING",
                title = "SHATTERED CRT: \"OVERDUE WARP PIPE\"",
                subtitle = "Pico & Beta Luigi hold off MX & Ultra M while BF & GF leap out of the TV!",
                conditionText = "Triggered when surviving Act 5 with ≥50% Health but < 3 Starman Notes",
                bannerResId = R.drawable.mmv2_ending_escape_1790592490896,
                accentColor = FnfGreen,
                musicMotifKey = "ending-escape",
                dialogueLines = listOf(
                    "BETA LUIGI" to "\"Boyfriend! Over here! We pried open a green Warp Pipe behind the Citadel throne!\"",
                    "PICO" to "\"Go! I'll hold back MX and Ultra M's tentacles with my blaster—jump through the static before the screen collapses!\"",
                    "BOYFRIEND" to "\"We're not leaving you two behind in this cartridge!\"",
                    "BETA LUIGI" to "\"Our code belongs to the NES now... Smash the cartridge once you're outside! GO!\"",
                    "NARRATOR" to "The living room CRT television explodes in emerald sparks! BF & GF tumble onto the carpet alive and shatter the cursed cartridge—remembering Luigi & Pico's sacrifice."
                )
            ),
            EndingCutsceneData(
                id = 3,
                badge = "ENDING 3 OF 3 • SECRET EXIT TRUE ENDING",
                title = "GOLDEN STARMAN: \"SECRET EXIT LIBERATION\"",
                subtitle = "Starman BF & GF shatter Ultra M's curse and free Luigi, Peach, Yoshi & Pico!",
                conditionText = "Triggered by collecting 3+ Golden Starman Notes (★) across Acts 1–5!",
                bannerResId = R.drawable.img_mmv2_ending_true_1790591037564,
                accentColor = FnfYellow,
                musicMotifKey = "ending-true",
                dialogueLines = listOf(
                    "STARMAN BF & GF" to "\"3 GOLDEN STARMAN NOTES COLLECTED! Invincibility harmony at 100%—igniting the Super Star Vocal Beam!\"",
                    "ULTRA M" to "\"WHAT?! Where did you find the third Starman Note?! My crimson code is burning away!\"",
                    "PRINCESS PEACH & LUIGI" to "\"The corruption is lifting! The Golden Secret Exit Keyhole is opening above the castle bridge!\"",
                    "PICO & YOSHI" to "\"Everyone through the Goal Tape! Ultra M's citadel is collapsing into pure 8-bit stardust!\"",
                    "NARRATOR" to "TRUE ENDING UNLOCKED! Boyfriend, Girlfriend, Luigi, Peach, Yoshi, and Pico cross the Secret Exit Goal Tape together as the Mushroom Kingdom is restored to peace!"
                )
            )
        )
    }

    val actMetadata = remember {
        listOf(
            Triple(1, "ACT I: CORRUPTED CITADEL", "Ultra M & Horror Mario"),
            Triple(2, "ACT II: PARANOIA MIRAGE", "Mr. Virtual & DJ Hallyboo"),
            Triple(3, "ACT III: LAVA PIPE AMBUSH", "MX & Turmoil"),
            Triple(4, "ACT IV: WARP PIPE ASSIST", "Pico, Beta Luigi & Mr. Sys"),
            Triple(5, "ACT V: SECRET EXIT FINALE", "Starman BF/GF vs Giant Ultra M")
        )
    }

    // Export real Psych Engine 0.7.3 pack.json, PNGs, and folder structure on launch
    val exportedModPath = remember(mod.id) {
        exportModToPsychFolder(context, detail)
    }

    val installedPsychPackage = remember {
        findInstalledPsychEnginePackage(context)
    }

    val vibrator = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? android.os.VibratorManager)?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    // Start real-time Mario's Madness V2 background synthesizer when on Stage or in Ending Cutscene
    LaunchedEffect(activeSection, isPlaying, currentSong, activeEndingCutscene, audioEnabled) {
        if (activeSection != 0 || !audioEnabled) {
            MarioMadnessAudioEngine.stopStageMusic()
            return@LaunchedEffect
        }
        if (activeEndingCutscene in 1..3) {
            val endingData = endingsCatalog[activeEndingCutscene - 1]
            val endingBpm = when (activeEndingCutscene) {
                1 -> 112
                2 -> 168
                else -> 185
            }
            MarioMadnessAudioEngine.startStageMusic(
                scope = this,
                songTitle = endingData.musicMotifKey,
                bpm = endingBpm,
                getAct = { if (activeEndingCutscene == 3) 5 else 1 },
                isAudioEnabled = { audioEnabled }
            )
        } else if (isPlaying) {
            MarioMadnessAudioEngine.startStageMusic(
                scope = this,
                songTitle = currentSong.title,
                bpm = currentSong.bpm,
                getAct = { currentAct },
                isAudioEnabled = { audioEnabled }
            )
        } else {
            MarioMadnessAudioEngine.stopStageMusic()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            MarioMadnessAudioEngine.stopStageMusic()
        }
    }

    fun triggerEndingEvaluation() {
        val earnedEnding = when {
            starmanStars >= 3 -> 3 // Ending 3: Secret Exit True Ending
            health >= 0.45f && misses <= 15 -> 2 // Ending 2: Warp Pipe Escape
            else -> 1 // Ending 1: Canon Bad Ending
        }
        activeEndingCutscene = earnedEnding
        endingDialogueIndex = 0
    }

    fun playNoteFeedback(lane: Int, isStarman: Boolean = false, isMissOrHurt: Boolean = false) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(
                    VibrationEffect.createOneShot(
                        if (isMissOrHurt) 45 else if (isStarman) 35 else 18,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            }
        } catch (_: Exception) {}

        if (!audioEnabled) return
        MarioMadnessAudioEngine.playVocalNoteBurst(
            scope = coroutineScope,
            lane = lane,
            isStarman = isStarman,
            isMissOrHurt = isMissOrHurt
        )
    }

    // 60FPS Mario's Madness V2 Psych Engine 0.7.3 Highway Loop
    LaunchedEffect(isPlaying, currentSong, selectedDifficulty, botPlay, luaMechanicsEnabled, activeEndingCutscene, activeSection) {
        if (!isPlaying || activeSection != 0 || activeEndingCutscene != 0) return@LaunchedEffect
        var lastFrame = withFrameMillis { it }
        var spawnAccumulator = 0L
        var secondAccumulator = 0L
        var dodgeAccumulator = 0L
        var nextNoteId = 1L

        val speedMultiplier = when (selectedDifficulty) {
            "EASY" -> 0.44f
            "NORMAL" -> 0.56f
            "HARD" -> 0.70f
            else -> 0.84f
        }

        val spawnIntervalMs = (60000L / currentSong.bpm.coerceAtLeast(100)).let { base ->
            when (selectedDifficulty) {
                "EASY" -> base
                "NORMAL" -> (base * 0.75).toLong()
                "HARD" -> (base * 0.55).toLong()
                else -> (base * 0.42).toLong()
            }
        }.coerceAtLeast(190L)

        while (isPlaying && activeEndingCutscene == 0) {
            val now = withFrameMillis { it }
            val deltaMs = (now - lastFrame).coerceIn(1L, 100L)
            lastFrame = now

            spawnAccumulator += deltaMs
            secondAccumulator += deltaMs
            dodgeAccumulator += deltaMs
            animTick++

            if (secondAccumulator >= 1000L) {
                secondAccumulator -= 1000L
                elapsedSeconds++

                // Auto-advance through the 5 Acts of Secret Exit / Mario's Madness V2 every 14 seconds
                val computedAct = ((elapsedSeconds / 14) + 1).coerceIn(1, 5)
                if (computedAct != currentAct) {
                    currentAct = computedAct
                    val actInfo = actMetadata[currentAct - 1]
                    judgementText = actInfo.first.let { "ACT $it" }
                    judgementMs = "${actInfo.second} (${actInfo.third})"
                    bossPoseText = actInfo.third.uppercase()
                }

                // Trigger the earned Ending Cutscene automatically after completing Act 5 (70s)
                if (elapsedSeconds >= 70) {
                    triggerEndingEvaluation()
                    break
                }

                // Ultra M Lua Health Drain during Acts 1-3 (disabled once 3 Starman Stars are collected!)
                if (luaMechanicsEnabled && starmanStars < 3 && health > 0.24f) {
                    health = (health - 0.014f).coerceAtLeast(0.15f)
                }
            }

            // Periodic Ultra M Lava Pipe Dodge Event
            if (luaMechanicsEnabled && dodgeAccumulator >= 8500L) {
                dodgeAccumulator = 0L
                dodgeAlertActive = true
                if (botPlay) {
                    dodgeAlertActive = false
                    judgementText = "PIPE DODGED! [BOT]"
                }
            }

            // Spawn new notes on beat (Normal notes, Fire Hurt Notes, and Golden Starman Notes)
            if (spawnAccumulator >= spawnIntervalMs) {
                spawnAccumulator -= spawnIntervalMs
                val lane = Random.nextInt(4)
                opponentPoseIndex = lane + 1
                val roll = Random.nextFloat()
                val isStarman = roll < 0.14f // 14% chance of Golden Starman Note (★)
                val isHurt = !isStarman && luaMechanicsEnabled && roll > 0.88f // 12% chance of Fire Hurt Note
                activeNotes.add(
                    PsychHighwayNote(
                        id = nextNoteId++,
                        lane = lane,
                        progress = 0f,
                        isHurtNote = isHurt,
                        isStarmanNote = isStarman
                    )
                )
            }

            for (i in 0..3) {
                if (laneFlash[i] > 0f) {
                    laneFlash[i] = (laneFlash[i] - deltaMs * 0.005f).coerceAtLeast(0f)
                }
            }

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
                    bfPoseIndex = note.lane + 1
                    combo++
                    if (combo > maxCombo) maxCombo = combo
                    totalNotesHit++
                    accuracySum += 1.0f
                    if (note.isStarmanNote) {
                        starmanStars++
                        score += 1500
                        health = (health + 0.18f).coerceAtMost(1f)
                        judgementText = "★ STARMAN! ($starmanStars/3)"
                        judgementMs = if (starmanStars >= 3) "SECRET EXIT UNLOCKED!" else "+1500 PTS [BOT]"
                        bfPoseText = "★ STARMAN!"
                        playNoteFeedback(note.lane, isStarman = true)
                    } else {
                        score += 350
                        health = (health + 0.035f).coerceAtMost(1f)
                        judgementText = "SICK!!"
                        judgementMs = "0.0ms [BOTPLAY]"
                        bfPoseText = listOf("LEFT", "DOWN", "UP", "RIGHT")[note.lane]
                        playNoteFeedback(note.lane)
                    }
                    iterator.remove()
                    continue
                }

                // Remove notes that passed the receptor
                if (newProg > 1.05f) {
                    if (!note.isHit && !note.isHurtNote && !note.isStarmanNote) {
                        misses++
                        combo = 0
                        totalNotesHit++
                        health = (health - 0.07f).coerceAtLeast(0.02f)
                        judgementText = "MISS"
                        judgementMs = "+165ms"
                        bfPoseText = "MISS!"
                    }
                    iterator.remove()
                }
            }
        }
    }

    fun onLanePressed(lane: Int) {
        laneFlash[lane] = 1f
        bfPoseIndex = lane + 1
        bfPoseText = listOf(" SING LEFT", "SING DOWN", "SING UP", "SING RIGHT")[lane]
        if (botPlay) return

        val candidate = activeNotes
            .filter { it.lane == lane && !it.isHit && it.progress in 0.64f..1.05f }
            .minByOrNull { abs(it.progress - 0.88f) }

        if (candidate != null) {
            candidate.isHit = true
            activeNotes.remove(candidate)

            if (candidate.isHurtNote) {
                misses++
                combo = 0
                health = (health - 0.18f).coerceAtLeast(0.01f)
                score = (score - 500).coerceAtLeast(0L)
                judgementText = "🔥 FIRE HURT NOTE!"
                judgementMs = "DANGER (-500)"
                bfPoseText = "BURNED!"
                playNoteFeedback(lane, isMissOrHurt = true)
                return
            }

            if (candidate.isStarmanNote) {
                starmanStars++
                combo++
                if (combo > maxCombo) maxCombo = combo
                totalNotesHit++
                accuracySum += 1.0f
                score += 1500
                health = (health + 0.22f).coerceAtMost(1f)
                judgementText = "★ STARMAN ($starmanStars/3)!"
                judgementMs = if (starmanStars >= 3) "ENDING 3 (SECRET EXIT) READY!" else "Collect ${3 - starmanStars} more for Secret Exit!"
                bfPoseText = "★ STARMAN POWER!"
                playNoteFeedback(lane, isStarman = true)
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
            playNoteFeedback(lane, isMissOrHurt = false)
        } else {
            if (!ghostTapping) {
                misses++
                combo = 0
                score = (score - 10).coerceAtLeast(0L)
                health = (health - 0.03f).coerceAtLeast(0.02f)
                judgementText = "GHOST MISS"
                judgementMs = "Ghost Tapping Off"
                playNoteFeedback(lane, isMissOrHurt = true)
            } else {
                playNoteFeedback(lane, isMissOrHurt = false)
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
            border = BorderStroke(1.dp, FnfRed.copy(alpha = 0.55f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
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
                                        text = "MMV2 #359554 • ${activePsychVersion.uppercase()}",
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = FnfYellow.copy(alpha = 0.2f),
                                    border = BorderStroke(0.8.dp, FnfYellow)
                                ) {
                                    Text(
                                        text = "★ $starmanStars/3 STARS",
                                        color = FnfYellow,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "${currentSong.title} • vs ${currentSong.opponent}",
                                color = FnfTextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
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

                        IconButton(
                            onClick = {
                                val unlockedEndingName = when {
                                    starmanStars >= 3 -> "Ending 3: Secret Exit True Ending"
                                    health >= 0.45f -> "Ending 2: Warp Pipe Escape"
                                    else -> "Ending 1: Canon Bad Ending"
                                }
                                onSaveScore(
                                    score,
                                    true,
                                    "Mario's Madness V2 (${currentSong.title} [$selectedDifficulty] - $unlockedEndingName - ★$starmanStars/3 - $accuracyPercent)"
                                )
                                Toast.makeText(
                                    context,
                                    "Saved ${String.format("%,d", score)} pts & $unlockedEndingName!",
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

                Spacer(modifier = Modifier.height(6.dp))

                // Mode Switcher: Playable MMv2 Stage vs 3 Endings Cutscene Trigger vs Lua/Chart vs ZIP/APK Export
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (activeSection == 0 && activeEndingCutscene == 0) FnfRed.copy(alpha = 0.25f) else FnfSurface,
                        border = BorderStroke(1.dp, if (activeSection == 0 && activeEndingCutscene == 0) FnfRed else FnfBorder),
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                activeSection = 0
                                activeEndingCutscene = 0
                            }
                            .testTag("tab_psych_runtime")
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.PlayArrow,
                                contentDescription = null,
                                tint = if (activeSection == 0 && activeEndingCutscene == 0) FnfRed else FnfTextMuted,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "MMV2 STAGE",
                                color = if (activeSection == 0 && activeEndingCutscene == 0) Color.White else FnfTextSecondary,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (activeSection == 0 && activeEndingCutscene > 0) FnfYellow.copy(alpha = 0.25f) else FnfSurface,
                        border = BorderStroke(1.dp, if (activeSection == 0 && activeEndingCutscene > 0) FnfYellow else FnfBorder),
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                activeSection = 0
                                if (activeEndingCutscene == 0) {
                                    triggerEndingEvaluation()
                                }
                            }
                            .testTag("tab_mmv2_endings")
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Movie,
                                contentDescription = null,
                                tint = if (activeSection == 0 && activeEndingCutscene > 0) FnfYellow else FnfTextMuted,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "3 ENDINGS",
                                color = if (activeSection == 0 && activeEndingCutscene > 0) FnfYellow else FnfTextSecondary,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (activeSection == 2) FnfCyan.copy(alpha = 0.22f) else FnfSurface,
                        border = BorderStroke(1.dp, if (activeSection == 2) FnfCyan else FnfBorder),
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { activeSection = 2 }
                            .testTag("tab_psych_073_studio")
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Code,
                                contentDescription = null,
                                tint = if (activeSection == 2) FnfCyan else FnfTextMuted,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "0.7.3 LUA",
                                color = if (activeSection == 2) FnfCyan else FnfTextSecondary,
                                fontSize = 9.sp,
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
                            modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Android,
                                contentDescription = null,
                                tint = if (activeSection == 1) FnfPurple else FnfTextMuted,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "SAVE .ZIP",
                                color = if (activeSection == 1) FnfPurple else FnfTextSecondary,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }
        }

        if (activeSection == 1) {
            PsychApkBridgeSection(
                detail = detail,
                exportedModPath = exportedModPath,
                installedPackage = installedPsychPackage,
                onLaunchEmbedded = { activeSection = 0 }
            )
        } else if (activeSection == 2) {
            Psych073StudioSection(
                detail = detail,
                currentSong = currentSong,
                exportedModPath = exportedModPath,
                onPlayStage = { activeSection = 0 }
            )
        } else if (activeEndingCutscene in 1..3) {
            // INTERACTIVE 3-ENDING CUTSCENE DIRECTOR OVERLAY
            MarioMadnessEndingCutsceneDirector(
                endings = endingsCatalog,
                activeEndingId = activeEndingCutscene,
                dialogueIndex = endingDialogueIndex,
                starmanStars = starmanStars,
                score = score,
                accuracyPercent = accuracyPercent,
                onSelectEnding = { newId ->
                    activeEndingCutscene = newId
                    endingDialogueIndex = 0
                },
                onNextDialogue = {
                    val currentEnding = endingsCatalog[activeEndingCutscene - 1]
                    if (endingDialogueIndex + 1 < currentEnding.dialogueLines.size) {
                        endingDialogueIndex++
                    } else {
                        endingDialogueIndex = 0
                    }
                },
                onResumeGameplay = {
                    activeEndingCutscene = 0
                    elapsedSeconds = 0
                    isPlaying = true
                },
                onSaveEndingToVault = { endingData ->
                    onSaveScore(
                        score.coerceAtLeast(15000L),
                        true,
                        "Unlocked ${endingData.badge}: ${endingData.title} (★$starmanStars/3 Starman Notes)"
                    )
                    Toast.makeText(
                        context,
                        "Unlocked & Saved ${endingData.title} to My Vault!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            )
        } else {
            // SECTION 0: Playable Mario's Madness V2 5-Act Stage & 4-Lane Highway
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                // Song Selector Strip (All Mario's Madness V2 Tracks)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    songs.forEachIndexed { idx, song ->
                        val isChosen = idx == selectedSongIndex
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isChosen) accentColor.copy(alpha = 0.28f) else FnfSurface,
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
                                text = "🍄 ${song.title} (${song.bpm} BPM)",
                                color = if (isChosen) FnfTextPrimary else FnfTextSecondary,
                                fontSize = 10.sp,
                                fontWeight = if (isChosen) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // 5-Act Interactive Selector + Quick 3 Endings Trigger Strip
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    actMetadata.forEach { (actNum, actLabel, actBoss) ->
                        val isCurrentAct = currentAct == actNum
                        val actColor = when (actNum) {
                            1 -> FnfRed
                            2 -> FnfPurple
                            3 -> FnfOrange
                            4 -> FnfCyan
                            else -> FnfYellow
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isCurrentAct) actColor.copy(alpha = 0.25f) else FnfSurface,
                            border = BorderStroke(1.dp, if (isCurrentAct) actColor else FnfBorder),
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .clickable {
                                    currentAct = actNum
                                    bossPoseText = actBoss.uppercase()
                                    judgementText = actLabel
                                    judgementMs = "vs $actBoss"
                                }
                        ) {
                            Text(
                                text = "ACT $actNum",
                                color = if (isCurrentAct) actColor else FnfTextMuted,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                            )
                        }
                    }

                    // Instant Ending 1 / 2 / 3 Trigger Pills
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = FnfRed.copy(alpha = 0.22f),
                        border = BorderStroke(1.dp, FnfRed),
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable {
                                activeEndingCutscene = 1
                                endingDialogueIndex = 0
                            }
                            .testTag("trigger_ending_1_btn")
                    ) {
                        Text(
                            text = "🎬 END 1: BAD",
                            color = FnfRed,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = FnfGreen.copy(alpha = 0.22f),
                        border = BorderStroke(1.dp, FnfGreen),
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable {
                                activeEndingCutscene = 2
                                endingDialogueIndex = 0
                            }
                            .testTag("trigger_ending_2_btn")
                    ) {
                        Text(
                            text = "🎬 END 2: ESCAPE",
                            color = FnfGreen,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = FnfYellow.copy(alpha = 0.25f),
                        border = BorderStroke(1.dp, FnfYellow),
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable {
                                starmanStars = starmanStars.coerceAtLeast(3)
                                activeEndingCutscene = 3
                                endingDialogueIndex = 0
                            }
                            .testTag("trigger_ending_3_btn")
                    ) {
                        Text(
                            text = "🎬 END 3: SECRET EXIT ★",
                            color = FnfYellow,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Modifiers Row (Downscroll, BotPlay, Ghost Tap, Difficulty)
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
                        label = "+1 ★ Starman",
                        active = starmanStars >= 3,
                        activeColor = FnfYellow,
                        onClick = {
                            starmanStars++
                            judgementText = "★ STARMAN ($starmanStars/3)"
                            judgementMs = if (starmanStars >= 3) "Ending 3: Secret Exit Unlocked!" else "Collect ${3 - starmanStars} more!"
                            playNoteFeedback(2, isStarman = true)
                        }
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
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Psych Engine 0.7.3 Health Bar + Starman Ending Meter
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = when (currentAct) {
                            1 -> "🍄"
                            2 -> "👁️"
                            3 -> "🩸"
                            4 -> "🔫"
                            else -> "👾"
                        },
                        fontSize = 16.sp
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(11.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(FnfRed)
                            .border(1.dp, Color.White.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(health.coerceIn(0.05f, 1f))
                                .background(
                                    Brush.horizontalGradient(
                                        colors = if (starmanStars >= 3) {
                                            listOf(FnfYellow, FnfCyan)
                                        } else {
                                            listOf(FnfGreen, FnfCyan)
                                        }
                                    )
                                )
                        )
                    }

                    Text(text = if (starmanStars >= 3) "🌟" else "🎤", fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(5.dp))

                // Main 4-Lane Mario's Madness V2 Stage & Note Highway
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .border(
                            1.5.dp,
                            if (starmanStars >= 3) FnfYellow else FnfRed.copy(alpha = 0.7f),
                            RoundedCornerShape(16.dp)
                        )
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
                    // Real Mario's Madness V2 Ultra M Corrupted Citadel Background Art
                    Image(
                        painter = painterResource(id = R.drawable.img_mmv2_stage_ultram_1790591016141),
                        contentDescription = "Ultra M Corrupted Citadel Stage",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Act-Responsive Stage Atmosphere Overlay
                    val actTint = when (currentAct) {
                        1 -> Color(0xFF1A0308).copy(alpha = 0.56f)
                        2 -> Color(0xFF1C0426).copy(alpha = 0.56f)
                        3 -> Color(0xFF240B02).copy(alpha = 0.56f)
                        4 -> Color(0xFF031A24).copy(alpha = 0.54f)
                        else -> Color(0xFF1F1602).copy(alpha = 0.50f)
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(actTint)
                    )

                    // Live 60FPS 3D Volumetric Mario's Madness V2 Boss, GF & Starman BF Stage Renderer
                    MarioMadness3DModelEngine.MarioMadness3DStageCanvas(
                        currentAct = currentAct,
                        animTick = animTick,
                        opponentPose = opponentPoseIndex,
                        bfPose = bfPoseIndex,
                        starmanActive = starmanStars >= 3,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Live Animated Stage Characters Strip (Opponent vs GF vs Boyfriend/Starman BF)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopCenter)
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left: Active Mario's Madness V2 Opponent Sprite Badge
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color.Black.copy(alpha = 0.68f),
                            border = BorderStroke(1.dp, FnfRed)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Text(
                                    text = when (currentAct) {
                                        1 -> "🍄"
                                        2 -> "👁️"
                                        3 -> "👹"
                                        4 -> "📺"
                                        else -> "👑"
                                    },
                                    fontSize = 15.sp
                                )
                                Column {
                                    Text(
                                        text = actMetadata[currentAct - 1].third.uppercase(),
                                        color = FnfRed,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                    Text(
                                        text = actMetadata[currentAct - 1].second,
                                        color = FnfTextSecondary,
                                        fontSize = 8.sp
                                    )
                                }
                            }
                        }

                        // Right: Boyfriend / Starman BF Sprite Badge
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color.Black.copy(alpha = 0.68f),
                            border = BorderStroke(1.dp, if (starmanStars >= 3) FnfYellow else FnfCyan)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = if (starmanStars >= 3) "★ STARMAN BF & GF" else "BOYFRIEND & GF",
                                        color = if (starmanStars >= 3) FnfYellow else FnfCyan,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                    Text(
                                        text = bfPoseText,
                                        color = FnfTextSecondary,
                                        fontSize = 8.sp
                                    )
                                }
                                Text(
                                    text = if (starmanStars >= 3) "🌟" else "🎤",
                                    fontSize = 15.sp
                                )
                            }
                        }
                    }

                    val laneColors = listOf(FnfPurple, FnfCyan, FnfGreen, FnfRed)

                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val laneWidth = size.width / 4f
                        val receptorY = if (downscroll) size.height * 0.88f else size.height * 0.14f
                        val spawnY = if (downscroll) 0f else size.height

                        for (lane in 0..3) {
                            val centerX = lane * laneWidth + laneWidth / 2f
                            val laneColor = laneColors[lane]

                            drawLine(
                                color = laneColor.copy(alpha = 0.16f),
                                start = Offset(centerX, 0f),
                                end = Offset(centerX, size.height),
                                strokeWidth = 2.dp.toPx()
                            )

                            val flashAlpha = laneFlash[lane]
                            if (flashAlpha > 0f) {
                                drawCircle(
                                    color = laneColor.copy(alpha = 0.38f * flashAlpha),
                                    radius = 34.dp.toPx(),
                                    center = Offset(centerX, receptorY)
                                )
                            }

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

                        // Draw Active Scrolling Notes (Standard, Fire Hurt Note, Golden Starman Note)
                        for (note in activeNotes) {
                            val centerX = note.lane * laneWidth + laneWidth / 2f
                            val noteY = spawnY + (receptorY - spawnY) * (note.progress / 0.88f)
                            val noteColor = laneColors[note.lane]

                            when {
                                note.isStarmanNote -> {
                                    // Golden Starman Power-Up Note (Unlocks Ending 3: Secret Exit!)
                                    drawCircle(
                                        color = FnfYellow.copy(alpha = 0.35f),
                                        radius = 30.dp.toPx(),
                                        center = Offset(centerX, noteY)
                                    )
                                    drawRoundRect(
                                        color = FnfYellow,
                                        topLeft = Offset(centerX - 24.dp.toPx(), noteY - 24.dp.toPx()),
                                        size = Size(48.dp.toPx(), 48.dp.toPx()),
                                        cornerRadius = CornerRadius(14.dp.toPx(), 14.dp.toPx())
                                    )
                                    drawRoundRect(
                                        color = Color.White,
                                        topLeft = Offset(centerX - 24.dp.toPx(), noteY - 24.dp.toPx()),
                                        size = Size(48.dp.toPx(), 48.dp.toPx()),
                                        cornerRadius = CornerRadius(14.dp.toPx(), 14.dp.toPx()),
                                        style = Stroke(width = 3.5.dp.toPx())
                                    )
                                }
                                note.isHurtNote -> {
                                    // Fire Mario / Poison Mushroom Hurt Note
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
                                }
                                else -> {
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
                    }

                    // Center Stage Judgement, Combo & Ending Forecast Overlay
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (botPlay) {
                            Text(
                                text = "[BOTPLAY ACTIVE]",
                                color = FnfGreen,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.2.sp
                            )
                        }
                        Text(
                            text = judgementText,
                            color = when {
                                judgementText.contains("STARMAN") -> FnfYellow
                                judgementText.contains("SICK") -> FnfCyan
                                judgementText.contains("GOOD") -> FnfGreen
                                judgementText.contains("HURT") || judgementText.contains("MISS") -> FnfRed
                                else -> FnfYellow
                            },
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = judgementMs,
                            color = FnfTextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                        if (combo > 1) {
                            Text(
                                text = "COMBO x$combo",
                                color = FnfPink,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    // Spacebar / Touch Lava Pipe Dodge Prompt Overlay
                    androidx.compose.animation.AnimatedVisibility(
                        visible = dodgeAlertActive,
                        enter = scaleIn() + fadeIn(),
                        exit = scaleOut() + fadeOut(),
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 48.dp)
                    ) {
                        Button(
                            onClick = {
                                dodgeAlertActive = false
                                score += 500
                                judgementText = "PIPE DODGED! (+500)"
                                judgementMs = "Escaped Ultra M's Ambush!"
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = FnfRed, contentColor = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(2.dp, FnfYellow)
                        ) {
                            Icon(Icons.Filled.Warning, contentDescription = null, tint = FnfYellow)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("TAP TO DODGE ULTRA M! [SPACEBAR]", fontWeight = FontWeight.Black, fontSize = 12.sp)
                        }
                    }

                    // Bottom Bar inside Stage: Current Ending Path Indicator
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.Black.copy(alpha = 0.72f),
                        border = BorderStroke(
                            1.dp,
                            when {
                                starmanStars >= 3 -> FnfYellow
                                health >= 0.45f -> FnfGreen
                                else -> FnfRed
                            }
                        ),
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 6.dp)
                            .clickable { triggerEndingEvaluation() }
                    ) {
                        Text(
                            text = when {
                                starmanStars >= 3 -> "🌟 ENDING 3 READY: SECRET EXIT TRUE ENDING (TAP TO PLAY CUTSCENE)"
                                health >= 0.45f -> "🟢 CURRENT PATH: ENDING 2 (ESCAPE) • HIT ${3 - starmanStars} MORE ★ FOR SECRET EXIT"
                                else -> "🩸 DANGER PATH: ENDING 1 (CANON BAD ENDING) • RAISE HEALTH OR HIT ★"
                            },
                            color = when {
                                starmanStars >= 3 -> FnfYellow
                                health >= 0.45f -> FnfGreen
                                else -> FnfRed
                            },
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(5.dp))

                // Authentic Psych Engine 0.7.3 Score Bar
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF101220),
                    border = BorderStroke(1.dp, FnfBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Score: ${String.format("%,d", score)} | Misses: $misses | ★ Stars: $starmanStars/3 | Rating: $psychRatingTag ($accuracyPercent)",
                        color = FnfTextPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 5.dp, horizontal = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // 4 Mobile Touch Strum Pad Buttons (Left, Down, Up, Right)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp),
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

/**
 * Interactive 3-Ending Cutscene Director for Mario's Madness V2 (#359554) + Secret Exit.
 * Allows watching and switching between all 3 endings (Bad Ending, Warp Pipe Escape, Secret Exit True Ending)
 * with custom artwork, voice-style dialogue lines, and soundtrack playback.
 */
@Composable
private fun MarioMadnessEndingCutsceneDirector(
    endings: List<EndingCutsceneData>,
    activeEndingId: Int,
    dialogueIndex: Int,
    starmanStars: Int,
    score: Long,
    accuracyPercent: String,
    onSelectEnding: (Int) -> Unit,
    onNextDialogue: () -> Unit,
    onResumeGameplay: () -> Unit,
    onSaveEndingToVault: (EndingCutsceneData) -> Unit
) {
    val currentEnding = endings.getOrElse(activeEndingId - 1) { endings.first() }
    val currentLine = currentEnding.dialogueLines.getOrElse(dialogueIndex) {
        currentEnding.dialogueLines.first()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 3 Endings Switcher Bar
        Text(
            text = "MARIO'S MADNESS V2 (#359554) • SELECT ANY OF THE 3 ENDINGS:",
            color = FnfYellow,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            endings.forEach { ending ->
                val isSelected = ending.id == activeEndingId
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) ending.accentColor.copy(alpha = 0.25f) else FnfSurface,
                    border = BorderStroke(1.5.dp, if (isSelected) ending.accentColor else FnfBorder),
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onSelectEnding(ending.id) }
                        .testTag("select_ending_card_${ending.id}")
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = when (ending.id) {
                                1 -> "🩸 ENDING 1"
                                2 -> "🟢 ENDING 2"
                                else -> "🌟 ENDING 3"
                            },
                            color = if (isSelected) ending.accentColor else FnfTextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = when (ending.id) {
                                1 -> "Canon Bad"
                                2 -> "Pipe Escape"
                                else -> "Secret Exit"
                            },
                            color = FnfTextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Main Cutscene Artwork & Dialogue Card
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = FnfSurface),
            border = BorderStroke(2.dp, currentEnding.accentColor)
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp)
                ) {
                    Image(
                        painter = painterResource(id = currentEnding.bannerResId),
                        contentDescription = currentEnding.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.82f)
                                    )
                                )
                            )
                    )

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = currentEnding.accentColor,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = currentEnding.badge,
                            color = Color.Black,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = currentEnding.title,
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = currentEnding.subtitle,
                            color = FnfTextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }

                // Interactive Cutscene Dialogue Box
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = currentEnding.accentColor.copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, currentEnding.accentColor)
                        ) {
                            Text(
                                text = "SPEAKER: ${currentLine.first}",
                                color = currentEnding.accentColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        Text(
                            text = "Scene ${dialogueIndex + 1} of ${currentEnding.dialogueLines.size}",
                            color = FnfTextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF090B14),
                        border = BorderStroke(1.dp, FnfBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNextDialogue() }
                    ) {
                        Text(
                            text = currentLine.second,
                            color = FnfTextPrimary,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(14.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "UNLOCK CONDITION: ${currentEnding.conditionText}",
                        color = FnfTextSecondary,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onNextDialogue,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("ending_next_dialogue_btn"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = currentEnding.accentColor,
                                contentColor = Color.Black
                            )
                        ) {
                            Text(
                                text = "NEXT SCENE (${dialogueIndex + 1}/${currentEnding.dialogueLines.size})",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Button(
                            onClick = { onSaveEndingToVault(currentEnding) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = FnfCyan,
                                contentColor = Color.Black
                            )
                        ) {
                            Icon(Icons.Filled.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("SAVE ENDING", fontSize = 11.sp, fontWeight = FontWeight.Black)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = onResumeGameplay,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, FnfBorder),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = FnfTextPrimary)
                    ) {
                        Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("RETURN TO MARIO'S MADNESS V2 STAGE", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
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
    val scope = rememberCoroutineScope()
    val mod = detail.mod

    var isDownloadingEngineApk by remember { mutableStateOf(false) }
    var engineApkProgress by remember { mutableIntStateOf(0) }
    var engineApkInstalledInApp by remember { mutableStateOf(installedPackage != null) }
    var isPackingZip by remember { mutableStateOf(false) }
    var packedMb by remember { mutableIntStateOf(0) }
    var totalMbTarget by remember { mutableIntStateOf(285) }

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
            isPackingZip = true
            packedMb = 0
            scope.launch(kotlinx.coroutines.Dispatchers.IO) {
                val ok = Psych073ModBuilder.writePsych073ModZipToUri(
                    context = context,
                    targetUri = uri,
                    detail = detail,
                    onProgress = { written, total, _ ->
                        packedMb = written
                        totalMbTarget = total
                    }
                )
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                    isPackingZip = false
                    if (ok) {
                        Toast.makeText(
                            context,
                            "Saved ${mod.id}-mmv2-268mb-3endings.zip ($totalMbTarget MB)!",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(context, "Could not save ZIP file.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    val saveApkBundleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/vnd.android.package-archive")
    ) { uri: Uri? ->
        if (uri != null) {
            isPackingZip = true
            packedMb = 0
            scope.launch(kotlinx.coroutines.Dispatchers.IO) {
                val ok = Psych073ModBuilder.writePsych073ModZipToUri(
                    context = context,
                    targetUri = uri,
                    detail = detail,
                    onProgress = { written, total, _ ->
                        packedMb = written
                        totalMbTarget = total
                    }
                )
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                    isPackingZip = false
                    if (ok) {
                        engineApkInstalledInApp = true
                        Toast.makeText(
                            context,
                            "Saved ${mod.engine} + ${mod.title} ($totalMbTarget MB) bundle!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
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
                            text = "MARIO'S MADNESS V2 (#359554) • ${mod.engine.uppercase()}",
                            color = FnfYellow,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Includes Real PNG Stage, Custom Notes & 3 Playable Endings",
                            color = FnfGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Export the complete Mario's Madness V2 (#359554) + Secret Exit (3 Endings) mod pack as a standalone .ZIP with pack.png, images/mmv2/stage_ultram.png, all 3 ending cutscene PNGs, custom note spritesheets, and 5-Act Lua scripts.",
                    color = FnfTextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

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
                        onClick = { isDownloadingEngineApk = true },
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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            if (!isPackingZip) {
                                val fileName = "${mod.id}-mmv2-268mb-3endings-psych073.zip"
                                saveZipLauncher.launch(fileName)
                            }
                        },
                        enabled = !isPackingZip,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("save_mod_zip_offline_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FnfGreen, contentColor = FnfDarkBg)
                    ) {
                        Icon(Icons.Filled.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isPackingZip) "PACKING $packedMb/$totalMbTarget MB" else "SAVE 285 MB .ZIP",
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
                            text = if (installedPackage != null) "OPEN EXTERNAL APK" else "PLAY MMV2 STAGE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://gamebanana.com/mods/359554"))
                            context.startActivity(intent)
                        },
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, FnfYellow),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = FnfYellow)
                    ) {
                        Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("MOD #359554", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

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
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
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
            .height(60.dp)
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
                modifier = Modifier.size(26.dp)
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

@Composable
private fun Psych073StudioSection(
    detail: FullModDetail,
    currentSong: com.example.data.model.SongItem,
    exportedModPath: String,
    onPlayStage: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val mod = detail.mod
    var selectedFileTab by remember { mutableIntStateOf(0) }
    var enableHealthDrain by remember { mutableStateOf(true) }
    var enableBeatZoom by remember { mutableStateOf(true) }
    var isPackingStudioZip by remember { mutableStateOf(false) }
    var studioPackedMb by remember { mutableIntStateOf(0) }
    var studioTotalMb by remember { mutableIntStateOf(285) }

    val savePsych073ZipLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/zip")
    ) { uri: Uri? ->
        if (uri != null) {
            isPackingStudioZip = true
            studioPackedMb = 0
            scope.launch(kotlinx.coroutines.Dispatchers.IO) {
                val ok = Psych073ModBuilder.writePsych073ModZipToUri(
                    context = context,
                    targetUri = uri,
                    detail = detail,
                    enableHealthDrain = enableHealthDrain,
                    enableBeatZoom = enableBeatZoom,
                    onProgress = { written, total, _ ->
                        studioPackedMb = written
                        studioTotalMb = total
                    }
                )
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                    isPackingStudioZip = false
                    if (ok) {
                        Toast.makeText(
                            context,
                            "Exported complete $studioTotalMb MB Mario's Madness V2 (#359554) 3-Endings Mod (.ZIP)!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    val fileTabs = listOf(
        "pack.json",
        "scripts/secret_exit_5act_director.lua",
        "stages/secret_exit_citadel.lua",
        "custom_notetypes/Hurt Note.lua",
        "custom_notetypes/Starman Note.lua",
        "custom_events/DodgeEvent.lua",
        "data/${Psych073ModBuilder.slugify(currentSong.title)}-hard.json",
        "weeks/secret_exit_reimagined.json"
    )

    val activeFileContent = remember(selectedFileTab, detail, currentSong, enableHealthDrain, enableBeatZoom) {
        when (selectedFileTab) {
            0 -> Psych073ModBuilder.generatePackJson(detail)
            1 -> Psych073ModBuilder.generateSecretExitDirectorLua(mod.title, enableHealthDrain, enableBeatZoom)
            2 -> Psych073ModBuilder.generateSecretExitStageLua()
            3 -> Psych073ModBuilder.generateHurtNoteLua()
            4 -> Psych073ModBuilder.generateStarmanNoteLua()
            5 -> Psych073ModBuilder.generateDodgeEventLua()
            6 -> Psych073ModBuilder.generateChartJson(currentSong, "hard", includeHurtNotes = true)
            else -> Psych073ModBuilder.generateWeekJson(detail)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = FnfSurface),
            border = BorderStroke(1.5.dp, FnfCyan)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Filled.Build, contentDescription = null, tint = FnfCyan, modifier = Modifier.size(22.dp))
                    Column {
                        Text(
                            text = "MARIO'S MADNESS V2 (#359554) 0.7.3 BUILDER",
                            color = FnfCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Bundles PNG Stage, 3 Ending Cutscene PNGs, Custom Note Sheets & 5-Act Lua Director",
                            color = FnfTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PsychTogglePill(
                        label = "Lua Health Drain (0.7.3)",
                        active = enableHealthDrain,
                        activeColor = FnfRed,
                        onClick = { enableHealthDrain = !enableHealthDrain }
                    )
                    PsychTogglePill(
                        label = "Beat Camera Zoom",
                        active = enableBeatZoom,
                        activeColor = FnfYellow,
                        onClick = { enableBeatZoom = !enableBeatZoom }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            if (!isPackingStudioZip) {
                                val zipName = "${Psych073ModBuilder.slugify(mod.id)}-mmv2-268mb-3endings-0.7.3.zip"
                                savePsych073ZipLauncher.launch(zipName)
                            }
                        },
                        enabled = !isPackingStudioZip,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("export_working_psych073_zip_btn"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FnfGreen, contentColor = FnfDarkBg)
                    ) {
                        Icon(Icons.Filled.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            if (isPackingStudioZip) "PACKING $studioPackedMb/$studioTotalMb MB" else "EXPORT 268 MB .ZIP",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Button(
                        onClick = {
                            Psych073ModBuilder.exportCompletePsych073ModToDisk(
                                context,
                                detail,
                                enableHealthDrain,
                                enableBeatZoom
                            )
                            Toast.makeText(
                                context,
                                "Rebuilt Mario's Madness V2 files in $exportedModPath",
                                Toast.LENGTH_SHORT
                            ).show()
                            onPlayStage()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FnfCyan, contentColor = FnfDarkBg)
                    ) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("PLAY IN STAGE", fontSize = 11.sp, fontWeight = FontWeight.Black)
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            fileTabs.forEachIndexed { index, tabName ->
                val isSelected = selectedFileTab == index
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) FnfYellow.copy(alpha = 0.2f) else FnfSurface,
                    border = BorderStroke(1.dp, if (isSelected) FnfYellow else FnfBorder),
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { selectedFileTab = index }
                ) {
                    Text(
                        text = tabName,
                        color = if (isSelected) FnfYellow else FnfTextSecondary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF090B14),
            border = BorderStroke(1.dp, FnfBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "FILE: ${fileTabs[selectedFileTab]} (Psych Engine 0.7.3 Schema)",
                    color = FnfYellow,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = activeFileContent,
                    color = FnfGreen,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    lineHeight = 15.sp
                )
            }
        }
    }
}

fun exportModToPsychFolder(context: Context, detail: FullModDetail): String {
    return Psych073ModBuilder.exportCompletePsych073ModToDisk(context, detail)
}

fun writeModPackZipToUri(context: Context, targetUri: Uri, detail: FullModDetail): Boolean {
    return Psych073ModBuilder.writePsych073ModZipToUri(context, targetUri, detail)
}
