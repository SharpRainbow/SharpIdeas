package ru.shrprnbw.ideas.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import ru.shrprnbw.ideas.data.mapper.toEntity
import ru.shrprnbw.ideas.data.remote.GenericPagingSource
import ru.shrprnbw.ideas.data.remote.IdeasApiService
import ru.shrprnbw.ideas.data.remote.dto.response.NoteKeywordDto
import ru.shrprnbw.ideas.domain.entity.Keyword
import ru.shrprnbw.ideas.domain.repository.AuthRepository
import ru.shrprnbw.ideas.domain.repository.KeywordRepository

class KeywordRepositoryImpl @Inject constructor(
    private val apiService: IdeasApiService,
    private val authRepository: AuthRepository
) : KeywordRepository {

    private var currentKeywordSource: PagingSource<Int, Keyword>? = null

    override fun getKeywords(noteId: String): Flow<PagingData<Keyword>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                GenericPagingSource(
                    fetch = { page, size ->
                        val token = authRepository.getValidToken()
                        apiService.getKeywords(
                            token = token,
                            noteId = noteId,
                            page = page,
                            limit = size
                        )
                    },
                    mapper = NoteKeywordDto::toEntity
                ).also { currentKeywordSource = it }
            }
        ).flow
    }

    override suspend fun requestKeywords(noteId: String) {
        val token = authRepository.getValidToken()
        apiService.generateNoteKeywords(token, noteId)
        currentKeywordSource?.invalidate()
    }

    override suspend fun deleteKeywords(noteId: String, keywordId: Long) {
        val token = authRepository.getValidToken()
        apiService.deleteKeywords(token, noteId, keywordId)
        currentKeywordSource?.invalidate()
    }

}