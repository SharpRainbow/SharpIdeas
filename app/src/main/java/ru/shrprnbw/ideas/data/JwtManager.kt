package ru.shrprnbw.ideas.data

import org.json.JSONObject
import ru.shrprnbw.ideas.domain.entity.TokenEmbeddedData
import javax.inject.Inject
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
class JwtManager @Inject constructor() {

    fun parseJwtToken(token: String): TokenEmbeddedData {
        val payload = JSONObject(
            String(
                Base64.withPadding(Base64.PaddingOption.PRESENT_OPTIONAL)
                    .decode(token.split('.')[1])
            )
        )
        return TokenEmbeddedData(
            email = payload.getString("sub")
        )
    }

    fun isTokenValid(token: String): Boolean {
        return try {
            val payload = JSONObject(
                String(
                    Base64.withPadding(
                        Base64.PaddingOption.PRESENT_OPTIONAL
                    ).decode(token.split('.')[1])
                )
            )
            payload.getLong("exp") > System.currentTimeMillis() / 1000
        } catch (e: Exception) {
            false
        }
    }

}