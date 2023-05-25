package edu.skillbox.skillcinema.presentation

import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.skillbox.skillcinema.App
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "SearchSettings2.VM"

class SearchSettings2ViewModel(
    private val application: App
) : AndroidViewModel(application) {

    private val _state = MutableStateFlow<SearchViewModelState>(
        SearchViewModelState.Searching
    )
    val state = _state.asStateFlow()

    var chosenCountryCode = application.country
//    var chosenCountryName: String? = countryCodeToName(application.country)
    var countrySearchText: String? = application.countrySearchText

    fun setAndSaveCountry(code: Int?) {
        chosenCountryCode = code
//        chosenCountryName = countryCodeToName(code)
        application.country = code
    }

    val countryChipsActive: MutableMap<String, Boolean> = mutableMapOf(
        "Россия" to true,
        "СССР" to true,
        "США" to true,
        "Франция" to true,
        "Великобритания" to true,
        "Германия" to true,
        "Германия (ФРГ)" to true,
        "Италия" to true,
        "Япония" to true,
        "Китай" to true,
        "Индия" to true,
        "Австралия" to true,
        "Канада" to true,
        "Испания" to true,
        "Мексика" to true,
        "Швейцария" to true,
        "Польша" to true,
        "Швеция" to true
    )

    fun searchCountry(searchedText: String?) {
        Log.d(TAG, "Запущена searchCountry($searchedText)")
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = SearchViewModelState.Searching
            Log.d(TAG, "Состояние поиска")
            var searchFail = searchedText != null
            if (searchedText.isNullOrBlank()) {
                application.countrySearchText = null
            } else {
                application.countrySearchText = searchedText
                countrySearchText = searchedText
            }
            Log.d(TAG, "viewModel.countrySearchText = $countrySearchText")
            Log.d(TAG, "application.countrySearchText = ${application.countrySearchText}")
            val currentChipsState = countryChipsActive
            currentChipsState.forEach {
                if (searchedText == null) {
                    countryChipsActive[it.key] = true
                } else if (it.key.contains(searchedText, ignoreCase = true)) {
                    countryChipsActive[it.key] = true
                    searchFail = false
                } else {
                    countryChipsActive[it.key] = false
                }
            }
            delay(10L)
            if (searchFail) {
                _state.value = SearchViewModelState.SearchFailed
                Log.d(TAG, "ViewModel. Состояние неудачного поиска")
                Log.d(TAG, countryChipsActive.toString())
            } else {
                _state.value = SearchViewModelState.SearchSuccessfull
                Log.d(TAG, "ViewModel. Состояние успешного поиска")
                Log.d(TAG, countryChipsActive.toString())
            }
        }
    }

//    private fun countryCodeToName(code: Int?): String? = when(code) {
//        1 -> "США"
//        2 -> "Швейцарии"
//        3 -> "Франции"
//        4 -> "Польши"
//        5 -> "Великобритании"
//        6 -> "Швеции"
//        7 -> "Индии"
//        8 -> "Испании"
//        9 -> "Германии"
//        10 -> "Италии"
//        12 -> "Германии (ФРГ)"
//        13 -> "Австралии"
//        14 -> "Канады"
//        15 -> "Мексики"
//        16 -> "Японии"
//        21 -> "Китая"
//        33 -> "СССР"
//        34 -> "России"
//        else -> null
//    }
}