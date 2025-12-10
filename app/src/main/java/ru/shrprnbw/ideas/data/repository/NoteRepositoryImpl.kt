package ru.shrprnbw.ideas.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import ru.shrprnbw.ideas.data.remote.IdeasApiService
import ru.shrprnbw.ideas.data.remote.paging.NotePagingSource
import ru.shrprnbw.ideas.data.remote.paging.NoteSearchPagingSource
import ru.shrprnbw.ideas.domain.entity.Note
import ru.shrprnbw.ideas.domain.repository.AuthRepository
import ru.shrprnbw.ideas.domain.repository.NoteRepository

class NoteRepositoryImpl @Inject constructor(
    private val apiService: IdeasApiService,
    private val authRepository: AuthRepository
) : NoteRepository {

    override fun getUserNotes(): Flow<PagingData<Note>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                NotePagingSource(
                    apiService,
                    authRepository
                )
            }
        ).flow
    }

    override fun searchNotes(
        globalSearch: Boolean,
        title: String,
        content: String,
        tagIds: List<Long>
    ): Flow<PagingData<Note>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                NoteSearchPagingSource(
                    apiService,
                    authRepository,
                    globalSearch,
                    title,
                    content,
                    tagIds
                )
            }
        ).flow
    }
}