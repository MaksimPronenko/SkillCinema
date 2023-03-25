package edu.skillbox.skillcinema

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App @Inject constructor() : Application() {

//class App : Application() {

//    lateinit var appComponent: AppComponent
//    lateinit var db: AppDatabase

    private val availableGenres = mapOf(
        1 to "Триллеры",
        2 to "Драмы",
        3 to "Криминальные фильмы",
        4 to "Мелодрамы",
        5 to "Детективы",
        6 to "Фантастика",
        11 to "Боевики",
        12 to "Фэнтези",
        13 to "Комедии",
        14 to "Военные фильмы",
        15 to "Исторические фильмы",
        17 to "Ужасы",
        18 to "Мультфильмы",
        19 to "Семейные фильмы",
//        22 to "Документальные фильмы",
//        23 to "Короткометражки",
        33 to "Детские фильмы"
    )
    private val availableCountries = mapOf(
        1 to "США",
        2 to "Швейцарии",
        3 to "Франции",
        4 to "Польши",
        5 to "Великобритании",
        6 to "Швеции",
        7 to "Индии",
        8 to "Испании",
        9 to "Германии",
        10 to "Италии",
        12 to "Германии",
        13 to "Австралии",
        14 to "Канады",
        15 to "Мексики",
        16 to "Японии",
        21 to "Китая",
        33 to "СССР",
        34 to "России"
    )

    var genre1key = availableGenres.keys.random()
    var genre2key = (availableGenres.keys - genre1key).random()
    var country1key = availableCountries.keys.random()
    var country2key = (availableCountries.keys - country1key).random()
    var filteredFilms1title = availableGenres[genre1key] + " " + availableCountries[country1key]
    var filteredFilms2title = availableGenres[genre2key] + " " + availableCountries[country2key]

    init {
        Log.d("AppMain", "g1 = $genre1key, c1 = $country1key, g2 = $genre2key, c2 = $country2key")
    }

    override fun onCreate() {
        super.onCreate()

//        appComponent = DaggerAppComponent.builder().dataModule(DataModule(context = this)).build()

        Log.d("AppMain", "Сработал App OnCreate\n" +
                "\tg1 = $genre1key, c1 = $country1key, g2 = $genre2key, c2 = $country2key")

    }
//    override fun onCreate() {
//        super.onCreate()
//        db = Room.databaseBuilder(
//            applicationContext,
//            AppDatabase::class.java,
//            "db"
//        ).build()
//    }

}