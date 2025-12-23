package ru.shrprnbw.ideas.domain.repository

import ru.shrprnbw.ideas.domain.entity.Tag

interface TagRepository {

    suspend fun getUserTags(): List<Tag>

    suspend fun createTag(name: String)

    suspend fun updateTag(tagId: Long, name: String)

    suspend fun deleteTag(tagId: Long)

}
