package ru.shrprnbw.ideas.data.remote.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ContentItemDto(
    @SerialName("id")
    val id: Long,
    @SerialName("position")
    val position: Long,
    @SerialName("version")
    val version: Long,
    @SerialName("contentType")
    val contentType: String,
    @SerialName("content")
    val content: String
)