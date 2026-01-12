@file:OptIn(
    ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalSharedTransitionApi::class
)

package ru.shrprnbw.ideas.presentation.screens.notes_list

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.AudioFile
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import ru.shrprnbw.ideas.R
import ru.shrprnbw.ideas.presentation.navigation.SEARCH_ANIMATION_DUR
import ru.shrprnbw.ideas.presentation.navigation.SEARCH_ANIMATION_KEY
import ru.shrprnbw.ideas.presentation.screens.NoteItem
import ru.shrprnbw.ideas.presentation.screens.NoteType
import ru.shrprnbw.ideas.presentation.screens.PagedItemsTemplate
import ru.shrprnbw.ideas.presentation.ui.theme.SharpIdeasTheme
import ru.shrprnbw.ideas.utils.DateFormatter
import ru.shrprnbw.ideas.utils.Utils

@Composable
fun NoteListScreen(
    modifier: Modifier = Modifier,
    viewModel: NoteListViewModel = hiltViewModel(),
    onNoteClicked: (String) -> Unit = {},
    onSearchTriggered: () -> Unit = {},
    onProfileClicked: () -> Unit = {},
    onMenuClicked: () -> Unit = {},
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null
) {
    val noteItems = viewModel.notesFlow.collectAsLazyPagingItems()
    val state = viewModel.state.collectAsState()

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
                    onMenuClicked = onMenuClicked,
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
                PagedItemsTemplate(
                    items = noteItems,
                    paddingValues = PaddingValues(),
                    errorText = stringResource(R.string.note_list_error_load),
                    keyProducer = { index -> noteItems[index]?.id ?: index },
                    listState = listState,
                    screenContent = {}
                ) { item ->
                    NoteItem(
                        title = item.title,
                        preview = if (item.audioNote) Utils.extractFileName(item.preview) else item.preview,
                        time = DateFormatter.formatDateToString(item.updatedAt),
                        type = if (item.audioNote) NoteType.Audio else NoteType.Text,
                        tags = item.tags,
                        onNoteClicked = {
                            onNoteClicked(item.id)
                        }
                    )
                }
            }
            FloatingActionButtonWithMenu(
                listState = listState,
                onTextNoteClicked = {
                    viewModel.processCommand(
                        NoteListCommand.ShowAddTextNoteDialog(true)
                    )
                },
                onAudioNoteClicked = {
                    viewModel.processCommand(
                        NoteListCommand.ShowAddAudioNoteDialog(true)
                    )
                }
            )
        }
        if (state.value.showAddTextNoteDialog || state.value.showAddAudioNoteDialog) {
            CreateNoteDialog(
                icon = {
                    Icon(
                        imageVector =
                            if (state.value.showAddTextNoteDialog) Icons.Filled.TextFields
                            else Icons.Rounded.AudioFile,
                        contentDescription = null
                    )
                },
                dialogTitle = if (state.value.showAddTextNoteDialog) stringResource(R.string.note_list_add_note) else stringResource(
                    R.string.note_list_add_audio
                ),
                noteName = state.value.newNoteTitle,
                onNoteNameChange = { title ->
                    viewModel.processCommand(
                        NoteListCommand.InputNewNoteTitle(title)
                    )
                },
                onDismissRequest = {
                    viewModel.processCommand(
                        if (state.value.showAddTextNoteDialog)
                            NoteListCommand.ShowAddTextNoteDialog(false)
                        else
                            NoteListCommand.ShowAddAudioNoteDialog(false)
                    )
                },
                onCreateNoteRequest = {
                    viewModel.processCommand(
                        if (state.value.showAddTextNoteDialog)
                            NoteListCommand.CreateTextNote(state.value.newNoteTitle)
                        else
                            NoteListCommand.CreateAudioNote(state.value.newNoteTitle)
                    )
                }
            )
        }
    }
}

@Composable
private fun SearchBar(
    modifier: Modifier = Modifier,
    searchPlaceholder: String = stringResource(R.string.search_screen_hint_search),
    onSearchTriggered: () -> Unit = {},
    onProfileClicked: () -> Unit = {},
    onMenuClicked: () -> Unit = {},
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
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .clickable(
                    onClick = onMenuClicked
                ),
            imageVector = Icons.Rounded.Menu,
            contentDescription = stringResource(R.string.menu_icon_description),
            tint = SearchBarDefaults.appBarWithSearchColors().appBarNavigationIconColor
        )
        with(sharedTransitionScope ?: return) {
            TextField(
                modifier = Modifier
                    .weight(1f)
                    .clip(SearchBarDefaults.inputFieldShape)
                    .sharedElement(
                        sharedContentState = rememberSharedContentState(key = SEARCH_ANIMATION_KEY),
                        animatedVisibilityScope = animatedVisibilityScope ?: return,
                        boundsTransform = { _, _ ->
                            tween(durationMillis = SEARCH_ANIMATION_DUR)
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
                        contentDescription = stringResource(R.string.search_icon_description),
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
            modifier = Modifier
                .clip(CircleShape)
                .clickable(
                    onClick = onProfileClicked
                ),
            imageVector = Icons.Rounded.AccountCircle,
            contentDescription = stringResource(R.string.account_icon_description),
            tint = SearchBarDefaults.appBarWithSearchColors().appBarActionIconColor
        )
    }
}

@Composable
fun BoxScope.FloatingActionButtonWithMenu(
    modifier: Modifier = Modifier,
    onTextNoteClicked: () -> Unit = {},
    onAudioNoteClicked: () -> Unit = {},
    listState: LazyListState = rememberLazyListState()
) {
    val context = LocalContext.current
    val fabVisible by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0 || !listState.canScrollForward
        }
    }
    val focusRequester = remember { FocusRequester() }
    val items =
        listOf(
            Icons.Filled.TextFields to stringResource(R.string.text_note_label),
            Icons.Rounded.AudioFile to stringResource(R.string.audio_note_label)
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
                tooltip = { PlainTooltip { Text(stringResource(R.string.toggle_menu)) } },
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
                                            label = context.getString(R.string.close_menu),
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
                onClick = {
                    if (item.first == Icons.Filled.TextFields) {
                        onTextNoteClicked()
                    } else if (item.first == Icons.Rounded.AudioFile) {
                        onAudioNoteClicked()
                    }
                    fabMenuExpanded = false
                },
                icon = { Icon(item.first, contentDescription = null) },
                text = { Text(text = item.second) },
            )
        }
    }
}

@Composable
private fun CreateNoteDialog(
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)?,
    dialogTitle: String,
    noteName: String,
    onNoteNameChange: (String) -> Unit,
    onDismissRequest: () -> Unit,
    onCreateNoteRequest: () -> Unit
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        icon = icon,
        title = { Text(text = dialogTitle, textAlign = TextAlign.Center) },
        text = {
            OutlinedTextField(
                value = noteName,
                onValueChange = onNoteNameChange,
                label = { Text(stringResource(R.string.note_name)) },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(onClick = onCreateNoteRequest) {
                Text(stringResource(R.string.create_button))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun NoteListScreenPreview() {
    SharpIdeasTheme {
        NoteListScreen()
    }
}