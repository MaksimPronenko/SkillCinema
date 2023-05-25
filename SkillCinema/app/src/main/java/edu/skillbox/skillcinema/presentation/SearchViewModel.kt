package edu.skillbox.skillcinema.presentation

import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import edu.skillbox.skillcinema.App
import edu.skillbox.skillcinema.data.FilmDao
import edu.skillbox.skillcinema.data.Repository
import edu.skillbox.skillcinema.data.SearchPagingSource
import edu.skillbox.skillcinema.models.FilmFiltered
import edu.skillbox.skillcinema.models.PagedFilmFilteredList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

private const val TAG = "Search.VM"

class SearchViewModel(
    private val repository: Repository,
    private val application: App,
    val dao: FilmDao
) : AndroidViewModel(application) {

    private val _state = MutableStateFlow<SearchViewModelState>(
        SearchViewModelState.Searching
    )
    val state = _state.asStateFlow()

    var searchText: String? = application.keyword
    var filmsFlow: Flow<PagingData<FilmFiltered>> = emptyFlow()
    var searchResult: PagedFilmFilteredList? = null
    var foundFilmsQuantity = 0


//    fun refreshAppData() {
//        chosenCountryCode = application.country
//    }
//    init {
//        Log.d(TAG, "Сработал init")
//        search(searchText)
//    }

    fun search(searchedText: String? = application.keyword) {
        Log.d(TAG, "Запущена search($searchedText)")
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = SearchViewModelState.Searching
            Log.d(TAG, "Состояние поиска")
            var error = false
            val jobSearch = viewModelScope.launch(Dispatchers.IO) {
                Log.d(TAG, "Начало jobSearch")
                if (searchedText.isNullOrBlank()) {
                    application.keyword = null
                } else {
                    application.keyword = searchedText
                    searchText = searchedText
                }

                kotlin.runCatching {
                    searchResult = repository.searchFilms(
                        country = application.country,
                        genre = application.genre,
                        order = application.order,
                        type = application.type,
                        ratingFrom = application.ratingFrom,
                        ratingTo = application.ratingTo,
                        yearFrom = application.yearFrom,
                        yearTo = application.yearTo,
                        keyword = application.keyword,
                        page = 1
                    )
                }.fold(
                    onSuccess = {
                        foundFilmsQuantity = searchResult?.total ?: 0
                    },
                    onFailure = {
                        error = true
                    }
                )
            }
            jobSearch.join()
            if (error) {
                _state.value = SearchViewModelState.SearchError
                Log.d(TAG, "ViewModel. Cостояние ошибки загрузки")
            } else if (foundFilmsQuantity > 0) {
                _state.value = SearchViewModelState.SearchSuccessfull
                Log.d(TAG, "ViewModel. Cостояние успешной загрузки")
                Log.d(TAG, "viewModel.searchText = $searchText")
                Log.d(TAG, "application.keyword = ${application.keyword}")
                Log.d(TAG, "application.country = ${application.country}")
                Log.d(TAG, "Создаём поток")
                filmsFlow = Pager(
                    config = PagingConfig(pageSize = 20),
                    pagingSourceFactory = {
                        SearchPagingSource(
                            application,
                            dao
                        )
                    }
                ).flow.cachedIn(viewModelScope)
            } else {
                _state.value = SearchViewModelState.SearchFailed
                Log.d(TAG, "ViewModel. Cостояние неудачного поиска")
            }
        }
    }
}