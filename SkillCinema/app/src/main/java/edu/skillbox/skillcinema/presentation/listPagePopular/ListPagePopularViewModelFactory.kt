package edu.skillbox.skillcinema.presentation.listPagePopular

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ListPagePopularViewModelFactory (private val listPagePopularViewModel: ListPagePopularViewModel) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListPagePopularViewModel::class.java)) {
            return listPagePopularViewModel as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}