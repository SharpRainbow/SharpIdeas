package ru.shrprnbw.ideas.domain.usecase

import ru.shrprnbw.ideas.domain.entity.Note
import ru.shrprnbw.ideas.domain.repository.NoteRepository
import javax.inject.Inject

class GetNoteInfoUseCase @Inject constructor(
    private val noteRepository: NoteRepository
) {

    suspend operator fun invoke(noteId: String): Note {
        return noteRepository.getNoteInfo(noteId)
    }

}