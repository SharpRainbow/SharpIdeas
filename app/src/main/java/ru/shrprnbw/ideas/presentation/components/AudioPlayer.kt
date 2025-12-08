package ru.shrprnbw.ideas.presentation.components

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.common.PlaybackException
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import ru.shrprnbw.ideas.presentation.ui.theme.CustomIcons

@Composable
fun AudioMessagePlayer(
    modifier: Modifier = Modifier,
    compactHeight: Dp = 48.dp,
    player: ExoPlayer
) {

    var positionMs by remember { mutableStateOf(0L) }
    var durationMs by remember { mutableStateOf(0L) }
    var isUserSeeking by remember { mutableStateOf(false) }
    var seekTemp by remember { mutableStateOf(0L) }

    LaunchedEffect(player) {
        while (isActive) {
            if (!isUserSeeking)
                positionMs = player.currentPosition
            val d = player.duration
            durationMs = if (d > 0) d else 0L
            delay(200L)
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(compactHeight)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = {
                if (player.isPlaying) player.pause() else player.play()
            }
        ) {
            if (player.isPlaying)
                Icon(Icons.Rounded.Pause, contentDescription = "Pause")
            else
                Icon(Icons.Rounded.PlayArrow, contentDescription = "Play")
        }

        Column(modifier = Modifier
            .weight(1f)
            .padding(start = 4.dp, end = 8.dp)) {
            val sliderEnabled = durationMs > 0L
            val sliderValue = when {
                isUserSeeking -> seekTemp.toFloat()
                sliderEnabled -> positionMs.toFloat()
                else -> 0f
            }
            Slider(
                value = sliderValue,
                onValueChange = { newValue ->
                    isUserSeeking = true
                    seekTemp = newValue.toLong()
                },
                onValueChangeFinished = {
                    isUserSeeking = false
                    val target = if (sliderEnabled) seekTemp.coerceIn(0L, durationMs) else 0L
                    if (target >= 0L) player.seekTo(target)
                },
                valueRange = 0f..(if (sliderEnabled) durationMs.toFloat() else 1f),
                enabled = sliderEnabled,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                if (player.isPlaying)
                    formatTime(positionMs)
                else
                    formatTime(durationMs),
                modifier = Modifier.padding(bottom = 2.dp)
            )
        }
    }
}

private fun formatTime(ms: Long): String {
    if (ms <= 0L) return "00:00"
    val totalSeconds = ms / 1000
    val seconds = (totalSeconds % 60).toInt()
    val minutes = ((totalSeconds / 60) % 60).toInt()
    val hours = (totalSeconds / 3600).toInt()
    return if (hours > 0) String.format("%d:%02d:%02d", hours, minutes, seconds)
    else String.format("%02d:%02d", minutes, seconds)
}