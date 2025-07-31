package com.sddrozdov.notes.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.sddrozdov.notes.data.model.ContentItemDbModel
import com.sddrozdov.notes.data.model.NoteDbModel
import com.sddrozdov.notes.data.model.NoteWithContentDbModel
import com.sddrozdov.notes.domain.model.ContentItem
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDao {
    @Transaction
    @Query("SELECT * FROM notes ORDER BY updatedAt DESC")
    fun getAllNotes(): Flow<List<NoteWithContentDbModel>>
    @Transaction
    @Query(
        """
        SELECT DISTINCT notes.* FROM notes JOIN content ON notes.id == content.noteId
        WHERE title LIKE '%' || :query || '%' 
        OR content LIKE '%' || :query || '%' 
        ORDER BY updatedAt DESC
        """
    )
    fun searchNotes(query: String): Flow<List<NoteWithContentDbModel>>
    @Transaction
    @Query("DELETE FROM notes WHERE id==:noteId")
    suspend fun deleteNote(noteId: Int)

    @Query("DELETE FROM content WHERE noteId==:noteId")
    suspend fun deleteNoteContent(noteId: Int)

    @Query("UPDATE notes SET isPinned = NOT isPinned WHERE id == :noteId")
    suspend fun switchPinnedStatus(noteId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addNote(noteDbModel: NoteDbModel): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addNoteContent(content: List<ContentItemDbModel>)
    @Transaction
    @Query("SELECT * FROM notes WHERE id = :noteId ")
    suspend fun getNote(noteId: Int): NoteWithContentDbModel

    @Transaction
    suspend fun addNoteWithContent(
        noteDbModel: NoteDbModel,
        content: List<ContentItem>
    ) {
        val noteId = addNote(noteDbModel).toInt()
        val contentItems = content.toContentItemDbModels(noteId = noteId)
        addNoteContent(contentItems)
    }

    @Transaction
    suspend fun editNote(
        noteDbModel: NoteDbModel,
        content: List<ContentItemDbModel>
    ) {
        addNote(noteDbModel)
        deleteNoteContent(noteId = noteDbModel.id)
        addNoteContent(content)
    }
}