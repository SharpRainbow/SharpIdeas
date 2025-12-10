package ru.shrprnbw.ideas.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.shrprnbw.ideas.data.repository.AuthRepositoryImpl
import ru.shrprnbw.ideas.data.repository.CredentialsRepositoryImpl
import ru.shrprnbw.ideas.data.repository.NoteRepositoryImpl
import ru.shrprnbw.ideas.data.repository.UserRepositoryImpl
import ru.shrprnbw.ideas.domain.repository.AuthRepository
import ru.shrprnbw.ideas.domain.repository.CredentialsRepository
import ru.shrprnbw.ideas.domain.repository.NoteRepository
import ru.shrprnbw.ideas.domain.repository.UserRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds
    @Singleton
    fun bindCredentialsRepository(impl: CredentialsRepositoryImpl): CredentialsRepository

    @Binds
    @Singleton
    fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    fun bintAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    fun bindNoteRepository(impl: NoteRepositoryImpl): NoteRepository

}