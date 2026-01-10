package ru.shrprnbw.ideas.domain.usecase

import ru.shrprnbw.ideas.domain.repository.GroupRepository
import javax.inject.Inject

class GetGroupUsersUseCase @Inject constructor(
    private val groupRepository: GroupRepository
) {

    suspend operator fun invoke(groupId: Long) = groupRepository.getGroupUsers(groupId)

}
