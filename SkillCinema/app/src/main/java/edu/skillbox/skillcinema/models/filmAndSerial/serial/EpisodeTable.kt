package edu.skillbox.skillcinema.models.filmAndSerial.serial

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "episode_table",
    primaryKeys = ["film_id_and_season_number", "episode_number"]
)
data class EpisodeTable(
    @ColumnInfo(name = "film_id_and_season_number")
    val filmIdAndSeasonNumber: String,
    @ColumnInfo(name = "season_number")
    val seasonNumber: Int,
    @ColumnInfo(name = "episode_number")
    val episodeNumber: Int,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "synopsis")
    val synopsis: String?,
    @ColumnInfo(name = "release_date_converted")
    val releaseDateConverted: String?
)