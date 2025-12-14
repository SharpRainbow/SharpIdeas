package ru.shrprnbw.ideas.domain.usecase

import ru.shrprnbw.ideas.domain.entity.Note
import ru.shrprnbw.ideas.domain.repository.NoteRepository
import javax.inject.Inject

class UpdateNoteUseCase @Inject constructor(
    private val noteRepository: NoteRepository
) {

    suspend operator fun invoke(note: Note) {
        if (note.noteUpdated && note.title.isNotBlank()) {
            noteRepository.updateNote(
                noteId = note.id,
                title = note.title
            )
        }
    }
}