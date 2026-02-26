package ru.shrprnbw.ideas.domain.usecase

import ru.shrprnbw.ideas.domain.repository.TranscriptionsRepository
import javax.inject.Inject

class GetTranscriptionDetailUseCase @Inject constructor(
    private val transcriptionsRepository: TranscriptionsRepository
) {

    suspend operator fun invoke(noteId: String, transcriptionId: String) =
        transcriptionsRepository.getTranscription(noteId, transcriptionId)

}
