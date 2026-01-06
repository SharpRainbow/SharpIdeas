package ru.shrprnbw.ideas.domain.repository

import ru.shrprnbw.ideas.domain.entity.TokenPair

interface AuthRepository {

    suspend fun register(
        username: String,
        email: String,
        password: String,
        firstName: String,
        lastName: String
    )

    suspend fun login(login: String, password: String): TokenPair

    suspend fun logout()

    suspend fun getValidToken(): String

    suspend fun loginWithGoogle(idToken: String): TokenPair

    suspend fun checkServerConnection(): Boolean

}