package ru.shrprnbw.ideas.domain.entity

data class BoardTask(
    val id: String,
    val columnId: Long,
    val title: String,
    val priority: TaskPriority,
    val position: Int,
    val startDate: Long? = null,
    val dueDate: Long? = null,
    val completedAt: Long? = null,
    val createdAt: Long,
    val assignees: List<User>,
    val labels: List<TaskLabel>
)
