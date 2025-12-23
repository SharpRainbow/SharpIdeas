package ru.shrprnbw.ideas.data.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import ru.shrprnbw.ideas.data.mapper.toEntity
import ru.shrprnbw.ideas.data.remote.IdeasApiService
import ru.shrprnbw.ideas.data.remote.dto.response.TagDto
import ru.shrprnbw.ideas.di.ApiModule
import ru.shrprnbw.ideas.domain.entity.Tag
import ru.shrprnbw.ideas.domain.repository.AuthRepository

class TagPagingSource(
    private val apiService: IdeasApiService,
    private val authRepository: AuthRepository
): PagingSource<Int, Tag>() {

    override fun getRefreshKey(state: PagingState<Int, Tag>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Tag> {
        val position = params.key ?: ApiModule.FIRST_PAGE_INDEX
        return try {
            val token = authRepository.getValidToken()
            val response = apiService.getUserCreatedTags(
                token = token,
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
                data = response.content.map(TagDto::toEntity),
                prevKey = if (position == ApiModule.FIRST_PAGE_INDEX) null else position - 1,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

}