package ru.shrprnbw.ideas.domain.usecase

import ru.shrprnbw.ideas.domain.entity.Note
import ru.shrprnbw.ideas.domain.repository.NoteRepository
import javax.inject.Inject

class UpdateNoteContentUseCase @Inject constructor(
    private val noteRepository: NoteRepository
) {

    suspend operator fun invoke(note: Note) {
        val updatedText = note.contents.filter { it.edited && it.data.isNotBlank() }
        if (updatedText.isNotEmpty()) {
            noteRepository.updateNoteContent(note.id, updatedText)
        }
    }

}