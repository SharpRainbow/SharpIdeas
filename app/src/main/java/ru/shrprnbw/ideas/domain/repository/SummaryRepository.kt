package ru.shrprnbw.ideas.domain.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.shrprnbw.ideas.domain.entity.Summary

interface SummaryRepository {

    fun getSummaries(noteId: String): Flow<PagingData<Summary>>

    suspend fun getSummary(noteId: String, summaryId: String): Summary

    suspend fun requestSummary(noteId: String)

    suspend fun deleteSummary(noteId: String, summaryId: Long)

}