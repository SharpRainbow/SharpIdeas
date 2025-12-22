package ru.shrprnbw.ideas.presentation.screens.transcription_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.shrprnbw.ideas.R
import ru.shrprnbw.ideas.domain.entity.Transcription
import ru.shrprnbw.ideas.domain.usecase.GetTranscriptionDetailUseCase

@HiltViewModel(assistedFactory = TranscriptionDetailViewModel.Factory::class)
class TranscriptionDetailViewModel @AssistedInject constructor(
    @Assisted("noteId") private val noteId: String,
    @Assisted("transcriptionId") private val transcriptionId: String,
    private val getTranscriptionDetailUseCase: GetTranscriptionDetailUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<TranscriptionDetailState>(TranscriptionDetailState.Loading)
    val state = _state.asStateFlow()

    init {
        loadTranscription()
    }

    private fun loadTranscription() {
        viewModelScope.launch {
            _state.update { TranscriptionDetailState.Loading }
            try {
                val transcription = getTranscriptionDetailUseCase(noteId, transcriptionId)
                _state.update { TranscriptionDetailState.Success(transcription) }
            } catch (e: Exception) {
                _state.update { TranscriptionDetailState.Error(R.string.error_network) }
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("noteId") noteId: String,
            @Assisted("transcriptionId") transcriptionId: String
        ): TranscriptionDetailViewModel
    }

}

sealed interface TranscriptionDetailState {
    data object Loading : TranscriptionDetailState
    data class Success(val transcription: Transcription) : TranscriptionDetailState
    data class Error(val errorMessage: Int) : TranscriptionDetailState
}
