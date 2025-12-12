package ru.shrprnbw.ideas.domain.usecase

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class GetGoogleSignInKey @Inject constructor(
    private val signInWithGoogleOption: GetSignInWithGoogleOption,
) {

    suspend operator fun invoke(@ActivityContext context: Context): String {
        val getCredentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(signInWithGoogleOption)
            .build()

        val credentialManager = CredentialManager.create(context)
        val credential = credentialManager.getCredential(
            request = getCredentialRequest,
            context = context
        ).credential
        when (credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential
                            .createFrom(credential.data)
                        return googleIdTokenCredential.idToken
                    } catch (e: GoogleIdTokenParsingException) {
                        throw Exception("Received an invalid google id token response", e)
                    }
                } else {
                    throw IllegalArgumentException("Unexpected type of credential")
                }
            }

            else -> {
                throw IllegalArgumentException("Unexpected type of credential")
            }
        }
    }

}