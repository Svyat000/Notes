@file:OptIn(ExperimentalFoundationApi::class)

package com.sddrozdov.notes.presentation.screens.notes

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sddrozdov.notes.domain.model.Note
import com.sddrozdov.notes.presentation.ui.theme.OtherNotesColors
import com.sddrozdov.notes.presentation.ui.theme.PinnedNotesColors

@Composable
fun NoteScreen(
    modifier: Modifier = Modifier,
    viewModel: NotesViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsState()

    LazyColumn(
        modifier = modifier
            .padding(top = 48.dp),
    ) {

        item {
            Title(
                modifier.padding(horizontal = 24.dp),
                text = "All Notes"
            )
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
        item {
            SearchBar(
                modifier.padding(horizontal = 24.dp),
                query = state.query,
                onQueryChange = { viewModel.processCommand(NotesCommand.InputSearchQuery(it)) })
        }
        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
        item {
            Subtitle(
                modifier.padding(horizontal = 24.dp),
                text = "Pinned"
            )
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
        item {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 24.dp)
            ) {
                state.pinnedNotes.forEachIndexed() { index, note ->
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
                            backGroundColor = PinnedNotesColors[index % PinnedNotesColors.size]
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            Subtitle(
                modifier.padding(horizontal = 24.dp),
                text = "Others"
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        itemsIndexed(
            state.otherNotes,
            key = { _, note -> note.id }
        ) { index, note ->
            NoteCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
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
                backGroundColor = OtherNotesColors[index % OtherNotesColors.size]
            )
            Spacer(modifier = Modifier.height(8.dp))
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
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.onSurface)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                shape = RoundedCornerShape(10.dp)
            ),
        value = query,
        onValueChange = onQueryChange,
        placeholder = {
            Text(
                text = "Search...",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search notes",
                tint = MaterialTheme.colorScheme.onSurface
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
            .padding(16.dp)
    ) {
        Text(
            text = note.title,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface

        )

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = note.updatedAt.toString(),
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant

        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = note.content,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium


        )
    }


}