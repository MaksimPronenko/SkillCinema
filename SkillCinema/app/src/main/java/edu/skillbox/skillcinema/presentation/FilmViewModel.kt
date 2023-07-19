package edu.skillbox.skillcinema.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.skillbox.skillcinema.data.Repository
import edu.skillbox.skillcinema.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

private const val TAG = "Film.VM"

class FilmViewModel(
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

    var actorsList: List<StaffTable> = emptyList()
    var actorsQuantity = 0
    var staffList: List<StaffTable> = emptyList()
    var staffQuantity = 0

    var imageTableList: List<ImageTable>? = null
    var gallerySize = 0

    var similarFilmTableList: List<SimilarFilmTable>? = null
    var similarsQuantity = 0
    var similars: MutableList<FilmItemData> = mutableListOf()
    private val _similarsFlow = MutableStateFlow<List<FilmItemData>>(emptyList())
    val similarsFlow = _similarsFlow.asStateFlow()

    var favorite = false
    private val _favoriteChannel = Channel<Boolean>()
    val favoriteChannel = _favoriteChannel.receiveAsFlow()

    var wantedToWatch = false
    private val _wantedToWatchChannel = Channel<Boolean>()
    val wantedToWatchChannel = _wantedToWatchChannel.receiveAsFlow()

    var viewed = false
    private val _viewedChannel = Channel<Boolean>()
    val viewedChannel = _viewedChannel.receiveAsFlow()

    var jobLoadSimilarFilmsData: Job? = null

    fun loadFilmData(filmId: Int) {
        Log.d(TAG, "Запущена loadFilmData($filmId)")
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = ViewModelState.Loading
            var filmDbViewedResult: Pair<FilmDbViewed?, Boolean>
            var filmDbViewed: FilmDbViewed?
            var filmDb: FilmDb?
            var error = false

            val jobGetMainFilmData = viewModelScope.launch(Dispatchers.IO) {
                filmDbViewedResult = repository.getFilmDbViewed(filmId)
                filmDbViewed = filmDbViewedResult.first
                error = filmDbViewedResult.second
                filmDb = filmDbViewed?.filmDb

                viewed = filmDbViewed?.viewed ?: false
                _viewedChannel.send(element = viewed)
                Log.d(TAG, "Просмотрен: $viewed")

                favorite = repository.isFilmExistsInCollection(filmId, "Любимое")
                _favoriteChannel.send(element = favorite)
                Log.d(TAG, "Коллекция \"Любимое\": $favorite")

                wantedToWatch = repository.isFilmExistsInCollection(filmId, "Хочу посмотреть")
                _wantedToWatchChannel.send(element = wantedToWatch)
                Log.d(TAG, "Коллекция \"Хочу посмотреть\": $wantedToWatch")

                if (filmDb != null) {
                    name = filmDb!!.filmTable.name
                    poster = filmDb!!.filmTable.poster
                    posterSmall = filmDb!!.filmTable.posterSmall
                    rating = filmDb!!.filmTable.rating
                    year = filmDb!!.filmTable.year

                    length = filmDb!!.filmTable.length
                    filmLength = repository.convertLength(length)

                    ratingAgeLimits = filmDb!!.filmTable.ratingAgeLimits
                    ageLimit = repository.convertAgeLimit(ratingAgeLimits)

                    shortDescription = filmDb!!.filmTable.shortDescription
                    description = filmDb!!.filmTable.description

                    countryList = repository.convertClassListToStringList(filmDb!!.countries)
                    countries = repository.convertStringListToString(countryList)

                    genreList = repository.convertClassListToStringList(filmDb!!.genres)
                    genres = repository.convertStringListToString(genreList)
                } else error = true

                // Загрузка данных актёров и персонала фильма из БД или из API с записью в БД
                val allStaffTableListLoadResult: Pair<List<StaffTable>?, Boolean> = repository.getStaffTableList(filmId)
                val allStaffTableList: List<StaffTable>? = allStaffTableListLoadResult.first
                if (allStaffTableListLoadResult.second) {
                    Log.d(TAG, "Ошибка загрузки List<StaffTable>")
                }
                if (allStaffTableList != null) {
                    val actorsAndStaff = repository.divideStaffByType(allStaffTableList)
                    actorsList = actorsAndStaff.actorsList
                    actorsQuantity = actorsList.size
                    staffList = actorsAndStaff.staffList
                    staffQuantity = staffList.size
                }

                // Загрузка галереи фильма из БД или из API с записью в БД
                val allGalleryLoadResult: Pair<List<ImageTable>?, Boolean> = repository.getAllGallery(filmId)
                imageTableList = allGalleryLoadResult.first
                if (allGalleryLoadResult.second) {
                    Log.d(TAG, "Ошибка загрузки List<ImageTable>")
                }
                gallerySize = imageTableList?.size ?: 0

                // Загрузка похожих фильмов из БД или из API с записью в БД
                val similarsLoadResult: Pair<List<SimilarFilmTable>?, Boolean> = repository.getSimilarFilmTableList(filmId)
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
                repository.addInterested(
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
                        repository.getFilmDbViewed(similarFilmTable.similarFilmId).first
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
}