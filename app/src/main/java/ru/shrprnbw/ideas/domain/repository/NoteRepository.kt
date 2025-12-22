package ru.shrprnbw.ideas.domain.repository

import android.net.Uri
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.shrprnbw.ideas.domain.entity.ContentItem
import ru.shrprnbw.ideas.domain.entity.Note
import ru.shrprnbw.ideas.domain.entity.NotePreview
import ru.shrprnbw.ideas.domain.entity.Transcription

interface NoteRepository {

    fun getUserNotes(): Flow<PagingData<NotePreview>>

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

    suspend fun deleteNoteContent(noteId: String, contentId: Long)

    suspend fun createTextNote(title: String)

    suspend fun createAudioNote(title: String)

    suspend fun deleteNote(noteId: String)

    fun getTranscriptions(noteId: String): Flow<PagingData<Transcription>>

    suspend fun getTranscription(noteId: String, transcriptionId: String): Transcription

}