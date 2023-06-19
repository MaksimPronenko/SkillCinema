package edu.skillbox.skillcinema.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.skillbox.skillcinema.data.Repository
import edu.skillbox.skillcinema.models.*
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
    var viewedFilmsIds: List<Int> = listOf()
    var viewed: MutableList<FilmDbViewed> = mutableListOf()
    private val _viewedFlow = MutableStateFlow<List<FilmDbViewed>>(emptyList())
    val viewedFlow = _viewedFlow.asStateFlow()

    var collectionInfoList: List<CollectionInfo> = emptyList()
    private val _collectionChannel = Channel<List<CollectionInfo>>()
    val collectionChannel = _collectionChannel.receiveAsFlow()

    var interestedQuantity = 0
    var interestedIds: List<InterestedTable> = listOf()
    var interested: MutableList<Pair<Int, InterestedItem>> = mutableListOf()
    private val _interestedFlow = MutableStateFlow<List<Pair<Int, InterestedItem>>>(emptyList())
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

            viewedQuantity = repository.getViewedFilmsQuantity()
            viewedFilmsIds = repository.getViewedFilmsIds()
            interestedQuantity = repository.getInterestedQuantity()
            interestedIds = repository.getInterestedList()

            synchronizeCollectionNames()
            collectionInfoList = repository.getCollectionInfoList()
            _collectionChannel.send(element = collectionInfoList)

            _state.value = ViewModelState.Loaded
            Log.d(TAG, "Состояние завершения загрузки")

            viewedFilmsIds.forEach { filmId ->
                val filmDbViewed = repository.getFilmDbViewed(filmId)
                if (filmDbViewed != null) viewed.add(filmDbViewed)
                _viewedFlow.value = viewed.toList()
            }

            interestedIds.forEach { interestedTable ->
                val interestedItem: InterestedItem? = when (interestedTable.type) {
                    0, 1 -> repository.getFilmDbViewed(interestedTable.id)
                    else -> repository.getPersonTable(interestedTable.id)
                }
                if (interestedItem != null) interested.add(Pair(interestedTable.type, interestedItem))
                _interestedFlow.value = interested.toList()
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

    fun removeAllViewedFilms() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = ViewModelState.Loading
            Log.d(TAG, "Состояние загрузки")
            repository.removeAllViewedFilms()
            viewedFilmsIds = repository.getViewedFilmsIds()

            _state.value = ViewModelState.Loaded
            Log.d(TAG, "Состояние рабочее")
            viewedFilmsIds.forEach { filmId ->
                val filmDbViewed = repository.getFilmDbViewed(filmId)
                if (filmDbViewed != null) viewed.add(filmDbViewed)
                _viewedFlow.value = viewed.toList()
            }
        }
    }
}