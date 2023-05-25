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
    ): PagedFilmFilteredList {
        Log.d(TAG, "Сработала searchFilms()")
        val result = retrofit.searchFilms(
            country,
            genre,
            order,
            type,
            ratingFrom,
            ratingTo,
            yearFrom,
            yearTo,
            keyword,
            page
        )
        Log.d(TAG, "$result")
        return result
    }

    suspend fun getFilmInfo(filmId: Int): FilmInfo {
        return retrofit.getFilmInfo(filmId)
    }

    suspend fun getFromApiFilmInfoDb(filmId: Int): FilmInfoDb {
        val filmInfo = retrofit.getFilmInfo(filmId)
        val staffInfo = retrofit.getStaff(filmId)
        val gallery = getAllGallery(filmId)
        val similars = retrofit.getSimilars(filmId).items

        val countries: MutableList<CountryTable> =
            emptyList<CountryTable>().toMutableList()
        val genres: MutableList<GenreTable> =
            emptyList<GenreTable>().toMutableList()
        val staffList: MutableList<StaffTable> =
            emptyList<StaffTable>().toMutableList()
        val images: MutableList<ImageTable> =
            emptyList<ImageTable>().toMutableList()
        val similarFilms: MutableList<SimilarFilmTable> =
            emptyList<SimilarFilmTable>().toMutableList()

        filmInfo.countries.forEach { countryClass ->
            countries.add(
                CountryTable(
                    filmId = filmId,
                    country = countryClass.country
                )
            )
        }
        filmInfo.genres.forEach { genreClass ->
            genres.add(
                GenreTable(
                    filmId = filmId,
                    genre = genreClass.genre
                )
            )
        }
        staffInfo.forEach { staff ->
            staffList.add(
                StaffTable(
                    filmId = filmId,
                    staffId = staff.staffId,
                    name = staff.nameRu ?: staff.nameEn ?: "",
                    description = staff.description,
                    posterUrl = staff.posterUrl,
                    professionText = staff.professionText,
                    professionKey = staff.professionKey
                )
            )
        }
        gallery.forEach { image ->
            images.add(
                ImageTable(
                    filmId = filmId,
                    image = image.imageUrl,
                    preview = image.previewUrl,
                    type = image.type
                )
            )
        }
        similars.forEach { similarFilm ->
            similarFilms.add(
                SimilarFilmTable(
                    filmId = filmId,
                    similarFilmId = similarFilm.filmId,
                    name = similarFilm.nameRu ?: similarFilm.nameEn ?: similarFilm.nameOriginal
                    ?: "",
                    posterUrlPreview = similarFilm.posterUrlPreview
                )
            )
        }

        return FilmInfoDb(
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
            genres = genres.toList(),
            collections = emptyList(),
            staffList = staffList.toList(),
            images = images.toList(),
            similarFilms = similarFilms.toList()
        )
    }

    suspend fun getSerialInfo(filmId: Int): SerialInfo {
        return retrofit.getSerialInfo(filmId)
    }

    suspend fun getFromApiSerialInfoDb(filmId: Int): SerialInfoDb {
        val serialInfo = retrofit.getSerialInfo(filmId)
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
        return SerialInfoDb(
            serialTable = SerialTable(
                filmId = filmId,
                totalSeasons = serialInfo.total
            ),
            seasonsWithEpisodes = seasonsWithEpisodes.toList()
        )
    }

    suspend fun getAllStaffList(filmId: Int): List<StaffInfo> {
        return retrofit.getStaff(filmId)
    }

    fun divideStaffByType(allStaffList: List<StaffInfo>): ActorsAndStaff {
        val actorsList: MutableList<StaffInfo> = emptyList<StaffInfo>().toMutableList()
        val staffList: MutableList<StaffInfo> = emptyList<StaffInfo>().toMutableList()
        allStaffList.forEach {
            if (it.professionKey == "ACTOR") actorsList.add(it)
            else staffList.add(it)
        }
        return ActorsAndStaff(actorsList.toList(), staffList.toList())
    }

    suspend fun getAllGallery(
        filmId: Int
    ): List<ImageWithType> {
        val summaryList: MutableList<ImageWithType> = emptyList<ImageWithType>().toMutableList()
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
            } while (page < 20 && pagedImages.totalPages > page++)
        }
        return summaryList.toList()
    }

    suspend fun getImages(
        filmId: Int,
        currentImage: String,
        imagesType: Int
    ): List<String> {
        Log.d("Pager", "Repository. chosenImage = $currentImage")
        val resultImageList: MutableList<String> = mutableListOf(currentImage)
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
            var page = 1
            do {
                val pagedImages = retrofit.getImages(filmId = filmId, type = type, page = page)
                pagedImages.items.forEach { image ->
                    resultImageList.add(image.imageUrl)
                }
            } while (pagedImages.totalPages > page++)
        }
        val resultImageSet: Set<String> = resultImageList.toSet().minus("")
        return resultImageSet.toList()
    }

    suspend fun getSimilars(filmId: Int): SimilarFilmList {
        return retrofit.getSimilars(filmId)
    }

    suspend fun getPersonInfo(personId: Int): PersonInfo {
        return retrofit.getPersonInfo(personId)
    }

    suspend fun getFromApiPersonInfoDb(personId: Int): PersonInfoDb {
        val personInfo = retrofit.getPersonInfo(personId)
        val filmsOfPerson: MutableList<FilmOfPersonTable> =
            emptyList<FilmOfPersonTable>().toMutableList()
//        val sortedFilmsOfPerson: MutableList<FilmOfPersonTable> =
//            emptyList<FilmOfPersonTable>().toMutableList()
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
//        filmsOfPerson.sortedByDescending { filmOfPersonTable -> filmOfPersonTable.rating }
//            .forEach { film ->
//                sortedFilmsOfPerson.add(film)
//            }
        return PersonInfoDb(
            personTable = PersonTable(
                personId = personId,
                name = personInfo.nameRu ?: personInfo.nameEn,
                sex = personInfo.sex,
                posterUrl = personInfo.posterUrl,
                profession = personInfo.profession
            ),
            filmsOfPerson = sortedFilmsOfPerson
        )
    }

