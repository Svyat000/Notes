package com.sddrozdov.notes.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sddrozdov.notes.data.model.NoteDbModel

@Database(entities = [NoteDbModel::class], version = 2, exportSchema = false)
abstract class NotesDataBase : RoomDatabase() {
    abstract fun notesDao(): NotesDao

    companion object {

        private var instance: NotesDataBase? = null
        private val Lock = Any()
        fun getInstance(context: Context): NotesDataBase {

            instance?.let { return it }

            synchronized(Lock) {

                instance?.let { return it }

                return Room.databaseBuilder(
                    context = context,
                    klass = NotesDataBase::class.java,
                    name = "notes.db"
                ).fallbackToDestructiveMigration(dropAllTables = true).build().also {
                    instance = it
                }
            }
        }
    }
}