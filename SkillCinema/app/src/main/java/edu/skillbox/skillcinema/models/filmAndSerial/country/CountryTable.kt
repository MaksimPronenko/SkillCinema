package edu.skillbox.skillcinema.models.filmAndSerial.country

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "country_table",
    primaryKeys = ["film_id", "country"]
)
data class CountryTable(
    @ColumnInfo(name = "film_id")
    val filmId: Int,
    @ColumnInfo(name = "country")
    val country: String
)