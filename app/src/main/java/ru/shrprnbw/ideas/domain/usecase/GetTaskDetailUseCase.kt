package ru.shrprnbw.ideas.domain.usecase

import ru.shrprnbw.ideas.domain.entity.TaskDetail
import ru.shrprnbw.ideas.domain.repository.BoardRepository
import javax.inject.Inject

class GetTaskDetailUseCase @Inject constructor(
    private val boardRepository: BoardRepository
) {
    suspend operator fun invoke(noteId: String, taskId: String): TaskDetail {
        return boardRepository.getTaskDetail(noteId, taskId)
    }
}
