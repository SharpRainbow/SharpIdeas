package ru.shrprnbw.ideas.domain.entity

data class TaskSummary(
    val taskId: String,
    val taskTitle: String,
    val priority: TaskPriority,
    val startDate: Long? = null,
    val dueDate: Long? = null,
    val boardNoteId: String,
    val boardTitle: String,
    val columnTitle: String
)
