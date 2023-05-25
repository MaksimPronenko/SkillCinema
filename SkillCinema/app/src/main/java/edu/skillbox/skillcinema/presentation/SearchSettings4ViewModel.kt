package edu.skillbox.skillcinema.presentation

import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.skillbox.skillcinema.App
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.floor

private const val TAG = "SearchSettings4.VM"

class SearchSettings4ViewModel(
    private val application: App
) : AndroidViewModel(application) {

    private val _state = MutableStateFlow<SearchViewModelState>(
        SearchViewModelState.Searching
    )
    val state = _state.asStateFlow()

    private val currentYear =
        Calendar.getInstance().get(Calendar.YEAR) // Нужен для формирования шага.

    var yearFrom = application.yearFrom
    var yearTo = application.yearTo

    var listYearFrom = generateListOf12Years(application.yearFrom ?: application.yearTo)
    private val _listYearFromChannel = Channel<List<Int>>()
    val listYearFromFlow = _listYearFromChannel.receiveAsFlow()

    var listYearTo = generateListOf12Years(application.yearTo)
    private val _listYearToChannel = Channel<List<Int>>()
    val listYearToFlow = _listYearToChannel.receiveAsFlow()

    fun setPeriodFrom(chosenYearFrom: Int?) {
        Log.d(TAG, "setYearFrom($chosenYearFrom)")
        yearFrom = chosenYearFrom
    }

    fun setPeriodTo(chosenYearTo: Int?) {
        Log.d(TAG, "setYearTo($chosenYearTo)")
        yearTo = chosenYearTo
    }

    fun saveChosenPeriod() {
        application.yearFrom = yearFrom
        application.yearTo = yearTo
    }

    private fun generateListOf12Years(yearToBeIncluded: Int?): List<Int> {
        Log.d(TAG, "generateListOf12Years($yearToBeIncluded)")
        val resultList: MutableList<Int> = emptyList<Int>().toMutableList()
        // Если год для включения в список отсутствует, то возвращаем список,
        // в котором текущий год является последним. Шаг сдвига списка назад listShiftStep = 0.
        val listShiftStep = if (yearToBeIncluded == null) 0
        else {
            if (currentYear >= yearToBeIncluded) (currentYear - yearToBeIncluded) / 12
            else floor((currentYear - yearToBeIncluded) / 12.0).toInt()
        }
        for (i in 0..11) {
            val year = currentYear - listShiftStep * 12 - 11 + i
            resultList.add(year)
        }
        Log.d(TAG, "resultList = $resultList")
        return resultList.toList()
    }

    private fun changeListOfYears(listToChange: List<Int>, direction: Boolean): List<Int> {
        // direction = true - назад на 12 лет
        val resultList: MutableList<Int> = emptyList<Int>().toMutableList()
        val step = if (direction) -12
        else 12
        listToChange.forEach { year ->
            resultList.add(year + step)
        }
        return resultList.toList()
    }

    fun changeListYearFrom(direction: Boolean) {
        Log.d(TAG, "changeListYearFrom($direction)")
        listYearFrom = changeListOfYears(listYearFrom, direction)
        Log.d(TAG, "listYearFrom = $listYearFrom")
        viewModelScope.launch(Dispatchers.IO) {
            _listYearFromChannel.send(element = listYearFrom)
        }
    }

    fun changeListYearTo(direction: Boolean) {
        Log.d(TAG, "changeListYearTo($direction)")
        listYearTo = changeListOfYears(listYearTo, direction)
        Log.d(TAG, "listYearTo = $listYearTo")
        viewModelScope.launch(Dispatchers.IO) {
            _listYearToChannel.send(element = listYearTo)
        }
    }

    fun checkChoiceLogic(): Boolean {
        return if (yearFrom == null || yearTo == null) true
        else yearFrom!! <= yearTo!!
    }
}