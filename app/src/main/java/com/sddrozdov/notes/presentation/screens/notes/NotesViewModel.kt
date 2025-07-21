@file:Suppress("OPT_IN_USAGE")

package com.sddrozdov.notes.presentation.screens.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sddrozdov.notes.data.repository.NoteRepositoryImpl
import com.sddrozdov.notes.domain.model.Note
import com.sddrozdov.notes.domain.useCases.AddNoteUseCase
import com.sddrozdov.notes.domain.useCases.DeleteNoteUseCase
import com.sddrozdov.notes.domain.useCases.EditNoteUseCase
import com.sddrozdov.notes.domain.useCases.GetAllNotesUseCase
import com.sddrozdov.notes.domain.useCases.GetNoteUseCase
import com.sddrozdov.notes.domain.useCases.SearchNotesUseCase
import com.sddrozdov.notes.domain.useCases.SwitchPinnedStatusUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NotesViewModel : ViewModel() {

    //private val dispatcher = Executors.newCachedThreadPool().asCoroutineDispatcher()


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

    //private val scope = CoroutineScope(Dispatchers.IO)

    init {
        addSomeNotes()
        query
            .onEach { input ->
                _state.update { it.copy(query = input) }
            }
            .flatMapLatest { input ->
                if (input.isBlank()) {
                    getAllNoteUseCase()
                } else {
                    searchNotesUseCase(input)
                }
            }
            .onEach { notes ->
                val pinnedNotes = notes.filter { it.isPinned }
                val otherNotes = notes.filter { !it.isPinned }
                _state.update { it.copy(pinnedNotes = pinnedNotes, otherNotes = otherNotes) }
            }
            .launchIn(viewModelScope)


//        scope.launch {
//            query.collect{
//
//            }
//        }

    }

    //delete this test method
    private fun addSomeNotes() {
        viewModelScope.launch {
            repeat(50) {
                addNoteUseCase(title = "Title №$it", content = "Content $it")
            }
        }

    }

    fun processCommand(command: NotesCommand) {
        viewModelScope.launch {
            when (command) {
                is NotesCommand.InputSearchQuery -> query.update { command.query.trim() }
                is NotesCommand.SwitchedPinnedStatus -> switchPinnedNoteUseCase(command.id)
                is NotesCommand.DeleteNote -> deleteNoteUseCase(command.noteId)
                is NotesCommand.EditNote -> {
                    val note = getNoteUseCase(command.note.id)
                    val title = note.title
                    editNoteUseCase(note.copy(title = "$title edited"))

                }
            }
        }
    }
}

sealed interface NotesCommand {
    data class InputSearchQuery(val query: String) : NotesCommand

    data class SwitchedPinnedStatus(val id: Int) : NotesCommand

    data class DeleteNote(val noteId: Int) : NotesCommand

    data class EditNote(val note: Note) : NotesCommand

}

data class NoteScreenState(
    val query: String = "",
    val pinnedNotes: List<Note> = listOf(),
    val otherNotes: List<Note> = listOf()

)

