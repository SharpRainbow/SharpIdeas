@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package ru.shrprnbw.ideas.presentation.screens.note_editor

import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Label
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material.icons.outlined.AudioFile
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.FormatBold
import androidx.compose.material.icons.outlined.FormatItalic
import androidx.compose.material.icons.outlined.FormatStrikethrough
import androidx.compose.material.icons.outlined.GroupAdd
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.RemoveRedEye
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.material.icons.rounded.AutoFixHigh
import androidx.compose.material.icons.rounded.Discount
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.compose.AsyncImage
import dev.jeziellago.compose.markdowntext.MarkdownText
import kotlinx.coroutines.launch
import ru.shrprnbw.ideas.R
import ru.shrprnbw.ideas.domain.entity.AccessType
import ru.shrprnbw.ideas.domain.entity.ContentItem
import ru.shrprnbw.ideas.domain.entity.ContentType
import ru.shrprnbw.ideas.domain.entity.Group
import ru.shrprnbw.ideas.domain.entity.Note
import ru.shrprnbw.ideas.domain.entity.Tag
import ru.shrprnbw.ideas.domain.entity.User
import ru.shrprnbw.ideas.presentation.components.AudioNotePlayer
import ru.shrprnbw.ideas.presentation.ui.theme.AudioGreen
import ru.shrprnbw.ideas.presentation.ui.theme.KeywordPurple
import ru.shrprnbw.ideas.presentation.ui.theme.TextBlue
import ru.shrprnbw.ideas.utils.DateFormatter
import ru.shrprnbw.ideas.utils.Utils

