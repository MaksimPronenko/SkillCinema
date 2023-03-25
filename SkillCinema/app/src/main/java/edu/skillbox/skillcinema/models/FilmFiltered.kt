package edu.skillbox.skillcinema.models

data class FilmFiltered(
    val kinopoiskId: Int,
    val imdbId: String,
    val nameRu: String,
    val nameEn: String,
    val nameOriginal: String,
    val countries: List<Country>,
    val genres: List<Genre>,
    val ratingKinopoisk: Double?,
    val ratingImdb: Double?,
    val year: String,
    val type: String,
    val posterUrl: String,
    val posterUrlPreview: String
)