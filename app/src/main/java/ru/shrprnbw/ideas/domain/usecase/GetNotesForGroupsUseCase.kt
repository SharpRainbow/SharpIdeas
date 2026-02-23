package ru.shrprnbw.ideas.domain.usecase

import ru.shrprnbw.ideas.domain.entity.NotePreview
import ru.shrprnbw.ideas.domain.repository.NoteRepository
import javax.inject.Inject

class GetNotesForGroupsUseCase @Inject constructor(
    private val noteRepository: NoteRepository
) {
    suspend operator fun invoke(
        groupIds: List<Long>,
        excludeNoteId: String
    ): List<NotePreview> {
        return groupIds
            .flatMap { groupId -> noteRepository.getGroupNotesSimple(groupId) }
            .distinctBy { it.id }
            .filter { it.id != excludeNoteId }
    }
}
