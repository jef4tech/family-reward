package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = SageGreen500,
    onPrimary = SoftCreamSurface,
    primaryContainer = SageGreen100,
    onPrimaryContainer = SageGreen700,
    secondary = WarmCoral500,
    onSecondary = SoftCreamSurface,
    secondaryContainer = WarmCoral100,
    onSecondaryContainer = WarmCoral700,
    tertiary = Amber500,
    onTertiary = SoftCreamSurface,
    tertiaryContainer = Amber100,
    onTertiaryContainer = Amber600,
    background = SoftCreamBackground,
    onBackground = OnSurfaceText,
    surface = SoftCreamSurface,
    onSurface = OnSurfaceText,
    surfaceVariant = SoftCreamSurfaceVariant,
    onSurfaceVariant = OnSurfaceTextSecondary,
    outline = OutlineColor,
    error = ErrorRed,
    onError = SoftCreamSurface
)

private val DarkColorScheme = darkColorScheme(
    primary = SageGreen200,
    onPrimary = SageGreen700,
    primaryContainer = SageGreen600,
    onPrimaryContainer = SageGreen50,
    secondary = WarmCoral100,
    onSecondary = WarmCoral700,
    secondaryContainer = WarmCoral600,
    onSecondaryContainer = WarmCoral50,
    tertiary = Amber100,
    onTertiary = Amber600,
    background = DarkBackground,
    onBackground = DarkOnSurfaceText,
    surface = DarkSurface,
    onSurface = DarkOnSurfaceText,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceTextSecondary,
    outline = DarkOutlineColor,
    error = ErrorRed,
    onError = SoftCreamSurface
)

@Composable
fun BloomFamilyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
