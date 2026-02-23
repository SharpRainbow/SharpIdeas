package ru.shrprnbw.ideas.domain.usecase

import ru.shrprnbw.ideas.domain.repository.BoardRepository
import javax.inject.Inject

class RemoveLabelFromTaskUseCase @Inject constructor(
    private val boardRepository: BoardRepository
) {
    suspend operator fun invoke(noteId: String, taskId: String, labelId: Long) {
        boardRepository.removeLabelFromTask(noteId, taskId, labelId)
    }
}
