package edu.skillbox.skillcinema.models.filmAndSerial.similar

data class SimilarFilm(
    val filmId: Int,
    val nameRu: String?,
    val nameEn: String?,
    val nameOriginal: String?,
    val posterUrlPreview: String
)
