package com.example.depgen.utils

import androidx.compose.ui.graphics.Color

fun Color.reverseSettings(): Pair<Color, Float> {
    val r = this.red
    val g = this.green
    val b = this.blue

    val brightness = maxOf(r, g, b).coerceAtLeast(0.0001f)

    val baseR = (r / brightness).coerceIn(0f, 1f)
    val baseG = (g / brightness).coerceIn(0f, 1f)
    val baseB = (b / brightness).coerceIn(0f, 1f)

    val selectedColor = Color(baseR, baseG, baseB, this.alpha)
    return selectedColor to brightness
}

fun Color.applyBrightness(brightness: Float): Color {
    val r = (this.red * brightness).coerceIn(0f, 1f)
    val g = (this.green * brightness).coerceIn(0f, 1f)
    val b = (this.blue * brightness).coerceIn(0f, 1f)
    return Color(r, g, b, this.alpha)
}

