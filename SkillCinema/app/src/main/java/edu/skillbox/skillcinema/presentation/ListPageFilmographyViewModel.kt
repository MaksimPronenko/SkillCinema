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
    var sex: String? = null
//    var personFilms: List<FilmOfPersonTable> = emptyList()

    var chosenType: Int = 0

    private val _filmsFlow = MutableStateFlow<List<FilmOfStaff>>(emptyList())
    val filmsFlow = _filmsFlow.asStateFlow()

//    var detailedFilms: List<FilmOfStaff> = emptyList()

//    private var uniqueFilmIDs: MutableList<Int> = emptyList<Int>().toMutableList()
//    var filmsUnique: MutableList<FilmOfPersonTable> = emptyList<FilmOfPersonTable>().toMutableList()
//    var quantityAllFilms = 0
//    var allFilmsList: MutableList<FilmOfStaff> =
//        emptyList<FilmOfStaff>().toMutableList()
//    private val _allFilmsFlow = MutableStateFlow<FilmOfStaff?>(null)
//    val allFilmsFlow = _allFilmsFlow.asStateFlow()
//    var allFilmsFlowIndex = 0
//    private val _allFilms = MutableStateFlow<List<FilmOfStaff>>(emptyList())
//    val allFilms = _allFilms.asStateFlow()

    var actorFilms: MutableList<FilmOfPersonTable> = emptyList<FilmOfPersonTable>().toMutableList()
    var actorFilmsDetailed: MutableList<FilmOfStaff> = emptyList<FilmOfStaff>().toMutableList()
    var quantityActorFilms = 0
//    private val _actorFilmsFlow = MutableStateFlow<List<FilmOfStaff>>(emptyList())
//    val actorFilmsFlow = _actorFilmsFlow.asStateFlow()
//    private val _actorFilmFlow = MutableStateFlow<FilmOfStaff?>(null)
//    val actorFilmFlow = _actorFilmFlow.asStateFlow()
//    var actorFilmFlowIndex = 0

    var actressFilms: MutableList<FilmOfPersonTable> =
        emptyList<FilmOfPersonTable>().toMutableList()
    var actressFilmsDetailed: MutableList<FilmOfStaff> = emptyList<FilmOfStaff>().toMutableList()
    var quantityActressFilms = 0
//    private val _actressFilmsFlow = MutableStateFlow<List<FilmOfStaff>>(emptyList())
//    val actressFilmsFlow = _actressFilmsFlow.asStateFlow()
//    private val _actressFilmFlow = MutableStateFlow<FilmOfStaff?>(null)
//    val actressFilmFlow = _actressFilmFlow.asStateFlow()
//    var actressFilmFlowIndex = 0

    var himselfFilms: MutableList<FilmOfPersonTable> =
        emptyList<FilmOfPersonTable>().toMutableList()
    var himselfFilmsDetailed: MutableList<FilmOfStaff> = emptyList<FilmOfStaff>().toMutableList()
    var quantityHimselfFilms = 0
//    private val _himselfFilmsFlow = MutableStateFlow<List<FilmOfStaff>>(emptyList())
//    val himselfFilmsFlow = _himselfFilmsFlow.asStateFlow()
//    private val _himselfFilmFlow = MutableStateFlow<FilmOfStaff?>(null)
//    val himselfFilmFlow = _himselfFilmFlow.asStateFlow()
//    var himselfFilmFlowIndex = 0

    var herselfFilms: MutableList<FilmOfPersonTable> =
        emptyList<FilmOfPersonTable>().toMutableList()
    var herselfFilmsDetailed: MutableList<FilmOfStaff> = emptyList<FilmOfStaff>().toMutableList()
    var quantityHerselfFilms = 0
//    private val _herselfFilmsFlow = MutableStateFlow<List<FilmOfStaff>>(emptyList())
//    val herselfFilmsFlow = _herselfFilmsFlow.asStateFlow()
//    private val _herselfFilmFlow = MutableStateFlow<FilmOfStaff?>(null)
//    val herselfFilmFlow = _herselfFilmFlow.asStateFlow()
//    var herselfFilmFlowIndex = 0

    var hronoTitrMaleFilms: MutableList<FilmOfPersonTable> =
        emptyList<FilmOfPersonTable>().toMutableList()
    var hronoTitrMaleFilmsDetailed: MutableList<FilmOfStaff> =
        emptyList<FilmOfStaff>().toMutableList()
    var quantityHronoTitrMaleFilms = 0
//    private val _hronoTitrMaleFilmsFlow = MutableStateFlow<List<FilmOfStaff>>(emptyList())
//    val hronoTitrMaleFilmsFlow = _hronoTitrMaleFilmsFlow.asStateFlow()
//    private val _hronoTitrMaleFilmFlow = MutableStateFlow<FilmOfStaff?>(null)
//    val hronoTitrMaleFilmFlow = _hronoTitrMaleFilmFlow.asStateFlow()
//    var hronoTitrMaleFilmFlowIndex = 0

    var hronoTitrFemaleFilms: MutableList<FilmOfPersonTable> =
        emptyList<FilmOfPersonTable>().toMutableList()
    var hronoTitrFemaleFilmsDetailed: MutableList<FilmOfStaff> =
        emptyList<FilmOfStaff>().toMutableList()
    var quantityHronoTitrFemaleFilms = 0
//    private val _hronoTitrFemaleFilmsFlow = MutableStateFlow<List<FilmOfStaff>>(emptyList())
//    val hronoTitrFemaleFilmsFlow = _hronoTitrFemaleFilmsFlow.asStateFlow()
//    private val _hronoTitrFemaleFilmFlow = MutableStateFlow<FilmOfStaff?>(null)
//    val hronoTitrFemaleFilmFlow = _hronoTitrFemaleFilmFlow.asStateFlow()
//    var hronoTitrFemaleFilmFlowIndex = 0

    var directorFilms: MutableList<FilmOfPersonTable> =
        emptyList<FilmOfPersonTable>().toMutableList()
    var directorFilmsDetailed: MutableList<FilmOfStaff> = emptyList<FilmOfStaff>().toMutableList()
    var quantityDirectorFilms = 0
//    private val _directorFilmsFlow = MutableStateFlow<List<FilmOfStaff>>(emptyList())
//    val directorFilmsFlow = _directorFilmsFlow.asStateFlow()
//    private val _directorFilmFlow = MutableStateFlow<FilmOfStaff?>(null)
//    val directorFilmFlow = _directorFilmFlow.asStateFlow()
//    var directorFilmFlowIndex = 0

    var producerFilms: MutableList<FilmOfPersonTable> =
        emptyList<FilmOfPersonTable>().toMutableList()
    var producerFilmsDetailed: MutableList<FilmOfStaff> = emptyList<FilmOfStaff>().toMutableList()
    var quantityProducerFilms = 0
//    private val _producerFilmsFlow = MutableStateFlow<List<FilmOfStaff>>(emptyList())
//    val producerFilmsFlow = _producerFilmsFlow.asStateFlow()
//    private val _producerFilmFlow = MutableStateFlow<FilmOfStaff?>(null)
//    val producerFilmFlow = _producerFilmFlow.asStateFlow()
//    var producerFilmFlowIndex = 0

    var producerUSSRFilms: MutableList<FilmOfPersonTable> =
        emptyList<FilmOfPersonTable>().toMutableList()
    var producerUSSRFilmsDetailed: MutableList<FilmOfStaff> =
        emptyList<FilmOfStaff>().toMutableList()
    var quantityProducerUSSRFilms = 0
