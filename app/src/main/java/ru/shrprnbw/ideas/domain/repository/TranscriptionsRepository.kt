package ru.shrprnbw.ideas.domain.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.shrprnbw.ideas.domain.entity.Transcription

interface TranscriptionsRepository {

    fun getTranscriptions(noteId: String): Flow<PagingData<Transcription>>

    suspend fun getTranscription(noteId: String, transcriptionId: String): Transcription

    suspend fun requestTranscription(noteId: String)

    suspend fun deleteTranscription(noteId: String, transcriptionId: Long)

}