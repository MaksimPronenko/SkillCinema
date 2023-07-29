package edu.skillbox.skillcinema.models.filmAndSerial.image

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "image_table",
    primaryKeys = ["film_id", "image", "preview"]
)
data class ImageTable(
    @ColumnInfo(name = "film_id")
    val filmId: Int,
    @ColumnInfo(name = "image")
    val image: String,
    @ColumnInfo(name = "preview")
    val preview: String,
    @ColumnInfo(name = "type")
    val type: String
)