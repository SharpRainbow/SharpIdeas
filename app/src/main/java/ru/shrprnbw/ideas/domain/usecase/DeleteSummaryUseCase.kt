package ru.shrprnbw.ideas.domain.usecase

import ru.shrprnbw.ideas.domain.repository.SummaryRepository
import javax.inject.Inject

class DeleteSummaryUseCase @Inject constructor(
    private val summaryRepository: SummaryRepository
) {

    suspend operator fun invoke(noteId: String, summaryId: String) =
        summaryRepository.deleteSummary(noteId, summaryId.toLong())

}
