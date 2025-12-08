package ru.shrprnbw.ideas.domain.repository

interface AuthRepository {

    suspend fun login(login: String, password: String): String

    suspend fun logout()

    suspend fun getValidToken(): String

}