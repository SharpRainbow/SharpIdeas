package ru.shrprnbw.ideas.data.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import ru.shrprnbw.ideas.data.mapper.toEntity
import ru.shrprnbw.ideas.data.remote.IdeasApiService
import ru.shrprnbw.ideas.data.remote.dto.response.TranscriptionDto
import ru.shrprnbw.ideas.di.ApiModule
import ru.shrprnbw.ideas.domain.entity.Transcription
import ru.shrprnbw.ideas.domain.repository.AuthRepository

class TranscriptionPagingSource(
    private val apiService: IdeasApiService,
    private val authRepository: AuthRepository,
    private val noteId: String
): PagingSource<Int, Transcription>() {

    override fun getRefreshKey(state: PagingState<Int, Transcription>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Transcription> {
        val position = params.key ?: ApiModule.FIRST_PAGE_INDEX
        return try {
            val token = authRepository.getValidToken()
            val response = apiService.getTranscriptions(
                token = token,
                noteId = noteId,
                page = position,
                limit = params.loadSize
            )
            val nextKey =
                if (response.page >= response.totalPages - 1) {
                    null
                } else {
                    position + 1
                }
            LoadResult.Page(
                data = response.content.map(TranscriptionDto::toEntity),
                prevKey = if (position == ApiModule.FIRST_PAGE_INDEX) null else position - 1,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

}
