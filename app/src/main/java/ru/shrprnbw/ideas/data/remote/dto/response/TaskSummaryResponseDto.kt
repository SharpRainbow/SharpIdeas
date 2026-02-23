package ru.shrprnbw.ideas.data.remote.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TaskSummaryResponseDto(
    @SerialName("taskId")
    val taskId: String,
    @SerialName("taskTitle")
    val taskTitle: String,
    @SerialName("priority")
    val priority: String,
    @SerialName("startDate")
    val startDate: Long? = null,
    @SerialName("dueDate")
    val dueDate: Long? = null,
    @SerialName("boardNoteId")
    val boardNoteId: String,
    @SerialName("boardTitle")
    val boardTitle: String,
    @SerialName("columnTitle")
    val columnTitle: String
)