//    private val _producerUSSRFilmsFlow = MutableStateFlow<List<FilmOfStaff>>(emptyList())
//    val producerUSSRFilmsFlow = _producerUSSRFilmsFlow.asStateFlow()
//    private val _producerUSSRFilmFlow = MutableStateFlow<FilmOfStaff?>(null)
//    val producerUSSRFilmFlow = _producerUSSRFilmFlow.asStateFlow()
//    var producerUSSRFilmFlowIndex = 0

    var voiceDirectorFilms: MutableList<FilmOfPersonTable> =
        emptyList<FilmOfPersonTable>().toMutableList()
    var voiceDirectorFilmsDetailed: MutableList<FilmOfStaff> =
        emptyList<FilmOfStaff>().toMutableList()
    var quantityVoiceDirectorFilms = 0
//    private val _voiceDirectorFilmsFlow = MutableStateFlow<List<FilmOfStaff>>(emptyList())
//    val voiceDirectorFilmsFlow = _voiceDirectorFilmsFlow.asStateFlow()
//    private val _voiceDirectorFilmFlow = MutableStateFlow<FilmOfStaff?>(null)
//    val voiceDirectorFilmFlow = _voiceDirectorFilmFlow.asStateFlow()
//    var voiceDirectorFilmFlowIndex = 0

    var writerFilms: MutableList<FilmOfPersonTable> = emptyList<FilmOfPersonTable>().toMutableList()
    var writerFilmsDetailed: MutableList<FilmOfStaff> = emptyList<FilmOfStaff>().toMutableList()
    var quantityWriterFilms = 0
//    private val _writerFilmsFlow = MutableStateFlow<List<FilmOfStaff>>(emptyList())
//    val writerFilmsFlow = _writerFilmsFlow.asStateFlow()
//    private val _writerFilmFlow = MutableStateFlow<FilmOfStaff?>(null)
//    val writerFilmFlow = _writerFilmFlow.asStateFlow()
//    var writerFilmFlowIndex = 0

    var operatorFilms: MutableList<FilmOfPersonTable> =
        emptyList<FilmOfPersonTable>().toMutableList()
    var operatorFilmsDetailed: MutableList<FilmOfStaff> = emptyList<FilmOfStaff>().toMutableList()
    var quantityOperatorFilms = 0
//    private val _operatorFilmsFlow = MutableStateFlow<List<FilmOfStaff>>(emptyList())
//    val operatorFilmsFlow = _operatorFilmsFlow.asStateFlow()
//    private val _operatorFilmFlow = MutableStateFlow<FilmOfStaff?>(null)
//    val operatorFilmFlow = _operatorFilmFlow.asStateFlow()
//    var operatorFilmFlowIndex = 0

    var editorFilms: MutableList<FilmOfPersonTable> = emptyList<FilmOfPersonTable>().toMutableList()
    var editorFilmsDetailed: MutableList<FilmOfStaff> = emptyList<FilmOfStaff>().toMutableList()
    var quantityEditorFilms = 0
//    private val _editorFilmsFlow = MutableStateFlow<List<FilmOfStaff>>(emptyList())
//    val editorFilmsFlow = _editorFilmsFlow.asStateFlow()
//    private val _editorFilmFlow = MutableStateFlow<FilmOfStaff?>(null)
//    val editorFilmFlow = _editorFilmFlow.asStateFlow()
//    var editorFilmFlowIndex = 0

    var composerFilms: MutableList<FilmOfPersonTable> =
        emptyList<FilmOfPersonTable>().toMutableList()
    var composerFilmsDetailed: MutableList<FilmOfStaff> = emptyList<FilmOfStaff>().toMutableList()
    var quantityComposerFilms = 0
//    private val _composerFilmsFlow = MutableStateFlow<List<FilmOfStaff>>(emptyList())
//    val composerFilmsFlow = _composerFilmsFlow.asStateFlow()
//    private val _composerFilmFlow = MutableStateFlow<FilmOfStaff?>(null)
//    val composerFilmFlow = _composerFilmFlow.asStateFlow()
//    var composerFilmFlowIndex = 0

    var designFilms: MutableList<FilmOfPersonTable> = emptyList<FilmOfPersonTable>().toMutableList()
    var designFilmsDetailed: MutableList<FilmOfStaff> = emptyList<FilmOfStaff>().toMutableList()
    var quantityDesignFilms = 0
//    private val _designFilmsFlow = MutableStateFlow<List<FilmOfStaff>>(emptyList())
//    val designFilmsFlow = _designFilmsFlow.asStateFlow()
//    private val _designFilmFlow = MutableStateFlow<FilmOfStaff?>(null)
//    val designFilmFlow = _designFilmFlow.asStateFlow()
//    var designFilmFlowIndex = 0

    var translatorFilms: MutableList<FilmOfPersonTable> =
        emptyList<FilmOfPersonTable>().toMutableList()
    var translatorFilmsDetailed: MutableList<FilmOfStaff> = emptyList<FilmOfStaff>().toMutableList()
    var quantityTranslatorFilms = 0
//    private val _translatorFilmsFlow = MutableStateFlow<List<FilmOfStaff>>(emptyList())
//    val translatorFilmsFlow = _translatorFilmsFlow.asStateFlow()
//    private val _translatorFilmFlow = MutableStateFlow<FilmOfStaff?>(null)
//    val translatorFilmFlow = _translatorFilmFlow.asStateFlow()
//    var translatorFilmFlowIndex = 0

    var unknownFilms: MutableList<FilmOfPersonTable> =
        emptyList<FilmOfPersonTable>().toMutableList()
    var unknownFilmsDetailed: MutableList<FilmOfStaff> = emptyList<FilmOfStaff>().toMutableList()
    var quantityUnknownFilms = 0
//    private val _unknownFilmsFlow = MutableStateFlow<List<FilmOfStaff>>(emptyList())
//    val unknownFilmsFlow = _unknownFilmsFlow.asStateFlow()
//    private val _unknownFilmFlow = MutableStateFlow<FilmOfStaff?>(null)
//    val unknownFilmFlow = _unknownFilmFlow.asStateFlow()
//    var unknownFilmFlowIndex = 0

    fun loadPersonData(staffId: Int) {
        Log.d(TAG, "loadFilmography($staffId)")
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

//                kotlin.runCatching {
//                    repository.getPersonInfo(staffId)
//                }.fold(
//                    onSuccess = {
//                        name = it.nameRu ?: it.nameEn
//                        if (name == null) error = true
//                        sex = it.sex
//                        personFilms = it.films
//                        personFilms.sortedByDescending { personFilmInfo -> personFilmInfo.rating?.toFloatOrNull() }
//                            .forEach { film ->
//                                sortedFilmIDs.add(film.filmId)
//                                sortedFilmIDs = sortedFilmIDs.toSet().toMutableList()
//                            }
//                    },
//                    onFailure = {
//                        Log.d(TAG, it.message ?: "Информация о персонале. Ошибка загрузки")
//                        error = true
//                    }
//                )
            }
            jobGetPersonData.join()

            if (error) {
                _state.value = ViewModelState.Error
                Log.d(TAG, "Состояние ошибки")
            } else {
                _state.value = ViewModelState.Loaded
                Log.d(TAG, "Состояние успешного завершения загрузки")
            }

