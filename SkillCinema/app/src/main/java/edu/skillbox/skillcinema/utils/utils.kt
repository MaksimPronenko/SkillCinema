package edu.skillbox.skillcinema.utils

import edu.skillbox.skillcinema.models.filmAndSerial.country.Country
import edu.skillbox.skillcinema.models.filmAndSerial.country.CountryTable
import edu.skillbox.skillcinema.models.filmAndSerial.genre.Genre
import edu.skillbox.skillcinema.models.filmAndSerial.genre.GenreTable
import javax.inject.Inject

const val ARG_FILM_ID = "filmId"
const val ARG_STAFF_ID = "staffId"

const val ARG_GENRE_1_KEY = "genre1Key"
const val ARG_COUNTRY_1_KEY = "country1Key"
const val ARG_GENRE_2_KEY = "genre2Key"
const val ARG_COUNTRY_2_KEY = "country2Key"

const val VIEW_TYPE_FILM = 0
const val VIEW_TYPE_SHOW_ALL = 1
const val VIEW_TYPE_CLEAR = 2

const val COLLECTION_FAVORITE = "favorite"
const val COLLECTION_WANTED_TO_WATCH = "wantedToWatch"
const val ARG_COLLECTION_NAME = "collectionName"
const val ARG_POSTER_SMALL = "posterSmall"
const val ARG_NAME = "name"
const val ARG_YEAR = "year"
const val ARG_GENRES = "genres"
const val ARG_FILM_NAME = "filmName"
const val ARG_STAFF_TYPE = "staffType"
const val ARG_CURRENT_IMAGE = "currentImage"
const val ARG_IMAGES_TYPE = "imagesType"

enum class FilmType{
    FILM, TV_SHOW, TV_SERIES, MINI_SERIES, ALL
}

enum class FilmOrder{
    RATING, NUM_VOTE, YEAR
}

enum class FilmTopType{
    TOP_100_POPULAR_FILMS, TOP_250_BEST_FILMS
}

enum class ProfessionKey{
    ACTOR,
    HIMSELF,
    HERSELF,
    HRONO_TITR_MALE,
    HRONO_TITR_FEMALE,
    DIRECTOR,
    PRODUCER,
    PRODUCER_USSR,
    VOICE_DIRECTOR,
    WRITER,
    OPERATOR,
    EDITOR,
    COMPOSER,
    DESIGN,
    TRANSLATOR,
    UNKNOWN
}

enum class ImageType{
    STILL,
    SHOOTING,
    POSTER,
    FAN_ART,
    PROMO,
    CONCEPT,
    WALLPAPER,
    COVER,
    SCREENSHOT
}

enum class Collections(val title: String) {
    FAVORITE("Любимое"),
    WANT_TO_WATCH("Хочу посмотреть")
}

class Converters @Inject constructor() {
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