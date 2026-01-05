package ru.shrprnbw.ideas.presentation.screens.profile_edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.shrprnbw.ideas.R
import ru.shrprnbw.ideas.domain.usecase.GetProfileInfoUseCase
import ru.shrprnbw.ideas.domain.usecase.UpdateUserInfoUseCase
import javax.inject.Inject

@HiltViewModel
class ProfileEditViewModel @Inject constructor(
    private val updateUserInfoUseCase: UpdateUserInfoUseCase,
    private val getProfileInfoUseCase: GetProfileInfoUseCase
): ViewModel() {

    private val _state = MutableStateFlow<ProfileEditState>(ProfileEditState.Editing())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            delay(500)
            getProfileInfo()
        }
    }

    fun processCommand(command: ProfileEditCommand) {
        when (command) {
            is ProfileEditCommand.InputEmail -> {
                _state.update {
                    if (it is ProfileEditState.Editing) {
                        it.copy(email = command.email, error = null)
                    } else {
                        it
                    }
                }
            }
            is ProfileEditCommand.InputUsername -> {
                _state.update {
                    if (it is ProfileEditState.Editing) {
                        it.copy(username = command.username, error = null)
                    } else {
                        it
                    }
                }
            }
            is ProfileEditCommand.SaveProfile -> {
                (_state.value as? ProfileEditState.Editing)?.let { currentState ->
                    viewModelScope.launch {
                        try {
                            updateUserInfoUseCase(
                                email = currentState.email,
                                username = currentState.username,
                            )
                            _state.update {
                                ProfileEditState.Finished
                            }
                        } catch (e: Exception) {
                            _state.update {
                                if (it is ProfileEditState.Editing) {
                                    it.copy(error = parseError(e))
                                } else {
                                    it
                                }
                            }
                            return@launch
                        }
                    }
                }
            }
        }
    }

    private fun parseError(e: Exception): Int {
        return e.message?.let { msg ->
            if (msg.contains("400")) {
                R.string.profile_invalid_data
            } else if (msg.contains("409")) {
                R.string.profile_data_conflict
            } else {
                R.string.error_unknown
            }
        } ?: R.string.error_unknown
    }

    private suspend fun getProfileInfo() {
        try {
            val profileInfo = getProfileInfoUseCase()
            _state.update {
                if (it is ProfileEditState.Editing) {
                    it.copy(
                        email = profileInfo.email,
                        username = profileInfo.username,
                        firstName = profileInfo.name,
                        lastName = profileInfo.surname
                    )
                } else {
                    it
                }
            }
        } catch (e: Exception) {
            _state.update {
                if (it is ProfileEditState.Editing) {
                    it.copy(
                        error = R.string.error_network
                    )
                } else {
                    it
                }
            }
        }
    }

}

sealed interface ProfileEditCommand {

    data class InputEmail(val email: String) : ProfileEditCommand

    data class InputUsername(val username: String) : ProfileEditCommand

    data object SaveProfile : ProfileEditCommand

}

sealed interface ProfileEditState {

    data class Editing(
        val email: String = "",
        val username: String = "",
        val firstName: String = "",
        val lastName: String = "",
        val error: Int? = null
    ) : ProfileEditState {
        val isSaveEnabled: Boolean
            get() = email.isNotBlank() && username.isNotBlank()
    }

    data object Finished : ProfileEditState

}