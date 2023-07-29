package edu.skillbox.skillcinema.models.filmAndSerial.film

import edu.skillbox.skillcinema.models.filmAndSerial.country.Country
import edu.skillbox.skillcinema.models.filmAndSerial.genre.Genre

data class FilmInfo(
    val kinopoiskId: Int,
    val imdbId: String?,
    val nameRu: String?,
    val nameEn: String?,
    val nameOriginal: String?,
    val posterUrl: String,
    val posterUrlPreview: String,
    val ratingKinopoisk: Float?,
    val year: Int?,
    val filmLength: Int?,
    val description: String?,
    val shortDescription: String?,
    val ratingAgeLimits: String?,
    val countries: List<Country>,
    val genres: List<Genre>
)