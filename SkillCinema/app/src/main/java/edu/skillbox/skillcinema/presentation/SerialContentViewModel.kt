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

private const val TAG = "SerialContentVM"

class SerialContentViewModel(
    private val repository: Repository
) : ViewModel() {

    private val _state = MutableStateFlow<ViewModelState>(
        ViewModelState.Loading
    )
    val state = _state.asStateFlow()

    var filmId: Int = 0
    var name: String = ""

    var chosenSeason: Int = 1

    var quantityOfSeasons = 0
    var seasonsList: List<Season> = emptyList()

    fun loadSerialInfo(filmId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = ViewModelState.Loading
            var error = false
            val jobLoading = viewModelScope.launch(Dispatchers.IO) {
                kotlin.runCatching {
                    repository.getSerialInfo(filmId)
                }.fold(
                    onSuccess = {
                        quantityOfSeasons = it.total
                        seasonsList = it.items
                    },
                    onFailure = {
                        Log.d(TAG, "${it.message} Информация о сериале. Ошибка загрузки")
                        error = true
                    }
                )
            }
            jobLoading.join()
            if (error) {
                _state.value = ViewModelState.Error
            } else {
                _state.value = ViewModelState.Loaded
            }
        }
    }

//    private fun convertLength(lengthInt: Int?): String? {
//        if (lengthInt == null || lengthInt <= 0) return null
//        val hours: Int = lengthInt / 60
//        val minutes: Int = lengthInt % 60
//        val hoursText: String = if (hours == 0) "" else " ч"
//        val minutesText: String = if (minutes == 0) "" else " мин"
//        val hoursPart: String = if (hours > 0) {
//            hours.toString() + hoursText
//        } else ""
//        val separator: String = if (hours > 0 && minutes > 0) " " else ""
//        val minutesPart: String = if (minutes > 0) {
//            minutes.toString() + minutesText
//        } else ""
//        return hoursPart + separator + minutesPart
//    }

//    private fun countiesListToString(countries: List<Country>): String {
//        var resultString = ""
//        countries.forEachIndexed { index, country ->
//            if (index == 0) resultString = country.country
//            else resultString += ", " + country.country
//        }
//        return resultString
//    }

//    private fun convertAgeLimit(ratingAgeLimits: String?): String? =
//        if (ratingAgeLimits != null) ratingAgeLimits.removePrefix("age") + "+" else null
//
//    override fun onCleared() {
//        super.onCleared()
//        filmId = 0
//    }

//    fun seasonQuantityToText(quantity: Int): String {
//        val remOfDivBy10 = quantity % 10
//        val remOfDivBy100 = quantity % 100
//        return "$quantity сезон" + when(remOfDivBy10){
//            1 -> if(remOfDivBy100 == 11) "ов" else ""
//            in 2..4 -> if(remOfDivBy100 == 12) "ов" else "а"
//            else -> "ов"
//        }
//    }

    fun episodeQuantityToText(quantity: Int): String {
        val remOfDivBy10 = quantity % 10
        val remOfDivBy100 = quantity % 100
        return "$quantity сери" + when(remOfDivBy10){
            1 -> if(remOfDivBy100 == 11) "й" else "я"
            in 2..4 -> if(remOfDivBy100 == 12) "й" else "и"
            else -> "й"
        }
    }
}