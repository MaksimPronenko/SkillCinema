package edu.skillbox.skillcinema.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import edu.skillbox.skillcinema.data.Repository
import edu.skillbox.skillcinema.data.SeriesPagingSource
import edu.skillbox.skillcinema.models.FilmFiltered
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "ListPageSeries"

class ListPageSeriesViewModel(
    private val repository: Repository
) : ViewModel() {

    private val _state = MutableStateFlow<ViewModelState>(
        ViewModelState.Loading
    )
    val state = _state.asStateFlow()

//    private val _premieres = MutableStateFlow<List<FilmPremiere>>(emptyList())
//    val premieres = _premieres.asStateFlow()

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

    var seriesPagesQuantity = 0
    val pagedSeries: Flow<PagingData<FilmFiltered>> = Pager(
        config = PagingConfig(pageSize = 20),
        pagingSourceFactory = { SeriesPagingSource() }
    ).flow.cachedIn(viewModelScope)
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
            loadSeries()
        }
    }

    private suspend fun loadSeries() {
        _state.value = ViewModelState.Loading
        val jobLoading = viewModelScope.launch(Dispatchers.IO) {
            seriesPagesQuantity = repository.getSeries(1).totalPages
        }
        jobLoading.join()
        _state.value = ViewModelState.Loaded
    }
}