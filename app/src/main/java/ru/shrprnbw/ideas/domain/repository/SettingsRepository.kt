package ru.shrprnbw.ideas.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {

    suspend fun saveBaseUrl(url: String)

    fun getBaseUrl(): Flow<String>

}