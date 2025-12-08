package ru.shrprnbw.ideas.data.remote.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdatePersonalInfoRequest(
    @SerialName("email")
    val email: String,
    @SerialName("username")
    val username: String
)