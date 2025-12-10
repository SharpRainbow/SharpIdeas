package ru.shrprnbw.ideas.data.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import ru.shrprnbw.ideas.data.remote.IdeasApiService
import ru.shrprnbw.ideas.data.remote.paging.NotePagingSource
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
}