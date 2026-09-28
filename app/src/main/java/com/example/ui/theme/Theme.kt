package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val TownColorScheme = darkColorScheme(
    primary = TownOrange,
    onPrimary = TownDarkBg,
    primaryContainer = TownSurfaceElevated,
    onPrimaryContainer = TownCream,
    secondary = TownGreen,
    onSecondary = TownDarkBg,
    secondaryContainer = TownSurfaceElevated,
    onSecondaryContainer = TownGreen,
    tertiary = TownDiscord,
    onTertiary = TownTextPrimary,
    background = TownDarkBg,
    onBackground = TownTextPrimary,
    surface = TownSurface,
    onSurface = TownTextPrimary,
    surfaceVariant = TownSurfaceElevated,
    onSurfaceVariant = TownTextSecondary,
    outline = TownBorder,
    error = TownRed,
    onError = TownDarkBg
)

@Composable
fun TownTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = TownDarkBg.toArgb()
                window.navigationBarColor = TownDarkBg.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = TownColorScheme,
        typography = Typography,
        content = content
    )
}