@Composable
fun NoteEditorScreen(
    modifier: Modifier = Modifier,
    noteId: String,
    viewModel: NoteEditorViewModel = hiltViewModel(
        creationCallback = { factory: NoteEditorViewModel.Factory ->
            factory.create(noteId)
        }
    ),
    onBackClicked: () -> Unit = { },
    onTranscriptionsClicked: (Boolean) -> Unit = { },
    onKeywordsClicked: (Boolean) -> Unit = { },
    onSummariesClicked: (Boolean) -> Unit = { }
) {

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        uri?.let {
            viewModel.processCommand(NoteEditorCommand.UploadImage(uri))
        }
    }

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(lifecycle) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.processCommand(NoteEditorCommand.LoadNote)
        }
    }

    val state = viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    when (val currentState = state.value) {

        NoteEditorState.Initial -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                ContainedLoadingIndicator()
            }
        }

        is NoteEditorState.Editing -> {

            Scaffold(
                modifier = modifier
                    .fillMaxWidth()
                    .imePadding(),
                topBar = {
                    NoteEditorTopAppBar(
                        currentState = currentState,
                        viewModel = viewModel,
                        onBackClicked = onBackClicked
                    )
                },
                snackbarHost = {
                    SnackbarHost(hostState = snackbarHostState)
                },
                bottomBar = {
                    if (!currentState.note.audioNote && !currentState.isPreviewMode) {
                        FormattingToolbar(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surface)
                                .imePadding()
                                .navigationBarsPadding(),
                            onFormatClick = { formatType ->
                                viewModel.processCommand(
                                    NoteEditorCommand.ApplyFormatting(formatType)
                                )
                            },
                            onTextAddClick = {
                                viewModel.processCommand(NoteEditorCommand.AddNoteTextItem)
                            },
                            onImageAddClick = {
                                imagePicker.launch(
                                    PickVisualMediaRequest(
                                        ActivityResultContracts.PickVisualMedia.ImageOnly
                                    )
                                )
                            }
                        )
                    }
                }
            ) { innerPadding ->
                val errorMessage = currentState.error?.let { stringResource(it) }
                LaunchedEffect(currentState.error) {
                    errorMessage?.let {
                        snackbarHostState.showSnackbar(errorMessage)
                        viewModel.processCommand(NoteEditorCommand.ResetError)
                    }
                }

                Content(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    note = currentState.note,
                    isPreviewMode = currentState.isPreviewMode,
                    setFocus = { index, selection ->
                        viewModel.processCommand(
                            NoteEditorCommand.SetFocus(
                                index,
                                selection
                            )
                        )
                    },
                    textSelectionRange = currentState.selection,
                    onTitleChanged = { title ->
                        viewModel.processCommand(
                            NoteEditorCommand.InputTitle(title)
                        )
                    },
                    onTextChanged = { idx, input ->
                        viewModel.processCommand(
                            NoteEditorCommand.InputContent(
                                input,
                                idx
                            )
                        )
                    },
                    onDeleteImageClick = { id ->
                        viewModel.processCommand(
                            NoteEditorCommand.DeleteContentItem(id)
                        )
                    },
                    onDeleteTextRequested = { idx ->
                        viewModel.processCommand(
                            NoteEditorCommand.DeleteContentItem(idx)
                        )
                    },
                    onTranscriptionsClicked = {
                        onTranscriptionsClicked(currentState.note.accessType == AccessType.OWNER)
                    },
                    onTagsClicked = {
                        viewModel.processCommand(NoteEditorCommand.OpenTagsDialog)
                    },
                    onKeywordsClicked = {
                        onKeywordsClicked(currentState.note.accessType == AccessType.OWNER)
                    },
                    onSummariesClicked = {
                        onSummariesClicked(currentState.note.accessType == AccessType.OWNER)
                    }
                )

                if (currentState.showTagsDialog) {
                    val pagesTagsList = viewModel.tagsLazyList.collectAsLazyPagingItems()
                    TagsBottomSheet(
                        noteTags = currentState.note.tags,
                        availableTags = pagesTagsList,
                        tagQuery = currentState.tagQuery,
                        tagError = currentState.tagError,
                        onDismiss = {
                            viewModel.processCommand(NoteEditorCommand.CloseTagsDialog)
                        },
                        onAddTag = { tag ->
                            viewModel.processCommand(NoteEditorCommand.AddTag(tag))
                            pagesTagsList.refresh()
                        },
                        onRemoveTag = { tag ->
                            viewModel.processCommand(NoteEditorCommand.RemoveTag(tag))
                        },
                        onQueryChange = { query ->
                            viewModel.processCommand(NoteEditorCommand.InputTagQuery(query))
                        }
                    )
                }

                if (currentState.showDeleteDialog) {
                    DeleteConfirmationDialog(
                        onConfirm = {
                            viewModel.processCommand(NoteEditorCommand.ConfirmNoteDeletion)
                        },
                        onDismiss = {
                            viewModel.processCommand(NoteEditorCommand.DismissDeletionDialog)
                        }
                    )
                }

                if (currentState.showShareSheet) {
                    ShareBottomSheet(
                        collaborators = currentState.note.collaborators,
                        noteGroups = currentState.note.groups,
                        availableGroups = currentState.availableGroups,
                        collaboratorEmail = currentState.collaboratorEmail,
                        shareError = currentState.shareError,
                        isLoading = currentState.isShareLoading,
                        onDismiss = {
                            viewModel.processCommand(NoteEditorCommand.CloseShareSheet)
                        },
                        onEmailChange = { email ->
                            viewModel.processCommand(NoteEditorCommand.InputCollaboratorEmail(email))
                        },
                        onAddCollaborator = { email ->
                            viewModel.processCommand(NoteEditorCommand.AddCollaboratorByEmail(email))
                        },
                        onRemoveCollaborator = { collaboratorId ->
                            viewModel.processCommand(
                                NoteEditorCommand.RemoveCollaborator(
                                    collaboratorId
                                )
                            )
                        },
                        onAddToGroup = { groupId ->
                            viewModel.processCommand(NoteEditorCommand.AddToGroup(groupId))
                        },
                        onRemoveFromGroup = { groupId ->
                            viewModel.processCommand(NoteEditorCommand.RemoveFromGroup(groupId))
                        }
                    )
                }

                if (currentState.uploadProgress != null) {
                    UploadProgressOverlay(
                        progress = currentState.uploadProgress
                    )
                }
            }
        }

        is NoteEditorState.Finished -> {
            LaunchedEffect(Unit) {
                onBackClicked()
            }
        }

    }

}

