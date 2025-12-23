package ru.shrprnbw.ideas.domain.usecase

import ru.shrprnbw.ideas.domain.repository.TagRepository
import javax.inject.Inject

class GetUserTagsUseCase @Inject constructor(
    private val tagRepository: TagRepository
) {

    operator fun invoke() = tagRepository.getUserTags()

}