package com.sddrozdov.notes.presentation.screens.creation

import android.net.Uri
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
                        val newContent = previousState.content.mapIndexed { index, contentItem ->
                            if (index == command.index && contentItem is ContentItem.Text) {
                                contentItem.copy(content = command.content)
                            } else {
                                contentItem
                            }
                        }
                        previousState.copy(
                            content = newContent
                        )
                    } else {
                        previousState
                    }
                }
            }

            is CreateNoteCommand.InputTitle -> {
                _state.update { previousState ->
                    if (previousState is CreateNoteState.Creation) {
                        previousState.copy(
                            title = command.title,
                        )
                    } else {
                        previousState
                    }
                }
            }


            CreateNoteCommand.Save -> {
                viewModelScope.launch {
                    _state.update { previuosState ->
                        if (previuosState is CreateNoteState.Creation) {
                            val title = previuosState.title
                            val content = previuosState.content.filter {
                                it !is ContentItem.Text || it.content.isNotBlank()
                            }
                            addNoteUseCase.invoke(title, content)
                            CreateNoteState.Finished
                        } else {
                            previuosState
                        }
                    }
                }
            }

            is CreateNoteCommand.AddImage -> {
                _state.update { previuosState ->
                    if (previuosState is CreateNoteState.Creation) {
                        val newItems = previuosState.content.toMutableList()
                        val lastItem = newItems.last()
                        if (lastItem is ContentItem.Text && lastItem.content.isBlank()) {
                            newItems.removeAt(newItems.lastIndex)
                        }
                        newItems.add(ContentItem.Image(command.uri.toString()))
                        newItems.add(ContentItem.Text(""))
                        previuosState.copy(content = newItems)
                    } else {
                        previuosState
                    }
                }
            }

            is CreateNoteCommand.DeleteImage -> {
                _state.update { previuosState ->
                    if (previuosState is CreateNoteState.Creation) {
                        previuosState.content.toMutableList().apply {
                            removeAt(command.index)
                        }.let {
                            previuosState.copy(content = it)
                        }
                    } else {
                        previuosState
                    }
                }
            }

            else -> {}
        }
    }
}

sealed interface CreateNoteCommand {
    data class InputTitle(val title: String) : CreateNoteCommand

    data class InputContent(val content: String, val index: Int) : CreateNoteCommand

    data class AddImage(val uri: Uri) : CreateNoteCommand

    data class DeleteImage(val index: Int) : CreateNoteCommand

    data object Save : CreateNoteCommand

    data object Back : CreateNoteCommand

}

sealed interface CreateNoteState {
    data class Creation(
        val title: String = "",
        val content: List<ContentItem> = listOf(ContentItem.Text("")),
    ) : CreateNoteState {
        val isSaveEnabled: Boolean
            get() {
                return when {
                    title.isBlank() -> false
                    content.isEmpty() -> false
                    else -> {
                        content.any {
                            it !is ContentItem.Text || it.content.isNotBlank()
                        }
                    }
                }
            }
    }

    data object Finished : CreateNoteState
}