package edu.skillbox.skillcinema.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.skillbox.skillcinema.data.Repository
import edu.skillbox.skillcinema.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FilmViewModel(
    private val repository: Repository,
) : ViewModel() {
    private val _state = MutableStateFlow<ViewModelState>(
        ViewModelState.Loading
    )
    val state = _state.asStateFlow()

    var filmId: Int = 0

    var name: String? = null
    var poster: String? = null
    var rating: Double? = null
    var year: String? = null
    var genres: String? = null
    var countries: String? = null
    var filmLength: String? = null
    var ageLimit: String? = null
    var shortDescription: String? = null
    var description: String? = null

    var actorsQuantity = 0
    private val _actors = MutableStateFlow<List<StaffInfo>>(emptyList())
    val actors = _actors.asStateFlow()

    var staffQuantity = 0
    private val _staff = MutableStateFlow<List<StaffInfo>>(emptyList())
    val staff = _staff.asStateFlow()

    var galleryQuantity = 0
    private val _gallery = MutableStateFlow<List<ImageWithType>>(emptyList())
    val gallery = _gallery.asStateFlow()

    var similarsQuantity = 0
    private val _similars = MutableStateFlow<List<SimilarFilm>>(emptyList())
    val similars = _similars.asStateFlow()

    fun loadFilmInfo(filmId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = ViewModelState.Loading
            val jobLoading = viewModelScope.launch(Dispatchers.IO) {
                val loadResult = repository.getFilmInfo(filmId)
                name = loadResult.nameRu ?: loadResult.nameEn ?: loadResult.nameOriginal ?: ""
                poster = loadResult.posterUrl
                    ?: loadResult.posterUrlPreview
                rating = loadResult.ratingKinopoisk
                year = if (loadResult.year != null) loadResult.year.toString() else ""
                genres = genresListToString(loadResult.genres)
                countries = countiesListToString(loadResult.countries)
                filmLength = convertLength(loadResult.filmLength)
                ageLimit = convertAgeLimit(loadResult.ratingAgeLimits)
                shortDescription = loadResult.shortDescription
                description = loadResult.description

                kotlin.runCatching {
                    repository.getActorsAndStaff(filmId)
                }.fold(
                    onSuccess = {
                        _actors.value = it.actorsList
                        actorsQuantity = it.actorsList.size
                        _staff.value = it.staffList
                        staffQuantity = it.staffList.size
                    },
                    onFailure = { Log.d("Персонал", it.message ?: "Ошибка загрузки") }
                )

                kotlin.runCatching {
                    repository.getImages(filmId)
                }.fold(
                    onSuccess = {
                        _gallery.value = it
                        galleryQuantity = it.size
                    },
                    onFailure = { Log.d("Галлерея", it.message ?: "Ошибка загрузки") }
                )

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

    private fun convertLength(lengthInt: Int?): String? {
        if (lengthInt == null || lengthInt <= 0) return null
        val hours: Int = lengthInt / 60
        val minutes: Int = lengthInt % 60
        val hoursText: String = if (hours == 0) "" else " ч"
        val minutesText: String = if (minutes == 0) "" else " мин"
        val hoursPart: String = if (hours > 0) {
            hours.toString() + hoursText
        } else ""
        val separator: String = if (hours > 0 && minutes > 0) " " else ""
        val minutesPart: String = if (minutes > 0) {
            minutes.toString() + minutesText
        } else ""
        return hoursPart + separator + minutesPart
    }

    private fun genresListToString(genres: List<Genre>): String {
        var resultString = ""
        genres.forEachIndexed { index, genre ->
            if (index == 0) resultString = genre.genre
            else resultString += ", " + genre.genre
        }
        return resultString
    }

    private fun countiesListToString(countries: List<Country>): String {
        var resultString = ""
        countries.forEachIndexed { index, country ->
            if (index == 0) resultString = country.country
            else resultString += ", " + country.country
        }
        return resultString
    }

    private fun convertAgeLimit(ratingAgeLimits: String?): String? =
        if (ratingAgeLimits != null) ratingAgeLimits.removePrefix("age") + "+" else null
}