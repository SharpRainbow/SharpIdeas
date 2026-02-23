package ru.shrprnbw.ideas.domain.usecase

import ru.shrprnbw.ideas.domain.repository.BoardRepository
import javax.inject.Inject

class DeleteTaskAttachmentUseCase @Inject constructor(
    private val boardRepository: BoardRepository
) {
    suspend operator fun invoke(noteId: String, taskId: String, attachmentId: Long) {
        boardRepository.deleteTaskAttachment(noteId, taskId, attachmentId)
    }
}
