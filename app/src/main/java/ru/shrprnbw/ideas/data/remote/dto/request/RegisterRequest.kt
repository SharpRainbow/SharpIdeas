package ru.shrprnbw.ideas.data.remote.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    @SerialName("username")
    val username: String,
    @SerialName("password")
    val password: String,
    @SerialName("email")
    val email: String,
    @SerialName("firstName")
    val firstName: String,
    @SerialName("lastName")
    val lastName: String
)