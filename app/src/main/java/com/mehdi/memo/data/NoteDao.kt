package com.mehdi.memo.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface NoteDao {
    @Query("SELECT * FROM note")
    fun getAll(): LiveData<List<Note>>

    @Query("SELECT * FROM `note` WHERE `id` IN (:noteIds)")
    fun getAllByIds(noteIds: IntArray): LiveData<List<Note>>

    @Insert
    fun insertAll(vararg notes: Note)

    @Delete
    fun delete(note: Note)
}