package edu.skillbox.skillcinema.data

import android.util.Log
import edu.skillbox.skillcinema.api.retrofit
import edu.skillbox.skillcinema.models.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.math.ceil

private const val TAG = "Repository"

class Repository @Inject constructor(private val dao: FilmDao) {
    // Набор ключей API. Каждый на 500 запросов.
    private val apiKeysList = listOf(
        "ce6f81de-e746-4a8b-8a79-4a7fe451b75d",
        "a8429c6b-2971-443a-9a72-59932af2324f",
        "ca8d9204-5d12-4fcd-bdfc-194f72a55394",
        "3a8ba0d1-8a76-4d8b-90e6-b3d00ed195c5"
    )
    private var currentApiKeyNumber = 0 // Номер текущего ключа.
    var currentApiKey = apiKeysList[0] // Активный ключ.

    // Переключение ключей API.
    private fun changeApiKey() {
        currentApiKeyNumber = if (currentApiKeyNumber == 3) 0 else currentApiKeyNumber + 1
        currentApiKey = apiKeysList[currentApiKeyNumber]
        Log.d(TAG, "changeApiKey(): currentApiKey = $currentApiKey")
    }

    // Получение из API данных фильма FilmInfo по filmId, с переключением ключей API.
    private suspend fun getFilmInfo(filmId: Int): Pair<FilmInfo?, Boolean> {
        for (i in 0..3) {
            Log.d(TAG, "getFilmInfo($filmId). apiKey = $currentApiKey")
            kotlin.runCatching {
                retrofit.getFilmInfo(apiKey = currentApiKey, filmId = filmId)
            }.fold(
                onSuccess = {
                    Log.d(TAG, "getFilmInfo($filmId). Успешная загрузка на итерации $i")
                    return Pair(first = it, second = false)
                },
                onFailure = {
                    Log.d(TAG, "getFilmInfo($filmId). Ошибка загрузки из Api. ${it.message ?: ""}")
                    changeApiKey()
                }
            )
        }
        return Pair(first = null, second = true)
    }

    // Получение из API данных фильма FilmDb по filmId.
    // FilmDb - класс для взаимодействия с БД.
    private suspend fun getFromApiFilmDb(filmId: Int): Pair<FilmDb?, Boolean> {
        val apiLoadResult = getFilmInfo(filmId)
        val filmInfo: FilmInfo? = apiLoadResult.first
        val error: Boolean = apiLoadResult.second

        val countries: MutableList<CountryTable> =
            emptyList<CountryTable>().toMutableList()
        val genres: MutableList<GenreTable> =
            emptyList<GenreTable>().toMutableList()

        filmInfo?.countries?.forEach { countryClass ->
            countries.add(
                CountryTable(
                    filmId = filmId,
                    country = countryClass.country
                )
            )
        }
        filmInfo?.genres?.forEach { genreClass ->
            genres.add(
                GenreTable(
                    filmId = filmId,
                    genre = genreClass.genre
                )
            )
        }

        return if (filmInfo != null) Pair(
            first = FilmDb(
                filmTable = FilmTable(
                    filmId = filmId,
                    name = filmInfo.nameRu ?: filmInfo.nameEn ?: filmInfo.nameOriginal ?: "",
                    poster = filmInfo.posterUrl,
                    posterSmall = filmInfo.posterUrlPreview,
                    rating = filmInfo.ratingKinopoisk,
                    year = filmInfo.year,
                    length = filmInfo.filmLength,
                    description = filmInfo.description,
                    shortDescription = filmInfo.shortDescription,
                    ratingAgeLimits = filmInfo.ratingAgeLimits
                ),
                countries = countries.toList(),
                genres = genres.toList()
            ),
            second = error
        ) else Pair(
            first = null,
            second = error
        )
    }

    // Получение данных фильма FilmDbViewed по filmId.
    // Эта функция запрашивает из БД, если нет, то берёт из API, и записывает в БД.
    suspend fun getFilmDbViewed(filmId: Int): Pair<FilmDbViewed?, Boolean> {
        var filmDb: FilmDb? = dao.getFilmDb(filmId)
        val apiLoadFilmDbResult: Pair<FilmDb?, Boolean>
        val viewed: Boolean = dao.isFilmExistsInViewed(filmId)
        var error = false

        if (filmDb == null) {
            apiLoadFilmDbResult = getFromApiFilmDb(filmId)
            filmDb = apiLoadFilmDbResult.first
            error = apiLoadFilmDbResult.second
            if (filmDb != null) {
                Log.d(TAG, "Фильм $filmId загружен из API")
                addFilmDb(filmDb)
                Log.d(TAG, "Фильм $filmId записан в БД")
            }
        } else Log.d(TAG, "Фильм $filmId загружен из БД")

        return if (filmDb != null) Pair(
            first = FilmDbViewed(filmDb = filmDb, viewed = viewed),
            second = error
        ) else Pair(
            first = null,
            second = error
        )
    }

