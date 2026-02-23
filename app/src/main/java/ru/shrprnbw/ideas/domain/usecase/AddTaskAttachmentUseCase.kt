package ru.shrprnbw.ideas.domain.usecase

import ru.shrprnbw.ideas.domain.entity.TaskAttachment
import ru.shrprnbw.ideas.domain.repository.BoardRepository
import javax.inject.Inject

class AddTaskAttachmentUseCase @Inject constructor(
    private val boardRepository: BoardRepository
) {
    suspend operator fun invoke(
        noteId: String,
        taskId: String,
        type: String,
        title: String?,
        content: String
    ): TaskAttachment {
        return boardRepository.addTaskAttachment(noteId, taskId, type, title, content)
    }
}
