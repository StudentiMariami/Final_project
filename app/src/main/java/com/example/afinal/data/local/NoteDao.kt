package com.example.afinal.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.afinal.model.Note

/**
 * DAO = Data Access Object
 *
 * This interface defines all the SQL operations for the "notes" table.
 * Room reads these annotations and GENERATES the actual SQL code at compile time.
 * We never write raw SQL manually — Room does it for us.
 *
 * @Dao — marks this interface as a Room DAO
 */
@Dao
interface NoteDao {

    /**
     * @Query — runs a custom SQL SELECT.
     * "ORDER BY timestamp DESC" means newest notes appear first.
     * Returns LiveData<List<Note>> so the UI automatically updates when data changes.
     */
    @Query("SELECT * FROM notes ORDER BY timestamp DESC")
    fun getAllNotes(): LiveData<List<Note>>

    /**
     * @Insert(onConflict = OnConflictStrategy.REPLACE)
     * Inserts a new Note row. If a row with the same primary key already exists,
     * it REPLACES it (used for update operations too).
     * suspend = runs on a background coroutine (never blocks the UI thread)
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note)

    /**
     * @Delete — generates DELETE SQL based on the Note's primary key.
     */
    @Delete
    suspend fun deleteNote(note: Note)

    /**
     * @Update — generates UPDATE SQL, matching by primary key.
     */
    @Update
    suspend fun updateNote(note: Note)

    /**
     * Fetches a single Note by its ID.
     * Used when navigating to the Detail screen.
     */
    @Query("SELECT * FROM notes WHERE id = :noteId LIMIT 1")
    suspend fun getNoteById(noteId: Int): Note?
}
