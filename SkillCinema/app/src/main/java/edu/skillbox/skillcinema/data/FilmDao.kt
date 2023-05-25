package edu.skillbox.skillcinema.data

import androidx.room.*
import edu.skillbox.skillcinema.models.*

@Dao
interface FilmDao {

    // Запрос на добавление новой записи любимого фильма
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFilmTable(filmTable: FilmTable)

    // Запрос на добавление новой записи страны фильма
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addCountryTable(countryTable: CountryTable)

    // Запрос на добавление новой записи жанра фильма
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addGenreTable(genreTable: GenreTable)

    // Запрос на проверку наличия фильма в указанной коллекции
    @Query("SELECT EXISTS (SELECT * FROM collection_table WHERE film_id LIKE :filmId AND collection LIKE :collectionName)")
    suspend fun isFilmExistsInCollection(filmId: Int, collectionName: String): Boolean

    // Запрос на добавление новой записи коллекции фильма
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addCollectionTable(collectionTable: CollectionTable)

    // Запрос на удаление фильма из коллекции
    @Query("DELETE FROM collection_table WHERE film_id LIKE :filmId AND collection LIKE :collectionName")
    suspend fun removeCollectionTable(filmId: Int, collectionName: String)

    // Запрос на добавление новой записи персонала фильма
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addStaffTable(staffTable: StaffTable)

    // Запрос на добавление новой записи галереи фильма
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addImageTable(imageTable: ImageTable)

    // Запрос на добавление новой записи похожего фильма
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSimilarFilmTable(similarFilmTable: SimilarFilmTable)

    // Запрос на добавление новой записи сериала
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSerialTable(serialTable: SerialTable)

    // Запрос на добавление новой записи сезона
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSeasonTable(seasonTable: SeasonTable)

    // Запрос на добавление новой записи сезона
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addEpisodeTable(episodeTable: EpisodeTable)

    // Запрос на добавление новой записи персонала
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPersonTable(personTable: PersonTable)

    // Запрос на добавление новой фильма, относящегося к персоналу
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFilmOfPersonTable(filmOfPersonTable: FilmOfPersonTable)

    // Запрос на проверку наличия записи данных фильма в БД
    @Query("SELECT EXISTS (SELECT film_id FROM film_table WHERE film_id LIKE :searchedFilmId)")
    suspend fun isFilmDataExists(searchedFilmId: Int): Boolean

    // Запрос на проверку наличия записи данных сериала в БД
    @Query("SELECT EXISTS (SELECT film_id FROM serial_table WHERE film_id LIKE :searchedFilmId)")
    suspend fun isSerialDataExists(searchedFilmId: Int): Boolean

    // Запрос на проверку наличия записи данных персонала в БД
    @Query("SELECT EXISTS (SELECT person_id FROM person_table WHERE person_id LIKE :searchedPersonId)")
    suspend fun isPersonDataExists(searchedPersonId: Int): Boolean

    // Запрос на получение данных фильма по filmId
    @Transaction
    @Query("SELECT * FROM film_table WHERE film_id LIKE :filmId")
    suspend fun getFilmInfoDb(filmId: Int): FilmInfoDb?

    // Запрос на получение данных сериала по filmId
    @Transaction
    @Query("SELECT * FROM serial_table WHERE film_id LIKE :filmId")
    suspend fun getSerialInfoDb(filmId: Int): SerialInfoDb

    @Query("SELECT * FROM staff_table WHERE film_id LIKE :filmId")
    suspend fun getAllStaffFromDb(filmId: Int): List<StaffTable>?

    // Запрос на получение данных персонала по personId
    @Transaction
    @Query("SELECT * FROM person_table WHERE person_id LIKE :personId")
    suspend fun getPersonInfoDb(personId: Int): PersonInfoDb

    // Запросы для проверки

//    // Запрос на получение данных фильма
//    @Transaction
//    @Query("SELECT * FROM film_table")
//    suspend fun getAllFilmInfoDb(): List<FilmInfoDb>

//    // Запрос для отображение всех строк таблицы
//    @Query("SELECT * FROM film_table")
//    suspend fun getAllFilmTable(): List<FilmTable>

    //     Запрос на поиск filmId в таблице
//    @Query("SELECT filmId FROM favorite WHERE filmId LIKE :searchedFilmId")
//    suspend fun getFilmId(searchedFilmId: Int): Int?

    // Запрос на изменение кол-ва слова
//    @Query("UPDATE favorite SET quantity = :newQuantity WHERE word = :existingWord")
//    suspend fun increaseQuantity(existingWord: String, newQuantity: Int)

    // Запрос на очистку таблицы
//    @Query("DELETE FROM film_table")
//    suspend fun clearTable()

//    @Query("INSERT OR REPLACE INTO country_table (film_id, country) VALUES (:filmId, :country)")
//    suspend fun addCountryTable(filmId: Int, country: String)
}