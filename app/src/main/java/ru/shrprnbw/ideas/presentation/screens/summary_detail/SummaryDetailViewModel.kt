package ru.shrprnbw.ideas.presentation.screens.summary_detail

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
import ru.shrprnbw.ideas.domain.entity.Summary
import ru.shrprnbw.ideas.domain.usecase.GetSummaryDetailUseCase

@HiltViewModel(assistedFactory = SummaryDetailViewModel.Factory::class)
class SummaryDetailViewModel @AssistedInject constructor(
    @Assisted("noteId") private val noteId: String,
    @Assisted("summaryId") private val summaryId: String,
    private val getSummaryDetailUseCase: GetSummaryDetailUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<SummaryDetailState>(SummaryDetailState.Loading)
    val state = _state.asStateFlow()

    init {
        loadSummary()
    }

    private fun loadSummary() {
        viewModelScope.launch {
            _state.update { SummaryDetailState.Loading }
            try {
                val summary = getSummaryDetailUseCase(noteId, summaryId)
                _state.update { SummaryDetailState.Success(summary) }
            } catch (e: Exception) {
                _state.update { SummaryDetailState.Error(R.string.error_network) }
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("noteId") noteId: String,
            @Assisted("summaryId") summaryId: String
        ): SummaryDetailViewModel
    }

}

sealed interface SummaryDetailState {
    data object Loading : SummaryDetailState
    data class Success(val summary: Summary) : SummaryDetailState
    data class Error(val errorMessage: Int) : SummaryDetailState
}
