package com.mehdi.memo.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(Note::class), version = 1)
abstract class AppDb : RoomDatabase () {
    abstract fun noteDao() : NoteDao
}