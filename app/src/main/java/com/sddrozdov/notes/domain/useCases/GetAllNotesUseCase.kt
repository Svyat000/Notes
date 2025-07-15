package com.sddrozdov.notes.domain.useCases

import com.sddrozdov.notes.domain.model.Note
import com.sddrozdov.notes.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow

class GetAllNotesUseCase(
    private val repository: NoteRepository
) {

    operator fun invoke(): Flow<List<Note>> {
        return repository.getAllNotes()
    }
}