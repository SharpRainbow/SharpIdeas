package ru.shrprnbw.ideas.data.mapper

import ru.shrprnbw.ideas.data.remote.dto.request.UpdateNoteTextRequest
import ru.shrprnbw.ideas.data.remote.dto.response.ContentItemDto
import ru.shrprnbw.ideas.data.remote.dto.response.NoteDto
import ru.shrprnbw.ideas.data.remote.dto.response.NotePreviewDto
import ru.shrprnbw.ideas.domain.entity.AccessType
import ru.shrprnbw.ideas.domain.entity.ContentItem
import ru.shrprnbw.ideas.domain.entity.ContentType
import ru.shrprnbw.ideas.domain.entity.Note
import ru.shrprnbw.ideas.domain.entity.NotePreview

fun NotePreviewDto.toEntity(): NotePreview {
    return NotePreview(
        id = this.id,
        owner = this.owner.toEntity(),
        title = this.title,
        preview = this.preview,
        tags = this.tags,
        accessType = this.accessType,
        updatedAt = this.updatedAt,
        audioNote = this.audioNote
    )
}

fun NoteDto.toEntity(accessType: String = this.accessType): Note {
    return Note(
        id = this.id,
        owner = this.owner.toEntity(),
        title = this.title,
        contents = this.contents.map { it.toEntity() },
        tags = this.tags,
        accessType = AccessType.valueOf(accessType),
        audioNote = this.audioNote,
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