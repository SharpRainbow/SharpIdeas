@file:OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)

package ru.shrprnbw.ideas.presentation.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.shrprnbw.ideas.R
import ru.shrprnbw.ideas.domain.entity.Tag
import ru.shrprnbw.ideas.domain.usecase.GetUserTagsUseCase
import ru.shrprnbw.ideas.domain.usecase.SearchNotesUseCase
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getUserTagsUseCase: GetUserTagsUseCase,
    private val searchNotesUseCase: SearchNotesUseCase
): ViewModel() {

    private val _state = MutableStateFlow(SearchScreenState())
    val state = _state.asStateFlow()

    val pagedNoteList = _state
        .debounce(500)
        .flatMapLatest { screenState ->
            val query = screenState.query
            val selectedTagIds = screenState.selectedTags.map { it.id }
            if (query.isBlank() && selectedTagIds.isEmpty()) {
                flowOf(PagingData.empty())
            } else {
                searchNotesUseCase(
                    globalSearch = false,
                    query = query,
                    tagIds = selectedTagIds
                )
            }
        }.cachedIn(viewModelScope)

    init {
        getUserTags()
    }

    fun processCommand(command: SearchCommand) {
        when(command) {
            is SearchCommand.InputQuery -> {
                _state.update { previousState ->
                    previousState.copy(
                        query = command.query,
                        error = null
                    )
                }
            }
            is SearchCommand.ToggleTag -> {
                _state.update { previousState ->
                    val currentTags = previousState.tags.toMutableMap()
                    val currentValue = currentTags[command.tag] ?: false
                    currentTags[command.tag] = !currentValue
                    previousState.copy(
                        tags = currentTags
                    )
                }
            }
        }
    }

    private fun getUserTags() {
        viewModelScope.launch {
            try {
                val tags = getUserTagsUseCase()
                val tagMap = tags.associateWith { false }
                _state.update { previousState ->
                    previousState.copy(
                        tags = tagMap,
                        error = null
                    )
                }
            }
            catch (e: Exception) {
                _state.update { previousState ->
                    previousState.copy(
                        error = R.string.error_network
                    )
                }
            }
        }
    }

}

sealed interface SearchCommand {

    data class InputQuery(val query: String) : SearchCommand

    data class ToggleTag(val tag: Tag) : SearchCommand

}

data class SearchScreenState(
    val query: String = "",
    val tags: Map<Tag, Boolean> = mapOf(),
    val error: Int? = null
) {

    val selectedTags: List<Tag>
        get() = tags.filter { it.value }.keys.toList()

}