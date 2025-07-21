package com.sddrozdov.notes.domain.useCases

import com.sddrozdov.notes.domain.repository.NoteRepository

class DeleteNoteUseCase(
    private val repository: NoteRepository
) {

    suspend operator fun invoke(noteId: Int) {
        repository.deleteNote(noteId)
    }
}