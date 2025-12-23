package ru.shrprnbw.ideas.presentation.screens.tag_management

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.shrprnbw.ideas.R
import ru.shrprnbw.ideas.domain.entity.Tag
import ru.shrprnbw.ideas.domain.usecase.CreateTagUseCase
import ru.shrprnbw.ideas.domain.usecase.DeleteTagUseCase
import ru.shrprnbw.ideas.domain.usecase.GetUserTagsUseCase
import ru.shrprnbw.ideas.domain.usecase.UpdateTagUseCase
import javax.inject.Inject

@HiltViewModel
class TagManagementViewModel @Inject constructor(
    private val getUserTagsUseCase: GetUserTagsUseCase,
    private val createTagUseCase: CreateTagUseCase,
    private val updateTagUseCase: UpdateTagUseCase,
    private val deleteTagUseCase: DeleteTagUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(TagManagementState())
    val state = _state.asStateFlow()

    init {
        loadTags()
    }

    fun processCommand(command: TagManagementCommand) {
        when (command) {
            is TagManagementCommand.InputNewTag -> {
                _state.update { it.copy(newTagInput = command.input) }
            }

            is TagManagementCommand.CreateTag -> {
                viewModelScope.launch {
                    try {
                        createTagUseCase(command.name)
                        _state.update { it.copy(newTagInput = "") }
                        loadTags()
                    } catch (e: Exception) {
                        _state.update { it.copy(error = R.string.tag_management_error_create) }
                    }
                }
            }

            is TagManagementCommand.StartEditTag -> {
                _state.update { previousState ->
                    previousState.copy(
                        editingTagId = command.tagId,
                        editingTagText = command.currentName
                    )
                }
            }

            is TagManagementCommand.CancelEditTag -> {
                _state.update { previousState ->
                    previousState.copy(
                        editingTagId = null,
                        editingTagText = ""
                    )
                }
            }

            is TagManagementCommand.UpdateTagInput -> {
                _state.update { previousState ->
                    previousState.copy(editingTagText = command.input)
                }
            }

            is TagManagementCommand.SaveTag -> {
                viewModelScope.launch {
                    try {
                        val newName = _state.value.editingTagText.trim()
                        _state.update { previousState ->
                            previousState.copy(
                                editingTagId = null,
                                editingTagText = ""
                            )
                        }
                        if (newName.isNotBlank() && newName != command.tag.name) {
                            updateTagUseCase(command.tag.id, newName)
                            loadTags()
                        }
                    } catch (e: Exception) {
                        Log.d("TagManagementViewModel", "Update tag error: ${e.message}")
                        _state.update { it.copy(error = R.string.tag_management_error_update) }
                    }
                }
            }

            is TagManagementCommand.DeleteTag -> {
                viewModelScope.launch {
                    try {
                        deleteTagUseCase(command.tagId)
                        loadTags()
                    } catch (e: Exception) {
                        Log.d("TagManagementViewModel", "Delete tag error: ${e.message}")
                        _state.update { it.copy(error = R.string.tag_management_error_delete) }
                    }
                }
            }
        }
    }

    private fun loadTags() {
        viewModelScope.launch {
            try {
                val tags = getUserTagsUseCase()
                _state.update { it.copy(tags = tags, error = null) }
            } catch (e: Exception) {
                Log.d("TagManagementViewModel", "Load tags error: ${e.message}")
                _state.update { it.copy(error = R.string.tag_management_error_load) }
            }
        }
    }
}

sealed interface TagManagementCommand {
    data class InputNewTag(val input: String) : TagManagementCommand
    data class CreateTag(val name: String) : TagManagementCommand
    data class StartEditTag(val tagId: Long, val currentName: String) : TagManagementCommand
    data class CancelEditTag(val tagId: Long) : TagManagementCommand
    data class UpdateTagInput(val input: String) : TagManagementCommand
    data class SaveTag(val tag: Tag) : TagManagementCommand
    data class DeleteTag(val tagId: Long) : TagManagementCommand
}

data class TagManagementState(
    val tags: List<Tag> = emptyList(),
    val newTagInput: String = "",
    val editingTagId: Long? = null,
    val editingTagText: String = "",
    val error: Int? = null
)
