package edu.skillbox.skillcinema.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SearchSettings2ViewModelFactory (private val searchSettings2ViewModel: SearchSettings2ViewModel) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchSettings2ViewModel::class.java)) {
            return searchSettings2ViewModel as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}