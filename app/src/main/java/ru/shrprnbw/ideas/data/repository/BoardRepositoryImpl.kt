package ru.shrprnbw.ideas.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import ru.shrprnbw.ideas.data.mapper.toEntity
import ru.shrprnbw.ideas.data.remote.GenericPagingSource
import ru.shrprnbw.ideas.data.remote.IdeasApiService
import ru.shrprnbw.ideas.data.remote.dto.request.AddAttachmentRequest
import ru.shrprnbw.ideas.data.remote.dto.request.AddTaskAssigneeRequest
import ru.shrprnbw.ideas.data.remote.dto.request.CreateColumnRequest
import ru.shrprnbw.ideas.data.remote.dto.request.CreateLabelRequest
import ru.shrprnbw.ideas.data.remote.dto.request.CreateTaskRequest
import ru.shrprnbw.ideas.data.remote.dto.request.MoveTaskRequest
import ru.shrprnbw.ideas.data.remote.dto.request.ReorderColumnsRequest
import ru.shrprnbw.ideas.data.remote.dto.request.ReorderTasksRequest
import ru.shrprnbw.ideas.data.remote.dto.request.UpdateColumnRequest
import ru.shrprnbw.ideas.data.remote.dto.request.UpdateTaskRequest
import ru.shrprnbw.ideas.data.remote.dto.response.UserDto
import ru.shrprnbw.ideas.domain.entity.BoardColumn
import ru.shrprnbw.ideas.domain.entity.BoardTask
import ru.shrprnbw.ideas.domain.entity.TaskAttachment
import ru.shrprnbw.ideas.domain.entity.TaskDetail
import ru.shrprnbw.ideas.domain.entity.TaskLabel
import ru.shrprnbw.ideas.domain.entity.TaskSummary
import ru.shrprnbw.ideas.domain.entity.User
import ru.shrprnbw.ideas.domain.repository.AuthRepository
import ru.shrprnbw.ideas.domain.repository.BoardRepository

