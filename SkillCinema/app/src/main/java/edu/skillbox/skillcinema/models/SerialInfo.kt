package edu.skillbox.skillcinema.models

data class SerialInfo(
    val total: Int,
    val items: List<Season>
)

data class Season(
    val number: Int,
    val episodes: List<Episode>
)

data class Episode(
    val seasonNumber: Int,
    val episodeNumber: Int,
    val nameRu: String?,
    val nameEn: String?,
    val synopsis: String?,
    val releaseDate: String?
)