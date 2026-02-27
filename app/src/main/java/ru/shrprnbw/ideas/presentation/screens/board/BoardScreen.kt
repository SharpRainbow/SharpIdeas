@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalLayoutApi::class)

package ru.shrprnbw.ideas.presentation.screens.board

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.Label
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.NoteAlt
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.CheckCircleOutline
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AppBarRow
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import ru.shrprnbw.ideas.R
import ru.shrprnbw.ideas.domain.entity.AccessType
import ru.shrprnbw.ideas.domain.entity.BoardColumn
import ru.shrprnbw.ideas.domain.entity.BoardTask
import ru.shrprnbw.ideas.domain.entity.NoteType
import ru.shrprnbw.ideas.domain.entity.TaskAttachment
import ru.shrprnbw.ideas.domain.entity.TaskLabel
import ru.shrprnbw.ideas.domain.entity.TaskPriority
import ru.shrprnbw.ideas.domain.entity.User
import ru.shrprnbw.ideas.domain.entity.boardColumns
import ru.shrprnbw.ideas.presentation.screens.NoteLabel
import ru.shrprnbw.ideas.presentation.screens.ShareBottomSheet
import ru.shrprnbw.ideas.presentation.ui.theme.BoardOrange
import ru.shrprnbw.ideas.utils.DateFormatter

@Composable
fun BoardScreen(
    noteId: String,
    viewModel: BoardViewModel = hiltViewModel<BoardViewModel, BoardViewModel.Factory>(
        creationCallback = { factory -> factory.create(noteId) }
    ),
    onBackClicked: () -> Unit = {},
    onNavigateToNote: (noteId: String, noteType: String) -> Unit = { _, _ -> }
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val availableAssignees = viewModel.availableAssignees.collectAsLazyPagingItems()
    val taskDetailState by viewModel.taskDetailState.collectAsStateWithLifecycle()
    val createTaskState by viewModel.createTaskState.collectAsStateWithLifecycle()
    val addAttachmentState by viewModel.addAttachmentState.collectAsStateWithLifecycle()
//    val addAssigneeState by viewModel.addAssigneeState.collectAsStateWithLifecycle()
    val labelManagementState by viewModel.labelManagementState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    val editingState = state as? BoardState.Editing

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (state) {
                            is BoardState.Editing -> (state as BoardState.Editing).note.title
                            else -> ""
                        },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    if (editingState != null) {
                        val accessType = editingState.note.accessType
                        if (accessType != AccessType.VIEWER) {
                            val renameLabel = stringResource(R.string.board_rename)
                            val labelsLabel = stringResource(R.string.board_manage_labels)
                            val shareLabel = stringResource(R.string.share_note)
                            val deleteLabel = stringResource(R.string.delete_note)
                            val errorColor = MaterialTheme.colorScheme.error

                            AppBarRow(maxItemCount = 2) {
                                customItem(
                                    appbarContent = {
                                        IconButton(onClick = { viewModel.processCommand(BoardCommand.ShowRenameBoardDialog) }) {
                                            Icon(Icons.Filled.Edit, contentDescription = renameLabel)
                                        }
                                    },
                                    menuContent = { menuState ->
                                        DropdownMenuItem(
                                            text = { Text(renameLabel) },
                                            trailingIcon = { Icon(Icons.Filled.Edit, contentDescription = null) },
                                            onClick = { menuState.dismiss(); viewModel.processCommand(BoardCommand.ShowRenameBoardDialog) }
                                        )
                                    }
                                )
                                customItem(
                                    appbarContent = {
                                        IconButton(onClick = { viewModel.processCommand(BoardCommand.ShowLabelManagementSheet) }) {
                                            Icon(Icons.AutoMirrored.Rounded.Label, contentDescription = labelsLabel)
                                        }
                                    },
                                    menuContent = { menuState ->
                                        DropdownMenuItem(
                                            text = { Text(labelsLabel) },
                                            trailingIcon = { Icon(Icons.AutoMirrored.Rounded.Label, contentDescription = null) },
                                            onClick = { menuState.dismiss(); viewModel.processCommand(BoardCommand.ShowLabelManagementSheet) }
                                        )
                                    }
                                )
                                if (accessType == AccessType.OWNER) {
                                    customItem(
                                        appbarContent = {
                                            IconButton(onClick = { viewModel.processCommand(BoardCommand.OpenShareSheet) }) {
                                                Icon(Icons.Outlined.Share, contentDescription = shareLabel)
                                            }
                                        },
                                        menuContent = { menuState ->
                                            DropdownMenuItem(
                                                text = { Text(shareLabel) },
                                                trailingIcon = { Icon(Icons.Outlined.Share, contentDescription = null) },
                                                onClick = { menuState.dismiss(); viewModel.processCommand(BoardCommand.OpenShareSheet) }
                                            )
                                        }
                                    )
                                    customItem(
                                        appbarContent = {
                                            IconButton(onClick = { viewModel.processCommand(BoardCommand.ShowDeleteNoteDialog) }) {
                                                Icon(Icons.Outlined.DeleteOutline, contentDescription = deleteLabel, tint = errorColor)
                                            }
                                        },
                                        menuContent = { menuState ->
                                            DropdownMenuItem(
                                                text = { Text(deleteLabel, color = errorColor) },
                                                trailingIcon = { Icon(Icons.Outlined.DeleteOutline, contentDescription = null, tint = errorColor) },
                                                onClick = { menuState.dismiss(); viewModel.processCommand(BoardCommand.ShowDeleteNoteDialog) }
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (editingState != null && editingState.note.accessType != AccessType.VIEWER) {
                FloatingActionButton(
                    onClick = {
                        viewModel.processCommand(BoardCommand.ShowAddColumnDialog(true))
                    }
                ) {
                    Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.board_add_column))
                }
            }
        }
    ) { paddingValues ->
        when (val currentState = state) {
            is BoardState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    ContainedLoadingIndicator()
                }
            }

            is BoardState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = stringResource(R.string.board_error_load),
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = { viewModel.processCommand(BoardCommand.Refresh) }) {
                            Text(stringResource(R.string.refresh))
                        }
                    }
                }
            }

            is BoardState.Editing -> {
                LaunchedEffect(currentState.error) {
                    currentState.error?.let {
                        snackbarHostState.showSnackbar(it)
                        viewModel.processCommand(BoardCommand.ClearError)
                    }
                }
                BoardContent(
                    state = currentState,
                    availableAssignees = availableAssignees,
                    paddingValues = paddingValues,
                    onCommand = viewModel::processCommand,
                    onNavigateToNote = onNavigateToNote,
                    taskDetailState = taskDetailState,
                    createTaskState = createTaskState,
                    addAttachmentState = addAttachmentState,
//                    addAssigneeState = addAssigneeState,
                    labelManagementState = labelManagementState,
                    onTaskDetailAction = viewModel::onTaskDetailAction,
                    onCreateTaskAction = viewModel::onCreateTaskAction,
                    onAddAttachmentAction = viewModel::onAddAttachmentAction,
                    onAddAssigneeAction = viewModel::onAddAssigneeAction,
                    onLabelManagementAction = viewModel::onLabelManagementAction
                )
            }

            is BoardState.Finished -> {
                LaunchedEffect(Unit) {
                    onBackClicked()
                }
            }
        }
    }
}

