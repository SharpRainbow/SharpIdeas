package ru.shrprnbw.ideas.domain.usecase

import ru.shrprnbw.ideas.domain.repository.BoardRepository
import javax.inject.Inject

class GetBoardAssigneesUseCase @Inject constructor(
    private val boardRepository: BoardRepository
) {
    operator fun invoke(noteId: String, taskId: String) = boardRepository.getBoardAssignees(noteId, taskId)
}
