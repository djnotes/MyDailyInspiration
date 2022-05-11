package com.mehdi.memo.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Note(
        @PrimaryKey (autoGenerate = true) @ColumnInfo(name = "id") val id : Int = 0 ,
        @ColumnInfo(name = "title") val title: String,
        @ColumnInfo(name = "text") val text: String,
        @ColumnInfo(name = "author") val author: String? = null,
        @ColumnInfo(name = "priority") val priority: Int? = null,
        @ColumnInfo(name = "last_modified") val lastModified: String? = null
)