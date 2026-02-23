@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package ru.shrprnbw.ideas.presentation.screens.my_tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.shrprnbw.ideas.R
import ru.shrprnbw.ideas.domain.entity.TaskDetail
import ru.shrprnbw.ideas.domain.entity.TaskPriority
import ru.shrprnbw.ideas.domain.entity.TaskSummary
import ru.shrprnbw.ideas.utils.DateFormatter

@Composable
fun MyTasksScreen(
    modifier: Modifier = Modifier,
    viewModel: MyTasksViewModel = hiltViewModel(),
    onMenuClicked: () -> Unit = {},
    onBoardClicked: (noteId: String) -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    val contentState = state as? MyTasksState.Content
    val errorMessage = contentState?.error?.let { stringResource(it) }
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.processCommand(MyTasksCommand.ClearError)
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.my_tasks_title)) },
                navigationIcon = {
                    IconButton(onClick = onMenuClicked) {
                        Icon(
                            imageVector = Icons.Rounded.Menu,
                            contentDescription = stringResource(R.string.menu_icon_description)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        when (val currentState = state) {
            is MyTasksState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    ContainedLoadingIndicator()
                }
            }

            is MyTasksState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = stringResource(currentState.message),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = {
                            viewModel.processCommand(MyTasksCommand.Refresh)
                        }) {
                            Text(stringResource(R.string.refresh))
                        }
                    }
                }
            }

            is MyTasksState.Content -> {
                PullToRefreshBox(
                    isRefreshing = currentState.isRefreshing,
                    onRefresh = { viewModel.processCommand(MyTasksCommand.Refresh) },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    if (currentState.tasks.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.my_tasks_empty),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(
                                currentState.tasks,
                                key = { it.taskId }
                            ) { task ->
                                MyTaskItem(
                                    task = task,
                                    onClick = {
                                        viewModel.processCommand(
                                            MyTasksCommand.ShowTaskDetail(
                                                task.taskId,
                                                task.boardNoteId
                                            )
                                        )
                                    },
                                    onComplete = {
                                        viewModel.processCommand(
                                            MyTasksCommand.CompleteTask(
                                                task.taskId,
                                                task.boardNoteId
                                            )
                                        )
                                    },
                                    onOpenBoard = { onBoardClicked(task.boardNoteId) }
                                )
                            }
                        }
                    }
                }

                if (currentState.taskDetail != null && currentState.taskDetailBoardNoteId != null) {
                    MyTaskDetailBottomSheet(
                        taskDetail = currentState.taskDetail,
                        onDismiss = {
                            viewModel.processCommand(MyTasksCommand.DismissTaskDetail)
                        },
                        onUpdateTask = { title, description, priority, startDate, dueDate ->
                            viewModel.processCommand(
                                MyTasksCommand.UpdateTask(
                                    taskId = currentState.taskDetail.id,
                                    boardNoteId = currentState.taskDetailBoardNoteId,
                                    title = title,
                                    description = description,
                                    priority = priority,
                                    startDate = startDate,
                                    dueDate = dueDate
                                )
                            )
                        },
                        onCompleteTask = {
                            viewModel.processCommand(
                                MyTasksCommand.CompleteTask(
                                    currentState.taskDetail.id,
                                    currentState.taskDetailBoardNoteId
                                )
                            )
                        },
                        onOpenBoard = {
                            viewModel.processCommand(MyTasksCommand.DismissTaskDetail)
                            onBoardClicked(currentState.taskDetailBoardNoteId)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun MyTaskItem(
    task: TaskSummary,
    onClick: () -> Unit,
    onComplete: () -> Unit,
    onOpenBoard: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

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
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                PriorityIndicator(task.priority)
                Spacer(modifier = Modifier.width(8.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.taskTitle,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${task.boardTitle} · ${task.columnTitle}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Filled.MoreVert,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.board_complete_task)) },
                            leadingIcon = {
                                Icon(Icons.Filled.CheckCircle, contentDescription = null)
                            },
                            onClick = { showMenu = false; onComplete() }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.my_tasks_open_board)) },
                            leadingIcon = {
                                Icon(Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = null)
                            },
                            onClick = { showMenu = false; onOpenBoard() }
                        )
                    }
                }
            }

            if (task.startDate != null || task.dueDate != null) {
                Spacer(modifier = Modifier.height(4.dp))
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
                Text(
                    text = dateText,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 16.dp)
                )
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
private fun MyTaskDetailBottomSheet(
    taskDetail: TaskDetail,
    onDismiss: () -> Unit,
    onUpdateTask: (title: String?, description: String?, priority: String?, startDate: Long?, dueDate: Long?) -> Unit,
    onCompleteTask: () -> Unit,
    onOpenBoard: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var editedTitle by remember(taskDetail.id) { mutableStateOf(taskDetail.title) }
    var editedDescription by remember(taskDetail.id) {
        mutableStateOf(taskDetail.description ?: "")
    }
    var editedPriority by remember(taskDetail.id) { mutableStateOf(taskDetail.priority) }
    val isCompleted = taskDetail.completedAt != null

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = stringResource(R.string.board_task_detail),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            item {
                OutlinedTextField(
                    value = editedTitle,
                    onValueChange = { editedTitle = it },
                    label = { Text(stringResource(R.string.board_task_title_hint)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            item {
                OutlinedTextField(
                    value = editedDescription,
                    onValueChange = { editedDescription = it },
                    label = { Text(stringResource(R.string.board_task_description_hint)) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 5
                )
            }

            item {
                Text(
                    text = stringResource(R.string.board_task_priority),
                    style = MaterialTheme.typography.labelLarge
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TaskPriority.entries.forEach { priority ->
                        FilterChip(
                            selected = editedPriority == priority,
                            onClick = { editedPriority = priority },
                            label = {
                                Text(
                                    when (priority) {
                                        TaskPriority.LOW -> stringResource(R.string.board_priority_low)
                                        TaskPriority.MEDIUM -> stringResource(R.string.board_priority_medium)
                                        TaskPriority.HIGH -> stringResource(R.string.board_priority_high)
                                        TaskPriority.CRITICAL -> stringResource(R.string.board_priority_critical)
                                    }
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = when (priority) {
                                    TaskPriority.LOW -> Color(0xFF4CAF50).copy(alpha = 0.2f)
                                    TaskPriority.MEDIUM -> Color(0xFFFFC107).copy(alpha = 0.2f)
                                    TaskPriority.HIGH -> Color(0xFFFF9800).copy(alpha = 0.2f)
                                    TaskPriority.CRITICAL -> Color(0xFFF44336).copy(alpha = 0.2f)
                                }
                            )
                        )
                    }
                }
            }

            if (taskDetail.startDate != null || taskDetail.dueDate != null) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        if (taskDetail.startDate != null) {
                            Text(
                                text = "${stringResource(R.string.board_task_start_date)}: ${
                                    DateFormatter.formatDateToString(taskDetail.startDate)
                                }",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (taskDetail.dueDate != null) {
                            Text(
                                text = "${stringResource(R.string.board_task_due_date)}: ${
                                    DateFormatter.formatDateToString(taskDetail.dueDate)
                                }",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(onClick = {
                        val titleChanged = editedTitle != taskDetail.title
                        val descChanged = editedDescription != (taskDetail.description ?: "")
                        val priorityChanged = editedPriority != taskDetail.priority
                        if (titleChanged || descChanged || priorityChanged) {
                            onUpdateTask(
                                if (titleChanged) editedTitle else null,
                                if (descChanged) editedDescription else null,
                                if (priorityChanged) editedPriority.name else null,
                                null, null// TODO: Add date pickers
                            )
                        }
                        onDismiss()
                    }) {
                        Text(stringResource(R.string.board_save))
                    }

                    if (!isCompleted) {
                        TextButton(onClick = onCompleteTask) {
                            Text(stringResource(R.string.board_complete_task))
                        }
                    }

                    TextButton(onClick = onOpenBoard) {
                        Text(stringResource(R.string.my_tasks_open_board))
                    }
                }
            }
        }
    }
}
