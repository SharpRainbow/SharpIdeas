package ru.shrprnbw.ideas.domain.repository

import kotlinx.coroutines.flow.Flow

interface CredentialsRepository {

    suspend fun saveCredentials(login: String, password: String)

    suspend fun saveToken(token: String)

    suspend fun clearCredentials()

    fun getToken(): Flow<String>

    fun getSavedLogin(): Flow<String>

    fun getSavedPassword(): Flow<String>

}