package edu.skillbox.skillcinema.data

import android.util.Log
import edu.skillbox.skillcinema.api.retrofit
import edu.skillbox.skillcinema.models.person.PersonInfo
import edu.skillbox.skillcinema.models.person.PersonInfoDb
import edu.skillbox.skillcinema.models.person.PersonTable
import edu.skillbox.skillcinema.models.person.filmOfPerson.FilmOfPersonTable
import javax.inject.Inject

private const val TAG = "RepositoryPerson"

class RepositoryPerson @Inject constructor(private val dao: FilmDao) {
    // Получение из API данных человека PersonInfo по personId, с переключением ключей API.
    private suspend fun getPersonInfo(personId: Int): Pair<PersonInfo?, Boolean> {
        for (i in 0..3) {
            Log.d(TAG, "getPersonInfo($personId). apiKey = ${ApyKeys.currentApiKey}")
            kotlin.runCatching {
                retrofit.getPersonInfo(apiKey = ApyKeys.currentApiKey, personId = personId)
            }.fold(
                onSuccess = {
                    Log.d(TAG, "getPersonInfo($personId). Успешная загрузка на итерации $i")
                    return Pair(first = it, second = false)
                },
                onFailure = {
                    Log.d(
                        TAG,
                        "getPersonInfo($personId). Ошибка загрузки из Api. ${it.message ?: ""}"
                    )
                    ApyKeys.changeApiKey()
                }
            )
        }
        return Pair(first = null, second = true)
    }

    // Получение из API данных человека PersonInfoDb по personId.
    // PersonInfoDb - класс для взаимодействия с БД.
    private suspend fun getFromApiPersonInfoDb(personId: Int): Pair<PersonInfoDb?, Boolean> {
        val apiLoadResult = getPersonInfo(personId)
        val personInfo: PersonInfo? = apiLoadResult.first
        val error: Boolean = apiLoadResult.second

        val filmsOfPerson: MutableList<FilmOfPersonTable> = mutableListOf()
        return if (personInfo != null) {
            personInfo.films.forEach { film ->
                filmsOfPerson.add(
                    FilmOfPersonTable(
                        personId = personId,
                        filmId = film.filmId,
                        name = film.nameRu ?: film.nameEn ?: "",
                        rating = film.rating?.toFloatOrNull(),
                        professionKey = film.professionKey
                    )
                )
            }
            val sortedFilmsOfPerson =
                filmsOfPerson.sortedByDescending { filmOfPersonTable -> filmOfPersonTable.rating }
            Pair(
                first = PersonInfoDb(
                    personTable = PersonTable(
                        personId = personId,
                        name = personInfo.nameRu ?: personInfo.nameEn,
                        sex = personInfo.sex,
                        posterUrl = personInfo.posterUrl,
                        profession = personInfo.profession
                    ),
                    filmsOfPerson = sortedFilmsOfPerson
                ),
                second = error
            )
        } else Pair(
            first = null,
            second = error
        )
    }

    // Получение данных человека PersonInfoDb по personId.
    // Эта функция запрашивает из БД, если нет, то берёт из API, и записывает в БД.
    suspend fun getPersonInfoDb(personId: Int): Pair<PersonInfoDb?, Boolean> {
        var personInfoDb: PersonInfoDb? = dao.getPersonInfoDb(personId)
        val apiLoadPersonInfoDbResult: Pair<PersonInfoDb?, Boolean>
        var error = false

        if (personInfoDb == null) {
            apiLoadPersonInfoDbResult = getFromApiPersonInfoDb(personId)
            personInfoDb = apiLoadPersonInfoDbResult.first
            error = apiLoadPersonInfoDbResult.second
            if (personInfoDb != null) {
                Log.d(TAG, "Данные человека $personId загружены из API")
                addPersonInfoDb(personInfoDb)
                Log.d(TAG, "Данные человека $personId записаны в БД")
            }
        } else Log.d(TAG, "Данные человека $personId загружены из БД")

        return Pair(first = personInfoDb, second = error)
    }

    // Запрос на добавление в БД данных персонала
    private suspend fun addPersonInfoDb(personInfoDb: PersonInfoDb) {
        dao.addPersonTable(personInfoDb.personTable)
        personInfoDb.filmsOfPerson.forEach { filmOfPerson ->
            dao.addFilmOfPersonTable(filmOfPerson)
        }
    }

    // Запрос на проверку наличия записи данных персонала в БД
    suspend fun getPersonTable(searchedPersonId: Int): PersonTable? {
        return dao.getPersonTable(searchedPersonId)
    }
}