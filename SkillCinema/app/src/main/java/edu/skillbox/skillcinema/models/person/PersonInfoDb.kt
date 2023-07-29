package edu.skillbox.skillcinema.models.person

import androidx.room.Embedded
import androidx.room.Relation
import edu.skillbox.skillcinema.models.person.filmOfPerson.FilmOfPersonTable

data class PersonInfoDb(
    @Embedded
    val personTable: PersonTable,

    @Relation(
        parentColumn = "person_id",
        entityColumn = "person_id"
    )
    val filmsOfPerson: List<FilmOfPersonTable>
)