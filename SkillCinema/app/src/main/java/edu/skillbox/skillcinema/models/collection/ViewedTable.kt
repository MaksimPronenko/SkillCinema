package edu.skillbox.skillcinema.models.collection

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "viewed_table")
data class ViewedTable(
    @PrimaryKey(autoGenerate = true)
    val position: Int,
    @ColumnInfo(name = "film_id")
    val filmId: Int
) {
    constructor(filmId: Int) : this(0, filmId)
}