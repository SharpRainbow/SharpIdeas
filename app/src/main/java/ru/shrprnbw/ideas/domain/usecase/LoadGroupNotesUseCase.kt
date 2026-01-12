package ru.shrprnbw.ideas.domain.usecase

import ru.shrprnbw.ideas.domain.repository.NoteRepository
import javax.inject.Inject

class LoadGroupNotesUseCase @Inject constructor(
    private val noteRepository: NoteRepository
) {

    operator fun invoke(groupId: Long) = noteRepository.getGroupNotes(groupId)

}