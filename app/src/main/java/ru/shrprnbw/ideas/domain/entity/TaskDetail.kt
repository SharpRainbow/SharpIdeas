package ru.shrprnbw.ideas.domain.entity

data class TaskDetail(
    val id: String,
    val columnId: Long,
    val createdBy: User,
    val title: String,
    val description: String?,
    val priority: TaskPriority,
    val position: Int,
    val startDate: Long? = null,
    val dueDate: Long? = null,
    val completedAt: Long? = null,
    val createdAt: Long,
    val updatedAt: Long,
    val assignees: List<User>,
    val labels: List<TaskLabel>,
    val attachments: List<TaskAttachment>
)
