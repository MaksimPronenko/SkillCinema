package edu.skillbox.skillcinema.presentation.listPageFilmography

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class ListPageFilmographyViewModelFactory (private val listPageFilmographyViewModel: ListPageFilmographyViewModel) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListPageFilmographyViewModel::class.java)) {
            return listPageFilmographyViewModel as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}