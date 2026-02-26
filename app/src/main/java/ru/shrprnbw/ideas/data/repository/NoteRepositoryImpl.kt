@file:OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)

package ru.shrprnbw.ideas.data.repository

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import ru.shrprnbw.ideas.data.ProgressEmittingRequestBody
import ru.shrprnbw.ideas.data.mapper.toEntity
import ru.shrprnbw.ideas.data.mapper.toUpdateRequest
import ru.shrprnbw.ideas.data.remote.GenericPagingSource
import ru.shrprnbw.ideas.data.remote.IdeasApiService
import ru.shrprnbw.ideas.data.remote.dto.request.AddCollaboratorRequest
import ru.shrprnbw.ideas.data.remote.dto.request.AddNoteTextRequest
import ru.shrprnbw.ideas.data.remote.dto.request.AddNoteToGroupRequest
import ru.shrprnbw.ideas.data.remote.dto.request.CreateNoteRequest
import ru.shrprnbw.ideas.data.remote.dto.request.RemoveNoteFromGroupRequest
import ru.shrprnbw.ideas.data.remote.dto.request.UpdateNoteRequest
import ru.shrprnbw.ideas.data.remote.dto.request.UpdateNoteTextBatchRequest
import ru.shrprnbw.ideas.data.remote.dto.response.NotePreviewDto
import ru.shrprnbw.ideas.data.remote.dto.response.PagedResponse
import ru.shrprnbw.ideas.domain.entity.AccessType
import ru.shrprnbw.ideas.domain.entity.ContentItem
import ru.shrprnbw.ideas.domain.entity.Note
import ru.shrprnbw.ideas.domain.entity.NotePreview
import ru.shrprnbw.ideas.domain.entity.NoteType
import ru.shrprnbw.ideas.domain.repository.AuthRepository
import ru.shrprnbw.ideas.domain.repository.CredentialsRepository
import ru.shrprnbw.ideas.domain.repository.NoteRepository
import java.io.IOException

