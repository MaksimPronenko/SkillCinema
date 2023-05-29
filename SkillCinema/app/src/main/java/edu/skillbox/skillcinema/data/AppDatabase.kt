package edu.skillbox.skillcinema.data

import androidx.room.Database
import androidx.room.RoomDatabase
import edu.skillbox.skillcinema.models.*

@Database(
    entities = [
        FilmTable::class,
        CountryTable::class,
        GenreTable::class,
        CollectionTable::class,
        StaffTable::class,
        ImageTable::class,
        SimilarFilmTable::class,
        SerialTable::class,
        SeasonTable::class,
        EpisodeTable::class,
        PersonTable::class,
        FilmOfPersonTable::class,
        CollectionExisting::class
    ], version = 16
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun filmDao(): FilmDao
}