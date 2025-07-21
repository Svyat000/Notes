package com.sddrozdov.notes.domain.useCases

import com.sddrozdov.notes.domain.repository.NoteRepository

class SwitchPinnedStatusUseCase(
    private val repository: NoteRepository
) {

    suspend operator fun invoke(noteId: Int) {
        repository.switchPinnedStatus(noteId)
    }
}