@Composable
fun Content(
    modifier: Modifier = Modifier,
    note: Note,
    isPreviewMode: Boolean = false,
    setFocus: (index: Int, selection: TextRange) -> Unit,
    textSelectionRange: TextRange = TextRange(0, 0),
    onTitleChanged: (String) -> Unit,
    onTextChanged: (Int, String) -> Unit,
    onDeleteImageClick: (Int) -> Unit,
    onDeleteTextRequested: (Int) -> Unit,
    onTranscriptionsClicked: () -> Unit = { },
    onTagsClicked: () -> Unit = { },
    onKeywordsClicked: () -> Unit = { },
    onSummariesClicked: () -> Unit = { }
) {
    var viewingImageUrl by remember { mutableStateOf<String?>(null) }

    Box(modifier = modifier) {
        Column( // TODO: Add lazy column with collapsing toolbar
            modifier = Modifier.fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                if (note.audioNote) {
                    NoteTransformation(
                        modifier = Modifier.weight(1f),
                        actionName = stringResource(R.string.transcriptions_action),
                        color = AudioGreen,
                        imageVector = Icons.Rounded.GraphicEq,
                        onClick = onTranscriptionsClicked
                    )
                } else {
                    NoteTransformation(
                        modifier = Modifier.weight(1f),
                        actionName = stringResource(R.string.keywords_action),
                        color = KeywordPurple,
                        imageVector = Icons.Rounded.Discount,
                        onClick = onKeywordsClicked
                    )
                    NoteTransformation(
                        modifier = Modifier.weight(1f),
                        actionName = stringResource(R.string.summary_action),
                        color = TextBlue,
                        imageVector = Icons.Rounded.AutoFixHigh,
                        onClick = onSummariesClicked
                    )
                }
            }

            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                value = note.title,
                onValueChange = onTitleChanged,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                textStyle = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                placeholder = {
                    Text(
                        text = stringResource(R.string.note_title),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                    )
                }
            )

            Text(
                modifier = Modifier.padding(horizontal = 24.dp),
                text = DateFormatter.formatDateToString(note.createdAt),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))
            TagsSection(
                tags = note.tags,
                onTagsClicked = onTagsClicked
            )
            Spacer(modifier = Modifier.height(4.dp))

            note.contents.forEachIndexed { itemIndex, contentItem ->
                when (contentItem.type) {
                    ContentType.AUDIO -> {
                        AudioNotePlayer(
                            audioUrl = contentItem.data,
                            accentColor = AudioGreen
                        )
                    }

                    ContentType.TEXT -> {
                        TextContent(
                            text = contentItem.data,
                            selectionRange = textSelectionRange,
                            editorView = !isPreviewMode,
                            setFocus = { selection ->
                                setFocus(itemIndex, selection)
                            },
                            onTextInput = { input ->
                                onTextChanged(itemIndex, input)
                            },
                            onDeleteRequest = {
                                onDeleteTextRequested(contentItem.id.toInt())
                            }
                        )
                    }

                    ContentType.IMAGE -> {
                        val previousItem = note.contents.getOrNull(itemIndex - 1)
                        if (previousItem?.type != ContentType.IMAGE) {
                            note.contents.drop(itemIndex).takeWhile { item ->
                                item.type == ContentType.IMAGE
                            }.let { images ->
                                ImageGroup(
                                    modifier = Modifier
                                        .padding(horizontal = 24.dp)
                                        .heightIn(max = 300.dp),
                                    images = images,
                                    onDeleteImageClick = onDeleteImageClick,
                                    onImageClick = { imageUrl ->
                                        viewingImageUrl = imageUrl
                                    }
                                )
                            }
                        }
                    }
                }
            }

        }

        viewingImageUrl?.let { imageUrl ->
            FullScreenImageViewer(
                imageUrl = imageUrl,
                onDismiss = { viewingImageUrl = null }
            )
        }
    }
}

@Composable
fun ImageGroup(
    modifier: Modifier = Modifier,
    images: List<ContentItem>,
    onDeleteImageClick: (Int) -> Unit,
    onImageClick: (String) -> Unit = {}
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        images.forEach { image ->
            ImageContent(
                modifier = Modifier.weight(1f),
                imageUrl = image.data,
                onImageClick = { onImageClick(image.data) },
                onDeleteImageClick = { onDeleteImageClick(image.id.toInt()) }
            )
        }
    }
}

