package ru.shrprnbw.ideas.domain.repository

import android.net.Uri
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.shrprnbw.ideas.domain.entity.ContentItem
import ru.shrprnbw.ideas.domain.entity.Keyword
import ru.shrprnbw.ideas.domain.entity.Note
import ru.shrprnbw.ideas.domain.entity.NotePreview
import ru.shrprnbw.ideas.domain.entity.Summary
import ru.shrprnbw.ideas.domain.entity.Transcription

interface NoteRepository {

    fun getUserNotes(): Flow<PagingData<NotePreview>>

    fun getSharedNotes(): Flow<PagingData<NotePreview>>

    fun getGroupNotes(groupId: Long): Flow<PagingData<NotePreview>>

    fun searchNotes(
        globalSearch: Boolean,
        title: String,
        content: String,
        tagIds: List<Long>,
        audioNote: Boolean?
    ): Flow<PagingData<NotePreview>>

    suspend fun getNoteInfo(noteId: String): Note

    suspend fun updateNote(noteId: String, title: String)

    suspend fun updateNoteContent(noteId: String, noteContent: List<ContentItem>)

    suspend fun addNoteText(noteId: String, text: String)

    suspend fun addNoteImage(noteId: String, imageUri: Uri)

    fun addNoteAudio(noteId: String, audioUri: Uri): Flow<Float>

    suspend fun deleteNoteContent(noteId: String, contentId: Long)

    suspend fun createTextNote(title: String)

    suspend fun createAudioNote(title: String)

    suspend fun deleteNote(noteId: String)

    fun getTranscriptions(noteId: String): Flow<PagingData<Transcription>>

    suspend fun getTranscription(noteId: String, transcriptionId: String): Transcription

    suspend fun requestTranscription(noteId: String)

    suspend fun deleteTranscription(noteId: String, transcriptionId: Long)

    fun getKeywords(noteId: String): Flow<PagingData<Keyword>>

    suspend fun requestKeywords(noteId: String)

    suspend fun deleteKeywords(noteId: String, keywordId: Long)

    fun getSummaries(noteId: String): Flow<PagingData<Summary>>

    suspend fun getSummary(noteId: String, summaryId: String): Summary

    suspend fun requestSummary(noteId: String)

    suspend fun deleteSummary(noteId: String, summaryId: Long)

    suspend fun addNoteTag(noteId: String, tag: String)

    suspend fun removeNoteTag(noteId: String, tag: String)

    suspend fun addCollaborator(noteId: String, email: String?, userId: String?)

    suspend fun removeCollaborator(noteId: String, collaboratorId: String)

    suspend fun addNoteToGroup(noteId: String, groupId: Long, accessType: String)

    suspend fun removeNoteFromGroup(noteId: String, groupId: Long)

}