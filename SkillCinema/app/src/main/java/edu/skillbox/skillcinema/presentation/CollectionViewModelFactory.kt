package edu.skillbox.skillcinema.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class CollectionViewModelFactory (private val collectionViewModel: CollectionViewModel) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CollectionViewModel::class.java)) {
            return collectionViewModel as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}