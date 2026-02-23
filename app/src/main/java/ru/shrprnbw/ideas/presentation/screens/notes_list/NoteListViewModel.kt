package ru.shrprnbw.ideas.presentation.screens.notes_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.shrprnbw.ideas.domain.entity.NotePreview
import ru.shrprnbw.ideas.domain.entity.NoteType
import ru.shrprnbw.ideas.domain.usecase.CreateNoteUseCase
import ru.shrprnbw.ideas.domain.usecase.LoadPersonalNotesUseCase
import javax.inject.Inject

@HiltViewModel
class NoteListViewModel @Inject constructor(
    private val getPersonalNotesUseCase: LoadPersonalNotesUseCase,
    private val createNoteUseCase: CreateNoteUseCase
): ViewModel() {

    private val _state = MutableStateFlow(NoteListState())
    val state = _state.asStateFlow()
    val notesFlow: Flow<PagingData<NotePreview>> = getPersonalNotesUseCase().cachedIn(viewModelScope)

    fun processCommand(command: NoteListCommand) {
        when (command) {

            is NoteListCommand.CreateTextNote -> {
                _state.update { previousState ->
                    previousState.copy(
                        showAddTextNoteDialog = false,
                        newNoteTitle = ""
                    )
                }
                viewModelScope.launch {
                    try {
                        createNoteUseCase(command.title, NoteType.TEXT)
                    } catch (_: Exception) {
                        //TODO: Handle
                    }
                }
            }

            is NoteListCommand.CreateAudioNote -> {
                _state.update { previousState ->
                    previousState.copy(
                        showAddAudioNoteDialog = false,
                        newNoteTitle = ""
                    )
                }
                viewModelScope.launch {
                    try {
                        createNoteUseCase(command.title, NoteType.AUDIO)
                    } catch (_: Exception) {
                        //TODO: Handle
                    }
                }
            }

            is NoteListCommand.CreateBoard -> {
                _state.update { previousState ->
                    previousState.copy(
                        showAddBoardDialog = false,
                        newNoteTitle = ""
                    )
                }
                viewModelScope.launch {
                    try {
                        createNoteUseCase(command.title, NoteType.BOARD)
                    } catch (_: Exception) {
                    }
                }
            }

            is NoteListCommand.InputNewNoteTitle -> {
                _state.update { previousState ->
                    previousState.copy(
                        newNoteTitle = command.title
                    )
                }
            }

            is NoteListCommand.ShowAddAudioNoteDialog -> {
                _state.update { previousState ->
                    previousState.copy(
                        showAddAudioNoteDialog = command.show,
                        newNoteTitle = ""
                    )
                }
            }

            is NoteListCommand.ShowAddTextNoteDialog -> {
                _state.update { previousState ->
                    previousState.copy(
                        showAddTextNoteDialog = command.show,
                        newNoteTitle = ""
                    )
                }
            }

            is NoteListCommand.ShowAddBoardDialog -> {
                _state.update { previousState ->
                    previousState.copy(
                        showAddBoardDialog = command.show,
                        newNoteTitle = ""
                    )
                }
            }
        }
    }

}

sealed interface NoteListCommand {

    data class CreateTextNote(val title: String): NoteListCommand

    data class CreateAudioNote(val title: String): NoteListCommand

    data class CreateBoard(val title: String): NoteListCommand

    data class ShowAddTextNoteDialog(val show: Boolean): NoteListCommand

    data class ShowAddAudioNoteDialog(val show: Boolean): NoteListCommand

    data class ShowAddBoardDialog(val show: Boolean): NoteListCommand

    data class InputNewNoteTitle(val title: String): NoteListCommand

}

data class NoteListState(
    val showAddTextNoteDialog: Boolean = false,
    val showAddAudioNoteDialog: Boolean = false,
    val showAddBoardDialog: Boolean = false,
    val newNoteTitle: String = ""
)
