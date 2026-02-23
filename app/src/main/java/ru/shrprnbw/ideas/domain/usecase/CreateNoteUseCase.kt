package ru.shrprnbw.ideas.domain.usecase

import ru.shrprnbw.ideas.domain.entity.NoteType
import ru.shrprnbw.ideas.domain.repository.NoteRepository
import javax.inject.Inject

class CreateNoteUseCase @Inject constructor(
    private val noteRepository: NoteRepository
) {

    suspend operator fun invoke(title: String, noteType: NoteType) {
        noteRepository.createNote(title, noteType)
    }

}
