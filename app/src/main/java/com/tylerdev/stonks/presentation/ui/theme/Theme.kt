package com.tylerdev.stonks.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Green80,
    onPrimary = Green20,
    primaryContainer = Green30,
    onPrimaryContainer = Green90,
    secondary = GreenGrey80,
    onSecondary = GreenGrey30,
    secondaryContainer = Color(0xFF374B3D),
    onSecondaryContainer = GreenGrey90,
    tertiary = Teal80,
    onTertiary = Teal30,
    tertiaryContainer = Color(0xFF004D61),
    onTertiaryContainer = Teal90,
    background = DarkSurface,
    onBackground = Color(0xFFE2E3DF),
    surface = DarkSurface,
    onSurface = Color(0xFFE2E3DF),
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFC0C9C1),
    outline = Color(0xFF8A938B),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
)

private val LightColorScheme = lightColorScheme(
    primary = Green40,
    onPrimary = Color.White,
    primaryContainer = Green90,
    onPrimaryContainer = Green10,
    secondary = GreenGrey50,
    onSecondary = Color.White,
    secondaryContainer = GreenGrey90,
    onSecondaryContainer = Color(0xFF0B1F12),
    tertiary = Color(0xFF38626F),
    onTertiary = Color.White,
    tertiaryContainer = Teal90,
    onTertiaryContainer = Teal20,
    background = LightSurface,
    onBackground = Color(0xFF1A1C1A),
    surface = LightSurface,
    onSurface = Color(0xFF1A1C1A),
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = Color(0xFF404942),
    outline = Color(0xFF707972),
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
)

@Composable
fun StonksTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
