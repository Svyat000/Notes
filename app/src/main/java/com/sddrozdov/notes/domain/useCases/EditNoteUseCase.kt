package com.sddrozdov.notes.domain.useCases

import com.sddrozdov.notes.domain.model.Note
import com.sddrozdov.notes.domain.repository.NoteRepository

class EditNoteUseCase(
    private val repository: NoteRepository
) {

    suspend operator fun invoke(note: Note) {
        repository.editNote(note.copy(updatedAt = System.currentTimeMillis()))
    }
}