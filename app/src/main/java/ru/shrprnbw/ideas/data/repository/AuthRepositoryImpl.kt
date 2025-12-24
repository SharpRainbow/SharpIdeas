package ru.shrprnbw.ideas.data.repository

import kotlinx.coroutines.flow.first
import ru.shrprnbw.ideas.data.JwtManager
import ru.shrprnbw.ideas.data.mapper.toEntity
import ru.shrprnbw.ideas.data.remote.IdeasApiService
import ru.shrprnbw.ideas.data.remote.dto.request.GoogleTokenAuthRequest
import ru.shrprnbw.ideas.data.remote.dto.request.LoginRequestDto
import ru.shrprnbw.ideas.data.remote.dto.request.RefreshRequest
import ru.shrprnbw.ideas.data.remote.dto.request.RegisterRequest
import ru.shrprnbw.ideas.domain.entity.TokenPair
import ru.shrprnbw.ideas.domain.repository.AuthRepository
import ru.shrprnbw.ideas.domain.repository.CredentialsRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val apiService: IdeasApiService,
    private val credentialsRepository: CredentialsRepository,
    private val jwtManager: JwtManager
) : AuthRepository {

    override suspend fun register(
        username: String,
        email: String,
        password: String,
        firstName: String,
        lastName: String
    ) {
        apiService.register(
            RegisterRequest(
                username = username,
                email = email,
                password = password,
                firstName = firstName,
                lastName = lastName
            )
        )
    }

    override suspend fun login(login: String, password: String): TokenPair {
        return apiService.loginV2(
            LoginRequestDto(
                email = login,
                password = password
            )
        ).toEntity()
    }

    override suspend fun logout() {
        credentialsRepository.clearCredentials()
    }

    override suspend fun getValidToken(): String { // TODO: check server time vs local time
        val token = credentialsRepository.getAccessToken().first()
        return if (jwtManager.isTokenValid(token)) {
            "Bearer $token"
        } else {
            val refreshToken = credentialsRepository.getRefreshToken().first()
            if (refreshToken.isNotBlank() && jwtManager.isRefreshTokenValid(refreshToken)) {
                refreshAccessToken(refreshToken)
            } else {
                loginWithCredentials()
            }
            "Bearer ${credentialsRepository.getAccessToken().first()}"
        }
    }

    override suspend fun loginWithGoogle(idToken: String): TokenPair {
        return apiService.loginWithGoogle(
            GoogleTokenAuthRequest(
                idToken = idToken
            )
        ).toEntity()
    }

    private suspend fun refreshAccessToken(refreshToken: String) {
        val refreshResponse = apiService.refreshAccessToken(
            RefreshRequest(
                refreshToken = refreshToken
            )
        )
        val login = credentialsRepository.getSavedLogin().first()
        credentialsRepository.saveRefreshToken(refreshResponse.refreshToken, login)
        credentialsRepository.saveAccessToken(refreshResponse.accessToken)
    }

    private suspend fun loginWithCredentials() {
        val login = credentialsRepository.getSavedLogin().first()
        val password = credentialsRepository.getSavedPassword().first()
        if (login.isNotBlank() && password.isNotBlank()) {
            val loginResponse = login(login, password)
            credentialsRepository.saveAccessToken(loginResponse.accessToken)
            credentialsRepository.saveRefreshToken(loginResponse.refreshToken, login)
        }
    }

}