package com.sddrozdov.notes.domain.useCases

import com.sddrozdov.notes.domain.repository.NoteRepository
import javax.inject.Inject

class SwitchPinnedStatusUseCase @Inject constructor(
    private val repository: NoteRepository
) {

    suspend operator fun invoke(noteId: Int) {
        repository.switchPinnedStatus(noteId)
    }
}