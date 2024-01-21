package edu.skillbox.skillcinema.presentation.staff

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.skillbox.skillcinema.data.RepositoryFilmAndSerial
import edu.skillbox.skillcinema.data.RepositoryCollections
import edu.skillbox.skillcinema.data.RepositoryPerson
import edu.skillbox.skillcinema.models.collection.InterestedTable
import edu.skillbox.skillcinema.models.filmAndSerial.film.FilmDbViewed
import edu.skillbox.skillcinema.models.filmAndSerial.film.FilmItemData
import edu.skillbox.skillcinema.models.person.filmOfPerson.FilmOfPersonTable
import edu.skillbox.skillcinema.models.person.PersonInfoDb
import edu.skillbox.skillcinema.presentation.ViewModelState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "Staff.VM"

class StaffViewModel(
    private val repositoryFilmAndSerial: RepositoryFilmAndSerial,
    private val repositoryPerson: RepositoryPerson,
    private val repositoryCollections: RepositoryCollections
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

    private var uniqueFilmIDs: MutableList<Int> = emptyList<Int>().toMutableList()
    private var filmsUnique: MutableList<FilmOfPersonTable> = emptyList<FilmOfPersonTable>().toMutableList()
    private var bestFilms: List<FilmOfPersonTable>? = null
    private var filmsExtended: MutableList<FilmItemData> = mutableListOf()
    private val _bestFilmsFlow = MutableStateFlow<List<FilmItemData>>(emptyList())
    val bestFilmsFlow = _bestFilmsFlow.asStateFlow()

    private var jobLoadBestFilmsExtended: Job? = null

    fun loadPersonData(staffId: Int) {
        Log.d(TAG, "loadPersonData($staffId)")
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = ViewModelState.Loading
            var personInfoDbResult: Pair<PersonInfoDb?, Boolean>
            var personInfoDb: PersonInfoDb?
            var error = false

            val jobGetPersonData = viewModelScope.launch(Dispatchers.IO) {
                personInfoDbResult = repositoryPerson.getPersonInfoDb(staffId)
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

                // Запись в список "Вам было интересно"
                repositoryCollections.addInterested(
                    InterestedTable(
                        id = staffId,
                        type = 2
                    )
                )
            }

            Log.d(TAG, "Запускаем loadBestFilmsExtended() из loadPersonData()")
            loadBestFilmsExtended()
        }
    }

    fun loadBestFilmsExtended() {
        if (jobLoadBestFilmsExtended?.isActive != true) {
            jobLoadBestFilmsExtended = viewModelScope.launch(Dispatchers.IO) {
                filmsExtended = mutableListOf()
                Log.d(TAG, "loadBestFilmsExtended(). Внутри Job.")
                bestFilms?.forEach { filmOfPersonTable ->
                    val filmDbViewed: FilmDbViewed? =
                        repositoryFilmAndSerial.getFilmDbViewed(filmOfPersonTable.filmId).first
                    if (filmDbViewed != null) {
                        val filmItemData: FilmItemData = filmDbViewed.convertToFilmItemData()
                        Log.d(TAG, "Загружен ${filmItemData.filmId}")
                        filmsExtended.add(filmItemData)
                        _bestFilmsFlow.value = filmsExtended.toList()
                    }
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
        Log.d(TAG, "Размер списка bestFilms: ${bestFilms?.size}")
    }
}