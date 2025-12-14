package ru.shrprnbw.ideas.data.remote.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddNoteTextRequest(
    @SerialName("content")
    val content: String
)