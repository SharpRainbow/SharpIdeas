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
import ru.shrprnbw.ideas.domain.usecase.GetTranscriptionsUseCase
import ru.shrprnbw.ideas.domain.usecase.RequestNoteTranscriptionUseCase

@HiltViewModel(assistedFactory = TranscriptionsViewModel.Factory::class)
class TranscriptionsViewModel @AssistedInject constructor(
    @Assisted("noteId") private val noteId: String,
    private val requestNoteTranscriptionUseCase: RequestNoteTranscriptionUseCase,
    getTranscriptionsUseCase: GetTranscriptionsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(TranscriptionsScreenState())
    val state = _state.asStateFlow()
    val transcriptions: Flow<PagingData<Transcription>> =
        getTranscriptionsUseCase(noteId).cachedIn(viewModelScope)

    @AssistedFactory
    interface Factory {
        fun create(@Assisted("noteId") noteId: String): TranscriptionsViewModel
    }

    fun processCommand(command: TranscriptionsCommand) {
        when (command) {
            is TranscriptionsCommand.ShowAddTranscriptionDialog -> {
                _state.update {
                    it.copy(showAddTranscriptionDialog = command.show)
                }
            }

            TranscriptionsCommand.AddTranscriptionTask -> {
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
        }
    }

    private fun parseErrorMessage(e: Exception): Int {
        Log.d("TranscriptionsViewModel", "Error requesting transcription", e)
        return when {
            e.message?.contains("429") == true ->
                ru.shrprnbw.ideas.R.string.transcriptions_error_rate_limit
            else -> ru.shrprnbw.ideas.R.string.transcriptions_error_unknown
        }
    }

}

sealed interface TranscriptionsCommand {
    data class ShowAddTranscriptionDialog(val show: Boolean) : TranscriptionsCommand

    data object AddTranscriptionTask : TranscriptionsCommand

    data object ResetError : TranscriptionsCommand
}

data class TranscriptionsScreenState(
    val showAddTranscriptionDialog: Boolean = false,
    val error: Int? = null
)
