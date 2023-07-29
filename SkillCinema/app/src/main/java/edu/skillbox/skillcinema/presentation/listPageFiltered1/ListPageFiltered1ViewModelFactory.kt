package edu.skillbox.skillcinema.presentation.listPageFiltered1

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ListPageFiltered1ViewModelFactory (private val listPageFiltered1ViewModel: ListPageFiltered1ViewModel) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListPageFiltered1ViewModel::class.java)) {
            return listPageFiltered1ViewModel as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}