@Composable
fun ImageContent(
    modifier: Modifier = Modifier,
    imageUrl: String,
    onImageClick: () -> Unit = {},
    onDeleteImageClick: () -> Unit
) {
    Box(
        modifier = modifier
    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clickable(onClick = onImageClick),
            model = imageUrl,
            contentDescription = null,
            contentScale = ContentScale.FillWidth
        )
        Icon(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .size(24.dp)
                .clickable(
                    onClick = onDeleteImageClick
                ),
            imageVector = Icons.Default.Close,
            contentDescription = stringResource(R.string.remove_image),
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun TextContent(
    modifier: Modifier = Modifier,
    text: String,
    onTextInput: (String) -> Unit,
    editorView: Boolean = true,
    setFocus: (selection: TextRange) -> Unit = { _ -> },
    selectionRange: TextRange = TextRange(0, 0),
    onDeleteRequest: () -> Unit = {}
) {
    if (editorView) {
//        val keyboardController = LocalSoftwareKeyboardController.current
        val bringIntoViewRequester = remember { BringIntoViewRequester() }
        val coroutineScope = rememberCoroutineScope()
        var textFieldValue by remember(text) {
            mutableStateOf(TextFieldValue(text = text, selection = selectionRange))
        }

        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                modifier = modifier
                    .weight(1f)
                    .onKeyEvent { event ->
                        if (event.type == KeyEventType.KeyUp && event.key == Key.Backspace) {
                            if (text.isEmpty()) {
                                onDeleteRequest()
                                true
                            } else {
                                false
                            }
                        } else {
                            false
                        }
                    }
                    .onFocusEvent {
                        if (it.hasFocus) {
                            setFocus(textFieldValue.selection)
                        }
                    },
                value = textFieldValue,
                onValueChange = { newValue ->
                    textFieldValue = newValue
                    onTextInput(newValue.text)
                    setFocus(textFieldValue.selection)
                },
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    color = LocalContentColor.current
                ),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                onTextLayout = {
                    val cursorRect = it.getCursorRect(textFieldValue.selection.start)
                    coroutineScope.launch {
                        bringIntoViewRequester.bringIntoView(cursorRect)
                    }
                },
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .bringIntoViewRequester(bringIntoViewRequester)
                    ) {
                        if (textFieldValue.text.isEmpty()) {
                            Text(
                                text = stringResource(R.string.add_text_hint),
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                            )
                        }
                        innerTextField()
                    }
                }
            )

            AnimatedVisibility(
                visible = textFieldValue.text.isEmpty(),
                enter = fadeIn() + expandHorizontally(),
                exit = fadeOut() + shrinkHorizontally()
            ) {
                IconButton(
                    modifier = Modifier.size(32.dp),
                    onClick = onDeleteRequest
                ) {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        imageVector = Icons.Outlined.DeleteOutline,
                        contentDescription = stringResource(R.string.delete),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
//            MarkdownEditor(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 28.dp)
//                    .onGloballyPositioned {
//                        fieldHeight = it.size.height.toFloat()
//                    }
//                    .onKeyEvent { event ->
//                        if (event.type == KeyEventType.KeyUp && event.key == Key.Backspace) {
//                            if (text.isEmpty()) {
//                                onDeleteRequest()
//                                true
//                            } else {
//                                false
//                            }
//                        } else {
//                            false
//                        }
//                    }.onFocusChanged { focusState ->
//                        if (!focusState.hasFocus) {
//                            keyboardController?.hide()
//                        }
//                    },
//                value = text,
//                onValueChange = onTextInput,
//                hint = R.string.add_text_hint
//            )
//            Spacer(modifier = Modifier.height(8.dp))
    } else {
        MarkdownText(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            markdown = text,
            style = TextStyle(
                fontSize = 16.sp,
                lineHeight = 24.sp,
                color = MaterialTheme.colorScheme.onSurface
            ),
            syntaxHighlightColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
fun FormattingToolbar(
    modifier: Modifier = Modifier,
    onFormatClick: (FormatType) -> Unit,
    onTextAddClick: () -> Unit = { },
    onImageAddClick: () -> Unit = { }
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Spacer(Modifier.width(8.dp))
        IconButton(
            onClick = onTextAddClick,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.TextFields,
                contentDescription = stringResource(R.string.add_text),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
        IconButton(
            onClick = onImageAddClick,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.AddPhotoAlternate,
                contentDescription = stringResource(R.string.add_photo),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
        FormatButton(
            painter = painterResource(R.drawable.outline_format_h1_40),
            contentDescription = stringResource(R.string.heading1_description),
            onClick = { onFormatClick(FormatType.HEADING_1) }
        )
        FormatButton(
            painter = painterResource(R.drawable.outline_format_h2_40),
            contentDescription = stringResource(R.string.heading1_description),
            onClick = { onFormatClick(FormatType.HEADING_2) }
        )
        FormatButton(
            painter = painterResource(R.drawable.outline_format_h3_40),
            contentDescription = stringResource(R.string.heading1_description),
            onClick = { onFormatClick(FormatType.HEADING_3) }
        )
        FormatButton(
            icon = Icons.Outlined.FormatBold,
            contentDescription = stringResource(R.string.bold_description),
            onClick = { onFormatClick(FormatType.BOLD) }
        )
        FormatButton(
            icon = Icons.Outlined.FormatItalic,
            contentDescription = stringResource(R.string.italic_description),
            onClick = { onFormatClick(FormatType.ITALIC) }
        )
        FormatButton(
            icon = Icons.Outlined.FormatStrikethrough,
            contentDescription = stringResource(R.string.strikethrough_description),
            onClick = { onFormatClick(FormatType.STRIKETHROUGH) }
        )
        FormatButton(
            icon = Icons.Outlined.Code,
            contentDescription = stringResource(R.string.monospace_description),
            onClick = { onFormatClick(FormatType.MONOSPACE) }
        )
        Spacer(Modifier.width(8.dp))
    }
}

@Composable
private fun FormatButton(
    icon: ImageVector? = null,
    painter: Painter? = null,
    contentDescription: String,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(40.dp)
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = MaterialTheme.colorScheme.onSurface
            )
        } else if (painter != null) {
            Icon(
                modifier = Modifier.size(IconButtonDefaults.largeIconSize),
                painter = painter,
                contentDescription = contentDescription,
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun NoteTransformation(
    modifier: Modifier = Modifier,
    actionName: String,
    imageVector: ImageVector,
    color: Color = Utils.generateColorContrast(actionName),
    onClick: () -> Unit = { }
) {
    Column(
        modifier = modifier
            .background(
                color = color.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = 1.dp,
                color = color,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = actionName,
            tint = color
        )
        Text(text = actionName)
    }
}

@Composable
fun TagsSection(
    modifier: Modifier = Modifier,
    tags: List<String>,
    onTagsClicked: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        if (tags.isEmpty()) {
            OutlinedButton(
                onClick = onTagsClicked,
                modifier = Modifier.padding(start = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.Label,
                    contentDescription = stringResource(R.string.note_tags_add),
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                Text(
                    text = stringResource(R.string.note_tags_empty),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            LazyRow(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(tags) { tag ->
                    AssistChip(
                        onClick = onTagsClicked,
                        label = {
                            Text(
                                text = tag,
                                fontSize = 12.sp
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            labelColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagsBottomSheet(
    noteTags: List<String>,
    availableTags: LazyPagingItems<Tag>,
    tagQuery: String,
    tagError: Int? = null,
    onDismiss: () -> Unit,
    onAddTag: (String) -> Unit,
    onRemoveTag: (String) -> Unit,
    onQueryChange: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.note_tags_manage),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (noteTags.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.note_tags),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(noteTags) { tag ->
                        FilterChip(
                            selected = true,
                            onClick = { onRemoveTag(tag) },
                            label = { Text(tag) },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove",
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            OutlinedTextField(
                value = tagQuery,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeholder = {
                    Text(stringResource(R.string.note_tags_input_hint))
                },
                trailingIcon = {
                    if (tagQuery.isNotBlank()) {
                        IconButton(onClick = {
                            onAddTag(tagQuery)
                        }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = stringResource(R.string.note_tags_add)
                            )
                        }
                    }
                },
                singleLine = true,
                isError = tagError != null,
                supportingText = {
                    if (tagError != null) {
                        Text(text = stringResource(tagError))
                    }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (availableTags.itemCount > 0) {
                Text(
                    text = stringResource(R.string.available_tags_title),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                val noteTags = remember(noteTags) {
                    noteTags.toSet()
                }
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(availableTags.itemCount) { index ->
                        availableTags[index]?.name?.let { tag ->
                            if (!noteTags.contains(tag)) {
                                AssistChip(
                                    onClick = { onAddTag(tag) },
                                    label = { Text(tag) }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun UploadProgressOverlay(
    modifier: Modifier = Modifier,
    progress: Float
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.uploading_audio),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            androidx.compose.material3.LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
            Text(
                text = "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun FullScreenImageViewer(
    imageUrl: String,
    onDismiss: () -> Unit
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var containerSize by remember { mutableStateOf(Size.Zero) }
    var imageAspectRatio by remember { mutableFloatStateOf(1f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f))
            .onSizeChanged { size ->
                containerSize = Size(size.width.toFloat(), size.height.toFloat())
            }
            .pointerInput(imageUrl, containerSize, imageAspectRatio) {
                detectTransformGestures { _, pan, zoom, _ ->
                    val newScale = (scale * zoom).coerceIn(1f, 5f)

                    // Calculate the displayed image size based on ContentScale.Fit
                    val containerAspect = if (containerSize.height > 0) containerSize.width / containerSize.height else 1f
                    val displayedWidth: Float
                    val displayedHeight: Float
                    if (imageAspectRatio > containerAspect) {
                        // Image is wider than container - width fills, height is smaller
                        displayedWidth = containerSize.width
                        displayedHeight = containerSize.width / imageAspectRatio
                    } else {
                        // Image is taller than container - height fills, width is smaller
                        displayedHeight = containerSize.height
                        displayedWidth = containerSize.height * imageAspectRatio
                    }

                    // Max offset = how much the scaled image extends beyond the container
                    val maxX = ((displayedWidth * newScale - containerSize.width) / 2f).coerceAtLeast(0f)
                    val maxY = ((displayedHeight * newScale - containerSize.height) / 2f).coerceAtLeast(0f)

                    scale = newScale
                    offsetX = (offsetX + pan.x).coerceIn(-maxX, maxX)
                    offsetY = (offsetY + pan.y).coerceIn(-maxY, maxY)
                }
            }
            .pointerInput(imageUrl) {
                detectTapGestures(
                    onTap = {
                        if (scale <= 1f) {
                            onDismiss()
                        }
                    },
                    onDoubleTap = {
                        if (scale > 1f) {
                            scale = 1f
                            offsetX = 0f
                            offsetY = 0f
                        } else {
                            scale = 2.5f
                        }
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            onSuccess = { state ->
                val painter = state.painter
                val intrinsicSize = painter.intrinsicSize
                if (intrinsicSize.width > 0 && intrinsicSize.height > 0) {
                    imageAspectRatio = intrinsicSize.width / intrinsicSize.height
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offsetX,
                    translationY = offsetY
                ),
            contentScale = ContentScale.Fit
        )

        IconButton(
            onClick = onDismiss,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.close),
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
fun DeleteConfirmationDialog(
    modifier: Modifier = Modifier,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AlertDialog(
            onDismissRequest = onDismiss,
            icon = {
                Icon(
                    imageVector = Icons.Outlined.DeleteOutline,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = {
                Text(text = stringResource(R.string.delete_note_request))
            },
            text = {
                Text(
                    text = stringResource(R.string.delete_note_confirmation),
                    style = TextStyle(lineBreak = LineBreak.Simple)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = onConfirm
                ) {
                    Text(text = stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismiss
                ) {
                    Text(text = stringResource(R.string.cancel))
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareBottomSheet(
    collaborators: List<User>,
    noteGroups: List<Group>,
    availableGroups: List<Group>,
    collaboratorEmail: String,
    shareError: Int?,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onEmailChange: (String) -> Unit,
    onAddCollaborator: (String) -> Unit,
    onRemoveCollaborator: (String) -> Unit,
    onAddToGroup: (Long) -> Unit,
    onRemoveFromGroup: (Long) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val noteGroupIds = remember(noteGroups) { noteGroups.map { it.id }.toSet() }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = stringResource(R.string.share_note_title),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.GroupAdd,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.share_with_group),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (noteGroups.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(noteGroups) { group ->
                        FilterChip(
                            selected = true,
                            onClick = { onRemoveFromGroup(group.id) },
                            label = { Text(group.name) },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = stringResource(R.string.share_remove_from_group),
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            enabled = !isLoading
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            val groupsToAdd = availableGroups.filter { it.id !in noteGroupIds }
            if (groupsToAdd.isEmpty()) {
                if (noteGroups.isEmpty()) {
                    Text(
                        text = stringResource(R.string.share_no_groups),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(groupsToAdd) { group ->
                        AssistChip(
                            onClick = { onAddToGroup(group.id) },
                            label = { Text(group.name) },
                            enabled = !isLoading
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.PersonAdd,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.share_with_users),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = collaboratorEmail,
                onValueChange = onEmailChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(stringResource(R.string.share_email_hint))
                },
                trailingIcon = {
                    if (collaboratorEmail.isNotBlank()) {
                        IconButton(
                            onClick = { onAddCollaborator(collaboratorEmail) },
                            enabled = !isLoading
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = stringResource(R.string.share_add_collaborator)
                            )
                        }
                    }
                },
                singleLine = true,
                isError = shareError != null,
                supportingText = {
                    if (shareError != null) {
                        Text(text = stringResource(shareError))
                    }
                },
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (collaborators.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.share_collaborators),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                collaborators.forEach { collaborator ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "${collaborator.firstName} ${collaborator.lastName}",
                                fontSize = 14.sp
                            )
                            Text(
                                text = collaborator.email,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(
                            onClick = { onRemoveCollaborator(collaborator.id) },
                            enabled = !isLoading
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = stringResource(R.string.share_remove_collaborator),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            if (isLoading) {
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    ContainedLoadingIndicator()
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun NoteEditorTopAppBar(
    modifier: Modifier = Modifier,
    currentState: NoteEditorState.Editing,
    viewModel: NoteEditorViewModel,
    onBackClicked: () -> Unit
) {
    val audioPicker: ManagedActivityResultLauncher<Array<String>, Uri?> =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocument(),
        ) { uri ->
            uri?.let {
                viewModel.processCommand(NoteEditorCommand.UploadAudio(uri))
            }
        }
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        TopAppBar(
            title = {},
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
                navigationIconContentColor = MaterialTheme.colorScheme.onSurface
            ),
            navigationIcon = {
                IconButton(
                    onClick = onBackClicked
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = stringResource(R.string.back)
                    )
                }
            },
            actions = {
                if (currentState.note.accessType == AccessType.VIEWER) {
                    return@TopAppBar
                }
                if (currentState.note.audioNote && currentState.note.contents.isEmpty()) {
                    Icon(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clickable(
                                onClick = {
                                    audioPicker.launch(
                                        arrayOf(
                                            "audio/mpeg",
                                            "audio/wav",
                                            "audio/m4a"
                                        )
                                    )
                                }
                            ),
                        imageVector = Icons.Outlined.AudioFile,
                        contentDescription = stringResource(R.string.add_audio),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                } else if (!currentState.note.audioNote) {
                    IconButton(
                        onClick = {
                            viewModel.processCommand(
                                NoteEditorCommand.TogglePreviewMode
                            )
                        },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = if (currentState.isPreviewMode)
                                Icons.Rounded.Edit
                            else
                                Icons.Outlined.RemoveRedEye,
                            contentDescription = if (currentState.isPreviewMode)
                                stringResource(R.string.edit_mode)
                            else
                                stringResource(R.string.preview_mode),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                AnimatedVisibility(!currentState.isPreviewMode) {
                    IconButton(
                        onClick = {
                            viewModel.processCommand(
                                NoteEditorCommand.OpenShareSheet
                            )
                        },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Share,
                            contentDescription = stringResource(R.string.share_note),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                AnimatedVisibility(!currentState.isPreviewMode) {
                    IconButton(
                        onClick = {
                            viewModel.processCommand(
                                NoteEditorCommand.ShowDeletionDialog
                            )
                        },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.DeleteOutline,
                            contentDescription = stringResource(R.string.delete_note),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        )
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp
        )
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun NoteEditorScreenPreview() {
    NoteEditorScreen(noteId = "0")
}