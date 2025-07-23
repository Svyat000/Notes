package com.sddrozdov.notes.data.repository

import android.content.Context
import com.sddrozdov.notes.data.NotesDataBase
import com.sddrozdov.notes.data.model.NoteDbModel
import com.sddrozdov.notes.data.toDbModel
import com.sddrozdov.notes.data.toEntities
import com.sddrozdov.notes.data.toEntity
import com.sddrozdov.notes.domain.model.Note
import com.sddrozdov.notes.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NoteRepositoryImpl private constructor(context: Context) : NoteRepository {

    private val notesDataBase = NotesDataBase.getInstance(context)

    private val notesDao = notesDataBase.notesDao()
    override suspend fun addNote(
        title: String,
        content: String,
        isPinned: Boolean,
        updatedAt: Long
    ) {
        val noteDbModel = NoteDbModel(0, title, content, updatedAt, isPinned)
        notesDao.addNote(noteDbModel)
    }

    override suspend fun deleteNote(noteId: Int) {
        notesDao.deleteNote(noteId)
    }

    override suspend fun editNote(note: Note) {
        notesDao.addNote(note.toDbModel())
    }

    override fun getAllNotes(): Flow<List<Note>> {
        return notesDao.getAllNotes().map { it.toEntities() }
    }

    override suspend fun getNote(noteId: Int): Note {
        return notesDao.getNote(noteId).toEntity()
    }

    override fun searchNote(query: String): Flow<List<Note>> {
        return notesDao.searchNotes(query).map { it.toEntities() }
    }

    override suspend fun switchPinnedStatus(noteId: Int) {
        notesDao.switchPinnedStatus(noteId)
    }

    companion object {

        private val lock = Any()
        private var instance: NoteRepositoryImpl? = null

        fun getInstance(context: Context): NoteRepositoryImpl {

            instance?.let { return it }
            synchronized(lock) {
                instance?.let { return it }

                return NoteRepositoryImpl(context = context).also {
                    instance = it }
            }
        }
    }
}