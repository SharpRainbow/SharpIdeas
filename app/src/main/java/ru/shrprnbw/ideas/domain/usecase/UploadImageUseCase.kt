package ru.shrprnbw.ideas.domain.usecase

import android.net.Uri
import ru.shrprnbw.ideas.domain.repository.NoteRepository
import javax.inject.Inject

class UploadImageUseCase @Inject constructor(
    private val noteRepository: NoteRepository
) {

    suspend operator fun invoke(noteId: String, imageUri: Uri) {
        noteRepository.addNoteImage(noteId, imageUri)
    }

}