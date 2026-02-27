package ru.shrprnbw.ideas.domain.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.shrprnbw.ideas.domain.entity.BoardColumn
import ru.shrprnbw.ideas.domain.entity.BoardTask
import ru.shrprnbw.ideas.domain.entity.TaskAttachment
import ru.shrprnbw.ideas.domain.entity.TaskDetail
import ru.shrprnbw.ideas.domain.entity.TaskLabel
import ru.shrprnbw.ideas.domain.entity.TaskSummary
import ru.shrprnbw.ideas.domain.entity.User

interface BoardRepository {

    suspend fun getMyTasks(): List<TaskSummary>

    suspend fun createColumn(noteId: String, title: String, wipLimit: Int? = null): BoardColumn

    suspend fun updateColumn(
        noteId: String,
        columnId: Long,
        title: String? = null,
        wipLimit: Int? = null,
        archived: Boolean? = null
    ): BoardColumn

    suspend fun deleteColumn(noteId: String, columnId: Long)

    suspend fun reorderColumns(noteId: String, orderedIds: List<Long>)

    suspend fun createTask(
        noteId: String,
        columnId: Long,
        title: String,
        description: String? = null,
        priority: String? = null,
        startDate: Long? = null,
        dueDate: Long? = null
    ): BoardTask

    suspend fun getTaskDetail(noteId: String, taskId: String): TaskDetail

    suspend fun updateTask(
        noteId: String,
        taskId: String,
        title: String? = null,
        description: String? = null,
        priority: String? = null,
        startDate: Long? = null,
        dueDate: Long? = null
    ): BoardTask

    suspend fun deleteTask(noteId: String, taskId: String)

    suspend fun moveTask(noteId: String, taskId: String, targetColumnId: Long, position: Int): BoardTask

    suspend fun completeTask(noteId: String, taskId: String): BoardTask

    suspend fun reopenTask(noteId: String, taskId: String): BoardTask

    suspend fun reorderTasks(noteId: String, columnId: Long, orderedIds: List<String>)

    suspend fun getBoardLabels(noteId: String): List<TaskLabel>

    suspend fun createLabel(noteId: String, name: String, color: String): TaskLabel

    suspend fun deleteLabel(noteId: String, labelId: Long)

    suspend fun addLabelToTask(noteId: String, taskId: String, labelId: Long)

    suspend fun removeLabelFromTask(noteId: String, taskId: String, labelId: Long)

    fun getBoardAssignees(noteId: String, taskId: String): Flow<PagingData<User>>

    suspend fun addTaskAssignee(noteId: String, taskId: String, userId: String)

    suspend fun removeTaskAssignee(noteId: String, taskId: String, userId: String)

    suspend fun addTaskAttachment(
        noteId: String,
        taskId: String,
        type: String,
        title: String?,
        content: String
    ): TaskAttachment

    suspend fun deleteTaskAttachment(noteId: String, taskId: String, attachmentId: Long)

}
