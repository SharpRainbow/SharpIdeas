package ru.shrprnbw.ideas.data.repository

import ru.shrprnbw.ideas.data.mapper.toEntity
import ru.shrprnbw.ideas.data.remote.IdeasApiService
import ru.shrprnbw.ideas.data.remote.dto.request.UpdatePersonalInfoRequest
import ru.shrprnbw.ideas.domain.entity.UserInfo
import ru.shrprnbw.ideas.domain.repository.AuthRepository
import ru.shrprnbw.ideas.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val apiService: IdeasApiService,
    private val authRepository: AuthRepository
) : UserRepository {

    override suspend fun getUserInfo(): UserInfo {// TODO: add flow for reactive updates
        val jwtToken = authRepository.getValidToken()
        return apiService.getUserInfo(
            jwtToken
        ).toEntity()
    }

    override suspend fun updateUserInfo(
        email: String,
        username: String
    ) {// TODO: trigger flow for reactive updates
        val jwtToken = authRepository.getValidToken()
        return apiService.updateUserInfo(
            jwtToken,
            UpdatePersonalInfoRequest(
                email = email,
                username = username
            )
        )
    }

}