package ru.shrprnbw.ideas.data.mapper

import ru.shrprnbw.ideas.data.remote.dto.request.UpdateNoteTextRequest
import ru.shrprnbw.ideas.data.remote.dto.response.AudioNoteContentsDto
import ru.shrprnbw.ideas.data.remote.dto.response.BoardNoteContentsDto
import ru.shrprnbw.ideas.data.remote.dto.response.ContentItemDto
import ru.shrprnbw.ideas.data.remote.dto.response.NoteDto
import ru.shrprnbw.ideas.data.remote.dto.response.NotePreviewDto
import ru.shrprnbw.ideas.data.remote.dto.response.TextNoteContentsDto
import ru.shrprnbw.ideas.domain.entity.AccessType
import ru.shrprnbw.ideas.domain.entity.ContentItem
import ru.shrprnbw.ideas.domain.entity.ContentType
import ru.shrprnbw.ideas.domain.entity.Note
import ru.shrprnbw.ideas.domain.entity.NoteContent
import ru.shrprnbw.ideas.domain.entity.NotePreview
import ru.shrprnbw.ideas.domain.entity.NoteType

fun NotePreviewDto.toEntity(): NotePreview {
    return NotePreview(
        id = this.id,
        owner = this.owner.toEntity(),
        title = this.title,
        preview = this.preview,
        tags = this.tags,
        accessType = this.accessType,
        updatedAt = this.updatedAt,
        noteType = resolveNoteType(this.noteType)
    )
}

fun NoteDto.toEntity(accessType: String = this.accessType): Note {
    val content = when (val c = this.contents) {
        is BoardNoteContentsDto -> NoteContent.Board(columns = c.columns.map { it.toEntity() })
        is TextNoteContentsDto -> NoteContent.Items(items = c.items.map { it.toEntity() })
        is AudioNoteContentsDto -> NoteContent.Items(items = c.items.map { it.toEntity() })
        null -> NoteContent.Items(items = emptyList())
    }

    val noteType = when (this.contents) {
        is BoardNoteContentsDto -> NoteType.BOARD
        is AudioNoteContentsDto -> NoteType.AUDIO
        is TextNoteContentsDto -> NoteType.TEXT
        null -> NoteType.TEXT
    }

    return Note(
        id = this.id,
        owner = this.owner.toEntity(),
        title = this.title,
        content = content,
        tags = this.tags,
        accessType = AccessType.valueOf(accessType),
        noteType = noteType,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        groups = this.groups.map { it.toEntity() },
        collaborators = this.collaborators.map { it.toEntity() },
    )
}

fun ContentItemDto.toEntity(): ContentItem {
    return ContentItem(
        id = this.id,
        position = this.position,
        version = this.version,
        type = ContentType.valueOf(this.contentType),
        data = this.content
    )
}

fun ContentItem.toUpdateRequest(): UpdateNoteTextRequest {
    return UpdateNoteTextRequest(
        id = this.id,
        content = this.data
    )
}

private fun resolveNoteType(noteTypeStr: String): NoteType {
    return try {
        NoteType.valueOf(noteTypeStr)
    } catch (_: IllegalArgumentException) {
        NoteType.TEXT
    }
}
