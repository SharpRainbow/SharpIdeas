package ru.shrprnbw.ideas.domain.usecase

import android.util.Log
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
        val token = authRepository.login(email, password)
        Log.d("LoginUseCase", "Invoking login: $token")
        credentialsRepository.saveCredentials(email, password)
        credentialsRepository.saveToken(token)
    }

}