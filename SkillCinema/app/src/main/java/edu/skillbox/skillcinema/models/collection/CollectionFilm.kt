package edu.skillbox.skillcinema.models.collection

open class RecyclerViewItem

data class CollectionFilm(
    val collectionName: String,
    val filmsQuantity: Int,
    val filmIncluded: Boolean
) : RecyclerViewItem()

class EmptyRecyclerItem : RecyclerViewItem()