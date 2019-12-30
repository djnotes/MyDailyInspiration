package com.mehdi.memo.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Note(
        @PrimaryKey val note_id: Int,
        @ColumnInfo(name = "note_title") val noteTitle: String?,
        @ColumnInfo(name = "note_text") val noteText: String?,
        @ColumnInfo(name = "note_author") val noteAuthor: String?
)
