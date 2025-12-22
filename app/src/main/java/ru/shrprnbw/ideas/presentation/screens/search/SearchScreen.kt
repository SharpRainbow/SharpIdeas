@file:OptIn(ExperimentalSharedTransitionApi::class)

package ru.shrprnbw.ideas.presentation.screens.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import ru.shrprnbw.ideas.R
import ru.shrprnbw.ideas.domain.entity.Tag
import ru.shrprnbw.ideas.presentation.navigation.Screen
import ru.shrprnbw.ideas.presentation.screens.NoteItem
import ru.shrprnbw.ideas.presentation.screens.NoteType
import ru.shrprnbw.ideas.presentation.screens.PagedItemsTemplate
import ru.shrprnbw.ideas.utils.DateFormatter
import ru.shrprnbw.ideas.utils.Utils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel(),
    onBackClicked: () -> Unit,
    sharedTransitionScope: SharedTransitionScope?,
    animatedVisibilityScope: AnimatedVisibilityScope?
) {

    val state by viewModel.state.collectAsState()
    val notesList = viewModel.pagedNoteList.collectAsLazyPagingItems()

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
                            },
                            clipInOverlayDuringTransition = OverlayClip(SearchBarDefaults.inputFieldShape)
                        )
                        .focusRequester(focusRequester),
                    value = state.query,
                    onValueChange = {
                        viewModel.processCommand(
                            SearchCommand.InputQuery(it)
                        )
                    },
                    placeholder = {
                        Text(
                            text = stringResource(R.string.search_screen_hint_search),
                        )
                    },
                    leadingIcon = {
                        Icon(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .clickable(
                                    onClick = onBackClicked
                                ),
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back"
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

            if (state.tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                FilterOptions(
                    availableTags = state.tags,
                    onTagToggle = {
                        viewModel.processCommand(
                            SearchCommand.ToggleTag(it)
                        )
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 1.dp
                )
            }

            PagedItemsTemplate(
                items = notesList,
                paddingValues = PaddingValues(),
                errorText = stringResource(R.string.note_list_error_load),
                keyProducer = { index -> notesList[index]?.id ?: index },
                showLoading = false,
                screenContent = {}
            ) { item ->
                NoteItem(
                    title = item.title,
                    preview = if (item.audioNote) Utils.extractFileName(item.preview) else item.preview,
                    time = DateFormatter.formatDateToString(item.updatedAt),
                    type = if (item.audioNote) NoteType.Audio else NoteType.Text,
                    tags = item.tags,
                    onNoteClicked = {

                    }
                )
            }
        }
    }
}

@Composable
fun FilterOptions(
    modifier: Modifier = Modifier,
    availableTags: Map<Tag, Boolean>,
    onTagToggle: (Tag) -> Unit
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        availableTags.forEach { tag ->
            item(
                key = tag.key.id
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
    onTagToggle: (Tag) -> Unit,
) {
    FilterChip(
        modifier = modifier,
        selected = isSelected,
        onClick = {
            onTagToggle(tag)
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