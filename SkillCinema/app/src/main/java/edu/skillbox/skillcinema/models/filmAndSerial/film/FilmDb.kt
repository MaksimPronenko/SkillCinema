package edu.skillbox.skillcinema.models.filmAndSerial.film

import androidx.room.Embedded
import androidx.room.Relation
import edu.skillbox.skillcinema.models.filmAndSerial.country.CountryTable
import edu.skillbox.skillcinema.models.filmAndSerial.genre.GenreTable

data class FilmDb(
    @Embedded
    val filmTable: FilmTable,

    @Relation(
        parentColumn = "film_id",
        entityColumn = "film_id"
    )
    val countries: List<CountryTable>,

    @Relation(
        parentColumn = "film_id",
        entityColumn = "film_id"
    )
    val genres: List<GenreTable>,
)