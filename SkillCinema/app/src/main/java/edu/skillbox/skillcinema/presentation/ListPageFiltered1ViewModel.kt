package edu.skillbox.skillcinema.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import edu.skillbox.skillcinema.data.FilmsFilteredPagingSource
import edu.skillbox.skillcinema.data.Repository
import edu.skillbox.skillcinema.models.FilmItemData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "ListPageFiltered1.VM"

class ListPageFiltered1ViewModel(
    private val repository: Repository
) : ViewModel() {

    private val _state = MutableStateFlow<ViewModelState>(
        ViewModelState.Loading
    )
    val state = _state.asStateFlow()

//    var genre1Key: Int? = null
//    var country1Key: Int? = null

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

    var filmsFiltered1PagesQuantity = 0
    lateinit var pagedFilmsFiltered1: Flow<PagingData<FilmItemData>>
//    = Pager(
//        config = PagingConfig(pageSize = 20),
//        pagingSourceFactory = {
//            FilmsFiltered1PagingSource(
//                repository,
//                genre1Key,
//                country1Key
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
//        loadFilmsFiltered1()
//    }

    fun loadFilmsFiltered1(genre1Key: Int, country1Key: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            var error = false
            _state.value = ViewModelState.Loading
            Log.d(TAG, "Cостояние = ${_state.value}")
            filmsFiltered1PagesQuantity =
                repository.getFilmsFiltered(genre1Key, country1Key, 1)?.totalPages ?: 0
            pagedFilmsFiltered1 = Pager(
                config = PagingConfig(pageSize = 20),
                pagingSourceFactory = {
                    FilmsFilteredPagingSource(
                        repository,
                        genre1Key,
                        country1Key
                    )
                }
            ).flow.cachedIn(viewModelScope)
            if (filmsFiltered1PagesQuantity == 0) error = true

            if (error) {
                _state.value = ViewModelState.Error
                Log.d(TAG, "Cостояние = ${_state.value}")
            } else {
                _state.value = ViewModelState.Loaded
                Log.d(TAG, "Cостояние = ${_state.value}")
            }
        }
    }
}