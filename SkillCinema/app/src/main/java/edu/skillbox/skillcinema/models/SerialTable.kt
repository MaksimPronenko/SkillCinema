package edu.skillbox.skillcinema.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "serial_table")
data class SerialTable(
    @PrimaryKey
    @ColumnInfo(name = "film_id")
    val filmId: Int,
    @ColumnInfo(name = "total_seasons")
    val totalSeasons: Int
)