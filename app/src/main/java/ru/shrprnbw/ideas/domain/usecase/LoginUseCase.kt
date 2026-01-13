package ru.shrprnbw.ideas.domain.usecase

import ru.shrprnbw.ideas.domain.repository.AuthRepository
import ru.shrprnbw.ideas.domain.repository.CredentialsRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val credentialsRepository: CredentialsRepository
) {

    suspend operator fun invoke(
        email: String,
        password: String
    ) {
        val tokens = authRepository.login(email, password)
        credentialsRepository.saveCredentials(email, password)
        credentialsRepository.saveRefreshToken(tokens.refreshToken, email)
        credentialsRepository.saveAccessToken(tokens.accessToken)
    }

}