package ru.shrprnbw.ideas.di

import android.content.Context
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.shrprnbw.ideas.R
import java.security.SecureRandom
import java.util.Base64

@Module
@InstallIn(SingletonComponent::class)
object OauthModule {

    @Provides
    fun provideGetSignInWithGoogleOption(
        @ApplicationContext context: Context
    ): GetSignInWithGoogleOption {
        return GetSignInWithGoogleOption.Builder(
            context.getString(R.string.google_mobile_client_id)
        ).setNonce(generateSecureRandomNonce())
            .build()
    }

    @Provides
    fun provideGoogleSignInOptions(
        @ApplicationContext context: Context
    ): GetGoogleIdOption {
        return GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(true)
            .setServerClientId(context.getString(R.string.google_mobile_client_id))
            .setAutoSelectEnabled(false)
            .setNonce(generateSecureRandomNonce())
            .build()
    }

    private fun generateSecureRandomNonce(byteLength: Int = 32): String {
        val randomBytes = ByteArray(byteLength)
        SecureRandom.getInstanceStrong().nextBytes(randomBytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes)
    }

}