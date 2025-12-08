package ru.shrprnbw.ideas.presentation.screens.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.shrprnbw.ideas.R
import ru.shrprnbw.ideas.domain.usecase.RegisterUseCase
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
): ViewModel() {

    private val _state = MutableStateFlow<RegisterScreenState>(RegisterScreenState.Editing())
    val state = _state.asStateFlow()

    fun processCommand(command: RegisterCommand) {
        when (command) {
            is RegisterCommand.InputEmail -> {
                _state.update { previousState ->
                    if (previousState is RegisterScreenState.Editing) {
                        previousState.copy(email = command.email)
                    } else {
                        previousState
                    }
                }
            }

            is RegisterCommand.InputUsername -> {
                _state.update { previousState ->
                    if (previousState is RegisterScreenState.Editing) {
                        previousState.copy(username = command.username)
                    } else {
                        previousState
                    }
                }
            }

            is RegisterCommand.InputPassword -> {
                _state.update { previousState ->
                    if (previousState is RegisterScreenState.Editing) {
                        previousState.copy(password = command.password)
                    } else {
                        previousState
                    }
                }
            }

            is RegisterCommand.InputFirstName -> {
                _state.update { previousState ->
                    if (previousState is RegisterScreenState.Editing) {
                        previousState.copy(firstName = command.firstName)
                    } else {
                        previousState
                    }
                }
            }

            is RegisterCommand.InputLastName -> {
                _state.update { previousState ->
                    if (previousState is RegisterScreenState.Editing) {
                        previousState.copy(lastName = command.lastName)
                    } else {
                        previousState
                    }
                }
            }

            RegisterCommand.Submit -> {
                val currentState = _state.value
                if (currentState is RegisterScreenState.Editing && currentState.isSubmitEnabled) {
                    _state.update {
                        currentState.copy(isLoading = true, error = null)
                    }
                    viewModelScope.launch {
                        try {
                            registerUseCase(
                                username = currentState.username,
                                email = currentState.email,
                                password = currentState.password,
                                firstName = currentState.firstName,
                                lastName = currentState.lastName
                            )
                            _state.update {
                                RegisterScreenState.Registered
                            }
                        } catch (e: Exception) {
                            _state.update {
                                if (it is RegisterScreenState.Editing) {
                                    it.copy(isLoading = false, error = parseError(e))
                                } else {
                                    it
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun parseError(e: Exception): Int {
        return e.message?.let { msg ->
            if (msg.contains("409")) {
                R.string.registration_error_user_exists
            } else if (msg.contains("400")) {
                R.string.registration_error_invalid_data
            } else {
                R.string.error_unknown
            }
        } ?: R.string.error_unknown
    }

}

sealed interface RegisterCommand {
    data class InputEmail(val email: String) : RegisterCommand
    data class InputUsername(val username: String) : RegisterCommand
    data class InputPassword(val password: String) : RegisterCommand
    data class InputFirstName(val firstName: String) : RegisterCommand
    data class InputLastName(val lastName: String) : RegisterCommand
    object Submit : RegisterCommand
}

sealed interface RegisterScreenState {

    data class Editing(
        val username: String = "",
        val email: String = "",
        val password: String = "",
        val firstName: String = "",
        val lastName: String = "",
        val error: Int? = null,
        val isLoading: Boolean = false
    ) : RegisterScreenState {
        val isSubmitEnabled: Boolean
            get() = username.isNotBlank()
                    && email.isNotBlank()
                    && password.isNotBlank()
                    && firstName.isNotBlank()
                    && lastName.isNotBlank()
                    && !isLoading
    }

    data object Registered : RegisterScreenState

}