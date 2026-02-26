package ru.shrprnbw.ideas.domain.usecase

import ru.shrprnbw.ideas.domain.entity.Summary
import ru.shrprnbw.ideas.domain.repository.SummaryRepository
import javax.inject.Inject

class GetSummaryDetailUseCase @Inject constructor(
    private val summaryRepository: SummaryRepository
) {
    suspend operator fun invoke(noteId: String, summaryId: String): Summary {
        return summaryRepository.getSummary(noteId, summaryId)
    }
}
