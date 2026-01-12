package ru.shrprnbw.ideas.data.remote.paging

import ru.shrprnbw.ideas.data.remote.dto.response.NotePreviewDto
import ru.shrprnbw.ideas.data.remote.dto.response.PagedResponse

interface NoteSearchStrategy {

    suspend fun searchNotes(page: Int, size: Int): PagedResponse<NotePreviewDto>

}