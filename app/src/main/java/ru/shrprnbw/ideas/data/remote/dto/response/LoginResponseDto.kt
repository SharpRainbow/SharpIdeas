package ru.shrprnbw.ideas.data.remote.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponseDto(
    @SerialName("email")
    val email: String,
    @SerialName("token")
    val token: String
)