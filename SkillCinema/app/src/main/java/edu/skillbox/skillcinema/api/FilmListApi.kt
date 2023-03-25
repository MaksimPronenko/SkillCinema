package edu.skillbox.skillcinema.api

import edu.skillbox.skillcinema.models.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface FilmListApi {
    @Headers("X-API-KEY: $api_key")
    @GET("/api/v2.2/films/premieres")
    suspend fun getPremieres(@Query("year") year: Int, @Query("month") month:String): FilmList

    @Headers("X-API-KEY: $api_key")
    @GET("/api/v2.2/films/top?type=TOP_100_POPULAR_FILMS")
    suspend fun getTop100Popular(@Query("page") page:Int): PagedFilmTopList

    @Headers("X-API-KEY: $api_key")
    @GET("/api/v2.2/films/top?type=TOP_250_BEST_FILMS")
    suspend fun getTop250(@Query("page") page:Int): PagedFilmTopList

    @Headers("X-API-KEY: $api_key")
    @GET("/api/v2.2/films?type=TV_SERIES&ratingFrom=8")
    suspend fun getSeries(@Query("page") page:Int): PagedFilmFilteredList

    @Headers("X-API-KEY: $api_key")
    @GET("/api/v2.2/films?type=FILM&ratingFrom=6")
    suspend fun getFilmFiltered(
        @Query("genres") genres: Int,
        @Query("countries") countries: Int,
        @Query("page") page:Int
    ): PagedFilmFilteredList

    @Headers("X-API-KEY: $api_key")
    @GET("/api/v2.2/films/{filmId}")
    suspend fun getFilmInfo(@Path("filmId") filmId: Int): FilmInfo

    @Headers("X-API-KEY: $api_key")
    @GET("/api/v1/staff")
    suspend fun getStaff(
        @Query("filmId") filmId: Int
    ): List<StaffInfo>

    @Headers("X-API-KEY: $api_key")
    @GET("/api/v2.2/films/{filmId}/images")
    suspend fun getImages(
        @Path("filmId") filmId: Int,
        @Query("type") type: String,
        @Query("page") page:Int
    ): PagedImages

    @Headers("X-API-KEY: $api_key")
    @GET("/api/v2.2/films/{filmId}/similars")
    suspend fun getSimilars(
        @Path("filmId") filmId: Int
    ): SimilarFilmList

    private companion object{
        private const val api_key = "ce6f81de-e746-4a8b-8a79-4a7fe451b75d"
//        private const val api_key = "a8429c6b-2971-443a-9a72-59932af2324f"
    }
}

val retrofit = Retrofit
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