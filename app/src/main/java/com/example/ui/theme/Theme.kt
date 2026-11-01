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

private val DarkColorScheme = darkColorScheme(
    primary = EmeraldPrimary,
    onPrimary = EmeraldDeepDark,
    primaryContainer = EmeraldCardDark,
    onPrimaryContainer = EmeraldLight,
    secondary = GoldAccent,
    onSecondary = EmeraldDeepDark,
    secondaryContainer = EmeraldBorderDark,
    onSecondaryContainer = GoldAccentLight,
    tertiary = EmeraldLight,
    background = EmeraldDeepDark,
    onBackground = TextPrimaryDark,
    surface = EmeraldSurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = EmeraldCardDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = EmeraldBorderDark
)

private val LightColorScheme = lightColorScheme(
    primary = EmeraldPrimaryLight,
    onPrimary = EmeraldSurfaceLight,
    primaryContainer = EmeraldBorderLight,
    onPrimaryContainer = EmeraldPrimaryLight,
    secondary = GoldAccent,
    onSecondary = EmeraldSurfaceLight,
    secondaryContainer = EmeraldBackgroundLight,
    onSecondaryContainer = TextPrimaryLight,
    tertiary = EmeraldPrimary,
    background = EmeraldBackgroundLight,
    onBackground = TextPrimaryLight,
    surface = EmeraldSurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = EmeraldBorderLight,
    onSurfaceVariant = TextSecondaryLight,
    outline = EmeraldBorderLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to Islamic emerald dark mode as on the web app
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.background.toArgb()
                window.navigationBarColor = colorScheme.background.toArgb()
                WindowCompat.getInsetsController(window, view).apply {
                    isAppearanceLightStatusBars = !darkTheme
                    isAppearanceLightNavigationBars = !darkTheme
                }
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
