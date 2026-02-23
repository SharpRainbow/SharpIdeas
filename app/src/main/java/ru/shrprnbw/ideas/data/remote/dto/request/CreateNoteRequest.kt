package ru.shrprnbw.ideas.data.remote.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateNoteRequest(
    @SerialName("title")
    val title: String,
    @SerialName("noteType")
    val noteType: String
)