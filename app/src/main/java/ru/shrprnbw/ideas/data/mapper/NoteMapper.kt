package ru.shrprnbw.ideas.data.mapper

import ru.shrprnbw.ideas.data.remote.dto.response.NotePreviewDto
import ru.shrprnbw.ideas.domain.entity.NotePreview

fun NotePreviewDto.toEntity(): NotePreview {
    return NotePreview(
        id = this.id,
        owner = this.owner.toEntity(),
        groupName = this.groupName,
        title = this.title,
        preview = this.preview,
        tags = this.tags,
        accessType = this.accessType,
        updatedAt = this.updatedAt,
        hasAudio = this.hasAudio
    )
}