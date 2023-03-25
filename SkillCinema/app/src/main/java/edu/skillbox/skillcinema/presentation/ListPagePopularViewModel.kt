package edu.skillbox.skillcinema.presentation

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.skillbox.skillcinema.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.Flow
import androidx.paging.PagingData
import edu.skillbox.skillcinema.models.FilmTop
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import edu.skillbox.skillcinema.App

private const val TAG = "ListPagePopular"

class ListPagePopularViewModel(
    private val repository: Repository,
    application: App
) : AndroidViewModel(application) {

    private val _state = MutableStateFlow<ViewModelState>(
        ViewModelState.Loading
    )
    val state = _state.asStateFlow()

//    private val _premieres = MutableStateFlow<List<FilmPremiere>>(emptyList())
//    val premieres = _premieres.asStateFlow()

    var top100PopularPagesQuantity = 0
    val pagedFilmsTop100Popular: Flow<PagingData<FilmTop>> = Pager(
        config = PagingConfig(pageSize = 20),
        pagingSourceFactory = { FilmsTop100PopularPagingSource() }
    ).flow.cachedIn(viewModelScope)
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
//    val pagedFilmsFiltered1: Flow<PagingData<FilmFiltered>> = Pager(
//        config = PagingConfig(pageSize = 20),
//        pagingSourceFactory = {
//            FilmFilteredPagingSource(
//                application.genre1key,
//                application.country1key
//            )
//        }
//    ).flow.cachedIn(viewModelScope)
//
//    val pagedFilmsFiltered2: Flow<PagingData<FilmFiltered>> = Pager(
//        config = PagingConfig(pageSize = 20),
//        pagingSourceFactory = {
//            FilmFilteredPagingSource(
//                application.genre2key,
//                application.country2key
//            )
//        }
//    ).flow.cachedIn(viewModelScope)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            loadPopular()
        }
    }

    private suspend fun loadPopular() {
        _state.value = ViewModelState.Loading
        val jobLoading = viewModelScope.launch(Dispatchers.IO) {
            top100PopularPagesQuantity = repository.getTop100Popular(1).pagesCount
        }
        jobLoading.join()
        _state.value = ViewModelState.Loaded
    }
}