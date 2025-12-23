@file:OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)

package ru.shrprnbw.ideas.presentation.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
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

    val tagsFlow: Flow<PagingData<Tag>> = getUserTagsUseCase().cachedIn(viewModelScope)

    val pagedNoteList = _state
        .debounce(500)
        .flatMapLatest { screenState ->
            val query = screenState.query
            val selectedTagIds = screenState.selectedTags
            if (query.isBlank() && selectedTagIds.isEmpty()) {
                flowOf(PagingData.empty())
            } else {
                searchNotesUseCase(
                    globalSearch = false,
                    query = query,
                    tagIds = selectedTagIds.toList(),
                    audioNote = screenState.noteTypeFilter.toApiParameter()
                )
            }
        }.cachedIn(viewModelScope)

//    init {
//        getUserTags()
//    }

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
                    val currentTags = previousState.selectedTags.toMutableSet()
                    if (currentTags.contains(command.tag.id)) {
                        currentTags.remove(command.tag.id)
                    } else {
                        currentTags.add(command.tag.id)
                    }
                    previousState.copy(
                        selectedTags = currentTags
                    )
                }
            }
            is SearchCommand.SetNoteTypeFilter -> {
                _state.update { previousState ->
                    previousState.copy(
                        noteTypeFilter = command.filter
                    )
                }
            }
        }
    }

//    private fun getUserTags() {
//        viewModelScope.launch {
//            try {
//                val tags = getUserTagsUseCase()
//                val tagMap = tags.associateWith { false }
//                _state.update { previousState ->
//                    previousState.copy(
//                        tags = tagMap,
//                        error = null
//                    )
//                }
//            }
//            catch (e: Exception) {
//                _state.update { previousState ->
//                    previousState.copy(
//                        error = R.string.error_network
//                    )
//                }
//            }
//        }
//    }

}

sealed interface SearchCommand {

    data class InputQuery(val query: String) : SearchCommand

    data class ToggleTag(val tag: Tag) : SearchCommand

    data class SetNoteTypeFilter(val filter: NoteTypeFilter) : SearchCommand

}

data class SearchScreenState(
    val query: String = "",
    val selectedTags: Set<Long> = emptySet(),
    val noteTypeFilter: NoteTypeFilter = NoteTypeFilter.ALL,
    val error: Int? = null
)

enum class NoteTypeFilter {
    ALL,
    AUDIO,
    TEXT;

    fun toApiParameter(): Boolean? = when (this) {
        ALL -> null
        AUDIO -> true
        TEXT -> false
    }
}