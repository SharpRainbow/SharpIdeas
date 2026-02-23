package ru.shrprnbw.ideas.data.remote.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReorderTasksRequest(
    @SerialName("orderedIds")
    val orderedIds: List<String>
)
