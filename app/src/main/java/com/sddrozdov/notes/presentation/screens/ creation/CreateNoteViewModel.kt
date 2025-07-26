package com.sddrozdov.notes.presentation.screens.creation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sddrozdov.notes.domain.model.ContentItem
import com.sddrozdov.notes.domain.useCases.AddNoteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateNoteViewModel @Inject constructor(private val addNoteUseCase: AddNoteUseCase) :
    ViewModel() {

    private val _state = MutableStateFlow<CreateNoteState>(CreateNoteState.Creation())
    val state = _state.asStateFlow()

    fun processCommand(command: CreateNoteCommand) {
        when (command) {
            CreateNoteCommand.Back -> {
                _state.update { CreateNoteState.Finished }
            }

            is CreateNoteCommand.InputContent -> {
                _state.update { previousState ->
                    if (previousState is CreateNoteState.Creation) {
                        previousState.copy(
                            content = command.content,
                            isSaveEnable = previousState.title.isNotBlank() && command.content.isNotBlank()
                        )
                    } else {
                        CreateNoteState.Creation(content = command.content)
                    }
                }
            }

            is CreateNoteCommand.InputTitle -> {
                _state.update { previousState ->
                    if (previousState is CreateNoteState.Creation) {
                        previousState.copy(
                            title = command.title,
                            isSaveEnable = previousState.title.isNotBlank() && previousState.content.isNotBlank()
                        )
                    } else {
                        CreateNoteState.Creation(title = command.title)
                    }
                }
            }


            CreateNoteCommand.Save -> {
                viewModelScope.launch {
                    _state.update { previuosState ->
                        if (previuosState is CreateNoteState.Creation) {
                            val title = previuosState.title
                            val content = ContentItem.Text(previuosState.content)
                            addNoteUseCase.invoke(title, listOf(content))
                            CreateNoteState.Finished
                        } else {
                            previuosState
                        }
                    }
                }
            }

            else -> {}
        }
    }

}

sealed interface CreateNoteCommand {
    data class InputTitle(val title: String) : CreateNoteCommand
    data class InputContent(val content: String) : CreateNoteCommand

    data object Save : CreateNoteCommand

    data object Back : CreateNoteCommand


}

sealed interface CreateNoteState {
    data class Creation(
        val title: String = "",
        val content: String = "",
        val isSaveEnable: Boolean = false
    ) : CreateNoteState

    data object Finished : CreateNoteState
}