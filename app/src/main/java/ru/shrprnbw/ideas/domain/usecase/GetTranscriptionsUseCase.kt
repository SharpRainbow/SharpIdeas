package ru.shrprnbw.ideas.domain.usecase

import ru.shrprnbw.ideas.domain.repository.NoteRepository
import javax.inject.Inject

class GetTranscriptionsUseCase @Inject constructor(
    private val noteRepository: NoteRepository
) {

    operator fun invoke(noteId: String) = noteRepository.getTranscriptions(noteId)

}
