@file:OptIn(ExperimentalCoroutinesApi::class)

package ru.shrprnbw.ideas.presentation.screens.group_management

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.shrprnbw.ideas.R
import ru.shrprnbw.ideas.domain.entity.Group
import ru.shrprnbw.ideas.domain.entity.User
import ru.shrprnbw.ideas.domain.usecase.AddUserToGroupUseCase
import ru.shrprnbw.ideas.domain.usecase.CreateGroupUseCase
import ru.shrprnbw.ideas.domain.usecase.DeleteGroupUseCase
import ru.shrprnbw.ideas.domain.usecase.GetGroupUsersUseCase
import ru.shrprnbw.ideas.domain.usecase.GetOwnedGroupsUseCase
import ru.shrprnbw.ideas.domain.usecase.InvalidateGroupUsersUseCase
import ru.shrprnbw.ideas.domain.usecase.RemoveUserFromGroupUseCase
import ru.shrprnbw.ideas.domain.usecase.UpdateGroupUseCase
import javax.inject.Inject

@HiltViewModel
class GroupManagementViewModel @Inject constructor(
    private val getOwnedGroupsUseCase: GetOwnedGroupsUseCase,
    private val createGroupUseCase: CreateGroupUseCase,
    private val updateGroupUseCase: UpdateGroupUseCase,
    private val deleteGroupUseCase: DeleteGroupUseCase,
    private val getGroupUsersUseCase: GetGroupUsersUseCase,
    private val addUserToGroupUseCase: AddUserToGroupUseCase,
    private val removeUserFromGroupUseCase: RemoveUserFromGroupUseCase,
    private val invalidateGroupUsersUseCase: InvalidateGroupUsersUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(GroupManagementState())
    val state = _state.asStateFlow()

    val groupUsersFlow: Flow<PagingData<User>> = _state.map {
        it.selectedGroup
    }.filterNotNull().distinctUntilChanged().flatMapLatest {
        getGroupUsersUseCase(it.id)
    }.cachedIn(viewModelScope)

    val groupsList = getOwnedGroupsUseCase()
        .catch { e ->
            Log.e("GroupManagementVM", "Error loading groups", e)
            _state.update { it.copy(error = R.string.group_management_error_load) }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun processCommand(command: GroupManagementCommand) {
        when (command) {
            is GroupManagementCommand.InputNewGroup -> {
                _state.update { it.copy(newGroupInput = command.input) }
            }

            is GroupManagementCommand.CreateGroup -> {
                viewModelScope.launch {
                    try {
                        createGroupUseCase(command.name)
                        _state.update { it.copy(newGroupInput = "") }
                    } catch (e: Exception) {
                        Log.d("GroupManagementVM", "Create group error: ${e.message}")
                        _state.update { it.copy(error = R.string.group_management_error_create) }
                    }
                }
            }

            is GroupManagementCommand.StartEditGroup -> {
                _state.update { previousState ->
                    previousState.copy(
                        editingGroupId = command.groupId,
                        editingGroupText = command.currentName
                    )
                }
            }

            is GroupManagementCommand.CancelEditGroup -> {
                _state.update { previousState ->
                    previousState.copy(
                        editingGroupId = null,
                        editingGroupText = ""
                    )
                }
            }

            is GroupManagementCommand.UpdateGroupInput -> {
                _state.update { previousState ->
                    previousState.copy(editingGroupText = command.input)
                }
            }

            is GroupManagementCommand.SaveGroup -> {
                viewModelScope.launch {
                    try {
                        val newName = _state.value.editingGroupText.trim()
                        _state.update { previousState ->
                            previousState.copy(
                                editingGroupId = null,
                                editingGroupText = ""
                            )
                        }
                        if (newName.isNotBlank() && newName != command.group.name) {
                            updateGroupUseCase(command.group.id, newName)
                        }
                    } catch (e: Exception) {
                        Log.d("GroupManagementVM", "Update group error: ${e.message}")
                        _state.update { it.copy(error = R.string.group_management_error_update) }
                    }
                }
            }

            is GroupManagementCommand.DeleteGroup -> {
                viewModelScope.launch {
                    try {
                        deleteGroupUseCase(command.groupId)
                        if (_state.value.selectedGroup?.id == command.groupId) {
                            _state.update {
                                it.copy(
                                    selectedGroup = null,
//                                    groupUsers = emptyList()
                                )
                            }
                        }
                    } catch (e: Exception) {
                        Log.d("GroupManagementVM", "Delete group error: ${e.message}")
                        _state.update { it.copy(error = R.string.group_management_error_delete) }
                    }
                }
            }

            is GroupManagementCommand.SelectGroup -> {
                _state.update {
                    it.copy(
                        selectedGroup = command.group,
//                        isLoadingUsers = true,
//                        groupUsers = emptyList()
                    )
                }
//                loadGroupUsers(command.group.id)
            }

            GroupManagementCommand.DeselectGroup -> {
                _state.update {
                    it.copy(
                        selectedGroup = null,
//                        groupUsers = emptyList(),
                        addUserInput = ""
                    )
                }
            }

            is GroupManagementCommand.InputAddUser -> {
                _state.update { it.copy(addUserInput = command.input) }
            }

            is GroupManagementCommand.ToggleAddByEmail -> {
                _state.update { it.copy(addUserByEmail = command.byEmail) }
            }

            is GroupManagementCommand.AddUserToGroup -> {
                val userData = _state.value.addUserInput.trim()
                val byEmail = _state.value.addUserByEmail
                if (userData.isNotBlank()) {
                    viewModelScope.launch {
                        try {
                            addUserToGroupUseCase(command.groupId, userData, byEmail)
                            _state.update { it.copy(addUserInput = "") }
                            invalidateGroupUsersUseCase()
                        } catch (e: Exception) {
                            Log.d("GroupManagementVM", "Add user error: ${e.message}")
                            val errorRes = when {
                                e.message?.contains("404") == true -> R.string.group_management_error_user_not_found
                                e.message?.contains("409") == true -> R.string.group_management_error_user_already_in_group
                                else -> R.string.group_management_error_add_user
                            }
                            _state.update { it.copy(error = errorRes) }
                        }
                    }
                }
            }

            is GroupManagementCommand.RemoveUserFromGroup -> {
                viewModelScope.launch {
                    try {
                        removeUserFromGroupUseCase(command.groupId, command.userId)
                        invalidateGroupUsersUseCase()
                    } catch (e: Exception) {
                        Log.d("GroupManagementVM", "Remove user error: ${e.message}")
                        _state.update { it.copy(error = R.string.group_management_error_remove_user) }
                    }
                }
            }

            GroupManagementCommand.ResetError -> {
                _state.update { it.copy(error = null) }
            }
        }
    }

//    private fun loadGroupUsers(groupId: Long) {
//        viewModelScope.launch {
//            try {
//                val users = getGroupUsersUseCase(groupId)
//                _state.update {
//                    it.copy(
//                        groupUsers = users,
//                        isLoadingUsers = false
//                    )
//                }
//            } catch (e: Exception) {
//                Log.d("GroupManagementVM", "Load users error: ${e.message}")
//                _state.update {
//                    it.copy(
//                        isLoadingUsers = false,
//                        error = R.string.group_management_error_load_users
//                    )
//                }
//            }
//        }
//    }

}

sealed interface GroupManagementCommand {
    data class InputNewGroup(val input: String) : GroupManagementCommand
    data class CreateGroup(val name: String) : GroupManagementCommand
    data class StartEditGroup(val groupId: Long, val currentName: String) : GroupManagementCommand
    data class CancelEditGroup(val groupId: Long) : GroupManagementCommand
    data class UpdateGroupInput(val input: String) : GroupManagementCommand
    data class SaveGroup(val group: Group) : GroupManagementCommand
    data class DeleteGroup(val groupId: Long) : GroupManagementCommand
    data class SelectGroup(val group: Group) : GroupManagementCommand
    data object DeselectGroup : GroupManagementCommand
    data class InputAddUser(val input: String) : GroupManagementCommand
    data class ToggleAddByEmail(val byEmail: Boolean) : GroupManagementCommand
    data class AddUserToGroup(val groupId: Long) : GroupManagementCommand
    data class RemoveUserFromGroup(val groupId: Long, val userId: String) : GroupManagementCommand
    data object ResetError : GroupManagementCommand
}

data class GroupManagementState(
    val newGroupInput: String = "",
    val editingGroupId: Long? = null,
    val editingGroupText: String = "",
    val selectedGroup: Group? = null,
//    val groupUsers: List<GroupUser> = emptyList(),
    val addUserInput: String = "",
    val addUserByEmail: Boolean = true,
//    val isLoadingUsers: Boolean = false,
    val error: Int? = null
)
