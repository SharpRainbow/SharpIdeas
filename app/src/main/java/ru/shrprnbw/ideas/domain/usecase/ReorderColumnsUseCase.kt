package ru.shrprnbw.ideas.domain.usecase

import ru.shrprnbw.ideas.domain.repository.BoardRepository
import javax.inject.Inject

class ReorderColumnsUseCase @Inject constructor(
    private val boardRepository: BoardRepository
) {
    suspend operator fun invoke(noteId: String, orderedIds: List<Long>) {
        boardRepository.reorderColumns(noteId, orderedIds)
    }
}
