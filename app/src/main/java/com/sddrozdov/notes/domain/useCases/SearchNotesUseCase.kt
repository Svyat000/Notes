package com.sddrozdov.notes.domain.useCases

import com.sddrozdov.notes.domain.model.Note
import kotlinx.coroutines.flow.Flow

class SearchNotesUseCase {

    operator fun invoke(query: String) : Flow<List<Note>> {
TODO()
    }

}