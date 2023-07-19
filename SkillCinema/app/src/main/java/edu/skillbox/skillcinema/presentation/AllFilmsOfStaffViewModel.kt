package edu.skillbox.skillcinema.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.skillbox.skillcinema.data.Repository
import edu.skillbox.skillcinema.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
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

    var personFilms: List<FilmOfPersonTable>? = null
    private var filmsExtended: MutableList<FilmItemData> = mutableListOf()
    private val _filmsFlow = MutableStateFlow<List<FilmItemData>>(emptyList())
    val filmsFlow = _filmsFlow.asStateFlow()

    var jobLoadFilmsExtended: Job? = null

    fun loadPersonData(staffId: Int) {
        Log.d(TAG, "loadAllFilmsOfStaff($staffId)")
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = ViewModelState.Loading
            var personInfoDbResult: Pair<PersonInfoDb?, Boolean>
            var personInfoDb: PersonInfoDb?
            var error = false

            val jobGetPersonData = viewModelScope.launch(Dispatchers.IO) {
                personInfoDbResult = repository.getPersonInfoDb(staffId)
                personInfoDb = personInfoDbResult.first
                error = personInfoDbResult.second

                if (personInfoDb == null) error = true
                else personInfoDbToVMData(personInfoDb!!)
            }
            jobGetPersonData.join()

            if (error) {
                _state.value = ViewModelState.Error
                Log.d(TAG, "Состояние ошибки")
            } else {
                _state.value = ViewModelState.Loaded
                Log.d(TAG, "Состояние успешного завершения загрузки")
            }

            Log.d(TAG, "Запускаем loadFilmsExtended() из loadPersonData()")
            loadFilmsExtended()
        }
    }

    fun loadFilmsExtended() {
        if (jobLoadFilmsExtended?.isActive != true) {
            jobLoadFilmsExtended = viewModelScope.launch(Dispatchers.IO) {
                filmsExtended = mutableListOf()
                Log.d(TAG, "loadFilmsExtended(). Внутри Job.")
                personFilms?.forEach { filmOfPersonTable ->
                    val filmDbViewed: FilmDbViewed? =
                        repository.getFilmDbViewed(filmOfPersonTable.filmId).first
                    if (filmDbViewed != null) {
                        val filmItemData: FilmItemData = filmDbViewed.convertToFilmItemData()
                        Log.d(TAG, "Загружен ${filmItemData.filmId}")
                        filmsExtended.add(filmItemData)
                        _filmsFlow.value = filmsExtended.toList()
                    }
                }
            }
        }
    }

    private fun personInfoDbToVMData(personInfoDb: PersonInfoDb) {
        name = personInfoDb.personTable.name
        personFilms = personInfoDb.filmsOfPerson
    }
}