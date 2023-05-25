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

private const val TAG = "AllFilmsOfStaff.VM"

class AllFilmsOfStaffViewModel(
    private val repository: Repository
) : ViewModel() {

    private val _state = MutableStateFlow<ViewModelState>(
        ViewModelState.Loading
    )
    val state = _state.asStateFlow()

    var staffId: Int = 0

    var name: String? = null

    var personFilms: List<FilmOfPersonTable> = emptyList()
    var filmsDetailed: MutableList<FilmOfStaff> = emptyList<FilmOfStaff>().toMutableList()
    private val _filmsFlow = MutableStateFlow<List<FilmOfStaff>>(emptyList())
    val filmsFlow = _filmsFlow.asStateFlow()

    fun loadPersonData(staffId: Int) {
        Log.d(TAG, "loadAllFilmsOfStaff($staffId)")
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

            if (error) {
                _state.value = ViewModelState.Error
                Log.d(TAG, "Состояние ошибки")
            } else {
                _state.value = ViewModelState.Loaded
                Log.d(TAG, "Состояние успешного завершения загрузки")
            }
        }
    }

    fun loadAllFilmsDetailed() {
        viewModelScope.launch(Dispatchers.IO) {
            val jobLoadAndSend = viewModelScope.launch(Dispatchers.IO) {
                personFilms.onEach { filmOfPersonTable ->
                    val newDownloadedFilm =
                        repository.getPersonFilmDetailed(filmOfPersonTable)
                    if (newDownloadedFilm != null) {
                        Log.d(TAG, "Загружен ${newDownloadedFilm.filmId}")
                        if (!filmsDetailed.any { filmOfStaff -> filmOfStaff.filmId == newDownloadedFilm.filmId }) {
                            filmsDetailed.add(newDownloadedFilm)
                            _filmsFlow.value = filmsDetailed.toList()
                            Log.d(TAG, "Доб-н ${newDownloadedFilm.filmId}")
                        } else {
                            Log.d(TAG, "Уже ${newDownloadedFilm.filmId}")
                        }
                    }
                }
            }
            jobLoadAndSend.join()
            _filmsFlow.value = filmsDetailed.toList()
            Log.d(TAG, "Финальный список длины ${filmsDetailed.size}")
        }
    }

    private fun personInfoDbToVMData(personInfoDb: PersonInfoDb) {
        name = personInfoDb.personTable.name
        personFilms = personInfoDb.filmsOfPerson
    }
}