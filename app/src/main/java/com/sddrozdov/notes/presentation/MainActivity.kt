package com.sddrozdov.notes.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.sddrozdov.notes.presentation.screens.creation.CreateNoteScreen
import com.sddrozdov.notes.presentation.screens.editing.EditNoteScreen
import com.sddrozdov.notes.presentation.screens.notes.NoteScreen
import com.sddrozdov.notes.presentation.ui.theme.NotesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NotesTheme {
                EditNoteScreen(
                    noteId = 2,
                    onFinished = {

                    }
                )
//                CreateNoteScreen(
//                    onFinished = {
//
//                    }
//                )
//                NoteScreen(
//                    onNoteClick = {
//
//                },
//                    onAddNoteClick = {
//
//                    }
//                )
            }
        }
    }
}

