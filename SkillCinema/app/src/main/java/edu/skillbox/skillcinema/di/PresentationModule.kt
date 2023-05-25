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
        application: App,
        dao: FilmDao
    ): ListPageFiltered1ViewModel {
        return ListPageFiltered1ViewModel(
            repository,
            application,
            dao
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
        application: App,
        dao: FilmDao
    ): ListPageFiltered2ViewModel {
        return ListPageFiltered2ViewModel(
            repository,
            application,
            dao
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

//    @Provides
//    @Singleton
//    fun provideFilmViewModel(
//        repository: Repository,
//        application: App
//    ): FilmViewModel {
//        return FilmViewModel(
//            repository,
//            application
//        )
//    }
//
//    @Provides
//    @Singleton
//    fun provideFilmViewModelFactory(filmViewModel: FilmViewModel): FilmViewModelFactory {
//        return FilmViewModelFactory(filmViewModel)
//    }

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
    fun provideSerialViewModel(
        repository: Repository
    ): SerialViewModel {
        return SerialViewModel(
            repository
        )
    }

    @Provides
    fun provideSerialViewModelFactory(serialViewModel: SerialViewModel): SerialViewModelFactory {
        return SerialViewModelFactory(serialViewModel)
    }

    @Provides
    fun provideSerialContentViewModel(
        repository: Repository
    ): SerialContentViewModel {
        return SerialContentViewModel(
            repository
        )
    }

    @Provides
    fun provideSerialContentViewModelFactory(serialContentViewModel: SerialContentViewModel): SerialContentViewModelFactory {
        return SerialContentViewModelFactory(serialContentViewModel)
    }

    @Provides
    fun provideBottomDialogViewModel(
        repository: Repository
    ): BottomDialogViewModel {
        return BottomDialogViewModel(
            repository
        )
    }
    @Provides
    fun provideBottomDialogViewModelFactory(bottomDialogViewModel: BottomDialogViewModel): BottomDialogViewModelFactory {
        return BottomDialogViewModelFactory(bottomDialogViewModel)
    }

    @Provides
    fun provideCollectionNameDialogViewModel() : CollectionNameDialogViewModel {
        return CollectionNameDialogViewModel()
    }
    @Provides
    fun provideCollectionNameDialogViewModelFactory(collectionNameDialogViewModel: CollectionNameDialogViewModel): CollectionNameDialogViewModelFactory {
        return CollectionNameDialogViewModelFactory(collectionNameDialogViewModel)
    }

    @Provides
    fun provideAllStaffViewModel(
        repository: Repository,
    ): AllStaffViewModel {
        return AllStaffViewModel(
            repository
        )
    }

    @Provides
    fun provideAllStaffViewModelFactory(allStaffViewModel: AllStaffViewModel): AllStaffViewModelFactory {
        return AllStaffViewModelFactory(allStaffViewModel)
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
    fun provideImagePagerViewModel(
        repository: Repository,
    ): ImagePagerViewModel {
        return ImagePagerViewModel(
            repository
        )
    }

    @Provides
    fun provideImagePagerViewModelFactory(imagePagerViewModel: ImagePagerViewModel): ImagePagerViewModelFactory {
        return ImagePagerViewModelFactory(imagePagerViewModel)
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

    @Provides
    fun provideStaffViewModel(
        repository: Repository,
    ): StaffViewModel {
        return StaffViewModel(
            repository
        )
    }

    @Provides
    fun provideStaffViewModelFactory(staffViewModel: StaffViewModel): StaffViewModelFactory {
        return StaffViewModelFactory(staffViewModel)
    }

    @Provides
    fun provideAllFilmsOfStaffViewModel(
        repository: Repository,
    ): AllFilmsOfStaffViewModel {
        return AllFilmsOfStaffViewModel(
            repository
        )
    }

    @Provides
    fun provideAllFilmsOfStaffViewModelFactory(allFilmsOfStaffViewModel: AllFilmsOfStaffViewModel): AllFilmsOfStaffViewModelFactory {
        return AllFilmsOfStaffViewModelFactory(allFilmsOfStaffViewModel)
    }

    @Provides
    fun provideListPageFilmographyViewModel(
        repository: Repository,
    ): ListPageFilmographyViewModel {
        return ListPageFilmographyViewModel(
            repository
        )
    }

    @Provides
    fun provideListPageFilmographyViewModelFactory(listPageFilmographyViewModel: ListPageFilmographyViewModel): ListPageFilmographyViewModelFactory {
        return ListPageFilmographyViewModelFactory(listPageFilmographyViewModel)
    }

    @Provides
    @Singleton
    fun provideSearchViewModel(
        repository: Repository,
        application: App,
        dao: FilmDao
    ): SearchViewModel {
        return SearchViewModel(
            repository,
            application,
            dao
        )
    }

    @Provides
    @Singleton
    fun provideSearchViewModelFactory(searchViewModel: SearchViewModel): SearchViewModelFactory {
        return SearchViewModelFactory(searchViewModel)
    }

    @Provides
    @Singleton
    fun provideSearchSettings1ViewModel(application: App): SearchSettings1ViewModel {
        return SearchSettings1ViewModel(application)
    }

    @Provides
    @Singleton
    fun provideSearchSettings1ViewModelFactory(searchSettings1ViewModel: SearchSettings1ViewModel): SearchSettings1ViewModelFactory {
        return SearchSettings1ViewModelFactory(searchSettings1ViewModel)
    }

    @Provides
    @Singleton
    fun provideSearchSettings2ViewModel(application: App): SearchSettings2ViewModel {
        return SearchSettings2ViewModel(application)
    }

    @Provides
    @Singleton
    fun provideSearchSettings2ViewModelFactory(searchSettings2ViewModel: SearchSettings2ViewModel): SearchSettings2ViewModelFactory {
        return SearchSettings2ViewModelFactory(searchSettings2ViewModel)
    }

    @Provides
    @Singleton
    fun provideSearchSettings3ViewModel(application: App): SearchSettings3ViewModel {
        return SearchSettings3ViewModel(application)
    }

    @Provides
    @Singleton
    fun provideSearchSettings3ViewModelFactory(searchSettings3ViewModel: SearchSettings3ViewModel): SearchSettings3ViewModelFactory {
        return SearchSettings3ViewModelFactory(searchSettings3ViewModel)
    }

    @Provides
    @Singleton
    fun provideSearchSettings4ViewModel(application: App): SearchSettings4ViewModel {
        return SearchSettings4ViewModel(application)
    }

    @Provides
    @Singleton
    fun provideSearchSettings4ViewModelFactory(searchSettings4ViewModel: SearchSettings4ViewModel): SearchSettings4ViewModelFactory {
        return SearchSettings4ViewModelFactory(searchSettings4ViewModel)
    }

    @Provides
    fun provideProfileViewModel(
        repository: Repository,
    ): ProfileViewModel {
        return ProfileViewModel(
            repository
        )
    }

    @Provides
    fun provideProfileViewModelFactory(profileViewModel: ProfileViewModel): ProfileViewModelFactory {
        return ProfileViewModelFactory(profileViewModel)
    }
}