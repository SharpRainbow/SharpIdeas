package ru.shrprnbw.ideas.domain.usecase

import ru.shrprnbw.ideas.domain.entity.BoardColumn
import ru.shrprnbw.ideas.domain.repository.BoardRepository
import javax.inject.Inject

class UpdateBoardColumnUseCase @Inject constructor(
    private val boardRepository: BoardRepository
) {
    suspend operator fun invoke(
        noteId: String,
        columnId: Long,
        title: String? = null,
        wipLimit: Int? = null,
        archived: Boolean? = null
    ): BoardColumn {
        return boardRepository.updateColumn(noteId, columnId, title, wipLimit, archived)
    }
}
