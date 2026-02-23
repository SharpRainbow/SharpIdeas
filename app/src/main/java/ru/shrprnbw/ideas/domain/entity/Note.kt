package ru.shrprnbw.ideas.domain.entity

data class Note(
    val id: String,
    val title: String,
    val createdAt: Long,
    val updatedAt: Long,
    val owner: User,
    val content: NoteContent,
    val tags: List<String>,
    val groups: List<Group>,
    val collaborators: List<User>,
    val accessType: AccessType,
    val noteType: NoteType,
    val noteUpdated: Boolean = false
)

val Note.textContents: List<ContentItem>
    get() = (content as? NoteContent.Items)?.items ?: emptyList()

val Note.boardColumns: List<BoardColumn>
    get() = (content as? NoteContent.Board)?.columns ?: emptyList()
