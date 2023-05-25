package edu.skillbox.skillcinema.presentation

sealed class CollectionNameViewModelState {
    object DataInput : CollectionNameViewModelState()
    object DataIsValid : CollectionNameViewModelState()
}