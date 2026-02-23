package ru.shrprnbw.ideas.domain.usecase

import ru.shrprnbw.ideas.domain.entity.TaskLabel
import ru.shrprnbw.ideas.domain.repository.BoardRepository
import javax.inject.Inject

class GetBoardLabelsUseCase @Inject constructor(
    private val boardRepository: BoardRepository
) {
    suspend operator fun invoke(noteId: String): List<TaskLabel> {
        return boardRepository.getBoardLabels(noteId)
    }
}
