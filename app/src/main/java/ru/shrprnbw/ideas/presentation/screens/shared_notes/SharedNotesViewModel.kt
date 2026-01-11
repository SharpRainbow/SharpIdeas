package ru.shrprnbw.ideas.presentation.screens.shared_notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import ru.shrprnbw.ideas.domain.entity.NotePreview
import ru.shrprnbw.ideas.domain.usecase.LoadSharedNotesUseCase
import javax.inject.Inject

@HiltViewModel
class SharedNotesViewModel @Inject constructor(
    private val getSharedNotesUseCase: LoadSharedNotesUseCase
): ViewModel() {

    val notesFlow: Flow<PagingData<NotePreview>> = getSharedNotesUseCase().cachedIn(viewModelScope)

}