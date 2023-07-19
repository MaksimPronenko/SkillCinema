package edu.skillbox.skillcinema.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.skillbox.skillcinema.data.Repository
import edu.skillbox.skillcinema.models.SeasonsWithEpisodes
import edu.skillbox.skillcinema.models.SerialInfoDb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "SerialContent.VM"

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
    var firstSeason: Int? = null

    var quantityOfSeasons = 0
    var seasonsList: List<SeasonsWithEpisodes> = emptyList()

    fun loadSerialInfo(filmId: Int) {
        Log.d(TAG, "Вызвана loadSerialInfo($filmId)")
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = ViewModelState.Loading
            var error = false
            val jobLoading = viewModelScope.launch(Dispatchers.IO) {
                // Загрузка данных сериала из БД или из API с записью в БД
                val serialInfoDbResult: Pair<SerialInfoDb?, Boolean> = repository.getSerialInfoDb(filmId)
                val serialInfoDb: SerialInfoDb? = serialInfoDbResult.first
                if (serialInfoDbResult.second) error = true
                serialInfoDb?.let {
                    quantityOfSeasons = it.serialTable.totalSeasons
                    seasonsList = it.seasonsWithEpisodes
                }
                firstSeason = seasonsList[0].seasonTable.seasonNumber
                Log.d(TAG, "loadSerialInfo($filmId): firstSeason = $firstSeason")
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