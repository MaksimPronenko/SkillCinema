package edu.skillbox.skillcinema.presentation.film

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.skillbox.skillcinema.data.RepositoryFilmAndSerial
import edu.skillbox.skillcinema.data.RepositoryCollections
import edu.skillbox.skillcinema.models.collection.CollectionTable
import edu.skillbox.skillcinema.models.collection.InterestedTable
import edu.skillbox.skillcinema.models.collection.ViewedTable
import edu.skillbox.skillcinema.models.filmAndSerial.film.FilmDb
import edu.skillbox.skillcinema.models.filmAndSerial.film.FilmDbViewed
import edu.skillbox.skillcinema.models.filmAndSerial.film.FilmItemData
import edu.skillbox.skillcinema.models.filmAndSerial.image.ImageTable
import edu.skillbox.skillcinema.models.filmAndSerial.similar.SimilarFilmTable
import edu.skillbox.skillcinema.models.filmAndSerial.staff.StaffTable
import edu.skillbox.skillcinema.presentation.ViewModelState
import edu.skillbox.skillcinema.utils.Converters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

private const val TAG = "Film.VM"

class FilmViewModel(
    private val repositoryFilmAndSerial: RepositoryFilmAndSerial,
    private val repositoryCollections: RepositoryCollections,
    private val converters: Converters
) : ViewModel() {

    private val _state = MutableStateFlow<ViewModelState>(
        ViewModelState.Loading
    )
    val state = _state.asStateFlow()

    var filmId: Int = 0

    var imdbId: String? = null

    var name: String = ""
    var poster: String = ""
    var posterSmall: String = ""
    var rating: Float? = null
    var year: Int? = null

    private var length: Int? = null
    private var filmLength: String? = null

    private var ratingAgeLimits: String? = null
    private var ageLimit: String? = null

    var shortDescription: String? = null
    var description: String? = null

    var shortDescriptionCollapsed = true
    var descriptionCollapsed = true

    var countries: String? = null
    private var countryList: List<String> = emptyList()

    var genres: String? = null
    private var genreList: List<String> = emptyList()

    var actorsList: List<StaffTable> = emptyList()
    var actorsQuantity = 0
    var staffList: List<StaffTable> = emptyList()
    var staffQuantity = 0

    var imageTableList: List<ImageTable>? = null
    var gallerySize = 0

    private var similarFilmTableList: List<SimilarFilmTable>? = null
    var similarsQuantity = 0
    private var similars: MutableList<FilmItemData> = mutableListOf()
    private val _similarsFlow = MutableStateFlow<List<FilmItemData>>(emptyList())
    val similarsFlow = _similarsFlow.asStateFlow()

    var favorite = false
    private val _favoriteChannel = Channel<Boolean>()
    val favoriteChannel = _favoriteChannel.receiveAsFlow()

    private var wantedToWatch = false
    private val _wantedToWatchChannel = Channel<Boolean>()
    val wantedToWatchChannel = _wantedToWatchChannel.receiveAsFlow()

    var viewed = false
    private val _viewedChannel = Channel<Boolean>()
    val viewedChannel = _viewedChannel.receiveAsFlow()

    private var jobLoadSimilarFilmsData: Job? = null

    fun loadFilmData(filmId: Int) {
        Log.d(TAG, "Запущена loadFilmData($filmId)")
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = ViewModelState.Loading
            var filmDbViewedResult: Pair<FilmDbViewed?, Boolean>
            var filmDbViewed: FilmDbViewed?
            var filmDb: FilmDb?
            var error = false

            val jobGetMainFilmData = viewModelScope.launch(Dispatchers.IO) {
                filmDbViewedResult = repositoryFilmAndSerial.getFilmDbViewed(filmId)
                filmDbViewed = filmDbViewedResult.first
                error = filmDbViewedResult.second
                filmDb = filmDbViewed?.filmDb

                viewed = filmDbViewed?.viewed ?: false
                _viewedChannel.send(element = viewed)
                Log.d(TAG, "Просмотрен: $viewed")

                favorite = repositoryCollections.isFilmExistsInCollection(filmId, "Любимое")
                _favoriteChannel.send(element = favorite)
                Log.d(TAG, "Коллекция \"Любимое\": $favorite")

                wantedToWatch = repositoryCollections.isFilmExistsInCollection(filmId, "Хочу посмотреть")
                _wantedToWatchChannel.send(element = wantedToWatch)
                Log.d(TAG, "Коллекция \"Хочу посмотреть\": $wantedToWatch")

                if (filmDb != null) {
                    imdbId = filmDb!!.filmTable.imdbId
                    name = filmDb!!.filmTable.name
                    poster = filmDb!!.filmTable.poster
                    posterSmall = filmDb!!.filmTable.posterSmall
                    rating = filmDb!!.filmTable.rating
                    year = filmDb!!.filmTable.year

                    length = filmDb!!.filmTable.length
                    filmLength = converters.convertLength(length)

                    ratingAgeLimits = filmDb!!.filmTable.ratingAgeLimits
                    ageLimit = converters.convertAgeLimit(ratingAgeLimits)

                    shortDescription = filmDb!!.filmTable.shortDescription
                    description = filmDb!!.filmTable.description

                    countryList = converters.convertClassListToStringList(filmDb!!.countries)
                    countries = converters.convertStringListToString(countryList)

                    genreList = converters.convertClassListToStringList(filmDb!!.genres)
                    genres = converters.convertStringListToString(genreList)
                } else error = true

                // Загрузка данных актёров и персонала фильма из БД или из API с записью в БД
                val allStaffTableListLoadResult: Pair<List<StaffTable>?, Boolean> = repositoryFilmAndSerial.getStaffTableList(filmId)
                val allStaffTableList: List<StaffTable>? = allStaffTableListLoadResult.first
                if (allStaffTableListLoadResult.second) {
                    Log.d(TAG, "Ошибка загрузки List<StaffTable>")
                }
                if (allStaffTableList != null) {
                    val actorsAndStaff = repositoryFilmAndSerial.divideStaffByType(allStaffTableList)
                    actorsList = actorsAndStaff.actorsList
                    actorsQuantity = actorsList.size
                    staffList = actorsAndStaff.staffList
                    staffQuantity = staffList.size
                }

                // Загрузка галереи фильма из БД или из API с записью в БД
                val allGalleryLoadResult: Pair<List<ImageTable>?, Boolean> = repositoryFilmAndSerial.getAllGallery(filmId)
                imageTableList = allGalleryLoadResult.first
                if (allGalleryLoadResult.second) {
                    Log.d(TAG, "Ошибка загрузки List<ImageTable>")
                }
                gallerySize = imageTableList?.size ?: 0

                // Загрузка похожих фильмов из БД или из API с записью в БД
                val similarsLoadResult: Pair<List<SimilarFilmTable>?, Boolean> = repositoryFilmAndSerial.getSimilarFilmTableList(filmId)
                similarFilmTableList = similarsLoadResult.first
                if (similarsLoadResult.second) {
                    Log.d(TAG, "Ошибка загрузки List<SimilarFilmTable>")
                }
                similarsQuantity = similarFilmTableList?.size ?: 0
            }
            jobGetMainFilmData.join()

            if (error) {
                _state.value = ViewModelState.Error
                Log.d(TAG, "Состояние ошибки VM")
            } else {
                _state.value = ViewModelState.Loaded
                Log.d(TAG, "Состояние уcпешного завершения загрузки VM")

                Log.d(TAG, "Запускаем loadSimilarFilmsData() из loadFilmData()")
                loadSimilarFilmsData()

                // Запись в список "Вам было интересно"
                repositoryCollections.addInterested(
                    InterestedTable(
                        id = filmId,
                        type = 0
                    )
                )
            }
        }
    }

    fun loadSimilarFilmsData() {
        if (jobLoadSimilarFilmsData?.isActive != true) {
            jobLoadSimilarFilmsData = viewModelScope.launch(Dispatchers.IO) {
                similars = mutableListOf()
                Log.d(TAG, "loadSimilarFilmsData(). Внутри Job.")
                similarFilmTableList?.forEach { similarFilmTable ->
                    val filmDbViewed =
                        repositoryFilmAndSerial.getFilmDbViewed(similarFilmTable.similarFilmId).first
                    if (filmDbViewed != null) {
                        val filmItemData: FilmItemData = filmDbViewed.convertToFilmItemData()
                        similars.add(filmItemData)
                        _similarsFlow.value = similars.toList()
                    }
                }
            }
        }
    }

    fun onCollectionButtonClick(collectionName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val filmExistsInCollection = repositoryCollections.isFilmExistsInCollection(filmId, collectionName)
            if (filmExistsInCollection) {
                repositoryCollections.removeCollectionTable(filmId, collectionName)
            } else {
                repositoryCollections.addCollectionTable(
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
            val filmViewed = repositoryCollections.isFilmExistsInViewed(filmId)
            if (filmViewed) {
                repositoryCollections.removeViewedFilm(filmId)
            } else {
                repositoryCollections.addViewedFilm(ViewedTable(filmId = filmId))
            }
            viewed = !filmViewed
            _viewedChannel.send(element = viewed)
            Log.d(TAG, "Просмотрен: $viewed")
        }
    }

    fun checkFilmInCollections() {
        viewModelScope.launch(Dispatchers.IO) {
            favorite = repositoryCollections.isFilmExistsInCollection(filmId, "Любимое")
            _favoriteChannel.send(element = favorite)
            wantedToWatch = repositoryCollections.isFilmExistsInCollection(filmId, "Хочу посмотреть")
            _wantedToWatchChannel.send(element = wantedToWatch)
            viewed = repositoryCollections.isFilmExistsInViewed(filmId)
            _viewedChannel.send(element = viewed)
            Log.d(TAG, "Коллекция \"Любимое\": $favorite")
            Log.d(TAG, "Коллекция \"Хочу посмотреть\": $wantedToWatch")
            Log.d(TAG, "Просмотрен: $viewed")
        }
    }

    fun getRatingAndNameString(): String {
        return if (rating != null)
            rating.toString() + ", " + name
        else
            name
    }

    fun getYearAndGenresString(): String {
        val yearPart = if (year == null) "" else year.toString() + ", "
        return yearPart + genres
    }

    fun getCountriesAndLengthAndAgeLimitString(): String = countries.toString() +
            if (filmLength != null) {
                ", " + filmLength.toString()
            } else {
                ""
            } +
            if (ageLimit != null) {
                ", " + ageLimit.toString()
            } else {
                ""
            }
}