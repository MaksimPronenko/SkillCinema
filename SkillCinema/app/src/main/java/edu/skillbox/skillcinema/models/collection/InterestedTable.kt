package edu.skillbox.skillcinema.models.collection

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "interested_table")
data class InterestedTable(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "position")
    val position: Long,
    @ColumnInfo(name = "id")
    val id: Int,
    @ColumnInfo(name = "type")
    val type: Int
) {
    constructor(id: Int, type: Int) : this(0, id, type)
}