package ru.shrprnbw.ideas.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query
import ru.shrprnbw.ideas.data.remote.dto.request.GoogleTokenAuthRequest
import ru.shrprnbw.ideas.data.remote.dto.request.LoginRequestDto
import ru.shrprnbw.ideas.data.remote.dto.request.RefreshRequest
import ru.shrprnbw.ideas.data.remote.dto.request.RegisterRequest
import ru.shrprnbw.ideas.data.remote.dto.request.UpdatePersonalInfoRequest
import ru.shrprnbw.ideas.data.remote.dto.response.LoginResponseDto
import ru.shrprnbw.ideas.data.remote.dto.response.NotePreviewDto
import ru.shrprnbw.ideas.data.remote.dto.response.TagDto
import ru.shrprnbw.ideas.data.remote.dto.response.TokenResponseDto
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

    @GET("$VERSION_1/users/tags")
    suspend fun getUserCreatedTags(
        @Header("Authorization") token: String,
    ): List<TagDto>

    @GET("$VERSION_1/notes/personal")
    suspend fun getUserNotes(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") limit: Int,
    ): List<NotePreviewDto>

    @GET("$VERSION_1/notes/search")
    suspend fun searchNotes(
        @Header("Authorization") token: String,
        @Query("globalSearch") globalSearch: Boolean,
        @Query("title") title: String?,
        @Query("content") content: String?,
        @Query("tagIds") tagIds: List<Long>?,
        @Query("page") page: Int,
        @Query("size") limit: Int,
    ): List<NotePreviewDto>

    @POST("$VERSION_2/auth/login-password")
    suspend fun loginV2(
        @Body request: LoginRequestDto
    ): Response<TokenResponseDto>

    @POST("$VERSION_2/auth/refresh")
    suspend fun refreshAccessToken(
        @Body request: RefreshRequest
    ): TokenResponseDto

    @POST("$VERSION_2/auth/exchange")
    suspend fun loginWithGoogle(
        @Body request: GoogleTokenAuthRequest
    ): TokenResponseDto

}