@Composable
private fun BoardContent(
    state: BoardState.Editing,
    availableAssignees: LazyPagingItems<User>,
    paddingValues: PaddingValues,
    onCommand: (BoardCommand) -> Unit,
    onNavigateToNote: (noteId: String, noteType: String) -> Unit,
    taskDetailState: TaskDetailState?,
    createTaskState: CreateTaskState?,
    addAttachmentState: AddAttachmentState?,
//    addAssigneeState: AddAssigneeState?,
    labelManagementState: LabelManagementState?,
    onTaskDetailAction: (TaskDetailAction) -> Unit,
    onCreateTaskAction: (CreateTaskAction) -> Unit,
    onAddAttachmentAction: (AddAttachmentAction) -> Unit,
    onAddAssigneeAction: (AddAssigneeAction) -> Unit,
    onLabelManagementAction: (LabelManagementAction) -> Unit
) {
    val columns = state.note.boardColumns.sortedBy { it.position }
    val scrollState = rememberScrollState()
    val accessType = state.note.accessType

    if (columns.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.board_empty_columns),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    } else {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .horizontalScroll(scrollState)
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            for (column in columns) {
                BoardColumnCard(
                    column = column,
                    accessType = accessType,
                    onAddTask = {
                        onCommand(BoardCommand.ShowAddTaskDialog(true, column.id))
                    },
                    onEditColumn = {
                        onCommand(
                            BoardCommand.ShowEditColumnDialog(
                                true, column.id, column.title, column.wipLimit
                            )
                        )
                    },
                    onDeleteColumn = {
                        onCommand(BoardCommand.ShowDeleteColumnDialog(true, column.id))
                    },
                    onMoveColumnLeft = {
                        onCommand(BoardCommand.MoveColumnLeft(column.id))
                    },
                    onMoveColumnRight = {
                        onCommand(BoardCommand.MoveColumnRight(column.id))
                    },
                    onTaskClicked = { taskId ->
                        onCommand(BoardCommand.ShowTaskDetail(taskId))
                    },
                    onCompleteTask = { taskId ->
                        onCommand(BoardCommand.CompleteTask(taskId))
                    },
                    onReopenTask = { taskId ->
                        onCommand(BoardCommand.ReopenTask(taskId))
                    },
                    onMoveTask = { taskId ->
                        onCommand(BoardCommand.ShowMoveTaskDialog(true, taskId))
                    },
                    onDeleteTask = { taskId ->
                        onCommand(BoardCommand.ShowDeleteTaskDialog(true, taskId))
                    },
                    onMoveTaskUp = { taskId ->
                        onCommand(BoardCommand.MoveTaskUp(taskId, column.id))
                    },
                    onMoveTaskDown = { taskId ->
                        onCommand(BoardCommand.MoveTaskDown(taskId, column.id))
                    }
                )
            }
        }
    }

    // Dialogs
    if (state.showRenameBoardDialog) {
        InputDialog(
            title = stringResource(R.string.board_rename),
            label = stringResource(R.string.board_rename_hint),
            value = state.inputText,
            onValueChange = { onCommand(BoardCommand.InputText(it)) },
            onDismiss = { onCommand(BoardCommand.DismissRenameBoardDialog) },
            onConfirm = { onCommand(BoardCommand.ConfirmRenameBoard) }
        )
    }

    if (state.showAddColumnDialog) {
        InputDialog(
            title = stringResource(R.string.board_add_column),
            label = stringResource(R.string.board_column_title_hint),
            value = state.inputText,
            onValueChange = { onCommand(BoardCommand.InputText(it)) },
            onDismiss = { onCommand(BoardCommand.ShowAddColumnDialog(false)) },
            onConfirm = { onCommand(BoardCommand.ConfirmCreateColumn) }
        )
    }

    if (state.showEditColumnDialog && state.editColumnId != null) {
        EditColumnDialog(
            title = state.inputText,
            wipLimit = state.editColumnWipLimit,
            onTitleChange = { onCommand(BoardCommand.InputText(it)) },
            onWipLimitChange = { onCommand(BoardCommand.InputEditColumnWipLimit(it)) },
            onDismiss = { onCommand(BoardCommand.ShowEditColumnDialog(false)) },
            onConfirm = { onCommand(BoardCommand.ConfirmUpdateColumn) }
        )
    }

    createTaskState?.let {
        CreateTaskDialog(state = it, onAction = onCreateTaskAction)
    }

    if (state.showDeleteColumnDialog && state.deleteColumnId != null) {
        ConfirmDialog(
            title = stringResource(R.string.board_delete_column),
            message = stringResource(R.string.board_delete_column_confirm),
            onDismiss = { onCommand(BoardCommand.ShowDeleteColumnDialog(false)) },
            onConfirm = { onCommand(BoardCommand.ConfirmDeleteColumn) }
        )
    }

    if (state.showDeleteTaskDialog && state.deleteTaskId != null) {
        ConfirmDialog(
            title = stringResource(R.string.board_delete_task),
            message = stringResource(R.string.board_delete_task_confirm),
            onDismiss = { onCommand(BoardCommand.ShowDeleteTaskDialog(false)) },
            onConfirm = { onCommand(BoardCommand.ConfirmDeleteTask) }
        )
    }

    if (state.showMoveTaskDialog && state.moveTaskId != null) {
        MoveTaskDialog(
            columns = state.note.boardColumns,
            currentTask = state.note.boardColumns
                .flatMap { it.tasks }
                .find { it.id == state.moveTaskId },
            onDismiss = { onCommand(BoardCommand.ShowMoveTaskDialog(false)) },
            onMoveToColumn = { columnId ->
                onCommand(BoardCommand.ConfirmMoveTask(columnId))
            }
        )
    }

    // Delete note dialog
    if (state.showDeleteNoteDialog) {
        ConfirmDialog(
            title = stringResource(R.string.delete_note_request),
            message = stringResource(R.string.delete_note_confirmation),
            onDismiss = { onCommand(BoardCommand.DismissDeleteNoteDialog) },
            onConfirm = { onCommand(BoardCommand.ConfirmDeleteNote) }
        )
    }

    // Share bottom sheet
    if (state.showShareSheet) {
        ShareBottomSheet(
            collaborators = state.note.collaborators,
            noteGroups = state.note.groups,
            availableGroups = state.availableGroups,
            collaboratorEmail = state.collaboratorEmail,
            shareError = state.shareError,
            isLoading = state.isShareLoading,
            onDismiss = { onCommand(BoardCommand.CloseShareSheet) },
            onEmailChange = { onCommand(BoardCommand.InputCollaboratorEmail(it)) },
            onAddCollaborator = { onCommand(BoardCommand.AddCollaboratorByEmail(it)) },
            onRemoveCollaborator = { onCommand(BoardCommand.RemoveCollaborator(it)) },
            onAddToGroup = { onCommand(BoardCommand.AddToGroup(it)) },
            onRemoveFromGroup = { onCommand(BoardCommand.RemoveFromGroup(it)) }
        )
    }

    // Label management sheet
    labelManagementState?.let {
        LabelManagementBottomSheet(state = it, onAction = onLabelManagementAction)
    }

    // Add attachment dialog
    addAttachmentState?.let {
        AddAttachmentDialog(state = it, onAction = onAddAttachmentAction)
    }

    // Add assignee dialog
    if (state.showAddAssignee) {
        AddAssigneeDialog(availableAssignees = availableAssignees, onAction = onAddAssigneeAction)
    }

    // Task detail bottom sheet
    taskDetailState?.let {
        TaskDetailBottomSheet(
            state = it,
            onAction = onTaskDetailAction,
            onNavigateToNote = onNavigateToNote
        )
    }
}

