package edu.skillbox.skillcinema.presentation.listPageFiltered2

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class ListPageFiltered2ViewModelFactory (private val listPageFiltered2ViewModel: ListPageFiltered2ViewModel) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListPageFiltered2ViewModel::class.java)) {
            return listPageFiltered2ViewModel as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}