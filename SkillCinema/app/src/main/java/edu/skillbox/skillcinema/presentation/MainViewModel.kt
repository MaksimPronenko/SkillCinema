package edu.skillbox.skillcinema.presentation

import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import edu.skillbox.skillcinema.App
import edu.skillbox.skillcinema.data.*
import edu.skillbox.skillcinema.models.FilmFiltered
import edu.skillbox.skillcinema.models.FilmPremiere
import edu.skillbox.skillcinema.models.FilmTop
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "Main"

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
    private val filmTop100PopularPagingSource: FilmsTop100PopularPagingSource,
    private val filmTop250PagingSource: FilmsTop250PagingSource,
    private val seriesPagingSource: SeriesPagingSource,
    private val filmsFiltered1PagingSource: FilmsFiltered1PagingSource,
    private val filmsFiltered2PagingSource: FilmsFiltered2PagingSource,
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

    var premieresQuantity = 0
    private val _premieres = MutableStateFlow<List<FilmPremiere>>(emptyList())
    val premieres = _premieres.asStateFlow()

    var top100PopularPagesQuantity = 0
    val pagedFilmsTop100Popular: Flow<PagingData<FilmTop>> = Pager(
        config = PagingConfig(pageSize = 20, prefetchDistance = 0, maxSize = 20),
        pagingSourceFactory = { filmTop100PopularPagingSource }
    ).flow.cachedIn(viewModelScope)

    var top250PagesQuantity = 0
    val pagedFilmsTop250: Flow<PagingData<FilmTop>> = Pager(
        config = PagingConfig(pageSize = 20, prefetchDistance = 0, maxSize = 20),
        pagingSourceFactory = { filmTop250PagingSource }
    ).flow.cachedIn(viewModelScope)

    var seriesPagesQuantity = 0
    val pagedSeries: Flow<PagingData<FilmFiltered>> = Pager(
        config = PagingConfig(pageSize = 20, prefetchDistance = 0, maxSize = 20),
        pagingSourceFactory = { seriesPagingSource }
    ).flow.cachedIn(viewModelScope)

    var filmsFiltered1PagesQuantity = 0
    val pagedFilmsFiltered1: Flow<PagingData<FilmFiltered>> = Pager(
        config = PagingConfig(pageSize = 20, prefetchDistance = 0, maxSize = 20),
        pagingSourceFactory = {
            filmsFiltered1PagingSource
//            FilmsFilteredPagingSource(
//                application.genre1key,
//                application.country1key
//            )
        }
    ).flow.cachedIn(viewModelScope)

    var filmsFiltered2PagesQuantity = 0
    val pagedFilmsFiltered2: Flow<PagingData<FilmFiltered>> = Pager(
        config = PagingConfig(pageSize = 20, prefetchDistance = 0, maxSize = 20),
        pagingSourceFactory = {
            filmsFiltered2PagingSource
//            FilmsFilteredPagingSource(
//                application.genre2key,
//                application.country2key
//            )
        }
    ).flow.cachedIn(viewModelScope)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            loadFilms(
                application.genre1key,
                application.country1key,
                application.genre2key,
                application.country2key
            )
        }
    }

    private suspend fun loadFilms(genre1: Int, country1: Int, genre2: Int, country2: Int) {
        _state.value = ViewModelState.Loading
        var error = false
        val jobLoading = viewModelScope.launch(Dispatchers.IO) {
            var listPremieres: List<FilmPremiere> = emptyList()
            kotlin.runCatching {
                listPremieres = repository.getPremieres()
                top100PopularPagesQuantity = repository.getTop100Popular(1).pagesCount
                top250PagesQuantity = repository.getTop250(1).pagesCount
                seriesPagesQuantity = repository.getSeries(1).totalPages
                filmsFiltered1PagesQuantity =
                    repository.getFilmFiltered(genre1, country1, 1).totalPages
                filmsFiltered2PagesQuantity =
                    repository.getFilmFiltered(genre2, country2, 1).totalPages
            }.fold(
                onSuccess = {
                    _premieres.value = listPremieres
                    premieresQuantity = listPremieres.size
                },
                onFailure = {
                    Log.d(TAG, it.message ?: "Ошибка загрузки премьер")
                    error = true
                }
            )
//            top100PopularPagesQuantity = repository.getTop100Popular(1).pagesCount
//            top250PagesQuantity = repository.getTop250(1).pagesCount
//            seriesPagesQuantity = repository.getSeries(1).totalPages
//            filmsFiltered1PagesQuantity = repository.getFilmFiltered(genre1, country1, 1).totalPages
//            filmsFiltered2PagesQuantity = repository.getFilmFiltered(genre2, country2, 1).totalPages
        }
        jobLoading.join()
        if (error) {
            _state.value = ViewModelState.Error
            Log.d("FilmVM", "ViewModel. Cостояние = ${_state.value}")
        } else {
            _state.value = ViewModelState.Loaded
            Log.d("FilmVM", "ViewModel. Cостояние = ${_state.value}")
        }
    }

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