package edu.skillbox.skillcinema.models.person

import edu.skillbox.skillcinema.models.person.filmOfPerson.FilmOfPerson

data class PersonInfo(
    val personId: Int,
    val nameRu: String?,
    val nameEn: String?,
    val sex: String?,
    val posterUrl: String,
    val profession: String?,
    val films: List<FilmOfPerson>
)