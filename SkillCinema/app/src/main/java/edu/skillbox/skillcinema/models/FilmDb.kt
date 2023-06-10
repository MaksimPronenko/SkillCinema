package edu.skillbox.skillcinema.models

import androidx.room.Embedded
import androidx.room.Relation

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