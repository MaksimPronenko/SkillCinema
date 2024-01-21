package edu.skillbox.skillcinema.presentation.main

import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.skillbox.skillcinema.App
import edu.skillbox.skillcinema.data.RepositoryMainLists
import edu.skillbox.skillcinema.models.filmAndSerial.film.FilmItemData
import edu.skillbox.skillcinema.models.filmFiltered.PagedFilmFilteredList
import edu.skillbox.skillcinema.models.filmTop.PagedFilmTopList
import edu.skillbox.skillcinema.models.premiere.FilmPremiere
import edu.skillbox.skillcinema.presentation.ViewModelState
import edu.skillbox.skillcinema.utils.FilmTopType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "Main.VM"

class MainViewModel(
    private val repositoryMainLists: RepositoryMainLists,
    application: App
) : AndroidViewModel(application) {

    private val _state = MutableStateFlow<ViewModelState>(
        ViewModelState.Loading
    )
    val state = _state.asStateFlow()

    val genre1Key: Int = application.genre1key
    val country1Key: Int = application.country1key
    val genre2Key: Int = application.genre2key
    val country2Key: Int = application.country2key

    var premieresQuantity = 0
    private var premieres: List<FilmPremiere> = emptyList()
    private var premieresExtended: MutableList<FilmItemData> = mutableListOf()
    private val _premieresExtendedFlow = MutableStateFlow<List<FilmItemData>>(emptyList())
    val premieresExtendedFlow = _premieresExtendedFlow.asStateFlow()

    var top100PopularPagesQuantity = 0
    var top100Popular: PagedFilmTopList? = null
    private var top100PopularExtended: MutableList<FilmItemData> = mutableListOf()
    private val _top100PopularExtendedFlow = MutableStateFlow<List<FilmItemData>>(emptyList())
    val top100PopularExtendedFlow = _top100PopularExtendedFlow.asStateFlow()

    var top250PagesQuantity = 0
    var top250: PagedFilmTopList? = null
    private var top250Extended: MutableList<FilmItemData> = mutableListOf()
    private val _top250ExtendedFlow = MutableStateFlow<List<FilmItemData>>(emptyList())
    val top250ExtendedFlow = _top250ExtendedFlow.asStateFlow()

    var serialsPagesQuantity = 0

    var serials: PagedFilmFilteredList? = null
    private var serialsExtended: MutableList<FilmItemData> = mutableListOf()
    private val _serialsExtendedFlow = MutableStateFlow<List<FilmItemData>>(emptyList())
    val serialsExtendedFlow = _serialsExtendedFlow.asStateFlow()

    var filmsFiltered1PagesQuantity = 0

    var filmsFiltered1: PagedFilmFilteredList? = null
    private var filmsFiltered1Extended: MutableList<FilmItemData> = mutableListOf()
    private val _filmsFiltered1ExtendedFlow = MutableStateFlow<List<FilmItemData>>(emptyList())
    val filmsFiltered1ExtendedFlow = _filmsFiltered1ExtendedFlow.asStateFlow()

    var filmsFiltered2PagesQuantity = 0

    var filmsFiltered2: PagedFilmFilteredList? = null
    private var filmsFiltered2Extended: MutableList<FilmItemData> = mutableListOf()
    private val _filmsFiltered2ExtendedFlow = MutableStateFlow<List<FilmItemData>>(emptyList())
    val filmsFiltered2ExtendedFlow = _filmsFiltered2ExtendedFlow.asStateFlow()

    init {
        loadFilms()
    }

    private fun loadFilms() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = ViewModelState.Loading
            var error = false

            val premieresLoadResult = repositoryMainLists.getPremieresList()
            premieres = premieresLoadResult.first
            premieresQuantity = premieres.size
            if (premieresLoadResult.second) error = true

            val top100PopularLoadResult = repositoryMainLists.getTopList(type = FilmTopType.TOP_100_POPULAR_FILMS.name, page = 1)
            top100Popular = top100PopularLoadResult.first
            top100PopularPagesQuantity = top100Popular?.pagesCount ?: 0
            if (top100PopularLoadResult.second) error = true

            val top250LoadResult = repositoryMainLists.getTopList(type = FilmTopType.TOP_250_BEST_FILMS.name, page = 1)
            top250 = top250LoadResult.first
            top250PagesQuantity = top250?.pagesCount ?: 0
            if (top250LoadResult.second) error = true

            val serialsLoadResult = repositoryMainLists.getSerials(page = 1)
            serials = serialsLoadResult.first
            serialsPagesQuantity = serials?.totalPages ?: 0
            if (serialsLoadResult.second) error = true

            val filmsFiltered1LoadResult = repositoryMainLists.getFilmsFilteredClearedFromNullRating(genre = genre1Key, country = country1Key, page = 1)
            filmsFiltered1 = filmsFiltered1LoadResult.first
            filmsFiltered1PagesQuantity = filmsFiltered1?.totalPages ?: 0
            if (filmsFiltered1LoadResult.second) error = true

            val filmsFiltered2LoadResult = repositoryMainLists.getFilmsFilteredClearedFromNullRating(genre = genre2Key, country = country2Key, page = 1)
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
                val extendedPremiereData = repositoryMainLists.extendPremiereData(filmPremiere)
                if (extendedPremiereData != null) {
                    premieresExtended.add(extendedPremiereData)
                    _premieresExtendedFlow.value = premieresExtended.toList()
                }
            }

            top100PopularExtended = mutableListOf()
            top100Popular?.films?.forEach { filmTop ->
                val extendedTopFilmData = repositoryMainLists.extendTopFilmData(filmTop)
                if (extendedTopFilmData != null) {
                    top100PopularExtended.add(extendedTopFilmData)
                    _top100PopularExtendedFlow.value = top100PopularExtended.toList()
                }
            }
            Log.d(TAG, "Отправлен top100popular размера ${top100PopularExtended.size}")

            top250Extended = mutableListOf()
            top250?.films?.forEach { filmTop ->
                val extendedTopFilmData = repositoryMainLists.extendTopFilmData(filmTop)
                if (extendedTopFilmData != null) {
                    top250Extended.add(extendedTopFilmData)
                    _top250ExtendedFlow.value = top250Extended.toList()
                }
            }
            Log.d(TAG, "Отправлен top250 размера ${top250Extended.size}")

            serialsExtended = mutableListOf()
            serials?.items?.forEach { filmFiltered ->
                val extendedFilteredFilmData = repositoryMainLists.extendFilteredFilmData(filmFiltered)
                if (extendedFilteredFilmData != null) {
                    serialsExtended.add(extendedFilteredFilmData)
                    _serialsExtendedFlow.value = serialsExtended.toList()
                }
            }
            Log.d(TAG, "Отправлен serials размера ${serialsExtended.size}")

            filmsFiltered1Extended = mutableListOf()
            filmsFiltered1?.items?.forEach { filmFiltered ->
                val extendedFilteredFilmData = repositoryMainLists.extendFilteredFilmData(filmFiltered)
                if (extendedFilteredFilmData != null) {
                    filmsFiltered1Extended.add(extendedFilteredFilmData)
                    _filmsFiltered1ExtendedFlow.value = filmsFiltered1Extended.toList()
                }
            }
            Log.d(TAG, "Отправлен filmsFiltered1 размера ${serialsExtended.size}")

            filmsFiltered2Extended = mutableListOf()
            filmsFiltered2?.items?.forEach { filmFiltered ->
                val extendedFilteredFilmData = repositoryMainLists.extendFilteredFilmData(filmFiltered)
                if (extendedFilteredFilmData != null) {
                    filmsFiltered2Extended.add(extendedFilteredFilmData)
                    _filmsFiltered2ExtendedFlow.value = filmsFiltered2Extended.toList()
                }
            }
            Log.d(TAG, "Отправлен filmsFiltered2 размера ${serialsExtended.size}")
        }
    }
}