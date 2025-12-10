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

    @GET("notes/personal")
    suspend fun getUserNotes(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") limit: Int,
    ): List<NoteDto>

}