package edu.skillbox.skillcinema.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.skillbox.skillcinema.data.Repository
import edu.skillbox.skillcinema.models.FilmItemData
import edu.skillbox.skillcinema.models.FilmPremiere
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "ListPagePremieres.VM"

class ListPagePremieresViewModel(
    private val repository: Repository
) : ViewModel() {

    private val _state = MutableStateFlow<ViewModelState>(
        ViewModelState.Loading
    )
    val state = _state.asStateFlow()

    var premieresQuantity = 0
    var premieres: List<FilmPremiere> = emptyList()
    var premieresExtended: MutableList<FilmItemData> = mutableListOf()
    private val _premieresExtendedFlow = MutableStateFlow<List<FilmItemData>>(emptyList())
    val premieresExtendedFlow = _premieresExtendedFlow.asStateFlow()

    init {
        loadPremieres()
    }

    private fun loadPremieres() {
        viewModelScope.launch(Dispatchers.IO) {
            var error = false
            _state.value = ViewModelState.Loading
            Log.d(TAG, "Cостояние = ${_state.value}")

            val premieresLoadResult = repository.getPremieresList()
            premieres = premieresLoadResult.first
            if (premieresLoadResult.second) error = true
            premieresQuantity = premieres.size

            if (error) {
                _state.value = ViewModelState.Error
                Log.d(TAG, "Состояние ошибки VM")
            } else {
                _state.value = ViewModelState.Loaded
                Log.d(TAG, "Состояние уcпешного завершения загрузки VM")
                Log.d(TAG, "Запускаем loadExtendedFilmData() из loadPremieres()")
                loadExtendedFilmData()
            }
        }
    }

    fun loadExtendedFilmData() {
        Log.d(TAG, "loadExtendedFilmData()")
        viewModelScope.launch(Dispatchers.IO) {
            premieresExtended = mutableListOf()
            premieres.forEach { filmPremiere ->
                val extendedPremiereData = repository.extendPremiereData(filmPremiere)
                if (extendedPremiereData != null) {
                    premieresExtended.add(extendedPremiereData)
                    _premieresExtendedFlow.value = premieresExtended.toList()
                }
            }
        }
    }
}