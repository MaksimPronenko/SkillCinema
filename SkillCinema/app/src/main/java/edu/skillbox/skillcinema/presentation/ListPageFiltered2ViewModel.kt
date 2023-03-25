package edu.skillbox.skillcinema.presentation

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import edu.skillbox.skillcinema.App
import edu.skillbox.skillcinema.data.FilmsFiltered2PagingSource
import edu.skillbox.skillcinema.data.Repository
import edu.skillbox.skillcinema.models.FilmFiltered
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "ListPageFiltered2"

class ListPageFiltered2ViewModel (
    private val repository: Repository,
    application: App
) : AndroidViewModel(application) {

    private val _state = MutableStateFlow<ViewModelState>(
        ViewModelState.Loading
    )
    val state = _state.asStateFlow()

    private val genre = application.genre2key
    private val country = application.country2key

//    private val _premieres = MutableStateFlow<List<FilmPremiere>>(emptyList())
//    val premieres = _premieres.asStateFlow()
//
//    var top100PopularPagesQuantity = 0
//    val pagedFilmsTop100Popular: Flow<PagingData<FilmTop>> = Pager(
//        config = PagingConfig(pageSize = 20),
//        pagingSourceFactory = { FilmTop100PopularPagingSource() }
//    ).flow.cachedIn(viewModelScope)
//
//    val pagedFilmsTop250: Flow<PagingData<FilmTop>> = Pager(
//        config = PagingConfig(pageSize = 20),
//        pagingSourceFactory = { FilmTop250PagingSource() }
//    ).flow.cachedIn(viewModelScope)
//
//    val pagedSeries: Flow<PagingData<FilmFiltered>> = Pager(
//        config = PagingConfig(pageSize = 20),
//        pagingSourceFactory = { SeriesPagingSource() }
//    ).flow.cachedIn(viewModelScope)
//
//    var filmsFiltered1PagesQuantity = 0
//    val pagedFilmsFiltered1: Flow<PagingData<FilmFiltered>> = Pager(
//        config = PagingConfig(pageSize = 20),
//        pagingSourceFactory = {
//            FilmFilteredPagingSource(
//                genre,
//                country
//            )
//        }
//    ).flow.cachedIn(viewModelScope)

var filmsFiltered2PagesQuantity = 0
    val pagedFilmsFiltered2: Flow<PagingData<FilmFiltered>> = Pager(
        config = PagingConfig(pageSize = 20),
        pagingSourceFactory = {
            FilmsFiltered2PagingSource(
                application
            )
        }
    ).flow.cachedIn(viewModelScope)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            loadFilmsFiltered2()
        }
    }

    private suspend fun loadFilmsFiltered2() {
        _state.value = ViewModelState.Loading
        val jobLoading = viewModelScope.launch(Dispatchers.IO) {
            filmsFiltered2PagesQuantity = repository.getFilmFiltered(genre, country, 1).totalPages
        }
        jobLoading.join()
        _state.value = ViewModelState.Loaded
    }
}