package ru.shrprnbw.ideas.data.remote.dto.response

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@Serializable
@OptIn(ExperimentalSerializationApi::class)
@JsonClassDiscriminator("type")
sealed interface NoteContentsDto

@Serializable
@SerialName("TEXT")
data class TextNoteContentsDto(
    val items: List<ContentItemDto> = emptyList()
) : NoteContentsDto

@Serializable
@SerialName("AUDIO")
data class AudioNoteContentsDto(
    val items: List<ContentItemDto> = emptyList()
) : NoteContentsDto

@Serializable
@SerialName("BOARD")
data class BoardNoteContentsDto(
    val columns: List<BoardColumnWithTasksDto> = emptyList()
) : NoteContentsDto
