@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package ru.shrprnbw.ideas.presentation.screens.note_editor

import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material.icons.outlined.RemoveRedEye
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
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
import com.colintheshots.twain.MarkdownText
import com.example.textiemdlibrary.AnnotationsManager
import ru.shrprnbw.ideas.R
import ru.shrprnbw.ideas.domain.entity.ContentItem
import ru.shrprnbw.ideas.domain.entity.ContentType
import ru.shrprnbw.ideas.domain.entity.Note
import ru.shrprnbw.ideas.domain.entity.Tag
import ru.shrprnbw.ideas.presentation.components.AudioNotePlayer
import ru.shrprnbw.ideas.presentation.ui.theme.AudioGreen
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
    onTranscriptionsClicked: () -> Unit = { }
) {

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(lifecycle) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.processCommand(NoteEditorCommand.LoadNote)
        }
    }

    val state = viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        uri?.let {
            viewModel.processCommand(NoteEditorCommand.UploadImage(uri))
        }
    }
    val audioPicker: ManagedActivityResultLauncher<Array<String>, Uri?> = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri ->
        uri?.let {
            viewModel.processCommand(NoteEditorCommand.UploadAudio(uri))
        }
    }

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
            var formatCallback by remember { mutableStateOf<((FormatType) -> Unit)?>(null) }

            Scaffold(
                modifier = modifier
                    .fillMaxWidth()
                    .imePadding(),
                topBar = {
                    Column {
                        TopAppBar(
                            title = {
                                Text(
                                    text = stringResource(R.string.create_note),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Color.Transparent,
                                navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                            ),
                            navigationIcon = {
                                Icon(
                                    modifier = Modifier
                                        .padding(start = 8.dp, end = 8.dp)
                                        .clickable(
                                            onClick = onBackClicked
                                        ),
                                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                    contentDescription = stringResource(R.string.back)
                                )
                            },
                            actions = {
                                if (currentState.note.audioNote && currentState.note.contents.isEmpty()) {
                                    Icon(
                                        modifier = Modifier
                                            .padding(end = 8.dp)
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
                                    Icon(
                                        modifier = Modifier
                                            .padding(end = 8.dp)
                                            .clickable(
                                                onClick = {
                                                    viewModel.processCommand(
                                                        NoteEditorCommand.TogglePreviewMode
                                                    )
                                                }
                                            ),
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
                                    Icon(
                                        modifier = Modifier
                                            .padding(end = 8.dp)
                                            .clickable(
                                                onClick = {
                                                    viewModel.processCommand(
                                                        NoteEditorCommand.AddNoteTextItem
                                                    )
                                                }
                                            ),
                                        imageVector = Icons.Outlined.TextFields,
                                        contentDescription = stringResource(R.string.add_photo),
                                        tint = MaterialTheme.colorScheme.onSurface
                                    )
                                    Icon(
                                        modifier = Modifier
                                            .padding(end = 8.dp)
                                            .clickable(
                                                onClick = {
                                                    imagePicker.launch(
                                                        PickVisualMediaRequest(
                                                            mediaType = ActivityResultContracts
                                                                .PickVisualMedia
                                                                .ImageOnly
                                                        )
                                                    )
                                                }
                                            ),
                                        imageVector = Icons.Outlined.AddPhotoAlternate,
                                        contentDescription = stringResource(R.string.add_photo),
                                        tint = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Icon(
                                    modifier = Modifier
                                        .padding(end = 8.dp)
                                        .clickable(
                                            onClick = {
                                                viewModel.processCommand(
                                                    NoteEditorCommand.ShowDeletionDialog
                                                )
                                            }
                                        ),
                                    imageVector = Icons.Outlined.DeleteOutline,
                                    contentDescription = stringResource(R.string.delete_note),
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        )
                        HorizontalDivider(
                            modifier = Modifier.fillMaxWidth(),
                            thickness = 1.dp
                        )
                    }
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
                                formatCallback?.invoke(formatType)
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
                    modifier = Modifier.padding(innerPadding),
                    note = currentState.note,
                    isPreviewMode = currentState.isPreviewMode,
                    onFormatCallbackChange = { callback ->
                        formatCallback = callback
                    },
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
                    onTranscriptionsClicked = onTranscriptionsClicked,
                    onTagsClicked = {
                        viewModel.processCommand(NoteEditorCommand.OpenTagsDialog)
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
    onFormatCallbackChange: ((FormatType) -> Unit) -> Unit = { },
    onTitleChanged: (String) -> Unit,
    onTextChanged: (Int, String) -> Unit,
    onDeleteImageClick: (Int) -> Unit,
    onDeleteTextRequested: (Int) -> Unit,
    onTranscriptionsClicked: () -> Unit = { },
    onTagsClicked: () -> Unit = { }
) {
    LazyColumn(
        modifier = modifier
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                if (note.audioNote) {
                    NoteTransformation(
                        modifier = Modifier.weight(1f),
                        actionName = "Transcriptions",
                        color = AudioGreen,
                        imageVector = Icons.Rounded.GraphicEq,
                        onClick = onTranscriptionsClicked
                    )
                } else {
                    NoteTransformation(
                        modifier = Modifier.weight(1f),
                        actionName = "Keywords",
                        color = Color(0xFF7B1FA2),
                        imageVector = Icons.Rounded.Discount
                    )
                    NoteTransformation(
                        modifier = Modifier.weight(1f),
                        actionName = "Summary",
                        color = TextBlue,
                        imageVector = Icons.Rounded.AutoFixHigh
                    )
                }
            }
        }

        item {
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
        }

        item {
            Text(
                modifier = Modifier.padding(horizontal = 24.dp),
                text = DateFormatter.formatCurrentDate(),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            TagsSection(
                tags = note.tags,
                onTagsClicked = onTagsClicked
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        itemsIndexed(
            note.contents,
            key = { _, contentItem ->
                contentItem.id
            }
        ) { itemIndex, contentItem ->
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
                        editorView = !isPreviewMode,
                        onFormatCallbackChange = onFormatCallbackChange,
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
                                onDeleteImageClick = onDeleteImageClick
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ImageGroup(
    modifier: Modifier = Modifier,
    images: List<ContentItem>,
    onDeleteImageClick: (Int) -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        images.forEach { image ->
            ImageContent(
                modifier = Modifier.weight(1f),
                imageUrl = image.data
            ) {
                onDeleteImageClick(image.id.toInt())
            }
        }
    }
}

@Composable
fun ImageContent(
    modifier: Modifier = Modifier,
    imageUrl: String,
    onDeleteImageClick: () -> Unit
) {
    Box(
        modifier = modifier
    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp)),
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
    onFormatCallbackChange: ((FormatType) -> Unit) -> Unit = { },
    onDeleteRequest: () -> Unit = {}
) {
    if (editorView) {
//        val keyboardController = LocalSoftwareKeyboardController.current
        val annotationsManager = remember { AnnotationsManager() }
//        val visualTransformation = remember { TextEditorVisualTransformer() }
        val bottomAnchorBringIntoViewRequester = remember { BringIntoViewRequester() }
        var fieldHeight by remember { mutableFloatStateOf(0f) }
        var textFieldValue by remember {
            mutableStateOf(TextFieldValue(text = text, selection = TextRange(text.length)))
        }

        // Update text without changing cursor position when text changes externally
        LaunchedEffect(text) {
            if (textFieldValue.text != text) {
                val currentSelection = textFieldValue.selection
                textFieldValue = TextFieldValue(
                    text = text,
                    selection = currentSelection.let {
                        if (it.start <= text.length) it else TextRange(text.length)
                    }
                )
            }
        }

        // Register formatting callback
        LaunchedEffect(Unit) { // TODO: Move to viewmodel
            onFormatCallbackChange { formatType ->
                val start = textFieldValue.selection.start
                val end = textFieldValue.selection.end

                if (start != end) {
                    val newText = when (formatType) {
                        FormatType.BOLD -> annotationsManager.applyBold(
                            textFieldValue.text, start, end
                        )
                        FormatType.ITALIC -> applyItalics(
                            textFieldValue.text, start, end
                        )
                        FormatType.STRIKETHROUGH -> applyStrikethrough(
                            textFieldValue.text, start, end
                        )
                        FormatType.MONOSPACE -> annotationsManager.applyMonospace(
                            textFieldValue.text, start, end
                        )
                    }
                    // Calculate the difference in length to adjust cursor position
                    val lengthDiff = newText.length - textFieldValue.text.length
                    val newCursorPosition = end + lengthDiff

                    textFieldValue = TextFieldValue(
                        text = newText,
                        selection = TextRange(newCursorPosition)
                    )
                    onTextInput(newText)
                }
            }
        }

        LaunchedEffect(fieldHeight) {
            bottomAnchorBringIntoViewRequester.bringIntoView()
        }

        Column(modifier = modifier) {

            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .onGloballyPositioned { coordinates ->
                        fieldHeight = coordinates.size.height.toFloat()
                    }
                    .onKeyEvent { event ->
                        if (event.type == KeyEventType.KeyUp && event.key == Key.Backspace) {
                            if (textFieldValue.text.isEmpty()) {
                                onDeleteRequest()
                                true
                            } else {
                                false
                            }
                        } else {
                            false
                        }
                    },
                value = textFieldValue,
                onValueChange = { newValue ->
                    textFieldValue = newValue
                    onTextInput(newValue.text)
                },
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                placeholder = {
                    Text(
                        text = stringResource(R.string.add_text_hint),
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                    )
                },
//                visualTransformation = visualTransformation
            )
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
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .bringIntoViewRequester(bottomAnchorBringIntoViewRequester)
            )

        }
    } else {
        MarkdownText(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            markdown = text,
            fontSize = 16.sp,
            style = TextStyle(
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            ),
        )
    }
}

@Composable
fun FormattingToolbar(
    modifier: Modifier = Modifier,
    onFormatClick: (FormatType) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FormatButton(
            icon = Icons.Outlined.FormatBold,
            contentDescription = "Bold",
            onClick = { onFormatClick(FormatType.BOLD) }
        )
        FormatButton(
            icon = Icons.Outlined.FormatItalic,
            contentDescription = "Italic",
            onClick = { onFormatClick(FormatType.ITALIC) }
        )
        FormatButton(
            icon = Icons.Outlined.FormatStrikethrough,
            contentDescription = "Strikethrough",
            onClick = { onFormatClick(FormatType.STRIKETHROUGH) }
        )
        FormatButton(
            icon = Icons.Outlined.Code,
            contentDescription = "Monospace",
            onClick = { onFormatClick(FormatType.MONOSPACE) }
        )
    }
}

@Composable
private fun FormatButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(40.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}

enum class FormatType {
    BOLD,
    ITALIC,
    STRIKETHROUGH,
    MONOSPACE
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
            OutlinedButton (
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
                    text = "Доступные теги",
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

private fun applyStrikethrough(text: String, selectionStart: Int, selectionEnd: Int): String {
    val annotation = "~~"
    val start = minOf(selectionStart, selectionEnd)
    val end = maxOf(selectionStart, selectionEnd)

    if (start != end) {
        val beforeSelection = text.substring(0, start)
        val selectedText = text.substring(start, end)
        val afterSelection = text.substring(end)

        val result = if (selectedText.startsWith(annotation) && selectedText.endsWith(annotation)) {
            val cleanText = selectedText.removeSurrounding(annotation)
            "$beforeSelection$cleanText$afterSelection"
        } else {
            "$beforeSelection~~$selectedText~~$afterSelection"
        }

        return result
    } else {
        val beforeCursor = text.substring(0, start)
        val afterCursor = text.substring(start)

        return "$beforeCursor~~$afterCursor~~"
    }
}

private fun applyItalics(text: String, selectionStart: Int, selectionEnd: Int): String {
    val annotation = "*"
    val start = minOf(selectionStart, selectionEnd)
    val end = maxOf(selectionStart, selectionEnd)

    if (start != end) {
        val beforeSelection = text.substring(0, start)
        val selectedText = text.substring(start, end)
        val afterSelection = text.substring(end)

        val result = if (selectedText.startsWith(annotation) && selectedText.endsWith(annotation)) {
            val cleanText = selectedText.removeSurrounding(annotation)
            "$beforeSelection$cleanText$afterSelection"
        } else {
            "${beforeSelection}*${selectedText}*${afterSelection}"
        }

        return result
    } else {
        val beforeCursor = text.substring(0, start)
        val afterCursor = text.substring(start)

        return "${beforeCursor}*${afterCursor}*"
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
                text = "Uploading audio...",
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

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun NoteEditorScreenPreview() {
    NoteEditorScreen(noteId = "0")
}