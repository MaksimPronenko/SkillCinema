package edu.skillbox.skillcinema.presentation.serialContent

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.skillbox.skillcinema.data.RepositoryFilmAndSerial
import edu.skillbox.skillcinema.models.filmAndSerial.serial.SeasonsWithEpisodes
import edu.skillbox.skillcinema.models.filmAndSerial.serial.SerialInfoDb
import edu.skillbox.skillcinema.presentation.ViewModelState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "SerialContent.VM"

class SerialContentViewModel(
    private val repositoryFilmAndSerial: RepositoryFilmAndSerial
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
                val serialInfoDbResult: Pair<SerialInfoDb?, Boolean> = repositoryFilmAndSerial.getSerialInfoDb(filmId)
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