package ru.shrprnbw.ideas.data.remote.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddNoteToGroupRequest(
    @SerialName("groupId")
    val groupId: Long,
    @SerialName("accessType")
    val accessType: String
)
