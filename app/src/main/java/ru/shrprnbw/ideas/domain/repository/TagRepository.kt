package ru.shrprnbw.ideas.domain.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.shrprnbw.ideas.domain.entity.Tag

interface TagRepository {

    fun getUserTags(): Flow<PagingData<Tag>>

    suspend fun createTag(name: String)

    suspend fun updateTag(tagId: Long, name: String)

    suspend fun deleteTag(tagId: Long)

}
