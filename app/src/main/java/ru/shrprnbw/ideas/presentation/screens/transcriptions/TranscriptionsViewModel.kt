package ru.shrprnbw.ideas.presentation.screens.transcriptions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import ru.shrprnbw.ideas.domain.entity.Transcription
import ru.shrprnbw.ideas.domain.usecase.GetTranscriptionsUseCase

@HiltViewModel(assistedFactory = TranscriptionsViewModel.Factory::class)
class TranscriptionsViewModel @AssistedInject constructor(
    @Assisted("noteId") private val noteId: String,
    getTranscriptionsUseCase: GetTranscriptionsUseCase
) : ViewModel() {

    val transcriptions: Flow<PagingData<Transcription>> =
        getTranscriptionsUseCase(noteId).cachedIn(viewModelScope)

    @AssistedFactory
    interface Factory {
        fun create(@Assisted("noteId") noteId: String): TranscriptionsViewModel
    }

}
