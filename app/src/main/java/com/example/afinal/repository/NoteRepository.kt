package com.example.afinal.repository

import androidx.lifecycle.LiveData
import com.example.afinal.data.local.NoteDao
import com.example.afinal.data.remote.ApiService
import com.example.afinal.model.Note

/**
 * REPOSITORY (the "Model" data layer in MVVM)
 *
 * The Repository is the single source of truth for data.
 * It decides whether to fetch data from the LOCAL database (Room)
 * or the REMOTE server (Retrofit/Firebase).
 *
 * The ViewModel talks ONLY to the Repository — never directly to Room or Retrofit.
 * This separation makes the code testable and maintainable.
 */
class NoteRepository(
    private val noteDao: NoteDao,        // Local Room database access
    private val apiService: ApiService   // Remote Retrofit API access
) {

    /**
     * getAllNotes() returns a LiveData list from Room.
     * Room automatically posts updates whenever the database changes.
     * The UI observes this LiveData and refreshes itself.
     */
    val allNotes: LiveData<List<Note>> = noteDao.getAllNotes()

    /**
     * Inserts a note into the LOCAL Room database.
     * suspend = must be called from a coroutine (not the main thread).
     */
    suspend fun insert(note: Note) {
        noteDao.insertNote(note)
    }

    /**
     * Deletes a note from the LOCAL Room database.
     */
    suspend fun delete(note: Note) {
        noteDao.deleteNote(note)
    }

    /**
     * Updates an existing note in Room.
     */
    suspend fun update(note: Note) {
        noteDao.updateNote(note)
    }

    /**
     * Fetches a note by ID from local Room.
     * Returns null if not found.
     */
    suspend fun getNoteById(id: Int): Note? {
        return noteDao.getNoteById(id)
    }

    /**
     * Example: fetch notes from the remote API and save them to Room.
     * This is a "sync" operation — pull remote data into local cache.
     * Returns true on success, false on failure.
     */
    suspend fun syncFromRemote(): Boolean {
        return try {
            val response = apiService.getNotes()
            if (response.isSuccessful) {
                response.body()?.forEach { note ->
                    noteDao.insertNote(note) // Save each remote note locally
                }
                true
            } else {
                false
            }
        } catch (e: Exception) {
            // Network unavailable or server error — fail gracefully
            false
        }
    }
}
