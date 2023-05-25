package edu.skillbox.skillcinema.models

import androidx.room.Embedded
import androidx.room.Relation

data class FilmInfoDb(
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

    @Relation(
        parentColumn = "film_id",
        entityColumn = "film_id"
    )
    val collections: List<CollectionTable>,

    @Relation(
        parentColumn = "film_id",
        entityColumn = "film_id"
    )
    val staffList: List<StaffTable>,

    @Relation(
        parentColumn = "film_id",
        entityColumn = "film_id"
    )
    val images: List<ImageTable>,

    @Relation(
        parentColumn = "film_id",
        entityColumn = "film_id"
    )
    val similarFilms: List<SimilarFilmTable>
)