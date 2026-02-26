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
import ru.shrprnbw.ideas.data.remote.dto.response.NoteSummaryDto
import ru.shrprnbw.ideas.domain.entity.Summary
import ru.shrprnbw.ideas.domain.repository.AuthRepository
import ru.shrprnbw.ideas.domain.repository.SummaryRepository

class SummaryRepositoryImpl @Inject constructor(
    private val apiService: IdeasApiService,
    private val authRepository: AuthRepository
) : SummaryRepository {

    private var currentSummarySource: PagingSource<Int, Summary>? = null

    override fun getSummaries(noteId: String): Flow<PagingData<Summary>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                GenericPagingSource(
                    fetch = { page, size ->
                        val token = authRepository.getValidToken()
                        apiService.getSummaries(
                            token = token,
                            noteId = noteId,
                            page = page,
                            limit = size
                        )
                    },
                    mapper = NoteSummaryDto::toEntity
                ).also { currentSummarySource = it }
            }
        ).flow
    }

    override suspend fun getSummary(noteId: String, summaryId: String): Summary {
        val token = authRepository.getValidToken()
        return apiService.getSummary(token, noteId, summaryId).toEntity()
    }

    override suspend fun requestSummary(noteId: String) {
        val token = authRepository.getValidToken()
        apiService.generateNoteSummary(token, noteId)
        currentSummarySource?.invalidate()
    }

    override suspend fun deleteSummary(noteId: String, summaryId: Long) {
        val token = authRepository.getValidToken()
        apiService.deleteSummary(token, noteId, summaryId)
        currentSummarySource?.invalidate()
    }

}