package edu.skillbox.skillcinema.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

//class ListPageSeriesViewModelFactory(val application: App) : ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        val repository = Repository()
//        return ListPageSeriesViewModel(repository, application) as T
//    }
//}

class ListPageSerialsViewModelFactory (private val listPageSerialsViewModel: ListPageSerialsViewModel) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListPageSerialsViewModel::class.java)) {
            return listPageSerialsViewModel as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}