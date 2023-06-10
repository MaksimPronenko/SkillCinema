package edu.skillbox.skillcinema.models

open class RecyclerViewItem

data class CollectionFilm(
    val collectionName: String,
    val filmsQuantity: Int,
    val filmIncluded: Boolean
) : RecyclerViewItem()

class EmptyRecyclerItem : RecyclerViewItem()