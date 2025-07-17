package com.sddrozdov.notes.domain.useCases

import com.sddrozdov.notes.domain.model.Note
import com.sddrozdov.notes.domain.repository.NoteRepository

class AddNoteUseCase(
    private val repository: NoteRepository
) {

    operator fun invoke(
        title: String,
        content: String,
    ) {
        repository.addNote(title, content)
    }
}