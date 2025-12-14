package ru.shrprnbw.ideas.domain.usecase

import ru.shrprnbw.ideas.domain.entity.Note
import ru.shrprnbw.ideas.domain.repository.NoteRepository
import javax.inject.Inject

class UpdateNoteContentUseCase @Inject constructor(
    private val noteRepository: NoteRepository
) {

    suspend operator fun invoke(note: Note) {
        val createdText = note.contents.filter { it.id == -1L }
        val existedText = note.contents - createdText.toSet()
        val updatedText = existedText.filter { it.edited && it.data.isNotBlank() }
        val deletedText = existedText.filter { it.data.isBlank() }
        for (c in createdText) {
            noteRepository.addNoteText(note.id, c.data)
        }
        if (updatedText.isNotEmpty()) {
            noteRepository.updateNoteContent(note.id, updatedText)
        }
    }

}