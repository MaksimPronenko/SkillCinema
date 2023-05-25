package edu.skillbox.skillcinema.presentation

sealed class SearchViewModelState {
    object Searching : SearchViewModelState()
    object SearchSuccessfull : SearchViewModelState()
    object SearchFailed : SearchViewModelState()
    object SearchError : SearchViewModelState()
}