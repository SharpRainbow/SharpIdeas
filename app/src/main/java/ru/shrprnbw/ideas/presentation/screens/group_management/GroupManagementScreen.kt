@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package ru.shrprnbw.ideas.presentation.screens.group_management

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.PersonRemove
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import ru.shrprnbw.ideas.R
import ru.shrprnbw.ideas.domain.entity.Group
import ru.shrprnbw.ideas.domain.entity.GroupUser

@Composable
fun GroupManagementScreen(
    modifier: Modifier = Modifier,
    viewModel: GroupManagementViewModel = hiltViewModel(),
    onMenuClicked: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val groupsList by viewModel.groupsList.collectAsState()
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        modifier = modifier
            .fillMaxWidth()
            .imePadding(),
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.group_management_title),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onMenuClicked) {
                            Icon(
                                imageVector = Icons.Rounded.Menu,
                                contentDescription = "Menu"
                            )
                        }
                    }
                )
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->
        val errorMessage = state.error?.let { stringResource(it) }
        LaunchedEffect(state.error) {
            errorMessage?.let {
                snackbarHostState.showSnackbar(errorMessage)
                viewModel.processCommand(GroupManagementCommand.ResetError)
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    var isAddGroupFocused by remember { mutableStateOf(false) }
                    if (!isAddGroupFocused) {
                        Icon(
                            imageVector = Icons.Outlined.Add,
                            contentDescription = null,
                            modifier = Modifier
                                .size(24.dp)
                                .clickable(
                                    onClick = {
                                        focusRequester.requestFocus()
                                    }
                                )
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = null,
                            modifier = Modifier
                                .size(24.dp)
                                .clickable(onClick = {
                                    viewModel.processCommand(
                                        GroupManagementCommand.InputNewGroup("")
                                    )
                                    focusManager.clearFocus()
                                })
                        )
                    }
                    TextField(
                        value = state.newGroupInput,
                        onValueChange = {
                            viewModel.processCommand(
                                GroupManagementCommand.InputNewGroup(it)
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(focusRequester)
                            .onFocusChanged { focusState ->
                                if (focusState.isFocused) {
                                    state.editingGroupId?.let {
                                        viewModel.processCommand(
                                            GroupManagementCommand.CancelEditGroup(it)
                                        )
                                    }
                                }
                                isAddGroupFocused = focusState.isFocused
                            },
                        placeholder = {
                            Text(
                                text = stringResource(R.string.group_management_create_hint),
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        singleLine = true,
                        trailingIcon = {
                            if (state.newGroupInput.isNotBlank()) {
                                IconButton(
                                    onClick = {
                                        viewModel.processCommand(
                                            GroupManagementCommand.CreateGroup(state.newGroupInput)
                                        )
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Check,
                                        contentDescription = "Create"
                                    )
                                }
                            }
                        }
                    )
                }
            }

            items(groupsList, key = { it.id }) { group ->
                GroupListItem(
                    group = group,
                    state = state,
                    viewModel = viewModel
                )
            }
        }

        if (state.selectedGroup != null) {
            ModalBottomSheet(
                onDismissRequest = {
                    viewModel.processCommand(GroupManagementCommand.DeselectGroup)
                },
                sheetState = bottomSheetState
            ) {
                GroupUsersBottomSheet(
                    group = state.selectedGroup!!,
                    users = state.groupUsers,
                    isLoading = state.isLoadingUsers,
                    addUserInput = state.addUserInput,
                    addUserByEmail = state.addUserByEmail,
                    onAddUserInputChange = {
                        viewModel.processCommand(GroupManagementCommand.InputAddUser(it))
                    },
                    onToggleByEmail = {
                        viewModel.processCommand(GroupManagementCommand.ToggleAddByEmail(it))
                    },
                    onAddUser = {
                        viewModel.processCommand(GroupManagementCommand.AddUserToGroup(state.selectedGroup!!.id))
                    },
                    onRemoveUser = { userId ->
                        viewModel.processCommand(
                            GroupManagementCommand.RemoveUserFromGroup(state.selectedGroup!!.id, userId)
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun GroupListItem(
    group: Group,
    state: GroupManagementState,
    viewModel: GroupManagementViewModel
) {
    val isEditing = state.editingGroupId == group.id

    if (isEditing) {
        HorizontalDivider(modifier = Modifier.fillMaxWidth(), thickness = 1.dp)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (!isEditing) {
                    viewModel.processCommand(GroupManagementCommand.SelectGroup(group))
                }
            }
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isEditing) Icons.Outlined.Delete else Icons.Outlined.Group,
            contentDescription = null,
            modifier = Modifier
                .size(24.dp)
                .clickable {
                    if (isEditing) {
                        viewModel.processCommand(GroupManagementCommand.DeleteGroup(group.id))
                    }
                },
            tint = if (isEditing) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.width(16.dp))

        if (isEditing) {
            var textFieldValue by remember {
                mutableStateOf(
                    TextFieldValue(
                        text = group.name,
                        selection = TextRange(group.name.length)
                    )
                )
            }
            val editFocusRequester = remember { FocusRequester() }

            TextField(
                value = textFieldValue,
                onValueChange = { newValue ->
                    textFieldValue = newValue
                    viewModel.processCommand(
                        GroupManagementCommand.UpdateGroupInput(newValue.text)
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(editFocusRequester)
                    .onFocusChanged(onFocusChanged = {
                        if (it.isFocused && state.newGroupInput.isNotEmpty()) {
                            viewModel.processCommand(
                                GroupManagementCommand.InputNewGroup("")
                            )
                        }
                    }),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                singleLine = true
            )

            LaunchedEffect(Unit) {
                editFocusRequester.requestFocus()
            }
        } else {
            Text(
                text = group.name,
                modifier = Modifier.weight(1f),
                fontSize = 16.sp
            )
        }

        IconButton(
            onClick = {
                if (isEditing) {
                    viewModel.processCommand(GroupManagementCommand.SaveGroup(group))
                } else {
                    viewModel.processCommand(
                        GroupManagementCommand.StartEditGroup(group.id, group.name)
                    )
                }
            }
        ) {
            Icon(
                imageVector = if (isEditing) Icons.Outlined.Check else Icons.Outlined.Edit,
                contentDescription = if (isEditing) "Save" else "Edit"
            )
        }
    }
    if (isEditing) {
        HorizontalDivider(modifier = Modifier.fillMaxWidth(), thickness = 1.dp)
    }
}

@Composable
private fun GroupUsersBottomSheet(
    group: Group,
    users: List<GroupUser>,
    isLoading: Boolean,
    addUserInput: String,
    addUserByEmail: Boolean,
    onAddUserInputChange: (String) -> Unit,
    onToggleByEmail: (Boolean) -> Unit,
    onAddUser: () -> Unit,
    onRemoveUser: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 32.dp)
    ) {
        Text(
            text = group.name,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.group_management_add_user),
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = addUserByEmail,
                onClick = { onToggleByEmail(true) },
                label = { Text("Email") }
            )
            FilterChip(
                selected = !addUserByEmail,
                onClick = { onToggleByEmail(false) },
                label = { Text("UUID") }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = addUserInput,
                onValueChange = onAddUserInputChange,
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        text = if (addUserByEmail) {
                            stringResource(R.string.group_management_enter_email)
                        } else {
                            stringResource(R.string.group_management_enter_uuid)
                        }
                    )
                },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                )
            )
            IconButton(
                onClick = onAddUser,
                enabled = addUserInput.isNotBlank()
            ) {
                Icon(
                    imageVector = Icons.Outlined.PersonAdd,
                    contentDescription = "Add user"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.group_management_members),
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                ContainedLoadingIndicator()
            }
        } else if (users.isEmpty()) {
            Text(
                text = stringResource(R.string.group_management_no_members),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        } else {
            users.forEach { user ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${user.firstName} ${user.lastName}",
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = user.email,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = { onRemoveUser(user.id) }) {
                        Icon(
                            imageVector = Icons.Outlined.PersonRemove,
                            contentDescription = "Remove user",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
                HorizontalDivider()
            }
        }
    }
}
