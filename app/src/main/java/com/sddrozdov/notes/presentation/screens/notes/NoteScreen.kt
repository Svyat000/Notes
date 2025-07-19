@file:OptIn(ExperimentalFoundationApi::class)

package com.sddrozdov.notes.presentation.screens.notes

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalMapOf
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.ProcessCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sddrozdov.notes.domain.model.Note

@Composable
fun NoteScreen(
    modifier: Modifier = Modifier,
    viewModel: NotesViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsState()

    LazyColumn(
        modifier = modifier
            .padding(top = 48.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        item {
            Title(text = "All Notes")
        }
        item {
            SearchBar(query = state.query,
                onQueryChange = { viewModel.processCommand(NotesCommand.InputSearchQuery(it)) })
        }
        item {
            Subtitle(text = "Pinned")
        }
        item {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.pinnedNotes.forEach { note ->
                    item(key = note.id) {
                        NoteCard(
                            note = note,
                            onClick = {
                                viewModel.processCommand(
                                    NotesCommand.EditNote(
                                        it
                                    )
                                )
                            },
                            onDoubleClick = {
                                viewModel.processCommand(
                                    NotesCommand.DeleteNote(
                                        it.id
                                    )
                                )
                            },
                            onLongClick = {
                                viewModel.processCommand(
                                    NotesCommand.SwitchedPinnedStatus(
                                        it.id
                                    )
                                )
                            },
                            backGroundColor = Color.Green
                        )
                    }
                }
            }
        }

        item {
            Subtitle(text = "Others")
        }

        items(
            state.otherNotes,
            key = { it.id }
        ) { note ->
            NoteCard(
                modifier = Modifier.fillMaxWidth(),
                note = note,
                onClick = {
                    viewModel.processCommand(
                        NotesCommand.EditNote(
                            it
                        )
                    )
                },
                onDoubleClick = {
                    viewModel.processCommand(
                        NotesCommand.DeleteNote(
                            it.id
                        )
                    )
                },
                onLongClick = {
                    viewModel.processCommand(
                        NotesCommand.SwitchedPinnedStatus(
                            it.id
                        )
                    )
                },
                backGroundColor = Color.Gray
            )
        }

//        state.otherNotes.forEach { note ->
//            item {
//                NoteCard(
//                    note = note,
//                ) { viewModel.processCommand(NotesCommand.SwitchedPinnedStatus(it.id)) }
//            }
//        }
    }
}

@Composable
private fun Title(
    modifier: Modifier = Modifier,
    text: String,
) {
    Text(
        modifier = modifier, text = text, fontSize = 24.sp, fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
private fun SearchBar(
    modifier: Modifier = Modifier,
    query: String,
    onQueryChange: (String) -> Unit
) {
    TextField(
        modifier = modifier.fillMaxWidth(),
        value = query,
        onValueChange = onQueryChange,
        placeholder = {
            Text(
                text = "Search...",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search notes"
            )
        }, shape = RoundedCornerShape(10.dp)

    )
}

@Composable
private fun Subtitle(
    modifier: Modifier = Modifier,
    text: String
) {
    Text(
        modifier = modifier,
        text = text,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
    )
}


@Composable
fun NoteCard(
    modifier: Modifier = Modifier,
    note: Note,
    backGroundColor: Color,
    onClick: (Note) -> Unit,
    onLongClick: (Note) -> Unit,
    onDoubleClick: (Note) -> Unit,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backGroundColor)
            .combinedClickable(
                onClick = { onClick(note) },
                onLongClick = { onLongClick(note) },
                onDoubleClick = { onDoubleClick(note) }
            )
    ) {
        Text(
            text = note.title,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface

        )
        Text(
            text = note.updatedAt.toString(),
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant

        )
        Text(
            text = note.content,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium


        )
    }


}