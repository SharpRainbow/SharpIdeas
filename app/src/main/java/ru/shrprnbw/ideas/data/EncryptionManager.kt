package ru.shrprnbw.ideas.data

import android.util.Base64
import com.google.crypto.tink.Aead
import javax.inject.Inject

class EncryptionManager @Inject constructor(
    private val aead: Aead
) {

    fun encryptData(data: String, associatedInfo: String): String {
        val ciphertext = aead.encrypt(
            data.toByteArray(Charsets.UTF_8),
            associatedInfo.toByteArray(Charsets.UTF_8)
        )
        return Base64.encodeToString(ciphertext, Base64.DEFAULT)
    }

    fun decryptData(ciphertextBase64: String, associatedInfo: String): String {
        val ciphertext = Base64.decode(ciphertextBase64, Base64.DEFAULT)
        val plaintext = aead.decrypt(
            ciphertext,
            associatedInfo.toByteArray(Charsets.UTF_8)
        )
        return String(plaintext, Charsets.UTF_8)
    }

}