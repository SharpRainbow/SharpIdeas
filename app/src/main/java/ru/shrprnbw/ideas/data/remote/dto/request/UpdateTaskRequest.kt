package ru.shrprnbw.ideas.data.remote.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateTaskRequest(
    @SerialName("title")
    val title: String? = null,
    @SerialName("description")
    val description: String? = null,
    @SerialName("priority")
    val priority: String? = null,
    @SerialName("startDate")
    val startDate: Long? = null,
    @SerialName("dueDate")
    val dueDate: Long? = null
)
