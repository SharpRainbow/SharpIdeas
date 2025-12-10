package ru.shrprnbw.ideas.domain.usecase

import ru.shrprnbw.ideas.domain.repository.NoteRepository
import javax.inject.Inject

class SearchNotesUseCase @Inject constructor(
    private val noteRepository: NoteRepository
) {

    operator fun invoke(
        globalSearch: Boolean,
        query: String,
        tagIds: List<Long>
    ) = noteRepository.searchNotes(
        globalSearch,
        query,
        query,
        tagIds
    )

}