package edu.skillbox.skillcinema.models

data class FilmPremiere(
    val kinopoiskId: Int,
    val nameRu: String,
    val nameEn: String,
    val year: String,
    val posterUrl: String,
    val posterUrlPreview: String,
    val countries: List<Country>,
    val genres: List<Genre>,
    val duration: Int,
    val premiereRu: String
)