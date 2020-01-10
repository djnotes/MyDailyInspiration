package com.mehdi.memo.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [Note::class], version = 1)
abstract class AppDb : RoomDatabase () {
    abstract fun noteDao() : NoteDao


    companion object {
        private var INSTANCE : AppDb?= null

        fun getDatabase (context: Context, scope: CoroutineScope) : AppDb {
            var tempInstance = INSTANCE
            if(tempInstance != null) {
                return tempInstance
            }

            synchronized(this) {
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDb::class.java,
                        "mdi.db"
                ).addCallback(NoteDbCallback(scope))
                        .build()
                INSTANCE = instance
                return instance
            }
        }

    }

    private class NoteDbCallback (private val scope: CoroutineScope) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let {
                    scope.launch {
                        populateDatabase(it.noteDao())
                    }
            }
        }

        suspend fun populateDatabase(noteDao: NoteDao) {
            val note  = Note(title = "Best Day", text = "Today is my best day of life", priority = 1, lastModified = "01-10-2020", author = "Mehdi")
            noteDao.insertAll(note)
        }
    }


}