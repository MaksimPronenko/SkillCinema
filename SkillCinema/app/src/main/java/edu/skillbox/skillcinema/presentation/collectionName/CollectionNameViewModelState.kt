package edu.skillbox.skillcinema.presentation.collectionName

sealed class CollectionNameViewModelState {
    object DataIsEmpty : CollectionNameViewModelState()
    object DataIsValid : CollectionNameViewModelState()
    object DataIsNotValid : CollectionNameViewModelState()
}