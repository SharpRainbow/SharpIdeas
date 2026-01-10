package ru.shrprnbw.ideas.domain.usecase

import ru.shrprnbw.ideas.domain.repository.GroupRepository
import javax.inject.Inject

class UpdateGroupUseCase @Inject constructor(
    private val groupRepository: GroupRepository
) {

    suspend operator fun invoke(groupId: Long, name: String) = groupRepository.updateGroup(groupId, name)

}