@Composable
private fun BoardColumnCard(
    column: BoardColumn,
    accessType: AccessType,
    onAddTask: () -> Unit,
    onEditColumn: () -> Unit,
    onDeleteColumn: () -> Unit,
    onMoveColumnLeft: () -> Unit,
    onMoveColumnRight: () -> Unit,
    onTaskClicked: (String) -> Unit,
    onCompleteTask: (String) -> Unit,
    onReopenTask: (String) -> Unit,
    onMoveTask: (String) -> Unit,
    onDeleteTask: (String) -> Unit,
    onMoveTaskUp: (String) -> Unit,
    onMoveTaskDown: (String) -> Unit
) {
    val canEditBoard = accessType != AccessType.VIEWER
    var showMenu by remember { mutableStateOf(false) }
    val tasks = column.tasks.sortedBy { it.position }

    Card(
        modifier = Modifier
            .width(280.dp)
            .fillMaxHeight(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Column header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 12.dp, end = 4.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = column.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    val countText = if (column.wipLimit != null) {
                        "${tasks.size}/${column.wipLimit}"
                    } else {
                        "${tasks.size}"
                    }
                    Text(
                        text = countText,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (column.wipLimit != null && tasks.size > column.wipLimit) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
                if (canEditBoard) {
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(
                                imageVector = Icons.Filled.MoreVert,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.board_move_column_left)) },
                                leadingIcon = {
                                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null)
                                },
                                onClick = { showMenu = false; onMoveColumnLeft() }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.board_move_column_right)) },
                                leadingIcon = {
                                    Icon(Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = null)
                                },
                                onClick = { showMenu = false; onMoveColumnRight() }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.board_edit_column)) },
                                leadingIcon = {
                                    Icon(Icons.Filled.Edit, contentDescription = null)
                                },
                                onClick = {
                                    showMenu = false
                                    onEditColumn()
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        stringResource(R.string.board_delete_column),
                                        color = MaterialTheme.colorScheme.error
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Filled.Delete,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                },
                                onClick = {
                                    showMenu = false
                                    onDeleteColumn()
                                }
                            )
                        }
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 12.dp),
                thickness = 1.dp
            )

            // Tasks list
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(tasks, key = { it.id }) { task ->
                    TaskCard(
                        task = task,
                        accessType = accessType,
                        onClick = { onTaskClicked(task.id) },
                        onComplete = {
                            if (task.completedAt != null) onReopenTask(task.id)
                            else onCompleteTask(task.id)
                        },
                        onMove = { onMoveTask(task.id) },
                        onDelete = { onDeleteTask(task.id) },
                        onMoveUp = { onMoveTaskUp(task.id) },
                        onMoveDown = { onMoveTaskDown(task.id) }
                    )
                }
            }

            // Add task button - EDITOR + OWNER only
            if (canEditBoard) {
                TextButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    onClick = onAddTask
                ) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(R.string.board_add_task))
                }
            }
        }
    }
}

