package edu.skillbox.skillcinema.models

import androidx.room.Embedded
import androidx.room.Relation

data class SeasonsWithEpisodes(
    @Embedded
    val seasonTable: SeasonTable,

    @Relation(
        parentColumn = "film_id_and_season_number",
        entityColumn = "film_id_and_season_number"
    )
    val episodes: List<EpisodeTable>
)