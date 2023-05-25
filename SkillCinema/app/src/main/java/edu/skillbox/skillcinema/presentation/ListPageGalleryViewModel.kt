package edu.skillbox.skillcinema.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.skillbox.skillcinema.data.Repository
import edu.skillbox.skillcinema.models.ImageWithType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "ListPageGallery"

class ListPageGalleryViewModel(
    private val repository: Repository
) : ViewModel() {

    private val _state = MutableStateFlow<ViewModelState>(
        ViewModelState.Loading
    )
    val state = _state.asStateFlow()

    var filmId: Int = 0

    var galleryQuantity = 0
    private val _gallery = MutableStateFlow<List<ImageWithType>>(emptyList())
    val gallery = _gallery.asStateFlow()

    private val imagesStillList: MutableList<ImageWithType> = emptyList<ImageWithType>().toMutableList()
    var quantityStill = 0
    private val _imagesStill = MutableStateFlow<List<ImageWithType>>(emptyList())
    val imagesStill = _imagesStill.asStateFlow()

    private val imagesShootingList: MutableList<ImageWithType> = emptyList<ImageWithType>().toMutableList()
    var quantityShooting = 0
    private val _imagesShooting = MutableStateFlow<List<ImageWithType>>(emptyList())
    val imagesShooting = _imagesShooting.asStateFlow()

    val imagesPosterList: MutableList<ImageWithType> = emptyList<ImageWithType>().toMutableList()
    var quantityPoster = 0
    private val _imagesPoster = MutableStateFlow<List<ImageWithType>>(emptyList())
    val imagesPoster = _imagesPoster.asStateFlow()

    val imagesFanArtList: MutableList<ImageWithType> = emptyList<ImageWithType>().toMutableList()
    var quantityFanArt = 0
    private val _imagesFanArt = MutableStateFlow<List<ImageWithType>>(emptyList())
    val imagesFanArt = _imagesFanArt.asStateFlow()

    val imagesPromoList: MutableList<ImageWithType> = emptyList<ImageWithType>().toMutableList()
    var quantityPromo = 0
    private val _imagesPromo = MutableStateFlow<List<ImageWithType>>(emptyList())
    val imagesPromo = _imagesPromo.asStateFlow()

    val imagesConceptList: MutableList<ImageWithType> = emptyList<ImageWithType>().toMutableList()
    var quantityConcept = 0
    private val _imagesConcept = MutableStateFlow<List<ImageWithType>>(emptyList())
    val imagesConcept = _imagesConcept.asStateFlow()

    val imagesWallpaperList: MutableList<ImageWithType> = emptyList<ImageWithType>().toMutableList()
    var quantityWallpaper = 0
    private val _imagesWallpaper = MutableStateFlow<List<ImageWithType>>(emptyList())
    val imagesWallpaper = _imagesWallpaper.asStateFlow()

    val imagesCoverList: MutableList<ImageWithType> = emptyList<ImageWithType>().toMutableList()
    var quantityCover = 0
    private val _imagesCover = MutableStateFlow<List<ImageWithType>>(emptyList())
    val imagesCover = _imagesCover.asStateFlow()

    val imagesScreenshotList: MutableList<ImageWithType> = emptyList<ImageWithType>().toMutableList()
    var quantityScreenshot = 0
    private val _imagesScreenshot = MutableStateFlow<List<ImageWithType>>(emptyList())
    val imagesScreenshot = _imagesScreenshot.asStateFlow()

    var chosenType: Int = 0

    fun loadGallery(filmId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = ViewModelState.Loading
            val jobLoading = viewModelScope.launch(Dispatchers.IO) {
                kotlin.runCatching {
                    repository.getAllGallery(filmId)
                }.fold(
                    onSuccess = {
                        _gallery.value = it
                        galleryQuantity = it.size

                        it.forEach { image ->
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
                    },
                    onFailure = { Log.d("Галерея", it.message ?: "Ошибка загрузки") }
                )
            }
            jobLoading.join()
            _state.value = ViewModelState.Loaded
        }
    }
}