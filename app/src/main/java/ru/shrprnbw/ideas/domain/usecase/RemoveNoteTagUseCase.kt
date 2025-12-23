package ru.shrprnbw.ideas.domain.usecase

import ru.shrprnbw.ideas.domain.repository.NoteRepository
import javax.inject.Inject

class RemoveNoteTagUseCase @Inject constructor(
    private val noteRepository: NoteRepository
) {

    suspend operator fun invoke(noteId: String, tag: String) {
        noteRepository.removeNoteTag(noteId, tag)
    }

}
