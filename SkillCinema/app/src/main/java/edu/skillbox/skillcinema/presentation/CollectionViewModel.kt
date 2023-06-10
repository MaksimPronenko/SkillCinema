package edu.skillbox.skillcinema.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.skillbox.skillcinema.data.Repository
import edu.skillbox.skillcinema.models.FilmDbViewed
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "Collection.VM"

class CollectionViewModel(
    private val repository: Repository
) : ViewModel() {

    private val _state = MutableStateFlow<ViewModelState>(
        ViewModelState.Loading
    )
    val state = _state.asStateFlow()

    var collection = ""

    var filmsIds: List<Int> = listOf()
    var filmsList: MutableList<FilmDbViewed> = mutableListOf()
    private val _filmsListFlow = MutableStateFlow<List<FilmDbViewed>>(emptyList())
    val filmsListFlow = _filmsListFlow.asStateFlow()

    fun loadData(collectionName: String) {
        Log.d(TAG, "loadData()")
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = ViewModelState.Loading
            Log.d(TAG, "Состояние загрузки")
            collection = collectionName
            filmsIds = if (collectionName.isBlank())
                repository.getViewedFilmsIds()
            else
                repository.getCollectionFilmsIds(collectionName)

            _state.value = ViewModelState.Loaded
            Log.d(TAG, "Состояние рабочее")
            filmsIds.forEach { filmId ->
                val filmDbViewed = repository.getFilmDbViewed(filmId)
                if (filmDbViewed != null) filmsList.add(filmDbViewed)
                _filmsListFlow.value = filmsList.toList()
            }
        }
    }

    fun removeAllViewedFilms(collectionName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = ViewModelState.Loading
            Log.d(TAG, "Состояние загрузки")
            filmsIds = if (collectionName.isBlank()) {
                repository.removeAllViewedFilms()
                repository.getViewedFilmsIds()
            }
            else {
//                repository.remove
                repository.getCollectionFilmsIds(collectionName)
            }

            _state.value = ViewModelState.Loaded
            Log.d(TAG, "Состояние рабочее")
            filmsIds.forEach { filmId ->
                val filmDbViewed = repository.getFilmDbViewed(filmId)
                if (filmDbViewed != null) filmsList.add(filmDbViewed)
                _filmsListFlow.value = filmsList.toList()
            }
        }
    }
}