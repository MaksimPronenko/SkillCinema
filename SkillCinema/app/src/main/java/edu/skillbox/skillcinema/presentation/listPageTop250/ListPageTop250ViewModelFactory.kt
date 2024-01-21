package edu.skillbox.skillcinema.presentation.listPageTop250

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class ListPageTop250ViewModelFactory (private val listPageTop250ViewModel: ListPageTop250ViewModel) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListPageTop250ViewModel::class.java)) {
            return listPageTop250ViewModel as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}