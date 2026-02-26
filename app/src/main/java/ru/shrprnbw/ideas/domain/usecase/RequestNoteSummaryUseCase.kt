package ru.shrprnbw.ideas.domain.usecase

import ru.shrprnbw.ideas.domain.repository.SummaryRepository
import javax.inject.Inject

class RequestNoteSummaryUseCase @Inject constructor(
    private val summaryRepository: SummaryRepository
) {
    suspend operator fun invoke(noteId: String) {
        summaryRepository.requestSummary(noteId)
    }
}
