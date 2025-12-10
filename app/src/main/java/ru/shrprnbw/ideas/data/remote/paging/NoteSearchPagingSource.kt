package ru.shrprnbw.ideas.data.remote.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import ru.shrprnbw.ideas.data.mapper.toEntity
import ru.shrprnbw.ideas.data.remote.IdeasApiService
import ru.shrprnbw.ideas.data.remote.dto.response.NoteDto
import ru.shrprnbw.ideas.domain.entity.Note
import ru.shrprnbw.ideas.domain.repository.AuthRepository

class NoteSearchPagingSource(
    private val apiService: IdeasApiService,
    private val authRepository: AuthRepository,
    private val globalSearch: Boolean,
    private val title: String,
    private val content: String,
    private val tagIds: List<Long>
): PagingSource<Int, Note>() {

    override fun getRefreshKey(state: PagingState<Int, Note>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Note> {
        val position = params.key ?: FIRST_PAGE_INDEX
        return try {
            val token = authRepository.getValidToken()
            val contentList = apiService.searchNotes(
                token = token,
                globalSearch = globalSearch,
                title = title.ifBlank { null },
                content = content.ifBlank { null },
                tagIds = tagIds.ifEmpty { null },
                page = position,
                limit = params.loadSize
            )
            val nextKey =
                if (contentList.isEmpty()) {
                    null
                } else {
                    position + (params.loadSize / NETWORK_PAGE_SIZE)
                }
            LoadResult.Page(
                data = contentList.map(NoteDto::toEntity),
                prevKey = if (position == FIRST_PAGE_INDEX) null else position - 1,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    companion object {
        const val NETWORK_PAGE_SIZE = 10
        const val FIRST_PAGE_INDEX = 0
    }

}