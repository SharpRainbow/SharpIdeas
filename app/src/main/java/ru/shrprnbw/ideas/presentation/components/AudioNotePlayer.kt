@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package ru.shrprnbw.ideas.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import ru.shrprnbw.ideas.presentation.ui.theme.AudioGreen
import ru.shrprnbw.ideas.utils.Utils

@Composable
fun AudioNotePlayer(
    modifier: Modifier = Modifier,
    audioUrl: String,
    accentColor: Color = AudioGreen
) {
    val context = LocalContext.current

    val player = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(audioUrl))
            prepare()
        }
    }

    var isPlaying by remember { mutableStateOf(false) }
    var currentPosition by remember { mutableLongStateOf(0L) }
    var duration by remember { mutableLongStateOf(0L) }
    var progressBarWidth by remember { mutableIntStateOf(0) }
    var isDragging by remember { mutableStateOf(false) }
    var dragPosition by remember { mutableLongStateOf(0L) }

    LaunchedEffect(player) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
            }
        }
        player.addListener(listener)

        while (isActive) {
            currentPosition = player.currentPosition
            val d = player.duration
            duration = if (d > 0) d else 0L
            delay(100L)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            player.release()
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.surfaceVariant,
                    RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        accentColor.copy(alpha = 0.2f),
                        CircleShape
                    ).clip(
                        CircleShape
                    )
                    .clickable {
                        if (isPlaying) {
                            player.pause()
                        } else {
                            player.play()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Пауза" else "Воспроизвести",
                    tint = accentColor
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = Utils.extractFileName(audioUrl),
                    color = accentColor,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.width(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .onSizeChanged { size ->
                                progressBarWidth = size.width
                            }
                    ) {
                        LinearWavyProgressIndicator(
                            progress = {
                                val position = if (isDragging) dragPosition else currentPosition
                                if (duration > 0) {
                                    (position.toFloat() / duration.toFloat()).coerceIn(0f, 1f)
                                } else {
                                    0f
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .pointerInput(player) {
                                    detectTapGestures { offset ->
                                        if (duration > 0 && progressBarWidth > 0) {
                                            val clickPositionRatio = (offset.x / progressBarWidth).coerceIn(0f, 1f)
                                            val targetPosition = (duration * clickPositionRatio).toLong()
                                            player.seekTo(targetPosition)
                                        }
                                    }
                                }
                                .pointerInput(player) {
                                    detectDragGestures(
                                        onDragStart = { _ ->
                                            if (duration > 0 && progressBarWidth > 0) {
                                                dragPosition = currentPosition
                                                isDragging = true
                                            }
                                        },
                                        onDrag = { change, _ ->
                                            if (duration > 0 && progressBarWidth > 0) {
                                                val dragPositionRatio = (change.position.x / progressBarWidth).coerceIn(0f, 1f)
                                                dragPosition = (duration * dragPositionRatio).toLong()
                                            }
                                        },
                                        onDragEnd = {
                                            player.seekTo(dragPosition)
                                            currentPosition = dragPosition
                                            isDragging = false
                                        },
                                        onDragCancel = {
                                            isDragging = false
                                        }
                                    )
                                },
                            color = accentColor,
                            trackColor = accentColor.copy(alpha = 0.2f),
                            amplitude = {if (isPlaying) 1f else 0f},
                        )
                    }

                    Spacer(Modifier.width(8.dp))

                    Text(
                        text = Utils.formatTime(
                            when {
                                isDragging -> dragPosition
                                isPlaying -> currentPosition
                                else -> duration
                            }
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
