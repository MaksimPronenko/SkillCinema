package edu.skillbox.skillcinema.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.skillbox.skillcinema.App
import edu.skillbox.skillcinema.data.*
import edu.skillbox.skillcinema.presentation.*
import edu.skillbox.skillcinema.presentation.allFilmsOfStaff.AllFilmsOfStaffViewModel
import edu.skillbox.skillcinema.presentation.allFilmsOfStaff.AllFilmsOfStaffViewModelFactory
import edu.skillbox.skillcinema.presentation.allInterested.AllInterestedViewModel
import edu.skillbox.skillcinema.presentation.allInterested.AllInterestedViewModelFactory
import edu.skillbox.skillcinema.presentation.allStaff.AllStaffViewModel
import edu.skillbox.skillcinema.presentation.allStaff.AllStaffViewModelFactory
import edu.skillbox.skillcinema.presentation.bottomDialogFragment.BottomDialogViewModel
import edu.skillbox.skillcinema.presentation.bottomDialogFragment.BottomDialogViewModelFactory
import edu.skillbox.skillcinema.presentation.collection.CollectionViewModel
import edu.skillbox.skillcinema.presentation.collection.CollectionViewModelFactory
import edu.skillbox.skillcinema.presentation.collectionName.CollectionNameDialogViewModel
import edu.skillbox.skillcinema.presentation.collectionName.CollectionNameDialogViewModelFactory
import edu.skillbox.skillcinema.presentation.film.FilmViewModel
import edu.skillbox.skillcinema.presentation.film.FilmViewModelFactory
import edu.skillbox.skillcinema.presentation.imagePager.ImagePagerViewModel
import edu.skillbox.skillcinema.presentation.imagePager.ImagePagerViewModelFactory
import edu.skillbox.skillcinema.presentation.listPageFilmography.ListPageFilmographyViewModel
import edu.skillbox.skillcinema.presentation.listPageFilmography.ListPageFilmographyViewModelFactory
import edu.skillbox.skillcinema.presentation.listPageFiltered1.ListPageFiltered1ViewModel
import edu.skillbox.skillcinema.presentation.listPageFiltered1.ListPageFiltered1ViewModelFactory
import edu.skillbox.skillcinema.presentation.listPageFiltered2.ListPageFiltered2ViewModel
import edu.skillbox.skillcinema.presentation.listPageFiltered2.ListPageFiltered2ViewModelFactory
import edu.skillbox.skillcinema.presentation.listPageGallery.ListPageGalleryViewModel
import edu.skillbox.skillcinema.presentation.listPageGallery.ListPageGalleryViewModelFactory
import edu.skillbox.skillcinema.presentation.listPagePopular.ListPagePopularViewModel
import edu.skillbox.skillcinema.presentation.listPagePopular.ListPagePopularViewModelFactory
import edu.skillbox.skillcinema.presentation.listPagePremieres.ListPagePremieresViewModel
import edu.skillbox.skillcinema.presentation.listPagePremieres.ListPagePremieresViewModelFactory
import edu.skillbox.skillcinema.presentation.listPageSerials.ListPageSerialsViewModel
import edu.skillbox.skillcinema.presentation.listPageSerials.ListPageSerialsViewModelFactory
import edu.skillbox.skillcinema.presentation.listPageSimilars.ListPageSimilarsViewModel
import edu.skillbox.skillcinema.presentation.listPageSimilars.ListPageSimilarsViewModelFactory
import edu.skillbox.skillcinema.presentation.listPageTop250.ListPageTop250ViewModel
import edu.skillbox.skillcinema.presentation.listPageTop250.ListPageTop250ViewModelFactory
import edu.skillbox.skillcinema.presentation.main.MainViewModel
import edu.skillbox.skillcinema.presentation.main.MainViewModelFactory
import edu.skillbox.skillcinema.presentation.profile.ProfileViewModel
import edu.skillbox.skillcinema.presentation.profile.ProfileViewModelFactory
import edu.skillbox.skillcinema.presentation.search.*
import edu.skillbox.skillcinema.presentation.serial.SerialViewModel
import edu.skillbox.skillcinema.presentation.serial.SerialViewModelFactory
import edu.skillbox.skillcinema.presentation.serialContent.SerialContentViewModel
import edu.skillbox.skillcinema.presentation.serialContent.SerialContentViewModelFactory
import edu.skillbox.skillcinema.presentation.staff.StaffViewModel
import edu.skillbox.skillcinema.presentation.staff.StaffViewModelFactory
import edu.skillbox.skillcinema.presentation.welcome.WelcomeViewModel
import edu.skillbox.skillcinema.presentation.welcome.WelcomeViewModelFactory
import edu.skillbox.skillcinema.utils.Converters
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
        repositoryMainLists: RepositoryMainLists,
        application: App
    ): MainViewModel {
        return MainViewModel(
            repositoryMainLists,
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
        repositoryMainLists: RepositoryMainLists
    ): ListPagePremieresViewModel {
        return ListPagePremieresViewModel(
            repositoryMainLists
        )
    }

    @Provides
    fun provideListPagePremieresViewModelFactory(listPagePremieresViewModel: ListPagePremieresViewModel): ListPagePremieresViewModelFactory {
        return ListPagePremieresViewModelFactory(listPagePremieresViewModel)
    }

    @Provides
    @Singleton
    fun provideListPagePopularViewModel(
        repositoryMainLists: RepositoryMainLists,
        application: App
    ): ListPagePopularViewModel {
        return ListPagePopularViewModel(
            repositoryMainLists,
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
        repositoryMainLists: RepositoryMainLists,
        application: App
    ): ListPageTop250ViewModel {
        return ListPageTop250ViewModel(
            repositoryMainLists,
            application
        )
    }

    @Provides
    @Singleton
    fun provideListPageTop250ViewModelFactory(listPageTop250ViewModel: ListPageTop250ViewModel): ListPageTop250ViewModelFactory {
        return ListPageTop250ViewModelFactory(listPageTop250ViewModel)
    }

    @Provides
    fun provideListPageFiltered1ViewModel(
        repositoryMainLists: RepositoryMainLists
    ): ListPageFiltered1ViewModel {
        return ListPageFiltered1ViewModel(
            repositoryMainLists
        )
    }

    @Provides
    fun provideListPageFiltered1ViewModelFactory(listPageFiltered1ViewModel: ListPageFiltered1ViewModel): ListPageFiltered1ViewModelFactory {
        return ListPageFiltered1ViewModelFactory(listPageFiltered1ViewModel)
    }

    @Provides
    fun provideListPageFiltered2ViewModel(
        repositoryMainLists: RepositoryMainLists
    ): ListPageFiltered2ViewModel {
        return ListPageFiltered2ViewModel(
            repositoryMainLists
        )
    }

    @Provides
    fun provideListPageFiltered2ViewModelFactory(listPageFiltered2ViewModel: ListPageFiltered2ViewModel): ListPageFiltered2ViewModelFactory {
        return ListPageFiltered2ViewModelFactory(listPageFiltered2ViewModel)
    }

    @Provides
    fun provideListPageSerialsViewModel(
        repositoryMainLists: RepositoryMainLists
    ): ListPageSerialsViewModel {
        return ListPageSerialsViewModel(
            repositoryMainLists
        )
    }

    @Provides
    fun provideListPageSerialsViewModelFactory(listPageSerialsViewModel: ListPageSerialsViewModel): ListPageSerialsViewModelFactory {
        return ListPageSerialsViewModelFactory(listPageSerialsViewModel)
    }

    @Provides
    fun provideFilmViewModel(
        repositoryFilmAndSerial: RepositoryFilmAndSerial,
        repositoryCollections: RepositoryCollections,
        converters: Converters
    ): FilmViewModel {
        return FilmViewModel(
            repositoryFilmAndSerial,
            repositoryCollections,
            converters
        )
    }

    @Provides
    fun provideFilmViewModelFactory(filmViewModel: FilmViewModel): FilmViewModelFactory {
        return FilmViewModelFactory(filmViewModel)
    }

    @Provides
    fun provideSerialViewModel(
        repositoryFilmAndSerial: RepositoryFilmAndSerial,
        repositoryCollections: RepositoryCollections,
        converters: Converters
    ): SerialViewModel {
        return SerialViewModel(
            repositoryFilmAndSerial,
            repositoryCollections,
            converters
        )
    }

    @Provides
    fun provideSerialViewModelFactory(serialViewModel: SerialViewModel): SerialViewModelFactory {
        return SerialViewModelFactory(serialViewModel)
    }

    @Provides
    fun provideSerialContentViewModel(
        repositoryFilmAndSerial: RepositoryFilmAndSerial
    ): SerialContentViewModel {
        return SerialContentViewModel(
            repositoryFilmAndSerial
        )
    }

    @Provides
    fun provideSerialContentViewModelFactory(serialContentViewModel: SerialContentViewModel): SerialContentViewModelFactory {
        return SerialContentViewModelFactory(serialContentViewModel)
    }

    @Provides
    fun provideBottomDialogViewModel(
        repositoryCollections: RepositoryCollections
    ): BottomDialogViewModel {
        return BottomDialogViewModel(
            repositoryCollections
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
        repositoryFilmAndSerial: RepositoryFilmAndSerial,
    ): AllStaffViewModel {
        return AllStaffViewModel(
            repositoryFilmAndSerial
        )
    }

    @Provides
    fun provideAllStaffViewModelFactory(allStaffViewModel: AllStaffViewModel): AllStaffViewModelFactory {
        return AllStaffViewModelFactory(allStaffViewModel)
    }

    @Provides
    fun provideListPageGalleryViewModel(
        repositoryFilmAndSerial: RepositoryFilmAndSerial,
    ): ListPageGalleryViewModel {
        return ListPageGalleryViewModel(
            repositoryFilmAndSerial
        )
    }

    @Provides
    fun provideListPageGalleryViewModelFactory(listPageGalleryViewModel: ListPageGalleryViewModel): ListPageGalleryViewModelFactory {
        return ListPageGalleryViewModelFactory(listPageGalleryViewModel)
    }

    @Provides
    fun provideImagePagerViewModel(
        repositoryFilmAndSerial: RepositoryFilmAndSerial,
    ): ImagePagerViewModel {
        return ImagePagerViewModel(
            repositoryFilmAndSerial
        )
    }

    @Provides
    fun provideImagePagerViewModelFactory(imagePagerViewModel: ImagePagerViewModel): ImagePagerViewModelFactory {
        return ImagePagerViewModelFactory(imagePagerViewModel)
    }

    @Provides
    fun provideListPageSimilarsViewModel(
        repositoryFilmAndSerial: RepositoryFilmAndSerial,
    ): ListPageSimilarsViewModel {
        return ListPageSimilarsViewModel(
            repositoryFilmAndSerial
        )
    }

    @Provides
    fun provideListPageSimilarsViewModelFactory(listPageSimilarsViewModel: ListPageSimilarsViewModel): ListPageSimilarsViewModelFactory {
        return ListPageSimilarsViewModelFactory(listPageSimilarsViewModel)
    }

    @Provides
    fun provideStaffViewModel(
        repositoryFilmAndSerial: RepositoryFilmAndSerial,
        repositoryPerson: RepositoryPerson,
        repositoryCollections: RepositoryCollections,
    ): StaffViewModel {
        return StaffViewModel(
            repositoryFilmAndSerial,
            repositoryPerson,
            repositoryCollections
        )
    }

    @Provides
    fun provideStaffViewModelFactory(staffViewModel: StaffViewModel): StaffViewModelFactory {
        return StaffViewModelFactory(staffViewModel)
    }

    @Provides
    fun provideAllFilmsOfStaffViewModel(
        repositoryFilmAndSerial: RepositoryFilmAndSerial,
        repositoryPerson: RepositoryPerson
    ): AllFilmsOfStaffViewModel {
        return AllFilmsOfStaffViewModel(
            repositoryFilmAndSerial,
            repositoryPerson
        )
    }

    @Provides
    fun provideAllFilmsOfStaffViewModelFactory(allFilmsOfStaffViewModel: AllFilmsOfStaffViewModel): AllFilmsOfStaffViewModelFactory {
        return AllFilmsOfStaffViewModelFactory(allFilmsOfStaffViewModel)
    }

    @Provides
    fun provideListPageFilmographyViewModel(
        repositoryFilmAndSerial: RepositoryFilmAndSerial,
        repositoryPerson: RepositoryPerson,
    ): ListPageFilmographyViewModel {
        return ListPageFilmographyViewModel(
            repositoryFilmAndSerial,
            repositoryPerson
        )
    }

    @Provides
    fun provideListPageFilmographyViewModelFactory(listPageFilmographyViewModel: ListPageFilmographyViewModel): ListPageFilmographyViewModelFactory {
        return ListPageFilmographyViewModelFactory(listPageFilmographyViewModel)
    }

    @Provides
    @Singleton
    fun provideSearchViewModel(
        repositoryMainLists: RepositoryMainLists,
        application: App
    ): SearchViewModel {
        return SearchViewModel(
            repositoryMainLists,
            application
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
        repositoryFilmAndSerial: RepositoryFilmAndSerial,
        repositoryPerson: RepositoryPerson,
        repositoryCollections: RepositoryCollections
    ): ProfileViewModel {
        return ProfileViewModel(
            repositoryFilmAndSerial,
            repositoryPerson,
            repositoryCollections
        )
    }

    @Provides
    fun provideProfileViewModelFactory(profileViewModel: ProfileViewModel): ProfileViewModelFactory {
        return ProfileViewModelFactory(profileViewModel)
    }

    @Provides
    fun provideCollectionViewModel(
        repositoryFilmAndSerial: RepositoryFilmAndSerial,
        repositoryCollections: RepositoryCollections
    ): CollectionViewModel {
        return CollectionViewModel(
            repositoryFilmAndSerial,
            repositoryCollections
        )
    }

    @Provides
    fun provideCollectionViewModelFactory(collectionViewModel: CollectionViewModel): CollectionViewModelFactory {
        return CollectionViewModelFactory(collectionViewModel)
    }

    @Provides
    fun provideAllInterestedViewModel(
        repositoryFilmAndSerial: RepositoryFilmAndSerial,
        repositoryPerson: RepositoryPerson,
        repositoryCollections: RepositoryCollections
    ): AllInterestedViewModel {
        return AllInterestedViewModel(
            repositoryFilmAndSerial,
            repositoryPerson,
            repositoryCollections
        )
    }

    @Provides
    fun provideAllInterestedViewModelFactory(allInterestedViewModel: AllInterestedViewModel): AllInterestedViewModelFactory {
        return AllInterestedViewModelFactory(allInterestedViewModel)
    }
}