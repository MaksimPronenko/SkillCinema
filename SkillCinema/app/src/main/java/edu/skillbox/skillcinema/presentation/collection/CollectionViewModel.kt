package edu.skillbox.skillcinema.presentation.collection

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.skillbox.skillcinema.data.RepositoryFilmAndSerial
import edu.skillbox.skillcinema.data.RepositoryCollections
import edu.skillbox.skillcinema.models.filmAndSerial.film.FilmItemData
import edu.skillbox.skillcinema.presentation.ViewModelState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "Collection.VM"

class CollectionViewModel(
    private val repositoryFilmAndSerial: RepositoryFilmAndSerial,
    private val repositoryCollections: RepositoryCollections
) : ViewModel() {

    private val _state = MutableStateFlow<ViewModelState>(
        ViewModelState.Loading
    )
    val state = _state.asStateFlow()

    var collection = ""

    private var filmsIds: List<Int> = listOf()
    private var filmsList: MutableList<FilmItemData> = mutableListOf()
    private val _filmsListFlow = MutableStateFlow<List<FilmItemData>>(emptyList())
    val filmsListFlow = _filmsListFlow.asStateFlow()

    private var jobLoadData: Job? = null
    private var jobCreateAndSendToAdapterFilmsList: Job? = null

    fun loadData(collectionName: String) {
        Log.d(TAG, "loadData()")
        if (jobLoadData?.isActive != true) {
            jobLoadData = viewModelScope.launch(Dispatchers.IO) {
                _state.value = ViewModelState.Loading
                Log.d(TAG, "Состояние загрузки")
                collection = collectionName
                filmsIds = if (collectionName.isBlank())
                    repositoryCollections.getViewedFilmsIds().orEmpty()
                else
                    repositoryCollections.getCollectionFilmsIds(collectionName)

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
                repositoryCollections.removeAllViewedFilms()
                repositoryCollections.getViewedFilmsIds().orEmpty()
            } else {
                repositoryCollections.removeCollectionFilms(collectionName)
                repositoryCollections.getCollectionFilmsIds(collectionName)
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
                        val filmDbViewed = repositoryFilmAndSerial.getFilmDbViewed(filmId).first
                        if (filmDbViewed != null) filmsList.add(filmDbViewed.convertToFilmItemData())
                        _filmsListFlow.value = filmsList.toList()
                    }
                }
            }
        }
    }
}