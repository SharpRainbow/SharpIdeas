package ru.shrprnbw.ideas.data.remote.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserInfoDto(
    @SerialName("id")
    val id: String,
    @SerialName("username")
    val username: String,
    @SerialName("firstName")
    val name: String,
    @SerialName("lastName")
    val surname: String,
    @SerialName("email")
    val email: String
)