package com.sddrozdov.notes.domain.useCases

import com.sddrozdov.notes.domain.repository.NoteRepository

class AddNoteUseCase(
    private val repository: NoteRepository
) {

    suspend operator fun invoke(
        title: String,
        content: String,
    ) {
        repository.addNote(
            title = title,
            content = content,
            isPinned = false,
            updatedAt = System.currentTimeMillis()
        )
    }
}