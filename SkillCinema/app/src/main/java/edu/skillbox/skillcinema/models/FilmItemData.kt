package edu.skillbox.skillcinema.models

data class FilmItemData(
    val filmId: Int,
    val name: String,
    val genres: String,
    val poster: String,
    val rating: String?,
    val viewed: Boolean
)
