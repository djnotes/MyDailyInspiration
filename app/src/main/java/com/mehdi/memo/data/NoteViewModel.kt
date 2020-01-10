package com.mehdi.memo.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: NoteRepository
    private val allNotes : LiveData<List<Note>>

    init{
        val dao  = AppDb.getDatabase(application.applicationContext, viewModelScope).noteDao()
        repository = NoteRepository(dao)
        allNotes = repository.notes
    }

    fun insert(note: Note) = viewModelScope.launch{
        repository.insert(note)
    }
}