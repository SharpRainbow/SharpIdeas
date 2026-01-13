@file:OptIn(FlowPreview::class)

package ru.shrprnbw.ideas.presentation.screens.note_editor

import android.net.Uri
import android.util.Log
import androidx.compose.ui.text.TextRange
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
import ru.shrprnbw.ideas.domain.entity.AccessType
import ru.shrprnbw.ideas.domain.entity.ContentItem
import ru.shrprnbw.ideas.domain.entity.ContentType
import ru.shrprnbw.ideas.domain.entity.Group
import ru.shrprnbw.ideas.domain.entity.Note
import ru.shrprnbw.ideas.domain.usecase.AddNoteCollaboratorUseCase
import ru.shrprnbw.ideas.domain.usecase.AddNoteTagUseCase
import ru.shrprnbw.ideas.domain.usecase.AddNoteTextBlockUseCase
import ru.shrprnbw.ideas.domain.usecase.AddNoteToGroupUseCase
import ru.shrprnbw.ideas.domain.usecase.DeleteNoteContentUseCase
import ru.shrprnbw.ideas.domain.usecase.DeleteNoteUseCase
import ru.shrprnbw.ideas.domain.usecase.GetNoteInfoUseCase
import ru.shrprnbw.ideas.domain.usecase.GetOwnedGroupsUseCase
import ru.shrprnbw.ideas.domain.usecase.GetUserTagsUseCase
import ru.shrprnbw.ideas.domain.usecase.RemoveNoteCollaboratorUseCase
import ru.shrprnbw.ideas.domain.usecase.RemoveNoteFromGroupUseCase
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
    private val addNoteCollaboratorUseCase: AddNoteCollaboratorUseCase,
    private val removeNoteCollaboratorUseCase: RemoveNoteCollaboratorUseCase,
    private val addNoteToGroupUseCase: AddNoteToGroupUseCase,
    private val removeNoteFromGroupUseCase: RemoveNoteFromGroupUseCase,
    private val getOwnedGroupsUseCase: GetOwnedGroupsUseCase,
    private val externalScope: CoroutineScope
) : ViewModel() {

    private val saveNoteFlow = MutableSharedFlow<Unit>()
    private val contentUpdates = mutableMapOf<Long, ContentItem>()
    private val mutex = Mutex()
    private val _state = MutableStateFlow<NoteEditorState>(NoteEditorState.Initial)
    val state = _state.asStateFlow()

    val tagsLazyList = getUserTagsUseCase().cachedIn(viewModelScope)

    init {
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
                    saveNoteContents()
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

            is NoteEditorCommand.OpenTagsDialog -> {
                viewModelScope.launch {
                    try {
                        _state.update { previousState ->
                            if (previousState is NoteEditorState.Editing
                                && previousState.note.accessType != AccessType.VIEWER) {
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
                    if (previousState is NoteEditorState.Editing
                        && previousState.note.accessType != AccessType.VIEWER) {
                        previousState.copy(
                            isPreviewMode = !previousState.isPreviewMode
                        )
                    } else {
                        previousState
                    }
                }
            }

            NoteEditorCommand.ConfirmNoteDeletion -> {
                viewModelScope.launch {
                    _state.update { previousState ->
                        if (previousState is NoteEditorState.Editing) {
                            try {
                                deleteNoteUseCase(noteId)
                                NoteEditorState.Finished
                            } catch (e: Exception) {
                                Log.d("NoteEditorViewModel", "Note delete error: ${e.message}")
                                previousState.copy(
                                    showDeleteDialog = false,
                                    error = R.string.edit_note_delete_error
                                )
                            }
                        } else {
                            previousState
                        }
                    }
                }
            }

            NoteEditorCommand.DismissDeletionDialog -> {
                _state.update { previousState ->
                    if (previousState is NoteEditorState.Editing) {
                        previousState.copy(
                            showDeleteDialog = false
                        )
                    } else {
                        previousState
                    }
                }
            }
            NoteEditorCommand.ShowDeletionDialog -> {
                _state.update { previousState ->
                    if (previousState is NoteEditorState.Editing) {
                        previousState.copy(
                            showDeleteDialog = true
                        )
                    } else {
                        previousState
                    }
                }
            }

            is NoteEditorCommand.ApplyFormatting -> {
                val currentState = _state.value
                if (currentState is NoteEditorState.Editing && currentState.focusedContentIndex != -1) {
                    val start = currentState.selection.start
                    val end = currentState.selection.end
                    val contentItem = currentState.note.contents[currentState.focusedContentIndex]
                    if (contentItem.type != ContentType.TEXT) {
                        return
                    }
                    val content = contentItem.data
                    val formatType = command.formatType
                    var isHeading = false
                    val formatSymbol = when (formatType) {
                        FormatType.BOLD -> "**"

                        FormatType.ITALIC -> "*"

                        FormatType.STRIKETHROUGH -> "~~"

                        FormatType.MONOSPACE -> "```"

                        FormatType.HEADING_1 -> {
                            isHeading = true
                            "# "
                        }

                        FormatType.HEADING_2 -> {
                            isHeading = true
                            "## "
                        }

                        FormatType.HEADING_3 -> {
                            isHeading = true
                            "### "
                        }
                    }
                    if (start == end && !isHeading) {
                        return
                    }
                    var selectionStart = start
                    var selectionEnd = end
                    val newText = if (isHeading) {
                        applyHeading(formatSymbol, content, start, end).also { newText ->
                            selectionStart += (newText.length - content.length)
                            selectionEnd = selectionStart
                        }
                    } else {
                        applyTextStyle(
                            formatSymbol,
                            content,
                            start,
                            end
                        ).also { newText ->
                            selectionEnd += (newText.length - content.length)
                        }
                    }
                    _state.update {
                        if (it is NoteEditorState.Editing) {
                            it.copy(
                                selection = TextRange(
                                    selectionStart,
                                    selectionEnd
                                )
                            )
                        } else {
                            it
                        }
                    }
                    processCommand(NoteEditorCommand.InputContent(
                        newText,
                        currentState.focusedContentIndex
                    ))
                }
            }

            is NoteEditorCommand.SetFocus -> {
                _state.update { previousState ->
                    if (previousState is NoteEditorState.Editing) {
                        previousState.copy(
                            focusedContentIndex = command.contentIdx,
                            selection = command.selection
                        )
                    } else {
                        previousState
                    }
                }
            }

            NoteEditorCommand.OpenShareSheet -> {
                viewModelScope.launch {
                    _state.update { previousState ->
                        if (previousState is NoteEditorState.Editing) {
                            previousState.copy(
                                showShareSheet = true,
                                isShareLoading = true,
                                shareError = null
                            )
                        } else {
                            previousState
                        }
                    }
                    try {
                        getOwnedGroupsUseCase().collect { groups ->
                            _state.update { previousState ->
                                if (previousState is NoteEditorState.Editing) {
                                    previousState.copy(
                                        availableGroups = groups,
                                        isShareLoading = false
                                    )
                                } else {
                                    previousState
                                }
                            }
                        }
                    } catch (e: Exception) {
                        Log.d("NoteEditorViewModel", "Load groups error: ${e.message}")
                        _state.update { previousState ->
                            if (previousState is NoteEditorState.Editing) {
                                previousState.copy(
                                    isShareLoading = false
                                )
                            } else {
                                previousState
                            }
                        }
                    }
                }
            }

            NoteEditorCommand.CloseShareSheet -> {
                _state.update { previousState ->
                    if (previousState is NoteEditorState.Editing) {
                        previousState.copy(
                            showShareSheet = false,
                            collaboratorEmail = "",
                            shareError = null
                        )
                    } else {
                        previousState
                    }
                }
            }

            is NoteEditorCommand.InputCollaboratorEmail -> {
                _state.update { previousState ->
                    if (previousState is NoteEditorState.Editing) {
                        previousState.copy(
                            collaboratorEmail = command.email,
                            shareError = null
                        )
                    } else {
                        previousState
                    }
                }
            }

            is NoteEditorCommand.AddCollaboratorByEmail -> {
                viewModelScope.launch {
                    _state.update { previousState ->
                        if (previousState is NoteEditorState.Editing) {
                            previousState.copy(isShareLoading = true)
                        } else {
                            previousState
                        }
                    }
                    try {
                        addNoteCollaboratorUseCase(noteId, command.email)
                        loadNote(noteId)
                        _state.update { previousState ->
                            if (previousState is NoteEditorState.Editing) {
                                previousState.copy(
                                    collaboratorEmail = "",
                                    isShareLoading = false,
                                    shareError = null
                                )
                            } else {
                                previousState
                            }
                        }
                    } catch (e: Exception) {
                        Log.d("NoteEditorViewModel", "Add collaborator error: ${e.message}")
                        _state.update { previousState ->
                            if (previousState is NoteEditorState.Editing) {
                                previousState.copy(
                                    shareError = parseAddCollaboratorError(e.message),
                                    isShareLoading = false
                                )
                            } else {
                                previousState
                            }
                        }
                    }
                }
            }

            is NoteEditorCommand.RemoveCollaborator -> {
                viewModelScope.launch {
                    _state.update { previousState ->
                        if (previousState is NoteEditorState.Editing) {
                            previousState.copy(isShareLoading = true)
                        } else {
                            previousState
                        }
                    }
                    try {
                        removeNoteCollaboratorUseCase(noteId, command.collaboratorId)
                        loadNote(noteId)
                        _state.update { previousState ->
                            if (previousState is NoteEditorState.Editing) {
                                previousState.copy(
                                    isShareLoading = false,
                                    shareError = null
                                )
                            } else {
                                previousState
                            }
                        }
                    } catch (e: Exception) {
                        Log.d("NoteEditorViewModel", "Remove collaborator error: ${e.message}")
                        _state.update { previousState ->
                            if (previousState is NoteEditorState.Editing) {
                                previousState.copy(
                                    shareError = R.string.share_error_remove_collaborator,
                                    isShareLoading = false
                                )
                            } else {
                                previousState
                            }
                        }
                    }
                }
            }

            is NoteEditorCommand.AddToGroup -> {
                viewModelScope.launch {
                    _state.update { previousState ->
                        if (previousState is NoteEditorState.Editing) {
                            previousState.copy(isShareLoading = true)
                        } else {
                            previousState
                        }
                    }
                    try {
                        addNoteToGroupUseCase(noteId, command.groupId)
                        loadNote(noteId)
                        _state.update { previousState ->
                            if (previousState is NoteEditorState.Editing) {
                                previousState.copy(
                                    isShareLoading = false,
                                    shareError = null
                                )
                            } else {
                                previousState
                            }
                        }
                    } catch (e: Exception) {
                        Log.d("NoteEditorViewModel", "Add to group error: ${e.message}")
                        _state.update { previousState ->
                            if (previousState is NoteEditorState.Editing) {
                                previousState.copy(
                                    shareError = R.string.share_error_add_to_group,
                                    isShareLoading = false
                                )
                            } else {
                                previousState
                            }
                        }
                    }
                }
            }

            is NoteEditorCommand.RemoveFromGroup -> {
                viewModelScope.launch {
                    val currentState = _state.value
                    if (currentState is NoteEditorState.Editing) {
                        _state.update { previousState ->
                            if (previousState is NoteEditorState.Editing) {
                                previousState.copy(isShareLoading = true)
                            } else {
                                previousState
                            }
                        }
                        try {
                            removeNoteFromGroupUseCase(noteId, command.groupId)
                            loadNote(noteId)
                            _state.update { previousState ->
                                if (previousState is NoteEditorState.Editing) {
                                    previousState.copy(
                                        isShareLoading = false,
                                        shareError = null
                                    )
                                } else {
                                    previousState
                                }
                            }
                        } catch (e: Exception) {
                            Log.d("NoteEditorViewModel", "Remove from group error: ${e.message}")
                            _state.update { previousState ->
                                if (previousState is NoteEditorState.Editing) {
                                    previousState.copy(
                                        shareError = R.string.share_error_remove_from_group,
                                        isShareLoading = false
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
    }

    private fun parseAddCollaboratorError(message: String?): Int {
        return when {
            message?.contains("404") == true -> R.string.share_error_collaborator_not_found
            message?.contains("409") == true -> R.string.share_error_collaborator_already_added
            else -> R.string.share_error_add_collaborator
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

    private fun applyTextStyle(annotation: String, text: String, selectionStart: Int, selectionEnd: Int): String {
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
                "$beforeSelection${annotation}$selectedText${annotation}$afterSelection"
            }

            return result
        } else {
            val beforeCursor = text.substring(0, start)
            val afterCursor = text.substring(start)

            val result = "$beforeCursor${annotation}$afterCursor${annotation}"

            return result
        }
    }

    private fun applyHeading(symbol: String, content: String, start: Int, end: Int): String {
        val lines = content.lines().toMutableList()
        var charCount = 0
        for (i in lines.indices) {
            val line = lines[i]
            val lineStart = charCount
            val lineEnd = charCount + line.length
            if (lineStart <= start && lineEnd >= end) {
                lines[i] = if (line.startsWith(symbol)) {
                    line.removePrefix(symbol)
                } else {
                    "${symbol}$line"
                }
                break
            }
            charCount += line.length + 1
        }
        return lines.joinToString("\n")
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

    data object OpenTagsDialog : NoteEditorCommand

    data object CloseTagsDialog : NoteEditorCommand

    data class AddTag(val tag: String) : NoteEditorCommand

    data class RemoveTag(val tag: String) : NoteEditorCommand

    data class InputTagQuery(val query: String) : NoteEditorCommand

    data object ResetError : NoteEditorCommand

    data object TogglePreviewMode : NoteEditorCommand

    data object ShowDeletionDialog : NoteEditorCommand

    data object DismissDeletionDialog : NoteEditorCommand

    data object ConfirmNoteDeletion : NoteEditorCommand

    data class SetFocus(val contentIdx: Int, val selection: TextRange) : NoteEditorCommand

    data class ApplyFormatting(val formatType: FormatType) : NoteEditorCommand

    data object OpenShareSheet : NoteEditorCommand

    data object CloseShareSheet : NoteEditorCommand

    data class InputCollaboratorEmail(val email: String) : NoteEditorCommand

    data class AddCollaboratorByEmail(val email: String) : NoteEditorCommand

    data class RemoveCollaborator(val collaboratorId: String) : NoteEditorCommand

    data class AddToGroup(val groupId: Long) : NoteEditorCommand

    data class RemoveFromGroup(val groupId: Long) : NoteEditorCommand

}

enum class FormatType {
    BOLD,
    ITALIC,
    STRIKETHROUGH,
    MONOSPACE,
    HEADING_1,
    HEADING_2,
    HEADING_3
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
        val isPreviewMode: Boolean = note.accessType == AccessType.VIEWER,
        val uploadProgress: Float? = null,
        val showDeleteDialog: Boolean = false,
        val focusedContentIndex: Int = -1,
        val selection: TextRange = TextRange.Zero,
        val showShareSheet: Boolean = false,
        val collaboratorEmail: String = "",
        val availableGroups: List<Group> = emptyList(),
        val shareError: Int? = null,
        val isShareLoading: Boolean = false
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