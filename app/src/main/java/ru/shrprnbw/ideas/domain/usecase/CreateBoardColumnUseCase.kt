package ru.shrprnbw.ideas.domain.usecase

import ru.shrprnbw.ideas.domain.entity.BoardColumn
import ru.shrprnbw.ideas.domain.repository.BoardRepository
import javax.inject.Inject

class CreateBoardColumnUseCase @Inject constructor(
    private val boardRepository: BoardRepository
) {
    suspend operator fun invoke(noteId: String, title: String, wipLimit: Int? = null): BoardColumn {
        return boardRepository.createColumn(noteId, title, wipLimit)
    }
}
