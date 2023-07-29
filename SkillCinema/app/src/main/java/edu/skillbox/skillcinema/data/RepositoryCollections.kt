package edu.skillbox.skillcinema.data

import android.util.Log
import edu.skillbox.skillcinema.models.collection.*
import javax.inject.Inject

private const val TAG = "RepositoryCollections"

class RepositoryCollections @Inject constructor(private val dao: FilmDao) {
    // Запрос на добавление фильма в просмотренные
    suspend fun addViewedFilm(viewedFilm: ViewedTable) {
        dao.addViewedFilm(viewedFilm)
    }

    // Запрос на проверку наличия фильма в просмотренных
    suspend fun isFilmExistsInViewed(filmId: Int): Boolean {
        return dao.isFilmExistsInViewed(filmId)
    }

    // Запрос на получение количества просмотренных фильмов
    suspend fun getViewedFilmsQuantity(): Int {
        return dao.getViewedFilmsQuantity()
    }

    // Запрос на получение списка id просмотренных фильмов
    suspend fun getViewedFilmsIds(): List<Int>? {
        return dao.getViewedFilmsIds()
    }

    // Запрос на удаление фильма из списка просмотренных
    suspend fun removeViewedFilm(viewedFilmId: Int) {
        dao.removeViewedFilm(viewedFilmId)
    }

    // Запрос на удаление всех просмотренных фильмов
    suspend fun removeAllViewedFilms() {
        dao.removeAllViewedFilms()
    }

    // Запрос на проверку наличия фильма в указанной коллекции
    suspend fun isFilmExistsInCollection(filmId: Int, collectionName: String): Boolean {
        return dao.isFilmExistsInCollection(filmId, collectionName)
    }

    // Запрос на добавление фильма в коллекцию
    suspend fun addCollectionTable(collectionTable: CollectionTable) {
        dao.addCollectionTable(collectionTable)
    }

    // Запрос на удаление фильма из коллекции
    suspend fun removeCollectionTable(filmId: Int, collectionName: String) {
        dao.removeCollectionTable(filmId, collectionName)
    }

    // Запрос на получение списка id фильмов коллекции
    suspend fun getCollectionFilmsIds(collectionName: String): List<Int> {
        return dao.getCollectionFilmsIds(collectionName)
    }

    // Запрос на получение списка имен коллекций, относящихся к фильмам
    suspend fun getCollectionNamesOfFilms(): List<String> {
        return dao.getCollectionNamesOfFilms()
    }

    // Получение списка коллекций
    suspend fun getCollectionInfoList(): List<CollectionInfo> {
        Log.d(TAG, "Вызвана getCollectionInfoList()")
        val collectionInfoList: MutableList<CollectionInfo> = mutableListOf()
        val collectionNamesList: List<String> = dao.getCollectionNames()
        Log.d(TAG, "getCollectionInfoList(). collectionNamesList = $collectionNamesList")
        collectionNamesList.forEach { collectionName ->
            val filmsQuantity: Int = dao.getCollectionFilmsQuantity(collectionName)
            collectionInfoList.add(
                CollectionInfo(
                    collectionName = collectionName,
                    filmsQuantity = filmsQuantity
                )
            )
        }
        Log.d(TAG, "getCollectionInfoList(). collectionInfoList = $collectionInfoList")
        return collectionInfoList.toList()
    }

    // Получение списка коллекций, с информацией, включён ли фильм в коллекцию
    suspend fun getCollectionFilmList(filmId: Int): List<CollectionFilm> {
        val collectionFilmList: MutableList<CollectionFilm> =
            emptyList<CollectionFilm>().toMutableList()
        val collectionNamesList: List<String> = dao.getCollectionNames()
        collectionNamesList.forEach { collectionName ->
            val filmsQuantity: Int = dao.getCollectionFilmsQuantity(collectionName)
            val isFilmExistsInCollection: Boolean =
                dao.isFilmExistsInCollection(filmId, collectionName)
            collectionFilmList.add(
                CollectionFilm(
                    collectionName = collectionName,
                    filmsQuantity = filmsQuantity,
                    filmIncluded = isFilmExistsInCollection
                )
            )
        }
        return collectionFilmList.toList()
    }

    // Запрос на добавление пустой коллекции без фильмов
    suspend fun addCollection(collection: CollectionExisting) {
        dao.addCollection(collection)
    }

    // Запрос на удаление коллекции и фильмов в ней
    suspend fun deleteCollection(collectionName: String) {
        dao.removeCollectionFilms(collectionName)
        dao.removeCollection(collectionName)
    }

    // Запрос на удаление всех фильмов из коллекции
    suspend fun removeCollectionFilms(collectionName: String) {
        dao.removeCollectionFilms(collectionName)
    }

    // Запрос на добавление фильма, сериала или человека в список "Вам было интересно"
    // с учётом ограничения длины истории в 400 элементов.
    suspend fun addInterested(interested: InterestedTable) {
        if (dao.isObjectExistsInInterested(id = interested.id, type = interested.type)) {
            dao.removeInterestedTable(id = interested.id, type = interested.type)
            dao.addInterested(InterestedTable(id = interested.id, type = interested.type))
        } else {
            val interestedQuantity = dao.getInterestedQuantity()
            if (interestedQuantity >= 400) dao.removeOldestInterested()
            dao.addInterested(interested)
        }
    }

    // Запрос на получение количества элементов в списке "Вам было интересно"
    suspend fun getInterestedQuantity(): Int {
        return dao.getInterestedQuantity()
    }

    // Запрос на получение списка id просмотренных фильмов
    suspend fun getInterestedList(): List<InterestedTable>? {
        return dao.getInterestedList()
    }

    // Запрос на удаление всех элементов в списке "Вам было интересно"
    suspend fun removeAllInterested() {
        dao.removeAllInterested()
    }
}