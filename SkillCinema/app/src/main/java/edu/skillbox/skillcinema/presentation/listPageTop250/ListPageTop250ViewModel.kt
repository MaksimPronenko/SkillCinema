package edu.skillbox.skillcinema.presentation.listPageTop250

import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import edu.skillbox.skillcinema.App
import edu.skillbox.skillcinema.data.FilmsTopListPagingSource
import edu.skillbox.skillcinema.data.RepositoryMainLists
import edu.skillbox.skillcinema.models.filmAndSerial.film.FilmItemData
import edu.skillbox.skillcinema.presentation.ViewModelState
import edu.skillbox.skillcinema.utils.FilmTopType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "ListPageTop250.VM"

class ListPageTop250ViewModel(
    private val repositoryMainLists: RepositoryMainLists,
    application: App
) : AndroidViewModel(application) {

    private val _state = MutableStateFlow<ViewModelState>(
        ViewModelState.Loading
    )
    val state = _state.asStateFlow()

    var top250PagesQuantity = 0

    lateinit var pagedFilmsTop250Extended: Flow<PagingData<FilmItemData>>

   fun loadTop250() {
       viewModelScope.launch(Dispatchers.IO) {
           var error = false
           _state.value = ViewModelState.Loading
           Log.d(TAG, "Cостояние = ${_state.value}")

           val top250LoadResult = repositoryMainLists.getTopList(type = FilmTopType.TOP_250_BEST_FILMS.name, page = 1)
           if (top250LoadResult.second) error = true
           top250PagesQuantity = top250LoadResult.first?.pagesCount ?: 0
           if (top250PagesQuantity == 0) error = true

           pagedFilmsTop250Extended = Pager(
               config = PagingConfig(pageSize = 20),
               pagingSourceFactory = {
                   FilmsTopListPagingSource(type = FilmTopType.TOP_250_BEST_FILMS.name, repository = repositoryMainLists)
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