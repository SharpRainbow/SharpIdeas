package ru.shrprnbw.ideas.data.remote

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response
import ru.shrprnbw.ideas.domain.repository.SettingsRepository
import javax.inject.Inject

class BaseUrlInterceptor @Inject constructor(
    private val settingsRepository: SettingsRepository
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val base = runBlocking { settingsRepository.getBaseUrl().first() } .toHttpUrlOrNull()
            ?: return chain.proceed(request)

        val originalUrl = request.url
        val newUrl = originalUrl.newBuilder()
            .scheme(base.scheme)
            .host(base.host)
            .port(base.port)
            .build()

        val newRequest = request.newBuilder()
            .url(newUrl)
            .build()

        return chain.proceed(newRequest)
    }
}