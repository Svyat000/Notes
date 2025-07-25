package com.sddrozdov.notes.domain.useCases

import com.sddrozdov.notes.domain.model.ContentItem
import com.sddrozdov.notes.domain.repository.NoteRepository
import javax.inject.Inject

class AddNoteUseCase @Inject constructor(
    private val repository: NoteRepository
) {

    suspend operator fun invoke(
        title: String,
        content: List<ContentItem>,
    ) {
        repository.addNote(
            title = title,
            content = content,
            isPinned = false,
            updatedAt = System.currentTimeMillis()
        )
    }
}