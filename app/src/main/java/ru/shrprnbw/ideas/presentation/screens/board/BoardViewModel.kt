package ru.shrprnbw.ideas.presentation.screens.board

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.shrprnbw.ideas.R
import ru.shrprnbw.ideas.domain.entity.AccessType
import ru.shrprnbw.ideas.domain.entity.Group
import ru.shrprnbw.ideas.domain.entity.Note
import ru.shrprnbw.ideas.domain.entity.TaskDetail
import ru.shrprnbw.ideas.domain.entity.TaskLabel
import ru.shrprnbw.ideas.domain.entity.boardColumns
import ru.shrprnbw.ideas.domain.usecase.AddLabelToTaskUseCase
import ru.shrprnbw.ideas.domain.usecase.AddNoteCollaboratorUseCase
import ru.shrprnbw.ideas.domain.usecase.AddNoteToGroupUseCase
import ru.shrprnbw.ideas.domain.usecase.AddTaskAssigneeUseCase
import ru.shrprnbw.ideas.domain.usecase.AddTaskAttachmentUseCase
import ru.shrprnbw.ideas.domain.usecase.CompleteBoardTaskUseCase
import ru.shrprnbw.ideas.domain.usecase.CreateBoardColumnUseCase
import ru.shrprnbw.ideas.domain.usecase.CreateBoardLabelUseCase
import ru.shrprnbw.ideas.domain.usecase.CreateBoardTaskUseCase
import ru.shrprnbw.ideas.domain.usecase.DeleteBoardColumnUseCase
import ru.shrprnbw.ideas.domain.usecase.DeleteBoardLabelUseCase
import ru.shrprnbw.ideas.domain.usecase.DeleteBoardTaskUseCase
import ru.shrprnbw.ideas.domain.usecase.DeleteNoteUseCase
import ru.shrprnbw.ideas.domain.usecase.DeleteTaskAttachmentUseCase
import ru.shrprnbw.ideas.domain.usecase.GetBoardAssigneesUseCase
import ru.shrprnbw.ideas.domain.usecase.GetBoardLabelsUseCase
import ru.shrprnbw.ideas.domain.usecase.GetNoteInfoUseCase
import ru.shrprnbw.ideas.domain.usecase.GetNotesForGroupsUseCase
import ru.shrprnbw.ideas.domain.usecase.GetOwnedGroupsUseCase
import ru.shrprnbw.ideas.domain.usecase.GetTaskDetailUseCase
import ru.shrprnbw.ideas.domain.usecase.MoveBoardTaskUseCase
import ru.shrprnbw.ideas.domain.usecase.RemoveLabelFromTaskUseCase
import ru.shrprnbw.ideas.domain.usecase.RemoveNoteCollaboratorUseCase
import ru.shrprnbw.ideas.domain.usecase.RemoveNoteFromGroupUseCase
import ru.shrprnbw.ideas.domain.usecase.RemoveTaskAssigneeUseCase
import ru.shrprnbw.ideas.domain.usecase.ReopenBoardTaskUseCase
import ru.shrprnbw.ideas.domain.usecase.ReorderColumnsUseCase
import ru.shrprnbw.ideas.domain.usecase.ReorderTasksUseCase
import ru.shrprnbw.ideas.domain.usecase.UpdateBoardColumnUseCase
import ru.shrprnbw.ideas.domain.usecase.UpdateBoardTaskUseCase
import ru.shrprnbw.ideas.domain.usecase.UpdateNoteUseCase

