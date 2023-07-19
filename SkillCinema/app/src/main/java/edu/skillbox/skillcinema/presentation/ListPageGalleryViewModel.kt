package edu.skillbox.skillcinema.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.skillbox.skillcinema.data.Repository
import edu.skillbox.skillcinema.models.ImageTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "ListPageGallery.VM"

class ListPageGalleryViewModel(
    private val repository: Repository
) : ViewModel() {

    private val _state = MutableStateFlow<ViewModelState>(
        ViewModelState.Loading
    )
    val state = _state.asStateFlow()

    var filmId: Int = 0

    var galleryQuantity = 0
    private val _gallery = MutableStateFlow<List<ImageTable>>(emptyList())
    val gallery = _gallery.asStateFlow()

    private val imagesStillList: MutableList<ImageTable> = mutableListOf()
    var quantityStill = 0
    private val _imagesStill = MutableStateFlow<List<ImageTable>>(emptyList())
    val imagesStill = _imagesStill.asStateFlow()

    private val imagesShootingList: MutableList<ImageTable> = mutableListOf()
    var quantityShooting = 0
    private val _imagesShooting = MutableStateFlow<List<ImageTable>>(emptyList())
    val imagesShooting = _imagesShooting.asStateFlow()

    val imagesPosterList: MutableList<ImageTable> = mutableListOf()
    var quantityPoster = 0
    private val _imagesPoster = MutableStateFlow<List<ImageTable>>(emptyList())
    val imagesPoster = _imagesPoster.asStateFlow()

    val imagesFanArtList: MutableList<ImageTable> = mutableListOf()
    var quantityFanArt = 0
    private val _imagesFanArt = MutableStateFlow<List<ImageTable>>(emptyList())
    val imagesFanArt = _imagesFanArt.asStateFlow()

    val imagesPromoList: MutableList<ImageTable> = mutableListOf()
    var quantityPromo = 0
    private val _imagesPromo = MutableStateFlow<List<ImageTable>>(emptyList())
    val imagesPromo = _imagesPromo.asStateFlow()

    val imagesConceptList: MutableList<ImageTable> = mutableListOf()
    var quantityConcept = 0
    private val _imagesConcept = MutableStateFlow<List<ImageTable>>(emptyList())
    val imagesConcept = _imagesConcept.asStateFlow()

    val imagesWallpaperList: MutableList<ImageTable> = mutableListOf()
    var quantityWallpaper = 0
    private val _imagesWallpaper = MutableStateFlow<List<ImageTable>>(emptyList())
    val imagesWallpaper = _imagesWallpaper.asStateFlow()

    val imagesCoverList: MutableList<ImageTable> = mutableListOf()
    var quantityCover = 0
    private val _imagesCover = MutableStateFlow<List<ImageTable>>(emptyList())
    val imagesCover = _imagesCover.asStateFlow()

    val imagesScreenshotList: MutableList<ImageTable> = mutableListOf()
    var quantityScreenshot = 0
    private val _imagesScreenshot = MutableStateFlow<List<ImageTable>>(emptyList())
    val imagesScreenshot = _imagesScreenshot.asStateFlow()

    var chosenType: Int = 0

    var error = true

    fun loadGallery(filmId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = ViewModelState.Loading
            val jobLoading = viewModelScope.launch(Dispatchers.IO) {
                val allGalleryLoadResult: Pair<List<ImageTable>?, Boolean> =
                    repository.getAllGallery(filmId)
                val imageTableList: List<ImageTable>? = allGalleryLoadResult.first
                if (allGalleryLoadResult.second || imageTableList.isNullOrEmpty()) {
                    Log.d(TAG, "Ошибка загрузки List<ImageTable>")
                } else {
                    error = false // Если хоть одно изображение получено, ошибки загрузки нет.
                    galleryQuantity = imageTableList.size
                    _gallery.value = imageTableList
                    imageTableList.forEach { image ->
                        when (image.type) {
                            "STILL" -> imagesStillList.add(image)
                            "SHOOTING" -> imagesShootingList.add(image)
                            "POSTER" -> imagesPosterList.add(image)
                            "FAN_ART" -> imagesFanArtList.add(image)
                            "PROMO" -> imagesPromoList.add(image)
                            "CONCEPT" -> imagesConceptList.add(image)
                            "WALLPAPER" -> imagesWallpaperList.add(image)
                            "COVER" -> imagesCoverList.add(image)
                            "SCREENSHOT" -> imagesScreenshotList.add(image)
                        }
                    }
                    quantityStill = imagesStillList.size
                    quantityShooting = imagesShootingList.size
                    quantityPoster = imagesPosterList.size
                    quantityFanArt = imagesFanArtList.size
                    quantityPromo = imagesPromoList.size
                    quantityConcept = imagesConceptList.size
                    quantityWallpaper = imagesWallpaperList.size
                    quantityCover = imagesCoverList.size
                    quantityScreenshot = imagesScreenshotList.size

                    _imagesStill.value = imagesStillList
                    _imagesShooting.value = imagesShootingList
                    _imagesPoster.value = imagesPosterList
                    _imagesFanArt.value = imagesFanArtList
                    _imagesPromo.value = imagesPromoList
                    _imagesConcept.value = imagesConceptList
                    _imagesWallpaper.value = imagesWallpaperList
                    _imagesCover.value = imagesCoverList
                    _imagesScreenshot.value = imagesScreenshotList
                }
            }
            jobLoading.join()
            if (error) {
                Log.d(TAG, "Ошибка загрузки изображений")
                _state.value = ViewModelState.Error
            } else {
                Log.d(TAG, "Состояние уcпешного завершения загрузки VM")
                _state.value = ViewModelState.Loaded
            }
        }
    }
}