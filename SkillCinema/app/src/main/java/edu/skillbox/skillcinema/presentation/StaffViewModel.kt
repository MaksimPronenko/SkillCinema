package edu.skillbox.skillcinema.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.skillbox.skillcinema.data.Repository
import edu.skillbox.skillcinema.models.FilmOfPersonTable
import edu.skillbox.skillcinema.models.FilmOfStaff
import edu.skillbox.skillcinema.models.PersonInfoDb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "Staff.VM"

class StaffViewModel(
    private val repository: Repository
) : ViewModel() {

    private val _state = MutableStateFlow<ViewModelState>(
        ViewModelState.Loading
    )
    val state = _state.asStateFlow()

    var staffId: Int = 0

    var name: String? = null
    var photo: String? = null
    var profession: String = ""
    var filmsQuantity: Int = 0

    //    var personFilms: List<FilmOfPersonTable> = emptyList()
    private var uniqueFilmIDs: MutableList<Int> = emptyList<Int>().toMutableList()
    var filmsUnique: MutableList<FilmOfPersonTable> = emptyList<FilmOfPersonTable>().toMutableList()
    var bestFilms: List<FilmOfPersonTable> = emptyList()

    //    var detailedFilms: List<FilmOfStaff> = emptyList()
    var detailedFilms: MutableList<FilmOfStaff> = emptyList<FilmOfStaff>().toMutableList()

//    var personFilmsInfo: List<FilmInfo> = emptyList()

    private val _personFilmsFlow = MutableStateFlow<List<FilmOfStaff>>(emptyList())
    val personFilmsFlow = _personFilmsFlow.asStateFlow()
//    private val _personFilmFlow = MutableStateFlow<FilmOfStaff?>(null)
//    val personFilmFlow = _personFilmFlow.asStateFlow()
//    var personFilmFlowIndex = 0

    fun loadPersonData(staffId: Int) {
        Log.d(TAG, "loadPersonData($staffId)")
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = ViewModelState.Loading
            var error = false

            val jobGetPersonData = viewModelScope.launch(Dispatchers.IO) {
                var personDataFoundInDb = false
                var personInfoDb: PersonInfoDb? = null
                val jobCheckPersonDataInDb = viewModelScope.launch(Dispatchers.IO) {
                    personDataFoundInDb = repository.isPersonDataExists(staffId)
                }
                jobCheckPersonDataInDb.join()
                Log.d(TAG, "Данные уже есть в БД: $personDataFoundInDb")

                if (personDataFoundInDb) {
                    val jobGetPersonDataFromDb = viewModelScope.launch {
                        personInfoDb = repository.getPersonInfoDb(staffId)
                    }
                    jobGetPersonDataFromDb.join()
                    Log.d(TAG, "Загружена из БД PersonInfoDb: $personInfoDb")

                    if (personInfoDb == null) error = true
                    else {
                        personInfoDbToVMData(personInfoDb!!)
                    }
                } else {
                    val jobGetPersonDataFromApi = viewModelScope.launch {
                        kotlin.runCatching {
                            repository.getFromApiPersonInfoDb(staffId)
                        }.fold(
                            onSuccess = {
                                personInfoDb = it
                                personInfoDbToVMData(personInfoDb!!)
                                Log.d(TAG, "Данные человека загружены из Api: $personInfoDb")
                            },
                            onFailure = {
                                Log.d(
                                    TAG,
                                    "Данные человека из Api. Ошибка загрузки. ${it.message ?: ""}"
                                )
                                error = true
                            }
                        )
                    }
                    jobGetPersonDataFromApi.join()

                    // Запись данных человека в базу данных после загрузки из Api
                    val jobAddPersonDataToDb = viewModelScope.launch {
                        personInfoDb?.let { repository.addPersonInfoDb(it) }
                    }
                    jobAddPersonDataToDb.join()
                    Log.d(TAG, "Данные человека записаны в базу данных")
                }
            }
            jobGetPersonData.join()

//            val job1Loading = viewModelScope.launch(Dispatchers.IO) {
//                kotlin.runCatching {
//                    repository.getPersonInfo(staffId)
//                }.fold(
//                    onSuccess = {
//                        name = it.nameRu ?: it.nameEn
//                        if (name == null) error = true
//                        photo = it.posterUrl
//                        profession = it.profession ?: ""
//                        personFilms = it.films
//                        personFilms.sortedByDescending { personFilmInfo -> personFilmInfo.rating?.toFloatOrNull() }
//                            .forEach { film ->
//                                sortedFilmIDs.add(film.filmId)
//                            }
//                        sortedFilmIDs = sortedFilmIDs.toSet().toMutableList()
//                        filmsQuantity = sortedFilmIDs.size
//                        sortedFilmIDs = if (sortedFilmIDs.size > 10) sortedFilmIDs.subList(
//                            0,
//                            9
//                        ) else sortedFilmIDs
//                    },
//                    onFailure = {
//                        Log.d("Информация о персонале", it.message ?: "Ошибка загрузки")
//                        error = true
//                    }
//                )
//            }
//            job1Loading.join()

//            val jobGetDetailedFilmsData = viewModelScope.launch(Dispatchers.IO) {
//                kotlin.runCatching {
//                    repository.getPersonFilmsDetailed(bestFilms)
//                    // Данная функция берёт каждый фильм из БД, если его нет, то загружает из Api,
//                    // а затем записывает в БД. Кроме того, в данные по каждому фильму добавляется
//                    // профессия данного человека в этом фильме (это в БД не пишется).
//                }.fold(
//                    onSuccess = {
//                        detailedFilms = it
//                    },
//                    onFailure = {
//                        Log.d(TAG, "Список фильмов персонала подробный. Ошибка загрузки. ${it.message ?: ""}")
//                        error = true
//                    }
//                )
//            }
//            jobGetDetailedFilmsData.join()

            if (error) {
                _state.value = ViewModelState.Error
                Log.d(TAG, "Состояние ошибки")
            } else {
                _state.value = ViewModelState.Loaded
                Log.d(TAG, "Состояние успешного завершения загрузки")
            }

            viewModelScope.launch(Dispatchers.IO) {
                bestFilms.forEach { filmOfPersonTable ->
                    kotlin.runCatching {
                        repository.getPersonFilmDetailed(filmOfPersonTable)
                    }.fold(
                        onSuccess = { newDownloadedFilm ->
                            if (newDownloadedFilm != null) {
                                detailedFilms.add(newDownloadedFilm)
                                _personFilmsFlow.value = detailedFilms.toList()
                                Log.d(TAG, "Во Flow отправлен список размера: ${detailedFilms.size}")
//                                personFilmFlowIndex = detailedFilms.size - 1
//                                _personFilmFlow.value = newDownloadedFilm
//                                Log.d(TAG, "Во Flow отправлен: ${newDownloadedFilm.filmId}")
                            }
                        },
                        onFailure = {
                            Log.d(
                                TAG,
                                "Список фильмов персонала подробный. Ошибка загрузки. ${it.message ?: ""}"
                            )
                        }
                    )
                }
            }
        }
    }

    private fun personInfoDbToVMData(personInfoDb: PersonInfoDb) {
        name = personInfoDb.personTable.name
        photo = personInfoDb.personTable.posterUrl
        profession = personInfoDb.personTable.profession ?: ""
        personInfoDb.filmsOfPerson.forEach { film ->
            if (!uniqueFilmIDs.contains(film.filmId)) {
                uniqueFilmIDs.add(film.filmId)
                filmsUnique.add(film)
            }
        }
        filmsQuantity = filmsUnique.size
        bestFilms = if (filmsQuantity > 10) filmsUnique.subList(
            0,
            10
        ) else filmsUnique
        Log.d(TAG, "Размер списка bestFilms: ${bestFilms.size}")
    }
}