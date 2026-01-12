@file:OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)

package ru.shrprnbw.ideas.data.repository

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import ru.shrprnbw.ideas.data.ProgressEmittingRequestBody
import ru.shrprnbw.ideas.data.mapper.toEntity
import ru.shrprnbw.ideas.data.mapper.toUpdateRequest
import ru.shrprnbw.ideas.data.remote.IdeasApiService
import ru.shrprnbw.ideas.data.remote.dto.request.AddCollaboratorRequest
import ru.shrprnbw.ideas.data.remote.dto.request.AddNoteTextRequest
import ru.shrprnbw.ideas.data.remote.dto.request.AddNoteToGroupRequest
import ru.shrprnbw.ideas.data.remote.dto.request.CreateNoteRequest
import ru.shrprnbw.ideas.data.remote.dto.request.RemoveNoteFromGroupRequest
import ru.shrprnbw.ideas.data.remote.dto.request.UpdateNoteRequest
import ru.shrprnbw.ideas.data.remote.dto.request.UpdateNoteTextBatchRequest
import ru.shrprnbw.ideas.data.remote.paging.FilterNotesSearchStrategy
import ru.shrprnbw.ideas.data.remote.paging.GroupNotesSearchStrategy
import ru.shrprnbw.ideas.data.remote.paging.KeywordPagingSource
import ru.shrprnbw.ideas.data.remote.paging.NotePagingSource
import ru.shrprnbw.ideas.data.remote.paging.PersonalNotesSearchStrategy
import ru.shrprnbw.ideas.data.remote.paging.SharedNotesSearchStrategy
import ru.shrprnbw.ideas.data.remote.paging.SummaryPagingSource
import ru.shrprnbw.ideas.data.remote.paging.TranscriptionPagingSource
import ru.shrprnbw.ideas.domain.entity.AccessType
import ru.shrprnbw.ideas.domain.entity.ContentItem
import ru.shrprnbw.ideas.domain.entity.Keyword
import ru.shrprnbw.ideas.domain.entity.Note
import ru.shrprnbw.ideas.domain.entity.NotePreview
import ru.shrprnbw.ideas.domain.entity.Summary
import ru.shrprnbw.ideas.domain.entity.Transcription
import ru.shrprnbw.ideas.domain.repository.AuthRepository
import ru.shrprnbw.ideas.domain.repository.CredentialsRepository
import ru.shrprnbw.ideas.domain.repository.NoteRepository
import java.io.IOException

