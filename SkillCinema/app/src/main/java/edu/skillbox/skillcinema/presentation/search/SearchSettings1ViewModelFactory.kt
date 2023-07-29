package edu.skillbox.skillcinema.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SearchSettings1ViewModelFactory (private val searchSettings1ViewModel: SearchSettings1ViewModel) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchSettings1ViewModel::class.java)) {
            return searchSettings1ViewModel as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}