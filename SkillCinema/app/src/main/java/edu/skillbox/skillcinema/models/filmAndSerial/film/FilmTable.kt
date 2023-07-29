package edu.skillbox.skillcinema.models.filmAndSerial.film

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "film_table")
data class FilmTable(
    @PrimaryKey
    @ColumnInfo(name = "film_id")
    val filmId: Int,
    @ColumnInfo(name = "imdb_id")
    val imdbId: String?,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "poster")
    val poster: String,
    @ColumnInfo(name = "poster_small")
    val posterSmall: String,
    @ColumnInfo(name = "rating")
    val rating: Float?,
    @ColumnInfo(name = "year")
    val year: Int?,
    @ColumnInfo(name = "length")
    val length: Int?,
    @ColumnInfo(name = "description")
    val description: String?,
    @ColumnInfo(name = "short_description")
    val shortDescription: String?,
    @ColumnInfo(name = "rating_age_limits")
    val ratingAgeLimits: String?
)