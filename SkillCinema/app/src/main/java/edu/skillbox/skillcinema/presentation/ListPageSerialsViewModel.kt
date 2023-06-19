package edu.skillbox.skillcinema.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import edu.skillbox.skillcinema.data.Repository
import edu.skillbox.skillcinema.data.SerialsPagingSource
import edu.skillbox.skillcinema.models.FilmItemData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "ListPageSerials.VM"

class ListPageSerialsViewModel(
    private val repository: Repository
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
            serialsPagesQuantity =
                repository.getSerials(1)?.totalPages ?: 0
            pagedSerials = Pager(
                config = PagingConfig(pageSize = 20),
                pagingSourceFactory = {
                    SerialsPagingSource(repository)
                }
            ).flow.cachedIn(viewModelScope)
            if (serialsPagesQuantity == 0) error = true

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