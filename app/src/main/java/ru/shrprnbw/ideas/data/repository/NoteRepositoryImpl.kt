@file:OptIn(ExperimentalCoroutinesApi::class)

package ru.shrprnbw.ideas.data.repository

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import ru.shrprnbw.ideas.data.mapper.toEntity
import ru.shrprnbw.ideas.data.mapper.toUpdateRequest
import ru.shrprnbw.ideas.data.remote.IdeasApiService
import ru.shrprnbw.ideas.data.remote.dto.request.AddNoteTextRequest
import ru.shrprnbw.ideas.data.remote.dto.request.UpdateNoteRequest
import ru.shrprnbw.ideas.data.remote.paging.NotePagingSource
import ru.shrprnbw.ideas.data.remote.paging.NoteSearchPagingSource
import ru.shrprnbw.ideas.domain.entity.ContentItem
import ru.shrprnbw.ideas.domain.entity.Note
import ru.shrprnbw.ideas.domain.entity.NotePreview
import ru.shrprnbw.ideas.domain.repository.AuthRepository
import ru.shrprnbw.ideas.domain.repository.NoteRepository
import java.io.IOException

class NoteRepositoryImpl @Inject constructor(
    private val apiService: IdeasApiService,
    private val authRepository: AuthRepository,
    @param:ApplicationContext private val context: Context
) : NoteRepository {

    private val notesRefreshTrigger = MutableSharedFlow<Unit>()

    override fun getUserNotes(): Flow<PagingData<NotePreview>> {
        return notesRefreshTrigger.onStart {
            emit(Unit)
        }.flatMapLatest {
            Pager(
                config = PagingConfig(
                    pageSize = 10,
                    enablePlaceholders = false
                ),
                pagingSourceFactory = {
                    NotePagingSource(
                        apiService,
                        authRepository
                    )
                }
            ).flow
        }
    }

    override fun searchNotes(
        globalSearch: Boolean,
        title: String,
        content: String,
        tagIds: List<Long>
    ): Flow<PagingData<NotePreview>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                NoteSearchPagingSource(
                    apiService,
                    authRepository,
                    globalSearch,
                    title,
                    content,
                    tagIds
                )
            }
        ).flow
    }

    override suspend fun getNoteInfo(noteId: String): Note {
        val token = authRepository.getValidToken()
        return apiService.getNoteDetail(token, noteId).toEntity()
    }

    override suspend fun updateNote(noteId: String, title: String) {
        val token = authRepository.getValidToken()
        apiService.updateNote(token, noteId, UpdateNoteRequest(title))
        notesRefreshTrigger.emit(Unit)
    }

    override suspend fun updateNoteContent(noteId: String, noteContent: List<ContentItem>) {
        val token = authRepository.getValidToken()
        for (contentItem in noteContent) {
            apiService.updateNoteText(
                token,
                noteId,
                contentItem.toUpdateRequest()
            )
        }
        notesRefreshTrigger.emit(Unit)
    }

    override suspend fun addNoteText(noteId: String, text: String) {
        val token = authRepository.getValidToken()
        apiService.addNoteText(
            token,
            noteId,
            AddNoteTextRequest(
                content = text
            )
        )
        notesRefreshTrigger.emit(Unit)
    }

    override suspend fun addNoteImage(noteId: String, imageUri: Uri) {
        val imageName = getFileNameFromUri(imageUri)
        val imageData = getFileDataFromUri(imageUri)
        val mimeType = getMimeTypeFromUri(imageUri)
        if (imageName == null || imageData == null) {
            throw IllegalArgumentException("Failed to retrieve image data from URI")
        }
        val imagePart = MultipartBody.Part.createFormData(
            "file",
            imageName,
            imageData.toRequestBody(
                (mimeType ?: "image/*").toMediaTypeOrNull()
            )
        )
        val token = authRepository.getValidToken()
        apiService.uploadNoteImage(
            token,
            noteId,
            imagePart
        )
    }

    override suspend fun deleteNoteContent(noteId: String, contentId: Long) {
        val token = authRepository.getValidToken()
        apiService.deleteNoteContent(
            token,
            noteId,
            contentId
        )
        notesRefreshTrigger.emit(Unit)
    }

    private fun getMimeTypeFromUri(uri: Uri): String? {
        return try {
            context.contentResolver.query(
                uri,
                arrayOf(MediaStore.Images.ImageColumns.MIME_TYPE),
                null,
                null,
                null
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    cursor.getString(cursor.getColumnIndexOrThrow(
                        MediaStore.Images.ImageColumns.MIME_TYPE
                    ))
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun getFileNameFromUri(uri: Uri): String? {
        return try {
            context.contentResolver.query(
                uri,
                arrayOf(MediaStore.Images.ImageColumns.DISPLAY_NAME),
                null,
                null,
                null
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    cursor.getString(cursor.getColumnIndexOrThrow(
                        MediaStore.Images.ImageColumns.DISPLAY_NAME
                    ))
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun getFileDataFromUri(uri: Uri): ByteArray? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                inputStream.readBytes()
            }
        } catch (e: IOException) {
            null
        }
    }
}