package ru.shrprnbw.ideas.data.remote.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotePreviewDto(
    @SerialName("id")
    val id: String,
    @SerialName("owner")
    val owner: UserDto,
    @SerialName("title")
    val title: String,
    @SerialName("preview")
    val preview: String,
    @SerialName("noteTags")
    val tags: List<String>,
    @SerialName("accessType")
    val accessType: String,
    @SerialName("updatedAt")
    val updatedAt: Long,
    @SerialName("audioNote")
    val audioNote: Boolean
)