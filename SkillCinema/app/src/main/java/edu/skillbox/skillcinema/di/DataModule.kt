package edu.skillbox.skillcinema.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import edu.skillbox.skillcinema.App
import edu.skillbox.skillcinema.data.*
import javax.inject.Singleton

//import dagger.hilt.InstallIn
//import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Provides
    @Singleton
    fun provideApp(@ApplicationContext context: Context): App {
        return context as App
    }

    @Provides
    @Singleton
    fun provideFilmDao(application: App): FilmDao {
        return application.db.filmDao()
    }

    @Provides
    fun provideRepository(dao: FilmDao): Repository {
        return Repository(dao)
    }

    @Provides
    fun provideFilmsTop100PopularPagingSource(repository: Repository): FilmsTop100PopularPagingSource {
        return FilmsTop100PopularPagingSource(repository)
    }

    @Provides
    fun provideFilmsTop250PagingSource(repository: Repository): FilmsTop250PagingSource {
        return FilmsTop250PagingSource(repository)
    }

    @Provides
    fun provideSeriesPagingSource(repository: Repository): SerialsPagingSource {
        return SerialsPagingSource(repository)
    }

//    @Provides
//    fun provideFilmsFilteredPagingSource(repository: Repository): FilmsFilteredPagingSource {
//        return FilmsFilteredPagingSource(repository)
//    }

//    @Provides
//    @Singleton
//    fun provideFilmsFiltered1PagingSource(application: App, dao: FilmDao): FilmsFiltered1PagingSource {
//        return FilmsFiltered1PagingSource(application, dao)
//    }

//    @Provides
//    @Singleton
//    fun provideFilmsFiltered2PagingSource(application: App, dao: FilmDao): FilmsFiltered2PagingSource {
//        return FilmsFiltered2PagingSource(application, dao)
//    }

    @Provides
    @Singleton
    fun provideSearchPagingSource(application: App, dao: FilmDao): SearchPagingSource {
        return SearchPagingSource(application, dao)
    }

}