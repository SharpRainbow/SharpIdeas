package ru.shrprnbw.ideas.data.mapper

import ru.shrprnbw.ideas.data.remote.dto.response.TagDto
import ru.shrprnbw.ideas.domain.entity.Tag

fun TagDto.toEntity() = Tag(
    id = id,
    name = name
)