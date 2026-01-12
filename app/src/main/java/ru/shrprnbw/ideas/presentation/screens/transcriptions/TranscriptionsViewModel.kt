package ru.shrprnbw.ideas.presentation.screens.transcriptions

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.shrprnbw.ideas.domain.entity.Transcription
import ru.shrprnbw.ideas.domain.usecase.DeleteTranscriptionUseCase
import ru.shrprnbw.ideas.domain.usecase.GetTranscriptionsUseCase
import ru.shrprnbw.ideas.domain.usecase.RequestNoteTranscriptionUseCase

@HiltViewModel(assistedFactory = TranscriptionsViewModel.Factory::class)
class TranscriptionsViewModel @AssistedInject constructor(
    @Assisted("noteId") private val noteId: String,
    @Assisted("hasAccess") private val hasAccess: Boolean,
    private val requestNoteTranscriptionUseCase: RequestNoteTranscriptionUseCase,
    private val deleteTranscriptionUseCase: DeleteTranscriptionUseCase,
    getTranscriptionsUseCase: GetTranscriptionsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(TranscriptionsScreenState(
        canCreateTranscriptions = hasAccess
    ))
    val state = _state.asStateFlow()
    val transcriptions: Flow<PagingData<Transcription>> =
        getTranscriptionsUseCase(noteId).cachedIn(viewModelScope)

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("noteId") noteId: String,
            @Assisted("hasAccess") hasAccess: Boolean
        ): TranscriptionsViewModel
    }

    fun processCommand(command: TranscriptionsCommand) {
        when (command) {
            is TranscriptionsCommand.ShowAddTranscriptionDialog -> {
                if (!hasAccess)
                    return
                _state.update {
                    it.copy(showAddTranscriptionDialog = command.show)
                }
            }

            TranscriptionsCommand.AddTranscriptionTask -> {
                if (!hasAccess)
                    return
                viewModelScope.launch {
                    _state.update { previousState ->
                        try {
                            requestNoteTranscriptionUseCase(noteId)
                            previousState.copy(showAddTranscriptionDialog = false)
                        } catch (e: Exception) {
                            previousState.copy(
                                showAddTranscriptionDialog = false,
                                error = parseErrorMessage(e)
                            )
                        }
                    }
                }
            }

            TranscriptionsCommand.ResetError -> {
                _state.update {
                    it.copy(error = null)
                }
            }

            is TranscriptionsCommand.DeleteTranscription -> {
                if (!hasAccess)
                    return
                viewModelScope.launch {
                    try {
                        deleteTranscriptionUseCase(noteId, command.transcriptionId)
                    } catch (e: Exception) {
                        Log.d("TranscriptionsViewModel", "Error deleting transcription", e)
                        _state.update {
                            it.copy(error = ru.shrprnbw.ideas.R.string.transcriptions_error_delete)
                        }
                    }
                }
            }
        }
    }

    private fun parseErrorMessage(e: Exception): Int {
        Log.d("TranscriptionsViewModel", "Error requesting transcription", e)
        return when {
            e.message?.contains("400") == true ->
                ru.shrprnbw.ideas.R.string.transcriptions_error_content_invalid
            e.message?.contains("409") == true ->
                ru.shrprnbw.ideas.R.string.transcriptions_error_rate_limit
            e.message?.contains("429") == true ->
                ru.shrprnbw.ideas.R.string.transcriptions_error_limit_reached
            else -> ru.shrprnbw.ideas.R.string.transcriptions_error_unknown
        }
    }

}

sealed interface TranscriptionsCommand {
    data class ShowAddTranscriptionDialog(val show: Boolean) : TranscriptionsCommand

    data object AddTranscriptionTask : TranscriptionsCommand

    data object ResetError : TranscriptionsCommand

    data class DeleteTranscription(val transcriptionId: String) : TranscriptionsCommand
}

data class TranscriptionsScreenState(
    val canCreateTranscriptions: Boolean,
    val showAddTranscriptionDialog: Boolean = false,
    val error: Int? = null
)
