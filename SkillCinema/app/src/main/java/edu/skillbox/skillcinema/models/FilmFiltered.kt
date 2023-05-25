package edu.skillbox.skillcinema.models

data class FilmFiltered(
    val kinopoiskId: Int,
    val nameRu: String?,
    val nameEn: String?,
    val nameOriginal: String?,
    val countries: List<Country>,
    val genres: List<Genre>,
    val ratingKinopoisk: Float?,
    val year: Int?,
    val type: String,
    val posterUrl: String,
    val posterUrlPreview: String
)