@HiltViewModel(assistedFactory = BoardViewModel.Factory::class)
class BoardViewModel @AssistedInject constructor(
    @Assisted("noteId") private val noteId: String,
    private val createColumnUseCase: CreateBoardColumnUseCase,
    private val updateColumnUseCase: UpdateBoardColumnUseCase,
    private val deleteColumnUseCase: DeleteBoardColumnUseCase,
    private val createTaskUseCase: CreateBoardTaskUseCase,
    private val updateTaskUseCase: UpdateBoardTaskUseCase,
    private val deleteTaskUseCase: DeleteBoardTaskUseCase,
    private val moveTaskUseCase: MoveBoardTaskUseCase,
    private val completeTaskUseCase: CompleteBoardTaskUseCase,
    private val reopenTaskUseCase: ReopenBoardTaskUseCase,
    private val getTaskDetailUseCase: GetTaskDetailUseCase,
    private val getBoardAssigneesUseCase: GetBoardAssigneesUseCase,
    private val getBoardLabelsUseCase: GetBoardLabelsUseCase,
    private val createBoardLabelUseCase: CreateBoardLabelUseCase,
    private val deleteBoardLabelUseCase: DeleteBoardLabelUseCase,
    private val addLabelToTaskUseCase: AddLabelToTaskUseCase,
    private val removeLabelFromTaskUseCase: RemoveLabelFromTaskUseCase,
    private val addTaskAssigneeUseCase: AddTaskAssigneeUseCase,
    private val removeTaskAssigneeUseCase: RemoveTaskAssigneeUseCase,
    private val addTaskAttachmentUseCase: AddTaskAttachmentUseCase,
    private val deleteTaskAttachmentUseCase: DeleteTaskAttachmentUseCase,
    private val reorderColumnsUseCase: ReorderColumnsUseCase,
    private val reorderTasksUseCase: ReorderTasksUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val getNoteInfoUseCase: GetNoteInfoUseCase,
    private val addNoteCollaboratorUseCase: AddNoteCollaboratorUseCase,
    private val removeNoteCollaboratorUseCase: RemoveNoteCollaboratorUseCase,
    private val addNoteToGroupUseCase: AddNoteToGroupUseCase,
    private val removeNoteFromGroupUseCase: RemoveNoteFromGroupUseCase,
    private val getOwnedGroupsUseCase: GetOwnedGroupsUseCase,
    private val updateNoteUseCase: UpdateNoteUseCase,
    private val getNotesForGroupsUseCase: GetNotesForGroupsUseCase
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(@Assisted("noteId") noteId: String): BoardViewModel
    }

    private val _state = MutableStateFlow<BoardState>(BoardState.Loading)
    val state = _state.asStateFlow()

    val availableAssignees = _state.filterIsInstance(BoardState.Editing::class).filter {
        it.showAddAssignee
    }.distinctUntilChanged().mapNotNull {
        it.taskDetail?.id
    }.flatMapLatest { taskId ->
        getBoardAssigneesUseCase(noteId, taskId)
    }.cachedIn(viewModelScope)

    private val _taskDetailState = MutableStateFlow<TaskDetailState?>(null)
    val taskDetailState: StateFlow<TaskDetailState?> = _taskDetailState.asStateFlow()

    private val _createTaskState = MutableStateFlow<CreateTaskState?>(null)
    val createTaskState: StateFlow<CreateTaskState?> = _createTaskState.asStateFlow()

    private val _addAttachmentState = MutableStateFlow<AddAttachmentState?>(null)
    val addAttachmentState: StateFlow<AddAttachmentState?> = _addAttachmentState.asStateFlow()

    private val _labelManagementState = MutableStateFlow<LabelManagementState?>(null)
    val labelManagementState: StateFlow<LabelManagementState?> = _labelManagementState.asStateFlow()

    init {
        loadBoard()
    }

    fun processCommand(command: BoardCommand) {
        when (command) {
            is BoardCommand.Refresh -> loadBoard()

            is BoardCommand.ConfirmCreateColumn -> confirmCreateColumn()

            is BoardCommand.ConfirmUpdateColumn -> confirmUpdateColumn()

            is BoardCommand.ConfirmDeleteColumn -> confirmDeleteColumn()

            is BoardCommand.ConfirmDeleteTask -> confirmDeleteTask()

            is BoardCommand.ConfirmMoveTask -> confirmMoveTask(command.targetColumnId)

            is BoardCommand.CompleteTask -> completeTask(command.taskId)

            is BoardCommand.ReopenTask -> reopenTask(command.taskId)

            is BoardCommand.ShowTaskDetail -> loadTaskDetail(command.taskId)

            is BoardCommand.ShowAddColumnDialog -> {
                updateEditingState { it.copy(showAddColumnDialog = command.show, inputText = "") }
            }

            is BoardCommand.ShowAddTaskDialog -> {
                if (command.show && command.columnId != null) {
                    _createTaskState.value = CreateTaskState(
                        columnId = command.columnId,
                        title = "",
                        description = "",
                        priority = null,
                        startDate = null,
                        dueDate = null
                    )
                } else {
                    _createTaskState.value = null
                }
            }

            is BoardCommand.ShowDeleteColumnDialog -> {
                updateEditingState {
                    it.copy(
                        showDeleteColumnDialog = command.show,
                        deleteColumnId = command.columnId
                    )
                }
            }

            is BoardCommand.ShowDeleteTaskDialog -> {
                updateEditingState {
                    it.copy(
                        showDeleteTaskDialog = command.show,
                        deleteTaskId = command.taskId
                    )
                }
            }

            is BoardCommand.ShowMoveTaskDialog -> {
                updateEditingState {
                    it.copy(
                        showMoveTaskDialog = command.show,
                        moveTaskId = command.taskId
                    )
                }
            }

            is BoardCommand.ShowEditColumnDialog -> {
                updateEditingState {
                    it.copy(
                        showEditColumnDialog = command.show,
                        editColumnId = command.columnId,
                        inputText = command.currentTitle,
                        editColumnWipLimit = command.currentWipLimit?.toString() ?: ""
                    )
                }
            }

            is BoardCommand.InputText -> {
                updateEditingState { it.copy(inputText = command.text) }
            }

            is BoardCommand.InputEditColumnWipLimit -> {
                updateEditingState { it.copy(editColumnWipLimit = command.value) }
            }

            is BoardCommand.ClearError -> {
                updateEditingState { it.copy(error = null) }
            }

            // Note-level actions
            is BoardCommand.ShowDeleteNoteDialog -> {
                updateEditingState { it.copy(showDeleteNoteDialog = true) }
            }

            is BoardCommand.DismissDeleteNoteDialog -> {
                updateEditingState { it.copy(showDeleteNoteDialog = false) }
            }

            is BoardCommand.ConfirmDeleteNote -> deleteNote()

            is BoardCommand.ShowRenameBoardDialog -> {
                val currentTitle = (_state.value as? BoardState.Editing)?.note?.title ?: ""
                updateEditingState {
                    it.copy(showRenameBoardDialog = true, inputText = currentTitle)
                }
            }

            is BoardCommand.DismissRenameBoardDialog -> {
                updateEditingState { it.copy(showRenameBoardDialog = false, inputText = "") }
            }

            is BoardCommand.ConfirmRenameBoard -> renameBoard()

            is BoardCommand.OpenShareSheet -> openShareSheet()

            is BoardCommand.CloseShareSheet -> {
                updateEditingState {
                    it.copy(
                        showShareSheet = false,
                        collaboratorEmail = "",
                        shareError = null
                    )
                }
            }

            is BoardCommand.InputCollaboratorEmail -> {
                updateEditingState {
                    it.copy(collaboratorEmail = command.email, shareError = null)
                }
            }

            is BoardCommand.AddCollaboratorByEmail -> addCollaborator(command.email)

            is BoardCommand.RemoveCollaborator -> removeCollaborator(command.collaboratorId)

            is BoardCommand.AddToGroup -> addToGroup(command.groupId)

            is BoardCommand.RemoveFromGroup -> removeFromGroup(command.groupId)

            is BoardCommand.ShowLabelManagementSheet -> {
                val editing = _state.value as? BoardState.Editing
                _labelManagementState.value = LabelManagementState(
                    labels = editing?.boardLabels ?: emptyList(),
                    newLabelName = "",
                    newLabelColor = "#4CAF50"
                )
                loadBoardLabels()
            }

            is BoardCommand.MoveColumnLeft -> moveColumnLeft(command.columnId)

            is BoardCommand.MoveColumnRight -> moveColumnRight(command.columnId)

            is BoardCommand.MoveTaskUp -> moveTaskUp(command.taskId, command.columnId)

            is BoardCommand.MoveTaskDown -> moveTaskDown(command.taskId, command.columnId)
        }
    }

    fun onTaskDetailAction(action: TaskDetailAction) {
        when (action) {
            TaskDetailAction.Dismiss -> {
                updateEditingState { it.copy(taskDetail = null) }
                _taskDetailState.value = null
            }
            TaskDetailAction.SaveEdits -> saveTaskEdits()
            is TaskDetailAction.EditTitle -> _taskDetailState.update { it?.copy(editedTitle = action.title) }
            is TaskDetailAction.EditDescription -> _taskDetailState.update { it?.copy(editedDescription = action.description) }
            is TaskDetailAction.EditPriority -> _taskDetailState.update { it?.copy(editedPriority = action.priority) }
            is TaskDetailAction.EditStartDate -> _taskDetailState.update { it?.copy(editedStartDate = action.date) }
            is TaskDetailAction.EditDueDate -> _taskDetailState.update { it?.copy(editedDueDate = action.date) }
            is TaskDetailAction.CompleteTask -> completeTask(action.taskId)
            is TaskDetailAction.ReopenTask -> reopenTask(action.taskId)
            is TaskDetailAction.DeleteTask -> processCommand(BoardCommand.ShowDeleteTaskDialog(true, action.taskId))
            is TaskDetailAction.MoveTask -> processCommand(BoardCommand.ShowMoveTaskDialog(true, action.taskId))
            is TaskDetailAction.AddLabel -> addLabelToTask(action.labelId)
            is TaskDetailAction.RemoveLabel -> removeLabelFromTask(action.labelId)
            TaskDetailAction.ShowAddAssignee -> {
                _state.update {
                    if (it is BoardState.Editing) {
                        it.copy(showAddAssignee = true)
                    } else {
                        it
                    }
                }
            }
            is TaskDetailAction.RemoveAssignee -> removeAssigneeFromTask(action.userId)
            TaskDetailAction.ShowAddAttachment -> {
                _addAttachmentState.value = AddAttachmentState(
                    type = "TEXT",
                    title = "",
                    content = "",
                    availableNotes = emptyList(),
                    isLoadingNotes = false,
                    selectedNoteRefId = null
                )
            }
            is TaskDetailAction.DeleteAttachment -> deleteAttachment(action.attachmentId)
        }
    }

    fun onCreateTaskAction(action: CreateTaskAction) {
        when (action) {
            is CreateTaskAction.EditTitle -> _createTaskState.update { it?.copy(title = action.title) }
            is CreateTaskAction.EditDescription -> _createTaskState.update { it?.copy(description = action.description) }
            is CreateTaskAction.EditPriority -> _createTaskState.update { it?.copy(priority = action.priority) }
            is CreateTaskAction.EditStartDate -> _createTaskState.update { it?.copy(startDate = action.date) }
            is CreateTaskAction.EditDueDate -> _createTaskState.update { it?.copy(dueDate = action.date) }
            CreateTaskAction.Dismiss -> _createTaskState.value = null
            CreateTaskAction.Confirm -> confirmCreateTask()
        }
    }

    fun onAddAttachmentAction(action: AddAttachmentAction) {
        when (action) {
            is AddAttachmentAction.EditType -> {
                _addAttachmentState.update {
                    it?.copy(type = action.type, content = "", selectedNoteRefId = null)
                }
                if (action.type == "NOTE_REFERENCE") loadAvailableNotes()
            }
            is AddAttachmentAction.EditTitle -> _addAttachmentState.update { it?.copy(title = action.title) }
            is AddAttachmentAction.EditContent -> _addAttachmentState.update { it?.copy(content = action.content) }
            is AddAttachmentAction.SelectNoteReference -> {
                _addAttachmentState.update {
                    it?.copy(
                        selectedNoteRefId = action.noteId,
                        content = "${action.noteId}|${action.noteType}",
                        title = action.noteTitle
                    )
                }
            }
            AddAttachmentAction.Dismiss -> _addAttachmentState.value = null
            AddAttachmentAction.Confirm -> confirmAddAttachment()
        }
    }

    fun onAddAssigneeAction(action: AddAssigneeAction) {
        when (action) {
            is AddAssigneeAction.Add -> addAssigneeToTask(action.userId)
            AddAssigneeAction.Dismiss -> _state.update {
                if (it is BoardState.Editing) {
                    it.copy(showAddAssignee = false)
                } else {
                    it
                }
            }
        }
    }

    fun onLabelManagementAction(action: LabelManagementAction) {
        when (action) {
            is LabelManagementAction.EditName -> _labelManagementState.update { it?.copy(newLabelName = action.name) }
            is LabelManagementAction.EditColor -> _labelManagementState.update { it?.copy(newLabelColor = action.color) }
            is LabelManagementAction.CreateLabel -> createLabel(action.name, action.color)
            is LabelManagementAction.DeleteLabel -> deleteLabel(action.labelId)
            LabelManagementAction.Dismiss -> _labelManagementState.value = null
        }
    }

    private fun loadBoard() {
        viewModelScope.launch {
            _state.value = BoardState.Loading
            try {
                val note = getNoteInfoUseCase(noteId)
                _state.value = BoardState.Editing(note = note)
            } catch (e: Exception) {
                _state.value = BoardState.Error(e.message ?: "Unknown error")
            }
        }
    }

    private fun confirmCreateColumn() {
        val currentState = _state.value as? BoardState.Editing ?: return
        val title = currentState.inputText.trim()
        if (title.isBlank()) return
        _state.update {
            currentState.copy(showAddColumnDialog = false, inputText = "")
        }
        viewModelScope.launch {
            try {
                createColumnUseCase(noteId, title)// TODO: Use return value
                reloadBoard()
            } catch (e: Exception) {
                updateEditingState { it.copy(error = "Unable to create column") }
            }
        }
    }

    private fun confirmUpdateColumn() {
        val currentState = _state.value as? BoardState.Editing ?: return
        val columnId = currentState.editColumnId ?: return
        val title = currentState.inputText.trim()
        if (title.isBlank()) return
        val wipLimit = currentState.editColumnWipLimit.toIntOrNull()
        updateEditingState {
            it.copy(showEditColumnDialog = false, inputText = "")
        }
        viewModelScope.launch {
            try {
                updateColumnUseCase(noteId, columnId, title, wipLimit, null)
                reloadBoard()
            } catch (e: Exception) {
                updateEditingState { it.copy(error = "Unable to update column") }
            }
        }
    }

    private fun confirmDeleteColumn() {
        val currentState = _state.value as? BoardState.Editing ?: return
        val columnId = currentState.deleteColumnId ?: return
        updateEditingState {
            it.copy(showDeleteColumnDialog = false, deleteColumnId = null)
        }
        viewModelScope.launch {
            try {
                deleteColumnUseCase(noteId, columnId)
                reloadBoard()
            } catch (e: Exception) {
                updateEditingState { it.copy(error = "Unable to delete column") }
            }
        }
    }

    private fun confirmCreateTask() {
        val createState = _createTaskState.value ?: return
        val title = createState.title.trim()
        if (title.isBlank()) return
        val description = createState.description.trim().takeIf { it.isNotEmpty() }
        val priority = createState.priority?.name
        val startDate = createState.startDate
        val dueDate = createState.dueDate
        val columnId = createState.columnId
        _createTaskState.value = null
        viewModelScope.launch {
            try {
                createTaskUseCase(noteId, columnId, title, description, priority, startDate, dueDate) // TODO: Use return value
                reloadBoard()
            } catch (e: Exception) {
                updateEditingState { it.copy(error = "Unable to create task") }
            }
        }
    }

    private fun saveTaskEdits() {
        val currentState = _state.value as? BoardState.Editing ?: return
        val taskDetail = currentState.taskDetail ?: return
        val editState = _taskDetailState.value ?: return
        val taskId = taskDetail.id
        val newTitle = editState.editedTitle.trim().takeIf { it != taskDetail.title }
        val newDesc = editState.editedDescription.trim().let { d ->
            if (d != (taskDetail.description ?: "")) d else null
        }
        val newPriority = editState.editedPriority.takeIf { it != taskDetail.priority }?.name
        val startDateChanged = editState.editedStartDate != taskDetail.startDate
        val dueDateChanged = editState.editedDueDate != taskDetail.dueDate
        if (newTitle != null || newDesc != null || newPriority != null || startDateChanged || dueDateChanged) {
            viewModelScope.launch {
                try {
                    updateTaskUseCase(
                        noteId, taskId, newTitle, newDesc, newPriority,
                        if (startDateChanged) editState.editedStartDate else null,
                        if (dueDateChanged) editState.editedDueDate else null
                    )// TODO: Use return value
                    reloadBoard()
                    reloadTaskDetailIfOpen(taskId)
                } catch (e: Exception) {
                    updateEditingState { it.copy(error = "Unable to update task") }
                }
            }
        }
    }

    private fun confirmDeleteTask() {
        val currentState = _state.value as? BoardState.Editing ?: return
        val taskId = currentState.deleteTaskId ?: return
        updateEditingState {
            it.copy(
                showDeleteTaskDialog = false,
                deleteTaskId = null,
                taskDetail = if (it.taskDetail?.id == taskId) null else it.taskDetail
            )
        }
        if (_taskDetailState.value?.taskDetail?.id == taskId) {
            _taskDetailState.value = null
        }
        viewModelScope.launch {
            try {
                deleteTaskUseCase(noteId, taskId)
                reloadBoard()
            } catch (e: Exception) {
                updateEditingState { it.copy(error = "Unable to delete task") }
            }
        }
    }

    private fun confirmMoveTask(targetColumnId: Long) {
        val currentState = _state.value as? BoardState.Editing ?: return
        val taskId = currentState.moveTaskId ?: return
        updateEditingState {
            it.copy(showMoveTaskDialog = false, moveTaskId = null)
        }
        viewModelScope.launch {
            try {
                moveTaskUseCase(noteId, taskId, targetColumnId, 0) // TODO: Use return value
                reloadBoard()
            } catch (e: Exception) {
                updateEditingState { it.copy(error = "Unable to move task") }
            }
        }
    }

    private fun completeTask(taskId: String) {
        viewModelScope.launch {
            try {
                completeTaskUseCase(noteId, taskId)// TODO: Use return value
                reloadBoard()
                reloadTaskDetailIfOpen(taskId)
            } catch (e: Exception) {
                updateEditingState { it.copy(error = "Unable to complete task") }
            }
        }
    }

    private fun reopenTask(taskId: String) {
        viewModelScope.launch {
            try {
                reopenTaskUseCase(noteId, taskId)// TODO: Use return value
                reloadBoard()
                reloadTaskDetailIfOpen(taskId)
            } catch (e: Exception) {
                updateEditingState { it.copy(error = "Unable to reopen task") }
            }
        }
    }

    private fun loadTaskDetail(taskId: String) {
        viewModelScope.launch {
            try {
                val detail = getTaskDetailUseCase(noteId, taskId)
                updateEditingState { it.copy(taskDetail = detail) }
                val editing = _state.value as? BoardState.Editing ?: return@launch
                _taskDetailState.value = TaskDetailState(
                    taskDetail = detail,
                    canEditBoard = editing.note.accessType != AccessType.VIEWER,
                    boardLabels = editing.boardLabels,
                    columns = editing.note.boardColumns,
                    editedTitle = detail.title,
                    editedDescription = detail.description ?: "",
                    editedPriority = detail.priority,
                    editedStartDate = detail.startDate,
                    editedDueDate = detail.dueDate
                )
                loadBoardLabels()
            } catch (e: Exception) {
                updateEditingState { it.copy(error = "Unable to load task details") }
            }
        }
    }

    private fun deleteNote() {
        viewModelScope.launch {
            try {
                deleteNoteUseCase(noteId)
                _state.value = BoardState.Finished
            } catch (e: Exception) {
                Log.d("BoardViewModel", "Delete note error: ${e.message}")
                updateEditingState {
                    it.copy(showDeleteNoteDialog = false, error = "Unable to delete board")
                }
            }
        }
    }

    private fun renameBoard() {
        val currentState = _state.value as? BoardState.Editing ?: return
        val title = currentState.inputText.trim()
        if (title.isBlank()) return
        updateEditingState { it.copy(showRenameBoardDialog = false, inputText = "") }
        viewModelScope.launch {
            try {
                val updatedNote = currentState.note.copy(title = title, noteUpdated = true)
                updateNoteUseCase(updatedNote)
                updateEditingState { it.copy(note = it.note.copy(title = title)) }
            } catch (e: Exception) {
                updateEditingState { it.copy(error = "Unable to rename board") }
            }
        }
    }

    private fun openShareSheet() {
        viewModelScope.launch {
            updateEditingState {
                it.copy(showShareSheet = true, isShareLoading = true, shareError = null)
            }
            try {
                val note = getNoteInfoUseCase(noteId)
                updateEditingState { it.copy(note = note) }
                getOwnedGroupsUseCase().collect { groups ->
                    updateEditingState {
                        it.copy(availableGroups = groups, isShareLoading = false)
                    }
                }
            } catch (e: Exception) {
                Log.d("BoardViewModel", "Open share sheet error: ${e.message}")
                updateEditingState { it.copy(isShareLoading = false) }
            }
        }
    }

    private fun addCollaborator(email: String) {
        viewModelScope.launch {
            updateEditingState { it.copy(isShareLoading = true) }
            try {
                addNoteCollaboratorUseCase(noteId, email)
                val note = getNoteInfoUseCase(noteId)
                updateEditingState {
                    it.copy(
                        note = note,
                        collaboratorEmail = "",
                        isShareLoading = false,
                        shareError = null
                    )
                }
            } catch (e: Exception) {
                Log.d("BoardViewModel", "Add collaborator error: ${e.message}")
                updateEditingState {
                    it.copy(
                        shareError = parseAddCollaboratorError(e.message),
                        isShareLoading = false
                    )
                }
            }
        }
    }

    private fun removeCollaborator(collaboratorId: String) {
        viewModelScope.launch {
            updateEditingState { it.copy(isShareLoading = true) }
            try {
                removeNoteCollaboratorUseCase(noteId, collaboratorId)
                val note = getNoteInfoUseCase(noteId)
                updateEditingState {
                    it.copy(note = note, isShareLoading = false, shareError = null)
                }
            } catch (e: Exception) {
                Log.d("BoardViewModel", "Remove collaborator error: ${e.message}")
                updateEditingState {
                    it.copy(
                        shareError = R.string.share_error_remove_collaborator,
                        isShareLoading = false
                    )
                }
            }
        }
    }

    private fun addToGroup(groupId: Long) {
        viewModelScope.launch {
            updateEditingState { it.copy(isShareLoading = true) }
            try {
                addNoteToGroupUseCase(noteId, groupId)
                val note = getNoteInfoUseCase(noteId)
                updateEditingState {
                    it.copy(note = note, isShareLoading = false, shareError = null)
                }
            } catch (e: Exception) {
                Log.d("BoardViewModel", "Add to group error: ${e.message}")
                updateEditingState {
                    it.copy(
                        shareError = R.string.share_error_add_to_group,
                        isShareLoading = false
                    )
                }
            }
        }
    }

    private fun removeFromGroup(groupId: Long) {
        viewModelScope.launch {
            updateEditingState { it.copy(isShareLoading = true) }
            try {
                removeNoteFromGroupUseCase(noteId, groupId)
                val note = getNoteInfoUseCase(noteId)
                updateEditingState {
                    it.copy(note = note, isShareLoading = false, shareError = null)
                }
            } catch (e: Exception) {
                Log.d("BoardViewModel", "Remove from group error: ${e.message}")
                updateEditingState {
                    it.copy(
                        shareError = R.string.share_error_remove_from_group,
                        isShareLoading = false
                    )
                }
            }
        }
    }

    private fun parseAddCollaboratorError(message: String?): Int {
        return when {
            message?.contains("404") == true -> R.string.share_error_collaborator_not_found
            message?.contains("409") == true -> R.string.share_error_collaborator_already_added
            else -> R.string.share_error_add_collaborator
        }
    }

    private fun loadBoardLabels() {
        viewModelScope.launch {
            try {
                val labels = getBoardLabelsUseCase(noteId)
                updateEditingState { it.copy(boardLabels = labels) }
                // Sync to component states that use boardLabels
                _taskDetailState.update { it?.copy(boardLabels = labels) }
                _labelManagementState.update { it?.copy(labels = labels) }
            } catch (e: Exception) {
                Log.d("BoardViewModel", "Load board labels error: ${e.message}")
            }
        }
    }

    private fun createLabel(name: String, color: String) {
        viewModelScope.launch {
            try {
                createBoardLabelUseCase(noteId, name, color)// TODO: Use return value
                loadBoardLabels()
                _labelManagementState.update { it?.copy(newLabelName = "", newLabelColor = "#4CAF50") }
            } catch (e: Exception) {
                updateEditingState { it.copy(error = "Unable to create label") }
            }
        }
    }

    private fun deleteLabel(labelId: Long) {
        viewModelScope.launch {
            try {
                deleteBoardLabelUseCase(noteId, labelId)
                loadBoardLabels()
            } catch (e: Exception) {
                updateEditingState { it.copy(error = "Unable to delete label") }
            }
        }
    }

    private fun addLabelToTask(labelId: Long) {
        val taskId = (_state.value as? BoardState.Editing)?.taskDetail?.id ?: return
        viewModelScope.launch {
            try {
                addLabelToTaskUseCase(noteId, taskId, labelId)
                reloadBoard()
                reloadTaskDetailIfOpen(taskId)
            } catch (e: Exception) {
                updateEditingState { it.copy(error = "Unable to add label to task") }
            }
        }
    }

    private fun removeLabelFromTask(labelId: Long) {
        val taskId = (_state.value as? BoardState.Editing)?.taskDetail?.id ?: return
        viewModelScope.launch {
            try {
                removeLabelFromTaskUseCase(noteId, taskId, labelId)
                reloadBoard()
                reloadTaskDetailIfOpen(taskId)
            } catch (e: Exception) {
                updateEditingState { it.copy(error = "Unable to remove label from task") }
            }
        }
    }

    private fun addAssigneeToTask(userId: String) {
        val taskId = (_state.value as? BoardState.Editing)?.taskDetail?.id ?: return
        viewModelScope.launch {
            try {
                addTaskAssigneeUseCase(noteId, taskId, userId)
                reloadBoard()
                reloadTaskDetailIfOpen(taskId)
            } catch (e: Exception) {
                updateEditingState { it.copy(error = "Unable to add assignee") }
            }
        }
    }

    private fun removeAssigneeFromTask(userId: String) {
        val taskId = (_state.value as? BoardState.Editing)?.taskDetail?.id ?: return
        viewModelScope.launch {
            try {
                removeTaskAssigneeUseCase(noteId, taskId, userId)
                reloadBoard()
                reloadTaskDetailIfOpen(taskId)
            } catch (e: Exception) {
                updateEditingState { it.copy(error = "Unable to remove assignee") }
            }
        }
    }

    private fun loadAvailableNotes() {
        val currentState = _state.value as? BoardState.Editing ?: return
        val groupIds = currentState.note.groups.map { it.id }
        if (groupIds.isEmpty()) return
        _addAttachmentState.update { it?.copy(isLoadingNotes = true) }
        viewModelScope.launch {
            try {
                val notes = getNotesForGroupsUseCase(groupIds, noteId)
                _addAttachmentState.update {
                    it?.copy(availableNotes = notes, isLoadingNotes = false)
                }
            } catch (e: Exception) {
                _addAttachmentState.update { it?.copy(isLoadingNotes = false) }
            }
        }
    }

    private fun confirmAddAttachment() {
        val currentState = _state.value as? BoardState.Editing ?: return
        val taskId = currentState.taskDetail?.id ?: return
        val attachState = _addAttachmentState.value ?: return
        val type = attachState.type
        val title = attachState.title.trim().ifBlank { null }
        val content = attachState.content.trim()
        if (content.isBlank()) return
        _addAttachmentState.value = null
        viewModelScope.launch {
            try {
                addTaskAttachmentUseCase(noteId, taskId, type, title, content)
                reloadTaskDetailIfOpen(taskId)
            } catch (e: Exception) {
                updateEditingState { it.copy(error = "Unable to add attachment") }
            }
        }
    }

    private fun deleteAttachment(attachmentId: Long) {
        val taskId = (_state.value as? BoardState.Editing)?.taskDetail?.id ?: return
        viewModelScope.launch {
            try {
                deleteTaskAttachmentUseCase(noteId, taskId, attachmentId)
                reloadTaskDetailIfOpen(taskId)
            } catch (e: Exception) {
                updateEditingState { it.copy(error = "Unable to delete attachment") }
            }
        }
    }

    private fun moveColumnLeft(columnId: Long) {
        val currentState = _state.value as? BoardState.Editing ?: return
        val columns = currentState.note.boardColumns.sortedBy { it.position }
        val index = columns.indexOfFirst { it.id == columnId }
        if (index <= 0) return
        val newOrder = columns.toMutableList().apply {
            val item = removeAt(index)
            add(index - 1, item)
        }
        viewModelScope.launch {
            try {
                reorderColumnsUseCase(noteId, newOrder.map { it.id })
                reloadBoard()
            } catch (e: Exception) {
                updateEditingState { it.copy(error = "Unable to reorder columns") }
            }
        }
    }

    private fun moveColumnRight(columnId: Long) {
        val currentState = _state.value as? BoardState.Editing ?: return
        val columns = currentState.note.boardColumns.sortedBy { it.position }
        val index = columns.indexOfFirst { it.id == columnId }
        if (index < 0 || index >= columns.size - 1) return
        val newOrder = columns.toMutableList().apply {
            val item = removeAt(index)
            add(index + 1, item)
        }
        viewModelScope.launch {
            try {
                reorderColumnsUseCase(noteId, newOrder.map { it.id })
                reloadBoard()
            } catch (e: Exception) {
                updateEditingState { it.copy(error = "Unable to reorder columns") }
            }
        }
    }

    private fun moveTaskUp(taskId: String, columnId: Long) {
        val currentState = _state.value as? BoardState.Editing ?: return
        val column = currentState.note.boardColumns.find { it.id == columnId } ?: return
        val tasks = column.tasks.sortedBy { it.position }
        val index = tasks.indexOfFirst { it.id == taskId }
        if (index <= 0) return
        val newOrder = tasks.toMutableList().apply {
            val item = removeAt(index)
            add(index - 1, item)
        }
        viewModelScope.launch {
            try {
                reorderTasksUseCase(noteId, columnId, newOrder.map { it.id })// TODO: Use return value
                reloadBoard()
            } catch (e: Exception) {
                updateEditingState { it.copy(error = "Unable to reorder tasks") }
            }
        }
    }

    private fun moveTaskDown(taskId: String, columnId: Long) {
        val currentState = _state.value as? BoardState.Editing ?: return
        val column = currentState.note.boardColumns.find { it.id == columnId } ?: return
        val tasks = column.tasks.sortedBy { it.position }
        val index = tasks.indexOfFirst { it.id == taskId }
        if (index < 0 || index >= tasks.size - 1) return
        val newOrder = tasks.toMutableList().apply {
            val item = removeAt(index)
            add(index + 1, item)
        }
        viewModelScope.launch {
            try {
                reorderTasksUseCase(noteId, columnId, newOrder.map { it.id })
                reloadBoard()
            } catch (e: Exception) {
                updateEditingState { it.copy(error = "Unable to reorder tasks") }
            }
        }
    }

    private suspend fun reloadBoard() {
        try {
            val note = getNoteInfoUseCase(noteId)
            val currentState = _state.value as? BoardState.Editing
            _state.value = (currentState ?: BoardState.Editing(note = note)).copy(note = note)
            // Sync shared data to TaskDetailState if open
            _taskDetailState.update { existing ->
                existing?.copy(
                    canEditBoard = note.accessType != AccessType.VIEWER,
                    columns = note.boardColumns
                )
            }
        } catch (_: Exception) {
            // Keep current state if reload fails
        }
    }

    private fun reloadTaskDetailIfOpen(taskId: String) {
        viewModelScope.launch {
            val currentState = _state.value as? BoardState.Editing
            if (currentState?.taskDetail?.id == taskId) {
                try {
                    val detail = getTaskDetailUseCase(noteId, taskId)
                    updateEditingState { it.copy(taskDetail = detail) }
                    _taskDetailState.update { existing ->
                        existing?.copy(
                            taskDetail = detail,
                            boardLabels = (_state.value as? BoardState.Editing)?.boardLabels
                                ?: existing.boardLabels
                        )
                    }
                } catch (_: Exception) { }
            }
        }
    }

    private fun updateEditingState(transform: (BoardState.Editing) -> BoardState.Editing) {
        _state.update { current ->
            if (current is BoardState.Editing) transform(current) else current
        }
    }
}

