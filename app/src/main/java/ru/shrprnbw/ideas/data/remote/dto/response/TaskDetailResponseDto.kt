package ru.shrprnbw.ideas.data.remote.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TaskDetailResponseDto(
    @SerialName("id")
    val id: String,
    @SerialName("columnId")
    val columnId: Long,
    @SerialName("createdBy")
    val createdBy: UserDto,
    @SerialName("title")
    val title: String,
    @SerialName("description")
    val description: String? = null,
    @SerialName("priority")
    val priority: String,
    @SerialName("position")
    val position: Int,
    @SerialName("startDate")
    val startDate: Long? = null,
    @SerialName("dueDate")
    val dueDate: Long? = null,
    @SerialName("completedAt")
    val completedAt: Long? = null,
    @SerialName("createdAt")
    val createdAt: Long,
    @SerialName("updatedAt")
    val updatedAt: Long,
    @SerialName("assignees")
    val assignees: List<UserDto>,
    @SerialName("labels")
    val labels: List<TaskLabelDto>,
    @SerialName("attachments")
    val attachments: List<TaskAttachmentDto>
)
