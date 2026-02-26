package ru.shrprnbw.ideas.domain.usecase

import ru.shrprnbw.ideas.domain.repository.KeywordRepository
import javax.inject.Inject

class RequestNoteKeywordsUseCase @Inject constructor(
    private val keywordRepository: KeywordRepository
) {
    suspend operator fun invoke(noteId: String) {
        keywordRepository.requestKeywords(noteId)
    }
}
