package edu.skillbox.skillcinema.presentation.film

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class FilmViewModelFactory (private val filmViewModel: FilmViewModel) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FilmViewModel::class.java)) {
            return filmViewModel as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}