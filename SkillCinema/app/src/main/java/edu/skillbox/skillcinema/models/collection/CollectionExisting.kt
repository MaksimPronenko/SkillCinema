package edu.skillbox.skillcinema.models.collection

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "collection_existing")
data class CollectionExisting(
    @PrimaryKey
    @ColumnInfo(name = "collection_name")
    val collectionName: String
)