package edu.skillbox.skillcinema.models

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "staff_table",
    primaryKeys = ["film_id", "staff_id", "profession_key"]
)
data class StaffTable(
    @ColumnInfo(name = "film_id")
    val filmId: Int,
    @ColumnInfo(name = "staff_id")
    val staffId: Int,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "description")
    val description: String?,
    @ColumnInfo(name = "poster_url")
    val posterUrl: String,
    @ColumnInfo(name = "profession_text")
    val professionText: String,
    @ColumnInfo(name = "profession_key")
    val professionKey: String
)