@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package ru.shrprnbw.ideas.presentation.screens.note_editor

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material.icons.outlined.AudioFile
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.material.icons.rounded.AutoFixHigh
import androidx.compose.material.icons.rounded.Discount
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import ru.shrprnbw.ideas.R
import ru.shrprnbw.ideas.domain.entity.ContentItem
import ru.shrprnbw.ideas.domain.entity.ContentType
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
    onBackClicked: () -> Unit = { }
) { // TODO: Add instrument bottom bar for text formatting (bold, italic, underline, etc.) + image and audio insertion
    // TODO: Add fab with available transformations

    val state = viewModel.state.collectAsState()
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        uri?.let {
            viewModel.processCommand(NoteEditorCommand.UploadImage(uri))
        }
    }

    when (val currentState = state.value) {

        NoteEditorState.Initial -> {

        }

        is NoteEditorState.Editing -> {
            Scaffold(
                modifier = modifier
                    .fillMaxWidth()
                    .imePadding(),
                topBar = {
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
                            if (currentState.note.audioNote) {
                                Icon(
                                    modifier = Modifier
                                        .padding(end = 8.dp)
                                        .clickable(
                                            onClick = {
//                                            imagePicker.launch(
//                                                PickVisualMediaRequest(
//                                                    mediaType = ActivityResultContracts
//                                                )
//                                            )
                                            }
                                        ),
                                    imageVector = Icons.Outlined.AudioFile,
                                    contentDescription = stringResource(R.string.add_audio),
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            } else {
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
                        }
                    )
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier.padding(innerPadding)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        if (currentState.note.audioNote) {
                            NoteTransformation(
                                modifier = Modifier.weight(1f),
                                actionName = "Transcriptions",
                                color = AudioGreen,
                                imageVector = Icons.Rounded.GraphicEq
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
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        value = currentState.note.title,
                        onValueChange = { title ->
                            viewModel.processCommand(
                                NoteEditorCommand.InputTitle(title)
                            )
                        },
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
                        text = DateFormatter.formatCurrentDate(),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Content(
                        modifier = Modifier.weight(1f),
                        contentList = currentState.note.contents,
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
                        }
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
    contentList: List<ContentItem>,
    onTextChanged: (Int, String) -> Unit,
    onDeleteImageClick: (Int) -> Unit,
    onDeleteTextRequested: (Int) -> Unit
) {
    LazyColumn(
        modifier = modifier
    ) {
        itemsIndexed(
            contentList,
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
                        onTextInput = { input ->
                            onTextChanged(itemIndex, input)
                        },
                        onDeleteRequest = {
                            onDeleteTextRequested(contentItem.id.toInt())
                        }
                    )
                }

                ContentType.IMAGE -> {
                    val previousItem = contentList.getOrNull(itemIndex - 1)
                    if (previousItem?.type != ContentType.IMAGE) {
                        contentList.drop(itemIndex).takeWhile { item ->
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
    onDeleteRequest: () -> Unit = {}
) {
    TextField(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
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
            },
        value = text,
        onValueChange = onTextInput,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        textStyle = TextStyle(
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface
        ),
        placeholder = {
            Text(
                text = stringResource(R.string.add_text_hint),
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
            )
        }
    )
}

@Composable
private fun NoteTransformation(
    modifier: Modifier = Modifier,
    actionName: String,
    imageVector: ImageVector,
    color: Color = Utils.generateColorContrast(actionName)
) {
    Column(
        modifier = modifier.background(
            color = color.copy(alpha = 0.1f),
            shape = RoundedCornerShape(8.dp)
        ).border(
            width = 1.dp,
            color = color,
            shape = RoundedCornerShape(8.dp)
        ).padding(12.dp),
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
@Preview(showBackground = true, showSystemUi = true)
fun NoteEditorScreenPreview() {
    NoteEditorScreen(noteId = "0")
}