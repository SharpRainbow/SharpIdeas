package ru.shrprnbw.ideas.data.remote.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NoteDto(
    @SerialName("id")
    val id: String,
    @SerialName("title")
    val title: String,
    @SerialName("owner")
    val owner: UserDto,
    @SerialName("contents")
    val contents: NoteContentsDto? = null,
    @SerialName("tags")
    val tags: List<String>,
    @SerialName("groups")
    val groups: List<GroupDto>,
    @SerialName("collaborators")
    val collaborators: List<UserDto>,
    @SerialName("updatedAt")
    val updatedAt: Long,
    @SerialName("createdAt")
    val createdAt: Long,
    @SerialName("accessType")
    val accessType: String
)
