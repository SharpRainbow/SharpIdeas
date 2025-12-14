package ru.shrprnbw.ideas.domain.usecase

import ru.shrprnbw.ideas.domain.repository.NoteRepository
import javax.inject.Inject

class DeleteNoteContentUseCase @Inject constructor(
    private val noteRepository: NoteRepository
) {

    suspend operator fun invoke(noteId: String, contentId: Long) {
        noteRepository.deleteNoteContent(noteId, contentId)
    }

}