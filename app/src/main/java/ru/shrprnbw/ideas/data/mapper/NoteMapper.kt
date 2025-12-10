package ru.shrprnbw.ideas.data.mapper

import ru.shrprnbw.ideas.data.remote.dto.response.NoteDto
import ru.shrprnbw.ideas.domain.entity.Note

fun NoteDto.toEntity(): Note {
    return Note(
        id = this.id,
        owner = this.owner.toEntity(),
        groupName = this.groupName,
        title = this.title,
        preview = this.preview,
        tags = this.tags,
        accessType = this.accessType,
    )
}