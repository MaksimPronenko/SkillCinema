package edu.skillbox.skillcinema.models

open class InterestedItem

data class FilmDbViewed(
    val filmDb: FilmDb,
    val viewed: Boolean
) : InterestedItem()