sealed interface BoardState {
    data object Loading : BoardState

    data class Editing(
        val note: Note,
        val taskDetail: TaskDetail? = null,
        val boardLabels: List<TaskLabel> = emptyList(),
        val showAddColumnDialog: Boolean = false,
        val showDeleteColumnDialog: Boolean = false,
        val deleteColumnId: Long? = null,
        val showDeleteTaskDialog: Boolean = false,
        val deleteTaskId: String? = null,
        val showMoveTaskDialog: Boolean = false,
        val moveTaskId: String? = null,
        val showEditColumnDialog: Boolean = false,
        val editColumnId: Long? = null,
        val editColumnWipLimit: String = "",
        val inputText: String = "",
        val error: String? = null,
        val showAddAssignee: Boolean = false,
        val showRenameBoardDialog: Boolean = false,
        val showDeleteNoteDialog: Boolean = false,
        val showShareSheet: Boolean = false,
        val collaboratorEmail: String = "",
        val availableGroups: List<Group> = emptyList(),
        val shareError: Int? = null,
        val isShareLoading: Boolean = false
    ) : BoardState

    data class Error(val message: String) : BoardState

    data object Finished : BoardState
}

sealed interface BoardCommand {
    data object Refresh : BoardCommand

    data object ConfirmCreateColumn : BoardCommand

