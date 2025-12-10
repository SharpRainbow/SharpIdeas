package ru.shrprnbw.ideas.domain.entity

data class NotePreview(
    val id: String,
    val owner: User,
    val groupName: String,
    val title: String,
    val preview: String,
    val tags: List<String>,
    val accessType: String,
    val updatedAt: Long,
    val hasAudio: Boolean
) // TODO: Add summaryAvailable, keywordsAvailable, transcriptAvailable