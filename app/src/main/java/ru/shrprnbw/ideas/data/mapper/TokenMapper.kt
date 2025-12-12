package ru.shrprnbw.ideas.data.mapper

import ru.shrprnbw.ideas.data.remote.dto.response.TokenResponseDto
import ru.shrprnbw.ideas.domain.entity.TokenPair

fun TokenResponseDto.toEntity() = TokenPair(
    accessToken = this.accessToken,
    refreshToken = this.refreshToken
)