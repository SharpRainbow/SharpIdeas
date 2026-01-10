package ru.shrprnbw.ideas.domain.usecase

import ru.shrprnbw.ideas.domain.repository.GroupRepository
import javax.inject.Inject

class CreateGroupUseCase @Inject constructor(
    private val groupRepository: GroupRepository
) {

    suspend operator fun invoke(name: String) = groupRepository.createGroup(name)

}
