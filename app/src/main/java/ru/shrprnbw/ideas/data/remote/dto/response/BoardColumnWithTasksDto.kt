package ru.shrprnbw.ideas.data.remote.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BoardColumnWithTasksDto(
    @SerialName("id")
    val id: Long,
    @SerialName("title")
    val title: String,
    @SerialName("position")
    val position: Int,
    @SerialName("wipLimit")
    val wipLimit: Int? = null,
    @SerialName("archived")
    val archived: Boolean,
    @SerialName("tasks")
    val tasks: List<TaskResponseDto>
)
