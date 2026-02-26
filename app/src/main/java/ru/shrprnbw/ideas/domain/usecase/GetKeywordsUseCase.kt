package ru.shrprnbw.ideas.domain.usecase

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.shrprnbw.ideas.domain.entity.Keyword
import ru.shrprnbw.ideas.domain.repository.KeywordRepository
import javax.inject.Inject

class GetKeywordsUseCase @Inject constructor(
    private val keywordRepository: KeywordRepository
) {
    operator fun invoke(noteId: String): Flow<PagingData<Keyword>> {
        return keywordRepository.getKeywords(noteId)
    }
}
