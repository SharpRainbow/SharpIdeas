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

private const val AUTH_PREFERENCE_NAME = "authentication"

private val Context.authDataStore: DataStore<Preferences> by preferencesDataStore(name = AUTH_PREFERENCE_NAME)

class CredentialsRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val encryptionManager: EncryptionManager
) : CredentialsRepository {
    override suspend fun saveCredentials(login: String, password: String) {
        val encryptedPassword = encryptionManager.encryptData(password, login)
        context.authDataStore.edit { preferences ->
            preferences[LOGIN_KEY] = login
            preferences[PASSWORD_KEY] = encryptedPassword
        }
    }

    override suspend fun saveAccessToken(token: String) {
        context.authDataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = token
        }
    }

    override suspend fun saveRefreshToken(token: String, login: String) {
        val encryptedToken = encryptionManager.encryptData(token, login)
        context.authDataStore.edit { preferences ->
            preferences[LOGIN_KEY] = login
            preferences[REFRESH_TOKEN_KEY] = encryptedToken
        }
    }

    override suspend fun clearCredentials() {
        context.authDataStore.edit {
            it.clear()
        }
    }

    override fun getAccessToken(): Flow<String> {
        return context.authDataStore.data.map { preferences ->
            preferences[ACCESS_TOKEN_KEY] ?: ""
        }
    }

    override fun getRefreshToken(): Flow<String> {
        return context.authDataStore.data.map { preferences ->
            preferences[LOGIN_KEY]?.let { login ->
                preferences[REFRESH_TOKEN_KEY]?.let { password ->
                    encryptionManager.decryptData(password, login)
                }
            } ?: ""
        }
    }

    override fun getSavedLogin(): Flow<String> {
        return context.authDataStore.data.map { preferences ->
            preferences[LOGIN_KEY] ?: ""
        }
    }

    override fun getSavedPassword(): Flow<String> {
        return context.authDataStore.data.map { preferences ->
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
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("TOKEN_ACCESS")

        private val REFRESH_TOKEN_KEY = stringPreferencesKey("TOKEN_REFRESH")

    }
}