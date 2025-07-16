@file:Suppress("OPT_IN_USAGE")

package com.sddrozdov.notes.presentation.screens.notes

import androidx.lifecycle.ViewModel
import com.sddrozdov.notes.data.repository.NoteRepositoryImpl
import com.sddrozdov.notes.domain.model.Note
import com.sddrozdov.notes.domain.useCases.AddNoteUseCase
import com.sddrozdov.notes.domain.useCases.DeleteNoteUseCase
import com.sddrozdov.notes.domain.useCases.EditNoteUseCase
import com.sddrozdov.notes.domain.useCases.GetAllNotesUseCase
import com.sddrozdov.notes.domain.useCases.GetNoteUseCase
import com.sddrozdov.notes.domain.useCases.SearchNotesUseCase
import com.sddrozdov.notes.domain.useCases.SwitchPinnedStatusUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class NotesViewModel : ViewModel() {

    private val repository = NoteRepositoryImpl

    private val addNoteUseCase = AddNoteUseCase(repository)
    private val editNoteUseCase = EditNoteUseCase(repository)
    private val deleteNoteUseCase = DeleteNoteUseCase(repository)
    private val getAllNoteUseCase = GetAllNotesUseCase(repository)
    private val getNoteUseCase = GetNoteUseCase(repository)
    private val searchNotesUseCase = SearchNotesUseCase(repository)
    private val switchPinnedNoteUseCase = SwitchPinnedStatusUseCase(repository)

    private val query = MutableStateFlow("")

    private val _state = MutableStateFlow(NoteScreenState())
    val state = _state.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.IO)

    init {

        query
            .flatMapLatest {
                if (it.isBlank()) {
                    getAllNoteUseCase()
                } else {
                    searchNotesUseCase(it)
                }
            }
            .onEach {
                val pinnedNotes = it.filter { it.isPinned }
                val otherNotes = it.filter { !it.isPinned }
                _state.update { it.copy(pinnedNotes = pinnedNotes, otherNotes = otherNotes) }
            }
            .launchIn(scope)


//        scope.launch {
//            query.collect{
//
//            }
//        }

    }

    fun processCommand(command: NotesCommand) {
        when (command) {
            is NotesCommand.InputSearchQuery -> NotesCommand.InputSearchQuery(command.query)
            is NotesCommand.SwitchedPinnedStatus -> switchPinnedNoteUseCase(command.id)
        }
    }


}

sealed interface NotesCommand {
    data class InputSearchQuery(val query: String) : NotesCommand

    data class SwitchedPinnedStatus(val id: Int) : NotesCommand

}

data class NoteScreenState(
    val query: String = "",
    val pinnedNotes: List<Note> = listOf(),
    val otherNotes: List<Note> = listOf()
)