@file:OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)

package ru.shrprnbw.ideas.data.repository

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import ru.shrprnbw.ideas.data.mapper.toEntity
import ru.shrprnbw.ideas.data.mapper.toGroupUser
import ru.shrprnbw.ideas.data.remote.IdeasApiService
import ru.shrprnbw.ideas.data.remote.dto.request.AddUserToGroupRequest
import ru.shrprnbw.ideas.data.remote.dto.request.CreateGroupRequest
import ru.shrprnbw.ideas.data.remote.dto.request.UpdateGroupRequest
import ru.shrprnbw.ideas.domain.entity.Group
import ru.shrprnbw.ideas.domain.entity.GroupUser
import ru.shrprnbw.ideas.domain.repository.AuthRepository
import ru.shrprnbw.ideas.domain.repository.GroupRepository
import javax.inject.Inject

class GroupRepositoryImpl @Inject constructor(
    private val apiService: IdeasApiService,
    private val authRepository: AuthRepository
) : GroupRepository {

    private val groupsRefreshTrigger = MutableSharedFlow<Unit>()

    override fun getOwnedGroups(): Flow<List<Group>> {
        return groupsRefreshTrigger.onStart {
            emit(Unit)
        }.debounce(
            timeoutMillis = 500
        ).flatMapLatest {
            flow {
                val token = authRepository.getValidToken()
                val groups = apiService.getOwnedGroups(token).map { it.toEntity() }
                emit(groups)
            }
        }
    }

    override suspend fun createGroup(name: String) {
        val token = authRepository.getValidToken()
        apiService.createGroup(token, CreateGroupRequest(name))
        groupsRefreshTrigger.emit(Unit)
    }

    override suspend fun updateGroup(groupId: Long, name: String) {
        val token = authRepository.getValidToken()
        apiService.updateGroup(token, groupId, UpdateGroupRequest(name))
        groupsRefreshTrigger.emit(Unit)
    }

    override suspend fun deleteGroup(groupId: Long) {
        val token = authRepository.getValidToken()
        apiService.deleteGroup(token, groupId)
        groupsRefreshTrigger.emit(Unit)
    }

    override suspend fun getGroupUsers(groupId: Long): List<GroupUser> { // TODO: Add flow
        val token = authRepository.getValidToken()
        return apiService.getGroupUsers(token, groupId).map { it.toGroupUser() }
    }

    override suspend fun addUserToGroup(groupId: Long, userData: List<String>, byEmail: Boolean) {
        val token = authRepository.getValidToken()
        apiService.addUserToGroup(token, groupId, AddUserToGroupRequest(userData, byEmail))
    }

    override suspend fun removeUserFromGroup(groupId: Long, userData: List<String>, byEmail: Boolean) {
        val token = authRepository.getValidToken()
        apiService.removeUserFromGroup(token, groupId, AddUserToGroupRequest(userData, byEmail))
    }
}
