package ru.shrprnbw.ideas.data.remote.paging

import ru.shrprnbw.ideas.data.remote.IdeasApiService
import ru.shrprnbw.ideas.data.remote.dto.response.NotePreviewDto
import ru.shrprnbw.ideas.data.remote.dto.response.PagedResponse
import ru.shrprnbw.ideas.domain.repository.AuthRepository
import javax.inject.Inject

class PersonalNotesSearchStrategy @Inject constructor(
    private val apiService: IdeasApiService,
    private val authRepository: AuthRepository
) : NoteSearchStrategy {

    override suspend fun searchNotes(page: Int, size: Int): PagedResponse<NotePreviewDto> {
        val token = authRepository.getValidToken()
        return apiService.getUserNotes(
            token = token,
            page = page,
            limit = size
        )
    }

}