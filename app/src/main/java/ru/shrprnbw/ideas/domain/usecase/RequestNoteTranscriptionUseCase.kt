package ru.shrprnbw.ideas.domain.usecase

import ru.shrprnbw.ideas.domain.repository.TranscriptionsRepository
import javax.inject.Inject

class RequestNoteTranscriptionUseCase @Inject constructor(
    private val transcriptionsRepository: TranscriptionsRepository
) {

    suspend operator fun invoke(noteId: String) {
        transcriptionsRepository.requestTranscription(noteId)
    }

}