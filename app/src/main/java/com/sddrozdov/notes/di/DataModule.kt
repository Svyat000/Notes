package com.sddrozdov.notes.di

import android.content.Context
import androidx.room.Database
import com.sddrozdov.notes.data.NotesDao
import com.sddrozdov.notes.data.NotesDataBase
import com.sddrozdov.notes.data.repository.NoteRepositoryImpl
import com.sddrozdov.notes.domain.repository.NoteRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Binds
    @Singleton
    fun bindNoteRepository(impl: NoteRepositoryImpl): NoteRepository

    companion object {

        @Provides
        @Singleton
        fun provideDatabase(
            @ApplicationContext context: Context
        ): NotesDataBase {
            return NotesDataBase.getInstance(context)
        }

        @Provides
        @Singleton
        fun provideNotesDa0(
            database: NotesDataBase
        ): NotesDao {
            return database.notesDao()
        }
    }
}