@Composable
private fun TaskCard(
    task: BoardTask,
    accessType: AccessType,
    onClick: () -> Unit,
    onComplete: () -> Unit,
    onMove: () -> Unit,
    onDelete: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit
) {
    val isCompleted = task.completedAt != null
    var showMenu by remember { mutableStateOf(false) }
    val canDeleteTask = accessType != AccessType.VIEWER

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                PriorityIndicator(task.priority)
                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                    color = if (isCompleted) MaterialTheme.colorScheme.onSurfaceVariant
                    else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )

                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            Icons.Filled.MoreVert,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    if (isCompleted) stringResource(R.string.board_reopen_task)
                                    else stringResource(R.string.board_complete_task)
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    if (isCompleted) Icons.Rounded.CheckCircleOutline
                                    else Icons.Filled.CheckCircle,
                                    contentDescription = null
                                )
                            },
                            onClick = { showMenu = false; onComplete() }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.board_move_task_up)) },
                            leadingIcon = {
                                Icon(Icons.Rounded.ArrowUpward, contentDescription = null)
                            },
                            onClick = { showMenu = false; onMoveUp() }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.board_move_task_down)) },
                            leadingIcon = {
                                Icon(Icons.Rounded.ArrowDownward, contentDescription = null)
                            },
                            onClick = { showMenu = false; onMoveDown() }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.board_move_task)) },
                            leadingIcon = {
                                Icon(Icons.Rounded.SwapHoriz, contentDescription = null)
                            },
                            onClick = { showMenu = false; onMove() }
                        )
                        if (canDeleteTask) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        stringResource(R.string.board_delete_task),
                                        color = MaterialTheme.colorScheme.error
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Filled.Delete,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                },
                                onClick = { showMenu = false; onDelete() }
                            )
                        }
                    }
                }
            }

            // Labels
            if (task.labels.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    for (label in task.labels.take(3)) {
                        TaskLabelChip(label)
                    }
                    if (task.labels.size > 3) {
                        Text(
                            text = "+${task.labels.size - 3}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Bottom row: dates + assignees
            if (task.startDate != null || task.dueDate != null || task.assignees.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val dateText = buildString {
                        if (task.startDate != null) {
                            append(DateFormatter.formatDateToString(task.startDate))
                        }
                        if (task.startDate != null && task.dueDate != null) {
                            append(" → ")
                        }
                        if (task.dueDate != null) {
                            append(DateFormatter.formatDateToString(task.dueDate))
                        }
                    }
                    if (dateText.isNotEmpty()) {
                        Text(
                            text = dateText,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (task.assignees.isNotEmpty()) {
                        Row(horizontalArrangement = Arrangement.spacedBy((-4).dp)) {
                            for (assignee in task.assignees.take(3)) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = (assignee.firstName.first())
                                            .uppercase(),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PriorityIndicator(priority: TaskPriority) {
    val color = when (priority) {
        TaskPriority.LOW -> Color(0xFF4CAF50)
        TaskPriority.MEDIUM -> Color(0xFFFFC107)
        TaskPriority.HIGH -> Color(0xFFFF9800)
        TaskPriority.CRITICAL -> Color(0xFFF44336)
    }
    Box(
        modifier = Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(color)
    )
}

@Composable
private fun TaskLabelChip(label: TaskLabel) {
    val chipColor = try {
        Color(label.color.toColorInt())
    } catch (_: Exception) {
        BoardOrange
    }
    NoteLabel(
        label = label.name,
        color = chipColor
    )
}

private fun tryParseColor(hex: String): Color {
    return try {
        Color(hex.toColorInt())
    } catch (_: Exception) {
        BoardOrange
    }
}

@Composable
private fun TaskDetailBottomSheet(// TODO: Use on my tasks screen
    state: TaskDetailState,
    onAction: (TaskDetailAction) -> Unit,
    onNavigateToNote: (noteId: String, noteType: String) -> Unit
) {
    val taskDetail = state.taskDetail
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val isCompleted = taskDetail.completedAt != null
    val canEditBoard = state.canEditBoard

    ModalBottomSheet(
        onDismissRequest = { onAction(TaskDetailAction.Dismiss) },
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
                if (isCompleted) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 4.dp)
                    ) {
                        Icon(
                            Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.board_complete_task),
                            color = Color(0xFF4CAF50),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }

                OutlinedTextField(
                    value = state.editedTitle,
                    onValueChange = { onAction(TaskDetailAction.EditTitle(it)) },
                    label = { Text(stringResource(R.string.board_task_title_hint)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = state.editedDescription,
                    onValueChange = { onAction(TaskDetailAction.EditDescription(it)) },
                    label = { Text(stringResource(R.string.board_task_description_hint)) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 6
                )

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.board_task_priority),
                    style = MaterialTheme.typography.labelLarge
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TaskPriority.entries.forEach { priority ->
                        val isSelected = state.editedPriority == priority
                        val priorityColor = when (priority) {
                            TaskPriority.LOW -> Color(0xFF4CAF50)
                            TaskPriority.MEDIUM -> Color(0xFFFFC107)
                            TaskPriority.HIGH -> Color(0xFFFF9800)
                            TaskPriority.CRITICAL -> Color(0xFFF44336)
                        }
                        val label = when (priority) {
                            TaskPriority.LOW -> stringResource(R.string.board_priority_low)
                            TaskPriority.MEDIUM -> stringResource(R.string.board_priority_medium)
                            TaskPriority.HIGH -> stringResource(R.string.board_priority_high)
                            TaskPriority.CRITICAL -> stringResource(R.string.board_priority_critical)
                        }
                        Card(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onAction(TaskDetailAction.EditPriority(priority)) },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) priorityColor.copy(alpha = 0.2f)
                                else MaterialTheme.colorScheme.surfaceContainerHigh
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = label,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isSelected) priorityColor
                                else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Start date
                DateTimePickerField(
                    label = stringResource(R.string.board_task_start_date),
                    dateTimeMillis = state.editedStartDate,
                    onDateTimeChanged = { onAction(TaskDetailAction.EditStartDate(it)) },
                    onClear = { onAction(TaskDetailAction.EditStartDate(null)) }
                )

                // Due date
                DateTimePickerField(
                    label = stringResource(R.string.board_task_due_date),
                    dateTimeMillis = state.editedDueDate,
                    onDateTimeChanged = { onAction(TaskDetailAction.EditDueDate(it)) },
                    onClear = { onAction(TaskDetailAction.EditDueDate(null)) }
                )

                // Labels section - EDITOR + OWNER only
                if (canEditBoard) {
                    Text(
                        text = stringResource(R.string.board_labels),
                        style = MaterialTheme.typography.labelLarge
                    )
                    val assignedLabelIds = taskDetail.labels.map { it.id }.toSet()
                    if (taskDetail.labels.isNotEmpty()) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            taskDetail.labels.forEach { label ->
                                FilterChip(
                                    selected = true,
                                    onClick = { onAction(TaskDetailAction.RemoveLabel(label.id)) },
                                    label = { Text(label.name) },
                                    trailingIcon = {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = tryParseColor(label.color).copy(alpha = 0.2f),
                                        selectedLabelColor = tryParseColor(label.color)
                                    )
                                )
                            }
                        }
                    }
                    val availableLabels = state.boardLabels.filter { label -> label.id !in assignedLabelIds }
                    if (availableLabels.isNotEmpty()) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            availableLabels.forEach { label ->
                                AssistChip(
                                    onClick = { onAction(TaskDetailAction.AddLabel(label.id)) },
                                    label = { Text(label.name) }
                                )
                            }
                        }
                    }
                }

                // Assignees section - EDITOR + OWNER only
                if (canEditBoard) {
                    Text(
                        text = stringResource(R.string.board_assignees),
                        style = MaterialTheme.typography.labelLarge
                    )
                    if (taskDetail.assignees.isNotEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            taskDetail.assignees.forEach { user ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primaryContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = user.firstName.first().uppercase(),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "${user.firstName} ${user.lastName}(@${user.username})",
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.weight(1f),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    IconButton(
                                        onClick = { onAction(TaskDetailAction.RemoveAssignee(user.id)) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        Text(
                            text = stringResource(R.string.board_no_assignees),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    TextButton(onClick = { onAction(TaskDetailAction.ShowAddAssignee) }) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(stringResource(R.string.board_add_assignee))
                    }
                }

                // Attachments section
                Text(
                    text = stringResource(R.string.board_attachments),
                    style = MaterialTheme.typography.labelLarge
                )
                if (taskDetail.attachments.isEmpty()) {
                    Text(
                        text = stringResource(R.string.board_no_attachments),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        taskDetail.attachments.forEach { attachment ->
                            AttachmentRow(
                                attachment = attachment,
                                onDelete = { onAction(TaskDetailAction.DeleteAttachment(attachment.id)) },
                                onNavigateToNote = onNavigateToNote
                            )
                        }
                    }
                }
                TextButton(onClick = { onAction(TaskDetailAction.ShowAddAttachment) }) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(R.string.board_add_attachment))
                }

                // Column info
                val columnName = state.columns.find { it.id == taskDetail.columnId }?.title ?: ""
                if (columnName.isNotBlank()) {
                    Text(
                        text = "Column: $columnName",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                HorizontalDivider()

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (canEditBoard) {
                        TextButton(onClick = { onAction(TaskDetailAction.DeleteTask(taskDetail.id)) }) {
                            Text(
                                stringResource(R.string.delete),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    TextButton(onClick = { onAction(TaskDetailAction.MoveTask(taskDetail.id)) }) {
                        Text(stringResource(R.string.board_move_task))
                    }

                    TextButton(
                        onClick = {
                            if (isCompleted) onAction(TaskDetailAction.ReopenTask(taskDetail.id))
                            else onAction(TaskDetailAction.CompleteTask(taskDetail.id))
                        }
                    ) {
                        Text(
                            if (isCompleted) stringResource(R.string.board_reopen_task)
                            else stringResource(R.string.board_complete_task)
                        )
                    }

                    TextButton(onClick = { onAction(TaskDetailAction.SaveEdits) }) {
                        Text(stringResource(R.string.board_save))
                    }
                }
        }
    }

}

@Composable
private fun AttachmentRow(
    attachment: TaskAttachment,
    onDelete: () -> Unit,
    onNavigateToNote: (noteId: String, noteType: String) -> Unit = { _, _ -> }
) {
    val uriHandler = LocalUriHandler.current
    val isClickable = attachment.type == "LINK" || attachment.type == "NOTE_REFERENCE"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .then(
                if (isClickable) Modifier.clickable {
                    when (attachment.type) {
                        "LINK" -> {
                            try { uriHandler.openUri(attachment.content) } catch (_: Exception) {}
                        }
                        "NOTE_REFERENCE" -> {
                            val parts = attachment.content.split("|", limit = 2)
                            if (parts.size == 2) {
                                onNavigateToNote(parts[0], parts[1])
                            }
                        }
                    }
                } else Modifier
            )
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = when (attachment.type) {
                "LINK" -> Icons.Outlined.Link
                "NOTE_REFERENCE" -> Icons.Outlined.NoteAlt
                else -> Icons.Outlined.Description
            },
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = if (isClickable) MaterialTheme.colorScheme.primary
                   else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = attachment.title ?: attachment.type,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = if (isClickable) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurface,
                textDecoration = if (isClickable) TextDecoration.Underline else TextDecoration.None
            )
            if (!isClickable || attachment.type == "LINK") {
                Text(
                    text = attachment.content,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                Icons.Default.Close,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun LabelManagementBottomSheet(
    state: LabelManagementState,
    onAction: (LabelManagementAction) -> Unit
) {
    val labels = state.labels
    val newLabelName = state.newLabelName
    val newLabelColor = state.newLabelColor
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val presetColors = listOf(
        "#4CAF50", "#2196F3", "#FF9800", "#F44336",
        "#9C27B0", "#00BCD4", "#795548", "#607D8F"
    )

    ModalBottomSheet(
        onDismissRequest = { onAction(LabelManagementAction.Dismiss) },
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = stringResource(R.string.board_manage_labels),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (labels.isEmpty()) {
                Text(
                    text = stringResource(R.string.board_no_labels),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                labels.forEach { label ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(tryParseColor(label.color))
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = label.name,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { onAction(LabelManagementAction.DeleteLabel(label.id)) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.board_create_label),
                style = MaterialTheme.typography.labelLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = newLabelName,
                onValueChange = { onAction(LabelManagementAction.EditName(it)) },
                label = { Text(stringResource(R.string.board_label_name_hint)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                presetColors.forEach { color ->
                    val isSelected = color == newLabelColor
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(tryParseColor(color))
                            .clickable { onAction(LabelManagementAction.EditColor(color)) }
                            .then(
                                if (isSelected) Modifier.padding(2.dp)
                                else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                Icons.Filled.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = {
                    if (newLabelName.isNotBlank()) {
                        onAction(LabelManagementAction.CreateLabel(newLabelName.trim(), newLabelColor))
                    }
                },
                enabled = newLabelName.isNotBlank()
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(stringResource(R.string.board_create_label))
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun AddAttachmentDialog(
    state: AddAttachmentState,
    onAction: (AddAttachmentAction) -> Unit
) {
    val isNoteRef = state.type == "NOTE_REFERENCE"
    val confirmEnabled = if (isNoteRef) state.selectedNoteRefId != null else state.content.isNotBlank()

    AlertDialog(
        onDismissRequest = { onAction(AddAttachmentAction.Dismiss) },
        title = { Text(stringResource(R.string.board_add_attachment)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        "TEXT" to stringResource(R.string.board_attachment_type_text),
                        "LINK" to stringResource(R.string.board_attachment_type_link),
                        "NOTE_REFERENCE" to stringResource(R.string.board_attachment_type_note_ref)
                    ).forEach { (typeValue, typeLabel) ->
                        FilterChip(
                            selected = state.type == typeValue,
                            onClick = { onAction(AddAttachmentAction.EditType(typeValue)) },
                            label = { Text(typeLabel) }
                        )
                    }
                }
                if (isNoteRef) {
                    Text(
                        text = stringResource(R.string.board_select_note_reference),
                        style = MaterialTheme.typography.labelLarge
                    )
                    if (state.isLoadingNotes) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            ContainedLoadingIndicator()
                        }
                    } else if (state.availableNotes.isEmpty()) {
                        Text(
                            text = stringResource(R.string.board_no_notes_in_groups),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            state.availableNotes.forEach { note ->
                                val isSelected = note.id == state.selectedNoteRefId
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable {
                                            onAction(AddAttachmentAction.SelectNoteReference(
                                                note.id,
                                                note.title,
                                                note.noteType.name
                                            ))
                                        },
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected)
                                            MaterialTheme.colorScheme.primaryContainer
                                        else MaterialTheme.colorScheme.surfaceContainerHigh
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(
                                                horizontal = 12.dp,
                                                vertical = 8.dp
                                            ),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = when (note.noteType) {
                                                NoteType.BOARD -> Icons.Outlined.Description
                                                else -> Icons.Outlined.NoteAlt
                                            },
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp),
                                            tint = if (isSelected)
                                                MaterialTheme.colorScheme.onPrimaryContainer
                                            else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = note.title,
                                            style = MaterialTheme.typography.bodyMedium,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            color = if (isSelected)
                                                MaterialTheme.colorScheme.onPrimaryContainer
                                            else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    OutlinedTextField(
                        value = state.title,
                        onValueChange = { onAction(AddAttachmentAction.EditTitle(it)) },
                        label = { Text(stringResource(R.string.board_attachment_title_hint)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = state.content,
                        onValueChange = { onAction(AddAttachmentAction.EditContent(it)) },
                        label = { Text(stringResource(R.string.board_attachment_content_hint)) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 4
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onAction(AddAttachmentAction.Confirm) },
                enabled = confirmEnabled
            ) {
                Text(stringResource(R.string.create_button))
            }
        },
        dismissButton = {
            TextButton(onClick = { onAction(AddAttachmentAction.Dismiss) }) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
private fun AddAssigneeDialog(
    availableAssignees: LazyPagingItems<User>,
    onAction: (AddAssigneeAction) -> Unit
) {
    AlertDialog(
        onDismissRequest = { onAction(AddAssigneeAction.Dismiss) },
        title = { Text(stringResource(R.string.board_add_assignee)) },
        text = {
            if (availableAssignees.loadState == LoadState.Loading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    ContainedLoadingIndicator()
                }
            } else if (availableAssignees.itemCount <= 0) {
                Text(
                    text = stringResource(R.string.board_no_assignees),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    items(availableAssignees.itemCount, key = {
                        availableAssignees[it]?.id ?: it
                    }) { index ->
                        val user = availableAssignees[index] ?: return@items
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onAction(AddAssigneeAction.Add(user.id)) }
                                .padding(vertical = 6.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = user.firstName.first().uppercase(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "${user.firstName} ${user.lastName}(@${user.username})",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Icon(
                                Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = { onAction(AddAssigneeAction.Dismiss) }) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
private fun MoveTaskDialog(
    columns: List<BoardColumn>,
    currentTask: BoardTask?,
    onDismiss: () -> Unit,
    onMoveToColumn: (Long) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.board_move_task)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                columns.sortedBy { it.position }.forEach { column ->
                    val isCurrent = currentTask?.columnId == column.id
                    TextButton(
                        onClick = {
                            if (!isCurrent) onMoveToColumn(column.id)
                        },
                        enabled = !isCurrent,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = column.title + if (isCurrent) " (current)" else "",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
private fun CreateTaskDialog(
    state: CreateTaskState,
    onAction: (CreateTaskAction) -> Unit
) {
    AlertDialog(
        onDismissRequest = { onAction(CreateTaskAction.Dismiss) },
        title = { Text(stringResource(R.string.board_add_task)) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = state.title,
                    onValueChange = { onAction(CreateTaskAction.EditTitle(it)) },
                    label = { Text(stringResource(R.string.board_task_title_hint)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = state.description,
                    onValueChange = { onAction(CreateTaskAction.EditDescription(it)) },
                    label = { Text(stringResource(R.string.board_task_description_hint)) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4
                )

                Text(
                    text = stringResource(R.string.board_task_priority),
                    style = MaterialTheme.typography.labelLarge
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TaskPriority.entries.forEach { priority ->
                        val isSelected = state.priority == priority
                        val priorityColor = when (priority) {
                            TaskPriority.LOW -> Color(0xFF4CAF50)
                            TaskPriority.MEDIUM -> Color(0xFFFFC107)
                            TaskPriority.HIGH -> Color(0xFFFF9800)
                            TaskPriority.CRITICAL -> Color(0xFFF44336)
                        }
                        val label = when (priority) {
                            TaskPriority.LOW -> stringResource(R.string.board_priority_low)
                            TaskPriority.MEDIUM -> stringResource(R.string.board_priority_medium)
                            TaskPriority.HIGH -> stringResource(R.string.board_priority_high)
                            TaskPriority.CRITICAL -> stringResource(R.string.board_priority_critical)
                        }
                        Card(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onAction(CreateTaskAction.EditPriority(priority)) },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) priorityColor.copy(alpha = 0.2f)
                                else MaterialTheme.colorScheme.surfaceContainerHigh
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = label,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isSelected) priorityColor
                                else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                DateTimePickerField(
                    label = stringResource(R.string.board_task_start_date),
                    dateTimeMillis = state.startDate,
                    onDateTimeChanged = { onAction(CreateTaskAction.EditStartDate(it)) },
                    onClear = { onAction(CreateTaskAction.EditStartDate(null)) }
                )

                DateTimePickerField(
                    label = stringResource(R.string.board_task_due_date),
                    dateTimeMillis = state.dueDate,
                    onDateTimeChanged = { onAction(CreateTaskAction.EditDueDate(it)) },
                    onClear = { onAction(CreateTaskAction.EditDueDate(null)) }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onAction(CreateTaskAction.Confirm) },
                enabled = state.title.isNotBlank()
            ) {
                Text(stringResource(R.string.create_button))
            }
        },
        dismissButton = {
            TextButton(onClick = { onAction(CreateTaskAction.Dismiss) }) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
private fun DateTimePickerField(
    label: String,
    dateTimeMillis: Long?,
    onDateTimeChanged: (Long) -> Unit,
    onClear: () -> Unit
) {
    val dateFormat = remember { java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault()) }
    val timeFormat = remember { java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val calendar = remember { java.util.Calendar.getInstance() }

    Column {
        Text(text = label, style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = { showDatePicker = true },
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = if (dateTimeMillis != null)
                        dateFormat.format(java.util.Date(dateTimeMillis))
                    else stringResource(R.string.board_task_select_date)
                )
            }
            OutlinedButton(
                onClick = { showTimePicker = true },
                enabled = dateTimeMillis != null
            ) {
                Text(
                    text = if (dateTimeMillis != null)
                        timeFormat.format(java.util.Date(dateTimeMillis))
                    else "--:--"
                )
            }
            if (dateTimeMillis != null) {
                IconButton(
                    onClick = onClear,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = stringResource(R.string.board_task_clear_date),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = dateTimeMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val selectedDate = datePickerState.selectedDateMillis
                    if (selectedDate != null) {
                        calendar.timeInMillis = selectedDate
                        if (dateTimeMillis != null) {
                            val prev = java.util.Calendar.getInstance().apply { timeInMillis = dateTimeMillis }
                            calendar.set(java.util.Calendar.HOUR_OF_DAY, prev.get(java.util.Calendar.HOUR_OF_DAY))
                            calendar.set(java.util.Calendar.MINUTE, prev.get(java.util.Calendar.MINUTE))
                        } else {
                            calendar.set(java.util.Calendar.HOUR_OF_DAY, 12)
                            calendar.set(java.util.Calendar.MINUTE, 0)
                        }
                        calendar.set(java.util.Calendar.SECOND, 0)
                        calendar.set(java.util.Calendar.MILLISECOND, 0)
                        onDateTimeChanged(calendar.timeInMillis)
                    }
                    showDatePicker = false
                }) { Text(stringResource(R.string.confirm_button)) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        val currentCalendar = remember(dateTimeMillis) {
            java.util.Calendar.getInstance().apply {
                if (dateTimeMillis != null) timeInMillis = dateTimeMillis
            }
        }
        val timePickerState = rememberTimePickerState(
            initialHour = currentCalendar.get(java.util.Calendar.HOUR_OF_DAY),
            initialMinute = currentCalendar.get(java.util.Calendar.MINUTE)
        )
        TimePickerDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    if (dateTimeMillis != null) {
                        calendar.timeInMillis = dateTimeMillis
                        calendar.set(java.util.Calendar.HOUR_OF_DAY, timePickerState.hour)
                        calendar.set(java.util.Calendar.MINUTE, timePickerState.minute)
                        calendar.set(java.util.Calendar.SECOND, 0)
                        calendar.set(java.util.Calendar.MILLISECOND, 0)
                        onDateTimeChanged(calendar.timeInMillis)
                    }
                    showTimePicker = false
                }) { Text(stringResource(R.string.confirm_button)) }
            },
            title = { Text(stringResource(R.string.board_task_select_time)) },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        ) {
            TimePicker(state = timePickerState)
        }
    }
}

@Composable
private fun InputDialog(
    title: String,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                label = { Text(label) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.create_button))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
private fun EditColumnDialog(
    title: String,
    wipLimit: String,
    onTitleChange: (String) -> Unit,
    onWipLimitChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.board_edit_column)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = onTitleChange,
                    label = { Text(stringResource(R.string.board_column_title_hint)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = wipLimit,
                    onValueChange = { value ->
                        if (value.isEmpty() || value.all { it.isDigit() }) {
                            onWipLimitChange(value)
                        }
                    },
                    label = { Text(stringResource(R.string.board_wip_limit)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.board_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
private fun ConfirmDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    stringResource(R.string.delete),
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
