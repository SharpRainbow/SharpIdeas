package ru.shrprnbw.ideas.domain.usecase

import ru.shrprnbw.ideas.domain.entity.TaskSummary
import ru.shrprnbw.ideas.domain.repository.BoardRepository
import javax.inject.Inject

class GetMyTasksUseCase @Inject constructor(
    private val boardRepository: BoardRepository
) {
    suspend operator fun invoke(): List<TaskSummary> {
        return boardRepository.getMyTasks()
    }
}
