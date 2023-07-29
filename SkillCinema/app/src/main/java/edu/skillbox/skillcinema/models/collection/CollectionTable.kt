package edu.skillbox.skillcinema.models.collection

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "collection_table",
    primaryKeys = ["collection", "film_id"]
)
data class CollectionTable(
    @ColumnInfo(name = "collection")
    val collection: String,
    @ColumnInfo(name = "film_id")
    val filmId: Int
)