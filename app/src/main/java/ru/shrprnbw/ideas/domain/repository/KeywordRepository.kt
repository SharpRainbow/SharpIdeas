package ru.shrprnbw.ideas.domain.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.shrprnbw.ideas.domain.entity.Keyword

interface KeywordRepository {

    fun getKeywords(noteId: String): Flow<PagingData<Keyword>>

    suspend fun requestKeywords(noteId: String)

    suspend fun deleteKeywords(noteId: String, keywordId: Long)
}