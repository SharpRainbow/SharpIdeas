package ru.shrprnbw.ideas.domain.entity

sealed interface NoteContent {
    data class Items(val items: List<ContentItem>) : NoteContent
    data class Board(val columns: List<BoardColumn>) : NoteContent
}
