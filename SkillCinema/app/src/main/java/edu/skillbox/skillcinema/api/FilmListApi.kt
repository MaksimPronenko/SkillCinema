package edu.skillbox.skillcinema.api

import edu.skillbox.skillcinema.models.*
import edu.skillbox.skillcinema.models.filmAndSerial.film.FilmInfo
import edu.skillbox.skillcinema.models.filmAndSerial.image.PagedImages
import edu.skillbox.skillcinema.models.person.PersonInfo
import edu.skillbox.skillcinema.models.filmAndSerial.serial.SerialInfo
import edu.skillbox.skillcinema.models.filmAndSerial.similar.SimilarFilmList
import edu.skillbox.skillcinema.models.filmAndSerial.staff.StaffInfo
import edu.skillbox.skillcinema.models.filmFiltered.PagedFilmFilteredList
import edu.skillbox.skillcinema.models.filmTop.PagedFilmTopList
import edu.skillbox.skillcinema.models.premiere.FilmList
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface FilmListApi {
    @GET("/api/v2.2/films/premieres")
    suspend fun getPremieres(
        @Header("X-API-KEY") apiKey: String,
        @Query("year") year: Int,
        @Query("month") month: String
    ): FilmList?

    @GET("/api/v2.2/films/top")
    suspend fun getTopList(
        @Header("X-API-KEY") apiKey: String,
        @Query("type") type: String,
        @Query("page") page: Int
    ): PagedFilmTopList?

    @GET("/api/v2.2/films?type=TV_SERIES&ratingFrom=8")
    suspend fun getSerials(
        @Header("X-API-KEY") apiKey: String,
        @Query("page") page: Int
    ): PagedFilmFilteredList?

    @GET("/api/v2.2/films?type=FILM&ratingFrom=6")
    suspend fun getFilmFiltered(
        @Header("X-API-KEY") apiKey: String,
        @Query("genres") genres: Int,
        @Query("countries") countries: Int,
        @Query("page") page: Int
    ): PagedFilmFilteredList?

    @GET("/api/v2.2/films/{filmId}")
    suspend fun getFilmInfo(
        @Header("X-API-KEY") apiKey: String,
        @Path("filmId") filmId: Int
    ): FilmInfo?

    @GET("/api/v1/staff/{personId}")
    suspend fun getPersonInfo(
        @Header("X-API-KEY") apiKey: String,
        @Path("personId") personId: Int
    ): PersonInfo?

    @GET("/api/v2.2/films")
    suspend fun searchFilms(
        @Header("X-API-KEY") apiKey: String,
        @Query("countries") countries: Int?,
        @Query("genres") genres: Int?,
        @Query("order") order: String,
        @Query("type") type: String,
        @Query("ratingFrom") ratingFrom: Int,
        @Query("ratingTo") ratingTo: Int,
        @Query("yearFrom") yearFrom: Int?,
        @Query("yearTo") yearTo: Int?,
        @Query("keyword") keyword: String?,
        @Query("page") page: Int
    ): PagedFilmFilteredList?

    @GET("/api/v2.2/films/{filmId}/seasons")
    suspend fun getSerialInfo(
        @Header("X-API-KEY") apiKey: String,
        @Path("filmId") filmId: Int
    ): SerialInfo?

    @GET("/api/v1/staff")
    suspend fun getStaff(
        @Header("X-API-KEY") apiKey: String,
        @Query("filmId") filmId: Int
    ): List<StaffInfo>?

    @GET("/api/v2.2/films/{filmId}/images")
    suspend fun getImages(
        @Header("X-API-KEY") apiKey: String,
        @Path("filmId") filmId: Int,
        @Query("type") type: String,
        @Query("page") page: Int
    ): PagedImages?

    @GET("/api/v2.2/films/{filmId}/similars")
    suspend fun getSimilars(
        @Header("X-API-KEY") apiKey: String,
        @Path("filmId") filmId: Int
    ): SimilarFilmList?
}

val retrofit: FilmListApi = Retrofit
    .Builder()
    .client(
        OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor().also {
            it.level = HttpLoggingInterceptor.Level.BODY
        }).build()
    )
    .baseUrl("https://kinopoiskapiunofficial.tech")
    .addConverterFactory(GsonConverterFactory.create())
    .build()
    .create(FilmListApi::class.java)