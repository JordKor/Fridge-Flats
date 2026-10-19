package com.fridgeflats.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Cream = Color(0xFFFFF4E6)
val Coral = Color(0xFFE8735A)
val Ink = Color(0xFF2B2B2B)
val Muted = Color(0xFF7A6F66)
val Brick = Color(0xFFF2C9A0)
val Roof = Color(0xFF9C5B3B)
val Danger = Color(0xFFC94A3A)

@Composable
fun FridgeFlatsTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Coral,
            onPrimary = Color.White,
            secondaryContainer = Color(0xFFFFD9C9),
            onSecondaryContainer = Ink,
            background = Cream,
            onBackground = Ink,
            surface = Cream,
            onSurface = Ink,
        ),
        content = content,
    )
}
