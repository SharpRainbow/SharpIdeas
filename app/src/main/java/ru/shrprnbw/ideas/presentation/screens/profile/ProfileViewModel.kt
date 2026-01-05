package ru.shrprnbw.ideas.presentation.screens.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.shrprnbw.ideas.domain.usecase.GetProfileInfoUseCase
import ru.shrprnbw.ideas.domain.usecase.LogoutUseCase
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getProfileInfoUseCase: GetProfileInfoUseCase,
    private val logoutUseCase: LogoutUseCase
): ViewModel() {

    private val _state = MutableStateFlow<ProfileScreenState>(ProfileScreenState.Displaying())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            delay(500)
            loadProfileData()
        }
    }

    fun processCommand(command: ProfileScreenCommand) {
        when (command) {
            is ProfileScreenCommand.EditProfile -> {
                _state.update {
                    ProfileScreenState.GoToEditProfile
                }
            }
            is ProfileScreenCommand.Logout -> {
                viewModelScope.launch {
                    logoutUseCase()
                    _state.update {
                        ProfileScreenState.Logout
                    }
                }
            }

            ProfileScreenCommand.RefreshProfile -> {
                viewModelScope.launch {
                    loadProfileData()
                }
            }
        }
    }

    private suspend fun loadProfileData() {
        _state.update { previousState ->
            if (previousState is ProfileScreenState.Displaying) {
                previousState.copy(isRefreshing = true)
            } else {
                previousState
            }
        }
        try {
            val userInfo = getProfileInfoUseCase()
            _state.update {
                ProfileScreenState.Displaying(
                    username = userInfo.username,
                    firstName = userInfo.name,
                    lastName = userInfo.surname,
                    email = userInfo.email
                )
            }
        } catch (e: Exception) {
            Log.e("ProfileViewModel", "Error loading profile info", e)
            _state.update {
                ProfileScreenState.Error(
                    message = "Failed to load profile information."
                )
            }
        }
    }

}

sealed interface ProfileScreenCommand {

    data object EditProfile : ProfileScreenCommand

    data object Logout : ProfileScreenCommand

    data object RefreshProfile : ProfileScreenCommand

}

sealed interface ProfileScreenState {

    data class Displaying(
        val username: String = "",
        val firstName: String = "",
        val lastName: String = "",
        val email: String = "",
        val isRefreshing: Boolean = false
    ) : ProfileScreenState

    data class Error(
        val message: String
    ) : ProfileScreenState

    data object GoToEditProfile : ProfileScreenState

    data object Logout : ProfileScreenState

}