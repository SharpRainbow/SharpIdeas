package ru.shrprnbw.ideas.domain.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.shrprnbw.ideas.domain.entity.User

interface UserRepository {

    suspend fun getUserInfo(): User

    suspend fun updateUserInfo(email: String, username: String)

    suspend fun getByGroupId(groupId: Long): Flow<PagingData<User>>

    fun invalidateGroupUsers()

}