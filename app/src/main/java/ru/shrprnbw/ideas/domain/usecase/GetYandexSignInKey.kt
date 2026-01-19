package ru.shrprnbw.ideas.domain.usecase

import android.content.Context
import androidx.activity.ComponentActivity
import com.yandex.authsdk.YandexAuthLoginOptions
import com.yandex.authsdk.YandexAuthOptions
import com.yandex.authsdk.YandexAuthResult
import com.yandex.authsdk.YandexAuthSdk
import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.coroutines.suspendCancellableCoroutine
import ru.shrprnbw.ideas.utils.OperationCancelledException
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class GetYandexSignInKey @Inject constructor() {

    suspend operator fun invoke(@ActivityContext context: Context): String {
        return suspendCancellableCoroutine { continuation ->
            val sdk = YandexAuthSdk.create(YandexAuthOptions(context))

            if (context !is ComponentActivity) {
                continuation.resumeWithException(
                    IllegalArgumentException("Context must be a ComponentActivity")
                )
                return@suspendCancellableCoroutine
            }

            val launcher = context.activityResultRegistry.register(
                "yandex_auth_${System.currentTimeMillis()}",
                sdk.contract
            ) { result ->
                when (result) {
                    is YandexAuthResult.Success -> {
                        continuation.resume(result.token.value)
                    }
                    is YandexAuthResult.Failure -> {
                        continuation.resumeWithException(
                            Exception("Yandex auth failed: ${result.exception.message}", result.exception)
                        )
                    }
                    YandexAuthResult.Cancelled -> {
                        continuation.resumeWithException(
                            OperationCancelledException("Yandex auth cancelled by user")
                        )
                    }
                }
            }

            continuation.invokeOnCancellation {
                launcher.unregister()
            }

            try {
                val loginOptions = YandexAuthLoginOptions()
                launcher.launch(loginOptions)
            } catch (e: Exception) {
                continuation.resumeWithException(e)
            }
        }
    }
}
