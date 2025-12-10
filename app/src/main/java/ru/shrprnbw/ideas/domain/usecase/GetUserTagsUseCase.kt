package ru.shrprnbw.ideas.domain.usecase

import ru.shrprnbw.ideas.domain.entity.Tag
import ru.shrprnbw.ideas.domain.repository.UserRepository
import javax.inject.Inject

class GetUserTagsUseCase @Inject constructor(
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(): List<Tag> {
        return userRepository.getCreatedTags()
    }

}