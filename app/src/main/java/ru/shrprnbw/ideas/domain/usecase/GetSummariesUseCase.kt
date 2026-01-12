package ru.shrprnbw.ideas.domain.usecase

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.shrprnbw.ideas.domain.entity.Summary
import ru.shrprnbw.ideas.domain.repository.NoteRepository
import javax.inject.Inject

class GetSummariesUseCase @Inject constructor(
    private val noteRepository: NoteRepository
) {
    operator fun invoke(noteId: String): Flow<PagingData<Summary>> {
        return noteRepository.getSummaries(noteId)
    }
}
