package ru.shrprnbw.ideas.domain.usecase

import ru.shrprnbw.ideas.domain.repository.AuthRepository
import ru.shrprnbw.ideas.domain.repository.CredentialsRepository
import ru.shrprnbw.ideas.domain.repository.UserRepository
import javax.inject.Inject

class LoginWithYandexUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val credentialsRepository: CredentialsRepository,
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(yandexToken: String) {
        val tokens = authRepository.loginWithYandex(yandexToken)
        credentialsRepository.saveAccessToken(tokens.accessToken)
        val userData = userRepository.getUserInfo()
        credentialsRepository.saveRefreshToken(tokens.refreshToken, userData.email)
    }

}
