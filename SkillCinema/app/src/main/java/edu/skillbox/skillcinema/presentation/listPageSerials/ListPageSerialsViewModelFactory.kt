package edu.skillbox.skillcinema.presentation.listPageSerials

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class ListPageSerialsViewModelFactory (private val listPageSerialsViewModel: ListPageSerialsViewModel) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListPageSerialsViewModel::class.java)) {
            return listPageSerialsViewModel as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}