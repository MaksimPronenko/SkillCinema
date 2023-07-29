package edu.skillbox.skillcinema.models.person.filmOfPerson

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "film_of_person_table",
    primaryKeys = ["person_id", "film_id"]
)
data class FilmOfPersonTable(
    @ColumnInfo(name = "person_id")
    val personId: Int,
    @ColumnInfo(name = "film_id")
    val filmId: Int,
    @ColumnInfo(name = "film_name")
    val name: String,
    @ColumnInfo(name = "rating")
    val rating: Float?,
    @ColumnInfo(name = "profession_key")
    val professionKey: String
)