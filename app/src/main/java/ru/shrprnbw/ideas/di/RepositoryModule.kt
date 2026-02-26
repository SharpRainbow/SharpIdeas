package ru.shrprnbw.ideas.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.shrprnbw.ideas.data.repository.AuthRepositoryImpl
import ru.shrprnbw.ideas.data.repository.BoardRepositoryImpl
import ru.shrprnbw.ideas.data.repository.CredentialsRepositoryImpl
import ru.shrprnbw.ideas.data.repository.GroupRepositoryImpl
import ru.shrprnbw.ideas.data.repository.KeywordRepositoryImpl
import ru.shrprnbw.ideas.data.repository.NoteRepositoryImpl
import ru.shrprnbw.ideas.data.repository.SettingsRepositoryImpl
import ru.shrprnbw.ideas.data.repository.SummaryRepositoryImpl
import ru.shrprnbw.ideas.data.repository.TagRepositoryImpl
import ru.shrprnbw.ideas.data.repository.TranscriptionsRepositoryImpl
import ru.shrprnbw.ideas.data.repository.UserRepositoryImpl
import ru.shrprnbw.ideas.domain.repository.AuthRepository
import ru.shrprnbw.ideas.domain.repository.BoardRepository
import ru.shrprnbw.ideas.domain.repository.CredentialsRepository
import ru.shrprnbw.ideas.domain.repository.GroupRepository
import ru.shrprnbw.ideas.domain.repository.KeywordRepository
import ru.shrprnbw.ideas.domain.repository.NoteRepository
import ru.shrprnbw.ideas.domain.repository.SettingsRepository
import ru.shrprnbw.ideas.domain.repository.SummaryRepository
import ru.shrprnbw.ideas.domain.repository.TagRepository
import ru.shrprnbw.ideas.domain.repository.TranscriptionsRepository
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

    @Binds
    @Singleton
    fun bindTagRepository(impl: TagRepositoryImpl): TagRepository

    @Binds
    @Singleton
    fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository

    @Binds
    @Singleton
    fun bindGroupRepository(impl: GroupRepositoryImpl): GroupRepository

    @Binds
    @Singleton
    fun bindBoardRepository(impl: BoardRepositoryImpl): BoardRepository

    @Binds
    @Singleton
    fun bindKeywordRepository(impl: KeywordRepositoryImpl): KeywordRepository

    @Binds
    @Singleton
    fun bindSummaryRepository(impl: SummaryRepositoryImpl): SummaryRepository

    @Binds
    @Singleton
    fun bindTranscriptionRepository(impl: TranscriptionsRepositoryImpl): TranscriptionsRepository

}