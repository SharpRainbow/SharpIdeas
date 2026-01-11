package ru.shrprnbw.ideas.domain.usecase

import ru.shrprnbw.ideas.domain.repository.NoteRepository
import javax.inject.Inject

class LoadSharedNotesUseCase @Inject constructor(
    private val noteRepository: NoteRepository
) {

    operator fun invoke() = noteRepository.getSharedNotes()

}