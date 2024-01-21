package edu.skillbox.skillcinema.presentation.search

import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import edu.skillbox.skillcinema.App
import edu.skillbox.skillcinema.data.RepositoryMainLists
import edu.skillbox.skillcinema.data.SearchPagingSource
import edu.skillbox.skillcinema.models.filmAndSerial.film.FilmItemData
import edu.skillbox.skillcinema.models.filmFiltered.PagedFilmFilteredList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "Search.VM"

class SearchViewModel(
    private val repositoryMainLists: RepositoryMainLists,
    private val application: App
) : AndroidViewModel(application) {

    private val _state = MutableStateFlow<SearchViewModelState>(
        SearchViewModelState.Searching
    )
    val state = _state.asStateFlow()

    var searchText: String? = application.keyword
    lateinit var filmsFlow: Flow<PagingData<FilmItemData>>

    private lateinit var searchResult: Pair<PagedFilmFilteredList?, Boolean>
    private var foundFilmsQuantity: Int? = null

    var jobSearch: Job? = null

    fun search(searchedText: String? = application.keyword) {
        Log.d(TAG, "Запущена search($searchedText)")
        viewModelScope.launch(Dispatchers.IO) {
            if (jobSearch?.isActive == true) {
                Log.d(TAG, "Останавливаем принудительно jobSearch")
                jobSearch!!.cancelAndJoin()
            }
            jobSearch = viewModelScope.launch(Dispatchers.IO) {
                Log.d(TAG, "Начало jobSearch")
                _state.value = SearchViewModelState.Searching
                Log.d(TAG, "Состояние поиска")
                var error = false
                val jobGetResultQuantity = viewModelScope.launch(Dispatchers.IO) {
                    Log.d(TAG, "Начало jobGetResultQuantity")
                    if (searchedText.isNullOrBlank()) {
                        application.keyword = null
                    } else {
                        application.keyword = searchedText
                        searchText = searchedText
                    }
                    searchResult = repositoryMainLists.searchFilms(
                        country = application.country,
                        genre = application.genre,
                        order = application.order,
                        type = application.type,
                        ratingFrom = application.ratingFrom,
                        ratingTo = application.ratingTo,
                        yearFrom = application.yearFrom,
                        yearTo = application.yearTo,
                        keyword = searchText,
                        page = 1
                    )
                    foundFilmsQuantity = searchResult.first?.total ?: 0
                    Log.d(TAG, "foundFilmsQuantity = $foundFilmsQuantity")
                    error = searchResult.second
                }
                jobGetResultQuantity.join()
                if (error) {
                    _state.value = SearchViewModelState.SearchError
                    Log.d(TAG, "ViewModel. Cостояние ошибки загрузки")
                } else if (foundFilmsQuantity!! > 0) {
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
                                repositoryMainLists
                            )
                        }
                    ).flow.cachedIn(viewModelScope)
                    _state.value = SearchViewModelState.SearchSuccessfull
                } else {
                    _state.value = SearchViewModelState.SearchFailed
                    Log.d(TAG, "ViewModel. Cостояние неудачного поиска")
                }
            }
        }
    }
}