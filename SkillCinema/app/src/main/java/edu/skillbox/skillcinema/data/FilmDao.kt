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

    // Запрос на получение данных фильма FilmDb по filmId
    @Query("SELECT * FROM person_table WHERE person_id LIKE :searchedPersonId")
    suspend fun getPersonTable(searchedPersonId: Int): PersonTable?

    // Запрос на получение данных фильма FilmDb по filmId
    @Query("SELECT * FROM film_table WHERE film_id LIKE :filmId")
    suspend fun getFilmDb(filmId: Int): FilmDb?

    // Запрос на получение данных фильма FilmInfoDb по filmId
    @Transaction
    @Query("SELECT * FROM film_table WHERE film_id LIKE :filmId")
    suspend fun getFilmInfoDb(filmId: Int): FilmInfoDb?

    // Запрос на получение данных сериала по filmId
    @Transaction
    @Query("SELECT * FROM serial_table WHERE film_id LIKE :filmId")
    suspend fun getSerialInfoDb(filmId: Int): SerialInfoDb?

    // Запрос на получение данных персонала фильма по filmId
    @Query("SELECT * FROM staff_table WHERE film_id LIKE :filmId")
    suspend fun getStaffTableList(filmId: Int): List<StaffTable>?

    // Запрос на получение галереи фильма по filmId
    @Query("SELECT * FROM image_table WHERE film_id LIKE :filmId")
    suspend fun getImageTableList(filmId: Int): List<ImageTable>?

    // Запрос на получение изображений фильма определённого типа по filmId
    @Query("SELECT image FROM image_table WHERE film_id LIKE :filmId AND type LIKE :type")
    suspend fun getImagesOfType(filmId: Int, type: String): List<String>?

    // Запрос на получение списка похожих фильмов по filmId
    @Query("SELECT * FROM similar_film_table WHERE film_id LIKE :filmId")
    suspend fun getSimilarFilmTableList(filmId: Int): List<SimilarFilmTable>?

    // Запрос на получение данных персонала по personId
    @Transaction
    @Query("SELECT * FROM person_table WHERE person_id LIKE :personId")
    suspend fun getPersonInfoDb(personId: Int): PersonInfoDb?

    // Запрос на проверку наличия фильма в указанной коллекции
    @Query("SELECT EXISTS (SELECT * FROM collection_table WHERE film_id = :filmId AND collection = :collectionName)")
    suspend fun isFilmExistsInCollection(filmId: Int, collectionName: String): Boolean

    // Запрос на добавление фильма в коллекцию
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addCollectionTable(collectionTable: CollectionTable)

    // Запрос на удаление фильма из коллекции
    @Query("DELETE FROM collection_table WHERE film_id = :filmId AND collection = :collectionName")
    suspend fun removeCollectionTable(filmId: Int, collectionName: String)

    // Запрос на получение количества фильмов в коллекции
    @Query("SELECT COUNT(*) FROM collection_table WHERE collection = :collectionName")
    suspend fun getCollectionFilmsQuantity(collectionName: String): Int

    // Запрос на получение списка id фильмов коллекции
    @Query("SELECT film_id FROM collection_table WHERE collection = :collectionName")
    suspend fun getCollectionFilmsIds(collectionName: String): List<Int>

    // Запрос на получение списка имен сохранённых коллекций
    @Query("SELECT collection_name FROM collection_existing")
    suspend fun getCollectionNames(): List<String>

    // Запрос на получение списка имен коллекций, относящихся к фильмам
    @Query("SELECT DISTINCT collection FROM collection_table")
    suspend fun getCollectionNamesOfFilms(): List<String>

    // Запрос на добавление новой коллекции
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addCollection(collection: CollectionExisting)

    // Запрос на удаление коллекции
    @Query("DELETE FROM collection_existing WHERE collection_name = :collectionName")
    suspend fun removeCollection(collectionName: String)

    // Запрос на удаление всех фильмов коллекции
    @Query("DELETE FROM collection_table WHERE collection = :collectionName")
    suspend fun removeCollectionFilms(collectionName: String)

    // Запрос на добавление фильма в просмотренные
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addViewedFilm(viewedFilm: ViewedTable)

    // Запрос на проверку наличия фильма в просмотренных
    @Query("SELECT EXISTS (SELECT * FROM viewed_table WHERE film_id = :filmId)")
    suspend fun isFilmExistsInViewed(filmId: Int): Boolean

    // Запрос на получение количества просмотренных фильмов
    @Query("SELECT COUNT(*) FROM viewed_table")
    suspend fun getViewedFilmsQuantity(): Int

    // Запрос на получение списка id просмотренных фильмов
    @Query("SELECT film_id FROM viewed_table")
    suspend fun getViewedFilmsIds(): List<Int>?

    // Запрос на удаление фильма из списка просмотренных
    @Query("DELETE FROM viewed_table WHERE film_id = :viewedFilmId")
    suspend fun removeViewedFilm(viewedFilmId: Int)

    // Запрос на удаление всех просмотренных фильмов
    @Query("DELETE FROM viewed_table")
    suspend fun removeAllViewedFilms()

    // Запрос на проверку наличия фильма в просмотренных
    @Query("SELECT EXISTS (SELECT * FROM interested_table WHERE id = :id AND type = :type)")
    suspend fun isObjectExistsInInterested(id: Int, type: Int): Boolean

    // Запрос на добавление фильма, сериала или человека в список "Вам было интересно"
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addInterested(interested: InterestedTable)

    // Запрос на удаление записи из таблицы "Вам было интересно"
    @Query("DELETE FROM interested_table WHERE id = :id AND type = :type")
    suspend fun removeInterestedTable(id: Int, type: Int)

    // Запрос на удаление самой старой записи из таблицы "Вам было интересно"
    @Query("DELETE FROM interested_table WHERE position = (SELECT MIN(position) FROM interested_table)")
    suspend fun removeOldestInterested()

    // Запрос на получение количества элементов в списке "Вам было интересно"
    @Query("SELECT COUNT(*) FROM interested_table")
    suspend fun getInterestedQuantity(): Int

    // Запрос на получение списка "Вам было интересно"
    @Query("SELECT * FROM interested_table")
    suspend fun getInterestedList(): List<InterestedTable>?

    // Запрос на удаление всех элементов в списке "Вам было интересно"
    @Query("DELETE FROM interested_table")
    suspend fun removeAllInterested()

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