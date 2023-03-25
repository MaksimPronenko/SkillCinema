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
    fun provideRepository(): Repository {
        return Repository()
    }

    @Provides
    fun provideFilmsTop100PopularPagingSource(): FilmsTop100PopularPagingSource {
        return FilmsTop100PopularPagingSource()
    }

    @Provides
    fun provideFilmsTop250PagingSource(): FilmsTop250PagingSource {
        return FilmsTop250PagingSource()
    }

    @Provides
    fun provideSeriesPagingSource(): SeriesPagingSource {
        return SeriesPagingSource()
    }

    @Provides
    @Singleton
    fun provideFilmsFiltered1PagingSource(application: App): FilmsFiltered1PagingSource {
        return FilmsFiltered1PagingSource(application)
    }

    @Provides
    @Singleton
    fun provideFilmsFiltered2PagingSource(application: App): FilmsFiltered2PagingSource {
        return FilmsFiltered2PagingSource(application)
    }

}