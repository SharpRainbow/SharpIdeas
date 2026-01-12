package ru.shrprnbw.ideas.presentation.screens.keywords

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
import ru.shrprnbw.ideas.domain.entity.Keyword
import ru.shrprnbw.ideas.domain.usecase.AddNoteTagUseCase
import ru.shrprnbw.ideas.domain.usecase.DeleteKeywordsUseCase
import ru.shrprnbw.ideas.domain.usecase.GetKeywordsUseCase
import ru.shrprnbw.ideas.domain.usecase.RemoveNoteTagUseCase
import ru.shrprnbw.ideas.domain.usecase.RequestNoteKeywordsUseCase

@HiltViewModel(assistedFactory = KeywordsViewModel.Factory::class)
class KeywordsViewModel @AssistedInject constructor(
    @Assisted("noteId") private val noteId: String,
    @Assisted("hasAccess") private val hasAccess: Boolean,
    private val requestNoteKeywordsUseCase: RequestNoteKeywordsUseCase,
    private val addNoteTagUseCase: AddNoteTagUseCase,
    private val removeNoteTagUseCase: RemoveNoteTagUseCase,
    private val deleteKeywordsUseCase: DeleteKeywordsUseCase,
    getKeywordsUseCase: GetKeywordsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(KeywordsScreenState(
        canCreateKeywords = hasAccess
    ))
    val state = _state.asStateFlow()
    val keywords: Flow<PagingData<Keyword>> =
        getKeywordsUseCase(noteId).cachedIn(viewModelScope)

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("noteId") noteId: String,
            @Assisted("hasAccess") hasAccess: Boolean
        ): KeywordsViewModel
    }

    fun processCommand(command: KeywordsCommand) {
        when (command) {
            is KeywordsCommand.ShowAddKeywordsDialog -> {
                if (!hasAccess)
                    return
                _state.update {
                    it.copy(showAddKeywordsDialog = command.show)
                }
            }

            KeywordsCommand.AddKeywordsTask -> {
                if (!hasAccess)
                    return
                viewModelScope.launch {
                    _state.update { previousState ->
                        try {
                            requestNoteKeywordsUseCase(noteId)
                            previousState.copy(showAddKeywordsDialog = false)
                        } catch (e: Exception) {
                            previousState.copy(
                                showAddKeywordsDialog = false,
                                error = parseErrorMessage(e)
                            )
                        }
                    }
                }
            }

            KeywordsCommand.ResetError -> {
                _state.update {
                    it.copy(error = null)
                }
            }

            is KeywordsCommand.ShowKeywordChipsDialog -> {
                if (command.keyword.keyword.isBlank() || !hasAccess)
                    return
                _state.update {
                    it.copy(
                        selectedKeyword = command.keyword,
                        showKeywordChipsDialog = true
                    )
                }
            }

            KeywordsCommand.DismissKeywordChipsDialog -> {
                _state.update {
                    it.copy(
                        selectedKeyword = null,
                        showKeywordChipsDialog = false
                    )
                }
            }

            is KeywordsCommand.AddKeywordAsTag -> {
                viewModelScope.launch {
                    _state.update { previousState ->
                        try {
                            addNoteTagUseCase(noteId, command.keyword)
                            previousState.copy(
                                addedKeywords = previousState.addedKeywords + command.keyword
                            )
                        } catch (e: Exception) {
                            Log.d("KeywordsViewModel", "Error adding keyword as tag", e)
                            previousState.copy(
                                error = when {
                                    e.message?.contains("409") == true ->
                                        ru.shrprnbw.ideas.R.string.note_tags_error_duplicate
                                    else -> ru.shrprnbw.ideas.R.string.note_tags_error_add
                                },
                                showKeywordChipsDialog = false
                            )
                        }
                    }
                }
            }

            is KeywordsCommand.RemoveNoteTag -> {
                viewModelScope.launch {
                    _state.update { previousState ->
                        try {
                            removeNoteTagUseCase(noteId, command.keyword)
                            previousState.copy(
                                addedKeywords = previousState.addedKeywords - command.keyword
                            )
                        } catch (e: Exception) {
                            Log.d("KeywordsViewModel", "Error removing note tag", e)
                            previousState.copy(
                                error = ru.shrprnbw.ideas.R.string.note_tags_error_remove,
                                showKeywordChipsDialog = false
                            )
                        }
                    }
                }
            }

            is KeywordsCommand.DeleteKeywords -> {
                if (!hasAccess)
                    return
                viewModelScope.launch {
                    try {
                        deleteKeywordsUseCase(noteId, command.keywordId)
                    } catch (e: Exception) {
                        Log.d("KeywordsViewModel", "Error deleting keywords", e)
                        _state.update {
                            it.copy(error = ru.shrprnbw.ideas.R.string.keywords_error_delete)
                        }
                    }
                }
            }
        }
    }

    private fun parseErrorMessage(e: Exception): Int {
        Log.d("KeywordsViewModel", "Error requesting keywords", e)
        return when {
            e.message?.contains("400") == true ->
                ru.shrprnbw.ideas.R.string.keywords_error_content_invalid
            e.message?.contains("409") == true ->
                ru.shrprnbw.ideas.R.string.keywords_error_rate_limit
            e.message?.contains("429") == true ->
                ru.shrprnbw.ideas.R.string.keywords_error_limit_reached
            else -> ru.shrprnbw.ideas.R.string.keywords_error_unknown
        }
    }

}

sealed interface KeywordsCommand {
    data class ShowAddKeywordsDialog(val show: Boolean) : KeywordsCommand

    data object AddKeywordsTask : KeywordsCommand

    data object ResetError : KeywordsCommand

    data class ShowKeywordChipsDialog(val keyword: Keyword) : KeywordsCommand

    data object DismissKeywordChipsDialog : KeywordsCommand

    data class AddKeywordAsTag(val keyword: String) : KeywordsCommand

    data class RemoveNoteTag(val keyword: String) : KeywordsCommand

    data class DeleteKeywords(val keywordId: Long) : KeywordsCommand
}

data class KeywordsScreenState(
    val canCreateKeywords: Boolean,
    val showAddKeywordsDialog: Boolean = false,
    val showKeywordChipsDialog: Boolean = false,
    val selectedKeyword: Keyword? = null,
    val addedKeywords: Set<String> = emptySet(),
    val error: Int? = null
)
