package edu.skillbox.skillcinema.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

//class ListPageSeriesViewModelFactory(val application: App) : ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        val repository = Repository()
//        return ListPageSeriesViewModel(repository, application) as T
//    }
//}

class ListPageSeriesViewModelFactory (private val listPageSeriesViewModel: ListPageSeriesViewModel) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListPageSeriesViewModel::class.java)) {
            return listPageSeriesViewModel as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}