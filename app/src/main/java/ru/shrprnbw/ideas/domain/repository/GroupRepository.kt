package ru.shrprnbw.ideas.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.shrprnbw.ideas.domain.entity.Group

interface GroupRepository {

    fun getOwnedGroups(): Flow<List<Group>>

    suspend fun createGroup(name: String)

    suspend fun updateGroup(groupId: Long, name: String)

    suspend fun deleteGroup(groupId: Long)

    suspend fun addUserToGroup(groupId: Long, userData: List<String>, byEmail: Boolean)

    suspend fun removeUserFromGroup(groupId: Long, userData: List<String>, byEmail: Boolean)

}
