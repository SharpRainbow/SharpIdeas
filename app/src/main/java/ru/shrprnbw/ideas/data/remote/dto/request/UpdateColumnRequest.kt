package ru.shrprnbw.ideas.data.remote.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateColumnRequest(
    @SerialName("title")
    val title: String? = null,
    @SerialName("wipLimit")
    val wipLimit: Int? = null,
    @SerialName("archived")
    val archived: Boolean? = null
)
