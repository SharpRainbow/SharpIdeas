package ru.shrprnbw.ideas.domain.repository

import ru.shrprnbw.ideas.domain.entity.UserInfo

interface UserRepository {

    suspend fun getUserInfo(): UserInfo

    suspend fun updateUserInfo(email: String, username: String)

}