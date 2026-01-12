package ru.shrprnbw.ideas.domain.usecase

import ru.shrprnbw.ideas.domain.repository.NoteRepository
import javax.inject.Inject

class DeleteKeywordsUseCase @Inject constructor(
    private val noteRepository: NoteRepository
) {

    suspend operator fun invoke(noteId: String, keywordId: Long) =
        noteRepository.deleteKeywords(noteId, keywordId)

}
