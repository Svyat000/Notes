@file:OptIn(ExperimentalFoundationApi::class)

package com.sddrozdov.notes.presentation.screens.notes

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.sddrozdov.notes.R
import com.sddrozdov.notes.domain.model.ContentItem
import com.sddrozdov.notes.domain.model.Note
import com.sddrozdov.notes.presentation.ui.theme.OtherNotesColors
import com.sddrozdov.notes.presentation.ui.theme.PinnedNotesColors
import com.sddrozdov.notes.presentation.utils.DateFormatter

@Composable
fun NoteScreen(
    modifier: Modifier = Modifier,
    viewModel: NotesViewModel = hiltViewModel(),
    onNoteClick: (Note) -> Unit,
    onAddNoteClick: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddNoteClick,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                containerColor = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_add_note),
                    //imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_note_button)
                )

            }
        }

    ) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding,
        ) {

            item {
                Title(
                    modifier.padding(horizontal = 24.dp),
                    text = stringResource(R.string.all_notes)
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
                    text = stringResource(R.string.pinned)
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
                                modifier = Modifier.widthIn(max = 160.dp),
                                note = note,
                                onNoteClick = onNoteClick,
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
                    text = stringResource(R.string.others)
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            itemsIndexed(
                state.otherNotes,
                key = { _, note -> note.id }
            ) { index, note ->
                val imageUrl = note.content
                    .filterIsInstance<ContentItem.Image>()
                    .map { it.url }
                    .firstOrNull()
                if (imageUrl == null) {
                    NoteCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        note = note,
                        onNoteClick = onNoteClick,
                        onLongClick = {
                            viewModel.processCommand(
                                NotesCommand.SwitchedPinnedStatus(
                                    it.id
                                )
                            )
                        },
                        backGroundColor = OtherNotesColors[index % OtherNotesColors.size]
                    )
                } else {
                    NoteCardWithImage(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        note = note,
                        imageUrl = imageUrl,
                        onNoteClick = onNoteClick,
                        onLongClick = {
                            viewModel.processCommand(
                                NotesCommand.SwitchedPinnedStatus(
                                    it.id
                                )
                            )
                        },
                        backGroundColor = OtherNotesColors[index % OtherNotesColors.size]
                    )
                }


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
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                shape = RoundedCornerShape(10.dp)
            ),
        value = query,
        onValueChange = onQueryChange,
        placeholder = {
            Text(
                text = stringResource(R.string.search),
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
                contentDescription = stringResource(R.string.search_notes),
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
    onNoteClick: (Note) -> Unit,
    onLongClick: (Note) -> Unit,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backGroundColor)
            .combinedClickable(
                onClick = { onNoteClick(note) },
                onLongClick = { onLongClick(note) },
            )
            .padding(16.dp)
    ) {
        Text(
            text = note.title,
            fontSize = 14.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurface

        )

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = DateFormatter.formatDateToString(note.updatedAt),
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant

        )

        note.content.filterIsInstance<ContentItem.Text>()
            .filter { it.content.isNotBlank() }
            .joinToString("\n") { it.content }
            .takeIf { it.isNotBlank() }
            ?.let {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = it,
                    fontSize = 16.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium
                )
            }
    }
}

@Composable
fun NoteCardWithImage(
    modifier: Modifier = Modifier,
    note: Note,
    imageUrl: String,
    backGroundColor: Color,
    onNoteClick: (Note) -> Unit,
    onLongClick: (Note) -> Unit,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backGroundColor)
            .combinedClickable(
                onClick = { onNoteClick(note) },
                onLongClick = { onLongClick(note) },
            )
    ) {
        Box {
            AsyncImage(
                modifier = Modifier
                    .heightIn(max = 120.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                model = imageUrl,
                contentDescription = "",
                contentScale = ContentScale.FillWidth
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(Color.Transparent, MaterialTheme.colorScheme.onSurface)
                        )
                    )
                    .padding(16.dp)
                    .align(Alignment.BottomStart)
            ) {
                Text(
                    text = note.title,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onPrimary

                )
                Text(
                    text = DateFormatter.formatDateToString(note.updatedAt),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onPrimary

                )
            }
        }

        note.content.filterIsInstance<ContentItem.Text>()
            .filter { it.content.isNotBlank() }
            .joinToString("\n") { it.content }
            .takeIf { it.isNotBlank() }
            ?.let {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = it,
                    fontSize = 16.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium
                )
            }
    }
}
