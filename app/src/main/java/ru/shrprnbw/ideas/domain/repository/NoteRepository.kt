package ru.shrprnbw.ideas.domain.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.shrprnbw.ideas.domain.entity.NotePreview

interface NoteRepository {

    fun getUserNotes(): Flow<PagingData<NotePreview>>

    fun searchNotes(
        globalSearch: Boolean,
        title: String,
        content: String,
        tagIds: List<Long>,
    ): Flow<PagingData<NotePreview>>

}