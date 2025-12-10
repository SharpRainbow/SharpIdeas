@file:OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)

package ru.shrprnbw.ideas.presentation.screens.notes_list

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Snooze
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.AudioFile
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.TextFields
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.material3.ToggleFloatingActionButtonDefaults.animateIcon
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import ru.shrprnbw.ideas.domain.entity.Note
import ru.shrprnbw.ideas.presentation.navigation.Screen
import ru.shrprnbw.ideas.presentation.ui.theme.SharpIdeasTheme
import ru.shrprnbw.ideas.utils.Utils

@Composable
fun NoteListScreen(
    modifier: Modifier = Modifier,
    viewModel: NoteListViewModel = hiltViewModel(),
    onFabClicked: () -> Unit = {},
    onNoteClicked: (String) -> Unit = {},
    onSearchTriggered: () -> Unit = {},
    onProfileClicked: () -> Unit = {},
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null
) {
    val noteItems = viewModel.notesFlow.collectAsLazyPagingItems()

    Scaffold(
        modifier = modifier.fillMaxSize(),
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            val listState = rememberLazyListState()
            Column {
                Spacer(
                    modifier = Modifier.height(12.dp)
                )
                SearchBar(
                    onSearchTriggered = onSearchTriggered,
                    onProfileClicked = onProfileClicked,
                    animatedVisibilityScope = animatedVisibilityScope,
                    sharedTransitionScope = sharedTransitionScope
                )
                Spacer(
                    modifier = Modifier.height(16.dp)
                )
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    thickness = 1.dp
                )
                NotesList(
                    onNoteClicked = onNoteClicked,
                    notesListState = listState,
                    lazyItems = noteItems
                )
            }
            FloatingActionButtonWithMenu(
                listState = listState
            )
        }
    }
}

@Composable
private fun SearchBar(
    modifier: Modifier = Modifier,
    searchPlaceholder: String = "Поиск заметок",
    onSearchTriggered: () -> Unit = {},
    onProfileClicked: () -> Unit = {},
    sharedTransitionScope: SharedTransitionScope?,
    animatedVisibilityScope: AnimatedVisibilityScope?
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Icon(
            imageVector = Icons.Rounded.Menu,
            contentDescription = "Menu Icon",
            tint = SearchBarDefaults.appBarWithSearchColors().appBarNavigationIconColor
        )
        with(sharedTransitionScope ?: return) {
            TextField(
                modifier = Modifier
                    .weight(1f)
                    .clip(SearchBarDefaults.inputFieldShape)
                    .sharedElement(
                        sharedContentState = rememberSharedContentState(key = Screen.Search.ANIMATION_KEY),
                        animatedVisibilityScope = animatedVisibilityScope ?: return,
                        boundsTransform = { _, _ ->
                            tween(durationMillis = Screen.Search.ANIMATION_DUR)
                        },
                        clipInOverlayDuringTransition = OverlayClip(SearchBarDefaults.inputFieldShape)
                    )
                    .clickable(
                        onClick = {
                            onSearchTriggered()
                        }
                    ),
                value = "",
                onValueChange = {},
                enabled = false,
                placeholder = {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = searchPlaceholder,
                        textAlign = TextAlign.Center,
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = "Search Icon",
                        tint = SearchBarDefaults.inputFieldColors().unfocusedLeadingIconColor
                    )
                },
                trailingIcon = {},
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                )
            )
        }
        Icon(
            modifier = Modifier.clickable(
                onClick = onProfileClicked
            ),
            imageVector = Icons.Rounded.AccountCircle,
            contentDescription = "Account Icon",
            tint = SearchBarDefaults.appBarWithSearchColors().appBarActionIconColor
        )
    }
}

@Composable
private fun NotesList(
    modifier: Modifier = Modifier,
    onNoteClicked: (String) -> Unit,
    notesListState: LazyListState = rememberLazyListState(),
    lazyItems: LazyPagingItems<Note>
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(top = 12.dp),
        state = notesListState
    ) {
        items(
            count = lazyItems.itemCount,
            key = { index -> lazyItems[index]?.id ?: index }
        ) { index ->
            val item = lazyItems[index]
            if (item != null) {
                NoteItem(
                    noteId = item.id,
                    title = item.title,
                    preview = item.preview,
                    time = "",
                    type = NoteType.Text,
                    duration = "",
                    tags = item.tags,
                    onNoteClicked = onNoteClicked
                )
            }
        }
    }
}

enum class NoteType { Text, Audio }

