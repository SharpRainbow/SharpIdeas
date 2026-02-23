package ru.shrprnbw.ideas.data.remote.paging

import ru.shrprnbw.ideas.data.remote.IdeasApiService
import ru.shrprnbw.ideas.data.remote.dto.response.NotePreviewDto
import ru.shrprnbw.ideas.data.remote.dto.response.PagedResponse
import ru.shrprnbw.ideas.domain.entity.NoteType
import ru.shrprnbw.ideas.domain.repository.AuthRepository

class FilterNotesSearchStrategy(
    private val apiService: IdeasApiService,
    private val authRepository: AuthRepository,
    private val globalSearch: Boolean,
    private val title: String,
    private val content: String,
    private val tagIds: List<Long>,
    private val noteType: NoteType?
) : NoteSearchStrategy {

    override suspend fun searchNotes(page: Int, size: Int): PagedResponse<NotePreviewDto> {
        val token = authRepository.getValidToken()
        return apiService.searchNotes(
            token = token,
            globalSearch = globalSearch,
            title = title.ifBlank { null },
            content = content.ifBlank { null },
            tagIds = tagIds.ifEmpty { null },
            noteType = noteType,
            page = page,
            limit = size
        )
    }

}