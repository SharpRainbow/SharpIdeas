package ru.shrprnbw.ideas.data.remote.paging

import ru.shrprnbw.ideas.data.remote.IdeasApiService
import ru.shrprnbw.ideas.data.remote.dto.response.NotePreviewDto
import ru.shrprnbw.ideas.data.remote.dto.response.PagedResponse
import ru.shrprnbw.ideas.domain.repository.AuthRepository

class GroupNotesSearchStrategy(
    private val apiService: IdeasApiService,
    private val authRepository: AuthRepository,
    private val groupId: Long
) : NoteSearchStrategy {

    override suspend fun searchNotes(page: Int, size: Int): PagedResponse<NotePreviewDto> {
        val token = authRepository.getValidToken()
        return apiService.getGroupNotes(
            token = token,
            groupId = groupId,
            page = page,
            limit = size
        )
    }

}