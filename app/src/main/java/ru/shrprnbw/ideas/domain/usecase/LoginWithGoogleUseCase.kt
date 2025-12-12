package ru.shrprnbw.ideas.domain.usecase

import ru.shrprnbw.ideas.domain.repository.AuthRepository
import ru.shrprnbw.ideas.domain.repository.CredentialsRepository
import ru.shrprnbw.ideas.domain.repository.UserRepository
import javax.inject.Inject

class LoginWithGoogleUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val credentialsRepository: CredentialsRepository,
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(idToken: String) {
        val tokens = authRepository.loginWithGoogle(idToken)
        credentialsRepository.saveAccessToken(tokens.accessToken)
        val userData = userRepository.getUserInfo()
        credentialsRepository.saveRefreshToken(tokens.refreshToken,userData.email)
    }

}