class NoteRepositoryImpl @Inject constructor(
    private val apiService: IdeasApiService,
    private val authRepository: AuthRepository,
    private val credentialsRepository: CredentialsRepository,
    @param:ApplicationContext private val context: Context
) : NoteRepository {

    // TODO: Add flow for single note updates
    private val activeNoteSources = mutableSetOf<PagingSource<Int, NotePreview>>()

    override fun getUserNotes(): Flow<PagingData<NotePreview>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                createNoteSource { page, size ->
                    val token = authRepository.getValidToken()
                    apiService.getUserNotes(
                        token = token,
                        page = page,
                        limit = size
                    )
                }
            }
        ).flow
    }

    override fun getSharedNotes(): Flow<PagingData<NotePreview>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                createNoteSource { page, size ->
                    val token = authRepository.getValidToken()
                    apiService.getSharedNotes(
                        token = token,
                        page = page,
                        limit = size
                    )
                }
            }
        ).flow
    }

    override fun getGroupNotes(groupId: Long): Flow<PagingData<NotePreview>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                createNoteSource { page, size ->
                    val token = authRepository.getValidToken()
                    apiService.getGroupNotes(
                        token = token,
                        groupId = groupId,
                        page = page,
                        limit = size
                    )
                }
            }
        ).flow
    }

    override fun searchNotes(
        globalSearch: Boolean,
        title: String,
        content: String,
        tagIds: List<Long>,
        noteType: NoteType?
    ): Flow<PagingData<NotePreview>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                createNoteSource { page, size ->
                    val token = authRepository.getValidToken()
                    apiService.searchNotes(
                        token = token,
                        globalSearch = globalSearch,
                        title = title.ifBlank { null },
                        content = content.ifBlank { null },
                        tagIds = tagIds.ifEmpty { null },
                        noteType = noteType,
                        page = page,
                        limit = size
                    )
                }
            }
        ).flow
    }

    override suspend fun getNoteInfo(noteId: String): Note {
        val token = authRepository.getValidToken()
        val noteInfo = apiService.getNoteDetail(token, noteId)
        val userEmail = credentialsRepository.getSavedLogin().first()
        return noteInfo.toEntity(
            accessType = if (noteInfo.owner.email == userEmail) {
                AccessType.OWNER.name
            } else {
                noteInfo.accessType
            }
        )
    }

    override suspend fun updateNote(noteId: String, title: String) {
        val token = authRepository.getValidToken()
        apiService.updateNote(token, noteId, UpdateNoteRequest(title))
        invalidateAllNoteSources()
    }

    override suspend fun updateNoteContent(noteId: String, noteContent: List<ContentItem>) {
        val token = authRepository.getValidToken()
        apiService.updateNoteText(
            token,
            noteId,
            UpdateNoteTextBatchRequest(
                notes = noteContent.map { it.toUpdateRequest() }
            )
        )
        invalidateAllNoteSources()
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
        invalidateAllNoteSources()
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

    override fun addNoteAudio(noteId: String, audioUri: Uri): Flow<Float> = callbackFlow {
        val audioName = getFileNameFromUri(audioUri)
        val mimeType = getMimeTypeFromUri(audioUri)
        if (audioName == null) {
            close(IllegalArgumentException("Failed to retrieve audio data from URI"))
            return@callbackFlow
        }
        trySend(0f)
        val requestBody = ProgressEmittingRequestBody(
            context = context,
            fileUri = audioUri,
            mediaType = mimeType ?: "audio/*",
            progressChannel = this
        )
        val audioPart = MultipartBody.Part.createFormData(
            "file",
            audioName,
            requestBody
        )
        try {
            val token = authRepository.getValidToken()
            apiService.uploadNoteAudio(token, noteId, audioPart)
            trySend(1f)
            close()
        } catch (e: Exception) {
            close(e)
        }
        awaitClose()
    }

    override suspend fun deleteNoteContent(noteId: String, contentId: Long) {
        val token = authRepository.getValidToken()
        apiService.deleteNoteContent(
            token,
            noteId,
            contentId
        )
        invalidateAllNoteSources()
    }

    override suspend fun createNote(title: String, noteType: NoteType) {
        val token = authRepository.getValidToken()
        apiService.createNote(
            token,
            CreateNoteRequest(
                title = title,
                noteType = noteType.name
            )
        )
        invalidateAllNoteSources()
    }

    override suspend fun deleteNote(noteId: String) {
        val token = authRepository.getValidToken()
        apiService.deleteNote(token, noteId)
        invalidateAllNoteSources()
    }

    override suspend fun addNoteTag(noteId: String, tag: String) {
        val token = authRepository.getValidToken()
        apiService.addNoteTag(token, noteId, tag)
        invalidateAllNoteSources()
    }

    override suspend fun removeNoteTag(noteId: String, tag: String) {
        val token = authRepository.getValidToken()
        apiService.removeNoteTag(token, noteId, tag)
        invalidateAllNoteSources()
    }

    override suspend fun addCollaborator(noteId: String, email: String?, userId: String?) {
        val token = authRepository.getValidToken()
        apiService.addNoteCollaborator(token, noteId, AddCollaboratorRequest(userId, email))
    }

    override suspend fun removeCollaborator(noteId: String, collaboratorId: String) {
        val token = authRepository.getValidToken()
        apiService.removeNoteCollaborator(token, noteId, collaboratorId)
    }

    override suspend fun addNoteToGroup(noteId: String, groupId: Long, accessType: String) {
        val token = authRepository.getValidToken()
        apiService.addNoteToGroup(token, noteId, AddNoteToGroupRequest(groupId, accessType))
    }

    override suspend fun removeNoteFromGroup(noteId: String, groupId: Long) {
        val token = authRepository.getValidToken()
        apiService.removeNoteFromGroup(token, noteId, RemoveNoteFromGroupRequest(groupId))
        invalidateAllNoteSources()
    }

    override suspend fun getGroupNotesSimple(groupId: Long): List<NotePreview> {
        val token = authRepository.getValidToken()
        return apiService.getGroupNotes(token, groupId, page = 0, limit = 100)
            .content.map { it.toEntity() }
    }

    private fun getMimeTypeFromUri(uri: Uri): String? {
        return try {
            context.contentResolver.getType(uri)
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

    private fun createNoteSource(
        fetch: suspend (Int, Int) -> PagedResponse<NotePreviewDto>
    ): GenericPagingSource<NotePreviewDto, NotePreview> {
        return GenericPagingSource(fetch, NotePreviewDto::toEntity).also { source ->
            activeNoteSources.add(source)
            source.registerInvalidatedCallback { activeNoteSources.remove(source) }
        }
    }

    private fun invalidateAllNoteSources() {
        activeNoteSources.toList().forEach { it.invalidate() }
    }
}