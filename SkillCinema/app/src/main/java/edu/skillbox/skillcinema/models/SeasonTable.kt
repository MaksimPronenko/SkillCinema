package edu.skillbox.skillcinema.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "season_table")
data class SeasonTable(
    @PrimaryKey
    @ColumnInfo(name = "film_id_and_season_number")
    val filmIdAndSeasonNumber: String,
    @ColumnInfo(name = "film_id")
    val filmId: Int,
    @ColumnInfo(name = "season_number")
    val seasonNumber: Int
)