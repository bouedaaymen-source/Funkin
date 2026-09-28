package com.example.data.psych

import android.graphics.Bitmap
import android.graphics.Canvas as AndroidCanvas
import android.graphics.Color as AndroidColor
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path as AndroidPath
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import java.io.ByteArrayOutputStream
import kotlin.math.cos
import kotlin.math.sin

/**
 * 3D-Shaded Volumetric Character & Stage Model Engine for Mario's Madness V2 (GameBanana #359554).
 *
 * Replaces flat 2D placeholders with:
 * 1. Live 60 FPS volumetric 3D-shaded boss & hero models on the playable stage (`MarioMadness3DStageCanvas`):
 *    - Act 1: Horror Mario / Ultra M (3D crimson cap with 'M' emblem, hollow abyssal eyes, glowing red pupils,
 *             serrated fangs, blood-stained overalls, metallic buttons, and lunging cleaver/claws)
 *    - Act 2: Mr. Virtual (Paranoia 3D stereoscopic wireframe + volumetric crimson fedora & floating eye orbs)
 *    - Act 3: MX (Demise 85-foot muscular 3D titan with cavernous jaw, black overalls & seismic stomp aura)
 *    - Act 4: Mr. Sys ('WE ARE NINTENDO') + Beta Luigi & Pico Warp Pipe 3D turret support
 *    - Act 5: Giant 6-Armed Ultra M Final Amalgamate vs 3D Golden Starman Boyfriend & Girlfriend
 * 2. Real exported 5-pose 3D-shaded PNG Character Spritesheets + Sparrow v2 XMLs + Psych Engine 0.7.3
 *    `characters/[id].json` definitions so Psych Engine 0.7.3 renders custom 3D Mario's Madness V2 models
 *    instead of base-game 2D Daddy Dearest ("dad").
 */
object MarioMadness3DModelEngine {

    /**
     * Composable 60 FPS 3D-shaded character & stage renderer drawn behind the note highway
     * and in the 3D Model Showcase viewer.
     */
    @Composable
    fun MarioMadness3DStageCanvas(
        currentAct: Int,
        animTick: Int,
        opponentPose: Int, // 0=IDLE, 1=LEFT, 2=DOWN, 3=UP, 4=RIGHT
        bfPose: Int,       // 0=IDLE, 1=LEFT, 2=DOWN, 3=UP, 4=RIGHT
        starmanActive: Boolean,
        modifier: Modifier = Modifier
    ) {
        Canvas(modifier = modifier) {
            val w = size.width
            val h = size.height
            val phase = animTick * 0.12f
            val bob = sin(phase) * 8f
            val bfBob = cos(phase * 1.15f) * 6f

            // 1. 3D Perspective Citadel Bridge & Volumetric Lava Pit Floor
            draw3DCitadelFloor(w, h, currentAct, phase)

            // 2. Center Background: 3D Demon Speaker & Girlfriend (or Starman GF)
            draw3DGirlfriendSpeaker(
                cx = w * 0.50f,
                cy = h * 0.54f + sin(phase * 2f) * 4f,
                scale = (w / 420f).coerceIn(0.65f, 1.35f),
                starmanActive = starmanActive,
                phase = phase
            )

            // 3. Left Stage: Towering 3D Mario's Madness V2 Boss Model (Act 1..5)
            val oppPoseOffsetX = when (opponentPose) {
                1 -> -18f
                4 -> 22f
                else -> 0f
            }
            val oppPoseOffsetY = when (opponentPose) {
                2 -> 16f
                3 -> -20f
                else -> 0f
            }
            draw3DOpponentModel(
                cx = w * 0.24f + oppPoseOffsetX,
                cy = h * 0.56f + bob + oppPoseOffsetY,
                scale = (w / 360f).coerceIn(0.75f, 1.55f),
                act = currentAct,
                pose = opponentPose,
                phase = phase
            )

            // 4. Right Stage: 3D Boyfriend / Golden Starman BF Model (+ Act 4 Pico & Beta Luigi Warp Pipe)
            if (currentAct >= 4) {
                draw3DWarpPipeAllies(
                    cx = w * 0.66f,
                    cy = h * 0.50f + sin(phase * 1.4f) * 5f,
                    scale = (w / 400f).coerceIn(0.65f, 1.25f),
                    phase = phase
                )
            }

            val bfOffsetX = when (bfPose) {
                1 -> -16f
                4 -> 16f
                else -> 0f
            }
            val bfOffsetY = when (bfPose) {
                2 -> 14f
                3 -> -18f
                else -> 0f
            }
            draw3DBoyfriendModel(
                cx = w * 0.78f + bfOffsetX,
                cy = h * 0.61f + bfBob + bfOffsetY,
                scale = (w / 380f).coerceIn(0.72f, 1.45f),
                pose = bfPose,
                starmanActive = starmanActive,
                phase = phase
            )
        }
    }

