package edu.skillbox.skillcinema.presentation.listPageSimilars

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class ListPageSimilarsViewModelFactory (private val listPageSimilarsViewModel: ListPageSimilarsViewModel) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListPageSimilarsViewModel::class.java)) {
            return listPageSimilarsViewModel as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}