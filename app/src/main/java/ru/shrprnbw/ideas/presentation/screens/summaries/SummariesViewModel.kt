package ru.shrprnbw.ideas.presentation.screens.summaries

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
import ru.shrprnbw.ideas.domain.entity.Summary
import ru.shrprnbw.ideas.domain.usecase.GetSummariesUseCase
import ru.shrprnbw.ideas.domain.usecase.RequestNoteSummaryUseCase

@HiltViewModel(assistedFactory = SummariesViewModel.Factory::class)
class SummariesViewModel @AssistedInject constructor(
    @Assisted("noteId") private val noteId: String,
    @Assisted("hasAccess") private val hasAccess: Boolean,
    private val requestNoteSummaryUseCase: RequestNoteSummaryUseCase,
    getSummariesUseCase: GetSummariesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SummariesScreenState(
        canCreateSummaries = hasAccess
    ))
    val state = _state.asStateFlow()
    val summaries: Flow<PagingData<Summary>> =
        getSummariesUseCase(noteId).cachedIn(viewModelScope)

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("noteId") noteId: String,
            @Assisted("hasAccess") hasAccess: Boolean
        ): SummariesViewModel
    }

    fun processCommand(command: SummariesCommand) {
        when (command) {
            is SummariesCommand.ShowAddSummaryDialog -> {
                if (!hasAccess)
                    return
                _state.update {
                    it.copy(showAddSummaryDialog = command.show)
                }
            }

            SummariesCommand.AddSummaryTask -> {
                if (!hasAccess)
                    return
                viewModelScope.launch {
                    _state.update { previousState ->
                        try {
                            requestNoteSummaryUseCase(noteId)
                            previousState.copy(showAddSummaryDialog = false)
                        } catch (e: Exception) {
                            previousState.copy(
                                showAddSummaryDialog = false,
                                error = parseErrorMessage(e)
                            )
                        }
                    }
                }
            }

            SummariesCommand.ResetError -> {
                _state.update {
                    it.copy(error = null)
                }
            }
        }
    }

    private fun parseErrorMessage(e: Exception): Int {
        Log.d("SummariesViewModel", "Error requesting summary", e)
        return when {
            e.message?.contains("429") == true ->
                ru.shrprnbw.ideas.R.string.transcriptions_error_rate_limit
            else -> ru.shrprnbw.ideas.R.string.transcriptions_error_unknown
        }
    }

}

sealed interface SummariesCommand {
    data class ShowAddSummaryDialog(val show: Boolean) : SummariesCommand

    data object AddSummaryTask : SummariesCommand

    data object ResetError : SummariesCommand
}

data class SummariesScreenState(
    val canCreateSummaries: Boolean,
    val showAddSummaryDialog: Boolean = false,
    val error: Int? = null
)
