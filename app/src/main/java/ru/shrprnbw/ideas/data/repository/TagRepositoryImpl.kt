package ru.shrprnbw.ideas.data.repository

import ru.shrprnbw.ideas.data.mapper.toEntity
import ru.shrprnbw.ideas.data.remote.IdeasApiService
import ru.shrprnbw.ideas.data.remote.dto.request.CreateTagRequest
import ru.shrprnbw.ideas.domain.entity.Tag
import ru.shrprnbw.ideas.domain.repository.AuthRepository
import ru.shrprnbw.ideas.domain.repository.TagRepository
import javax.inject.Inject

class TagRepositoryImpl @Inject constructor(
    private val apiService: IdeasApiService,
    private val authRepository: AuthRepository
) : TagRepository {

    override suspend fun getUserTags(): List<Tag> {
        val token = authRepository.getValidToken()
        return apiService.getUserCreatedTags(token).map { it.toEntity() }
    }

    override suspend fun createTag(name: String) {
        val token = authRepository.getValidToken()
        apiService.createTag(token, CreateTagRequest(name))
    }

    override suspend fun updateTag(tagId: Long, name: String) {
        val token = authRepository.getValidToken()
        apiService.updateTag(token, tagId, CreateTagRequest(name))
    }

    override suspend fun deleteTag(tagId: Long) {
        val token = authRepository.getValidToken()
        apiService.deleteTag(token, tagId)
    }
}
