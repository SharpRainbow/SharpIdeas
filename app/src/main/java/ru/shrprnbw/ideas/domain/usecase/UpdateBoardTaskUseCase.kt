package ru.shrprnbw.ideas.domain.usecase

import ru.shrprnbw.ideas.domain.entity.BoardTask
import ru.shrprnbw.ideas.domain.repository.BoardRepository
import javax.inject.Inject

class UpdateBoardTaskUseCase @Inject constructor(
    private val boardRepository: BoardRepository
) {
    suspend operator fun invoke(
        noteId: String,
        taskId: String,
        title: String? = null,
        description: String? = null,
        priority: String? = null,
        startDate: Long? =null,
        dueDate: Long? = null
    ): BoardTask {
        return boardRepository.updateTask(noteId, taskId, title, description, priority, startDate, dueDate)
    }
}
