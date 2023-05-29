package edu.skillbox.skillcinema.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.skillbox.skillcinema.data.Repository
import edu.skillbox.skillcinema.models.CollectionExisting
import edu.skillbox.skillcinema.models.CollectionInfo
import edu.skillbox.skillcinema.models.FilmTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

private const val TAG = "Profile.VM"

class ProfileViewModel(
    private val repository: Repository
) : ViewModel() {

    private val _state = MutableStateFlow<ViewModelState>(
        ViewModelState.Loading
    )
    val state = _state.asStateFlow()

    var viewedQuantity = 0
    private val _viewedFlow = MutableStateFlow<List<FilmTable>>(emptyList())
    val viewedFlow = _viewedFlow.asStateFlow()

    var collectionInfoList: List<CollectionInfo> = emptyList()
    private val _collectionChannel = Channel<List<CollectionInfo>>()
    val collectionChannel = _collectionChannel.receiveAsFlow()

    var newCollectionName = ""

    var interestedQuantity = 0
    private val _interestedFlow = MutableStateFlow<List<FilmTable>>(emptyList())
    val interestedFlow = _interestedFlow.asStateFlow()

    init {
        loadProfileData()
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

    private fun loadProfileData() {
        Log.d(TAG, "Запущена loadProfileData()")
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = ViewModelState.Loading
            var error = false

            synchronizeCollectionNames()
            collectionInfoList = repository.getCollectionInfoList()
            _collectionChannel.send(element = collectionInfoList)

            if (error) {
                _state.value = ViewModelState.Error
                Log.d(TAG, "Состояние ошибки")
            } else {
                _state.value = ViewModelState.Loaded
                Log.d(TAG, "Состояние успешного завершения загрузки")
            }
        }
    }

    fun createNewCollection(collectionName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addCollection(CollectionExisting(collectionName = collectionName))
            collectionInfoList = repository.getCollectionInfoList()
            _collectionChannel.send(element = collectionInfoList)
        }
    }

    fun deleteCollection(collectionName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteCollection(collectionName)
            collectionInfoList = repository.getCollectionInfoList()
            _collectionChannel.send(element = collectionInfoList)
        }
    }
}