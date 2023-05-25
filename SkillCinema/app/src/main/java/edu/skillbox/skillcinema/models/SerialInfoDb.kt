package edu.skillbox.skillcinema.models

import androidx.room.Embedded
import androidx.room.Relation

data class SerialInfoDb(
    @Embedded
    val serialTable: SerialTable,

    @Relation(
        parentColumn = "film_id",
        entityColumn = "film_id",
        entity = SeasonTable::class
    )
    val seasonsWithEpisodes: List<SeasonsWithEpisodes>,
)