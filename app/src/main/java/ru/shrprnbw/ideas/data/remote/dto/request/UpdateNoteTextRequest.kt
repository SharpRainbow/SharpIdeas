package ru.shrprnbw.ideas.data.remote.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateNoteTextRequest(
    @SerialName("id")
    val id: Long,
    @SerialName("content")
    val content: String
)
