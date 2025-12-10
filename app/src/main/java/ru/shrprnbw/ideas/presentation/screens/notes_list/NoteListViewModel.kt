package ru.shrprnbw.ideas.presentation.screens.notes_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.shrprnbw.ideas.domain.entity.Note
import ru.shrprnbw.ideas.domain.entity.Tag
import ru.shrprnbw.ideas.domain.usecase.LoadPersonalNotesUseCase
import javax.inject.Inject

@HiltViewModel
class NoteListViewModel @Inject constructor(
    private val getPersonalNotesUseCase: LoadPersonalNotesUseCase
): ViewModel() {
//    private val _state = MutableStateFlow(NoteListState())
//    val state = _state.asStateFlow()

    val notesFlow: Flow<PagingData<Note>> = getPersonalNotesUseCase().cachedIn(viewModelScope)

}

//data class NoteListState(
//    val query: String = "",
//    val tags: Map<Tag, Boolean> = mapOf(),
//    val notes: List<Note> = listOf()
//)