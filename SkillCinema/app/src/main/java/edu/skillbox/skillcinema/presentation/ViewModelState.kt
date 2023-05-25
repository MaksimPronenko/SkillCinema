package edu.skillbox.skillcinema.presentation

sealed class ViewModelState {
    object Loading : ViewModelState()
    object Loaded : ViewModelState()
    object Error : ViewModelState()
}