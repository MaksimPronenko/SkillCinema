package edu.skillbox.skillcinema.models.filmTop

import edu.skillbox.skillcinema.models.filmAndSerial.country.Country
import edu.skillbox.skillcinema.models.filmAndSerial.genre.Genre

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