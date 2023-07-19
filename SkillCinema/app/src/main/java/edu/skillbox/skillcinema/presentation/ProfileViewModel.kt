package edu.skillbox.skillcinema.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.skillbox.skillcinema.data.Repository
import edu.skillbox.skillcinema.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
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
    var viewed: MutableList<FilmItemData> = mutableListOf()
    private val _viewedFlow = MutableStateFlow<List<FilmItemData>>(emptyList())
    val viewedFlow = _viewedFlow.asStateFlow()

    var collectionInfoList: List<CollectionInfo> = emptyList()
    private val _collectionChannel = Channel<List<CollectionInfo>>()
    val collectionChannel = _collectionChannel.receiveAsFlow()

    var interestedQuantity = 0
    var interestedIds: List<InterestedTable> = listOf()
    var interested: MutableList<Interested> = mutableListOf()
    private val _interestedFlow = MutableStateFlow<List<Interested>>(emptyList())
    val interestedFlow = _interestedFlow.asStateFlow()

    var jobLoadProfileData: Job? = null
    var jobCreateAndSendToAdapterViewedList: Job? = null
    var jobCreateAndSendToAdapterInterestedList: Job? = null

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

    fun loadProfileData() {
        Log.d(TAG, "Запущена loadProfileData()")
        if (jobLoadProfileData?.isActive != true) {
            jobLoadProfileData = viewModelScope.launch(Dispatchers.IO) {
                _state.value = ViewModelState.Loading

                viewedQuantity = repository.getViewedFilmsQuantity()
                viewedFilmsIds = repository.getViewedFilmsIds().orEmpty()
                Log.d(TAG, "viewedFilmsIds = $viewedFilmsIds")
                interestedQuantity = repository.getInterestedQuantity()
                interestedIds = repository.getInterestedList().orEmpty()

                synchronizeCollectionNames()
                collectionInfoList = repository.getCollectionInfoList()
                _collectionChannel.send(element = collectionInfoList)

                _state.value = ViewModelState.Loaded
                Log.d(TAG, "Состояние завершения загрузки")

                Log.d(TAG, "Запускаем createAndSendToAdapterViewedList() из loadProfileData()")
                createAndSendToAdapterViewedList()

                Log.d(TAG, "Запускаем createAndSendToAdapterInterestedList() из loadProfileData()")
                createAndSendToAdapterInterestedList()
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
            viewedQuantity = repository.getViewedFilmsQuantity()
            viewedFilmsIds = repository.getViewedFilmsIds().orEmpty()
            Log.d(TAG, "viewedFilmsIds = $viewedFilmsIds")

            _state.value = ViewModelState.Loaded
            Log.d(TAG, "Состояние рабочее")
            Log.d(TAG, "Запускаем createAndSendToAdapterViewedList() из removeAllViewedFilms()")
            createAndSendToAdapterViewedList()
            createAndSendToAdapterInterestedList()
        }
    }

    fun clearAllInterested() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = ViewModelState.Loading
            Log.d(TAG, "Состояние загрузки")
            repository.removeAllInterested()
            interested = mutableListOf()
            interestedQuantity = repository.getInterestedQuantity()
            interestedIds = repository.getInterestedList().orEmpty()
            Log.d(TAG, "interestedIds = $interestedIds")
            _state.value = ViewModelState.Loaded
            Log.d(TAG, "Состояние рабочее")
            Log.d(TAG, "Запускаем createAndSendToAdapterInterestedList() из clearAllInterested()")
            createAndSendToAdapterInterestedList()
        }
    }

    private fun createAndSendToAdapterViewedList() {
        if (jobCreateAndSendToAdapterViewedList?.isActive != true) {
            jobCreateAndSendToAdapterViewedList = viewModelScope.launch(Dispatchers.IO) {
                viewed = mutableListOf()
                Log.d(TAG, "createAndSendToAdapterViewedList(). Внутри Job.")
                if (viewedFilmsIds.isEmpty()) {
                    _viewedFlow.value = emptyList()
                } else {
                    viewedFilmsIds.forEach { filmId ->
                        val filmDbViewed = repository.getFilmDbViewed(filmId).first
                        if (filmDbViewed != null) viewed.add(filmDbViewed.convertToFilmItemData())
                        Log.d(TAG, "_viewedFlow.value=${viewed.toList().reversed()}")
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
                Log.d(TAG, "createAndSendToAdapterInterestedList(). Внутри Job.")
                if (interestedIds.isEmpty()) {
                    _interestedFlow.value = emptyList()
                } else {
                    interestedIds.forEach { interestedTable ->
                        val interestedItem: Interested? = when (interestedTable.type) {
                            0, 1 -> repository.getFilmDbViewed(interestedTable.id).first?.let {
                                Interested(
                                    type = interestedTable.type,
                                    interestedViewItem = it.convertToFilmItemData()
                                )
                            }
                            else -> repository.getPersonTable(interestedTable.id)?.let {
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