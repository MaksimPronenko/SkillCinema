package edu.skillbox.skillcinema.presentation

sealed class CollectionNameViewModelState {
    object DataIsEmpty : CollectionNameViewModelState()
    object DataIsValid : CollectionNameViewModelState()
    object DataIsNotValid : CollectionNameViewModelState()
}