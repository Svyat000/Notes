package com.sddrozdov.notes.presentation.screens.editing

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sddrozdov.notes.domain.model.ContentItem
import com.sddrozdov.notes.domain.model.Note
import com.sddrozdov.notes.domain.useCases.DeleteNoteUseCase
import com.sddrozdov.notes.domain.useCases.EditNoteUseCase
import com.sddrozdov.notes.domain.useCases.GetNoteUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = EditNoteViewModel.Factory::class)
class EditNoteViewModel @AssistedInject constructor(
    @Assisted("noteId") private val noteId: Int,
    private val editNoteUseCase: EditNoteUseCase,
    private val getNoteUseCase: GetNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,

    ) : ViewModel() {

    private val _state = MutableStateFlow<EditNoteState>(EditNoteState.Initial)
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.update {
                val note = getNoteUseCase(noteId)
                val content = if (note.content.lastOrNull() !is ContentItem.Text) {
                    note.content + ContentItem.Text("")
                } else {
                    note.content
                }
                EditNoteState.Editing(note.copy(content = content))
            }
        }
    }

    fun processCommand(command: EditNoteCommand) {
        when (command) {
            EditNoteCommand.Back -> {
                _state.update { EditNoteState.Finished }
            }

            is EditNoteCommand.InputContent -> {
                _state.update { previousState ->
                    if (previousState is EditNoteState.Editing) {
                        val newContent =
                            previousState.note.content
                                .mapIndexed { index, contentItem ->
                                    if (index == command.index && contentItem is ContentItem.Text) {
                                        contentItem.copy(content = command.content)
                                    } else {
                                        contentItem
                                    }
                                }
                        val newNote = previousState.note.copy(content = newContent)
                        previousState.copy(note = newNote)
                    } else {
                        previousState
                    }
                }
            }

            is EditNoteCommand.InputTitle -> {
                _state.update { previousState ->
                    if (previousState is EditNoteState.Editing) {
                        val newNote = previousState.note.copy(title = command.title)
                        previousState.copy(note = newNote)
                    } else {
                        previousState
                    }
                }
            }


            EditNoteCommand.Save -> {
                viewModelScope.launch {
                    _state.update { previuosState ->
                        if (previuosState is EditNoteState.Editing) {
                            val note = previuosState.note
                            val content = note.content.filter {
                                it !is ContentItem.Text || it.content.isNotBlank()
                            }
                            editNoteUseCase(note.copy(content = content))
                            EditNoteState.Finished
                        } else {
                            previuosState
                        }
                    }
                }
            }

            EditNoteCommand.Delete -> {
                viewModelScope.launch {
                    _state.update { previuosState ->
                        if (previuosState is EditNoteState.Editing) {
                            val note = previuosState.note
                            deleteNoteUseCase(note.id)
                            EditNoteState.Finished
                        } else {
                            previuosState
                        }
                    }
                }
            }

            is EditNoteCommand.AddImage -> {
                _state.update { previuosState ->
                    if (previuosState is EditNoteState.Editing) {
                        val oldNote = previuosState.note
                        oldNote.content.toMutableList().apply {
                            val lastItem = last()
                            if (lastItem is ContentItem.Text && lastItem.content.isBlank()) {
                                removeAt(lastIndex)
                            }
                            add(ContentItem.Image(command.uri.toString()))
                            add(ContentItem.Text(""))
                        }.let {
                            val newNote = oldNote.copy(content = it)
                            previuosState.copy(note = newNote)
                        }
                    } else {
                        previuosState
                    }
                }

            }

            is EditNoteCommand.DeleteImage -> {
                _state.update { previuosState ->
                    if (previuosState is EditNoteState.Editing) {
                        val oldNote = previuosState.note
                        oldNote.content.toMutableList().apply {
                            removeAt(command.index)
                        }.let {
                            val newNote = oldNote.copy(content = it)
                            previuosState.copy(note = newNote)
                        }
                    } else {
                        previuosState
                    }
                }
            }
        }
    }

    @AssistedFactory
    interface Factory {

        fun create(
            @Assisted("noteId") noteId: Int
        ): EditNoteViewModel
    }

}

sealed interface EditNoteCommand {
    data class InputTitle(val title: String) : EditNoteCommand
    data class InputContent(val content: String, val index: Int) : EditNoteCommand
    data class AddImage(val uri: Uri) : EditNoteCommand
    data class DeleteImage(val index: Int) : EditNoteCommand
    data object Save : EditNoteCommand
    data object Back : EditNoteCommand
    data object Delete : EditNoteCommand
}

sealed interface EditNoteState {
    data object Initial : EditNoteState
    data class Editing(
        val note: Note
    ) : EditNoteState {
        val isSaveEnabled: Boolean
            get() {
                return when {
                    note.title.isBlank() -> false
                    note.content.isEmpty() -> false
                    else -> {
                        note.content.any {
                            it !is ContentItem.Text || it.content.isNotBlank()
                        }
                    }
                }
            }
    }

    data object Finished : EditNoteState
}