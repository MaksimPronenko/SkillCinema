package edu.skillbox.skillcinema.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SearchSettings4ViewModelFactory (private val searchSettings4ViewModel: SearchSettings4ViewModel) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchSettings4ViewModel::class.java)) {
            return searchSettings4ViewModel as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}