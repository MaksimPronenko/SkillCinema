package edu.skillbox.skillcinema.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.skillbox.skillcinema.data.Repository
import edu.skillbox.skillcinema.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

private const val TAG = "SerialVM"

class SerialViewModel(
    private val repository: Repository
) : ViewModel() {

    private val _state = MutableStateFlow<ViewModelState>(
        ViewModelState.Loading
    )
    val state = _state.asStateFlow()

    var filmId: Int = 0

    var name: String = ""
    var poster: String = ""
    var posterSmall: String = ""
    var rating: Float? = null
    var year: Int? = null

    var length: Int? = null
    var filmLength: String? = null

    var ratingAgeLimits: String? = null
    var ageLimit: String? = null

    var shortDescription: String? = null
    var description: String? = null

    var shortDescriptionCollapsed = true
    var descriptionCollapsed = true

    var countries: String? = null
    var countryList: List<String> = emptyList()

    var genres: String? = null
    var genreList: List<String> = emptyList()

    var quantityOfSeasons = 0
    var quantityOfEpisodes = 0
    var seasonsInformation: String = ""
    var serialInformation: String = ""

    var allStaffList: List<StaffInfo> = emptyList()
    var actorsList: List<StaffInfo> = emptyList()
    var actorsQuantity = 0
    var staffList: List<StaffInfo> = emptyList()
    var staffQuantity = 0

    var gallerySize = 0
    var imageWithTypeList: List<ImageWithType> = emptyList()

    var similarFilmList: List<SimilarFilm> = emptyList()
    var similarsQuantity = 0

    var favorite = false
    private val _favoriteChannel = Channel<Boolean>()
    val favoriteChannel = _favoriteChannel.receiveAsFlow()

    var wantedToWatch = false
    private val _wantedToWatchChannel = Channel<Boolean>()
    val wantedToWatchChannel = _wantedToWatchChannel.receiveAsFlow()

    var viewed = false
    private val _viewedChannel = Channel<Boolean>()
    val viewedChannel = _viewedChannel.receiveAsFlow()

    fun loadSerialData(filmId: Int) {
        Log.d(TAG, "loadSerialData($filmId)")
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = ViewModelState.Loading
            var error = false

            val jobFilmData = viewModelScope.launch(Dispatchers.IO) {
                var filmDataFoundInDb = false
                val jobCheckFilmDataInDb = viewModelScope.launch(Dispatchers.IO) {
                    filmDataFoundInDb = repository.isFilmDataExists(filmId)
                }
                jobCheckFilmDataInDb.join()
                Log.d(TAG, "Данные фильма в БД: $filmDataFoundInDb")

                if (filmDataFoundInDb) {
                    var filmInfoDb: FilmInfoDb? = null
                    val jobGetFilmInfoDb = viewModelScope.launch {
                        filmInfoDb = repository.getFilmInfoDb(filmId)
                    }
                    jobGetFilmInfoDb.join()
                    Log.d(TAG, "Загружена из БД FilmInfoDb: $filmInfoDb")

                    if (filmInfoDb == null) error = true
                    else {
                        name = filmInfoDb!!.filmTable.name
                        poster = filmInfoDb!!.filmTable.poster ?: ""
                        posterSmall = filmInfoDb!!.filmTable.posterSmall ?: ""
                        rating = filmInfoDb!!.filmTable.rating
                        year = filmInfoDb!!.filmTable.year

                        length = filmInfoDb!!.filmTable.length
                        filmLength = repository.convertLength(length)

                        ratingAgeLimits = filmInfoDb!!.filmTable.ratingAgeLimits
                        ageLimit = repository.convertAgeLimit(ratingAgeLimits)

                        shortDescription = filmInfoDb!!.filmTable.shortDescription
                        description = filmInfoDb!!.filmTable.description

                        countryList =
                            repository.convertClassListToStringList(filmInfoDb!!.countries)
                        countries = repository.convertStringListToString(countryList)

                        genreList = repository.convertClassListToStringList(filmInfoDb!!.genres)
                        genres = repository.convertStringListToString(genreList)

                        allStaffList =
                            repository.convertStaffTableListToStaffInfoList(filmInfoDb!!.staffList)
                        val actorsAndStaff = repository.divideStaffByType(allStaffList)
                        actorsList = actorsAndStaff.actorsList
                        actorsQuantity = actorsList.size
                        staffList = actorsAndStaff.staffList
                        staffQuantity = staffList.size

                        imageWithTypeList =
                            repository.convertImageTableListToImageWithTypeList(filmInfoDb!!.images)
                        gallerySize = imageWithTypeList.size

                        similarFilmList =
                            repository.convertSimilarFilmTableListToSimilarFilmList(filmInfoDb!!.similarFilms)
                        similarsQuantity = similarFilmList.size
                    }
                } else {
                    val jobGetFilmDataFromApi = viewModelScope.launch {
                        kotlin.runCatching {
                            repository.getFilmInfo(filmId)
                        }.fold(
                            onSuccess = {
                                name = it?.nameRu ?: it?.nameEn ?: it?.nameOriginal ?: ""
                                poster = it?.posterUrl ?: ""
                                posterSmall = it?.posterUrlPreview ?: ""
                                rating = it?.ratingKinopoisk
                                year = it?.year

                                length = it?.filmLength
                                filmLength = repository.convertLength(length)

                                ratingAgeLimits = it?.ratingAgeLimits
                                ageLimit = repository.convertAgeLimit(ratingAgeLimits)

                                shortDescription = it?.shortDescription
                                description = it?.description

                                if (it != null) {
                                    countryList = repository.convertClassListToStringList(it.countries)
                                }
                                countries = repository.convertStringListToString(countryList)

                                if (it != null) {
                                    genreList = repository.convertClassListToStringList(it.genres)
                                }
                                genres = repository.convertStringListToString(genreList)
                                Log.d(TAG, "Загружена из Api FilmInfo: $it")
                            },
                            onFailure = {
                                Log.d(
                                    TAG,
                                    "Информация о фильме из Api. Ошибка загрузки. ${it.message ?: ""}"
                                )
                                error = true
                            }
                        )

                        kotlin.runCatching {
                            repository.getAllStaffList(filmId)
                        }.fold(
                            onSuccess = {
                                allStaffList = it
                                val actorsAndStaff = repository.divideStaffByType(allStaffList)
                                actorsList = actorsAndStaff.actorsList
                                actorsQuantity = actorsList.size
                                staffList = actorsAndStaff.staffList
                                staffQuantity = staffList.size
                                Log.d(TAG, "Загружен из Api List<StaffInfo>: $it")
                            },
                            onFailure = {
                                Log.d(TAG, "Персонал. Ошибка загрузки. ${it.message ?: ""}")
                                error = true
                            }
                        )

                        kotlin.runCatching {
                            repository.getAllGallery(filmId)
                        }.fold(
                            onSuccess = {
                                imageWithTypeList = it
                                gallerySize = it.size
                                Log.d(TAG, "Загружен из Api List<ImageWithType>: $it")
                            },
                            onFailure = {
                                Log.d(TAG, "Галерея. Ошибка загрузки. ${it.message ?: ""}")
                                error = true
                            }
                        )

                        kotlin.runCatching {
                            repository.getSimilars(filmId)
                        }.fold(
                            onSuccess = {
                                similarFilmList = it.items
                                similarsQuantity = it.total
                                Log.d(TAG, "Загружен из Api SimilarFilmList: $it")
                            },
                            onFailure = {
                                Log.d(TAG, "Похожие фильмы. Ошибка загрузки. ${it.message ?: ""}")
                                error = true
                            }
                        )
                    }
                    jobGetFilmDataFromApi.join()

                    // Запись данных фильма в базу данных после загрузки из Api
                    val jobAddFilmDataToDb = viewModelScope.launch {
                        repository.addFilmTable(
                            FilmTable(
                                filmId = filmId,
                                name = name,
                                poster = poster,
                                posterSmall = posterSmall,
                                rating = rating,
                                year = year,
                                length = length,
                                description = description,
                                shortDescription = shortDescription,
                                ratingAgeLimits = ratingAgeLimits
                            )
                        )
                        repository.addCountryTable(filmId, countryList)
                        repository.addGenreTable(filmId, genreList)
                        repository.addStaffTable(filmId, allStaffList)
                        repository.addImageTable(filmId, imageWithTypeList)
                        repository.addSimilarFilmTable(filmId, similarFilmList)
                    }
                    jobAddFilmDataToDb.join()
                    Log.d(TAG, "Данные фильма записаны в базу данных")
                }
            }

            val jobSerialData = viewModelScope.launch(Dispatchers.IO) {
                var serialDataFoundInDb = false
                var serialInfoDb: SerialInfoDb? = null
                val jobCheckSerialDataInDb = viewModelScope.launch(Dispatchers.IO) {
                    serialDataFoundInDb = repository.isSerialDataExists(filmId)
                }
                jobCheckSerialDataInDb.join()
                Log.d(TAG, "Данные сериала в БД: $serialDataFoundInDb")

                if (serialDataFoundInDb) {
                    val jobGetSerialInfoDb = viewModelScope.launch {
                        serialInfoDb = repository.getSerialInfoDb(filmId)
                    }
                    jobGetSerialInfoDb.join()
                    Log.d(TAG, "Загружена из БД SerialInfoDb: $serialInfoDb")

                    if (serialInfoDb == null) error = true
                    else {
                        serialInfoDbToVMData(serialInfoDb!!)
                    }
                } else {
                    val jobGetSerialDataFromApi = viewModelScope.launch {
                        kotlin.runCatching {
                            repository.getFromApiSerialInfoDb(filmId)
                        }.fold(
                            onSuccess = {
                                serialInfoDb = it
                                serialInfoDbToVMData(serialInfoDb!!)
                                Log.d(TAG, "Данные сериала загружены из Api: $serialInfoDb")
                            },
                            onFailure = {
                                Log.d(
                                    TAG,
                                    "Данные сериала из Api. Ошибка загрузки. ${it.message ?: ""}"
                                )
                                error = true
                            }
                        )
                    }
                    jobGetSerialDataFromApi.join()

                    // Запись данных сериала в базу данных после загрузки из Api
                    val jobAddSerialDataToDb = viewModelScope.launch {
                        serialInfoDb?.let { repository.addSerialInfoDb(it) }
                    }
                    jobAddSerialDataToDb.join()
                    Log.d(TAG, "Данные сериала записаны в базу данных")
                }
            }
            jobFilmData.join()
            jobSerialData.join()

            favorite = repository.isFilmExistsInCollection(filmId, "Любимое")
            _favoriteChannel.send(element = favorite)
            wantedToWatch = repository.isFilmExistsInCollection(filmId, "Хочу посмотреть")
            _wantedToWatchChannel.send(element = wantedToWatch)
            viewed = repository.isFilmExistsInViewed(filmId)
            _viewedChannel.send(element = viewed)
            Log.d(TAG, "Коллекция \"Любимое\": $favorite")
            Log.d(TAG, "Коллекция \"Хочу посмотреть\": $wantedToWatch")
            Log.d(TAG, "Просмотрен: $viewed")

            if (error) {
                _state.value = ViewModelState.Error
                Log.d(TAG, "Состояние ошибки VM")
            } else {
                _state.value = ViewModelState.Loaded
                Log.d(TAG, "Состояние умпешного завершения загрузки VM")

                // Запись в список "Вам было инетересно"
                repository.addInterested(
                    InterestedTable(
                        id = filmId,
                        type = 1
                    )
                )
            }
        }
    }

    private fun serialInfoDbToVMData(serialInfoDb: SerialInfoDb) {
        quantityOfSeasons = serialInfoDb.serialTable.totalSeasons
        quantityOfEpisodes = 0
        serialInfoDb.seasonsWithEpisodes.forEach { season ->
            quantityOfEpisodes += season.episodes.size
        }
        seasonsInformation = seasonQuantityToText(quantityOfSeasons)
        serialInformation =
            "$seasonsInformation, " + episodeQuantityToText(
                quantityOfEpisodes
            )
    }

    fun onCollectionButtonClick(collectionName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val filmExistsInCollection = repository.isFilmExistsInCollection(filmId, collectionName)
            if (filmExistsInCollection) {
                repository.removeCollectionTable(filmId, collectionName)
            } else {
                repository.addCollectionTable(
                    CollectionTable(
                        collection = collectionName,
                        filmId = filmId
                    )
                )
            }
            if (collectionName == "Любимое") {
                favorite = !filmExistsInCollection
                _favoriteChannel.send(element = favorite)
                Log.d(TAG, "Коллекция \"Любимое\": $favorite")
            } else if (collectionName == "Хочу посмотреть") {
                wantedToWatch = !filmExistsInCollection
                _wantedToWatchChannel.send(element = wantedToWatch)
                Log.d(TAG, "Коллекция \"Хочу посмотреть\": $wantedToWatch")
            }
        }
    }

    fun onViewedButtonClick() {
        viewModelScope.launch(Dispatchers.IO) {
            val filmViewed = repository.isFilmExistsInViewed(filmId)
            if (filmViewed) {
                repository.removeViewedFilm(filmId)
            } else {
                repository.addViewedFilm(ViewedTable(filmId = filmId))
            }
            viewed = !filmViewed
            _viewedChannel.send(element = viewed)
            Log.d(TAG, "Просмотрен: $viewed")
        }
    }

    fun checkFilmInCollections() {
        viewModelScope.launch(Dispatchers.IO) {
            favorite = repository.isFilmExistsInCollection(filmId, "Любимое")
            _favoriteChannel.send(element = favorite)
            wantedToWatch = repository.isFilmExistsInCollection(filmId, "Хочу посмотреть")
            _wantedToWatchChannel.send(element = wantedToWatch)
            viewed = repository.isFilmExistsInViewed(filmId)
            _viewedChannel.send(element = viewed)
            Log.d(TAG, "Коллекция \"Любимое\": $favorite")
            Log.d(TAG, "Коллекция \"Хочу посмотреть\": $wantedToWatch")
            Log.d(TAG, "Просмотрен: $viewed")
        }
    }

    private fun seasonQuantityToText(quantity: Int): String {
        val remOfDivBy10 = quantity % 10
        val remOfDivBy100 = quantity % 100
        return "$quantity сезон" + when (remOfDivBy10) {
            1 -> if (remOfDivBy100 == 11) "ов" else ""
            in 2..4 -> if (remOfDivBy100 == 12) "ов" else "а"
            else -> "ов"
        }
    }

    private fun episodeQuantityToText(quantity: Int): String {
        val remOfDivBy10 = quantity % 10
        val remOfDivBy100 = quantity % 100
        return "$quantity сери" + when (remOfDivBy10) {
            1 -> if (remOfDivBy100 == 11) "й" else "я"
            in 2..4 -> if (remOfDivBy100 == 12) "й" else "и"
            else -> "й"
        }
    }
}