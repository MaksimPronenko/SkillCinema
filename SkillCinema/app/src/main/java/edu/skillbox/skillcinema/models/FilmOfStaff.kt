package edu.skillbox.skillcinema.models

data class FilmOfStaff(
    val filmId: Int,
    val name: String,
    val poster: String?,
    val rating: Float?,
    val year: Int?,
    val genres: String,
    val profession: String
)