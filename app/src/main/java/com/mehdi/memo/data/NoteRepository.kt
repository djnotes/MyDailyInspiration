package com.mehdi.memo.data

import androidx.lifecycle.LiveData

class NoteRepository (private val noteDao: NoteDao){

    val notes : LiveData<List<Note>> = noteDao.getAll()

    suspend fun insert(note: Note){
        noteDao.insertAll(note)
    }
}