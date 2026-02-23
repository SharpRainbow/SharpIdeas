package ru.shrprnbw.ideas.domain.usecase

import ru.shrprnbw.ideas.domain.entity.User
import ru.shrprnbw.ideas.domain.repository.BoardRepository
import javax.inject.Inject

class GetBoardAssigneesUseCase @Inject constructor(
    private val boardRepository: BoardRepository
) {
    suspend operator fun invoke(noteId: String): List<User> {
        return boardRepository.getBoardAssignees(noteId)
    }
}
