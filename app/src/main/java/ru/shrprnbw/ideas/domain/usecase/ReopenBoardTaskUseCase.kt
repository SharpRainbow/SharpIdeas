package ru.shrprnbw.ideas.domain.usecase

import ru.shrprnbw.ideas.domain.entity.BoardTask
import ru.shrprnbw.ideas.domain.repository.BoardRepository
import javax.inject.Inject

class ReopenBoardTaskUseCase @Inject constructor(
    private val boardRepository: BoardRepository
) {
    suspend operator fun invoke(noteId: String, taskId: String): BoardTask {
        return boardRepository.reopenTask(noteId, taskId)
    }
}
