package ru.shrprnbw.ideas.data.remote.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import ru.shrprnbw.ideas.data.mapper.toEntity
import ru.shrprnbw.ideas.data.remote.IdeasApiService
import ru.shrprnbw.ideas.data.remote.dto.response.NotePreviewDto
import ru.shrprnbw.ideas.di.ApiModule
import ru.shrprnbw.ideas.domain.entity.NotePreview
import ru.shrprnbw.ideas.domain.repository.AuthRepository

class NoteSearchPagingSource(
    private val apiService: IdeasApiService,
    private val authRepository: AuthRepository,
    private val globalSearch: Boolean,
    private val title: String,
    private val content: String,
    private val tagIds: List<Long>,
    private val audioNote: Boolean?
): PagingSource<Int, NotePreview>() {

    override fun getRefreshKey(state: PagingState<Int, NotePreview>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NotePreview> {
        val position = params.key ?: ApiModule.FIRST_PAGE_INDEX
        return try {
            val token = authRepository.getValidToken()
            val response = apiService.searchNotes(
                token = token,
                globalSearch = globalSearch,
                title = title.ifBlank { null },
                content = content.ifBlank { null },
                tagIds = tagIds.ifEmpty { null },
                audioNote = audioNote,
                page = position,
                limit = params.loadSize
            )
            Log.d("NoteSearchPagingSource", "Loaded page $position with ${response.content.size} items")
            val nextKey =
                if (response.page >= response.totalPages - 1) {
                    null
                } else {
                    position + 1
                }
            LoadResult.Page(
                data = response.content.map(NotePreviewDto::toEntity),
                prevKey = if (position == ApiModule.FIRST_PAGE_INDEX) null else position - 1,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }



}