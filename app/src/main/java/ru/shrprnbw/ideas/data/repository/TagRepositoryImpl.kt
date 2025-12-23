@file:OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)

package ru.shrprnbw.ideas.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import ru.shrprnbw.ideas.data.remote.IdeasApiService
import ru.shrprnbw.ideas.data.remote.dto.request.CreateTagRequest
import ru.shrprnbw.ideas.data.remote.paging.TagPagingSource
import ru.shrprnbw.ideas.domain.entity.Tag
import ru.shrprnbw.ideas.domain.repository.AuthRepository
import ru.shrprnbw.ideas.domain.repository.TagRepository
import javax.inject.Inject

class TagRepositoryImpl @Inject constructor(
    private val apiService: IdeasApiService,
    private val authRepository: AuthRepository
) : TagRepository {

    private val tagsRefreshTrigger = MutableSharedFlow<Unit>()

    override fun getUserTags(): Flow<PagingData<Tag>> {
        return tagsRefreshTrigger.onStart {
            emit(Unit)
        }.debounce(
            timeoutMillis = 1000
        ).flatMapLatest {
            Pager(
                config = PagingConfig(
                    pageSize = 10,
                    enablePlaceholders = false
                ),
                pagingSourceFactory = {
                    TagPagingSource(
                        apiService,
                        authRepository
                    )
                }
            ).flow
        }
    }

    override suspend fun createTag(name: String) {
        val token = authRepository.getValidToken()
        apiService.createTag(token, CreateTagRequest(name))
        tagsRefreshTrigger.emit(Unit)
    }

    override suspend fun updateTag(tagId: Long, name: String) {
        val token = authRepository.getValidToken()
        apiService.updateTag(token, tagId, CreateTagRequest(name))
        tagsRefreshTrigger.emit(Unit)
    }

    override suspend fun deleteTag(tagId: Long) {
        val token = authRepository.getValidToken()
        apiService.deleteTag(token, tagId)
        tagsRefreshTrigger.emit(Unit)
    }
}