//            viewModelScope.launch(Dispatchers.IO) {
//                when (chosenType) {
//                    0 -> {
//                        actorFilms.forEachIndexed { index, filmOfPersonTable ->
//                            kotlin.runCatching {
//                                Log.d(TAG, "Актёр. Грузим фильм с индексом $index")
//                                repository.getPersonFilmDetailed(filmOfPersonTable)
//                            }.fold(
//                                onSuccess = { newDownloadedFilm ->
//                                    if (newDownloadedFilm != null) {
//                                        actorFilmsDetailed.add(newDownloadedFilm)
//                                        actorFilmFlowIndex = index
//                                        _actorFilmFlow.value = newDownloadedFilm
//                                    }
//                                },
//                                onFailure = {
//                                    Log.d(TAG, "Актёр. Ошибка загрузки. ${it.message ?: ""}")
//                                }
//                            )
//                        }
//                    }
//                    1 -> {
//                        actressFilms.forEachIndexed { index, filmOfPersonTable ->
//                            kotlin.runCatching {
//                                Log.d(TAG, "Актриса. Грузим фильм с индексом $index")
//                                repository.getPersonFilmDetailed(filmOfPersonTable)
//                            }.fold(
//                                onSuccess = { newDownloadedFilm ->
//                                    if (newDownloadedFilm != null) {
//                                        actressFilmsDetailed.add(newDownloadedFilm)
//                                        actressFilmFlowIndex = index
//                                        _actressFilmFlow.value = newDownloadedFilm
//                                    }
//                                },
//                                onFailure = {
//                                    Log.d(TAG, "Актриса. Ошибка загрузки. ${it.message ?: ""}")
//                                }
//                            )
//                        }
//                    }
//                    2 -> {
//                        himselfFilms.forEachIndexed { index, filmOfPersonTable ->
//                            kotlin.runCatching {
//                                Log.d(TAG, "Играет сам себя. Грузим фильм с индексом $index")
//                                repository.getPersonFilmDetailed(filmOfPersonTable)
//                            }.fold(
//                                onSuccess = { newDownloadedFilm ->
//                                    if (newDownloadedFilm != null) {
//                                        himselfFilmsDetailed.add(newDownloadedFilm)
//                                        himselfFilmFlowIndex = index
//                                        _himselfFilmFlow.value = newDownloadedFilm
//                                    }
//                                },
//                                onFailure = {
//                                    Log.d(TAG, "Актриса. Ошибка загрузки. ${it.message ?: ""}")
//                                }
//                            )
//                        }
//                    }
//                    3 -> {
//                        herselfFilms.forEachIndexed { index, filmOfPersonTable ->
//                            kotlin.runCatching {
//                                Log.d(TAG, "Играет сама себя. Грузим фильм с индексом $index")
//                                repository.getPersonFilmDetailed(filmOfPersonTable)
//                            }.fold(
//                                onSuccess = { newDownloadedFilm ->
//                                    if (newDownloadedFilm != null) {
//                                        herselfFilmsDetailed.add(newDownloadedFilm)
//                                        herselfFilmFlowIndex = index
//                                        _herselfFilmFlow.value = newDownloadedFilm
//                                    }
//                                },
//                                onFailure = {
//                                    Log.d(TAG, "Актриса. Ошибка загрузки. ${it.message ?: ""}")
//                                }
//                            )
//                        }
//                    }
//                    4 -> {
//
//                    }
//                    5 -> {
//
//                    }
//                    6 -> {
//
//                    }
//                    7 -> {
//
//                    }
//                    8 -> {
//
//                    }
//                    9 -> {
//
//                    }
//                    10 -> {
//
//                    }
//                    11 -> {
//
//                    }
//                    12 -> {
//
//                    }
//                    13 -> {
//
//                    }
//                    14 -> {
//
//                    }
//                    15 -> {
//
//                    }
//                    16 -> {
//
//                    }
//                    17 -> {
//
//                    }
//                }
//            }

//            val jobGetDetailedFilmsData = viewModelScope.launch(Dispatchers.IO) {
//                kotlin.runCatching {
//                    repository.getPersonFilmsDetailed(personFilms)
//                    // Данная функция берёт каждый фильм из БД, если его нет, то загружает из Api,
//                    // а затем записывает в БД. Кроме того, в данные по каждому фильму добавляется
//                    // профессия данного человека в этом фильме (это в БД не пишется).
//                }.fold(
//                    onSuccess = {
//                        detailedFilms = it
//
//                        allFilmsList = removeDuplicateFilms(it.toMutableList())
//                        _allFilms.value = allFilmsList
//                        quantityAllFilms = allFilmsList.size
//
//                        it.forEach { film ->
//                            when (film.profession) {
//                                "DIRECTOR" -> directorFilmsList.add(film)
//                                "PRODUCER" -> producerFilmsList.add(film)
//                                "PRODUCER_USSR" -> producerUSSRFilmsList.add(film)
//                                "ACTOR" -> {
//                                    if (sex == "MALE") actorFilmsList.add(film)
//                                    else actressFilmsList.add(film)
//                                }
//                                "HIMSELF" -> {
//                                    if (sex == "MALE") himselfFilmsList.add(film)
//                                    else herselfFilmsList.add(film)
//                                }
//                                "HERSELF" -> {
//                                    if (sex == "MALE") himselfFilmsList.add(film)
//                                    else herselfFilmsList.add(film)
//                                }
//                                "HRONO_TITR_MALE" -> {
//                                    if (sex == "MALE") hronoTitrMaleFilmsList.add(film)
//                                    else hronoTitrFemaleFilmsList.add(film)
//                                }
//                                "HRONO_TITR_FEMALE" -> {
//                                    if (sex == "MALE") hronoTitrMaleFilmsList.add(film)
//                                    else hronoTitrFemaleFilmsList.add(film)
//                                }
//                                "VOICE_DIRECTOR" -> voiceDirectorFilmsList.add(film)
//                                "WRITER" -> writerFilmsList.add(film)
//                                "OPERATOR" -> operatorFilmsList.add(film)
//                                "EDITOR" -> editorFilmsList.add(film)
//                                "COMPOSER" -> composerFilmsList.add(film)
//                                "DESIGN" -> designFilmsList.add(film)
//                                "TRANSLATOR" -> translatorFilmsList.add(film)
//                                "UNKNOWN" -> unknownFilmsList.add(film)
//                            }
//                        }
//
//                        quantityDirectorFilms = directorFilmsList.size
//                        quantityProducerFilms = producerFilmsList.size
//                        quantityProducerUSSRFilms = producerUSSRFilmsList.size
//                        quantityActorFilms = actorFilmsList.size
//                        quantityActressFilms = actressFilmsList.size
//                        quantityHimselfFilms = himselfFilmsList.size
//                        quantityHerselfFilms = herselfFilmsList.size
//                        quantityHronoTitrMaleFilms = hronoTitrMaleFilmsList.size
//                        quantityHronoTitrFemaleFilms = hronoTitrFemaleFilmsList.size
//                        quantityVoiceDirectorFilms = voiceDirectorFilmsList.size
//                        quantityWriterFilms = writerFilmsList.size
//                        quantityOperatorFilms = operatorFilmsList.size
//                        quantityEditorFilms = editorFilmsList.size
//                        quantityComposerFilms = composerFilmsList.size
//                        quantityDesignFilms = designFilmsList.size
//                        quantityTranslatorFilms = translatorFilmsList.size
//                        quantityUnknownFilms = unknownFilmsList.size
//
//                        _directorFilms.value = directorFilmsList
//                        _producerFilms.value = producerFilmsList
//                        _producerUSSRFilms.value = producerUSSRFilmsList
//                        _actorFilms.value = actorFilmsList
//                        _actressFilms.value = actressFilmsList
//                        _himselfFilms.value = himselfFilmsList
//                        _herselfFilms.value = herselfFilmsList
//                        _hronoTitrMaleFilms.value = hronoTitrMaleFilmsList
//                        _hronoTitrFemaleFilms.value = hronoTitrFemaleFilmsList
//                        _voiceDirectorFilms.value = voiceDirectorFilmsList
//                        _writerFilms.value = writerFilmsList
//                        _operatorFilms.value = operatorFilmsList
//                        _editorFilms.value = editorFilmsList
//                        _composerFilms.value = composerFilmsList
//                        _designFilms.value = designFilmsList
//                        _translatorFilms.value = translatorFilmsList
//                        _unknownFilms.value = unknownFilmsList
//                    },
//                    onFailure = {
//                        Log.d(TAG, "Списки фильмов по должностям. Ошибка загрузки. ${it.message ?: ""}")
//                        error = true
//                    }
//                )
//            }
//            jobGetDetailedFilmsData.join()

