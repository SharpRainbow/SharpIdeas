package ru.shrprnbw.ideas.utils

import androidx.compose.ui.graphics.Color

object Utils {

    fun generateColor(input: String): Color {
        val hash = input.hashCode()
        val r = ((hash shr 16) and 0xFF).coerceIn(64, 192)
        val g = ((hash shr 8) and 0xFF).coerceIn(64, 192)
        val b = (hash and 0xFF).coerceIn(64, 192)
        return Color(r / 255f, g / 255f, b / 255f)
    }

    fun generateColorContrast(input: String): Color {
        val hash = input.hashCode()
        val hue = (hash % 360).toFloat().coerceIn(0f, 360f)
        val saturation = 0.5f
        val lightness = 0.4f
        return Color.hsl(hue, saturation, lightness)
    }

}