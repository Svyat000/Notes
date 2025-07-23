package com.sddrozdov.notes.domain.model
data class Note(
    val id: Int,
    val title: String,
    val content: String,
    val updatedAt: Long,
    val isPinned: Boolean
)
