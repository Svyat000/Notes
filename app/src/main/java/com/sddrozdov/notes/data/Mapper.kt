package com.sddrozdov.notes.data

import com.sddrozdov.notes.data.model.NoteDbModel
import com.sddrozdov.notes.domain.model.Note

fun Note.toDbModel(): NoteDbModel{
    return NoteDbModel(id, title, content, updatedAt, isPinned)
}

fun NoteDbModel.toEntity(): Note{
    return Note(id, title, content, updatedAt, isPinned)
}

fun List<NoteDbModel>.toEntities() : List<Note>{
    return this.map { it.toEntity() }
}