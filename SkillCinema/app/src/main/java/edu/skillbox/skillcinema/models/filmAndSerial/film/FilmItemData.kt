package edu.skillbox.skillcinema.models.filmAndSerial.film

import edu.skillbox.skillcinema.models.collection.RecyclerViewItem

open class InterestedViewItem : RecyclerViewItem()

data class FilmItemData(
    val filmId: Int,
    val name: String,
    val year: String?,
    val genres: String,
    val poster: String,
    val rating: String?,
    val viewed: Boolean
) : InterestedViewItem()