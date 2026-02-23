package ru.shrprnbw.ideas.presentation.screens.board

import ru.shrprnbw.ideas.domain.entity.BoardColumn
import ru.shrprnbw.ideas.domain.entity.NotePreview
import ru.shrprnbw.ideas.domain.entity.TaskDetail
import ru.shrprnbw.ideas.domain.entity.TaskLabel
import ru.shrprnbw.ideas.domain.entity.TaskPriority
import ru.shrprnbw.ideas.domain.entity.User

data class TaskDetailState(
    val taskDetail: TaskDetail,
    val canEditBoard: Boolean,
    val boardLabels: List<TaskLabel>,
    val columns: List<BoardColumn>,
    val editedTitle: String,
    val editedDescription: String,
    val editedPriority: TaskPriority?,
    val editedStartDate: Long?,
    val editedDueDate: Long?
)

sealed interface TaskDetailAction {
    data object Dismiss : TaskDetailAction
    data object SaveEdits : TaskDetailAction
    data class EditTitle(val title: String) : TaskDetailAction
    data class EditDescription(val description: String) : TaskDetailAction
    data class EditPriority(val priority: TaskPriority) : TaskDetailAction
    data class EditStartDate(val date: Long?) : TaskDetailAction
    data class EditDueDate(val date: Long?) : TaskDetailAction
    data class CompleteTask(val taskId: String) : TaskDetailAction
    data class ReopenTask(val taskId: String) : TaskDetailAction
    data class DeleteTask(val taskId: String) : TaskDetailAction
    data class MoveTask(val taskId: String) : TaskDetailAction
    data class AddLabel(val labelId: Long) : TaskDetailAction
    data class RemoveLabel(val labelId: Long) : TaskDetailAction
    data object ShowAddAssignee : TaskDetailAction
    data class RemoveAssignee(val userId: String) : TaskDetailAction
    data object ShowAddAttachment : TaskDetailAction
    data class DeleteAttachment(val attachmentId: Long) : TaskDetailAction
}

data class CreateTaskState(
    val columnId: Long,
    val title: String,
    val description: String,
    val priority: TaskPriority?,
    val startDate: Long?,
    val dueDate: Long?
)

sealed interface CreateTaskAction {
    data class EditTitle(val title: String) : CreateTaskAction
    data class EditDescription(val description: String) : CreateTaskAction
    data class EditPriority(val priority: TaskPriority?) : CreateTaskAction
    data class EditStartDate(val date: Long?) : CreateTaskAction
    data class EditDueDate(val date: Long?) : CreateTaskAction
    data object Dismiss : CreateTaskAction
    data object Confirm : CreateTaskAction
}

data class AddAttachmentState(
    val type: String,
    val title: String,
    val content: String,
    val availableNotes: List<NotePreview>,
    val isLoadingNotes: Boolean,
    val selectedNoteRefId: String?
)

sealed interface AddAttachmentAction {
    data class EditType(val type: String) : AddAttachmentAction
    data class EditTitle(val title: String) : AddAttachmentAction
    data class EditContent(val content: String) : AddAttachmentAction
    data class SelectNoteReference(
        val noteId: String,
        val noteTitle: String,
        val noteType: String
    ) : AddAttachmentAction
    data object Dismiss : AddAttachmentAction
    data object Confirm : AddAttachmentAction
}

data class AddAssigneeState(
    val unassignedUsers: List<User>,
    val isLoading: Boolean
)

sealed interface AddAssigneeAction {
    data class Add(val userId: String) : AddAssigneeAction
    data object Dismiss : AddAssigneeAction
}

data class LabelManagementState(
    val labels: List<TaskLabel>,
    val newLabelName: String,
    val newLabelColor: String
)

sealed interface LabelManagementAction {
    data class EditName(val name: String) : LabelManagementAction
    data class EditColor(val color: String) : LabelManagementAction
    data class CreateLabel(val name: String, val color: String) : LabelManagementAction
    data class DeleteLabel(val labelId: Long) : LabelManagementAction
    data object Dismiss : LabelManagementAction
}
