package ru.shrprnbw.ideas.presentation.screens.my_tasks

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.shrprnbw.ideas.R
import ru.shrprnbw.ideas.domain.entity.TaskDetail
import ru.shrprnbw.ideas.domain.entity.TaskPriority
import ru.shrprnbw.ideas.domain.entity.TaskSummary
import ru.shrprnbw.ideas.domain.usecase.CompleteBoardTaskUseCase
import ru.shrprnbw.ideas.domain.usecase.GetMyTasksUseCase
import ru.shrprnbw.ideas.domain.usecase.GetTaskDetailUseCase
import ru.shrprnbw.ideas.domain.usecase.UpdateBoardTaskUseCase
import javax.inject.Inject

@HiltViewModel
class MyTasksViewModel @Inject constructor(
    private val getMyTasksUseCase: GetMyTasksUseCase,
    private val completeBoardTaskUseCase: CompleteBoardTaskUseCase,
    private val updateBoardTaskUseCase: UpdateBoardTaskUseCase,
    private val getTaskDetailUseCase: GetTaskDetailUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<MyTasksState>(MyTasksState.Loading)
    val state = _state.asStateFlow()

    init {
        loadTasks()
    }

    fun processCommand(command: MyTasksCommand) {
        when (command) {
            is MyTasksCommand.Refresh -> loadTasks()

            is MyTasksCommand.ShowTaskDetail -> {
                viewModelScope.launch {
                    _state.update { currentState ->
                        if (currentState is MyTasksState.Content) {
                            try {
                                val detail = getTaskDetailUseCase(command.boardNoteId, command.taskId)
                                currentState.copy(
                                    taskDetail = detail,
                                    taskDetailBoardNoteId = command.boardNoteId
                                )
                            } catch (e: Exception) {
                                Log.d("MyTasksViewModel", "Load task detail error: ${e.message}")
                                currentState.copy(error = R.string.my_tasks_error_load)
                            }
                        } else {
                            currentState
                        }
                    }
                }
            }

            is MyTasksCommand.DismissTaskDetail -> {
                _state.update { currentState ->
                    if (currentState is MyTasksState.Content) {
                        currentState.copy(
                            taskDetail = null,
                            taskDetailBoardNoteId = null
                        )
                    } else {
                        currentState
                    }
                }
            }

            is MyTasksCommand.CompleteTask -> {
                viewModelScope.launch {
                    _state.update { currentState ->
                        if (currentState is MyTasksState.Content) {
                            try {
                                completeBoardTaskUseCase(command.boardNoteId, command.taskId)
                                currentState.copy(
                                    tasks = currentState.tasks.filter { it.taskId != command.taskId },
                                    taskDetail = null,
                                    taskDetailBoardNoteId = null
                                )
                            } catch (e: Exception) {
                                Log.d("MyTasksViewModel", "Complete task error: ${e.message}")
                                currentState.copy(error = R.string.my_tasks_error_complete)
                            }
                        } else {
                            currentState
                        }
                    }
                }
            }

            is MyTasksCommand.UpdateTask -> {
                viewModelScope.launch {
                    _state.update { currentState ->
                        if (currentState is MyTasksState.Content) {
                            try {
                                updateBoardTaskUseCase(
                                    noteId = command.boardNoteId,
                                    taskId = command.taskId,
                                    title = command.title,
                                    description = command.description,
                                    priority = command.priority,
                                    startDate = command.startDate,
                                    dueDate = command.dueDate
                                )
                                return@update MyTasksState.Content(
                                    tasks = currentState.tasks.map { updatedTask ->
                                        if (updatedTask.taskId == command.taskId) {
                                            updatedTask.copy(
                                                taskTitle = command.title ?: updatedTask.taskTitle,
                                                priority = command.priority?.let {
                                                    TaskPriority.fromString(it)
                                                } ?: updatedTask.priority,
                                                startDate = command.startDate ?: updatedTask.startDate,
                                                dueDate = command.dueDate ?: updatedTask.dueDate
                                            )
                                        } else {
                                            updatedTask
                                        }
                                    }
                                )
                            } catch (e: Exception) {
                                Log.d("MyTasksViewModel", "Update task error: ${e.message}")
                                currentState.copy(error = R.string.my_tasks_error_update)
                            }
                        } else {
                            currentState
                        }
                    }
                }
            }

            is MyTasksCommand.ClearError -> {
                _state.update { prev ->
                    (prev as? MyTasksState.Content)?.copy(error = null) ?: prev
                }
            }
        }
    }

    private fun loadTasks() {
        viewModelScope.launch {
            val previousTasks = (_state.value as? MyTasksState.Content)?.tasks
            if (previousTasks == null) {
                _state.value = MyTasksState.Loading
            }
            try {
                val tasks = getMyTasksUseCase()
                _state.value = MyTasksState.Content(
                    tasks = tasks,
                    isRefreshing = false
                )
            } catch (e: Exception) {
                Log.d("MyTasksViewModel", "Load tasks error: ${e.message}")
                if (previousTasks != null) {
                    _state.update { prev ->
                        (prev as? MyTasksState.Content)?.copy(
                            error = R.string.my_tasks_error_load,
                            isRefreshing = false
                        ) ?: prev
                    }
                } else {
                    _state.value = MyTasksState.Error(R.string.my_tasks_error_load)
                }
            }
        }
    }
}

sealed interface MyTasksState {
    data object Loading : MyTasksState

    data class Content(
        val tasks: List<TaskSummary>,
        val isRefreshing: Boolean = false,
        val taskDetail: TaskDetail? = null,
        val taskDetailBoardNoteId: String? = null,
        val error: Int? = null
    ) : MyTasksState

    data class Error(val message: Int) : MyTasksState
}

sealed interface MyTasksCommand {
    data object Refresh : MyTasksCommand
    data class ShowTaskDetail(val taskId: String, val boardNoteId: String) : MyTasksCommand
    data object DismissTaskDetail : MyTasksCommand
    data class CompleteTask(val taskId: String, val boardNoteId: String) : MyTasksCommand
    data class UpdateTask(
        val taskId: String,
        val boardNoteId: String,
        val title: String? = null,
        val description: String? = null,
        val priority: String? = null,
        val startDate: Long? = null,
        val dueDate: Long? = null
    ) : MyTasksCommand
    data object ClearError : MyTasksCommand
}
