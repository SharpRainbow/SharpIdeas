package ru.shrprnbw.ideas.domain.usecase

import ru.shrprnbw.ideas.domain.repository.BoardRepository
import javax.inject.Inject

class RemoveTaskAssigneeUseCase @Inject constructor(
    private val boardRepository: BoardRepository
) {
    suspend operator fun invoke(noteId: String, taskId: String, userId: String) {
        boardRepository.removeTaskAssignee(noteId, taskId, userId)
    }
}
