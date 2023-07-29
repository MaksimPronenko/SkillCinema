package edu.skillbox.skillcinema.data

import android.util.Log
import edu.skillbox.skillcinema.api.retrofit
import edu.skillbox.skillcinema.data.ApyKeys.changeApiKey
import edu.skillbox.skillcinema.data.ApyKeys.currentApiKey
import edu.skillbox.skillcinema.models.filmAndSerial.country.CountryTable
import edu.skillbox.skillcinema.models.filmAndSerial.film.FilmDb
import edu.skillbox.skillcinema.models.filmAndSerial.film.FilmDbViewed
import edu.skillbox.skillcinema.models.filmAndSerial.film.FilmInfo
import edu.skillbox.skillcinema.models.filmAndSerial.film.FilmTable
import edu.skillbox.skillcinema.models.filmAndSerial.genre.GenreTable
import edu.skillbox.skillcinema.models.filmAndSerial.image.ImageTable
import edu.skillbox.skillcinema.models.filmAndSerial.image.PagedImages
import edu.skillbox.skillcinema.models.filmAndSerial.serial.*
import edu.skillbox.skillcinema.models.filmAndSerial.similar.SimilarFilmList
import edu.skillbox.skillcinema.models.filmAndSerial.similar.SimilarFilmTable
import edu.skillbox.skillcinema.models.filmAndSerial.staff.ActorsAndStaff
import edu.skillbox.skillcinema.models.filmAndSerial.staff.StaffInfo
import edu.skillbox.skillcinema.models.filmAndSerial.staff.StaffTable
import edu.skillbox.skillcinema.utils.ImageType
import javax.inject.Inject

private const val TAG = "RepositoryFilmAndSerial"

class RepositoryFilmAndSerial @Inject constructor(private val dao: FilmDao) {
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
                    imdbId = filmInfo.imdbId,
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
            ImageType.STILL.name,
            ImageType.SHOOTING.name,
            ImageType.POSTER.name,
            ImageType.FAN_ART.name,
            ImageType.PROMO.name,
            ImageType.CONCEPT.name,
            ImageType.WALLPAPER.name,
            ImageType.COVER.name,
            ImageType.SCREENSHOT.name
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
                ImageType.STILL.name,
                ImageType.SHOOTING.name,
                ImageType.POSTER.name,
                ImageType.FAN_ART.name,
                ImageType.PROMO.name,
                ImageType.CONCEPT.name,
                ImageType.WALLPAPER.name,
                ImageType.COVER.name,
                ImageType.SCREENSHOT.name
            )
            1 -> listOf(ImageType.STILL.name)
            2 -> listOf(ImageType.SHOOTING.name)
            3 -> listOf(ImageType.POSTER.name)
            4 -> listOf(ImageType.FAN_ART.name)
            5 -> listOf(ImageType.PROMO.name)
            6 -> listOf(ImageType.CONCEPT.name)
            7 -> listOf(ImageType.WALLPAPER.name)
            8 -> listOf(ImageType.COVER.name)
            else -> listOf(ImageType.SCREENSHOT.name)
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

    // Запрос на добавление персонала фильма
    private suspend fun addStaffTableList(staffTableList: List<StaffTable>) {
        staffTableList.forEach { staffTable -> dao.addStaffTable(staffTable) }
    }

    // Запрос на добавление галереи фильма
    private suspend fun addImageTableList(imageTableList: List<ImageTable>) {
        imageTableList.forEach { imageWithType -> dao.addImageTable(imageWithType) }
    }

    // Запрос на добавление похожих фильмов
    private suspend fun addSimilarFilmTable(similarFilmTableList: List<SimilarFilmTable>) {
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
    private suspend fun addSerialInfoDb(serialInfoDb: SerialInfoDb) {
        dao.addSerialTable(serialInfoDb.serialTable)
        serialInfoDb.seasonsWithEpisodes.forEach { seasonWithEpisodes ->
            dao.addSeasonTable(seasonWithEpisodes.seasonTable)
            seasonWithEpisodes.episodes.forEach { episode ->
                dao.addEpisodeTable(episode)
            }
        }
    }
}