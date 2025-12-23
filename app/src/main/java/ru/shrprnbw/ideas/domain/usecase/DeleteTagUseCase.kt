package ru.shrprnbw.ideas.domain.usecase

import ru.shrprnbw.ideas.domain.repository.TagRepository
import javax.inject.Inject

class DeleteTagUseCase @Inject constructor(
    private val tagRepository: TagRepository
) {

    suspend operator fun invoke(tagId: Long) {
        tagRepository.deleteTag(tagId)
    }

}