    // Получение из API данных человека PersonInfo по personId, с переключением ключей API.
    private suspend fun getPersonInfo(personId: Int): Pair<PersonInfo?, Boolean> {
        for (i in 0..3) {
            Log.d(TAG, "getPersonInfo($personId). apiKey = $currentApiKey")
            kotlin.runCatching {
                retrofit.getPersonInfo(apiKey = currentApiKey, personId = personId)
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
                    changeApiKey()
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

    private suspend fun getPremieres(year: Int, month: String): Pair<FilmList?, Boolean> {
        for (i in 0..3) {
            Log.d(TAG, "getPremieres($year, $month). apiKey = $currentApiKey")
            kotlin.runCatching {
                retrofit.getPremieres(apiKey = currentApiKey, year = year, month = month)
            }.fold(
                onSuccess = {
                    Log.d(TAG, "getPremieres($year, $month). Успешная загрузка на итерации $i")
                    return Pair(first = it, second = false)
                },
                onFailure = {
                    Log.d(
                        TAG,
                        "getPremieres($year, $month). Ошибка загрузки из Api. ${it.message ?: ""}"
                    )
                    changeApiKey()
                }
            )
        }
        return Pair(first = null, second = true)
    }

    suspend fun getPremieresList(): Pair<List<FilmPremiere>, Boolean> {

        val calendar: Calendar = Calendar.getInstance()
        val currentDate: Date = calendar.time // Текущая дата
        val currentYear = calendar.get(Calendar.YEAR) // Текущий год
        val currentMonth = calendar.get(Calendar.MONTH) // Текущий месяц

        calendar.add(Calendar.DAY_OF_MONTH, 15) // Прибавляю 15, а не 14, из-за того, что
        // в дате фильма указано время 00:00:00, а текущее время больше.
        val datePlus15Days: Date = calendar.time // Дата через 2 недели
        val datePlus15DaysYear = calendar.get(Calendar.YEAR) // Получили месяц новый
        val datePlus15DaysMonth = calendar.get(Calendar.MONTH) // Получили месяц новый

        val premieresThisMonthLoadResult: Pair<FilmList?, Boolean> =
            getPremieres(currentYear, monthToString(currentMonth))
        val premieresThisMonth = premieresThisMonthLoadResult.first?.items ?: emptyList()

        var premieresNextMonthLoadResult: Pair<FilmList?, Boolean>? = null
        var premieresNextMonth: List<FilmPremiere> = emptyList()
        if (datePlus15DaysMonth != currentMonth) {
            premieresNextMonthLoadResult =
                getPremieres(
                    datePlus15DaysYear,
                    monthToString(datePlus15DaysMonth)
                )
            premieresNextMonth = premieresNextMonthLoadResult.first?.items ?: emptyList()
        }

        // Определяем, была ли ошибка загрузки из API. Нулевой список сам по себе ошибкой
        // не считаю, т.к. может премьер и не быть за какой-то период.
        val error =
            (premieresThisMonthLoadResult.second) || (premieresNextMonthLoadResult?.second ?: false)

        val premieres2Months = premieresThisMonth + premieresNextMonth

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US) // Формат даты из Api

        val premieresNext2Weeks: MutableList<FilmPremiere> =
            emptyList<FilmPremiere>().toMutableList()
        premieres2Months.forEach {
            calendar.time = dateFormat.parse(it.premiereRu) as Date
            // Прибавляем день, чтобы получить премьеры и за текущий день,
            // т.к. текущее время больше 00:00:00 в дате фильма.
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            val premiereDate: Date = calendar.time
            if (premiereDate in currentDate..datePlus15Days) premieresNext2Weeks.add(it)
        }

        return Pair(first = premieresNext2Weeks.toList().shuffled(), second = error)
    }

    private fun monthToString(month: Int): String {
        return when (month) {
            0 -> "JANUARY"
            1 -> "FEBRUARY"
            2 -> "MARCH"
            3 -> "APRIL"
            4 -> "MAY"
            5 -> "JUNE"
            6 -> "JULY"
            7 -> "AUGUST"
            8 -> "SEPTEMBER"
            9 -> "OCTOBER"
            10 -> "NOVEMBER"
            else -> "DECEMBER"
        }
    }

    suspend fun extendPremiereData(filmPremiere: FilmPremiere): FilmItemData? {
        val filmId = filmPremiere.kinopoiskId
        val name = filmPremiere.nameRu ?: filmPremiere.nameEn
        val genres = filmPremiere.genres.joinToString(", ") { it.genre }
        val viewed: Boolean = dao.isFilmExistsInViewed(filmId)
        return if (name != null)
            FilmItemData(
                filmId = filmId,
                name = name,
                year = filmPremiere.year.toString(),
                genres = genres,
                poster = filmPremiere.posterUrlPreview,
                rating = null,
                viewed = viewed
            )
        else null
    }

    // Получение из API списка Top, с переключением ключей API
    suspend fun getTopList(type: String, page: Int): Pair<PagedFilmTopList?, Boolean> {
        for (i in 0..3) {
            Log.d(TAG, "getTopList($type). apiKey = $currentApiKey")
            kotlin.runCatching {
                retrofit.getTopList(apiKey = currentApiKey, type = type, page = page)
            }.fold(
                onSuccess = {
                    Log.d(TAG, "getTopList($type). Успешная загрузка на итерации $i")
                    return Pair(first = it, second = false)
                },
                onFailure = {
                    Log.d(TAG, "getTopList($type). Ошибка загрузки из Api. ${it.message ?: ""}")
                    changeApiKey()
                }
            )
        }
        return Pair(first = null, second = true)
    }

    // Используется только в PagingSource
    suspend fun getTopListExtended(type: String, page: Int): Pair<List<FilmItemData>?, Boolean> {
        Log.d(TAG, "Вызвана getTopListExtended($type)")
        val loadResult: Pair<PagedFilmTopList?, Boolean> = getTopList(type = type, page = page)
        val pagedFilmTopList: PagedFilmTopList? = loadResult.first
        val error: Boolean = loadResult.second
        return if (pagedFilmTopList == null) {
            Pair(first = null, second = error)
        } else {
            Pair(first = extendTop(pagedFilmTopList), second = error)
        }
    }

    // Используется в getTopListExtended()
    private suspend fun extendTop(topList: PagedFilmTopList): List<FilmItemData> {
        val topExtended: MutableList<FilmItemData> = mutableListOf()
        topList.films.forEach { filmTop ->
            val filmId = filmTop.filmId
            val name = filmTop.nameRu ?: filmTop.nameEn
            val genres = filmTop.genres.joinToString(", ") { it.genre }
            val rating: String? =
                if (filmTop.rating == null || filmTop.rating.contains("%", true)) null
                else filmTop.rating
            val viewed: Boolean = dao.isFilmExistsInViewed(filmId)
            if (name != null) {
                topExtended.add(
                    FilmItemData(
                        filmId = filmId,
                        name = name,
                        year = filmTop.year,
                        genres = genres,
                        poster = filmTop.posterUrlPreview,
                        rating = rating,
                        viewed = viewed
                    )
                )
            }
        }
        return topExtended.toList()
    }

    // Используется в MainViewModel() по отношению к загруженным ранее из API спискам Top100Popular
    // и Top250 для преобразования данных каждого фильма отдельно в тип FilmItemData (с обращением
    // в БД для получения Viewed) с постепенной подгрузкой увеличивающегося списка в Recycler.
    // Это происходит при каждом возврате на окно, без повторной подгрузки базового списка из API.
    suspend fun extendTopFilmData(filmTop: FilmTop): FilmItemData? {
        val filmId = filmTop.filmId
        val name = filmTop.nameRu ?: filmTop.nameEn
        val genres = filmTop.genres.joinToString(", ") { it.genre }
        val rating: String? =
            if (filmTop.rating == null || filmTop.rating.contains("%", true)) null
            else filmTop.rating
        val viewed: Boolean = dao.isFilmExistsInViewed(filmId)
        return if (name != null)
            FilmItemData(
                filmId = filmId,
                name = name,
                year = filmTop.year,
                genres = genres,
                poster = filmTop.posterUrlPreview,
                rating = rating,
                viewed = viewed
            )
        else null
    }

    // Получение из API списка сериалов, с переключением ключей API.
    suspend fun getSerials(page: Int): Pair<PagedFilmFilteredList?, Boolean> {
        for (i in 0..3) {
            Log.d(TAG, "getSerials($page). apiKey = $currentApiKey")
            kotlin.runCatching {
                retrofit.getSerials(apiKey = currentApiKey, page = page)
            }.fold(
                onSuccess = {
                    Log.d(TAG, "getSerials($page). Успешная загрузка на итерации $i")
                    return Pair(first = it, second = false)
                },
                onFailure = {
                    Log.d(TAG, "getSerials($page). Ошибка загрузки из Api. ${it.message ?: ""}")
                    changeApiKey()
                }
            )
        }
        return Pair(first = null, second = true)
    }

    // Используется только в PagingSource
    suspend fun getSerialsExtended(page: Int): Pair<List<FilmItemData>?, Boolean> {
        Log.d(TAG, "Вызвана getSerialsExtended($page)")
        val loadResult: Pair<PagedFilmFilteredList?, Boolean> = getSerials(page = page)
        val pagedFilmFilteredList: PagedFilmFilteredList? = loadResult.first
        val error: Boolean = loadResult.second
        return if (pagedFilmFilteredList == null) {
            Pair(first = null, second = error)
        } else {
            Pair(first = extendFilmsFiltered(pagedFilmFilteredList), second = error)
        }
    }

    // Получение из API списка фильмов по жанрам и странам, с переключением ключей API.
    private suspend fun getFilmsFiltered(
        genre: Int,
        country: Int,
        page: Int
    ): Pair<PagedFilmFilteredList?, Boolean> {
        Log.d(TAG, "Вызвана getFilmsFiltered($genre, $country, $page)")
        for (i in 0..3) {
            Log.d(TAG, "getFilmsFiltered(). apiKey = $currentApiKey")
            kotlin.runCatching {
                retrofit.getFilmFiltered(
                    apiKey = currentApiKey,
                    genres = genre,
                    countries = country,
                    page = page
                )
            }.fold(
                onSuccess = {
                    Log.d(
                        TAG,
                        "getFilmsFiltered($genre, $country, $page). Успешная загрузка на итерации $i"
                    )
                    return Pair(first = it, second = false)
                },
                onFailure = {
                    Log.d(
                        TAG,
                        "getFilmsFiltered($genre, $country, $page). Ошибка загрузки из Api. ${it.message ?: ""}"
                    )
                    changeApiKey()
                }
            )
        }
        return Pair(first = null, second = true)
    }

    // Для получения фильмов с рейтингом выше 6, производится отсев фильмов рейтингом null, затем
    // пересчитывается количество оставшихся фильмов и количество страниц.
    suspend fun getFilmsFilteredClearedFromNullRating(
        genre: Int,
        country: Int,
        page: Int
    ): Pair<PagedFilmFilteredList?, Boolean> {
        Log.d(TAG, "Вызвана getFilmsFilteredClearedFromNullRating($genre, $country, $page)")
        val apiResult: Pair<PagedFilmFilteredList?, Boolean> = getFilmsFiltered(
            genre = genre,
            country = country,
            page = page
        )
        val pagedFilmFilteredList: PagedFilmFilteredList? = apiResult.first
        val error = apiResult.second

        if (error) return Pair(first = null, second = true)
        if (pagedFilmFilteredList == null) return Pair(first = null, second = false)
        var newTotal: Int = pagedFilmFilteredList.total
        var newTotalPages = pagedFilmFilteredList.totalPages
        val filmsFilteredRatingAbove6: MutableList<FilmFiltered> = mutableListOf()
        pagedFilmFilteredList.items.forEach {
            if (it.ratingKinopoisk != null) filmsFilteredRatingAbove6.add(it)
            else {
                newTotal -= 1
                newTotalPages = ceil(newTotal / 20.0).toInt()
            }
        }
        return Pair(
            first = PagedFilmFilteredList(
                total = newTotal,
                totalPages = newTotalPages,
                items = filmsFilteredRatingAbove6
            ),
            second = false
        )
    }

    // Используется только в PagingSource
    suspend fun getFilmsFilteredExtended(
        genre: Int,
        country: Int,
        page: Int
    ): Pair<List<FilmItemData>?, Boolean> {
        Log.d(TAG, "Вызвана getFilmsFilteredExtended(genre=$genre, country=$country, $page)")
        val loadResult: Pair<PagedFilmFilteredList?, Boolean> =
            getFilmsFilteredClearedFromNullRating(genre = genre, country = country, page = page)
        val pagedFilmFilteredList: PagedFilmFilteredList? = loadResult.first
        val error: Boolean = loadResult.second
        return if (pagedFilmFilteredList == null) {
            Pair(first = null, second = error)
        } else {
            Pair(first = extendFilmsFiltered(pagedFilmFilteredList), second = error)
        }
    }

    // Используется в MainViewModel() по отношению к загруженным ранее из API спискам FilmFiltered
    // для преобразования данных каждого фильма отдельно в тип FilmItemData (с обращением
    // в БД для получения Viewed) с постепенной подгрузкой увеличивающегося списка в Recycler.
    // Это происходит при каждом возврате на окно, без повторной подгрузки базового списка из API.
    private suspend fun extendFilmsFiltered(filmsList: PagedFilmFilteredList): List<FilmItemData> {
        val filmsDataExtended: MutableList<FilmItemData> = mutableListOf()
        filmsList.items.forEach { filmFiltered ->
            val filmId = filmFiltered.kinopoiskId
            val name = filmFiltered.nameRu ?: filmFiltered.nameEn ?: filmFiltered.nameOriginal
            val year = if (filmFiltered.year == null) null
            else filmFiltered.year.toString()
            val genres = filmFiltered.genres.joinToString(", ") { it.genre }
            val rating: String? = if (filmFiltered.ratingKinopoisk == null) null
            else filmFiltered.ratingKinopoisk.toString()
            val viewed: Boolean = dao.isFilmExistsInViewed(filmId)
            if (name != null) {
                filmsDataExtended.add(
                    FilmItemData(
                        filmId = filmId,
                        name = name,
                        year = year,
                        genres = genres,
                        poster = filmFiltered.posterUrlPreview,
                        rating = rating,
                        viewed = viewed
                    )
                )
            }
        }
        return filmsDataExtended.toList()
    }

    suspend fun extendFilteredFilmData(filmFiltered: FilmFiltered): FilmItemData? {
        val filmId = filmFiltered.kinopoiskId
        val name = filmFiltered.nameRu ?: filmFiltered.nameEn ?: filmFiltered.nameOriginal
        val year = if (filmFiltered.year == null) null
        else filmFiltered.year.toString()
        val genres = filmFiltered.genres.joinToString(", ") { it.genre }
        val rating: String? = if (filmFiltered.ratingKinopoisk == null) null
        else filmFiltered.ratingKinopoisk.toString()
        val viewed: Boolean = dao.isFilmExistsInViewed(filmId)
        return if (name != null)
            FilmItemData(
                filmId = filmId,
                name = name,
                year = year,
                genres = genres,
                poster = filmFiltered.posterUrlPreview,
                rating = rating,
                viewed = viewed
            )
        else null
    }

    // Получение из API результатов поиска, с переключением ключей API.
    suspend fun searchFilms(
        country: Int?,
        genre: Int?,
        order: String,
        type: String,
        ratingFrom: Int,
        ratingTo: Int,
        yearFrom: Int?,
        yearTo: Int?,
        keyword: String?,
        page: Int
    ): Pair<PagedFilmFilteredList?, Boolean> {
        for (i in 0..3) {
            Log.d(TAG, "searchFilms($keyword). apiKey = $currentApiKey")
            kotlin.runCatching {
                retrofit.searchFilms(
                    apiKey = currentApiKey,
                    countries = country,
                    genres = genre,
                    order = order,
                    type = type,
                    ratingFrom = ratingFrom,
                    ratingTo = ratingTo,
                    yearFrom = yearFrom,
                    yearTo = yearTo,
                    keyword = keyword,
                    page = page
                )
            }.fold(
                onSuccess = {
                    Log.d(TAG, "searchFilms($keyword). Успешная загрузка на итерации $i")
                    return Pair(first = it, second = false)
                },
                onFailure = {
                    Log.d(TAG, "searchFilms($keyword). Ошибка загрузки из Api. ${it.message ?: ""}")
                    changeApiKey()
                }
            )
        }
        Log.d(TAG, "Все 4 ключа API не дали результата. Возвращаем null")
        return Pair(first = null, second = true)
    }

    // На основе результатов searchFilms() формируем список фильмов List<FilmItemData>,
    // в котором к каждому фильму прибавляется значение Viewed (из БД), и данные фильма
    // преобразуются для отображения в стандартном ViewHolder.
    suspend fun searchFilmsExtended(
        country: Int?,
        genre: Int?,
        order: String,
        type: String,
        ratingFrom: Int,
        ratingTo: Int,
        yearFrom: Int?,
        yearTo: Int?,
        keyword: String?,
        page: Int
    ): Pair<List<FilmItemData>?, Boolean> {
        Log.d(TAG, "Вызвана searchFilmsExtended($keyword)")
        val loadResult: Pair<PagedFilmFilteredList?, Boolean> = searchFilms(
            country = country,
            genre = genre,
            order = order,
            type = type,
            ratingFrom = ratingFrom,
            ratingTo = ratingTo,
            yearFrom = yearFrom,
            yearTo = yearTo,
            keyword = keyword,
            page = page
        )
        val pagedFilmFilteredList: PagedFilmFilteredList? = loadResult.first
        val error: Boolean = loadResult.second
        return if (pagedFilmFilteredList == null) {
            Pair(first = null, second = error)
        } else {
            Pair(first = extendFilmsFiltered(pagedFilmFilteredList), second = error)
        }
    }

    // Получение из API данных фильма FilmInfo по filmId, с переключением ключей API.
    private suspend fun getStaff(filmId: Int): Pair<List<StaffInfo>?, Boolean> {
        for (i in 0..3) {
            Log.d(TAG, "getStaff($filmId). apiKey = $currentApiKey")
            kotlin.runCatching {
                retrofit.getStaff(apiKey = currentApiKey, filmId = filmId)
            }.fold(
                onSuccess = {
                    Log.d(TAG, "getStaff($filmId). Успешная загрузка на итерации $i")
                    return Pair(first = it, second = false)
                },
                onFailure = {
                    Log.d(TAG, "getStaff($filmId). Ошибка загрузки из Api. ${it.message ?: ""}")
                    changeApiKey()
                }
            )
        }
        return Pair(first = null, second = true)
    }

    // Получение из API данных персонала List<StaffTable> по filmId.
    // StaffTable - класс для взаимодействия с БД.
    private suspend fun getFromApiStaffTableList(filmId: Int): Pair<List<StaffTable>?, Boolean> {
        val apiLoadResult = getStaff(filmId)
        val staffInfoList: List<StaffInfo>? = apiLoadResult.first
        val error: Boolean = apiLoadResult.second

        if (error) return Pair(first = null, second = true)
        if (staffInfoList == null) return Pair(first = null, second = false)

        val staffTableList: MutableList<StaffTable> = mutableListOf()
        staffInfoList.forEach { staffInfo ->
            staffTableList.add(
                StaffTable(
                    filmId = filmId,
                    staffId = staffInfo.staffId,
                    name = staffInfo.nameRu ?: staffInfo.nameEn ?: "",
                    description = staffInfo.description,
                    posterUrl = staffInfo.posterUrl,
                    professionText = staffInfo.professionText,
                    professionKey = staffInfo.professionKey
                )
            )
        }
        return Pair(first = staffTableList.toList(), second = false)
    }

    // Получение данных данных персонала List<StaffTable> по filmId.
    // Эта функция запрашивает из БД, если нет, то берёт из API, и записывает в БД.
    suspend fun getStaffTableList(filmId: Int): Pair<List<StaffTable>?, Boolean> {
        var staffTableList: List<StaffTable>? = dao.getStaffTableList(filmId)
        val apiLoadStaffTableListResult: Pair<List<StaffTable>?, Boolean>
        val error: Boolean

        if (staffTableList.isNullOrEmpty()) {
            apiLoadStaffTableListResult = getFromApiStaffTableList(filmId)
            staffTableList = apiLoadStaffTableListResult.first
            error = apiLoadStaffTableListResult.second
            if (!staffTableList.isNullOrEmpty()) {
                Log.d(TAG, "Данные персонала фильма $filmId загружены из API")
                addStaffTableList(staffTableList)
                Log.d(TAG, "Данные персонала фильма $filmId записаны в БД")
            }
        } else {
            Log.d(TAG, "Данные персонала фильма $filmId загружены из БД")
            Log.d(TAG, staffTableList.toString())
            error = false
        }

        return Pair(first = staffTableList, second = error)
    }

    // Получение из API данных фильма SerialInfo по filmId, с переключением ключей API.
    private suspend fun getSerialInfo(filmId: Int): Pair<SerialInfo?, Boolean> {
        for (i in 0..3) {
            Log.d(TAG, "getSerialInfo($filmId). apiKey = $currentApiKey")
            kotlin.runCatching {
                retrofit.getSerialInfo(apiKey = currentApiKey, filmId = filmId)
            }.fold(
                onSuccess = {
                    Log.d(TAG, "getSerialInfo($filmId). Успешная загрузка на итерации $i")
                    return Pair(first = it, second = false)
                },
                onFailure = {
                    Log.d(
                        TAG,
                        "getSerialInfo($filmId). Ошибка загрузки из Api. ${it.message ?: ""}"
                    )
                    changeApiKey()
                }
            )
        }
        return Pair(first = null, second = true)
    }

    // Получение из API данных сериала SerialInfoDb по filmId.
    // SerialInfoDb - класс для взаимодействия с БД.
    private suspend fun getFromApiSerialInfoDb(filmId: Int): Pair<SerialInfoDb?, Boolean> {
        val apiLoadResult = getSerialInfo(filmId)
        val serialInfo: SerialInfo? = apiLoadResult.first
        val error: Boolean = apiLoadResult.second

        if (error) return Pair(first = null, second = true)
        if (serialInfo == null) return Pair(first = null, second = false)

        val seasonsWithEpisodes: MutableList<SeasonsWithEpisodes> =
            emptyList<SeasonsWithEpisodes>().toMutableList()
        serialInfo.items.forEach { season ->
            val filmIdAndSeasonNumber = "$filmId/_${season.number}"
            val seasonTable = SeasonTable(
                filmIdAndSeasonNumber = filmIdAndSeasonNumber,
                filmId = filmId,
                seasonNumber = season.number
            )
            val episodes: MutableList<EpisodeTable> =
                emptyList<EpisodeTable>().toMutableList()
            season.episodes.forEach { episode ->
                episodes.add(
                    EpisodeTable(
                        filmIdAndSeasonNumber = filmIdAndSeasonNumber,
                        seasonNumber = episode.seasonNumber,
                        episodeNumber = episode.episodeNumber,
                        name = episode.nameRu ?: episode.nameEn ?: "",
                        synopsis = episode.synopsis,
                        releaseDateConverted = convertDate(episode.releaseDate)
                    )
                )
            }
            seasonsWithEpisodes.add(
                SeasonsWithEpisodes(
                    seasonTable = seasonTable,
                    episodes = episodes
                )
            )
        }
        return Pair(
            first = SerialInfoDb(
                serialTable = SerialTable(
                    filmId = filmId,
                    totalSeasons = serialInfo.total
                ),
                seasonsWithEpisodes = seasonsWithEpisodes.toList()
            ),
            second = false
        )
    }

    // Получение данных сериала SerialInfoDb по filmId.
    // Эта функция запрашивает из БД, если нет, то берёт из API, и записывает в БД.
    suspend fun getSerialInfoDb(filmId: Int): Pair<SerialInfoDb?, Boolean> {
        var serialInfoDb: SerialInfoDb? = dao.getSerialInfoDb(filmId)
        val apiLoadSerialInfoDbResult: Pair<SerialInfoDb?, Boolean>
        val error: Boolean

        if (serialInfoDb == null) {
            apiLoadSerialInfoDbResult = getFromApiSerialInfoDb(filmId)
            serialInfoDb = apiLoadSerialInfoDbResult.first
            error = apiLoadSerialInfoDbResult.second
            if (serialInfoDb != null) {
                Log.d(TAG, "Данные сериала $filmId загружены из API")
                addSerialInfoDb(serialInfoDb)
                Log.d(TAG, "Данные сериала $filmId записаны в БД")
            }
        } else {
            Log.d(TAG, "Данные сериала $filmId загружены из БД")
            error = false
        }

        return Pair(first = serialInfoDb, second = error)
    }

    fun divideStaffByType(allStaffList: List<StaffTable>): ActorsAndStaff {
        val actorsList: MutableList<StaffTable> = mutableListOf()
        val staffList: MutableList<StaffTable> = mutableListOf()
        allStaffList.forEach {
            if (it.professionKey == "ACTOR") actorsList.add(it)
            else staffList.add(it)
        }
        return ActorsAndStaff(actorsList.toList(), staffList.toList())
    }

    // Получение из API данных фильма SerialInfo по filmId, с переключением ключей API.
    private suspend fun getImages(
        filmId: Int,
        type: String,
        page: Int
    ): Pair<PagedImages?, Boolean> {
        for (i in 0..3) {
            Log.d(TAG, "getImages($filmId). apiKey = $currentApiKey")
            kotlin.runCatching {
                retrofit.getImages(
                    apiKey = currentApiKey,
                    filmId = filmId,
                    type = type,
                    page = page
                )
            }.fold(
                onSuccess = {
                    Log.d(TAG, "getImages($filmId). Успешная загрузка на итерации $i")
                    return Pair(first = it, second = false)
                },
                onFailure = {
                    Log.d(TAG, "getImages($filmId). Ошибка загрузки из Api. ${it.message ?: ""}")
                    changeApiKey()
                }
            )
        }
        return Pair(first = null, second = true)
    }

    // Получение из API списка изображений по filmId.
    // ImageTable - класс для взаимодействия с БД.
    private suspend fun getFromApiAllGallery(filmId: Int): Pair<List<ImageTable>?, Boolean> {
        val summaryList: MutableList<ImageTable> = mutableListOf()
        var error = true
        listOf(
            "STILL",
            "SHOOTING",
            "POSTER",
            "FAN_ART",
            "PROMO",
            "CONCEPT",
            "WALLPAPER",
            "COVER",
            "SCREENSHOT"
        ).forEach { type ->
            var page = 1
            do {
                val pagedImages = getImages(filmId = filmId, type = type, page = page)
                if (pagedImages.first != null && !pagedImages.second) {
                    pagedImages.first!!.items.forEach { image ->
                        summaryList.add(
                            ImageTable(
                                filmId = filmId,
                                image = image.imageUrl,
                                preview = image.previewUrl,
                                type = type
                            )
                        )
                    }
                    error = false // Если хоть одно изображение получено, ошибки загрузки нет.
                }
            } while (page < 20 && (pagedImages.first?.totalPages ?: 0) > page++)
        }
        return if (error) Pair(first = null, second = true)
        else Pair(first = summaryList.toList(), second = false)
    }

    // Получение списка изображений по filmId.
    // Эта функция запрашивает из БД, если нет, то берёт из API, и записывает в БД.
    suspend fun getAllGallery(filmId: Int): Pair<List<ImageTable>?, Boolean> {
        var imageTableList: List<ImageTable>? = dao.getImageTableList(filmId)
        val apiLoadAllGalleryResult: Pair<List<ImageTable>?, Boolean>
        val error: Boolean

        if (imageTableList.isNullOrEmpty()) {
            apiLoadAllGalleryResult = getFromApiAllGallery(filmId)
            imageTableList = apiLoadAllGalleryResult.first
            error = apiLoadAllGalleryResult.second
            if (!imageTableList.isNullOrEmpty()) {
                Log.d(TAG, "Список изображений $filmId загружен из API")
                addImageTableList(imageTableList)
                Log.d(TAG, "Список изображений $filmId записан в БД")
            }
        } else {
            Log.d(TAG, "Список изображений $filmId загружен из БД")
            Log.d(TAG, imageTableList.toString())
            error = false
        }

        return Pair(first = imageTableList, second = error)
    }

    // Получение списка изображений, включая изображение, с которого был переход на список.
    // Тип изображений в итоговом списке соответствует типу того, с которого был переход.
    suspend fun getImagesWithCurrentImage(
        filmId: Int,
        currentImage: String,
        imagesType: Int
    ): Pair<List<String>?, Boolean> {
        Log.d("Pager", "Repository. chosenImage = $currentImage")
        val resultImageList: MutableList<String> = mutableListOf(currentImage)
        var error = true
        val types: List<String> = when (imagesType) {
            0 -> listOf(
                "STILL",
                "SHOOTING",
                "POSTER",
                "FAN_ART",
                "PROMO",
                "CONCEPT",
                "WALLPAPER",
                "COVER",
                "SCREENSHOT"
            )
            1 -> listOf("STILL")
            2 -> listOf("SHOOTING")
            3 -> listOf("POSTER")
            4 -> listOf("FAN_ART")
            5 -> listOf("PROMO")
            6 -> listOf("CONCEPT")
            7 -> listOf("WALLPAPER")
            8 -> listOf("COVER")
            else -> listOf("SCREENSHOT")
        }
        types.forEach { type ->
            var imagesList: List<String>? = dao.getImagesOfType(filmId = filmId, type = type)
            if (imagesList == null) {
                var page = 1
                val imagesListFromApi: MutableList<String> = mutableListOf()
                do {
                    val apiLoadImagesOfTypeResult: Pair<PagedImages?, Boolean> =
                        getImages(filmId = filmId, type = type, page = page)
                    val pagedImages: PagedImages? = apiLoadImagesOfTypeResult.first
                    val imageTableList: MutableList<ImageTable> = mutableListOf()
                    if (!apiLoadImagesOfTypeResult.second && pagedImages != null && pagedImages.items.isNotEmpty()) {
                        Log.d(TAG, "Изображения $filmId типа $type загружены из API (page $page)")
                        pagedImages.items.forEach { image ->
                            imageTableList.add(
                                ImageTable(
                                    filmId = filmId,
                                    image = image.imageUrl,
                                    preview = image.previewUrl,
                                    type = type
                                )
                            )
                        }
                        addImageTableList(imageTableList)
                        Log.d(TAG, "Изображения $filmId типа $type записаны в БД (page $page)")
                    }
                    imagesListFromApi.addAll(imageTableList.map { imageTable -> imageTable.image })
                } while ((pagedImages?.totalPages ?: 0) > page++)
                imagesList = imagesListFromApi.toList()
            } else {
                Log.d(TAG, "Изображения $filmId типа $type загружены из БД")
            }
            if (imagesList.isNotEmpty()) {
                resultImageList.addAll(imagesList)
                error = false // Если хоть одно изображение получено, ошибки загрузки нет.
            }
        }
        val resultImageSet: Set<String> = resultImageList.toSet().minus("")

        return if (error) Pair(first = null, second = true)
        else Pair(first = resultImageSet.toList(), second = false)
    }

    // Получение из API похожих фильмов SimilarFilmList по filmId, с переключением ключей API.
    private suspend fun getSimilars(filmId: Int): Pair<SimilarFilmList?, Boolean> {
        for (i in 0..3) {
            Log.d(TAG, "getSimilars($filmId). apiKey = $currentApiKey")
            kotlin.runCatching {
                retrofit.getSimilars(apiKey = currentApiKey, filmId = filmId)
            }.fold(
                onSuccess = {
                    Log.d(TAG, "getSimilars($filmId). Успешная загрузка на итерации $i")
                    return Pair(first = it, second = false)
                },
                onFailure = {
                    Log.d(TAG, "getSimilars($filmId). Ошибка загрузки из Api. ${it.message ?: ""}")
                    changeApiKey()
                }
            )
        }
        return Pair(first = null, second = true)
    }

    // Получение из API похожих фильмов List<SimilarFilmTable> по filmId.
    // SimilarFilmTable - класс для взаимодействия с БД.
    private suspend fun getFromApiSimilarFilmTableList(filmId: Int): Pair<List<SimilarFilmTable>?, Boolean> {
        val apiLoadResult = getSimilars(filmId)
        val similarFilmList: SimilarFilmList? = apiLoadResult.first
        val error: Boolean = apiLoadResult.second
        if (similarFilmList == null || error) {
            return Pair(first = null, second = error)
        } else {
            val similarFilmTableList: MutableList<SimilarFilmTable> = mutableListOf()
            similarFilmList.items.forEach { similarFilm ->
                val name = similarFilm.nameRu ?: similarFilm.nameEn ?: similarFilm.nameOriginal
                if (name != null) {
                    similarFilmTableList.add(
                        SimilarFilmTable(
                            filmId = filmId,
                            similarFilmId = similarFilm.filmId,
                            name = name,
                            posterUrlPreview = similarFilm.posterUrlPreview
                        )
                    )
                }
            }
            return Pair(first = similarFilmTableList.toList(), second = false)
        }
    }

    // Получение похожих фильмов List<SimilarFilmTable> по filmId.
    // Эта функция запрашивает из БД, если нет, то берёт из API, и записывает в БД.
    suspend fun getSimilarFilmTableList(filmId: Int): Pair<List<SimilarFilmTable>?, Boolean> {
        var similarFilmTableList: List<SimilarFilmTable>? = dao.getSimilarFilmTableList(filmId)
        val error: Boolean

        if (similarFilmTableList.isNullOrEmpty()) {
            val apiLoadSimilarsResult: Pair<List<SimilarFilmTable>?, Boolean> = getFromApiSimilarFilmTableList(filmId)
            similarFilmTableList = apiLoadSimilarsResult.first
            error = apiLoadSimilarsResult.second
            if (!similarFilmTableList.isNullOrEmpty()) {
                Log.d(TAG, "Похожие фильмы $filmId загружены из API")
                addSimilarFilmTable(similarFilmTableList)
                Log.d(TAG, "Похожие фильмы $filmId записаны в БД")
            }
        } else {
            Log.d(TAG, "Похожие фильмы $filmId загружены из БД")
            error = false
        }
        Log.d(TAG, similarFilmTableList.toString())
        return Pair(first = similarFilmTableList, second = error)
    }

    fun convertClassListToStringList(anyList: List<Any>): List<String> {
        val resultList: MutableList<String> = emptyList<String>().toMutableList()
        if (anyList.isNotEmpty()) {
            when (anyList[0]) {
                is Country -> anyList.forEach { resultList.add((it as Country).country) }
                is CountryTable -> anyList.forEach { resultList.add((it as CountryTable).country) }
                is Genre -> anyList.forEach { resultList.add((it as Genre).genre) }
                is GenreTable -> anyList.forEach { resultList.add((it as GenreTable).genre) }
            }
        }
        return resultList.toList()
    }

    fun convertStringListToString(stringList: List<String>): String {
        var resultString = ""
        stringList.forEachIndexed { index, element ->
            if (index == 0) resultString = element
            else resultString += ", $element"
        }
        return resultString
    }

    private fun convertDate(dateFromApi: String?): String? {
        if (dateFromApi == null) return null
        else {
            val listOfValues: List<String> = dateFromApi.split("-")
            val month: String = when (listOfValues[1]) {
                "01" -> "января"
                "02" -> "февраля"
                "03" -> "марта"
                "04" -> "апреля"
                "05" -> "мая"
                "06" -> "июня"
                "07" -> "июля"
                "08" -> "августа"
                "09" -> "сентября"
                "10" -> "октября"
                "11" -> "ноября"
                "12" -> "декабря"
                else -> return null
            }
            return "${listOfValues[2]} $month ${listOfValues[0]}"
        }
    }

// Функции с запросами Dao

    // Запрос на добавление новой записи любимого фильма
    suspend fun addFilmTable(filmTable: FilmTable) {
        Log.d("BD", "Репозиторий. Нажата кнопка Favorite. Пытаемся записать ${filmTable.filmId}")
        dao.addFilmTable(filmTable)
    }

    // Запрос на добавление списка стран фильма в таблицу стран
    suspend fun addCountryTable(filmId: Int, countryList: List<String>) {
        countryList.forEach { country ->
            dao.addCountryTable(
                CountryTable(
                    filmId = filmId,
                    country = country
                )
            )
        }
    }

    // Запрос на добавление списка жанров фильма в таблицу стран
    suspend fun addGenreTable(filmId: Int, genreList: List<String>) {
        genreList.forEach { genre ->
            dao.addGenreTable(
                GenreTable(
                    filmId = filmId,
                    genre = genre
                )
            )
        }
    }

    // Запрос на добавление персонала фильма
    suspend fun addStaffTableList(staffTableList: List<StaffTable>) {
        staffTableList.forEach { staffTable -> dao.addStaffTable(staffTable) }
    }

    // Запрос на добавление галереи фильма
    suspend fun addImageTableList(imageTableList: List<ImageTable>) {
        imageTableList.forEach { imageWithType -> dao.addImageTable(imageWithType) }
    }

    // Запрос на добавление похожих фильмов
    suspend fun addSimilarFilmTable(similarFilmTableList: List<SimilarFilmTable>) {
        similarFilmTableList.forEach { similarFilmTable -> dao.addSimilarFilmTable(similarFilmTable) }
    }

    // Запрос на добавление в БД данных фильма FilmDb
    private suspend fun addFilmDb(filmDb: FilmDb) {
        dao.addFilmTable(filmDb.filmTable)
        filmDb.countries.forEach { countryTable ->
            dao.addCountryTable(countryTable)
        }
        filmDb.genres.forEach { genreTable ->
            dao.addGenreTable(genreTable)
        }
    }

    // Запрос на добавление в БД данных сериала
    suspend fun addSerialInfoDb(serialInfoDb: SerialInfoDb) {
        dao.addSerialTable(serialInfoDb.serialTable)
        serialInfoDb.seasonsWithEpisodes.forEach { seasonWithEpisodes ->
            dao.addSeasonTable(seasonWithEpisodes.seasonTable)
            seasonWithEpisodes.episodes.forEach { episode ->
                dao.addEpisodeTable(episode)
            }
        }
    }

    // Запрос на добавление в БД данных персонала
    suspend fun addPersonInfoDb(personInfoDb: PersonInfoDb) {
        dao.addPersonTable(personInfoDb.personTable)
        personInfoDb.filmsOfPerson.forEach { filmOfPerson ->
            dao.addFilmOfPersonTable(filmOfPerson)
        }
    }

    // Запрос на получение данных фильма FilmDb по filmId
    suspend fun getFilmDb(filmId: Int): FilmDb? {
        return dao.getFilmDb(
            filmId
        )
    }

    // Запрос на получение данных персонала фильма по filmId
    suspend fun getFromDaoStaffTableList(filmId: Int): List<StaffTable>? {
        return dao.getStaffTableList(filmId)
    }

    // Запрос на получение галереи фильма по filmId
    suspend fun getFromDaoImageTableList(filmId: Int): List<ImageTable>? {
        return dao.getImageTableList(filmId)
    }

    // Запрос на получение данных сериала по filmId
    suspend fun getFromDaoSerialInfoDb(filmId: Int): SerialInfoDb? {
        return dao.getSerialInfoDb(
            filmId
        )
    }

    // Запрос на проверку наличия записи данных персонала в БД
    suspend fun isPersonDataExists(searchedPersonId: Int): Boolean {
        return dao.isPersonDataExists(
            searchedPersonId
        )
    }

    // Запрос на проверку наличия записи данных персонала в БД
    suspend fun getPersonTable(searchedPersonId: Int): PersonTable? {
        return dao.getPersonTable(searchedPersonId)
    }

    // Запрос на получение данных персонала по personId, и сортировка по убыванию рейтинга
    suspend fun getFromDaoPersonInfoDb(personId: Int): PersonInfoDb? {
        val personInfoDb = dao.getPersonInfoDb(personId)
        val sortedFilmsOfPerson: MutableList<FilmOfPersonTable> =
            emptyList<FilmOfPersonTable>().toMutableList()
        return if (personInfoDb == null) null
        else {
            personInfoDb.filmsOfPerson.sortedByDescending { filmOfPersonTable -> filmOfPersonTable.rating }
                .forEach { film ->
                    sortedFilmsOfPerson.add(film)
                }
            PersonInfoDb(
                personTable = personInfoDb.personTable,
                filmsOfPerson = sortedFilmsOfPerson.toList()
            )
        }
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
    suspend fun getCollectionFilmsIds(collectionName: String): List<Int>? {
        return dao.getCollectionFilmsIds(collectionName)
    }

    // Запрос на получение списка имен коллекций, относящихся к фильмам
    suspend fun getCollectionNamesOfFilms(): List<String> {
        return dao.getCollectionNamesOfFilms()
    }

    // Получение списка коллекций
    suspend fun getCollectionInfoList(): List<CollectionInfo> {
        val collectionInfoList: MutableList<CollectionInfo> =
            emptyList<CollectionInfo>().toMutableList()
        val collectionNamesList: List<String> = dao.getCollectionNames()
        collectionNamesList.forEach { collectionName ->
            val filmsQuantity: Int = dao.getCollectionFilmsQuantity(collectionName)
            collectionInfoList.add(
                CollectionInfo(
                    collectionName = collectionName,
                    filmsQuantity = filmsQuantity
                )
            )
        }
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

    fun convertLength(lengthInt: Int?): String? {
        if (lengthInt == null || lengthInt <= 0) return null
        val hours: Int = lengthInt / 60
        val minutes: Int = lengthInt % 60
        val hoursText: String = if (hours == 0) "" else " ч"
        val minutesText: String = if (minutes == 0) "" else " мин"
        val hoursPart: String = if (hours > 0) {
            hours.toString() + hoursText
        } else ""
        val separator: String = if (hours > 0 && minutes > 0) " " else ""
        val minutesPart: String = if (minutes > 0) {
            minutes.toString() + minutesText
        } else ""
        return hoursPart + separator + minutesPart
    }

    fun convertAgeLimit(ratingAgeLimits: String?): String? =
        if (ratingAgeLimits != null) ratingAgeLimits.removePrefix("age") + "+" else null
}