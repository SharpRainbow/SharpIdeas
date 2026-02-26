package ru.shrprnbw.ideas.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import kotlinx.coroutines.flow.Flow
import ru.shrprnbw.ideas.data.mapper.toEntity
import ru.shrprnbw.ideas.data.remote.IdeasApiService
import ru.shrprnbw.ideas.data.remote.dto.request.UpdatePersonalInfoRequest
import ru.shrprnbw.ideas.data.remote.paging.GroupUserPagingSource
import ru.shrprnbw.ideas.domain.entity.User
import ru.shrprnbw.ideas.domain.repository.AuthRepository
import ru.shrprnbw.ideas.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val apiService: IdeasApiService,
    private val authRepository: AuthRepository
) : UserRepository {

    private var currentGroupPagingSource: PagingSource<Int, User>? = null

    override suspend fun getUserInfo(): User {// TODO: add flow for reactive updates
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

    override suspend fun getByGroupId(groupId: Long): Flow<PagingData<User>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                GroupUserPagingSource(
                    apiService,
                    authRepository,
                    groupId
                ).also { currentGroupPagingSource = it }
            }
        ).flow
    }

    override fun invalidateGroupUsers() {
        currentGroupPagingSource?.invalidate()
    }
}