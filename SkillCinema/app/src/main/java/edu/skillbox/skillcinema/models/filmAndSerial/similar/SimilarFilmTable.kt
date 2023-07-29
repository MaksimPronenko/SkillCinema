package edu.skillbox.skillcinema.models.filmAndSerial.similar

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "similar_film_table",
    primaryKeys = ["film_id", "similar_film_id"]
)
data class SimilarFilmTable(
    @ColumnInfo(name = "film_id")
    val filmId: Int,
    @ColumnInfo(name = "similar_film_id")
    val similarFilmId: Int,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "description")
    val posterUrlPreview: String
)