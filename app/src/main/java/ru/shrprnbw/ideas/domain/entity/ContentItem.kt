package ru.shrprnbw.ideas.domain.entity

data class ContentItem(
    val id: Long,
    val position: Long,
    val version: Long,
    val type: ContentType,
    val data: String,
    val edited: Boolean = false
)
