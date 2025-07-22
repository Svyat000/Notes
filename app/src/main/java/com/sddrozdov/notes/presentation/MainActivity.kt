package com.sddrozdov.notes.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.sddrozdov.notes.presentation.screens.notes.NoteScreen
import com.sddrozdov.notes.presentation.ui.theme.NotesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NotesTheme {
                NoteScreen(
                    onNoteClick = {

                },
                    onAddNoteClick = {

                    }
                )
            }
        }
    }
}

