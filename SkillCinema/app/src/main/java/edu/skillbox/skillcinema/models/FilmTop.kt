package edu.skillbox.skillcinema.models

data class FilmTop(
    val filmId: Int,
    val nameRu: String?,
    val nameEn: String?,
    val year: String?,
    val filmLength: String?,
    val countries: List<Country>,
    val genres: List<Genre>,
    val rating: String?,
    val ratingVoteCount: Int?,
    val posterUrl: String,
    val posterUrlPreview: String
)