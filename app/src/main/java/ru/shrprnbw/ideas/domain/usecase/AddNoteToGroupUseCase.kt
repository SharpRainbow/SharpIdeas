package ru.shrprnbw.ideas.domain.usecase

import ru.shrprnbw.ideas.domain.repository.NoteRepository
import javax.inject.Inject

class AddNoteToGroupUseCase @Inject constructor(
    private val noteRepository: NoteRepository
) {

    suspend operator fun invoke(noteId: String, groupId: Long, accessType: String = "VIEWER") =
        noteRepository.addNoteToGroup(noteId, groupId, accessType)

}
