package ru.shrprnbw.ideas.domain.usecase

import ru.shrprnbw.ideas.domain.entity.TaskLabel
import ru.shrprnbw.ideas.domain.repository.BoardRepository
import javax.inject.Inject

class CreateBoardLabelUseCase @Inject constructor(
    private val boardRepository: BoardRepository
) {
    suspend operator fun invoke(noteId: String, name: String, color: String): TaskLabel {
        return boardRepository.createLabel(noteId, name, color)
    }
}
