package ru.shrprnbw.ideas.data.remote.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NoteDto(
    @SerialName("id")
    val id: String,
    @SerialName("title")
    val title: String,
//    @SerialName("groupId")
//    val groupId: Long?,
    @SerialName("groupName")
    val groupName: String?,
    @SerialName("owner")
    val owner: UserDto,
    @SerialName("contents")
    val contents: List<ContentItemDto>,
    @SerialName("tags")
    val tags: List<String>,
    @SerialName("collaborators")
    val collaborators: List<UserDto>,
    @SerialName("audioNote")
    val audioNote: Boolean,
    @SerialName("updatedAt")
    val updatedAt: Long,
    @SerialName("createdAt")
    val createdAt: Long,
    @SerialName("accessType")
    val accessType: String
)