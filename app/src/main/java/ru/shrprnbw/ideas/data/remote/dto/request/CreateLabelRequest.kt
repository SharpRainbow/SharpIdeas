package ru.shrprnbw.ideas.data.remote.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateLabelRequest(
    @SerialName("name")
    val name: String,
    @SerialName("color")
    val color: String
)
