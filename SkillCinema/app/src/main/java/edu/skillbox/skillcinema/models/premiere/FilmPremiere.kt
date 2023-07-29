package edu.skillbox.skillcinema.models.premiere

import edu.skillbox.skillcinema.models.filmAndSerial.country.Country
import edu.skillbox.skillcinema.models.filmAndSerial.genre.Genre

data class FilmPremiere(
    val kinopoiskId: Int,
    val nameRu: String?,
    val nameEn: String?,
    val year: Int,
    val posterUrl: String,
    val posterUrlPreview: String,
    val countries: List<Country>,
    val genres: List<Genre>,
    val duration: Int,
    val premiereRu: String
)