class BoardRepositoryImpl @Inject constructor(
    private val apiService: IdeasApiService,
    private val authRepository: AuthRepository
) : BoardRepository {

    private var currentTaskAssigneeSource: PagingSource<Int, User>? = null

    override suspend fun getMyTasks(): List<TaskSummary> {
        val token = authRepository.getValidToken()
        return apiService.getMyTasks(token).map { it.toEntity() }
    }

    override suspend fun createColumn(noteId: String, title: String, wipLimit: Int?): BoardColumn {
        val token = authRepository.getValidToken()
        return apiService.createColumn(token, noteId, CreateColumnRequest(title, wipLimit)).toEntity()
    }

    override suspend fun updateColumn(
        noteId: String,
        columnId: Long,
        title: String?,
        wipLimit: Int?,
        archived: Boolean?
    ): BoardColumn {
        val token = authRepository.getValidToken()
        return apiService.updateColumn(
            token, noteId, columnId,
            UpdateColumnRequest(title, wipLimit, archived)
        ).toEntity()
    }

    override suspend fun deleteColumn(noteId: String, columnId: Long) {
        val token = authRepository.getValidToken()
        apiService.deleteColumn(token, noteId, columnId)
    }

    override suspend fun reorderColumns(noteId: String, orderedIds: List<Long>) {
        val token = authRepository.getValidToken()
        apiService.reorderColumns(token, noteId, ReorderColumnsRequest(orderedIds))
    }

    override suspend fun createTask(
        noteId: String,
        columnId: Long,
        title: String,
        description: String?,
        priority: String?,
        startDate: Long?,
        dueDate: Long?
    ): BoardTask {
        val token = authRepository.getValidToken()
        return apiService.createTask(
            token, noteId, columnId,
            CreateTaskRequest(title, description, priority, startDate, dueDate)
        ).toEntity()
    }

    override suspend fun getTaskDetail(noteId: String, taskId: String): TaskDetail {
        val token = authRepository.getValidToken()
        return apiService.getTaskDetail(token, noteId, taskId).toEntity()
    }

    override suspend fun updateTask(
        noteId: String,
        taskId: String,
        title: String?,
        description: String?,
        priority: String?,
        startDate: Long?,
        dueDate: Long?
    ): BoardTask {
        val token = authRepository.getValidToken()
        return apiService.updateTask(
            token, noteId, taskId,
            UpdateTaskRequest(title, description, priority, startDate, dueDate)
        ).toEntity()
    }

    override suspend fun deleteTask(noteId: String, taskId: String) {
        val token = authRepository.getValidToken()
        apiService.deleteTask(token, noteId, taskId)
    }

    override suspend fun moveTask(
        noteId: String,
        taskId: String,
        targetColumnId: Long,
        position: Int
    ): BoardTask {
        val token = authRepository.getValidToken()
        return apiService.moveTask(
            token, noteId, taskId,
            MoveTaskRequest(targetColumnId, position)
        ).toEntity()
    }

    override suspend fun completeTask(noteId: String, taskId: String): BoardTask {
        val token = authRepository.getValidToken()
        return apiService.completeTask(token, noteId, taskId).toEntity()
    }

    override suspend fun reopenTask(noteId: String, taskId: String): BoardTask {
        val token = authRepository.getValidToken()
        return apiService.reopenTask(token, noteId, taskId).toEntity()
    }

    override suspend fun reorderTasks(noteId: String, columnId: Long, orderedIds: List<String>) {
        val token = authRepository.getValidToken()
        apiService.reorderTasks(token, noteId, columnId, ReorderTasksRequest(orderedIds))
    }

    override suspend fun getBoardLabels(noteId: String): List<TaskLabel> {
        val token = authRepository.getValidToken()
        return apiService.getBoardLabels(token, noteId).map { it.toEntity() }
    }

    override suspend fun createLabel(noteId: String, name: String, color: String): TaskLabel {
        val token = authRepository.getValidToken()
        return apiService.createLabel(token, noteId, CreateLabelRequest(name, color)).toEntity()
    }

    override suspend fun deleteLabel(noteId: String, labelId: Long) {
        val token = authRepository.getValidToken()
        apiService.deleteLabel(token, noteId, labelId)
    }

    override suspend fun addLabelToTask(noteId: String, taskId: String, labelId: Long) {
        val token = authRepository.getValidToken()
        apiService.addLabelToTask(token, noteId, taskId, labelId)
    }

    override suspend fun removeLabelFromTask(noteId: String, taskId: String, labelId: Long) {
        val token = authRepository.getValidToken()
        apiService.removeLabelFromTask(token, noteId, taskId, labelId)
    }

    override fun getBoardAssignees(noteId: String, taskId: String): Flow<PagingData<User>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                GenericPagingSource(
                    fetch = { page, size ->
                        val token = authRepository.getValidToken()
                        apiService.getBoardAssignees(token, noteId, taskId, page, size)
                    },
                    mapper = UserDto::toEntity
                ).also { currentTaskAssigneeSource = it }
            }
        ).flow
    }

    override suspend fun addTaskAssignee(noteId: String, taskId: String, userId: String) {
        val token = authRepository.getValidToken()
        apiService.addTaskAssignee(token, noteId, taskId, AddTaskAssigneeRequest(userId))
        currentTaskAssigneeSource?.invalidate()
    }

    override suspend fun removeTaskAssignee(noteId: String, taskId: String, userId: String) {
        val token = authRepository.getValidToken()
        apiService.removeTaskAssignee(token, noteId, taskId, userId)
        currentTaskAssigneeSource?.invalidate()
    }

    override suspend fun addTaskAttachment(
        noteId: String,
        taskId: String,
        type: String,
        title: String?,
        content: String
    ): TaskAttachment {
        val token = authRepository.getValidToken()
        return apiService.addTaskAttachment(
            token, noteId, taskId,
            AddAttachmentRequest(type, title, content)
        ).toEntity()
    }

    override suspend fun deleteTaskAttachment(noteId: String, taskId: String, attachmentId: Long) {
        val token = authRepository.getValidToken()
        apiService.deleteTaskAttachment(token, noteId, taskId, attachmentId)
    }

}
