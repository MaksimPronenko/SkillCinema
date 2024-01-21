package edu.skillbox.skillcinema.presentation.listPageFiltered1

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import edu.skillbox.skillcinema.data.FilmsFilteredPagingSource
import edu.skillbox.skillcinema.data.RepositoryMainLists
import edu.skillbox.skillcinema.models.filmAndSerial.film.FilmItemData
import edu.skillbox.skillcinema.presentation.ViewModelState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "ListPageFiltered1.VM"

class ListPageFiltered1ViewModel(
    private val repositoryMainLists: RepositoryMainLists
) : ViewModel() {

    private val _state = MutableStateFlow<ViewModelState>(
        ViewModelState.Loading
    )
    val state = _state.asStateFlow()

    private var filmsFiltered1PagesQuantity = 0
    lateinit var pagedFilmsFiltered1: Flow<PagingData<FilmItemData>>

    fun loadFilmsFiltered1(genre1Key: Int, country1Key: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            var error = false
            _state.value = ViewModelState.Loading
            Log.d(TAG, "Cостояние = ${_state.value}")

            val filmsFiltered1LoadResult = repositoryMainLists.getFilmsFilteredClearedFromNullRating(genre1Key, country1Key, 1)
            if (filmsFiltered1LoadResult.second) error = true
            filmsFiltered1PagesQuantity = filmsFiltered1LoadResult.first?.totalPages ?: 0
            if (filmsFiltered1PagesQuantity == 0) error = true

            pagedFilmsFiltered1 = Pager(
                config = PagingConfig(pageSize = 20),
                pagingSourceFactory = {
                    FilmsFilteredPagingSource(
                        repositoryMainLists,
                        genre1Key,
                        country1Key
                    )
                }
            ).flow.cachedIn(viewModelScope)

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