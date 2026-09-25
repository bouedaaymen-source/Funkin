package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val FnfColorScheme = darkColorScheme(
    primary = FnfCyan,
    onPrimary = FnfDarkBg,
    primaryContainer = FnfSurfaceElevated,
    onPrimaryContainer = FnfCyan,
    secondary = FnfPink,
    onSecondary = FnfDarkBg,
    secondaryContainer = FnfSurfaceElevated,
    onSecondaryContainer = FnfPink,
    tertiary = FnfPurple,
    onTertiary = FnfDarkBg,
    background = FnfDarkBg,
    onBackground = FnfTextPrimary,
    surface = FnfSurface,
    onSurface = FnfTextPrimary,
    surfaceVariant = FnfSurfaceElevated,
    onSurfaceVariant = FnfTextSecondary,
    outline = FnfBorder,
    error = FnfRed,
    onError = FnfDarkBg
)

@Composable
fun FnfTheme(
    darkTheme: Boolean = true, // Rhythm game aesthetic is best experienced in dark mode
    content: @Composable () -> Unit
) {
    val colorScheme = FnfColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = FnfDarkBg.toArgb()
                window.navigationBarColor = FnfDarkBg.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
