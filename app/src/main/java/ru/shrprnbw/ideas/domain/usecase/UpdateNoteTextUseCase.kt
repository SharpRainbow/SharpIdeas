package ru.shrprnbw.ideas.domain.usecase

import ru.shrprnbw.ideas.domain.entity.ContentItem
import ru.shrprnbw.ideas.domain.repository.NoteRepository
import javax.inject.Inject

class UpdateNoteTextUseCase @Inject constructor(
    private val noteRepository: NoteRepository
) {

    suspend operator fun invoke(noteId: String, updatedText: List<ContentItem>) {
        if (updatedText.isNotEmpty()) {
            noteRepository.updateNoteContent(noteId, updatedText)
        }
    }

}