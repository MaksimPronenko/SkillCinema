package edu.skillbox.skillcinema.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "person_table")
data class PersonTable(
    @PrimaryKey
    @ColumnInfo(name = "person_id")
    val personId: Int,
    @ColumnInfo(name = "name")
    val name: String?,
    @ColumnInfo(name = "sex")
    val sex: String?,
    @ColumnInfo(name = "poster_url")
    val posterUrl: String,
    @ColumnInfo(name = "profession")
    val profession: String?
)