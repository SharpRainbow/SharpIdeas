package ru.shrprnbw.ideas.domain.entity

data class TaskAttachment(
    val id: Long,
    val type: String,
    val title: String?,
    val content: String,
    val createdAt: Long
)
