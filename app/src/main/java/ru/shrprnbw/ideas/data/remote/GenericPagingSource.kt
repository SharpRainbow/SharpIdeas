package ru.shrprnbw.ideas.data.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import ru.shrprnbw.ideas.data.remote.dto.response.PagedResponse
import ru.shrprnbw.ideas.di.ApiModule

class GenericPagingSource<DTO, E : Any>(
    private val fetch: suspend (page: Int, size: Int) -> PagedResponse<DTO>,
    private val mapper: (DTO) -> E
): PagingSource<Int, E>() {

    override fun getRefreshKey(state: PagingState<Int, E>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, E> {
        val position = params.key ?: ApiModule.FIRST_PAGE_INDEX
        return try {
            val response = fetch(position, params.loadSize)
            val nextKey =
                if (response.page >= response.totalPages - 1) {
                    null
                } else {
                    position + 1
                }
            LoadResult.Page(
                data = response.content.map(mapper),
                prevKey = if (position == ApiModule.FIRST_PAGE_INDEX) null else position - 1,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

}