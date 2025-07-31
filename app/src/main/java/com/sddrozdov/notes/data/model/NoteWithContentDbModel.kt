package com.sddrozdov.notes.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class NoteWithContentDbModel(
    @Embedded
    val noteDbModel: NoteDbModel,
    @Relation(parentColumn = "id", entityColumn = "noteId")
    val content: List<ContentItemDbModel>
)
