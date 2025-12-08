package ru.shrprnbw.ideas.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.shrprnbw.ideas.data.EncryptionManager
import ru.shrprnbw.ideas.domain.repository.CredentialsRepository
import javax.inject.Inject

private const val PREFERENCE_NAME = "authentication"

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFERENCE_NAME)

class CredentialsRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val encryptionManager: EncryptionManager
) : CredentialsRepository {
    override suspend fun saveCredentials(login: String, password: String) {
        val encryptedPassword = encryptionManager.encryptData(password, login)
        context.dataStore.edit { preferences ->
            preferences[LOGIN_KEY] = login
            preferences[PASSWORD_KEY] = encryptedPassword
        }
    }

    override suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    override suspend fun clearCredentials() {
        context.dataStore.edit {
            it.clear()
        }
    }

    override fun getToken(): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[TOKEN_KEY] ?: ""
        }
    }

    override fun getSavedLogin(): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[LOGIN_KEY] ?: ""
        }
    }

    override fun getSavedPassword(): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[LOGIN_KEY]?.let { login ->
                preferences[PASSWORD_KEY]?.let { password ->
                    encryptionManager.decryptData(password, login)
                }
            } ?: ""
        }
    }

    companion object {
        private val LOGIN_KEY = stringPreferencesKey("LOGIN")
        private val PASSWORD_KEY = stringPreferencesKey("PASS")
        private val TOKEN_KEY = stringPreferencesKey("TOKEN")

    }
}