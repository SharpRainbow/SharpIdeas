package ru.shrprnbw.ideas.domain.usecase

import ru.shrprnbw.ideas.domain.repository.NoteRepository
import javax.inject.Inject

class DeleteTranscriptionUseCase @Inject constructor(
    private val noteRepository: NoteRepository
) {

    suspend operator fun invoke(noteId: String, transcriptionId: String) =
        noteRepository.deleteTranscription(noteId, transcriptionId.toLong())

}
