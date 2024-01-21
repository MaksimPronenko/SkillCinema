package edu.skillbox.skillcinema.presentation.listPageSimilars

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.skillbox.skillcinema.data.RepositoryFilmAndSerial
import edu.skillbox.skillcinema.models.filmAndSerial.film.FilmItemData
import edu.skillbox.skillcinema.models.filmAndSerial.similar.SimilarFilmTable
import edu.skillbox.skillcinema.presentation.ViewModelState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "ListPageSimilars.VM"

class ListPageSimilarsViewModel(
    private val repositoryFilmAndSerial: RepositoryFilmAndSerial
) : ViewModel() {

    private val _state = MutableStateFlow<ViewModelState>(
        ViewModelState.Loading
    )
    val state = _state.asStateFlow()

    var filmId: Int = 0

    var similarFilmTableList: List<SimilarFilmTable>? = null
    private var similarsQuantity = 0
    private var similars: MutableList<FilmItemData> = mutableListOf()
    private val _similarsFlow = MutableStateFlow<List<FilmItemData>>(emptyList())
    val similarsFlow = _similarsFlow.asStateFlow()

    fun loadSimilars(filmId: Int) {
        Log.d(TAG, "loadSimilars($filmId)")
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = ViewModelState.Loading
            var error = false

            val jobGetData = viewModelScope.launch(Dispatchers.IO) {
                // Загрузка похожих фильмов из БД или из API с записью в БД
                val similarsLoadResult: Pair<List<SimilarFilmTable>?, Boolean> = repositoryFilmAndSerial.getSimilarFilmTableList(filmId)
                similarFilmTableList = similarsLoadResult.first
                if (similarsLoadResult.second) {
                    error = true
                    Log.d(TAG, "Ошибка загрузки List<SimilarFilmTable>")
                }
                similarsQuantity = similarFilmTableList?.size ?: 0
            }
            jobGetData.join()

            if (error) {
                _state.value = ViewModelState.Error
                Log.d(TAG, "Состояние ошибки VM")
            } else {
                _state.value = ViewModelState.Loaded
                Log.d(TAG, "Состояние уcпешного завершения загрузки VM")

//                loadSimilarFilmsData()
            }
        }
    }

    fun loadSimilarFilmsData() {
        viewModelScope.launch(Dispatchers.IO) {
            similars = mutableListOf()
            Log.d(
                TAG,
                "loadSimilarFilmsData(), similarFilmTableList: ${similarFilmTableList?.size ?: 0}"
            )
            similarFilmTableList?.forEach { similarFilmTable ->
                val filmDbViewed = repositoryFilmAndSerial.getFilmDbViewed(similarFilmTable.similarFilmId).first
                if (filmDbViewed != null) {
                    val filmItemData: FilmItemData = filmDbViewed.convertToFilmItemData()
                    similars.add(filmItemData)
                    _similarsFlow.value = similars.toList()
                }
            }
        }
    }
}