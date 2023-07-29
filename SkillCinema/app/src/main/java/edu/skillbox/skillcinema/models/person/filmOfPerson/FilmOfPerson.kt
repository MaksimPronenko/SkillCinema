package edu.skillbox.skillcinema.models.person.filmOfPerson

data class FilmOfPerson(
    val filmId: Int,
    val nameRu: String?,
    val nameEn: String?,
    val rating: String?,
    val professionKey: String
)