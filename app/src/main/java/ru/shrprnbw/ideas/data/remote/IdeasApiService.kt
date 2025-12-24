package ru.shrprnbw.ideas.data.remote

import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import ru.shrprnbw.ideas.data.remote.dto.request.AddNoteTextRequest
import ru.shrprnbw.ideas.data.remote.dto.request.CreateNoteRequest
import ru.shrprnbw.ideas.data.remote.dto.request.CreateTagRequest
import ru.shrprnbw.ideas.data.remote.dto.request.GoogleTokenAuthRequest
import ru.shrprnbw.ideas.data.remote.dto.request.LoginRequestDto
import ru.shrprnbw.ideas.data.remote.dto.request.RefreshRequest
import ru.shrprnbw.ideas.data.remote.dto.request.RegisterRequest
import ru.shrprnbw.ideas.data.remote.dto.request.UpdateNoteRequest
import ru.shrprnbw.ideas.data.remote.dto.request.UpdateNoteTextBatchRequest
import ru.shrprnbw.ideas.data.remote.dto.request.UpdatePersonalInfoRequest
import ru.shrprnbw.ideas.data.remote.dto.response.LoginResponseDto
import ru.shrprnbw.ideas.data.remote.dto.response.NoteDto
import ru.shrprnbw.ideas.data.remote.dto.response.NotePreviewDto
import ru.shrprnbw.ideas.data.remote.dto.response.PagedResponse
import ru.shrprnbw.ideas.data.remote.dto.response.TagDto
import ru.shrprnbw.ideas.data.remote.dto.response.TokenResponseDto
import ru.shrprnbw.ideas.data.remote.dto.response.TranscriptionDto
import ru.shrprnbw.ideas.data.remote.dto.response.UserInfoDto

interface IdeasApiService {

    companion object {
        const val VERSION_1 = "v1"
        const val VERSION_2 = "v2"
    }

    @POST("$VERSION_1/users/login")
    suspend fun login(
        @Body request: LoginRequestDto
    ): LoginResponseDto

    @POST("$VERSION_1/users/register")
    suspend fun register(
        @Body request: RegisterRequest
    )

    @GET("$VERSION_1/users/personal_info")
    suspend fun getUserInfo(
        @Header("Authorization") token: String,
    ): UserInfoDto

    @PATCH("$VERSION_1/users/personal_info")
    suspend fun updateUserInfo(
        @Header("Authorization") token: String,
        @Body request: UpdatePersonalInfoRequest
    )

    @GET("$VERSION_2/tags")
    suspend fun getUserCreatedTags(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") limit: Int,
    ): PagedResponse<TagDto>

    @GET("$VERSION_1/notes/personal")
    suspend fun getUserNotes(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") limit: Int,
    ): PagedResponse<NotePreviewDto>

    @GET("$VERSION_1/notes/search")
    suspend fun searchNotes(
        @Header("Authorization") token: String,
        @Query("globalSearch") globalSearch: Boolean,
        @Query("title") title: String?,
        @Query("content") content: String?,
        @Query("tagIds") tagIds: List<Long>?,
        @Query("audioNote") audioNote: Boolean?,
        @Query("page") page: Int,
        @Query("size") limit: Int,
    ): PagedResponse<NotePreviewDto>

    @POST("$VERSION_2/auth/login-password")
    suspend fun loginV2(
        @Body request: LoginRequestDto
    ): TokenResponseDto

    @POST("$VERSION_2/auth/refresh")
    suspend fun refreshAccessToken(
        @Body request: RefreshRequest
    ): TokenResponseDto

    @POST("$VERSION_2/auth/exchange")
    suspend fun loginWithGoogle(
        @Body request: GoogleTokenAuthRequest
    ): TokenResponseDto

    @POST("$VERSION_1/notes")
    suspend fun createNote(
        @Header("Authorization") token: String,
        @Body createRequest: CreateNoteRequest
    )

    @DELETE("$VERSION_1/notes/{noteId}")
    suspend fun deleteNote(
        @Header("Authorization") token: String,
        @Path("noteId") noteId: String
    )

    @POST("$VERSION_1/notes/{noteId}/contents/text")
    suspend fun addNoteText(
        @Header("Authorization") token: String,
        @Path("noteId") noteId: String,
        @Body textRequest: AddNoteTextRequest
    )

    @GET("$VERSION_1/notes/{noteId}")
    suspend fun getNoteDetail(
        @Header("Authorization") token: String,
        @Path("noteId") noteId: String
    ): NoteDto

    @PATCH("$VERSION_1/notes/{noteId}")
    suspend fun updateNote(
        @Header("Authorization") token: String,
        @Path("noteId") noteId: String,
        @Body updateRequest: UpdateNoteRequest
    )

    @PATCH("$VERSION_1/notes/{noteId}/contents/text")
    suspend fun updateNoteText(
        @Header("Authorization") token: String,
        @Path("noteId") noteId: String,
        @Body updateRequest: UpdateNoteTextBatchRequest
    )

    @Multipart
    @POST("$VERSION_1/notes/{noteId}/contents/image")
    suspend fun uploadNoteImage(
        @Header("Authorization") token: String,
        @Path("noteId") noteId: String,
        @Part file: MultipartBody.Part
    )

    @DELETE("$VERSION_1/notes/{noteId}/contents/{contentId}")
    suspend fun deleteNoteContent(
        @Header("Authorization") token: String,
        @Path("noteId") noteId: String,
        @Path("contentId") contentId: Long
    )

    @GET("$VERSION_1/notes/{noteId}/transcriptions")
    suspend fun getTranscriptions(
        @Header("Authorization") token: String,
        @Path("noteId") noteId: String,
        @Query("page") page: Int,
        @Query("size") limit: Int
    ): PagedResponse<TranscriptionDto>

    @GET("$VERSION_1/notes/{noteId}/transcriptions/{transcriptionId}")
    suspend fun getTranscription(
        @Header("Authorization") token: String,
        @Path("noteId") noteId: String,
        @Path("transcriptionId") transcriptionId: String
    ): TranscriptionDto

    @POST("$VERSION_1/notes/{noteId}/tags")
    suspend fun addNoteTag(
        @Header("Authorization") token: String,
        @Path("noteId") noteId: String,
        @Query("tag") tag: String
    )

    @DELETE("$VERSION_1/notes/{noteId}/tags")
    suspend fun removeNoteTag(
        @Header("Authorization") token: String,
        @Path("noteId") noteId: String,
        @Query("tag") tag: String
    )

    @POST("$VERSION_2/tags")
    suspend fun createTag(
        @Header("Authorization") token: String,
        @Body request: CreateTagRequest
    )

    @PATCH("$VERSION_2/tags/{tagId}")
    suspend fun updateTag(
        @Header("Authorization") token: String,
        @Path("tagId") tagId: Long,
        @Body request: CreateTagRequest
    )

    @DELETE("$VERSION_2/tags/{tagId}")
    suspend fun deleteTag(
        @Header("Authorization") token: String,
        @Path("tagId") tagId: Long
    )

}