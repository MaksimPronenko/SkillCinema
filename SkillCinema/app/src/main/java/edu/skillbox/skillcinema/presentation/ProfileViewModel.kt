package edu.skillbox.skillcinema.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.skillbox.skillcinema.data.Repository
import edu.skillbox.skillcinema.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private val _collectionFlow = MutableStateFlow<List<CollectionTable>>(emptyList())
    val collectionFlow = _collectionFlow.asStateFlow()

    var interestedQuantity = 0
    private val _interestedFlow = MutableStateFlow<List<FilmTable>>(emptyList())
    val interestedFlow = _interestedFlow.asStateFlow()

    init {
        loadProfileData()
    }

    private fun loadProfileData() {
        Log.d(TAG, "Запущена loadProfileData()")
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = ViewModelState.Loading
            var error = false

            if (error) {
                _state.value = ViewModelState.Error
                Log.d(TAG, "Состояние ошибки")
            } else {
                _state.value = ViewModelState.Loaded
                Log.d(TAG, "Состояние успешного завершения загрузки")
            }
        }
    }
}