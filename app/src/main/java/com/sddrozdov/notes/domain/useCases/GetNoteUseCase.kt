package com.sddrozdov.notes.domain.useCases

import com.sddrozdov.notes.domain.model.Note
import com.sddrozdov.notes.domain.repository.NoteRepository
import javax.inject.Inject

class GetNoteUseCase @Inject constructor(
    private val repository: NoteRepository
) {

    suspend operator fun invoke(noteId: Int): Note {
        return repository.getNote(noteId)
    }
}