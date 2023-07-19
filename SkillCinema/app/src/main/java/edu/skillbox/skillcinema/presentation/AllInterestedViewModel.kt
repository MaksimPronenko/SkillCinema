package edu.skillbox.skillcinema.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.skillbox.skillcinema.data.Repository
import edu.skillbox.skillcinema.models.Interested
import edu.skillbox.skillcinema.models.InterestedTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "AllInterested.VM"

class AllInterestedViewModel(
    private val repository: Repository
) : ViewModel() {

    private val _state = MutableStateFlow<ViewModelState>(
        ViewModelState.Loading
    )
    val state = _state.asStateFlow()

    var interestedIds: List<InterestedTable> = listOf()
    var interested: MutableList<Interested> = mutableListOf()
    private val _interestedFlow = MutableStateFlow<List<Interested>>(emptyList())
    val interestedFlow = _interestedFlow.asStateFlow()

    var jobCreateAndSendToAdapterInterestedList: Job? = null

    init {
        loadData()
    }

    fun loadData() {
        Log.d(TAG, "Запущена loadData()")
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = ViewModelState.Loading

            interestedIds = repository.getInterestedList().orEmpty()

            _state.value = ViewModelState.Loaded
            Log.d(TAG, "Состояние завершения загрузки")

            Log.d(TAG, "Запускаем createAndSendToAdapterInterestedList() из loadProfileData()")
            createAndSendToAdapterInterestedList()
        }
    }

    fun clearAllInterested() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = ViewModelState.Loading
            Log.d(TAG, "Состояние загрузки")
            repository.removeAllInterested()
            interested = mutableListOf()
            interestedIds = repository.getInterestedList().orEmpty()
            Log.d(TAG, "interestedIds = $interestedIds")
            _state.value = ViewModelState.Loaded
            Log.d(TAG, "Состояние рабочее")
            Log.d(TAG, "Запускаем createAndSendToAdapterInterestedList() из clearAllInterested()")
            createAndSendToAdapterInterestedList()
        }
    }

    fun createAndSendToAdapterInterestedList() {
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