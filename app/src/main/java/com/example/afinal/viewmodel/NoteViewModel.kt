package com.example.afinal.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.afinal.data.local.NoteDatabase
import com.example.afinal.data.remote.RetrofitInstance
import com.example.afinal.model.Note
import com.example.afinal.repository.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * VIEWMODEL (the "VM" in MVVM)
 *
 * The ViewModel is the bridge between the UI (View) and the data (Repository/Model).
 * It survives configuration changes like screen rotation — unlike Activities/Fragments.
 *
 * AndroidViewModel(application) gives us the Application context,
 * which we need to build the Room database.
 *
 * KEY RULE: ViewModel NEVER holds a reference to an Activity, Fragment, or View!
 * This prevents memory leaks.
 */
class NoteViewModel(application: Application) : AndroidViewModel(application) {

    // Initialize Room database, DAO, and Repository
    private val repository: NoteRepository

    // LiveData the UI observes — when this changes, the RecyclerView updates automatically
    val allNotes: LiveData<List<Note>>

    // LiveData for showing loading state (true = loading, false = done)
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    // LiveData for error messages (null = no error)
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    // LiveData for sync success status
    private val _syncSuccess = MutableLiveData<Boolean>()
    val syncSuccess: LiveData<Boolean> get() = _syncSuccess

    init {
        // Build the database and wire up the repository
        val noteDao = NoteDatabase.getDatabase(application).noteDao()
        repository = NoteRepository(noteDao, RetrofitInstance.api)
        // allNotes is backed by Room's LiveData — auto-updates when DB changes
        allNotes = repository.allNotes
    }

    /**
     * Insert a new note.
     * viewModelScope.launch runs the coroutine tied to the ViewModel's lifecycle.
     * Dispatchers.IO runs the DB operation on a background IO thread (never UI thread).
     */
    fun insert(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(note)
    }

    /**
     * Delete a note from Room.
     */
    fun delete(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(note)
    }

    /**
     * Update an existing note.
     */
    fun update(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(note)
    }

    /**
     * Fetch notes from the remote API and save them to local Room DB.
     * Shows a loading indicator while running.
     */
    fun syncFromRemote() = viewModelScope.launch(Dispatchers.IO) {
        _isLoading.postValue(true)          // postValue is thread-safe (called from IO thread)
        val success = repository.syncFromRemote()
        _syncSuccess.postValue(success)
        if (!success) {
            _errorMessage.postValue("Failed to sync from server")
        }
        _isLoading.postValue(false)
    }

    /**
     * Clear any error message after it has been shown to the user.
     */
    fun clearError() {
        _errorMessage.value = null
    }
}
