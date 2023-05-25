package edu.skillbox.skillcinema.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.skillbox.skillcinema.data.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BottomDialogViewModel(
    private val repository: Repository,
) : ViewModel() {
    private val _state = MutableStateFlow<ViewModelState>(
        ViewModelState.Loading
    )
    val state = _state.asStateFlow()

    var filmId: Int = 0
    var posterSmall = ""
    var name = ""
    var year = ""
    var genres = ""
    var newCollectionName = ""

    fun loadFilmInfo(filmId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = ViewModelState.Loading
            val jobLoading = viewModelScope.launch(Dispatchers.IO) {
                val filmInfo = repository.getFilmInfo(filmId)
            }
            jobLoading.join()
            _state.value = ViewModelState.Loaded
        }
    }
}