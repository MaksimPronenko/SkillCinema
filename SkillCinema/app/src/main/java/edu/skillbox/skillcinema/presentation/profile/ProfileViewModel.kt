package edu.skillbox.skillcinema.presentation.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.skillbox.skillcinema.data.RepositoryFilmAndSerial
import edu.skillbox.skillcinema.data.RepositoryCollections
import edu.skillbox.skillcinema.data.RepositoryPerson
import edu.skillbox.skillcinema.models.collection.CollectionExisting
import edu.skillbox.skillcinema.models.collection.CollectionInfo
import edu.skillbox.skillcinema.models.collection.Interested
import edu.skillbox.skillcinema.models.collection.InterestedTable
import edu.skillbox.skillcinema.models.filmAndSerial.film.FilmItemData
import edu.skillbox.skillcinema.presentation.ViewModelState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

private const val TAG = "Profile.VM"

class ProfileViewModel(
    private val repositoryFilmAndSerial: RepositoryFilmAndSerial,
    private val repositoryPerson: RepositoryPerson,
    private val repositoryCollections: RepositoryCollections
) : ViewModel() {

    private val _state = MutableStateFlow<ViewModelState>(
        ViewModelState.Loading
    )
    val state = _state.asStateFlow()

    var viewedQuantity = 0
    private var viewedFilmsIds: List<Int> = listOf()
    var viewed: MutableList<FilmItemData> = mutableListOf()
    private val _viewedFlow = MutableStateFlow<List<FilmItemData>>(emptyList())
    val viewedFlow = _viewedFlow.asStateFlow()

    var collectionInfoList: List<CollectionInfo> = emptyList()
    private val _collectionChannel = Channel<List<CollectionInfo>>()
    val collectionChannel = _collectionChannel.receiveAsFlow()

    var interestedQuantity = 0
    private var interestedIds: List<InterestedTable> = listOf()
    private var interested: MutableList<Interested> = mutableListOf()
    private val _interestedFlow = MutableStateFlow<List<Interested>>(emptyList())
    val interestedFlow = _interestedFlow.asStateFlow()

    var jobLoadProfileData: Job? = null
    private var jobCreateAndSendToAdapterViewedList: Job? = null
    private var jobCreateAndSendToAdapterInterestedList: Job? = null

    private suspend fun synchronizeCollectionNames() {
        // В этой функции коллеции, фигурирующие вместе с фильмами в collection_table,
        // переписываются в список коллекций collection_existing.
        val collections: MutableSet<String> = mutableSetOf("Любимое", "Хочу посмотреть")
        val collectionsOfFilms: List<String> = repositoryCollections.getCollectionNamesOfFilms()
        collections.addAll(collectionsOfFilms)
        collections.forEach { collectionName ->
            repositoryCollections.addCollection(CollectionExisting(collectionName = collectionName))
        }
    }

    fun loadProfileData() {
        if (jobLoadProfileData?.isActive != true) {
            jobLoadProfileData = viewModelScope.launch(Dispatchers.IO) {
                _state.value = ViewModelState.Loading

                viewedQuantity = repositoryCollections.getViewedFilmsQuantity()
                viewedFilmsIds = repositoryCollections.getViewedFilmsIds().orEmpty()
                Log.d(TAG, "viewedFilmsIds = $viewedFilmsIds")
                interestedQuantity = repositoryCollections.getInterestedQuantity()
                interestedIds = repositoryCollections.getInterestedList().orEmpty()

                synchronizeCollectionNames()
                collectionInfoList = repositoryCollections.getCollectionInfoList()

                _state.value = ViewModelState.Loaded

                _collectionChannel.send(element = collectionInfoList)

                createAndSendToAdapterViewedList()

                createAndSendToAdapterInterestedList()
            }
        }
    }

    fun createNewCollection(collectionName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repositoryCollections.addCollection(CollectionExisting(collectionName = collectionName))
            Log.d(TAG, "getCollectionInfoList() вызываем из createNewCollection($collectionName)")
            collectionInfoList = repositoryCollections.getCollectionInfoList()
            _collectionChannel.send(element = collectionInfoList)
        }
    }

    fun deleteCollection(collectionName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repositoryCollections.deleteCollection(collectionName)
            Log.d(TAG, "getCollectionInfoList() вызываем из deleteCollection($collectionName)")
            collectionInfoList = repositoryCollections.getCollectionInfoList()
            _collectionChannel.send(element = collectionInfoList)
        }
    }

    fun removeAllViewedFilms() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = ViewModelState.Loading
            repositoryCollections.removeAllViewedFilms()
            viewedQuantity = repositoryCollections.getViewedFilmsQuantity()
            viewedFilmsIds = repositoryCollections.getViewedFilmsIds().orEmpty()

            _state.value = ViewModelState.Loaded
            createAndSendToAdapterViewedList()
            createAndSendToAdapterInterestedList()
        }
    }

    fun clearAllInterested() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = ViewModelState.Loading
            repositoryCollections.removeAllInterested()
            interested = mutableListOf()
            interestedQuantity = repositoryCollections.getInterestedQuantity()
            interestedIds = repositoryCollections.getInterestedList().orEmpty()
            _state.value = ViewModelState.Loaded
            createAndSendToAdapterInterestedList()
        }
    }

    private fun createAndSendToAdapterViewedList() {
        if (jobCreateAndSendToAdapterViewedList?.isActive != true) {
            jobCreateAndSendToAdapterViewedList = viewModelScope.launch(Dispatchers.IO) {
                viewed = mutableListOf()
                if (viewedFilmsIds.isEmpty()) {
                    _viewedFlow.value = emptyList()
                } else {
                    viewedFilmsIds.forEach { filmId ->
                        val filmDbViewed = repositoryFilmAndSerial.getFilmDbViewed(filmId).first
                        if (filmDbViewed != null) viewed.add(filmDbViewed.convertToFilmItemData())
                        _viewedFlow.value = viewed.toList().reversed()
                    }
                }
            }
        }
    }

    private fun createAndSendToAdapterInterestedList() {
        if (jobCreateAndSendToAdapterInterestedList?.isActive != true) {
            jobCreateAndSendToAdapterInterestedList = viewModelScope.launch(Dispatchers.IO) {
                interested = mutableListOf()
                if (interestedIds.isEmpty()) {
                    _interestedFlow.value = emptyList()
                } else {
                    interestedIds.forEach { interestedTable ->
                        val interestedItem: Interested? = when (interestedTable.type) {
                            0, 1 -> repositoryFilmAndSerial.getFilmDbViewed(interestedTable.id).first?.let {
                                Interested(
                                    type = interestedTable.type,
                                    interestedViewItem = it.convertToFilmItemData()
                                )
                            }
                            else -> repositoryPerson.getPersonTable(interestedTable.id)?.let {
                                Interested(
                                    type = interestedTable.type,
                                    interestedViewItem = it
                                )
                            }
                        }
                        if (interestedItem != null) interested.add(interestedItem)
                        _interestedFlow.value = interested.toList().reversed()
                    }
                }
            }
        }
    }
}