    data object ConfirmUpdateColumn : BoardCommand

    data object ConfirmDeleteColumn : BoardCommand

    data object ConfirmDeleteTask : BoardCommand

    data class ConfirmMoveTask(val targetColumnId: Long) : BoardCommand

    data class CompleteTask(val taskId: String) : BoardCommand

    data class ReopenTask(val taskId: String) : BoardCommand

    data class ShowTaskDetail(val taskId: String) : BoardCommand

    data class ShowAddColumnDialog(val show: Boolean) : BoardCommand

    data class ShowAddTaskDialog(val show: Boolean, val columnId: Long? = null) : BoardCommand

    data class ShowDeleteColumnDialog(val show: Boolean, val columnId: Long? = null) : BoardCommand

    data class ShowDeleteTaskDialog(val show: Boolean, val taskId: String? = null) : BoardCommand

    data class ShowMoveTaskDialog(val show: Boolean, val taskId: String? = null) : BoardCommand

    data class ShowEditColumnDialog(
        val show: Boolean,
        val columnId: Long? = null,
        val currentTitle: String = "",
        val currentWipLimit: Int? = null
    ) : BoardCommand

    data class InputEditColumnWipLimit(val value: String) : BoardCommand

    data class InputText(val text: String) : BoardCommand

    data object ClearError : BoardCommand

    // Note-level actions
    data object ShowRenameBoardDialog : BoardCommand
    data object DismissRenameBoardDialog : BoardCommand
    data object ConfirmRenameBoard : BoardCommand
    data object ShowDeleteNoteDialog : BoardCommand
    data object DismissDeleteNoteDialog : BoardCommand
    data object ConfirmDeleteNote : BoardCommand
    data object OpenShareSheet : BoardCommand
    data object CloseShareSheet : BoardCommand
    data class InputCollaboratorEmail(val email: String) : BoardCommand
    data class AddCollaboratorByEmail(val email: String) : BoardCommand
    data class RemoveCollaborator(val collaboratorId: String) : BoardCommand
    data class AddToGroup(val groupId: Long) : BoardCommand
    data class RemoveFromGroup(val groupId: Long) : BoardCommand

    // Label management
    data object ShowLabelManagementSheet : BoardCommand

    // Reordering
    data class MoveColumnLeft(val columnId: Long) : BoardCommand
    data class MoveColumnRight(val columnId: Long) : BoardCommand
    data class MoveTaskUp(val taskId: String, val columnId: Long) : BoardCommand
    data class MoveTaskDown(val taskId: String, val columnId: Long) : BoardCommand
}
