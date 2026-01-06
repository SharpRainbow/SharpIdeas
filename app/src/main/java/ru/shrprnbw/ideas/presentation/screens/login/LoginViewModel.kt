package ru.shrprnbw.ideas.presentation.screens.login

import android.content.Context
import android.content.Intent
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.shrprnbw.ideas.R
import ru.shrprnbw.ideas.domain.usecase.CheckServerConnectionUseCase
import ru.shrprnbw.ideas.domain.usecase.GetBaseUrlUseCase
import ru.shrprnbw.ideas.domain.usecase.GetGoogleSignInKey
import ru.shrprnbw.ideas.domain.usecase.IsLoggedInUseCase
import ru.shrprnbw.ideas.domain.usecase.LoginUseCase
import ru.shrprnbw.ideas.domain.usecase.LoginWithGoogleUseCase
import ru.shrprnbw.ideas.domain.usecase.SetBaseUrlUseCase
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val isLoggedInUseCase: IsLoggedInUseCase,
    private val getGoogleSignInKey: GetGoogleSignInKey,
    private val loginWithGoogleUseCase: LoginWithGoogleUseCase,
    private val getBaseUrlUseCase: GetBaseUrlUseCase,
    private val setBaseUrlUseCase: SetBaseUrlUseCase,
    private val checkServerConnectionUseCase: CheckServerConnectionUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<LoginScreenState>(LoginScreenState.InputData())
    val state = _state.asStateFlow()

    init {
        observeLoginState()
        observeBaseUrl()
    }

    fun processCommand(command: LoginCommand) {
        when (command) {
            is LoginCommand.InputEmail -> {
                _state.update { previousState ->
                    if (previousState is LoginScreenState.InputData) {
                        previousState.copy(email = command.email)
                    } else {
                        previousState
                    }
                }
            }

            is LoginCommand.InputPassword -> {
                _state.update { previousState ->
                    if (previousState is LoginScreenState.InputData) {
                        previousState.copy(password = command.password)
                    } else {
                        previousState
                    }
                }
            }

            is LoginCommand.Login -> {
                _state.update { previousState ->
                    if (previousState is LoginScreenState.InputData) {
                        previousState.copy(isLoading = true, errorMessage = null)
                    } else {
                        previousState
                    }
                }
                viewModelScope.launch {
                    (_state.value as? LoginScreenState.InputData)?.let { state ->
                        setBaseUrlUseCase(state.serverUrl)
                        runCatching {
                            if (checkServerConnectionUseCase()) {
                                Result.success(Unit)
                            } else {
                                Result.failure(Exception("Unable to connect to server"))
                            }
                        }.onSuccess {
                            try {
                                loginUseCase(
                                    state.email,
                                    state.password
                                )
                            } catch (e: Exception) {
                                _state.update { previousState ->
                                    if (previousState is LoginScreenState.InputData) {
                                        previousState.copy(
                                            isLoading = false,
                                            errorMessage = parseErrorMessage(e)
                                        )
                                    } else {
                                        previousState
                                    }
                                }
                            }
                        }.onFailure {
                            _state.update { previousState ->
                                if (previousState is LoginScreenState.InputData) {
                                    previousState.copy(
                                        isLoading = false,
                                        errorMessage = R.string.login_error_invalid_server_url
                                    )
                                } else {
                                    previousState
                                }
                            }
                        }
                    }
                }
            }

            is LoginCommand.LoginWithGoogle -> {
                viewModelScope.launch {
                    try {
                        val idToken = getGoogleSignInKey(command.context)
                        if (idToken.isBlank()) {
                            throw Exception("No idToken from Google")
                        }
                        loginWithGoogleUseCase(idToken)
                    } catch (e: Exception) {
                        _state.update { previousState ->
                            if (previousState is LoginScreenState.InputData) {
                                previousState.copy(
                                    isLoading = false,
                                    errorMessage = R.string.registration_error_google_error
                                )
                            } else {
                                previousState
                            }
                        }
                    }
                }
            }

            is LoginCommand.ResetErrorMessage -> {
                _state.update { previousState ->
                    if (previousState is LoginScreenState.InputData) {
                        previousState.copy(errorMessage = null)
                    } else {
                        previousState
                    }
                }
            }

            is LoginCommand.InputServerUrl -> {
                _state.update { previousState ->
                    if (previousState is LoginScreenState.InputData) {
                        previousState.copy(serverUrl = command.url)
                    } else {
                        previousState
                    }
                }
            }
        }
    }

    @StringRes
    private fun parseErrorMessage(exception: Exception): Int {
        return exception.message?.let { msg ->
            if(msg.contains("401")) {
                R.string.login_error_invalid_credentials
            } else if(msg.contains("404")) {
                R.string.login_error_user_not_found
            } else {
                R.string.error_unknown
            }
        } ?: R.string.error_unknown
    }

    private fun observeLoginState() {
        viewModelScope.launch {
            isLoggedInUseCase()
                .distinctUntilChanged()
                .onEach { isLoggedIn ->
                    if (isLoggedIn) {
                        _state.value = LoginScreenState.LoggedIn
                    }
                }
                .launchIn(viewModelScope)
        }
    }

    private fun observeBaseUrl() {
        viewModelScope.launch {
            getBaseUrlUseCase()
                .distinctUntilChanged()
                .onEach { url ->
                    _state.update { previousState ->
                        if (previousState is LoginScreenState.InputData) {
                            previousState.copy(serverUrl = url)
                        } else {
                            previousState
                        }
                    }
                }
                .launchIn(viewModelScope)
        }
    }

}

sealed interface LoginCommand {

    data class InputEmail(val email: String) : LoginCommand

    data class InputPassword(val password: String) : LoginCommand

    data object Login : LoginCommand

    data class LoginWithGoogle(@param:ActivityContext val context: Context) : LoginCommand

    data object ResetErrorMessage : LoginCommand

    data class InputServerUrl(val url: String) : LoginCommand
}

sealed interface LoginScreenState {

    data class InputData(
        val email: String = "",
        val password: String = "",
        val isLoading: Boolean = false,
        val errorMessage: Int? = null,
        val signInIntent: Intent? = null,
        val serverUrl: String = ""
    ) : LoginScreenState {

        val isLoginEnabled: Boolean
            get() = !isLoading && email.isNotBlank() && password.isNotBlank()

    }

    data object LoggedIn : LoginScreenState

}