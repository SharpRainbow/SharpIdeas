package ru.shrprnbw.ideas.data.repository

import kotlinx.coroutines.flow.first
import ru.shrprnbw.ideas.data.JwtManager
import ru.shrprnbw.ideas.data.remote.IdeasApiService
import ru.shrprnbw.ideas.data.remote.dto.request.LoginRequestDto
import ru.shrprnbw.ideas.domain.repository.AuthRepository
import ru.shrprnbw.ideas.domain.repository.CredentialsRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val apiService: IdeasApiService,
    private val credentialsRepository: CredentialsRepository,
    private val jwtManager: JwtManager
) : AuthRepository {

    override suspend fun login(login: String, password: String): String {
        return apiService.login(
            LoginRequestDto(login, password)
        ).token
    }

    override suspend fun logout() {
        credentialsRepository.clearCredentials()
    }

    override suspend fun getValidToken(): String {
        var token = credentialsRepository.getToken().first()
        if (jwtManager.isTokenValid(token)) {
            token = "Bearer $token"
        } else {
            val login = credentialsRepository.getSavedLogin().first()
            val password = credentialsRepository.getSavedPassword().first()
            if (login.isNotBlank() && password.isNotBlank()) {
                val newToken = login(login, password)
                credentialsRepository.saveToken(newToken)
            }
            return "Bearer ${credentialsRepository.getToken().first()}"
        }
        return token
    }

}