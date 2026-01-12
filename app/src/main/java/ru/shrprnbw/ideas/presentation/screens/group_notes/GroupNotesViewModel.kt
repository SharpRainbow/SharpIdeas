package ru.shrprnbw.ideas.presentation.screens.group_notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import ru.shrprnbw.ideas.domain.entity.NotePreview
import ru.shrprnbw.ideas.domain.usecase.LoadGroupNotesUseCase

@HiltViewModel(assistedFactory = GroupNotesViewModel.Factory::class)
class GroupNotesViewModel @AssistedInject constructor(
    @Assisted("groupId") private val groupId: Long,
    private val getGroupNotesUseCase: LoadGroupNotesUseCase
): ViewModel() {

    val notesFlow: Flow<PagingData<NotePreview>> = getGroupNotesUseCase(groupId).cachedIn(viewModelScope)

    @AssistedFactory
    interface Factory {
        fun create(@Assisted("groupId") groupId: Long): GroupNotesViewModel
    }

}