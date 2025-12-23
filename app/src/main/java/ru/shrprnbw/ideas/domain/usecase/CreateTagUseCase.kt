package ru.shrprnbw.ideas.domain.usecase

import ru.shrprnbw.ideas.domain.repository.TagRepository
import javax.inject.Inject

class CreateTagUseCase @Inject constructor(
    private val tagRepository: TagRepository
) {

    suspend operator fun invoke(name: String) {
        tagRepository.createTag(name)
    }

}
