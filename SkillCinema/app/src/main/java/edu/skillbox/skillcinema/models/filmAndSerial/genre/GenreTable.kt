package edu.skillbox.skillcinema.models.filmAndSerial.genre

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "genre_table",
    primaryKeys = ["film_id", "genre"]
)
data class GenreTable(
    @ColumnInfo(name = "film_id")
    val filmId: Int,
    @ColumnInfo(name = "genre")
    val genre: String
)