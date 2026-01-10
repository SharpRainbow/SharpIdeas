package ru.shrprnbw.ideas.data.remote.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddUserToGroupRequest(
    @SerialName("userData")
    val userData: List<String>,
    @SerialName("byEmail")
    val byEmail: Boolean = false
)
