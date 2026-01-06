package ru.shrprnbw.ideas.utils

import androidx.compose.ui.graphics.Color
import java.net.URLDecoder

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

    fun formatTime(ms: Long): String {
        if (ms <= 0L) return "00:00"
        val totalSeconds = ms / 1000
        val seconds = (totalSeconds % 60).toInt()
        val minutes = ((totalSeconds / 60) % 60).toInt()
        val hours = (totalSeconds / 3600).toInt()
        return if (hours > 0) {
            String.format("%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }

    fun extractFileName(s3Url: String): String {
        return try {
            val lastSegment = s3Url.split('/').lastOrNull() ?: return "Аудиозапись"
            val fileNameWithUuid = lastSegment.split('?').firstOrNull() ?: return "Аудиозапись"

            val uuidPattern = """^[a-fA-F0-9-]{36}_(.+)$""".toRegex()
            val matchResult = uuidPattern.find(fileNameWithUuid)

            URLDecoder.decode(matchResult?.groupValues?.getOrNull(1) ?: fileNameWithUuid, "UTF-8")
        } catch (e: Exception) {
            "Аудиозапись"
        }
    }

}