package edu.skillbox.skillcinema.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SearchViewModelFactory (private val searchViewModel: SearchViewModel) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return searchViewModel as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}