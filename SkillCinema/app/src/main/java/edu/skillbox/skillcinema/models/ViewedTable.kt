package edu.skillbox.skillcinema.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "viewed_table")
data class ViewedTable(
    @PrimaryKey
    @ColumnInfo(name = "film_id")
    val filmId: Int
)