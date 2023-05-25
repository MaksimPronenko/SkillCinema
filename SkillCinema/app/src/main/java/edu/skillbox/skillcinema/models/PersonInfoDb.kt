package edu.skillbox.skillcinema.models

import androidx.room.Embedded
import androidx.room.Relation

data class PersonInfoDb(
    @Embedded
    val personTable: PersonTable,

    @Relation(
        parentColumn = "person_id",
        entityColumn = "person_id"
    )
    val filmsOfPerson: List<FilmOfPersonTable>
)