class NoteRepositoryImpl @Inject constructor(
    private val apiService: IdeasApiService,
    private val authRepository: AuthRepository,
    private val credentialsRepository: CredentialsRepository,
    private val personalNotesSearchStrategy: PersonalNotesSearchStrategy,
    private val sharedNotesSearchStrategy: SharedNotesSearchStrategy,
    @param:ApplicationContext private val context: Context
) : NoteRepository {

    // TODO: Add flow for single note updates
    private val notesRefreshTrigger = MutableSharedFlow<Unit>()
    private val transcriptionsRefreshTrigger = MutableSharedFlow<Unit>()
    private val keywordsRefreshTrigger = MutableSharedFlow<Unit>()
    private val summariesRefreshTrigger = MutableSharedFlow<Unit>()

    override fun getUserNotes(): Flow<PagingData<NotePreview>> {
        return notesRefreshTrigger.onStart {
            emit(Unit)
        }.debounce(
            timeoutMillis = 1000
        ).flatMapLatest {
            Pager(
                config = PagingConfig(
                    pageSize = 10,
                    enablePlaceholders = false
                ),
                pagingSourceFactory = {
                    NotePagingSource(personalNotesSearchStrategy)
                }
            ).flow
        }
    }

    override fun getSharedNotes(): Flow<PagingData<NotePreview>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                NotePagingSource(sharedNotesSearchStrategy)
            }
        ).flow
    }

    override fun getGroupNotes(groupId: Long): Flow<PagingData<NotePreview>> {
        return notesRefreshTrigger.onStart {
            emit(Unit)
        }.debounce(
            timeoutMillis = 1000
        ).flatMapLatest {
            Pager(
                config = PagingConfig(
                    pageSize = 10,
                    enablePlaceholders = false
                ),
                pagingSourceFactory = {
                    NotePagingSource(
                        GroupNotesSearchStrategy(
                            apiService,
                            authRepository,
                            groupId
                        )
                    )
                }
            ).flow
        }
    }

    override fun searchNotes(
        globalSearch: Boolean,
        title: String,
        content: String,
        tagIds: List<Long>,
        audioNote: Boolean?
    ): Flow<PagingData<NotePreview>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                NotePagingSource(
                    FilterNotesSearchStrategy(
                        apiService,
                        authRepository,
                        globalSearch,
                        title,
                        content,
                        tagIds,
                        audioNote
                    )
                )
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
        notesRefreshTrigger.emit(Unit)
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
        notesRefreshTrigger.emit(Unit)
    }

    override suspend fun createTextNote(title: String) {
        val token = authRepository.getValidToken()
        apiService.createNote(
            token,
            CreateNoteRequest(
                title = title,
                audioNote = false
            )
        )
        notesRefreshTrigger.emit(Unit)
    }

    override suspend fun createAudioNote(title: String) {
        val token = authRepository.getValidToken()
        apiService.createNote(
            token,
            CreateNoteRequest(
                title = title,
                audioNote = true
            )
        )
        notesRefreshTrigger.emit(Unit)
    }

    override suspend fun deleteNote(noteId: String) {
        val token = authRepository.getValidToken()
        apiService.deleteNote(token, noteId)
        notesRefreshTrigger.emit(Unit)
    }

    override fun getTranscriptions(noteId: String): Flow<PagingData<Transcription>> {
        return transcriptionsRefreshTrigger.onStart {
            emit(Unit)
        }.debounce(
            timeoutMillis = 1000
        ).flatMapLatest {
            Pager(
                config = PagingConfig(
                    pageSize = 10,
                    enablePlaceholders = false
                ),
                pagingSourceFactory = {
                    TranscriptionPagingSource(
                        apiService,
                        authRepository,
                        noteId
                    )
                }
            ).flow
        }
    }

    override suspend fun getTranscription(noteId: String, transcriptionId: String): Transcription {
        val token = authRepository.getValidToken()
        return apiService.getTranscription(token, noteId, transcriptionId).toEntity()
    }

    override suspend fun requestTranscription(noteId: String) {
        val token = authRepository.getValidToken()
        apiService.generateNoteTranscription(token, noteId)
        transcriptionsRefreshTrigger.emit(Unit)
    }

    override fun getKeywords(noteId: String): Flow<PagingData<Keyword>> {
        return keywordsRefreshTrigger.onStart {
            emit(Unit)
        }.debounce(
            timeoutMillis = 1000
        ).flatMapLatest {
            Pager(
                config = PagingConfig(
                    pageSize = 10,
                    enablePlaceholders = false
                ),
                pagingSourceFactory = {
                    KeywordPagingSource(
                        apiService,
                        authRepository,
                        noteId
                    )
                }
            ).flow
        }
    }

    override suspend fun requestKeywords(noteId: String) {
        val token = authRepository.getValidToken()
        apiService.generateNoteKeywords(token, noteId)
        keywordsRefreshTrigger.emit(Unit)
    }

    override fun getSummaries(noteId: String): Flow<PagingData<Summary>> {
        return summariesRefreshTrigger.onStart {
            emit(Unit)
        }.debounce(
            timeoutMillis = 1000
        ).flatMapLatest {
            Pager(
                config = PagingConfig(
                    pageSize = 10,
                    enablePlaceholders = false
                ),
                pagingSourceFactory = {
                    SummaryPagingSource(
                        apiService,
                        authRepository,
                        noteId
                    )
                }
            ).flow
        }
    }

    override suspend fun getSummary(noteId: String, summaryId: String): Summary {
        val token = authRepository.getValidToken()
        return apiService.getSummary(token, noteId, summaryId).toEntity()
    }

    override suspend fun requestSummary(noteId: String) {
        val token = authRepository.getValidToken()
        apiService.generateNoteSummary(token, noteId)
        summariesRefreshTrigger.emit(Unit)
    }

    override suspend fun addNoteTag(noteId: String, tag: String) {
        val token = authRepository.getValidToken()
        apiService.addNoteTag(token, noteId, tag)
        notesRefreshTrigger.emit(Unit)
    }

    override suspend fun removeNoteTag(noteId: String, tag: String) {
        val token = authRepository.getValidToken()
        apiService.removeNoteTag(token, noteId, tag)
        notesRefreshTrigger.emit(Unit)
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
        notesRefreshTrigger.emit(Unit)
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
}