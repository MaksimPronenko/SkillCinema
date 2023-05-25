package edu.skillbox.skillcinema.presentation

import androidx.lifecycle.AndroidViewModel
import edu.skillbox.skillcinema.App
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val TAG = "SearchSettings1.VM"

class SearchSettings1ViewModel(
    private val application: App
) : AndroidViewModel(application) {

    private val _state = MutableStateFlow<SearchViewModelState>(
        SearchViewModelState.Searching
    )
    val state = _state.asStateFlow()

    var chosenCountryCode = application.country
    var chosenGenreCode = application.genre
    var order = application.order
    var type = application.type
    var ratingFrom = application.ratingFrom
    var ratingTo = application.ratingTo
    var yearFrom = application.yearFrom
    var yearTo = application.yearTo
    var showWatched = application.showWatched

    fun refreshAppData() {
        chosenCountryCode = application.country
        chosenGenreCode = application.genre
//        order = application.order
//        type = application.type
        ratingFrom = application.ratingFrom
        ratingTo = application.ratingTo
        yearFrom = application.yearFrom
        yearTo = application.yearTo
//        showWatched = application.showWatched
    }

    fun setAndSaveOrder(chosenOrder: String) {
        order = chosenOrder
        application.order = chosenOrder
    }

    fun setAndSaveType(chosenType: String) {
        type = chosenType
        application.type = chosenType
    }

    fun setAndSaveRatingFrom(chosenRatingFrom: Int) {
        ratingFrom = chosenRatingFrom
        application.ratingFrom = chosenRatingFrom
    }

    fun setAndSaveRatingTo(chosenRatingTo: Int) {
        ratingTo = chosenRatingTo
        application.ratingTo = chosenRatingTo
    }

    fun changeAndSaveShowWatched(): Boolean {
        showWatched = !showWatched
        application.showWatched = showWatched
        return showWatched
    }
}