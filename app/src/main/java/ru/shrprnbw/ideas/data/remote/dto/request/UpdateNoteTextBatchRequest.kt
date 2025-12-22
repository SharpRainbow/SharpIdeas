package ru.shrprnbw.ideas.data.remote.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateNoteTextBatchRequest(
    @SerialName("textContents")
    val notes: List<UpdateNoteTextRequest>
)