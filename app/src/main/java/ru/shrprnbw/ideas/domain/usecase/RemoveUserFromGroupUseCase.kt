package ru.shrprnbw.ideas.domain.usecase

import ru.shrprnbw.ideas.domain.repository.GroupRepository
import javax.inject.Inject

class RemoveUserFromGroupUseCase @Inject constructor(
    private val groupRepository: GroupRepository
) {

    suspend operator fun invoke(groupId: Long, userId: String) =
        groupRepository.removeUserFromGroup(groupId, listOf(userId), byEmail = false)

}
