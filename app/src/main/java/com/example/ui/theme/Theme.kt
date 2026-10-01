package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun DjezzyOusimTheme(
    content: @Composable () -> Unit
) {
    val palette = OusimThemeManager.currentPalette

    val scheme = if (palette.isLight) {
        lightColorScheme(
            primary = palette.primary,
            onPrimary = Color.White,
            primaryContainer = palette.surfaceElevated,
            onPrimaryContainer = palette.primary,
            secondary = palette.redAccent,
            onSecondary = Color.White,
            secondaryContainer = palette.surfaceElevated,
            onSecondaryContainer = palette.redAccent,
            tertiary = palette.cyan,
            onTertiary = Color.White,
            background = palette.darkBg,
            onBackground = palette.textPrimary,
            surface = palette.surface,
            onSurface = palette.textPrimary,
            surfaceVariant = palette.surfaceElevated,
            onSurfaceVariant = palette.textSecondary,
            outline = palette.border,
            error = palette.redAccent,
            onError = Color.White
        )
    } else {
        darkColorScheme(
            primary = palette.primary,
            onPrimary = Color.White,
            primaryContainer = palette.surfaceElevated,
            onPrimaryContainer = palette.primaryLight,
            secondary = palette.redAccent,
            onSecondary = Color.White,
            secondaryContainer = palette.surfaceElevated,
            onSecondaryContainer = palette.redAccent,
            tertiary = palette.cyan,
            onTertiary = palette.darkBg,
            background = palette.darkBg,
            onBackground = palette.textPrimary,
            surface = palette.surface,
            onSurface = palette.textPrimary,
            surfaceVariant = palette.surfaceElevated,
            onSurfaceVariant = palette.textSecondary,
            outline = palette.border,
            error = palette.redAccent,
            onError = Color.White
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = palette.surface.toArgb()
                window.navigationBarColor = palette.surface.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = palette.isLight
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = palette.isLight
            }
        }
    }

    MaterialTheme(
        colorScheme = scheme,
        typography = Typography,
        content = content
    )
}
