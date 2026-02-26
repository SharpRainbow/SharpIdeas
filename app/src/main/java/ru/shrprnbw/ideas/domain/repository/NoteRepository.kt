package ru.shrprnbw.ideas.domain.repository

import android.net.Uri
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.shrprnbw.ideas.domain.entity.ContentItem
import ru.shrprnbw.ideas.domain.entity.Note
import ru.shrprnbw.ideas.domain.entity.NotePreview
import ru.shrprnbw.ideas.domain.entity.NoteType

interface NoteRepository {

    fun getUserNotes(): Flow<PagingData<NotePreview>>

    fun getSharedNotes(): Flow<PagingData<NotePreview>>

    fun getGroupNotes(groupId: Long): Flow<PagingData<NotePreview>>

    fun searchNotes(
        globalSearch: Boolean,
        title: String,
        content: String,
        tagIds: List<Long>,
        noteType: NoteType?
    ): Flow<PagingData<NotePreview>>

    suspend fun getNoteInfo(noteId: String): Note

    suspend fun updateNote(noteId: String, title: String)

    suspend fun updateNoteContent(noteId: String, noteContent: List<ContentItem>)

    suspend fun addNoteText(noteId: String, text: String)

    suspend fun addNoteImage(noteId: String, imageUri: Uri)

    fun addNoteAudio(noteId: String, audioUri: Uri): Flow<Float>

    suspend fun deleteNoteContent(noteId: String, contentId: Long)

    suspend fun createNote(title: String, noteType: NoteType)

    suspend fun deleteNote(noteId: String)

    suspend fun addNoteTag(noteId: String, tag: String)

    suspend fun removeNoteTag(noteId: String, tag: String)

    suspend fun addCollaborator(noteId: String, email: String?, userId: String?)

    suspend fun removeCollaborator(noteId: String, collaboratorId: String)

    suspend fun addNoteToGroup(noteId: String, groupId: Long, accessType: String)

    suspend fun removeNoteFromGroup(noteId: String, groupId: Long)

    suspend fun getGroupNotesSimple(groupId: Long): List<NotePreview>

}