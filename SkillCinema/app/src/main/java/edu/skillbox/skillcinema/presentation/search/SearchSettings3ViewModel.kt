package edu.skillbox.skillcinema.presentation.search

import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.skillbox.skillcinema.App
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "SearchSettings3.VM"

class SearchSettings3ViewModel(
    private val application: App
) : AndroidViewModel(application) {

    private val _state = MutableStateFlow<SearchViewModelState>(
        SearchViewModelState.Searching
    )
    val state = _state.asStateFlow()

    var chosenGenreCode = application.genre
    var genreSearchText: String? = application.genreSearchText

    fun setAndSaveGenre(code: Int?) {
        chosenGenreCode = code
        application.genre = code
    }

    val genreChipsActive: MutableMap<String, Boolean> = mutableMapOf(
        "Детектив" to true,
        "Триллер" to true,
        "Боевик" to true,
        "Криминальный фильм" to true,
        "Драма" to true,
        "Мелодрама" to true,
        "Семейный фильм" to true,
        "Комедия" to true,
        "Мультфильм" to true,
        "Детский фильм" to true,
        "Фэнтези" to true,
        "Фантастика" to true,
        "Ужасы" to true,
        "Военный фильм" to true,
        "Исторический фильм" to true
    )

    fun searchGenre(searchedText: String?) {
        Log.d(TAG, "Запущена searchGenre($searchedText)")
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = SearchViewModelState.Searching
            Log.d(TAG, "Состояние поиска")
            var searchFail = searchedText != null
            if (searchedText.isNullOrBlank()) {
                application.genreSearchText = null
            } else {
                application.genreSearchText = searchedText
                genreSearchText = searchedText
            }
            Log.d(TAG, "viewModel.genreSearchText = $genreSearchText")
            Log.d(TAG, "application.genreSearchText = ${application.genreSearchText}")
            val currentChipsState = genreChipsActive
            currentChipsState.forEach {
                if (searchedText == null) {
                    genreChipsActive[it.key] = true
                } else if (it.key.contains(searchedText, ignoreCase = true)) {
                    genreChipsActive[it.key] = true
                    searchFail = false
                } else {
                    genreChipsActive[it.key] = false
                }
            }
            delay(10L)
            if (searchFail) {
                _state.value = SearchViewModelState.SearchFailed
                Log.d(TAG, "ViewModel. Состояние неудачного поиска")
                Log.d(TAG, genreChipsActive.toString())
            } else {
                _state.value = SearchViewModelState.SearchSuccessfull
                Log.d(TAG, "ViewModel. Состояние успешного поиска")
                Log.d(TAG, genreChipsActive.toString())
            }
        }
    }
}