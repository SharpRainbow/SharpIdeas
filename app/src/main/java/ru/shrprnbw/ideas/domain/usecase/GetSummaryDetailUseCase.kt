package ru.shrprnbw.ideas.domain.usecase

import ru.shrprnbw.ideas.domain.entity.Summary
import ru.shrprnbw.ideas.domain.repository.NoteRepository
import javax.inject.Inject

class GetSummaryDetailUseCase @Inject constructor(
    private val noteRepository: NoteRepository
) {
    suspend operator fun invoke(noteId: String, summaryId: String): Summary {
        return noteRepository.getSummary(noteId, summaryId)
    }
}
