package edu.skillbox.skillcinema.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.skillbox.skillcinema.data.Repository
import edu.skillbox.skillcinema.models.FilmItemData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
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
    var filmsList: MutableList<FilmItemData> = mutableListOf()
    private val _filmsListFlow = MutableStateFlow<List<FilmItemData>>(emptyList())
    val filmsListFlow = _filmsListFlow.asStateFlow()

    var jobLoadData: Job? = null
    var jobCreateAndSendToAdapterFilmsList: Job? = null

    fun loadData(collectionName: String) {
        Log.d(TAG, "loadData()")
        if (jobLoadData?.isActive != true) {
            jobLoadData = viewModelScope.launch(Dispatchers.IO) {
                _state.value = ViewModelState.Loading
                Log.d(TAG, "Состояние загрузки")
                collection = collectionName
                filmsIds = if (collectionName.isBlank())
                    repository.getViewedFilmsIds().orEmpty()
                else
                    repository.getCollectionFilmsIds(collectionName).orEmpty()

                _state.value = ViewModelState.Loaded
                Log.d(TAG, "Состояние рабочее")
                Log.d(TAG, "Запускаем createAndSendToAdapterFilmsList() из loadData()")
                createAndSendToAdapterFilmsList()
            }
        }
    }

    fun clear(collectionName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = ViewModelState.Loading
            Log.d(TAG, "Состояние загрузки")
            filmsIds = if (collectionName.isBlank()) {
                repository.removeAllViewedFilms()
                repository.getViewedFilmsIds().orEmpty()
            } else {
                repository.removeCollectionFilms(collectionName)
                repository.getCollectionFilmsIds(collectionName).orEmpty()
            }

            _state.value = ViewModelState.Loaded
            Log.d(TAG, "Состояние рабочее")
            loadData(collectionName)
        }
    }

    private fun createAndSendToAdapterFilmsList() {
        if (jobCreateAndSendToAdapterFilmsList?.isActive != true) {
            jobCreateAndSendToAdapterFilmsList = viewModelScope.launch(Dispatchers.IO) {
                filmsList = mutableListOf()
                Log.d(TAG, "createAndSendToAdapterFilmsList(). Внутри Job.")
                if (filmsIds.isEmpty()) {
                    _filmsListFlow.value = emptyList()
                } else {
                    filmsIds.forEach { filmId ->
                        val filmDbViewed = repository.getFilmDbViewed(filmId).first
                        if (filmDbViewed != null) filmsList.add(filmDbViewed.convertToFilmItemData())
                        _filmsListFlow.value = filmsList.toList()
                    }
                }
            }
        }
    }
}