@file:OptIn(ExperimentalSharedTransitionApi::class)

package ru.shrprnbw.ideas.presentation.screens.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AudioFile
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.TextFields
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.shrprnbw.ideas.domain.entity.Tag
import ru.shrprnbw.ideas.presentation.navigation.Screen
import ru.shrprnbw.ideas.presentation.screens.notes_list.NoteType
import ru.shrprnbw.ideas.utils.Utils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    onBackClicked: () -> Unit,
    sharedTransitionScope: SharedTransitionScope?,
    animatedVisibilityScope: AnimatedVisibilityScope?
) {
    Scaffold(
        modifier = modifier.fillMaxWidth(),
    ) { innerPadding ->
        val focusRequester = remember { FocusRequester() }
        val keyboardController = LocalSoftwareKeyboardController.current

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
            keyboardController?.show()
        }

        Column(
            modifier = Modifier.padding(innerPadding),
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            with(sharedTransitionScope ?: return@Scaffold) {
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .sharedElement(
                            sharedContentState = rememberSharedContentState(key = Screen.Search.ANIMATION_KEY),
                            animatedVisibilityScope = animatedVisibilityScope ?: return@Scaffold,
                            boundsTransform = { _, _ ->
                                tween(durationMillis = Screen.Search.ANIMATION_DUR)
                            }
                        )
                        .focusRequester(focusRequester),
                    value = "",
                    onValueChange = { },
                    placeholder = {
                        Text(
                            text = "Поиск заметок",
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.clickable(
                                onClick = onBackClicked
                            )
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        focusedContainerColor = MaterialTheme.colorScheme.background,
                        unfocusedContainerColor = MaterialTheme.colorScheme.background
                    )
                )
            }

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp
            )

            // Search results would go here
        }
    }
}

@Composable
fun FilterOptions(
    modifier: Modifier = Modifier,
    availableTags: Map<Tag, Boolean>,
    onTagToggle: (Long) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        availableTags.forEach { tag ->
            item(
                key = tag.key
            ) {
                TagChip(
                    tag = tag.key,
                    isSelected = tag.value,
                    onTagToggle = onTagToggle
                )
            }
        }
    }
}

@Composable
private fun TagChip(
    modifier: Modifier = Modifier,
    tag: Tag,
    isSelected: Boolean,
    onTagToggle: (Long) -> Unit,
) {
    FilterChip(
        modifier = modifier,
        selected = isSelected,
        onClick = {
            onTagToggle(tag.id)
        },
        label = {
            Text(text = tag.name)
        },
        leadingIcon = {
            AnimatedVisibility(
                visible = isSelected,
                enter = expandHorizontally(expandFrom = Alignment.Start),
                exit = shrinkHorizontally(shrinkTowards = Alignment.Start)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Done,
                    contentDescription = "Selected",
                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                )
            }
        }
    )
}

@Composable
private fun NoteItem(
    modifier: Modifier = Modifier,
    noteId: String = "",
    title: String,
    preview: String,
    time: String,
    type: NoteType,
    duration: String? = null,
    transformations: List<String>,
    onNoteClicked: (String) -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                onClick = { onNoteClicked(noteId) }
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Column {
                Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                if (preview.isNotBlank()) {
                    Text(
                        text = preview,
                        fontSize = 14.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (type == NoteType.Text) {
                            NoteLabel(
                                label = "Текст",
                                color = Color(0xFF0061A4),
                                imageVector = Icons.Rounded.TextFields
                            )
                        } else {
                            NoteLabel(
                                label = "Аудио",
                                color = Color(0xFF388E3C),
                                imageVector = Icons.Rounded.AudioFile
                            )
                        }
                        for (t in transformations) {
                            NoteLabel(
                                label = t,
                                color = Utils.generateColorContrast(t),
                                imageVector = Icons.Rounded.GraphicEq
                            )
                        }
                    }
                }
                Text(
                    modifier = Modifier.wrapContentWidth(),
                    text = time,
                    color = Color.Gray,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun NoteLabel(
    modifier: Modifier = Modifier,
    label: String,
    color: Color,
    imageVector: ImageVector
) {
    Row(
        modifier = modifier
            .background(
                color = color.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(16.dp)
        )
        Spacer(Modifier.width(4.dp))
        Text(label, color = color, fontSize = 12.sp)
    }
}