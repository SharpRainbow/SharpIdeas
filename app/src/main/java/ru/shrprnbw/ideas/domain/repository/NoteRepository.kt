package ru.shrprnbw.ideas.domain.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.shrprnbw.ideas.domain.entity.Note

interface NoteRepository {

    fun getUserNotes(): Flow<PagingData<Note>>

}