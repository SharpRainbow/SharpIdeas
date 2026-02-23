package ru.shrprnbw.ideas.domain.usecase

import ru.shrprnbw.ideas.domain.entity.BoardTask
import ru.shrprnbw.ideas.domain.repository.BoardRepository
import javax.inject.Inject

class CreateBoardTaskUseCase @Inject constructor(
    private val boardRepository: BoardRepository
) {
    suspend operator fun invoke(
        noteId: String,
        columnId: Long,
        title: String,
        description: String? = null,
        priority: String? = null,
        startDate: Long? = null,
        dueDate: Long? = null
    ): BoardTask {
        return boardRepository.createTask(noteId, columnId, title, description, priority, startDate, dueDate)
    }
}
