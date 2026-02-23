package ru.shrprnbw.ideas.domain.usecase

import ru.shrprnbw.ideas.domain.repository.BoardRepository
import javax.inject.Inject

class ReorderTasksUseCase @Inject constructor(
    private val boardRepository: BoardRepository
) {
    suspend operator fun invoke(noteId: String, columnId: Long, orderedIds: List<String>) {
        boardRepository.reorderTasks(noteId, columnId, orderedIds)
    }
}
