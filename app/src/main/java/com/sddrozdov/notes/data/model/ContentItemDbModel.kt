package com.sddrozdov.notes.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import kotlinx.serialization.Serializable

@Entity(
    tableName = "content",
    primaryKeys = ["noteId", "order"],
    foreignKeys = [ForeignKey(
        entity = NoteDbModel::class, parentColumns = ["id"],
        childColumns = ["noteId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class ContentItemDbModel(
    val noteId: Int,
    val contentType: ContentType,
    val content: String,
    val order: Int

) {

}

enum class ContentType {
    TEXT, IMAGE
}