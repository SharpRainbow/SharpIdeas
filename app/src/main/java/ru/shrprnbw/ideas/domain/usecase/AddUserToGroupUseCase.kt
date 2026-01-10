package ru.shrprnbw.ideas.domain.usecase

import ru.shrprnbw.ideas.domain.repository.GroupRepository
import javax.inject.Inject

class AddUserToGroupUseCase @Inject constructor(
    private val groupRepository: GroupRepository
) {

    suspend operator fun invoke(groupId: Long, userData: String, byEmail: Boolean) =
        groupRepository.addUserToGroup(groupId, listOf(userData), byEmail)

}
