package edu.skillbox.skillcinema.data

import android.util.Log
import edu.skillbox.skillcinema.api.retrofit
import edu.skillbox.skillcinema.models.filmAndSerial.film.FilmItemData
import edu.skillbox.skillcinema.models.filmFiltered.FilmFiltered
import edu.skillbox.skillcinema.models.filmFiltered.PagedFilmFilteredList
import edu.skillbox.skillcinema.models.filmTop.FilmTop
import edu.skillbox.skillcinema.models.filmTop.PagedFilmTopList
import edu.skillbox.skillcinema.models.premiere.FilmList
import edu.skillbox.skillcinema.models.premiere.FilmPremiere
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.math.ceil

private const val TAG = "RepositoryMainLists"

class RepositoryMainLists @Inject constructor(private val dao: FilmDao) {
    private suspend fun getPremieres(year: Int, month: String): Pair<FilmList?, Boolean> {
        for (i in 0..3) {
            Log.d(TAG, "getPremieres($year, $month). apiKey = ${ApyKeys.currentApiKey}")
            kotlin.runCatching {
                retrofit.getPremieres(apiKey = ApyKeys.currentApiKey, year = year, month = month)
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
                    ApyKeys.changeApiKey()
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
            Log.d(TAG, "getTopList($type). apiKey = ${ApyKeys.currentApiKey}")
            kotlin.runCatching {
                retrofit.getTopList(apiKey = ApyKeys.currentApiKey, type = type, page = page)
            }.fold(
                onSuccess = {
                    Log.d(TAG, "getTopList($type). Успешная загрузка на итерации $i")
                    return Pair(first = it, second = false)
                },
                onFailure = {
                    Log.d(TAG, "getTopList($type). Ошибка загрузки из Api. ${it.message ?: ""}")
                    ApyKeys.changeApiKey()
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
            Log.d(TAG, "getSerials($page). apiKey = ${ApyKeys.currentApiKey}")
            kotlin.runCatching {
                retrofit.getSerials(apiKey = ApyKeys.currentApiKey, page = page)
            }.fold(
                onSuccess = {
                    Log.d(TAG, "getSerials($page). Успешная загрузка на итерации $i")
                    return Pair(first = it, second = false)
                },
                onFailure = {
                    Log.d(TAG, "getSerials($page). Ошибка загрузки из Api. ${it.message ?: ""}")
                    ApyKeys.changeApiKey()
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
            Log.d(TAG, "getFilmsFiltered(). apiKey = ${ApyKeys.currentApiKey}")
            kotlin.runCatching {
                retrofit.getFilmFiltered(
                    apiKey = ApyKeys.currentApiKey,
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
                    ApyKeys.changeApiKey()
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
            Log.d(TAG, "searchFilms($keyword). apiKey = ${ApyKeys.currentApiKey}")
            kotlin.runCatching {
                retrofit.searchFilms(
                    apiKey = ApyKeys.currentApiKey,
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
                    ApyKeys.changeApiKey()
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
}