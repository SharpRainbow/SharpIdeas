package ru.shrprnbw.ideas.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create
import ru.shrprnbw.ideas.data.remote.BaseUrlInterceptor
import ru.shrprnbw.ideas.data.remote.IdeasApiService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    const val DEFAULT_BASE_URL = "http://192.168.0.18:8080/"//"https://v603193.hosted-by-vdsina.com/"
    const val FIRST_PAGE_INDEX = 0

    @Provides
    @Singleton
    fun provideJson(): Json {
        return Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }
    }

    @Provides
    @Singleton
    fun provideConverterFactory(json: Json): Converter.Factory {
        return json.asConverterFactory(
            "application/json".toMediaType()
        )
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        converterFactory: Converter.Factory,
        baseUrlInterceptor: BaseUrlInterceptor
    ): Retrofit {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        } // TODO: Debug only! Remove later
        return Retrofit.Builder()
            .baseUrl(DEFAULT_BASE_URL)
            .client(
                okhttp3.OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .addInterceptor(baseUrlInterceptor)
                    .build()
            )
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(converterFactory)
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): IdeasApiService {
        return retrofit.create<IdeasApiService>()
    }

}