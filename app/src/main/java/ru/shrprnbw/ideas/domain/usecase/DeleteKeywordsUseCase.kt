package ru.shrprnbw.ideas.domain.usecase

import ru.shrprnbw.ideas.domain.repository.KeywordRepository
import javax.inject.Inject

class DeleteKeywordsUseCase @Inject constructor(
    private val keywordRepository: KeywordRepository
) {

    suspend operator fun invoke(noteId: String, keywordId: Long) =
        keywordRepository.deleteKeywords(noteId, keywordId)

}
