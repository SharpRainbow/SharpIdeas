package ru.shrprnbw.ideas.domain.usecase

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.shrprnbw.ideas.domain.entity.Summary
import ru.shrprnbw.ideas.domain.repository.SummaryRepository
import javax.inject.Inject

class GetSummariesUseCase @Inject constructor(
    private val summaryRepository: SummaryRepository
) {
    operator fun invoke(noteId: String): Flow<PagingData<Summary>> {
        return summaryRepository.getSummaries(noteId)
    }
}
