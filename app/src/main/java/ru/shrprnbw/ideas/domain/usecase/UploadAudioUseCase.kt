package ru.shrprnbw.ideas.domain.usecase

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import ru.shrprnbw.ideas.domain.repository.NoteRepository
import javax.inject.Inject

class UploadAudioUseCase @Inject constructor(
    private val noteRepository: NoteRepository
) {

    operator fun invoke(noteId: String, audioUri: Uri): Flow<Float> {
        return noteRepository.addNoteAudio(noteId, audioUri)
    }

}
