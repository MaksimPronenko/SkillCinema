package edu.skillbox.skillcinema.presentation

import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import edu.skillbox.skillcinema.App
import edu.skillbox.skillcinema.data.FilmsTopListPagingSource
import edu.skillbox.skillcinema.data.Repository
import edu.skillbox.skillcinema.models.FilmItemData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "ListPagePopular.VM"

class ListPagePopularViewModel(
    private val repository: Repository,
    application: App
) : AndroidViewModel(application) {

    private val _state = MutableStateFlow<ViewModelState>(
        ViewModelState.Loading
    )
    val state = _state.asStateFlow()

    var top100PopularPagesQuantity = 0

    lateinit var pagedFilmsTop100PopularExtended: Flow<PagingData<FilmItemData>>

     fun loadTop100Popular() {
         viewModelScope.launch(Dispatchers.IO) {
             var error = false
             _state.value = ViewModelState.Loading
             Log.d(TAG, "Cостояние = ${_state.value}")

             val top100PopularLoadResult = repository.getTopList(type = "TOP_100_POPULAR_FILMS", page = 1)
             if (top100PopularLoadResult.second) error = true
             top100PopularPagesQuantity = top100PopularLoadResult.first?.pagesCount ?: 0
             if (top100PopularPagesQuantity == 0) error = true

             pagedFilmsTop100PopularExtended = Pager(
                 config = PagingConfig(pageSize = 20),
                 pagingSourceFactory = {
                     FilmsTopListPagingSource(type = "TOP_100_POPULAR_FILMS", repository = repository)
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