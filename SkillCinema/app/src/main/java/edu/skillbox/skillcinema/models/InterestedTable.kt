package edu.skillbox.skillcinema.models

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "interested_table",
    primaryKeys = ["id", "type"]
)
data class InterestedTable(
    @ColumnInfo(name = "id")
    val id: Int,
    @ColumnInfo(name = "type")
    val type: Int
)