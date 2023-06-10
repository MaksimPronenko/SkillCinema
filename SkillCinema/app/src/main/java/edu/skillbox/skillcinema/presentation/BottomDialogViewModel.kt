package edu.skillbox.skillcinema.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.skillbox.skillcinema.data.Repository
import edu.skillbox.skillcinema.models.CollectionExisting
import edu.skillbox.skillcinema.models.CollectionFilm
import edu.skillbox.skillcinema.models.CollectionTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

private const val TAG = "BottomDialog.VM"

class BottomDialogViewModel(
    private val repository: Repository,
) : ViewModel() {

    var filmId: Int = 0
    var posterSmall = ""
    var name = ""
    var year = ""
    var genres = ""

    var favorite = false
    var wantedToWatch = false

    var collectionFilmList: List<CollectionFilm> = emptyList()
    private val _collectionChannel = Channel<List<CollectionFilm>>()
    val collectionChannel = _collectionChannel.receiveAsFlow()

    init {
        loadCollectionData()
        Log.d(TAG, "Сработал init")
    }

    private suspend fun synchronizeCollectionNames() {
        // В этой функции коллеции, фигурирующие вместе с фильмами в collection_table,
        // переписываются в список коллекций collection_existing.
        val collections: MutableList<String> = mutableListOf("Любимое", "Хочу посмотреть")
        val collectionsOfFilms: List<String> = repository.getCollectionNamesOfFilms()
        collections.addAll(collectionsOfFilms)
        collections.forEach { collectionName ->
            repository.addCollection(CollectionExisting(collectionName = collectionName))
        }
    }

    fun loadCollectionData() {
        Log.d(TAG, "Запущена loadCollectionData()")
        viewModelScope.launch(Dispatchers.IO) {
//            _state.value = ViewModelState.Loading
            synchronizeCollectionNames()
            collectionFilmList = repository.getCollectionFilmList(filmId = filmId)
            Log.d(TAG, "collectionFilmList = $collectionFilmList")
            _collectionChannel.send(element = collectionFilmList)
//            _state.value = ViewModelState.Loaded
        }
    }

    fun onCollectionItemClick(collectionFilm: CollectionFilm) {
        viewModelScope.launch(Dispatchers.IO) {
            if (collectionFilm.filmIncluded) {
                repository.removeCollectionTable(
                    filmId = filmId,
                    collectionName = collectionFilm.collectionName
                )
            } else {
                repository.addCollectionTable(
                    CollectionTable(
                        collection = collectionFilm.collectionName,
                        filmId = filmId
                    )
                )
            }
            loadCollectionData()
        }
    }

    fun createNewCollection(collectionName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addCollection(CollectionExisting(collectionName = collectionName))
            collectionFilmList = repository.getCollectionFilmList(filmId = filmId)
            _collectionChannel.send(element = collectionFilmList)
        }
    }
}