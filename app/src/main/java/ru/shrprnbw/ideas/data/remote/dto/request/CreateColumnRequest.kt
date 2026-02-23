package ru.shrprnbw.ideas.data.remote.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateColumnRequest(
    @SerialName("title")
    val title: String,
    @SerialName("wipLimit")
    val wipLimit: Int? = null
)
