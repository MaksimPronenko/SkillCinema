package edu.skillbox.skillcinema.data

import edu.skillbox.skillcinema.api.retrofit
import edu.skillbox.skillcinema.models.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.math.ceil

class Repository @Inject constructor() {

    suspend fun getPremieres(): List<FilmPremiere> {

        val calendar: Calendar = Calendar.getInstance()
        val currentDate: Date = calendar.time // Текущая дата
        val currentYear = calendar.get(Calendar.YEAR) // Текущий год
        val currentMonth = calendar.get(Calendar.MONTH) // Текущий месяц

        calendar.add(Calendar.DAY_OF_MONTH, 15) // Прибавляю 15, а не 14, из-за того, что
        // в дате фильма указано время 00:00:00, а текущее время больше.
        val datePlus15Days: Date = calendar.time // Дата через 2 недели
        val datePlus15DaysYear = calendar.get(Calendar.YEAR) // Получили месяц новый
        val datePlus15DaysMonth = calendar.get(Calendar.MONTH) // Получили месяц новый

        val premieresThisMonth =
            retrofit.getPremieres(currentYear, monthToString(currentMonth)).items

        var premieresNextMonth: List<FilmPremiere> = emptyList()
        if (datePlus15DaysMonth != currentMonth) premieresNextMonth =
            retrofit.getPremieres(datePlus15DaysYear, monthToString(datePlus15DaysMonth)).items

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

//            calendar.time = dateFormat.parse("2023-02-23") as Date
//            val startDate:Date = calendar.time

            if (premiereDate in currentDate..datePlus15Days) premieresNext2Weeks.add(it)
        }

        return premieresNext2Weeks.toList().shuffled()
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

    suspend fun getTop100Popular(page: Int): PagedFilmTopList {
        return retrofit.getTop100Popular(page)
    }

    suspend fun getTop250(page: Int): PagedFilmTopList {
        return retrofit.getTop250(page)
    }

    suspend fun getSeries(page: Int): PagedFilmFilteredList {
        return retrofit.getSeries(page)
    }

    suspend fun getFilmFiltered(genres: Int, countries: Int, page: Int): PagedFilmFilteredList {

        val apiResult: PagedFilmFilteredList = retrofit.getFilmFiltered(genres, countries, page)
        var newTotal: Int = apiResult.total
        var newTotalPages = apiResult.totalPages

        val filmsFilteredRatingAbove6: MutableList<FilmFiltered> =
            emptyList<FilmFiltered>().toMutableList()
        apiResult.items.forEach {
            if (it.ratingKinopoisk != null) filmsFilteredRatingAbove6.add(it)
            else {
                newTotal -= 1
                newTotalPages = ceil(newTotal / 20.0).toInt()
            }
        }

        return PagedFilmFilteredList(
            total = newTotal,
            totalPages = newTotalPages,
            items = filmsFilteredRatingAbove6
        )
    }

    suspend fun getFilmInfo(filmId: Int): FilmInfo {
        return retrofit.getFilmInfo(filmId)
    }

    suspend fun getActorsAndStaff(filmId: Int): ActorsAndStaff {
        val apiResult = retrofit.getStaff(filmId)
        val actorsList: MutableList<StaffInfo> = emptyList<StaffInfo>().toMutableList()
        val staffList: MutableList<StaffInfo> = emptyList<StaffInfo>().toMutableList()
        apiResult.forEach {
            if (it.professionKey == "ACTOR") actorsList.add(it)
            else staffList.add(it)
        }
        return ActorsAndStaff(actorsList.toList(), staffList.toList())
    }

    suspend fun getImages(
        filmId: Int,
        types: List<String> = listOf(
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
    ): List<ImageWithType> {
        val summaryList: MutableList<ImageWithType> = emptyList<ImageWithType>().toMutableList()
        types.forEach { type ->
            var page = 1
            do {
                val pagedImages = retrofit.getImages(filmId = filmId, type = type, page = page)
                pagedImages.items.forEach { image ->
                    summaryList.add(
                        ImageWithType(
                            type = type,
                            imageUrl = image.imageUrl,
                            previewUrl = image.previewUrl
                        )
                    )
                }
            } while (pagedImages.totalPages > page++)
        }
        return summaryList.toList()
    }

    suspend fun getSimilars(filmId: Int): SimilarFilmList {
        return retrofit.getSimilars(filmId)
    }
}