package ru.shrprnbw.ideas.domain.repository

import kotlinx.coroutines.flow.Flow

interface CredentialsRepository {

    suspend fun saveCredentials(login: String, password: String)

    suspend fun saveAccessToken(token: String)

    suspend fun saveRefreshToken(token: String, login: String)

    suspend fun clearCredentials()

    fun getAccessToken(): Flow<String>

    fun getRefreshToken(): Flow<String>

    fun getSavedLogin(): Flow<String>

    fun getSavedPassword(): Flow<String>

}