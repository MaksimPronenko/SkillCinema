package edu.skillbox.skillcinema.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.skillbox.skillcinema.data.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "ImagePager.VM"

class ImagePagerViewModel(
    private val repository: Repository
) : ViewModel() {

    private val _state = MutableStateFlow<ViewModelState>(
        ViewModelState.Loading
    )
    val state = _state.asStateFlow()

    var filmId: Int = 0

    private val _images = MutableStateFlow<List<String>>(emptyList())
    val images = _images.asStateFlow()

    var error = true

    fun loadImages(
        filmId: Int,
        currentImage: String,
        imagesType: Int
    ) {
        Log.d(
            TAG,
            "loadImages(filmId = $filmId, currentImage = $currentImage, imagesType = $imagesType"
        )
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = ViewModelState.Loading
            // Загрузка изображений из БД или из API с записью в БД
            val imagesLoadResult: Pair<List<String>?, Boolean> =
                repository.getImagesWithCurrentImage(
                    filmId = filmId,
                    currentImage = currentImage,
                    imagesType = imagesType
                )
            val imagesList: List<String>? = imagesLoadResult.first
            if (imagesLoadResult.second || imagesList.isNullOrEmpty()) {
                Log.d(TAG, "Ошибка загрузки изображений")
                _state.value = ViewModelState.Error
            } else {
                _images.value = imagesList
                Log.d(TAG, "Состояние уcпешного завершения загрузки VM")
                _state.value = ViewModelState.Loaded
            }
        }
    }
}