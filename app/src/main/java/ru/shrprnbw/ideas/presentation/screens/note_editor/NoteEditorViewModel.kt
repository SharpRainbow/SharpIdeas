@file:OptIn(FlowPreview::class)

package ru.shrprnbw.ideas.presentation.screens.note_editor

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.shrprnbw.ideas.R
import ru.shrprnbw.ideas.domain.entity.ContentItem
import ru.shrprnbw.ideas.domain.entity.Note
import ru.shrprnbw.ideas.domain.usecase.AddNoteTagUseCase
import ru.shrprnbw.ideas.domain.usecase.AddNoteTextBlockUseCase
import ru.shrprnbw.ideas.domain.usecase.DeleteNoteContentUseCase
import ru.shrprnbw.ideas.domain.usecase.DeleteNoteUseCase
import ru.shrprnbw.ideas.domain.usecase.GetNoteInfoUseCase
import ru.shrprnbw.ideas.domain.usecase.GetUserTagsUseCase
import ru.shrprnbw.ideas.domain.usecase.RemoveNoteTagUseCase
import ru.shrprnbw.ideas.domain.usecase.UpdateNoteTextUseCase
import ru.shrprnbw.ideas.domain.usecase.UpdateNoteUseCase
import ru.shrprnbw.ideas.domain.usecase.UploadAudioUseCase
import ru.shrprnbw.ideas.domain.usecase.UploadImageUseCase

