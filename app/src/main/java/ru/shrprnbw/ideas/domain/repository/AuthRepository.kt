package ru.shrprnbw.ideas.domain.repository

interface AuthRepository {

    suspend fun register(
        username: String,
        email: String,
        password: String,
        firstName: String,
        lastName: String
    )

    suspend fun login(login: String, password: String): String

    suspend fun logout()

    suspend fun getValidToken(): String

}