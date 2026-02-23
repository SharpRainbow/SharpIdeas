package ru.shrprnbw.ideas.data.remote.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddTaskAssigneeRequest(
    @SerialName("userId")
    val userId: String
)
