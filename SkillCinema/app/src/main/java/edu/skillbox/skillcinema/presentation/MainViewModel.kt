package edu.skillbox.skillcinema.presentation

import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.skillbox.skillcinema.App
import edu.skillbox.skillcinema.data.Repository
import edu.skillbox.skillcinema.models.FilmItemData
import edu.skillbox.skillcinema.models.FilmPremiere
import edu.skillbox.skillcinema.models.PagedFilmFilteredList
import edu.skillbox.skillcinema.models.PagedFilmTopList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "Main.VM"

//class MainViewModel @Inject constructor(
//    private val repository: Repository,
//    private val filmTop100PopularPagingSource: FilmsTop100PopularPagingSource,
//    private val filmTop250PagingSource: FilmsTop250PagingSource,
//    private val seriesPagingSource: SeriesPagingSource,
//    private val filmsFiltered1PagingSource: FilmsFiltered1PagingSource,
//    private val filmsFiltered2PagingSource: FilmsFiltered2PagingSource,
//    private val application: App
//) : AndroidViewModel(application) {

class MainViewModel(
    private val repository: Repository,
//    private val filmTop100PopularPagingSource: FilmsTop100PopularPagingSource,
//    private val filmTop250PagingSource: FilmsTop250PagingSource,
//    private val serialsPagingSource: SerialsPagingSource,
//    private val filmsFiltered1PagingSource: FilmsFilteredPagingSource,
//    private val filmsFiltered2PagingSource: FilmsFilteredPagingSource,
    application: App
) : AndroidViewModel(application) {

//class MainViewModel @Inject constructor(
//    private val repository: Repository,
//    application: App
//) : AndroidViewModel(application) {

    private val _state = MutableStateFlow<ViewModelState>(
        ViewModelState.Loading
    )
    val state = _state.asStateFlow()

    val genre1Key: Int = application.genre1key
    val country1Key: Int = application.country1key
    val genre2Key: Int = application.genre2key
    val country2Key: Int = application.country2key

    var premieresQuantity = 0
    var premieres: List<FilmPremiere> = emptyList()
    var premieresExtended: MutableList<FilmItemData> = mutableListOf()
    private val _premieresExtendedFlow = MutableStateFlow<List<FilmItemData>>(emptyList())
    val premieresExtendedFlow = _premieresExtendedFlow.asStateFlow()
//    private val _premieres = MutableStateFlow<List<FilmPremiere>>(emptyList())
//    val premieres = _premieres.asStateFlow()

    var top100PopularPagesQuantity = 0
    var top100Popular: PagedFilmTopList? = null
    var top100PopularExtended: MutableList<FilmItemData> = mutableListOf()
    private val _top100PopularExtendedFlow = MutableStateFlow<List<FilmItemData>>(emptyList())
    val top100PopularExtendedFlow = _top100PopularExtendedFlow.asStateFlow()

//    val pagedFilmsTop100Popular: Flow<PagingData<FilmTop>> = Pager(
//        config = PagingConfig(pageSize = 20, prefetchDistance = 0, maxSize = 20),
//        pagingSourceFactory = { filmTop100PopularPagingSource }
//    ).flow.cachedIn(viewModelScope)
//    val pagedFilmsTop100PopularExtended: Flow<PagingData<FilmItemData>> = Pager(
//        config = PagingConfig(pageSize = 20, prefetchDistance = 0, maxSize = 20),
//        pagingSourceFactory = { filmTop100PopularPagingSource }
//    ).flow.cachedIn(viewModelScope)

    var top250PagesQuantity = 0
    var top250: PagedFilmTopList? = null
    var top250Extended: MutableList<FilmItemData> = mutableListOf()
    private val _top250ExtendedFlow = MutableStateFlow<List<FilmItemData>>(emptyList())
    val top250ExtendedFlow = _top250ExtendedFlow.asStateFlow()

    //    val pagedFilmsTop250: Flow<PagingData<FilmTop>> = Pager(
//        config = PagingConfig(pageSize = 20, prefetchDistance = 0, maxSize = 20),
//        pagingSourceFactory = { filmTop250PagingSource }
//    ).flow.cachedIn(viewModelScope)
//    val pagedFilmsTop250Extended: Flow<PagingData<FilmItemData>> = Pager(
//        config = PagingConfig(pageSize = 20, prefetchDistance = 0, maxSize = 20),
//        pagingSourceFactory = { filmTop250PagingSource }
//    ).flow.cachedIn(viewModelScope)

    var serialsPagesQuantity = 0

    //    val pagedSeries: Flow<PagingData<FilmFiltered>> = Pager(
//        config = PagingConfig(pageSize = 20, prefetchDistance = 0, maxSize = 20),
//        pagingSourceFactory = { seriesPagingSource }
//    ).flow.cachedIn(viewModelScope)
    var serials: PagedFilmFilteredList? = null
    var serialsExtended: MutableList<FilmItemData> = mutableListOf()
    private val _serialsExtendedFlow = MutableStateFlow<List<FilmItemData>>(emptyList())
    val serialsExtendedFlow = _serialsExtendedFlow.asStateFlow()

    var filmsFiltered1PagesQuantity = 0

    //    val pagedFilmsFiltered1: Flow<PagingData<FilmFiltered>> = Pager(
//        config = PagingConfig(pageSize = 20, prefetchDistance = 0, maxSize = 20),
//        pagingSourceFactory = { filmsFiltered1PagingSource }
//    ).flow.cachedIn(viewModelScope)
    var filmsFiltered1: PagedFilmFilteredList? = null
    var filmsFiltered1Extended: MutableList<FilmItemData> = mutableListOf()
    private val _filmsFiltered1ExtendedFlow = MutableStateFlow<List<FilmItemData>>(emptyList())
    val filmsFiltered1ExtendedFlow = _filmsFiltered1ExtendedFlow.asStateFlow()

    var filmsFiltered2PagesQuantity = 0

    //    val pagedFilmsFiltered2: Flow<PagingData<FilmFiltered>> = Pager(
//        config = PagingConfig(pageSize = 20, prefetchDistance = 0, maxSize = 20),
//        pagingSourceFactory = { filmsFiltered2PagingSource }
//    ).flow.cachedIn(viewModelScope)
    var filmsFiltered2: PagedFilmFilteredList? = null
    var filmsFiltered2Extended: MutableList<FilmItemData> = mutableListOf()
    private val _filmsFiltered2ExtendedFlow = MutableStateFlow<List<FilmItemData>>(emptyList())
    val filmsFiltered2ExtendedFlow = _filmsFiltered2ExtendedFlow.asStateFlow()

    init {
        loadFilms()
    }

    private fun loadFilms() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = ViewModelState.Loading
            var error = false

            val premieresLoadResult = repository.getPremieresList()
            premieres = premieresLoadResult.first
            premieresQuantity = premieres.size
            if (premieresLoadResult.second) error = true

            val top100PopularLoadResult = repository.getTopList(type = "TOP_100_POPULAR_FILMS", page = 1)
            top100Popular = top100PopularLoadResult.first
            top100PopularPagesQuantity = top100Popular?.pagesCount ?: 0
            if (top100PopularLoadResult.second) error = true

            val top250LoadResult = repository.getTopList(type = "TOP_250_BEST_FILMS", page = 1)
            top250 = top250LoadResult.first
            top250PagesQuantity = top250?.pagesCount ?: 0
            if (top250LoadResult.second) error = true

            val serialsLoadResult = repository.getSerials(page = 1)
            serials = serialsLoadResult.first
            serialsPagesQuantity = serials?.totalPages ?: 0
            if (serialsLoadResult.second) error = true

            val filmsFiltered1LoadResult = repository.getFilmsFilteredClearedFromNullRating(genre = genre1Key, country = country1Key, page = 1)
            filmsFiltered1 = filmsFiltered1LoadResult.first
            filmsFiltered1PagesQuantity = filmsFiltered1?.totalPages ?: 0
            if (filmsFiltered1LoadResult.second) error = true

            val filmsFiltered2LoadResult = repository.getFilmsFilteredClearedFromNullRating(genre = genre2Key, country = country2Key, page = 1)
            filmsFiltered2 = filmsFiltered2LoadResult.first
            filmsFiltered2PagesQuantity = filmsFiltered2?.totalPages ?: 0
            if (filmsFiltered2LoadResult.second) error = true

            if (error) {
                _state.value = ViewModelState.Error
                Log.d(TAG, "Cостояние = ${_state.value}")
            } else {
                _state.value = ViewModelState.Loaded
                Log.d(TAG, "Cостояние = ${_state.value}")
                loadExtendedFilmData()
            }
        }
    }

    fun loadExtendedFilmData() {
        viewModelScope.launch(Dispatchers.IO) {
            premieresExtended = mutableListOf()
            premieres.forEach { filmPremiere ->
                val extendedPremiereData = repository.extendPremiereData(filmPremiere)
                if (extendedPremiereData != null) {
                    premieresExtended.add(extendedPremiereData)
                    _premieresExtendedFlow.value = premieresExtended.toList()
                }
            }

            top100PopularExtended = mutableListOf()
            top100Popular?.films?.forEach { filmTop ->
                val extendedTopFilmData = repository.extendTopFilmData(filmTop)
                if (extendedTopFilmData != null) {
                    top100PopularExtended.add(extendedTopFilmData)
                    _top100PopularExtendedFlow.value = top100PopularExtended.toList()
                }
            }
            Log.d(TAG, "Отправлен top100popular размера ${top100PopularExtended.size}")

            top250Extended = mutableListOf()
            top250?.films?.forEach { filmTop ->
                val extendedTopFilmData = repository.extendTopFilmData(filmTop)
                if (extendedTopFilmData != null) {
                    top250Extended.add(extendedTopFilmData)
                    _top250ExtendedFlow.value = top250Extended.toList()
                }
            }
            Log.d(TAG, "Отправлен top250 размера ${top250Extended.size}")

            serialsExtended = mutableListOf()
            serials?.items?.forEach { filmFiltered ->
                val extendedFilteredFilmData = repository.extendFilteredFilmData(filmFiltered)
                if (extendedFilteredFilmData != null) {
                    serialsExtended.add(extendedFilteredFilmData)
                    _serialsExtendedFlow.value = serialsExtended.toList()
                }
            }
            Log.d(TAG, "Отправлен serials размера ${serialsExtended.size}")

            filmsFiltered1Extended = mutableListOf()
            filmsFiltered1?.items?.forEach { filmFiltered ->
                val extendedFilteredFilmData = repository.extendFilteredFilmData(filmFiltered)
                if (extendedFilteredFilmData != null) {
                    filmsFiltered1Extended.add(extendedFilteredFilmData)
                    _filmsFiltered1ExtendedFlow.value = filmsFiltered1Extended.toList()
                }
            }
            Log.d(TAG, "Отправлен filmsFiltered1 размера ${serialsExtended.size}")

            filmsFiltered2Extended = mutableListOf()
            filmsFiltered2?.items?.forEach { filmFiltered ->
                val extendedFilteredFilmData = repository.extendFilteredFilmData(filmFiltered)
                if (extendedFilteredFilmData != null) {
                    filmsFiltered2Extended.add(extendedFilteredFilmData)
                    _filmsFiltered2ExtendedFlow.value = filmsFiltered2Extended.toList()
                }
            }
            Log.d(TAG, "Отправлен filmsFiltered2 размера ${serialsExtended.size}")
        }
    }

//    private suspend fun extendPagedTopToList(pagedFilmTopList: PagedFilmTopList?): List<FilmItemData> {
//        val topExtended: MutableList<FilmItemData> = mutableListOf()
//            pagedFilmTopList?.films?.forEach { filmTop ->
//                val extendedTopFilmData = repository.extendTopFilmData(filmTop)
//                if (extendedTopFilmData != null)
//                    topExtended.add(extendedTopFilmData)
//            }
//        return topExtended.toList()
//    }

//        viewModelScope.launch(Dispatchers.IO) {
//            kotlin.runCatching {
//            _state.value = MainViewModelState.MainLoading
//            repository.getPremieres(2023, "FEBRUARY")
//            repository.searchData()
//            }.fold(
//            onSuccess = {
//                _state.value = MainViewModelState.MainLoaded
//            },
//            onFailure = { _state.value = MainViewModelState.MainError }
//            )
//        }
}