package com.sddrozdov.notes.domain.repository

import com.sddrozdov.notes.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {

    fun addNote(title: String, content: String)

    fun deleteNote(noteId: Int)

    fun editNote(note: Note)

    fun getAllNotes(): Flow<List<Note>>

    fun getNote(noteId: Int): Note

    fun searchNote(query: String): Flow<List<Note>>

    fun switchPinnedStatus(noteId: Int)

}