//    suspend fun getPersonFilms(filmIDs: List<Int>): List<FilmInfo> {
//        val resultFilmList: MutableList<FilmInfo> = emptyList<FilmInfo>().toMutableList()
//        filmIDs.forEach { filmId -> resultFilmList.add(retrofit.getFilmInfo(filmId)) }
//        Log.d("StaffFilmIDs", resultFilmList.size.toString())
//        return resultFilmList.toList()
//    }

//    suspend fun getPersonFilmsDetailed(films: List<FilmOfPersonTable>): List<FilmOfStaff> {
//        val filmsDetailedList: MutableList<FilmOfStaff> = emptyList<FilmOfStaff>().toMutableList()
//        films.forEach { film ->
//            var filmInfoDb = getFilmInfoDb(film.filmId)
//            if (filmInfoDb == null) {
//                Log.d(TAG, "Фильм ${film.filmId} не обнаружен в БД")
//                kotlin.runCatching {
//                    getFromApiFilmInfoDb(film.filmId)
//                }.fold(
//                    onSuccess = {
//                        filmInfoDb = it
//                        Log.d(TAG, "Фильм ${film.filmId} загружен из Api")
//                        addFilmInfoDb(it)
//                        Log.d(TAG, "Фильм ${film.filmId} записан в БД")
//                    },
//                    onFailure = {
//                        Log.d(TAG, "Фильм не загрузился ни из БД, ни из Api. ${it.message ?: ""}")
//                    }
//                )
//            } else Log.d(TAG, "Фильм ${film.filmId} загружен из БД")
//            if (filmInfoDb != null) {
//                filmsDetailedList.add(
//                    FilmOfStaff(
//                        filmId = filmInfoDb!!.filmTable.filmId,
//                        name = filmInfoDb!!.filmTable.name,
//                        poster = filmInfoDb!!.filmTable.posterSmall,
//                        rating = filmInfoDb!!.filmTable.rating,
//                        year = filmInfoDb!!.filmTable.year,
//                        genres = convertStringListToString(
//                            convertClassListToStringList(filmInfoDb!!.genres)
//                        ),
//                        profession = film.professionKey
//                    )
//                )
//            }
//        }
//        return filmsDetailedList.toList()
//    }

    suspend fun getPersonFilmDetailed(film: FilmOfPersonTable): FilmOfStaff? {
        var filmInfoDb = getFilmInfoDb(film.filmId)
        if (filmInfoDb == null) {
            Log.d(TAG, "Фильм ${film.filmId} не обнаружен в БД")
            kotlin.runCatching {
                getFromApiFilmInfoDb(film.filmId)
            }.fold(
                onSuccess = {
                    filmInfoDb = it
                    Log.d(TAG, "Фильм ${film.filmId} загружен из Api")
                    addFilmInfoDb(it)
                    Log.d(TAG, "Фильм ${film.filmId} записан в БД")
                },
                onFailure = {
                    Log.d(TAG, "Фильм не загрузился ни из БД, ни из Api. ${it.message ?: ""}")
                }
            )
        } else Log.d(TAG, "Фильм ${film.filmId} загружен из БД")
        return if (filmInfoDb != null) {
            FilmOfStaff(
                filmId = filmInfoDb!!.filmTable.filmId,
                name = filmInfoDb!!.filmTable.name,
                poster = filmInfoDb!!.filmTable.posterSmall,
                rating = filmInfoDb!!.filmTable.rating,
                year = filmInfoDb!!.filmTable.year,
                genres = convertStringListToString(
                    convertClassListToStringList(filmInfoDb!!.genres)
                ),
                profession = film.professionKey
            )
        } else null
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

//    fun genresListToString(genres: List<Genre>): String {
//        var resultString = ""
//        genres.forEachIndexed { index, genre ->
//            if (index == 0) resultString = genre.genre
//            else resultString += ", " + genre.genre
//        }
//        return resultString
//    }

//    fun filmsQuantityToText(quantity: Int, baseWord: String): String {
//        val remOfDivBy10 = quantity % 10
//        val remOfDivBy100 = quantity % 100
//        return "$quantity $baseWord" + when (remOfDivBy10) {
//            1 -> if (remOfDivBy100 == 11) "ов" else ""
//            in 2..4 -> if (remOfDivBy100 == 12) "ов" else "а"
//            else -> "ов"
//        }
//    }

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

    // Запрос на проверку наличия фильма в указанной коллекции
    suspend fun isFilmExistsInCollection(filmId: Int, collectionName: String): Boolean {
        return dao.isFilmExistsInCollection(filmId, collectionName)
    }

    // Запрос на добавление коллекции, включающей фильм
    suspend fun addCollectionTable(collectionTable: CollectionTable) {
        dao.addCollectionTable(collectionTable)
    }

    // Запрос на удаление фильма из коллекции
    suspend fun removeCollectionTable(filmId: Int, collectionName: String) {
        dao.removeCollectionTable(filmId, collectionName)
    }

    // Запрос на добавление персонала фильма
    suspend fun addStaffTable(filmId: Int, staffInfoList: List<StaffInfo>) {
        staffInfoList.forEach { staffInfo ->
            dao.addStaffTable(
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
    }

    // Запрос на добавление галереи фильма
    suspend fun addImageTable(filmId: Int, imageWithTypeList: List<ImageWithType>) {
        imageWithTypeList.forEach { imageWithType ->
            dao.addImageTable(
                ImageTable(
                    filmId = filmId,
                    image = imageWithType.imageUrl,
                    preview = imageWithType.previewUrl,
                    type = imageWithType.type
                )
            )
        }
    }

    // Запрос на добавление похожих фильмов
    suspend fun addSimilarFilmTable(filmId: Int, similarFilmList: List<SimilarFilm>) {
        similarFilmList.forEach { similarFilm ->
            dao.addSimilarFilmTable(
                SimilarFilmTable(
                    filmId = filmId,
                    similarFilmId = similarFilm.filmId,
                    name = similarFilm.nameRu ?: similarFilm.nameEn ?: similarFilm.nameOriginal
                    ?: "",
                    posterUrlPreview = similarFilm.posterUrlPreview
                )
            )
        }
    }

    // Запрос на добавление в БД данных фильма
    suspend fun addFilmInfoDb(filmInfoDb: FilmInfoDb) {
        dao.addFilmTable(filmInfoDb.filmTable)
        filmInfoDb.countries.forEach { countryTable ->
            dao.addCountryTable(countryTable)
        }
        filmInfoDb.genres.forEach { genreTable ->
            dao.addGenreTable(genreTable)
        }
        filmInfoDb.staffList.forEach { staffTable ->
            dao.addStaffTable(staffTable)
        }
        filmInfoDb.images.forEach { imageTable ->
            dao.addImageTable(imageTable)
        }
        filmInfoDb.similarFilms.forEach { similarFilmTable ->
            dao.addSimilarFilmTable(similarFilmTable)
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

    // Запрос на проверку наличия записи данных фильма в БД
    suspend fun isFilmDataExists(searchedFilmId: Int): Boolean {
        return dao.isFilmDataExists(
            searchedFilmId
        )
    }

    // Запрос на получение данных фильма по filmId
    suspend fun getFilmInfoDb(filmId: Int): FilmInfoDb? {
        return dao.getFilmInfoDb(
            filmId
        )
    }

    suspend fun getAllStaffFromDb(filmId: Int): List<StaffTable>? {
        return dao.getAllStaffFromDb(filmId)
    }

    // Запрос на проверку наличия записи данных сериала в БД
    suspend fun isSerialDataExists(searchedFilmId: Int): Boolean {
        return dao.isSerialDataExists(
            searchedFilmId
        )
    }

    // Запрос на получение данных сериала по filmId
    suspend fun getSerialInfoDb(filmId: Int): SerialInfoDb {
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

    // Запрос на получение данных персонала по personId, и сортировка по убыванию рейтинга
    suspend fun getPersonInfoDb(personId: Int): PersonInfoDb {
        val personInfoDb = dao.getPersonInfoDb(personId)
        val sortedFilmsOfPerson: MutableList<FilmOfPersonTable> =
            emptyList<FilmOfPersonTable>().toMutableList()
        personInfoDb.filmsOfPerson.sortedByDescending { filmOfPersonTable -> filmOfPersonTable.rating }
            .forEach { film ->
                sortedFilmsOfPerson.add(film)
            }
        return PersonInfoDb(
            personTable = personInfoDb.personTable,
            filmsOfPerson = sortedFilmsOfPerson.toList()
        )
    }

// Запросы для проверки

    // Запрос на получение данных фильма
//    suspend fun getAllFilmInfoDb(): List<FilmInfoDb> {
//        return dao.getAllFilmInfoDb()
//    }

    // Запрос для отображение всех строк таблицы
//    suspend fun getAllFilmTable(): List<FilmTable> {
//        return dao.getAllFilmTable()
//    }

// Запрос на очистку таблицы
//    suspend fun clearTable() {
//        dao.clearTable()
//    }
//    private val repositoryScope = CoroutineScope(Dispatchers.Default)
//    fun getAllFavorite(): StateFlow<List<Favorite>> {
//        Log.d("BD", "Репозиторий. Пытаемся запросить все любымые фильмы")
//        return dao.getAllFavorite()
//            .stateIn(
//                scope = repositoryScope,
//                started = SharingStarted.WhileSubscribed(5000L),
//                initialValue = emptyList()
//            )
//    }
//    val allFavorite = dao.getAllFavorite()
//        .stateIn(
//            scope = repositoryScope,
//            started = SharingStarted.WhileSubscribed(5000L),
//            initialValue = emptyList()
//        )

    fun convertStaffTableListToStaffInfoList(staffTableList: List<StaffTable>): List<StaffInfo> {
        val staffInfoList: MutableList<StaffInfo> =
            emptyList<StaffInfo>().toMutableList()
        staffTableList.forEach {
            staffInfoList.add(
                StaffInfo(
                    staffId = it.staffId,
                    nameRu = it.name,
                    nameEn = null,
                    description = it.description,
                    posterUrl = it.posterUrl,
                    professionText = it.professionText,
                    professionKey = it.professionKey
                )
            )
        }
        return staffInfoList.toList()
    }

    fun convertImageTableListToImageWithTypeList(imageTableList: List<ImageTable>): List<ImageWithType> {
        val imageWithTypeList: MutableList<ImageWithType> =
            emptyList<ImageWithType>().toMutableList()
        imageTableList.forEach {
            imageWithTypeList.add(
                ImageWithType(
                    type = it.type,
                    imageUrl = it.image,
                    previewUrl = it.preview
                )
            )
        }
        return imageWithTypeList.toList()
    }

    fun convertSimilarFilmTableListToSimilarFilmList(similarFilmTableList: List<SimilarFilmTable>): List<SimilarFilm> {
        val similarFilmList: MutableList<SimilarFilm> =
            emptyList<SimilarFilm>().toMutableList()
        similarFilmTableList.forEach {
            similarFilmList.add(
                SimilarFilm(
                    filmId = it.similarFilmId,
                    nameRu = it.name,
                    nameEn = null,
                    nameOriginal = null,
                    posterUrlPreview = it.posterUrlPreview
                )
            )
        }
        return similarFilmList.toList()
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

//    fun convertImageWithTypeListToImageTableList(imageWithTypeList: List<ImageWithType>): List<ImageTable> {
//        val imageTableList: MutableList<ImageTable> = emptyList<ImageTable>().toMutableList()
//        imageWithTypeList.forEach {
//            imageTableList.add(
//                ImageTable(
//                    type = it.type,
//                    image = it.imageUrl,
//                    preview = it.previewUrl
//                )
//            )
//        }
//        return imageWithTypeList.toList()
//    }
}