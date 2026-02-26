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
import ru.shrprnbw.ideas.data.remote.dto.response.TranscriptionDto
import ru.shrprnbw.ideas.domain.entity.Transcription
import ru.shrprnbw.ideas.domain.repository.AuthRepository
import ru.shrprnbw.ideas.domain.repository.TranscriptionsRepository

class TranscriptionsRepositoryImpl @Inject constructor(
    private val apiService: IdeasApiService,
    private val authRepository: AuthRepository
) : TranscriptionsRepository {

    private var currentTranscriptionSource: PagingSource<Int, Transcription>? = null

    override fun getTranscriptions(noteId: String): Flow<PagingData<Transcription>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                GenericPagingSource(
                    fetch = { page, size ->
                        val token = authRepository.getValidToken()
                        apiService.getTranscriptions(
                            token = token,
                            noteId = noteId,
                            page = page,
                            limit = size
                        )
                    },
                    mapper = TranscriptionDto::toEntity
                ).also { currentTranscriptionSource = it }
            }
        ).flow
    }

    override suspend fun getTranscription(noteId: String, transcriptionId: String): Transcription {
        val token = authRepository.getValidToken()
        return apiService.getTranscription(token, noteId, transcriptionId).toEntity()
    }

    override suspend fun requestTranscription(noteId: String) {
        val token = authRepository.getValidToken()
        apiService.generateNoteTranscription(token, noteId)
        currentTranscriptionSource?.invalidate()
    }

    override suspend fun deleteTranscription(noteId: String, transcriptionId: Long) {
        val token = authRepository.getValidToken()
        apiService.deleteTranscription(token, noteId, transcriptionId)
        currentTranscriptionSource?.invalidate()
    }

}