package com.example.ui.screens

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.FnfBorder
import com.example.ui.theme.FnfCyan
import com.example.ui.theme.FnfDarkBg
import com.example.ui.theme.FnfGreen
import com.example.ui.theme.FnfPink
import com.example.ui.theme.FnfPurple
import com.example.ui.theme.FnfRed
import com.example.ui.theme.FnfSurface
import com.example.ui.theme.FnfSurfaceElevated
import com.example.ui.theme.FnfTextMuted
import com.example.ui.theme.FnfTextPrimary
import com.example.ui.theme.FnfTextSecondary
import com.example.ui.theme.FnfYellow

data class BeatSong(val title: String, val bpm: Int, val modName: String)

@Composable
fun RhythmLabScreen(
    score: Int,
    combo: Int,
    maxCombo: Int,
    latestRating: String?,
    accuracyHits: Pair<Int, Int>,
    onArrowTap: (String) -> Unit,
    onResetScore: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val vibrator = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE)?.let {
                (it as? android.os.VibratorManager)?.defaultVibrator
            } ?: (context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator)
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    val sampleTracks = remember {
        listOf(
            BeatSong("Lo-Fight", 130, "VS Whitty"),
            BeatSong("Release", 150, "Garcello"),
            BeatSong("Zavodila", 175, "Mid-Fight Masses"),
            BeatSong("Triple Trouble", 180, "Sonic.EXE"),
            BeatSong("Expurgation", 215, "The Tricky Mod")
        )
    }
    var selectedTrackIndex by remember { mutableIntStateOf(0) }
    val currentTrack = sampleTracks[selectedTrackIndex]

    // Pulsing speaker animation tied to BPM
    val beatDurationMs = (60000 / currentTrack.bpm).toLong()
    val infiniteTransition = rememberInfiniteTransition(label = "speakerBeat")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.14f,
        animationSpec = infiniteRepeatable(
            animation = tween((beatDurationMs / 2).toInt(), easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "speakerPulse"
    )

    fun triggerHaptic() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(30)
            }
        } catch (_: Exception) {}
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FnfDarkBg)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "FUNKIN' RHYTHM LAB",
                    color = FnfCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "ARROW BEAT TESTER",
                    color = FnfTextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black
                )
            }

            IconButton(
                onClick = onResetScore,
                modifier = Modifier
                    .background(FnfSurfaceElevated, CircleShape)
                    .testTag("reset_beat_test_btn")
            ) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = "Reset Score",
                    tint = FnfCyan
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Track selector pills
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            sampleTracks.forEachIndexed { index, track ->
                val isSelected = selectedTrackIndex == index
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) FnfPink.copy(alpha = 0.2f) else FnfSurface,
                    border = BorderStroke(1.dp, if (isSelected) FnfPink else FnfBorder),
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { selectedTrackIndex = index }
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = track.title.take(7),
                            color = if (isSelected) FnfPink else FnfTextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Text(
                            text = "${track.bpm}",
                            color = FnfTextMuted,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Speaker Stage Simulation
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = FnfSurface),
            border = BorderStroke(1.5.dp, FnfBorder)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Background subtle beat grid
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Score & Accuracy HUD
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("SCORE", color = FnfTextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = String.format("%,d", score),
                                color = FnfCyan,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            val (hits, total) = accuracyHits
                            val accPercent = if (total > 0) (hits * 100 / total) else 100
                            Text("ACCURACY", color = FnfTextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = "$accPercent%",
                                color = FnfGreen,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    // Pulsing Boombox & Rating Center
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = 12.dp)
                    ) {
                        // Boombox Speakers Icon
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(110.dp)
                                .scale(pulseScale)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            FnfPink.copy(alpha = 0.4f),
                                            Color.Transparent
                                        )
                                    ),
                                    CircleShape
                                )
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = FnfSurfaceElevated,
                                border = BorderStroke(2.dp, FnfCyan),
                                modifier = Modifier.size(76.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Filled.VolumeUp,
                                        contentDescription = "Speakers",
                                        tint = FnfCyan,
                                        modifier = Modifier.size(38.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Dynamic Judgement Rating Pop-up ("SICK!!", "GOOD", "BAD", "SHIT")
                        AnimatedVisibility(
                            visible = latestRating != null,
                            enter = scaleIn() + fadeIn(),
                            exit = scaleOut() + fadeOut()
                        ) {
                            Text(
                                text = latestRating ?: "",
                                color = when (latestRating) {
                                    "SICK!!" -> FnfCyan
                                    "GOOD" -> FnfGreen
                                    "BAD" -> FnfYellow
                                    else -> FnfRed
                                },
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.sp
                            )
                        }

                        // Combo Counter
                        if (combo > 0) {
                            Text(
                                text = "$combo COMBO!",
                                color = FnfPink,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black
                            )
                        } else {
                            Text(
                                text = "TAP ARROWS TO THE BEAT",
                                color = FnfTextMuted,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Track & BPM info pill
                    Row(
                        modifier = Modifier
                            .background(FnfDarkBg, RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "${currentTrack.title} (${currentTrack.modName})",
                            color = FnfTextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text("•", color = FnfTextMuted)
                        Text(
                            text = "${currentTrack.bpm} BPM",
                            color = FnfYellow,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 4 Rhythm Arrow Buttons (Left ⬅️ Purple, Down ⬇️ Cyan, Up ⬆️ Green, Right ➡️ Red)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 90.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ArrowButton(
                direction = "LEFT",
                icon = Icons.Filled.ArrowBack,
                color = FnfPurple,
                onTap = {
                    triggerHaptic()
                    onArrowTap("LEFT")
                },
                modifier = Modifier.testTag("arrow_left")
            )

            ArrowButton(
                direction = "DOWN",
                icon = Icons.Filled.ArrowDownward,
                color = FnfCyan,
                onTap = {
                    triggerHaptic()
                    onArrowTap("DOWN")
                },
                modifier = Modifier.testTag("arrow_down")
            )

            ArrowButton(
                direction = "UP",
                icon = Icons.Filled.ArrowUpward,
                color = FnfGreen,
                onTap = {
                    triggerHaptic()
                    onArrowTap("UP")
                },
                modifier = Modifier.testTag("arrow_up")
            )

            ArrowButton(
                direction = "RIGHT",
                icon = Icons.Filled.ArrowForward,
                color = FnfRed,
                onTap = {
                    triggerHaptic()
                    onArrowTap("RIGHT")
                },
                modifier = Modifier.testTag("arrow_right")
            )
        }
    }
}

@Composable
private fun ArrowButton(
    direction: String,
    icon: ImageVector,
    color: Color,
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Surface(
        modifier = modifier
            .size(72.dp)
            .scale(if (isPressed) 0.88f else 1f)
            .clip(RoundedCornerShape(18.dp))
            .clickable(interactionSource = interactionSource, indication = null) {
                onTap()
            },
        shape = RoundedCornerShape(18.dp),
        color = if (isPressed) color else color.copy(alpha = 0.15f),
        border = BorderStroke(2.dp, color)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = direction,
                tint = if (isPressed) FnfDarkBg else color,
                modifier = Modifier.size(34.dp)
            )
        }
    }
}
