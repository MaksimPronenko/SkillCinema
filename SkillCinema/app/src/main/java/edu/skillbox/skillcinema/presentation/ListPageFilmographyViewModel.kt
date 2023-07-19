package edu.skillbox.skillcinema.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.skillbox.skillcinema.data.Repository
import edu.skillbox.skillcinema.models.FilmDbViewed
import edu.skillbox.skillcinema.models.FilmItemData
import edu.skillbox.skillcinema.models.FilmOfPersonTable
import edu.skillbox.skillcinema.models.PersonInfoDb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "Filmography.VM"

class ListPageFilmographyViewModel(
    private val repository: Repository
) : ViewModel() {

    private val _state = MutableStateFlow<ViewModelState>(
        ViewModelState.Loading
    )
    val state = _state.asStateFlow()

    var staffId: Int = 0

    var name: String? = null
    private var sex: String? = null

    var chosenType: Int = 0

    private var filmsOfPerson: MutableList<FilmOfPersonTable> = mutableListOf()
    private var filmsExtended: MutableList<FilmItemData> = mutableListOf()
    private val _filmsFlow = MutableStateFlow<List<FilmItemData>>(emptyList())
    val filmsFlow = _filmsFlow.asStateFlow()

    private var actorFilms: MutableList<FilmOfPersonTable> = mutableListOf()
    var quantityActorFilms = 0

    private var actressFilms: MutableList<FilmOfPersonTable> = mutableListOf()
    var quantityActressFilms = 0

    private var himselfFilms: MutableList<FilmOfPersonTable> = mutableListOf()
    var quantityHimselfFilms = 0

    private var herselfFilms: MutableList<FilmOfPersonTable> = mutableListOf()
    var quantityHerselfFilms = 0

    private var hronoTitrMaleFilms: MutableList<FilmOfPersonTable> = mutableListOf()
    var quantityHronoTitrMaleFilms = 0

    private var hronoTitrFemaleFilms: MutableList<FilmOfPersonTable> = mutableListOf()
    var quantityHronoTitrFemaleFilms = 0

    private var directorFilms: MutableList<FilmOfPersonTable> = mutableListOf()
    var quantityDirectorFilms = 0

    private var producerFilms: MutableList<FilmOfPersonTable> = mutableListOf()
    var quantityProducerFilms = 0

    private var producerUSSRFilms: MutableList<FilmOfPersonTable> = mutableListOf()
    var quantityProducerUSSRFilms = 0

    private var voiceDirectorFilms: MutableList<FilmOfPersonTable> = mutableListOf()
    var quantityVoiceDirectorFilms = 0

    private var writerFilms: MutableList<FilmOfPersonTable> = mutableListOf()
    var quantityWriterFilms = 0

    private var operatorFilms: MutableList<FilmOfPersonTable> = mutableListOf()
    var quantityOperatorFilms = 0

    private var editorFilms: MutableList<FilmOfPersonTable> = mutableListOf()
    var quantityEditorFilms = 0

    private var composerFilms: MutableList<FilmOfPersonTable> = mutableListOf()
    var quantityComposerFilms = 0

    private var designFilms: MutableList<FilmOfPersonTable> = mutableListOf()
    var quantityDesignFilms = 0

    private var translatorFilms: MutableList<FilmOfPersonTable> = mutableListOf()
    var quantityTranslatorFilms = 0

    private var unknownFilms: MutableList<FilmOfPersonTable> = mutableListOf()
    var quantityUnknownFilms = 0

    fun loadPersonData(staffId: Int) {
        Log.d(TAG, "loadFilmography($staffId)")
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
        }
    }

    fun loadFilms(type: Int) {
        chosenType = type
        viewModelScope.launch(Dispatchers.IO) {
            filmsExtended = mutableListOf()
            filmsOfPerson = when (chosenType) {
                0 -> actorFilms
                1 -> actressFilms
                2 -> himselfFilms
                3 -> herselfFilms
                4 -> hronoTitrMaleFilms
                5 -> hronoTitrFemaleFilms
                6 -> directorFilms
                7 -> producerFilms
                8 -> producerUSSRFilms
                9 -> voiceDirectorFilms
                10 -> writerFilms
                11 -> operatorFilms
                12 -> editorFilms
                13 -> composerFilms
                14 -> designFilms
                15 -> translatorFilms
                else -> unknownFilms
            }
            filmsOfPerson.apply {
                forEach { filmOfPersonTable ->
                    val filmDbViewed: FilmDbViewed? =
                        repository.getFilmDbViewed(filmOfPersonTable.filmId).first
                    if (filmDbViewed != null &&
                        !filmsExtended.any { filmExtended ->
                            filmExtended.filmId == filmDbViewed.filmDb.filmTable.filmId
                        }
                    ) {
                        val filmItemData: FilmItemData = filmDbViewed.convertToFilmItemData()
                        Log.d(TAG, "Тип $type. Загружен ${filmItemData.filmId}")
                        filmsExtended.add(filmItemData)
                        if (chosenType == type) {
                            Log.d(
                                TAG,
                                "Тип VM $chosenType равен обрабатываемому типу функции $type"
                            )
                            _filmsFlow.value = filmsExtended.toList()
                            Log.d(TAG, "Тип $type. Доб-н ${filmItemData.filmId}")
                        } else {
                            Log.d(TAG, "Был тип $type. Смена типа. return@apply")
                            return@apply
                        }
                    }
                }
            }
            if (chosenType == type) {
                _filmsFlow.value = filmsExtended.toList()
                Log.d(TAG, "Тип $type. Финальный список длины ${filmsExtended.size}")
            }

        }
    }

    private fun personInfoDbToVMData(personInfoDb: PersonInfoDb) {
        name = personInfoDb.personTable.name
        sex = personInfoDb.personTable.sex
        personInfoDb.filmsOfPerson.forEach { film ->
            when (film.professionKey) {
                "ACTOR" -> {
                    if (sex == "FEMALE") actressFilms.add(film)
                    else actorFilms.add(film)
                }
                "HIMSELF" -> {
                    if (sex == "FEMALE") herselfFilms.add(film)
                    else himselfFilms.add(film)
                }
                "HERSELF" -> {
                    if (sex == "MALE") himselfFilms.add(film)
                    else herselfFilms.add(film)
                }
                "HRONO_TITR_MALE" -> {
                    if (sex == "FEMALE") hronoTitrFemaleFilms.add(film)
                    else hronoTitrMaleFilms.add(film)
                }
                "HRONO_TITR_FEMALE" -> {
                    if (sex == "MALE") hronoTitrMaleFilms.add(film)
                    else hronoTitrFemaleFilms.add(film)
                }
                "DIRECTOR" -> directorFilms.add(film)
                "PRODUCER" -> producerFilms.add(film)
                "PRODUCER_USSR" -> producerUSSRFilms.add(film)
                "VOICE_DIRECTOR" -> voiceDirectorFilms.add(film)
                "WRITER" -> writerFilms.add(film)
                "OPERATOR" -> operatorFilms.add(film)
                "EDITOR" -> editorFilms.add(film)
                "COMPOSER" -> composerFilms.add(film)
                "DESIGN" -> designFilms.add(film)
                "TRANSLATOR" -> translatorFilms.add(film)
                "UNKNOWN" -> unknownFilms.add(film)
            }
        }
        quantityActorFilms = actorFilms.size
        quantityActressFilms = actressFilms.size
        quantityHimselfFilms = himselfFilms.size
        quantityHerselfFilms = herselfFilms.size
        quantityHronoTitrMaleFilms = hronoTitrMaleFilms.size
        quantityHronoTitrFemaleFilms = hronoTitrFemaleFilms.size
        quantityDirectorFilms = directorFilms.size
        quantityProducerFilms = producerFilms.size
        quantityProducerUSSRFilms = producerUSSRFilms.size
        quantityVoiceDirectorFilms = voiceDirectorFilms.size
        quantityWriterFilms = writerFilms.size
        quantityOperatorFilms = operatorFilms.size
        quantityEditorFilms = editorFilms.size
        quantityComposerFilms = composerFilms.size
        quantityDesignFilms = designFilms.size
        quantityTranslatorFilms = translatorFilms.size
        quantityUnknownFilms = unknownFilms.size

        if (quantityActorFilms > 0) chosenType = 0
        else if (quantityActressFilms > 0) chosenType = 1
        else if (quantityHimselfFilms > 0) chosenType = 2
        else if (quantityHerselfFilms > 0) chosenType = 3
        else if (quantityHronoTitrMaleFilms > 0) chosenType = 4
        else if (quantityHronoTitrFemaleFilms > 0) chosenType = 5
        else if (quantityDirectorFilms > 0) chosenType = 6
        else if (quantityProducerFilms > 0) chosenType = 7
        else if (quantityProducerUSSRFilms > 0) chosenType = 8
        else if (quantityVoiceDirectorFilms > 0) chosenType = 9
        else if (quantityWriterFilms > 0) chosenType = 10
        else if (quantityOperatorFilms > 0) chosenType = 11
        else if (quantityEditorFilms > 0) chosenType = 12
        else if (quantityComposerFilms > 0) chosenType = 13
        else if (quantityDesignFilms > 0) chosenType = 14
        else if (quantityTranslatorFilms > 0) chosenType = 15
        else if (quantityUnknownFilms > 0) chosenType = 16
    }
}