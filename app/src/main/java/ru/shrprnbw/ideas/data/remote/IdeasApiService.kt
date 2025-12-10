package ru.shrprnbw.ideas.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query
import ru.shrprnbw.ideas.data.remote.dto.request.LoginRequestDto
import ru.shrprnbw.ideas.data.remote.dto.request.RegisterRequest
import ru.shrprnbw.ideas.data.remote.dto.request.UpdatePersonalInfoRequest
import ru.shrprnbw.ideas.data.remote.dto.response.LoginResponseDto
import ru.shrprnbw.ideas.data.remote.dto.response.NoteDto
import ru.shrprnbw.ideas.data.remote.dto.response.TagDto
import ru.shrprnbw.ideas.data.remote.dto.response.UserInfoDto

interface IdeasApiService {

    @POST("users/login")
    suspend fun login(
        @Body request: LoginRequestDto
    ): LoginResponseDto

    @POST("users/register")
    suspend fun register(
        @Body request: RegisterRequest
    )

    @GET("users/personal_info")
    suspend fun getUserInfo(
        @Header("Authorization") token: String,
    ): UserInfoDto

    @PATCH("users/personal_info")
    suspend fun updateUserInfo(
        @Header("Authorization") token: String,
        @Body request: UpdatePersonalInfoRequest
    )

    @GET("users/tags")
    suspend fun getUserCreatedTags(
        @Header("Authorization") token: String,
    ): List<TagDto>

    @GET("notes/personal")
    suspend fun getUserNotes(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") limit: Int,
    ): List<NoteDto>

    @GET("notes/search")
    suspend fun searchNotes(
        @Header("Authorization") token: String,
        @Query("globalSearch") globalSearch: Boolean,
        @Query("title") title: String?,
        @Query("content") content: String?,
        @Query("tagIds") tagIds: List<Long>?,
        @Query("page") page: Int,
        @Query("size") limit: Int,
    ): List<NoteDto>

}