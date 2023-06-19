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
import edu.skillbox.skillcinema.models.FilmItemData
import edu.skillbox.skillcinema.models.FilmTop
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "ListPageTop250"

class ListPageTop250ViewModel(
    private val repository: Repository,
    application: App
) : AndroidViewModel(application) {

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

    var top250PagesQuantity = 0
//    val pagedFilmsTop250: Flow<PagingData<FilmTop>> = Pager(
//        config = PagingConfig(pageSize = 20),
//        pagingSourceFactory = { FilmsTop250PagingSource(repository) }
//    ).flow.cachedIn(viewModelScope)
//    val pagedFilmsTop250Extended: Flow<PagingData<FilmItemData>> = Pager(
//        config = PagingConfig(pageSize = 20, prefetchDistance = 0, maxSize = 20),
//        pagingSourceFactory = { FilmsTop250PagingSource(repository) }
//    ).flow.cachedIn(viewModelScope)
    lateinit var pagedFilmsTop250Extended: Flow<PagingData<FilmItemData>>
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

//    init {
//        viewModelScope.launch(Dispatchers.IO) {
//            loadTop250()
//        }
//    }

   fun loadTop250() {
       viewModelScope.launch(Dispatchers.IO) {
           var error = false
           _state.value = ViewModelState.Loading
           Log.d(TAG, "Cостояние = ${_state.value}")
           top250PagesQuantity =
               repository.getTop250(1)?.pagesCount ?: 0
           pagedFilmsTop250Extended = Pager(
               config = PagingConfig(pageSize = 20),
               pagingSourceFactory = {
                   FilmsTop250PagingSource(repository)
               }
           ).flow.cachedIn(viewModelScope)
           if (top250PagesQuantity == 0) error = true

           if (error) {
               _state.value = ViewModelState.Error
               Log.d(TAG, "Cостояние = ${_state.value}")
           } else {
               _state.value = ViewModelState.Loaded
               Log.d(TAG, "Cостояние = ${_state.value}")
           }
       }

//        _state.value = ViewModelState.Loading
//        val jobLoading = viewModelScope.launch(Dispatchers.IO) {
//            top250PagesQuantity = repository.getTop250(1)?.pagesCount ?: 0
//        }
//        jobLoading.join()
//        _state.value = ViewModelState.Loaded
    }
}