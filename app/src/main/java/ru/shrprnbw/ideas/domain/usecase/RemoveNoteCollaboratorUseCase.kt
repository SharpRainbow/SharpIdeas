package ru.shrprnbw.ideas.domain.usecase

import ru.shrprnbw.ideas.domain.repository.NoteRepository
import javax.inject.Inject

class RemoveNoteCollaboratorUseCase @Inject constructor(
    private val noteRepository: NoteRepository
) {

    suspend operator fun invoke(noteId: String, collaboratorId: String) =
        noteRepository.removeCollaborator(noteId, collaboratorId)

}
