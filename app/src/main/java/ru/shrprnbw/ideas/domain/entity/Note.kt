package ru.shrprnbw.ideas.domain.entity

data class Note(
    val id: String,
    val owner: User,
    val groupName: String,
    val title: String,
    val preview: String,
    val tags: List<String>,
    val accessType: String
) // TODO: Add createdAt, updatedAt, summaryAvailable, keywordsAvailable, transcriptAvailable, hasAudio