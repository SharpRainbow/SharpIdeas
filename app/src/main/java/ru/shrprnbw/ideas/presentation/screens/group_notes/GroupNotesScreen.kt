package ru.shrprnbw.ideas.presentation.screens.group_notes

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import ru.shrprnbw.ideas.R
import ru.shrprnbw.ideas.presentation.screens.NoteItem
import ru.shrprnbw.ideas.presentation.screens.NoteType
import ru.shrprnbw.ideas.presentation.screens.PagedItemsTemplate
import ru.shrprnbw.ideas.utils.DateFormatter
import ru.shrprnbw.ideas.utils.Utils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupNotesScreen(
    modifier: Modifier = Modifier,
    groupId: Long,
    screenTitle: String,
    viewModel: GroupNotesViewModel = hiltViewModel(
        creationCallback = { factory: GroupNotesViewModel.Factory ->
            factory.create(groupId)
        }
    ),
    onNoteClicked: (noteId: String) -> Unit,
    onBackClicked: () -> Unit,
) {
    val noteItems = viewModel.notesFlow.collectAsLazyPagingItems()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = screenTitle,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.back_button_description)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        PagedItemsTemplate(
            items = noteItems,
            paddingValues = paddingValues,
            errorText = stringResource(R.string.note_list_error_load),
            keyProducer = { index -> noteItems[index]?.id ?: index },
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
}