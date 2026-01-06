package ru.shrprnbw.ideas.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.shrprnbw.ideas.di.ApiModule
import ru.shrprnbw.ideas.domain.repository.SettingsRepository

private const val SETTINGS_PREFERENCE_NAME = "settings"

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = SETTINGS_PREFERENCE_NAME)

class SettingsRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : SettingsRepository {

    override suspend fun saveBaseUrl(url: String) {
        context.settingsDataStore.edit { preferences ->
            preferences[BASE_URL_KEY] = url
        }
    }

    override fun getBaseUrl(): Flow<String> {
        return context.settingsDataStore.data.map { preferences ->
            preferences[BASE_URL_KEY] ?: ApiModule.DEFAULT_BASE_URL
        }
    }

    companion object {
        private val BASE_URL_KEY = stringPreferencesKey("BASE_URL")
    }
}