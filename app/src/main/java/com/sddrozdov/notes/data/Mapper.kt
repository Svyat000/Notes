package com.sddrozdov.notes.data

import com.sddrozdov.notes.data.model.ContentItemDbModel
import com.sddrozdov.notes.data.model.ContentType
import com.sddrozdov.notes.data.model.NoteDbModel
import com.sddrozdov.notes.data.model.NoteWithContentDbModel
import com.sddrozdov.notes.domain.model.ContentItem
import com.sddrozdov.notes.domain.model.Note

fun Note.toDbModel(): NoteDbModel {
    return NoteDbModel(id, title, updatedAt, isPinned)
}

fun List<ContentItem>.toContentItemDbModels(noteId: Int): List<ContentItemDbModel> {
    return mapIndexed() { index, contentItem ->
        when (contentItem) {
            is ContentItem.Image -> {
                ContentItemDbModel(
                    noteId = noteId,
                    contentType = ContentType.IMAGE,
                    content = contentItem.url,
                    order = index
                )
            }

            is ContentItem.Text -> {
                ContentItemDbModel(
                    noteId = noteId,
                    contentType = ContentType.TEXT,
                    content = contentItem.content,
                    order = index
                )
            }
        }
    }
}

fun List<ContentItemDbModel>.toContentItem(): List<ContentItem> {
    return map { contentItem ->
        when (contentItem.contentType) {
            ContentType.TEXT -> {
                ContentItem.Text(content = contentItem.content)
            }

            ContentType.IMAGE -> {
                ContentItem.Image(url = contentItem.content)
            }
        }
    }
}


fun NoteWithContentDbModel.toEntity(): Note {
    return Note(
        id = noteDbModel.id,
        title = noteDbModel.title,
        content = content.toContentItem(),
        updatedAt = noteDbModel.updatedAt,
        isPinned = noteDbModel.isPinned
    )
}

fun List<NoteWithContentDbModel>.toEntities(): List<Note> {
    return this.map { it.toEntity() }
}