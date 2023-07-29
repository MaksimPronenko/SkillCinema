package edu.skillbox.skillcinema.presentation.collectionName

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class CollectionNameDialogViewModelFactory (private val collectionNameDialogViewModel: CollectionNameDialogViewModel) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CollectionNameDialogViewModel::class.java)) {
            return collectionNameDialogViewModel as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}