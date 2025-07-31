package com.sddrozdov.notes.data.repository

import com.sddrozdov.notes.data.ImageFileManager
import com.sddrozdov.notes.data.NotesDao
import com.sddrozdov.notes.data.model.NoteDbModel
import com.sddrozdov.notes.data.toContentItemDbModels
import com.sddrozdov.notes.data.toDbModel
import com.sddrozdov.notes.data.toEntities
import com.sddrozdov.notes.data.toEntity
import com.sddrozdov.notes.domain.model.ContentItem
import com.sddrozdov.notes.domain.model.Note
import com.sddrozdov.notes.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NoteRepositoryImpl @Inject constructor(
    private val notesDao: NotesDao,
    private val imageFileManager: ImageFileManager,
) : NoteRepository {

    override suspend fun addNote(
        title: String,
        content: List<ContentItem>,
        isPinned: Boolean,
        updatedAt: Long
    ) {
        val processedContent = content.processForStorage()
        val noteDbModel = NoteDbModel(0, title, updatedAt, isPinned)
        notesDao.addNoteWithContent(noteDbModel, processedContent)
    }

    override suspend fun deleteNote(noteId: Int) {
        val note = notesDao.getNote(noteId).toEntity()
        notesDao.deleteNote(noteId)

        note.content.filterIsInstance<ContentItem.Image>().map { it.url }.forEach {
            imageFileManager.deleteImage(it)
        }
    }

    override suspend fun editNote(note: Note) {
        val oldNote = notesDao.getNote(note.id).toEntity()
        val oldUrls = oldNote.content.filterIsInstance<ContentItem.Image>().map { it.url }
        val newUrls = note.content.filterIsInstance<ContentItem.Image>().map { it.url }
        val removedUrls = oldUrls - newUrls

        removedUrls.forEach {
            imageFileManager.deleteImage(it)
        }

        val processedContent = note.content.processForStorage()
        val processedNote = note.copy(content = processedContent)
        notesDao.editNote(
            noteDbModel = processedNote.toDbModel(),
            content = processedContent.toContentItemDbModels(note.id)
        )
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

    private suspend fun List<ContentItem>.processForStorage(): List<ContentItem> {
        return map { contentItem ->
            when (contentItem) {
                is ContentItem.Text -> {
                    contentItem
                }

                is ContentItem.Image -> {
                    if (imageFileManager.isInternal(contentItem.url)) {
                        contentItem
                    } else {
                        val internalPath =
                            imageFileManager.copyImageToInternalStorage(contentItem.url)
                        ContentItem.Image(internalPath)
                    }
                }
            }
        }
    }
}