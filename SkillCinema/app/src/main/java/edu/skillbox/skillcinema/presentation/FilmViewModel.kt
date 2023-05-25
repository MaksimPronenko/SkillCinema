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

private const val TAG = "FilmVM"

class FilmViewModel(
    private val repository: Repository
) : ViewModel() {

//class FilmViewModel (
//    private val repository: Repository,
//    application: App
//) : AndroidViewModel(application) {

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

//    private val _actors = MutableStateFlow<List<StaffInfo>>(emptyList())
//    val actors = _actors.asStateFlow()
//    private val _staff = MutableStateFlow<List<StaffInfo>>(emptyList())
//    val staff = _staff.asStateFlow()
//    private val _gallery = MutableStateFlow<List<ImageWithType>>(emptyList())
//    val gallery = _gallery.asStateFlow()
//    private val _similars = MutableStateFlow<List<SimilarFilm>>(emptyList())
//    val similars = _similars.asStateFlow()

    fun loadFilmInfo(filmId: Int) {
        Log.d(TAG, "Запущена loadFilmInfo($filmId)")
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = ViewModelState.Loading
            var error = false

            var filmFoundInDb = false
            val jobCheckingFilmInfoDb = viewModelScope.launch(Dispatchers.IO) {
                filmFoundInDb = repository.isFilmDataExists(filmId)
            }
            jobCheckingFilmInfoDb.join()
            Log.d(TAG, "Наличие фильма в БД: $filmFoundInDb")

            if (filmFoundInDb) {
                var filmInfoDb: FilmInfoDb? = null
                val getFilmInfoDbJob = viewModelScope.launch {
                    filmInfoDb = repository.getFilmInfoDb(filmId)
                    Log.d(TAG, "Загружена из БД FilmInfoDb (до join): $filmInfoDb")
                }
                getFilmInfoDbJob.join()
                Log.d(TAG, "Загружена из БД FilmInfoDb (после join): $filmInfoDb")

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

                    countryList = repository.convertClassListToStringList(filmInfoDb!!.countries)
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
                val getDataFromApiJob = viewModelScope.launch {
                    kotlin.runCatching {
                        repository.getFilmInfo(filmId)
                    }.fold(
                        onSuccess = {
                            name = it.nameRu ?: it.nameEn ?: it.nameOriginal ?: ""
                            poster = it.posterUrl ?: ""
                            posterSmall = it.posterUrlPreview ?: ""
                            rating = it.ratingKinopoisk
                            year = it.year

                            length = it.filmLength
                            filmLength = repository.convertLength(length)

                            ratingAgeLimits = it.ratingAgeLimits
                            ageLimit = repository.convertAgeLimit(ratingAgeLimits)

                            shortDescription = it.shortDescription
                            description = it.description

                            countryList = repository.convertClassListToStringList(it.countries)
                            countries = repository.convertStringListToString(countryList)

                            genreList = repository.convertClassListToStringList(it.genres)
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
                        },
                        onFailure = {
                            Log.d(TAG, "Похожие фильмы. Ошибка загрузки. ${it.message ?: ""}")
                            error = true
                        }
                    )
                }
                getDataFromApiJob.join()

                // Запись данных фильма в базу данных после загрузки из Api

                val addFilmDataToDbJob = viewModelScope.launch {
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
                addFilmDataToDbJob.join()
                Log.d(TAG, "Закончена запись данных фильма в базу данных")
            }

            favorite = repository.isFilmExistsInCollection(filmId, "Любимое")
            _favoriteChannel.send(element = favorite)
            wantedToWatch = repository.isFilmExistsInCollection(filmId, "Хочу посмотреть")
            _wantedToWatchChannel.send(element = wantedToWatch)
            Log.d(TAG, "Коллекция \"Любимое\": $favorite")
            Log.d(TAG, "Коллекция \"Хочу посмотреть\": $wantedToWatch")

            if (error) {
                _state.value = ViewModelState.Error
                Log.d(TAG, "Состояние ошибки VM")
            } else {
                _state.value = ViewModelState.Loaded
                Log.d(TAG, "Состояние уcпешного завершения загрузки VM")
            }
        }
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
            }
            else if (collectionName == "Хочу посмотреть"){
                wantedToWatch = !filmExistsInCollection
                _wantedToWatchChannel.send(element = wantedToWatch)
            }
        }
    }

//    private fun checkFilmInCollections() {
//        viewModelScope.launch(Dispatchers.IO) {
//            favorite = repository.isFilmExistsInCollection(filmId, "Любимое")
//            _favoriteChannel.send(element = favorite)
//            wantedToWatch = repository.isFilmExistsInCollection(filmId, "Хочу посмотреть")
//            _wantedToWatchChannel.send(element = wantedToWatch)
//            Log.d(TAG,"Коллекция \"Любимое\": $favorite")
//            Log.d(TAG,"Коллекция \"Хочу посмотреть\": $wantedToWatch")
//        }
//    }
}