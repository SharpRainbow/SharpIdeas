package ru.shrprnbw.ideas.domain.usecase

import ru.shrprnbw.ideas.domain.repository.TagRepository
import javax.inject.Inject

class UpdateTagUseCase @Inject constructor(
    private val tagRepository: TagRepository
) {

    suspend operator fun invoke(tagId: Long, name: String) {
        tagRepository.updateTag(tagId, name)
    }

}
