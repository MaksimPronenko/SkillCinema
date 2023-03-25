package edu.skillbox.skillcinema.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

//class ListPagePopularViewModelFactory(val application: App) : ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        val repository = Repository()
//        return ListPagePopularViewModel(repository, application) as T
//    }
//}

class ListPagePopularViewModelFactory (private val listPagePopularViewModel: ListPagePopularViewModel) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListPagePopularViewModel::class.java)) {
            return listPagePopularViewModel as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}