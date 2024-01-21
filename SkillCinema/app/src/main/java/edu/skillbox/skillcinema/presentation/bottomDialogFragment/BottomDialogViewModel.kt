package edu.skillbox.skillcinema.presentation.bottomDialogFragment

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.skillbox.skillcinema.data.RepositoryCollections
import edu.skillbox.skillcinema.models.collection.CollectionExisting
import edu.skillbox.skillcinema.models.collection.CollectionFilm
import edu.skillbox.skillcinema.models.collection.CollectionTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

private const val TAG = "BottomDialog.VM"

class BottomDialogViewModel(
    private val repositoryCollections: RepositoryCollections,
) : ViewModel() {

    var filmId: Int = 0
    var posterSmall = ""
    var name = ""
    var year = ""
    var genres = ""

    var favorite = false
    var wantedToWatch = false

    private var collectionFilmList: List<CollectionFilm> = emptyList()
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
        val collectionsOfFilms: List<String> = repositoryCollections.getCollectionNamesOfFilms()
        collections.addAll(collectionsOfFilms)
        collections.forEach { collectionName ->
            repositoryCollections.addCollection(CollectionExisting(collectionName = collectionName))
        }
    }

    fun loadCollectionData() {
        Log.d(TAG, "Запущена loadCollectionData()")
        viewModelScope.launch(Dispatchers.IO) {
            synchronizeCollectionNames()
            collectionFilmList = repositoryCollections.getCollectionFilmList(filmId = filmId)
            Log.d(TAG, "collectionFilmList = $collectionFilmList")
            _collectionChannel.send(element = collectionFilmList)
        }
    }

    fun onCollectionItemClick(collectionFilm: CollectionFilm) {
        viewModelScope.launch(Dispatchers.IO) {
            if (collectionFilm.filmIncluded) {
                repositoryCollections.removeCollectionTable(
                    filmId = filmId,
                    collectionName = collectionFilm.collectionName
                )
            } else {
                repositoryCollections.addCollectionTable(
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
            repositoryCollections.addCollection(CollectionExisting(collectionName = collectionName))
            collectionFilmList = repositoryCollections.getCollectionFilmList(filmId = filmId)
            _collectionChannel.send(element = collectionFilmList)
        }
    }
}