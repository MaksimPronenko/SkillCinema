package edu.skillbox.skillcinema.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SearchSettings3ViewModelFactory (private val searchSettings3ViewModel: SearchSettings3ViewModel) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchSettings3ViewModel::class.java)) {
            return searchSettings3ViewModel as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}