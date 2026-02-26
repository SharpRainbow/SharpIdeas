package ru.shrprnbw.ideas.domain.usecase

import ru.shrprnbw.ideas.domain.repository.TranscriptionsRepository
import javax.inject.Inject

class GetTranscriptionsUseCase @Inject constructor(
    private val transcriptionsRepository: TranscriptionsRepository
) {

    operator fun invoke(noteId: String) = transcriptionsRepository.getTranscriptions(noteId)

}
