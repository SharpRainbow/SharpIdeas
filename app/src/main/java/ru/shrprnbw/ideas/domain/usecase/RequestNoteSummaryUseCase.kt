package ru.shrprnbw.ideas.domain.usecase

import ru.shrprnbw.ideas.domain.repository.NoteRepository
import javax.inject.Inject

class RequestNoteSummaryUseCase @Inject constructor(
    private val noteRepository: NoteRepository
) {
    suspend operator fun invoke(noteId: String) {
        noteRepository.requestSummary(noteId)
    }
}