//            val job2Loading = viewModelScope.launch(Dispatchers.IO) {
//                kotlin.runCatching {
//                    repository.getPersonFilmsDetailed(personFilms)
//                }.fold(
//                    onSuccess = {
//                        allFilmsList = removeDuplicateFilms(it.toMutableList())
//                        _allFilms.value = allFilmsList
//                        quantityAllFilms = allFilmsList.size
//
//                        it.forEach { film ->
//                            when (film.profession) {
//                                "DIRECTOR" -> directorFilmsList.add(film)
//                                "PRODUCER" -> producerFilmsList.add(film)
//                                "PRODUCER_USSR" -> producerUSSRFilmsList.add(film)
//                                "ACTOR" -> {
//                                    if (sex == "MALE") actorFilmsList.add(film)
//                                    else actressFilmsList.add(film)
//                                }
//                                "HIMSELF" -> {
//                                    if (sex == "MALE") himselfFilmsList.add(film)
//                                    else herselfFilmsList.add(film)
//                                }
//                                "HERSELF" -> {
//                                    if (sex == "MALE") himselfFilmsList.add(film)
//                                    else herselfFilmsList.add(film)
//                                }
//                                "HRONO_TITR_MALE" -> {
//                                    if (sex == "MALE") hronoTitrMaleFilmsList.add(film)
//                                    else hronoTitrFemaleFilmsList.add(film)
//                                }
//                                "HRONO_TITR_FEMALE" -> {
//                                    if (sex == "MALE") hronoTitrMaleFilmsList.add(film)
//                                    else hronoTitrFemaleFilmsList.add(film)
//                                }
//                                "VOICE_DIRECTOR" -> voiceDirectorFilmsList.add(film)
//                                "WRITER" -> writerFilmsList.add(film)
//                                "OPERATOR" -> operatorFilmsList.add(film)
//                                "EDITOR" -> editorFilmsList.add(film)
//                                "COMPOSER" -> composerFilmsList.add(film)
//                                "DESIGN" -> designFilmsList.add(film)
//                                "TRANSLATOR" -> translatorFilmsList.add(film)
//                                "UNKNOWN" -> unknownFilmsList.add(film)
//                            }
//                        }
//
//                        directorFilmsList = removeDuplicateFilms(directorFilmsList)
//                        producerFilmsList = removeDuplicateFilms(producerFilmsList)
//                        producerUSSRFilmsList = removeDuplicateFilms(producerUSSRFilmsList)
//                        actorFilmsList = removeDuplicateFilms(actorFilmsList)
//                        actressFilmsList = removeDuplicateFilms(actressFilmsList)
//                        himselfFilmsList = removeDuplicateFilms(himselfFilmsList)
//                        herselfFilmsList = removeDuplicateFilms(herselfFilmsList)
//                        hronoTitrMaleFilmsList = removeDuplicateFilms(hronoTitrMaleFilmsList)
//                        hronoTitrFemaleFilmsList = removeDuplicateFilms(hronoTitrFemaleFilmsList)
//                        voiceDirectorFilmsList = removeDuplicateFilms(voiceDirectorFilmsList)
//                        writerFilmsList = removeDuplicateFilms(writerFilmsList)
//                        operatorFilmsList = removeDuplicateFilms(operatorFilmsList)
//                        editorFilmsList = removeDuplicateFilms(editorFilmsList)
//                        composerFilmsList = removeDuplicateFilms(composerFilmsList)
//                        designFilmsList = removeDuplicateFilms(designFilmsList)
//                        translatorFilmsList = removeDuplicateFilms(translatorFilmsList)
//                        unknownFilmsList = removeDuplicateFilms(unknownFilmsList)
//
//                        quantityDirectorFilms = directorFilmsList.size
//                        quantityProducerFilms = producerFilmsList.size
//                        quantityProducerUSSRFilms = producerUSSRFilmsList.size
//                        quantityActorFilms = actorFilmsList.size
//                        quantityActressFilms = actressFilmsList.size
//                        quantityHimselfFilms = himselfFilmsList.size
//                        quantityHerselfFilms = herselfFilmsList.size
//                        quantityHronoTitrMaleFilms = hronoTitrMaleFilmsList.size
//                        quantityHronoTitrFemaleFilms = hronoTitrFemaleFilmsList.size
//                        quantityVoiceDirectorFilms = voiceDirectorFilmsList.size
//                        quantityWriterFilms = writerFilmsList.size
//                        quantityOperatorFilms = operatorFilmsList.size
//                        quantityEditorFilms = editorFilmsList.size
//                        quantityComposerFilms = composerFilmsList.size
//                        quantityDesignFilms = designFilmsList.size
//                        quantityTranslatorFilms = translatorFilmsList.size
//                        quantityUnknownFilms = unknownFilmsList.size
//
//                        _directorFilms.value = directorFilmsList
//                        _producerFilms.value = producerFilmsList
//                        _producerUSSRFilms.value = producerUSSRFilmsList
//                        _actorFilms.value = actorFilmsList
//                        _actressFilms.value = actressFilmsList
//                        _himselfFilms.value = himselfFilmsList
//                        _herselfFilms.value = herselfFilmsList
//                        _hronoTitrMaleFilms.value = hronoTitrMaleFilmsList
//                        _hronoTitrFemaleFilms.value = hronoTitrFemaleFilmsList
//                        _voiceDirectorFilms.value = voiceDirectorFilmsList
//                        _writerFilms.value = writerFilmsList
//                        _operatorFilms.value = operatorFilmsList
//                        _editorFilms.value = editorFilmsList
//                        _composerFilms.value = composerFilmsList
//                        _designFilms.value = designFilmsList
//                        _translatorFilms.value = translatorFilmsList
//                        _unknownFilms.value = unknownFilmsList
//                    },
//                    onFailure = {
//                        Log.d(TAG, it.message ?: "Фильмы. Ошибка загрузки")
//                        error = true
//                    }
//                )
//            }
//            job2Loading.join()
        }
    }

    fun loadActorFilms() {
        printFilmIDsToLog(actorFilms)
        viewModelScope.launch(Dispatchers.IO) {
            val jobLoadAndSend = viewModelScope.launch(Dispatchers.IO) {
                actorFilms.apply {
                    forEach { filmOfPersonTable ->
                        val newDownloadedFilm =
                            repository.getPersonFilmDetailed(filmOfPersonTable)
                        if (newDownloadedFilm != null) {
                            Log.d(TAG, "Актёр. Загружен ${newDownloadedFilm.filmId}")
                            if (!actorFilmsDetailed.any { filmOfStaff -> filmOfStaff.filmId == newDownloadedFilm.filmId }) {
                                actorFilmsDetailed.add(newDownloadedFilm)
                                if (chosenType == 0) {
                                    Log.d(TAG, "Актёр. chosenType = $chosenType")
//                                        _actorFilmsFlow.value = actorFilmsDetailed.toList()
                                    _filmsFlow.value = actorFilmsDetailed.toList()
                                    Log.d(TAG, "Актёр. Доб-н ${newDownloadedFilm.filmId}")
                                } else {
                                    Log.d(TAG, "Актёр. Смена типа. return@apply")
                                    return@apply
                                }
                            } else {
                                Log.d(TAG, "Актёр. Уже ${newDownloadedFilm.filmId}")
                            }
                        }
                    }
                }
            }
            jobLoadAndSend.join()
            if (chosenType == 0) {
//                _actorFilmsFlow.value = actorFilmsDetailed.toList()
                _filmsFlow.value = actorFilmsDetailed.toList()
                Log.d(TAG, "Актёр. Финальный список длины ${actorFilmsDetailed.size}")
            }
        }
    }

    fun loadActressFilms() {
        printFilmIDsToLog(actressFilms)
        viewModelScope.launch(Dispatchers.IO) {
            val jobLoadAndSend = viewModelScope.launch(Dispatchers.IO) {
                actressFilms.apply {
                    forEach { filmOfPersonTable ->
                        val newDownloadedFilm =
                            repository.getPersonFilmDetailed(filmOfPersonTable)
                        if (newDownloadedFilm != null) {
                            Log.d(TAG, "Актриса. Загружен ${newDownloadedFilm.filmId}")
                            if (!actressFilmsDetailed.any { filmOfStaff -> filmOfStaff.filmId == newDownloadedFilm.filmId }) {
                                actressFilmsDetailed.add(newDownloadedFilm)
                                if (chosenType == 1) {
                                    Log.d(TAG, "Актриса. chosenType = $chosenType")
//                                    _actressFilmsFlow.value = actressFilmsDetailed.toList()
                                    _filmsFlow.value = actressFilmsDetailed.toList()
                                    Log.d(TAG, "Актриса. Доб-н ${newDownloadedFilm.filmId}")
                                } else {
                                    Log.d(TAG, "Актриса. Смена типа. return@apply")
                                    return@apply
                                }
                            } else {
                                Log.d(TAG, "Актриса. Уже ${newDownloadedFilm.filmId}")
                            }
                        }
                    }
                }
            }
            jobLoadAndSend.join()
            if (chosenType == 1) {
//                _actressFilmsFlow.value = actressFilmsDetailed.toList()
                _filmsFlow.value = actressFilmsDetailed.toList()
                Log.d(TAG, "Актриса. Финальный список длины ${actressFilmsDetailed.size}")
            }
        }
    }

    fun loadHimselfFilms() {
        printFilmIDsToLog(himselfFilms)
        viewModelScope.launch(Dispatchers.IO) {
            val jobLoadAndSend = viewModelScope.launch(Dispatchers.IO) {
                himselfFilms.apply {
                    forEach { filmOfPersonTable ->
                        val newDownloadedFilm =
                            repository.getPersonFilmDetailed(filmOfPersonTable)
                        if (newDownloadedFilm != null) {
                            Log.d(TAG, "Сам себя. Загружен ${newDownloadedFilm.filmId}")
                            if (!himselfFilmsDetailed.any { filmOfStaff -> filmOfStaff.filmId == newDownloadedFilm.filmId }) {
                                himselfFilmsDetailed.add(newDownloadedFilm)
                                if (chosenType == 2) {
                                    Log.d(TAG, "Сам себя. chosenType = $chosenType")
//                                    _himselfFilmsFlow.value = himselfFilmsDetailed.toList()
                                    _filmsFlow.value = himselfFilmsDetailed.toList()
                                    Log.d(TAG, "Сам себя. Доб-н ${newDownloadedFilm.filmId}")
                                } else {
                                    Log.d(TAG, "Сам себя. Смена типа. return@apply")
                                    return@apply
                                }
                            } else {
                                Log.d(TAG, "Сам себя. Уже ${newDownloadedFilm.filmId}")
                            }
                        }
                    }
                }
            }
            jobLoadAndSend.join()
            if (chosenType == 2) {
                //            _himselfFilmsFlow.value = himselfFilmsDetailed.toList()
                _filmsFlow.value = himselfFilmsDetailed.toList()
                Log.d(TAG, "Сам себя. Финальный список длины ${himselfFilmsDetailed.size}")
            }
        }
    }

    fun loadHerselfFilms() {
        printFilmIDsToLog(herselfFilms)
        viewModelScope.launch(Dispatchers.IO) {
            val jobLoadAndSend = viewModelScope.launch(Dispatchers.IO) {
                herselfFilms.apply {
                    forEach { filmOfPersonTable ->
                        val newDownloadedFilm =
                            repository.getPersonFilmDetailed(filmOfPersonTable)
                        if (newDownloadedFilm != null) {
                            Log.d(TAG, "Сама себя. Загружен ${newDownloadedFilm.filmId}")
                            if (!herselfFilmsDetailed.any { filmOfStaff -> filmOfStaff.filmId == newDownloadedFilm.filmId }) {
                                herselfFilmsDetailed.add(newDownloadedFilm)
                                if (chosenType == 3) {
                                    Log.d(TAG, "Сама себя. chosenType = $chosenType")
//                                    _herselfFilmsFlow.value = herselfFilmsDetailed.toList()
                                    _filmsFlow.value = herselfFilmsDetailed.toList()
                                    Log.d(TAG, "Сама себя. Доб-н ${newDownloadedFilm.filmId}")
                                } else {
                                    Log.d(TAG, "Сама себя. Смена типа. return@apply")
                                    return@apply
                                }
                            } else {
                                Log.d(TAG, "Сама себя. Уже ${newDownloadedFilm.filmId}")
                            }
                        }
                    }
                }
            }
            jobLoadAndSend.join()
            if (chosenType == 3) {
                //            _herselfFilmsFlow.value = herselfFilmsDetailed.toList()
                _filmsFlow.value = herselfFilmsDetailed.toList()
                Log.d(TAG, "Сама себя. Финальный список длины ${herselfFilmsDetailed.size}")
            }
        }
    }

    fun loadHronoTitrMaleFilms() {
        printFilmIDsToLog(hronoTitrMaleFilms)
        viewModelScope.launch(Dispatchers.IO) {
            val jobLoadAndSend = viewModelScope.launch(Dispatchers.IO) {
                hronoTitrMaleFilms.apply {
                    forEach { filmOfPersonTable ->
                        val newDownloadedFilm =
                            repository.getPersonFilmDetailed(filmOfPersonTable)
                        if (newDownloadedFilm != null) {
                            Log.d(TAG, "Актёр дубляжа. Загружен ${newDownloadedFilm.filmId}")
                            if (!hronoTitrMaleFilmsDetailed.any { filmOfStaff -> filmOfStaff.filmId == newDownloadedFilm.filmId }) {
                                hronoTitrMaleFilmsDetailed.add(newDownloadedFilm)
                                if (chosenType == 4) {
                                    Log.d(TAG, "Актёр дубляжа. chosenType = $chosenType")
//                                    _hronoTitrMaleFilmsFlow.value =
//                                        hronoTitrMaleFilmsDetailed.toList()
                                    _filmsFlow.value = hronoTitrMaleFilmsDetailed.toList()
                                    Log.d(TAG, "Актёр дубляжа. Доб-н ${newDownloadedFilm.filmId}")
                                } else {
                                    Log.d(TAG, "Актёр дубляжа. Смена типа. return@apply")
                                    return@apply
                                }
                            } else {
                                Log.d(TAG, "Актёр дубляжа. Уже ${newDownloadedFilm.filmId}")
                            }
                        }
                    }
                }
            }
            jobLoadAndSend.join()
            if (chosenType == 4) {
                //            _hronoTitrMaleFilmsFlow.value = hronoTitrMaleFilmsDetailed.toList()
                _filmsFlow.value = hronoTitrMaleFilmsDetailed.toList()
                Log.d(
                    TAG,
                    "Актёр дубляжа. Финальный список длины ${hronoTitrMaleFilmsDetailed.size}"
                )
            }
        }
    }

    fun loadHronoTitrFemaleFilms() {
        printFilmIDsToLog(hronoTitrFemaleFilms)
        viewModelScope.launch(Dispatchers.IO) {
            val jobLoadAndSend = viewModelScope.launch(Dispatchers.IO) {
                hronoTitrFemaleFilms.apply {
                    forEach { filmOfPersonTable ->
                        val newDownloadedFilm =
                            repository.getPersonFilmDetailed(filmOfPersonTable)
                        if (newDownloadedFilm != null) {
                            Log.d(TAG, "Актриса дубляжа. Загружен ${newDownloadedFilm.filmId}")
                            if (!hronoTitrFemaleFilmsDetailed.any { filmOfStaff -> filmOfStaff.filmId == newDownloadedFilm.filmId }) {
                                hronoTitrFemaleFilmsDetailed.add(newDownloadedFilm)
                                if (chosenType == 5) {
                                    Log.d(TAG, "Актриса дубляжа. chosenType = $chosenType")
//                                    _hronoTitrFemaleFilmsFlow.value =
//                                        hronoTitrFemaleFilmsDetailed.toList()
                                    _filmsFlow.value = hronoTitrFemaleFilmsDetailed.toList()
                                    Log.d(TAG, "Актриса дубляжа. Доб-н ${newDownloadedFilm.filmId}")
                                } else {
                                    Log.d(TAG, "Актриса дубляжа. Смена типа. return@apply")
                                    return@apply
                                }
                            } else {
                                Log.d(TAG, "Актриса дубляжа. Уже ${newDownloadedFilm.filmId}")
                            }

                        }
                    }
                }
            }
            jobLoadAndSend.join()
            if (chosenType == 5) {
                //            _hronoTitrFemaleFilmsFlow.value = hronoTitrFemaleFilmsDetailed.toList()
                _filmsFlow.value = hronoTitrFemaleFilmsDetailed.toList()
                Log.d(
                    TAG,
                    "Актриса дубляжа. Финальный список длины ${hronoTitrFemaleFilmsDetailed.size}"
                )
            }
        }
    }

    fun loadDirectorFilms() {
        printFilmIDsToLog(directorFilms)
        viewModelScope.launch(Dispatchers.IO) {
            val jobLoadAndSend = viewModelScope.launch(Dispatchers.IO) {
                directorFilms.apply {
                    forEach { filmOfPersonTable ->
                        val newDownloadedFilm =
                            repository.getPersonFilmDetailed(filmOfPersonTable)
                        if (newDownloadedFilm != null) {
                            Log.d(TAG, "Режисёр. Загружен ${newDownloadedFilm.filmId}")
                            if (!directorFilmsDetailed.any { filmOfStaff -> filmOfStaff.filmId == newDownloadedFilm.filmId }) {
                                directorFilmsDetailed.add(newDownloadedFilm)
                                if (chosenType == 6) {
                                    Log.d(TAG, "Режисёр. chosenType = $chosenType")
//                                    _directorFilmsFlow.value = directorFilmsDetailed.toList()
                                    _filmsFlow.value = directorFilmsDetailed.toList()
                                    Log.d(TAG, "Режисёр. Доб-н ${newDownloadedFilm.filmId}")
                                } else {
                                    Log.d(TAG, "Режисёр. Смена типа. return@apply")
                                    return@apply
                                }
                            } else {
                                Log.d(TAG, "Режисёр. Уже ${newDownloadedFilm.filmId}")
                            }
                        }
                    }
                }
            }
            jobLoadAndSend.join()
            if (chosenType == 6) {
                //            _directorFilmsFlow.value = directorFilmsDetailed.toList()
                _filmsFlow.value = directorFilmsDetailed.toList()
                Log.d(TAG, "Режисёр. Финальный список длины ${directorFilmsDetailed.size}")
            }
        }
    }

    fun loadProducerFilms() {
        printFilmIDsToLog(producerFilms)
        viewModelScope.launch(Dispatchers.IO) {
            val jobLoadAndSend = viewModelScope.launch(Dispatchers.IO) {
                producerFilms.apply {
                    forEach { filmOfPersonTable ->
                        val newDownloadedFilm =
                            repository.getPersonFilmDetailed(filmOfPersonTable)
                        if (newDownloadedFilm != null) {
                            Log.d(TAG, "Продюсер. Загружен ${newDownloadedFilm.filmId}")
                            if (!producerFilmsDetailed.any { filmOfStaff -> filmOfStaff.filmId == newDownloadedFilm.filmId }) {
                                producerFilmsDetailed.add(newDownloadedFilm)
                                if (chosenType == 7) {
                                    Log.d(TAG, "Продюсер. chosenType = $chosenType")
//                                    _producerFilmsFlow.value = producerFilmsDetailed.toList()
                                    _filmsFlow.value = producerFilmsDetailed.toList()
                                    Log.d(TAG, "Продюсер. Доб-н ${newDownloadedFilm.filmId}")
                                } else {
                                    Log.d(TAG, "Продюсер. Смена типа. return@apply")
                                    return@apply
                                }
                            } else {
                                Log.d(TAG, "Продюсер. Уже ${newDownloadedFilm.filmId}")
                            }
                        }
                    }
                }
            }
            jobLoadAndSend.join()
            if (chosenType == 7) {
                //            _producerFilmsFlow.value = producerFilmsDetailed.toList()
                _filmsFlow.value = producerFilmsDetailed.toList()
                Log.d(TAG, "Продюсер. Финальный список длины ${producerFilmsDetailed.size}")
            }
        }
    }

    fun loadProducerUSSRFilms() {
        printFilmIDsToLog(producerUSSRFilms)
        viewModelScope.launch(Dispatchers.IO) {
            val jobLoadAndSend = viewModelScope.launch(Dispatchers.IO) {
                producerUSSRFilms.apply {
                    forEach { filmOfPersonTable ->
                        val newDownloadedFilm =
                            repository.getPersonFilmDetailed(filmOfPersonTable)
                        if (newDownloadedFilm != null) {
                            Log.d(TAG, "Дир-р фильма. Загружен ${newDownloadedFilm.filmId}")
                            if (!producerUSSRFilmsDetailed.any { filmOfStaff -> filmOfStaff.filmId == newDownloadedFilm.filmId }) {
                                producerUSSRFilmsDetailed.add(newDownloadedFilm)
                                if (chosenType == 8) {
                                    Log.d(TAG, "Дир-р фильма. chosenType = $chosenType")
//                                    _producerUSSRFilmsFlow.value =
//                                        producerUSSRFilmsDetailed.toList()
                                    _filmsFlow.value = producerUSSRFilmsDetailed.toList()
                                    Log.d(TAG, "Дир-р фильма. Доб-н ${newDownloadedFilm.filmId}")
                                } else {
                                    Log.d(TAG, "Дир-р фильма. Смена типа. return@apply")
                                    return@apply
                                }
                            } else {
                                Log.d(TAG, "Дир-р фильма. Уже ${newDownloadedFilm.filmId}")
                            }
                        }
                    }
                }
            }
            jobLoadAndSend.join()
            if (chosenType == 8) {
                //            _producerUSSRFilmsFlow.value = producerUSSRFilmsDetailed.toList()
                _filmsFlow.value = producerUSSRFilmsDetailed.toList()
                Log.d(TAG, "Дир-р фильма. Финальный список длины ${producerUSSRFilmsDetailed.size}")
            }
        }
    }

    fun loadVoiceDirectorFilms() {
        printFilmIDsToLog(voiceDirectorFilms)
        viewModelScope.launch(Dispatchers.IO) {
            val jobLoadAndSend = viewModelScope.launch(Dispatchers.IO) {
                voiceDirectorFilms.apply {
                    forEach { filmOfPersonTable ->
                        val newDownloadedFilm =
                            repository.getPersonFilmDetailed(filmOfPersonTable)
                        if (newDownloadedFilm != null) {
                            Log.d(TAG, "Реж-р звука. Загружен ${newDownloadedFilm.filmId}")
                            if (!voiceDirectorFilmsDetailed.any { filmOfStaff -> filmOfStaff.filmId == newDownloadedFilm.filmId }) {
                                voiceDirectorFilmsDetailed.add(newDownloadedFilm)
                                if (chosenType == 9) {
                                    Log.d(TAG, "Реж-р звука. chosenType = $chosenType")
//                                    _voiceDirectorFilmsFlow.value =
//                                        voiceDirectorFilmsDetailed.toList()
                                    _filmsFlow.value = voiceDirectorFilmsDetailed.toList()
                                    Log.d(TAG, "Реж-р звука. Доб-н ${newDownloadedFilm.filmId}")
                                } else {
                                    Log.d(TAG, "Реж-р звука. Смена типа. return@apply")
                                    return@apply
                                }
                            } else {
                                Log.d(TAG, "Реж-р звука. Уже ${newDownloadedFilm.filmId}")
                            }
                        }
                    }
                }
            }
            jobLoadAndSend.join()
            if (chosenType == 9) {
                //            _voiceDirectorFilmsFlow.value = producerUSSRFilmsDetailed.toList()
                _filmsFlow.value = voiceDirectorFilmsDetailed.toList()
                Log.d(TAG, "Реж-р звука. Финальный список длины ${voiceDirectorFilmsDetailed.size}")
            }
        }
    }

    fun loadWriterFilms() {
        printFilmIDsToLog(writerFilms)
        viewModelScope.launch(Dispatchers.IO) {
            val jobLoadAndSend = viewModelScope.launch(Dispatchers.IO) {
                writerFilms.apply {
                    forEach { filmOfPersonTable ->
                        val newDownloadedFilm =
                            repository.getPersonFilmDetailed(filmOfPersonTable)
                        if (newDownloadedFilm != null) {
                            Log.d(TAG, "Сценарист. Загружен ${newDownloadedFilm.filmId}")
                            if (!writerFilmsDetailed.any { filmOfStaff -> filmOfStaff.filmId == newDownloadedFilm.filmId }) {
                                writerFilmsDetailed.add(newDownloadedFilm)
                                if (chosenType == 10) {
                                    Log.d(TAG, "Сценарист. chosenType = $chosenType")
//                                    _writerFilmsFlow.value =
//                                        writerFilmsDetailed.toList()
                                    _filmsFlow.value = writerFilmsDetailed.toList()
                                    Log.d(TAG, "Сценарист. Доб-н ${newDownloadedFilm.filmId}")
                                } else {
                                    Log.d(TAG, "Сценарист. Смена типа. return@apply")
                                    return@apply
                                }
                            } else {
                                Log.d(TAG, "Сценарист. Уже ${newDownloadedFilm.filmId}")
                            }
                        }
                    }
                }
            }
            jobLoadAndSend.join()
            if (chosenType == 10) {
                //            _writerFilmsFlow.value = writerFilmsDetailed.toList()
                _filmsFlow.value = writerFilmsDetailed.toList()
                Log.d(TAG, "Сценарист. Финальный список длины ${writerFilmsDetailed.size}")
            }
        }
    }

    fun loadOperatorFilms() {
        printFilmIDsToLog(operatorFilms)
        viewModelScope.launch(Dispatchers.IO) {
            val jobLoadAndSend = viewModelScope.launch(Dispatchers.IO) {
                operatorFilms.apply {
                    forEach { filmOfPersonTable ->
                        val newDownloadedFilm =
                            repository.getPersonFilmDetailed(filmOfPersonTable)
                        if (newDownloadedFilm != null) {
                            Log.d(TAG, "Оператор. Загружен ${newDownloadedFilm.filmId}")
                            if (!operatorFilmsDetailed.any { filmOfStaff -> filmOfStaff.filmId == newDownloadedFilm.filmId }) {
                                operatorFilmsDetailed.add(newDownloadedFilm)
                                if (chosenType == 11) {
                                    Log.d(TAG, "Оператор. chosenType = $chosenType")
//                                    _operatorFilmsFlow.value =
//                                        operatorFilmsDetailed.toList()
                                    _filmsFlow.value = operatorFilmsDetailed.toList()
                                    Log.d(TAG, "Оператор. Доб-н ${newDownloadedFilm.filmId}")
                                } else {
                                    Log.d(TAG, "Оператор. Смена типа. return@apply")
                                    return@apply
                                }
                            } else {
                                Log.d(TAG, "Оператор. Уже ${newDownloadedFilm.filmId}")
                            }
                        }
                    }
                }
            }
            jobLoadAndSend.join()
            if (chosenType == 11) {
                //            _operatorFilmsFlow.value = operatorFilmsDetailed.toList()
                _filmsFlow.value = operatorFilmsDetailed.toList()
                Log.d(TAG, "Оператор. Финальный список длины ${operatorFilmsDetailed.size}")
            }
        }
    }

    fun loadEditorFilms() {
        printFilmIDsToLog(editorFilms)
        viewModelScope.launch(Dispatchers.IO) {
            val jobLoadAndSend = viewModelScope.launch(Dispatchers.IO) {
                editorFilms.apply {
                    forEach { filmOfPersonTable ->
                        val newDownloadedFilm =
                            repository.getPersonFilmDetailed(filmOfPersonTable)
                        if (newDownloadedFilm != null) {
                            Log.d(TAG, "Монтажёр. Загружен ${newDownloadedFilm.filmId}")
                            if (!editorFilmsDetailed.any { filmOfStaff -> filmOfStaff.filmId == newDownloadedFilm.filmId }) {
                                editorFilmsDetailed.add(newDownloadedFilm)
                                if (chosenType == 12) {
                                    Log.d(TAG, "Монтажёр. chosenType = $chosenType")
//                                    _editorFilmsFlow.value =
//                                        editorFilmsDetailed.toList()
                                    _filmsFlow.value = editorFilmsDetailed.toList()
                                    Log.d(TAG, "Монтажёр. Доб-н ${newDownloadedFilm.filmId}")
                                } else {
                                    Log.d(TAG, "Монтажёр. Смена типа. return@apply")
                                    return@apply
                                }
                            } else {
                                Log.d(TAG, "Монтажёр. Уже ${newDownloadedFilm.filmId}")
                            }
                        }
                    }
                }
            }
            jobLoadAndSend.join()
            if (chosenType == 12) {
                //            _editorFilmsFlow.value = editorFilmsDetailed.toList()
                _filmsFlow.value = editorFilmsDetailed.toList()
                Log.d(TAG, "Монтажёр. Финальный список длины ${editorFilmsDetailed.size}")
            }
        }
    }

    fun loadComposerFilms() {
        printFilmIDsToLog(composerFilms)
        viewModelScope.launch(Dispatchers.IO) {
            val jobLoadAndSend = viewModelScope.launch(Dispatchers.IO) {
                composerFilms.apply {
                    forEach { filmOfPersonTable ->
                        val newDownloadedFilm =
                            repository.getPersonFilmDetailed(filmOfPersonTable)
                        if (newDownloadedFilm != null) {
                            Log.d(TAG, "Композитор. Загружен ${newDownloadedFilm.filmId}")
                            if (!composerFilmsDetailed.any { filmOfStaff -> filmOfStaff.filmId == newDownloadedFilm.filmId }) {
                                composerFilmsDetailed.add(newDownloadedFilm)
                                if (chosenType == 13) {
                                    Log.d(TAG, "Композитор. chosenType = $chosenType")
//                                    _composerFilmsFlow.value =
//                                        composerFilmsDetailed.toList()
                                    _filmsFlow.value = composerFilmsDetailed.toList()
                                    Log.d(TAG, "Композитор. Доб-н ${newDownloadedFilm.filmId}")
                                } else {
                                    Log.d(TAG, "Композитор. Смена типа. return@apply")
                                    return@apply
                                }
                            } else {
                                Log.d(TAG, "Композитор. Уже ${newDownloadedFilm.filmId}")
                            }
                        }
                    }
                }
            }
            jobLoadAndSend.join()
            if (chosenType == 13) {
                //            _composerFilmsFlow.value = composerFilmsDetailed.toList()
                _filmsFlow.value = composerFilmsDetailed.toList()
                Log.d(TAG, "Композитор. Финальный список длины ${composerFilmsDetailed.size}")
            }
        }
    }

    fun loadDesignFilms() {
        printFilmIDsToLog(designFilms)
        viewModelScope.launch(Dispatchers.IO) {
            val jobLoadAndSend = viewModelScope.launch(Dispatchers.IO) {
                designFilms.apply {
                    forEach { filmOfPersonTable ->
                        val newDownloadedFilm =
                            repository.getPersonFilmDetailed(filmOfPersonTable)
                        if (newDownloadedFilm != null) {
                            Log.d(TAG, "Художник. Загружен ${newDownloadedFilm.filmId}")
                            if (!designFilmsDetailed.any { filmOfStaff -> filmOfStaff.filmId == newDownloadedFilm.filmId }) {
                                designFilmsDetailed.add(newDownloadedFilm)
                                if (chosenType == 14) {
                                    Log.d(TAG, "Художник. chosenType = $chosenType")
//                                    _designFilmsFlow.value =
//                                        designFilmsDetailed.toList()
                                    _filmsFlow.value = designFilmsDetailed.toList()
                                    Log.d(TAG, "Художник. Доб-н ${newDownloadedFilm.filmId}")
                                } else {
                                    Log.d(TAG, "Художник. Смена типа. return@apply")
                                    return@apply
                                }
                            } else {
                                Log.d(TAG, "Художник. Уже ${newDownloadedFilm.filmId}")
                            }
                        }
                    }
                }
            }
            jobLoadAndSend.join()
            if (chosenType == 14) {
                //            _designFilmsFlow.value = designFilmsDetailed.toList()
                _filmsFlow.value = designFilmsDetailed.toList()
                Log.d(TAG, "Художник. Финальный список длины ${designFilmsDetailed.size}")
            }
        }
    }

    fun loadTranslatorFilms() {
        printFilmIDsToLog(translatorFilms)
        viewModelScope.launch(Dispatchers.IO) {
            val jobLoadAndSend = viewModelScope.launch(Dispatchers.IO) {
                translatorFilms.apply {
                    forEach { filmOfPersonTable ->
                        val newDownloadedFilm =
                            repository.getPersonFilmDetailed(filmOfPersonTable)
                        if (newDownloadedFilm != null) {
                            Log.d(TAG, "Переводчик. Загружен ${newDownloadedFilm.filmId}")
                            if (!translatorFilmsDetailed.any { filmOfStaff -> filmOfStaff.filmId == newDownloadedFilm.filmId }) {
                                translatorFilmsDetailed.add(newDownloadedFilm)
                                if (chosenType == 15) {
                                    Log.d(TAG, "Переводчик. chosenType = $chosenType")
//                                    _translatorFilmsFlow.value =
//                                        translatorFilmsDetailed.toList()
                                    _filmsFlow.value = translatorFilmsDetailed.toList()
                                    Log.d(TAG, "Переводчик. Доб-н ${newDownloadedFilm.filmId}")
                                } else {
                                    Log.d(TAG, "Переводчик. Смена типа. return@apply")
                                    return@apply
                                }
                            } else {
                                Log.d(TAG, "Переводчик. Уже ${newDownloadedFilm.filmId}")
                            }
                        }
                    }
                }
            }
            jobLoadAndSend.join()
            if (chosenType == 15) {
                //            _translatorFilmsFlow.value = translatorFilmsDetailed.toList()
                _filmsFlow.value = translatorFilmsDetailed.toList()
                Log.d(TAG, "Переводчик. Финальный список длины ${translatorFilmsDetailed.size}")
            }
        }
    }

    fun loadUnknownFilms() {
        printFilmIDsToLog(unknownFilms)
        viewModelScope.launch(Dispatchers.IO) {
            val jobLoadAndSend = viewModelScope.launch(Dispatchers.IO) {
                unknownFilms.apply {
                    forEach { filmOfPersonTable ->
                        val newDownloadedFilm =
                            repository.getPersonFilmDetailed(filmOfPersonTable)
                        if (newDownloadedFilm != null) {
                            Log.d(TAG, "Неизвестно. Загружен ${newDownloadedFilm.filmId}")
                            if (!unknownFilmsDetailed.any { filmOfStaff -> filmOfStaff.filmId == newDownloadedFilm.filmId }) {
                                unknownFilmsDetailed.add(newDownloadedFilm)
                                if (chosenType == 16) {
                                    Log.d(TAG, "Неизвестно. chosenType = $chosenType")
//                                    _unknownFilmsFlow.value =
//                                        unknownFilmsDetailed.toList()
                                    _filmsFlow.value = unknownFilmsDetailed.toList()
                                    Log.d(TAG, "Неизвестно. Доб-н ${newDownloadedFilm.filmId}")
                                } else {
                                    Log.d(TAG, "Неизвестно. Смена типа. return@apply")
                                    return@apply
                                }
                            } else {
                                Log.d(TAG, "Неизвестно. Уже ${newDownloadedFilm.filmId}")
                            }
                        }
                    }
                }
            }
            jobLoadAndSend.join()
            if (chosenType == 16) {
                //            _unknownFilmsFlow.value = unknownFilmsDetailed.toList()
                _filmsFlow.value = unknownFilmsDetailed.toList()
                Log.d(TAG, "Неизвестно. Финальный список длины ${unknownFilmsDetailed.size}")
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

    private fun printFilmIDsToLog(films: MutableList<FilmOfPersonTable>) {
        val filmIDs: MutableList<Int> = emptyList<Int>().toMutableList()
        films.forEach { film ->
            filmIDs.add(film.filmId)
        }
        Log.d(TAG, "$filmIDs")
    }

//    private fun removeDuplicateFilms(films: MutableList<FilmOfStaff>): MutableList<FilmOfStaff> {
//        val clearedFilmList: MutableList<FilmOfStaff> = emptyList<FilmOfStaff>().toMutableList()
//        val allFilmsId: MutableSet<Int> = emptySet<Int>().toMutableSet()
//        films.forEach { film ->
//            if (!allFilmsId.contains(film.filmId)) {
//                clearedFilmList.add(film)
//                allFilmsId.add(film.filmId)
//            }
//        }
//        return clearedFilmList
//    }
}