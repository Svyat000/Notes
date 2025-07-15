package com.sddrozdov.notes.domain.useCases

import com.sddrozdov.notes.domain.model.Note
import com.sddrozdov.notes.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow

class GetNoteUseCase(
    private val repository: NoteRepository
) {

    operator fun invoke(noteId: Int): Note {
        return repository.getNote(noteId)
    }
}