@Composable
private fun NoteItem(
    modifier: Modifier = Modifier,
    noteId: String = "",
    title: String,
    preview: String,
    time: String,
    type: NoteType,
    duration: String? = null,
    tags: List<String> = emptyList<String>(),
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
            if (type == NoteType.Audio) {
                Spacer(modifier = Modifier.height(12.dp))
                Column(
                    modifier = Modifier.fillMaxWidth()
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
                        Icon(
                            modifier = Modifier
                                .background(
                                    Color(0xFF388E3C).copy(alpha = 0.2f),
                                    CircleShape
                                )
                                .padding(4.dp),
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color(0xFF388E3C)
                        )
                        Spacer(Modifier.width(8.dp))
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Аудиозапись", color = Color(0xFF388E3C), fontSize = 12.sp)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    LinearWavyProgressIndicator(
                                        progress = { 0.35f },
                                        color = Color(0xFF388E3C)
                                    )
                                }
                                Spacer(Modifier.width(8.dp))
                                if (duration != null) {
                                    Text(duration, color = Color.Gray, fontSize = 12.sp)
                                }
                            }
                        }
                    }
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
                        for (t in tags) {
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
                    fontSize = 12.sp
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

@Composable
fun BoxScope.FloatingActionButtonWithMenu(
    modifier: Modifier = Modifier,
    onTextNoteClicked: () -> Unit = {},
    onAudioNoteClicked: () -> Unit = {},
    listState: LazyListState = rememberLazyListState()
) { // TODO: replace with actual menu
    val fabVisible by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0 || !listState.canScrollForward
        }
    }
    val focusRequester = remember { FocusRequester() }
    val items =
        listOf(
            Icons.AutoMirrored.Filled.Message to "Reply",
            Icons.Filled.People to "Reply all",
            Icons.Filled.Contacts to "Forward",
            Icons.Filled.Snooze to "Snooze",
            Icons.Filled.Archive to "Archive",
            Icons.AutoMirrored.Filled.Label to "Label",
        )
    var fabMenuExpanded by rememberSaveable { mutableStateOf(false) }
    BackHandler(fabMenuExpanded) { fabMenuExpanded = false }
    FloatingActionButtonMenu(
        modifier = modifier.align(Alignment.BottomEnd),
        expanded = fabMenuExpanded,
        button = {
            TooltipBox(
                positionProvider =
                    TooltipDefaults.rememberTooltipPositionProvider(
                        TooltipAnchorPosition.Above
                    ),
                tooltip = { PlainTooltip { Text("Toggle menu") } },
                state = rememberTooltipState(),
            ) {
                ToggleFloatingActionButton(
                    modifier =
                        Modifier
                            .animateFloatingActionButton(
                                visible = fabVisible || fabMenuExpanded,
                                alignment = Alignment.BottomEnd,
                            )
                            .focusRequester(focusRequester),
                    checked = fabMenuExpanded,
                    onCheckedChange = { fabMenuExpanded = !fabMenuExpanded },
                ) {
                    val imageVector by remember {
                        derivedStateOf {
                            if (checkedProgress > 0.5f) Icons.Filled.Close else Icons.Filled.Add
                        }
                    }
                    Icon(
                        painter = rememberVectorPainter(imageVector),
                        contentDescription = null,
                        modifier = Modifier.animateIcon({ checkedProgress }),
                    )
                }
            }
        },
    ) {
        items.forEachIndexed { i, item ->
            FloatingActionButtonMenuItem(
                modifier =
                    Modifier
                        .semantics {
                            isTraversalGroup = true
                            if (i == items.size - 1) {
                                customActions =
                                    listOf(
                                        CustomAccessibilityAction(
                                            label = "Close menu",
                                            action = {
                                                fabMenuExpanded = false
                                                true
                                            },
                                        )
                                    )
                            }
                        }
                        .then(
                            if (i == 0) {
                                Modifier.onKeyEvent {
                                    if (
                                        it.type == KeyEventType.KeyDown &&
                                        (it.key == Key.DirectionUp ||
                                                (it.isShiftPressed && it.key == Key.Tab))
                                    ) {
                                        focusRequester.requestFocus()
                                        return@onKeyEvent true
                                    }
                                    return@onKeyEvent false
                                }
                            } else {
                                Modifier
                            }
                        ),
                onClick = { fabMenuExpanded = false },
                icon = { Icon(item.first, contentDescription = null) },
                text = { Text(text = item.second) },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NoteListScreenPreview() {
    SharpIdeasTheme {
        NoteListScreen()
    }
}