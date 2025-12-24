package ru.shrprnbw.ideas.presentation.screens.tag_management

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Label
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import ru.shrprnbw.ideas.R
import ru.shrprnbw.ideas.domain.entity.Tag
import ru.shrprnbw.ideas.presentation.screens.PagedItemsTemplate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagManagementScreen(
    modifier: Modifier = Modifier,
    viewModel: TagManagementViewModel = hiltViewModel(),
    onMenuClicked: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val tagsList = viewModel.tagsLazyList.collectAsLazyPagingItems()
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = modifier
            .fillMaxWidth()
            .imePadding(),
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.tag_management_title),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onMenuClicked) {
                            Icon(
                                imageVector = Icons.Rounded.Menu,
                                contentDescription = "Menu"
                            )
                        }
                    }
                )
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->
        val errorMessage = state.error?.let { stringResource(it) }
        LaunchedEffect(state.error) {
            errorMessage?.let {
                snackbarHostState.showSnackbar(errorMessage)
                viewModel.processCommand(TagManagementCommand.ResetError)
            }
        }
        PagedItemsTemplate(
            items = tagsList,
            keyProducer = { index -> tagsList[index]?.id ?: index },
            paddingValues = innerPadding,
            errorText = stringResource(R.string.tags_list_error_loading),
            listPadding = 0.dp,
            listContentPadding = PaddingValues(),
            listVerticalArrangement = Arrangement.spacedBy(0.dp),
            screenContent = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    var isAddTagFocused by remember { mutableStateOf(false) }
                    if (!isAddTagFocused) {
                        Icon(
                            imageVector = Icons.Outlined.Add,
                            contentDescription = null,
                            modifier = Modifier
                                .size(24.dp)
                                .clickable(
                                    onClick = {
                                        focusRequester.requestFocus()
                                    }
                                )
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = null,
                            modifier = Modifier
                                .size(24.dp)
                                .clickable(onClick = {
                                    viewModel.processCommand(
                                        TagManagementCommand.InputNewTag("")
                                    )
                                    focusManager.clearFocus()
                                })
                        )
                    }
                    TextField(
                        value = state.newTagInput,
                        onValueChange = {
                            viewModel.processCommand(
                                TagManagementCommand.InputNewTag(it)
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(focusRequester)
                            .onFocusChanged { focusState ->
                                if (focusState.isFocused) {
                                    state.editingTagId?.let {
                                        viewModel.processCommand(
                                            TagManagementCommand.CancelEditTag(it)
                                        )
                                    }
                                }
                                isAddTagFocused = focusState.isFocused
                            },
                        placeholder = {
                            Text(
                                text = stringResource(R.string.tag_management_create_hint),
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        singleLine = true,
                        trailingIcon = {
                            if (state.newTagInput.isNotBlank()) {
                                IconButton(
                                    onClick = {
                                        viewModel.processCommand(
                                            TagManagementCommand.CreateTag(state.newTagInput)
                                        )
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Check,
                                        contentDescription = "Create"
                                    )
                                }
                            }
                        }
                    )
                }
            }
        ) { tag ->
            TagListItem(
                tag = tag,
                state = state,
                viewModel = viewModel
            )
        }
    }
}

@Composable
private fun TagListItem(
    tag: Tag,
    state: TagManagementState,
    viewModel: TagManagementViewModel
) {
    val isEditing = state.editingTagId == tag.id

    if (isEditing) {
        HorizontalDivider(modifier = Modifier.fillMaxWidth(), thickness = 1.dp)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (!isEditing) {
                    viewModel.processCommand(
                        TagManagementCommand.StartEditTag(tag.id, tag.name)
                    )
                }
            }
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isEditing) Icons.Outlined.Delete else Icons.AutoMirrored.Outlined.Label,
            contentDescription = null,
            modifier = Modifier
                .size(24.dp)
                .clickable {
                    if (isEditing) {
                        viewModel.processCommand(
                            TagManagementCommand.DeleteTag(tag.id)
                        )
                    }
                },
            tint = if (isEditing) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.width(16.dp))

        if (isEditing) {
            var textFieldValue by remember {
                mutableStateOf(
                    TextFieldValue(
                        text = tag.name,
                        selection = TextRange(tag.name.length)
                    )
                )
            }
            val editFocusRequester = remember { FocusRequester() }

            TextField(
                value = textFieldValue,
                onValueChange = { newValue ->
                    textFieldValue = newValue
                    viewModel.processCommand(
                        TagManagementCommand.UpdateTagInput(newValue.text)
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(editFocusRequester)
                    .onFocusChanged(onFocusChanged = {
                        if (it.isFocused && state.newTagInput.isNotEmpty()) {
                            viewModel.processCommand(
                                TagManagementCommand.InputNewTag("")
                            )
                        }
                    }),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                singleLine = true
            )

            LaunchedEffect(Unit) {
                editFocusRequester.requestFocus()
            }
        } else {
            Text(
                text = tag.name,
                modifier = Modifier.weight(1f),
                fontSize = 16.sp
            )
        }

        IconButton(
            onClick = {
                if (isEditing) {
                    viewModel.processCommand(
                        TagManagementCommand.SaveTag(tag)
                    )
                } else {
                    viewModel.processCommand(
                        TagManagementCommand.StartEditTag(tag.id, tag.name)
                    )
                }
            }
        ) {
            Icon(
                imageVector = if (isEditing) Icons.Outlined.Check else Icons.Outlined.Edit,
                contentDescription = if (isEditing) "Save" else "Edit"
            )
        }

    }
    if (isEditing) {
        HorizontalDivider(modifier = Modifier.fillMaxWidth(), thickness = 1.dp)
    }
}
