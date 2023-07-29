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
    @Singleton
    fun provideRepositoryFilmAndSerial(dao: FilmDao): RepositoryFilmAndSerial {
        return RepositoryFilmAndSerial(dao)
    }

    @Provides
    @Singleton
    fun provideRepositoryMainLists(dao: FilmDao): RepositoryMainLists {
        return RepositoryMainLists(dao)
    }

    @Provides
    @Singleton
    fun provideRepositoryPerson(dao: FilmDao): RepositoryPerson {
        return RepositoryPerson(dao)
    }

    @Provides
    @Singleton
    fun provideRepositoryCollections(dao: FilmDao): RepositoryCollections {
        return RepositoryCollections(dao)
    }

    @Provides
    fun provideSeriesPagingSource(repository: RepositoryMainLists): SerialsPagingSource {
        return SerialsPagingSource(repository)
    }

    @Provides
    @Singleton
    fun provideSearchPagingSource(application: App, repository: RepositoryMainLists): SearchPagingSource {
        return SearchPagingSource(application, repository)
    }
}