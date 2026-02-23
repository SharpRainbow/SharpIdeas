package ru.shrprnbw.ideas.domain.entity

data class BoardColumn(
    val id: Long,
    val title: String,
    val position: Int,
    val wipLimit: Int? = null,
    val archived: Boolean,
    val tasks: List<BoardTask>
)
