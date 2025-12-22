package ru.shrprnbw.ideas.data.remote.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TranscriptionSegmentDto(
    @SerialName("start")
    val start: String,
    @SerialName("end")
    val end: String,
    @SerialName("text")
    val text: String,
    @SerialName("speaker")
    val speaker: String
)
