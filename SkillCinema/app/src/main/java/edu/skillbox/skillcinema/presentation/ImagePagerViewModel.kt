package edu.skillbox.skillcinema.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.skillbox.skillcinema.data.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ImagePagerViewModel(
    private val repository: Repository
) : ViewModel() {

    private val _state = MutableStateFlow<ViewModelState>(
        ViewModelState.Loading
    )
    val state = _state.asStateFlow()

    var filmId: Int = 0

    var imagesQuantity = 0
    private val _images = MutableStateFlow<List<String>>(emptyList())
    val images = _images.asStateFlow()

    fun loadImages(
        filmId: Int,
        currentImage: String,
        imagesType: Int
    ) {
        Log.d("Pager", "VM. currentImage = $currentImage")
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = ViewModelState.Loading
            kotlin.runCatching {
                repository.getImages(filmId, currentImage, imagesType)
            }.fold(
                onSuccess = {
                    _images.value = it
                    imagesQuantity = it.size
                    _state.value = ViewModelState.Loaded
                },
                onFailure = {
                    _state.value = ViewModelState.Error
                    Log.d("Изображения Pager", it.message ?: "Ошибка загрузки")
                }
            )
        }
    }
}