package ru.shrprnbw.ideas.domain.entity

data class Note(
    val id: String,
    val title: String,
    val createdAt: Long,
    val updatedAt: Long,
    val owner: User,
    val contents: List<ContentItem>,
    val tags: List<String>,
    val collaborators: List<User>,
    val accessType: AccessType,
    val audioNote: Boolean,
    val noteUpdated: Boolean = false
)