package ru.shrprnbw.ideas.domain.entity

data class TranscriptionSegment(
    val start: String,
    val end: String,
    val text: String,
    val speaker: String
)