@HiltViewModel(assistedFactory = NoteEditorViewModel.Factory::class)
class NoteEditorViewModel @AssistedInject constructor(
    @Assisted("noteId") private val noteId: String,
    private val getNoteInfoUseCase: GetNoteInfoUseCase,
    private val updateNoteUseCase: UpdateNoteUseCase,
    private val uploadImageUseCase: UploadImageUseCase,
    private val uploadAudioUseCase: UploadAudioUseCase,
    private val deleteNoteContentUseCase: DeleteNoteContentUseCase,
    private val addNoteTextBlockUseCase: AddNoteTextBlockUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val addNoteTagUseCase: AddNoteTagUseCase,
    private val removeNoteTagUseCase: RemoveNoteTagUseCase,
    private val getUserTagsUseCase: GetUserTagsUseCase,
    private val updateNoteTextUseCase: UpdateNoteTextUseCase,
    private val externalScope: CoroutineScope
) : ViewModel() {

    private val saveNoteFlow = MutableSharedFlow<Unit>()
    private val contentUpdates = mutableMapOf<Long, ContentItem>()
    private val mutex = Mutex()
    private val _state = MutableStateFlow<NoteEditorState>(NoteEditorState.Initial)
    val state = _state.asStateFlow()

    val tagsLazyList = getUserTagsUseCase().cachedIn(viewModelScope)

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
                viewModelScope.launch {
                    _state.update { previousState ->
                        if (previousState is NoteEditorState.Editing) {
                            val newContents = previousState.note.contents.toMutableList()
                            val editedId = newContents[command.index].id
                            if (newContents[command.index].data == command.content) {
                                return@update previousState
                            }
                            newContents[command.index] = newContents[command.index].copy(
                                data = command.content,
                                edited = editedId != -1L
                            )
                            previousState.copy(
                                note = previousState.note.copy(
                                    contents = newContents
                                )
                            ).also {
                                mutex.withLock {
                                    contentUpdates[editedId] = newContents[command.index]
                                    saveNoteFlow.emit(Unit)
                                }
                            }
                        } else {
                            previousState
                        }
                    }
                }
            }

            is NoteEditorCommand.InputTitle -> {
                viewModelScope.launch {
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

            is NoteEditorCommand.UploadAudio -> {
                uploadAudioUseCase(noteId, command.uri).onCompletion { cause ->
                    if (cause == null) {
                        loadNote(noteId)
                        _state.update { previousState ->
                            if (previousState is NoteEditorState.Editing) {
                                previousState.copy(
                                    uploadProgress = null
                                )
                            } else {
                                previousState
                            }
                        }
                    }
                }.onEach { progress ->
                    _state.update { previousState ->
                        if (previousState is NoteEditorState.Editing) {
                            previousState.copy(
                                uploadProgress = progress
                            )
                        } else {
                            previousState
                        }
                    }
                }.catch { e ->
                    Log.d("NoteEditorViewModel", "Audio upload error: ${e.message}")
                    _state.update { previousState ->
                        if (previousState is NoteEditorState.Editing) {
                            previousState.copy(
                                error = R.string.edit_note_audio_upload_error,
                                uploadProgress = null
                            )
                        } else {
                            previousState
                        }
                    }
                }.launchIn(viewModelScope)
            }

            is NoteEditorCommand.DeleteContentItem -> {
                viewModelScope.launch {
                    try {
                        val contentId = command.contentId.toLong()
                        deleteNoteContentUseCase(noteId, contentId)
                        launch {
                            mutex.withLock {
                                contentUpdates.remove(contentId)
                                saveNoteFlow.emit(Unit)
                            }
                        }
                        launch {
                            loadNote(noteId)
                        }
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

            is NoteEditorCommand.DeleteNote -> {
                viewModelScope.launch {
                    _state.update { previousState ->
                        if (previousState is NoteEditorState.Editing) {
                            try {
                                deleteNoteUseCase(noteId)
                                NoteEditorState.Finished
                            } catch (e: Exception) {
                                Log.d("NoteEditorViewModel", "Note delete error: ${e.message}")
                                previousState.copy(
                                    error = R.string.edit_note_delete_error
                                )
                            }
                        } else {
                            previousState
                        }
                    }
                }
            }

            is NoteEditorCommand.OpenTagsDialog -> {
                viewModelScope.launch {
                    try {
                        _state.update { previousState ->
                            if (previousState is NoteEditorState.Editing) {
                                previousState.copy(
                                    showTagsDialog = true,
                                    tagError = null
                                )
                            } else {
                                previousState
                            }
                        }
                    } catch (e: Exception) {
                        Log.d("NoteEditorViewModel", "Load tags error: ${e.message}")
                    }
                }
            }

            is NoteEditorCommand.CloseTagsDialog -> {
                _state.update { previousState ->
                    if (previousState is NoteEditorState.Editing) {
                        previousState.copy(
                            showTagsDialog = false,
                            tagQuery = ""
                        )
                    } else {
                        previousState
                    }
                }
            }

            is NoteEditorCommand.InputTagQuery -> {
                _state.update { previousState ->
                    if (previousState is NoteEditorState.Editing) {
                        previousState.copy(
                            tagQuery = command.query,
                            tagError = null
                        )
                    } else {
                        previousState
                    }
                }
            }

            is NoteEditorCommand.AddTag -> {
                viewModelScope.launch {
                    try {
                        addNoteTagUseCase(noteId, command.tag)
                        _state.update { previousState ->
                            if (previousState is NoteEditorState.Editing) {
                                previousState.copy(
                                    tagQuery = "",
                                    note = previousState.note.copy(
                                        tags = previousState.note.tags + command.tag
                                    )
                                )
                            } else {
                                previousState
                            }
                        }
                    } catch (e: Exception) {
                        Log.d("NoteEditorViewModel", "Add tag error: ${e.message}")
                        _state.update { previousState ->
                            if (previousState is NoteEditorState.Editing) {
                                previousState.copy(
                                    tagError = if (e.message?.contains("409") == true)
                                        R.string.note_tags_error_duplicate
                                    else
                                        R.string.note_tags_error_add,
                                )
                            } else {
                                previousState
                            }
                        }
                    }
                }
            }

            is NoteEditorCommand.RemoveTag -> {
                viewModelScope.launch {
                    try {
                        removeNoteTagUseCase(noteId, command.tag)
                        _state.update { previousState ->
                            if (previousState is NoteEditorState.Editing) {
                                previousState.copy(
                                    tagQuery = "",
                                    note = previousState.note.copy(
                                        tags = previousState.note.tags - command.tag
                                    )
                                )
                            } else {
                                previousState
                            }
                        }
                    } catch (e: Exception) {
                        Log.d("NoteEditorViewModel", "Remove tag error: ${e.message}")
                        _state.update { previousState ->
                            if (previousState is NoteEditorState.Editing) {
                                previousState.copy(
                                    error = R.string.note_tags_error_remove
                                )
                            } else {
                                previousState
                            }
                        }
                    }
                }
            }

            NoteEditorCommand.ResetError -> {
                _state.update { previousState ->
                    if (previousState is NoteEditorState.Editing) {
                        previousState.copy(
                            error = null
                        )
                    } else {
                        previousState
                    }
                }
            }

            NoteEditorCommand.TogglePreviewMode -> {
                _state.update { previousState ->
                    if (previousState is NoteEditorState.Editing) {
                        previousState.copy(
                            isPreviewMode = !previousState.isPreviewMode
                        )
                    } else {
                        previousState
                    }
                }
            }
        }
    }

    private suspend fun loadNote(noteId: String) {
        _state.update { previousState ->
            try {
                val note = getNoteInfoUseCase(noteId)
                val contentItems = note.contents.toMutableList()
                if (previousState is NoteEditorState.Editing) {
                    previousState.copy(
                        note = note.copy(contents = contentItems)
                    )
                } else {
                    NoteEditorState.Editing(
                        note = note.copy(contents = contentItems)
                    )
                }
            } catch (e: Exception) {
                Log.d("NoteEditorViewModel", "Load note error: ${e.message}")
                if (previousState is NoteEditorState.Editing) {
                    previousState.copy(
                        error = R.string.edit_note_load_error
                    )
                } else {
                    NoteEditorState.Finished
                }
            }
        }
    }

    private fun autoSaveNote() {
        saveNoteFlow
            .debounce(2000)
            .onEach {
                saveNoteContents()
            }
            .onCompletion {
                externalScope.launch {
                    saveNoteContents()
                }
            }
            .launchIn(viewModelScope)
        state
            .filter { (it is NoteEditorState.Editing) && it.note.noteUpdated }
            .debounce(500)
            .onEach { currentState ->
                (currentState as? NoteEditorState.Editing)?.let {
                    saveNote(it.note)
                }
            }
            .launchIn(viewModelScope)
    }

    private suspend fun saveNoteContents() {
        mutex.lock()
        try {
            updateNoteTextUseCase(noteId, contentUpdates.values.toList())
            contentUpdates.clear()
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
        } finally {
            mutex.unlock()
        }
    }

    private suspend fun saveNote(note: Note) {
        try {
            updateNoteUseCase(note)
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

    data class UploadAudio(val uri: Uri) : NoteEditorCommand

    data class DeleteContentItem(val contentId: Int) : NoteEditorCommand

    data object DeleteNote : NoteEditorCommand

    data object OpenTagsDialog : NoteEditorCommand

    data object CloseTagsDialog : NoteEditorCommand

    data class AddTag(val tag: String) : NoteEditorCommand

    data class RemoveTag(val tag: String) : NoteEditorCommand

    data class InputTagQuery(val query: String) : NoteEditorCommand

    data object ResetError : NoteEditorCommand

    data object TogglePreviewMode : NoteEditorCommand

}

sealed interface NoteEditorState {

    data object Initial : NoteEditorState

    data class Editing(
        val note: Note,
        val showTagsDialog: Boolean = false,
        val tagQuery: String = "",
        val availableTags: Set<Long> = emptySet(),
        val error: Int? = null,
        val tagError: Int? = null,
        val isPreviewMode: Boolean = false,
        val uploadProgress: Float? = null
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