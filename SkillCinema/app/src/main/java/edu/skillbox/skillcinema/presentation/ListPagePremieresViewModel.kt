package edu.skillbox.skillcinema.presentation

import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.skillbox.skillcinema.App
import edu.skillbox.skillcinema.data.*
import edu.skillbox.skillcinema.models.FilmPremiere
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "ListPagePremieres"

class ListPagePremieresViewModel(
    private val repository: Repository
) : ViewModel() {

    private val _state = MutableStateFlow<ViewModelState>(
        ViewModelState.Loading
    )
    val state = _state.asStateFlow()

    var premieresQuantity = 0
    private val _premieres = MutableStateFlow<List<FilmPremiere>>(emptyList())
    val premieres = _premieres.asStateFlow()

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
            loadPremieres()
        }
    }

    private suspend fun loadPremieres() {
        _state.value = ViewModelState.Loading
        val jobLoading = viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                repository.getPremieres()
            }.fold(
                onSuccess = {
                    _premieres.value = it
                    premieresQuantity = it.size
                },
                onFailure = { Log.d(TAG, it.message ?: "Ошибка загрузки премьер") }
            )
        }
        jobLoading.join()
        _state.value = ViewModelState.Loaded
    }
}