package com.example.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

enum class OusimThemePreset(
    val id: String,
    val titleEn: String,
    val titleAr: String,
    val titleFr: String,
    val subtitle: String,
    val previewPrimary: Color,
    val previewSecondary: Color,
    val previewBg: Color
) {
    NOTIBYTE_BLUE_RED_LIGHT(
        id = "NOTIBYTE_BLUE_RED_LIGHT",
        titleEn = "NotiByte Blue & Red (Light)",
        titleAr = "نوتي بايت أزرق وأحمر (فاتح)",
        titleFr = "NotiByte Bleu & Rouge (Clair)",
        subtitle = "Official NotiByte Clean White with Blue & Djezzy Red",
        previewPrimary = Color(0xFF0D6EFD),
        previewSecondary = Color(0xFFE30613),
        previewBg = Color(0xFFF8FAFF)
    ),
    NOTIBYTE_BLUE_RED_DARK(
        id = "NOTIBYTE_BLUE_RED_DARK",
        titleEn = "NotiByte Blue & Red (Dark)",
        titleAr = "نوتي بايت أزرق وأحمر (داكن)",
        titleFr = "NotiByte Bleu & Rouge (Sombre)",
        subtitle = "Deep Navy Blue & Crimson Red",
        previewPrimary = Color(0xFF2979FF),
        previewSecondary = Color(0xFFFF1744),
        previewBg = Color(0xFF0A101D)
    ),
    DJEZZY_CRIMSON(
        id = "DJEZZY_CRIMSON",
        titleEn = "Djezzy Red & Royal Blue",
        titleAr = "جازي أحمر وأزرق ملكي",
        titleFr = "Djezzy Rouge & Bleu Royal",
        subtitle = "Bold Crimson Red Primary & Electric Blue",
        previewPrimary = Color(0xFFE30613),
        previewSecondary = Color(0xFF0D6EFD),
        previewBg = Color(0xFFF8FAFF)
    )
}

data class OusimPalette(
    val isLight: Boolean,
    val darkBg: Color,
    val surface: Color,
    val surfaceElevated: Color,
    val border: Color,
    val primary: Color,       // Blue
    val primaryLight: Color,  // Light Blue
    val redAccent: Color,     // Red
    val green: Color,
    val gold: Color,
    val cyan: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color
)

object OusimThemeManager {
    var activePreset by mutableStateOf(OusimThemePreset.NOTIBYTE_BLUE_RED_LIGHT)
        private set

    var customAccentOverride by mutableStateOf<Color?>(null)
        private set

    var currentPalette by mutableStateOf(paletteFor(OusimThemePreset.NOTIBYTE_BLUE_RED_LIGHT, null))
        private set

    fun applyTheme(preset: OusimThemePreset, accentOverride: Color? = customAccentOverride) {
        activePreset = preset
        customAccentOverride = accentOverride
        currentPalette = paletteFor(preset, accentOverride)
    }

    fun applyThemeById(presetId: String, customHex: Long? = null) {
        val preset = OusimThemePreset.entries.find { it.id == presetId }
            ?: OusimThemePreset.NOTIBYTE_BLUE_RED_LIGHT
        val overrideColor = customHex?.takeIf { it != 0L }?.let { Color(it.toULong()) }
        applyTheme(preset, overrideColor)
    }

    fun setCustomAccent(color: Color?) {
        customAccentOverride = color
        currentPalette = paletteFor(activePreset, color)
    }

    private fun paletteFor(preset: OusimThemePreset, accentOverride: Color?): OusimPalette {
        val base = when (preset) {
            OusimThemePreset.NOTIBYTE_BLUE_RED_LIGHT -> OusimPalette(
                isLight = true,
                darkBg = Color(0xFFF6F9FF),
                surface = Color(0xFFFFFFFF),
                surfaceElevated = Color(0xFFEBF3FF),
                border = Color(0xFFD6E4FF),
                primary = Color(0xFF0D6EFD),
                primaryLight = Color(0xFF1E88E5),
                redAccent = Color(0xFFE30613),
                green = Color(0xFF0D6EFD),
                gold = Color(0xFFE30613),
                cyan = Color(0xFF1565C0),
                textPrimary = Color(0xFF101828),
                textSecondary = Color(0xFF344054),
                textMuted = Color(0xFF667085)
            )

            OusimThemePreset.NOTIBYTE_BLUE_RED_DARK -> OusimPalette(
                isLight = false,
                darkBg = Color(0xFF080E1A),
                surface = Color(0xFF111B2E),
                surfaceElevated = Color(0xFF182640),
                border = Color(0xFF253B63),
                primary = Color(0xFF2979FF),
                primaryLight = Color(0xFF5393FF),
                redAccent = Color(0xFFFF1744),
                green = Color(0xFF2979FF),
                gold = Color(0xFFFF1744),
                cyan = Color(0xFF40C4FF),
                textPrimary = Color(0xFFF5F8FF),
                textSecondary = Color(0xFFD0DCFF),
                textMuted = Color(0xFF8CA0C7)
            )

            OusimThemePreset.DJEZZY_CRIMSON -> OusimPalette(
                isLight = true,
                darkBg = Color(0xFFFFF8F9),
                surface = Color(0xFFFFFFFF),
                surfaceElevated = Color(0xFFFFECEF),
                border = Color(0xFFFFCDD4),
                primary = Color(0xFFE30613),
                primaryLight = Color(0xFFFF1744),
                redAccent = Color(0xFF0D6EFD),
                green = Color(0xFF0D6EFD),
                gold = Color(0xFFE30613),
                cyan = Color(0xFF1565C0),
                textPrimary = Color(0xFF141824),
                textSecondary = Color(0xFF344054),
                textMuted = Color(0xFF667085)
            )
        }

        return if (accentOverride != null) {
            base.copy(
                primary = accentOverride,
                primaryLight = accentOverride
            )
        } else {
            base
        }
    }
}

// Semantic Blue & Red NotiByte tokens
val NotiBlue: Color get() = OusimThemeManager.currentPalette.primary
val NotiBlueLight: Color get() = OusimThemeManager.currentPalette.primaryLight
val NotiRed: Color get() = OusimThemeManager.currentPalette.redAccent
val NotiBg: Color get() = OusimThemeManager.currentPalette.darkBg
val NotiSurface: Color get() = OusimThemeManager.currentPalette.surface
val NotiSurfaceSoft: Color get() = OusimThemeManager.currentPalette.surfaceElevated
val NotiBorder: Color get() = OusimThemeManager.currentPalette.border

// Backwards-compatible aliases
val DjezzyDarkBg: Color get() = OusimThemeManager.currentPalette.darkBg
val DjezzySurface: Color get() = OusimThemeManager.currentPalette.surface
val DjezzySurfaceElevated: Color get() = OusimThemeManager.currentPalette.surfaceElevated
val DjezzyBorder: Color get() = OusimThemeManager.currentPalette.border

val DjezzyRed: Color get() = OusimThemeManager.currentPalette.redAccent
val DjezzyRedLight: Color get() = OusimThemeManager.currentPalette.primary
val OusimGreen: Color get() = OusimThemeManager.currentPalette.primary
val OusimGold: Color get() = OusimThemeManager.currentPalette.redAccent
val OusimCyan: Color get() = OusimThemeManager.currentPalette.cyan

val DjezzyTextPrimary: Color get() = OusimThemeManager.currentPalette.textPrimary
val DjezzyTextSecondary: Color get() = OusimThemeManager.currentPalette.textSecondary
val DjezzyTextMuted: Color get() = OusimThemeManager.currentPalette.textMuted
