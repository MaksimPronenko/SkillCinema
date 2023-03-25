package edu.skillbox.skillcinema.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.skillbox.skillcinema.App
import edu.skillbox.skillcinema.data.*
import edu.skillbox.skillcinema.presentation.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class PresentationModule {

    @Provides
    @Singleton
    fun provideWelcomeViewModel(application: App): WelcomeViewModel {
        return WelcomeViewModel(application)
    }

    @Provides
    @Singleton
    fun provideWelcomeViewModelFactory(welcomeViewModel: WelcomeViewModel): WelcomeViewModelFactory {
        return WelcomeViewModelFactory(welcomeViewModel)
    }

    @Provides
    @Singleton
    fun provideMainViewModel(
        repository: Repository,
        filmsTop100PopularPagingSource: FilmsTop100PopularPagingSource,
        filmsTop250PagingSource: FilmsTop250PagingSource,
        seriesPagingSource: SeriesPagingSource,
        filmsFiltered1PagingSource: FilmsFiltered1PagingSource,
        filmsFiltered2PagingSource: FilmsFiltered2PagingSource,
        application: App
    ): MainViewModel {
        return MainViewModel(
            repository,
            filmsTop100PopularPagingSource,
            filmsTop250PagingSource,
            seriesPagingSource,
            filmsFiltered1PagingSource,
            filmsFiltered2PagingSource,
            application
        )
    }

    @Provides
    @Singleton
    fun provideMainViewModelFactory(mainViewModel: MainViewModel): MainViewModelFactory {
        return MainViewModelFactory(mainViewModel)
    }

    @Provides
    fun provideListPagePremieresViewModel(
        repository: Repository
    ): ListPagePremieresViewModel {
        return ListPagePremieresViewModel(
            repository
        )
    }

    @Provides
    fun provideListPagePremieresViewModelFactory(listPagePremieresViewModel: ListPagePremieresViewModel): ListPagePremieresViewModelFactory {
        return ListPagePremieresViewModelFactory(listPagePremieresViewModel)
    }

    @Provides
    @Singleton
    fun provideListPagePopularViewModel(
        repository: Repository,
        application: App
    ): ListPagePopularViewModel {
        return ListPagePopularViewModel(
            repository,
            application
        )
    }

    @Provides
    @Singleton
    fun provideListPagePopularViewModelFactory(listPagePopularViewModel: ListPagePopularViewModel): ListPagePopularViewModelFactory {
        return ListPagePopularViewModelFactory(listPagePopularViewModel)
    }

    @Provides
    @Singleton
    fun provideListPageTop250ViewModel(
        repository: Repository,
        application: App
    ): ListPageTop250ViewModel {
        return ListPageTop250ViewModel(
            repository,
            application
        )
    }

    @Provides
    @Singleton
    fun provideListPageTop250ViewModelFactory(listPageTop250ViewModel: ListPageTop250ViewModel): ListPageTop250ViewModelFactory {
        return ListPageTop250ViewModelFactory(listPageTop250ViewModel)
    }

    @Provides
    @Singleton
    fun provideListPageFiltered1ViewModel(
        repository: Repository,
        application: App
    ): ListPageFiltered1ViewModel {
        return ListPageFiltered1ViewModel(
            repository,
            application
        )
    }

    @Provides
    @Singleton
    fun provideListPageFiltered1ViewModelFactory(listPageFiltered1ViewModel: ListPageFiltered1ViewModel): ListPageFiltered1ViewModelFactory {
        return ListPageFiltered1ViewModelFactory(listPageFiltered1ViewModel)
    }

    @Provides
    @Singleton
    fun provideListPageFiltered2ViewModel(
        repository: Repository,
        application: App
    ): ListPageFiltered2ViewModel {
        return ListPageFiltered2ViewModel(
            repository,
            application
        )
    }

    @Provides
    @Singleton
    fun provideListPageFiltered2ViewModelFactory(listPageFiltered2ViewModel: ListPageFiltered2ViewModel): ListPageFiltered2ViewModelFactory {
        return ListPageFiltered2ViewModelFactory(listPageFiltered2ViewModel)
    }

    @Provides
    fun provideListPageSeriesViewModel(
        repository: Repository
    ): ListPageSeriesViewModel {
        return ListPageSeriesViewModel(
            repository
        )
    }

    @Provides
    fun provideListPageSeriesViewModelFactory(listPageSeriesViewModel: ListPageSeriesViewModel): ListPageSeriesViewModelFactory {
        return ListPageSeriesViewModelFactory(listPageSeriesViewModel)
    }

    @Provides
    fun provideFilmViewModel(
        repository: Repository
    ): FilmViewModel {
        return FilmViewModel(
            repository
        )
    }

    @Provides
    fun provideFilmViewModelFactory(filmViewModel: FilmViewModel): FilmViewModelFactory {
        return FilmViewModelFactory(filmViewModel)
    }

    @Provides
    fun provideListPageGalleryViewModel(
        repository: Repository,
    ): ListPageGalleryViewModel {
        return ListPageGalleryViewModel(
            repository
        )
    }

    @Provides
    fun provideListPageGalleryViewModelFactory(listPageGalleryViewModel: ListPageGalleryViewModel): ListPageGalleryViewModelFactory {
        return ListPageGalleryViewModelFactory(listPageGalleryViewModel)
    }

    @Provides
    fun provideListPageSimilarsViewModel(
        repository: Repository,
    ): ListPageSimilarsViewModel {
        return ListPageSimilarsViewModel(
            repository
        )
    }

    @Provides
    fun provideListPageSimilarsViewModelFactory(listPageSimilarsViewModel: ListPageSimilarsViewModel): ListPageSimilarsViewModelFactory {
        return ListPageSimilarsViewModelFactory(listPageSimilarsViewModel)
    }

}