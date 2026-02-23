package ru.shrprnbw.ideas.data.mapper

import ru.shrprnbw.ideas.data.remote.dto.response.BoardColumnResponseDto
import ru.shrprnbw.ideas.data.remote.dto.response.BoardColumnWithTasksDto
import ru.shrprnbw.ideas.data.remote.dto.response.TaskAttachmentDto
import ru.shrprnbw.ideas.data.remote.dto.response.TaskDetailResponseDto
import ru.shrprnbw.ideas.data.remote.dto.response.TaskLabelDto
import ru.shrprnbw.ideas.data.remote.dto.response.TaskResponseDto
import ru.shrprnbw.ideas.data.remote.dto.response.TaskSummaryResponseDto
import ru.shrprnbw.ideas.domain.entity.BoardColumn
import ru.shrprnbw.ideas.domain.entity.BoardTask
import ru.shrprnbw.ideas.domain.entity.TaskAttachment
import ru.shrprnbw.ideas.domain.entity.TaskDetail
import ru.shrprnbw.ideas.domain.entity.TaskLabel
import ru.shrprnbw.ideas.domain.entity.TaskPriority
import ru.shrprnbw.ideas.domain.entity.TaskSummary

fun BoardColumnWithTasksDto.toEntity(): BoardColumn {
    return BoardColumn(
        id = this.id,
        title = this.title,
        position = this.position,
        wipLimit = this.wipLimit,
        archived = this.archived,
        tasks = this.tasks.map { it.toEntity() }
    )
}

fun BoardColumnResponseDto.toEntity(): BoardColumn {
    return BoardColumn(
        id = this.id,
        title = this.title,
        position = this.position,
        wipLimit = this.wipLimit,
        archived = this.archived,
        tasks = emptyList()
    )
}

fun TaskResponseDto.toEntity(): BoardTask {
    return BoardTask(
        id = this.id,
        columnId = this.columnId,
        title = this.title,
        priority = TaskPriority.fromString(this.priority),
        position = this.position,
        startDate = this.startDate,
        dueDate = this.dueDate,
        completedAt = this.completedAt,
        createdAt = this.createdAt,
        assignees = this.assignees.map { it.toEntity() },
        labels = this.labels.map { it.toEntity() }
    )
}

fun TaskDetailResponseDto.toEntity(): TaskDetail {
    return TaskDetail(
        id = this.id,
        columnId = this.columnId,
        createdBy = this.createdBy.toEntity(),
        title = this.title,
        description = this.description,
        priority = TaskPriority.fromString(this.priority),
        position = this.position,
        startDate = this.startDate,
        dueDate = this.dueDate,
        completedAt = this.completedAt,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        assignees = this.assignees.map { it.toEntity() },
        labels = this.labels.map { it.toEntity() },
        attachments = this.attachments.map { it.toEntity() }
    )
}

fun TaskLabelDto.toEntity(): TaskLabel {
    return TaskLabel(
        id = this.id,
        name = this.name,
        color = this.color
    )
}

fun TaskAttachmentDto.toEntity(): TaskAttachment {
    return TaskAttachment(
        id = this.id,
        type = this.type,
        title = this.title,
        content = this.content,
        createdAt = this.createdAt
    )
}

fun TaskSummaryResponseDto.toEntity(): TaskSummary {
    return TaskSummary(
        taskId = this.taskId,
        taskTitle = this.taskTitle,
        priority = TaskPriority.fromString(this.priority),
        startDate = this.startDate,
        dueDate = this.dueDate,
        boardNoteId = this.boardNoteId,
        boardTitle = this.boardTitle,
        columnTitle = this.columnTitle
    )
}
