package ru.shrprnbw.ideas.data.remote.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MoveTaskRequest(
    @SerialName("targetColumnId")
    val targetColumnId: Long,
    @SerialName("position")
    val position: Int
)
