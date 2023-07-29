package edu.skillbox.skillcinema.presentation.listPageSerials

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import edu.skillbox.skillcinema.data.RepositoryMainLists
import edu.skillbox.skillcinema.data.SerialsPagingSource
import edu.skillbox.skillcinema.models.filmAndSerial.film.FilmItemData
import edu.skillbox.skillcinema.presentation.ViewModelState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "ListPageSerials.VM"

class ListPageSerialsViewModel(
    private val repositoryMainLists: RepositoryMainLists
) : ViewModel() {

    private val _state = MutableStateFlow<ViewModelState>(
        ViewModelState.Loading
    )
    val state = _state.asStateFlow()

    var serialsPagesQuantity = 0
    lateinit var pagedSerials: Flow<PagingData<FilmItemData>>

    fun loadSerials() {
        viewModelScope.launch(Dispatchers.IO) {
            var error = false
            _state.value = ViewModelState.Loading
            Log.d(TAG, "Cостояние = ${_state.value}")

            val serialsLoadResult = repositoryMainLists.getSerials(page = 1)
            if (serialsLoadResult.second) error = true
            serialsPagesQuantity = serialsLoadResult.first?.totalPages ?: 0
            if (serialsPagesQuantity == 0) error = true

            pagedSerials = Pager(
                config = PagingConfig(pageSize = 20),
                pagingSourceFactory = {
                    SerialsPagingSource(repositoryMainLists)
                }
            ).flow.cachedIn(viewModelScope)

            if (error) {
                _state.value = ViewModelState.Error
                Log.d(TAG, "Cостояние = ${_state.value}")
            } else {
                _state.value = ViewModelState.Loaded
                Log.d(TAG, "Cостояние = ${_state.value}")
            }
        }
    }
}