package ru.shrprnbw.ideas.domain.entity

data class TokenPair(
    val accessToken: String,
    val refreshToken: String
)