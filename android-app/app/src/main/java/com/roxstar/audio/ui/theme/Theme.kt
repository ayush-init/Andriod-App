package com.roxstar.audio.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

private val DarkColorScheme = darkColorScheme(
    primary = RoxstarPrimary,
    secondary = RoxstarAccent,
    tertiary = RoxstarViolet,
    background = DarkCanvas,
    surface = DarkGlassSurface,
    onPrimary = DarkTextPrimary,
    onSecondary = DarkTextPrimary,
    onTertiary = DarkTextPrimary,
    onBackground = DarkTextPrimary,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkGlassSurfaceLight,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkGlassBorder
)

private val LightColorScheme = lightColorScheme(
    primary = RoxstarPrimary,
    secondary = RoxstarAccent,
    tertiary = RoxstarViolet,
    background = LightCanvas,
    surface = LightGlassSurface,
    onPrimary = LightTextPrimary,
    onSecondary = LightTextPrimary,
    onTertiary = LightTextPrimary,
    onBackground = LightTextPrimary,
    onSurface = LightTextPrimary,
    surfaceVariant = LightGlassSurfaceHighlight,
    onSurfaceVariant = LightTextSecondary,
    outline = LightGlassBorderSubtle
)

@Composable
fun RoxstarTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val glassColors = if (darkTheme) DarkGlassColors else LightGlassColors

    CompositionLocalProvider(
        LocalGlassColors provides glassColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = RoxstarTypography,
            content = content
        )
    }
}