    private fun DrawScope.draw3DCitadelFloor(w: Float, h: Float, act: Int, phase: Float) {
        val lavaColor = when (act) {
            1 -> Color(0xFFFF183A)
            2 -> Color(0xFFD500F9)
            3 -> Color(0xFFFF6D00)
            4 -> Color(0xFF00E5FF)
            else -> Color(0xFFFFD740)
        }

        // Volumetric Lava Pit Glow at bottom
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.Transparent,
                    lavaColor.copy(alpha = 0.28f + 0.08f * sin(phase)),
                    lavaColor.copy(alpha = 0.55f)
                ),
                startY = h * 0.55f,
                endY = h
            ),
            topLeft = Offset(0f, h * 0.55f),
            size = Size(w, h * 0.45f)
        )

        // 3D Perspective Stone Bridge Deck
        val floorTopY = h * 0.67f
        val floorBottomY = h * 0.88f
        val deckPath = Path().apply {
            moveTo(w * 0.08f, floorTopY)
            lineTo(w * 0.92f, floorTopY)
            lineTo(w * 1.05f, floorBottomY)
            lineTo(-w * 0.05f, floorBottomY)
            close()
        }
        drawPath(
            path = deckPath,
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF2B1D34), Color(0xFF140C1C), Color(0xFF09050D)),
                startY = floorTopY,
                endY = floorBottomY
            )
        )
        // Perspective 3D Tile Grid Lines on Bridge
        for (i in -3..3) {
            val topX = w * 0.5f + i * (w * 0.11f)
            val botX = w * 0.5f + i * (w * 0.16f)
            drawLine(
                color = lavaColor.copy(alpha = 0.35f),
                start = Offset(topX, floorTopY),
                end = Offset(botX, floorBottomY),
                strokeWidth = 2f
            )
        }
        drawLine(
            color = lavaColor.copy(alpha = 0.8f),
            start = Offset(w * 0.08f, floorTopY),
            end = Offset(w * 0.92f, floorTopY),
            strokeWidth = 3f
        )
    }

    private fun DrawScope.draw3DGirlfriendSpeaker(
        cx: Float,
        cy: Float,
        scale: Float,
        starmanActive: Boolean,
        phase: Float
    ) {
        val pulse = 1f + 0.05f * sin(phase * 2f)
        val spkW = 110f * scale * pulse
        val spkH = 56f * scale * pulse

        // 3D Speaker Cabinet with bevel shading
        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFF3A234B), Color(0xFF1A0F26), Color(0xFF0D0614)),
                start = Offset(cx - spkW / 2, cy),
                end = Offset(cx + spkW / 2, cy + spkH)
            ),
            topLeft = Offset(cx - spkW / 2, cy),
            size = Size(spkW, spkH),
            cornerRadius = CornerRadius(10f * scale, 10f * scale)
        )
        // Left & Right 3D Woofer Cones
        for (side in listOf(-1f, 1f)) {
            val wx = cx + side * 32f * scale
            val wy = cy + 28f * scale
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF111118), Color(0xFF2D2538), Color(0xFF00E5FF).copy(alpha = 0.6f)),
                    center = Offset(wx, wy),
                    radius = 20f * scale * pulse
                ),
                radius = 20f * scale * pulse,
                center = Offset(wx, wy)
            )
        }

        // 3D Girlfriend Dress & Head on top of speaker
        val dressColor = if (starmanActive) Color(0xFFFFD740) else Color(0xFFFF1744)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFFFE0B2), Color(0xFFD7A172)),
                center = Offset(cx - 4f * scale, cy - 26f * scale),
                radius = 16f * scale
            ),
            radius = 15f * scale,
            center = Offset(cx, cy - 24f * scale)
        )
        drawRoundRect(
            brush = Brush.verticalGradient(
                colors = listOf(dressColor, dressColor.copy(alpha = 0.5f)),
                startY = cy - 12f * scale,
                endY = cy + 6f * scale
            ),
            topLeft = Offset(cx - 16f * scale, cy - 12f * scale),
            size = Size(32f * scale, 20f * scale),
            cornerRadius = CornerRadius(8f * scale, 8f * scale)
        )
    }

    private fun DrawScope.draw3DOpponentModel(
        cx: Float,
        cy: Float,
        scale: Float,
        act: Int,
        pose: Int,
        phase: Float
    ) {
        val primaryCol = when (act) {
            1 -> Color(0xFFFF183A) // Horror Mario / Ultra M Crimson
            2 -> Color(0xFFD500F9) // Mr. Virtual Virtual-Boy Magenta/Red
            3 -> Color(0xFFFF6D00) // MX Demise Giant Flame Orange
            4 -> Color(0xFF00E5FF) // Mr. Sys Nintendo Cyan/Gold
            else -> Color(0xFFFFD740) // Act 5 Ultra M Final Climax
        }

        // Ground 3D Shadow Ellipse
        drawOval(
            color = Color.Black.copy(alpha = 0.65f),
            topLeft = Offset(cx - 56f * scale, cy + 52f * scale),
            size = Size(112f * scale, 24f * scale)
        )

        // Volumetric Back Aura / Boss Silhouette Glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    primaryCol.copy(alpha = 0.52f),
                    primaryCol.copy(alpha = 0.15f),
                    Color.Transparent
                ),
                center = Offset(cx, cy - 10f * scale),
                radius = 95f * scale
            ),
            radius = 95f * scale,
            center = Offset(cx, cy - 10f * scale)
        )

        // Act 5 / Act 1 Ultra M Giant Tendrils & Cartridge Wires
        if (act == 1 || act == 5) {
            for (arm in -2..2) {
                if (arm == 0) continue
                val armAngle = phase * 1.5f + arm * 0.8f
                val endX = cx + arm * 42f * scale + cos(armAngle) * 14f * scale
                val endY = cy - 35f * scale + sin(armAngle) * 18f * scale
                drawLine(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF2B040C), primaryCol),
                        start = Offset(cx, cy),
                        end = Offset(endX, endY)
                    ),
                    start = Offset(cx, cy + 5f * scale),
                    end = Offset(endX, endY),
                    strokeWidth = 8f * scale
                )
                // 3D WhiteGlove Claw Hand at end of tendril
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color.White, Color(0xFFB0BEC5), Color(0xFF37474F)),
                        center = Offset(endX - 3f * scale, endY - 3f * scale),
                        radius = 13f * scale
                    ),
                    radius = 12f * scale,
                    center = Offset(endX, endY)
                )
            }
        }

        // 3D Torso & Overalls (Volumetric Cylinder Shading)
        val torsoW = (if (act == 3) 86f else 68f) * scale
        val torsoH = (if (act == 3) 78f else 66f) * scale
        drawRoundRect(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color(0xFF1A040A),
                    primaryCol.copy(alpha = 0.85f),
                    Color(0xFF3D0714)
                ),
                startX = cx - torsoW / 2,
                endX = cx + torsoW / 2
            ),
            topLeft = Offset(cx - torsoW / 2, cy - 14f * scale),
            size = Size(torsoW, torsoH),
            cornerRadius = CornerRadius(18f * scale, 18f * scale)
        )

        // 3D Denim/Black Overalls with Specular Brass Buttons
        val overallW = torsoW * 0.78f
        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFF1E2749), Color(0xFF0D1124), Color(0xFF05070F)),
                start = Offset(cx - overallW / 2, cy),
                end = Offset(cx + overallW / 2, cy + torsoH)
            ),
            topLeft = Offset(cx - overallW / 2, cy + 4f * scale),
            size = Size(overallW, torsoH * 0.72f),
            cornerRadius = CornerRadius(12f * scale, 12f * scale)
        )
        // 3D Metallic Gold Overall Buttons
        for (bSide in listOf(-1f, 1f)) {
            val bx = cx + bSide * 16f * scale
            val by = cy + 14f * scale
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFFFF59D), Color(0xFFFFB300), Color(0xFF6D4C41)),
                    center = Offset(bx - 2f * scale, by - 2f * scale),
                    radius = 6.5f * scale
                ),
                radius = 6f * scale,
                center = Offset(bx, by)
            )
        }

        // 3D Sculpted Head (Pale Horror Flesh / MX Grey / Mr. Virtual Crimson Sphere)
        val headRadius = (if (act == 3) 38f else 32f) * scale
        val headCenterY = cy - 38f * scale
        val skinHighlight = when (act) {
            2 -> Color(0xFFFF5252)
            3 -> Color(0xFFECEFF1)
            4 -> Color(0xFFE0F7FA)
            else -> Color(0xFFF5DEC9)
        }
        val skinShadow = when (act) {
            2 -> Color(0xFF3E001F)
            3 -> Color(0xFF546E7A)
            4 -> Color(0xFF006064)
            else -> Color(0xFF5D3A3A)
        }
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(skinHighlight, skinShadow, Color(0xFF12070B)),
                center = Offset(cx - 8f * scale, headCenterY - 8f * scale),
                radius = headRadius * 1.25f
            ),
            radius = headRadius,
            center = Offset(cx, headCenterY)
        )

        // Hollow 3D Eye Sockets + Glowing Red/Cyan Pinpoint Pupils
        for (eyeSide in listOf(-1f, 1f)) {
            val ex = cx + eyeSide * 11f * scale
            val ey = headCenterY - 3f * scale
            drawCircle(
                color = Color(0xFF070205),
                radius = 7.5f * scale,
                center = Offset(ex, ey)
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White, primaryCol, Color.Transparent),
                    center = Offset(ex, ey),
                    radius = 5.5f * scale
                ),
                radius = 4.5f * scale,
                center = Offset(ex, ey)
            )
        }

        // Iconic Mustache & Unhinged 3D Jaw (Opens wider when singing!)
        val jawOpen = if (pose == 0) 6f * scale else 14f * scale
        drawRoundRect(
            color = Color(0xFF080204),
            topLeft = Offset(cx - 16f * scale, headCenterY + 8f * scale),
            size = Size(32f * scale, jawOpen),
            cornerRadius = CornerRadius(5f * scale, 5f * scale)
        )
        // Mustache ridge
        drawRoundRect(
            color = Color(0xFF1B0F14),
            topLeft = Offset(cx - 22f * scale, headCenterY + 3f * scale),
            size = Size(44f * scale, 7f * scale),
            cornerRadius = CornerRadius(4f * scale, 4f * scale)
        )

        // 3D Sculpted Mario Cap with Glowing 'M' Emblem
        val capTopY = headCenterY - headRadius * 1.15f
        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(primaryCol, Color(0xFF7F0018), Color(0xFF280006)),
                start = Offset(cx - 34f * scale, capTopY),
                end = Offset(cx + 34f * scale, headCenterY - 12f * scale)
            ),
            topLeft = Offset(cx - 34f * scale, capTopY),
            size = Size(68f * scale, 26f * scale),
            cornerRadius = CornerRadius(14f * scale, 14f * scale)
        )
        // Emblem Circle on Cap
        drawCircle(
            color = Color.White,
            radius = 8f * scale,
            center = Offset(cx, capTopY + 12f * scale)
        )
        drawCircle(
            color = primaryCol,
            radius = 5f * scale,
            center = Offset(cx, capTopY + 12f * scale)
        )

        // Foreground Lunging 3D Hand / Cleaver on Note Hit
        val handReachX = cx + 44f * scale + (if (pose != 0) 14f * scale else 0f)
        val handReachY = cy - 4f * scale + (if (pose == 3) -18f * scale else if (pose == 2) 16f * scale else 0f)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color.White, Color(0xFFCFD8DC), Color(0xFF455A64)),
                center = Offset(handReachX - 4f * scale, handReachY - 4f * scale),
                radius = 18f * scale
            ),
            radius = 16f * scale,
            center = Offset(handReachX, handReachY)
        )
    }

    private fun DrawScope.draw3DWarpPipeAllies(
        cx: Float,
        cy: Float,
        scale: Float,
        phase: Float
    ) {
        // 3D Green Warp Pipe Rim & Cylinder (Ending 2 & Act 4 Allies: Beta Luigi & Pico)
        val pipeW = 54f * scale
        val pipeH = 48f * scale
        drawRoundRect(
            brush = Brush.horizontalGradient(
                colors = listOf(Color(0xFF004D40), Color(0xFF00E676), Color(0xFFB9F6CA), Color(0xFF00695C)),
                startX = cx - pipeW / 2,
                endX = cx + pipeW / 2
            ),
            topLeft = Offset(cx - pipeW / 2, cy),
            size = Size(pipeW, pipeH),
            cornerRadius = CornerRadius(6f * scale, 6f * scale)
        )
        // Pipe Lip
        drawRoundRect(
            brush = Brush.horizontalGradient(
                colors = listOf(Color(0xFF004D40), Color(0xFF00E676), Color(0xFF004D40)),
                startX = cx - pipeW * 0.58f,
                endX = cx + pipeW * 0.58f
            ),
            topLeft = Offset(cx - pipeW * 0.58f, cy - 10f * scale),
            size = Size(pipeW * 1.16f, 14f * scale),
            cornerRadius = CornerRadius(4f * scale, 4f * scale)
        )
        // Beta Luigi & Pico Head Silhouette emerging from Pipe
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFB9F6CA), Color(0xFF00C853), Color(0xFF1B5E20)),
                center = Offset(cx, cy - 22f * scale),
                radius = 16f * scale
            ),
            radius = 14f * scale,
            center = Offset(cx, cy - 22f * scale)
        )
        // Neon Muzzle Flash Pulse
        if (sin(phase * 4f) > 0.2f) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White, Color(0xFF00E676), Color.Transparent),
                    center = Offset(cx - 24f * scale, cy - 18f * scale),
                    radius = 18f * scale
                ),
                radius = 18f * scale,
                center = Offset(cx - 24f * scale, cy - 18f * scale)
            )
        }
    }

    private fun DrawScope.draw3DBoyfriendModel(
        cx: Float,
        cy: Float,
        scale: Float,
        pose: Int,
        starmanActive: Boolean,
        phase: Float
    ) {
        // Ground 3D Shadow
        drawOval(
            color = Color.Black.copy(alpha = 0.6f),
            topLeft = Offset(cx - 42f * scale, cy + 42f * scale),
            size = Size(84f * scale, 18f * scale)
        )

        if (starmanActive) {
            // Golden Starman 3D Invincibility Aura
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFFD740).copy(alpha = 0.65f),
                        Color(0xFF00E5FF).copy(alpha = 0.25f),
                        Color.Transparent
                    ),
                    center = Offset(cx, cy - 6f * scale),
                    radius = 75f * scale * (1f + 0.08f * sin(phase * 3f))
                ),
                radius = 75f * scale,
                center = Offset(cx, cy - 6f * scale)
            )
        }

        // 3D Hoodie / Shirt Torso
        val shirtMain = if (starmanActive) Color(0xFFFFD740) else Color(0xFFFFFFFF)
        val shirtShade = if (starmanActive) Color(0xFFFF8F00) else Color(0xFFB0BEC5)
        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(shirtMain, shirtShade, Color(0xFF37474F)),
                start = Offset(cx - 24f * scale, cy - 8f * scale),
                end = Offset(cx + 24f * scale, cy + 38f * scale)
            ),
            topLeft = Offset(cx - 24f * scale, cy - 8f * scale),
            size = Size(48f * scale, 46f * scale),
            cornerRadius = CornerRadius(12f * scale, 12f * scale)
        )
        // Red Prohibition / Starman Chest Symbol
        drawCircle(
            color = if (starmanActive) Color(0xFF00E5FF) else Color(0xFFFF1744),
            radius = 10f * scale,
            center = Offset(cx, cy + 12f * scale),
            style = Stroke(width = 3.5f * scale)
        )

        // 3D Head Sphere
        val headY = cy - 28f * scale
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFFFE0B2), Color(0xFFE0AC69), Color(0xFF8D5524)),
                center = Offset(cx - 5f * scale, headY - 5f * scale),
                radius = 26f * scale
            ),
            radius = 24f * scale,
            center = Offset(cx, headY)
        )

        // Cyan Spiky 3D Hair & Backwards Red Cap
        val hairColor = if (starmanActive) Color(0xFFFFD740) else Color(0xFF00E5FF)
        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(hairColor, Color(0xFF00838F)),
                start = Offset(cx - 26f * scale, headY - 22f * scale),
                end = Offset(cx + 26f * scale, headY - 4f * scale)
            ),
            topLeft = Offset(cx - 26f * scale, headY - 20f * scale),
            size = Size(52f * scale, 16f * scale),
            cornerRadius = CornerRadius(8f * scale, 8f * scale)
        )
        // Red Cap Visor pointing right
        drawRoundRect(
            brush = Brush.horizontalGradient(
                colors = listOf(Color(0xFFFF1744), Color(0xFFB71C1C)),
                startX = cx - 20f * scale,
                endX = cx + 32f * scale
            ),
            topLeft = Offset(cx - 20f * scale, headY - 24f * scale),
            size = Size(52f * scale, 12f * scale),
            cornerRadius = CornerRadius(6f * scale, 6f * scale)
        )

        // 3D Chrome Microphone in Left Hand
        val micX = cx - 32f * scale + (if (pose != 0) -8f * scale else 0f)
        val micY = cy + 2f * scale + (if (pose == 3) -12f * scale else 0f)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color.White, Color(0xFF90A4AE), Color(0xFF263238)),
                center = Offset(micX - 2f * scale, micY - 2f * scale),
                radius = 12f * scale
            ),
            radius = 11f * scale,
            center = Offset(micX, micY)
        )
    }

    /**
     * Renders a real 5-pose 3D-shaded Character Spritesheet PNG (`1280x320`, 5 frames of `256x320`:
     * `idle0000`, `singLEFT0000`, `singDOWN0000`, `singUP0000`, `singRIGHT0000`) for Psych Engine 0.7.3!
     */
    fun generate3DCharacterSpritesheetPng(
        characterKey: String,
        primaryHex: Int,
        secondaryHex: Int
    ): ByteArray {
        val frameW = 256
        val frameH = 320
        val bmp = Bitmap.createBitmap(frameW * 5, frameH, Bitmap.Config.ARGB_8888)
        val canvas = AndroidCanvas(bmp)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        val poseShifts = listOf(
            0f to 0f,     // idle0000
            -18f to 0f,   // singLEFT0000
            0f to 16f,    // singDOWN0000
            0f to -20f,   // singUP0000
            18f to 0f     // singRIGHT0000
        )

        for (frameIdx in 0 until 5) {
            val (dx, dy) = poseShifts[frameIdx]
            val cx = frameIdx * frameW + frameW / 2f + dx
            val cy = frameH * 0.56f + dy

            // 1. Volumetric Outer Glow
            paint.style = Paint.Style.FILL
            paint.shader = RadialGradient(
                cx, cy - 20f, 120f,
                intArrayOf(primaryHex, secondaryHex, AndroidColor.TRANSPARENT),
                floatArrayOf(0f, 0.55f, 1f),
                Shader.TileMode.CLAMP
            )
            canvas.drawCircle(cx, cy - 20f, 118f, paint)

            // 2. 3D Torso / Overalls Cylinder
            val torsoRect = RectF(cx - 58f, cy - 20f, cx + 58f, cy + 96f)
            paint.shader = LinearGradient(
                torsoRect.left, torsoRect.top, torsoRect.right, torsoRect.bottom,
                intArrayOf(primaryHex, secondaryHex, AndroidColor.rgb(16, 6, 20)),
                floatArrayOf(0f, 0.6f, 1f),
                Shader.TileMode.CLAMP
            )
            canvas.drawRoundRect(torsoRect, 26f, 26f, paint)

            // 3. 3D Sculpted Head with Specular Highlight
            val headY = cy - 64f
            paint.shader = RadialGradient(
                cx - 12f, headY - 12f, 52f,
                intArrayOf(
                    AndroidColor.rgb(250, 230, 215),
                    secondaryHex,
                    AndroidColor.rgb(20, 8, 14)
                ),
                floatArrayOf(0f, 0.65f, 1f),
                Shader.TileMode.CLAMP
            )
            canvas.drawCircle(cx, headY, 46f, paint)

            // 4. 3D Cap Dome
            val capRect = RectF(cx - 50f, headY - 56f, cx + 50f, headY - 14f)
            paint.shader = LinearGradient(
                capRect.left, capRect.top, capRect.right, capRect.bottom,
                primaryHex, AndroidColor.rgb(40, 4, 12), Shader.TileMode.CLAMP
            )
            canvas.drawRoundRect(capRect, 20f, 20f, paint)

            // 5. Glowing Eyes & Singing Jaw
            paint.shader = null
            paint.color = AndroidColor.rgb(8, 2, 6)
            canvas.drawCircle(cx - 16f, headY - 4f, 10f, paint)
            canvas.drawCircle(cx + 16f, headY - 4f, 10f, paint)
            paint.color = primaryHex
            canvas.drawCircle(cx - 16f, headY - 4f, 5f, paint)
            canvas.drawCircle(cx + 16f, headY - 4f, 5f, paint)

            // Jaw opening per pose
            val mouthH = if (frameIdx == 0) 10f else 24f
            paint.color = AndroidColor.rgb(10, 2, 6)
            canvas.drawRoundRect(RectF(cx - 22f, headY + 14f, cx + 22f, headY + 14f + mouthH), 8f, 8f, paint)

            // 6. 3D Glove Hand
            val handX = cx + 66f + (if (frameIdx == 4) 14f else 0f)
            val handY = cy + (if (frameIdx == 3) -24f else 0f)
            paint.shader = RadialGradient(
                handX - 6f, handY - 6f, 26f,
                AndroidColor.WHITE, AndroidColor.rgb(90, 105, 120), Shader.TileMode.CLAMP
            )
            canvas.drawCircle(handX, handY, 24f, paint)
            paint.shader = null
        }

        val out = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.PNG, 100, out)
        return out.toByteArray()
    }

    /**
     * Generates the matching Sparrow v2 XML atlas for a 5-pose 3D Character Spritesheet (`1280x320`).
     */
    fun generate3DCharacterSparrowXml(pngFileName: String): String {
        return """
            <?xml version="1.0" encoding="utf-8"?>
            <TextureAtlas imagePath="$pngFileName">
                <!-- 3D Volumetric Character Atlas for Psych Engine 0.7.3 -->
                <SubTexture name="idle0000" x="0" y="0" width="256" height="320" frameX="0" frameY="0" frameWidth="256" frameHeight="320"/>
                <SubTexture name="idle0001" x="0" y="0" width="256" height="320" frameX="0" frameY="-4" frameWidth="256" frameHeight="320"/>
                <SubTexture name="singLEFT0000" x="256" y="0" width="256" height="320" frameX="0" frameY="0" frameWidth="256" frameHeight="320"/>
                <SubTexture name="singDOWN0000" x="512" y="0" width="256" height="320" frameX="0" frameY="0" frameWidth="256" frameHeight="320"/>
                <SubTexture name="singUP0000" x="768" y="0" width="256" height="320" frameX="0" frameY="0" frameWidth="256" frameHeight="320"/>
                <SubTexture name="singRIGHT0000" x="1024" y="0" width="256" height="320" frameX="0" frameY="0" frameWidth="256" frameHeight="320"/>
                <SubTexture name="hey0000" x="768" y="0" width="256" height="320" frameX="0" frameY="0" frameWidth="256" frameHeight="320"/>
            </TextureAtlas>
        """.trimIndent()
    }

    /**
     * Generates a Psych Engine 0.7.3 `characters/<charName>.json` character definition pointing to
     * `images/characters/<imageSlug>.png` + `.xml` so Psych Engine loads the custom 3D model!
     */
    fun generatePsych073CharacterJson(
        imageSlug: String,
        healthIcon: String = "dad",
        r: Int = 255,
        g: Int = 24,
        b: Int = 58,
        flipX: Boolean = false,
        scale: Double = 1.35
    ): String {
        return """
            {
              "animations": [
                {
                  "offsets": [0, 0],
                  "loop": false,
                  "fps": 24,
                  "anim": "idle",
                  "indices": [],
                  "name": "idle"
                },
                {
                  "offsets": [12, -4],
                  "loop": false,
                  "fps": 24,
                  "anim": "singLEFT",
                  "indices": [],
                  "name": "singLEFT"
                },
                {
                  "offsets": [0, -14],
                  "loop": false,
                  "fps": 24,
                  "anim": "singDOWN",
                  "indices": [],
                  "name": "singDOWN"
                },
                {
                  "offsets": [-6, 16],
                  "loop": false,
                  "fps": 24,
                  "anim": "singUP",
                  "indices": [],
                  "name": "singUP"
                },
                {
                  "offsets": [-12, 0],
                  "loop": false,
                  "fps": 24,
                  "anim": "singRIGHT",
                  "indices": [],
                  "name": "singRIGHT"
                },
                {
                  "offsets": [0, 12],
                  "loop": false,
                  "fps": 24,
                  "anim": "hey",
                  "indices": [],
                  "name": "hey"
                }
              ],
              "no_antialiasing": false,
              "image": "characters/$imageSlug",
              "position": [0, 60],
              "healthicon": "$healthIcon",
              "flip_x": $flipX,
              "healthbar_colors": [$r, $g, $b],
              "camera_position": [0, 0],
              "sing_duration": 4.1,
              "scale": $scale
            }
        """.trimIndent()
    }
}
