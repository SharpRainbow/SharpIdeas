package ru.shrprnbw.ideas.domain.usecase

import ru.shrprnbw.ideas.domain.repository.GroupRepository
import javax.inject.Inject

class GetOwnedGroupsUseCase @Inject constructor(
    private val groupRepository: GroupRepository
) {

    operator fun invoke() = groupRepository.getOwnedGroups()

}
