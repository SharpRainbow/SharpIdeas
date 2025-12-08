package ru.shrprnbw.ideas.di

import android.app.Application
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeysetHandle
import com.google.crypto.tink.RegistryConfiguration
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.aead.AesGcmKeyManager
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object EncryptionModule {

    private const val PREF_FILE = "ideas_secure_prefs"
    private const val MASTER_KEY_URI = "android-keystore://ideas_master_key"
    private const val KEYSET_NAME = "ideas_keyset"

    @Provides
    @Singleton
    fun provideAead(application: Application): Aead {
        AeadConfig.register()

        val keysetHandle: KeysetHandle = AndroidKeysetManager.Builder()
            .withSharedPref(
                application,
                "${application.packageName}.${KEYSET_NAME}",
                "${application.packageName}.${PREF_FILE}")
            .withKeyTemplate(AesGcmKeyManager.aes256GcmTemplate())
            .withMasterKeyUri(MASTER_KEY_URI)
            .build()
            .keysetHandle

        return keysetHandle.getPrimitive(
            RegistryConfiguration.get(),
            Aead::class.java
        )
    }

}