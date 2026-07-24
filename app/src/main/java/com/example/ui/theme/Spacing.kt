package com.example.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Strongly typed design tokens for Spacing, Radius, Icons, and Touch Targets
object BloomSpacing {
    val None: Dp = 0.dp
    val XXS: Dp = 2.dp
    val XS: Dp = 4.dp
    val SM: Dp = 8.dp
    val MD: Dp = 12.dp
    val Base: Dp = 16.dp
    val LG: Dp = 24.dp
    val XL: Dp = 32.dp
    val XXL: Dp = 48.dp

    // Layout Specific
    val ScreenMargin: Dp = 16.dp
    val CardPadding: Dp = 16.dp
    val SectionSpacing: Dp = 20.dp
    val MinTouchTarget: Dp = 48.dp
}

object BloomRadius {
    val Small: Dp = 8.dp
    val Medium: Dp = 12.dp
    val Large: Dp = 16.dp
    val ExtraLarge: Dp = 24.dp
    val Pill: Dp = 50.dp
}

object BloomIconSize {
    val Small: Dp = 18.dp
    val Medium: Dp = 24.dp
    val Large: Dp = 32.dp
    val ExtraLarge: Dp = 48.dp
    val HeroAvatar: Dp = 72.dp
}

object BloomElevation {
    val Level0: Dp = 0.dp
    val Level1: Dp = 2.dp
    val Level2: Dp = 4.dp
    val Level3: Dp = 8.dp
}
