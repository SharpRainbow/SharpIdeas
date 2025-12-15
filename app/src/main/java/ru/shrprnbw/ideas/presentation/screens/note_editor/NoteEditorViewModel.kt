@file:OptIn(FlowPreview::class)

package ru.shrprnbw.ideas.presentation.screens.note_editor

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.shrprnbw.ideas.R
import ru.shrprnbw.ideas.domain.entity.Note
import ru.shrprnbw.ideas.domain.usecase.AddNoteTextBlockUseCase
import ru.shrprnbw.ideas.domain.usecase.DeleteNoteContentUseCase
import ru.shrprnbw.ideas.domain.usecase.GetNoteInfoUseCase
import ru.shrprnbw.ideas.domain.usecase.UpdateNoteContentUseCase
import ru.shrprnbw.ideas.domain.usecase.UpdateNoteUseCase
import ru.shrprnbw.ideas.domain.usecase.UploadImageUseCase

@HiltViewModel(assistedFactory = NoteEditorViewModel.Factory::class)
class NoteEditorViewModel @AssistedInject constructor(
    @Assisted("noteId") private val noteId: String,
    private val getNoteInfoUseCase: GetNoteInfoUseCase,
    private val updateNoteContentUseCase: UpdateNoteContentUseCase,
    private val updateNoteUseCase: UpdateNoteUseCase,
    private val uploadImageUseCase: UploadImageUseCase,
    private val deleteNoteContentUseCase: DeleteNoteContentUseCase,
    private val addNoteTextBlockUseCase: AddNoteTextBlockUseCase,
    private val externalScope: CoroutineScope
) : ViewModel() {

    private val _state = MutableStateFlow<NoteEditorState>(NoteEditorState.Initial)
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            loadNote(noteId)
        }
        autoSaveNote()
    }

    fun processCommand(command: NoteEditorCommand) {
        when (command) {
            is NoteEditorCommand.LoadNote -> {
                viewModelScope.launch {
                    loadNote(noteId)
                }
            }

            NoteEditorCommand.AddNoteTextItem -> {
                viewModelScope.launch {
                    addNoteTextBlockUseCase(noteId, "")
                    loadNote(noteId)
                }
            }

            is NoteEditorCommand.InputContent -> {
                _state.update { previousState ->
                    if (previousState is NoteEditorState.Editing) {
                        val newContents = previousState.note.contents.toMutableList()
                        val editedId = newContents[command.index].id
                        newContents[command.index] = newContents[command.index].copy(
                            data = command.content,
                            edited = editedId != -1L
                        )
                        previousState.copy(
                            note = previousState.note.copy(
                                contents = newContents
                            )
                        )
                    } else {
                        previousState
                    }
                }
            }

            is NoteEditorCommand.InputTitle -> {
                _state.update { previousState ->
                    if (previousState is NoteEditorState.Editing) {
                        previousState.copy(
                            note = previousState.note.copy(
                                title = command.title,
                                noteUpdated = true
                            )
                        )
                    } else {
                        previousState
                    }
                }
            }

            is NoteEditorCommand.UploadImage -> {
                viewModelScope.launch {
                    try {
                        uploadImageUseCase(noteId, command.uri)
                        loadNote(noteId)
                    } catch (e: Exception) {
                        Log.d("NoteEditorViewModel", "Image upload error: ${e.message}")
                        _state.update { previousState ->
                            if (previousState is NoteEditorState.Editing) {
                                previousState.copy(
                                    error = R.string.edit_note_image_upload_error
                                )
                            } else {
                                previousState
                            }
                        }
                    }
                }
            }

            is NoteEditorCommand.DeleteContentItem -> {
                viewModelScope.launch {
                    try {
                        deleteNoteContentUseCase(noteId, command.contentId.toLong())
                        loadNote(noteId)
                    } catch (e: Exception) {
                        Log.d("NoteEditorViewModel", "Content delete error: ${e.message}")
                        _state.update { previousState ->
                            if (previousState is NoteEditorState.Editing) {
                                previousState.copy(
                                    error = R.string.edit_note_content_delete_error
                                )
                            } else {
                                previousState
                            }
                        }
                    }
                }
            }
        }
    }

    private suspend fun loadNote(noteId: String) {
        val note = getNoteInfoUseCase(noteId)
        val contentItems = note.contents.toMutableList()
        _state.update {
            NoteEditorState.Editing(
                note = note.copy(contents = contentItems)
            )
        }
    }

    private fun autoSaveNote() {
        state.filter {
            it is NoteEditorState.Editing && it.isSaveEnabled
        }
            .debounce(2000)
            .onEach {
                saveNote()
            }
            .onCompletion {
                externalScope.launch {
                    saveNote()
                }
            }
            .launchIn(viewModelScope)
    }

    private suspend fun saveNote() {
        try {
            val previousState = _state.value
            if (previousState is NoteEditorState.Editing) {
                val note = previousState.note
                updateNoteUseCase(note)
                updateNoteContentUseCase(note)
            }
        } catch (e: Exception) {
            Log.d("NoteEditorViewModel", "Save command error: ${e.message}")
            _state.update { previousState ->
                if (previousState is NoteEditorState.Editing) {
                    previousState.copy(
                        error = R.string.edit_note_error
                    )
                } else {
                    previousState
                }
            }
        }
    }

    @AssistedFactory
    interface Factory {

        fun create(@Assisted("noteId") noteId: String): NoteEditorViewModel
    }

}

sealed interface NoteEditorCommand {

    data object LoadNote : NoteEditorCommand

    data object AddNoteTextItem : NoteEditorCommand

    data class InputTitle(val title: String) : NoteEditorCommand

    data class InputContent(val content: String, val index: Int) : NoteEditorCommand

    data class UploadImage(val uri: Uri) : NoteEditorCommand

    data class DeleteContentItem(val contentId: Int) : NoteEditorCommand

}

sealed interface NoteEditorState {

    data object Initial : NoteEditorState

    data class Editing(
        val note: Note,
        val error: Int? = null
    ) : NoteEditorState {
        val isSaveEnabled: Boolean
            get() {
                return when {
                    note.title.isBlank() && note.contents.isEmpty() -> false
                    else -> {
                        note.contents.any {
                            it.data.isNotBlank()
                        }
                    }
                }
            }
    }

    data object Finished : NoteEditorState

}