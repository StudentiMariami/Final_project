package com.example.afinal.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.afinal.model.Note

/**
 * ROOM DATABASE
 *
 * This abstract class is the main access point for the local SQLite database.
 * Room generates the actual implementation at compile time.
 *
 * @Database(entities = [Note::class], version = 1)
 *   - entities: the list of tables (one @Entity class = one table)
 *   - version: if you change the schema, increment this number
 *
 * exportSchema = false — disables exporting the DB schema to a file (not needed here)
 */
@Database(entities = [Note::class], version = 1, exportSchema = false)
abstract class NoteDatabase : RoomDatabase() {

    // Room will implement this function and return our NoteDao
    abstract fun noteDao(): NoteDao

    companion object {
        /**
         * @Volatile ensures the INSTANCE value is always read from main memory,
         * not from a CPU cache — critical for thread safety.
         */
        @Volatile
        private var INSTANCE: NoteDatabase? = null

        /**
         * Singleton pattern — we only ever create ONE database instance.
         * synchronized(this) prevents two threads from creating it simultaneously.
         */
        fun getDatabase(context: Context): NoteDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,   // Use application context to avoid memory leaks
                    NoteDatabase::class.java,
                    "note_database"               // The file name for the SQLite database
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
