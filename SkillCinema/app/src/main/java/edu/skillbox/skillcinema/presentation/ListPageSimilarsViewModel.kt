package edu.skillbox.skillcinema.presentation

import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.skillbox.skillcinema.App
import edu.skillbox.skillcinema.data.Repository
import edu.skillbox.skillcinema.models.SimilarFilm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "ListPageSimilars"

class ListPageSimilarsViewModel(
    private val repository: Repository
) : ViewModel() {

    private val _state = MutableStateFlow<ViewModelState>(
        ViewModelState.Loading
    )
    val state = _state.asStateFlow()

    var filmId: Int = 0

    var similarsQuantity = 0
    private val _similars = MutableStateFlow<List<SimilarFilm>>(emptyList())
    val similars = _similars.asStateFlow()

    fun loadSimilars(filmId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = ViewModelState.Loading
            val jobLoading = viewModelScope.launch(Dispatchers.IO) {
                kotlin.runCatching {
                    repository.getSimilars(filmId)
                }.fold(
                    onSuccess = {
                        _similars.value = it.items
                        similarsQuantity = it.total
                    },
                    onFailure = { Log.d("Похожие фильмы", it.message ?: "Ошибка загрузки") }
                )
            }
            jobLoading.join()
            _state.value = ViewModelState.Loaded
        }
    }
}