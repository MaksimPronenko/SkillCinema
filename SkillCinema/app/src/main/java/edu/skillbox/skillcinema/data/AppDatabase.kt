package edu.skillbox.skillcinema.data

import androidx.room.Database
import androidx.room.RoomDatabase
import edu.skillbox.skillcinema.models.collection.CollectionExisting
import edu.skillbox.skillcinema.models.collection.CollectionTable
import edu.skillbox.skillcinema.models.collection.InterestedTable
import edu.skillbox.skillcinema.models.collection.ViewedTable
import edu.skillbox.skillcinema.models.filmAndSerial.country.CountryTable
import edu.skillbox.skillcinema.models.filmAndSerial.film.FilmTable
import edu.skillbox.skillcinema.models.filmAndSerial.genre.GenreTable
import edu.skillbox.skillcinema.models.filmAndSerial.image.ImageTable
import edu.skillbox.skillcinema.models.person.filmOfPerson.FilmOfPersonTable
import edu.skillbox.skillcinema.models.person.PersonTable
import edu.skillbox.skillcinema.models.filmAndSerial.serial.EpisodeTable
import edu.skillbox.skillcinema.models.filmAndSerial.serial.SeasonTable
import edu.skillbox.skillcinema.models.filmAndSerial.serial.SerialTable
import edu.skillbox.skillcinema.models.filmAndSerial.similar.SimilarFilmTable
import edu.skillbox.skillcinema.models.filmAndSerial.staff.StaffTable

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
        CollectionExisting::class,
        ViewedTable::class,
        InterestedTable::class
    ], version = 22
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun filmDao(): FilmDao
}